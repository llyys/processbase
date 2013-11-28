package org.processbase.bonita.services.impl;

import com.mongodb.DBObject;
import com.thoughtworks.xstream.XStream;
import liquibase.Liquibase;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.ow2.bonita.facade.exception.DocumentAlreadyExistsException;
import org.ow2.bonita.facade.exception.DocumentNotFoundException;
import org.ow2.bonita.facade.exception.DocumentationCreationException;
import org.ow2.bonita.facade.impl.SearchResult;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.search.DocumentCriterion;
import org.ow2.bonita.search.DocumentSearchBuilder;
import org.ow2.bonita.search.index.DocumentIndex;
import org.ow2.bonita.services.Document;
import org.ow2.bonita.services.DocumentationManager;
import org.ow2.bonita.services.impl.DocumentImpl;
import org.ow2.bonita.util.BonitaConstants;
import org.ow2.bonita.util.EnvTool;
import org.ow2.bonita.util.Misc;
import org.ow2.bonita.util.xml.XStreamUtil;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class DbDocumentationManager extends AbstractDocumentationManager {
    private static final Logger LOG = Logger.getLogger(DocumentationManager.class.getName());
   // private SessionFactory sessionFactory;
    JdbcTemplate jdbc=null;

    public DbDocumentationManager(String value) throws Exception {
        Properties properties=loadProperties(value);
        String driver=properties.getProperty("hibernate.connection.driver_class");
        String url=properties.getProperty("hibernate.connection.url");
        String username=properties.getProperty("hibernate.connection.username");
        String password=properties.getProperty("hibernate.connection.password");

        PostgresDatabase db = new PostgresDatabase();

        try {
            Driver driverInstance= (Driver) Driver.class.forName(driver).newInstance();
            SimpleDriverDataSource dataSource = new SimpleDriverDataSource(driverInstance, url, username, password);

            jdbc=new JdbcTemplate(dataSource);

            JdbcConnection connection = new JdbcConnection(dataSource.getConnection());
            db.setConnection(connection);
            ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();

            Liquibase lq = new Liquibase("liqubase-master.xml", resourceAccessor, connection);
            lq.update("");

            mimeProperties= ResourceBundle.getBundle("mime");

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static Properties loadProperties(String value){
        Misc.checkArgsNotNull(value);
        Properties properties = new Properties();
        String property = "property:";
        String path = value;
        if (value.startsWith(property)) {
            path = System.getProperty(value.substring(property.length()));
        } else if (value.startsWith("${" + BonitaConstants.HOME + "}")) {
            path = path.replace("${" + BonitaConstants.HOME + "}", System.getProperty(BonitaConstants.HOME));
        }

        FileInputStream in;
        try {
            in = new FileInputStream(path);

            properties.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    @Override
    public Document createDocument(String name, ProcessDefinitionUUID definitionUUID, ProcessInstanceUUID instanceUUID) throws DocumentationCreationException {
        return null;
    }

    @Override
    public Document createDocument(String name, ProcessDefinitionUUID definitionUUID, ProcessInstanceUUID instanceUUID, String fileName, String contentMimeType, byte[] fileContent) throws DocumentationCreationException {
        DocumentImpl doc = new DocumentImpl(name, null, EnvTool.getUserId(), new Date(), new Date(), true, true,null, null, fileName, contentMimeType, fileContent==null?0:fileContent.length, definitionUUID, instanceUUID);
        if(fileContent==null || fileContent.length==0)
            return doc;
        long id=jdbc.queryForLong("select nextval('BN_DOCUMENT_FILE_SEQ')");
        doc.setId(Long.toString(id));
        long oid = createOid(fileContent);
        jdbc.update("insert into BN_DOCUMENT_FILE (id, document_name, definition_uuid, instance_uuid, last_modified_by, creation_date, last_modification_date, file_name, content_mime_type, content_size, BLOB_VALUE, XML_VALUE) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)"
                , id
                , name
                , doc.getProcessDefinitionUUID().getValue()
                , doc.getProcessInstanceUUID().getValue()
                , doc.getLastModifiedBy()
                , doc.getCreationDate()
                , doc.getLastModificationDate()
                , doc.getContentFileName()
                , doc.getContentMimeType()
                , doc.getContentSize()
                , oid<0?null:oid
                , doc.toString()
        );
        return doc;
    }

    public static Document deSerialize(String xml, String name){
        XStream xstream = XStreamUtil.getDefaultXstream();
        DocumentImpl document1=new DocumentImpl(name);
        return (DocumentImpl)xstream.fromXML(xml, document1);
    }

    @Override
    public Document getDocument(String documentId) throws DocumentNotFoundException {
        String obj=jdbc.queryForObject("select XML_VALUE from BN_DOCUMENT_FILE where id=?", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(0);
            }
        }, documentId);
        return deSerialize(obj, documentId);
    }

    @Override
    public void deleteDocument(String documentId, boolean allVersions) throws DocumentNotFoundException {
        long oid=jdbc.queryForLong("select BLOB_VALUE from BN_DOCUMENT_FILE where id=?", documentId);
        jdbc.update("delete from BN_DOCUMENT_FILE where id=?", documentId);
        try {
            deleteOid(oid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] getContent(Document document) throws DocumentNotFoundException {
        if(StringUtils.isEmpty(document.getId()))
            return null;

        try {
            Long oid=jdbc.queryForLong("SELECT blob_value from bn_document_file where Id=?", document.getId());
            LargeObjectManager lobj = ((org.postgresql.PGConnection)jdbc.getDataSource().getConnection()).getLargeObjectAPI();
            if(oid>0){
                LargeObject obj = lobj.open(oid, LargeObjectManager.READ);
                byte buf[] = new byte[obj.size()];
                obj.read(buf, 0, obj.size());
                // Do something with the data read here
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                try {
                    IOUtils.copy(obj.getInputStream(), stream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                obj.close();
                buf = stream.toByteArray();
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return buf;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public List<Document> getVersionsOfDocument(String documentId) throws DocumentNotFoundException {
        return null;
    }

    @Override
    public Document createVersion(String documentId, boolean isMajorVersion, String fileName, String mimeType, byte[] content) throws DocumentationCreationException {
        long oid=jdbc.queryForLong("select BLOB_VALUE from BN_DOCUMENT_FILE where id=?", documentId);
        updateOid(content, oid);
        return null;
    }
    private long updateOid(byte[] content, long oid) {

        try {
            LargeObjectManager lobj = deleteOid(oid);
            oid = lobj.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
            LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);
            storeDataToOid(content, obj);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return oid;
    }

    private LargeObjectManager deleteOid(long oid) throws SQLException {
        LargeObjectManager lobj = ((org.postgresql.PGConnection)jdbc.getDataSource().getConnection()).getLargeObjectAPI();
        lobj.delete(oid);
        return lobj;
    }

    private long createOid(byte[] content) {
        long oid=0;
        if(content==null)
            return 0;
        try {
            LargeObjectManager lobj = ((org.postgresql.PGConnection)jdbc.getDataSource().getConnection()).getLargeObjectAPI();

            oid = lobj.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
            // Open the large object for writing
            LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);
            storeDataToOid(content, obj);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return oid;
    }

    private void storeDataToOid(byte[] content, LargeObject obj) throws SQLException {
        ByteArrayInputStream bis=new ByteArrayInputStream(content);
        // Copy the data from the file to the large object
        byte buf[] = new byte[2048];
        int s, tl = 0;
        while ((s = bis.read(buf, 0, 2048)) > 0) {
            obj.write(buf, 0, s);
            tl += s;
        }
        obj.close();
    }

    @Override
    public SearchResult search(DocumentSearchBuilder builder, int fromResult, int maxResults) {
        HashMap<DocumentIndex, DocumentCriterion> crits = new HashMap<DocumentIndex, DocumentCriterion>();
        for (Object o : builder.getQuery()) {
            if (o instanceof String) {
            }
            else if (o instanceof DocumentCriterion) {
                DocumentCriterion dc = (DocumentCriterion) o;
                crits.put(dc.getField(), dc);
            }
        }
        ProcessInstanceUUID processInstanceUUID=null;
        ProcessDefinitionUUID processDefinitionUUID=null;

        String procDefString = crits.containsKey(DocumentIndex.PROCESS_DEFINITION_UUID) ? crits.get(DocumentIndex.PROCESS_DEFINITION_UUID).getValue().toString() : null;
        String procInstString = crits.containsKey(DocumentIndex.PROCESS_INSTANCE_UUID) ? crits.get(DocumentIndex.PROCESS_INSTANCE_UUID).getValue().toString() : "DEFINITION_LEVEL_DOCUMENT";
        String attachmentName = crits.containsKey(DocumentIndex.NAME) ? crits.get(DocumentIndex.NAME).getValue().toString() : null;
        List<Document> files = null;
        final String userId = EnvTool.getUserId();

        files= (List<Document>) jdbc.query("select id, document_name, definition_uuid, instance_uuid, last_modified_by, creation_date, last_modification_date, file_name, content_mime_type, content_size from BN_DOCUMENT_FILE where DOCUMENT_NAME=? and definition_uuid=? and instance_uuid=?", new RowMapper<Document>() {

            @Override
            public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
                Document document;
                ProcessDefinitionUUID definition_uuid = new ProcessDefinitionUUID(rs.getString("definition_uuid"));
                ProcessInstanceUUID instance_uuid = new ProcessInstanceUUID(rs.getString("instance_uuid"));
                document = new DocumentImpl(rs.getString("document_name"), null, rs.getString("last_modified_by"), rs.getDate("creation_date"), rs.getDate("last_modification_date"), true, true,null, null, rs.getString("file_name"), rs.getString("content_mime_type"), rs.getLong("content_size"), definition_uuid, instance_uuid);
                document.setId(Long.toString(rs.getLong("id")));
                return document;
            }
        }, attachmentName, procDefString, procInstString);
        if(files.size()==0 && attachmentName!=null){
            try {
                files = getProcessDefinitionDocuments(new ProcessInstanceUUID(procInstString), new ProcessDefinitionUUID(procDefString), attachmentName);
            } catch (DocumentationCreationException e) {
                LOG.severe(e.getMessage());
            }
        }
        return new SearchResult(files, files.size());
    }

    @Override
    public void attachDocumentTo(ProcessDefinitionUUID processDefinitionUUID, ProcessInstanceUUID processInstanceUUID, String documentId) throws DocumentNotFoundException {

        Document doc = getDocument(documentId);
        byte[] fileContent = getContent(doc);
        try {
            createDocument(doc.getName(), processDefinitionUUID, processInstanceUUID, doc.getContentFileName(), doc.getContentMimeType(), fileContent );
        } catch (DocumentAlreadyExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentationCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
