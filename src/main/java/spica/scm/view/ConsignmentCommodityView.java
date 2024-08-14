/*
 * @(#)ConsignmentCommodityView.java	1.0 Fri Jul 22 10:57:43 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.SelectItem;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.app.faces.JsfIo;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.ConsignmentCommodity;
import spica.scm.service.ConsignmentCommodityService;
import spica.scm.domain.Consignment;
import spica.scm.domain.ServiceCommodity;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.app.faces.Jsf;

/**
 * ConsignmentCommodityView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:43 IST 2016
 */
@Named(value = "consignmentCommodityView")
@ViewScoped
public class ConsignmentCommodityView implements Serializable {

  private transient ConsignmentCommodity consignmentCommodity;	//Domain object/selected Domain.
  private transient LazyDataModel<ConsignmentCommodity> consignmentCommodityLazyModel; 	//For lazy loading datatable.
  private transient ConsignmentCommodity[] consignmentCommoditySelected;	 //Selected Domain Array
  private transient Part invoiceFilePathPart;
  private transient Part debitNoteFilePathPart;

  /**
   * Default Constructor.
   */
  public ConsignmentCommodityView() {
    super();
  }

  /**
   * Return ConsignmentCommodity.
   *
   * @return ConsignmentCommodity.
   */
  public ConsignmentCommodity getConsignmentCommodity() {
    if (consignmentCommodity == null) {
      consignmentCommodity = new ConsignmentCommodity();
    }
    return consignmentCommodity;
  }

  /**
   * Set ConsignmentCommodity.
   *
   * @param consignmentCommodity.
   */
  public void setConsignmentCommodity(ConsignmentCommodity consignmentCommodity) {
    this.consignmentCommodity = consignmentCommodity;
  }

