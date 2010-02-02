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
import org.ow2.bonita.definition.TxHook;
import org.ow2.bonita.facade.APIAccessor;
import org.ow2.bonita.facade.runtime.ActivityBody;
import org.ow2.bonita.facade.runtime.ActivityInstance;

/**
 *
 * @author mgubaidullin
 */
public class ErrorTxHook implements TxHook {

    public void execute(APIAccessor apiAccessor, ActivityInstance<ActivityBody> activity) throws Exception {
        Logger.getLogger(ErrorTxHook.class.getName()).log(Level.SEVERE, "ErrorTxHook executed " + activity.getUUID().toString());
        throw new UnsupportedOperationException("ErrorTxHook");
    }

    
}
