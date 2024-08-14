/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.view;

import java.io.Serializable;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DashboardModel;
import spica.scm.common.Dashboard;
import spica.scm.service.CompanyDashboardService;
import spica.sys.UserRuntimeView;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import java.util.Date;
import java.util.Map;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import org.apache.commons.collections4.map.LinkedMap;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;
import spica.constant.AccountingConstant;
import spica.fin.view.AccountingMainView;
import spica.reports.model.FilterParameters;
import spica.scm.common.ChequeReview;
import spica.scm.util.ChartType;
import spica.scm.util.SalesPurchase;
import spica.sys.SystemConstants;
import wawo.app.config.AppConfig;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.entity.util.DateUtil;

/**
 *
 * @author java-2
 */
@Named(value = "companyDashboardView")
@ViewScoped
public class CompanyDashboardView implements Serializable {

  private DashboardModel dashboard;

  private AccountGroup accountGroup;
  private Account account;

  private transient Date selectedDate;
  private transient Date maxDate;

  private org.primefaces.model.charts.bar.BarChartModel salesChartJsBar;
  private FilterParameters filterParameters;
  private String dateType;
  private boolean calendar;
  private boolean navbtn = true;

  private Dashboard salesBox;
  private Dashboard purchaseBox;
  private Dashboard outstandingBox;

  private List<Dashboard> topCustomerOutstandingList;
  private List<Dashboard> topSupplierOutstandingList;
  private ChequeReview chequeReview;
  private ChequeReview chequeIssued;
  private String[] selectedChartItems;
  private List<String> chartItems;

  @Inject
  private AccountingMainView accountingMainView;

  public void init(MainView main) {
    main.setViewType(ViewTypes.list);
    salesBox = new Dashboard();
    purchaseBox = new Dashboard();
    topCustomerOutstandingList = new ArrayList<>();
    topSupplierOutstandingList = new ArrayList<>();
    setBarTypes();
    createBox(main);
  }

  /**
   * Creates a new instance of CompanyDashboardView
   */
  public CompanyDashboardView() {

  }

