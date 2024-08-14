/*
 * @(#)VendorClaimView.java	1.0 Mon Jan 22 10:40:44 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.constant.AccountingConstant;
import spica.constant.ReportConstant;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.TaxCode;
import spica.scm.common.PlatformData;
import spica.scm.domain.Account;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.fin.domain.VendorClaim;
import spica.fin.domain.VendorClaimDetail;
import spica.fin.domain.VendorClaimHeader;
import spica.fin.service.VendorClaimService;
import spica.scm.domain.Vendor;
import spica.scm.domain.Company;
import spica.fin.domain.VendorClaimStatus;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.CompanyLicense;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.VendorAddress;
import spica.scm.domain.VendorLicense;
import spica.scm.print.PrintVendorClaim;
import spica.scm.service.CommodityService;
import spica.scm.service.CompanyAddressService;
import spica.scm.service.CompanyLicenseService;
import spica.scm.service.CompanySettingsService;
import spica.scm.service.VendorAddressService;
import spica.scm.service.VendorLicenseService;
import spica.scm.tax.GstIndia;
import spica.scm.tax.TaxCalculator;
import spica.scm.view.ScmLookupView;
import spica.sys.ClaimConstants;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * VendorClaimView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Jan 22 10:40:44 IST 2018
 */
@Named(value = "vendorClaimView")
@ViewScoped
public class VendorClaimView implements Serializable {

  private transient VendorClaim vendorClaim;	//Domain object/selected Domain.
  private transient LazyDataModel<VendorClaim> vendorClaimLazyModel; 	//For lazy loading datatable.
  private transient VendorClaim[] vendorClaimSelected;	 //Selected Domain Array
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
  private boolean draft;
  private List<VendorClaim> claimList;
  private List<VendorClaimHeader> headerList;
  private List<VendorClaimDetail> claimDetailList;

  private List<AccountingLedger> inputTaxList;
  private List<AccountingLedger> outPutTaxList;
  private List<AccountingLedger> expenseList;
  private HashMap<Integer, TaxCode> taxMap;
  private Double totalTax;
  private Double totalTaxAmount;
  private Map<String, Double[]> taxSummation;
  private List<String> taxList;
  private List<TaxCode> tdsTaxCodelist;
  private Double totalExpense;
  private TaxCalculator taxCalculator;

  @PostConstruct
  public void init() {
    VendorClaim claim = (VendorClaim) Jsf.popupParentValue(VendorClaim.class);
    Integer claimId = (Integer) Jsf.popupParentValue(Integer.class);
    if (claim != null) {
      setVendorClaim(claim);
    } else {
      getVendorClaim().setId(claimId);
    }
  }

  /**
   * Default Constructor.
   */
  public VendorClaimView() {
    super();
  }

  /**
   * Return VendorClaim.
   *
   * @return VendorClaim.
   */
  public VendorClaim getVendorClaim() {
    if (vendorClaim == null) {
      vendorClaim = new VendorClaim();
    }
    if (vendorClaim.getCompanyId() == null) {
      vendorClaim.setCompanyId(UserRuntimeView.instance().getCompany());
    }
    return vendorClaim;
  }

