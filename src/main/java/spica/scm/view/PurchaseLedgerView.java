/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.reports.model.FilterParameters;

import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.PurchaseLedger;
import spica.scm.service.CompanySettingsService;
import spica.scm.service.PurchaseLedgerService;
import spica.sys.UserRuntimeService;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author fify
 */
@Named(value = "purchaseLedgerView")
@ViewScoped
public class PurchaseLedgerView implements Serializable {

  private transient List<PurchaseLedger> purchaseLedgerList;
  private FilterParameters filterParameters;
  private Double taxableValue;
  private Double invoiceAmount;
  private Double taxValue;
  private Double purchaseValue;
  private boolean returnIncluded;
  private boolean damageExpiry;
  private Double totalQty;
  private Double schemeDiscount;
  private Double productDiscount;
  private Double invoiceDiscount;
  private Double cgst;
  private Double sgst;
  private Double igst;
  private Double totalTaxValue;
  private boolean showReference;
  private transient List<PurchaseLedger> purchaseRegisterList;
  private transient List<Account> accountList;

  public List<PurchaseLedger> getPurchaseLedgerList(MainView main) {
    if (StringUtil.isEmpty(purchaseLedgerList) && !main.hasError()) {
      try {
        Company company = UserRuntimeView.instance().getCompany();
        CompanySettingsService.selectIfNull(main, company);
        showReference = (company.getCompanySettings().getEnableCompanyPrefix() != null && company.getCompanySettings().getEnableCompanyPrefix().intValue() == 1) ? true : false;
        if ("2".equals(filterParameters.getReportType())) {
          purchaseLedgerList = PurchaseLedgerService.getPurchaseLedgerList(main, company, filterParameters, returnIncluded, damageExpiry);
        } else {
          purchaseLedgerList = PurchaseLedgerService.getProductEntrySqlPaged(main, company, filterParameters, returnIncluded, damageExpiry);
        }
        taxValue = 0.0;
        taxableValue = 0.0;
        invoiceAmount = 0.0;
        purchaseValue = 0.0;
        totalQty = 0.0;
        schemeDiscount = 0.0;
        productDiscount = 0.0;
        invoiceDiscount = 0.0;
        cgst = 0.0;
        sgst = 0.0;
        igst = 0.0;
        totalTaxValue = 0.0;
        for (PurchaseLedger purchaseLedger : purchaseLedgerList) {
          if (purchaseLedger.getTaxableValue() != null) {
            taxableValue += purchaseLedger.getTaxableValue();
          }
          if (purchaseLedger.getBillAmount() != null) {
            invoiceAmount += purchaseLedger.getBillAmount();
          }
          if (purchaseLedger.getGoodsValue() != null) {
            purchaseValue += purchaseLedger.getGoodsValue();
          }
          if (purchaseLedger.getValueIgst() != null) {
            igst += purchaseLedger.getValueIgst();
          }
          if (purchaseLedger.getPurchaseQty() != null) {
            totalQty += purchaseLedger.getPurchaseQty();
          }
          if (purchaseLedger.getTaxValue() != null) {
            totalTaxValue += purchaseLedger.getTaxValue();
          }
          if (purchaseLedger.getSchemeDiscount() != null) {
            schemeDiscount += purchaseLedger.getSchemeDiscount();
          }
          if (purchaseLedger.getProductDiscount() != null) {
            productDiscount += purchaseLedger.getProductDiscount();
          }
          if (purchaseLedger.getInvoiceDiscount() != null) {
            invoiceDiscount += purchaseLedger.getInvoiceDiscount();
          }
          if (purchaseLedger.getValueCgst() != null) {
            cgst += purchaseLedger.getValueCgst();
          }
          if (purchaseLedger.getValueSgst() != null) {
            sgst += purchaseLedger.getValueSgst();
          }
          if (purchaseLedger.getTaxValue() != null) {
            taxValue += purchaseLedger.getTaxValue();
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return purchaseLedgerList;
  }

  public void setPurchaseLedgerList(List<PurchaseLedger> purchaseLedgerList) {
    this.purchaseLedgerList = purchaseLedgerList;
  }

  public List<PurchaseLedger> getPurchaseRegisterList(MainView main) {
    if (StringUtil.isEmpty(purchaseRegisterList) && !main.hasError()) {
      try {
        Company company = UserRuntimeView.instance().getCompany();
        CompanySettingsService.selectIfNull(main, company);
        showReference = (company.getCompanySettings().getEnableCompanyPrefix() != null && company.getCompanySettings().getEnableCompanyPrefix().intValue() == 1) ? true : false;
        if ("2".equals(filterParameters.getReportType())) {
          purchaseRegisterList = PurchaseLedgerService.getPurchaseRegisterProductWise(main, company, filterParameters, returnIncluded, damageExpiry, accountList);
        } else {
          purchaseRegisterList = PurchaseLedgerService.getPurchaseRegisterInvoiceWise(main, company, filterParameters, returnIncluded, damageExpiry, accountList);
        }
        taxValue = 0.0;
        taxableValue = 0.0;
        invoiceAmount = 0.0;
        purchaseValue = 0.0;
        totalQty = 0.0;
        schemeDiscount = 0.0;
        productDiscount = 0.0;
        invoiceDiscount = 0.0;
        cgst = 0.0;
        sgst = 0.0;
        igst = 0.0;
        totalTaxValue = 0.0;
        for (PurchaseLedger purchaseRegister : purchaseRegisterList) {
          if (purchaseRegister.getTaxableValue() != null) {
            taxableValue += purchaseRegister.getTaxableValue();
          }
          if (purchaseRegister.getBillAmount() != null) {
            invoiceAmount += purchaseRegister.getBillAmount();
          }
          if (purchaseRegister.getGoodsValue() != null) {
            purchaseValue += purchaseRegister.getGoodsValue();
          }
          if (purchaseRegister.getValueIgst() != null) {
            igst += purchaseRegister.getValueIgst();
          }
          if (purchaseRegister.getPurchaseQty() != null) {
            totalQty += purchaseRegister.getPurchaseQty();
          }
          if (purchaseRegister.getTaxValue() != null) {
            totalTaxValue += purchaseRegister.getTaxValue();
          }
          if (purchaseRegister.getSchemeDiscount() != null) {
            schemeDiscount += purchaseRegister.getSchemeDiscount();
          }
          if (purchaseRegister.getProductDiscount() != null) {
            productDiscount += purchaseRegister.getProductDiscount();
          }
          if (purchaseRegister.getInvoiceDiscount() != null) {
            invoiceDiscount += purchaseRegister.getInvoiceDiscount();
          }
          if (purchaseRegister.getValueCgst() != null) {
            cgst += purchaseRegister.getValueCgst();
          }
          if (purchaseRegister.getValueSgst() != null) {
            sgst += purchaseRegister.getValueSgst();
          }
          if (purchaseRegister.getTaxValue() != null) {
            taxValue += purchaseRegister.getTaxValue();
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return purchaseRegisterList;
  }

  public void setPurchaseRegisterList(List<PurchaseLedger> purchaseRegisterList) {
    this.purchaseRegisterList = purchaseRegisterList;
  }

  /**
   *
   * @param filter
   * @return
   */
  public void submitForm() {
    purchaseLedgerList = null;
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

  public List<Account> accountAutoForRegister(String filter) {
    MainView main = Jsf.getMain();
    try {
      return ScmLookupExtView.accountByCompanyAuto(filter, UserRuntimeView.instance().getCompany().getId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void accountSelectEventForRegister(SelectEvent event) {
    Account account = (Account) event.getObject();
    if (accountList == null) {
      accountList = new ArrayList<>();
    }
    accountList.add(account);
    setPurchaseRegisterList(null);
    getFilterParameters().setAccountGroup(null);
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
    setAccountList(null);
    setPurchaseLedgerList(null);
    setPurchaseRegisterList(null);
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
    setPurchaseLedgerList(null);
    setPurchaseRegisterList(null);
  }

  public void backOrNext(int pos) {
    setPurchaseLedgerList(null);
    setPurchaseRegisterList(null);
    if (wawo.entity.util.DateUtil.moveMonths(getFilterParameters().getFromDate(), pos).compareTo(new Date()) <= 0) {
      getFilterParameters().setFromDate(wawo.entity.util.DateUtil.moveMonths(getFilterParameters().getFromDate(), pos));
    }
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

  public boolean isReturnIncluded() {
    return returnIncluded;
  }

  public void setReturnIncluded(boolean returnIncluded) {
    this.returnIncluded = returnIncluded;
  }

  public boolean isDamageExpiry() {
    return damageExpiry;
  }

  public void setDamageExpiry(boolean damageExpiry) {
    this.damageExpiry = damageExpiry;
  }

  public void reset() {
    purchaseLedgerList = null;
    setFilterParameters(null);
  }

  public Double getTaxValue() {
    return taxValue;
  }

  public void setTaxValue(Double taxValue) {
    this.taxValue = taxValue;
  }

  public Double getTotalQty() {
    return totalQty;
  }

  public void setTotalQty(Double totalQty) {
    this.totalQty = totalQty;
  }

  public Double getTotalTaxValue() {
    return totalTaxValue;
  }

  public void setTotalTaxValue(Double totalTaxValue) {
    this.totalTaxValue = totalTaxValue;
  }

  public void updateReportType() {
    filterParameters.getReportType();
    setPurchaseLedgerList(null);
    setPurchaseRegisterList(null);
    Jsf.update("purchaseLedgerTable");

  }

  public Double getSchemeDiscount() {
    return schemeDiscount;
  }

  public void setSchemeDiscount(Double schemeDiscount) {
    this.schemeDiscount = schemeDiscount;
  }

  public Double getProductDiscount() {
    return productDiscount;
  }

  public void setProductDiscount(Double productDiscount) {
    this.productDiscount = productDiscount;
  }

  public Double getInvoiceDiscount() {
    return invoiceDiscount;
  }

  public void setInvoiceDiscount(Double invoiceDiscount) {
    this.invoiceDiscount = invoiceDiscount;
  }

  public Double getCgst() {
    return cgst;
  }

  public void setCgst(Double cgst) {
    this.cgst = cgst;
  }

  public Double getSgst() {
    return sgst;
  }

  public void setSgst(Double sgst) {
    this.sgst = sgst;
  }

  public Double getIgst() {
    return igst;
  }

  public void setIgst(Double igst) {
    this.igst = igst;
  }

  public boolean isShowReference() {
    return showReference;
  }

  public void setShowReference(boolean showReference) {
    this.showReference = showReference;
  }

  public List<AccountGroup> accountGroupAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      return UserRuntimeService.accountGroupFilterAutoAll(main, filter, UserRuntimeView.instance().getCompany().getId(), UserRuntimeView.instance().getAppUser());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public List<Account> getAccountList() {
    return accountList;
  }

  public void setAccountList(List<Account> accountList) {
    this.accountList = accountList;
  }

}
