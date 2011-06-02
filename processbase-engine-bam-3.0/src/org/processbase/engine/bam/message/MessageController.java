/**
 * Copyright (C) 2011 PROCESSBASE Ltd.
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
package org.processbase.engine.bam.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.processbase.engine.bam.BAMConstants;
import org.processbase.engine.bam.db.HibernateUtil;

/**
 *
 * @author marat
 */
public class MessageController {

    static private class sqlTimestampConverter implements JsonSerializer<Timestamp> {

        static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

        public JsonElement serialize(Timestamp t, Type type, JsonSerializationContext jsc) {
            return new JsonPrimitive(sdf.format(t));
        }
    }

    public static String kpiToJson(Kpi kpi) {
        GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        gb.registerTypeAdapter(Timestamp.class, new sqlTimestampConverter());
        Gson gson = gb.create();
        return gson.toJson(kpi);
    }

    public static Kpi jsonToKpi(String kpiString) {
        GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        gb.registerTypeAdapter(Timestamp.class, new sqlTimestampConverter());
        Gson gson = gb.create();
        return gson.fromJson(kpiString, Kpi.class);
    }

    public static String kpiToXml(Kpi kpi) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(kpi.getClass());
        Marshaller marshaller = context.createMarshaller();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshal(kpi, baos);
        return new String(baos.toByteArray());
    }

    public static Kpi xmlToKpi(String xmlString) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Kpi.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (Kpi) unmarshaller.unmarshal(new ByteArrayInputStream(xmlString.getBytes()));
    }

    public static void sendKpiToMQ(Kpi kpi) throws JMSException, NamingException {
        Context ctx = null;
        javax.jms.QueueConnection connection = null;
        javax.jms.QueueSession session = null;
        try {
            ctx = new InitialContext();
            QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) ctx.lookup(BAMConstants.BAM_MQ_CONNECTION_FACTORY);
            Queue jmsQueue = (Queue) ctx.lookup(BAMConstants.BAM_MQ_DESTINATION_RESOURCE);
            connection = queueConnectionFactory.createQueueConnection();
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createSender(jmsQueue);
            messageProducer.send(session.createObjectMessage((Serializable) kpi));
//            session.commit();
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (connection != null) {
                connection.close();
            }
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    public static void saveKpi(Kpi kpi) {
        Connection conn = null;
        try {
            StringBuilder sqlScript = new StringBuilder();
            ArrayList values = new ArrayList();
            sqlScript.append("INSERT INTO ").append(kpi.getCode()).append(" (ID, ");
            if (kpi.getServerID() != null) {
                sqlScript.append("SERVER_ID,");
                values.add(kpi.getServerID());
            }
            if (kpi.getTimeStamp() != null) {
                sqlScript.append("KPI_TIMESTAMP, KPI_YEAR, KPI_QUATER, KPI_MONTH, KPI_WEEK, KPI_DAY, KPI_DAY_OF_WEEK, KPI_HOUR, KPI_MINUTE, ");
                values.add(kpi.getTimeStamp());
                Calendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(kpi.getTimeStamp().getTime());
                values.add(calendar.get(Calendar.YEAR));
                double month = calendar.get(Calendar.MONTH) + 1;
                values.add(Math.ceil(month / 3));
                values.add(calendar.get(Calendar.MONTH) + 1);
                values.add(calendar.get(Calendar.WEEK_OF_YEAR));
                values.add(calendar.get(Calendar.DAY_OF_MONTH));
                values.add(calendar.get(Calendar.DAY_OF_WEEK) - 1);
                values.add(calendar.get(Calendar.HOUR) + (calendar.get(Calendar.AM_PM) == Calendar.PM ? 12 : 0));
                values.add(calendar.get(Calendar.MINUTE));
            }
            if (kpi.getEventID() != null) {
                sqlScript.append("EVENT_ID,");
                values.add(kpi.getEventID());
            }
            if (kpi.getEventName() != null) {
                sqlScript.append("EVENT_NAME,");
                values.add(kpi.getEventName());
            }
            if (kpi.getProcess() != null) {
                if (kpi.getProcess().getProcessDefinitionID() != null) {
                    sqlScript.append("PROCESS_DEF_ID,");
                    values.add(kpi.getProcess().getProcessDefinitionID());
                }
                if (kpi.getProcess().getProcessDefinitionName() != null) {
                    sqlScript.append("PROCESS_DEF_NAME,");
                    values.add(kpi.getProcess().getProcessDefinitionName());
                }
                if (kpi.getProcess().getProcessDefinitionVersion() != null) {
                    sqlScript.append("PROCESS_DEF_VERSION,");
                    values.add(kpi.getProcess().getProcessDefinitionVersion());
                }
                if (kpi.getProcess().getProcessInstanceID() != null) {
                    sqlScript.append("PROCESS_INST_ID,");
                    values.add(kpi.getProcess().getProcessInstanceID());
                }
                if (kpi.getProcess().getActivityInstanceID() != null) {
                    sqlScript.append("ACT_INST_ID,");
                    values.add(kpi.getProcess().getActivityInstanceID());
                }
                if (kpi.getProcess().getActivityName() != null) {
                    sqlScript.append("ACT_INST_NAME,");
                    values.add(kpi.getProcess().getActivityName());
                }
                if (kpi.getProcess().getActivityIteration() != null) {
                    sqlScript.append("ACT_INST_ITER,");
                    values.add(kpi.getProcess().getActivityIteration());
                }
            }
            if (kpi.getFacts() != null && !kpi.getFacts().isEmpty()) {
                for (FactType fact : kpi.getFacts()) {
                    sqlScript.append(fact.getCode()).append(", ");
                    values.add(fact.getValue());
                }
            }
            if (kpi.getDims() != null && !kpi.getDims().isEmpty()) {
                for (DimensionType dim : kpi.getDims()) {
                    sqlScript.append(dim.getCode()).append(",");
                    values.add(dim.getValue());
                }
            }

            sqlScript.replace(sqlScript.length() - 1, sqlScript.length(), ") VALUES (");
            for (int i = 0; i < values.size(); i++) {
                sqlScript.append("?,");
            }

            sqlScript.append("?)");
            conn = newConnection();

            PreparedStatement ps = conn.prepareStatement("SELECT PB_SEQUENCE.NEXTVAL from Dual");
            ps.execute();
            ps.getResultSet().next();
            Integer sequenceNextVal = ps.getResultSet().getInt(1);
            values.add(0, sequenceNextVal);

            ps = conn.prepareStatement(sqlScript.toString());
            for (int parIndex = 0; parIndex < values.size(); parIndex++) {
                if (values.get(parIndex) instanceof String) {
                    ps.setString(parIndex + 1, values.get(parIndex).toString());
                } else if (values.get(parIndex) instanceof java.sql.Timestamp) {
                    ps.setTimestamp(parIndex + 1, (java.sql.Timestamp) values.get(parIndex));
                } else if (values.get(parIndex) instanceof java.lang.Integer) {
                    ps.setInt(parIndex + 1, (java.lang.Integer) values.get(parIndex));
                } else if (values.get(parIndex) instanceof java.lang.Double) {
                    ps.setDouble(parIndex + 1, (java.lang.Double) values.get(parIndex));
                } else if (values.get(parIndex) instanceof java.math.BigDecimal) {
                    ps.setBigDecimal(parIndex + 1, (java.math.BigDecimal) values.get(parIndex));
                } else if (values.get(parIndex) instanceof java.lang.Long) {
                    ps.setLong(parIndex + 1, (java.lang.Long) values.get(parIndex));
                }
            }
            ps.execute();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
            }
        }
    }

    public static Connection newConnection() {
        Connection con = null;
        try {
            InitialContext context = new InitialContext();
            //Look up our data source
            //DataSource ds = (DataSource) context.lookup(BAMConstants.BAM_DB_POOLNAME);
            return HibernateUtil.getConfiguration().buildSettings().getConnectionProvider().getConnection();
            //Allocate and use a connection from the pool
            //con = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
             e.printStackTrace();
        }
        return con;
    }
}
