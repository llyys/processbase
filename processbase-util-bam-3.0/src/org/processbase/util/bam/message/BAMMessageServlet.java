package org.processbase.util.bam.message;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.processbase.util.bam.BAMConstants;

/**
 *
 * @author marat
 */

public class BAMMessageServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        BAMConstants.loadConstants();
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
            boolean isMultipart = request.getParts().size() > 0;
//            System.out.println("isMultipart = " + isMultipart);
            if (isMultipart) {
                byte[] b1 = new byte[Integer.parseInt(Long.valueOf(request.getPart("body").getSize()).toString())];
                request.getPart("body").getInputStream().read(b1);
                body = new String (b1);
                byte[] b2 = new byte[Integer.parseInt(Long.valueOf(request.getPart("type").getSize()).toString())];
                request.getPart("type").getInputStream().read(b2);
                type = new String (b2);
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
