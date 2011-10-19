/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.processbase.bonita.services.impl;

import com.liferay.client.soap.portal.model.UserSoap;
import com.liferay.client.soap.portal.service.http.UserServiceSoapServiceLocator;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.HttpPrincipal;
import com.liferay.portal.service.http.UserServiceHttp;
import junit.framework.TestCase;

/**
 *
 * @author marat
 */
public class LiferayDocumentationManagerTest extends TestCase {

    public LiferayDocumentationManagerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSearch() {
//        try {
//            HttpPrincipal http = new HttpPrincipal("http:/localhost:8080","test","test", false);
//            http.setCompanyId(13004);
//            User currentUser = UserServiceHttp.getUserById(http, 10169);
//
//
////            UserServiceSoapServiceLocator xxx = new UserServiceSoapServiceLocator();
////
////            com.liferay.client.soap.portal.service.http.UserServiceSoap soap = xxx.getPortal_UserService();
////            
////            
////            UserSoap currentUser = soap.getUserById(10169); 
//            System.out.println(currentUser.getFirstName());
//
//
//
//
//
//            //            processBaseGroup = GroupLocalServiceUtil.getGroup(groupId);
//            //            folderService = DLFolderLocalServiceUtil.getService();
//            //            fileService = DLFileEntryLocalServiceUtil.getService();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            fail(ex.getMessage());
//        }


//        FileInputStream fis = null;
//        try {
//            System.out.println("search");
//            File test = new File("test.xml");
//            fis = new FileInputStream(test);
//            byte[] b = new byte[Long.valueOf(test.length()).intValue()];
//            fis.read(b);
//            fis.close();
//            System.out.println(new String(b));
//            DocumentSearchBuilder builder = DocumentSearchBuilder.valueOf(new String(b));
        //        int fromResult = 0;
        //        int maxResults = 0;
//            LiferayDocumentationManager instance = new LiferayDocumentationManager("13004", "10282", "10169");
//            SearchResult expResult = instance.search(builder, 0, 999999999);
        //        fail("The test case is a prototype.");
        //        assertEquals(expResult, result);
        //        fail("The test case is a prototype.");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            try {
//                fis.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
    }
}
