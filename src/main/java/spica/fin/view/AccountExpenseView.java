/*
 * @(#)AccountExpenseView.java	1.0 Mon Nov 27 13:55:44 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.fin.domain.AccountExpense;
import spica.fin.service.AccountExpenseService;
import spica.scm.domain.Company;
import spica.scm.domain.Account;
import spica.fin.domain.AccountExpenseDetail;
import spica.fin.domain.AccountExpenseTransaction;
import spica.scm.domain.Consignment;
import spica.scm.domain.AccountGroup;
import spica.fin.domain.AccountingDocType;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingTransaction;
import spica.fin.domain.AccountingTransactionDetail;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.fin.service.delete.AccountExpenseDetailService;
import spica.fin.service.AccountExpenseTransactionService;
import spica.fin.service.LedgerExternalDataService;
import spica.scm.service.AccountGroupService;
import spica.fin.service.AccountingLedgerService;
import spica.fin.service.AccountingTransactionService;
import spica.sys.FileConstant;
import spica.sys.UserRuntimeView;
import wawo.app.config.AppConfig;
import wawo.app.faces.Jsf;
import spica.constant.AccountingConstant;
import spica.fin.domain.TaxCode;
import spica.scm.service.AccountService;
import spica.scm.tax.TaxCalculator;
import spica.scm.view.ScmLookupExtView;
import spica.scm.view.ScmLookupView;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeService;
import wawo.app.common.AppService;

/**
 * AccountExpenseView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Nov 27 13:55:44 IST 2017
 */
@Named(value = "accountExpenseView")
@ViewScoped
public class AccountExpenseView implements Serializable {

  private transient AccountExpense accountExpense;	//Domain object/selected Domain.
  private transient LazyDataModel<AccountExpense> accountExpenseLazyModel; 	//For lazy loading datatable.
  private transient AccountExpense[] accountExpenseSelected;	 //Selected Domain Array
  private transient Integer selectedExpense = 0;
  private transient Double reimbursementExpenseTotal;
  private transient Double nonReimbursementExpenseTotal;
  private transient AccountingTransactionDetailItem accountingTransactionDetailItem;
  private transient String expenseFrom;
  private transient TaxCalculator taxCalculator;
  private transient boolean intrastate;

  private transient List<AccountGroup> groupList;
  private transient List<Account> accountList;

  private transient String nar;
  private transient Double nonReimbursementIgst;
  private transient Double nonReimbursementCgst;
  private transient Double nonReimbursementSgst;
  private transient Double nonReimbursementAmount;
  private transient Double reimbursementIgst;
  private transient Double reimbursementCgst;
  private transient Double reimbursementSgst;
  private transient Double reimbursementAmount;

  /**
   */
  public AccountExpenseView() {
    super();
  }

