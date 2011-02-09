/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.processbase.bonita.services.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.ow2.bonita.services.LargeDataRepository;

/**
 *
 * @author marat
 */
public class DBLargeDataRepository implements LargeDataRepository {

    private static final Object CONSTRUCTOR_MUTEX = new Object();
    private String poolName = null;

    public DBLargeDataRepository(final String value) {
        poolName = value;
    }

    public void storeData(List<String> categories, String key, Serializable value, boolean overWrite) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean deleteData(List<String> list, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean deleteData(List<String> list) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clean() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<String> getKeys() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<String> getKeys(List<String> list) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<String> getKeys(List<String> list, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T getData(Class<T> type, List<String> list, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> Map<String, T> getData(Class<T> type, List<String> list) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> Map<String, T> getData(Class<T> type, List<String> list, Collection<String> clctn) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> Map<String, T> getDataFromRegex(Class<T> type, List<String> list, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Connection getConnection() {
        Connection con = null;
        try {
            InitialContext context = new InitialContext();
            //Look up our data source
            DataSource ds = (DataSource) context.lookup(poolName);
            //Allocate and use a connection from the pool
            con = ds.getConnection();
        } catch (SQLException e) {
            Logger.getLogger(DBLargeDataRepository.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        } catch (Exception e) {
            Logger.getLogger(DBLargeDataRepository.class.getName()).log(Level.SEVERE, e.getMessage());
        }
        return con;
    }
}
