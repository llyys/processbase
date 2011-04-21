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
package org.processbase.ui.osgi.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.ow2.bonita.facade.privilege.Rule;
import org.ow2.bonita.facade.privilege.Rule.RuleType;
import org.ow2.bonita.facade.uuid.RuleExceptionUUID;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.osgi.PbPanelModule;
import org.processbase.ui.osgi.PbPanelModuleService;
import org.processbase.ui.osgi.PbPanelModuleServiceListener;

/**
 *
 * @author marat
 */
public class PbPanelModuleServiceImpl implements PbPanelModuleService {

    private Map<String, PbPanelModule> modules = new HashMap<String, PbPanelModule>();
    private ArrayList<PbPanelModuleServiceListener> listeners = new ArrayList<PbPanelModuleServiceListener>();

    @SuppressWarnings("unchecked")
    public synchronized void registerModule(PbPanelModule module) {
        System.out.println("module registered SERVIRE_IMPL - " + module.getName());
        modules.put(module.getClass().getCanonicalName(), module);
        for (PbPanelModuleServiceListener listener : (ArrayList<PbPanelModuleServiceListener>) listeners.clone()) {
            listener.moduleRegistered(this, module);
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized void unregisterModule(PbPanelModule module) {
        System.out.println("module unregistered SERVIRE_IMPL - " + module.getName());
        modules.remove(module.getClass().getCanonicalName());
        for (PbPanelModuleServiceListener listener : (ArrayList<PbPanelModuleServiceListener>) listeners.clone()) {
            listener.moduleUnregistered(this, module);
        }
    }

    public Map<String, PbPanelModule> getModules() {
        return modules;
    }

    public synchronized void addListener(PbPanelModuleServiceListener listener) {
        System.out.println("addListener SERVIRE_IMPL - " + listener);
        listeners.add(listener);
    }

    public synchronized void removeListener(PbPanelModuleServiceListener listener) {
        System.out.println("removeListener SERVIRE_IMPL - " + listener);
        listeners.remove(listener);
    }

    private void addModuleRule(String name) {
        try {
            BPMModule bpm = new BPMModule("osgi");
            Rule rule = bpm.createRule(name, name, name, RuleType.CUSTOM);
            Set<RuleExceptionUUID> uis = new HashSet<RuleExceptionUUID>(1);
            uis.add(new RuleExceptionUUID(name));
            bpm.addExceptionsToRuleByUUID(rule.getUUID(), uis);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
