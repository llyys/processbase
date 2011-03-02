/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.processbase.bam.db;

import java.util.Iterator;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Mappings;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.type.TypeFactory;

/**
 *
 * @author marat
 */
public class ScriptGenerator {

    public static final int CREATE_SCRIPT = 0;
    public static final int DROP_SCRIPT = 1;

    public String getCreateTableScript(MetaKpi metaKpi, String dialect, int type) {
        try {
            Configuration config = new Configuration();

            Dialect d = (Dialect) Class.forName(dialect).newInstance();

            Mappings mappings = config.createMappings();
            Table table = mappings.addTable(null, null, metaKpi.getCode(), null, false);

            Column id = new Column("ID");
            SimpleValue idVal = new SimpleValue(table);
            idVal.setTypeName(TypeFactory.basic("long").getName());
            id.setValue(idVal);
//            id.setUnique(true);

            Column kpiCode = new Column("KPI_CODE");
            SimpleValue nameVal = new SimpleValue(table);
            nameVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            kpiCode.setValue(nameVal);
            kpiCode.setLength(16);

            Column kpiTimeStamp = new Column("KPI_TIMESTAMP");
            kpiTimeStamp.setSqlType(TypeFactory.basic(java.util.Date.class.getName()).getName());

            Column kpiYear = new Column("KPI_YEAR");
            SimpleValue kpiYearVal = new SimpleValue(table);
            kpiYearVal.setTypeName(TypeFactory.basic("short").getName());
            kpiYear.setLength(4);
            kpiYear.setValue(kpiYearVal);

            Column kpiQuater = new Column("KPI_QUATER");
            SimpleValue kpiQuaterVal = new SimpleValue(table);
            kpiQuaterVal.setTypeName(TypeFactory.basic("short").getName());
            kpiQuater.setLength(4);
            kpiQuater.setValue(kpiQuaterVal);

            Column kpiMonth = new Column("KPI_MONTH");
            SimpleValue kpiMonthVal = new SimpleValue(table);
            kpiMonthVal.setTypeName(TypeFactory.basic("short").getName());
            kpiMonth.setLength(4);
            kpiMonth.setValue(kpiMonthVal);

            Column kpiWeek = new Column("KPI_WEEK");
            SimpleValue kpiWeekVal = new SimpleValue(table);
            kpiWeekVal.setTypeName(TypeFactory.basic("short").getName());
            kpiWeek.setLength(4);
            kpiWeek.setValue(kpiWeekVal);

            Column kpiDay = new Column("KPI_DAY");
            SimpleValue kpiDayVal = new SimpleValue(table);
            kpiDayVal.setTypeName(TypeFactory.basic("short").getName());
            kpiDay.setLength(4);
            kpiDay.setValue(kpiDayVal);

            Column kpiDayOfWeek = new Column("KPI_DAY_OF_WEEK");
            SimpleValue kpiDayOfWeekVal = new SimpleValue(table);
            kpiDayOfWeekVal.setTypeName(TypeFactory.basic("short").getName());
            kpiDayOfWeek.setLength(4);
            kpiDayOfWeek.setValue(kpiDayOfWeekVal);


            Column serverId = new Column("SERVER_ID");
            SimpleValue serverIdVal = new SimpleValue(table);
            serverIdVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            serverId.setValue(serverIdVal);
            serverId.setLength(200);

            Column eventId = new Column("EVENT_ID");
            SimpleValue eventIdVal = new SimpleValue(table);
            eventIdVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            eventId.setValue(eventIdVal);
            eventId.setLength(200);

            Column eventName = new Column("EVENT_NAME");
            SimpleValue eventNameVal = new SimpleValue(table);
            eventNameVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            eventName.setValue(eventNameVal);
            eventName.setLength(200);

            Column processDefinitionId = new Column("PROCESS_DEF_ID");
            SimpleValue processDefinitionIdVal = new SimpleValue(table);
            processDefinitionIdVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            processDefinitionId.setValue(processDefinitionIdVal);
            processDefinitionId.setLength(200);

            Column processDefinitionName = new Column("PROCESS_DEF_NAME");
            SimpleValue processDefinitionNameVal = new SimpleValue(table);
            processDefinitionNameVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            processDefinitionName.setValue(processDefinitionNameVal);
            processDefinitionName.setLength(200);

            Column processDefinitionVersion = new Column("PROCESS_DEF_VERSION");
            SimpleValue processDefinitionVersionVal = new SimpleValue(table);
            processDefinitionVersionVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            processDefinitionVersion.setValue(processDefinitionVersionVal);
            processDefinitionVersion.setLength(200);

            Column processInstanceId = new Column("PROCESS_INST_ID");
            SimpleValue processInstanceIdVal = new SimpleValue(table);
            processInstanceIdVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            processInstanceId.setValue(processInstanceIdVal);
            processInstanceId.setLength(200);

            Column activityInstanceId = new Column("ACT_INST_ID");
            SimpleValue activityInstanceIdVal = new SimpleValue(table);
            activityInstanceIdVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            activityInstanceId.setValue(activityInstanceIdVal);
            activityInstanceId.setLength(200);

            Column activityInstanceName = new Column("ACT_INST_NAME");
            SimpleValue activityInstanceNameVal = new SimpleValue(table);
            activityInstanceNameVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            activityInstanceName.setValue(activityInstanceNameVal);
            activityInstanceName.setLength(200);

            Column activityInstanceIter = new Column("ACT_INST_ITER");
            SimpleValue activityInstanceIterVal = new SimpleValue(table);
            activityInstanceIterVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            activityInstanceIter.setValue(activityInstanceIterVal);
            activityInstanceIter.setLength(200);

            PrimaryKey key = new PrimaryKey();
            key.setName("PK_" + metaKpi.getCode());
            key.addColumn(id);
            table.setPrimaryKey(key);

            table.addColumn(id);
            table.addColumn(kpiCode);
            table.addColumn(kpiTimeStamp);
            table.addColumn(kpiYear);
            table.addColumn(kpiQuater);
            table.addColumn(kpiMonth);
            table.addColumn(kpiDay);
            table.addColumn(kpiDayOfWeek);
            table.addColumn(eventId);
            table.addColumn(eventName);
            table.addColumn(processDefinitionId);
            table.addColumn(processDefinitionName);
            table.addColumn(processInstanceId);
            table.addColumn(activityInstanceId);
            table.addColumn(activityInstanceName);
            table.addColumn(activityInstanceIter);

            for (Iterator<MetaDim> i = metaKpi.getMetaDims().iterator(); i.hasNext();) {
                MetaDim metaDim = i.next();
                Column dim = new Column(metaDim.getCode());
                SimpleValue dimVal = new SimpleValue(table);
                dimVal.setTypeName(TypeFactory.basic(metaDim.getValueType()).getName());
                dim.setValue(dimVal);
                dim.setLength(metaDim.getValueLength());
                table.addColumn(dim);
                
                Index index = new Index();
                index.setName("IX_" + metaDim.getCode());
                index.addColumn(dim);
                table.addIndex(index);
            }

            for (Iterator<MetaFact> i = metaKpi.getMetaFacts().iterator(); i.hasNext();) {
                MetaFact metaFact = i.next();
                Column dim = new Column(metaFact.getCode());
                SimpleValue dimVal = new SimpleValue(table);
                dimVal.setTypeName(TypeFactory.basic("java.math.BigDecimal").getName());
                dim.setValue(dimVal);
                table.addColumn(dim);
            }

            StringBuilder result = new StringBuilder();
            if (type == CREATE_SCRIPT) {
                result.append(table.sqlCreateString(d, config.buildMapping(), null, null));
            } else if (type == DROP_SCRIPT) {
                result.append(table.sqlDropString(d, null, null));
            }
            return result.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
