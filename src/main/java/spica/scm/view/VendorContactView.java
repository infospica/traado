/*
 * @(#)VendorContactView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
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
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.VendorContact;
import spica.scm.service.VendorContactService;
import spica.scm.domain.Vendor;
import spica.scm.domain.Account;
import spica.scm.domain.Designation;
import spica.scm.service.DesignationService;
import wawo.app.faces.Jsf;

/**
 * VendorContactView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "vendorContactView")
@ViewScoped
public class VendorContactView implements Serializable {

  private transient VendorContact vendorContact;	//Domain object/selected Domain.
  private transient LazyDataModel<VendorContact> vendorContactLazyModel; 	//For lazy loading datatable.
  private transient VendorContact[] vendorContactSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public VendorContactView() {
    super();
  }

  /**
   * Return VendorContact.
   *
   * @return VendorContact.
   */
  public VendorContact getVendorContact() {
    if (vendorContact == null) {
      vendorContact = new VendorContact();
    }
    return vendorContact;
  }

  /**
   * Set VendorContact.
   *
   * @param vendorContact.
   */
  public void setVendorContact(VendorContact vendorContact) {
    this.vendorContact = vendorContact;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchVendorContact(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getVendorContact().reset();
          getVendorContact().setVendorId(parent);
        } else if (ViewType.editform.toString().equals(viewType)) {
          setVendorContact((VendorContact) VendorContactService.selectByPk(main, getVendorContact()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadVendorContactList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }
  private Vendor parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Vendor) Jsf.dialogParent(Vendor.class);
    getVendorContact().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Create vendorContactLazyModel.
   *
   * @param main
   */
  private void loadVendorContactList(final MainView main) {
    if (vendorContactLazyModel == null) {
      vendorContactLazyModel = new LazyDataModel<VendorContact>() {
        private List<VendorContact> list;

        @Override
        public List<VendorContact> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = VendorContactService.listPaged(main);
            main.commit(vendorContactLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(VendorContact vendorContact) {
          return vendorContact.getId();
        }

        @Override
        public VendorContact getRowData(String rowKey) {
          if (list != null) {
            for (VendorContact obj : list) {
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
    String SUB_FOLDER = "scm_vendor_contact/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveVendorContact(MainView main) {
    return saveOrCloneVendorContact(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneVendorContact(MainView main) {
    main.setViewType("newform");
    return saveOrCloneVendorContact(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneVendorContact(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            VendorContactService.insertOrUpdate(main, getVendorContact());
//            VendorContactService.makeDefault(main, getVendorContact());
            break;
          case "clone":
            VendorContactService.clone(main, getVendorContact());
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
   * Delete one or many VendorContact.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteVendorContact(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(vendorContactSelected)) {
        VendorContactService.deleteByPkArray(main, getVendorContactSelected()); //many record delete from list
        main.commit("success.delete");
        vendorContactSelected = null;
      } else {
        VendorContactService.deleteByPk(main, getVendorContact());  //individual record delete from list or edit form
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
   * Return LazyDataModel of VendorContact.
   *
   * @return
   */
  public LazyDataModel<VendorContact> getVendorContactLazyModel() {
    return vendorContactLazyModel;
  }

  /**
   * Return VendorContact[].
   *
   * @return
   */
  public VendorContact[] getVendorContactSelected() {
    return vendorContactSelected;
  }

  /**
   * Set VendorContact[].
   *
   * @param vendorContactSelected
   */
  public void setVendorContactSelected(VendorContact[] vendorContactSelected) {
    this.vendorContactSelected = vendorContactSelected;
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
  public void vendorContactDialogClose() {
    Jsf.returnDialog(null);
  }

  public void onRowEdit(RowEditEvent event) {
    MainView main = Jsf.getMain();
    try {
      VendorContact contact = (VendorContact) event.getObject();
      if (contact.getDesignationId() != null && !contact.getDesignationId().getId().equals(DesignationService.VENDOR_SALES_AGENT)) {
        contact.setPanNo(null);
      }
      VendorContactService.insertOrUpdate(main, contact);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
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

  public void ajaxEventHandler(SelectEvent event) {
  }

}
