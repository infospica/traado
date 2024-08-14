/*
 * @(#)PackTypeService.java	1.0 Thu Aug 25 13:58:57 IST 2016 
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
import spica.scm.domain.PackType;
import spica.scm.validate.PackTypeIs;

/**
 * PackTypeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Aug 25 13:58:57 IST 2016
 */
public abstract class PackTypeService {

  /**
   * PackType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPackTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_pack_type", PackType.class, main);
    sql.main("select scm_pack_type.id,scm_pack_type.title,scm_pack_type.sort_order,scm_pack_type.type_level,scm_pack_type.created_by,scm_pack_type.modified_by,scm_pack_type.created_at,scm_pack_type.modified_at from scm_pack_type scm_pack_type"); //Main query
    sql.count("select count(scm_pack_type.id) as total from scm_pack_type scm_pack_type"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_pack_type.title", "scm_pack_type.created_by", "scm_pack_type.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_pack_type.id", "scm_pack_type.sort_order", "scm_pack_type.type_level"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_pack_type.created_at", "scm_pack_type.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PackType.
   *
   * @param main
   * @return List of PackType
   */
  public static final List<PackType> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPackTypeSqlPaged(main));
  }

//  /**
//   * Return list of PackType based on condition
//   * @param main
//   * @return List<PackType>
//   */
//  public static final List<PackType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPackTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select PackType by key.
   *
   * @param main
   * @param packType
   * @return PackType
   */
  public static final PackType selectByPk(Main main, PackType packType) {
    return (PackType) AppService.find(main, PackType.class, packType.getId());
  }

  /**
   * Insert PackType.
   *
   * @param main
   * @param packType
   */
  public static final void insert(Main main, PackType packType) {
    PackTypeIs.insertAble(main, packType);  //Validating
    AppService.insert(main, packType);

  }

  /**
   * Update PackType by key.
   *
   * @param main
   * @param packType
   * @return PackType
   */
  public static final PackType updateByPk(Main main, PackType packType) {
    PackTypeIs.updateAble(main, packType); //Validating
    return (PackType) AppService.update(main, packType);
  }

  /**
   * Insert or update PackType
   *
   * @param main
   * @param packType
   */
  public static void insertOrUpdate(Main main, PackType packType) {
    if (packType.getId() == null) {
      insert(main, packType);
    } else {
      updateByPk(main, packType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param packType
   */
  public static void clone(Main main, PackType packType) {
    packType.setId(null); //Set to null for insert
    insert(main, packType);
  }

  /**
   * Delete PackType.
   *
   * @param main
   * @param packType
   */
  public static final void deleteByPk(Main main, PackType packType) {
    PackTypeIs.deleteAble(main, packType); //Validation
    AppService.delete(main, PackType.class, packType.getId());
  }

  /**
   * Delete Array of PackType.
   *
   * @param main
   * @param packType
   */
  public static final void deleteByPkArray(Main main, PackType[] packType) {
    for (PackType e : packType) {
      deleteByPk(main, e);
    }
  }
}
