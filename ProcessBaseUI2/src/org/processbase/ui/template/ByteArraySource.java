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
package org.processbase.ui.template;

import com.vaadin.terminal.StreamResource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;


/**
 *
 * @author mgubaidullin
 */
public class ByteArraySource implements StreamResource.StreamSource {

    public byte[] byteArray = null;

    public ByteArraySource(byte[] byteArray) {
        super();
        this.byteArray = byteArray;
    }

    public InputStream getStream() {
        try {
            return new ByteArrayInputStream(byteArray);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
