package spica.reports.service;

import java.util.ArrayList;
import java.util.List;
import spica.reports.model.CustomerReport;
import spica.reports.model.SupplierReport;
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
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;

/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
/**
 *
 * @author godson
 */
public class MasterReportService {

  public static List<Product> getProductReportList(Main main, Company company, AccountGroup accountGroup, Account account, ServiceCommodity commodity, ProductCategory productCategory) {
    main.clear();
    String sql = productQuery(main, company, accountGroup, account, commodity, productCategory);
    return AppService.list(main, Product.class, sql, main.getParamData().toArray());
  }

  public static List<CustomerReport> getCustomerReportList(Main main, District district, Territory territory, SalesAgent salesAgentId, Integer groupStatus) {
    main.clear();
    String sql = null;
    String group = " group by ";
    boolean flag = false;
    if (district == null && territory == null && salesAgentId == null && groupStatus == null) {
      sql = "select t1.id as customer_id, t1.customer_name,t6.title as trade_profile,t1.gst_no,t1.pan_no,t3.state_name,t4.district_name,t5.territory_name,t2.address,\n"
              + "t2.phone_1,t2.phone_2,t2.email,t9.group_name ,lic.dl_no\n"
              + "from scm_customer t1\n"
              + "inner join scm_customer_address t2 on t2.customer_id = t1.id \n"
              + "inner join scm_customer_trade_profile t7 on t7.customer_id = t1.id\n"
              + "left join scm_state t3 on t2.state_id = t3.id\n"
              + "left join scm_district t4 on t2.district_id = t4.id\n"
              + "left join scm_territory t5 on t2.territory_id = t5.id\n"
              + "left join scm_trade_profile t6 on t7.trade_profile_id = t6.id\n"
              + "left join scm_sales_account t8 on t8.customer_id = t1.id\n"
              + "left join scm_account_group t9 on t9.id = t8.account_group_id \n"
              + "LEFT JOIN(SELECT string_agg(license_key::varchar, ',') as dl_no,customer_id from scm_customer_license group by customer_id) as lic\n"
              + "ON lic.customer_id=t1.id\n"
              + "where t1.company_id = ?\n"
              + "order by t1.customer_name asc";
      main.param(UserRuntimeView.instance().getCompany().getId());
    } else {
      sql = "select t1.id as customer_id, t1.customer_name,t6.title as trade_profile,t1.gst_no,t1.pan_no,t3.state_name,\n"
              + "t4.district_name,t2.address,t2.phone_1,t2.phone_2,t2.email,t15.group_name,lic.dl_no \n"
              + "from scm_customer t1 \n"
              + "inner join scm_customer_address t2 on t2.customer_id = t1.id \n"
              + "inner join scm_customer_trade_profile t7 on t7.customer_id = t1.id\n"
              + "left join scm_state t3 on t2.state_id = t3.id\n"
              + "left join scm_district t4 on t2.district_id = t4.id\n"
              + "left join scm_trade_profile t6 on t7.trade_profile_id = t6.id\n"
              + "left join scm_sales_account t8 on t8.customer_id = t1.id\n"
              + "left join scm_account_group t15 on t15.id = t8.account_group_id \n"
              + "LEFT JOIN(SELECT string_agg(license_key::varchar, ',') as dl_no,customer_id from scm_customer_license group by customer_id) as lic\n"
              + "ON lic.customer_id=t1.id\n";

      String join = "left join scm_sales_agent_account_group t9 on t9.account_group_id = t8.account_group_id\n"
              + "left join scm_sales_agent_territory t10 on t10.sales_agent_id=? \n"
              + "left join scm_territory_district t11 on t11.territory_id in(t10.territory_id) \n";

      String where = "where t1.company_id = ?  \n";
      group += " t15.group_name,";

      if (district != null && salesAgentId == null) {
        where += " and t2.district_id = ? ";
        main.param(UserRuntimeView.instance().getCompany().getId());
        main.param(district.getId());
      } else if (salesAgentId != null && district == null) {
        flag = true;
        sql += join;
        where += " and t9.sales_agent_id= ? and t2.district_id=t11.district_id";
        main.param(salesAgentId.getId());
        main.param(UserRuntimeView.instance().getCompany().getId());
        main.param(salesAgentId.getId());
      } else if (salesAgentId != null && district != null) {
        flag = true;
        sql += join;
        where += " and t9.sales_agent_id= ? and t2.district_id = ? and t2.district_id=t11.district_id ";
        main.param(salesAgentId.getId());
        main.param(UserRuntimeView.instance().getCompany().getId());
        main.param(salesAgentId.getId());
        main.param(district.getId());
      }

      if (territory != null) {
        if (!flag) {
          sql += " left join scm_territory_district t9 on t9.district_id = t2.district_id ";
          where += " and t9.territory_id = ? ";
          if (district == null) {
            main.param(UserRuntimeView.instance().getCompany().getId());
          }
          main.param(territory.getId());
        } else {
          where += " and t11.territory_id = ? ";
          main.param(territory.getId());
        }
      }

      if (groupStatus != null) {
        if (groupStatus.intValue() == 1) {
          where += " and t8.account_group_id is null ";
        } else {
          where += " and t8.account_group_id is not null ";
        }
        if (main.getParamData().toArray().length == 0) {
          main.param(UserRuntimeView.instance().getCompany().getId());
        }
      }
      group += " t1.id, t1.customer_name,t6.title ,t1.gst_no,t1.pan_no,t3.state_name,t4.district_name,t2.address,t2.phone_1,t2.phone_2,t2.email,lic.dl_no ";
      String order = " order by t1.customer_name asc";

      sql += where + group + order;
    }
    return AppDb.getList(main.dbConnector(), CustomerReport.class, sql, main.getParamData().toArray());
  }

