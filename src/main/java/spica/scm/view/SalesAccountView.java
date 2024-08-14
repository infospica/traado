/*
 * @(#)SalesAccountView.java	1.0 Fri Dec 23 10:28:08 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.SalesAccount;
import spica.scm.service.SalesAccountService;
import spica.scm.domain.Company;
import spica.scm.domain.Customer;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.CustomerTradeProfile;
import spica.scm.service.AccountGroupService;
import spica.scm.service.TradeProfileService;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * SalesAccountView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Dec 23 10:28:08 IST 2016
 */
@Named(value = "salesAccountView")
@ViewScoped
public class SalesAccountView implements Serializable {

  private transient SalesAccount salesAccount;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesAccount> salesAccountLazyModel; 	//For lazy loading datatable.
  private transient SalesAccount[] salesAccountSelected;	 //Selected Domain Array
  private transient Customer parentCustomer;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    setParentCustomer((Customer) Jsf.popupParentValue(Customer.class));
    getSalesAccount().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Default Constructor.
   */
  public SalesAccountView() {
    super();
  }

  /**
   * Return SalesAccount.
   *
   * @return SalesAccount.
   */
  public SalesAccount getSalesAccount() {
    if (salesAccount == null) {
      salesAccount = new SalesAccount();
    }
    return salesAccount;
  }

  /**
   * Set SalesAccount.
   *
   * @param salesAccount.
   */
  public void setSalesAccount(SalesAccount salesAccount) {
    this.salesAccount = salesAccount;
  }

  public Customer getParentCustomer() {
    return parentCustomer;
  }

  public void setParentCustomer(Customer parentCustomer) {
    this.parentCustomer = parentCustomer;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesAccount(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getSalesAccount().reset();
          getSalesAccount().setCustomerId(getParentCustomer());
          getSalesAccount().setCompanyId(UserRuntimeView.instance().getCompany());
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setSalesAccount((SalesAccount) SalesAccountService.selectByPk(main, getSalesAccount()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadSalesAccountList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create salesAccountLazyModel.
   *
   * @param main
   */
  private void loadSalesAccountList(final MainView main) {
    if (salesAccountLazyModel == null) {
      salesAccountLazyModel = new LazyDataModel<SalesAccount>() {
        private List<SalesAccount> list;

        @Override
        public List<SalesAccount> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesAccountService.listPaged(main);
            main.commit(salesAccountLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesAccount salesAccount) {
          return salesAccount.getId();
        }

        @Override
        public SalesAccount getRowData(String rowKey) {
          if (list != null) {
            for (SalesAccount obj : list) {
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

  private void uploadFiles() {
    String SUB_FOLDER = "scm_sales_account/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesAccount(MainView main) {
    return saveOrCloneSalesAccount(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesAccount(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesAccount(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesAccount(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesAccountService.insertOrUpdate(main, getSalesAccount());
            break;
          case "clone":
            SalesAccountService.clone(main, getSalesAccount());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many SalesAccount.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesAccount(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesAccountSelected)) {
        SalesAccountService.deleteByPkArray(main, getSalesAccountSelected()); //many record delete from list
        main.commit("success.delete");
        salesAccountSelected = null;
      } else {
        SalesAccountService.deleteByPk(main, getSalesAccount());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())) {
          main.setViewType(ViewTypes.newform);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Return LazyDataModel of SalesAccount.
   *
   * @return
   */
  public LazyDataModel<SalesAccount> getSalesAccountLazyModel() {
    return salesAccountLazyModel;
  }

  /**
   * Return SalesAccount[].
   *
   * @return
   */
  public SalesAccount[] getSalesAccountSelected() {
    return salesAccountSelected;
  }

  /**
   * Set SalesAccount[].
   *
   * @param salesAccountSelected
   */
  public void setSalesAccountSelected(SalesAccount[] salesAccountSelected) {
    this.salesAccountSelected = salesAccountSelected;
  }

  /**
   * Company autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.companyAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.companyAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Company> companyAuto(String filter) {
    return ScmLookupView.companyAuto(filter);
  }

  /**
   * Customer autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.customerAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.customerAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Customer> customerAuto(String filter) {
    return ScmLookupView.customerAuto(filter);
  }

  /**
   * AccountGroup autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountGroupAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountGroupAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountGroup> accountGroupAuto(String filter) {
    return ScmLookupExtView.accountGroupForSalesAccount(filter, getParentCustomer(), getSalesAccount());
  }

  /**
   *
   * @param main
   * @return
   */
  public List<CustomerTradeProfile> customerTradeProfile(MainView main) {
    try {
      return TradeProfileService.customerTradeProfile(main, getParentCustomer());
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Method to select the account group price list.
   *
   * @return
   */
  public List<AccountGroupPriceList> accountGroupPriceListByAccountGroup() {
    List<AccountGroupPriceList> accountGroupPriceList = null;
    if (getSalesAccount().getAccountGroupId() != null) {
      accountGroupPriceList = ScmLookupExtView.accountGroupPriceListByAccountGroup(getSalesAccount().getAccountGroupId());
      if (accountGroupPriceList != null && !accountGroupPriceList.isEmpty() && accountGroupPriceList.size() == 1 && getSalesAccount().getAccountGroupPriceListId() == null) {
        getSalesAccount().setAccountGroupPriceListId(accountGroupPriceList.get(0));
      }
    }
    return accountGroupPriceList;
  }

  /**
   *
   * @param main
   * @return
   */
  public List<AccountGroup> customerAccountGroup(MainView main) {
    try {
      return AccountGroupService.customerAccountGroup(main, UserRuntimeView.instance().getCompany(), getSalesAccount().getCustomerTradeProfileId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   * @param event
   */
  public void itemSelectEvent(SelectEvent event) {
    getSalesAccount().setCustomerTradeProfileId((CustomerTradeProfile) event.getObject());
  }

  /**
   *
   * @param main
   * @param salesAccount
   * @return
   */
  public String deleteSalesAccount(MainView main, SalesAccount salesAccount) {
    try {
      SalesAccountService.deleteSalesAccount(main, salesAccount);
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   */
  public void customerSalesAccPopupClose() {
    Jsf.popupReturn(getParentCustomer(), null);
  }
}
