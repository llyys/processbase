package org.processbase.bonita.services.impl.db;

import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.SQLException;

public class LargeDbDataDao {
    JdbcTemplate jdbc=null;
    public LargeDbDataDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public byte[] readObject(long id) throws SQLException {
        LargeObject obj = getLargeObjectManager().open(id, LargeObjectManager.READ);
        byte buf[] = new byte[obj.size()];
        obj.read(buf, 0, obj.size());
        return buf;
    }

    public long createOid(byte[] content) {
        long oid=0;
        if(content==null)
            return 0;
        try {
            LargeObjectManager lobj = getLargeObjectManager();
            oid = lobj.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
            LargeObject obj = lobj.open(oid, LargeObjectManager.WRITE);
            storeDataToOid(content, obj);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return oid;
    }
    private Connection connection;
    private LargeObjectManager getLargeObjectManager() {
        try {

            connection = jdbc.getDataSource().getConnection();
            connection.setAutoCommit(false);
            return ((PGConnection)connection).getLargeObjectAPI();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void storeDataToOid(byte[] content, LargeObject obj) throws SQLException {
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

    public long updateOid(byte[] content, long oid) {

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

    public LargeObjectManager deleteOid(long oid) throws SQLException {
        LargeObjectManager lobj = getLargeObjectManager();
        lobj.delete(oid);
        return lobj;
    }
}
