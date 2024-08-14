/*
 * @(#)VendorBankView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.domain.Account;
import spica.scm.domain.Bank;
import spica.scm.domain.BankAccountType;
import spica.scm.domain.Status;
import spica.scm.domain.Vendor;
import spica.scm.domain.VendorBank;
import spica.scm.domain.VendorBankContact;
import spica.fin.service.BankAccountTypeService;
import spica.scm.service.VendorBankContactService;
import spica.scm.service.VendorBankService;
import spica.scm.view.ScmLookupExtView;
import spica.scm.view.ScmLookupView;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * VendorBankView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "vendorBankView")
@ViewScoped
public class VendorBankView implements Serializable {

  private transient VendorBank vendorBank;	//Domain object/selected Domain.
  private transient LazyDataModel<VendorBank> vendorBankLazyModel; 	//For lazy loading datatable.
  private transient VendorBank[] vendorBankSelected;	 //Selected Domain Array
  private List<VendorBankContact> vendorBankContactList;
  private int activeIndex = 0; //tab active index, reset to 0 when new records

  private Vendor parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Vendor) Jsf.popupParentValue(Vendor.class);
    getVendorBank().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Default Constructor.
   */
  public VendorBankView() {
    super();
  }

  /**
   * Return VendorBank.
   *
   * @return VendorBank.
   */
  public VendorBank getVendorBank() {
    if (vendorBank == null) {
      vendorBank = new VendorBank();
    }
    return vendorBank;
  }

  /**
   * Set VendorBank.
   *
   * @param vendorBank.
   */
  public void setVendorBank(VendorBank vendorBank) {
    this.vendorBank = vendorBank;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchVendorBank(MainView main, String viewType) {
    //this.main = main;
    vendorBankContactList = null;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          activeIndex = 0;
          getVendorBank().reset();
          getVendorBank().setVendorId(parent);
          getVendorBank().setBankAccountTypeId(BankAccountTypeService.selectBankAccountTypeBySortOrder(main));
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setVendorBank((VendorBank) VendorBankService.selectByPk(main, getVendorBank()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadVendorBankList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create vendorBankLazyModel.
   *
   * @param main
   */
  private void loadVendorBankList(final MainView main) {
    if (vendorBankLazyModel == null) {
      vendorBankLazyModel = new LazyDataModel<VendorBank>() {
        private List<VendorBank> list;

        @Override
        public List<VendorBank> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = VendorBankService.listPaged(main, UserRuntimeView.instance().getCompany());
            main.commit(vendorBankLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(VendorBank vendorBank) {
          return vendorBank.getId();
        }

        @Override
        public VendorBank getRowData(String rowKey) {
          if (list != null) {
            for (VendorBank obj : list) {
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
    String SUB_FOLDER = "scm_vendor_bank/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveVendorBank(MainView main) {
    return saveOrCloneVendorBank(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneVendorBank(MainView main) {
    main.setViewType("newform");
    return saveOrCloneVendorBank(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneVendorBank(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            VendorBankService.insertOrUpdate(main, getVendorBank());
//            VendorBankService.makeDefault(main, getVendorBank());
            break;
          case "clone":
            VendorBankService.clone(main, getVendorBank());
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
   * Delete one or many VendorBank.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteVendorBank(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(vendorBankSelected)) {
        VendorBankService.deleteByPkArray(main, getVendorBankSelected()); //many record delete from list
        main.commit("success.delete");
        vendorBankSelected = null;
      } else {
        VendorBankService.deleteByPk(main, getVendorBank());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())) {
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

  /**
   * Return LazyDataModel of VendorBank.
   *
   * @return
   */
  public LazyDataModel<VendorBank> getVendorBankLazyModel() {
    return vendorBankLazyModel;
  }

  /**
   * Return VendorBank[].
   *
   * @return
   */
  public VendorBank[] getVendorBankSelected() {
    return vendorBankSelected;
  }

  /**
   * Set VendorBank[].
   *
   * @param vendorBankSelected
   */
  public void setVendorBankSelected(VendorBank[] vendorBankSelected) {
    this.vendorBankSelected = vendorBankSelected;
  }

  /**
   * Vendor autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.vendorAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.vendorAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Vendor> vendorAuto(String filter) {
    return ScmLookupView.vendorAuto(filter);
  }

  /**
   * BankAccountType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.bankAccountTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.bankAccountTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<BankAccountType> bankAccountTypeAuto(String filter) {
    return ScmLookupView.bankAccountTypeAuto(filter);
  }

  /**
   * Account autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Account> accountAuto(String filter) {
    return ScmLookupView.accountAuto(filter);
  }

  /**
   * Bank autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.bankAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.bankAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Bank> bankAuto(String filter) {
    //return ScmLookupView.bankAuto(filter);
    return ScmLookupExtView.bankAutoFilterByCompanyCountry(UserRuntimeView.instance().getCompany().getCountryId().getId(), filter);
  }

  /**
   * Return all bank contact of a vendor.
   *
   * @param main
   * @return
   */
  public List<VendorBankContact> getVendorBankContactsList(MainView main) {
    if (vendorBankContactList == null) {
      try {
        vendorBankContactList = VendorBankContactService.contactListByVendorBank(main, getVendorBank());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return vendorBankContactList;
  }

  /**
   * Opening add new page in a dialog.
   */
  public void vendorBankContactNewDialog() {
    Jsf.openDialog("/scm/vendor_bank_contact.xhtml", getVendorBank());
  }

  /**
   * Closing the dialog.
   *
   * @param event
   */
  public void vendorBankContactDialogReturn(SelectEvent event) {
    Jsf.closeDialog(getVendorBank());
    vendorBankContactList = null; // Reset to null to fetch updated list
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void vendorBankContactEditDialog(Integer id) {
    Jsf.openDialog("/scm/vendor_bank_contact.xhtml", getVendorBank(), id);
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void vendorBankContactDialogClose() {
    Jsf.popupReturn(parent, null);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }
}
