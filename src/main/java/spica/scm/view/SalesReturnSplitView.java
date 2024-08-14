/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import spica.fin.domain.TaxCode;
import spica.scm.common.ConsignmentRate;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.Consignment;
import spica.scm.domain.Customer;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import spica.scm.domain.SalesReturnItemSplit;
import spica.scm.domain.SalesReturnSplit;
import spica.scm.export.LineItemExport;
import spica.scm.service.ConsignmentService;
import spica.scm.service.SalesReturnService;
import spica.scm.service.SalesReturnSplitService;
import spica.scm.tax.TaxCalculator;
import spica.scm.util.SalesInvoiceUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
@Named(value = "salesReturnSplitView")
@ViewScoped
public class SalesReturnSplitView implements Serializable {

  private transient SalesReturn salesReturn;
  private transient SalesReturnSplit salesReturnSplit;
  private transient List<SalesReturnItemSplit> salesReturnItemSplitList;
  private transient List<SalesReturnItem> salesReturnItemList;
  //private transient String pageTitle;
  private transient TaxCalculator taxCalculator;
  private transient AccountGroup accountGroup;
  private transient ConsignmentRate consignmentRate;
  private transient String lrAmount;
  private transient Double oldLrAmount;
  private transient Double lrAmountMax;
  private transient boolean valueMrpChanged;
  private transient boolean intraStateSales;
  private transient boolean interStateSales;
  private List<TaxCode> taxCodeList;
  private transient Integer rowIndex;

  /**
   * Default Constructor.
   */
  public SalesReturnSplitView() {
    super();
  }

  @PostConstruct
  public void init() {
    Integer invoiceId = (Integer) Jsf.popupParentValue(Integer.class);
    if (invoiceId != null) {
      getSalesReturnSplit().setId(invoiceId);
    }
  }

  public String switchSalesReturnSplit(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isEdit() && !main.hasError()) {
          //  setSalesReturnSplitPageTitle();
          setSalesReturnSplit((SalesReturnSplit) SalesReturnSplitService.selectByPk(main, getSalesReturnSplit()));
          getSalesReturnSplit().setDecimalPrecision(getAccountGroup().getCustomerDecimalPrecision() == null ? 2 : getAccountGroup().getCustomerDecimalPrecision());
          getSalesReturnSplit().setBusinessArea(SalesInvoiceUtil.getSalesMode(getCompany(), getSalesReturnSplit().getCustomerId()));
          setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getSalesReturnSplit().getTaxProcessorId().getProcessorClass()));
          setSalesReturnItemSplitList(SalesReturnSplitService.selectSalesReturnItemSplitList(main, getSalesReturnSplit()));
          salesReturn = new SalesReturn(salesReturnSplit);
          List<SalesReturnItem> returnList = new ArrayList<>();
          for (SalesReturnItemSplit list : getSalesReturnItemSplitList()) {
            returnList.add(new SalesReturnItem(list));
          }
          setSalesReturnItemList(returnList);
          getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), false);
          updateConsignmentDetails(main, getSalesReturn().getConsignmentId());
          setValueMrpChanged(SalesReturnService.isProductValueMrpChanged(main, getSalesReturn()));
          //getTaxCalculator().processSalesReturnCalculation(getSalesReturn(), getSalesReturnItemList(), false);
          //updateProductHashCode(getSalesReturnItemList());
          setSalesMode(getCompany(), getSalesReturn().getCustomerId());
          lookupGstTaxCode();
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  private void updateConsignmentDetails(MainView main, Consignment consignment) {
    if (consignment != null) {
      setOldLrAmount(getSalesReturn().getTotalExpenseAmount());
      setConsignmentRate(ConsignmentService.selectConsignmentRateByAccountGroupConsignment(main, getSalesReturn().getCustomerId(), getSalesReturn().getAccountGroupId(), getSalesReturn().getConsignmentId().getId()));
      setConsignmentRecieptDetails(getConsignmentRate());
    }
  }

  private void setConsignmentRecieptDetails(ConsignmentRate rate) {
    StringBuilder sb = new StringBuilder();
    if (rate != null && rate.getId() != null) {
      if (rate.getLrAmount() == 0 && rate.getTotalExpenseAmount() > 0) {
        setLrAmount("( " + String.valueOf(rate.getTotalExpenseAmount()) + ")");
        lrAmountMax = rate.getTotalExpenseAmount();
        if (rate.getNoOfInvoice() != null && rate.getNoOfInvoice() == 1 && getSalesReturn().getTotalExpenseAmount() == null) {
          getSalesReturn().setTotalExpenseAmount(rate.getTotalExpenseAmount());
        }
      } else if (rate.getTotalExpenseAmount().equals(rate.getLrAmount()) && rate.getTotalExpenseAmount() > 0) {
        setLrAmount("( " + String.valueOf(rate.getTotalExpenseAmount()) + ")");
      } else if (rate.getLrAmountDifference() > 0) {
        if (getSalesReturn().getTotalExpenseAmount() != null && getSalesReturn().getTotalExpenseAmount() > 0) {
          lrAmountMax = getSalesReturn().getTotalExpenseAmount() + rate.getLrAmountDifference();
        } else {
          lrAmountMax = rate.getLrAmountDifference();
        }
        sb.append(rate.getLrAmountDifference());
        sb.append(" ( ").append(rate.getTotalExpenseAmount()).append(" )");
        setLrAmount(sb.toString());
      }
    }
  }

