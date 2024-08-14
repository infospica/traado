/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.fin.service;

import java.util.List;
import spica.fin.domain.AccountingEntityType;
import spica.fin.domain.AccountingLedger;
import spica.scm.domain.Currency;
import wawo.app.Main;
import spica.constant.AccountingConstant;
import spica.constant.ScmConstant;
import spica.fin.domain.AccountingGroup;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyBank;
import spica.scm.domain.Country;
import spica.scm.domain.Customer;
import spica.scm.domain.State;
import spica.fin.domain.TaxCode;
import spica.scm.domain.Transporter;
import spica.scm.domain.UserProfile;
import spica.scm.domain.Vendor;
import spica.fin.common.LedgerObjects;
import spica.scm.domain.SalesAgent;
import wawo.app.common.AppService;
import wawo.entity.util.StringUtil;

/**
 * Create ledger from external application.
 *
 * @author Arun VC
 */
public abstract class LedgerExternalService {

  public static void deleteLedgerBank(Main main, CompanyBank bank) {
    deleteLedger(main, bank.getId(), AccountingConstant.ACC_ENTITY_BANK.getId());
  }

  public static void saveLedgerBank(Main main, CompanyBank bank) {
    //Noping and tax for bank, 
    LedgerObjects ledger = getLedgerObject(null, null, bank.getBranchAddress(), bank.getCompanyId().getStateId(), bank.getCompanyId().getCurrencyId(), bank.getCompanyId().getCountryId());
    AccountingGroup group = ScmConstant.BANK_AC_TYPE_OVERDRAFT.getId() == bank.getBankAccountTypeId().getId() ? AccountingConstant.GROUP_BANK_OD : AccountingConstant.GROUP_BANK_ACCOUNT;
    saveLedger(main, bank.getId(), bank.getAccountName(), AccountingConstant.ACC_ENTITY_BANK, group, bank.getCompanyId(), ledger, null);

  }

  public static void deleteLedgerTransporter(Main main, Transporter transporter) {
    deleteLedger(main, transporter.getId(), AccountingConstant.ACC_ENTITY_CUSTOMER.getId());
  }

  public static void saveLedgerTransporter(Main main, Transporter trasporter) {
    LedgerObjects ledger = getLedgerObject(trasporter.getGstNo(), trasporter.getTransporterPin(), trasporter.getTransporterAddress(), trasporter.getStateId(), trasporter.getCompanyId().getCurrencyId(), trasporter.getCountryId());
    saveLedger(main, trasporter.getId(), trasporter.getTransporterName(), AccountingConstant.ACC_ENTITY_TRANSPORTER, AccountingConstant.GROUP_SUNDRY_CREDITORS, trasporter.getCompanyId(), ledger, null);
  }

  public static void deleteLedgerSalesAgent(Main main, SalesAgent profile) {
    deleteLedger(main, profile.getId(), AccountingConstant.ACC_ENTITY_AGENT.getId());
  }

  public static void saveLedgerSalesAgent(Main main, SalesAgent profile) {
    //TODO add state and pin for sales agent also country by default inherit from company
    // Now defaulting to companyes
    LedgerObjects ledger = getLedgerObject(profile.getGstNo(), profile.getCompanyId().getCompanyPin(), profile.getAddress1(), profile.getCompanyId().getStateId(), profile.getCompanyId().getCurrencyId(), profile.getCompanyId().getCountryId());
    saveLedger(main, profile.getId(), profile.getName(), AccountingConstant.ACC_ENTITY_AGENT, AccountingConstant.GROUP_SUNDRY_CREDITORS, profile.getCompanyId(), ledger, null);
  }

  public static void deleteLedgerCustomer(Main main, Customer customer) {
    deleteLedger(main, customer.getId(), AccountingConstant.ACC_ENTITY_CUSTOMER.getId());
  }

  public static void saveLedgerCustomer(Main main, Customer customer) {
    LedgerObjects ledger = getLedgerObject(customer.getGstNo(), customer.getCustomerPin(), customer.getCustomerAddress(), customer.getStateId(), customer.getCompanyId().getCurrencyId(), customer.getCountryId());
    saveLedger(main, customer.getId(), customer.getCustomerName(), AccountingConstant.ACC_ENTITY_CUSTOMER, AccountingConstant.GROUP_SUNDRY_DEBTORS, customer.getCompanyId(), ledger, null);
  }

  public static void deleteLedgerVendor(Main main, Vendor vendor) {
    deleteLedger(main, vendor.getId(), AccountingConstant.ACC_ENTITY_VENDOR.getId());
  }