  public static List<SupplierReport> getSupplierList(Main main) {
    String sql = "select t1.vendor_name,t2.title as supplier_group,UPPER(t1.gst_no) as gst_no,UPPER(t1.pan_no) as pan_no,t3.state_name,t4.address,t4.phone_1,t4.phone_2,"
            + "tab1.commodity,tab2.brand from scm_vendor t1 \n"
            + "left join scm_supplier_group t2 on t2.id =t1.supplier_group_id\n"
            + "left join scm_state t3 on t3.id = t1.state_id\n"
            + "left join scm_vendor_address t4 on t4.vendor_id = t1.id\n"
            + "left join (SELECT t6.vendor_id,string_agg(title, ',') as commodity FROM scm_service_commodity t5,scm_vendor_commodity t6 where t5.id =t6.commodity_id group by t6.vendor_id) tab1 on tab1.vendor_id = t1.id \n"
            + "left join (SELECT t8.vendor_id,string_agg(name, ',') as brand FROM scm_brand t7,scm_supplier_brand t8 where t7.id =t8.brand_id group by t8.vendor_id) tab2 on tab2.vendor_id = t1.id \n"
            + "where t1.company_id = ?\n"
            + "order by t1.vendor_name asc";
    return AppDb.getList(main.dbConnector(), SupplierReport.class, sql, new Object[]{UserRuntimeView.instance().getCompany().getId()});
  }

  public static List<District> selectDistrictAuto(Main main, Integer companyId, Territory territory, String filter) {
    main.clear();
    String sql = null;
    if (territory == null) {
      sql = "select t1.* from scm_district t1\n"
              + "left join  scm_customer_address t2 on t2.district_id = t1.id\n"
              + "left join scm_customer t3 on t3.id = t2.customer_id where t3.company_id = ?  ";
      main.param(companyId);

    } else {
      sql = "select t1.* from scm_district t1\n"
              + "left join scm_territory_district t2 on  t2.district_id = t1.id where t2.territory_id = ? ";
      main.param(territory.getId());
    }

    sql += "and upper(t1.district_name) like ? group by t1.id order by district_name asc";
    main.param("%" + filter.toUpperCase() + "%");
    List<District> list = AppService.list(main, District.class, sql, main.getParamData().toArray());
    return list;
  }

  public static List<Territory> selectTerritoryByCompanyAndDistrict(Main main, Integer companyId, District district) {
    main.clear();
    String sql = "select t9.id,t9.territory_name from \n"
            + "scm_customer t1,\n"
            + "scm_customer_address t2,\n"
            + "scm_sales_account t5,\n"
            + "scm_sales_agent_account_group t6,\n"
            + "scm_user_profile t7,\n"
            + "scm_sales_agent_territory t8,\n"
            + "scm_territory t9\n"
            + "where t1.company_id = ? ANd t2.customer_id = t1.id "
            + "AND t5.customer_id = t1.id AND t5.account_group_id = t6.account_group_id\n"
            + "AND t7.id = t6.sales_agent_id AND t8.sales_agent_id = t6.sales_agent_id\n"
            + "AND t9.id = t8.territory_id \n";

    main.param(companyId);
    if (district != null) {
      sql += " and t2.district_id = ? \n";
      main.param(district.getId());
    }
    sql += " GROUP BY t9.id,t9.territory_name order by t9.territory_name asc";
    return AppService.list(main, Territory.class, sql, main.getParamData().toArray());
  }