//  private void setSalesReturnSplitPageTitle() {
//    if (isDamagedSalesReturnSplit()) {
//      setPageTitle("Sales Return - Damaged");
//    } else {
//      setPageTitle("Sales Return - Saleable");
//    }
//  }
  private List<TaxCode> lookupGstTaxCode() {
    if (taxCodeList == null) {
      taxCodeList = ScmLookupExtView.lookupGstTaxCode(UserRuntimeView.instance().getCompany());
    }
    return taxCodeList;
  }

  private void setSalesMode(Company company, Customer customer) {
    setInterStateSales(false);
    setIntraStateSales(false);
    if (customer != null) {
      int salesMode = SalesInvoiceUtil.getSalesMode(company, customer);
      setIntraStateSales(SalesInvoiceUtil.INTRA_STATE_SALES == salesMode);
      setInterStateSales(SalesInvoiceUtil.INTER_STATE_SALES == salesMode);
    }
    if (getSalesReturnSplit().getSezZone() != null && getSalesReturnSplit().getSezZone().intValue() == 1) {
      setIntraStateSales(false);
      setInterStateSales(true);
    }
  }
//FIXME main not required

  public void printLandscape(MainView main) {
    CompanySettings settings = UserRuntimeView.instance().getCompany().getCompanySettings();
    if (settings.getLandScapeFormat().equals(SystemConstants.PRINT_SINGLE_LINE)) {
      getSalesReturnSplit().setPrintType(SystemConstants.PRINT_SINGLE_LINE);
    } else {
      getSalesReturnSplit().setPrintType(SystemConstants.PRINT_MULTIPLE_LINE);
    }
    Jsf.popupForm(getTaxCalculator().getSalesReturnPrintIText(), getSalesReturnSplit());
  }

  public void printPortrait(MainView main) {
    getSalesReturnSplit().setPrintType(SystemConstants.PRINT_PORTRAIT);
    Jsf.popupForm(getTaxCalculator().getSalesReturnPrintIText(), getSalesReturnSplit());
  }

  public void export(MainView main) {
    try {
      String name = "Sales_Invoice_items";
      LineItemExport.exportSalesReturnItems(name, main, getSalesReturn(), getSalesReturnItemList());
    } catch (Throwable t) {
      main.rollback(t, "error.export");
    } finally {
      main.close();
    }
  }

  public boolean isDamagedSalesReturnSplit() {
    return (getSalesReturnSplit().getSalesReturnType() != null && getSalesReturnSplit().getSalesReturnType().equals(SystemConstants.SALES_RETURN_TYPE_DAMAGED));
  }

  public boolean isTaxable() {
    return (getSalesReturn().getIsTaxable() != null ? getSalesReturn().getIsTaxable().equals(SystemConstants.DEFAULT) : true);
  }

  public Company getCompany() {
    return UserRuntimeView.instance().getCompany();
  }

