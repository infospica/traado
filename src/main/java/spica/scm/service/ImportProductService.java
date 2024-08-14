/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.List;
import spica.scm.domain.Account;
import spica.scm.domain.Brand;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Company;
import spica.scm.domain.Contract;
import spica.scm.domain.ImportProduct;
import spica.scm.domain.Manufacture;
import spica.scm.domain.Product;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.ProductClassification;
import spica.scm.domain.ProductPackingDetail;
import spica.scm.domain.ProductPreset;
import spica.scm.domain.ProductStatus;
import spica.scm.domain.ProductType;
import spica.scm.domain.ProductUnit;
import spica.scm.domain.Vendor;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
public class ImportProductService {

  public static void insertOrUpdate(Main main, Company companyId) throws Exception {
    List<ImportProduct> importProductList = selectProductLogList(main, companyId, UserRuntimeView.instance().getAppUser().getName(), SystemConstants.IMPORT_SUCCESS);
    for (ImportProduct importProduct : importProductList) {
      Product product = new Product();
      if (validateProduct(main, importProduct, product, companyId)) {
        product = getProduct(importProduct, product, companyId);
        if (!isExist(main, importProduct, companyId.getId())) {
          ProductService.insertOrUpdate(main, product);
          saveManufacture(main, importProduct, product);
          saveProductPreset(main, importProduct, product);
        }
      }
    }
  }

  public static void insertOrUpdateProductImport(Main main, ImportProduct importProduct) throws Exception {
    if (importProduct.getId() == null) {
      AppService.insert(main, importProduct);
    } else {
      AppService.update(main, importProduct);
    }
  }