  public static void saveLedgerVendor(Main main, Vendor vendor) {
    LedgerObjects ledger = getLedgerObject(vendor.getGstNo(), vendor.getVendorPin(), vendor.getVendorAddress(), vendor.getStateId(), vendor.getCompanyId().getCurrencyId(), vendor.getCountryId());
    saveLedger(main, vendor.getId(), vendor.getVendorName(), AccountingConstant.ACC_ENTITY_VENDOR, AccountingConstant.GROUP_SUNDRY_CREDITORS, vendor.getCompanyId(), ledger, null);
  }

  public static void deleteLedgerTaxCode(Main main, TaxCode taxcode) {
    deleteLedger(main, taxcode.getId(), AccountingConstant.ACC_ENTITY_TAX.getId());
  }

  public static void saveLedgerTaxCode(Main main, Company company) {
    List<TaxCode> subTaxCodeList = null;
    List<TaxCode> taxCodeList = AppService.list(main, TaxCode.class, "select * from scm_tax_code where parent_id is null and country_id = ? and id not in (select entity_id from fin_accounting_ledger where accounting_entity_type_id = ? and company_id = ?)", new Object[]{company.getCountryId().getId(), AccountingConstant.ACC_ENTITY_TAX.getId(), company.getId()});
    if (!StringUtil.isEmpty(taxCodeList)) {
      for (TaxCode taxCode : taxCodeList) {
        LedgerExternalService.saveLedgerTaxCode(main, company, taxCode);
        subTaxCodeList = AppService.list(main, TaxCode.class, "select * from scm_tax_code where parent_id = ?", new Object[]{taxCode.getId()});
        for (TaxCode subTaxCode : subTaxCodeList) {
          LedgerExternalService.saveLedgerTaxCode(main, company, subTaxCode);
        }
      }
      main.em().flush(); //Required if you are fetching the saved tax code in your code
    }
  }

  //TODO handle with tax regime
  public static void saveLedgerTaxCode(Main main, Company company, TaxCode taxCode) {
    if (AccountingConstant.TAX_HEAD_TDS.getId().intValue() == taxCode.getTaxHeadId().getId()) {
      //TODO CHANGE THE ACCOUNTING HEAD TO ASSET AND lIABILITY
      saveLedger(main, taxCode.getId(), taxCode.getCode(), AccountingConstant.ACC_ENTITY_TAX, AccountingConstant.GROUP_CURRENT_ASSET, company, null, AccountingConstant.LEDGER_CODE_TAX_TDS_ASSET);
      saveLedger(main, taxCode.getId(), taxCode.getCode(), AccountingConstant.ACC_ENTITY_TAX, AccountingConstant.GROUP_CURRENT_LIABILITY, company, null, AccountingConstant.LEDGER_CODE_TAX_TDS_LIABILITY);
    } else if (AccountingConstant.TAX_HEAD_GST.getId().intValue() == taxCode.getTaxHeadId().getId()) {
      if (AccountingConstant.TAX_TYPE_IGST == taxCode.getTaxType()) {
        saveLedger(main, taxCode.getId(), taxCode.getCode(), AccountingConstant.ACC_ENTITY_TAX, AccountingConstant.ACC_HEAD_TAX_IGST, company, null, AccountingConstant.LEDGER_CODE_TAX_INPUT);
        saveLedger(main, taxCode.getId(), taxCode.getCode(), AccountingConstant.ACC_ENTITY_TAX, AccountingConstant.ACC_HEAD_TAX_IGST, company, null, AccountingConstant.LEDGER_CODE_TAX_OUTPUT);
      } else if (AccountingConstant.TAX_TYPE_CGST == taxCode.getTaxType()) {
        saveLedger(main, taxCode.getId(), taxCode.getCode(), AccountingConstant.ACC_ENTITY_TAX, AccountingConstant.ACC_HEAD_TAX_CGST, company, null, AccountingConstant.LEDGER_CODE_TAX_INPUT);
        saveLedger(main, taxCode.getId(), taxCode.getCode(), AccountingConstant.ACC_ENTITY_TAX, AccountingConstant.ACC_HEAD_TAX_CGST, company, null, AccountingConstant.LEDGER_CODE_TAX_OUTPUT);
      } else if (AccountingConstant.TAX_TYPE_SGST == taxCode.getTaxType()) {
        saveLedger(main, taxCode.getId(), taxCode.getCode(), AccountingConstant.ACC_ENTITY_TAX, AccountingConstant.ACC_HEAD_TAX_SGST, company, null, AccountingConstant.LEDGER_CODE_TAX_INPUT);
        saveLedger(main, taxCode.getId(), taxCode.getCode(), AccountingConstant.ACC_ENTITY_TAX, AccountingConstant.ACC_HEAD_TAX_SGST, company, null, AccountingConstant.LEDGER_CODE_TAX_OUTPUT);
      }
    } else if (AccountingConstant.TAX_HEAD_GST_CESS.getId().intValue() == taxCode.getTaxHeadId().getId()) {
      saveLedger(main, taxCode.getId(), taxCode.getCode(), AccountingConstant.ACC_ENTITY_TAX, AccountingConstant.GROUP_CURRENT_LIABILITY, company, null, AccountingConstant.LEDGER_CODE_TAX_CESS_LIABILITY);
      // throw new RuntimeException("Begger not implemented 1");
      //SystemRuntimeView.saveLedger(main, taxCode.getId(), taxCode.getCode(), AccountingConstant.ACC_ENTITY_TAX, AccountingConstant.ACC_HEAD_TAX_CESS, company, null);
    } else if (AccountingConstant.TAX_HEAD_TCS.getId().intValue() == taxCode.getTaxHeadId().getId()) {
      saveLedger(main, taxCode.getId(), taxCode.getCode(), AccountingConstant.ACC_ENTITY_TAX, AccountingConstant.GROUP_CURRENT_ASSET, company, null, AccountingConstant.LEDGER_CODE_TAX_TCS_ASSET);
      saveLedger(main, taxCode.getId(), taxCode.getCode(), AccountingConstant.ACC_ENTITY_TAX, AccountingConstant.GROUP_CURRENT_LIABILITY, company, null, AccountingConstant.LEDGER_CODE_TAX_TCS_LIABILITY);
    } else {
      throw new RuntimeException("Begger not implemented 2");
    }
  }

