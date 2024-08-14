/*
 * @(#)AccountView.java	1.0 Thu Apr 07 11:31:23 IST 2016
 *
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import spica.reports.model.CompanyCustomerProductwiseSales;
import spica.reports.service.CompanyCustomerProductwiseSalesService;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import spica.reports.model.FilterParameters;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Customer;
import spica.scm.domain.Product;
import spica.scm.view.ScmLookupExtView;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 * AccountView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:23 IST 2016
 */
@Named(value = "companyCustomerProductwiseSalesView")
@ViewScoped
public class CompanyCustomerProductwiseSalesView implements Serializable {

  private FilterParameters filterParameters;

  private transient List<CompanyCustomerProductwiseSales> companyCustomerProductwiseSalesList;
  private Map<String, Double> amountByCustomer;
  private Map<String, Double> taxByCustomer;
  private Map<String, Double> goodsValueByCustomer;
  private Map<String, Double> freeQuantityByCustomer;
  private Map<String, Double> quantityByCustomer;
  private List<Product> selectedProductList;
  private Company company;

  public List<CompanyCustomerProductwiseSales> getCompanyCustomerProductwiseSalesList(MainView main) {
    if (StringUtil.isEmpty(companyCustomerProductwiseSalesList)) {
      try {
        companyCustomerProductwiseSalesList = CompanyCustomerProductwiseSalesService.getCompanyCustomerProductwiseSalesList(main, company, filterParameters, selectedProductList);
        amountByCustomer = companyCustomerProductwiseSalesList.stream().collect(Collectors.groupingBy(CompanyCustomerProductwiseSales::getCustomerName, Collectors.summingDouble(CompanyCustomerProductwiseSales::getNetAmount)));
        taxByCustomer = companyCustomerProductwiseSalesList.stream().collect(Collectors.groupingBy(CompanyCustomerProductwiseSales::getCustomerName, Collectors.summingDouble(CompanyCustomerProductwiseSales::getTax)));
        goodsValueByCustomer = companyCustomerProductwiseSalesList.stream().collect(Collectors.groupingBy(CompanyCustomerProductwiseSales::getCustomerName, Collectors.summingDouble(CompanyCustomerProductwiseSales::getGoodsValue)));
        freeQuantityByCustomer = companyCustomerProductwiseSalesList.stream().collect(Collectors.groupingBy(CompanyCustomerProductwiseSales::getCustomerName, Collectors.summingDouble(CompanyCustomerProductwiseSales::getQtyFree)));
        quantityByCustomer = companyCustomerProductwiseSalesList.stream().collect(Collectors.groupingBy(CompanyCustomerProductwiseSales::getCustomerName, Collectors.summingDouble(CompanyCustomerProductwiseSales::getQty)));
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return companyCustomerProductwiseSalesList;
  }

  public void setCompanyCustomerProductwiseSalesList(List<CompanyCustomerProductwiseSales> companyCustomerProductwiseSalesList) {
    this.companyCustomerProductwiseSalesList = companyCustomerProductwiseSalesList;
  }

  public Double getAmountByCustomer(String customerName) {
    if (amountByCustomer != null) {
      return amountByCustomer.get(customerName);
    } else {
      return null;
    }
  }

  public void backOrNext(int pos) {
    if (wawo.entity.util.DateUtil.moveMonths(getFilterParameters().getFromDate(), pos).compareTo(new Date()) <= 0) {
      getFilterParameters().setFromDate(wawo.entity.util.DateUtil.moveMonths(getFilterParameters().getFromDate(), pos));
    }
  }

  public FilterParameters getFilterParameters() {
    if (filterParameters == null) {
      filterParameters = new FilterParameters();
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
      filterParameters.setFromDate(cal.getTime());
      filterParameters.setToDate(new Date());
    }
    return filterParameters;

  }

  public void setFilterParameters(FilterParameters filterParameters) {
    this.filterParameters = filterParameters;
  }

  public Double getTaxByCustomer(String customerName) {
    if (taxByCustomer != null) {
      return taxByCustomer.get(customerName);
    } else {
      return null;
    }
  }

  public Double getGoodsValueByCustomer(String customerName) {
    if (goodsValueByCustomer != null) {
      return goodsValueByCustomer.get(customerName);
    } else {
      return null;
    }
  }

  public Double getFreeQtyByCustomer(String customerName) {
    if (freeQuantityByCustomer != null) {
      return freeQuantityByCustomer.get(customerName);
    } else {
      return null;
    }
  }

  public Double getQtyByCustomer(String customerName) {
    if (quantityByCustomer != null) {
      return quantityByCustomer.get(customerName);
    } else {
      return null;
    }
  }

  public List<AccountGroup> accountGroupAuto(String filter) {
    return ScmLookupExtView.accountGroupAuto(company, filter);
  }

  public List<Account> accountAuto(String filter) {
    if (filterParameters.getAccountGroup() != null && filterParameters.getAccountGroup().getId() != null) {
      MainView main = Jsf.getMain();
      try {
        return ScmLookupExtView.accountByAccountGroupProfile(main, filterParameters.getAccountGroup(), filter);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public List<Customer> customerAuto(String filter) {
    if (filterParameters.getAccountGroup() != null && filterParameters.getAccountGroup().getId() != null) {
      return ScmLookupExtView.selectCustomerByAccountGroup(filterParameters.getAccountGroup(), filter);
    }
    return null;
  }

  public void onProductSelect(SelectEvent event) {
    Set<Product> tempSet = new HashSet<>();
    List<Product> tempList;
    if (getSelectedProductList().size() > 1) {
      for (Product prod : getSelectedProductList()) {
        tempSet.add(prod);
      }
      tempList = new ArrayList<>(tempSet);
      setSelectedProductList(tempList);
    }
    setCompanyCustomerProductwiseSalesList(null);
  }

  /**
   *
   * @param event
   */
  public void onProductUnSelect(UnselectEvent event) {
    if (getSelectedProductList().size() == 1) {
      getSelectedProductList().clear();
    }
    setCompanyCustomerProductwiseSalesList(null);
  }

  public void reset() {
    setCompanyCustomerProductwiseSalesList(null);
    getCompany();
  }

  public void accountGroupSelectEvent() {
    setCompanyCustomerProductwiseSalesList(null);
    getFilterParameters().setAccount(null);
    getFilterParameters().setCustomer(null);
    setSelectedProductList(null);
  }

  public void accountSelectEvent() {
    setCompanyCustomerProductwiseSalesList(null);
    setSelectedProductList(null);
  }

  public List<Product> getSelectedProductList() {
    return selectedProductList;
  }

  public void setSelectedProductList(List<Product> selectedProductList) {
    this.selectedProductList = selectedProductList;
  }

  public List<Product> getProductList(String filter) {
    if (filterParameters.getAccountGroup() != null) {
      return ScmLookupExtView.productByAccountGroupAuto(filter, company.getId(), filterParameters.getAccountGroup().getId());
    }
    return null;
  }

  public Company getCompany() {
    company = UserRuntimeView.instance().getCompany();
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

}
