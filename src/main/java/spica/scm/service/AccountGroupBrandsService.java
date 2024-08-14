/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Brand;
import spica.scm.domain.AccountGroupBrands;
import spica.scm.domain.Vendor;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.MainView;
import wawo.entity.core.SqlPage;
import wawo.entity.util.StringUtil;

/**
 *
 * @author Anoop Jayachandran
 */
public abstract class AccountGroupBrandsService {

  /**
   * AccountGroupBrands paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAccountGroupBrandsSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_account_group_brands", AccountGroupBrands.class, main);
    sql.main("select t1.id,t1.brand_id,t1.account_group_id from scm_account_group_brands t1 "); //Main query
    sql.count("select count(t1.id) as total from scm_account_group_brands t1 "); //Count query
    sql.join("left outer join scm_account_group t2 on (t2.id = t1.account_group_id) left outer join scm_brand t3 on (t3.id = t1.brand_id)"); //Join Query

    sql.string(new String[]{"t3.name"}); //String search or sort fields
    sql.number(new String[]{"t1.id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AccountGroupBrands.
   *
   * @param main
   * @return List of AccountGroupBrands
   */
  public static final List<AccountGroupBrands> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAccountGroupBrandsSqlPaged(main));
  }

  /**
   * Select AccountGroupBrands by key.
   *
   * @param main
   * @param accountGroupBrands
   * @return AccountGroupBrands
   */
  public static final AccountGroupBrands selectByPk(Main main, AccountGroupBrands accountGroupBrands) {
    return (AccountGroupBrands) AppService.find(main, AccountGroupBrands.class, accountGroupBrands.getId());
  }

  /**
   * Insert AccountGroupBrands.
   *
   * @param main
   * @param accountGroupBrands
   */
  public static final void insert(Main main, AccountGroupBrands accountGroupBrands) {
    AppService.insert(main, accountGroupBrands);
    
  }

  /**
   * Update AccountGroupBrands by key.
   *
   * @param main
   * @param accountGroupBrands
   * @return AccountGroupBrands
   */
  public static final AccountGroupBrands updateByPk(Main main, AccountGroupBrands accountGroupBrands) {
    return (AccountGroupBrands) AppService.update(main, accountGroupBrands);
  }

  /**
   * Insert or update AccountGroupBrands
   *
   * @param main
   * @param accountGroupBrands
   */
  public static void insertOrUpdate(Main main, AccountGroupBrands accountGroupBrands) {
    if (accountGroupBrands.getId() == null) {
      insert(main, accountGroupBrands);
    } else {
      updateByPk(main, accountGroupBrands);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param accountGroupBrands
   */
  public static void clone(Main main, AccountGroupBrands accountGroupBrands) {
    accountGroupBrands.setId(null); //Set to null for insert
    insert(main, accountGroupBrands);
  }

  /**
   * Delete AccountGroupBrands.
   *
   * @param main
   * @param accountGroupBrands
   */
  public static final void deleteByPk(Main main, AccountGroupBrands accountGroupBrands) {
    AppService.delete(main, AccountGroupBrands.class, accountGroupBrands.getId());
  }

  /**
   * Delete Array of AccountGroupBrands.
   *
   * @param main
   * @param accountGroupBrandss
   */
  public static final void deleteByPkArray(Main main, AccountGroupBrands[] accountGroupBrandss) {
    for (AccountGroupBrands e : accountGroupBrandss) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param vendor
   * @param brandList
   */
  public static void insertOrUpdate(Main main, AccountGroup accountGroup, List<Brand> brandList) {
    if (accountGroup.getId() != null) {
      AppService.deleteSql(main, AccountGroupBrands.class, "delete from scm_account_group_brands where account_group_id = ?", new Object[]{accountGroup.getId()});
      if (brandList != null && !brandList.isEmpty()) {
        for (Brand brand : brandList) {
          AccountGroupBrandsService.insert(main, new AccountGroupBrands(brand, accountGroup));
        }
      }
    }
  }

  /**
   *
   * @param main
   * @param vendor
   * @return
   */
  public static List<Brand> selectBrandByAccountGroup(Main main, AccountGroup accountGroup) {
    return AppService.list(main, Brand.class, "select id, code from scm_brand where id in (select brand_id from scm_account_group_brands where account_group_id = ?)", new Object[]{accountGroup.getId()});
  }
  
  public static void insertSecondaryAccountBrands(Main main, Vendor vendorId, AccountGroup defaultAccountGroup) {
    List<Brand> brandList = AppService.list(main, Brand.class, "select * from scm_brand where id in(select brand_id from scm_supplier_brand where vendor_id = ?)", new Object[]{vendorId.getId()});
    
    if (!StringUtil.isEmpty(brandList)) {
      for (Brand brand : brandList) {
        if (!AppService.exist(main, "select 1 from scm_account_group_brands where account_group_id = ? and brand_id = ?", new Object[]{defaultAccountGroup.getId(), brand.getId()})) {
          insert(main, new AccountGroupBrands(brand, defaultAccountGroup));
        }
      }
    }
  }
}
