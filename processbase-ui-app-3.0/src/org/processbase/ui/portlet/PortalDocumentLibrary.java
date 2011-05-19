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
package org.processbase.ui.portlet;

import com.liferay.documentlibrary.DuplicateFileException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.processbase.ui.core.Constants;

/**
 *
 * @author mgubaidullin
 */
public class PortalDocumentLibrary {

    private Group processBaseGroup;
    private DLFolderLocalService folderService = null;
    private DLFileEntryLocalService fileService = null;
    private User currentUser;

    public PortalDocumentLibrary(User currentUser) {
        try {
            this.currentUser = currentUser;
            processBaseGroup = GroupLocalServiceUtil.getGroup(currentUser.getCompanyId(), Constants.DL_GROUP);
            folderService = DLFolderLocalServiceUtil.getService();
            fileService = DLFileEntryLocalServiceUtil.getService();
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

    public List<DLFileEntry> getProcessFiles(String processUUID) throws SystemException {
        return fileService.getFileEntries(processBaseGroup.getGroupId(), getFolder(processUUID).getFolderId());
    }

    public void deleteFile(long fileId) throws SystemException, PortalException {
        fileService.deleteDLFileEntry(fileId);
    }

    public DLFileEntry addFile(String processUUID, String title, String description, File file, String[] tags) throws PortalException, SystemException {
        DLFileEntry fileEntry = null;
        try {
            fileEntry = fileService.addFileEntry(currentUser.getUserId(),
                    processBaseGroup.getGroupId(),
                    getFolder(processUUID).getFolderId(),
                    file.getName(),
                    title,
                    description,
                    null,
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

    public DLFileEntry addFile(String processUUID, String title, String description, String fileName, byte[] body, String[] tags) throws PortalException, SystemException {
        DLFileEntry fileEntry = null;
        try {
            fileEntry = fileService.addFileEntry(currentUser.getUserId(),
                    processBaseGroup.getGroupId(),
                    getFolder(processUUID).getFolderId(),
                    fileName,
                    title,
                    description,
                    null,
                    null,
                    body,
                    new ServiceContext());
        } catch (DuplicateFileException ex) {
        }
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

    public DLFileEntry getFileEntry(String processUUID, String title) {
        try {
            return fileService.getFileEntryByTitle(processBaseGroup.getGroupId(), getFolder(processUUID).getFolderId(), title);
        } catch (PortalException ex) {
            ex.printStackTrace();
            return null;
        } catch (SystemException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public byte[] getFileBody(String processUUID, String title) {
        DLFileEntry file = getFileEntry(processUUID, title);
        return getFileBody(processUUID, file.getFileEntryId());
    }

    public byte[] getFileBody(String processUUID, long fileId) {
        byte[] result = null;
        try {
            DLFileEntry file = getFileEntry(fileId);
            result = new byte[Long.valueOf(file.getSize()).intValue()];
            InputStream is = fileService.getFileAsStream(currentUser.getCompanyId(),
                    currentUser.getUserId(),
                    processBaseGroup.getGroupId(),
                    getFolder(processUUID).getFolderId(),
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

    public void saveFile(String processUUID, String fileId, String fileName, byte[] fileBody) throws Exception{
        addFile(processUUID, fileId, fileName, fileName, fileBody, new String[0]);
    }

    public Map<String, String> getFileList(String processUUID) throws Exception{
        HashMap<String, String> result = new HashMap<String, String>();
        List<DLFileEntry> entries = getProcessFiles(processUUID);
        for (DLFileEntry fileEntry : entries){
            result.put(fileEntry.getTitle(), fileEntry.getDescription());
            System.out.println(fileEntry.getName()+" - "+fileEntry.getTitle()+" - "+fileEntry.getDescription());
        }
        return result;
    }

}