  public static boolean validateProduct(Main main, ImportProduct importProduct, Product product, Company company) {
    Integer vendorId = null;
    if (product == null) {
      product = new Product();
    }
    String error = "";
    boolean validate = true;
    if (isExist(main, importProduct, company.getId())) {
      error = "Duplicate product ";
      validate = false;
    } else {
      if (importProduct.getSupplierName() == null) {
        error += "Empty supplier name ";
        validate = false;
      } else {
        Vendor vendor = (Vendor) AppService.single(main, Vendor.class, "select id from scm_vendor where UPPER(vendor_name)=? AND company_id=?",
                getParam(importProduct.getSupplierName(), company.getId()));
        if (vendor == null) {
          error += (error.isEmpty() ? "" : ",") + "Supplier not found ";
          validate = false;
        } else {
          vendorId = vendor.getId();
        }
      }
      if (importProduct.getCommodity() == null) {
        error += (error.isEmpty() ? "" : ", ") + "Empty commodity ";
        validate = false;
      } else {
        ServiceCommodity commodity = null;
        if (vendorId != null) {
          commodity = (ServiceCommodity) AppService.single(main, ServiceCommodity.class, "select id from scm_service_commodity where UPPER(title)=? AND (company_id is null or company_id=?) \n"
                  + "and id in (select commodity_id from scm_vendor_commodity where vendor_id=?)",
                  getParam(importProduct.getCommodity(), company.getId(), vendorId));
        }
        product.setCommodityId(commodity);
        if (commodity == null) {
          error += (error.isEmpty() ? "" : ",") + "Commodity not found";
          validate = false;
        }
      }
      if (importProduct.getProductCategory() == null) {
        error += (error.isEmpty() ? "" : ",") + "Empty product category ";
        validate = false;
      } else {
        ProductCategory prodCat = null;
        if (product.getCommodityId() != null) {
          prodCat = (ProductCategory) AppService.single(main, ProductCategory.class, "select id from scm_product_category where UPPER(title)=? and commodity_id=?", getParam(importProduct.getProductCategory(), product.getCommodityId().getId()));
        }
        product.setProductCategoryId(prodCat);
        if (prodCat == null) {
          error += (error.isEmpty() ? "" : ", ") + "Product category not found";
          importProduct.setProductCategory(null);
          validate = false;
        }
      }
      if (importProduct.getProductType() == null) {
        error += (error.isEmpty() ? "" : ", ") + "Empty product type";
        validate = false;
      } else {
        ProductType type = null;
        if (product.getCommodityId() != null) {
          type = (ProductType) AppService.single(main, ProductType.class, "select id from scm_product_type where UPPER(title)=? and commodity_id=?", getParam(importProduct.getProductType(), product.getCommodityId().getId()));
        }
        product.setProductTypeId(type);
        if (type == null) {
          error += (error.isEmpty() ? "" : ", ") + "Product type not found";
          importProduct.setProductType(null);
          validate = false;
        }
      }
      if (importProduct.getProductUnit() == null) {
        error += (error.isEmpty() ? "" : ", ") + "Empty product unit";
        validate = false;
      } else {
        ProductUnit productUnit = (ProductUnit) AppService.single(main, ProductUnit.class, "select id from scm_product_unit where UPPER(title)=?", getParam(importProduct.getProductUnit()));
        product.setProductUnitId(productUnit);
        if (productUnit == null) {
          error += (error.isEmpty() ? "" : ", ") + "Product unit not found";
          importProduct.setProductUnit(null);
          validate = false;
        }
      }
      if (importProduct.getProductClassification() != null) {
        ProductClassification classification = null;
        classification = (ProductClassification) AppService.single(main, ProductClassification.class, "select id from scm_product_classification where UPPER(title)=? and company_id=?",
                getParam(importProduct.getProductClassification(), product.getCompanyId().getId()));
        importProduct.setProductClassification(classification == null ? null : classification.getTitle());
        product.setProductClassificationId(classification);
      }
      if (importProduct.getBrand() == null) {
        error += (error.isEmpty() ? "" : ", ") + "Empty brand";
        validate = false;
      } else {
        Brand brand = null;
        if (vendorId != null) {
          brand = (Brand) AppService.single(main, Brand.class, "select id from scm_brand where UPPER(code)=? and company_id =? \n"
                  + "and id in (select brand_id from scm_supplier_brand where vendor_id = ?)", getParam(importProduct.getBrand(), company.getId(), vendorId));
        }
        product.setBrandId(brand);
        if (brand == null) {
          error += (error.isEmpty() ? "" : ", ") + "Brand not found";
          importProduct.setBrand(null);
          validate = false;
        }
      }
      if (importProduct.getHsnCode() == null) {
        error += (error.isEmpty() ? "" : ", ") + "Empty HSN Code";
        validate = false;
      }
      if (importProduct.getPackSize() == null) {
        error += (error.isEmpty() ? "" : ", ") + "Empty Pack Size";
        validate = false;
      }
      if (importProduct.getProductPackingDetail() == null) {
        error += (error.isEmpty() ? "" : ", ") + "Empty Packing Detail";
        validate = false;
      } else {
        List<ProductPackingDetail> detailList = AppService.list(main, ProductPackingDetail.class, "select * from scm_product_packing_detail where UPPER(title)=?",
                getParam(importProduct.getProductPackingDetail()));
        ProductPackingDetail detail = detailList.isEmpty() ? null : detailList.get(0);
        product.setProductPackingDetailId(detail);
        if (detail == null) {
          error += (error.isEmpty() ? "" : ", ") + "Packing Detail not found";
          importProduct.setProductPackingDetail(null);
          validate = false;
        }
      }
      if (importProduct.getManufacturer() == null) {
        error += (error.isEmpty() ? "" : ", ") + "Empty Manufacture";
      } else {
        Manufacture manufacture = (Manufacture) AppService.single(main, Manufacture.class, "select id from scm_manufacture where UPPER(code)=? and company_id =?",
                getParam(importProduct.getManufacturer(), company.getId()));
        if (manufacture == null) {
          error += (error.isEmpty() ? "" : ", ") + "Manufacture not found";
          importProduct.setManufacturer(null);
          validate = false;
        }
      }
    }
    if (!validate) {
      importProduct.setStatus(SystemConstants.IMPORT_FAILED);
    } else {
      importProduct.setStatus(SystemConstants.IMPORT_SUCCESS);
    }
    importProduct.setError(error);
    AppService.update(main, importProduct);
    main.em().flush();
    return validate;

  }

