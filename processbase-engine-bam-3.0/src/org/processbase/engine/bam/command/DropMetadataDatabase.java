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

package org.processbase.engine.bam.command;

import org.ow2.bonita.env.Environment;
import org.ow2.bonita.util.Command;
import org.processbase.engine.bam.metadata.HibernateUtil;

/**
 *
 * @author marat
 */
public class DropMetadataDatabase implements Command<Void> {

    public Void execute(Environment e) throws Exception {

         HibernateUtil hutil = new HibernateUtil();
        hutil.dropSchema();
//        hutil.generateSchema();
//        MetaDim m1 = new MetaDim(1, "DIM01", "DIM01", "java.lang.String", Short.parseShort("10"));
//        MetaDim m2 = new MetaDim(2, "DIM02", "DIM02", "int", Short.parseShort("5"));
//        hutil.addMetaDim(m1);
//        hutil.addMetaDim(m2);
//
//        MetaKpi k = new MetaKpi();
//        k.setCode("KPI_0001");
//        k.setName("KPI_0001");
//        k.setOwner("test");
//        k.setStatus("EDITABLE");
//        k.getMetaDims().add(m1);
//        k.getMetaDims().add(m2);
//        hutil.addMetaKpi(k);
        
        return null;
    }

}
