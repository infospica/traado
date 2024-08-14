/*
 * @(#)PrefixTypeService.java	1.0 Fri Oct 28 09:25:53 IST 2016
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
import spica.scm.domain.PrefixType;
import wawo.entity.core.UserMessageException;

/**
 * PrefixTypeService
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Oct 28 09:25:53 IST 2016
 */
public abstract class PrefixTypeService {

  public final static int PURCHASE_REQ_INVOICE_PREFIX_ID = 1;
  public final static int PURCHASE_ORDER_INVOICE_PREFIX_ID = 2;
  public final static int PURCHASE_RETURN_INVOICE_PREFIX_ID = 3;
  public final static int SALES_INVOICE_PREFIX_ID = 4;
  public final static int SALES_RETURN_PREFIX_ID = 5;
  public final static int SALES_GROUP_PREFIX_ID = 6;
  public final static int SALES_INVOICE_CONSIGNMENT_PREFIX_ID = 7;
  public final static int SALES_RETURN_CONSIGNMENT_PREFIX_ID = 8;
  public final static int VALUE_CREDIT_NOTE_TAXABLE_PREFIX_ID = 9;
  public final static int VALUE_DEBIT_NOTE_TAXABLE_PREFIX_ID = 10;
  public final static int VALUE_CREDIT_NOTE_NON_TAXABLE_PREFIX_ID = 18;
  public final static int VALUE_DEBIT_NOTE_NON_TAXABLE_PREFIX_ID = 19;
  public final static int SALES_SERVICES_TAXABLE_PREFIX_ID = 20;
  public final static int SALES_SERVICES_NON_TAXABLE_PREFIX_ID = 21;
  public final static int VOUCHER_ENTRY_PREFIX_ID = 11;
  public final static int SALES_RETURN_REPLACEMENT_CLAIM_INVOICE_PREFIX_ID = 12;
  public final static int SALES_REQUISITION_PREFIX_ID = 13;
  public final static int SALES_ORDER_PREFIX_ID = 14;
  public final static int PRODUCT_ENTRY_INVOICE_PREFIX_ID = 15;
  public final static int PURCHASE_CONSIGNMENT_PREFIX_ID = 16;
  public final static int PURCHASE_RETURN_CONSIGNMENT_PREFIX_ID = 17;
  public final static int STOCK_ADJUSTMENT_PREFIX_ID = 22;
  public final static int STOCK_MOVEMENT_PREFIX_ID = 23;
  public final static String SALES_INVOICE = "Sales Invoice";
  public final static String PURCHASE_REQ_INVOICE_PREFIX = "PQ";
  public final static String PURCHASE_ORDER_INVOICE_PREFIX = "PO";
  public final static String PURCHASE_RETURN_INVOICE_PREFIX = "PR";
  public final static String PRODUCT_ENTRY_INVOICE_PREFIX = "PE";
  public final static String SALES_INVOICE_PREFIX = "SA";
  public final static String SALES_REQUISITION_PREFIX = "SQ";
  public final static String SALES_ORDER_PREFIX = "SO";
  public final static String SALES_REPALCEMENT_SETTLEMENT_INVOICE_PREFIX = "SS";
  public final static String SALES_RETURN_SALEABLE_INVOICE_PREFIX = "SR";
  public final static String SALES_INVOICE_CONSIGNMENT_PREFIX = "SAC";
  public final static String SALES_RETURN_CONSIGNMENT_PREFIX = "SRC";
  public final static String VALUE_CREDIT_NOTE_TAXABLE_PREFIX = "CNT";
  public final static String VALUE_CREDIT_NOTE_NON_TAXABLE_PREFIX = "CNNT";
  public final static String VALUE_DEBIT_NOTE_TAXABLE_PREFIX = "DNT";
  public final static String VALUE_DEBIT_NOTE_NON_TAXABLE_PREFIX = "DNNT";
  public final static String SALES_SERVICES_TAXABLE_PREFIX = "SST";
  public final static String SALES_SERVICES_NON_TAXABLE_PREFIX = "SSNT";
  public final static String VOUCHER_ENTRY_PREFIX = "VE";
  public final static String PURCHASE_CONSIGNMENT_PREFIX = "PC";
  public final static String PURCHASE_RETURN_CONSIGNMENT_PREFIX = "PRC";
  public final static String STOCK_ADJUSTMENT_PREFIX = "ADJ";
  public final static String STOCK_MOVEMENT_PREFIX = "MVMNT";

  /**
   * PrefixType paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getPrefixTypeSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_prefix_type", PrefixType.class, main);
    sql.main("select scm_prefix_type.id,scm_prefix_type.title,scm_prefix_type.created_by,scm_prefix_type.modified_by,scm_prefix_type.created_at,scm_prefix_type.modified_at from scm_prefix_type scm_prefix_type"); //Main query
    sql.count("select count(scm_prefix_type.id) as total from scm_prefix_type scm_prefix_type"); //Count query
    sql.join(""); //Join Query

    sql.string(new String[]{"scm_prefix_type.title", "scm_prefix_type.created_by", "scm_prefix_type.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_prefix_type.id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_prefix_type.created_at", "scm_prefix_type.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of PrefixType.
   *
   * @param main
   * @return List of PrefixType
   */
  public static final List<PrefixType> listPaged(Main main) {
    return AppService.listPagedJpa(main, getPrefixTypeSqlPaged(main));
  }

//  /**
//   * Return list of PrefixType based on condition
//   * @param main
//   * @return List<PrefixType>
//   */
//  public static final List<PrefixType> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getPrefixTypeSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select PrefixType by key.
   *
   * @param main
   * @param prefixType
   * @return PrefixType
   */
  public static final PrefixType selectByPk(Main main, PrefixType prefixType) {
    return (PrefixType) AppService.find(main, PrefixType.class, prefixType.getId());
  }

  /**
   * Insert PrefixType.
   *
   * @param main
   * @param prefixType
   */
  public static final void insert(Main main, PrefixType prefixType) {
    insertAble(main, prefixType);  //Validating
    AppService.insert(main, prefixType);

  }

  /**
   * Update PrefixType by key.
   *
   * @param main
   * @param prefixType
   * @return PrefixType
   */
  public static final PrefixType updateByPk(Main main, PrefixType prefixType) {
    updateAble(main, prefixType); //Validating
    return (PrefixType) AppService.update(main, prefixType);
  }

  /**
   * Insert or update PrefixType
   *
   * @param main
   * @param prefixType
   */
  public static void insertOrUpdate(Main main, PrefixType prefixType) {
    if (prefixType.getId() == null) {
      insert(main, prefixType);
    } else {
      updateByPk(main, prefixType);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param prefixType
   */
  public static void clone(Main main, PrefixType prefixType) {
    prefixType.setId(null); //Set to null for insert
    insert(main, prefixType);
  }

  /**
   * Delete PrefixType.
   *
   * @param main
   * @param prefixType
   */
  public static final void deleteByPk(Main main, PrefixType prefixType) {
    deleteAble(main, prefixType); //Validation
    AppService.delete(main, PrefixType.class, prefixType.getId());
  }

  /**
   * Delete Array of PrefixType.
   *
   * @param main
   * @param prefixType
   */
  public static final void deleteByPkArray(Main main, PrefixType[] prefixType) {
    for (PrefixType e : prefixType) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param prefixType
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, PrefixType prefixType) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param prefixType
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, PrefixType prefixType) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_prefix_type where upper(title)=?", new Object[]{StringUtil.toUpperCase(prefixType.getTitle())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param prefixType
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, PrefixType prefixType) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from scm_prefix_type where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(prefixType.getTitle()), prefixType.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
