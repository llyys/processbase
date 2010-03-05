/**
 * Copyright (C) 2010 PROCESSBASE
 * PROCESSBASE Ltd, Almaty, Kazakhstan
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
package org.processbase.ui.util;

import com.liferay.documentlibrary.DuplicateFileException;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalService;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalService;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mgubaidullin
 */
public class DocumentLibraryUtil {

    private User currentUser;
    private String processUUID;
    private Group processBaseGroup;
    private DLFolderLocalService folderService = null;
    private DLFolder folder = null;
    private DLFileEntryLocalService fileService = null;

    public DocumentLibraryUtil(User currentUser, String processUUID) {
        try {
            this.currentUser = currentUser;
            this.processUUID = processUUID;
            this.processBaseGroup = GroupLocalServiceUtil.search(currentUser.getCompanyId(), "PROCESSBASE", "", null, 0, 1).get(0);
            folderService = DLFolderLocalServiceUtil.getService();
            fileService = DLFileEntryLocalServiceUtil.getService();
            folder = getFolder();
        } catch (Exception ex) {
            System.out.println("8888888888888888888888888888888888888");
        }
    }

    public DLFolder getFolder() {
        DLFolder result = null;
        try {
            System.out.println("1111111111111111111111");
            result = folderService.getFolder(processBaseGroup.getGroupId(), 0, processUUID);
        } catch (Exception nsfex) {
            System.out.println("2222222222222222222222222");
            if (result == null) {
                try {
                    result = folderService.addFolder(currentUser.getUserId(), processBaseGroup.getGroupId(), 0, processUUID, processUUID, new ServiceContext());
                } catch (PortalException ex) {
                    System.out.println("3333333333333333");
                } catch (SystemException ex) {
                    System.out.println("444444444444444444444");
                }
            }
            System.out.println("3333333333333333333333333333");
        }
        return result;
    }

    public List<DLFileEntry> getProcessFiles() throws SystemException {
        return fileService.getFileEntries(folder.getFolderId());
    }

    public void deleteFile(DLFileEntry fileEnrty) throws SystemException {
        fileService.deleteDLFileEntry(fileEnrty);
    }

    public DLFileEntry addFile(String title, String description, File file, String[] tags) throws PortalException, SystemException {
        DLFileEntry fileEnrty = null;
        try {
            fileEnrty = fileService.addFileEntry(currentUser.getUserId(),
                    folder.getFolderId(),
                    file.getName(),
                    title,
                    description,
                    null,
                    file,
                    new ServiceContext());
        } catch (DuplicateFileException ex) {
//                fileEnrty = fileService.updateFileEntry(currentUser.getUserId(),
//                        folder.getFolderId(),
//                        "PASSPORT.pdf",
//                        "Паспорт",
//                        "Это паспорт",
//                        null,
//                        new File("C:\\Users\\mgubaidullin\\Desktop\\docs\\PASSPORT.pdf"),
//                        new ServiceContext());
            }

//        fileService.updateTagsAsset(currentUser.getUserId(),
//                fileEnrty,
//                new java.lang.String[]{"PROCESSBASE"},
//                tags);
        return fileEnrty;
    }

    public DLFileEntry updateFile(DLFileEntry file) {
        try {
            return fileService.updateDLFileEntry(file);
        } catch (SystemException ex) {
            ex.printStackTrace();
            return file;
        }
    }

    public byte[] getFileBody(DLFileEntry file) {
        byte[] result = new byte[file.getSize()];
        try {
            InputStream is = fileService.getFileAsStream(currentUser.getCompanyId(),
                    currentUser.getUserId(),
                    folder.getFolderId(),
                    file.getName());
            is.read(result);
        } catch (PortalException ex) {
            ex.printStackTrace();
        } catch (SystemException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
