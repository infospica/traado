/*
 * @(#)TransporterContactService.java	1.0 Mon Aug 08 17:59:21 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Transporter;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.TransporterContact;
import spica.scm.domain.UserProfile;
import spica.scm.validate.TransporterContactIs;

/**
 * TransporterContactService
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:21 IST 2016
 */
public abstract class TransporterContactService {

  /**
   * TransporterContact paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getTransporterContactSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_transporter_contact", TransporterContact.class, main);
    sql.main("select scm_transporter_contact.id,scm_transporter_contact.transporter_id,scm_transporter_contact.designation_id,scm_transporter_contact.user_profile_id,scm_transporter_contact.sort_order,scm_transporter_contact.status_id,scm_transporter_contact.created_by,scm_transporter_contact.modified_by,scm_transporter_contact.created_at,scm_transporter_contact.modified_at from scm_transporter_contact scm_transporter_contact "); //Main query
    sql.count("select count(scm_transporter_contact.id) as total from scm_transporter_contact scm_transporter_contact "); //Count query
    sql.join("left outer join scm_transporter scm_transporter_contacttransporter_id on (scm_transporter_contacttransporter_id.id = scm_transporter_contact.transporter_id) left outer join scm_designation scm_transporter_contactdesignation_id on (scm_transporter_contactdesignation_id.id = scm_transporter_contact.designation_id) left outer join scm_user_profile scm_transporter_contactuser_profile_id on (scm_transporter_contactuser_profile_id.id = scm_transporter_contact.user_profile_id) left outer join scm_status scm_transporter_contactstatus_id on (scm_transporter_contactstatus_id.id = scm_transporter_contact.status_id)"); //Join Query

    sql.string(new String[]{"scm_transporter_contacttransporter_id.transporter_name", "scm_transporter_contactdesignation_id.title", "scm_transporter_contactuser_profile_id.user_code", "scm_transporter_contactstatus_id.title", "scm_transporter_contact.created_by", "scm_transporter_contact.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_transporter_contact.id", "scm_transporter_contact.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_transporter_contact.created_at", "scm_transporter_contact.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of TransporterContact.
   *
   * @param main
   * @return List of TransporterContact
   */
  public static final List<TransporterContact> listPaged(Main main) {
    return AppService.listPagedJpa(main, getTransporterContactSqlPaged(main));
  }

//  /**
//   * Return list of TransporterContact based on condition
//   * @param main
//   * @return List<TransporterContact>
//   */
//  public static final List<TransporterContact> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getTransporterContactSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select TransporterContact by key.
   *
   * @param main
   * @param transporterContact
   * @return TransporterContact
   */
  public static final TransporterContact selectByPk(Main main, TransporterContact transporterContact) {
    return (TransporterContact) AppService.find(main, TransporterContact.class, transporterContact.getId());
  }

  /**
   * Insert TransporterContact.
   *
   * @param main
   * @param transporterContact
   */
  public static final void insert(Main main, TransporterContact transporterContact) {
    TransporterContactIs.insertAble(main, transporterContact);  //Validating
    AppService.insert(main, transporterContact);

  }

  /**
   * Update TransporterContact by key.
   *
   * @param main
   * @param transporterContact
   * @return TransporterContact
   */
  public static final TransporterContact updateByPk(Main main, TransporterContact transporterContact) {
    TransporterContactIs.updateAble(main, transporterContact); //Validating
    return (TransporterContact) AppService.update(main, transporterContact);
  }

  /**
   * Insert or update TransporterContact
   *
   * @param main
   * @param transporterContact
   */
  public static void insertOrUpdate(Main main, TransporterContact transporterContact) {
    if (transporterContact.getId() == null) {
      insert(main, transporterContact);
    } else {
      updateByPk(main, transporterContact);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param transporterContact
   */
  public static void clone(Main main, TransporterContact transporterContact) {
    transporterContact.setId(null); //Set to null for insert
    insert(main, transporterContact);
  }

  /**
   * Delete TransporterContact.
   *
   * @param main
   * @param transporterContact
   */
  public static final void deleteByPk(Main main, TransporterContact transporterContact) {
    TransporterContactIs.deleteAble(main, transporterContact); //Validation
    AppService.delete(main, TransporterContact.class, transporterContact.getId());
  }

  /**
   * Delete Array of TransporterContact.
   *
   * @param main
   * @param transporterContact
   */
  public static final void deleteByPkArray(Main main, TransporterContact[] transporterContact) {
    for (TransporterContact e : transporterContact) {
      deleteByPk(main, e);
    }
  }

  public static final List<TransporterContact> contactListByTransporter(Main main, Transporter transporter) {
    SqlPage sql = getTransporterContactSqlPaged(main);
    if (transporter.getId() == null) {
      return null;
    }
    sql.cond("where scm_transporter_contact.transporter_id=?");
    sql.param(transporter.getId());
    return AppService.listAllJpa(main, sql);
  }

  public static void insertArray(Main main, UserProfile[] userProfileSelected, Transporter transporter) {
    if (userProfileSelected != null) {
      TransporterContact transporterContact;
      for (UserProfile userProfile : userProfileSelected) {  //Reinserting
        transporterContact = new TransporterContact();
        transporterContact.setUserProfileId(userProfile);
        transporterContact.setTransporterId(transporter);
        transporterContact.setDesignationId(userProfile.getDesignationId());
        transporterContact.setStatusId(userProfile.getStatusId());
        insert(main, transporterContact);
      }
    }
  }

  public static final void deleteTransporterContact(Main main, TransporterContact transporterContact) {
    AppService.deleteSql(main, TransporterContact.class, "delete from scm_transporter_contact where id = ?", new Object[]{transporterContact.getId()});
  }
}
