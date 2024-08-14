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
import spica.reports.model.CompanyCustomerSales;
import spica.reports.model.FilterParameters;
import spica.scm.common.PlatformSummary;
import spica.scm.domain.Account;
import spica.scm.domain.Company;
import spica.scm.domain.ProductEntry;
import spica.scm.export.ExcelSheet;
import spica.scm.service.PlatformService;
import spica.scm.util.MathUtil;
import spica.sys.UserRuntimeView;
import wawo.app.faces.MainView;
import wawo.entity.util.DateUtil;
import wawo.entity.util.StringUtil;

/**
 *
 * @author Godson Joseph
 */
@Named(value = "tradingDifferenceView")
@ViewScoped
public class TradingDifferenceView implements Serializable {

  private transient Company company;
  private transient Account account;
  private transient List<PlatformSummary> platformSummaryList;
  private transient FilterParameters filterParameters;
  private transient ProductEntry productEntry;
  private transient Double totalMarginDifference;

  public List<PlatformSummary> getPlatformSummaryList(MainView main) {
    if (StringUtil.isEmpty(platformSummaryList) && !main.hasError()) {
      try {
        platformSummaryList = new ArrayList<PlatformSummary>();
        totalMarginDifference = 0.0;
        List<PlatformSummary> list = PlatformService.getTradingDifferenceList(main, getCompany(), getAccount(), productEntry, filterParameters);
        for (PlatformSummary ps : list) {
          if (ps.getMarginDifferencePerUnit() != null && MathUtil.roundOff(ps.getMarginDifferencePerUnit(), 2) != 0) {
            platformSummaryList.add(ps);
            totalMarginDifference += ps.getTotalMarginDifference();
          }
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return platformSummaryList;
  }

  public List<ProductEntry> getProductEntryList(MainView main) {
    List<ProductEntry> productEntryList = null;
    try {
      productEntryList = PlatformService.getProductEntryList(main, getAccount());
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return productEntryList;
  }

  public void setPlatformSummaryList(List<PlatformSummary> platformSummaryList) {
    this.platformSummaryList = platformSummaryList;
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

  public void onFromDateSelect(SelectEvent event) {
    filterParameters.setFromDate((Date) event.getObject());
  }

  public void backOrNext(int pos) {
    filterParameters.setFromDate(DateUtil.moveMonths(filterParameters.getFromDate(), pos));
//    filterParameters.setToDate(DateUtil.moveMonths(filterParameters.getToDate(), pos));
    setPlatformSummaryList(null);
  }

  public void reset() {
    setCompany(null);
    setAccount(null);
    setProductEntry(null);
    setPlatformSummaryList(null);
  }

  public Long getRowKey() {
    return new Date().getTime();
  }

  public Company getCompany() {
    if (company == null) {
      company = UserRuntimeView.instance().getCompany();
    }
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public ProductEntry getProductEntry() {
    return productEntry;
  }

  public void setProductEntry(ProductEntry productEntry) {
    this.productEntry = productEntry;
  }

  private Account getAccount() {
    if (account == null) {
      account = UserRuntimeView.instance().getAccount();
    }
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Double getTotalMarginDifference() {
    return totalMarginDifference;
  }

  public void setTotalMarginDifference(Double totalMarginDifference) {
    this.totalMarginDifference = totalMarginDifference;
  }

  public void export(MainView main) {
    try {
      List<CompanyCustomerSales> debitCreditList = PlatformService.getDebitCreditList(main, getCompany(), getAccount(), productEntry, filterParameters);
      ExcelSheet.createTradingDifferenceExcel(main, platformSummaryList, debitCreditList, getCompany(), getAccount());
    } catch (Throwable ex) {
      main.rollback(ex, "error.select");
    } finally {
      main.close();
    }
  }
}
