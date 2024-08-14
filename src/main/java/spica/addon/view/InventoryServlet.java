/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.addon.view;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.service.AccountGroupPriceListService;
import spica.scm.service.UserService;
import spica.sys.domain.User;
import wawo.app.config.AppFactory;
import wawo.entity.core.AppEm;
import wawo.entity.core.AppJson;
import wawo.entity.core.AppLog;

/**
 *
 * @author Arun
 */
@WebServlet(name = "InventoryServlet", urlPatterns = {"/inventory"})
public class InventoryServlet extends HttpServlet {

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   * https://localhost:8080/inventory?uid=iceGYMSE7QMhLJqgLJkvZNbFT44CeUF624xGSkDbCmm&agid=197&pid=2460
   *
   * @param request servlet request
   * @param response servlet response
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
    AppEm em = null;
    try {
      response.setContentType("application/json;charset=UTF-8");
      em = AppFactory.getNewEm();

      String uniqueId = request.getParameter("uid"); // uid = token_encoded value
      String accountGroupId = request.getParameter("agid"); // uid = token_encoded value
      String productId = request.getParameter("pid");
      /* 
      For future
      String pstock = request.getParameter("ps"); //if product stock then return stock of product where expose=1 in product table  
      String plist = request.getParameter("pl"); //if product list then return product list where expose=1 in product table  
       */
      if (uniqueId != null) {
        User user = UserService.selectUserByToken(em.getDbConnector(), uniqueId); //TODO sec_user where token_encoded=? and is_active=1
        //
        if (user != null && user.getId() != null) {
          if (accountGroupId != null && productId != null) {
            AccountGroupPriceList pl = AccountGroupPriceListService.selectDefaultAccountGroupPriceList(em.getDbConnector(), new AccountGroup(Integer.parseInt(accountGroupId), null));
            response.getWriter().print(AppJson.list(em.getDbConnector(),
                    "select SUM(quantity_available+COALESCE(quantity_free_available,0)) as quantity FROM getproductsdetailsforgstsale(?,?,?,null,?)",
                    new Object[]{Integer.parseInt(accountGroupId), Integer.parseInt(productId), pl.getId(), new Date()}));

          }
          if (accountGroupId != null && productId == null) {
            response.getWriter().print(AppJson.list(em.getDbConnector(),
                    "select scm_product.id,scm_product.product_name ,scm_product_category.title as product_category\n"
                    + "from scm_product, scm_product_category,scm_product_preset \n"
                    + "where scm_product.id=scm_product_preset.product_id\n"
                    + "AND scm_product_preset.account_id in(select account_id from scm_account_group_detail where account_group_id=?)\n"
                    + "AND scm_product.product_category_id=scm_product_category.id\n"
                    + "GROUP BY scm_product.id,scm_product.product_name ,scm_product_category.title\n"
                    + "ORDER BY scm_product.product_name",
                    new Object[]{Integer.parseInt(accountGroupId)}));
          }
//          if (StringUtil.equals("1", request.getParameter("psag"))) { //psag = 1 if product stock then return stock of product where expose=1 in account group table  
//            response.getWriter().print(AppJson.list(em.getDbConnector(), "select * from table", null));
//          } else if (StringUtil.equals("1", request.getParameter("plag"))) { //plag = 1 if product list then return product list where expose=1 in product table  
//            response.getWriter().print(AppJson.list(em.getDbConnector(), "select * from table", null));
//          }

          response.getWriter().flush();
        } else {
          response.getWriter().print("Forbidden Access");
          response.getWriter().flush();
        }
      }
    } catch (Throwable t) {
      printErr(response, request, t, "Something went wrong");
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  private void printErr(HttpServletResponse response, HttpServletRequest request, Throwable t, String mess) {
    try {
      AppLog.fatal(t, "InventoryServlet" + request.getParameterMap().toString());
      if (response.getWriter() != null) {
        response.getWriter().print("[{ \"error\" : \"true\", \"mess\" : \"" + mess + "\" }]");
        response.getWriter().flush();
      }
    } catch (IOException ex) {
      Logger.getLogger(InventoryServlet.class.getName()).log(Level.SEVERE, null, ex);
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
