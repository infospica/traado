/*
 * @(#)SecPrivilageService.java	1.0 Fri Feb 03 19:15:39 IST 2017 
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
import spica.sys.domain.Privilage;
import spica.scm.validate.PrivilageIs;

/**
 * PrivilageService
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 03 19:15:39 IST 2017 
 */

public abstract class PrivilageService {  
 
 /**
   * SecPrivilage paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPrivilageSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("sec_privilage", Privilage.class, main);
    sql.main("select sec_privilage.id,sec_privilage.title,sec_privilage.sort_order from sec_privilage sec_privilage"); //Main query
    sql.count("select count(sec_privilage.id) as total from sec_privilage sec_privilage"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"sec_privilage.title"}); //String search or sort fields
    sql.number(new String[]{"sec_privilage.id"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }
	
 /**
  * Return List of SecPrivilage.
  * @param main
  * @return List of SecPrivilage
  */
  public static final List<Privilage> listPaged(Main main) {
     return AppService.listPagedJpa(main, getPrivilageSqlPaged(main));
  }

//  /**
//   * Return list of SecPrivilage based on condition
//   * @param main
//   * @return List<SecPrivilage>
//   */
//  public static final List<SecPrivilage> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getSecPrivilageSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

 /**
  * Select SecPrivilage by key.
  * @param main
  * @param secPrivilage
  * @return SecPrivilage
  */
  public static final Privilage selectByPk(Main main, Privilage Privilage) {
    return (Privilage) AppService.find(main, Privilage.class, Privilage.getId());
  }

 /**
  * Insert SecPrivilage.
  * @param main
  * @param secPrivilage
  */
  public static final void insert(Main main, Privilage Privilage) {
    PrivilageIs.insertAble(main, Privilage);  //Validating
    AppService.insert(main, Privilage);

  }

 /**
  * Update SecPrivilage by key.
  * @param main
  * @param secPrivilage
  * @return SecPrivilage
  */
  public static final Privilage updateByPk(Main main, Privilage Privilage) {
    PrivilageIs.updateAble(main, Privilage); //Validating
    return (Privilage) AppService.update(main, Privilage);
  }

  /**
   * Insert or update SecPrivilage
   *
   * @param main
   * @param secPrivilage
   */
  public static void insertOrUpdate(Main main, Privilage Privilage) {
    if (Privilage.getId() == null) {
      insert(main, Privilage);
    }
    else{
      updateByPk(main, Privilage);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param secPrivilage
   */
  public static void clone(Main main, Privilage Privilage) {
    Privilage.setId(null); //Set to null for insert
    insert(main, Privilage);
  }

 /**
  * Delete SecPrivilage.
  * @param main
  * @param secPrivilage
  */
  public static final void deleteByPk(Main main, Privilage Privilage) {
    PrivilageIs.deleteAble(main, Privilage); //Validation
    AppService.delete(main, Privilage.class, Privilage.getId());
  }
	
 /**
  * Delete Array of SecPrivilage.
  * @param main
  * @param secPrivilage
  */
  public static final void deleteByPkArray(Main main, Privilage[] Privilage) {
    for (Privilage e : Privilage) {
      deleteByPk(main, e);
    }
  }
}
