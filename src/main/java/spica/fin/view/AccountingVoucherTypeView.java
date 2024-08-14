/*
 * @(#)AccountingVoucherTypeView.java	1.0 Fri Apr 28 12:24:49 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.fin.domain.AccountingPrefix;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.fin.domain.AccountingVoucherType;
import spica.fin.service.AccountingPrefixService;
import spica.fin.service.AccountingVoucherTypeService;
import spica.sys.UserRuntimeView;

/**
 * AccountingVoucherTypeView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Apr 28 12:24:49 IST 2017
 */
@Named(value = "accountingVoucherTypeView")
@ViewScoped
public class AccountingVoucherTypeView implements Serializable {

  private transient AccountingVoucherType accountingVoucherType;	//Domain object/selected Domain.
  private transient AccountingPrefix finAccountingPrefix;
  private transient LazyDataModel<AccountingVoucherType> accountingVoucherTypeLazyModel; 	//For lazy loading datatable.
  private transient AccountingVoucherType[] accountingVoucherTypeSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public AccountingVoucherTypeView() {
    super();
  }

  /**
   * Return AccountingVoucherType.
   *
   * @return AccountingVoucherType.
   */
  public AccountingVoucherType getAccountingVoucherType() {
    if (accountingVoucherType == null) {
      accountingVoucherType = new AccountingVoucherType();
    }
    return accountingVoucherType;
  }

  public AccountingPrefix getFinAccountingPrefix() {
    if (finAccountingPrefix == null) {
      finAccountingPrefix = new AccountingPrefix();
    }
    if (finAccountingPrefix.getCompanyId() == null) {
      finAccountingPrefix.setCompanyId(UserRuntimeView.instance().getCompany());
      finAccountingPrefix.setFinancialYearId(finAccountingPrefix.getCompanyId().getCurrentFinancialYear());
    }
    return finAccountingPrefix;
  }

  /**
   * Set AccountingVoucherType.
   *
   * @param accountingVoucherType.
   */
  public void setAccountingVoucherType(AccountingVoucherType accountingVoucherType) {
    this.accountingVoucherType = accountingVoucherType;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAccountingVoucherType(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getAccountingVoucherType().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setAccountingVoucherType((AccountingVoucherType) AccountingVoucherTypeService.selectByPk(main, getAccountingVoucherType()));
          finAccountingPrefix = AccountingPrefixService.selectByVoucherType(main, accountingVoucherType, getFinAccountingPrefix().getCompanyId());
        } else if (ViewTypes.isList(viewType)) {
          getFinAccountingPrefix().setCompanyId(null);
          loadAccountingVoucherTypeList(main);
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create accountingVoucherTypeLazyModel.
   *
   * @param main
   */
  private void loadAccountingVoucherTypeList(final MainView main) {
    if (accountingVoucherTypeLazyModel == null) {
      accountingVoucherTypeLazyModel = new LazyDataModel<AccountingVoucherType>() {
        private List<AccountingVoucherType> list;

        @Override
        public List<AccountingVoucherType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = AccountingVoucherTypeService.listPaged(main);
            main.commit(accountingVoucherTypeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AccountingVoucherType accountingVoucherType) {
          return accountingVoucherType.getId();
        }

        @Override
        public AccountingVoucherType getRowData(String rowKey) {
          if (list != null) {
            for (AccountingVoucherType obj : list) {
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
    String SUB_FOLDER = "fin_accounting_voucher_type/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveAccountingVoucherType(MainView main) {
    return saveOrCloneAccountingVoucherType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccountingVoucherType(MainView main) {
    main.setViewType("newform");
    return saveOrCloneAccountingVoucherType(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccountingVoucherType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            AccountingVoucherTypeService.insertOrUpdate(main, getAccountingVoucherType());
            getFinAccountingPrefix().setVoucherTypeId(getAccountingVoucherType());
            AccountingPrefixService.insertOrUpdate(main, getFinAccountingPrefix());
            break;
          case "clone":
            AccountingVoucherTypeService.clone(main, getAccountingVoucherType());
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
   * Delete one or many AccountingVoucherType.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountingVoucherType(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(accountingVoucherTypeSelected)) {
        AccountingVoucherTypeService.deleteByPkArray(main, getAccountingVoucherTypeSelected()); //many record delete from list
        main.commit("success.delete");
        accountingVoucherTypeSelected = null;
      } else {
        AccountingVoucherTypeService.deleteByPk(main, getAccountingVoucherType());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (ViewTypes.isEdit(main.getViewType())) {
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
   * Return LazyDataModel of AccountingVoucherType.
   *
   * @return
   */
  public LazyDataModel<AccountingVoucherType> getAccountingVoucherTypeLazyModel() {
    return accountingVoucherTypeLazyModel;
  }

  /**
   * Return AccountingVoucherType[].
   *
   * @return
   */
  public AccountingVoucherType[] getAccountingVoucherTypeSelected() {
    return accountingVoucherTypeSelected;
  }

  /**
   * Set AccountingVoucherType[].
   *
   * @param accountingVoucherTypeSelected
   */
  public void setAccountingVoucherTypeSelected(AccountingVoucherType[] accountingVoucherTypeSelected) {
    this.accountingVoucherTypeSelected = accountingVoucherTypeSelected;
  }

}
