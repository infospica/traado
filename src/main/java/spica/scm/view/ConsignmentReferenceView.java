/*
 * @(#)ConsignmentReferenceView.java	1.0 Fri Jul 22 10:57:43 IST 2016 
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
import javax.servlet.http.Part;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypeAction;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.app.faces.JsfIo;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.ConsignmentReference;
import spica.scm.service.ConsignmentReferenceService;
import spica.scm.domain.Consignment;
import spica.scm.domain.PurchaseOrder;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.SalesOrder;
import spica.scm.domain.SalesReturn;
import spica.scm.service.PurchaseOrderService;
import spica.scm.service.PurchaseReturnService;
import wawo.app.faces.Jsf;

/**
 * ConsignmentReferenceView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:43 IST 2016
 */
@Named(value = "consignmentReferenceView")
@ViewScoped
public class ConsignmentReferenceView implements Serializable {

  private transient ConsignmentReference consignmentReference;	//Domain object/selected Domain.
  private transient LazyDataModel<ConsignmentReference> consignmentReferenceLazyModel; 	//For lazy loading datatable.
  private transient ConsignmentReference[] consignmentReferenceSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public ConsignmentReferenceView() {
    super();
  }

  /**
   * Return ConsignmentReference.
   *
   * @return ConsignmentReference.
   */
  public ConsignmentReference getConsignmentReference() {
    if (consignmentReference == null) {
      consignmentReference = new ConsignmentReference();
    }
    return consignmentReference;
  }

