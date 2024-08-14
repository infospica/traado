/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.fin.service.AccountingFinancialYearService;
import spica.fin.service.CompanyFinancialYearService;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.Company;
import spica.scm.domain.CompanySettings;
import spica.scm.service.AccountGroupDocPrefixService;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
@Named(value = "financialYearView")
@ViewScoped
public class FinancialYearView implements Serializable {

  private CompanySettings companySettings;
  private transient Company company;
  private transient List<AccountingFinancialYear> financialYearList;
  private transient AccountingFinancialYear[] financialYearListSelected;
  private transient LazyDataModel<AccountingFinancialYear> financialYearLazyModel; 	//For lazy loading datatable.

  @PostConstruct
  public void init() {
    companySettings = (CompanySettings) Jsf.dialogParent(CompanySettings.class);
    company = companySettings != null ? companySettings.getCompanyId() : null;
  }

  public CompanySettings getCompanySettings() {
    return companySettings;
  }

  public void setCompanySettings(CompanySettings companySettings) {
    this.companySettings = companySettings;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public List<AccountingFinancialYear> getFinancialYearList() {
    return financialYearList;
  }

  public void setFinancialYearList(List<AccountingFinancialYear> financialYearList) {
    this.financialYearList = financialYearList;
  }

  public AccountingFinancialYear[] getFinancialYearListSelected() {
    return financialYearListSelected;
  }

  public void setFinancialYearListSelected(AccountingFinancialYear[] financialYearListSelected) {
    this.financialYearListSelected = financialYearListSelected;
  }

  public LazyDataModel<AccountingFinancialYear> getFinancialYearLazyModel() {
    return financialYearLazyModel;
  }

  public void setFinancialYearLazyModel(LazyDataModel<AccountingFinancialYear> financialYearLazyModel) {
    this.financialYearLazyModel = financialYearLazyModel;
  }

  public String switchFinancialYear(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        loadFinancialYearList(main);
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create financialYearLazyModel.
   *
   * @param main
   */
  private void loadFinancialYearList(final MainView main) {
    if (financialYearLazyModel == null) {
      financialYearLazyModel = new LazyDataModel<AccountingFinancialYear>() {
        private List<AccountingFinancialYear> list;

        @Override
        public List<AccountingFinancialYear> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = AccountingFinancialYearService.listPaged(main, getCompany());
            main.commit(financialYearLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AccountingFinancialYear accountingFinancialYear) {
          return accountingFinancialYear.getId();
        }

        @Override
        public AccountingFinancialYear getRowData(String rowKey) {
          if (list != null) {
            for (AccountingFinancialYear obj : list) {
              if (rowKey.equals(obj.getId().toString())) {
                return obj;
              }
            }
          }
          return null;
        }
      };
    }
  }

  public void financialYearPopupClose() {
    Jsf.popupReturn(getCompanySettings(), null);
  }

  public void addNewFinancialYear(MainView main) {
    try {
      if (financialYearListSelected != null) {
        for (AccountingFinancialYear financialYear : financialYearListSelected) {
          CompanyFinancialYearService.addNewFinancialYear(main, financialYear, company);
          AccountGroupDocPrefixService.addNewPrefixesForNewFinancialYear(main, company, financialYear);
        }
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }
}
