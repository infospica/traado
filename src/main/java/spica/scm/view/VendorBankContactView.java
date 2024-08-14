/*
 * @(#)VendorBankContactView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.VendorBankContact;
import spica.scm.service.VendorBankContactService;
import spica.scm.domain.VendorBank;
import spica.scm.domain.Account;
import spica.scm.domain.Designation;
import spica.scm.domain.Status;
import spica.scm.service.DesignationService;
import wawo.app.faces.Jsf;

/**
 * VendorBankContactView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "vendorBankContactView")
@ViewScoped
public class VendorBankContactView implements Serializable {

  private transient VendorBankContact vendorBankContact;	//Domain object/selected Domain.
  private transient LazyDataModel<VendorBankContact> vendorBankContactLazyModel; 	//For lazy loading datatable.
  private transient VendorBankContact[] vendorBankContactSelected;	 //Selected Domain Array

  private VendorBank parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (VendorBank) Jsf.popupParentValue(VendorBank.class);
    getVendorBankContact().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Default Constructor.
   */
  public VendorBankContactView() {
    super();
  }

  public VendorBank getParent() {
    return parent;
  }

  public void setParent(VendorBank parent) {
    this.parent = parent;
  }

  /**
   * Return VendorBankContact.
   *
   * @return VendorBankContact.
   */
  public VendorBankContact getVendorBankContact() {
    if (vendorBankContact == null) {
      vendorBankContact = new VendorBankContact();
    }
    return vendorBankContact;
  }

  /**
   * Set VendorBankContact.
   *
   * @param vendorBankContact.
   */
  public void setVendorBankContact(VendorBankContact vendorBankContact) {
    this.vendorBankContact = vendorBankContact;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchVendorBankContact(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        getVendorBankContact().setVendorBankId(parent);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getVendorBankContact().reset();
          getVendorBankContact().setVendorBankId(parent);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setVendorBankContact((VendorBankContact) VendorBankContactService.selectByPk(main, getVendorBankContact()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadVendorBankContactList(main);
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
   * Create vendorBankContactLazyModel.
   *
   * @param main
   */
  private void loadVendorBankContactList(final MainView main) {
    if (vendorBankContactLazyModel == null) {
      vendorBankContactLazyModel = new LazyDataModel<VendorBankContact>() {
        private List<VendorBankContact> list;

        @Override
        public List<VendorBankContact> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = VendorBankContactService.listPaged(main, parent);
            main.commit(vendorBankContactLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(VendorBankContact vendorBankContact) {
          return vendorBankContact.getId();
        }

        @Override
        public VendorBankContact getRowData(String rowKey) {
          if (list != null) {
            for (VendorBankContact obj : list) {
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
    String SUB_FOLDER = "scm_vendor_bank_contact/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveVendorBankContact(MainView main) {
    return saveOrCloneVendorBankContact(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneVendorBankContact(MainView main) {
    main.setViewType("newform");
    return saveOrCloneVendorBankContact(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneVendorBankContact(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            VendorBankContactService.insertOrUpdate(main, getVendorBankContact());
//            VendorBankContactService.makeDefault(main, getVendorBankContact());
            break;
          case "clone":
            VendorBankContactService.clone(main, getVendorBankContact());
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
   * Delete one or many VendorBankContact.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteVendorBankContact(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(vendorBankContactSelected)) {
        VendorBankContactService.deleteByPkArray(main, getVendorBankContactSelected()); //many record delete from list
        main.commit("success.delete");
        vendorBankContactSelected = null;
      } else {
        VendorBankContactService.deleteByPk(main, getVendorBankContact());  //individual record delete from list or edit form
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
   * Return LazyDataModel of VendorBankContact.
   *
   * @return
   */
  public LazyDataModel<VendorBankContact> getVendorBankContactLazyModel() {
    return vendorBankContactLazyModel;
  }

  /**
   * Return VendorBankContact[].
   *
   * @return
   */
  public VendorBankContact[] getVendorBankContactSelected() {
    return vendorBankContactSelected;
  }

  /**
   * Set VendorBankContact[].
   *
   * @param vendorBankContactSelected
   */
  public void setVendorBankContactSelected(VendorBankContact[] vendorBankContactSelected) {
    this.vendorBankContactSelected = vendorBankContactSelected;
  }

  /**
   * VendorBank autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.vendorBankAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.vendorBankAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<VendorBank> vendorBankAuto(String filter) {
    return ScmLookupView.vendorBankAuto(filter);
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
   * Designation autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.designationAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.designationAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Designation> designationAuto(String filter) {
    return ScmLookupView.designationAuto(filter);
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void vendorBankContactDialogClose() {
    Jsf.popupClose(parent);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }

  public List<Designation> selectDesignationByVendorContext(MainView main) {
    try {
      return DesignationService.selectDesignationByVendorContext(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }
}
