/*
 * @(#)DebitCreditNoteView.java	1.0 Tue Feb 27 12:40:23 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.fin.domain.AccountingLedger;
import spica.fin.domain.AccountingTransactionDetailItem;
import spica.fin.domain.DebitCreditNote;
import spica.fin.domain.DebitCreditNoteItem;
import spica.fin.domain.DebitCreditNotePlatform;
import spica.fin.domain.TaxCode;
import spica.fin.service.DebitCreditNoteItemService;
import spica.fin.service.DebitCreditNoteService;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Platform;
import spica.fin.service.AccountingLedgerService;
import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Customer;
import spica.scm.domain.Product;
import spica.scm.domain.ProductCategory;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.service.AccountGroupService;
import spica.scm.service.CompanySettingsService;
import spica.scm.service.CustomerService;
import spica.scm.service.PlatformService;
import spica.scm.service.ProductService;
import spica.scm.tax.TaxCalculator;
import spica.scm.view.ScmLookupExtView;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * DebitCreditNoteView
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue Feb 27 12:40:23 IST 2018
 */
@Named(value = "debitCreditNoteView")
@ViewScoped
public class DebitCreditNoteView implements Serializable {
  
  private DebitCreditNote debitCreditNote;	//Domain object/selected Domain.
  private transient LazyDataModel<DebitCreditNote> debitCreditNoteLazyModel; 	//For lazy loading datatable.
  private transient DebitCreditNote[] debitCreditNoteSelected;	 //Selected Domain Array
  // private transient TaxCalculator taxCalculator;
  private Account account;
  private AccountGroup accountGroup;

  // private transient String debitCreditNoteParty;
  private transient List<DebitCreditNoteItem> debitCreditNoteItemList;
  private transient List<DebitCreditNotePlatform> debitCreditNotePlatformList;
  private transient List<Platform> platformList;
  private boolean taxableDebitCreditNoteExist;
  private transient AccountingTransactionDetailItem accountingTransactionDetailItem;

//  private transient CompanySettings companySettings;
  private boolean invoiceWise;
  //private transient boolean intraState;

  /**
   * Default Constructor.
   */
  public DebitCreditNoteView() {
    super();
  }
  
