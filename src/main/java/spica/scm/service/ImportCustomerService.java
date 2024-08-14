/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Company;
import spica.scm.domain.Country;
import spica.scm.domain.CustomerImporter;
import spica.scm.domain.District;
import spica.scm.domain.ImportProduct;
import spica.scm.domain.State;
import spica.scm.domain.Territory;
import spica.scm.util.AppUtil;
import spica.sys.SystemConstants;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppDb;
import wawo.entity.core.UserMessageException;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
public class ImportCustomerService {

  public static List<CustomerImporter> selectCustomerLogList(Main main, Company company, Integer status) {
    return AppService.list(main, CustomerImporter.class, "select * from scm_customer_importer where status=? and company_id=?  order by line_no asc",
            new Object[]{status, company.getId()});
  }

  public static boolean validateProduct(Main main, CustomerImporter customerImporter, Company company) {
    String error = "";
    boolean validate = true;
    if (customerImporter.getCustomerName() != null && !StringUtil.isEmpty(customerImporter.getCustomerName())) {
      if (isExist(main, customerImporter, company.getId())) {
        error = "Duplicate Customer ";
        validate = false;
      } else {
        if (customerImporter.getCountryId() == null) {
          error += (error.isEmpty() ? "" : ", ") + "Empty Country field";
          validate = false;
        }
        if (customerImporter.getStateId() == null) {
          error += (error.isEmpty() ? "" : ", ") + "Empty State field";
          validate = false;
        }
        if (customerImporter.getDistrictId() == null) {
          error += (error.isEmpty() ? "" : ", ") + "Empty District field";
          validate = false;
        }
        if (customerImporter.getGstNo() == null || (customerImporter.getGstNo()!=null && customerImporter.getGstNo().isEmpty())) {
//          error += (error.isEmpty() ? "" : ", ") + "Empty Gst No";
//          validate = false;
        } else {
          if (!AppUtil.isValidGstin(customerImporter.getGstNo())) {
            error += (error.isEmpty() ? "" : ", ") + "Invalid Gst No";
            validate = false;
          } else {
            if (!updateStateAndPan(main, customerImporter, customerImporter.getGstNo(), error)) {
              validate = false;
            }
          }
        }
        if (customerImporter.getAddress() == null) {
          error += (error.isEmpty() ? "" : ", ") + "Empty Address";
          validate = false;
        }

        if (customerImporter.getCustomerType() == null) {
          error += (error.isEmpty() ? "" : ", ") + "Empty Customer Type";
          validate = false;
        }
      }
    }
    if (!validate) {
      customerImporter.setStatus(SystemConstants.IMPORT_FAILED);
    } else {
      customerImporter.setStatus(SystemConstants.IMPORT_SUCCESS);
    }
    customerImporter.setError(error);
    AppService.update(main, customerImporter);
    main.em().flush();
    return validate;

  }

  public static final boolean isExist(Main main, CustomerImporter customerImporter, Integer companyId) throws UserMessageException {
    if (AppService.exist(main, "select '1' from scm_customer where upper(customer_name)=? and company_id=?", new Object[]{StringUtil.toUpperCase(customerImporter.getCustomerName()), companyId})) {
      return true;
    }
    if (AppService.exist(main, "select '1' from scm_customer_importer where upper(customer_name)=? and company_id=? and id not in(?)",
            new Object[]{StringUtil.toUpperCase(customerImporter.getCustomerName()), companyId, customerImporter.getId()})) {
      return true;
    }
    return false;
  }

  public static boolean updateStateAndPan(Main main, CustomerImporter customerImporter, String gstin, String error) {
    if (AppUtil.isValidGstin(gstin)) {
      customerImporter.setPanNo(gstin.substring(2, 12));
      if (customerImporter.getCountryId() != null) {
        State state = StateService.selectStateByStateCodeAndCountry(main, customerImporter.getCountryId(), gstin.substring(0, 2));
        if (state != null) {
          customerImporter.setStateId(state);
          if (customerImporter.getDistrictId() != null && !customerImporter.getDistrictId().getStateId().getId().equals(state.getId())) {
            customerImporter.setDistrictId(null);
            error += (error.isEmpty() ? "" : ", ") + "District does not belong to the entered State";
            return false;
          }
          return true;
        } else {
          customerImporter.setStateId(null);
          customerImporter.setDistrictId(null);
          error += (error.isEmpty() ? "" : ", ") + "Error in Gst No";
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  public static Country findCountryId(Main main, String country) {
    if (country != null) {
      return (Country) AppService.single(main, Country.class, "select * from scm_country where UPPER(country_name)=?", new Object[]{StringUtil.toUpperCase(country)});
    }
    return null;
  }

  public static State findStateId(Main main, String state, Country country) {
    if (state != null && country != null && country.getId() != null) {
      return (State) AppService.single(main, State.class, "select * from scm_state where UPPER(state_name)=? and country_id=?", new Object[]{StringUtil.toUpperCase(state), country.getId()});
    }
    return null;
  }

  public static District findDistrictId(Main main, String district, State state) {
    if (district != null && state != null && state.getId() != null) {
      return (District) AppService.single(main, District.class, "select * from scm_district where UPPER(district_name)=? and state_id=?", new Object[]{StringUtil.toUpperCase(district), state.getId()});
    }
    return null;
  }

  public static Territory findTerritoryId(Main main, String territory, Country country, Company company) {
    if (territory != null && country != null && country.getId() != null) {
      return (Territory) AppService.single(main, Territory.class, "select * from scm_territory where UPPER(territory_name)=? and country_id=? and company_id=?",
              new Object[]{StringUtil.toUpperCase(territory), country.getId(), company.getId()});
    }
    return null;
  }

  public static void deleteCustomerImport(Main main, Company company) {
    AppService.deleteSql(main, CustomerImporter.class, "delete from scm_customer_importer where company_id=? ", new Object[]{company.getId()});
  }

  public static void importCustomer(Main main, List<CustomerImporter> importCustomerList) {
    if (importCustomerList != null) {
      for (CustomerImporter customerImporter : importCustomerList) {
        AppService.insert(main, customerImporter);
      }
    }
  }

  public static final void deleteByPk(Main main, CustomerImporter importer) {
    AppService.delete(main, CustomerImporter.class, importer.getId());
  }

  public static void insertOrUpdateCustomerImport(Main main, CustomerImporter importer) throws Exception {
    if (importer.getId() == null) {
      AppService.insert(main, importer);
    } else {
      AppService.update(main, importer);
    }
  }
}
