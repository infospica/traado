/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.view;

import java.io.Serializable;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import spica.reports.model.CustomerReport;
import spica.reports.model.FilterParameters;
import spica.reports.model.SupplierReport;
import spica.reports.service.MasterReportService;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Company;
import spica.scm.domain.CustomerImporter;
import spica.scm.domain.District;
import spica.scm.domain.Product;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.SalesAgent;
import spica.scm.domain.Territory;
import spica.scm.export.ExcelSheet;
import spica.scm.service.SalesAgentService;
import spica.scm.view.PopUpView;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
@Named(value = "masterReportView")
@ViewScoped
public class MasterReportView implements Serializable {

  private transient List<Product> productReportList;
  private transient List<CustomerReport> customerReportList;
  private transient List<SupplierReport> supplierReportList;
  private transient FilterParameters filterParameters;
  private transient District district;
  private transient Territory territory;
  private transient SalesAgent salesAgent;
  private transient Integer groupStatus;
  // private String fileName;
  private Account account;
  private AccountGroup accountGroup;

  private Company company;
  private List<ServiceCommodity> commodityList;
  private ServiceCommodity commodity;
  private ProductCategory productCategory;
  private List<ProductCategory> productCategoryList;

//  @Inject
//  private PopUpView popUpView;
  public List<Product> getProductReportList(MainView main) {
    if (StringUtil.isEmpty(productReportList) && !main.hasError()) {
      try {
        commodityList = null;
        productCategoryList = null;
        productReportList = MasterReportService.getProductReportList(main, getCompany(), accountGroup, account, commodity, productCategory);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return productReportList;
  }

  public List<CustomerReport> getCustomerReportList(MainView main) {
    if (StringUtil.isEmpty(customerReportList) && !main.hasError()) {
      try {
        customerReportList = MasterReportService.getCustomerReportList(main, getDistrict(), getTerritory(), getSalesAgent(), getGroupStatus());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return customerReportList;
  }

  public FilterParameters getFilterParameters() {
    if (filterParameters == null) {
      filterParameters = new FilterParameters();
    }
    return filterParameters;
  }

  public void setFilterParameters(FilterParameters filterParameters) {
    this.filterParameters = filterParameters;
  }

  public List getSupplierReportList(MainView main) {
    try {
      supplierReportList = MasterReportService.getSupplierList(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return supplierReportList;
  }

  public void reset() {
    productReportList = null;
    customerReportList = null;
  }

  public List<District> districtAuto(String filter) {
    List<District> list = null;
    MainView main = Jsf.getMain();
    try {
      list = MasterReportService.selectDistrictAuto(main, UserRuntimeView.instance().getCompany().getId(), getTerritory(), filter);
      list.add(0, new District(0, "All District"));
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return list;
  }

  public void selectDistrict(SelectEvent event) {
    District dist = (District) event.getObject();
    customerReportList = null;
    if (dist != null) {
      setDistrict(dist);
    } else {
      setDistrict(null);
    }
  }

  public List<Territory> selectTerritory(MainView main) {
    List<Territory> territoryList = null;
    try {
      territoryList = MasterReportService.selectTerritoryByCompanyAndDistrict(main, UserRuntimeView.instance().getCompany().getId(), getDistrict());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return territoryList;
  }

  public void setDistrict(District district) {
    this.district = district;
  }

  public District getDistrict() {
    return district;
  }

  public Territory getTerritory() {
    return territory;
  }

  public void setTerritory(Territory territory) {
    this.territory = territory;
    customerReportList = null;
  }

  public List<SalesAgent> salesAgentAuto(String filter) {
    MainView main = Jsf.getMain();
    List<SalesAgent> list = null;
    try {
      list = SalesAgentService.salesAgentsAll(main, company, filter);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return list;
  }

  public void selectSalesAgent(SelectEvent event) {
    setSalesAgent((SalesAgent) event.getObject());
    customerReportList = null;
  }

  public SalesAgent getSalesAgent() {
    return salesAgent;
  }

  public void setSalesAgent(SalesAgent salesAgent) {
    this.salesAgent = salesAgent;
  }

  public Integer getGroupStatus() {
    return groupStatus;
  }

  public void setGroupStatus(Integer groupStatus) {
    this.groupStatus = groupStatus;
    customerReportList = null;
  }

  public String getFileName() {
    String file = "";
    if (getSalesAgent() != null) {
      file = "_" + getSalesAgent().getName();
    }
    if (getDistrict() != null) {
      if (file.isEmpty()) {
        file = "_" + getDistrict().getDistrictName();
      } else {
        file += "_" + getDistrict().getDistrictName();
      }
    }
    if (getTerritory() != null) {
      if (file.isEmpty()) {
        file = "_" + getTerritory().getTerritoryName();
      } else {
        file += "_" + getTerritory().getTerritoryName();
      }
    }
    return file;
  }
//
//  public void setFileName(String fileName) {
//    this.fileName = fileName;
//  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public void setProductReportList(List<Product> productReportList) {
    this.productReportList = productReportList;
  }

  public void accountSelectEvent(SelectEvent event) {
    setProductReportList(null);
    setAccountGroup(null);
  }

  public void accountGroupSelectEvent(SelectEvent event) {
    setProductReportList(null);
    setAccount(null);
    setCommodity(null);
    setProductCategory(null);
  }

  public Company getCompany() {
    if (company == null) {
      company = UserRuntimeView.instance().getCompany();
    } else {
      if (company.getId().intValue() != UserRuntimeView.instance().getCompany().getId()) {
        company = UserRuntimeView.instance().getCompany();
        account = null;
        commodityList = null;
        productReportList = null;
        productCategoryList = null;
      }
    }
    return company;
  }

  public List<ServiceCommodity> getCommodityList() {
    if (StringUtil.isEmpty(commodityList)) {
      MainView main = Jsf.getMain();
      try {
        commodityList = MasterReportService.selectCommodityByAccountAndCompany(main, getCompany(), getAccount());
        setCommodity(null);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return commodityList;
  }

  public ServiceCommodity getCommodity() {
    return commodity;
  }

  public void setCommodity(ServiceCommodity commodity) {
    productCategoryList = null;
    this.commodity = commodity;
  }

  public void showProduct(Product product, PopUpView popupView) {
    if (product != null && product.getId() != null) {
      product.setProductMaster(true);
      product.setAcccount(getAccount());
      popupView.showProductMaster(product);
    }
  }

  public ProductCategory getProductCategory() {
    return productCategory;
  }

  public void setProductCategory(ProductCategory productCategory) {
    this.productCategory = productCategory;
  }

  public List<ProductCategory> getProductCategoryList() {
    if (productCategoryList == null) {
      MainView main = Jsf.getMain();
      try {
        productCategoryList = MasterReportService.selectProductCategoryByCompanyAccountCommodity(main, getCompany(), getAccountGroup(), getAccount(), getCommodity());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
      setProductCategory(null);
    }
    return productCategoryList;
  }

  public void exportCustomer(MainView main) {
    try {
      List<CustomerImporter> customerImporterList = MasterReportService.selectCustomerForExport(main, getCompany(), getDistrict());
      ExcelSheet.createCustomerExport(main, customerImporterList, district, false);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public AccountGroup getAccountGroup() {
    return accountGroup;
  }

  public void setAccountGroup(AccountGroup accountGroup) {
    this.accountGroup = accountGroup;
  }

}
