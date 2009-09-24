/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.util;

import java.security.MessageDigest;
import sun.misc.BASE64Encoder;

/**
 *
 * @author mgubaidullin
 */
public class PasswordService {

        public static String encrypt(String plaintext) throws Exception {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA"); //step 2
            md.update(plaintext.getBytes("UTF-8")); //step 3
            byte raw[] = md.digest(); //step 4
            String hash = (new BASE64Encoder()).encode(raw); //step 5
            return hash; //step 6
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }


}
