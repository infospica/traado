/*
 * @(#)SalesAgentClaimView.java	1.0 Fri May 19 11:03:19 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.constant.AccountingConstant;
import spica.scm.common.PlatformData;
import spica.scm.domain.Account;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.SalesAgentClaim;
import spica.scm.service.SalesAgentClaimService;
import spica.scm.domain.Vendor;
import spica.scm.domain.Company;
import spica.scm.domain.UserProfile;
//import spica.scm.domain.SalesAgentClaimStatus;
import spica.sys.UserRuntimeView;
import spica.scm.common.ClaimObjects;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.scm.domain.SalesAgentClaimDetail;
import spica.fin.domain.TaxCode;
import spica.fin.service.AccountingLedgerService;
import spica.fin.service.LedgerExternalDataService;
import spica.scm.domain.SalesAgent;
import spica.scm.service.SalesAgentClaimDetailService;
import spica.scm.view.AccountUtil;
import spica.scm.view.ScmLookupExtView;
import spica.scm.view.ScmLookupView;
import wawo.app.faces.Jsf;

/**
 * SalesAgentClaimView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri May 19 11:03:19 IST 2017
 */
@Named(value = "salesAgentClaimView")
@ViewScoped
public class SalesAgentClaimView implements Serializable {

  private transient SalesAgentClaim salesAgentClaim;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesAgentClaim> salesAgentClaimLazyModel; 	//For lazy loading datatable.
  private transient SalesAgentClaim[] salesAgentClaimSelected;	 //Selected Domain Array
  List<Integer> year = new ArrayList<>();
  Calendar now = Calendar.getInstance();
  int currentYear = now.get(Calendar.YEAR);
  private int selectedYear;
  Map<Integer, String> monthsMap = new HashMap<Integer, String>();
  Map<Integer, String> allMonthsMap = new HashMap<Integer, String>();
  int currentMonth = now.get(Calendar.MONTH);
  private int selectedMonth;
  private String selectedClaimMonthName;
  private List<Account> accountList;
  private Account selectedAccount;
  private transient SalesAgent salesAgent;
  List<SalesAgentClaimDetail> claim = new ArrayList<>();
  int monthNumber = 0;
  ClaimObjects obj = new ClaimObjects();
  private Double invoiceAmountTotal;
  private Double claimAmountTotal;
  private Double approvedAmountTotal;
  private transient boolean draft;
  private transient String draftLabel;
  private transient AccountingTransactionDetailItem accountingTransactionDetailItem;

  /**
   * Default Constructor.
   */
  public SalesAgentClaimView() {
    super();
  }

  @PostConstruct
  public void init() {
    accountingTransactionDetailItem = (AccountingTransactionDetailItem) Jsf.popupParentValue(AccountingTransactionDetailItem.class);
    if (accountingTransactionDetailItem != null) {
      Integer id = Integer.parseInt(accountingTransactionDetailItem.getDocumentNumber());
      getSalesAgentClaim().setId(id);
    }
  }

  /**
   * Return SalesAgentClaim.
   *
   * @return SalesAgentClaim.
   */
  public SalesAgentClaim getSalesAgentClaim() {
    if (salesAgentClaim == null) {
      salesAgentClaim = new SalesAgentClaim();
    }
    if (salesAgentClaim.getCompanyId() == null) {
      salesAgentClaim.setCompanyId(UserRuntimeView.instance().getCompany());
    }
    return salesAgentClaim;
  }

