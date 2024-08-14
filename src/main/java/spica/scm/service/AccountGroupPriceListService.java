/*
 * @(#)AccountGroupPriceListService.java	1.0 Wed Apr 13 15:41:17 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountGroupPriceList;
import spica.scm.domain.ProductPricelist;
import spica.scm.validate.AccountGroupPriceListIs;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;
import wawo.entity.core.UserMessageException;
import wawo.entity.db.system.DbConnector;

/**
 * AccountGroupPriceListService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Apr 13 15:41:17 IST 2016
 */
public abstract class AccountGroupPriceListService {

  /**
   * AccountGroupPriceList paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountGroupPriceListSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_account_group_price_list", AccountGroupPriceList.class, main);
    sql.main("select scm_account_group_price_list.id,scm_account_group_price_list.account_group_id,scm_account_group_price_list.title,scm_account_group_price_list.sort_order,scm_account_group_price_list.created_by,scm_account_group_price_list.modified_by,scm_account_group_price_list.created_at,scm_account_group_price_list.modified_at from scm_account_group_price_list scm_account_group_price_list "); //Main query
    sql.count("select count(scm_account_group_price_list.id) as total from scm_account_group_price_list scm_account_group_price_list "); //Count query
    sql.join("left outer join scm_account_group scm_account_group_price_listaccount_group_id on (scm_account_group_price_listaccount_group_id.id = scm_account_group_price_list.account_group_id)"); //Join Query

    sql.string(new String[]{"scm_account_group_price_listaccount_group_id.group_name", "scm_account_group_price_list.title", "scm_account_group_price_list.created_by", "scm_account_group_price_list.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_account_group_price_list.id", "scm_account_group_price_list.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_account_group_price_list.created_at", "scm_account_group_price_list.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AccountGroupPriceList.
   *
   * @param main
   * @return List of AccountGroupPriceList
   */
  public static final List<AccountGroupPriceList> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAccountGroupPriceListSqlPaged(main));
  }

