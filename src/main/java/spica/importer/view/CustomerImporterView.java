/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.importer.view;

import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import spica.scm.domain.Customer;
import spica.scm.service.CustomerImporterService;
import spica.scm.view.ScmLookupExtView;
import spica.sys.UserRuntimeView;
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
    } finally {
      main.close();
    }
  }
}