//  public void setPageTitle(String pageTitle) {
//    this.pageTitle = pageTitle;
//  }
  public SalesReturn getSalesReturn() {
    return salesReturn;
  }

  public void setSalesReturn(SalesReturn salesReturn) {
    this.salesReturn = salesReturn;
  }

  public SalesReturnSplit getSalesReturnSplit() {
    if (salesReturnSplit == null) {
      salesReturnSplit = new SalesReturnSplit();
    }
    return salesReturnSplit;
  }

  public void setSalesReturnSplit(SalesReturnSplit salesReturnSplit) {
    this.salesReturnSplit = salesReturnSplit;
  }

  public AccountGroup getAccountGroup() {
    if (accountGroup == null) {
      accountGroup = UserRuntimeView.instance().getAccountGroup();
    }
    return accountGroup;
  }

  public TaxCalculator getTaxCalculator() {
    return taxCalculator;
  }

  public void setTaxCalculator(TaxCalculator taxCalculator) {
    this.taxCalculator = taxCalculator;
  }

  public List<SalesReturnItemSplit> getSalesReturnItemSplitList() {
    return salesReturnItemSplitList;
  }

  public void setSalesReturnItemSplitList(List<SalesReturnItemSplit> salesReturnItemSplitList) {
    this.salesReturnItemSplitList = salesReturnItemSplitList;
  }

  public List<SalesReturnItem> getSalesReturnItemList() {
    return salesReturnItemList;
  }

  public void setSalesReturnItemList(List<SalesReturnItem> salesReturnItemList) {
    this.salesReturnItemList = salesReturnItemList;
  }

  public ConsignmentRate getConsignmentRate() {
    return consignmentRate;
  }

  public void setConsignmentRate(ConsignmentRate consignmentRate) {
    this.consignmentRate = consignmentRate;
  }

  public String getLrAmount() {
    return lrAmount;
  }

  public void setLrAmount(String lrAmount) {
    this.lrAmount = lrAmount;
  }

  public Double getOldLrAmount() {
    return oldLrAmount;
  }

  public void setOldLrAmount(Double oldLrAmount) {
    this.oldLrAmount = oldLrAmount;
  }

  public Double getLrAmountMax() {
    return lrAmountMax;
  }

  public void setLrAmountMax(Double lrAmountMax) {
    this.lrAmountMax = lrAmountMax;
  }

  public boolean isValueMrpChanged() {
    return valueMrpChanged;
  }

  public void setValueMrpChanged(boolean valueMrpChanged) {
    this.valueMrpChanged = valueMrpChanged;
  }

  public boolean isIntraStateSales() {
    return intraStateSales;
  }

  public void setIntraStateSales(boolean intraStateSales) {
    this.intraStateSales = intraStateSales;
  }

  public boolean isInterStateSales() {
    return interStateSales;
  }

  public void setInterStateSales(boolean interStateSales) {
    this.interStateSales = interStateSales;
  }

  public List<TaxCode> getTaxCodeList() {
    return taxCodeList;
  }

  public void setTaxCodeList(List<TaxCode> taxCodeList) {
    this.taxCodeList = taxCodeList;
  }

  public Integer getRowIndex() {
    return rowIndex;
  }

  public void setRowIndex(Integer rowIndex) {
    this.rowIndex = rowIndex;
  }

}