  private static Product getProduct(ImportProduct importProduct, Product product, Company company) {
    product.setCompanyId(company);
    product.setProductName(importProduct.getProductName());
    product.setProductStatusId(new ProductStatus(SystemConstants.PRODUCT_STATUS_ACTIVE));
    product.setPackSize(importProduct.getPackSize());
    product.setHsnCode(importProduct.getHsnCode());
    product.setExpirySalesDays(importProduct.getExpirySalesDays());
    product.setPackingDescription(importProduct.getPackingDescription());

    return product;
  }

  public static Account getAccount(Main main, Company company, String vendor) {
    String sql = "select scm_account.id from scm_account left join scm_vendor on scm_vendor.id=scm_account.vendor_id where UPPER(vendor_name)=? and scm_vendor.company_id=?";
    return (Account) AppService.single(main, Account.class, sql, new Object[]{vendor.toUpperCase(), company.getId()});
  }

  public static List<ImportProduct> selectSupplierList(Main main, Integer companyId) {
    String sql = "select t1.id as list_id,t1.vendor_name as supplier_name,t3.ptr_margin_percent as ptr_margin_percentage,t3.pts_margin_percent as pts_margin_percentage,\n"
            + "t3.margin_percentage from scm_vendor t1 "
            + "left join scm_account t2 on t1.id=t2.vendor_id \n"
            + "left join scm_contract t3 on t2.id=t3.account_id where t1.company_id=?";
    return AppDb.getList(main.dbConnector(), ImportProduct.class, sql, new Object[]{companyId});
  }

  public static List<ImportProduct> selectProductCategoryList(Main main) {
    String sql = "select t1.id as list_id, t1.title, t2.title as commodity from scm_product_category t1,scm_service_commodity t2 where t2.id=t1.commodity_id order by commodity";
    return AppDb.getList(main.dbConnector(), ImportProduct.class, sql, new Object[]{});
  }

  public static List<ImportProduct> selectProductTypeList(Main main) {
    String sql = "select id as list_id, title from scm_product_type";
    return AppDb.getList(main.dbConnector(), ImportProduct.class, sql, new Object[]{});
  }

  public static List<ImportProduct> selectCommodityList(Main main, Integer companyId) {
    String sql = "select t1.id as list_id, t1.title, t3.vendor_name as supplier_name, t3.id from scm_service_commodity t1 left join scm_vendor_commodity t2 on t1.id=t2.commodity_id\n"
            + " left join scm_vendor t3 on t2.vendor_id=t3.id where (t1.company_id=? or t1.company_id is null) and (t3.vendor_name is not null) order by supplier_name asc";
    return AppDb.getList(main.dbConnector(), ImportProduct.class, sql, new Object[]{companyId});
  }

  public static List<ImportProduct> selectProductUnitList(Main main) {
    String sql = "select id as list_id, title from scm_product_unit";
    return AppDb.getList(main.dbConnector(), ImportProduct.class, sql, new Object[]{});
  }

  public static List<ImportProduct> selectClassificationList(Main main) {
    String sql = "select t1.id as list_id, t1.title, t2.title as commodity from scm_product_classification t1,scm_service_commodity t2 where t2.id=t1.commodity_id order by commodity";
    return AppDb.getList(main.dbConnector(), ImportProduct.class, sql, new Object[]{});
  }

  public static List<ImportProduct> selectBrandList(Main main, Integer companyId) {
    String sql = "select t1.id as list_id, t1.name as title,t1.code,t3.vendor_name as supplier_name from scm_brand t1 left join scm_supplier_brand t2 on t1.id=t2.brand_id \n"
            + "left join scm_vendor t3 on t3.id=t2.vendor_id where t1.company_id=?";
    return AppDb.getList(main.dbConnector(), ImportProduct.class, sql, new Object[]{companyId});
  }

