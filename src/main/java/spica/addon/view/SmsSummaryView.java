/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.addon.view;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import spica.addon.model.SmsUsages;
import spica.addon.service.SmsUsageService;
import spica.fin.domain.SmsLog;
import spica.reports.model.FilterParameters;
import spica.scm.domain.Company;
import spica.scm.domain.CompanySettings;
import spica.scm.service.CompanySettingsService;
import spica.sys.FileConstant;
import spica.sys.UserRuntimeView;
import spica.sys.domain.SmsProvider;
import wawo.app.config.ViewType;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.DateUtil;
import wawo.entity.util.StringUtil;

/**
 *
 * @author sujesh
 */
@Named(value = "smsSummaryView")
@ViewScoped
public class SmsSummaryView implements Serializable {

  private SmsUsages smsUsages;
  private List<CompanySettings> usageList;
  private List<SmsLog> smsLogList;
  private Integer companyId;
  private FilterParameters filterParameters;
  private transient int tabIndex;
  private List<SmsProvider> smsProviders;
  private transient SmsProvider selectedProvider;
  private Integer smsAllowed = null;
  private boolean shareSms;

  @PostConstruct
  public void init() {
    companyId = (Integer) Jsf.popupParentValue(Integer.class);
  }

  public void loadAllStatus(MainView main) {
    loadSmsUsages(main);
    loadProvidersStatus(main);
  }

  public String switchSmsLog(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {

        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {

        } else if (ViewType.list.toString().equals(viewType)) {
          if (companyId != null) {
            getCompanySmsLog(main);
          }
        }
      } catch (Throwable t) {
        main.rollback(t,"error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public void loadSmsUsages(MainView main) {
    try {
      if (usageList == null) {
        usageList = SmsUsageService.loadCompanySmsUsages(main);
      }
    } catch (Throwable t) {
      main.rollback(t,"error.select");
    } finally {
      main.close();
    }

  }
 

  public void getCompanySmsLog(MainView main) {
    try {
      if (smsLogList == null) {
        smsLogList = SmsUsageService.loadSmsLogByCompanyAndDate(main, companyId, getFilterParameters().getFromDate(), getFilterParameters().getToDate());
      }
    } catch (Throwable t) {
      main.rollback(t,"error.select");
    } finally {
      main.close();
    }
  }

  public void loadProvidersStatus(MainView main) {
    try {
      if (smsProviders == null) {
        smsProviders = SmsUsageService.loadSmsProviders(main);
      }
    } catch (Throwable t) {
      main.rollback(t,"error.select");
    } finally {
      main.close();
    }

  }

  public void clearLog(MainView main, Integer companyId) {
    try {
      SmsUsageService.deleteAllLogByCompanyId(main, companyId);
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public void clearLogByDate(MainView main) {
    try {
      SmsUsageService.deleteAllLogByCompanyAndDate(main, companyId, getFilterParameters().getFromDate(), getFilterParameters().getToDate());
      reset(main);
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public void reset(MainView main) {
    smsLogList = null;
    getCompanySmsLog(main);
  }

  public List<CompanySettings> getUsageList() {
    return usageList;
  }

  public void setUsageList(List<CompanySettings> usageList) {
    this.usageList = usageList;
  }

  public List<SmsLog> getSmsLogList() {
    return smsLogList;
  }

  public FilterParameters getFilterParameters() {
    filterParameters = filterParameters == null ? new FilterParameters():filterParameters; 
    return filterParameters;
  }

  public void setFilterParameters(FilterParameters filterParameters) {
    this.filterParameters = filterParameters;
  }


  public String smsBalanceColor(int count) {
    if (count == 0) {
      return "black";
    } else if (count > 250 && count <= 500) {
      return "#D7DF01";
    } else if (count > 0 && count <= 250) {
      return "red";
    }
    return "green";
  }

  public void insertOrUpdateSmsProvider(MainView main) {
    try {
      if (getSelectedProvider() != null) {
        SmsUsageService.insertOrUpdate(main, selectedProvider);
        main.commit("success.save");
        smsProviders = null;
        loadProvidersStatus(main);

      } else {
        main.error("error.save");
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void updateAvailableQty() {
    if (smsAllowed == null) {
      smsAllowed = getSelectedProvider().getAvailableQuantity() == null ? 0 : getSelectedProvider().getAvailableQuantity();
    }
    getSelectedProvider().setAvailableQuantity(smsAllowed + selectedProvider.getSmsQuantity());
  }

  public int getTabIndex() {
    return tabIndex;
  }

  public void setTabIndex(int tabIndex) {
    this.tabIndex = tabIndex;
  }

  public List<SmsProvider> getSmsProviders() {
    return smsProviders;
  }

  public void setSmsProviders(List<SmsProvider> smsProviders) {
    this.smsProviders = smsProviders;
  }

  public SmsProvider getSelectedProvider() {
    selectedProvider = selectedProvider == null ? new SmsProvider() : selectedProvider;
    return selectedProvider;
  }

  public void setSelectedProvider(SmsProvider selectedProvider) {
    this.selectedProvider = selectedProvider;
    smsAllowed = null;
  }

  public void shareAllowedSmsToOtherCompany(SmsProvider shareSmsProvider) {
    setTabIndex(0);
    setSelectedProvider(shareSmsProvider);
    shareSms = true;
  }

  public void updateCompanySettingSmsQuantity(MainView main, CompanySettings cs) {
    try {
      if (cs.getUpdatedSmsQuantity() != null && cs.getUpdatedSmsQuantity() > 0 && (selectedProvider.getAvailableQuantity()-cs.getUpdatedSmsQuantity())>=0) {
        cs.setSmsAllowed(cs.getSmsAllowed() + cs.getUpdatedSmsQuantity());
        CompanySettingsService.insertOrUpdate(main, cs);
        getSelectedProvider().setAvailableQuantity(selectedProvider.getAvailableQuantity()-cs.getUpdatedSmsQuantity());
        SmsUsageService.insertOrUpdate(main, selectedProvider);
        main.commit("success.save");
        usageList = null;
        smsProviders = null;
        loadSmsUsages(main) ;        
        loadProvidersStatus(main);
      } else {
        main.error("amount.not.tally");
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public boolean isShareSms() {
    return shareSms;
  }

}
