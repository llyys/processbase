/**
 * Copyright (C) 2011 PROCESSBASE
 * PROCESSBASE Ltd
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.processbase.bonita.services.impl;

import com.liferay.documentlibrary.DuplicateFileException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalService;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalService;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author marat
 */
public class LiferayDocumentLibrary {

    private Group processBaseGroup;
    private DLFolderLocalService folderService = null;
    private DLFileEntryLocalService fileService = null;
    private User currentUser;

    public LiferayDocumentLibrary(String companyMx, String groupName, String userScreenName) {
        try {
            Company company = CompanyLocalServiceUtil.getCompanyByMx(companyMx);
            this.currentUser = UserLocalServiceUtil.getUserByScreenName(company.getCompanyId(), userScreenName);
            processBaseGroup = GroupLocalServiceUtil.getGroup(company.getCompanyId(), groupName);
            folderService = DLFolderLocalServiceUtil.getService();
            fileService = DLFileEntryLocalServiceUtil.getService();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public DLFolder getFolder(String processDefinitionUUID, String processInstanceUUID) {
        if (processInstanceUUID == null) {
            processInstanceUUID = "DEFINITION_LEVEL_DOCUMENT";
        }
        DLFolder parent = getFolder(processDefinitionUUID, 0);
        DLFolder child = getFolder(processInstanceUUID, parent.getFolderId());
        return child;
    }

    private DLFolder getFolder(String folderName, long parentFolderId) {
//        System.out.println("getFolder " + folderName + " " + parentFolderId);
        DLFolder result = null;
        try {
            result = folderService.getFolder(processBaseGroup.getGroupId(), parentFolderId, folderName);
        } catch (Exception nsfex) {
//            nsfex.printStackTrace();
            System.out.println("getFolder exception " + nsfex.getMessage());
            if (result == null) {
                try {
                    result = folderService.addFolder(currentUser.getUserId(), processBaseGroup.getGroupId(), parentFolderId, folderName, folderName, new ServiceContext());
                } catch (PortalException ex) {
                    ex.printStackTrace();
                } catch (SystemException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return result;
    }

    public List<DLFileEntry> getProcessFiles(String processDefinitionUUID, String processInstanceUUID) throws SystemException {
        DLFolder child = getFolder(processDefinitionUUID, processInstanceUUID);
        return fileService.getFileEntries(processBaseGroup.getGroupId(), child.getFolderId());
    }

    public DLFileEntry getProcessFile(String processDefinitionUUID, String processInstanceUUID, String name) throws SystemException, PortalException {
        DLFolder child = getFolder(processDefinitionUUID, processInstanceUUID);
        return fileService.getFileEntryByTitle(processBaseGroup.getGroupId(), child.getFolderId(), name);
    }

    public void deleteFile(long fileId) throws SystemException, PortalException {
        fileService.deleteDLFileEntry(fileId);
    }

    public DLFileEntry addFile(String definitionUUID, String instanceUUID, String name, String fileName, byte[] body) throws PortalException, SystemException {
        DLFileEntry fileEntry = null;
        try {
            DLFolder folder = getFolder(definitionUUID, 0);
            if (instanceUUID != null) {
                folder = getFolder(instanceUUID, folder.getFolderId());
            }
            fileEntry = fileService.addFileEntry(currentUser.getUserId(),
                    processBaseGroup.getGroupId(),
                    folder.getFolderId(),
                    fileName,
                    name,
                    fileName,
                    null,
                    null,
                    body,
                    new ServiceContext());
        } catch (DuplicateFileException ex) {
            ex.printStackTrace();
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

    public DLFileEntry updateFileEntry(
            long folderId,
            String name,
            String sourceFileName,
            String title,
            String description,
            String changeLog,
            boolean majorVersion,
            String extraSettings,
            byte[] bytes) {
        try {
            return fileService.updateFileEntry(currentUser.getUserId(),
                    processBaseGroup.getGroupId(),
                    folderId,
                    name,
                    sourceFileName,
                    title,
                    description,
                    changeLog,
                    majorVersion,
                    extraSettings,
                    bytes,
                    new ServiceContext());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
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

    public DLFileEntry getFileEntry(String processDefinitionUUID, String processInstanceUUID, String title) {
        try {
            DLFolder child = getFolder(processInstanceUUID, processInstanceUUID);
            return fileService.getFileEntryByTitle(processBaseGroup.getGroupId(), child.getFolderId(), title);
        } catch (PortalException ex) {
            ex.printStackTrace();
            return null;
        } catch (SystemException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public byte[] getFileBody(String processDefinitionUUID, String processInstanceUUID, String title) {
        DLFileEntry file = getFileEntry(processDefinitionUUID, processInstanceUUID, title);
        return getFileBody(file.getFileEntryId());
    }
    
    public byte[] getFileBody(long fileId) {
        byte[] result = new byte[0];
        try {
            DLFileEntry file = getFileEntry(fileId);
            if (file != null) {
                result = new byte[Long.valueOf(file.getSize()).intValue()];
                InputStream is = fileService.getFileAsStream(currentUser.getCompanyId(),
                        currentUser.getUserId(),
                        processBaseGroup.getGroupId(),
                        file.getFolderId(),
                        file.getName());
                is.read(result);
            }
        } catch (PortalException ex) {
            ex.printStackTrace();
        } catch (SystemException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
//        System.out.println("getFileBody retult = " + result);
        return result;
    }
//    public Map<String, String> getFileList(String processUUID) throws Exception {
//        HashMap<String, String> result = new HashMap<String, String>();
//        List<DLFileEntry> entries = getProcessFiles(processUUID);
//        for (DLFileEntry fileEntry : entries) {
//            result.put(fileEntry.getTitle(), fileEntry.getDescription());
//            System.out.println(fileEntry.getName() + " - " + fileEntry.getTitle() + " - " + fileEntry.getDescription());
//        }
//        return result;
//    }
}
