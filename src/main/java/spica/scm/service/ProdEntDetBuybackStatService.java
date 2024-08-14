/*
 * @(#)ProdEntDetBuybackStatService.java	1.0 Thu Sep 08 18:33:22 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */

package spica.scm.service;

import java.util.List;
import wawo.app.AppSec;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.ProdEntDetBuybackStat;
import spica.scm.validate.ProdEntDetBuybackStatIs;

/**
 * ProdEntDetBuybackStatService
 * @author	Spirit 1.2
 * @version	1.0, Thu Sep 08 18:33:22 IST 2016 
 */

public abstract class ProdEntDetBuybackStatService {  
 
 /**
   * ProdEntDetBuybackStat paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getProdEntDetBuybackStatSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_prod_ent_det_buyback_stat", ProdEntDetBuybackStat.class, main);
    sql.main("select scm_prod_ent_det_buyback_stat.id,scm_prod_ent_det_buyback_stat.title,scm_prod_ent_det_buyback_stat.sort_order,scm_prod_ent_det_buyback_stat.created_by,scm_prod_ent_det_buyback_stat.modified_by,scm_prod_ent_det_buyback_stat.created_at,scm_prod_ent_det_buyback_stat.modified_at from scm_prod_ent_det_buyback_stat scm_prod_ent_det_buyback_stat"); //Main query
    sql.count("select count(scm_prod_ent_det_buyback_stat.id) as total from scm_prod_ent_det_buyback_stat scm_prod_ent_det_buyback_stat"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_prod_ent_det_buyback_stat.title","scm_prod_ent_det_buyback_stat.created_by","scm_prod_ent_det_buyback_stat.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_prod_ent_det_buyback_stat.id","scm_prod_ent_det_buyback_stat.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_prod_ent_det_buyback_stat.created_at","scm_prod_ent_det_buyback_stat.modified_at"});  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of ProdEntDetBuybackStat.
  * @param main
  * @return List of ProdEntDetBuybackStat
  */
  public static final List<ProdEntDetBuybackStat> listPaged(Main main) {
     return AppService.listPagedJpa(main, getProdEntDetBuybackStatSqlPaged(main));
  }

//  /**
//   * Return list of ProdEntDetBuybackStat based on condition
//   * @param main
//   * @return List<ProdEntDetBuybackStat>
//   */
//  public static final List<ProdEntDetBuybackStat> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getProdEntDetBuybackStatSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select ProdEntDetBuybackStat by key.
  * @param main
  * @param prodEntDetBuybackStat
  * @return ProdEntDetBuybackStat
  */
  public static final ProdEntDetBuybackStat selectByPk(Main main, ProdEntDetBuybackStat prodEntDetBuybackStat) {
    return (ProdEntDetBuybackStat) AppService.find(main, ProdEntDetBuybackStat.class, prodEntDetBuybackStat.getId());
  }

 /**
  * Insert ProdEntDetBuybackStat.
  * @param main
  * @param prodEntDetBuybackStat
  */
  public static final void insert(Main main, ProdEntDetBuybackStat prodEntDetBuybackStat) {
    ProdEntDetBuybackStatIs.insertAble(main, prodEntDetBuybackStat);  //Validating
    AppService.insert(main, prodEntDetBuybackStat);

  }

 /**
  * Update ProdEntDetBuybackStat by key.
  * @param main
  * @param prodEntDetBuybackStat
  * @return ProdEntDetBuybackStat
  */
  public static final ProdEntDetBuybackStat updateByPk(Main main, ProdEntDetBuybackStat prodEntDetBuybackStat) {
    ProdEntDetBuybackStatIs.updateAble(main, prodEntDetBuybackStat); //Validating
    return (ProdEntDetBuybackStat) AppService.update(main, prodEntDetBuybackStat);
  }

  /**
   * Insert or update ProdEntDetBuybackStat
   *
   * @param main
   * @param prodEntDetBuybackStat
   */
  public static void insertOrUpdate(Main main, ProdEntDetBuybackStat prodEntDetBuybackStat) {
    if (prodEntDetBuybackStat.getId() == null) {
      insert(main, prodEntDetBuybackStat);
    }
    else{
      updateByPk(main, prodEntDetBuybackStat);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param prodEntDetBuybackStat
   */
  public static void clone(Main main, ProdEntDetBuybackStat prodEntDetBuybackStat) {
    prodEntDetBuybackStat.setId(null); //Set to null for insert
    insert(main, prodEntDetBuybackStat);
  }

 /**
  * Delete ProdEntDetBuybackStat.
  * @param main
  * @param prodEntDetBuybackStat
  */
  public static final void deleteByPk(Main main, ProdEntDetBuybackStat prodEntDetBuybackStat) {
    ProdEntDetBuybackStatIs.deleteAble(main, prodEntDetBuybackStat); //Validation
    AppService.delete(main, ProdEntDetBuybackStat.class, prodEntDetBuybackStat.getId());
  }
	
 /**
  * Delete Array of ProdEntDetBuybackStat.
  * @param main
  * @param prodEntDetBuybackStat
  */
  public static final void deleteByPkArray(Main main, ProdEntDetBuybackStat[] prodEntDetBuybackStat) {
    for (ProdEntDetBuybackStat e : prodEntDetBuybackStat) {
      deleteByPk(main, e);
    }
  }
}
