/*
 * @(#)DebitCreditNotePlatformService.java	1.0 Tue Feb 27 12:40:25 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.service.delete;

import java.util.List;
import spica.fin.domain.DebitCreditNotePlatform;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import wawo.entity.core.UserMessageException;

/**
 * DebitCreditNotePlatformService
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue Feb 27 12:40:25 IST 2018
 */
public abstract class DebitCreditNotePlatformService {

  /**
   * DebitCreditNotePlatform paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getDebitCreditNotePlatformSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("fin_debit_credit_note_platform", DebitCreditNotePlatform.class, main);
    sql.main("select fin_debit_credit_note_platform.id,fin_debit_credit_note_platform.fin_debit_credit_note_id,fin_debit_credit_note_platform.platform_id from fin_debit_credit_note_platform fin_debit_credit_note_platform "); //Main query
    sql.count("select count(fin_debit_credit_note_platform.id) as total from fin_debit_credit_note_platform fin_debit_credit_note_platform "); //Count query
    sql.join("left outer join fin_debit_credit_note fin_debit_credit_note_platformfin_debit_credit_note_id on (fin_debit_credit_note_platformfin_debit_credit_note_id.id = fin_debit_credit_note_platform.fin_debit_credit_note_id) left outer join scm_platform fin_debit_credit_note_platformplatform_id on (fin_debit_credit_note_platformplatform_id.id = fin_debit_credit_note_platform.platform_id)"); //Join Query

    sql.string(null); //String search or sort fields
    sql.number(new String[]{"fin_debit_credit_note_platform.id", "fin_debit_credit_note_platformfin_debit_credit_note_id.company_id", "fin_debit_credit_note_platformplatform_id.credit_amount_required"}); //Numeric search or sort fields
    sql.date(null);  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of DebitCreditNotePlatform.
   *
   * @param main
   * @return List of DebitCreditNotePlatform
   */
  public static final List<DebitCreditNotePlatform> listPaged(Main main) {
    return AppService.listPagedJpa(main, getDebitCreditNotePlatformSqlPaged(main));
  }

//  /**
//   * Return list of DebitCreditNotePlatform based on condition
//   * @param main
//   * @return List<DebitCreditNotePlatform>
//   */
//  public static final List<DebitCreditNotePlatform> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getDebitCreditNotePlatformSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select DebitCreditNotePlatform by key.
   *
   * @param main
   * @param debitCreditNotePlatform
   * @return DebitCreditNotePlatform
   */
  public static final DebitCreditNotePlatform selectByPk(Main main, DebitCreditNotePlatform debitCreditNotePlatform) {
    return (DebitCreditNotePlatform) AppService.find(main, DebitCreditNotePlatform.class, debitCreditNotePlatform.getId());
  }

  /**
   * Insert DebitCreditNotePlatform.
   *
   * @param main
   * @param debitCreditNotePlatform
   */
  public static final void insert(Main main, DebitCreditNotePlatform debitCreditNotePlatform) {
    insertAble(main, debitCreditNotePlatform);  //Validating
    AppService.insert(main, debitCreditNotePlatform);

  }

  /**
   * Update DebitCreditNotePlatform by key.
   *
   * @param main
   * @param debitCreditNotePlatform
   * @return DebitCreditNotePlatform
   */
  public static final DebitCreditNotePlatform updateByPk(Main main, DebitCreditNotePlatform debitCreditNotePlatform) {
    updateAble(main, debitCreditNotePlatform); //Validating
    return (DebitCreditNotePlatform) AppService.update(main, debitCreditNotePlatform);
  }

  /**
   * Insert or update DebitCreditNotePlatform
   *
   * @param main
   * @param debitCreditNotePlatform
   */
  public static void insertOrUpdate(Main main, DebitCreditNotePlatform debitCreditNotePlatform) {
    if (debitCreditNotePlatform.getId() == null) {
      insert(main, debitCreditNotePlatform);
    } else {
      updateByPk(main, debitCreditNotePlatform);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param debitCreditNotePlatform
   */
  public static void clone(Main main, DebitCreditNotePlatform debitCreditNotePlatform) {
    debitCreditNotePlatform.setId(null); //Set to null for insert
    insert(main, debitCreditNotePlatform);
  }

  /**
   * Delete DebitCreditNotePlatform.
   *
   * @param main
   * @param debitCreditNotePlatform
   */
  public static final void deleteByPk(Main main, DebitCreditNotePlatform debitCreditNotePlatform) {
    deleteAble(main, debitCreditNotePlatform); //Validation
    AppService.delete(main, DebitCreditNotePlatform.class, debitCreditNotePlatform.getId());
  }

  /**
   * Delete Array of DebitCreditNotePlatform.
   *
   * @param main
   * @param debitCreditNotePlatform
   */
  public static final void deleteByPkArray(Main main, DebitCreditNotePlatform[] debitCreditNotePlatform) {
    for (DebitCreditNotePlatform e : debitCreditNotePlatform) {
      deleteByPk(main, e);
    }
  }

  /**
   * Validate delete.
   *
   * @param main
   * @param debitCreditNotePlatform
   * @throws UserMessageException
   */
  private static final void deleteAble(Main main, DebitCreditNotePlatform debitCreditNotePlatform) throws UserMessageException {

  }

  /**
   * Validate insert.
   *
   * @param main
   * @param debitCreditNotePlatform
   * @throws UserMessageException
   */
  private static final void insertAble(Main main, DebitCreditNotePlatform debitCreditNotePlatform) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_debit_credit_note_platform where upper()=?", new Object[]{StringUtil.toUpperCase(debitCreditNotePlatform.get())})) {
    //  throw new UserMessageException("name.exist");
    //}
  }

  /**
   * Validate update.
   *
   * @param main
   * @param debitCreditNotePlatform
   * @throws UserMessageException
   */
  private static final void updateAble(Main main, DebitCreditNotePlatform debitCreditNotePlatform) throws UserMessageException {
    //if (AppService.exist(main, "select '1' from fin_debit_credit_note_platform where upper()=? and id !=?", new Object[]{StringUtil.toUpperCase(debitCreditNotePlatform.get()), debitCreditNotePlatform.getId()})) {
    //  throw new UserMessageException("name.exist");
    //}
  }
}
