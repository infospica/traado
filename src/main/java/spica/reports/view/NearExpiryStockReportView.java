/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.fin.common.FilterObjects;
import spica.reports.model.FilterParameters;
import spica.reports.model.NearExpiryStockReport;
import spica.reports.service.NearExpiryStockReportService;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.view.ScmLookupExtView;
import spica.sys.SystemConstants;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.DateUtil;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
@Named(value = "nearExpiryStockReportView")
@ViewScoped
public class NearExpiryStockReportView extends FilterObjects implements Serializable {

  private transient List<NearExpiryStockReport> nearExpiryStockReportList;
  private transient FilterParameters filterParameters;
  private Long qty;
  private Double stockValue;
  private Integer reportType;

  public List<NearExpiryStockReport> getNearExpiryStockReportList(MainView main) {
    if (StringUtil.isEmpty(nearExpiryStockReportList) && !main.hasError()) {
      try {
        if (filterParameters.getAccountGroup() != null || filterParameters.getAccount() != null) {
          nearExpiryStockReportList = NearExpiryStockReportService.getNearExpiryStockReportList(main, filterParameters);
          qty = 0l;
          stockValue = 0.0;
          if (!nearExpiryStockReportList.isEmpty() && nearExpiryStockReportList.size() > 0 && nearExpiryStockReportList != null) {
            for (NearExpiryStockReport list : nearExpiryStockReportList) {
              qty += (list.getQuantitySaleable() + list.getQuantityFreeSaleable());
              stockValue += ((list.getQuantitySaleable() + list.getQuantityFreeSaleable()) * list.getPurchaseRate());
            }
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return nearExpiryStockReportList;
  }

  public void setNearExpiryStockReportList(List<NearExpiryStockReport> nearExpiryStockReportList) {
    this.nearExpiryStockReportList = nearExpiryStockReportList;
  }

  public FilterParameters getFilterParameters() {
    if (filterParameters == null) {
      filterParameters = new FilterParameters();
      filterParameters.setFilterOption(SystemConstants.DEFAULT_EXPIRY_DAYS);
    }
    return filterParameters;
  }

  public void accountGroupSelectEvent(SelectEvent event) {
    AccountGroup accountGroup = (AccountGroup) event.getObject();
    if (accountGroup != null) {
      if (accountGroup.getId() != null) {
        filterParameters.setAccountGroup(accountGroup);
        filterParameters.setAccount(null);
        filterParameters.setCustomer(null);
      }
    }
    reset();
  }

  public List<Account> accountAuto(String filter) {
    if (filterParameters.getAccountGroup() != null && filterParameters.getAccountGroup().getId() != null) {
      MainView main = Jsf.getMain();
      try {
        return ScmLookupExtView.accountByAccountGroupProfile(main, filterParameters.getAccountGroup(), filter);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public void backOrNext(int pos) {
    filterParameters.setFromDate(DateUtil.moveMonths(filterParameters.getFromDate(), pos));
    reset();
  }

  public void reset() {
    setNearExpiryStockReportList(null);
  }

  public Long getQty() {
    return qty;
  }

  public void setQty(Long qty) {
    this.qty = qty;
  }

  public Double getStockValue() {
    return stockValue;
  }

  public void setStockValue(Double stockValue) {
    this.stockValue = stockValue;
  }

  public Integer getReportType() {
    if (reportType == null) {
      reportType = SystemConstants.SUMMARY;
    }
    return reportType;
  }

  public void setReportType(Integer reportType) {
    this.reportType = reportType;
    filterParameters.setReportType(Integer.toString(reportType));
  }

}
