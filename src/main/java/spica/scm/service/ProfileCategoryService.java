///*
// * @(#)ProfileCategoryService.java	1.0 Tue Jun 28 12:38:26 IST 2016 
// *
// * Copyright 2000-2016 Wawo Foundation. All rights reserved.
// * Use is subject to license terms.
// *
// */
//
//package spica.scm.service;
//
//import java.util.List;
//import wawo.app.AppSec;
//import wawo.app.common.AppService;
//import wawo.app.Main;
//import wawo.entity.core.SqlPage;
//import spica.scm.domain.ProfileCategory;
//import spica.scm.validate.ProfileCategoryIs;
//
///**
// * ProfileCategoryService
// * @author	Spirit 1.2
// * @version	1.0, Tue Jun 28 12:38:26 IST 2016 
// */
//
//public abstract class ProfileCategoryService {  
// 
// /**
//   * ProfileCategory paginated query.
//   *
//   * @param main
//   * @return SqlPage
//   */
//  private static SqlPage getProfileCategorySqlPaged(Main main) {
//    SqlPage sql = AppService.sqlPage("scm_profile_category", ProfileCategory.class, main);
//    sql.main("select scm_profile_category.id,scm_profile_category.title,scm_profile_category.sort_order,scm_profile_category.created_by,scm_profile_category.modified_by,scm_profile_category.created_at,scm_profile_category.modified_at from scm_profile_category scm_profile_category"); //Main query
//    sql.count("select count(scm_profile_category.id) from scm_profile_category scm_profile_category"); //Count query
//    sql.join(""); //Join Query
//
//    sql.string(new String[]{"scm_profile_category.title","scm_profile_category.created_by","scm_profile_category.modified_by"}); //String search or sort fields
//    sql.number(new String[]{"scm_profile_category.id","scm_profile_category.sort_order"}); //Numeric search or sort fields
//    sql.date(new String[]{"scm_profile_category.created_at","scm_profile_category.modified_at"});  //Date search or sort fields
//    return sql;
//  }
//	
// /**
//  * Return List of ProfileCategory.
//  * @param main
//  * @return List of ProfileCategory
//  */
//  public static final List<ProfileCategory> listPaged(Main main) {
//     return AppService.listPagedJpa(main, getProfileCategorySqlPaged(main));
//  }
//
////  /**
////   * Return list of ProfileCategory based on condition
////   * @param main
////   * @return List<ProfileCategory>
////   */
////  public static final List<ProfileCategory> listPagedBySomeCondition(Main main) {
////     SqlPage sql = getProfileCategorySqlPaged(main);
////     sql.cond("where 1=? and 2=?");
////     sql.param("valueof1");
////     sql.param("valueof2");
////     return AppService.listPagedJpa(main, sql); // For pagination in view
////   //  return AppService.listAllJpa(main, sql); // Return the full records
////  }
//
// /**
//  * Select ProfileCategory by key.
//  * @param main
//  * @param profileCategory
//  * @return ProfileCategory
//  */
//  public static final ProfileCategory selectByPk(Main main, ProfileCategory profileCategory) {
//    return (ProfileCategory) AppService.find(main, ProfileCategory.class, profileCategory.getId());
//  }
//
// /**
//  * Insert ProfileCategory.
//  * @param main
//  * @param profileCategory
//  */
//  public static final void insert(Main main, ProfileCategory profileCategory) {
//    ProfileCategoryIs.insertAble(main, profileCategory);  //Validating
//    AppService.insert(main, profileCategory);
//
//  }
//
// /**
//  * Update ProfileCategory by key.
//  * @param main
//  * @param profileCategory
//  * @return ProfileCategory
//  */
//  public static final ProfileCategory updateByPk(Main main, ProfileCategory profileCategory) {
//    ProfileCategoryIs.updateAble(main, profileCategory); //Validating
//    return (ProfileCategory) AppService.update(main, profileCategory);
//  }
//
//  /**
//   * Insert or update ProfileCategory
//   *
//   * @param main
//   * @param ProfileCategory
//   */
//  public static void insertOrUpdate(Main main, ProfileCategory profileCategory) {
//    if (profileCategory.getId() == null) {
//      insert(main, profileCategory);
//    }
//    else{
//        updateByPk(main, profileCategory);
//    }
//  }
//
//  /**
//   * Clone and existing object
//   *
//   * @param main
//   * @param profileCategory
//   */
//  public static void clone(Main main, ProfileCategory profileCategory) {
//    profileCategory.setId(null); //Set to null for insert
//    insert(main, profileCategory);
//  }
//
// /**
//  * Delete ProfileCategory.
//  * @param main
//  * @param profileCategory
//  */
//  public static final void deleteByPk(Main main, ProfileCategory profileCategory) {
//    ProfileCategoryIs.deleteAble(main, profileCategory); //Validation
//    AppService.delete(main, ProfileCategory.class, profileCategory.getId());
//  }
//	
// /**
//  * Delete Array of ProfileCategory.
//  * @param main
//  * @param profileCategory
//  */
//  public static final void deleteByPkArray(Main main, ProfileCategory[] profileCategory) {
//    for (ProfileCategory e : profileCategory) {
//      deleteByPk(main, e);
//    }
//  }
//}