  public static List<ImportProduct> selectPackingDetailList(Main main) {
    String sql = "select id as list_id, title from scm_product_packing_detail";
    return AppDb.getList(main.dbConnector(), ImportProduct.class, sql, new Object[]{});
  }

  public static List<ImportProduct> selectManufactureList(Main main, Integer companyId) {
    String sql = "select id as list_id,name as title,code from scm_manufacture where company_id=?";
    return AppDb.getList(main.dbConnector(), ImportProduct.class, sql, new Object[]{companyId});
  }

  public static List<ImportProduct> selectAllProductLogList(Main main, Company company, String user) {
    return AppDb.getList(main.dbConnector(), ImportProduct.class, "select * from scm_product_import where company_id=? and created_by=? order by line_no asc",
            new Object[]{company.getId(), user});
  }

  public static List<ImportProduct> selectProductLogList(Main main, Company company, String user, Integer status) {
    return AppDb.getList(main.dbConnector(), ImportProduct.class, "select * from scm_product_import where status=? and company_id=? and created_by=? order by line_no asc",
            new Object[]{status, company.getId(), user});
  }

  public static void importProduct(Main main, List<ImportProduct> importProductList) {
    if (importProductList != null) {
      for (ImportProduct product : importProductList) {
        AppService.insert(main, product);
      }
    }
  }

  public static Integer getSupplierId(Main main, Company company, String supplierName) {
    Vendor vendor = (Vendor) AppService.single(main, Vendor.class, "select id from scm_vendor where UPPER(vendor_name)=? and (company_id=? or company_id is null)",
            getParam(supplierName, company.getId()));
    if (vendor != null) {
      return vendor.getId();
    }
    return null;
  }

  public static Integer getCommodityId(Main main, Company company, String commodityName) {
    ServiceCommodity commodity = (ServiceCommodity) AppService.single(main, ServiceCommodity.class, "select id from scm_service_commodity where UPPER(title)=? and (company_id=? or company_id is null)",
            getParam(commodityName, company.getId()));
    if (commodity != null) {
      return commodity.getId();
    }
    return null;
  }

  public static void deleteProductImport(Main main, Company company, String user) {
    AppService.deleteSql(main, ImportProduct.class, "delete from scm_product_import where company_id=? and created_by=?", new Object[]{company.getId(), user});
  }

  public static final List<Vendor> supplierAuto(Main main, Company company, String filter) {
    return AppService.list(main, Vendor.class, "select id, vendor_name from scm_vendor where upper(vendor_name) like ? AND (company_id is null or company_id=?) order by upper(vendor_name) asc",
            new Object[]{"%" + filter.toUpperCase() + "%", company.getId()});
  }

  public static final List<ServiceCommodity> commodityAuto(Main main, Company company, String filter, Integer vendorId) {
    return AppService.list(main, ServiceCommodity.class, "select id, title from scm_service_commodity where upper(title) like ? AND (company_id is null or company_id=?) \n"
            + "and id in (select commodity_id from scm_vendor_commodity where vendor_id =?) order by upper(title) asc",
            new Object[]{"%" + filter.toUpperCase() + "%", company.getId(), vendorId});
  }

  public static final List<ProductCategory> productCategoryByCommodityAuto(Main main, Integer commodityId, String filter) {
    return AppService.list(main, ServiceCommodity.class, "select id, title from scm_product_category where upper(title) like ? AND commodity_id=? order by upper(title) asc",
            new Object[]{"%" + filter.toUpperCase() + "%", commodityId});
  }

  public static final List<Brand> brandByCompanyAuto(Main main, String filter, Company company, Integer vendorId) {
    return AppService.list(main, Brand.class, "select id, name, code from scm_brand where company_id = ? and upper(code) like ? \n"
            + "and id in (select brand_id from scm_supplier_brand where vendor_id =?)order by upper(code) asc", new Object[]{company.getId(), "%" + filter.toUpperCase() + "%", vendorId});
  }

  public static final List<ProductPackingDetail> productPackingDetailAuto(Main main, String filter) {
    return AppService.list(main, ProductPackingDetail.class, "select id, title from scm_product_packing_detail where upper(title) like ? order by upper(title) asc", new Object[]{"%" + filter.toUpperCase() + "%"});
  }