  @PostConstruct
  public void init() {
    accountingTransactionDetailItem = (AccountingTransactionDetailItem) Jsf.popupParentValue(AccountingTransactionDetailItem.class);
    if (accountingTransactionDetailItem != null) {
      getAccountExpense().setId(accountingTransactionDetailItem.getAccountingTransactionDetailId().getAccountingTransactionId().getEntityId());
    }

    MainView main = Jsf.getMain();
    try {
      Consignment consignment = (Consignment) Jsf.popupParentValue(Consignment.class);

      if (null != consignment) {
        //   AccountExpense accountExpenseConsignment = null;
        if (consignment.getId() != null) {
          setAccountExpense(AccountExpenseService.selectByConsignment(main, consignment));
        }
        if (getAccountExpense().getId() == null) {
          getAccountExpense().setId(null);

          getAccountExpense().setEntryDate(new Date());
        } //else {     
        if (consignment.getAccountGroupId() != null) {
          setExpenseFrom("2");
          getAccountExpense().setAccountGroupId(consignment.getAccountGroupId());
          getAccountExpense().setConsignmentSalesId(consignment);
          getAccountExpense().setExpenseType(AccountingConstant.EXPENSE_TYPE_SALES);
        } else {
          setExpenseFrom("1");
          getAccountExpense().setAccountId(consignment.getAccountId());
          getAccountExpense().setConsignmentPurchaseId(consignment);
          getAccountExpense().setExpenseType(AccountingConstant.EXPENSE_TYPE_PURCHASE);
        }
        // getAccountExpense().setId(accountExpenseConsignment.getId());
        // }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   * Return AccountExpense.
   *
   * @return AccountExpense.
   */
  public AccountExpense getAccountExpense() {
    if (accountExpense == null) {
      accountExpense = new AccountExpense();
    }
    if (accountExpense.getCompanyId() == null) {
      accountExpense.setCompanyId(UserRuntimeView.instance().getCompany());
    }
    return accountExpense;
  }

  /**
   * Set AccountExpense.
   *
   * @param accountExpense.
   */
  public void setAccountExpense(AccountExpense accountExpense) {
    this.accountExpense = accountExpense;
  }

  public String getExpenseFrom() {
    return expenseFrom;
  }

  public void setExpenseFrom(String expenseFrom) {
    this.expenseFrom = expenseFrom;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAccountExpense(MainView main, String viewType, String expenseFrom) {
    if (!StringUtil.isEmpty(viewType)) {
      setGroupList(null);
      try {
        main.setViewType(viewType);
        if (!StringUtil.isEmpty(expenseFrom)) {
          setExpenseFrom(expenseFrom);
          if (!"0".equals(expenseFrom)) {
            getAccountExpense().setExpenseType(Integer.valueOf(expenseFrom));
          }
        }
        if (main.isNew() && !main.hasError()) {
          getAccountExpense().reset();
//          if (!"0".equals(expenseFrom)) {
//            getAccountExpense().setExpenseType(Integer.valueOf(expenseFrom));
//          }
          getAccountExpense().setEntryDate(new Date());
          if ("1".equals(getExpenseFrom())) {
            getAccountExpense().setAccountId(UserRuntimeView.instance().getAccount());
          } else if ("2".equals(getExpenseFrom())) {
            getAccountExpense().setAccountGroupId(UserRuntimeView.instance().getAccountGroup());
          }
          setReimbursementExpenseTotal(0.0);
          setNonReimbursementExpenseTotal(0.0);
        } else if (main.isEdit() && !main.hasError()) {
          if (getAccountExpense().getId() != null) {
            setAccountExpense((AccountExpense) AccountExpenseService.selectByPk(main, getAccountExpense()));
            reimbursementExpenseTotal = 0.0;
            nonReimbursementExpenseTotal = 0.0;
            reimbursementAmount = 0.0;
            reimbursementCgst = 0.0;
            reimbursementIgst = 0.0;
            reimbursementSgst = 0.0;
            nonReimbursementAmount = 0.0;
            nonReimbursementCgst = 0.0;
            nonReimbursementIgst = 0.0;
            nonReimbursementSgst = 0.0;

            if (!getAccountExpense().getAccountExpenseIdAccountExpenseDetail().isEmpty()) {
              for (AccountExpenseDetail detail : getAccountExpense().getAccountExpenseIdAccountExpenseDetail()) {
                if (detail.isIsReimbursable()) {
                  accountExpense.addAccountExpenseDetailReimpursableList(detail);
                  reimbursementExpenseTotal += detail.getPaymentValue();
                  reimbursementAmount += detail.getTotalAmount();
                  if (!isIntrastate()) {
                    if (detail.getIgstAmount() != null) {
                      reimbursementIgst += detail.getIgstAmount();
                    }
                  } else {
                    if (detail.getCgstAmount() != null) {
                      reimbursementCgst += detail.getCgstAmount();
                    }
                    if (detail.getSgstAmount() != null) {
                      reimbursementSgst += detail.getSgstAmount();
                    }
                  }
                } else {
                  accountExpense.addAccountExpenseDetailList(detail);
                  nonReimbursementExpenseTotal += detail.getPaymentValue();
                  nonReimbursementAmount += detail.getTotalAmount();
                  if (!isIntrastate()) {
                    if (detail.getIgstAmount() != null) {
                      nonReimbursementIgst += detail.getIgstAmount();
                    }
                  } else {
                    if (detail.getCgstAmount() != null) {
                      nonReimbursementCgst += detail.getCgstAmount();
                    }
                    if (detail.getSgstAmount() != null) {
                      nonReimbursementSgst += detail.getSgstAmount();
                    }
                  }
                }
              }
            }
          }

        } else if (main.isList()) {
          getAccountExpense().setCompanyId(null);
          main.getPageData().setSearchKeyWord(null);
          if ("0".equals(expenseFrom)) {
            getAccountExpense().setExpenseType(AccountingConstant.EXPENSE_TYPE_PURCHASE);
          }
          //    setSelectedExpense(null); //FIXME why
          loadAccountExpenseList(main);
        }
        createAccountAndAccountGroupList(main);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create accountExpenseLazyModel.
   *
   * @param main
   */
  private void loadAccountExpenseList(final MainView main) {
    if (accountExpenseLazyModel == null) {
      accountExpenseLazyModel = new LazyDataModel<AccountExpense>() {
        private List<AccountExpense> list;

        @Override
        public List<AccountExpense> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if ("0".equals(getExpenseFrom())) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = AccountExpenseService.listPaged(main, getAccountExpense().getCompanyId(), selectedExpense);
              main.commit(accountExpenseLazyModel, first, pageSize);
            } else if ("1".equals(getExpenseFrom()) && UserRuntimeView.instance().getAccount() != null) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = AccountExpenseService.listPagedByAccount(main, getAccountExpense().getCompanyId(), UserRuntimeView.instance().getAccount());
              main.commit(accountExpenseLazyModel, first, pageSize);
            } else if ("2".equals(getExpenseFrom()) && UserRuntimeView.instance().getAccountGroup() != null) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = AccountExpenseService.listPagedByAccountGroup(main, getAccountExpense().getCompanyId(), UserRuntimeView.instance().getAccountGroup());
              main.commit(accountExpenseLazyModel, first, pageSize);
            }

          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AccountExpense accountExpense) {
          return accountExpense.getId();
        }

        @Override
        public AccountExpense getRowData(String rowKey) {
          if (list != null) {
            for (AccountExpense obj : list) {
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
    String SUB_FOLDER = "scm_account_expense/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveAccountExpense(MainView main) {
    return saveOrCloneAccountExpense(main, "save", AccountingConstant.DATA_DRAFT, false);
  }

  public void saveAccountExpenseClose(MainView main) {
    saveAccountExpense(main);
    dialogClose();
  }

  public String saveAccountExpenseConfirm(MainView main) {
    return saveOrCloneAccountExpense(main, "save", AccountingConstant.DATA_CONFIRMED, false);
  }

  public String saveBackToDraft(MainView main) {
    return saveOrCloneAccountExpense(main, "save", AccountingConstant.DATA_DRAFT, true);
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
//  public String cloneAccountExpense(MainView main) {
//    main.setViewType(ViewTypes.newform);
//    return saveOrCloneAccountExpense(main, "clone", );
//  }
  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccountExpense(MainView main, String key, int type, boolean backToEdit) {
    try {
      boolean sv = true;
      // double reimbursable = 0.0, non_reimbursable = 0.0;
      uploadFiles(); //File upload
      getAccountExpense().setStatus(type);
      nar = null;
      if (backToEdit) {
        main.clear();
        main.param(getAccountExpense().getId());
        AppService.updateSql(main, AccountExpenseDetail.class, "delete from scm_account_expense_transaction where expense_id=?", false);
        for (AccountExpenseDetail detail : getAccountExpense().getAccountExpenseIdAccountExpenseDetail()) {
          main.clear();
          main.param(null);
          main.param(detail.getId());
          AppService.updateSql(main, AccountExpenseDetail.class, "update scm_account_expense_detail set transaction_id=? where id=?", false);
          main.clear();
        }
        List<AccountingTransaction> list = (List<AccountingTransaction>) AppService.list(main, AccountingTransaction.class, "select id from fin_accounting_transaction where accounting_entity_type_id=? and entity_id = ? order by id desc", new Object[]{AccountingConstant.ACC_ENTITY_EXPENSES.getId(), getAccountExpense().getId()});
        for (AccountingTransaction tran : list) {
          AccountingTransactionService.deleteByPk(main, tran);
        }
        main.clear();
        main.param(null);
        main.param(AccountingConstant.DATA_DRAFT);
        AppService.updateSql(main, AccountExpense.class, "update scm_account_expense set status=? where id=?", false);
        main.clear();
        for (AccountExpenseDetail detail : getAccountExpense().getAccountExpenseIdAccountExpenseDetail()) {
          detail.setTransactionId(null);
        }
        getAccountExpense().setStatus(AccountingConstant.DATA_DRAFT);
        AppService.update(main, getAccountExpense());
        main.commit("success." + key);
        return null;
      }
      if (null != key) {
        switch (key) {
          case "save":
            if (type == AccountingConstant.DATA_DRAFT || (reimbursementExpenseTotal > 0 || nonReimbursementExpenseTotal > 0)) {
              if (!getAccountExpense().getAccountExpenseIdAccountExpenseDetail().isEmpty()) {
                getAccountExpense().getAccountExpenseIdAccountExpenseDetail().clear();
              }
              summaryTotal(true);
              List<Account> accountList = new ArrayList<>();
              //getAccountExpense().getAccountId() == null 
              //Rmove this condition if system need to auto split the expence to all accounts in the group            
              if (getAccountExpense().getAccountId() == null && getAccountExpense().getAccountGroupId() != null) {
                accountList = AccountGroupService.selectAccoutByAccountGroup(main, getAccountExpense().getAccountGroupId());
              } else {
                accountList.add(0, getAccountExpense().getAccountId());
              }
//            for (AccountExpenseDetail exp : getAccountExpense().getAccountExpenseIdAccountExpenseDetail()) {
//              if (nar != null) {
//                nar = nar + ", " + exp.getExpenseLedgerId().getTitle();
//              } else {
//                nar = exp.getExpenseLedgerId().getTitle();
//              }
//            }
              // int count = 0;
              if (getAccountExpense().isConfirmed()) {
                for (Account account : accountList) {
                  for (AccountExpenseDetail exp : getAccountExpense().getAccountExpenseIdAccountExpenseDetail()) {
                    if (exp.getExpenseLedgerId() != null && account != null) {
                      if ((exp.getExpenseLedgerId() != null && exp.getBankCashLedgerId() != null) && exp.getVendorLedgerId() == null) { //case 1
                        makePayment(main, exp.getExpenseLedgerId(), exp, null);
                      } else if ((exp.getExpenseLedgerId() != null && exp.getVendorLedgerId() != null) && exp.getBankCashLedgerId() == null) { // case 2
                        makeExpense(main, exp.getVendorLedgerId(), exp);
                      } else if (exp.getExpenseLedgerId() != null && exp.getVendorLedgerId() != null && exp.getBankCashLedgerId() != null) { //case 3
                        AccountingTransactionDetailItem item = makeExpense(main, exp.getVendorLedgerId(), exp);
                        makePayment(main, exp.getVendorLedgerId(), exp, item);
                      }
                    }
                  }
                }
                for (Account account : accountList) {
                  if (reimbursementExpenseTotal > 0) {
                    accountToReimbursable(main, getAccountExpense(), reimbursementExpenseTotal, account);
                  }
                }
              }
              getAccountExpense().setNarration(nar);
              getAccountExpense().setReimbursableExpenseAmount(reimbursementExpenseTotal);
              getAccountExpense().setNonReimbursableExpenseAmount(nonReimbursementExpenseTotal);
//            if (getReimbursementExpenseTotal() > 0 || getNonReimbursementExpenseTotal() > 0) {
              AccountExpenseService.insertOrUpdate(main, getAccountExpense());
            } else {
              sv = false;
            }
//            }
            break;
          case "clone":
            break;
        }
        if (sv) {
          main.commit("success." + key);
        } else {
          main.error("error.line.item.missing");
          getAccountExpense().setStatus(AccountingConstant.DATA_DRAFT);
        }
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  private AccountingTransactionDetailItem makeExpense(MainView main, AccountingLedger ledger, AccountExpenseDetail exp) {

    AccountingTransaction tran = LedgerExternalDataService.getTransactionExpense(main, getAccountExpense().getId(), getAccountExpense().getEntryDate(), getAccountExpense().getCompanyId().getCurrencyId(), getAccountExpense().getCompanyId());
//    String docNo = AccountingPrefixService.getPrefix(main, transation, transation.getCompanyId(), false, count);
//    transation.setDocumentNumber(docNo);

    if (exp.isIsReimbursable()) {
      tran.setNarration("Reimbursable expense");
    } else {
      tran.setNarration("Non Reimbursable expense");
    }
    if (exp.getNote() != null) {
      tran.setNarration(tran.getNarration() + ", " + exp.getNote());
    }
    if (getAccountExpense().isExpenseSales()) {
      tran.setAccountGroupId(UserRuntimeView.instance().getAccountGroup());
    } else if (getAccountExpense().isExpensePurchase()) {
      tran.setAccountGroupId(getAccountExpense().getAccountGroupId());
    }
    AccountingTransactionDetail payableDetail = LedgerExternalDataService.getTransactionDetailNew(tran, ledger, null, exp.getPaymentValue());
    AccountingTransactionDetail taxDetail = LedgerExternalDataService.getTransactionDetailNew(tran, exp.getExpenseLedgerId(), exp.getTaxAmount() != null ? exp.getPaymentValue() : exp.getTotalAmount(), null);
    LedgerExternalDataService.setFirstAsCredit(tran);

    AccountingTransactionDetailItem item = LedgerExternalDataService.getDetailItemExpensePayable(payableDetail, exp.getPaymentValue(), exp.getTotalAmount(), exp.getPaymentValue(), null, exp.getTaxAmount());
    item.setDocumentDate(exp.getPaymentDate());

    //getAccountExpense().getConsignmentPurchaseId() == null ? getAccountExpense().getConsignmentSalesId().getConsignmentNo() : getAccountExpense().getConsignmentPurchaseId().getConsignmentNo()
    item.setDocumentNumber(exp.getPaymentDocumentNo()); //TODO this should be bill no from above
    exp.setIsTax(false);
    if (StringUtil.gtDouble(exp.getIgstAmount(), 0.00) || StringUtil.gtDouble(exp.getSgstAmount(), 0.00) || StringUtil.gtDouble(exp.getCgstAmount(), 0.00)) {
      exp.setIsTax(true);
      LedgerExternalDataService.addTaxExpense(main, tran, exp);
    }
    AccountingTransactionService.insertOrUpdateAll(main, tran);
    exp.setTransactionId(tran);
    AccountExpenseDetailService.insertOrUpdate(main, exp);
    if (nar != null) {
      nar = nar + tran.getDocumentNumber() + "" + tran.getNarration();
    } else {
      nar = tran.getDocumentNumber() + "" + tran.getNarration();
    }
    return item;
  }

  private void makePayment(MainView main, AccountingLedger ledger, AccountExpenseDetail exp, AccountingTransactionDetailItem payable) {

    if (exp != null && getAccountExpense() != null && getAccountExpense().getCompanyId() != null) {
      AccountingTransaction transation = LedgerExternalDataService.getTransactionExpensePayment(main, getAccountExpense().getId(), getAccountExpense().getEntryDate(), getAccountExpense().getCompanyId().getCurrencyId(), getAccountExpense().getCompanyId());
      //  transation.setDocumentNumber(getAccountExpense().getConsignmentPurchaseId() == null ? getAccountExpense().getConsignmentSalesId().getConsignmentNo() : getAccountExpense().getConsignmentPurchaseId().getConsignmentNo());

      if (exp.isIsReimbursable()) {
        transation.setNarration("Reimbursable expense paid");
      } else {
        transation.setNarration("Non Reimbursable expense paid");
      }
      if (exp.getPaymentDocumentNo() != null) {
        transation.setNarration(transation.getNarration() + ", Doc No[" + exp.getPaymentDocumentNo() + "]");
      }
      if (exp.getNote() != null) {
        transation.setNarration(transation.getNarration() + ", " + exp.getNote());
      }

      if (getAccountExpense().isExpenseSales()) {
        transation.setAccountGroupId(UserRuntimeView.instance().getAccountGroup());
      } else if (getAccountExpense().isExpensePurchase()) {
        transation.setAccountGroupId(getAccountExpense().getAccountGroupId());
      }

      exp.setTransactionId(transation);
      AccountingTransactionDetail detail1 = LedgerExternalDataService.getTransactionDetailNew(transation, ledger, exp.getPaymentValue(), null);
      LedgerExternalDataService.getTransactionDetailNew(transation, exp.getBankCashLedgerId(), null, exp.getPaymentValue());
      LedgerExternalDataService.setFirstAsDebit(transation);
      AccountingTransactionDetailItem item = LedgerExternalDataService.getDetailItemNew(detail1, exp.getPaymentType(), exp.getPaymentValue(), exp.getTaxAmount() == null ? exp.getPaymentValue() : exp.getTotalAmount(), exp.getPaymentValue(), null, exp.getTaxAmount());
      //  AccountingTransactionDetailItem item = LedgerExternalDataService.getDetailItemNew(detail1, exp.getPaymentType(), exp.getPaymentValue(), exp.getPaymentValue(), exp.getPaymentValue(), null, null); //no discount/tax
      item.setDocumentDate(exp.getPaymentDate());
      item.setDocumentNumber(exp.getPaymentDocumentNo());
      if (payable != null) {
        LedgerExternalDataService.settle(detail1, payable, item, exp.getPaymentValue());
        item.setStatus(AccountingConstant.STATUS_PROCESSED);
        item.setBalanceAmount(0.00);
        payable.setStatus(AccountingConstant.STATUS_PROCESSED);
        payable.setBalanceAmount(0.00);
      }
      AccountingTransactionService.insertOrUpdateAll(main, transation);
      AccountExpenseDetailService.insertOrUpdate(main, exp);
      if (nar != null) {
        nar = nar + transation.getDocumentNumber() + "" + transation.getNarration();
      } else {
        nar = transation.getDocumentNumber() + "" + transation.getNarration();
      }
    }
    // accountToReimbursable(main, exp);
  }

  private void accountToReimbursable(MainView main, AccountExpense expense, Double totalAmount, Account account) {
    AccountingLedger reimbRecLedger = AccountingLedgerService.selectLedgerByLedgerCode(main, AccountingConstant.LEDGER_CODE_REMIMB_RECEIVABLE, getAccountExpense().getCompanyId().getId());
    AccountingLedger vendorLedger = AccountingLedgerService.selectLedgerByEntity(main, account.getVendorId().getId(), AccountingConstant.ACC_ENTITY_VENDOR, getAccountExpense().getCompanyId().getId());

    AccountingTransaction transation = LedgerExternalDataService.getTransactionExpenseDebitNote(main, getAccountExpense().getId(), getAccountExpense().getEntryDate(), getAccountExpense().getCompanyId().getCurrencyId(), getAccountExpense().getCompanyId());
    // transation.setDocumentNumber(getAccountExpense().getConsignmentPurchaseId() == null ? getAccountExpense().getConsignmentSalesId().getConsignmentNo() : getAccountExpense().getConsignmentPurchaseId().getConsignmentNo());
    //transation.setNarration("Reimbursable expenses.");
    transation.setNarration(nar);

    AccountingTransactionDetail detail1 = LedgerExternalDataService.getTransactionDetail(main, transation, vendorLedger, totalAmount, null);
    LedgerExternalDataService.getTransactionDetail(main, transation, reimbRecLedger, null, totalAmount);
    LedgerExternalDataService.setFirstAsDebit(transation);

    AccountingTransactionDetailItem item = LedgerExternalDataService.getDetailItemExpenseReceivable(detail1, totalAmount, totalAmount, totalAmount, null, null);
    if (getAccountExpense().getConsignmentPurchaseId() != null && getAccountExpense().getConsignmentSalesId() != null) {
      item.setDocumentNumber(getAccountExpense().getConsignmentPurchaseId() == null ? getAccountExpense().getConsignmentSalesId().getConsignmentNo() : getAccountExpense().getConsignmentPurchaseId().getConsignmentNo());
    }

    AccountExpenseTransaction expenseTransaction = new AccountExpenseTransaction();
    expenseTransaction.setExpenseId(expense);
    expenseTransaction.setTransactionId(transation);
    expenseTransaction.setAccountId(account);
    expenseTransaction.setAmount(totalAmount);
    AccountingTransactionService.insertOrUpdateAll(main, transation);
    AccountExpenseTransactionService.insertOrUpdate(main, expenseTransaction);
    //  main.em().flush();
  }

  /**
   * Delete one or many AccountExpense.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccountExpense(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(accountExpenseSelected)) {
        AccountExpenseService.deleteByPkArray(main, getAccountExpenseSelected()); //many record delete from list
        main.commit("success.delete");
        accountExpenseSelected = null;
      } else {
        AccountExpenseService.deleteByPk(main, getAccountExpense());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (main.isEdit()) {
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

  public void showDetails(AccountingTransaction accountingTransaction) {
    Jsf.popupForm(FileConstant.ACCOUNTING_TRANSACTION, accountingTransaction, accountingTransaction.getId());
  }

  /**
   * Return LazyDataModel of AccountExpense.
   *
   * @return
   */
  public LazyDataModel<AccountExpense> getAccountExpenseLazyModel() {
    return accountExpenseLazyModel;
  }

  /**
   * Return AccountExpense[].
   *
   * @return
   */
  public AccountExpense[] getAccountExpenseSelected() {
    return accountExpenseSelected;
  }

  /**
   * Set AccountExpense[].
   *
   * @param accountExpenseSelected
   */
  public void setAccountExpenseSelected(AccountExpense[] accountExpenseSelected) {
    this.accountExpenseSelected = accountExpenseSelected;
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
   * Account autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
//  public List<Account> accountAuto(String filter) {
//    return ScmLookupView.accountAuto(filter);
//  }
//  public List<Account> accountByGroup(String filter) {
//    return ScmLookupExtView.lookupAccountByAccountGroup(getAccountExpense().getAccountGroupId());
//  }
  /**
   * Consignment autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.consignmentAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.consignmentAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Consignment> consignmentAuto(String filter) {
    if (getAccountExpense().getAccountId() == null) {
      return null;
    }
    return ScmLookupExtView.consignmentExpensePurchaseAuto(getAccountExpense().getAccountId().getId(), filter);
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<Consignment> consignmentSalesAuto(String filter) {
    if (getAccountExpense().getAccountGroupId() == null) {
      return null;
    }
    return ScmLookupExtView.consignmentExpenseSalesAuto(getAccountExpense().getAccountGroupId().getId(), filter);
  }

  private void selectAccountList() {
    accountList = new ArrayList<>();
    if (getAccountExpense().isExpenseSales() && UserRuntimeView.instance().getAccountGroup() != null) {
      accountList = ScmLookupExtView.lookupAccountByAccountGroup(UserRuntimeView.instance().getAccountGroup());
      if (accountList.size() == 1) {
        getAccountExpense().setAccountId(accountList.get(0));
      }
      setAccountList(accountList);
    }
  }

  private void selectAccountGroupList(MainView main) {
    groupList = new ArrayList<>();
    Account acc = new Account();
    if (!getExpenseFrom().equals(String.valueOf(0))) {
      acc = UserRuntimeView.instance().getAccount();
    } else if (getExpenseFrom().equals(String.valueOf(0))) {
      acc = getAccountExpense().getAccountId();
    }
    if (acc != null) {
      groupList = UserRuntimeService.accountGroupByAccount(main, acc);
      if (groupList.size() == 1) {
        getAccountExpense().setAccountGroupId(groupList.get(0));
      }
    }
    setGroupList(groupList);
  }

  public List<AccountGroup> getGroupList(MainView main) {
    if (groupList == null) {
      try {
        selectAccountGroupList(main);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return groupList;
  }

  public void setGroupList(List<AccountGroup> groupList) {
    this.groupList = groupList;
  }

  public List<Account> getAccountList() {
    return accountList;
  }

  public void setAccountList(List<Account> accountList) {
    this.accountList = accountList;
  }

  /**
   *
   * @return
   */
  public List<AccountExpenseDetail> getAccountExpenseList() {
    if (StringUtil.isEmpty(accountExpense.getAccountExpenseDetailList())) {
      addNewExpense();
    }
    return accountExpense.getAccountExpenseDetailList();
  }

  /**
   *
   * @return
   */
  public List<AccountExpenseDetail> getAccountReimbursableExpenseList() {
    if (StringUtil.isEmpty(accountExpense.getAccountExpenseDetailReimpursableList())) {
      addNewReimbursableExpense();
    }
    return accountExpense.getAccountExpenseDetailReimpursableList();
  }

  public void addNewExpense() {
    if (StringUtil.isEmpty(getAccountExpense().getAccountExpenseDetailList()) || getAccountExpense().getAccountExpenseDetailList().size() > 0 && getAccountExpense().getAccountExpenseDetailList().get(0).getExpenseLedgerId() != null) {

      AccountExpenseDetail accountExpenseDetail = new AccountExpenseDetail();
      accountExpense.addAccountExpenseDetailList(accountExpenseDetail);
      // Jsf.update("accountExpenseDetailTable");
      // Jsf.execute("focusExpense()");//calling script for focus
    }//else{
    //   Integer row = Jsf.getParameterInt("rownumber");
    // }
  }

  public void addNewReimbursableExpense() {
    if (StringUtil.isEmpty(getAccountExpense().getAccountExpenseDetailReimpursableList()) || getAccountExpense().getAccountExpenseDetailReimpursableList().size() > 0 && getAccountExpense().getAccountExpenseDetailReimpursableList().get(0).getExpenseLedgerId() != null) {
      AccountExpenseDetail accountExpenseDetail = new AccountExpenseDetail();
      accountExpenseDetail.setIsReimbursable(true);
      accountExpense.addAccountExpenseDetailReimpursableList(accountExpenseDetail);
      // Jsf.update("accountReimbursableTable");
      // Jsf.execute("focusReimbursable()"); //calling script for focus
    }//else{
    //  Integer row = Jsf.getParameterInt("rownumber");
    // }
  }

//  private boolean hasTax(AccountExpenseDetail accountExpenseDetail) {
//    return (null != accountExpenseDetail.getExpenseLedgerId() && null != accountExpenseDetail.getExpenseLedgerId().getIgstId());
//  }
  public void calculateTax(AccountExpenseDetail accountExpenseDetail) {
//    if (hasTax(accountExpenseDetail)) {
//      accountExpenseDetail.setIsTax(true);
//    } else {
//      accountExpenseDetail.setIsTax(false);
//    }
    updateTaxAmount(accountExpenseDetail);
  }

  public void updateTaxAmount(AccountExpenseDetail accountExpenseDetail) {
    double taxAmount = 0.0;
    if (accountExpenseDetail.getTotalAmount() == null) {
      accountExpenseDetail.setTaxAmount(null);
      accountExpenseDetail.setPaymentValue(accountExpenseDetail.getTotalAmount());
      accountExpenseDetail.setSgstAmount(null);
      accountExpenseDetail.setCgstAmount(null);
      accountExpenseDetail.setIgstAmount(null);
    } else {
      if (accountExpenseDetail.getTaxCode() != null) {
        taxAmount = accountExpenseDetail.getTotalAmount() * (accountExpenseDetail.getTaxCode().getRatePercentage() / 100);
      } else {
        taxAmount = 0;
      }
      accountExpenseDetail.setPaymentValue(accountExpenseDetail.getTotalAmount() + taxAmount);
      if (isIntrastate()) {
        accountExpenseDetail.setSgstAmount(taxAmount / 2);
        accountExpenseDetail.setCgstAmount(taxAmount / 2);
      } else {
        accountExpenseDetail.setIgstAmount(taxAmount);
      }

    }
    summaryTotal(true);
  }

  private void summaryTotal(boolean b) {
    nonReimbursementExpenseTotal = getTotal(getAccountExpense().getAccountExpenseDetailList(), b);
    reimbursementExpenseTotal = getTotal(getAccountExpense().getAccountExpenseDetailReimpursableList(), b);
    setTotalAmount(getAccountExpense().getAccountExpenseDetailList(), getAccountExpense().getAccountExpenseDetailReimpursableList());
  }

  private Double getTotal(List<AccountExpenseDetail> expenseDetail, boolean addToList) {
    Double sum = 0.00;
    for (AccountExpenseDetail detail : expenseDetail) {
      if (null != detail.getExpenseLedgerId() && (null != detail.getVendorLedgerId() || null != detail.getBankCashLedgerId()) && null != detail.getPaymentValue()) {
        if (addToList) {
          getAccountExpense().getAccountExpenseIdAccountExpenseDetail().add(detail);
        }
      }
      if (null != detail.getPaymentValue()) {
        sum += detail.getPaymentValue();
      }
    }
    return sum;
  }

  public void actionDeleteExpense(MainView main, AccountExpenseDetail accountExpenseDetail) {
    try {
      accountExpense.removeAccountExpenseDetailList(accountExpenseDetail);
      if (accountExpenseDetail.getId() != null) {
        AccountExpenseDetailService.deleteByPk(main, accountExpenseDetail);
        main.commit("success.delete");
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public void actionDeleteReimbursementExpense(MainView main, AccountExpenseDetail accountExpenseDetail) {
    try {
      accountExpense.removeAccountExpenseDetailReimpursableList(accountExpenseDetail);
      if (accountExpenseDetail.getId() != null) {
        AccountExpenseDetailService.deleteByPk(main, accountExpenseDetail);
        main.commit("success.delete");
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  /**
   *
   */
  public void dialogClose() {
    Jsf.closePopup(accountingTransactionDetailItem);
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<AccountingLedger> accountingLedgerPartyExpenseAuto(String filter) {
    return FinLookupView.ledgerDebtorsAndCreditorsAuto(filter, getAccountExpense().getCompanyId().getId());
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<AccountingLedger> accountingLedgerExpenseAuto(String filter) {
    return FinLookupView.ledgerExpenseDirectIndirectAndFixedAssetAuto(filter, getAccountExpense().getCompanyId().getId());
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<AccountingLedger> accountingLedgerExpenseBankAuto(String filter) {
    return FinLookupView.ledgerPaymentAuto(filter, getAccountExpense().getCompanyId().getId());
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<AccountingDocType> accountingDocTypeAuto(String filter) {
    return ScmLookupView.accountingDocTypeAuto(filter);
  }

  public List<AccountingDocType> accountingDocType() {
    return FinLookupView.accountingDocTypeByVoucher(AccountingConstant.VOUCHER_TYPE_PAYMENT.getId());
  }

  public void filterByExpenseType(SelectEvent event) {
    setSelectedExpense((Integer) event.getObject());
  }

  public Integer getSelectedExpense() {
    return selectedExpense;
  }

  public void setSelectedExpense(Integer selectedExpense) {
    this.selectedExpense = selectedExpense;
  }

  public Double getReimbursementExpenseTotal() {
    return reimbursementExpenseTotal;
  }

  public void setReimbursementExpenseTotal(Double reimbursementExpenseTotal) {
    this.reimbursementExpenseTotal = reimbursementExpenseTotal;
  }

  public Double getNonReimbursementExpenseTotal() {
    return nonReimbursementExpenseTotal;
  }

  public void setNonReimbursementExpenseTotal(Double nonReimbursementExpenseTotal) {
    this.nonReimbursementExpenseTotal = nonReimbursementExpenseTotal;
  }

  public void validateReimbursable(FacesContext context, UIComponent component, Object value) throws ValidatorException {

    Integer status = (Integer) Jsf.getAttribute("rowIndex");
    AccountExpenseDetail detail = getAccountExpense().getAccountExpenseDetailReimpursableList().get(status);
    if ((null == value && null == detail.getBankCashLedgerId()) && detail.getExpenseLedgerId() != null) {
      Jsf.error(component, "choose.party.or.payment.ledger", null);
    }
  }

  public void validateExpense(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    Integer status = (Integer) Jsf.getAttribute("rowIndex");
    AccountExpenseDetail detail = getAccountExpense().getAccountExpenseDetailList().get(status);
    if ((null == value && null == detail.getBankCashLedgerId()) && detail.getExpenseLedgerId() != null) {
      Jsf.error(component, "choose.party.or.payment.ledger", null);
    }
  }

  public List<TaxCode> accountExpenseTaxCode() {
    return ScmLookupExtView.lookupGstTaxCode(getAccountExpense().getCompanyId());
  }

  public TaxCalculator getTaxCalculator() {
    return taxCalculator;
  }

  public boolean isIntrastate() {
    return intrastate;
  }

  public void setIntrastate(boolean intrastate) {
    this.intrastate = intrastate;
  }

  private void createAccountAndAccountGroupList(MainView main) {
    selectAccountList();
    selectAccountGroupList(main);
    updateTaxType();
  }

  public void updateTaxType() {
    setGroupList(null);
    if (getAccountExpense().getAccountId() != null) {
      if (getAccountExpense().getAccountId().getVendorAddressId().getStateId().getId().intValue() == getAccountExpense().getCompanyId().getStateId().getId()) {
        setIntrastate(true);
      } else {
        setIntrastate(false);
      }
    }
  }

  public Double getNonReimbursementIgst() {
    return nonReimbursementIgst;
  }

  public Double getNonReimbursementCgst() {
    return nonReimbursementCgst;
  }

  public Double getNonReimbursementSgst() {
    return nonReimbursementSgst;
  }

  public Double getNonReimbursementAmount() {
    return nonReimbursementAmount;
  }

  public Double getReimbursementIgst() {
    return reimbursementIgst;
  }

  public Double getReimbursementCgst() {
    return reimbursementCgst;
  }

  public Double getReimbursementSgst() {
    return reimbursementSgst;
  }

  public Double getReimbursementAmount() {
    return reimbursementAmount;
  }

  public void setTotalAmount(List<AccountExpenseDetail> nonReImbExpList, List<AccountExpenseDetail> reImpExpList) {
    reimbursementAmount = 0.0;
    reimbursementCgst = 0.0;
    reimbursementIgst = 0.0;
    reimbursementSgst = 0.0;
    nonReimbursementAmount = 0.0;
    nonReimbursementCgst = 0.0;
    nonReimbursementIgst = 0.0;
    nonReimbursementSgst = 0.0;
    for (AccountExpenseDetail exp : nonReImbExpList) {
      nonReimbursementAmount += exp.getTotalAmount() == null ? 0.0 : exp.getTotalAmount();
      nonReimbursementCgst += exp.getCgstAmount() == null ? 0.0 : exp.getCgstAmount();
      nonReimbursementSgst += exp.getSgstAmount() == null ? 0.0 : exp.getSgstAmount();
      nonReimbursementIgst += exp.getIgstAmount() == null ? 0.0 : exp.getIgstAmount();
    }
    for (AccountExpenseDetail exp : reImpExpList) {
      reimbursementAmount += exp.getTotalAmount() == null ? 0.0 : exp.getTotalAmount();
      reimbursementCgst += exp.getCgstAmount() == null ? 0.0 : exp.getCgstAmount();
      reimbursementSgst += exp.getSgstAmount() == null ? 0.0 : exp.getSgstAmount();
      reimbursementIgst += exp.getIgstAmount() == null ? 0.0 : exp.getIgstAmount();
    }
  }

  public List<AccountGroup> getGroupList() {
    return groupList;
  }

}