  @PostConstruct
  public void init() {
    MainView main = Jsf.getMain();
    try {
      accountingTransactionDetailItem = (AccountingTransactionDetailItem) Jsf.popupParentValue(AccountingTransactionDetailItem.class);
      Integer debitCreditId = (Integer) Jsf.popupParentValue(Integer.class);
      if (accountingTransactionDetailItem != null) {
        debitCreditNote = DebitCreditNoteService.selectByAccountingTransactionDetailItem(main, accountingTransactionDetailItem.getId());
      } else if (debitCreditId != null) {
        debitCreditNote = DebitCreditNoteService.selectByPk(main, new DebitCreditNote(debitCreditId));
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   * Return DebitCreditNote.
   *
   * @return DebitCreditNote.
   */
  public DebitCreditNote getDebitCreditNote() {
    if (debitCreditNote == null) {
      debitCreditNote = new DebitCreditNote();
    }
    if (debitCreditNote.getCompanyId() == null) {
      debitCreditNote.setCompanyId(UserRuntimeView.instance().getCompany());
      debitCreditNote.setFinancialYearId(debitCreditNote.getCompanyId().getCurrentFinancialYear());
    }
    return debitCreditNote;
  }

  /**
   * Set DebitCreditNote.
   *
   * @param debitCreditNote.
   */
  public void setDebitCreditNote(DebitCreditNote debitCreditNote) {
    this.debitCreditNote = debitCreditNote;
  }
  
  public List<Platform> getPlatformList() {
    return platformList;
  }
  
  public void setPlatformList(List<Platform> platformList) {
    this.platformList = platformList;
  }
  
  public List<DebitCreditNotePlatform> getDebitCreditNotePlatformList() {
    if (debitCreditNotePlatformList == null) {
      debitCreditNotePlatformList = new ArrayList<>();
    }
    return debitCreditNotePlatformList;
  }
  
  public void setDebitCreditNotePlatformList(List<DebitCreditNotePlatform> debitCreditNotePlatformList) {
    this.debitCreditNotePlatformList = debitCreditNotePlatformList;
  }

  /**
   *
   * @return
   */
  private TaxCalculator getTaxCalculator() {
    return SystemRuntimeConfig.getTaxCalculator(getDebitCreditNote().getTaxProcessorId().getProcessorClass());
  }
//
//  public void setTaxCalculator(TaxCalculator taxCalculator) {
//    this.taxCalculator = taxCalculator;
//  }

  public List<DebitCreditNoteItem> getDebitCreditNoteItemList() {
    return debitCreditNoteItemList;
  }
  
  public void setDebitCreditNoteItemList(List<DebitCreditNoteItem> debitCreditNoteItemList) {
    this.debitCreditNoteItemList = debitCreditNoteItemList;
  }

  /**
   * Return LazyDataModel of DebitCreditNote.
   *
   * @return
   */
  public LazyDataModel<DebitCreditNote> getDebitCreditNoteLazyModel() {
    return debitCreditNoteLazyModel;
  }

  /**
   * Return DebitCreditNote[].
   *
   * @return
   */
  public DebitCreditNote[] getDebitCreditNoteSelected() {
    return debitCreditNoteSelected;
  }
  
  public Account getAccount() {
    if (account == null && (isSupplierDebitCreditNote() || isSupplierSalesbill())) {
      account = UserRuntimeView.instance().getAccount();
    }
    return account;
  }
  
  public void setAccount(Account account) {
    getDebitCreditNote().setAccountId(account);
    this.account = account;
  }
  
  public AccountGroup getAccountGroup() {
    if (accountGroup == null && isCustomerDebitCreditNote()) {
      accountGroup = UserRuntimeView.instance().getAccountGroup();
    }
    return accountGroup;
  }
  
  public void setAccountGroup(AccountGroup accountGroup) {
    getDebitCreditNote().setAccountGroupId(accountGroup);
    this.accountGroup = accountGroup;
  }

  /**
   * Set DebitCreditNote[].
   *
   * @param debitCreditNoteSelected
   */
  public void setDebitCreditNoteSelected(DebitCreditNote[] debitCreditNoteSelected) {
    this.debitCreditNoteSelected = debitCreditNoteSelected;
  }
  
  public boolean isTaxableDebitCreditNoteExist() {
    return taxableDebitCreditNoteExist;
  }
  
  public void setTaxableDebitCreditNoteExist(boolean taxableDebitCreditNoteExist) {
    this.taxableDebitCreditNoteExist = taxableDebitCreditNoteExist;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @param invoiceType
   * @param debitCreditNoteParty
   * @return
   */
  public String switchDebitCreditNote(MainView main, String viewType, String invoiceType, String debitCreditNoteParty) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (!StringUtil.isEmpty(invoiceType)) {
          getDebitCreditNote().setInvoiceType(Integer.parseInt(invoiceType));
          getDebitCreditNote().setDebitCreditParty(Integer.parseInt(debitCreditNoteParty));
        }
        if (main.isNew() && !main.hasError()) {
          getDebitCreditNote().reset();
          getDebitCreditNote().setDebitCreditParty(Integer.parseInt(debitCreditNoteParty));
          getDebitCreditNote().setInvoiceType(Integer.parseInt(invoiceType));
          getDebitCreditNote().setEntryDate(SystemRuntimeConfig.getMaxEntryDate(getDebitCreditNote().getCompanyId()));
          // getDebitCreditNote().setInvoiceDate(new Date());
          getDebitCreditNote().setAccountId(getAccount());
          getDebitCreditNote().setAccountGroupId(getAccountGroup());
          setSupplierDebitCreditAccountGroup(main);
          getDebitCreditNote().setTaxableInvoice(SystemConstants.TAXABLE_DEBIT_CREDIT_NOTE);
          getDebitCreditNote().setTaxProcessorId(getDebitCreditNote().getCompanyId().getCountryTaxRegimeId().getTaxProcessorId());
          // setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getDebitCreditNote().getTaxProcessorId().getProcessorClass()));
          setDebitCreditNoteItemList(null);
          if (debitCreditNoteParty.equals("1")) {
            getDebitCreditNote().setSezZone((getAccount().getVendorId().getTaxable() != null && getAccount().getVendorId().getTaxable().intValue() == 2) ? 1 : 0);
          }
          if (isSupplierDebitCreditNote() || isSupplierSalesbill()) {
            getDebitCreditNote().setAccountingLedgerId(AccountingLedgerService.selectSupplierAccountingLedger(main, getAccount().getVendorId()));
            interState(getDebitCreditNote().getAccountingLedgerId());
            setDebitCreditNoteType(getDebitCreditNote().getIsInterstate(), getDebitCreditNote().getSezZone());
            
          }
//          boolean inter = (getDebitCreditNote().getIsInterstate() != null && SystemConstants.INTERSTATE.intValue() == getDebitCreditNote().getIsInterstate()) ? true : false;
//          if (inter || (getDebitCreditNote().getSezZone() != null && getDebitCreditNote().getSezZone().intValue() == 1)) {
//            setIntraState(false);
//          } else {
//            setIntraState(true);
//          }
          DebitCreditNoteService.selectDebitCreditNoteInvoiceNo(main, getDebitCreditNote(), true);
        } else if (main.isEdit() && !main.hasError()) {
          getDebitCreditNote().setPlatformList(null);
          getDebitCreditNote().setDebitCreditNoteItemList(null);
          getDebitCreditNote().setDebitCreditNotePlatformList(null);
          setDebitCreditNote((DebitCreditNote) DebitCreditNoteService.selectByPk(main, getDebitCreditNote()));
          setDebitCreditNoteItemList(DebitCreditNoteItemService.selectByDebitCreditNote(main, getDebitCreditNote()));
          //  setTaxCalculator(SystemRuntimeConfig.getTaxCalculator(getDebitCreditNote().getTaxProcessorId().getProcessorClass()));
          setDebitCreditNoteType(getDebitCreditNote().getIsInterstate(), getDebitCreditNote().getSezZone());
          getTaxCalculator().processDebitNoteCalculation(getDebitCreditNote(), getDebitCreditNoteItemList());
          
          if (StringUtil.isEmpty(getDebitCreditNoteItemList())) {
            addNewDebitCreditNoteItem();
          }
          setSupplierDebitCreditAccountGroup(main);
        } else if (main.isList()) {
          // getDebitCreditNote().reset();
          main.getPageData().setSearchKeyWord(null);
          setDebitCreditNoteItemList(null);
          getDebitCreditNote().setCompanyId(null);
          setAccount(null);
          setAccountGroup(null);
          setDebitCreditNotePlatformList(null);
          loadDebitCreditNoteList(main);
        }
        CompanySettingsService.selectIfNull(main, getDebitCreditNote().getCompanyId());
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create debitCreditNoteLazyModel.
   *
   * @param main
   */
  private void loadDebitCreditNoteList(final MainView main) {
    if (debitCreditNoteLazyModel == null) {
      debitCreditNoteLazyModel = new LazyDataModel<DebitCreditNote>() {
        private List<DebitCreditNote> list;
        
        @Override
        public List<DebitCreditNote> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            if (getDebitCreditNote().getAccountGroupId() == null) {
              debitCreditNote.setAccountGroupId(getAccountGroup());
            }
            if (getDebitCreditNote().getAccountId() == null) {
              debitCreditNote.setAccountId(getAccount());
            }
            if (debitCreditNote.getAccountGroupId() != null || debitCreditNote.getAccountId() != null) {
              list = DebitCreditNoteService.listPagedByParty(main, getDebitCreditNote());
            }
            main.commit(debitCreditNoteLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }
        
        @Override
        public Object getRowKey(DebitCreditNote debitCreditNote) {
          return debitCreditNote.getId();
        }
        
        @Override
        public DebitCreditNote getRowData(String rowKey) {
          if (list != null) {
            for (DebitCreditNote obj : list) {
              if (rowKey.equals(obj.getId().toString())) {
                return obj;
              }
            }
          }
          return null;
        }
      };
    }
  }
  
  private void uploadFiles() {
    String SUB_FOLDER = "fin_debit_credit_note/";
  }

//  private boolean isDebitCreditNoteConfirmable() {
//    boolean rvalue = false;
//    if (!StringUtil.isEmpty(getDebitCreditNoteItemList())) {
//      for (DebitCreditNoteItem debitCreditNoteItem : getDebitCreditNoteItemList()) {
//        if (!StringUtil.isEmpty(debitCreditNoteItem.getTitle()) && debitCreditNoteItem.getTaxableValue() != null) {
//          rvalue = true;
//          break;
//        }
//      }
//    }
//    return rvalue;
//  }
  /**
   *
   */
  public void addNewDebitCreditNoteItem() {
    DebitCreditNoteItem item = new DebitCreditNoteItem();
    item.setDebitCreditNoteId(getDebitCreditNote());
    getDebitCreditNoteItemList().add(0, item);
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveDebitCreditNote(MainView main, Integer status) {
    if (SystemConstants.CONFIRMED.intValue() == status) {
      if (getDebitCreditNote().getInvoiceGroup().size() == 0) {
        main.error("error.actual.line.item.missing");
        return null;
      }
    }
    getDebitCreditNote().setStatusId(status);
    return saveOrCloneDebitCreditNote(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneDebitCreditNote(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneDebitCreditNote(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneDebitCreditNote(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            getTaxCalculator().processDebitNoteCalculation(getDebitCreditNote(), getDebitCreditNoteItemList());
            DebitCreditNoteService.insertOrUpdate(main, getDebitCreditNote(), getDebitCreditNoteItemList());
            if (SystemConstants.CONFIRMED.intValue() == getDebitCreditNote().getStatusId()) {
              // Outstanding entry
              getDebitCreditNote().setDebitCreditNoteItemList(getDebitCreditNoteItemList());
              getTaxCalculator().saveDebitCreditNote(main, getDebitCreditNote());
            }
            break;
          case "clone":
            DebitCreditNoteService.selectDebitCreditNoteInvoiceNo(main, getDebitCreditNote(), true);
            getDebitCreditNote().setStatusId(SystemConstants.DRAFT);
            DebitCreditNoteService.clone(main, getDebitCreditNote(), getDebitCreditNoteItemList());
            
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   * @param main
   * @return
   */
  public String insertOrUpdatedebitCreditNoteItem(MainView main) {
    boolean isNew = false;
    try {
      Integer index = Jsf.getParameterInt("rownumber");
      DebitCreditNoteItem debitCreditNoteItem = getDebitCreditNoteItemList().get(index);
      if (debitCreditNoteItem != null && debitCreditNoteItem.getTitle() != null && debitCreditNoteItem.getTaxableValue() != null) {
        if (debitCreditNoteItem.getId() == null) {
          isNew = true;
        }
        getTaxCalculator().processDebitNoteCalculation(debitCreditNote, debitCreditNoteItemList);
        debitCreditNoteItem = debitCreditNoteItemList.get(index);
        DebitCreditNoteItemService.insertOrUpdate(main, debitCreditNoteItem);
        
        if (!isTaxableDebitCreditNoteExist() && debitCreditNoteItem.getTaxCodeId() != null) {
          setTaxableDebitCreditNoteExist(true);
        }
        
        if (isNew || index == 0) {
          addNewDebitCreditNoteItem();
        }
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many DebitCreditNote.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteDebitCreditNote(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(debitCreditNoteSelected)) {
        DebitCreditNoteService.deleteByPkArray(main, getDebitCreditNoteSelected()); //many record delete from list
        main.commit("success.delete");
        debitCreditNoteSelected = null;
      } else {
        DebitCreditNoteService.deleteByPk(main, getDebitCreditNote());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (main.isEdit()) {
          main.setViewType(ViewTypes.newform);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }
  
  public void actionDeleteDebitCreditNoteItem(MainView main, DebitCreditNoteItem debitCreditNoteItem) {
    try {
      if (debitCreditNoteItem != null && debitCreditNoteItem.getId() != null && debitCreditNoteItem.getId() > 0) {
        DebitCreditNoteItemService.deleteByPk(main, debitCreditNoteItem);
        getDebitCreditNoteItemList().remove(debitCreditNoteItem);
        updatePlatFormList(getDebitCreditNotePlatformList(), debitCreditNoteItem);
        getTaxCalculator().processDebitNoteCalculation(debitCreditNote, debitCreditNoteItemList);
        DebitCreditNoteService.insertOrUpdate(main, debitCreditNote);
      } else {
        getDebitCreditNoteItemList().remove(debitCreditNoteItem);
        getTaxCalculator().processDebitNoteCalculation(debitCreditNote, debitCreditNoteItemList);
      }
      if (getDebitCreditNoteItemList().isEmpty()) {
        addNewDebitCreditNoteItem();
      }
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }
  
  public void updatePlatFormList(List<DebitCreditNotePlatform> list, DebitCreditNoteItem debitCreditNoteItem) {
    if (list.size() > 0) {
      Iterator<DebitCreditNotePlatform> it = list.iterator();
      while (it.hasNext()) {
        DebitCreditNotePlatform platForm = it.next();
        if (platForm.getDebitCreditNoteItemId().getId() == debitCreditNoteItem.getId()) {
          it.remove();
        }
      }
    }
  }
  
  public List<Product> productAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      if (isCustomerDebitCreditNote()) { //2
        return DebitCreditNoteService.getProductListByLedgerAndAccountGroup(main, getDebitCreditNote().getAccountingLedgerId().getEntityId(), getDebitCreditNote().getAccountGroupId(), filter);
      } else if (isSupplierDebitCreditNote() || isSupplierSalesbill()) { //1
        return DebitCreditNoteService.getProductPurchaseByLedgerAndAccount(main, getDebitCreditNote(), filter);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }
  
  public List<ServiceCommodity> serviceAuto(String filter) {
    return ScmLookupExtView.lookupServiceCommodity(getDebitCreditNote().getCompanyId(), filter);
  }
  
  public List<ProductCategory> categoryAuto(String filter) {
    return ScmLookupExtView.lookupProductCategoryAuto(filter);
  }
  
  public List<SalesInvoiceItem> getSalesInvoiceList(MainView main, DebitCreditNoteItem debitCreditNoteItem) {
    try {
      if (isCustomerDebitCreditNote() && debitCreditNoteItem.getSalesInvoiceItemList() == null) {
        if (debitCreditNoteItem != null && debitCreditNoteItem.getProductId() != null) {
          getDebitCreditNote().getAccountingLedgerId();
          if (getDebitCreditNote().getAccountingLedgerId().isCustomer()) {
            debitCreditNoteItem.setSalesInvoiceItemList(DebitCreditNoteService.selectInvoiceByProductAndAccountGroupAndCustomer(main, debitCreditNoteItem.getProductId(), getDebitCreditNote().getAccountGroupId(), getDebitCreditNote().getAccountingLedgerId().getEntityId()));
          }
          
        }
      }
      return debitCreditNoteItem.getSalesInvoiceItemList();
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }
  
  public List<ProductEntryDetail> getProductInvoiceList(MainView main, DebitCreditNoteItem debitCreditNoteItem) {
    try {
      if (isSupplierDebitCreditNote() || isSupplierSalesbill()) {
        if (debitCreditNoteItem != null && debitCreditNoteItem.getProductId() != null && debitCreditNoteItem.getProductEntryDetailList() == null) {
          debitCreditNoteItem.setProductEntryDetailList(DebitCreditNoteService.getPurchaseProductInvoice(main, debitCreditNoteItem.getProductId(), getDebitCreditNote().getAccountId()));
        }
      }
      return debitCreditNoteItem.getProductEntryDetailList();
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }
  
  public void updateSalesInvoiceRef(DebitCreditNoteItem debitCreditNoteItem) {
    debitCreditNoteItem.setRefInvoiceNo(debitCreditNoteItem.getSalesInvoiceItemId() == null ? null : debitCreditNoteItem.getSalesInvoiceItemId().getSalesInvoiceId().getInvoiceNo());
    debitCreditNoteItem.setRefInvoiceDate(debitCreditNoteItem.getSalesInvoiceItemId() == null ? null : debitCreditNoteItem.getSalesInvoiceItemId().getSalesInvoiceId().getInvoiceDate());
  }
  
  public void updateProductInvoiceRef(DebitCreditNoteItem debitCreditNoteItem) {
    debitCreditNoteItem.setRefInvoiceNo(debitCreditNoteItem.getProductEntryDetailId() == null ? null : debitCreditNoteItem.getProductEntryDetailId().getProductEntryId().getAccountInvoiceNo());
    debitCreditNoteItem.setRefInvoiceDate(debitCreditNoteItem.getProductEntryDetailId() == null ? null : debitCreditNoteItem.getProductEntryDetailId().getProductEntryId().getInvoiceDate());
  }
  
  public void updateHsnCodeByProduct(DebitCreditNoteItem debitCreditNoteItem) {
    Product product = debitCreditNoteItem.getProductId();
    setHsnCode(debitCreditNoteItem, product.getProductName(), product.getHsnCode());
    debitCreditNoteItem.setProductId(product);
    debitCreditNoteItem.setRefInvoiceDate(null);
  }
  
  public void updateHsnCodeByService(DebitCreditNoteItem debitCreditNoteItem) {
    ServiceCommodity serviceCommodity = debitCreditNoteItem.getServiceId();
    setHsnCode(debitCreditNoteItem, serviceCommodity.getTitle(), serviceCommodity.getHsnSacCode());
    debitCreditNoteItem.setServiceId(serviceCommodity);
  }
  
  public void updateHsnCodeByCategory(DebitCreditNoteItem debitCreditNoteItem) {
    ProductCategory productCategory = debitCreditNoteItem.getProductCategoryId();
    setHsnCode(debitCreditNoteItem, productCategory.getTitle(), productCategory.getHsnCode());
    debitCreditNoteItem.setProductCategoryId(productCategory);
  }
  
  private void setHsnCode(DebitCreditNoteItem debitCreditNoteItem, String title, String hsn) {
    debitCreditNoteItem.resetValues();
    debitCreditNoteItem.setTitle(title);
    debitCreditNoteItem.setHsnSacCode(hsn);
    // debitCreditNoteItem.setRefInvoiceDate(null);
    if (isCustomerDebitCreditNote()) {
      debitCreditNoteItem.setSalesInvoiceItemList(null);
    } else if (isSupplierDebitCreditNote() || isSupplierSalesbill()) {
      debitCreditNoteItem.setProductEntryDetailList(null);
    }
    debitCreditNoteItem.setInvoiceWise(getDebitCreditNote().getCompanyId().getCompanySettings().getInvoiceWise());
  }
  
  public void resetToDraft(MainView main) {
    try {
      DebitCreditNoteService.resetToDraft(main, getDebitCreditNote());
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }
  
  public boolean isDraft() {
    return getDebitCreditNote() != null && getDebitCreditNote().getStatusId() != null && SystemConstants.DRAFT.equals(getDebitCreditNote().getStatusId());
  }
  
  public boolean isConfirmed() {
    return getDebitCreditNote() != null && getDebitCreditNote().getStatusId() != null && SystemConstants.CONFIRMED.equals(getDebitCreditNote().getStatusId());
  }
  
  public boolean isCustomerDebitCreditNote() {
    return (!StringUtil.isEmpty(getDebitCreditNote().getDebitCreditParty().toString()) && DebitCreditNoteService.CUSTOMER_DEBIT_CREDIT_NOTE.intValue() == getDebitCreditNote().getDebitCreditParty());
  }
  
  public boolean isSupplierDebitCreditNote() {
    return (!StringUtil.isEmpty(getDebitCreditNote().getDebitCreditParty().toString()) && DebitCreditNoteService.SUPPLIER_DEBIT_CREDIT_NOTE.intValue() == getDebitCreditNote().getDebitCreditParty());
  }
  
  public boolean isSupplierSalesbill() {
    return (!StringUtil.isEmpty(getDebitCreditNote().getDebitCreditParty().toString()) && DebitCreditNoteService.SUPPLIER_SALES_BILL.intValue() == getDebitCreditNote().getDebitCreditParty());
  }
  
  public void taxableRateBlurEvent(DebitCreditNoteItem debitCreditNoteItem) {
    getTaxCalculator().processDebitNoteCalculation(getDebitCreditNote(), getDebitCreditNoteItemList());
  }
  
  public void taxRateSelectEvent(DebitCreditNoteItem debitCreditNoteItem) {
    debitCreditNoteItem.setIgstAmount(null);
    debitCreditNoteItem.setSgstAmount(null);
    debitCreditNoteItem.setCgstAmount(null);
    getTaxCalculator().processDebitNoteCalculation(getDebitCreditNote(), getDebitCreditNoteItemList());
  }
  
  public void taxableInvoiceChangeEvent() {
    if (SystemConstants.NON_TAXABLE_DEBIT_CREDIT_NOTE.equals(getDebitCreditNote().getTaxableInvoice())) {
      if (!StringUtil.isEmpty(getDebitCreditNoteItemList())) {
        for (DebitCreditNoteItem debitCreditNoteItem : getDebitCreditNoteItemList()) {
          debitCreditNoteItem.setCgstAmount(null);
          debitCreditNoteItem.setIgstAmount(null);
          debitCreditNoteItem.setNetValue(null);
          debitCreditNoteItem.setSgstAmount(null);
          debitCreditNoteItem.setTaxCodeId(null);
          debitCreditNoteItem.setTaxValue(null);
        }
      }
    }
    getTaxCalculator().processDebitNoteCalculation(getDebitCreditNote(), getDebitCreditNoteItemList());
  }

  /**
   * AccountingLedger autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountingLedgerAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountingLedgerAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<AccountingLedger> accountingLedgerAuto(String filter) {
    Integer entityFromId = null;
    entityFromId = getAccount() != null ? getAccount().getId() : getAccountGroup() != null ? getAccountGroup().getId() : null;
    return FinLookupView.ledgerByEntityAuto(filter, getDebitCreditNote().getCompanyId().getId(), entityFromId, getDebitCreditNote().getDebitCreditParty().toString());
  }

  /**
   *
   * @return
   */
  public List<TaxCode> lookupGstTaxCode(MainView main, DebitCreditNoteItem debitCreditNoteItem) {
    try {
      
      List<TaxCode> codeList = new ArrayList<>();
      if (debitCreditNoteItem != null && debitCreditNoteItem.getProductId() != null) {
        if (isCustomerDebitCreditNote()) {
          codeList.add(ProductService.getProductSalesTaxCode(main, debitCreditNoteItem.getProductId()));
        } else if (isSupplierDebitCreditNote() || isSupplierSalesbill()) {
          codeList.add(ProductService.getProductPurchaseTaxCode(main, debitCreditNoteItem.getProductId()));
        }
      } else if (debitCreditNoteItem.getServiceId() != null) {
        codeList.add(DebitCreditNoteService.selectTaxCodeByCommodity(main, debitCreditNoteItem.getServiceId()));
      } else if (debitCreditNoteItem.getProductCategoryId() != null) {
        codeList.add(DebitCreditNoteService.selectTaxCodeByCategory(main, debitCreditNoteItem.getProductCategoryId()));
      } else {
        return ScmLookupExtView.lookupGstTaxCode(getDebitCreditNote().getCompanyId());
      }
      debitCreditNoteItem.setTaxCodeId(codeList.get(0));
      taxableRateBlurEvent(debitCreditNoteItem); //FIXME godson do we need to call this here
      return codeList;
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }
  
  public List<Account> lookupAccount() {
    if (getAccountGroup() != null) {
      return ScmLookupExtView.lookupAccountByAccountGroup(getAccountGroup());
    }
    return null;
  }
  
  public boolean isSalesDebitCreditNote() {
    if (getDebitCreditNote().getDebitCreditParty() != null && getDebitCreditNote().getDebitCreditParty() == SystemConstants.SALES_DEBIT_CREDIT_NOTE.intValue()) {
      List<Account> accountList = lookupAccount();
      if (accountList.size() == 1) {
        getDebitCreditNote().setAccountId(accountList.get(0));
      }
      return true;
    }
    return false;
  }

  /**
   *
   * @param event
   */
  public void ledgerSelectEvent(SelectEvent event) {
    MainView main = Jsf.getMain();
    try {
      AccountingLedger le = (AccountingLedger) event.getObject();
      DebitCreditNoteService.selectDebitCreditNoteInvoiceNo(main, getDebitCreditNote(), true);
      if (le != null && getDebitCreditNote().getDebitCreditParty().intValue() == 2) {
        Customer cust = CustomerService.selectByPk(main, new Customer(le.getEntityId()));
        getDebitCreditNote().setSezZone((cust.getTaxable() != null && !StringUtil.isEmpty(cust.getGstNo()) && cust.getTaxable() == 2) ? 1 : 0);
        interState(le);
//        if (SystemRuntimeConfig.isInterstateBusiness(getCompany().getStateId().getStateCode(), cust.getStateId().getStateCode())) {
//          getDebitCreditNote().setIsInterstate(SystemConstants.INTERSTATE);
//        } else {
//          getDebitCreditNote().setIsInterstate(SystemConstants.INTRASTATE);
//        }
      }
      setDebitCreditNoteType(getDebitCreditNote().getIsInterstate(), getDebitCreditNote().getSezZone());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }
  
  private void interState(AccountingLedger accountingLedger) {
    if (accountingLedger != null) {
      if (accountingLedger.getStateId() != null) {
        if (SystemRuntimeConfig.isInterstateBusiness(getDebitCreditNote().getCompanyId().getStateId().getStateCode(), accountingLedger.getStateId().getStateCode())) {
          getDebitCreditNote().setIsInterstate(SystemConstants.INTERSTATE);
        } else {
          getDebitCreditNote().setIsInterstate(SystemConstants.INTRASTATE);
        }
//        if (accountingLedger.getStateId().getId() == getCompany().getStateId().getId().intValue()) {
//          getDebitCreditNote().setIsInterstate(SystemConstants.INTRASTATE);
//        } else {
//          getDebitCreditNote().setIsInterstate(SystemConstants.INTERSTATE);
//        }
      }
    }
  }
  
  private void setDebitCreditNoteType(Integer interstate, Integer sezZone) {
    //  setInterstate(false);
    if (interstate.intValue() == 1 || sezZone.intValue() == 1) {
      getDebitCreditNote().setSalesArea(SystemConstants.INTERSTATE);
    } else {
      getDebitCreditNote().setSalesArea(SystemConstants.INTRASTATE);
    }
  }

//  public boolean isIntraState() {
//    return intraState;
//  }
//
//  public void setIntraState(boolean intraState) {
//    this.intraState = intraState;
//  }
  public void platformPopup() {
    Jsf.popupList(FileConstant.PLATFORM, getDebitCreditNote());
  }

  /**
   *
   * @param main
   */
  public void platformPopupReturn(MainView main) {
    try {
      setDebitCreditNote((DebitCreditNote) Jsf.popupParentValue(DebitCreditNote.class));
      if (!StringUtil.isEmpty(getDebitCreditNote().getDebitCreditNoteItemList())) {
        getDebitCreditNoteItemList().addAll(getDebitCreditNote().getDebitCreditNoteItemList());
        getDebitCreditNotePlatformList().addAll(getDebitCreditNote().getDebitCreditNotePlatformList());
      }
      getTaxCalculator().processDebitNoteCalculation(getDebitCreditNote(), getDebitCreditNoteItemList());
      DebitCreditNoteItemService.insertOrUpdate(main, getDebitCreditNoteItemList(), getDebitCreditNotePlatformList());
      PlatformService.updatePlatformStatus(main, getDebitCreditNote().getPlatformList(), SystemRuntimeConfig.PLATFORM_STATUS_PROCESSING.getId());
      DebitCreditNoteService.insertOrUpdate(main, getDebitCreditNote());
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param context
   * @param component
   * @param value
   * @throws ValidatorException
   */
  public void validateConfirmedNetValue(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    String strStatus = Jsf.getParameter("status");
    Integer status = StringUtil.isEmpty(strStatus) ? null : (Integer.parseInt(strStatus));
    if (status != null && SystemConstants.CONFIRMED.equals(status) && value == null) {
      Jsf.error(component, "netamount.required");
    }
  }
  
  public void validateInvoice(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    String strStatus = Jsf.getParameter("status");
    if (isSupplierDebitCreditNote() || isSupplierSalesbill()) {
      Integer status = StringUtil.isEmpty(strStatus) ? null : (Integer.parseInt(strStatus));
      if (status != null && SystemConstants.CONFIRMED.equals(status) && (value == null || "".equals(value.toString()))) {
        Jsf.error(component, "documentno.required");
      }
    }
  }
  
  public void validateInvoiceDate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    String strStatus = Jsf.getParameter("status");
    if (isSupplierDebitCreditNote() || isSupplierSalesbill()) {
      Integer status = StringUtil.isEmpty(strStatus) ? null : (Integer.parseInt(strStatus));
      if (null != value) {
        Date date = (Date) value;
        Date from = getNow().getTime();
        if (date.after(new Date()) && !date.toString().equals(from.toString())) {
          Jsf.error(component, "date.lte.today", null);
        }
      }
      
      if (status != null && SystemConstants.CONFIRMED.equals(status) && value == null) {
        Jsf.error(component, "documentdate.required");
      }
    }
  }
  
  private Calendar getNow() {
    Calendar now = Calendar.getInstance();
    now.set(Calendar.HOUR, 0);
    now.set(Calendar.MINUTE, 0);
    now.set(Calendar.SECOND, 0);
    now.set(Calendar.HOUR_OF_DAY, 0);
    return now;
  }
  
  public void actionSaveDebitCreditNote(MainView main) {
    try {
      DebitCreditNoteService.insertOrUpdate(main, debitCreditNote);
      if (isSupplierDebitCreditNote() || isSupplierSalesbill()) {
        double difference = getDebitCreditNote().getConfirmedNetValue() - getDebitCreditNote().getNetValue();
        if (difference != 0) {
          if (difference > 0) {
            PlatformService.insertDebitCreditNote(main, getDebitCreditNote().getAccountId(), SystemRuntimeConfig.PLATFORM_DIFFERENCE, null, difference, getDebitCreditNote(), PlatformService.NORMAL_FUND_STATE);
          } else {
            PlatformService.insertDebitCreditNote(main, getDebitCreditNote().getAccountId(), SystemRuntimeConfig.PLATFORM_DIFFERENCE, difference, null, getDebitCreditNote(), PlatformService.NORMAL_FUND_STATE);
          }
        }
      }
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }
  
  public boolean isPlatformDeletable(MainView main) {
    try {
      if (getDebitCreditNote().getConfirmedNetValue() != null && getDebitCreditNote().getConfirmedNetValue() > 0) {
        Platform platform = DebitCreditNoteService.selectPlatFormInSettlementByDebitCredit(main, getDebitCreditNote());
        if (platform == null || platform.getId() == null) {
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
      
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return false;
  }
  
  public void actionDeletePlatform(MainView main) {
    try {
      getDebitCreditNote().setConfirmedNetValue(null);
      DebitCreditNoteService.insertOrUpdate(main, getDebitCreditNote());
      PlatformService.deleteByDebitCreditNote(main, getDebitCreditNote());
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }
  
  public void createPrintPortrait(String key) {
    getDebitCreditNote().setPrintType(SystemConstants.PRINT_PORTRAIT);
    if (SystemConstants.PDF_ITEXT.equals(key)) {
      Jsf.popupForm(getTaxCalculator().getDebitCreditNotePrintIText(), getDebitCreditNote());
    } else if (SystemConstants.PDF_KENDO.equals(key)) {
      Jsf.popupForm(getTaxCalculator().getDebitCreditNotePrint(), getDebitCreditNote());
    }
  }
  
  public void ledgerPopup(AccountingLedger accountingLedger) {
    Jsf.popupForm(FileConstant.ACCOUNTING_LEDGER, accountingLedger, accountingLedger.getId());
  }
  
  public void updateDocumentNoByAccount() {
    MainView main = Jsf.getMain();
    try {
      if (getDebitCreditNote().getAccountId() != null) {
        DebitCreditNoteService.selectDebitCreditNoteInvoiceNo(main, getDebitCreditNote(), true);
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }
  
  public void openProduct() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      // opens a new form if id is null else edit
      Jsf.popupForm(FileConstant.PRODUCT, new Product(), null);
    }
  }
  
  public void openServices() {
    if (UserRuntimeView.instance().getReferencePopupAllowed()) {
      // opens a new form if id is null else edit
      Jsf.popupForm(FileConstant.SERVICES, new ServiceCommodity(), null);
    }
  }
  
  public boolean isInvoiceWise() {
    if (getDebitCreditNote().getCompanyId().getCompanySettings().getInvoiceWise() != null) {
      invoiceWise = getDebitCreditNote().getCompanyId().getCompanySettings().getInvoiceWise() == SystemConstants.SHOW_INVOICE_WISE ? true : false;
    }
    if (isSupplierDebitCreditNote()) {
      invoiceWise = false;
    }
    return invoiceWise;
  }
  
  public void setInvoiceWise(boolean invoiceWise) {
    this.invoiceWise = invoiceWise;
  }
  
  public List<SalesInvoice> selectInvoiceAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      return DebitCreditNoteService.selectInvoiceByCustomer(main, getDebitCreditNote().getAccountingLedgerId().getEntityId(),
              getDebitCreditNote().getAccountGroupId(), filter);
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return null;
  }
  
  public List<Product> selectProductList(MainView main) {
    try {
      if (getDebitCreditNote().getSalesInvoice() != null && isCustomerDebitCreditNote()) {
        return DebitCreditNoteService.selectProductBySalesInvoice(main, getDebitCreditNote().getSalesInvoice());
      } else if (getDebitCreditNote().getProductEntry() != null && isSupplierDebitCreditNote()) {
        return DebitCreditNoteService.selectProductBySupplierInvoice(main, getDebitCreditNote().getProductEntry());
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return null;
  }
  
  public void updateLineItemByProduct(DebitCreditNoteItem debitCreditNoteItem) {
    if (isCustomerDebitCreditNote()) {
      debitCreditNoteItem.setServiceId(null);
      debitCreditNoteItem.setProductCategoryId(null);
      debitCreditNoteItem.setTitle(debitCreditNoteItem.getProductId().getProductName());
      debitCreditNoteItem.setHsnSacCode(debitCreditNoteItem.getProductId().getHsnCode());
      debitCreditNoteItem.setRefInvoiceNo(getDebitCreditNote().getSalesInvoice().getInvoiceNo());
      debitCreditNoteItem.setRefInvoiceDate(getDebitCreditNote().getSalesInvoice().getInvoiceEntryDate());
      debitCreditNoteItem.setSalesInvoiceItemList(null);
    }
    if (isSupplierDebitCreditNote()) {
      debitCreditNoteItem.setServiceId(null);
      debitCreditNoteItem.setProductCategoryId(null);
      debitCreditNoteItem.setTitle(debitCreditNoteItem.getProductId().getProductName());
      debitCreditNoteItem.setHsnSacCode(debitCreditNoteItem.getProductId().getHsnCode());
      debitCreditNoteItem.setRefInvoiceNo(getDebitCreditNote().getProductEntry().getAccountInvoiceNo());
      debitCreditNoteItem.setRefInvoiceDate(getDebitCreditNote().getProductEntry().getInvoiceDate());
    }
    debitCreditNoteItem.setInvoiceWise(getDebitCreditNote().getCompanyId().getCompanySettings().getInvoiceWise());
  }
  
  public List<ProductEntry> selectInvoiceBySupplierAuto(String filter) {
    MainView main = Jsf.getMain();
    try {
      return DebitCreditNoteService.selectInvoiceBySupplier(main, getDebitCreditNote().getAccountId(), filter);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }
  
  private void setSupplierDebitCreditAccountGroup(MainView main) {
    try {
      if (isSupplierDebitCreditNote()) {
        if (getDebitCreditNote().getAccountGroupId() == null && getDebitCreditNote().getAccountId() != null) {
          getDebitCreditNote().setAccountGroupId(AccountGroupService.selectDefaultAccountGroupByAccount(main, getDebitCreditNote().getAccountId()));
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }
  
}