  public static List selectCommodityByAccountAndCompany(Main main, Company company, Account account) {
    main.clear();
    String sql = "";
    sql = "select T3.* from scm_vendor_commodity T1,scm_vendor T2,scm_service_commodity T3\n"
            + "WHERE T1.vendor_id = T2.id AND T2.company_id = ? AND T1.commodity_id = T3.id\n";
    main.param(company.getId());
    if (account != null) {
      sql += " AND T1.vendor_id = ? \n";
      main.param(account.getVendorId().getId());
    }
    sql += " Group By T3.id order by T3.title asc";
    return AppService.list(main, ServiceCommodity.class, sql, main.getParamData().toArray());
  }

  public static List<ProductCategory> selectProductCategoryByCompanyAccountCommodity(Main main, Company company, AccountGroup accountGroup, Account account, ServiceCommodity commodity) {
    main.clear();

    String sql = "select * from scm_product_category where id in (select product_category_id from (" + productQuery(main, company, accountGroup, account, commodity, null) + ") tab)"
            + " order by scm_product_category.title asc";

    return AppService.list(main, ProductCategory.class, sql, main.getParamData().toArray());
  }

  private static String productQuery(Main main, Company company, AccountGroup accountGroup, Account account, ServiceCommodity commodity, ProductCategory productCategory) {
    String sql = "select prod.id,prod.product_name,string_agg(DISTINCT(clas.title::varchar),',') as description,prod.commodity_id,prod.product_category_id,prod.product_type_id,\n"
            + "prod.hsn_code,prod.pack_size,prod.product_unit_id,prod.brand_id,prod.expiry_sales_days,prod.product_status_id from scm_product prod\n"
            + "left join scm_product_classification_detail clas_det on prod.id=clas_det.product_id\n"
            + "left join scm_product_classification clas on clas.id = clas_det.classification_id\n"
            + "left join scm_product_preset on scm_product_preset.product_id=prod.id\n"
            + "where ";
    String groupBy = "group by prod.id,prod.product_name,prod.commodity_id,prod.product_category_id,prod.product_type_id,\n"
            + "prod.hsn_code,prod.pack_size,prod.product_unit_id,prod.brand_id,prod.expiry_sales_days,prod.product_status_id \n";
    if (company != null && company.getId() != null) {
      sql += " prod.company_id = ?  ";
      main.param(company.getId());
    }
    if (accountGroup != null && accountGroup.getId() != null) {
      sql += " and scm_product_preset.account_id in(select account_id from scm_account_group_detail where account_group_id=?)\n";
      main.param(accountGroup.getId());
    }
    if (account != null && account.getId() != null) {
      if (commodity != null && commodity.getId() != null) {
        sql += " AND prod.commodity_id = ? ";
        main.param(commodity.getId());
      } else {
        sql += " AND prod.commodity_id in (select commodity_id from scm_vendor_commodity where vendor_id = ? )\n"
                + " and brand_id IN(SELECT brand_id FROM  scm_supplier_brand WHERE vendor_id = ? ) ";
        main.param(account.getVendorId().getId());
        main.param(account.getVendorId().getId());
      }
    } else if (commodity != null && commodity.getId() != null) {
      sql += " AND prod.commodity_id = ? ";
      main.param(commodity.getId());
    }
    if (productCategory != null && productCategory.getId() != null) {
      sql += " AND prod.product_category_id = ? ";
      main.param(productCategory.getId());
    }
    sql += groupBy + " order by prod.product_name";
    return sql;
  }

  public static List selectCustomerForExport(Main main, Company company, District district) {
    List<CustomerImporter> customerImporterList = new ArrayList<>();
    String sql = "SELECT cust.company_id,cust.id,cust.customer_name,cust.country_id,cust.state_id,cust_add.district_id,cust_add.city_or_town,cust_add.territory_id,cust.pan_no,cust.gst_no,\n"
            + "cust_add.address,cust_add.pin,cust_add.phone_1,cust_add.phone_2,cust_add.phone_3,cust_add.fax_1,cust_add.email,trade.id as customer_type,cust.taxable\n"
            + "FROM scm_customer cust,scm_customer_address cust_add,scm_customer_trade_profile cust_trade,scm_trade_profile trade\n"
            + "where cust.company_id=?\n"
            + "and cust.id = cust_add.customer_id and cust.id=cust_trade.customer_id and cust_trade.customer_id=cust.id and cust_trade.trade_profile_id=trade.id\n"
            + "and cust_add.address_type_id=1 ";
    ArrayList<Object> params = new ArrayList<>();
    params.add(company.getId());
    if (district != null) {
      sql += " and cust_add.district_id=?";
      params.add(district.getId());
    }
    customerImporterList = AppService.list(main, CustomerImporter.class, sql, params.toArray());
    return customerImporterList;
  }
}