  /**
   * Set ConsignmentReference.
   *
   * @param consignmentReference.
   */
  public void setConsignmentReference(ConsignmentReference consignmentReference) {
    this.consignmentReference = consignmentReference;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  private Consignment parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Consignment) Jsf.popupParentValue(Consignment.class);
    getConsignmentReference().setId(Jsf.getParameterInt("id"));
    getConsignmentReference().setConsignmentId(parent);
  }

  public String switchConsignmentReference(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          
          getConsignmentReference().reset();
//          getConsignmentReference().setConsignmentId(parent);
          POList=null;
          PRList=null;
        } else if (ViewType.editform.toString().equals(viewType)) {
          setConsignmentReference((ConsignmentReference) ConsignmentReferenceService.selectByPk(main, getConsignmentReference()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadConsignmentReferenceList(main);
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
   * Create consignmentReferenceLazyModel.
   *
   * @param main
   */
  private void loadConsignmentReferenceList(final MainView main) {
    if (consignmentReferenceLazyModel == null) {
      consignmentReferenceLazyModel = new LazyDataModel<ConsignmentReference>() {
        private List<ConsignmentReference> list;

        @Override
        public List<ConsignmentReference> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ConsignmentReferenceService.listPaged(main);
            main.commit(consignmentReferenceLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ConsignmentReference consignmentReference) {
          return consignmentReference.getId();
        }

        @Override
        public ConsignmentReference getRowData(String rowKey) {
          if (list != null) {
            for (ConsignmentReference obj : list) {
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
    String SUB_FOLDER = "scm_consignment_reference/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveConsignmentReference(MainView main) {
    return saveOrCloneConsignmentReference(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneConsignmentReference(MainView main) {
    main.setViewType("newform");
    return saveOrCloneConsignmentReference(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneConsignmentReference(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ConsignmentReferenceService.insertOrUpdate(main, getConsignmentReference());
            break;
          case "clone":
            ConsignmentReferenceService.clone(main, getConsignmentReference());
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
   * Delete one or many ConsignmentReference.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteConsignmentReference(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(consignmentReferenceSelected)) {
        ConsignmentReferenceService.deleteByPkArray(main, getConsignmentReferenceSelected()); //many record delete from list
        main.commit("success.delete");
        consignmentReferenceSelected = null;
      } else {
        ConsignmentReferenceService.deleteByPk(main, getConsignmentReference());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ConsignmentReference.
   *
   * @return
   */
  public LazyDataModel<ConsignmentReference> getConsignmentReferenceLazyModel() {
    return consignmentReferenceLazyModel;
  }

  /**
   * Return ConsignmentReference[].
   *
   * @return
   */
  public ConsignmentReference[] getConsignmentReferenceSelected() {
    return consignmentReferenceSelected;
  }

  /**
   * Set ConsignmentReference[].
   *
   * @param consignmentReferenceSelected
   */
  public void setConsignmentReferenceSelected(ConsignmentReference[] consignmentReferenceSelected) {
    this.consignmentReferenceSelected = consignmentReferenceSelected;
  }

  /**
   * Consignment autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.consignmentAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.consignmentAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Consignment> consignmentAuto(String filter) {
    return ScmLookupView.consignmentAuto(filter);
  }

  /**
   * PurchaseOrder autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.purchaseOrderAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.purchaseOrderAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<PurchaseOrder> purchaseOrderAuto(String filter) {
    return ScmLookupView.purchaseOrderAuto(filter);
  }

  /**
   * PurchaseReturn autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.purchaseReturnAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.purchaseReturnAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<PurchaseReturn> purchaseReturnAuto(String filter) {
    return ScmLookupView.purchaseReturnAuto(filter);
  }

  /**
   * SalesOrder autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.salesOrderAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.salesOrderAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<SalesOrder> salesOrderAuto(String filter) {
    return ScmLookupView.salesOrderAuto(filter);
  }

  /**
   * SalesReturn autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.salesReturnAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.salesReturnAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<SalesReturn> salesReturnAuto(String filter) {
    return ScmLookupView.salesReturnAuto(filter);
  }

  public List<PurchaseOrder> getPO(MainView main) {
    try {
      POList = PurchaseOrderService.selectPO(main, getConsignmentReference().getConsignmentId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return POList;
  }
  private List<PurchaseOrder> POList;
  private transient PurchaseOrder[] purchaseOrderSelected;	 //Selected Domain Array

  public PurchaseOrder[] getPurchaseOrderSelected() {
    return purchaseOrderSelected;
  }

  public void setPurchaseOrderSelected(PurchaseOrder[] purchaseOrderSelected) {
    this.purchaseOrderSelected = purchaseOrderSelected;
  }

  public String insertConsignmentPO(MainView main) {
    try {
      ConsignmentReferenceService.insertArray(main, getPurchaseOrderSelected(), getConsignmentReference().getConsignmentId());
      main.commit("success.select");
    } catch (Throwable t) {
      main.rollback(t, "error.list");
    } finally {
      main.close();
    }
    return null;
  }
  

  public void consignmentPODialogClose() {
    Jsf.popupReturn(parent, null);
  }

  public List<PurchaseReturn> getPR(MainView main) {
    try {
      PRList = PurchaseReturnService.selectPR(main, getConsignmentReference().getConsignmentId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return PRList;
  }
  private List<PurchaseReturn> PRList;
  private transient PurchaseReturn[] purchaseReturnSelected;	 //Selected Domain Array

  public PurchaseReturn[] getPurchaseReturnSelected() {
    return purchaseReturnSelected;
  }

  public void setPurchaseReturnSelected(PurchaseReturn[] purchaseReturnSelected) {
    this.purchaseReturnSelected = purchaseReturnSelected;
  }
  
  
  
  

  public String insertConsignmentPR(MainView main) {
    try {
      ConsignmentReferenceService.insertArray1(main, getPurchaseReturnSelected(), getConsignmentReference().getConsignmentId());
      main.commit("success.select");
    } catch (Throwable t) {
      main.rollback(t, "error.list");
    } finally {
      main.close();
    }
    return null;
  }
  public void consignmentReferencePOPopupReturned() {
    POList = null; // Reset to null to fetch updated list
  }
 public void consignmentReferencePRDialogReturn(SelectEvent event) {
    Jsf.closeDialog(getConsignmentReference());
    PRList = null; // Reset to null to fetch updated list
  }

}
