/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.reports.model.FilterParameters;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.ProductEntry;
import spica.scm.service.PurchaseRegisterReportService;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;

/**
 *
 * @author sujesh
 */
@Named(value = "purchaseRegisterReportView")
@ViewScoped
public class PurchaseRegisterReportView implements Serializable {

  private ProductEntry productEntry;
  private List<ProductEntry> productEntryList;
  private Double taxableValue;
  private Double invoiceAmount;
  private Double purchaseValue;

  private FilterParameters filterParameters;

  public List<ProductEntry> loadPurchaseRegisterList(MainView main) {
    try {
      if (getFilterParameters().getAccount() != null && getFilterParameters().getAccount().getId() != null) {
        loadPurchaseReportByAccount(main, getFilterParameters());
      } else if (getFilterParameters().getAccountGroup() != null && getFilterParameters().getAccountGroup().getId() != null) {
        loadPurchaseReportByAccountGroup(main, getFilterParameters());
      } else {
        loadPurchaseReport(main);
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return getProductEntryList();
  }

  public List<Account> accountAuto(String filter) {
    if (filterParameters.getAccountGroup() != null) {
      if (filterParameters.getAccountGroup().getId() != null) {
        MainView main = Jsf.getMain();
        try {
          return ScmLookupExtView.accountByAccountGroupProfile(main, filterParameters.getAccountGroup(), filter);
        } catch (Throwable t) {
          main.rollback(t, "error.select");
        } finally {
          main.close();
        }
      }
    }
    return null;
  }

  public void accountGroupSelectEvent(SelectEvent event) {
    AccountGroup accountGroup = (AccountGroup) event.getObject();
    if (accountGroup != null) {
      if (accountGroup.getId() != null) {
        filterParameters.setAccountGroup(accountGroup);
      } else {
        filterParameters.setAccountGroup(null);
      }
    }
    getFilterParameters().setAccount(null);
    setProductEntryList(null);
  }

  public void accountSelectEvent(SelectEvent event) {
    Account account = (Account) event.getObject();
    if (account != null) {
      if (account.getId() != null) {
        getFilterParameters().setAccount(account);
      } else {
        getFilterParameters().setAccount(null);
      }
    }
    setProductEntryList(null);
  }

  public void backOrNext(int pos) {
    if (wawo.entity.util.DateUtil.moveMonths(getFilterParameters().getFromDate(), pos).compareTo(new Date()) <= 0) {
      getFilterParameters().setFromDate(wawo.entity.util.DateUtil.moveMonths(getFilterParameters().getFromDate(), pos));
    }
  }

  private void loadPurchaseReportByAccount(MainView main, FilterParameters filterParameters) {
    setProductEntryList(PurchaseRegisterReportService.selectAllPurchaseEntryByAccount(main, filterParameters));
  }

  private void loadPurchaseReportByAccountGroup(MainView main, FilterParameters filterParameter) {
    setProductEntryList(PurchaseRegisterReportService.selectAllPurchaseEntryByAccountGroup(main, filterParameter));
  }

  private void loadPurchaseReport(MainView main) {
    setProductEntryList(PurchaseRegisterReportService.selectAllEntry(main, getFilterParameters()));
  }

  public ProductEntry getProductEntry() {
    return productEntry;
  }

  public void setProductEntry(ProductEntry productEntry) {
    this.productEntry = productEntry;
  }

  public List<ProductEntry> getProductEntryList() {
    if (productEntryList == null) {
      productEntryList = new ArrayList<>();
    }
    taxableValue = 0.0;
    invoiceAmount = 0.0;
    purchaseValue = 0.0;
    for (ProductEntry productEntry : productEntryList) {
      if (productEntry.getInvoiceAmountAssessable() != null) {
        taxableValue += productEntry.getInvoiceAmountAssessable();
      }
      if (productEntry.getInvoiceAmount() != null) {
        invoiceAmount += productEntry.getInvoiceAmount();
      }
      if (productEntry.getInvoiceAmountNet() != null) {
        purchaseValue += productEntry.getInvoiceAmountNet();
      }
    }
    return productEntryList;
  }

  public void setProductEntryList(List<ProductEntry> productEntryList) {
    this.productEntryList = productEntryList;
  }

  public FilterParameters getFilterParameters() {
    if (filterParameters == null) {
      filterParameters = new FilterParameters();
    }
    return filterParameters;
  }

  public void setFilterParameters(FilterParameters filterParameters) {
    this.filterParameters = filterParameters;
  }

  public Double getTaxableValue() {
    return taxableValue;
  }

  public void setTaxableValue(Double taxableValue) {
    this.taxableValue = taxableValue;
  }

  public Double getInvoiceAmount() {
    return invoiceAmount;
  }

  public void setInvoiceAmount(Double invoiceAmount) {
    this.invoiceAmount = invoiceAmount;
  }

  public Double getPurchaseValue() {
    return purchaseValue;
  }

  public void setPurchaseValue(Double purchaseValue) {
    this.purchaseValue = purchaseValue;
  }
}
