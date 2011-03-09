/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.processbase.bam.message;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.processbase.core.Constants;

/**
 *
 * @author marat
 */
public class BAMMessageServlet extends HttpServlet {

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
        PrintWriter out = response.getWriter();
        response.setContentType("text/html;charset=UTF-8");
        try {
//            for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
//                Object o = e.nextElement();
//                System.out.println(o + " = " + request.getParameter((String) o));
//            }
            if (request.getParameter("type").toString().equalsIgnoreCase("json")) {
//                System.out.println("JSON");
                Kpi kpi = MessageController.jsonToKpi(request.getParameter("body"));
                MessageController.sendKpiToMQ(kpi);
            } else if (request.getParameter("type").toString().equalsIgnoreCase("xml")) {
                System.out.println("XML");
                Kpi kpi = MessageController.xmlToKpi(request.getParameter("body"));
                MessageController.sendKpiToMQ(kpi);
            }
            out.println("OK");
        } catch (Exception ex) {
            out.println(ex.getMessage());
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
