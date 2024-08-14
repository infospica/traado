/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.xml.bind.DatatypeConverter;
//import wawo.entity.util.IoUtil;

/**
 *
 * @author Anoop Jayachandran
 */
@WebServlet(name = "InvoiceToPdfServlet", urlPatterns = {"/invoiceToPdf"})
public class InvoiceToPdfServlet extends HttpServlet {

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    try {
      String fileName = request.getParameter("fileName");
      fileName = fileName.trim().replaceAll("/", "_") + ".pdf";

      String base64 = request.getParameter("base64");
      String contentType = request.getParameter("contentType");
      String realPath = request.getServletContext().getRealPath("/") + "invoicePdf/";

      //System.out.println("Contex Path :" + request.getServletContext().getContextPath());
      //System.out.println("Real Path :" + request.getServletContext().getRealPath(""));
      //System.out.println("Context Name :" + request.getServletContext().getServletContextName());
      //System.out.println("PDF Root :" + realPath);
//      response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
      response.setHeader("Content-Disposition", "inline;filename=" + fileName);
      response.setContentType(contentType);

      byte[] data = Base64.getDecoder().decode(base64.getBytes("UTF-8"));//DatatypeConverter.parseBase64Binary(base64);

      File pdf = new File(realPath + fileName);
      try (FileOutputStream fout = new FileOutputStream(pdf)) {
        fout.write(data);
      }

      response.setContentLength(data.length);
      response.getOutputStream().write(data);
      response.flushBuffer();
    } catch (Throwable t) {
      Logger.getLogger(InvoiceToPdfServlet.class.getName()).log(Level.SEVERE, "failed to generate invoice", t);
    }
  }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /**
   * Handles the HTTP <code>GET</code> method.
   *
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
   *
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
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Short description";
  }// </editor-fold>

}
