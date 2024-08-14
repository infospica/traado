/*
 * @(#)DebitCreditNoteItemService.java	1.0 Tue Feb 27 12:40:25 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service;

import spica.fin.service.delete.DebitCreditNotePlatformService;
import java.util.List;
import spica.fin.domain.DebitCreditNote;
import spica.fin.domain.DebitCreditNoteItem;
import spica.fin.domain.DebitCreditNotePlatform;
import spica.scm.domain.Platform;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.sys.SystemRuntimeConfig;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 * DebitCreditNoteItemService
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue Feb 27 12:40:25 IST 2018
 */
public abstract class DebitCreditNoteItemService {

  /**
   * DebitCreditNoteItem paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getDebitCreditNoteItemSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_debit_credit_note_item", DebitCreditNoteItem.class, main);
    sql.main("select fin_debit_credit_note_item.id,fin_debit_credit_note_item.debit_credit_note_id,fin_debit_credit_note_item.title,fin_debit_credit_note_item.description,fin_debit_credit_note_item.tax_code_id,fin_debit_credit_note_item.taxable_value,fin_debit_credit_note_item.tax_value,fin_debit_credit_note_item.igst_amount,fin_debit_credit_note_item.sgst_amount,fin_debit_credit_note_item.cgst_amount,fin_debit_credit_note_item.net_value from fin_debit_credit_note_item fin_debit_credit_note_item "); //Main query
    sql.count("select count(fin_debit_credit_note_item.id) as total from fin_debit_credit_note_item fin_debit_credit_note_item "); //Count query
    sql.join("left outer join fin_debit_credit_note fin_debit_credit_note_itemdebit_credit_note_id on (fin_debit_credit_note_itemdebit_credit_note_id.id = fin_debit_credit_note_item.debit_credit_note_id) left outer join scm_tax_code fin_debit_credit_note_itemtax_code_id on (fin_debit_credit_note_itemtax_code_id.id = fin_debit_credit_note_item.tax_code_id)"); //Join Query

    sql.string(new String[]{"fin_debit_credit_note_item.title", "fin_debit_credit_note_item.description", "fin_debit_credit_note_itemtax_code_id.code"}); //String search or sort fields
    sql.number(new String[]{"fin_debit_credit_note_item.id", "fin_debit_credit_note_itemdebit_credit_note_id.company_id", "fin_debit_credit_note_item.taxable_value", "fin_debit_credit_note_item.tax_value", "fin_debit_credit_note_item.igst_amount", "fin_debit_credit_note_item.sgst_amount", "fin_debit_credit_note_item.cgst_amount", "fin_debit_credit_note_item.net_value"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of DebitCreditNoteItem.
   *
   * @param main
   * @return List of DebitCreditNoteItem
   */
  public static final List<DebitCreditNoteItem> listPaged(Main main) {
    return AppService.listPagedJpa(main, getDebitCreditNoteItemSqlPaged(main));
  }

//  /**
//   * Return list of DebitCreditNoteItem based on condition
//   * @param main
//   * @return List<DebitCreditNoteItem>
//   */
//  public static final List<DebitCreditNoteItem> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getDebitCreditNoteItemSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select DebitCreditNoteItem by key.
   *
   * @param main
   * @param debitCreditNoteItem
   * @return DebitCreditNoteItem
   */
  public static final DebitCreditNoteItem selectByPk(Main main, DebitCreditNoteItem debitCreditNoteItem) {
    return (DebitCreditNoteItem) AppService.find(main, DebitCreditNoteItem.class, debitCreditNoteItem.getId());
  }

  /**
   * Insert DebitCreditNoteItem.
   *
   * @param main
   * @param debitCreditNoteItem
   */
  public static final void insert(Main main, DebitCreditNoteItem debitCreditNoteItem) {
    insertAble(main, debitCreditNoteItem);  //Validating
    AppService.insert(main, debitCreditNoteItem);

  }

  /**
   * Update DebitCreditNoteItem by key.
   *
   * @param main
   * @param debitCreditNoteItem
   * @return DebitCreditNoteItem
   */
  public static final DebitCreditNoteItem updateByPk(Main main, DebitCreditNoteItem debitCreditNoteItem) {
    updateAble(main, debitCreditNoteItem); //Validating
    return (DebitCreditNoteItem) AppService.update(main, debitCreditNoteItem);
  }