  private Consignment parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Consignment) Jsf.popupParentValue(Consignment.class);
    getConsignmentCommodity().setId(Jsf.getParameterInt("id"));
  }

  public boolean isCommoditySelected() {
    return getConsignmentCommodity().getCommodityId() != null && getConsignmentCommodity().getCommodityId().getId() != null;
  }

  public boolean isPurchaseConsignment() {
    return parent.getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_PURCHASE);
  }

  public boolean isPurchaseReturnConsignment() {
    return parent.getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_PURCHASE_RETURN);
  }

  public boolean isSalesConsignment() {
    return parent.getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_SALES);
  }

  public boolean isSalesReturnConsignment() {
    return parent.getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_SALES_RETURN);
  }

  public boolean isFromVendor() {
    return (parent.getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_PURCHASE));
  }

  public boolean isFromCompany() {
    return (parent.getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_SALES) || parent.getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_PURCHASE_RETURN));
  }

  public boolean isFromCustomer() {
    return (parent.getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_SALES_RETURN));
  }

  public boolean isToVendor() {
    return (parent.getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_PURCHASE_RETURN));
  }

  public boolean isToCompany() {
    return (parent.getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_SALES_RETURN) || parent.getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_PURCHASE));
  }

  public boolean isToCustomer() {
    return (parent.getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_SALES));
  }

  public List<ServiceCommodity> getCommodity() {
    List<ServiceCommodity> list;
    if (isFromVendor()) {
      list = ScmLookupExtView.vendorCommodityByVendorId(parent.getVendorId().getId());
    } else {
      list = ScmLookupExtView.commodityAuto();
    }
    return list;
  }

  public List<SelectItem> getConsignmentPackingUnit() {
    List<SelectItem> list = new ArrayList<>();
    for (String unit : SystemRuntimeConfig.CONSIGNMENT_PACKING_UNIT) {
      list.add(new SelectItem(unit, list.size() + 1));
    }
    return list;
  }

  public List<SelectItem> getConsignmentWeightUnit() {
    List<SelectItem> list = new ArrayList<>();
    for (String unit : SystemRuntimeConfig.CONSIGNMENT_WEIGHT_UNIT) {
      list.add(new SelectItem(unit, list.size() + 1));
    }
    return list;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchConsignmentCommodity(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getConsignmentCommodity().reset();
          getConsignmentCommodity().setConsignmentId(parent);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setConsignmentCommodity((ConsignmentCommodity) ConsignmentCommodityService.selectByPk(main, getConsignmentCommodity()));

        } else if (ViewType.list.toString().equals(viewType)) {
          loadConsignmentCommodityList(main);
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
   * Create consignmentCommodityLazyModel.
   *
   * @param main
   */
  private void loadConsignmentCommodityList(final MainView main) {
    if (consignmentCommodityLazyModel == null) {
      consignmentCommodityLazyModel = new LazyDataModel<ConsignmentCommodity>() {
        private List<ConsignmentCommodity> list;

        @Override
        public List<ConsignmentCommodity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ConsignmentCommodityService.listPaged(main);
            main.commit(consignmentCommodityLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ConsignmentCommodity consignmentCommodity) {
          return consignmentCommodity.getId();
        }

        @Override
        public ConsignmentCommodity getRowData(String rowKey) {
          if (list != null) {
            for (ConsignmentCommodity obj : list) {
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

  private void uploadFiles() throws IOException {
    String SUB_FOLDER = "scm_consignment_commodity/";
    if (invoiceFilePathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(invoiceFilePathPart, getConsignmentCommodity().getInvoiceFilePath(), SUB_FOLDER);
      getConsignmentCommodity().setInvoiceFilePath(JsfIo.getDbPath(invoiceFilePathPart, SUB_FOLDER));
      invoiceFilePathPart = null;	//import to set as null.
    }
    if (debitNoteFilePathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(debitNoteFilePathPart, getConsignmentCommodity().getDebitNoteFilePath(), SUB_FOLDER);
      getConsignmentCommodity().setDebitNoteFilePath(JsfIo.getDbPath(debitNoteFilePathPart, SUB_FOLDER));
      debitNoteFilePathPart = null;	//import to set as null.
    }
  }

  /**
   * Insert or update.
   *
   * @param main
   * @param isClose
   * @return the page to display.
   */
  public String saveConsignmentCommodity(MainView main, boolean isClose) {
    if (isCommoditySelected()) {
      getConsignmentCommodity().setCommodityName(null);
    }
    if (isClose) {
      consignmentCommodityPopupClose();
      Jsf.execute("parent.consignmentCommodityPopupReturned(); closePopup()");
    }
    return saveOrCloneConsignmentCommodity(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneConsignmentCommodity(MainView main) {
    main.setViewType("newform");
    return saveOrCloneConsignmentCommodity(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneConsignmentCommodity(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ConsignmentCommodityService.insertOrUpdate(main, getConsignmentCommodity());
            break;
          case "clone":
            ConsignmentCommodityService.clone(main, getConsignmentCommodity());
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

  public void validateCommodity(FacesContext context, UIComponent comp, Object value) {
    ServiceCommodity comodity = (ServiceCommodity) value;
    if (comodity == null) {
      if (Jsf.hasParameter("commodityName")) {
        if (Jsf.isEmptyParameter("commodityName")) {
          Jsf.error(comp, "select.comodity.or.name");
        }
      }
    }
  }

  /**
   * Delete one or many ConsignmentCommodity.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteConsignmentCommodity(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(consignmentCommoditySelected)) {
        ConsignmentCommodityService.deleteByPkArray(main, getConsignmentCommoditySelected()); //many record delete from list
        main.commit("success.delete");
        consignmentCommoditySelected = null;
      } else {
        ConsignmentCommodityService.deleteByPk(main, getConsignmentCommodity());  //individual record delete from list or edit form
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

  public String deleteSalesConsignmentCommodity(MainView main, ConsignmentCommodity consignmentCommodity) {
    try {

      ConsignmentCommodityService.deleteByPk(main, consignmentCommodity);  //individual record delete from list or edit form
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Return LazyDataModel of ConsignmentCommodity.
   *
   * @return
   */
  public LazyDataModel<ConsignmentCommodity> getConsignmentCommodityLazyModel() {
    return consignmentCommodityLazyModel;
  }

  /**
   * Return ConsignmentCommodity[].
   *
   * @return
   */
  public ConsignmentCommodity[] getConsignmentCommoditySelected() {
    return consignmentCommoditySelected;
  }

  /**
   * Set ConsignmentCommodity[].
   *
   * @param consignmentCommoditySelected
   */
  public void setConsignmentCommoditySelected(ConsignmentCommodity[] consignmentCommoditySelected) {
    this.consignmentCommoditySelected = consignmentCommoditySelected;
  }

  /**
   * Return Part.
   *
   * @return Part.
   */
  public Part getInvoiceFilePathPart() {
    return invoiceFilePathPart;
  }

  /**
   * Set Part invoiceFilePathPart.
   *
   * @param invoiceFilePathPart.
   */
  public void setInvoiceFilePathPart(Part invoiceFilePathPart) {
    if (this.invoiceFilePathPart == null || invoiceFilePathPart != null) {
      this.invoiceFilePathPart = invoiceFilePathPart;
    }
  }

  /**
   * Return Part.
   *
   * @return Part.
   */
  public Part getDebitNoteFilePathPart() {
    return debitNoteFilePathPart;
  }

  /**
   * Set Part debitNoteFilePathPart.
   *
   * @param debitNoteFilePathPart.
   */
  public void setDebitNoteFilePathPart(Part debitNoteFilePathPart) {
    if (this.debitNoteFilePathPart == null || debitNoteFilePathPart != null) {
      this.debitNoteFilePathPart = debitNoteFilePathPart;
    }
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
   * Commodity autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.commodityAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.commodityAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ServiceCommodity> commodityAuto(String filter) {
    return ScmLookupView.commodityAuto(filter);
  }

  public void consignmentCommodityPopupClose() {
    Jsf.popupReturn(parent, null);
  }
//  public boolean renderButton() {
//    return (getConsignmentCommodity().getConsignmentId()==null || getConsignmentCommodity().getConsignmentId().getConsignmentStatusId().getId()==ConsignmentService.draft);
//  }

  public void consignmentCommodityReceiptPopup(ConsignmentCommodity consignmentCommodity) {
    Jsf.popupForm(FileConstant.CONSIGNMENT_COMMODITY_RECEIPT_DETAIL, consignmentCommodity);
  }

  public void consignmentCommodityReceiptDialogReturn(SelectEvent event) {
    Jsf.closeDialog(getConsignmentCommodity());
  }

  public boolean isConsignmentCancelled() {
    return getConsignmentCommodity().getConsignmentId().getConsignmentStatusId() != null && getConsignmentCommodity().getConsignmentId().getConsignmentStatusId().getId() == SystemConstants.CONSIGNMENT_STATUS_CENCELLED;
  }

  public boolean isConfirmed() {
    return getConsignmentCommodity().getConsignmentId().getConsignmentStatusId() != null && getConsignmentCommodity().getConsignmentId().getConsignmentStatusId().getId() >= SystemConstants.CONSIGNMENT_STATUS_CONFIRMED;
  }
}
