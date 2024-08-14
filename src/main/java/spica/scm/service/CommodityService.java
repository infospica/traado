/*
 * @(#)CommodityService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Company;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.ProductType;
import spica.scm.domain.Vendor;
import spica.scm.validate.ValidateUtil;
import spica.sys.UserRuntimeView;
import wawo.app.faces.MainView;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * CommodityService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class CommodityService {

  public static final Integer COMMODITY = 1;
  public static final Integer SERVICE = 2;

  /**
   * Commodity paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCommoditySqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_service_commodity", ServiceCommodity.class, main);
    sql.main("select scm_service_commodity.id,scm_service_commodity.title,scm_service_commodity.sort_order,scm_service_commodity.commodity_code,scm_service_commodity.hsn_sac_code,"
            + "scm_service_commodity.country_id,scm_service_commodity.company_id,scm_service_commodity.purchase_tax_code_id,scm_service_commodity.sales_tax_code_id,"
            + "scm_service_commodity.purchase_cess_tax_code_id,scm_service_commodity.sales_cess_tax_code_id,scm_service_commodity.commodity_or_service, "
            + "scm_service_commodity.created_at,scm_service_commodity.modified_at,scm_service_commodity.created_by,scm_service_commodity.modified_by "
            + "from scm_service_commodity scm_service_commodity "); //Main query
    sql.count("select count(scm_service_commodity.id) as total from scm_service_commodity scm_service_commodity "); //Count query
    sql.join("left outer join scm_company scm_company on (scm_company.id = scm_service_commodity.company_id) "
            + "left outer join scm_country scm_country on (scm_country.id = scm_service_commodity.country_id) "
            + "left outer join scm_tax_code purchase_tax_code on (purchase_tax_code.id = scm_service_commodity.purchase_tax_code_id) "
            + "left outer join scm_tax_code sales_tax_code on (sales_tax_code.id = scm_service_commodity.sales_tax_code_id) "
            + "left outer join scm_tax_code purchase_cess_tax_code on (purchase_cess_tax_code.id = scm_service_commodity.purchase_cess_tax_code_id) "
            + "left outer join scm_tax_code sales_cess_tax_code on (sales_cess_tax_code.id = scm_service_commodity.sales_cess_tax_code_id)");//Join Query

    sql.string(new String[]{"scm_service_commodity.title", "scm_service_commodity.commodity_code", "scm_service_commodity.hsn_sac_code", "scm_service_commodity.created_by", "scm_service_commodity.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_service_commodity.id", "scm_service_commodity.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_service_commodity.created_at", "scm_service_commodity.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Commodity.
   *
   * @param main
   * @param company
   * @param type
   * @return List of Commodity
   */
  public static final List<ServiceCommodity> listPaged(Main main, Company company, Integer type) {
    if (company != null) {
      SqlPage sql = getCommoditySqlPaged(main);
      sql.cond("where scm_service_commodity.commodity_or_service = ? and (scm_service_commodity.company_id = ? or (scm_service_commodity.country_id = ? and scm_service_commodity.company_id is null))");
      sql.param(type);
      sql.param(company.getId());
      sql.param(company.getCountryId().getId());
      return AppService.listPagedJpa(main, sql);
    }
    return null;
    //return AppService.listPagedJpa(main, getCommoditySqlPaged(main));
  }