  private static void saveLedger(Main main, Integer entityId, String ledgerName, AccountingEntityType entityType, AccountingGroup acGroup, Company companyId, LedgerObjects ledgerObject, String taxLedgerType) {
    AccountingLedger ald = AccountingLedgerService.selectLedgerByEntity(main, entityId, entityType, companyId.getId());
    if (ald == null) {
      ald = new AccountingLedger();
      ald.setEntityId(entityId);
      ald.setAccountingGroupId(acGroup);
      ald.setAccountingEntityTypeId(entityType);
      ald.setCompanyId(companyId);
      ald.setLedgerCode(taxLedgerType);
      ald.setCountryId(companyId.getCountryId());
      ald.setStateId(companyId.getStateId());
      if (ald.isVendorOrCustomers()) {
        ald.setBillwise(1);
      }
    }
    if (taxLedgerType != null) {
      ald.setTitle(ledgerName + " " + taxLedgerType);
    } else {
      ald.setTitle(ledgerName);
    }
    ald.setLedgerCode(taxLedgerType);
    if (ledgerObject != null) {
      ald.setGstTaxNo(ledgerObject.getGstTaxNo());
      ald.setPin(ledgerObject.getPin());
      ald.setAddress(ledgerObject.getAddress());
      ald.setHsnSacCode(ledgerObject.getHsnSacCode());
      ald.setStateId(ledgerObject.getStateId());
      ald.setIgstId(ledgerObject.getIgstId());
      ald.setCgstId(ledgerObject.getCgstId());
      ald.setSgstId(ledgerObject.getSgstId());
      ald.setCurrencyId(ledgerObject.getCurrencyId());
      ald.setCessId(ledgerObject.getCessId());
      ald.setCountryId(ledgerObject.getCountryId());
    }
    AppService.insert(main, ald);
    // AccountingLedgerService.insertOrUpdate(main, ald);
  }

  private static LedgerObjects getLedgerObject(String gstTaxNo, String pin, String address, State state, Currency currency, Country country) {
    LedgerObjects ledgerObjects = new LedgerObjects();
    ledgerObjects.setGstTaxNo(gstTaxNo);
    ledgerObjects.setPin(pin);
    ledgerObjects.setAddress(address);
    ledgerObjects.setStateId(state);
    ledgerObjects.setCurrencyId(currency);
    ledgerObjects.setCountryId(country);
    return ledgerObjects;
  }

  private static void deleteLedger(Main main, Integer entityId, Integer entityTypeId) {
//    AppService.deleteSql(main, ChequeEntryService.class, "delete from fin_cheque_entry where ledger_id in ( select id from fin_accounting_ledger where entity_id=? and accounting_entity_type_id=? )", new Object[]{entityId, entityTypeId});
    AppService.deleteSql(main, AccountingLedger.class, "delete from fin_accounting_ledger where entity_id=? and accounting_entity_type_id=?", new Object[]{entityId, entityTypeId});//Validation
  }

  public static void createDefaultPrefixAndLedger(Main main, Company company) {
    AccountingPrefixService.insertCompanyAccountingPrefix(main, company);
    AccountingLedgerService.insertCompanyAccountingLedger(main, company);
  }

}
