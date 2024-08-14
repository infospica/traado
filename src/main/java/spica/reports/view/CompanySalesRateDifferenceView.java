/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import spica.reports.model.CompanySalesRateDifference;
import spica.reports.model.CompanyCustomerSalesRender;
import spica.reports.model.FilterParameters;
import spica.reports.service.CompanySalesRateDifferenceService;
import spica.scm.domain.Customer;
import spica.sys.UserRuntimeService;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author krishna.vm
 */
@Named(value = "companySalesRateDifferenceView")
@ViewScoped
public class CompanySalesRateDifferenceView implements Serializable {

  private transient List<CompanySalesRateDifference> companySalesRateDifferenceList;
  private transient FilterParameters filterParameters;
  private String[] selectedColumns;
  private List<String> columnNames;
  private transient boolean renValue;
  private transient CompanyCustomerSalesRender companyCustomerSalesRender;

  public List<CompanySalesRateDifference> getCompanySalesRateDifferenceList(MainView main) {
    if (StringUtil.isEmpty(companySalesRateDifferenceList)) {
      try {
        if (filterParameters.getFromDate() == null) {
          filterParameters.setFromDate(UserRuntimeView.instance().getStartFiscalDate());
        }
        companyCustomerSalesRender = new CompanyCustomerSalesRender();
        companyCustomerSalesRender.setRender(true);
        companySalesRateDifferenceList = CompanySalesRateDifferenceService.getCompanySalesRateDifferenceList(main, UserRuntimeView.instance().getCompany(), getFilterParameters());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return companySalesRateDifferenceList;
  }

  public void setCompanySalesRateDifferenceList(List<CompanySalesRateDifference> companySalesRateDifferenceList) {
    this.companySalesRateDifferenceList = companySalesRateDifferenceList;
  }

  public List<Customer> customerAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      return UserRuntimeService.getCustomerList(main, UserRuntimeView.instance().getCompany(), filter);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
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

  public void applyFilter(ValueChangeEvent event) {
    companySalesRateDifferenceList = null;
    columnNames = null;
    selectedColumns = null;

  }

  public void submitForm() {
    applyFilter(null);
  }

  public void applyColumnFilter(AjaxBehaviorEvent event) {

    companyCustomerSalesRender.setRender(false);
    for (String selectedColumn : selectedColumns) {
      if (selectedColumn.equals("Supplier Name")) {
        companyCustomerSalesRender.setSupplierName(true);
      } else if (selectedColumn.equals("Customer Name")) {
        companyCustomerSalesRender.setCustomerName(true);
      } else if (selectedColumn.equals("GST No")) {
        companyCustomerSalesRender.setGstNo(true);
      } else if (selectedColumn.equals("Invoice No")) {
        companyCustomerSalesRender.setInvoiceNo(true);
      } else if (selectedColumn.equals("Invoice Date")) {
        companyCustomerSalesRender.setInvoiceDate(true);
      } else if (selectedColumn.equals("MFR Code")) {
        companyCustomerSalesRender.setMfrCode(true);
      } else if (selectedColumn.equals("Product Name")) {
        companyCustomerSalesRender.setProductName(true);
      } else if (selectedColumn.equals("HSN Code")) {
        companyCustomerSalesRender.setHsnCode(true);
      } else if (selectedColumn.equals("Pack Type")) {
        companyCustomerSalesRender.setPackType(true);
      } else if (selectedColumn.equals("Batch No")) {
        companyCustomerSalesRender.setBatchNo(true);
      } else if (selectedColumn.equals("Expiry date")) {
        companyCustomerSalesRender.setExpiryDate(true);
      } else if (selectedColumn.equals("Qty")) {
        companyCustomerSalesRender.setQty(true);
      } else if (selectedColumn.equals("Qty Free")) {
        companyCustomerSalesRender.setQtyFree(true);
      } else if (selectedColumn.equals("MRP")) {
        companyCustomerSalesRender.setValueMrp(true);
      } else if (selectedColumn.equals("PTS")) {
        companyCustomerSalesRender.setValuePts(true);
      } else if (selectedColumn.equals("RATE")) {
        companyCustomerSalesRender.setValueRate(true);
      } else if (selectedColumn.equals("Goods Value")) {
        companyCustomerSalesRender.setGoodsValue(true);
      } else if (selectedColumn.equals("Scheme Discount")) {
        companyCustomerSalesRender.setSchemeDiscount(true);
      } else if (selectedColumn.equals("Product Discount")) {
        companyCustomerSalesRender.setProductDiscount(true);
      } else if (selectedColumn.equals("Invoice Discount")) {
        companyCustomerSalesRender.setInvoiceDiscount(true);
      } else if (selectedColumn.equals("Rate Diff")) {
        companyCustomerSalesRender.setRateDiff(true);
      }
    }

  }

  public boolean getRenValue() {
    return renValue;
  }

  public String[] getSelectedColumns() {
    return selectedColumns;
  }

  public void setSelectedColumns(String[] selectedColumns) {
    this.selectedColumns = selectedColumns;
  }

  public List<String> getColumnNames() {
    if (columnNames == null) {
      columnNames = new ArrayList<String>();

      columnNames.add("Supplier Name");

      columnNames.add("Customer Name");
      columnNames.add("GST No");

      columnNames.add("Invoice No");
      columnNames.add("Invoice Date");

      columnNames.add("MFR Code");
      columnNames.add("Product Name");
      columnNames.add("HSN Code");
      columnNames.add("Pack Type");
      columnNames.add("Batch No");
      columnNames.add("Expiry date");

      columnNames.add("Qty");
      columnNames.add("Qty Free");
      columnNames.add("MRP");
      columnNames.add("PTS");
      columnNames.add("RATE");

      columnNames.add("Goods Value");
      columnNames.add("Scheme Discount");
      columnNames.add("Product Discount");
      columnNames.add("Invoice Discount");
      columnNames.add("Rate Diff");

      selectedColumns = new String[columnNames.size()];
      selectedColumns = columnNames.toArray(selectedColumns);
    }

    return columnNames;
  }

  public void setColumnNames(List<String> columnNames) {
    this.columnNames = columnNames;
  }

  public CompanyCustomerSalesRender getCompanyCustomerSalesRender() {
    return companyCustomerSalesRender;
  }

  public void setCompanyCustomerSalesRender(CompanyCustomerSalesRender companyCustomerSalesRender) {
    this.companyCustomerSalesRender = companyCustomerSalesRender;
  }

}
