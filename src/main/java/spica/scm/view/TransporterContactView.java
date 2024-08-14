/*
 * @(#)TransporterContactView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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

import spica.scm.domain.TransporterContact;
import spica.scm.service.TransporterContactService;
import spica.scm.domain.Transporter;
import spica.scm.domain.Designation;
import spica.scm.domain.Status;
import spica.scm.service.DesignationService;
import wawo.app.faces.Jsf;

/**
 * TransporterContactView
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016 
 */

@Named(value="transporterContactView")
@ViewScoped
public class TransporterContactView implements Serializable{

  private transient TransporterContact transporterContact;	//Domain object/selected Domain.
  private transient LazyDataModel<TransporterContact> transporterContactLazyModel; 	//For lazy loading datatable.
  private transient TransporterContact[] transporterContactSelected;	 //Selected Domain Array
  
  private Transporter parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Transporter) Jsf.dialogParent(Transporter.class);
    getTransporterContact().setId(Jsf.getParameterInt("id"));
  }
  
  /**
   * Default Constructor.
   */   
  public TransporterContactView() {
    super();
  }

  public Transporter getParent() {
    return parent;
  }

  public void setParent(Transporter parent) {
    this.parent = parent;
  } 
  
  /**
   * Return TransporterContact.
   * @return TransporterContact.
   */  
  public TransporterContact getTransporterContact() {
    if(transporterContact == null) {
      transporterContact = new TransporterContact();
    }
    return transporterContact;
  }   
  
  /**
   * Set TransporterContact.
   * @param transporterContact.
   */   
  public void setTransporterContact(TransporterContact transporterContact) {
    this.transporterContact = transporterContact;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchTransporterContact(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getTransporterContact().reset();
          getTransporterContact().setTransporterId(parent);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setTransporterContact((TransporterContact) TransporterContactService.selectByPk(main, getTransporterContact()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadTransporterContactList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally{
        main.close();
      }
    }
    return null;
  } 
 
  /**
   * Create transporterContactLazyModel.
   * @param main
   */
  private void loadTransporterContactList(final MainView main) {
    if (transporterContactLazyModel == null) {
      transporterContactLazyModel = new LazyDataModel<TransporterContact>() {
      private List<TransporterContact> list;      
      @Override
      public List<TransporterContact> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = TransporterContactService.listPaged(main);
          main.commit(transporterContactLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(TransporterContact transporterContact) {
        return transporterContact.getId();
      }
      @Override
        public TransporterContact getRowData(String rowKey) {
          if (list != null) {
            for (TransporterContact obj : list) {
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
    String SUB_FOLDER = "scm_transporter_contact/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveTransporterContact(MainView main) {
    return saveOrCloneTransporterContact(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTransporterContact(MainView main) {
    main.setViewType("newform");
    return saveOrCloneTransporterContact(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTransporterContact(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            TransporterContactService.insertOrUpdate(main, getTransporterContact());
//            TransporterContactService.makeDefault(main, getTransporterContact());
            break;
          case "clone":
            TransporterContactService.clone(main, getTransporterContact());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error"+ key);
    } finally {
      main.close();
    }
    return null;
  }

  
  /**
   * Delete one or many TransporterContact.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTransporterContact(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(transporterContactSelected)) {
        TransporterContactService.deleteByPkArray(main, getTransporterContactSelected()); //many record delete from list
        main.commit("success.delete");
        transporterContactSelected = null;
      } else {
        TransporterContactService.deleteByPk(main, getTransporterContact());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())){
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
   * Return LazyDataModel of TransporterContact.
   * @return
   */
  public LazyDataModel<TransporterContact> getTransporterContactLazyModel() {
    return transporterContactLazyModel;
  }

 /**
  * Return TransporterContact[].
  * @return 
  */
  public TransporterContact[] getTransporterContactSelected() {
    return transporterContactSelected;
  }
  
  /**
   * Set TransporterContact[].
   * @param transporterContactSelected 
   */
  public void setTransporterContactSelected(TransporterContact[] transporterContactSelected) {
    this.transporterContactSelected = transporterContactSelected;
  }
 


 /**
  * Transporter autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.transporterAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.transporterAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<Transporter> transporterAuto(String filter) {
    return ScmLookupView.transporterAuto(filter);
  }
 /**
  * Designation autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.designationAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.designationAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<Designation> designationAuto(String filter) {
    return ScmLookupView.designationAuto(filter);
  }
  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void transporterContactDialogClose() {
    Jsf.returnDialog(null);
  }
  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }
  
  public void onRowEdit(TransporterContact transporterContact) {
    MainView main = Jsf.getMain();
    try {
      TransporterContactService.insertOrUpdate(main, transporterContact);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }
  
  public List<Designation> selectDesignationByTransporterContext(MainView main) {
    try {
      return DesignationService.selectDesignationByTransporterContext(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }
}
