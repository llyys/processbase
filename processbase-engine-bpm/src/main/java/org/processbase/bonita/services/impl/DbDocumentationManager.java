package org.processbase.bonita.services.impl;

import liquibase.Liquibase;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.apache.commons.lang.StringUtils;
import org.ow2.bonita.facade.def.element.AttachmentDefinition;
import org.ow2.bonita.facade.exception.DocumentAlreadyExistsException;
import org.ow2.bonita.facade.exception.DocumentNotFoundException;
import org.ow2.bonita.facade.exception.DocumentationCreationException;
import org.ow2.bonita.facade.impl.SearchResult;
import org.ow2.bonita.facade.uuid.AbstractUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.search.DocumentCriterion;
import org.ow2.bonita.search.DocumentSearchBuilder;
import org.ow2.bonita.search.index.DocumentIndex;
import org.ow2.bonita.services.Document;
import org.ow2.bonita.services.DocumentationManager;
import org.ow2.bonita.util.BonitaConstants;
import org.ow2.bonita.util.EnvTool;
import org.ow2.bonita.util.Misc;
import org.processbase.bonita.services.impl.db.DbDocument;
import org.processbase.bonita.services.impl.db.LargeDbDataDao;
import org.processbase.bonita.services.impl.db.OidConnection;
import org.processbase.bonita.services.impl.filedocument.AttachmentInstance;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class DbDocumentationManager extends AbstractDocumentationManager {
    private static final Logger LOG = Logger.getLogger(DocumentationManager.class.getName());

    JdbcTemplate jdbc=null;
    LargeDbDataDao oidDao=null;
    DbDocument active;
    public DbDocumentationManager(String value) throws Exception {
        Properties properties=loadProperties(value);
        String driver=properties.getProperty("hibernate.connection.driver_class");
        String url=properties.getProperty("hibernate.connection.url");
        String username=properties.getProperty("hibernate.connection.username");
        String password=properties.getProperty("hibernate.connection.password");

        PostgresDatabase db = new PostgresDatabase();

        JdbcConnection connection = null;
        try {
            Driver driverInstance= (Driver) Driver.class.forName(driver).newInstance();
            SimpleDriverDataSource dataSource = new SimpleDriverDataSource(driverInstance, url, username, password);

            jdbc=new JdbcTemplate(dataSource);
            oidDao = new LargeDbDataDao(jdbc);

            connection = new JdbcConnection(dataSource.getConnection());
            db.setConnection(connection);
            ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();

            Liquibase lq = new Liquibase("liqubase-master.xml", resourceAccessor, connection);
            lq.update("");

            mimeProperties= ResourceBundle.getBundle("mime");

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            connection.close();
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

    public static String getMd5(byte[] data){
        MessageDigest m = null;
        try {
            if(data==null || data.length==0)
                return null;
            m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(data);
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            String hashtext = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while(hashtext.length() < 32 ){
                hashtext = "0"+hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    @Override
    public Document createDocument(final String name, ProcessDefinitionUUID definitionUUID, ProcessInstanceUUID instanceUUID, String fileName, String contentMimeType, final byte[] fileContent) throws DocumentationCreationException {
        String md5 = getMd5(fileContent);
        //if(instanceUUID==null)
        //    instanceUUID=new ProcessInstanceUUID(DEFINITION_LEVEL_DOCUMENT);
        DbDocument d = (DbDocument) getDocument(name, definitionUUID, instanceUUID);
        if(d!=null)
        {
            if(!StringUtils.equalsIgnoreCase(md5, d.getHash())){
                long oid = oidDao.createOid(fileContent);

                d.setHash(md5);
                d.setOid(oid);
                d.setContentSize(fileContent == null ? 0 : fileContent.length);
                d.setContentMimeType(contentMimeType);
                d.setContentFileName(fileName);
                d.setLastModificationDate(new Date());
                d.setLastModifiedBy(EnvTool.getUserId());

                UpdateDocument(d);
            }
            return d;
        }

        final DbDocument doc = new DbDocument(name, null, EnvTool.getUserId(), new Date(), new Date(), true, true, null, null, fileName, contentMimeType, fileContent == null ? 0 : fileContent.length, definitionUUID, instanceUUID);

        Connection connection = null;
        try {
            long id=jdbc.queryForLong("select nextval('BN_DOCUMENT_FILE_SEQ')");
            doc.setId(Long.toString(id));
            connection = jdbc.getDataSource().getConnection();

            long oid=0;
            String hash="";
            if(active!=null)
            {
                oid=active.getOid();
                hash=active.getHash();
            }


            if(fileContent!=null && fileContent.length>0 && !hash.equalsIgnoreCase(md5))
            {
               oid = oidDao.createOid(fileContent);
            }

            doc.setOid(oid);
            doc.setHash(md5);
            if(getDocument(name, definitionUUID, instanceUUID)==null){

                doc.setLastModificationDate(new Date());
                doc.setLastModifiedBy(EnvTool.getUserId());

                jdbc.update("insert into BN_DOCUMENT_FILE (" +
                    "id, document_name, definition_uuid, instance_uuid, last_modified_by, " +
                    "creation_date, last_modification_date, file_name, content_mime_type, " +
                    "content_size, content_hash, blob_value, xml_value, author) " +
                    " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                    , id
                    , name
                    , getUuidValue(doc.getProcessDefinitionUUID())
                    , getUuidValue(doc.getProcessInstanceUUID())
                    , doc.getLastModifiedBy()
                    , doc.getCreationDate()
                    , doc.getLastModificationDate()
                    , doc.getContentFileName()
                    , doc.getContentMimeType()
                    , doc.getContentSize()
                    , doc.getHash()
                    , doc.getOid()
                    , doc.toString()
                    , doc.getAuthor()
                );

            }

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeConnection(connection);
        }
        return doc;
    }

    private void closeConnection(Connection connection) {
        if(connection!=null)
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }


    private void UpdateDocument(DbDocument d) {

        long id=Long.parseLong(d.getId());
        int result=jdbc.update("update BN_DOCUMENT_FILE set " +
                "file_name = ?, last_modified_by = ?, last_modification_date=?, content_mime_type=?, " +
                "content_size=?, content_hash=?, blob_value=?, xml_value=?" +
                " WHERE id =?"
            , d.getContentFileName()
            , d.getLastModifiedBy()
            , d.getLastModificationDate()
            , d.getContentMimeType()
            , d.getContentSize()
            , d.getHash()
            , d.getOid()
            , d.toString()
            , id
        );
    }


    @Override
    public Document getDocument(String documentId) throws DocumentNotFoundException {
        String obj=jdbc.queryForObject("select XML_VALUE from BN_DOCUMENT_FILE where id=?", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1);
            }
        }, new Long(documentId));
        return DbDocument.deSerialize(obj);
    }

    public Document getDocument(String name, ProcessDefinitionUUID definitionUUID, ProcessInstanceUUID instanceUUID) {
        try{
            String obj=jdbc.queryForObject("select XML_VALUE from BN_DOCUMENT_FILE where document_name=? and definition_uuid=? and instance_uuid = ?", new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString(1);
                    }
                },
                name,
                getUuidValue(definitionUUID),
                getUuidValue(instanceUUID));

            return DbDocument.deSerialize(obj);
        }catch (IncorrectResultSizeDataAccessException ex){
            return null;
        }
    }

    @Override
    public void deleteDocument(String documentId, boolean allVersions) throws DocumentNotFoundException {
        long oid=jdbc.queryForLong("select BLOB_VALUE from BN_DOCUMENT_FILE where id=?", documentId);
        jdbc.update("delete from BN_DOCUMENT_FILE where id=?", documentId);
        OidConnection oidConnection = null;
        try {
            oidConnection=new OidConnection(jdbc);
            oidDao.deleteOid(oid, oidConnection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
           OidConnection.commitAndClose(oidConnection);
        }
    }

    public String getUuidValue(AbstractUUID uuid){
        if(uuid==null || uuid.getValue()==null)
            return "NULL";
        return uuid.getValue();
    }

    @Override
    public byte[] getContent(Document document) throws DocumentNotFoundException {

        try {
            active = (DbDocument) document;
            ProcessDefinitionUUID definitionUUID = document.getProcessDefinitionUUID();
            ProcessInstanceUUID processInstanceUUID = document.getProcessInstanceUUID();
            //Long oid=jdbc.queryForLong("SELECT blob_value from bn_document_file where document_name=? and definition_uuid=? and COALESCE(instance_uuid, '') = ?", document.getName(), getUuidValue(definitionUUID), getUuidValue(processInstanceUUID));

            if(active.getOid()>0){
                return oidDao.readObject(active.getOid());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    @Override
    public List<Document> getVersionsOfDocument(String documentId) throws DocumentNotFoundException {
        List<Document> files = new ArrayList<Document>();
        files.add(getDocument(documentId));
        return files;
    }

    @Override
    protected List<Document> getProcessDefinitionDocuments(ProcessInstanceUUID processInstanceUUID, ProcessDefinitionUUID processDefinitionUUID, String attachmentName) throws DocumentationCreationException {
        List<Document> files = new ArrayList<Document>();//try load attachments from processdefinition and initialize.
        //this happens when document repository does not have documents in it but

        AttachmentInstance attachemnt=getLargeDataRepositoryAttachment(processDefinitionUUID, attachmentName);

        if(attachemnt!=null){
            String fileType=null;
            String mimeType=null;
            AttachmentDefinition attachmentDefinition = attachemnt.getAttachmentDefinition();
            if(attachmentDefinition.getFileName()!=null){
                fileType=attachmentDefinition.getFileName().substring(attachmentDefinition.getFileName().lastIndexOf('.')+1);
                mimeType=mimeProperties.getString(fileType);
            }
            active=null;
            //DbDocument document = new DbDocument(attachmentDefinition.getName(), null, EnvTool.getUserId(), new Date(), new Date(), true, true, null, null, attachmentDefinition.getFileName(), mimeType, 0, processDefinitionUUID, null);
            DbDocument document = (DbDocument) createDocument(attachmentName, processDefinitionUUID,processInstanceUUID , attachmentDefinition.getFileName(), mimeType, attachemnt.getData());
            files.add(document);
        }
        return files;
    }

    @Override
    public Document createVersion(String documentId, boolean isMajorVersion, String fileName, String mimeType, byte[] content) throws DocumentationCreationException {

        DbDocument doc = null;
        try {
            doc = (DbDocument) getDocument(documentId);
            doc.setContentFileName(fileName);
            doc.setContentMimeType(mimeType);
            long oid = oidDao.updateOid(content, doc.getOid());
            doc.setOid(oid);
            doc.setContentSize(content.length);
            String md5 = getMd5(content);
            doc.setHash(md5);
            UpdateDocument(doc);

        } catch (DocumentNotFoundException e) {
            e.printStackTrace();
        }


        return doc;
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

        String instId = crits.containsKey(DocumentIndex.PROCESS_INSTANCE_UUID) ? crits.get(DocumentIndex.PROCESS_INSTANCE_UUID).getValue().toString() : null;
        if(StringUtils.isNotEmpty(instId))
            processInstanceUUID = new ProcessInstanceUUID(instId);

        String defId = crits.containsKey(DocumentIndex.PROCESS_DEFINITION_UUID) ? crits.get(DocumentIndex.PROCESS_DEFINITION_UUID).getValue().toString() : null;
        if(StringUtils.isNotEmpty(defId))
            processDefinitionUUID=new ProcessDefinitionUUID(defId);

        if(processDefinitionUUID==null)
            processDefinitionUUID=getDefinitionFromInstance(processInstanceUUID);



        String attachmentName = crits.containsKey(DocumentIndex.NAME) ? crits.get(DocumentIndex.NAME).getValue().toString() : null;
        List<Document> files = null;
        final String userId = EnvTool.getUserId();

        List<Object> params=new ArrayList<Object>();
        List<String> paramKeys=new ArrayList<String>();
        paramKeys.add(" definition_uuid = ? ");
        params.add(getUuidValue(processDefinitionUUID));
        if(attachmentName!=null)
        {
            params.add(attachmentName);
            paramKeys.add(" DOCUMENT_NAME = ? ");
        }
        if(processInstanceUUID!=null){
           params.add(getUuidValue(processInstanceUUID));
           paramKeys.add(" instance_uuid = ? ");
        }
        Object[] objects = params.toArray();

        files= (List<Document>) jdbc.query("select id, document_name, definition_uuid, instance_uuid, last_modified_by, creation_date, last_modification_date, file_name, content_mime_type, content_size, content_hash, blob_value from BN_DOCUMENT_FILE where "+ StringUtils.join(paramKeys.toArray(), " and "), new RowMapper<Document>() {

            @Override
            public Document mapRow(ResultSet rs, int rowNum) throws SQLException {
                DbDocument document;
                ProcessDefinitionUUID definition_uuid = new ProcessDefinitionUUID(rs.getString("definition_uuid"));
                String uid = rs.getString("instance_uuid");
                ProcessInstanceUUID instance_uuid = null;
                if (!StringUtils.isEmpty(uid) && !"NULL".equalsIgnoreCase(uid))
                    instance_uuid = new ProcessInstanceUUID(uid);

                document = new DbDocument(rs.getString("document_name"), null, rs.getString("last_modified_by"), rs.getDate("creation_date"), rs.getDate("last_modification_date"), true, true, null, null, rs.getString("file_name"), rs.getString("content_mime_type"), rs.getLong("content_size"), definition_uuid, instance_uuid);
                document.setId(Long.toString(rs.getLong("id")));
                document.setOid(rs.getLong("blob_value"));
                document.setHash(rs.getString("content_hash"));
                return document;
            }
        }, objects);
        if(files.size()==0 && attachmentName!=null){
            try {
                Document doc=getDocument(attachmentName, processDefinitionUUID, processInstanceUUID);
                if(doc!=null)
                    files.add(doc);
                else
                    files = getProcessDefinitionDocuments(processInstanceUUID, processDefinitionUUID, attachmentName);
            } catch (DocumentationCreationException e) {
                LOG.severe(e.getMessage());
            }
        }
        return new SearchResult(files, files.size());
    }

    private ProcessDefinitionUUID getDefinitionFromInstance(ProcessInstanceUUID processInstanceUUID) {
        return processInstanceUUID.getProcessDefinitionUUID();
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
