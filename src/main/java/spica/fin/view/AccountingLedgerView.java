/*
 * @(#)AccountingLedgerView.java	1.0 Fri Feb 24 15:58:33 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.fin.service.LedgerExternalDataService;
import spica.fin.domain.AccountingEntityType;
import spica.fin.domain.AccountingGroup;
import spica.fin.domain.AccountingLedger;
import spica.scm.domain.Company;
import spica.scm.domain.Country;
import spica.scm.domain.State;
import spica.fin.domain.TaxCode;
import spica.fin.domain.TradeOutstanding;
import spica.fin.service.AccountingLedgerService;
import spica.fin.service.TradeOutstandingService;
import spica.constant.AccountingConstant;
import spica.fin.service.AccountingLedgerTransactionService;
import spica.scm.util.AppUtil;
import spica.scm.view.ScmLookupExtView;
import spica.scm.view.ScmLookupView;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * AccountingLedgerView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 24 15:58:33 IST 2017
 */
@Named(value = "accountingLedgerView")
@ViewScoped
public class AccountingLedgerView implements Serializable {

  private transient AccountingLedger accountingLedger;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountingLedger> accountingLedgerLazyModel; 	//For lazy loading datatable.
  private transient AccountingLedger[] accountingLedgerSelected;	 //Selected Domain Array
  private transient boolean billwise;
  private transient List<TradeOutstanding> tradeOutstandingList;
  private transient AccountingGroup selectedAccountingGroup;
  private transient boolean rcm;
  private transient boolean tds;
  String gstNo;
  private transient Double closingBalanceCredit;
  private transient Double closingBalanceDebit;

  @Inject
  private AccountingMainView accountingMainView;

  /**
   * Default Constructor.
   */
  public AccountingLedgerView() {
    super();
  }

  @PostConstruct
  public void init() {
    accountingLedger = (AccountingLedger) Jsf.popupParentValue(AccountingLedger.class);
  }

  /**
   * Return AccountingLedger.
   *
   * @return AccountingLedger.
   */
  public AccountingLedger getAccountingLedger() {
    if (accountingLedger == null) {
      accountingLedger = new AccountingLedger();
    }
    if (accountingLedger.getCompanyId() == null) {
      accountingLedger.setCompanyId(UserRuntimeView.instance().getCompany());
    }
    return accountingLedger;
  }

  /**
   * Set AccountingLedger.
   *
   * @param accountingLedger.
   */
  public void setAccountingLedger(AccountingLedger accountingLedger) {
    this.accountingLedger = accountingLedger;
  }

  public List<TradeOutstanding> getTradeOutstandingList() {
    if (tradeOutstandingList == null) {
      tradeOutstandingList = new ArrayList<>();
    }
    return tradeOutstandingList;
  }