  /**
   * Set SalesAgentClaim.
   *
   * @param salesAgentClaim.
   */
  public void setSalesAgentClaim(SalesAgentClaim salesAgentClaim) {
    this.salesAgentClaim = salesAgentClaim;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesAgentClaim(MainView main, String viewType) {
    setDraft(false);
    setDraftLabel(SalesAgentClaimService.CONFIRMED_LABEL);
    countYear();
    countMonth();
    AccountingLedger reimbRecLedger = AccountingLedgerService.selectLedgerByLedgerCode(main, AccountingConstant.LEDGER_CODE_COMMISSION_PAYABLE, getSalesAgentClaim().getCompanyId().getId());
    // generateClaimMonths();
    if (!StringUtil.isEmpty(viewType)) {
      try {
        if (salesAgent == null && "newform".equals(viewType)) {
          main.error("select.sales.agent");
          return null;
        }
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getSalesAgentClaim().reset();
          calculateClaim(main);
          setDraft(true);
          getSalesAgentClaim().setLedgerId(reimbRecLedger);
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setSalesAgentClaim((SalesAgentClaim) SalesAgentClaimService.selectByPk(main, getSalesAgentClaim()));
          claim = SalesAgentClaimDetailService.selectBySalesAgentClaim(main, getSalesAgentClaim());
          getSalesAgentClaim().setTotalAmountClaim(0.0);
          for (SalesAgentClaimDetail list : claim) {
            getSalesAgentClaim().setTotalAmountClaim(getSalesAgentClaim().getTotalAmountClaim() + list.getApprovedAmount());
          }
          if (getSalesAgentClaim().getStatus() == SalesAgentClaimService.DRAFT) {
            setDraft(true);
            setDraftLabel(SalesAgentClaimService.DRAFT_LABEL);
          }
          addIgst(getSalesAgentClaim());
          lessTds(getSalesAgentClaim());
        } else if (ViewTypes.isList(viewType)) {
          getSalesAgentClaim().setCompanyId(null);
          setSalesAgent(null);
          loadSalesAgentClaimList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  public void calculateClaim(MainView main) {
    try {
      if (!claim.isEmpty()) {
        claim.clear();
      }
      claim = SalesAgentClaimService.getCommision(main, salesAgent.getId(), monthNumber);
      getSalesAgentClaim().setTotalAmountClaim(0.0);
      for (SalesAgentClaimDetail list : claim) {
        getSalesAgentClaim().setTotalAmountClaim(getSalesAgentClaim().getTotalAmountClaim() + list.getApprovedAmount());
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }

  /**
   * Create salesAgentClaimLazyModel.
   *
   * @param main
   */
  private void loadSalesAgentClaimList(final MainView main) {
    if (salesAgentClaimLazyModel == null) {
      salesAgentClaimLazyModel = new LazyDataModel<SalesAgentClaim>() {
        private List<SalesAgentClaim> list;

        @Override
        public List<SalesAgentClaim> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesAgentClaimService.listPaged(main, salesAgent, selectedYear, selectedMonth);
            main.commit(salesAgentClaimLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesAgentClaim salesAgentClaim) {
          return salesAgentClaim.getId();
        }

        @Override
        public SalesAgentClaim getRowData(String rowKey) {
          if (list != null) {
            for (SalesAgentClaim obj : list) {
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
    String SUB_FOLDER = "scm_vendor_claim/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesAgentClaim(MainView main, int type) {
    return saveOrCloneSalesAgentClaim(main, "save", type);
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesAgentClaim(MainView main, String key, int type) {
    try {
      uploadFiles(); //File upload
      getSalesAgentClaim().setStatus(type);
      if (null != key) {
        switch (key) {
          case "save":
            // SalesAgentClaimService.insertOrUpdate(main, getSalesAgentClaim());
            invoiceAmountTotal = 0.0;
            claimAmountTotal = 0.0;
            approvedAmountTotal = 0.0;
            for (SalesAgentClaimDetail list : getClaim()) {
              invoiceAmountTotal += list.getInvoiceAmount();
              claimAmountTotal += list.getClaimAmount();
              approvedAmountTotal += list.getApprovedAmount();
            }
            getSalesAgentClaim().setApprovedAmountTotal(approvedAmountTotal);
            getSalesAgentClaim().setClaimAmountTotal(claimAmountTotal);
            getSalesAgentClaim().setInvoiceAmountTotal(invoiceAmountTotal);
            if (salesAgent != null) {
              getSalesAgentClaim().setSalesAgentId(salesAgent);
            }
            SalesAgentClaimService.insertOrUpdate(main, getSalesAgentClaim());
            SalesAgentClaimDetailService.insertOrUpdate(main, getClaim(), getSalesAgentClaim());
            if (getSalesAgentClaim().getStatus() == SalesAgentClaimService.CONFIRMED) {
              LedgerExternalDataService.saveSalesAgentCommission(main, getClaim(), getSalesAgentClaim());
            }
            break;
          case "clone":
            SalesAgentClaimService.clone(main, getSalesAgentClaim());
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
   * Delete one or many SalesAgentClaim.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesAgentClaim(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(salesAgentClaimSelected)) {
        SalesAgentClaimService.deleteByPkArray(main, getSalesAgentClaimSelected()); //many record delete from list
        main.commit("success.delete");
        salesAgentClaimSelected = null;
      } else {
        SalesAgentClaimService.deleteByPk(main, getSalesAgentClaim());  //individual record delete from list or edit form
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

  public List countYear() {
    year.clear();
    int yearMax = now.get(Calendar.YEAR);
    int yearMin = yearMax - 5;
    for (int i = yearMax - 1; i >= yearMin; i--) {
      year.add(i);
    }
    selectedYear = currentYear;
    return year;
  }

  public Map countMonth() {
    monthsMap.clear();
    allMonthsMap.clear();
    String[] months = new DateFormatSymbols().getShortMonths();
    int whichMonth = months.length - 1;
    if (selectedYear == currentYear) {
      whichMonth = currentMonth + 1;
      selectedMonth = currentMonth + 1;

    } else {
      selectedMonth = 1;
    }
    //selectedMonth = 0;
    for (int i = 0; i < whichMonth; i++) {
      monthsMap.put(i + 1, (i + 1 < 10 ? "0" : "") + (i + 1 + " - " + months[i]));
    }
    for (int i = 0; i < 12; i++) {
      allMonthsMap.put(i + 1, months[i]);
    }
    return monthsMap;
  }

  public List<PlatformData> generateClaimMonths(MainView main) throws ParseException {

    List<PlatformData> allDates = new ArrayList<>();
    List<PlatformData> allDatesTemp = new ArrayList<>();
    String monthString = new DateFormatSymbols().getMonths()[currentMonth == 0 ? currentMonth : currentMonth - 1];
    String maxDate = monthString + "-" + currentYear;
    SimpleDateFormat monthDate = new SimpleDateFormat("MMMM-yyyy");
    SimpleDateFormat monthDate1 = new SimpleDateFormat("M");
    SimpleDateFormat monthDate2 = new SimpleDateFormat("MMM");
    SimpleDateFormat monthDate3 = new SimpleDateFormat("yyyy");
    Calendar cal = Calendar.getInstance();
    cal.setTime(monthDate.parse(maxDate));
    try {
      getAccountList();
//      if (selectedAccount == null) {
//        main.error("account.required");
//        return null;
//      }

      List<SalesAgentClaim> processedClaimList = SalesAgentClaimService.listPaged(main, salesAgent, selectedYear, selectedMonth);
      for (int i = 1; i <= 12; i++) {
        PlatformData pd = new PlatformData();
        pd.setClaimMonth(Integer.parseInt(monthDate1.format(cal.getTime())));
        pd.setClaimMonthName(monthDate2.format(cal.getTime()));
        pd.setClaimYear(Integer.parseInt(monthDate3.format(cal.getTime())));
        allDates.add(pd);
        cal.add(Calendar.MONTH, -1);
      }

      for (SalesAgentClaim vc : processedClaimList) {
        cal.setTime(vc.getCreatedAt());
        for (PlatformData pf : allDates) {
          if (pf.getClaimMonth() == cal.get(Calendar.MONTH) + 1 && pf.getClaimYear() == cal.get(Calendar.YEAR)) {
            allDatesTemp.add(pf);
          }
        }
      }
      for (PlatformData pf : allDatesTemp) {
        allDates.remove(pf);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return allDates;
  }

  public void newClaim(MainView main, PlatformData p) {
    try {
      main.setViewType("newform");
      monthNumber = p.getClaimMonth();
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void yearFilter(SelectEvent event) {
    selectedYear = (int) event.getObject();
    countMonth();
  }

  public void monthFilter(SelectEvent event) {
    selectedMonth = (int) event.getObject();
  }

  public void accountFilter(SelectEvent event) {
    selectedAccount = (Account) event.getObject();
  }

  /**
   * Return LazyDataModel of SalesAgentClaim.
   *
   * @return
   */
  public LazyDataModel<SalesAgentClaim> getSalesAgentClaimLazyModel() {
    return salesAgentClaimLazyModel;
  }

  /**
   * Return SalesAgentClaim[].
   *
   * @return
   */
  public SalesAgentClaim[] getSalesAgentClaimSelected() {
    return salesAgentClaimSelected;
  }

  /**
   * Set SalesAgentClaim[].
   *
   * @param salesAgentClaimSelected
   */
  public void setSalesAgentClaimSelected(SalesAgentClaim[] salesAgentClaimSelected) {
    this.salesAgentClaimSelected = salesAgentClaimSelected;
  }

  public int getCurrentYear() {
    return currentYear;
  }

  public void setCurrentYear(int currentYear) {
    this.currentYear = currentYear;
  }

  public int getSelectedYear() {
    return selectedYear;
  }

  public void setSelectedYear(int selectedYear) {
    this.selectedYear = selectedYear;
  }

  public int getCurrentMonth() {
    return currentMonth;
  }

  public void setCurrentMonth(int currentMonth) {
    this.currentMonth = currentMonth;
  }

  public int getSelectedMonth() {
    return selectedMonth;
  }

  public void setSelectedMonth(int selectedMonth) {
    this.selectedMonth = selectedMonth;
  }

  public Map<Integer, String> getMonthsMap() {
    return monthsMap;
  }

  public void setMonthsMap(Map<Integer, String> monthsMap) {
    this.monthsMap = monthsMap;
  }

  public List<Integer> getYear() {
    return year;
  }

  public void setYear(List<Integer> year) {
    this.year = year;
  }

  public String getSelectedClaimMonthName() {
    return selectedClaimMonthName;
  }

  public void setSelectedClaimMonthName(String selectedClaimMonthName) {
    this.selectedClaimMonthName = selectedClaimMonthName;
  }

  public Map<Integer, String> getAllMonthsMap() {
    return allMonthsMap;
  }

  public void setAllMonthsMap(Map<Integer, String> allMonthsMap) {
    this.allMonthsMap = allMonthsMap;
  }

  public List<Account> getAccountList() {
    if (accountList == null) {
      accountList = new ArrayList<>();
      for (Account account : UserRuntimeView.instance().getNavAccountList()) {
        if (AccountUtil.isCarryingForwardingAgent(account) || AccountUtil.isConsigneeSalesAgent(account)) {
          accountList.add(account);
          setSelectedAccount(account);
        }
      }
    }

    return accountList;
  }

  public void setAccountList(List<Account> accountList) {
    this.accountList = accountList;
  }

  public Account getSelectedAccount() {
    return selectedAccount;
  }

  public void setSelectedAccount(Account selectedAccount) {
    this.selectedAccount = selectedAccount;
  }

  /**
   * ScmVendor autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.scmVendorAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.scmVendorAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Vendor> scmVendorAuto(String filter) {
    return ScmLookupView.vendorAuto(filter);
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

  /**
   * SalesAgentClaimStatus autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.salesAgentClaimStatusAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.salesAgentClaimStatusAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
//  public List<SalesAgentClaimStatus> salesAgentClaimStatusAuto(String filter) {
//    return ScmLookupView.scmSalesAgentClaimStatusAuto(filter);
//  }
  public List<UserProfile> scmUserProfileAuto(String filter) {
    return ScmLookupExtView.userProfileAuto(filter, getSalesAgentClaim().getCompanyId().getId());
  }

  public SalesAgent getSalesAgent() {
    return salesAgent;
  }

  public void setSalesAgent(SalesAgent salesAgent) {
    this.salesAgent = salesAgent;
  }

  public List<SalesAgentClaimDetail> getClaim() {
    return claim;
  }

  public void setClaim(List<SalesAgentClaimDetail> claim) {
    this.claim = claim;
  }

  public boolean isDraft() {
    return draft;
  }

  public void setDraft(boolean draft) {
    this.draft = draft;
  }

  public String getDraftLabel() {
    return draftLabel;
  }

  public void setDraftLabel(String draftLabel) {
    this.draftLabel = draftLabel;
  }

  /**
   *
   */
  public void dialogClose() {
    Jsf.closePopup(accountingTransactionDetailItem);
  }

  public List<AccountingLedger> accountingLedgerSalesAgentAuto(String filter) {
    return FinLookupView.ledgerAssetsAndLiabilityAuto(filter, getSalesAgentClaim().getCompanyId().getId());

  }

  public List<TaxCode> taxCodeAuto() {
    return FinLookupView.taxCodeTdsHead(getSalesAgentClaim().getCompanyId());
  }

  public List<TaxCode> taxCodeSalesAgentAuto() {
    return FinLookupView.taxCodeNonTdsHead(getSalesAgentClaim().getCompanyId());
  }

  public void addIgst(SalesAgentClaim salesAgentClaim) {
    Double rate = 0.0;
    salesAgentClaim.setGstAmountClaim(0.0);
    if (salesAgentClaim.getTaxCodeIgstId() != null) {
      for (TaxCode list : salesAgentClaim.getTaxCodeIgstId().getTaxCodeList()) {
        rate += list.getRatePercentage();
      }
      salesAgentClaim.setGstAmountClaim(salesAgentClaim.getTotalAmountClaim() * rate / 100);
    }
    // gstAmountClaim = gstAmountClaim + totalAmountClaim;
  }

  public void lessTds(SalesAgentClaim salesAgentClaim) {
    Double rate = 0.0;
    salesAgentClaim.setTdsAmountClaim(0.0);
    if (salesAgentClaim.getTaxCodeId() != null) {
      rate = salesAgentClaim.getTaxCodeId().getTaxCodeList().stream().map((list) -> list.getRatePercentage()).reduce(rate, (accumulator, _item) -> accumulator + _item);
      rate = salesAgentClaim.getTaxCodeId().getTaxCodeList().stream().mapToDouble(TaxCode::getRatePercentage).sum();
      salesAgentClaim.setTdsAmountClaim(salesAgentClaim.getTotalAmountClaim() * rate / 100);
    }
    // tdsAmountClaim = totalAmountClaim - tdsAmountClaim;
  }
}
