/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.hook;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ow2.bonita.definition.Hook;
import org.ow2.bonita.facade.QueryAPIAccessor;
import org.ow2.bonita.facade.runtime.ActivityBody;
import org.ow2.bonita.facade.runtime.ActivityInstance;

/**
 *
 * @author mgubaidullin
 */
public class ErrorHook implements Hook {

    public void execute(QueryAPIAccessor queryAPIAccessor, ActivityInstance<ActivityBody> activity) throws Exception {
        Logger.getLogger(ErrorHook.class.getName()).log(Level.SEVERE, "ErrorHook executed " + activity.getUUID().toString());
        throw new UnsupportedOperationException("ErrorHook");
    }
}
