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
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.core.Constants;

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
            Constants.loadConstants();
            this.currentUser = currentUser;
            this.processUUID = processUUID;
            this.processBaseGroup = GroupLocalServiceUtil.search(currentUser.getCompanyId(), Constants.DL_GROUP, "", null, 0, 1).get(0);
            folderService = DLFolderLocalServiceUtil.getService();
            fileService = DLFileEntryLocalServiceUtil.getService();
            if (processUUID != null) {
                folder = getFolder(processUUID);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public DLFolder getFolder(String folderName) {
        DLFolder result = null;
        try {
            result = folderService.getFolder(processBaseGroup.getGroupId(), 0, folderName);
        } catch (Exception nsfex) {
            if (result == null) {
                try {
                    result = folderService.addFolder(currentUser.getUserId(), processBaseGroup.getGroupId(), 0, folderName, folderName, new ServiceContext());
                } catch (PortalException ex) {
                    ex.printStackTrace();
                } catch (SystemException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return result;
    }

    public List<DLFileEntry> getProcessFiles() throws SystemException {
        return fileService.getFileEntries(folder.getFolderId());
    }

    public void deleteFile(long fileId) throws SystemException, PortalException {
        fileService.deleteDLFileEntry(fileId);
    }

    public DLFileEntry addFile(String title, String description, File file, String[] tags) throws PortalException, SystemException {
        DLFileEntry fileEntry = null;
        try {
            fileEntry = fileService.addFileEntry(currentUser.getUserId(),
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
        return fileEntry;
    }

    public DLFileEntry updateFileEntry(DLFileEntry file) {
        try {
            return fileService.updateDLFileEntry(file);
        } catch (SystemException ex) {
            ex.printStackTrace();
            return file;
        }
    }

    public DLFileEntry getFileEntry(long fileId) {
        try {
            return fileService.getDLFileEntry(fileId);
        } catch (PortalException ex) {
            ex.printStackTrace();
            return null;
        } catch (SystemException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public byte[] getFileBody(long fileId) {
        byte[] result = null;
        try {
            DLFileEntry file = getFileEntry(fileId);
            result = new byte[file.getSize()];
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

    public String getProcessUUID() {
        return processUUID;
    }
}
