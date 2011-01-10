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
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Group;
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
import org.processbase.core.Constants;
import org.processbase.ui.portlet.PbPortlet;

/**
 *
 * @author mgubaidullin
 */
public class DocumentLibraryUtil {

    private Group processBaseGroup;
    private DLFolderLocalService folderService = null;
    private DLFileEntryLocalService fileService = null;

    public DocumentLibraryUtil() {
        try {
            processBaseGroup = GroupLocalServiceUtil.getGroup(PbPortlet.getCurrent().getPortalUser().getCompanyId(), Constants.DL_GROUP);
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
                    result = folderService.addFolder(PbPortlet.getCurrent().getPortalUser().getUserId(), processBaseGroup.getGroupId(), 0, folderName, folderName, new ServiceContext());
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
            fileEntry = fileService.addFileEntry(PbPortlet.getCurrent().getPortalUser().getUserId(),
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
            fileEntry = fileService.addFileEntry(PbPortlet.getCurrent().getPortalUser().getUserId(),
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

    public byte[] getFileBody(String processUUID, long fileId) {
        byte[] result = null;
        try {
            DLFileEntry file = getFileEntry(fileId);
            result = new byte[Long.valueOf(file.getSize()).intValue()];
            InputStream is = fileService.getFileAsStream(PbPortlet.getCurrent().getPortalUser().getCompanyId(),
                    PbPortlet.getCurrent().getPortalUser().getUserId(),
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
}
