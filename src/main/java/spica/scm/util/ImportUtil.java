/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.util;

import java.util.Date;
import spica.scm.domain.Brand;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.Import;
import spica.scm.domain.Manufacture;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.ProductClassification;
import spica.scm.domain.ProductPackingDetail;
import spica.scm.domain.ProductType;
import spica.scm.domain.ProductUnit;
import spica.scm.domain.Vendor;
import spica.scm.service.BrandService;
import spica.scm.service.CommodityService;
import spica.scm.service.ManufactureService;
import spica.scm.service.ProductBatchService;
import spica.scm.service.ProductCategoryService;
import spica.scm.service.ProductClassificationService;
import spica.scm.service.ProductPackingDetailService;
import spica.scm.service.ProductService;
import spica.scm.service.ProductTypeService;
import spica.scm.service.ProductUnitService;
import spica.scm.service.VendorService;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
public class ImportUtil {

  public static final Import selectByPk(Main main, Import importSales) {
    return (Import) AppService.find(main, Import.class, importSales.getId());
  }

  public static void insertOrUpdateImport(Main main, Import imports) {
    if (imports.getId() != null) {
      AppService.update(main, imports);
    } else {
      AppService.insert(main, imports);
    }
  }

  public static final Product getProductId(Main main, String productName, Integer companyId) throws UserMessageException {
    return (Product) AppService.single(main, Product.class, "select * from scm_product where upper(REPLACE(product_name, ' ', ''))=? and company_id=?",
            new Object[]{StringUtil.toUpperCase(removeWhiteSpace(productName)), companyId});
  }

  public static final ProductBatch getBatchId(Main main, String batchNo, Integer productId, Double valueMrp, Integer companyId) throws UserMessageException {
    return (ProductBatch) AppService.single(main, ProductBatch.class, "select * from scm_product_batch where upper(TRIM(batch_no))=? and product_id=? and value_mrp=? "
            + "and (manufacture_id is null or manufacture_id in(select id from scm_manufacture where company_id=19)) limit 1",
            new Object[]{StringUtil.toUpperCase(batchNo.trim()), productId, valueMrp, companyId});
  }

  public static Object[] getParam(String param1, Integer param2) {
    return new Object[]{StringUtil.toUpperCase(StringUtil.trim(param1 == null ? "" : param1)), param2};
  }

  public static Object[] getParam(String param1, Integer param2, Double param3) {
    return new Object[]{StringUtil.toUpperCase(StringUtil.trim(param1 == null ? "" : param1)), param2, param3};
  }

  public static Object[] getParam(String param1, Integer param2, Double param3, Date param4) {
    return new Object[]{StringUtil.toUpperCase(StringUtil.trim(param1 == null ? "" : param1)), param2, param3,
      param4 != null ? SystemRuntimeConfig.SDF_YYYY_MM_DD.format(param4) : null};
  }

  //FIXME these two methods can return multiple reocords alle
  public static CompanyAddress getCompanyAddress(Main main, Company company) {
    return (CompanyAddress) AppService.single(main, CompanyAddress.class, "select * from scm_company_address where company_id=?", new Object[]{company.getId()});
  }

  public static CustomerAddress getCustomerAddress(Main main, Customer customer) {
    return (CustomerAddress) AppService.single(main, CustomerAddress.class, "select * from scm_customer_address where customer_id = ?", new Object[]{customer.getId()});
  }

  public static Product getProductbyId(Main main, String id) {
    if (id != null) {
      return ProductService.selectByPk(main, new Product(Integer.valueOf(id)));
    }
    return null;
  }

  public static Brand getBrandbyId(Main main, String id) {
    if (id != null) {
      return BrandService.selectByPk(main, new Brand(Integer.valueOf(id)));
    }
    return null;
  }

  public static Vendor getVendorbyId(Main main, String id) {
    if (id != null) {
      return VendorService.selectByPk(main, new Vendor(Integer.valueOf(id)));
    }
    return null;
  }

  public static ServiceCommodity getCommoditybyId(Main main, String id) {
    if (id != null) {
      return CommodityService.selectByPk(main, new ServiceCommodity(Integer.valueOf(id)));
    }
    return null;
  }

  public static ProductCategory getProductCategorybyId(Main main, String id) {
    if (id != null) {
      return ProductCategoryService.selectByPk(main, new ProductCategory(Integer.valueOf(id)));
    }
    return null;
  }

  public static ProductType getProductTypebyId(Main main, String id) {
    if (id != null) {
      return ProductTypeService.selectByPk(main, new ProductType(Integer.valueOf(id)));
    }
    return null;
  }

  public static Manufacture getmanufacturebyId(Main main, String id) {
    if (id != null) {
      return ManufactureService.selectByPk(main, new Manufacture(Integer.valueOf(id)));
    }
    return null;
  }

  public static ProductClassification getProductClassificationbyId(Main main, String id) {
    if (id != null) {
      return ProductClassificationService.selectByPk(main, new ProductClassification(Integer.valueOf(id)));
    }
    return null;
  }

  public static ProductUnit getProductUnitbyId(Main main, String id) {
    if (id != null) {
      return ProductUnitService.selectByPk(main, new ProductUnit(Integer.valueOf(id)));
    }
    return null;
  }

  public static ProductPackingDetail getProductPackingDetailbyId(Main main, String id) {
    if (id != null) {
      return ProductPackingDetailService.selectByPk(main, new ProductPackingDetail(Integer.valueOf(id)));
    }
    return null;
  }

  public static ProductBatch getProductBatchbyId(Main main, String id) {
    if (id != null) {
      return ProductBatchService.selectByPk(main, new ProductBatch(Integer.valueOf(id)));
    }
    return null;
  }

  public static final Product getProductIdWithPacking(Main main, String productName, Integer companyId) throws UserMessageException {
    return (Product) AppService.single(main, Product.class, "select * from scm_product where (upper(CONCAT(product_name,' ',COALESCE(packing_description,'')))=? OR upper(product_name)=?) "
            + "and company_id=?",
            new Object[]{StringUtil.toUpperCase(productName), StringUtil.toUpperCase(productName), companyId});
  }

  public static String removeWhiteSpace(String str) {
    if (str != null) {
      str = str.replaceAll(" ", "");
    }
    return str;
  }
}
