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

import java.util.ArrayList;
import org.ow2.bonita.env.Environment;
import org.ow2.bonita.util.Command;
import org.processbase.engine.bam.db.HibernateUtil;

/**
 *
 * @author marat
 */
public class ExecuteScripts implements Command<Void> {

    private ArrayList<String> scripts;

    public ExecuteScripts(ArrayList<String> scripts) {
        this.scripts = scripts;
    }

    public Void execute(Environment e) throws Exception {
        HibernateUtil hutil = new HibernateUtil();
        hutil.executeScripts(scripts);
        return null;
    }
}
