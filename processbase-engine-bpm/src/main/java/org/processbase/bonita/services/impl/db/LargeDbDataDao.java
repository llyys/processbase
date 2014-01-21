package org.processbase.bonita.services.impl.db;

import org.apache.commons.io.IOUtils;
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
        OidConnection oidConnection = null;
        try {
            oidConnection = new OidConnection(this.jdbc);
            LargeObject obj = oidConnection.getLargeObjectManager().open(id, LargeObjectManager.READ);
            byte buf[] = new byte[obj.size()];
            obj.read(buf, 0, obj.size());
            return buf;
        }
        finally {
            OidConnection.commitAndClose(oidConnection);
        }
    }

    public long createOid(byte[] content) {
        long oid=0;
        OidConnection oidConnection = null;
        if(content==null) {
            return 0;
        }
        try {
            oidConnection = new OidConnection(this.jdbc);

            oid = oidConnection.getLargeObjectManager().createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
            LargeObject obj = oidConnection.getLargeObjectManager().open(oid, LargeObjectManager.WRITE);
            storeDataToOid(content, obj);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            OidConnection.commitAndClose(oidConnection);
        }

        return oid;
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
        OidConnection oidConnection = null;
        try {
            oidConnection=new OidConnection(jdbc);
            deleteOid(oid, oidConnection);
            LargeObjectManager manager = oidConnection.getLargeObjectManager();
            oid = manager.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);
            LargeObject obj = manager.open(oid, LargeObjectManager.WRITE);
            storeDataToOid(content, obj);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            OidConnection.commitAndClose(oidConnection);
        }
        return oid;
    }

    public void deleteOid(long oid, OidConnection oidConnection) throws SQLException {
           oidConnection.getLargeObjectManager().delete(oid);
    }
}