  public static List<ProductClassification> productClassificationAuto(Main main, Integer companyId, String filter) {
    return AppService.list(main, ProductClassification.class, "select id,title from scm_product_classification where upper(title) like ? and company_id=? order by upper(title) asc",
            new Object[]{"%" + filter.toUpperCase() + "%", companyId});
  }

  public static final void deleteByPk(Main main, ImportProduct importProduct) {
    AppService.delete(main, ImportProduct.class, importProduct.getId());
  }

  public static final boolean isExist(Main main, ImportProduct importProduct, Integer companyId) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_product where upper(product_name)=? and company_id=?", new Object[]{StringUtil.toUpperCase(importProduct.getProductName()), companyId})) {
      return true;
    }
    return false;
  }

  public static void insertOrUpdate(Main main, ProductPreset productPreset) {
    if (productPreset.getId() == null) {
      AppService.insert(main, productPreset);
    } else {
      AppService.update(main, productPreset);
    }
  }

  private static Object[] getParam(String param1) {
    return getParam(param1, null);
  }

  private static Object[] getParam(String param1, Integer param2) {
    if (param2 != null) {
      return new Object[]{StringUtil.toUpperCase(StringUtil.trim(param1)), param2};
    } else {
      return new Object[]{StringUtil.toUpperCase(StringUtil.trim(param1))};
    }
  }

  private static Object[] getParam(String param1, Integer param2, Integer param3) {
    if (param3 != null) {
      return new Object[]{StringUtil.toUpperCase(StringUtil.trim(param1)), param2, param3};
    } else {
      return getParam(param1, param2);
    }
  }

  private static void saveManufacture(Main main, ImportProduct importProduct, Product product) {
    List<Manufacture> manufactureList = new ArrayList<>();
    Manufacture manufacture = selectManufacture(main, importProduct.getManufacturer());
    manufactureList.add(manufacture);
    ManufactureProductService.insertOrUpdate(main, product, manufactureList);
  }

  private static void saveProductPreset(Main main, ImportProduct importProduct, Product product) {
    Account account = getAccount(main, UserRuntimeView.instance().getCompany(), importProduct.getSupplierId().getVendorName());
    ProductPreset productPreset = new ProductPreset();
    productPreset.setAccountId(account);
    productPreset.setProductId(product);
    Contract contract = selectActiveContract(main, account);
    productPreset.setPtrMarginPercentage(importProduct.getPtrMarginPercentage());
    productPreset.setPtsMarginPercentage(importProduct.getPtsMarginPercentage());
    productPreset.setMarginPercentage(importProduct.getMarginPercentage());
    productPreset.setMrpltePtrRateDerivationCriterion(importProduct.getPtrMarkupOrMarkdown());
    productPreset.setPtrPtsRateDerivationCriterion(importProduct.getPtsMarkupOrMarkdown());
    productPreset.setPtsSsRateDerivationCriterion(importProduct.getMarginMarkupOrMarkdown());
    if (productPreset.getPtrMarginPercentage() == null && contract != null) {
      productPreset.setPtrMarginPercentage(contract.getPtrMarginPercent());
    }
    if (productPreset.getPtsMarginPercentage() == null && contract != null) {
      productPreset.setPtsMarginPercentage(contract.getPtsMarginPercent());
    }
    if (productPreset.getMarginPercentage() == null && contract != null) {
      productPreset.setMarginPercentage(contract.getMarginPercentage());
    }
    ProductPresetService.insert(main, productPreset);
  }

  private static Contract selectActiveContract(Main main, Account account) {
    return (Contract) AppService.single(main, Contract.class, "select * from scm_contract where account_id = ? and contract_status_id=?", new Object[]{account.getId(), 1});
  }

  public static Manufacture selectManufacture(Main main, String obj) {
    return (Manufacture) AppService.single(main, Manufacture.class, "select id from scm_manufacture where UPPER(code)=?", getParam(obj));
  }
}
