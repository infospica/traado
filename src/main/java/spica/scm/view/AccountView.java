/*
 * @(#)AccountView.java	1.0 Thu Apr 07 11:31:23 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 * Account act as a business contract between a Company and Vendor.
 * A Company have only one active contract with a vendor to do their business.
 * A contract can have multiple versions. A new version will be created if an ammendment/renew applied to an existing contract.
 *
 */
package spica.scm.view;

import spica.scm.util.AccountContractUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.SelectItem;
import spica.scm.common.SelectItemInteger;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupDocPrefix;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.AccountStatus;
import spica.fin.domain.AccountingLedger;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyBank;
import spica.scm.domain.Contract;
import spica.scm.domain.ContractCommiByRange;
import spica.scm.domain.ContractStatus;
import spica.scm.domain.Period;
import spica.scm.domain.TradeProfile;
import spica.scm.domain.Vendor;
import spica.scm.domain.VendorAddress;
import spica.scm.domain.VendorBank;
import spica.fin.service.AccountGroupDetailService;
import spica.fin.service.AccountingLedgerService;
import spica.fin.view.FinLookupView;
import spica.scm.service.AccountGroupBrandsService;
import spica.scm.service.AccountGroupDocPrefixService;
import spica.scm.service.AccountGroupPriceListService;
import spica.scm.service.AccountGroupService;
import spica.scm.service.AccountService;
import spica.scm.service.AccountStatusService;
import spica.scm.service.ContractCommiByRangeService;
import spica.scm.service.ContractService;
import spica.scm.service.VendorService;
import spica.scm.util.CountryTaxRegimeUtil;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.JsfIo;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * AccountView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:23 IST 2016
 */
@Named(value = "accountView")
@ViewScoped
public class AccountView implements Serializable {

  private transient Account account;	//Domain object/selected Domain.
  private transient LazyDataModel<Account> accountLazyModel; 	//For lazy loading datatable.
  private transient Account[] accountSelected;	 //Selected Domain Array
  private transient Part documentCopyPathPart;
  private transient List<ContractCommiByRange> contractCommiByRangeList;
  private transient List<AccountGroupPriceList> accountGroupPriceList;
  private transient List<TradeProfile> lookupTradeProfileList;
  private transient List<AccountStatus> lookupAccountStatus;
  private transient List<ContractStatus> lookupContractStatus;
  private transient List<SelectItem> listPointOfPurchase;
  private transient ContractCommiByRange selectedCommissionRange;
  private double totalMargin;
  private transient boolean editMode;
  private transient AccountGroup defaultAccountGroup;
  private transient List<AccountGroupDocPrefix> accountGroupDocPrefixList;
  private transient List<AccountGroupDocPrefix> accountGroupDocPurchasePrefixList;
  private transient List<AccountGroupDocPrefix> accountGroupDocDebitCreditList;
  private boolean secondaryVendor = false;
  private boolean pointOfPurchaseApplicable = false;
  private boolean testBool = true;
  private String tempCode;
//  private Company company;
  private boolean accountEditable = false;
  private transient int currentRow;

  @PostConstruct
  public void init() {
    Integer accountId = (Integer) Jsf.popupParentValue(Integer.class);
    if (accountId != null) {
      getAccount().setId(accountId);
    } else {
      Account acc = (Account) Jsf.popupParentValue(Account.class);
      if (acc != null) {
        getAccount().setId(acc.getId());
        setActiveIndex(1);
      }
    }
  }
  /**
   * TabView tab index
   */
  private int activeIndex;

  /**
   * Company & Vendor - Contract relation object.
   */
  private Contract contract = null;

  /**
   * Default Constructor.
   */
  public AccountView() {
    super();
  }

  public boolean isTestBool() {
    return testBool;
  }

  public void setTestBool(boolean testBool) {
    this.testBool = testBool;
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }

  public double getTotalMargin() {
    return totalMargin;
  }

  public void setTotalMargin(double totalMargin) {
    this.totalMargin = totalMargin;
  }

  public ContractCommiByRange getSelectedCommissionRange() {
    return selectedCommissionRange;
  }

  public void setSelectedCommissionRange(ContractCommiByRange selectedCommissionRange) {
    this.selectedCommissionRange = selectedCommissionRange;
  }

  public AccountGroup getDefaultAccountGroup() {
    return defaultAccountGroup;
  }

  public void setDefaultAccountGroup(AccountGroup defaultAccountGroup) {
    this.defaultAccountGroup = defaultAccountGroup;
  }

  public List<AccountGroupDocPrefix> getAccountGroupDocPurchasePrefixList() {
    return accountGroupDocPurchasePrefixList;
  }

  public void setAccountGroupDocPurchasePrefixList(List<AccountGroupDocPrefix> accountGroupDocPurchasePrefixList) {
    this.accountGroupDocPurchasePrefixList = accountGroupDocPurchasePrefixList;
  }

  public List<AccountGroupDocPrefix> getAccountGroupDocDebitCreditList() {
    return accountGroupDocDebitCreditList;
  }

  public void setAccountGroupDocDebitCreditList(List<AccountGroupDocPrefix> accountGroupDocDebitCreditList) {
    this.accountGroupDocDebitCreditList = accountGroupDocDebitCreditList;
  }

  public boolean isSecondaryVendor() {
    return secondaryVendor;
  }

  public void setSecondaryVendor(boolean secondaryVendor) {
    this.secondaryVendor = secondaryVendor;
  }

