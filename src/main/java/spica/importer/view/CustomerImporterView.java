/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.importer.view;

import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import spica.scm.domain.Country;
import spica.scm.domain.CustomerImporter;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.District;
import spica.scm.domain.State;
import spica.scm.domain.Territory;
import spica.scm.domain.TradeProfile;
import spica.scm.service.CustomerImporterService;
import spica.scm.view.ScmLookupExtView;
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.app.faces.MainView;

/**
 *
 * @author java-2
 */
@Named(value = "customerImporterView")
@ViewScoped
public class CustomerImporterView implements Serializable {

  private transient Customer fromCustomer;
  private transient Customer toCustomer;

  public Customer getFromCustomer() {
    return fromCustomer;
  }

  public void setFromCustomer(Customer fromCustomer) {
    this.fromCustomer = fromCustomer;
  }

  public Customer getToCustomer() {
    return toCustomer;
  }

  public void setToCustomer(Customer toCustomer) {
    this.toCustomer = toCustomer;
  }

  public List<Customer> customerAuto(String filter) {
//    if (getSalesInvoice() != null && getSalesInvoice().getAccountGroupId() != null) {
    return ScmLookupExtView.selectCustomerByCompany(UserRuntimeView.instance().getCompany(), filter);
//    }

  }

  public void customerTransfer(MainView main) {
    try {
      CustomerImporterService.replaceCustomer(main, getFromCustomer(), getToCustomer());
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  /**
   * Creates a new instance of CustomerImportView
   */
  public CustomerImporterView() {
  }



  public void actionCompanyDelete(MainView main) {
    try {
      CustomerImporterService.deleteCompanyById(main, UserRuntimeView.instance().getCompany().getId());
      main.commit("success.delete");
    } catch (Throwable t) {      
      main.rollback(t, "error.delete", null);
    }finally{
      main.close();
    }
  }
}