  /**
   * Set VendorClaim.
   *
   * @param vendorClaim.
   */
  public void setVendorClaim(VendorClaim vendorClaim) {
    this.vendorClaim = vendorClaim;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchVendorClaim(MainView main, String viewType) {
    countMonth();
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          genNewVendorClaim(main);
          VendorClaimService.updateInvoiceNumber(main, getVendorClaim(), true);
        } else if (main.isEdit() && !main.hasError()) {
          loadSelectedVendorClaimList(main);
        } else if (main.isList()) {
          getVendorClaim().setCompanyId(null);
          accountList = null;
          selectedAccount = null;
          getAccountList();
          loadVendorClaimList(main);
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
   * Create vendorClaimLazyModel.
   *
   * @param main
   */
  private void loadVendorClaimList(MainView main) {
    if (vendorClaimLazyModel == null) {
      vendorClaimLazyModel = new LazyDataModel<VendorClaim>() {
        private List<VendorClaim> list;

        @Override
        public List<VendorClaim> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = VendorClaimService.listPaged(main, getSelectedAccount().getVendorId().getId(), getVendorClaim().getCompanyId().getId());
            main.commit(vendorClaimLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(VendorClaim vendorClaim) {
          return vendorClaim.getId();
        }

        @Override
        public VendorClaim getRowData(String rowKey) {
          if (list != null) {
            for (VendorClaim obj : list) {
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
  public String saveVendorClaim(MainView main, Integer type) {
    getVendorClaim().setStatusId(type);
    return saveOrCloneVendorClaim(main, "save", type);
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneVendorClaim(MainView main, String key, Integer type) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            if (getVendorClaim().getStatusId().intValue() == SystemConstants.DRAFT && StringUtil.isEmpty(getVendorClaim().getInvoiceNumber())) {
              VendorClaimService.updateInvoiceNumber(main, getVendorClaim(), getVendorClaim().getStatusId().intValue() == SystemConstants.DRAFT);
            }
            getVendorClaim().setAccountId(selectedAccount);
            VendorClaimService.insertOrUpdate(main, getVendorClaim(), claimDetailList);
            if (getVendorClaim().getStatusId().intValue() == SystemConstants.CONFIRMED) {
              getTaxCalculator().saveVendorClaim(main, getVendorClaim(), getClaimDetailList());

//              PlatformService.insertVendorClaim(main, selectedAccount, SystemRuntimeConfig.VENDOR_CLAIM_DESC, getVendorClaim().getCommissionClaim(), null, getVendorClaim(), PlatformService.NORMAL_FUND_STATE);
            }
            loadSelectedVendorClaimList(main);
            break;
          case "clone":
            VendorClaimService.clone(main, getVendorClaim());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
//        loadSelectedVendorClaimList(main);
      }
    } catch (Throwable t) {
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many VendorClaim.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteVendorClaim(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(vendorClaimSelected)) {
        VendorClaimService.deleteByPkArray(main, getVendorClaimSelected()); //many record delete from list
        main.commit("success.delete");
        vendorClaimSelected = null;
      } else {
        VendorClaimService.deleteByPk(main, getVendorClaim());  //individual record delete from list or edit form
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

  /**
   * Return LazyDataModel of VendorClaim.
   *
   * @return
   */
  public LazyDataModel<VendorClaim> getVendorClaimLazyModel() {
    return vendorClaimLazyModel;
  }

  /**
   * Return VendorClaim[].
   *
   * @return
   */
  public VendorClaim[] getVendorClaimSelected() {
    return vendorClaimSelected;
  }

  /**
   * Set VendorClaim[].
   *
   * @param vendorClaimSelected
   */
  public void setVendorClaimSelected(VendorClaim[] vendorClaimSelected) {
    this.vendorClaimSelected = vendorClaimSelected;
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
    return ScmLookupView.vendorAuto(filter);
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
   * VendorClaimStatus autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.vendorClaimStatusAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.vendorClaimStatusAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<VendorClaimStatus> vendorClaimStatusAuto(String filter) {
    return ScmLookupView.vendorClaimStatusAuto(filter);
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
      /// selectedMonth = currentMonth + 1;

    } else {
      //  selectedMonth = 1;
    }
    selectedMonth = 0;
    for (int i = 0; i < whichMonth; i++) {
      monthsMap.put(i + 1, (i + 1 < 10 ? "0" : "") + (i + 1 + " - " + months[i]));
    }
    for (int i = 0; i < 12; i++) {
      allMonthsMap.put(i + 1, months[i]);
    }
    return monthsMap;
  }

  public List<PlatformData> generateClaimMonths(MainView main) {

    List<PlatformData> allDates = new ArrayList<>();
    // List<PlatformData> allDatesTemp = new ArrayList<>();
    String monthString = new DateFormatSymbols().getMonths()[currentMonth == 0 ? currentMonth : currentMonth - 1];
    String maxDate = monthString + "-" + currentYear;
    SimpleDateFormat monthDate = new SimpleDateFormat("MMMM-yyyy");
    SimpleDateFormat monthDate1 = new SimpleDateFormat("MM");
    SimpleDateFormat monthDate2 = new SimpleDateFormat("MMM");
    SimpleDateFormat monthDate3 = new SimpleDateFormat("yyyy");

    try {
      Calendar cal = Calendar.getInstance();
      cal.setTime(monthDate.parse(maxDate));
      getAccountList();
      if (selectedAccount == null) {
        main.error("account.required");
        return null;
      }
      //TODO FIXME sujeesh this is called multiple times
      List<VendorClaim> processedClaimList = null;
      if (getVendorClaim().getAccountId() != null) {
        processedClaimList = VendorClaimService.selectProcessedClaim(main, getVendorClaim());
      }
      
//      List<VendorClaim> processedClaimList = null;

      for (int i = 1; i <= 12; i++) {
        PlatformData pd = new PlatformData();
        pd.setClaimMonth(Integer.parseInt(monthDate1.format(cal.getTime())));
        pd.setClaimMonthName(monthDate2.format(cal.getTime()));
        pd.setClaimYear(Integer.parseInt(monthDate3.format(cal.getTime())));
        if (processedClaimList == null || !processedClaimList.stream().filter(o -> (o.getClaimMonth().intValue() == pd.getClaimMonth())).findFirst().isPresent()) {
          allDates.add(pd);
        }
        cal.add(Calendar.MONTH, -1);
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
      vendorClaim = new VendorClaim();
      getVendorClaim().reset();
      taxSummation = new HashMap<>();
      getVendorClaim().setClaimYear(p.getClaimYear());
      getVendorClaim().setClaimMonth(p.getClaimMonth());
      setSelectedClaimMonthName(p.getClaimMonthName());
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
    vendorClaimLazyModel = null;
    MainView main = Jsf.getMain();
    try {
      loadVendorClaimList(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
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
    if (getVendorClaim() != null) {
      selectedClaimMonthName = new DateFormatSymbols().getMonths()[getVendorClaim().getClaimMonth() - 1];
    }
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
    if (StringUtil.isEmpty(accountList)) {
      Account tmpAccount = null;
      accountList = new ArrayList<>();
      if (UserRuntimeView.instance().getNavAccountList() != null) {
        for (Account account : UserRuntimeView.instance().getNavAccountList()) {
          accountList.add(account);
          if (selectedAccount == null) {
            if (getVendorClaim() != null && getVendorClaim().getVendorId() != null) {
              if (account.getVendorId() != null && account.getVendorId().getId().intValue() == getVendorClaim().getVendorId().getId()) {
                setSelectedAccount(account);
              }
            }
          }
          if (tmpAccount == null) {
            tmpAccount = account;
          }
        }
        if (selectedAccount == null) {
          selectedAccount = tmpAccount;
        }
      }
    }

    return accountList;
  }

  public Account getSelectedAccount() {
    return selectedAccount;
  }

  public void setSelectedAccount(Account selectedAccount) {
    this.selectedAccount = selectedAccount;
  }

  public boolean isDraft() {
    return draft;
  }

  public void setDraft(boolean draft) {
    this.draft = draft;
  }

  private void genNewVendorClaim(MainView main) {
    getVendorClaim().setCompanyId(getVendorClaim().getCompanyId());
    getVendorClaim().setVendorId(getSelectedAccount().getVendorId());
    getVendorClaim().setAccountId(getSelectedAccount());
    getVendorClaim().setNarration("For the" + getSelectedClaimMonthName() + " - " + getVendorClaim().getClaimYear());
    claimDetailList = new ArrayList<>();
    claimDetailList.add(getNewVendorClaimDetailObject(ClaimConstants.CLAIM_SALES));
    claimDetailList.add(getNewVendorClaimDetailObject(ClaimConstants.CLAIM_SALES_RETURN));
    claimDetailList.add(getNewVendorClaimDetailObject(ClaimConstants.CLAIM_PURCHASE));
    claimDetailList.add(getNewVendorClaimDetailObject(ClaimConstants.CLAIM_PURCHASE_RETURN));
    claimDetailList.add(getNewVendorClaimDetailObject(ClaimConstants.CLAIM_DEBIT_NOTE));
    claimDetailList.add(getNewVendorClaimDetailObject(ClaimConstants.CLAIM_CREDIT_NOTE));
    claimDetailList.add(getNewVendorClaimDetailObject(ClaimConstants.CLAIM_EXPENSES));
    createTaxMap(main);
  }

  public void newTableRow(Integer rowIndex, Integer claimtype) {
    for (int i = rowIndex; i < claimDetailList.size(); i++) {
      if ((i + 1 == claimDetailList.size()) || claimDetailList.get(i + 1).getClaimType() != claimtype) {
        rowIndex = i;
        break;
      }
    }

    Double amount = 0.0, taxSum = 0.0;
    if (claimDetailList.get(rowIndex).getClaimType() == claimtype) {
      amount = claimDetailList.get(rowIndex).getTotalTaxableAmount();
      taxSum = claimDetailList.get(rowIndex).getTotalTaxAmount() == null ? 0.0 : claimDetailList.get(rowIndex).getTotalTaxAmount();
      claimDetailList.get(rowIndex).setTotalTaxableAmount(null);
      claimDetailList.get(rowIndex).setTotalTaxAmount(null);
    }
    claimDetailList.add(rowIndex + 1, new VendorClaimDetail(new VendorClaimHeader(claimtype, new VendorClaim()), 1, claimtype));
    if (claimtype.intValue() != ClaimConstants.CLAIM_EXPENSES) {
      claimDetailList.get(rowIndex + 1).setTotalTaxableAmount(amount);
      claimDetailList.get(rowIndex + 1).setTotalTaxAmount(taxSum);
    }
  }

  public List<AccountingLedger> getTaxCode(MainView main, Integer type) {
    if (inputTaxList == null || outPutTaxList == null) {
      try {
        inputTaxList = VendorClaimService.selectInputTax(main, getVendorClaim().getCompanyId());
        outPutTaxList = VendorClaimService.selectOutputTax(main, getVendorClaim().getCompanyId());
        expenseList = VendorClaimService.getExpenseList(main, getVendorClaim().getCompanyId().getId());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    if (type == 1) {
      return inputTaxList;
    } else if (type == 2) {
      return outPutTaxList;
    } else {
      return expenseList;
    }

  }

  public void findSumTaxableValue(Integer rowIndex, Integer accountingEntityTypeId) {
    setLineItemValues(accountingEntityTypeId);
  }

  public void findSumTaxableValue(Integer rowIndex, Integer accountingEntityTypeId, VendorClaimDetail detail) {
    if (detail.getAccountingLedgerId() != null) {
      TaxCode tc = taxMap.get(detail.getAccountingLedgerId().getEntityId());
      if (tc != null && tc.getParentId() != null) {
        AccountingLedger acLedger = VendorClaimService.getAccountingLedgerByTaxCodeAndLedgerCode(Jsf.getMain(), getVendorClaim().getCompanyId().getId(), tc.getParentId().getId(), tc.getId(), detail.getAccountingLedgerId().getLedgerCode());
        if (getClaimDetailList().get(rowIndex).getRelatedRow() == null) {
          newTableRow(rowIndex, detail.getClaimType());
        }
        getClaimDetailList().get(rowIndex + 1).setAccountingLedgerId(acLedger);
        getClaimDetailList().get(rowIndex + 1).setTaxableAmount(getClaimDetailList().get(rowIndex).getTaxableAmount());
        getClaimDetailList().get(rowIndex + 1).setTaxValue((getClaimDetailList().get(rowIndex).getTaxableAmount() * tc.getRatePercentage()) / 100);
        getClaimDetailList().get(rowIndex + 1).setIncludable(false);
        getClaimDetailList().get(rowIndex + 1).setRelatedRow(rowIndex);
        getClaimDetailList().get(rowIndex).setRelatedRow(rowIndex + 1);
        getClaimDetailList().get(rowIndex + 1).setTaxCodeId(tc);
        // getClaimDetailList().get(rowIndex + 1).setTotalTaxAmount(getClaimDetailList().get(rowIndex).getTaxValue() + getClaimDetailList().get(rowIndex + 1).getTaxValue());
        totalTaxAmount += getClaimDetailList().get(rowIndex + 1).getTaxValue();
        //newTableRow(rowIndex + 1, detail.getClaimType());
      }
    }
    setLineItemValues(accountingEntityTypeId);
  }

  private void setLineItemValues(Integer claimType) {
    Double sum = 0.0, taxSum = 0.0;
    boolean start = false, lastLineItem = false;
    int index = 0, taxableIndex = 0;
    totalTax = 0.0;
    totalTaxAmount = 0.0;
    VendorClaimDetail prvsClaimDetail = null;
    for (VendorClaimDetail claimDetail : claimDetailList) {
//      if (claimDetail.isIncludable()) {
      totalTax += claimDetail.getTaxableAmount() == null ? 0.0 : claimDetail.getTaxableAmount();
      totalTaxAmount += claimDetail.getTaxValue() == null ? 0.0 : claimDetail.getTaxValue();
      if (claimDetail.getClaimType() == claimType) {
        start = true;
        setLineItemTax(index);

        if (claimDetail.isIncludable()) {
          sum += claimDetail.getTaxableAmount() == null ? 0.0 : claimDetail.getTaxableAmount();
        }
        taxSum += claimDetail.getTaxValue() == null ? 0.0 : claimDetail.getTaxValue();

        claimDetailList.get(index).setTotalTaxableAmount(null);
        claimDetailList.get(index).setTotalTaxAmount(null);

        taxableIndex = index;
      } else if (start) {
        lastLineItem = true;
      }
      if (lastLineItem || claimDetailList.size() == index + 1) {
        claimDetailList.get(taxableIndex).setTotalTaxableAmount(claimType.intValue() == ClaimConstants.CLAIM_EXPENSES ? null : sum == 0.0 ? null : sum);
        claimDetailList.get(taxableIndex).setTotalTaxAmount(claimType.intValue() == ClaimConstants.CLAIM_EXPENSES ? null : taxSum == 0.0 ? null : taxSum);
        break;
      }
      System.out.println("Total Taxable Amount " + sum);
//      }

      index++;
    }
    createTaxSummationTable(claimDetailList);
  }

  public void setLineItemTax(Integer rowIndex) {
    if (claimDetailList.get(rowIndex).getAccountingLedgerId() != null) {
      Integer id = claimDetailList.get(rowIndex).getAccountingLedgerId().getEntityId();
      if (id != null) {
        TaxCode tax = taxMap.get(id);
        if (tax != null) {
          Double taxable = claimDetailList.get(rowIndex).getTaxableAmount();
          claimDetailList.get(rowIndex).setTaxCodeId(tax);
          if (taxable != null) {
            claimDetailList.get(rowIndex).setTaxValue((taxable * tax.getRatePercentage()) / 100);
          }
        }
      }
    }
  }

  public void deleteRow(Integer rowIndex, Integer claimType, Integer childRow) {

    Integer nxtOrPrvs = claimDetailList.get(rowIndex).getRelatedRow();
    if (nxtOrPrvs != null) {
      if (nxtOrPrvs < rowIndex) {
        rowIndex = nxtOrPrvs;
      } else {
        nxtOrPrvs = rowIndex;
      }
      claimDetailList.remove(nxtOrPrvs.intValue());
      childRow = 0;
      //claimDetailList.remove(nxtOrPrvs.intValue());
    }
    claimDetailList.remove(rowIndex.intValue());
    if (childRow.intValue() == 0) {
      boolean hasChild = false;
      for (VendorClaimDetail detail : claimDetailList) {
        if (detail.getClaimType().intValue() == claimType) {
          if (!hasChild) {
            detail.setChildRow(0);
          }
          hasChild = true;
          break;
        }
      }
      if (!hasChild) {
        claimDetailList.add(rowIndex, getNewVendorClaimDetailObject(claimType));
      }
    }
    findSumTaxableValue(rowIndex, claimType);
  }

  public List<VendorClaim> getClaimList() {
    return claimList;
  }

  public List<VendorClaimHeader> getHeaderList() {
    return headerList;
  }

  public HashMap<Integer, TaxCode> getTaxMap() {
    if (taxMap == null) {
      taxMap = new HashMap<>();
    }
    return taxMap;
  }

  private void loadSelectedVendorClaimList(MainView main) {
    claimDetailList = new ArrayList<>();
    headerList = new ArrayList<>();
    setVendorClaim(VendorClaimService.selectByPk(main, getVendorClaim()));
    claimDetailList = VendorClaimService.selectVendorClaimDetailByClaim(main, getVendorClaim());
    createTaxMap(main);
    createTaxSummationTable(claimDetailList);
  }

  //FIXME these getters should be a variable and laod if null to avoild multiple calls
  public List<TaxCode> getGSTTaxCode(MainView main) {
    List<TaxCode> list = null;
    try {
      list = VendorClaimService.getIGSTTaxCode(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return list;
  }

  public List<TaxCode> getCGSTTaxCode(MainView main) {
    List<TaxCode> list = null;
    try {
      list = VendorClaimService.getCGSTTaxCode(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return list;
  }

  public List<TaxCode> getSGSTTaxCode(MainView main) {
    List<TaxCode> list = null;
    try {
      list = VendorClaimService.getSGSTTaxCode(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return list;
  }

  public void updateIGSTTaxCode(int taxType) {
    if (getVendorClaim().getCommissionAmount() != null) {

      if (getVendorClaim().getSgstTaxCode() != null) {
        getVendorClaim().setTaxValueSgst(getVendorClaim().getCommissionAmount() * (getVendorClaim().getSgstTaxCode().getRatePercentage() / 100.0f));
      }

      if (getVendorClaim().getCgstTaxCode() != null) {
        getVendorClaim().setTaxValueCgst(getVendorClaim().getCommissionAmount() * (getVendorClaim().getCgstTaxCode().getRatePercentage() / 100.0f));
      }
      if (getVendorClaim().getIgstTaxCode() != null) {
        getVendorClaim().setTaxValueIgst(getVendorClaim().getCommissionAmount() * (getVendorClaim().getIgstTaxCode().getRatePercentage() / 100.0f));
      }

      if (getVendorClaim().getTdsTaxCode() != null && getVendorClaim().getCommissionAmount() != null && getVendorClaim().getTdsTaxCode() != null) {
        getVendorClaim().setTdsValue(getVendorClaim().getCommissionAmount() * (getVendorClaim().getTdsTaxCode().getRatePercentage() / 100.0f));
      }

      getVendorClaim().setTaxValueIgstSum(getVendorClaim().getCommissionAmount()
              + (getVendorClaim().getTaxValueIgst() == null ? 0.0 : getVendorClaim().getTaxValueIgst())
              + (getVendorClaim().getTaxValueCgst() == null ? 0.0 : getVendorClaim().getTaxValueCgst())
              + (getVendorClaim().getTaxValueSgst() == null ? 0.0 : getVendorClaim().getTaxValueSgst()));
      getVendorClaim().setCommissionClaim(getVendorClaim().getTaxValueIgstSum() - (getVendorClaim().getTdsValue() == null ? 0.0 : getVendorClaim().getTdsValue()));
    }

  }

  public List<VendorClaimDetail> getClaimDetailList() {
    return claimDetailList;
  }

  public void setClaimDetailList(List<VendorClaimDetail> claimDetailList) {
    this.claimDetailList = claimDetailList;
  }

  private void createTaxSummationTable(List<VendorClaimDetail> claimDetailList) {
    taxSummation = new HashMap<>();
    taxList = new ArrayList<>();
    totalTax = 0.0;
    totalTaxAmount = 0.0;
    totalExpense = 0.0;
    for (VendorClaimDetail detail : claimDetailList) {
      if (detail.getClaimType() != ClaimConstants.CLAIM_EXPENSES) {
        totalTax += detail.getTotalTaxableAmount() == null ? 0.0 : detail.getTotalTaxableAmount();
        totalTaxAmount += detail.getTaxValue() == null ? 0.0 : detail.getTaxValue();
      } else {
        totalExpense += detail.getTaxableAmount() == null ? 0.0 : detail.getTaxableAmount();
      }
      if (detail.getAccountingLedgerId() != null && detail.getAccountingLedgerId().getLedgerCode() != null) {
//        if (detail.getAccountingLedgerId().getLedgerCode().equals(AccountingConstant.LEDGER_CODE_TAX_INPUT)) {
//          if (taxSummation.containsKey(detail.getTaxCodeId().getCode())) {
//            double tax = (taxSummation.get(detail.getTaxCodeId().getCode())[0] == null ? 0.0 : taxSummation.get(detail.getTaxCodeId().getCode())[0]) + (detail.getTaxValue() == null ? 0.0 : detail.getTaxValue());
//            double dummy = taxSummation.get(detail.getTaxCodeId().getCode())[1] == null ? 0.0 : taxSummation.get(detail.getTaxCodeId().getCode())[1];
//        
//            taxSummation.put(detail.getTaxCodeId().getCode(), new Double[]{tax, dummy});
//          } else {
//            taxSummation.put(detail.getTaxCodeId().getCode(), new Double[]{detail.getTaxValue(), 0.0});
//          }
//
//        }
//        if (detail.getAccountingLedgerId().getLedgerCode().equals(AccountingConstant.LEDGER_CODE_TAX_OUTPUT)) {
//          if (taxSummation.containsKey(detail.getTaxCodeId().getCode())) {
//            double tax = (taxSummation.get(detail.getTaxCodeId().getCode())[1] == null ? 0.0 : taxSummation.get(detail.getTaxCodeId().getCode())[1]) + (detail.getTaxValue() == null ? 0.0 : detail.getTaxValue());
//            double dummy = taxSummation.get(detail.getTaxCodeId().getCode())[0] == null ? 0.0 : taxSummation.get(detail.getTaxCodeId().getCode())[0];
//            taxSummation.put(detail.getTaxCodeId().getCode(), new Double[]{dummy, tax});
//          } else {
//            taxSummation.put(detail.getTaxCodeId().getCode(), new Double[]{0.0, detail.getTaxValue()});
//          }
//        }
        boolean input = detail.getAccountingLedgerId().getLedgerCode().equals(AccountingConstant.LEDGER_CODE_TAX_INPUT);
        if(detail.getTaxCodeId()!= null){
        Double[] val = taxSummation.get(detail.getTaxCodeId().getCode());
        if (val != null) {
          Double t = val[0];
          Double d = val[1];
          double tax = t == null ? 0.0 : t + (detail.getTaxValue() == null ? 0.0 : detail.getTaxValue());
          double dummy = d == null ? 0.0 : d;
          taxSummation.put(detail.getTaxCodeId().getCode(), new Double[]{input ? tax : dummy, input ? dummy : tax});
        } else {
          taxSummation.put(detail.getTaxCodeId().getCode(), new Double[]{input ? detail.getTaxValue()==null?0.0:detail.getTaxValue()==null?0.0:detail.getTaxValue() : 0.0, input ? 0.0 : detail.getTaxValue()==null?0.0:detail.getTaxValue()});
        }
        }
      }
    }

    findGstClaim();

    taxList.addAll(taxSummation.keySet());
  }

  public double getTaxInputValue(String code) {
    if (!StringUtil.isEmpty(code) && (taxSummation != null && taxSummation.size() > 0)) {
      return taxSummation.get(code)[0] == null ? 0.0 : taxSummation.get(code)[0];
    } else {
      return 0.0;
    }
  }

  public double getTaxOutputValue(String code) {
    if (!StringUtil.isEmpty(code) && (taxSummation != null && taxSummation.size() > 0)) {
      return taxSummation.get(code)[1] == null ? 0.0 : taxSummation.get(code)[1];
    } else {
      return 0.0;
    }
  }

  public double taxInputOutPutDifference(String code) {
    if (!StringUtil.isEmpty(code) && (taxSummation != null && taxSummation.size() > 0)) {
      return (taxSummation.get(code)[0] == null ? 0.0 : taxSummation.get(code)[0]) - (taxSummation.get(code)[1] == null ? 0.0 : taxSummation.get(code)[1]);
    } else {
      return 0.0;
    }
  }

  private void createTaxMap(MainView main) {
    List<TaxCode> taxList = VendorClaimService.selectTaxByCompany(main, getVendorClaim().getCompanyId().getId());
    tdsTaxCodelist = VendorClaimService.getTdsTaxCode(main, getVendorClaim().getCompanyId().getId());
    if (taxList != null) {
      for (TaxCode taxCode : taxList) {
        getTaxMap().put(taxCode.getId(), taxCode);
      }
    }
  }

  private void findGstClaim() {
    double gstClaim = 0.0;
    if (taxSummation != null && !taxSummation.isEmpty()) {
      for (String key : taxSummation.keySet()) {
        gstClaim += ((taxSummation.get(key)[0] == null ? 0.0 : taxSummation.get(key)[0]) - (taxSummation.get(key)[1] == null ? 0.0 : taxSummation.get(key)[1]));
      }
    }
    getVendorClaim().setGstClaim(gstClaim);
  }

  //FIXME check method is called once
  public List getCommodityServices(MainView main) {
    List<ServiceCommodity> serviceList = null;
    try {
      serviceList = CommodityService.getServices(main, getVendorClaim().getCompanyId(), "");
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return serviceList;
  }

  public void showPdf(MainView main, String type) {
    try {
      getVendorClaim().setPrintType(type.equals(SystemConstants.PRINT_COMMISSION_CLAIM) ? SystemConstants.PRINT_COMMISSION_CLAIM : SystemConstants.PRINT_COMMISSION_EXPENSE);
      Jsf.popupForm(FileConstant.PRINT_VENODR_CLAIM, getVendorClaim());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void printClaim(MainView main, Integer status) throws Exception {
    // FIXME sujesh correct this from throwing error
    try {
      Document document = null;
      FacesContext fc = FacesContext.getCurrentInstance();
      HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
      String fileName = "file_sample" + ".pdf";
      response.setContentType("application/pdf");
      response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

      PdfWriter pdfWriter;

      Rectangle pageSize = null;
      pageSize = new Rectangle(595, 842);
      document = new Document(pageSize, 20, 20, 20, 20);

      pdfWriter = PdfWriter.getInstance(document, response.getOutputStream());
      document.open();
      //
      PrintVendorClaim print = new PrintVendorClaim();

      document.add(print.initiatePrint(main, getVendorClaim(), ReportConstant.SALES_COMMISSION));
      print.createFooterBar(main, document, pdfWriter);
      document.close();
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public Double getTotalTax() {
    return totalTax;
  }

  public void setTotalTax(Double totalTax) {
    this.totalTax = totalTax;
  }

  public Double getTotalTaxAmount() {
    return totalTaxAmount;
  }

  public void setTotalTaxAmount(Double totalTaxAmount) {
    this.totalTaxAmount = totalTaxAmount;
  }

  public List<String> getTaxList() {
    return taxList;
  }

  public void setVendorClaimLazyModel(LazyDataModel<VendorClaim> vendorClaimLazyModel) {
    this.vendorClaimLazyModel = vendorClaimLazyModel;
  }

  public List<TaxCode> getTdsTaxCodelist() {
    return tdsTaxCodelist;
  }

  public Double getTotalExpense() {
    return totalExpense;
  }

  public void setTotalExpense(Double totalExpense) {
    this.totalExpense = totalExpense;
  }

  public void updateServiceTax(MainView main) {
    try {
      if (getVendorClaim() != null && getVendorClaim().getService() != null) {
        if (getVendorClaim().getService().getSalesTaxCodeId() != null && getSelectedAccount().getIsPurchaseInterstate() != null) {
          if (getSelectedAccount().getIsPurchaseInterstate().intValue() == SystemConstants.INTRASTATE_PURCHASE) {
            getVendorClaim().setIgstTaxCode(getVendorClaim().getService().getSalesTaxCodeId());
          } else {
            List<TaxCode> list = VendorClaimService.getTaxCodeByParent(main, getVendorClaim().getService().getSalesTaxCodeId().getId());
            for (TaxCode tax : list) {
              if (tax.getTaxType().intValue() == AccountingConstant.TAX_TYPE_CGST) {
                getVendorClaim().setCgstTaxCode(tax);
              }
              if (tax.getTaxType().intValue() == AccountingConstant.TAX_TYPE_SGST) {
                getVendorClaim().setSgstTaxCode(tax);
              }
            }
          }
          updateIGSTTaxCode(0);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  private VendorClaimDetail getNewVendorClaimDetailObject(Integer type) {
    switch (type) {
      case 1:
        return new VendorClaimDetail(new VendorClaimHeader(ClaimConstants.CLAIM_SALES, new VendorClaim()), 0, ClaimConstants.CLAIM_SALES);
      case 2:
        return new VendorClaimDetail(new VendorClaimHeader(ClaimConstants.CLAIM_SALES_RETURN, new VendorClaim()), 0, ClaimConstants.CLAIM_SALES_RETURN);
      case 3:
        return new VendorClaimDetail(new VendorClaimHeader(ClaimConstants.CLAIM_PURCHASE, new VendorClaim()), 0, ClaimConstants.CLAIM_PURCHASE);
      case 4:
        return new VendorClaimDetail(new VendorClaimHeader(ClaimConstants.CLAIM_PURCHASE_RETURN, new VendorClaim()), 0, ClaimConstants.CLAIM_PURCHASE_RETURN);
      case 5:
        return new VendorClaimDetail(new VendorClaimHeader(ClaimConstants.CLAIM_DEBIT_NOTE, new VendorClaim()), 0, ClaimConstants.CLAIM_DEBIT_NOTE);
      case 6:
        return new VendorClaimDetail(new VendorClaimHeader(ClaimConstants.CLAIM_CREDIT_NOTE, new VendorClaim()), 0, ClaimConstants.CLAIM_CREDIT_NOTE);
      case 7:
        return new VendorClaimDetail(new VendorClaimHeader(ClaimConstants.CLAIM_EXPENSES, new VendorClaim()), 0, ClaimConstants.CLAIM_EXPENSES);
      default:
        return null;
    }
  }

  public TaxCalculator getTaxCalculator() {
    if (taxCalculator == null) {
      taxCalculator = new GstIndia();
    }
    return taxCalculator;
  }

  public Date getToday() {
    return new Date();
  }

  public void resetToDraft(MainView main, Integer status) {
    try {
      getVendorClaim().setStatusId(status);
      VendorClaimService.resetToDraft(main, getVendorClaim());
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

}
