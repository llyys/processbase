package org.processbase.bonita.services.impl.db;

import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class OidConnection implements Closeable{
    private LargeObjectManager largeObjectManager;
    private JdbcTemplate jdbc;
    private Connection connection;
    public OidConnection(JdbcTemplate jdbc) throws SQLException {
        this.jdbc = jdbc;
        connection = jdbc.getDataSource().getConnection();
        connection.setAutoCommit(false);
        largeObjectManager = ((PGConnection)connection).getLargeObjectAPI();
    }



    @Override
    public void close() throws IOException {
        if(connection!=null)
            try {
                largeObjectManager=null;
                connection.close();
                connection=null;
            } catch (SQLException e) {
                e.printStackTrace();
            }

    }

    public void commit(){
        if(connection!=null)
            try {
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }

    }

    public static void closeQuietly(OidConnection connection){
        if(connection!=null)
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LargeObjectManager getLargeObjectManager() {
        return largeObjectManager;
    }

    public static void commitAndClose(OidConnection oidConnection) {
        oidConnection.commit();
        closeQuietly(oidConnection);
    }
}