  public void setTradeOutstandingList(List<TradeOutstanding> tradeOutstandingList) {
    this.tradeOutstandingList = tradeOutstandingList;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAccountingLedger(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getAccountingLedger().reset();
          getAccountingLedger().setCurrencyId(getAccountingLedger().getCompanyId().getCurrencyId());
          billwise = false;
          // setRender(null);
        } else if (main.isEdit() && !main.hasError()) {
          setAccountingLedger((AccountingLedger) AccountingLedgerService.selectByPk(main, getAccountingLedger()));
          getBalance(main);
          billwise = (getAccountingLedger().getBillwise() != null && getAccountingLedger().getBillwise() == AccountingConstant.BILLWISE);
          tds = (getAccountingLedger().getTds() != null && getAccountingLedger().getTds() == AccountingConstant.TDS);
          rcm = (getAccountingLedger().getRcm() != null && getAccountingLedger().getRcm() == AccountingConstant.RCM);
          // setRender(getAccountingLedger().getAccountingGroupId());
          if (getAccountingLedger().isVendorOrCustomers()) {
            setTradeOutstandingList(TradeOutstandingService.selectOutstandingByOpeningLedger(main, getAccountingLedger()));
          }
        } else if (main.isList()) {
          getAccountingLedger().setCompanyId(null);
          loadAccountingLedgerList(main);
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
   * Create accountingLedgerLazyModel.
   *
   * @param main
   */
  private void loadAccountingLedgerList(final MainView main) {
    if (accountingLedgerLazyModel == null) {
      accountingLedgerLazyModel = new LazyDataModel<AccountingLedger>() {
        private List<AccountingLedger> list;

        @Override
        public List<AccountingLedger> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = AccountingLedgerService.listPaged(main, getAccountingLedger().getCompanyId(), getSelectedAccountingGroup());
            main.commit(accountingLedgerLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AccountingLedger accountingLedger) {
          return accountingLedger.getId();
        }

        @Override
        public AccountingLedger getRowData(String rowKey) {
          if (list != null) {
            for (AccountingLedger obj : list) {
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

  public void saveOpeningBalance() {
    double openingBalance = 0;
    double creditValue = 0;
    double debitValue = 0;
    if (!StringUtil.isEmpty(getTradeOutstandingList())) {
      for (TradeOutstanding tradeOut : getTradeOutstandingList()) {
        if (tradeOut.getIsDebitOrCredit() == AccountingConstant.IS_DEBIT) {
          debitValue += tradeOut.getValueAfterTax() == null ? 0 : tradeOut.getValueAfterTax();
          tradeOut.setValueRemainingBalance(tradeOut.getValueAfterTax());
          tradeOut.setValueBeforeTax(tradeOut.getValueAfterTax());
        } else if (tradeOut.getIsDebitOrCredit() == AccountingConstant.IS_CREDIT) {
          creditValue += tradeOut.getValueAfterTax() == null ? 0 : tradeOut.getValueAfterTax();
          tradeOut.setValueRemainingBalance(tradeOut.getValueAfterTax());
          tradeOut.setValueBeforeTax(tradeOut.getValueAfterTax());
        }
      }

      if (creditValue > debitValue) {
        openingBalance = creditValue - debitValue;
        getAccountingLedger().setIsDebitOrCredit(AccountingConstant.IS_CREDIT);
      } else if (debitValue > creditValue) {
        openingBalance = debitValue - creditValue;
        getAccountingLedger().setIsDebitOrCredit(AccountingConstant.IS_DEBIT);
      }

      //double openingBalance = getTradeOutstandingList().stream().map(TradeOutstanding::getBalanceAmount).collect(Collectors.summingDouble(i -> i));
      if (openingBalance != 0) {
        hasChange = true;
        getAccountingLedger().setOpeningBalance(Math.abs(openingBalance));
      } else {
        hasChange = false;
        getAccountingLedger().setOpeningBalance(null);
      }
    }
    Jsf.execute("PF('openBalanceDlgVar').hide()");
  }

  private transient boolean hasChange = false;

  public void openingBalanceModified(ValueChangeEvent event) {
    if ((event.getOldValue() == null || event.getNewValue() == null) || (!event.getNewValue().equals(event.getOldValue()))) {
      hasChange = true;
    }
  }

  private void uploadFiles() {
    String SUB_FOLDER = "fin_accounting_ledger/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveAccountingLedger(MainView main) {
    return saveOrCloneAccountingLedger(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccountingLedger(MainView main) {
    main.setViewType("newform");
    return saveOrCloneAccountingLedger(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccountingLedger(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            getAccountingLedger().setCurrencyId(getAccountingLedger().getCompanyId().getCurrencyId());
            if (getAccountingLedger().isDebtorsOrCreditors()) {
              getAccountingLedger().setBillwise(billwise ? 1 : 0);
              getAccountingLedger().setTds(isTds() ? 1 : 0);
              getAccountingLedger().setRcm(isRcm() ? 1 : 0);
            } else {
              getAccountingLedger().setBillwise(0);
              getAccountingLedger().setTds(0);
              getAccountingLedger().setRcm(0);
            }

            LedgerExternalDataService.setChildTaxCodes(main, getAccountingLedger());
            if (!getAccountingLedger().isExpense()) {
              getAccountingLedger().setIgstId(null);
              getAccountingLedger().setCgstId(null);
              getAccountingLedger().setSgstId(null);
              getAccountingLedger().setHsnSacCode(null);
            }
            if (getAccountingLedger().getOpeningBalance() != null) {
              getAccountingLedger().setOpeningBalance(getAccountingLedger().getOpeningBalance() <= 0.0 ? null : getAccountingLedger().getOpeningBalance());
            }
            AccountingLedgerService.insertOrUpdate(main, getAccountingLedger());
            if (hasChange) {
              AccountingLedgerService.insertOrUpdateOpeningBalance(main, getAccountingLedger(), getTradeOutstandingList());
              hasChange = false;
            }

            break;
          case "clone":
            AccountingLedgerService.clone(main, getAccountingLedger());
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
   * Delete one or many AccountingLedger.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountingLedger(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(accountingLedgerSelected)) {
        AccountingLedgerService.deleteByPkArray(main, getAccountingLedgerSelected()); //many record delete from list
        main.commit("success.delete");
        accountingLedgerSelected = null;
      } else {
        AccountingLedgerService.deleteByPk(main, getAccountingLedger());  //individual record delete from list or edit form
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
   * Return LazyDataModel of AccountingLedger.
   *
   * @return
   */
  public LazyDataModel<AccountingLedger> getAccountingLedgerLazyModel() {
    return accountingLedgerLazyModel;
  }

  /**
   * Return AccountingLedger[].
   *
   * @return
   */
  public AccountingLedger[] getAccountingLedgerSelected() {
    return accountingLedgerSelected;
  }

  /**
   * Set AccountingLedger[].
   *
   * @param accountingLedgerSelected
   */
  public void setAccountingLedgerSelected(AccountingLedger[] accountingLedgerSelected) {
    this.accountingLedgerSelected = accountingLedgerSelected;
  }

  /**
   * AccountingGroup autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountingGroupAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountingGroupAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountingGroup> accountingGroupAuto(String filter) {
    return FinLookupView.accountingGroupAuto(filter, getAccountingLedger().getCompanyId().getId());
  }

  /**
   * AccountingEntityType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountingEntityTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountingEntityTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountingEntityType> accountingEntityTypeAuto(String filter) {
    return ScmLookupView.accountingEntityTypeAuto(filter);
  }

  /**
   * ScmCompany autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.scmCompanyAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.scmCompanyAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Company> scmCompanyAuto(String filter) {
    return ScmLookupView.companyAuto(filter);
  }

  public boolean isBillwise() {
    return billwise;
  }

  public void setBillwise(boolean billwise) {
    this.billwise = billwise;
  }

  public List<TaxCode> taxCodeAuto() {
    return FinLookupView.taxCodeGstHead(getAccountingLedger().getCompanyId());
  }

  public List<TaxCode> taxCodeTdsAuto() {
    return FinLookupView.taxCodeTdsHead(getAccountingLedger().getCompanyId());
  }

  public void accountingGroupSelectEvent(SelectEvent event) {
    //setRender((AccountingGroup) event.getObject());
  }

//  private void setRender(AccountingGroup accountingGroup) {
//    //expenseOrincome = false;
//    //  sundryDebtorsOrCreditors = false;
//    if (accountingGroup == null || accountingGroup.getAccountingHeadId() == null || accountingGroup.getAccountingHeadId().getId() == null) {
//      return;
//    }
//
////    int headId = accountingGroup.getId().intValue();
//    //   expenseOrincome = (UserRuntimeService.GROUP_DIRECT_EXPENSE.getId() == headId || UserRuntimeService.GROUP_INDIRECT_EXPENSE.getId() == headId);
//    //   sundryDebtorsOrCreditors = (UserRuntimeService.ACCOUNTING_GROUP_SUNDRY_CREDITORS.getId() == headId || UserRuntimeService.ACCOUNTING_GROUP_SUNDRY_DEBTORS.getId() == headId);
//    //   openingBalancePopupRender = (getAccountingLedger().getAccountingEntityTypeId() != null && (getAccountingLedger().getAccountingEntityTypeId().getId().equals(UserRuntimeService.ACC_ENTITY_CUSTOMER.getId()) || getAccountingLedger().getAccountingEntityTypeId().getId().equals(UserRuntimeService.ACC_ENTITY_VENDOR.getId())));
//    // openingBalanceEditable = (getAccountingLedger().getOpeningBalance() == null);
//  }
  public boolean isRcm() {
    return rcm;
  }

  public void setRcm(boolean rcm) {
    this.rcm = rcm;
  }

  public boolean isTds() {
    return tds;
  }

  public void setTds(boolean tds) {
    this.tds = tds;
    System.out.println("spica.fin.view.AccountingLedgerView.setTds()" + tds);
  }

  public boolean hasOpeningBalance() {
    return (getAccountingLedger().getOpeningBalance() == null);
  }

  public List<Country> countryAuto(String filter) {
    return ScmLookupView.countryAuto(filter);
  }

  public void countrySelectEvent(SelectEvent event) {
    Country country = (Country) event.getObject();
    if (country != null) {
      if (getAccountingLedger().getCountryId() == null) {
        getAccountingLedger().setCountryId(country);
      } else if (!getAccountingLedger().getCountryId().getId().equals(country.getId())) {
        getAccountingLedger().setStateId(null);
      }
      if (country.getCurrencyId() != null) {
        getAccountingLedger().setCurrencyId(country.getCurrencyId());
      }
    } else {
      getAccountingLedger().setStateId(null);
    }
  }

  public List<State> stateAuto(String filter) {
    if (getAccountingLedger().getCountryId() != null && getAccountingLedger().getCountryId().getId() != null) {
      return ScmLookupExtView.addressStateAuto(getAccountingLedger().getCountryId().getId(), filter);
    }
    return null;
  }

  public void stateSelectEvent(SelectEvent event) {
    State state = (State) event.getObject();
    if (state != null) {
      if (getAccountingLedger().getStateId() == null) {
        getAccountingLedger().setStateId(state);
      } else if (!getAccountingLedger().getStateId().getId().equals(state.getId())) {
        getAccountingLedger().setStateId(state);

      }
    } else {
      getAccountingLedger().setStateId(null);
    }
  }

//  public boolean isSundryDebtorsOrCreditors() {
//    return sundryDebtorsOrCreditors;
//  }
  public boolean isValidGst() {
    return AppUtil.isValidGstin(accountingLedger.getGstTaxNo());
  }

  public void gstinChangeEventHandler() {
    System.out.println(getGstNo());

  }

  public String getGstNo() {
    return gstNo;
  }

  public void setGstNo(String gstNo) {
    this.gstNo = gstNo;
  }

  public AccountingGroup getSelectedAccountingGroup() {
    return selectedAccountingGroup;
  }

  public void setSelectedAccountingGroup(AccountingGroup selectedAccountingGroup) {
    this.selectedAccountingGroup = selectedAccountingGroup;
  }

  public void loadAccountGroupList(TradeOutstanding outstanding) {
    if (outstanding.getAccountGroupList() == null) {
      if (getAccountingLedger().isCustomer()) {
        outstanding.setAccountGroupList(ScmLookupExtView.accountGroupByCustomerAccount(getAccountingLedger().getEntityId(), outstanding.getAccountId()));
      } else {
        outstanding.setAccountGroupList(ScmLookupExtView.accountGroupByAccount(outstanding.getAccountId()));
      }
      if (!StringUtil.isEmpty(outstanding.getAccountGroupList())) {
        if (outstanding.getAccountGroupList().size() == 1) {
          outstanding.setAccountGroupId(outstanding.getAccountGroupList().get(0));
        }
      }
    }
  }

  private void getBalance(MainView main) {
    if (accountingLedger != null) {
      AccountingLedgerTransactionService.selectLedgerBalance(main, accountingLedger);
      closingBalanceCredit = Math.abs(accountingLedger.getBalanceCredit() == null ? 0 : accountingLedger.getBalanceCredit());
      closingBalanceDebit = accountingLedger.getBalanceDebit() == null ? 0 : accountingLedger.getBalanceDebit();
    }
  }

  public Double getClosingBalanceCredit() {
    return closingBalanceCredit;
  }

  public void setClosingBalanceCredit(Double closingBalanceCredit) {
    this.closingBalanceCredit = closingBalanceCredit;
  }

  public Double getClosingBalanceDebit() {
    return closingBalanceDebit;
  }

  public void setClosingBalanceDebit(Double closingBalanceDebit) {
    this.closingBalanceDebit = closingBalanceDebit;
  }

}
