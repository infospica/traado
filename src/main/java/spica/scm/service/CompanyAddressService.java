/*
 * @(#)CompanyAddressService.java	1.0 Wed Mar 30 12:35:28 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Company;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.CompanyAddress;
import spica.scm.validate.CompanyAddressIs;
import wawo.app.faces.MainView;

/**
 * CompanyAddressService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:28 IST 2016
 */
public abstract class CompanyAddressService {

  /**
   * CompanyAddress paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getCompanyAddressSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_company_address", CompanyAddress.class, main);
    sql.main("select scm_company_address.id,scm_company_address.company_id,scm_company_address.address_type_id,scm_company_address.address,scm_company_address.country_id,scm_company_address.state_id,scm_company_address.district_id,scm_company_address.pin,scm_company_address.territory_id,scm_company_address.phone_1,scm_company_address.phone_2,scm_company_address.phone_3,scm_company_address.fax_1,scm_company_address.fax_2,scm_company_address.email,scm_company_address.website,scm_company_address.sort_order,scm_company_address.status_id,scm_company_address.created_by,scm_company_address.modified_by,scm_company_address.created_at,scm_company_address.modified_at from scm_company_address scm_company_address "); //Main query
    sql.count("select count(scm_company_address.id) as total from scm_company_address scm_company_address "); //Count query
    sql.join("left outer join scm_company scm_company_addresscompany_id on (scm_company_addresscompany_id.id = scm_company_address.company_id) left outer join scm_address_type scm_company_addressaddress_type_id on (scm_company_addressaddress_type_id.id = scm_company_address.address_type_id) left outer join scm_country scm_company_addresscountry_id on (scm_company_addresscountry_id.id = scm_company_address.country_id) left outer join scm_state scm_company_addressstate_id on (scm_company_addressstate_id.id = scm_company_address.state_id) left outer join scm_district scm_company_addressdistrict_id on (scm_company_addressdistrict_id.id = scm_company_address.district_id) left outer join scm_territory scm_company_addressterritory_id on (scm_company_addressterritory_id.id = scm_company_address.territory_id) left outer join scm_status scm_company_addressstatus_id on (scm_company_addressstatus_id.id = scm_company_address.status_id)"); //Join Query

    sql.string(new String[]{"scm_company_addresscompany_id.company_name", "scm_company_addressaddress_type_id.title", "scm_company_address.address", "scm_company_addresscountry_id.country_name", "scm_company_addressstate_id.state_name", "scm_company_addressdistrict_id.district_name", "scm_company_address.pin", "scm_company_addressterritory_id.territory_name", "scm_company_address.phone_1", "scm_company_address.phone_2", "scm_company_address.phone_3", "scm_company_address.fax_1", "scm_company_address.fax_2", "scm_company_address.email", "scm_company_address.website", "scm_company_addressstatus_id.title", "scm_company_address.created_by", "scm_company_address.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_company_address.id", "scm_company_address.sort_order"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_company_address.created_at", "scm_company_address.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of CompanyAddress.
   *
   * @param main
   * @return List of CompanyAddress
   */
  public static final List<CompanyAddress> listPaged(Main main) {
    return AppService.listPagedJpa(main, getCompanyAddressSqlPaged(main));
  }

  /**
   * Return all address of a company.
   *
   * @param main
   * @param company
   * @return
   */
  public static final List<CompanyAddress> addressListByCompany(Main main, Company company) {
    SqlPage sql = getCompanyAddressSqlPaged(main);
    if (company.getId() == null) {
      return null;
    }
    sql.cond("where scm_company_address.company_id=?");
    sql.param(company.getId());
    return AppService.listAllJpa(main, sql);
  }

//  /**
//   * Return list of CompanyAddress based on condition
//   * @param main
//   * @return List<CompanyAddress>
//   */
//  public static final List<CompanyAddress> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getCompanyAddressSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select CompanyAddress by key.
   *
   * @param main
   * @param companyAddress
   * @return CompanyAddress
   */
  public static final CompanyAddress selectByPk(Main main, CompanyAddress companyAddress) {
    return (CompanyAddress) AppService.find(main, CompanyAddress.class, companyAddress.getId());
  }

  /**
   * Insert CompanyAddress.
   *
   * @param main
   * @param companyAddress
   */
  public static final void insert(Main main, CompanyAddress companyAddress) {
    CompanyAddressIs.insertAble(main, companyAddress);  //Validating
    AppService.insert(main, companyAddress);

  }

  /**
   * Update CompanyAddress by key.
   *
   * @param main
   * @param companyAddress
   * @return CompanyAddress
   */
  public static final CompanyAddress updateByPk(Main main, CompanyAddress companyAddress) {
    CompanyAddressIs.updateAble(main, companyAddress); //Validating
    return (CompanyAddress) AppService.update(main, companyAddress);
  }

  /**
   * Insert or update CompanyAddress
   *
   * @param main
   * @param companyAddress
   */
  public static void insertOrUpdate(Main main, CompanyAddress companyAddress) {
    if (companyAddress.getId() == null) {
      insert(main, companyAddress);
    }
    updateByPk(main, companyAddress);
  }

  /**
   * Making default for newly added or updated if isdefault is checked
   *
   * @param main
   * @param companyAddress
   */
  public static void makeDefault(Main main, CompanyAddress companyAddress) {
    if (companyAddress.getSortOrder() == 0) {
      main.param(1);
      main.param(companyAddress.getCompanyId().getId());
      main.param(companyAddress.getId());
      main.param(0);
      AppService.updateSql(main, CompanyAddress.class, "update scm_company_address set modified_by = ?, modified_at = ?, sort_order = ? where company_id = ?  and id <> ? and sort_order = ?", true);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param companyAddress
   */
  public static void clone(Main main, CompanyAddress companyAddress) {
    companyAddress.setId(null); //Set to null for insert
    insert(main, companyAddress);
  }

  /**
   * Delete CompanyAddress.
   *
   * @param main
   * @param companyAddress
   */
  public static final void deleteByPk(Main main, CompanyAddress companyAddress) {
    CompanyAddressIs.deleteAble(main, companyAddress); //Validation
    AppService.delete(main, CompanyAddress.class, companyAddress.getId());
  }

  /**
   * Delete Array of CompanyAddress.
   *
   * @param main
   * @param companyAddress
   */
  public static final void deleteByPkArray(Main main, CompanyAddress[] companyAddress) {
    for (CompanyAddress e : companyAddress) {
      deleteByPk(main, e);
    }
  }

  public static final CompanyAddress selectCompanyRegisteredAddress(Main main, Company company) {
    return (CompanyAddress) AppService.single(main, CompanyAddress.class, "select * from scm_company_address where company_id = ? and address_type_id = ?", new Object[]{company.getId(), AddressTypeService.REGISTERED_ADDRESS});
  }
}
