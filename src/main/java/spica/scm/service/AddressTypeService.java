/*
 * @(#)AddressTypeService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
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
import spica.scm.domain.AddressType;
import spica.scm.validate.ValidateUtil;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * AddressTypeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class AddressTypeService {

  public static final Integer REGISTERED_ADDRESS = 1;

  /**
   * AddressType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getAddressTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_address_type", AddressType.class, main);
    sql.main("select scm_address_type.id,scm_address_type.title,scm_address_type.sort_order,scm_address_type.created_by,scm_address_type.modified_by,scm_address_type.created_at,scm_address_type.modified_at from scm_address_type scm_address_type"); //Main query
    sql.count("select count(scm_address_type.id) from scm_address_type scm_address_type"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_address_type.title", "scm_address_type.created_by", "scm_address_type.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_address_type.id", "scm_address_type.sort_order"}); //Number search or sort fields
    sql.date(new String[]{"scm_address_type.created_at", "scm_address_type.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of AddressType.
   *
   * @param main
   * @return List of AddressType
   */
  public static final List<AddressType> listPaged(Main main) {
    return AppService.listPagedJpa(main, getAddressTypeSqlPaged(main));
  }

//  /**
//   * Return list of AddressType based on condition
//   * @param main
//   * @return List<AddressType>
//   */
//  public static final List<AddressType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getAddressTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select AddressType by key.
   *
   * @param main
   * @param addressType
   * @return AddressType
   */
  public static final AddressType selectByPk(Main main, AddressType addressType) {
    return (AddressType) AppService.find(main, AddressType.class, addressType.getId());
  }

  /**
   * Insert AddressType.
   *
   * @param main
   * @param addressType
   */
  public static final void insert(Main main, AddressType addressType) {
    insertAble(main, addressType);  //Validating
    AppService.insert(main, addressType);

  }

  /**
   * Update AddressType by key.
   *
   * @param main
   * @param addressType
   * @return AddressType
   */
  public static final AddressType updateByPk(Main main, AddressType addressType) {
    updateAble(main, addressType); //Validating
    return (AddressType) AppService.update(main, addressType);
  }

  /**
   * Insert or update AddressType
   *
   * @param main
   * @param addressType
   */
  public static void insertOrUpdate(Main main, AddressType addressType) {
    if (addressType.getId() == null) {
      insert(main, addressType);
    } else {
      updateByPk(main, addressType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param addressType
   */
  public static void clone(Main main, AddressType addressType) {
    addressType.setId(null); //Set to null for insert
    insert(main, addressType);
  }

  /**
   * Delete AddressType.
   *
   * @param main
   * @param addressType
   */
  public static final void deleteByPk(Main main, AddressType addressType) {
    deleteAble(main, addressType); //Validation
    AppService.delete(main, AddressType.class, addressType.getId());
  }

  /**
   * Delete Array of AddressType.
   *
   * @param main
   * @param addressType
   */
  public static final void deleteByPkArray(Main main, AddressType[] addressType) {
    for (AddressType e : addressType) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param addressType
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, AddressType addressType) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param addressType
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, AddressType addressType) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_address_type where upper(title)=?", new Object[]{StringUtil.toUpperCase(addressType.getTitle())})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

  /**
   * Validate update.
   *
   * @param main
   * @param addressType
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, AddressType addressType) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_address_type where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(addressType.getTitle()), addressType.getId()})) {
      throw new UserMessageException("name.exist", ValidateUtil.getFieldName("title"));
    }
  }

}