//  /**
//   * Return list of AccountGroupPriceList based on condition
//   * @param main
//   * @return List<AccountGroupPriceList>
//   */
//  public static final List<AccountGroupPriceList> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAccountGroupPriceListSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select AccountGroupPriceList by key.
   *
   * @param main
   * @param accountGroupPriceList
   * @return AccountGroupPriceList
   */
  public static final AccountGroupPriceList selectByPk(Main main, AccountGroupPriceList accountGroupPriceList) {
    return (AccountGroupPriceList) AppService.find(main, AccountGroupPriceList.class, accountGroupPriceList.getId());
  }

  /**
   * Insert AccountGroupPriceList.
   *
   * @param main
   * @param accountGroupPriceList
   */
  public static final void insert(Main main, AccountGroupPriceList accountGroupPriceList) {
    AccountGroupPriceListIs.insertAble(main, accountGroupPriceList);  //Validating
    AppService.insert(main, accountGroupPriceList);

  }

  /**
   * Update AccountGroupPriceList by key.
   *
   * @param main
   * @param accountGroupPriceList
   * @return AccountGroupPriceList
   */
  public static final AccountGroupPriceList updateByPk(Main main, AccountGroupPriceList accountGroupPriceList) {
    AccountGroupPriceListIs.updateAble(main, accountGroupPriceList); //Validating
    return (AccountGroupPriceList) AppService.update(main, accountGroupPriceList);
  }

  /**
   * Insert or update AccountGroupPriceList
   *
   * @param main
   * @param accountGroupPriceList
   */
  public static void insertOrUpdate(Main main, AccountGroupPriceList accountGroupPriceList) {
    if (accountGroupPriceList.getId() == null) {
      insert(main, accountGroupPriceList);
    } else {
      updateByPk(main, accountGroupPriceList);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountGroupPriceList
   */
  public static void clone(Main main, AccountGroupPriceList accountGroupPriceList) {
    accountGroupPriceList.setId(null); //Set to null for insert
    insert(main, accountGroupPriceList);
  }

  /**
   * Delete AccountGroupPriceList.
   *
   * @param main
   * @param accountGroupPriceList
   */
  public static final void deleteByPk(Main main, AccountGroupPriceList accountGroupPriceList) {
    AccountGroupPriceListIs.deleteAble(main, accountGroupPriceList); //Validation
    AppService.delete(main, AccountGroupPriceList.class, accountGroupPriceList.getId());
  }

  /**
   * Delete Array of AccountGroupPriceList.
   *
   * @param main
   * @param accountGroupPriceList
   */
  public static final void deleteByPkArray(Main main, AccountGroupPriceList[] accountGroupPriceList) {
    for (AccountGroupPriceList e : accountGroupPriceList) {
      deleteByPk(main, e);
    }
  }

  public static List<AccountGroupPriceList> selectAccountGroupPriceListByAccount(Main main, Integer accountId) {
    List<AccountGroupPriceList> accountGroupPriceList = AppService.list(main, AccountGroupPriceList.class,
            "select agpl.* from scm_account_group ag left join scm_account_group_price_list agpl on ag.id = agpl.account_group_id "
            + "left join scm_account_group_detail agd on agd.account_group_id = agpl.account_group_id "
            + "where agd.account_id = ? and agpl.account_group_id is not null order by agpl.is_default desc", new Object[]{accountId});
    return accountGroupPriceList;
  }

  public static List<AccountGroupPriceList> selectAccountGroupPriceListByPrimaryAccount(Main main, Account accountId) {
    AccountGroup defaultAccountGroup = AccountGroupService.selectDefaultAccountGroupByAccount(main, accountId);
    if (defaultAccountGroup != null) {
      List<AccountGroupPriceList> accountGroupPriceList = AppService.list(main, AccountGroupPriceList.class,
              "select * from scm_account_group_price_list where account_group_id = ? order by id", new Object[]{defaultAccountGroup.getId()});
      return accountGroupPriceList;
    }
    return null;
  }

  public static List<AccountGroupPriceList> selectAccountGroupPriceListByAccountGroup(Main main, AccountGroup accountGroup) {
    List<AccountGroupPriceList> accountGroupPriceList = AppService.list(main, AccountGroupPriceList.class,
            "select * from scm_account_group_price_list where account_group_id = ? order by id", new Object[]{accountGroup.getId()});
    return accountGroupPriceList;
  }

  public static List<AccountGroupPriceList> listAccountPriceListByAccountGroup(Main main, AccountGroup accountGroup) {
    SqlPage sqlPage = getAccountGroupPriceListSqlPaged(main);
    sqlPage.cond("where scm_account_group_price_list.account_group_id = ?");
    sqlPage.param(accountGroup.getId());
    return AppService.listPagedJpa(main, sqlPage);
  }

//  public static AccountGroupPriceList selectDefualtAccountGroupPriceList(Main main, Integer accountId) {
//    return (AccountGroupPriceList) AppService.single(main, AccountGroupPriceList.class, "select * from scm_account_group_price_list scm_account_group_price_list "
//            + "inner join scm_account_group scm_account_group on scm_account_group.id = scm_account_group_detail.account_group_id "
//            + "inner join scm_account_group_detail scm_account_group_detail on scm_account_group_detail.account_group_id = scm_account_group_detail.account_group_id "
//            + "where scm_account_group_detail.account_id = ? and scm_account_group.is_default = ? and scm_account_group_price_list.is_default = ?", new Object[]{accountId, 1, 1});
//  }
  public static AccountGroupPriceList selectDefaultAccountGroupPriceList(Main main, Account accountId) {
    return (AccountGroupPriceList) AppService.single(main, AccountGroupPriceList.class, "select scm_account_group_price_list.* from scm_account_group_price_list scm_account_group_price_list "
            + "inner join scm_account_group scm_account_group on scm_account_group.id = scm_account_group_price_list.account_group_id "
            + "inner join scm_account_group_detail scm_account_group_detail on scm_account_group_detail.account_group_id = scm_account_group.id "
            + "where scm_account_group_detail.account_id = ? and scm_account_group.is_default = 1 and scm_account_group_price_list.is_default = 1", new Object[]{accountId.getId()});
  }

  public static AccountGroupPriceList selectDefaultAccountGroupPriceList(Main main, AccountGroup accountGroup) {
    return (AccountGroupPriceList) AppService.single(main, AccountGroupPriceList.class, "select * from scm_account_group_price_list where account_group_id = ? and is_default = 1", new Object[]{accountGroup.getId()});
  }

  public static AccountGroupPriceList selectDefaultAccountGroupPriceList(DbConnector dbConnector, AccountGroup accountGroup) {
    return (AccountGroupPriceList) AppDb.single(dbConnector, AccountGroupPriceList.class, "select * from scm_account_group_price_list where account_group_id = ? and is_default = 1", new Object[]{accountGroup.getId()});
  }

  public static void deleteAccountGroupPriceList(Main main, AccountGroupPriceList accountGroupPriceList) {
    priceListDeletable(main, accountGroupPriceList);
    AppService.deleteSql(main, ProductPricelist.class, "delete from scm_product_pricelist where account_group_price_list_id = ?", new Object[]{accountGroupPriceList.getId()});
    deleteByPk(main, accountGroupPriceList);
  }

  private static void priceListDeletable(Main main, AccountGroupPriceList accountGroupPriceList) {
    if (AppService.exist(main, " select '1' from scm_sales_invoice where account_group_price_list_id = ? ", new Object[]{accountGroupPriceList.getId()})) {
      throw new UserMessageException("error.pricelist.used");
    }
  }

  public static void cloneDefaultPriceList(MainView main, AccountGroupPriceList accountGroupPriceList, Account account) {
    AccountGroup defaultGroup = AccountGroupService.selectDefaultAccountGroupByAccount(main, account);
    String sql = "select * from scm_product_pricelist where account_group_price_list_id = ( select id from scm_account_group_price_list where account_group_id = ? and is_default = 1 )";
    List<ProductPricelist> productPricelist = AppService.list(main, ProductPricelist.class, sql, new Object[]{defaultGroup.getId()});
    for (ProductPricelist priceList : productPricelist) {
      ProductPricelist ppl = new ProductPricelist();
      ppl = switchToEntity(priceList);
      ppl.setId(null);
      ppl.setAccountGroupPriceListId(accountGroupPriceList);
      AppService.insert(main, ppl);
    }
  }

  private static ProductPricelist switchToEntity(ProductPricelist entity) {
    ProductPricelist pojo = new ProductPricelist();
    pojo.setAccountGroupPriceListId(entity.getAccountGroupPriceListId());
    pojo.setActualSellingPriceDerived(entity.getActualSellingPriceDerived());
    pojo.setExpectedLandingRate(entity.getExpectedLandingRate());
    pojo.setIsMrpChange(entity.getIsMrpChange());
    pojo.setIsProductDiscountToCustomer(entity.getIsProductDiscountToCustomer());
    pojo.setIsSchemeDiscountToCustomer(entity.getIsSchemeDiscountToCustomer());
    pojo.setLandingRate(entity.getLandingRate());
    pojo.setMarginPercentageDeviation(entity.getMarginPercentageDeviation());
    pojo.setMarginValueDeviation(entity.getMarginValueDeviation());
    pojo.setMarginValueDeviationDer(entity.getMarginValueDeviationDer());
    pojo.setProductDetailId(entity.getProductDetailId());
    pojo.setProductDiscountPercentage(entity.getProductDiscountPercentage());
    pojo.setProductDiscountValueDerived(entity.getProductDiscountValueDerived());
    pojo.setProductEntryDetailId(entity.getProductEntryDetailId());
    pojo.setSchemeDiscountPercentage(entity.getSchemeDiscountPercentage());
    pojo.setSchemeDiscountReplacement(entity.getSchemeDiscountReplacement());
    pojo.setSchemeDiscountValueDerived(entity.getSchemeDiscountValueDerived());
    pojo.setValueMrp(entity.getValueMrp());
    pojo.setValuePtr(entity.getValuePtr());
    pojo.setValuePtrPerProdPieceSell(entity.getValuePtrPerProdPieceSell());
    pojo.setValuePts(entity.getValuePts());
    pojo.setValuePtsPerProdPieceSell(entity.getValuePtsPerProdPieceSell());
    pojo.setVendorMarginValue(entity.getVendorMarginValue());
    pojo.setVendorMarginValueDer(entity.getVendorMarginValueDer());
    return pojo;
  }
}