  public List<AccountGroupDocPrefix> getAccountGroupDocPurchasePrefixList(MainView main) {
    try {
      if (getAccountGroupDocPurchasePrefixList() == null) {
        if (main.isEdit()) {
          if (account.getCompanyId().getDocPrefixBasedOnCompany() == 1) {
            setAccountGroupDocPurchasePrefixList(AccountGroupDocPrefixService.selectDocPurchasePrefixesBasedOnCompany(main, account.getCompanyId(), account.getFinancialYear()));
          } else {
            setAccountGroupDocPurchasePrefixList(AccountGroupDocPrefixService.selectAccountGroupDocPurchasePrefixes(main, getAccount(), getDefaultAccountGroup()));
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return accountGroupDocPurchasePrefixList;
  }

  public List<AccountGroupDocPrefix> getAccountGroupDocDebitCreditList(MainView main) {
    try {
      if (getAccountGroupDocDebitCreditList() == null) {
        if (main.isEdit()) {
          setAccountGroupDocDebitCreditList(AccountGroupDocPrefixService.selectDebitCreditNoteAccountGroupDocPrefix(main, getAccount(), getDefaultAccountGroup()));
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return accountGroupDocDebitCreditList;
  }

  public boolean isEditMode() {
    return editMode;
  }

  public void setEditMode(boolean editMode) {
    this.editMode = editMode;
  }

  public Contract getContract() {
    if (contract == null) {
      contract = getContractInstance();
      if (getAccount() != null && getAccount().getId() != null) {
        contract.setContractCode(getAccount().getAccountCode().toUpperCase() + "_CON_" + contract.getVersionNo());
      }
    }
    return contract;
  }

  public void setContract(Contract contract) {
    this.contract = contract;
  }

  private Contract getContractInstance() {
    Contract contractObject = new Contract();
    contractObject.setVersionNo("1");
    contractObject.setIsFactoryDirect(2);
    contractObject.setCommissionByRange(2);
    contractObject.setCstClaimable(2);
    contractObject.setLoadingExpenseClaimable(2);
    contractObject.setLoadingExpenseClaimable(2);
    contractObject.setUnloadingExpenseClaimable(2);
    contractObject.setFreightExpenseClaimable(2);
    contractObject.setOtherExpenseClaimable(2);
    contractObject.setCommissionValueType(1);
    return contractObject;
  }

//  public Company getCompany() {
//    return UserRuntimeView.instance().getCompany();
//  }
  public List<TradeProfile> getLookupTradeProfileList() {
    if (lookupTradeProfileList == null) {
      lookupTradeProfileList = ScmLookupExtView.tradeProfileSecondAndThirdLevel();
    }
    return lookupTradeProfileList;
  }

  public void setLookupTradeProfileList(List<TradeProfile> lookupTradeProfileList) {
    this.lookupTradeProfileList = lookupTradeProfileList;
  }

  public List<AccountStatus> getLookupAccountStatus(MainView main) {
    try {
      lookupAccountStatus = ScmLookupExtView.accountStatus(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return lookupAccountStatus;
  }

  public void setLookupAccountStatus(List<AccountStatus> lookupAccountStatus) {
    this.lookupAccountStatus = lookupAccountStatus;
  }

  public List<ContractStatus> getLookupContractStatus() {
    if (isContractCreated()) {
      lookupContractStatus = ScmLookupExtView.contractStatus(getContract().getContractStatusId().getId());
    } else {
      lookupContractStatus = ScmLookupExtView.contractStatus(AccountContractUtil.CONTRACT_STATUS_DRAFT);
    }
    return lookupContractStatus;
  }

  public void setLookupContractStatus(List<ContractStatus> lookupContractStatus) {
    this.lookupContractStatus = lookupContractStatus;
  }

  public List<AccountGroupDocPrefix> getAccountGroupDocPrefixList() {
    return accountGroupDocPrefixList;
  }

  public void setAccountGroupDocPrefixList(List<AccountGroupDocPrefix> accountGroupDocPrefixList) {
    this.accountGroupDocPrefixList = accountGroupDocPrefixList;
  }

  public List<AccountGroupDocPrefix> getAccountGroupDocPrefix(MainView main) {
    try {
      if (getAccountGroupDocPrefixList() == null && main.isEdit()) {
        setAccountGroupDocPrefixList(AccountGroupDocPrefixService.selectDefaultAccountGroupDocPrefix(main, getAccount()));
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return getAccountGroupDocPrefixList();
  }

  /**
   * The method will return the list of AccountGroupPriceList.
   *
   * @param main
   * @return
   */
  public List<AccountGroupPriceList> getAccountGroupPriceList(MainView main) {
    if (getAccountGroupPriceList().isEmpty() && main.isEdit()) {
      try {
        setAccountGroupPriceList(AccountGroupPriceListService.selectAccountGroupPriceListByPrimaryAccount(main, getAccount()));
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return getAccountGroupPriceList();
  }

  public List<AccountGroupPriceList> getAccountGroupPriceList() {
    if (accountGroupPriceList == null) {
      accountGroupPriceList = new ArrayList<>();
    }
    return accountGroupPriceList;
  }

  public void setAccountGroupPriceList(List<AccountGroupPriceList> accountGroupPriceList) {
    this.accountGroupPriceList = accountGroupPriceList;
  }

  /**
   * Method handler for Account Group Price List datatable RowEditEvent event.
   *
   * Method will update the title field of account group price list object.
   *
   * @param event
   */
  public void onAccountGroupPriceListRowEdit(RowEditEvent event) {
    AccountGroupPriceList agpl = (AccountGroupPriceList) event.getObject();
    MainView main = Jsf.getMain();
    try {
      if (agpl.getIsDefault() == null) {
        agpl.setIsDefault(0);
      }
      AccountGroupPriceListService.insertOrUpdate(main, agpl);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void setListPointOfPurchase(List<SelectItem> listPointOfPurchase) {
    this.listPointOfPurchase = listPointOfPurchase;
  }

  /**
   *
   * @return
   */
  public boolean isSuperStockiest() {
    if (getAccount() != null && getAccount().getCompanyTradeProfileId() != null) {
      return (getAccount().getCompanyTradeProfileId().getId().equals(AccountUtil.TRADE_TYPE_SS));
    }
    return false;
  }

  public boolean isMarketer() {
    if (getAccount() != null && getAccount().getCompanyTradeProfileId() != null) {
      return (getAccount().getCompanyTradeProfileId().getId().equals(AccountUtil.TRADE_TYPE_MARKETER));
    }
    return false;
  }

  /**
   *
   * @param main
   * @param accountGroup
   * @return
   */
  public String deleteAccountGroup(MainView main, AccountGroup accountGroup) {
    try {
      AccountGroupDetailService.deleteAccountGroupRelation(main, accountGroup, getAccount());
      setAccountGroupPriceList(null);
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Return Part.
   *
   * @return Part.
   */
  public Part getDocumentCopyPathPart() {
    return documentCopyPathPart;
  }

  /**
   * Set Part documentCopyPathPart.
   *
   * @param documentCopyPathPart.
   */
  public void setDocumentCopyPathPart(Part documentCopyPathPart) {
    if (this.documentCopyPathPart == null || documentCopyPathPart != null) {
      this.documentCopyPathPart = documentCopyPathPart;
    }
  }

  public List<ContractCommiByRange> getContractCommiByRangeList() {
    if (contractCommiByRangeList == null) {
      contractCommiByRangeList = new ArrayList<>();
    }
    return contractCommiByRangeList;
  }

  public void setContractCommiByRangeList(List<ContractCommiByRange> contractCommiByRangeList) {
    this.contractCommiByRangeList = contractCommiByRangeList;
  }

  /**
   * Return Account.
   *
   * @return Account.
   */
  public Account getAccount() {
    if (account == null) {
      account = new Account();
    }
    if (account.getCompanyId() == null) {
      account.setCompanyId(UserRuntimeView.instance().getCompany());
    }
    if (account.getFinancialYear() == null) {
      account.setFinancialYear(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
    }

    return account;
  }

  /**
   * Set Account.
   *
   * @param account.
   */
  public void setAccount(Account account) {
    this.account = account;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAccount(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        if (UserRuntimeView.instance().getCompany() == null) {
          main.error("company.required");
          main.setViewType(ViewTypes.list);
          return null;
        }
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          contractCommiByRangeList = null;
          resetAccount();
          setEditMode(false);
          getAccount().setAccountStatusId(new AccountStatus(AccountStatusService.ACCOUNT_STATUS_DRAFT));
          getAccount().setIsSchemeApplicable(SystemConstants.QUANTITY_SCHEME);
          getAccount().setPurchaseChannel(AccountService.GROUP_SUPPLIER);
          getAccount().setSalesChannel(AccountService.GROUP_SUPPLIER);
          getAccount().setRenderPtr(AccountService.RENDER_PTR);
          getAccount().setRenderPts(AccountService.RENDER_PTS);
          getAccount().setPtsDerivationCriteria(AccountService.PTS_DERIVATION_CRITERIA);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setAccountGroupDocPrefixList(null);
          setAccountGroupPriceList(null);
          contractCommiByRangeList = null;
          setEditMode(false);
          setAccount((Account) AccountService.selectByPk(main, getAccount()));
          setDefaultAccountGroup(AccountGroupService.selectDefaultAccountGroupByAccount(main, getAccount()));
          tempCode = getAccount().getAccountCode();
          // Selecting accounts contract object.
          //if (getAccount().getVendorId().getVendorType().equals(VendorService.VENDOR_TYPE_PRIMARY)) {
          setContract(ContractService.selectContractByAccount(main, getAccount().getId()));
          if (getContract() != null && getContract().getId() != null) {
            contractCommiByRangeList = ContractCommiByRangeService.selectContractCommissionRangeByContract(main, getContract().getId());
            //Selecting contract claimables
            setCommissionValueType();
            if (isSuperStockiest()) {
              actionCalculateTotalMargin();
            }
          } else {
            setBenefitType();
          }

          if (getContract().getCommissionValueFixed() != null) {
            getContract().setCommissionValueType(1);
          } else if (getContract().getCommissionValuePercent() != null) {
            getContract().setCommissionValueType(2);
          }
          //}

        } else if (ViewType.list.toString().equals(viewType)) {
          setActiveIndex(0);
          setTotalMargin(0);
          setSecondaryVendor(false);
          setAccountGroupDocPurchasePrefixList(null);
          setAccountGroupPriceList(null);
          setAccountGroupDocDebitCreditList(null);
          resetAccount();
          setContract(null);
          loadAccountList(main);
        }
      } catch (Throwable t) {
        main.rollback(t, "error.list");
      } finally {
        main.close();
      }
    }
    return null;
  }

  private void setCommissionValueType() {
    getContract().setCommissionValueType(1);
    if (getContract().getCommissionValueFixed() != null && getContract().getCommissionValueFixed() >= 0) {
      getContract().setCommissionValueType(1);
    } else if (getContract().getCommissionValueFixed() != null && getContract().getCommissionValueFixed() >= 0) {
      getContract().setCommissionValueType(2);
    } else if (contractCommiByRangeList != null && !contractCommiByRangeList.isEmpty()) {
      if (contractCommiByRangeList.get(0).getValueFixed() != null && contractCommiByRangeList.get(0).getValueFixed() >= 0) {
        getContract().setCommissionValueType(1);
      } else if (contractCommiByRangeList.get(0).getValuePercentage() != null && contractCommiByRangeList.get(0).getValuePercentage() >= 0) {
        getContract().setCommissionValueType(2);
      }
    } else {
      getContract().setCommissionValueType(1);
    }
  }

  /**
   *
   */
  private void resetAccount() {
    getAccount().reset();
    getContract().reset();
    setAccountGroupPriceList(null);
    setActiveIndex(0);
    setLookupAccountStatus(null);
    setLookupContractStatus(null);
    setContractCommiByRangeList(null);
    setLookupTradeProfileList(null);
    setListPointOfPurchase(null);
    setDefaultAccountGroup(null);
    setAccountGroupDocPrefixList(null);
    setAccountGroupDocPurchasePrefixList(null);
    setAccountGroupDocDebitCreditList(null);
    editMode = false;
    secondaryVendor = false;
    pointOfPurchaseApplicable = false;
  }

  /**
   * Create accountLazyModel.
   *
   * @param main
   */
  private void loadAccountList(final MainView main) {
    if (accountLazyModel == null) {
      accountLazyModel = new LazyDataModel<Account>() {
        private List<Account> list;

        @Override
        public List<Account> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            if (getAccount().getCompanyId() != null) {
              list = AccountService.listPaged(main, getAccount().getCompanyId());
              main.commit(accountLazyModel, first, pageSize);
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
        public Object getRowKey(Account account) {
          return account.getId();
        }

        @Override
        public Account getRowData(String rowKey) {
          if (list != null) {
            for (Account obj : list) {
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

  /**
   *
   */
  private void uploadFiles() throws IOException {
    String SUB_FOLDER = "scm_contract/";
    if (documentCopyPathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(documentCopyPathPart, getContract().getDocumentCopyPath(), SUB_FOLDER);
      getContract().setDocumentCopyPath(JsfIo.getDbPath(documentCopyPathPart, SUB_FOLDER));
      documentCopyPathPart = null;	//import to set as null.
    }
  }

  public String actionUpdateAccount(MainView main) {
    try {
      setEditMode(false);
      getAccount().setAccountCode(getAccount().getAccountCode().toUpperCase());
      AccountService.insertOrUpdate(main, getAccount());
      if (getTempCode() != null && !getTempCode().equals(getAccount().getAccountCode())) {
        AccountGroupDocPrefixService.updateDocPrefixByAccount(main, getTempCode(), getAccount(), getAccount().getFinancialYear());
      }
      AccountService.updateAccount(main, account);
      AccountGroupService.updateAccountGroup(main, defaultAccountGroup);
      setAccountGroupDocPrefixList(null);
      setAccountGroupDocPurchasePrefixList(null);
      setAccountGroupPriceList(null);
      setAccountGroupDocDebitCreditList(null);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Insert or update.
   *
   * @param main
   * @param status
   * @return the page to display.
   */
  public String saveAccount(MainView main, int status) {
    try {
      getAccount().setAccountStatusId(new AccountStatus(status));
      if (status == AccountStatusService.ACCOUNT_STATUS_INACTIVE) {
        AccountService.updateAccountStatus(main, getAccount().getId(), AccountStatusService.ACCOUNT_STATUS_INACTIVE, ContractUtil.getContractStatusInactive());
      } else {
        return saveOrCloneAccount(main, "save");
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }

    return null;
  }

  /**
   *
   * @param main
   * @return
   */
  public String actionUpdateAccountStatus(MainView main) {
    try {
      AccountService.updateAccountStatus(main, getAccount(), AccountStatusService.ACCOUNT_STATUS_ACTIVE);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveContract(MainView main) {
    getContract().setAccountId(getAccount());
    if (getContract() != null && getContract().getContractStatusId() == null) {
      getContract().setContractStatusId(AccountContractUtil.getContractStatusIntance(AccountContractUtil.CONTRACT_STATUS_DRAFT));
    }
    return saveOrCloneContract(main, "save");
  }

  /**
   * Save to active state
   *
   * @param main
   * @return the page to display.
   */
  public String confirmContract(MainView main) {
    getContract().setAccountId(getAccount());
    getContract().setContractStatusId(AccountContractUtil.getContractStatusIntance(AccountContractUtil.CONTRACT_STATUS_ACTIVE));
    return saveOrCloneContract(main, "confirm");
  }

  /**
   * Renewing contract
   *
   * @param main
   * @return
   */
  public String renewContract(MainView main) {
    try {
      Integer activeContractId = getContract().getId();
      getContract().setContractStatusId(AccountContractUtil.getContractStatusIntance(AccountContractUtil.CONTRACT_STATUS_RENEW));
      getContract().setVersionNo("" + (Integer.parseInt(getContract().getVersionNo()) + 1));
      getContract().setContractCode(getAccount().getAccountCode().toUpperCase() + "_CON_" + contract.getVersionNo());
      ContractService.clone(main, getContract());
      ContractCommiByRangeService.selectInsertCommissionByRangeByContract(main, activeContractId, getContract());
      main.commit("success.renew");
    } catch (Throwable t) {
      main.rollback(t, "error.renew");
    } finally {
      main.close();
    }
    return null;
  }

  public String suspendContract(MainView main) {
    try {
      ContractService.updateConstractStatus(main, getContract().getId(), ContractUtil.getContractStatusInactive());
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  public String resumeContract(MainView main) {
    try {
      ContractService.updateConstractStatus(main, getContract().getId(), ContractUtil.getContractStatusActive());
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAccount(MainView main) {
    main.setViewType("newform");
    return saveOrCloneAccount(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAccount(MainView main, String key) {
    boolean isNewAccount = true;
    String companyStateCode;
    String vendorStateCode;
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            if (CountryTaxRegimeUtil.isGstRegime(getAccount().getCompanyId())) {
              companyStateCode = getAccount().getCompanyId().getGstNo().substring(0, 2);
              vendorStateCode = getAccount().getGstNo().substring(0, 2);
              if (!StringUtil.isEmpty(getAccount().getCompanyId().getGstNo()) && !StringUtil.isEmpty(getAccount().getGstNo())) {
                if (companyStateCode.equals(vendorStateCode)) {
                  // Intrastate Purchase
                  getAccount().setIsPurchaseInterstate(SystemConstants.INTRASTATE_PURCHASE);
                } else {
                  // Interstate Purchase
                  getAccount().setIsPurchaseInterstate(SystemConstants.INTERSTATE_PURCHASE);
                }
              } else {
                main.error("company.gstin.required");
                main.setViewType(ViewTypes.list);
                return null;
              }
            }

            getAccount().setStatutoryFormType(accountStatutoryForm(getAccount().getCompanyTradeProfileId().getId(), getAccount().getIsPurchaseInterstate()));
            /**
             * commodity_movement_type 1 = Stock Transfer (in case of CSA & C&F) , 2 =Purchase (in case of SS)
             */
            getAccount().setCommodityMovementType((isSuperStockiest() ? AccountContractUtil.COMMODITY_MOVEMENT_TYPE_PURCHASE : AccountContractUtil.COMMODITY_MOVEMENT_TYPE_STOCK_TRANSFER));
            if (getAccount().getId() != null && getAccount().getId() > 0) {
              isNewAccount = false;
            }

            getAccount().setAccountId(null);
            getAccount().setAccountCode(getAccount().getAccountCode().toUpperCase());
            AccountService.insertOrUpdate(main, getAccount());
            String prefix = "";
            String groupName = "";
            if (isNewAccount && getAccount().getVendorId().getVendorType().equals(VendorService.VENDOR_TYPE_PRIMARY)) {
              prefix = getAccount().getAccountCode();
              groupName = getAccount().getVendorId().getVendorName();
//              if (getAccount().getSalesChannel().equals(AccountService.GROUP_SUPPLIER)) {
//                groupName = getAccount().getVendorId().getSupplierGroupId().getTitle();
//              }

              /**
               * creates default account group Account group Sales Invoice prefix. Creates Purchase Requisition, Purchase Order, Sales Invoice Prefix(Account Group Invoice Prefix)
               */
              setDefaultAccountGroup(AccountGroupService.insertDefaultAccountGroupAndDetail(main, getAccount(), groupName, prefix));

              /**
               * Insert default account group doc prefixes
               */
              AccountGroupDocPrefixService.insertDefaultAccountGroupDocPrifix(main, getAccount(), getDefaultAccountGroup());

              /**
               * Creates Account group price list
               */
              AccountGroupPriceList accountGroupPrList = new AccountGroupPriceList();
              accountGroupPrList.setAccountGroupId(getDefaultAccountGroup());
              accountGroupPrList.setIsDefault(1);
              accountGroupPrList.setTitle(getDefaultAccountGroup().getGroupName() + "_PL");
              accountGroupPrList.setVariationPercentage(0.00);
              AccountGroupPriceListService.insert(main, accountGroupPrList);
            } else if (isNewAccount && getAccount().getVendorId().getVendorType().equals(VendorService.VENDOR_TYPE_SECONDARY)) {

              ContractService.clonePrimaryContract(main, getAccount());

              if (AccountService.INDIVIDUAL_SUPPLIER.equals(getAccount().getSalesChannel())) {
                groupName = getAccount().getVendorId().getVendorName();

//                if (account.getSalesChannel().equals(AccountService.GROUP_SUPPLIER)) {
//                  groupName = getAccount().getVendorId().getSupplierGroupId().getTitle();
//                }
                setDefaultAccountGroup(AccountGroupService.insertDefaultAccountGroupAndDetail(main, getAccount(), groupName, getAccount().getAccountCode()));

                /**
                 * Insert default account group doc prefixes
                 */
                if (AccountService.INDIVIDUAL_SUPPLIER.equals(getAccount().getSalesChannel()) && AccountService.INDIVIDUAL_SUPPLIER.equals(getAccount().getPurchaseChannel())) {
                  AccountGroupDocPrefixService.insertDefaultAccountGroupDocPrifix(main, getAccount(), getDefaultAccountGroup());
                } else if (AccountService.INDIVIDUAL_SUPPLIER.equals(getAccount().getSalesChannel())) {
                  AccountGroupDocPrefixService.insertSalesPrefixes(main, getAccount(), getDefaultAccountGroup());
                }

                /**
                 * Creates Account group price list
                 */
                AccountGroupPriceList accountGroupPrList = new AccountGroupPriceList();
                accountGroupPrList.setAccountGroupId(getDefaultAccountGroup());
                accountGroupPrList.setIsDefault(1);
                accountGroupPrList.setTitle(getDefaultAccountGroup().getGroupName() + "_PL");
                accountGroupPrList.setVariationPercentage(0.00);
                AccountGroupPriceListService.insert(main, accountGroupPrList);
              } else {
                // get default account group for secondary vendor account.
                setDefaultAccountGroup(AccountGroupService.selectDefaultAccountGroupByAccount(main, getAccount()));

                AccountGroupBrandsService.insertSecondaryAccountBrands(main, account.getVendorId(), getDefaultAccountGroup());
                // Creates purchase prefixes for secondary vendor account.
                if (AccountService.INDIVIDUAL_SUPPLIER.equals(getAccount().getPurchaseChannel())) {
                  AccountGroupDocPrefixService.insertPurchasePrefixes(main, getAccount(), getDefaultAccountGroup());
                }

                // insert new entry in account group detail, account_id :: defaultAccountGroup              
                AccountGroupDetailService.insertSecondaryVendorsAccountGroup(main, getAccount());
              }

            }
            if (getDefaultAccountGroup() != null) {
              AccountGroupService.updateAccountGroup(main, defaultAccountGroup);
            }
            if (getAccount().getAccountStatusId().getId().equals(AccountStatusService.ACCOUNT_STATUS_ACTIVE)) {
              // update the status of Account group doc prefix and default account group.
              if (getAccount().getVendorId().getVendorType().equals(VendorService.VENDOR_TYPE_PRIMARY)) {
                AccountGroupService.updateDefaultAccountGroupStatus(main, getDefaultAccountGroup());
              }
              AccountGroupDocPrefixService.updateAccountGroupDocPrefixStatus(main, getAccount(), getDefaultAccountGroup(), getAccount().getFinancialYear());
            }
            break;
          case "clone":
            AccountService.clone(main, getAccount());
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

  public String getPrimaryVendorName() {
    if (AccountUtil.isScondaryVendorAccount(getAccount())) {
      return ("(Secondary of " + getAccount().getVendorId().getVendorId().getVendorName() + ")");
    }
    return "";
  }

  /**
   * ss & interstate yes - C Form else no form ss & interstate no - no form c&f or csa & interstate no - no form c&f or csa & interstate yes - F Form else no form
   *
   * @param tradeType
   * @param interstate
   * @return
   */
  private Integer accountStatutoryForm(int tradeType, int interstate) {
    if (isSuperStockiest() && interstate == 1) {
      return AccountContractUtil.C_FORM;
    } else if ((AccountUtil.TRADE_TYPE_CSA == tradeType || AccountUtil.TRADE_TYPE_CF == tradeType) && interstate == 1) {
      return AccountContractUtil.F_FORM;
    }
    return null;
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneContract(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        if (getContract().getCommissionValueType() == 1) {
          getContract().setCommissionValuePercent(null);
        } else if (getContract().getCommissionValueType() == 2) {
          getContract().setCommissionValueFixed(null);
        }
        switch (key) {
          case "save":
            ContractService.insertOrUpdate(main, getContract());
            Date formDate = getContract().getValidFrom();
            Date toDate = getContract().getValidTo();
            int num = 0;
            num = toDate.compareTo(formDate);
            if (num < 0 || num == 0) {
              main.error("error.date");
              return null;
            }
            ContractCommiByRangeService.insertOrUpdateList(main, getContract(), contractCommiByRangeList);
            break;
          case "clone":
            ContractService.clone(main, getContract());
            ContractService.updateContractStatus(main, getAccount().getId(), getContract().getId());
            break;
          case "confirm":
            ContractService.insertOrUpdate(main, getContract());
            ContractService.updateContractStatus(main, getAccount().getId(), getContract().getId());
            ContractCommiByRangeService.insertOrUpdateList(main, getContract(), contractCommiByRangeList);
            key = "save";
            //     UserRuntimeView.instance().setNavAccountList(null); //Dont think this is required
            main.commit();
            //    UserRuntimeView.instance().changeAccount(main, getContract().getAccountId()); //Dont thing this is required
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
   * Delete one or many Account.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAccount(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(accountSelected)) {
        AccountService.deleteByPkArray(main, getAccountSelected()); //many record delete from list
        main.commit("success.delete");
        accountSelected = null;
      } else {
        AccountService.deleteByPk(main, getAccount());  //individual record delete from list or edit form
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

  public void deleteContract(MainView main) {
    try {
      if (getContract() != null && getContract().getId() != null) {
        ContractService.deleteByPk(main, getContract());
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }

  /**
   * Return LazyDataModel of Account.
   *
   * @return
   */
  public LazyDataModel<Account> getAccountLazyModel() {
    return accountLazyModel;
  }

  /**
   * Return Account[].
   *
   * @return
   */
  public Account[] getAccountSelected() {
    return accountSelected;
  }

  /**
   * Set Account[].
   *
   * @param accountSelected
   */
  public void setAccountSelected(Account[] accountSelected) {
    this.accountSelected = accountSelected;
  }

  public boolean rangeToRequired(int rowIndex) {
    if (getContractCommiByRangeList() != null) {
      if (getContractCommiByRangeList().size() == 1) {
        return false;
      } else if ((getContractCommiByRangeList().size() - 1) != rowIndex) {
        return true;
      }
    }
    return false;
  }

  /**
   * Autocomplete ajax event
   *
   * @param event
   */
  public void ajaxEventHandler(SelectEvent event) {
  }

  public void ajaxBehaviorEventHandler() {
  }

  public void ajaxEventCommissionByRange(AjaxBehaviorEvent event) {
    setContractCommiByRangeList(null);
    getContract().setCommissionValueFixed(null);
    getContract().setCommissionValuePercent(null);
  }

  public void accountGroupDocPrefixDialogReturn() {
    setAccountGroupDocPrefixList(null);
  }

  /**
   * Account group dialog returned event handler.
   */
  public void accountGroupDocPrefixReturned() {
    setAccountGroupDocPrefixList(null);
    setAccountGroupPriceList(null);
    setAccountGroupDocPurchasePrefixList(null);
    setAccountGroupDocDebitCreditList(null);
  }

  /**
   * Account prefix dialog returned event handler.
   */
  public void accountGroupPurchaseDocPrefixListReturned() {
    setAccountGroupDocPurchasePrefixList(null);
  }

  public void accountGroupDocDebitCreditListReturned() {
    setAccountGroupDocDebitCreditList(null);
  }

  public void accountGroupDocPrefixEditDialog(Integer accountGroupId) {
    Jsf.popupForm(FileConstant.ACCOUNT_GROUP_DOC_PREFIX, getAccount(), accountGroupId);
  }

  public void accountGroupPurchaseDocPrefixEditDialog(Integer accountGroupDocPrefixId) {
    Jsf.popupForm(FileConstant.ACCOUNT_GROUP_DOC_PREFIX, getAccount(), accountGroupDocPrefixId);
  }

  public void defaultProductPriceListFormDialog(AccountGroupPriceList agpl) {
    Jsf.popupList(FileConstant.PRODUCT_PRICE_LIST_VO, getAccount(), agpl.getId());
  }

  public void accountDebitCreditEditDialog(Integer accountGroupDocPrefixId) {
    Jsf.popupForm(FileConstant.ACCOUNT_GROUP_DOC_PREFIX, getAccount(), accountGroupDocPrefixId);
  }

  /**
   * contract review dialog
   */
  public void contractListDialog() {
    Jsf.popupList(FileConstant.CONRACT_LIST, getContract());
  }

  public void contractListDialogReturn(SelectEvent event) {
    Jsf.closeDialog(getContract());
  }

  public String addNewAccountGroupPriceList() {
    if (accountGroupPriceList != null && (accountGroupPriceList.isEmpty() || accountGroupPriceList.get(0).getId() != null)) {
      AccountGroupPriceList groupPriceList = new AccountGroupPriceList();
      groupPriceList.setAccountGroupId(getDefaultAccountGroup());
      getAccountGroupPriceList().add(0, groupPriceList);
    }
    return null;
  }

  public boolean isContractCreated() {
    return getContract() != null && getContract().getId() != null;
  }

  public boolean isActiveContract() {
    if (isContractCreated()) {
      return getContract().getContractStatusId().getId() == AccountContractUtil.CONTRACT_STATUS_ACTIVE;
    }
    return false;
  }

  public boolean isRenewContract() {
    if (isContractCreated()) {
      return getContract().getContractStatusId().getId() == AccountContractUtil.CONTRACT_STATUS_RENEW;
    }
    return false;
  }

  public boolean isSuspendContract() {
    if (isContractCreated()) {
      return getContract().getContractStatusId().getId() == AccountContractUtil.CONTRACT_STATUS_INACTIVE;
    }
    return false;
  }

  public boolean isActivableContract() {
    if (isContractCreated()) {
      if ((getContract().getContractStatusId().getId() == AccountContractUtil.CONTRACT_STATUS_DRAFT
              || getContract().getContractStatusId().getId() == AccountContractUtil.CONTRACT_STATUS_RENEW)) {
        return true;
      }
    }
    return false;
  }

  public boolean isDraftContract() {
    if (isContractCreated()) {
      return (getContract().getContractStatusId().getId() == AccountContractUtil.CONTRACT_STATUS_DRAFT);
    } else {
      return false;
    }
  }

  /**
   *
   * @param status
   * @return
   */
  public String getContractStatus(ContractStatus status) {
    return AccountContractUtil.getContractStatus(status);
  }

  /**
   * Method to add new range in commission by range data table.
   *
   * @return
   */
  public String addNewContractCommiByRange() {
    if (contractCommiByRangeList == null) {
      contractCommiByRangeList = new ArrayList<>();
    }
    if (contractCommiByRangeList != null && (contractCommiByRangeList.isEmpty() || contractCommiByRangeList.get(0).getId() != null)) {
      ContractCommiByRange ccr = new ContractCommiByRange();

      if (contractCommiByRangeList.isEmpty()) {
        ccr.setId(0 - 1);
        contractCommiByRangeList.add(0, ccr);
      } else {
        if (getContract() != null && getContract().getId() == null) {
          if (validateCommiByRangeRequieredFields(contractCommiByRangeList.get(0))) {
            ccr.setId(0 - (contractCommiByRangeList.size() + 1));
            contractCommiByRangeList.add(0, ccr);
          }
        } else if (validateCommiByRangeRequieredFields(contractCommiByRangeList.get(0))) {
          contractCommiByRangeList.add(0, ccr);
        }
      }
    }
    return null;
  }

  private boolean validateCommiByRangeRequieredFields(ContractCommiByRange ccr) {
    return ((ccr.getRangeFrom() != null && ccr.getRangeTo() != null) && (ccr.getValuePercentage() != null || ccr.getValueFixed() != null));
  }

  /**
   *
   * @param event
   */
  public void actionInsertOrUpdateCommissionRange(MainView main) {
    try {
      Integer rowIndex = Jsf.getParameterInt("rownumber");
      setCurrentRow(rowIndex);
      if (getContractCommiByRangeList() != null && !getContractCommiByRangeList().isEmpty()) {
        ContractCommiByRange contractComm = getContractCommiByRangeList().get(rowIndex);
        if (getAccount().getId() != null) {
          contractComm.setContractId(getContract());
          ContractCommiByRangeService.insertOrUpdate(main, contractComm);
          getContractCommiByRangeList().get(rowIndex).setContractId(getContract());
        } else {
          getContractCommiByRangeList().get(rowIndex).setContractId(getContract());
        }
        if (rowIndex == getContractCommiByRangeList().size() - 1) {
          actionAddNewCommissionRange(contractComm);
        }
      }

    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void actionAddNewCommissionRange(ContractCommiByRange commitionRange) {
    ContractCommiByRange contractComm = new ContractCommiByRange();
    if (commitionRange != null) {
      if (commitionRange.getRangeTo() != null) {
        contractComm.setRangeFrom(commitionRange.getRangeTo() + 1);
      }
    }
    contractComm.setContractId(getContract());
    getContractCommiByRangeList().add(contractComm);
  }

  /**
   *
   * @return
   */
  public List<SelectItem> getListPurchaseLevel() {
    if (isSuperStockiest()) {
      listPointOfPurchase = new ArrayList<>();
      SelectItem si1 = new SelectItem();
      si1.setItemLabel("First Purchase");
      si1.setIntValue(1);
      listPointOfPurchase.add(si1);
      SelectItem si2 = new SelectItem();
      si2.setItemLabel("Second Purchase");
      si2.setIntValue(2);
      listPointOfPurchase.add(si2);
    }
    return listPointOfPurchase;
  }

  public List<SelectItemInteger> getListYesNo() {
    List<SelectItemInteger> listYesNo = new ArrayList<>();
    SelectItemInteger si1 = new SelectItemInteger();
    si1.setItemLabel("Yes");
    si1.setIntValue(1);
    listYesNo.add(si1);
    if (isSuperStockiest() || getAccount().getId() == null) {
      SelectItemInteger si2 = new SelectItemInteger();
      si2.setItemLabel("No");
      si2.setIntValue(2);
      listYesNo.add(si2);
    }
    return listYesNo;
  }

  public List<SelectItemInteger> getSelectItemYesNo() {
    List<SelectItemInteger> listYesNo = new ArrayList<>();
    SelectItemInteger si1 = new SelectItemInteger();
    si1.setItemLabel("Yes");
    si1.setIntValue(1);
    listYesNo.add(si1);
    SelectItemInteger si2 = new SelectItemInteger();
    si2.setItemLabel("No");
    si2.setIntValue(2);
    listYesNo.add(si2);
    return listYesNo;
  }

  public boolean isBenefitByCommission() {
    if (getAccount() != null && getAccount().getCompanyTradeProfileId() != null) {
      if (getAccount().getCompanyTradeProfileId().getId() != null) {
        return (getAccount().getCompanyTradeProfileId().getId().equals(AccountUtil.TRADE_TYPE_CSA) || getAccount().getCompanyTradeProfileId().getId() == AccountUtil.TRADE_TYPE_CF);
      }
    }
    return false;
  }

  public boolean isBenefitByMarginPercentage() {
    return (getAccount() != null && getAccount().getCompanyTradeProfileId() != null && getAccount().getCompanyTradeProfileId().getId().equals(AccountUtil.TRADE_TYPE_SS));
  }

  public boolean isFixedCommissionByRange() {
    if (getContract() != null && getContract().getBenefitType() != null && getContract().getCommissionValueType() != null && getContract().getCommissionByRange() != null) {
      return (getContract().getBenefitType() == 1 && getContract().getCommissionValueType() == 1 && getContract().getCommissionByRange() == 2);
    }
    return false;
  }

  public boolean isPercentageCommissionByRange() {
    if (getContract() != null && getContract().getBenefitType() != null && getContract().getCommissionValueType() != null && getContract().getCommissionByRange() != null) {
      return (getContract().getBenefitType() == 1 && getContract().getCommissionValueType() == 2 && getContract().getCommissionByRange() == 2);
    }
    return false;
  }

  public boolean isFixedCommissionValue() {
    return (getContract().getBenefitType() == 1 && getContract().getCommissionValueType() == 1);
  }

  public boolean isPercentageCommissionValue() {
    return (getContract().getBenefitType() == 1 && getContract().getCommissionValueType() == 2);
  }

  public boolean isCommisionByRange() {
    if (getContract() != null) {
      return (getContract().getCommissionByRange() != null && getContract().getCommissionByRange() == 1);
    }
    return false;
  }

  public List<SelectItem> getListCommissionValueType() {
    List<SelectItem> listYesNo = new ArrayList<>();
    SelectItem si1 = new SelectItem();
    si1.setItemLabel("Fixed");
    si1.setIntValue(1);
    listYesNo.add(si1);
    SelectItem si2 = new SelectItem();
    si2.setItemLabel("Percentage");
    si2.setIntValue(2);
    listYesNo.add(si2);
    return listYesNo;
  }

  public List<SelectItem> getListCommissionOn() {
    List<SelectItem> listYesNo = new ArrayList<>();
    SelectItem si1 = new SelectItem();
    si1.setItemLabel("Sales");
    si1.setIntValue(1);
    listYesNo.add(si1);
    SelectItem si2 = new SelectItem();
    si2.setItemLabel("Collection");
    si2.setIntValue(2);
    listYesNo.add(si2);
    return listYesNo;
  }

  public List<SelectItem> getListPurchaseCreditOn() {
    List<SelectItem> list = new ArrayList<>();
    SelectItem si1 = new SelectItem();
    si1.setItemLabel("Goods Received");
    si1.setIntValue(1);
    list.add(si1);
    SelectItem si2 = new SelectItem();
    si2.setItemLabel("Purchase Invoice");
    si2.setIntValue(2);
    list.add(si2);
    SelectItem si3 = new SelectItem();
    si3.setItemLabel("Sales");
    si3.setIntValue(3);
    list.add(si3);
    SelectItem si4 = new SelectItem();
    si4.setItemLabel("Collection");
    si4.setIntValue(4);
    list.add(si4);
    SelectItem si5 = new SelectItem();
    si5.setItemLabel("LR Date");
    si5.setIntValue(5);
    list.add(si5);
    return list;
  }

  /**
   *
   */
  private void setBenefitType() {
    if (isSuperStockiest()) {
      getContract().setBenefitType(2);
    } else {
      getContract().setBenefitType(1);
    }
  }

  /**
   *
   * @return
   */
  public boolean isCommissionRangeExist() {
    return (!StringUtil.isEmpty(getContractCommiByRangeList()));
  }

  /**
   *
   * @param marginPercentage
   * @param vendorReservePercent
   */
  private void calculateTotalMargin(Double marginPercentage, Double vendorReservePercent) {
    setTotalMargin(marginPercentage + vendorReservePercent);
  }

  /**
   *
   */
  public void actionCalculateTotalMargin() {
    try {
      double marginPercentage = getContract().getMarginPercentage() == null ? 0 : getContract().getMarginPercentage();
      double vendorReservePercent = getContract().getVendorReservePercent() == null ? 0 : getContract().getVendorReservePercent();
      calculateTotalMargin(marginPercentage, vendorReservePercent);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /**
   *
   * @return
   */
  public boolean isPointOfPurchaseApplicable() {
    if (getAccount() != null && getAccount().getCompanyTradeProfileId() != null) {
      this.pointOfPurchaseApplicable = isSuperStockiest();
    }
    return this.pointOfPurchaseApplicable;
  }

  public void setPointOfPurchaseApplicable(boolean pointOfPurchaseApplicable) {
    this.pointOfPurchaseApplicable = pointOfPurchaseApplicable;
  }

  /**
   *
   * @return
   */
  public boolean isFactoryDirect() {
    return (getContract() != null && getContract().getIsFactoryDirect() != null && getContract().getIsFactoryDirect() == 1);
  }

  /**
   *
   * @param main
   */
  public void deleteContractCommissionRange(MainView main, ContractCommiByRange selectedCommissionRange) {
    try {
      if (getContractCommiByRangeList() != null) {
        if (getAccount() != null && getAccount().getId() != null) {
          if (selectedCommissionRange.getId() != null) {
            ContractCommiByRangeService.deleteByPk(main, selectedCommissionRange);
            main.commit();
          }
          getContractCommiByRangeList().remove(selectedCommissionRange);
        } else {
          getContractCommiByRangeList().remove(selectedCommissionRange);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  /**
   * Trade type select one ajax event handler.
   *
   * @param event
   */
  public void updatePointOfPurchase(SelectEvent event) {
    TradeProfile tp = (TradeProfile) event.getObject();
    getAccount().setCompanyTradeProfileId(tp);
    if (AccountUtil.isCarryingForwardingAgent(getAccount()) || AccountUtil.isConsigneeSalesAgent(getAccount())) {
      getAccount().setIsPurchaseInterstate(1);
    }
  }

  /**
   *
   * @param event
   */
  public void updateVendorDetails(SelectEvent event) {

    MainView main = Jsf.getMain();
    try {
      Vendor v = (Vendor) event.getObject();
      getAccount().setAccountTitle(v.getVendorName());
      getAccount().setTin(v.getTinNo());
      getAccount().setCstRegNo(v.getCstNo());
      getAccount().setGstNo(v.getGstNo());
      setSecondaryVendor(false);
      if (v.getVendorId() != null) {
        setSecondaryVendor(true);
        Account primaryAccount = AccountService.selectAccountByVendor(main, v.getVendorId());
        getAccount().setCompanyTradeProfileId(primaryAccount.getCompanyTradeProfileId());
        getAccount().setPurchaseChannel(primaryAccount.getPurchaseChannel());
        getAccount().setSalesChannel(primaryAccount.getSalesChannel());
        if (AccountUtil.isCarryingForwardingAgent(getAccount()) || AccountUtil.isConsigneeSalesAgent(getAccount())) {
          getAccount().setIsPurchaseInterstate(1);
        }
      } else {
        getAccount().setCompanyTradeProfileId(null);
        getAccount().setIsPurchaseInterstate(2);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }

  }

  public boolean disableInterState() {
    if (getAccount() == null) {
      return false;
    } else if (getAccount() != null && AccountUtil.isActiveAccount(getAccount())) {
      return true;
    } else if (getAccount() != null && getAccount().getCompanyTradeProfileId() != null && (AccountUtil.isCarryingForwardingAgent(getAccount()) || AccountUtil.isConsigneeSalesAgent(getAccount()))) {
      return true;
    }
    return false;
  }

  public boolean isAccountEditable() {
    if (AccountUtil.isActiveAccount(getAccount())) {
      return editMode;
    }
    return true;
  }

  /**
   *
   * @param filter
   * @return
   */
  public List<AccountingLedger> accountingLedgerPurchaseItemsAuto(String filter) {
    return FinLookupView.ledgerPurchaseAuto(filter, getAccount().getCompanyId().getId());
  }

  public List<AccountingLedger> accountingLedgerSupplierClaimItemsAuto(String filter) {
    return FinLookupView.ledgerDirectAndIndirectIncomeAuto(filter, getAccount().getCompanyId().getId());
  }

  /**
   * Vendor autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.vendorAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.vendorAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Vendor> vendorAuto(String filter) {
    if (getAccount().getId() != null) {
      return ScmLookupExtView.accountVendorAuto(filter, getAccount().getCompanyId().getId(), getAccount());
    } else {
      return ScmLookupExtView.accountVendorAuto(filter, getAccount().getCompanyId().getId(), null);
    }
  }

  /**
   * Periods autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.periodsAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.periodsAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Period> periodsAuto(String filter) {
    return ScmLookupView.periodsAuto(filter);
  }

  /**
   *
   * @return
   */
  public List<CompanyAddress> companyAddress() {
    if (getAccount().getCompanyId().getId() != null) {
      return ScmLookupExtView.companyAddress(getAccount().getCompanyId().getId());
    }
    return null;
  }

  /**
   *
   * @return
   */
  public List<CompanyBank> companyBank() {
    if (getAccount().getCompanyId().getId() != null) {
      return ScmLookupExtView.companyBank(getAccount().getCompanyId().getId());
    }
    return null;
  }

  /**
   *
   * @return
   */
  public List<VendorBank> vendorBank() {
    List<VendorBank> list = null;
    if (getAccount().getVendorId() != null && getAccount().getVendorId().getId() != null) {
      list = ScmLookupExtView.vendorBank(getAccount().getVendorId().getId());
    }
    return list;
  }

  /**
   *
   * @return
   */
  public List<VendorAddress> vendorAddress() {
    List<VendorAddress> list = null;
    if (getAccount().getVendorId() != null && getAccount().getVendorId().getId() != null) {
      list = ScmLookupExtView.vendorAddress(getAccount().getVendorId().getId());
    }
    return list;
  }

  /**
   *
   * @param main
   * @param accountGroupPriceList
   */
  public void deleteAccountGroupPriceList(MainView main, AccountGroupPriceList accountGroupPriceList) {
    try {
      if (accountGroupPriceList != null && accountGroupPriceList.getId() != null) {
        AccountGroupPriceListService.deleteByPk(main, accountGroupPriceList);
        main.commit("success.delete");
      }
      getAccountGroupPriceList().remove(accountGroupPriceList);
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }

  }

  public String getTempCode() {
    return tempCode;
  }

  public int getCurrentRow() {
    return currentRow;
  }

  public void setCurrentRow(int currentRow) {
    this.currentRow = currentRow;
  }

  public void validateRangeFrom(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    Integer intValue = (Integer) value;
    int rowIndex = Integer.parseInt(component.getClientId().split(":")[1]);
    ContractCommiByRange prevContractComm;
    getContractCommiByRangeList().get(rowIndex).setRangeFrom(intValue);
    if (intValue != null && rowIndex != 0) {
      prevContractComm = getContractCommiByRangeList().get(rowIndex - 1);
      if (prevContractComm.getRangeTo() != null && intValue <= prevContractComm.getRangeTo()) {
        Jsf.error(component, "error.invalid.rangefrom");
      }
    }
  }

  public void validateRangeTo(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    Integer intValue = (Integer) value;
    int rowIndex = Integer.parseInt(component.getClientId().split(":")[1]);
    ContractCommiByRange contractCom = getContractCommiByRangeList().get(rowIndex);

    if (intValue != null) {
      if (!isValidToRange(contractCom, intValue)) {
        Jsf.error(component, "error.invalid.rangeto");
      }
    }

    int repeatIndex = isRangeDuplicated(getContractCommiByRangeList(), rowIndex, intValue);
    if (repeatIndex == rowIndex) {
      Jsf.error(component, "error.range.duplicated");
    }
  }

  private boolean isValidToRange(ContractCommiByRange contractComm, Integer rangeTo) {
    return ((contractComm.getRangeFrom() != null) && (rangeTo > contractComm.getRangeFrom()));
  }

  private int isRangeDuplicated(List<ContractCommiByRange> contractCommList, Integer rowIndex, Integer rangeTo) {
    String range1;
    String range2;
    int rcount;
    int index1 = 0;
    int index2;

    for (ContractCommiByRange contract : contractCommList) {
      if (rowIndex == index1) {
        range1 = contract.getRangeFrom() + "" + rangeTo;
      } else {
        range1 = contract.getRangeFrom() + "" + contract.getRangeTo();
      }
      rcount = 0;
      index2 = 0;
      index1++;
      for (ContractCommiByRange acc : contractCommList) {
        if (index2 == rowIndex) {
          range2 = acc.getRangeFrom() + "" + rangeTo;
        } else {
          range2 = acc.getRangeFrom() + "" + contract.getRangeTo();
        }
        if (range1.equals(range2)) {
          rcount++;
        }
        if (rcount > 1) {
          return index2;
        }
        index2++;
      }
    }
    return -1;
  }

  public void updateExpenseLedger() {
    if (getDefaultAccountGroup().getServiceAsExpense().intValue() == 0) {
      getDefaultAccountGroup().setExpenseLedgerId(null);
    }
  }

  public void updateExpenseLedger2() {
    if (getDefaultAccountGroup().getService2Enabled().intValue() == 0) {
      getDefaultAccountGroup().setService2LedgerId(null);
    }
  }

  public List<AccountingLedger> salesExpenseLedgerAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      return AccountingLedgerService.selectLedgerListByLedgerExpense(main, getAccount().getCompanyId().getId(), filter);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

}
