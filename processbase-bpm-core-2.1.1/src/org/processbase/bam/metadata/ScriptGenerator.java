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
package org.processbase.bam.metadata;

import java.util.ArrayList;
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
import org.processbase.core.Constants;

/**
 *
 * @author marat
 */
public class ScriptGenerator {

    public static final int CREATE_SCRIPT = 0;
    public static final int DROP_SCRIPT = 1;
    private ArrayList<String> result = new ArrayList<String>();
    private ArrayList<Column> dimColumns = new ArrayList<Column>(1);
    private Table table;

    public ArrayList<String> getCreateTableScript(MetaKpi metaKpi, int type) {

        try {
            Configuration config = new Configuration();

            Dialect d = (Dialect) Class.forName(Constants.BAM_DB_DIALECT).newInstance();

            Mappings mappings = config.createMappings();
            table = mappings.addTable(null, null, metaKpi.getCode(), null, false);

            Column id = new Column("ID");
            SimpleValue idVal = new SimpleValue(table);
            idVal.setTypeName(TypeFactory.basic("long").getName());
            id.setValue(idVal);
            

            Column kpiTimeStamp = new Column("KPI_TIMESTAMP");
            kpiTimeStamp.setSqlType(TypeFactory.basic(java.util.Date.class.getName()).getName());

            Column kpiYear = new Column("KPI_YEAR");
            SimpleValue kpiYearVal = new SimpleValue(table);
            kpiYearVal.setTypeName(TypeFactory.basic("short").getName());
            kpiYear.setLength(4);
            kpiYear.setValue(kpiYearVal);
            addIndex(kpiYear);

            Column kpiQuater = new Column("KPI_QUATER");
            SimpleValue kpiQuaterVal = new SimpleValue(table);
            kpiQuaterVal.setTypeName(TypeFactory.basic("short").getName());
            kpiQuater.setLength(4);
            kpiQuater.setValue(kpiQuaterVal);
            addIndex(kpiQuater);

            Column kpiMonth = new Column("KPI_MONTH");
            SimpleValue kpiMonthVal = new SimpleValue(table);
            kpiMonthVal.setTypeName(TypeFactory.basic("short").getName());
            kpiMonth.setLength(4);
            kpiMonth.setValue(kpiMonthVal);
            addIndex(kpiMonth);

            Column kpiWeek = new Column("KPI_WEEK");
            SimpleValue kpiWeekVal = new SimpleValue(table);
            kpiWeekVal.setTypeName(TypeFactory.basic("short").getName());
            kpiWeek.setLength(4);
            kpiWeek.setValue(kpiWeekVal);
            addIndex(kpiWeek);

            Column kpiDay = new Column("KPI_DAY");
            SimpleValue kpiDayVal = new SimpleValue(table);
            kpiDayVal.setTypeName(TypeFactory.basic("short").getName());
            kpiDay.setLength(4);
            kpiDay.setValue(kpiDayVal);
            addIndex(kpiDay);

            Column kpiDayOfWeek = new Column("KPI_DAY_OF_WEEK");
            SimpleValue kpiDayOfWeekVal = new SimpleValue(table);
            kpiDayOfWeekVal.setTypeName(TypeFactory.basic("short").getName());
            kpiDayOfWeek.setLength(4);
            kpiDayOfWeek.setValue(kpiDayOfWeekVal);
            addIndex(kpiDayOfWeek);


            Column serverId = new Column("SERVER_ID");
            SimpleValue serverIdVal = new SimpleValue(table);
            serverIdVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            serverId.setValue(serverIdVal);
            serverId.setLength(200);
            addIndex(serverId);

            Column eventId = new Column("EVENT_ID");
            SimpleValue eventIdVal = new SimpleValue(table);
            eventIdVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            eventId.setValue(eventIdVal);
            eventId.setLength(200);
            addIndex(eventId);

            Column eventName = new Column("EVENT_NAME");
            SimpleValue eventNameVal = new SimpleValue(table);
            eventNameVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            eventName.setValue(eventNameVal);
            eventName.setLength(200);
            addIndex(eventName);

            Column processDefinitionId = new Column("PROCESS_DEF_ID");
            SimpleValue processDefinitionIdVal = new SimpleValue(table);
            processDefinitionIdVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            processDefinitionId.setValue(processDefinitionIdVal);
            processDefinitionId.setLength(200);
            addIndex(processDefinitionId);

            Column processDefinitionName = new Column("PROCESS_DEF_NAME");
            SimpleValue processDefinitionNameVal = new SimpleValue(table);
            processDefinitionNameVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            processDefinitionName.setValue(processDefinitionNameVal);
            processDefinitionName.setLength(200);
            addIndex(processDefinitionName);

            Column processDefinitionVersion = new Column("PROCESS_DEF_VERSION");
            SimpleValue processDefinitionVersionVal = new SimpleValue(table);
            processDefinitionVersionVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            processDefinitionVersion.setValue(processDefinitionVersionVal);
            processDefinitionVersion.setLength(200);
            addIndex(processDefinitionVersion);

            Column processInstanceId = new Column("PROCESS_INST_ID");
            SimpleValue processInstanceIdVal = new SimpleValue(table);
            processInstanceIdVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            processInstanceId.setValue(processInstanceIdVal);
            processInstanceId.setLength(200);
            addIndex(processInstanceId);

            Column activityInstanceId = new Column("ACT_INST_ID");
            SimpleValue activityInstanceIdVal = new SimpleValue(table);
            activityInstanceIdVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            activityInstanceId.setValue(activityInstanceIdVal);
            activityInstanceId.setLength(200);
            addIndex(activityInstanceId);

            Column activityInstanceName = new Column("ACT_INST_NAME");
            SimpleValue activityInstanceNameVal = new SimpleValue(table);
            activityInstanceNameVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            activityInstanceName.setValue(activityInstanceNameVal);
            activityInstanceName.setLength(200);
            addIndex(activityInstanceName);

            Column activityInstanceIter = new Column("ACT_INST_ITER");
            SimpleValue activityInstanceIterVal = new SimpleValue(table);
            activityInstanceIterVal.setTypeName(TypeFactory.basic("java.lang.String").getName());
            activityInstanceIter.setValue(activityInstanceIterVal);
            activityInstanceIter.setLength(200);
            addIndex(activityInstanceIter);

            PrimaryKey key = new PrimaryKey();
            key.setName(metaKpi.getCode() + "_PK");
            key.addColumn(id);
            table.setPrimaryKey(key);
            

            table.addColumn(id);
            table.addColumn(kpiTimeStamp);
            table.addColumn(kpiYear);
            table.addColumn(kpiQuater);
            table.addColumn(kpiMonth);
            table.addColumn(kpiWeek);
            table.addColumn(kpiDay);
            table.addColumn(kpiDayOfWeek);
            table.addColumn(serverId);
            table.addColumn(eventId);
            table.addColumn(eventName);
            table.addColumn(processDefinitionId);
            table.addColumn(processDefinitionVersion);
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
                if (metaDim.getValueLength() != null) {
                    dim.setLength(metaDim.getValueLength());
                }
                table.addColumn(dim);
                addIndex(dim);
            }

            for (Iterator<MetaFact> i = metaKpi.getMetaFacts().iterator(); i.hasNext();) {
                MetaFact metaFact = i.next();
                Column dim = new Column(metaFact.getCode());
                SimpleValue dimVal = new SimpleValue(table);
                dimVal.setTypeName(TypeFactory.basic("java.math.BigDecimal").getName());
                dim.setValue(dimVal);
                table.addColumn(dim);
            }

            if (type == CREATE_SCRIPT) {
                result.add(0, table.sqlCreateString(d, config.buildMapping(), null, null));
            } else if (type == DROP_SCRIPT) {
                result.clear();
                result.add(table.sqlDropString(d, null, null));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private void addIndex(Column column) {
        try {
            Dialect dialect = (Dialect) Class.forName(Constants.BAM_DB_DIALECT).newInstance();
            dimColumns.add(column);
            result.add(Index.buildSqlCreateIndexString(dialect, table.getName()+ "_IDX_" + column.getName(), table, dimColumns.iterator(), false, null, null));
            dimColumns.clear();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    
}