  /**
   * Insert or update DebitCreditNoteItem
   *
   * @param main
   * @param debitCreditNoteItem
   */
  public static void insertOrUpdate(Main main, DebitCreditNoteItem debitCreditNoteItem) {
    if (debitCreditNoteItem.getId() == null) {
      insert(main, debitCreditNoteItem);
    } else {
      updateByPk(main, debitCreditNoteItem);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param debitCreditNoteItem
   */
  public static void clone(Main main, DebitCreditNoteItem debitCreditNoteItem) {
    debitCreditNoteItem.setId(null); //Set to null for insert
    insert(main, debitCreditNoteItem);
  }

  /**
   * Delete DebitCreditNoteItem.
   *
   * @param main
   * @param debitCreditNoteItem
   */
  public static final void deleteByPk(Main main, DebitCreditNoteItem debitCreditNoteItem) {
    deleteAble(main, debitCreditNoteItem); //Validation
    String sql = "update scm_platform set modified_by = ?, modified_at = ?, status_id = ? where id in(select platform_id from fin_debit_credit_note_platform where debit_credit_note_item_id = ?)";
    main.param(SystemRuntimeConfig.PLATFORM_STATUS_NEW.getId());
    main.param(debitCreditNoteItem.getId());
    AppService.updateSql(main, Platform.class, sql, true);
    main.clear();
    AppService.deleteSql(main, DebitCreditNotePlatform.class, "delete from fin_debit_credit_note_platform where debit_credit_note_item_id = ?", new Object[]{debitCreditNoteItem.getId()});
    AppService.delete(main, DebitCreditNoteItem.class, debitCreditNoteItem.getId());
  }

  /**
   * Delete Array of DebitCreditNoteItem.
   *
   * @param main
   * @param debitCreditNoteItem
   */
  public static final void deleteByPkArray(Main main, DebitCreditNoteItem[] debitCreditNoteItem) {
    for (DebitCreditNoteItem e : debitCreditNoteItem) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param debitCreditNote
   * @return
   */
  public static final List<DebitCreditNoteItem> selectByDebitCreditNote(Main main, DebitCreditNote debitCreditNote) {
    return AppService.list(main, DebitCreditNoteItem.class, "select * from fin_debit_credit_note_item where debit_credit_note_id = ?", new Object[]{debitCreditNote.getId()});
  }

  /**
   *
   * @param main
   * @param debitCreditNoteItemList
   * @param debitCreditNotePlatformList
   */
  public static void insertOrUpdate(Main main, List<DebitCreditNoteItem> debitCreditNoteItemList, List<DebitCreditNotePlatform> debitCreditNotePlatformList) {
    if (!StringUtil.isEmpty(debitCreditNoteItemList)) {
      for (DebitCreditNoteItem debitCreditNoteItem : debitCreditNoteItemList) {
        if (debitCreditNoteItem.getTitle() != null && debitCreditNoteItem.getTaxableValue() != null) {
          insertOrUpdate(main, debitCreditNoteItem);
        }
      }
    }

    if (!StringUtil.isEmpty(debitCreditNotePlatformList)) {
      for (DebitCreditNotePlatform debitCreditNotePlatform : debitCreditNotePlatformList) {
        DebitCreditNotePlatformService.insertOrUpdate(main, debitCreditNotePlatform);
      }
    }

  }

  /**
   * Validate delete.
   *
   * @param main
   * @param debitCreditNoteItem
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, DebitCreditNoteItem debitCreditNoteItem) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param debitCreditNoteItem
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, DebitCreditNoteItem debitCreditNoteItem) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_debit_credit_note_item where upper(title)=?", new Object[]{StringUtil.toUpperCase(debitCreditNoteItem.getTitle())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param debitCreditNoteItem
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, DebitCreditNoteItem debitCreditNoteItem) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_debit_credit_note_item where upper(title)=? and id !=?", new Object[]{StringUtil.toUpperCase(debitCreditNoteItem.getTitle()), debitCreditNoteItem.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
