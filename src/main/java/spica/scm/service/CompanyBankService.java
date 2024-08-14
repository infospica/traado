/*
 * @(#)CompanyBankService.java	1.0 Wed Mar 30 12:35:28 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.fin.service.LedgerExternalService;
import spica.scm.domain.Company;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.CompanyBank;
import spica.scm.domain.CompanyBankContact;
import spica.scm.validate.CompanyBankIs;

/**
 * CompanyBankService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class CompanyBankService {

  /**
   * CompanyBank paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCompanyBankSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_company_bank", CompanyBank.class, main);
    sql.main("select scm_company_bank.id,scm_company_bank.company_id,scm_company_bank.bank_account_type_id,scm_company_bank.account_name,scm_company_bank.account_no,scm_company_bank.bank_id,scm_company_bank.branch_name,scm_company_bank.branch_code,scm_company_bank.ifsc_code,scm_company_bank.branch_address,scm_company_bank.status_id,scm_company_bank.sort_order,scm_company_bank.created_by,scm_company_bank.modified_by,scm_company_bank.created_at,scm_company_bank.modified_at from scm_company_bank scm_company_bank "); //Main query
    sql.count("select count(scm_company_bank.id) as total from scm_company_bank scm_company_bank "); //Count query
    sql.join("left outer join scm_company scm_company_bankcompany_id on (scm_company_bankcompany_id.id = scm_company_bank.company_id) left outer join scm_bank_account_type scm_company_bankbank_account_type_id on (scm_company_bankbank_account_type_id.id = scm_company_bank.bank_account_type_id) left outer join scm_bank scm_company_bankbank_id on (scm_company_bankbank_id.id = scm_company_bank.bank_id) left outer join scm_status scm_company_bankstatus_id on (scm_company_bankstatus_id.id = scm_company_bank.status_id)"); //Join Query

    sql.string(new String[]{"scm_company_bankcompany_id.company_name", "scm_company_bankbank_account_type_id.title", "scm_company_bank.account_name", "scm_company_bank.account_no", "scm_company_bankbank_id.name", "scm_company_bank.branch_name", "scm_company_bank.branh_code", "scm_company_bank.ifsc_code", "scm_company_bank.branch_address", "scm_company_bankstatus_id.title", "scm_company_bank.created_by", "scm_company_bank.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_company_bank.id", "scm_company_bank.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_company_bank.created_at", "scm_company_bank.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of CompanyBank.
   *
   * @param main
   * @return List of CompanyBank
   */
  public static final List<CompanyBank> listPaged(Main main) {
    return AppService.listPagedJpa(main, getCompanyBankSqlPaged(main));
  }

  /**
   * Return all bank of a company.
   *
   * @param main
   * @param company
   * @return
   */
  public static final List<CompanyBank> bankListByCompany(Main main, Company company) {
    SqlPage sql = getCompanyBankSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_company_bank.company_id=?");
    sql.param(company.getId());
    return AppService.listAllJpa(main, sql);
  }
//  /**
//   * Return list of CompanyBank based on condition
//   * @param main
//   * @return List<CompanyBank>
//   */
//  public static final List<CompanyBank> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCompanyBankSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }

  /**
   * Select CompanyBank by key.
   *
   * @param main
   * @param companyBank
   * @return CompanyBank
   */
  public static final CompanyBank selectByPk(Main main, CompanyBank companyBank) {
    return (CompanyBank) AppService.find(main, CompanyBank.class, companyBank.getId());
  }

  /**
   * Insert or update CompanyBank
   *
   * @param main
   * @param CompanyBank
   */
  public static void insertOrUpdate(Main main, CompanyBank companyBank) {
    if (companyBank.getId() == null) {
      CompanyBankIs.insertAble(main, companyBank);  //Validating
      AppService.insert(main, companyBank);
    } else {
      CompanyBankIs.updateAble(main, companyBank); //Validating
      companyBank = (CompanyBank) AppService.update(main, companyBank);
    }
    LedgerExternalService.saveLedgerBank(main, companyBank);
  }

  /**
   * Making default for newly added or updated if isdefault is checked
   *
   * @param main
   * @param companyBank
   */
  public static void makeDefault(Main main, CompanyBank companyBank) {
    if (companyBank.getSortOrder() == 0) {
      main.param(1);
      main.param(companyBank.getCompanyId().getId());
      main.param(companyBank.getId());
      main.param(0);
      AppService.updateSql(main, CompanyBank.class, "update scm_company_bank set modified_by = ?, modified_at = ?, sort_order = ? where company_id = ? and id <> ? and sort_order = ?", true);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param companyBank
   */
  public static void clone(Main main, CompanyBank companyBank) {
    companyBank.setId(null); //Set to null for insert
    insertOrUpdate(main, companyBank);
  }

  /**
   * Delete CompanyBank.
   *
   * @param main
   * @param companyBank
   */
  public static final void deleteByPk(Main main, CompanyBank companyBank) {
    LedgerExternalService.deleteLedgerBank(main, companyBank);
    AppService.deleteSql(main, CompanyBankContact.class, "delete from scm_company_bank_contact scm_company_bank_contact where scm_company_bank_contact.company_bank_id=?", new Object[]{companyBank.getId()});
    AppService.delete(main, CompanyBank.class, companyBank.getId());
  }

  /**
   * Delete Array of CompanyBank.
   *
   * @param main
   * @param companyBank
   */
  public static final void deleteByPkArray(Main main, CompanyBank[] companyBank) {
    for (CompanyBank e : companyBank) {
      deleteByPk(main, e);
    }
  }

  public static CompanyBank bankByCompanyId(Main main, Company company) {
    CompanyBank companyBank = null;
    if (company != null) {
      companyBank = (CompanyBank) AppService.single(main, CompanyBank.class, "select * from scm_company_bank where company_id = ? and status_id=1 limit 1 ",
              new Object[]{company.getId()});
    }
    return companyBank;
  }
}
