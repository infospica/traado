/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.addon.view;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import spica.addon.service.DocumentService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import spica.constant.ScmConstant;
import spica.scm.domain.DocumentStore;
import spica.scm.export.ExcelUtil;
import spica.sys.SystemConstants;
import wawo.app.config.AppFactory;
import wawo.entity.core.AppEm;
import wawo.entity.core.AppIo;

/**
 *
 * @author sujesh
 */
@WebServlet(name = "DocumentDownloader", urlPatterns = {"/download"})
public class DocumentDownloader extends HttpServlet {

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   *
   * @param request servlet request
   * @param response servlet response
   */

  protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
    AppEm em = null;
    try {
      em = AppFactory.getNewEm();
      String uniqueId = request.getParameter("uid"); //TODO better to decode and use
      if (uniqueId != null) {
        DocumentStore documentStore = DocumentService.selectByUniqueId(em, uniqueId);
        if (documentStore != null && documentStore.getId() != null) {
          if (documentStore.getStatus() != null) {
            if (documentStore.getStatus().intValue() == SystemConstants.CONFIRMED.intValue()) {
              ExcelUtil.downloadInline(AppIo.getPrivateFolder("") + documentStore.getFilePath(), response);
            } else {
              if (documentStore.getStatus().intValue() == SystemConstants.CANCELLED.intValue()) {
                response.getWriter().print("Document is canceled");
              } else {
                response.getWriter().print("Document is out dated, try after some time");
              }
              response.getWriter().flush();
            }
          } else {
            response.getWriter().print("Forbidden Access");
            response.getWriter().flush();
          }
        } else {
          response.getWriter().print("Forbidden Access");
          response.getWriter().flush();
        }
      }
    } catch (Throwable t) {
      try {
        Logger.getLogger(DocumentDownloader.class.getName()).log(Level.SEVERE, null, t);
        response.getWriter().print("Something went wrong");
        response.getWriter().flush();
      } catch (IOException ex) {
        //ignored
      }
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    processRequest(request, response);
  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Document Download Servlet";
  }// </editor-fold>

}