  public List<Account> accountAuto(String filter) {
    if (accountGroup != null && accountGroup.getId() != null) {
      MainView main = Jsf.getMain();
      try {
        return ScmLookupExtView.accountByAccountGroupProfile(main, accountGroup, filter);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public void accountGroupSelectEvent(SelectEvent event) {
    AccountGroup accountGroup = (AccountGroup) event.getObject();
    if (accountGroup != null) {
      if (accountGroup.getId() != null) {
        setAccountGroup(accountGroup);
        setAccount(null);
        getFilterParameters().setAccount(null);
      }

    }
  }

  public AccountGroup getAccountGroup() {
    return accountGroup;
  }

  public void setAccountGroup(AccountGroup accountGroup) {
    this.accountGroup = accountGroup;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Date getSelectedDate() {
    if (selectedDate == null) {
      selectedDate = new Date();
    }
    return selectedDate;
  }

  public void setSelectedDate(Date selectedDate) {
    this.selectedDate = selectedDate;
  }

  public Date getMaxDate() {
    if (maxDate == null) {
      maxDate = new Date();
    }
    return maxDate;
  }

  public void setMaxDate(Date maxDate) {
    this.maxDate = maxDate;
  }

  public DashboardModel getDashboard() {
    return dashboard;
  }

  public org.primefaces.model.charts.bar.BarChartModel getSalesChartJsBar(MainView main) {
    try {
      salesChartJsBar = new org.primefaces.model.charts.bar.BarChartModel();
      if (UserRuntimeView.instance().getCompany() != null && UserRuntimeView.instance().getAccount() != null) {
        List<Dashboard> salesList = null;
        List<Dashboard> purchaseList = null;
        List<Dashboard> salesServiceList = null;
        long days = DAYS.between(getFilterParameters().getFromDate().toInstant(), getFilterParameters().getToDate().toInstant());
        boolean dayOfMonth = (days <= 10) ? true : false;
        if (Arrays.stream(selectedChartItems).anyMatch(ChartType.SALES_INVOICE.getType()::equals)) {
          salesList = CompanyDashboardService.getDashboardSalesList(main, UserRuntimeView.instance().getCompany(), getFilterParameters());
        }
        if (Arrays.stream(selectedChartItems).anyMatch(ChartType.PURCHASE.getType()::equals)) {
          purchaseList = CompanyDashboardService.getDashboardPurchaseList(main, UserRuntimeView.instance().getCompany(), getFilterParameters());
        }
        if (Arrays.stream(selectedChartItems).anyMatch(ChartType.SALES_SERVICE.getType()::equals)) {
          salesServiceList = CompanyDashboardService.getDashboardSalesServiceList(main, UserRuntimeView.instance().getCompany(), getFilterParameters());
        }

        if (salesList != null || purchaseList != null || salesServiceList != null) {
          createBarChartJsModel(salesChartJsBar, "Month", salesList, purchaseList, salesServiceList, dayOfMonth);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return salesChartJsBar;
  }

  private void createBarChartJsModel(org.primefaces.model.charts.bar.BarChartModel salesChartJsBar, String month, List<Dashboard> salesList, List<Dashboard> purchaseList, List<Dashboard> salesServiceList, boolean dayOfMonth) {
    // SalesPurchase salesPurchase = new SalesPurchase();
    Map<String, SalesPurchase> map = new LinkedMap<>();
    if (salesList != null) {
      salesList.forEach((Dashboard dash) -> {
        if (map.containsKey((dayOfMonth == true) ? dash.getDayOfMonth() : dash.getYearMonth())) {
          map.put((dayOfMonth == true) ? dash.getDayOfMonth() : dash.getYearMonth(), new SalesPurchase(map.get((dayOfMonth == true) ? dash.getDayOfMonth() : dash.getYearMonth()).getSale() + dash.getAmount(), 0.0));
        } else {
          map.put((dayOfMonth == true) ? dash.getDayOfMonth() : dash.getYearMonth(), new SalesPurchase(dash.getAmount(), 0.0));
        }
      });
    }

    if (purchaseList != null) {
      purchaseList.forEach((Dashboard dash) -> {
        if (map.containsKey((dayOfMonth == true) ? dash.getDayOfMonth() : dash.getYearMonth())) {
          map.put((dayOfMonth == true) ? dash.getDayOfMonth() : dash.getYearMonth(), new SalesPurchase(map.get((dayOfMonth == true) ? dash.getDayOfMonth() : dash.getYearMonth()).getSale(), map.get((dayOfMonth == true) ? dash.getDayOfMonth() : dash.getYearMonth()).getPurchase() + dash.getAmount()));
        } else {
          map.put((dayOfMonth == true) ? dash.getDayOfMonth() : dash.getYearMonth(), new SalesPurchase(0.0, dash.getAmount()));
        }
      });

    }
    if (salesServiceList != null) {

      salesServiceList.forEach((Dashboard dash) -> {
        if (map.containsKey(dash.getYearMonth())) {
          if (map.get(dayOfMonth ? dash.getDayOfMonth() : dash.getYearMonth()).getSalesService() != null) {
            map.put(dayOfMonth ? dash.getDayOfMonth() : dash.getYearMonth(), new SalesPurchase(map.get(dayOfMonth ? dash.getDayOfMonth() : dash.getYearMonth()).getSale(), map.get(dayOfMonth ? dash.getDayOfMonth() : dash.getYearMonth()).getPurchase(), map.get(dayOfMonth ? dash.getDayOfMonth() : dash.getYearMonth()).getSalesService() + dash.getAmount()));
          } else {
            map.put(dayOfMonth ? dash.getDayOfMonth() : dash.getYearMonth(), new SalesPurchase(map.get(dayOfMonth ? dash.getDayOfMonth() : dash.getYearMonth()).getSale(), map.get(dayOfMonth ? dash.getDayOfMonth() : dash.getYearMonth()).getPurchase(), dash.getAmount()));
          }
        } else {
          map.put(dayOfMonth ? dash.getDayOfMonth() : dash.getYearMonth(), new SalesPurchase(0.0, 0.0, dash.getAmount()));
        }
      });

    }

    ChartData data = new ChartData();
    List<Number> sales = new ArrayList<>();
    List<Number> purchase = new ArrayList<>();
    List<Number> salesService = new ArrayList<>();
    List<String> labels = new ArrayList<>();

    for (Map.Entry<String, SalesPurchase> m : map.entrySet()) {
      sales.add(m.getValue().getSale());
      purchase.add(m.getValue().getPurchase());
      salesService.add(m.getValue().getSalesService());
      labels.add(m.getKey());
    }

    BarChartDataSet salesBar = new BarChartDataSet();
    salesBar.setLabel("Sales");
    salesBar.setBackgroundColor("rgba(66,165,245, 0.4)");
    salesBar.setBorderColor("rgb(66,165,245)");
    salesBar.setBorderWidth(1);
    salesBar.setData(sales);

    BarChartDataSet purchaseBar = new BarChartDataSet();
    purchaseBar.setBackgroundColor("rgba(126,87,194, 0.4)");
    purchaseBar.setBorderColor("rgb(126,87,194)");
    purchaseBar.setBorderWidth(1);
    purchaseBar.setLabel("Purchase");
    purchaseBar.setData(purchase);

    BarChartDataSet salesServiceBar = new BarChartDataSet();
    salesServiceBar.setBackgroundColor("rgba(102,102,255, 0.4)");
    salesServiceBar.setBorderColor("rgb(102,102,255)");
    salesServiceBar.setBorderWidth(1);
    salesServiceBar.setLabel("Sales Service Invoice");
    salesServiceBar.setData(salesService);

    if (Arrays.stream(selectedChartItems).anyMatch(ChartType.SALES_INVOICE.getType()::equals)) {
      data.addChartDataSet(salesBar);
    }
    if (Arrays.stream(selectedChartItems).anyMatch(ChartType.PURCHASE.getType()::equals)) {
      data.addChartDataSet(purchaseBar);
    }
    if (Arrays.stream(selectedChartItems).anyMatch(ChartType.SALES_SERVICE.getType()::equals)) {
      data.addChartDataSet(salesServiceBar);
    }
    data.setLabels(labels);
    salesChartJsBar.setData(data);

    //Options
    BarChartOptions options = new BarChartOptions();
    CartesianScales cScales = new CartesianScales();
    CartesianLinearAxes linearAxes = new CartesianLinearAxes();
    CartesianLinearTicks ticks = new CartesianLinearTicks();
    ticks.setBeginAtZero(true);
    linearAxes.setTicks(ticks);
    cScales.addYAxesData(linearAxes);
    options.setScales(cScales);

    Title title = new Title();
    title.setDisplay(true);
//    title.setText("Bar Chart");
    options.setTitle(title);

    salesChartJsBar.setOptions(options);
  }

  public void backOrNext(int pos) {
    filterParameters.setFromDate(DateUtil.moveMonths(filterParameters.getFromDate(), pos));

  }

  public FilterParameters getFilterParameters() {
    if (filterParameters == null) {
      filterParameters = new FilterParameters();
      filterParameters.setToDate(new Date());
      filterParameters.setFromDate(DateUtil.moveDays(DateUtil.moveMonths(filterParameters.getToDate(), -12), -1));
      filterParameters.setFilterType(SystemConstants.CUSTOM_DATE);
    }
    return filterParameters;
  }

  public void setFilterParameters(FilterParameters filterParameters) {
    this.filterParameters = filterParameters;
  }

  public String getDateType() {
    if (StringUtil.isEmpty(dateType)) {
      setDateType(SystemConstants.CURRENT_YEAR);
    }
    return dateType;
  }

  public void setDateType(String dateType) {
    this.dateType = dateType;
  }

  public boolean isCalendar() {
    return calendar;
  }

  public String getCurrency() {
    if(UserRuntimeView.instance().getCompany() != null)
    return UserRuntimeView.instance().getCompany().getCurrencyId().getSymbol();
    return null;
  }

  public void createBox(MainView main) {
    try {
      createInitialDates();
      salesBox = CompanyDashboardService.getSalesBox(main, filterParameters);
      purchaseBox = CompanyDashboardService.getPurchaseBox(main, filterParameters);
      outstandingBox = CompanyDashboardService.getOutstandingBox(main, filterParameters);
      topCustomerOutstandingList = CompanyDashboardService.selectTopOutstanding(main, filterParameters, 5, AccountingConstant.ACC_ENTITY_CUSTOMER.getId());
      topSupplierOutstandingList = CompanyDashboardService.selectTopOutstanding(main, filterParameters, 5, AccountingConstant.ACC_ENTITY_VENDOR.getId());
      chequeReview = CompanyDashboardService.selectChequeBoxItems(main, UserRuntimeView.instance().getCompany(), 1);
      chequeIssued = CompanyDashboardService.selectChequeBoxItems(main, UserRuntimeView.instance().getCompany(), 0);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void createInitialDates() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
    getFilterParameters().setFirstDayOfweek(new Date(cal.getTimeInMillis()));
    cal.set(Calendar.DAY_OF_MONTH, 1);
    getFilterParameters().setFirstDayOfMonth(new Date(cal.getTimeInMillis()));
  }

  public Dashboard getSalesBox() {
    return salesBox;
  }

  public Dashboard getPurchaseBox() {
    return purchaseBox;
  }

  public Dashboard getOutstandingBox() {
    return outstandingBox;
  }

  public void setFilterDate() {
    if (getFilterParameters().getFilterType() != null) {
      if (filterParameters.getFilterType().equals(SystemConstants.TODAY)) {
        filterParameters.setFromDate(new Date());
        filterParameters.setToDate(new Date());
        navbtn = false;
        calendar = true;
      } else if (filterParameters.getFilterType().equals(SystemConstants.YESTERDAY)) {
        filterParameters.setFromDate(DateUtil.moveDays(new Date(), -1));
        filterParameters.setToDate((DateUtil.moveDays(new Date(), -1)));
        navbtn = false;
        calendar = true;
      } else if (filterParameters.getFilterType().equals(SystemConstants.LAST_7DAYS)) {
        filterParameters.setFromDate(DateUtil.moveDays(new Date(), -6));
        filterParameters.setToDate(new Date());
        navbtn = false;
        calendar = true;
      } else if (filterParameters.getFilterType().equals(SystemConstants.LAST_MONTH)) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DATE, 1);
        filterParameters.setFromDate(new Date(cal.getTimeInMillis()));
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        filterParameters.setToDate(new Date(cal.getTimeInMillis()));
        navbtn = false;
        calendar = true;
      } else if (filterParameters.getFilterType().equals(SystemConstants.CURRENT_MONTH)) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, 1);
        filterParameters.setFromDate(new Date(cal.getTimeInMillis()));
        filterParameters.setToDate(new Date());
        navbtn = false;
        calendar = true;
      } else if (filterParameters.getFilterType().equals(SystemConstants.CURRENT_YEAR)) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 0);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, 1);
        filterParameters.setFromDate(new Date(cal.getTimeInMillis()));
        filterParameters.setToDate(new Date());
        navbtn = false;
        calendar = true;
      } else if (filterParameters.getFilterType().equals(SystemConstants.PREVIOUS_YEAR)) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, 1);
        filterParameters.setFromDate(new Date(cal.getTimeInMillis()));
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        filterParameters.setToDate(new Date(cal.getTimeInMillis()));
        navbtn = false;
        calendar = true;
      } else if (filterParameters.getFilterType().equals(SystemConstants.CURRENT_FINANCIAL_YEAR)) {
        getFilterParameters().setFromDate(UserRuntimeView.instance().getMinEntryDate());
        getFilterParameters().setToDate(UserRuntimeView.instance().getMaxEntryDate());
        navbtn = false;
        calendar = true;
      } else if (filterParameters.getFilterType().equals(SystemConstants.PREVIOUS_FINANCIAL_YEAR)) {
        getFilterParameters().setFromDate(DateUtil.moveMonths(UserRuntimeView.instance().getMinEntryDate(), -12));
        getFilterParameters().setToDate(DateUtil.moveDays(UserRuntimeView.instance().getMinEntryDate(), -1));
        navbtn = false;
        calendar = true;
      } else if (filterParameters.getFilterType().equals(SystemConstants.CUSTOM_DATE)) {
        navbtn = true;
        calendar = false;
      }
    }

  }

  public String getPath(String url) {
    return AppConfig.contextName + url;
  }

  public String getPath(String url, Integer inOrOut) {
    accountingMainView.setChequeInOrOut(inOrOut);
    return AppConfig.contextName + url;
  }

  public String getPath(String url, String type, Integer inOrOut) {
    accountingMainView.setChequeReviewType(type);
    accountingMainView.setChequeInOrOut(inOrOut);
    return AppConfig.contextName + url;
  }

  public List<Dashboard> getTopCustomerOutstandingList() {
    return topCustomerOutstandingList;
  }

  public List<Dashboard> getTopSupplierOutstandingList() {
    return topSupplierOutstandingList;
  }

  public boolean isNavbtn() {
    return navbtn;
  }

  public List<String> getChartItems() {
    chartItems = new ArrayList<>();
    for (ChartType type : ChartType.values()) {
      chartItems.add(type.getType());
    }
    return chartItems;
  }

  public void setChartItems(List<String> chartItems) {
    this.chartItems = chartItems;
  }

  public String[] getSelectedChartItems() {
    return selectedChartItems;
  }

  public void setSelectedChartItems(String[] selectedChartItems) {
    this.selectedChartItems = selectedChartItems;
  }

  public void applyColumnFilter(AjaxBehaviorEvent event) {

  }

  private void setBarTypes() {
    List<String> types = new ArrayList<>();
    if (StringUtil.isEmpty(selectedChartItems)) {
      for (ChartType type : ChartType.values()) {
        types.add(type.getType());
      }
      selectedChartItems = types.toArray(new String[types.size()]);
    }
  }

  public ChequeReview getChequeReview() {
    return chequeReview;
  }

  public ChequeReview getChequeIssued() {
    return chequeIssued;
  }

}