//  /**
//   * Return list of Commodity based on condition
//   * @param main
//   * @return List<Commodity>
//   */
//  public static final List<Commodity> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCommoditySqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select Commodity by key.
   *
   * @param main
   * @param commodity
   * @return Commodity
   */
  public static final ServiceCommodity selectByPk(Main main, ServiceCommodity commodity) {
    return (ServiceCommodity) AppService.find(main, ServiceCommodity.class, commodity.getId());
  }

  /**
   * Insert Commodity.
   *
   * @param main
   * @param commodity
   */
  public static final void insert(Main main, ServiceCommodity commodity) {
    insertAble(main, commodity);  //Validating
    AppService.insert(main, commodity);

  }

  /**
   * Update Commodity by key.
   *
   * @param main
   * @param commodity
   * @return Commodity
   */
  public static final ServiceCommodity updateByPk(Main main, ServiceCommodity commodity) {
    updateAble(main, commodity); //Validating
    return (ServiceCommodity) AppService.update(main, commodity);
  }

  /**
   * Insert or update Commodity
   *
   * @param main
   * @param commodity
   */
  public static void insertOrUpdate(Main main, ServiceCommodity commodity) {
    if (commodity.getId() == null) {
      if (commodity.getCompanyId() == null) {
        commodity.setCountryId(UserRuntimeView.instance().getCompany().getCountryId());
      } else {
        commodity.setCountryId(commodity.getCompanyId().getCountryId());
      }
      insert(main, commodity);
    } else {
      updateByPk(main, commodity);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param commodity
   */
  public static void clone(Main main, ServiceCommodity commodity) {
    commodity.setId(null); //Set to null for insert
    insert(main, commodity);
  }

  /**
   * Delete Commodity.
   *
   * @param main
   * @param commodity
   */
  public static final void deleteByPk(Main main, ServiceCommodity commodity) {
    deleteAble(main, commodity); //Validation    
    AppService.deleteSql(main, ProductType.class, "delete from scm_product_type where commodity_id = ?", new Object[]{commodity.getId()});
    AppService.deleteSql(main, ProductCategory.class, "delete from scm_product_category where commodity_id=?", new Object[]{commodity.getId()});
//    AppService.deleteSql(main, ProductClassification.class, "delete from scm_product_classification scm_product_classification where scm_product_classification.commodity_id=?", new Object[]{commodity.getId()});
    AppService.delete(main, ServiceCommodity.class, commodity.getId());
  }

  /**
   * Delete Array of Commodity.
   *
   * @param main
   * @param commodity
   */
  public static final void deleteByPkArray(Main main, ServiceCommodity[] commodity) {
    for (ServiceCommodity e : commodity) {
      deleteByPk(main, e);
    }
  }

  public static List<ServiceCommodity> selectByVendor(MainView main, Vendor vendor) {
    List<ServiceCommodity> list = null;
    SqlPage sql = getCommoditySqlPaged(main);
    sql.join("left outer join scm_vendor_commodity scm_vendor_commodity on (scm_service_commodity.id = scm_vendor_commodity.commodity_id)");
    if (vendor.getId() == null) {
      return null;
    }
    sql.cond("where scm_vendor_commodity.vendor_id = ?");
    sql.param(vendor.getId());
    list = AppService.listAllJpa(main, sql);
    if (main.isEdit()) {
      for (ServiceCommodity com : list) {
        com.setPurchased(AppService.exist(main, "select '1' from scm_service_commodity scm_service_commodity "
                + "inner join scm_product scm_product on scm_service_commodity.id = scm_product.commodity_id and scm_service_commodity.id = ? "
                + "inner join scm_product_preset scm_product_preset on scm_product.id = scm_product_preset.product_id "
                + "inner join scm_account scm_account on scm_account.id = scm_product_preset.account_id and scm_account.vendor_id = ? ", new Object[]{com.getId(), vendor.getId()}));
      }
    }
    return list;
  }

  public static List<ServiceCommodity> listPagedNotInVendor(Main main, Vendor vendor) {
    SqlPage sql = getCommoditySqlPaged(main);
    if (vendor.getId() == null) {
      return null;
    }
    sql.cond("where scm_service_commodity.id not in(select scm_vendor_commodity.commodity_id from scm_vendor_commodity scm_vendor_commodity where scm_vendor_commodity.vendor_id =?)");
    sql.param(vendor.getId());
    return AppService.listPagedJpa(main, sql);
  }

  public static ServiceCommodity commodityInfo(Main main, int id) {
    return (ServiceCommodity) AppService.single(main, ServiceCommodity.class, "select * from scm_service_commodity where id = ?", new Object[]{id});
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param commodity
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, ServiceCommodity commodity) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param commodity
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, ServiceCommodity commodity) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_service_commodity where upper(title)=? and company_id = ?", new Object[]{StringUtil.toUpperCase(commodity.getTitle()), UserRuntimeView.instance().getCompany().getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param commodity
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, ServiceCommodity commodity) throws UserMessageException {
    if (AppService.exist(main, "select id from scm_service_commodity where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(commodity.getTitle()), commodity.getId()})) {
      throw new UserMessageException("name.exist");
    }
  }

  public static List<ServiceCommodity> getServices(MainView main, Company company, String filter) {
    String sql = "select * from scm_service_commodity where \n"
            + " company_id = ? \n"
            + "AND commodity_or_service = 2 AND UPPER(title) like ? ";
    return AppService.list(main, ServiceCommodity.class, sql, new Object[]{company.getId(), "%" + filter.toUpperCase() + "%"});
  }
}
