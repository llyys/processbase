/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.processbase.bam.message;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.processbase.core.Constants;

/**
 *
 * @author marat
 */
public class BAMMessageServlet extends HttpServlet {

    private Vector paramOrder;
    private Hashtable parameters;

    @Override
    public void init() throws ServletException {
        super.init();
        Constants.loadConstants();
    }

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = null;
        String body = null;
        PrintWriter out = response.getWriter();
        response.setContentType("text/html;charset=UTF-8");
        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
//            System.out.println("isMultipart = " + isMultipart);
            if (isMultipart) {
                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                List items = upload.parseRequest(request);
                Iterator iter = items.iterator();
//                System.out.println("--------------------------------------- 1 ");
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
                    if (item.isFormField()) {if (item.getFieldName().equals("body")){
                            body = item.getString();
                        } else if (item.getFieldName().equals("type")){
                            type = item.getString();
                        }
                    }
                }
            } else {
                type = request.getParameter("type").toString();
                body = request.getParameter("body").toString();
            }

            if (type.equalsIgnoreCase("json")) {
                Kpi kpi = MessageController.jsonToKpi(body);
                MessageController.sendKpiToMQ(kpi);
            } else if (type.equalsIgnoreCase("xml")) {
                Kpi kpi = MessageController.xmlToKpi(body);
                MessageController.sendKpiToMQ(kpi);
            }
            out.println("OK");
        } catch (Exception ex) {
            out.println(ex.getMessage());
            ex.printStackTrace();
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
