/*
 * @(#)ConsignmentReceiptView.java	1.0 Fri Jul 22 10:57:43 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.IOException;
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
import spica.scm.domain.Consignment;
import spica.scm.domain.ConsignmentDetail;
import spica.scm.domain.ConsignmentStatus;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.Vendor;
import spica.scm.service.ConsignmentDetailService;
import spica.scm.service.ConsignmentService;
import spica.sys.FileConstant;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.JsfIo;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * ConsignmentReceiptView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:43 IST 2016
 */
@Named(value = "consignmentReceiptView")
@ViewScoped
public class ConsignmentReceiptView implements Serializable {

  //private transient ConsignmentReceipt consignmentReceipt;	//Domain object/selected Domain.
  private transient LazyDataModel<ConsignmentDetail> consignmentReceiptLazyModel; 	//For lazy loading datatable.
  private transient ConsignmentDetail[] consignmentReceiptSelected;	 //Selected Domain Array
  private transient Consignment consignment;
  private transient ProductEntry productEntry;
  private transient Consignment selectedConsignment;
  private transient ConsignmentDetail consignmentDetail;
  private transient Part documentPathPart;
  //private List<ConsignmentDetail> consignmentReceiptList;
  private Consignment parent;

  @PostConstruct
  public void init() {
    MainView main = Jsf.getMain();
    try {
      parent = (Consignment) Jsf.popupParentValue(Consignment.class);
      productEntry = (ProductEntry) Jsf.popupParentValue(ProductEntry.class);
      if (parent != null) {
        setConsignmentDetail((ConsignmentDetail) ConsignmentDetailService.selectConsignmentDetailByConsignment(main, parent.getId()));
        if (consignmentDetail == null) {
          getConsignmentDetail().setConsignmentId(parent);
          main.setViewType(ViewTypes.newform);
        } else {
          main.setViewType(ViewTypes.editform);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  /**
   * Default Constructor.
   */
  public ConsignmentReceiptView() {
    super();
  }

  /**
   * Return ConsignmentReceipt.
   *
   * @return ConsignmentReceipt.
   */
//  public ConsignmentReceipt getConsignmentReceipt() {
//    if (consignmentReceipt == null) {
//      consignmentReceipt = new ConsignmentReceipt();
//    }
//    return consignmentReceipt;
//  }
  /**
   * Set ConsignmentReceipt.
   *
   * @param consignmentReceipt.
   */
//  public void setConsignmentReceipt(ConsignmentReceipt consignmentReceipt) {
//    this.consignmentReceipt = consignmentReceipt;
//  }
  public Consignment getConsignment() {
    if (consignment == null) {
      consignment = new Consignment();
      consignment.setCompanyId(UserRuntimeView.instance().getCompany());
      consignment.setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
    }
    return consignment;
  }

  public void setConsignment(Consignment consignment) {
    this.consignment = consignment;
  }

  public Consignment getSelectedConsignment() {
    return selectedConsignment;
  }

  public void setSelectedConsignment(Consignment selectedConsignment) {
    this.selectedConsignment = selectedConsignment;
  }

  public ConsignmentDetail getConsignmentDetail() {
    if (consignmentDetail == null) {
      consignmentDetail = new ConsignmentDetail();
    }
    return consignmentDetail;
  }

  public void setConsignmentDetail(ConsignmentDetail consignmentDetail) {
    this.consignmentDetail = consignmentDetail;
  }

  /**
   * Return LazyDataModel of ConsignmentReceipt.
   *
   * @return
   */
  public LazyDataModel<ConsignmentDetail> getConsignmentReceiptLazyModel() {
    return consignmentReceiptLazyModel;
  }

  /**
   * Return ConsignmentReceipt[].
   *
   * @return
   */
  public ConsignmentDetail[] getConsignmentReceiptSelected() {
    return consignmentReceiptSelected;
  }

  /**
   * Set ConsignmentReceipt[].
   *
   * @param consignmentReceiptSelected
   */
  public void setConsignmentReceiptSelected(ConsignmentDetail[] consignmentReceiptSelected) {
    this.consignmentReceiptSelected = consignmentReceiptSelected;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchConsignmentReceipt(MainView main, String viewType) {
    //consignmentReceiptList = null;
    if (!StringUtil.isEmpty(viewType) && !main.hasError()) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          if (UserRuntimeView.instance().getCompany() == null) {
            main.error("company.required");
            main.setViewType(ViewTypes.list);
            return null;
          } else if (UserRuntimeView.instance().getAccount() == null) {
            main.error("account.required");
            if (ViewType.newform.toString().equals(viewType)) {
              main.setViewType(ViewTypes.list);
            }
            return null;
          } else if (UserRuntimeView.instance().getContract() == null) {
            main.error("contract.required");
            if (ViewType.newform.toString().equals(viewType)) {
              main.setViewType(ViewTypes.list);
            }
            return null;
          }
          getConsignment().reset();
          getConsignmentDetail().reset();
          setConsignorDetails(main);
        } else if (ViewType.editform.toString().equals(viewType)) {
          setConsignmentDetail((ConsignmentDetail) ConsignmentDetailService.selectByPk(main, getConsignmentDetail()));
          setConsignment(getConsignmentDetail().getConsignmentId());
        } else if (ViewType.list.toString().equals(viewType)) {
          main.getPageData().setSearchKeyWord(null);
          loadConsignmentReceiptList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  private void setConsignorDetails(MainView main) {
    try {
      if (productEntry == null) {
        getConsignment().setCompanyId(UserRuntimeView.instance().getCompany());
        getConsignment().setVendorId(UserRuntimeView.instance().getAccount().getVendorId());
        getConsignment().setAccountId(UserRuntimeView.instance().getAccount());
        getConsignment().setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
      } else {
        getConsignment().setCompanyId(productEntry.getCompanyId());
        getConsignment().setVendorId(productEntry.getAccountId().getVendorId());
        getConsignment().setAccountId(productEntry.getAccountId());
        getConsignment().setFinancialYearId(productEntry.getFinancialYearId());
      }
      ConsignmentService.setFromVendorAddress(main, getConsignment());
      ConsignmentService.setToCompanyAddress(main, getConsignment());
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }

  /**
   * Create consignmentReceiptLazyModel.
   *
   * @param main
   */
  private void loadConsignmentReceiptList(final MainView main) {
    if (consignmentReceiptLazyModel == null) {
      consignmentReceiptLazyModel = new LazyDataModel<ConsignmentDetail>() {
        private List<ConsignmentDetail> list;

        @Override
        public List<ConsignmentDetail> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (UserRuntimeView.instance().getAccount() != null) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = ConsignmentDetailService.listPagedPurchaseConsignmentDetailByAccount(main, UserRuntimeView.instance().getAccountCurrent(), UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
              //list = ConsignmentDetailService.listPagedProductEntryByAccount(main, UserRuntimeView.instance().getAccount());
              main.commit(consignmentReceiptLazyModel, first, pageSize);
            }
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ConsignmentDetail consignmentReceipt) {
          return consignmentReceipt.getId();
        }

        @Override
        public ConsignmentDetail getRowData(String rowKey) {
          if (list != null) {
            for (ConsignmentDetail obj : list) {
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
    String SUB_FOLDER = "scm_consignment_receipt/";
    if (documentPathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(documentPathPart, getConsignmentDetail().getReceiptReturnFilePath(), SUB_FOLDER);
      getConsignmentDetail().setReceiptReturnFilePath(JsfIo.getDbPath(documentPathPart, SUB_FOLDER));
      documentPathPart = null;	//import to set as null.
    }
  }

  public void saveConsignmentReceiptClose(MainView main) {
    saveConsignmentReceipt(main);
    consignmentReceiptPopupClose();
    Jsf.execute("parent.receiptPopupReturned()");
    Jsf.execute("closePopup()");
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveConsignmentReceipt(MainView main) {
    return saveOrCloneConsignmentReceipt(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneConsignmentReceipt(MainView main) {
    main.setViewType("newform");
    return saveOrCloneConsignmentReceipt(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneConsignmentReceipt(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ConsignmentDetailService.insertOrUpdate(main, getConsignmentDetail());
            break;
          case "clone":
            ConsignmentDetailService.clone(main, getConsignmentDetail());
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

  public void saveConsignmentReceiptInfoClose(MainView main) {
    saveConsignmentReceiptInfo(main);
    consignmentPopupClose();
    Jsf.execute("parent.consignmentReturned()");
    Jsf.execute("closePopup()");
  }

  public String saveConsignmentReceiptInfo(MainView main) {
    try {
      uploadFiles(); //File upload
      getConsignment().setConsignmentDetail(getConsignmentDetail());
      ConsignmentService.insertOrUpdateInvoiceEntryConsignmentDetail(main, getConsignment());
      main.commit("success.save");
      main.setViewType(ViewTypes.editform);
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
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
   *
   *
   * @return
   */
  public List<ConsignmentStatus> consignmentStatus() {
    return ScmLookupExtView.lookupConsignmentReceiptStatus();
  }

  public void consignmentReceiptPopupClose() {
    Jsf.popupReturn(parent, null);
  }

  public void fromVendorDetails(SelectEvent event) {
    MainView main = Jsf.getMain();
    //TODO adress selection must be based on address type consignment

    try {
      getConsignment().setVendorId((Vendor) event.getObject());
      getConsignment().setAccountId(UserRuntimeView.instance().getAccount());
      if (getConsignment().getVendorId() != null) {
        ConsignmentService.setFromVendorAddresss(main, getConsignment());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void details(SelectEvent event) {
    MainView main = Jsf.getMain();
    try {
      if (event.getObject() != null) {
        setConsignment((Consignment) event.getObject());
        if (getConsignment().getId() != null) {
          setConsignmentDetail((ConsignmentDetail) ConsignmentDetailService.selectConsignmentDetailByConsignment(main, getConsignment().getId()));
        }
      } else {
        setConsignorDetails(main);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public List<Consignment> consignmentInfo(MainView main) {
    try {
      if (UserRuntimeView.instance().getCompany() != null && UserRuntimeView.instance().getAccount() != null) {
        return ConsignmentService.consignmentInfo(main, UserRuntimeView.instance().getCompany(), UserRuntimeView.instance().getAccount());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public List<Vendor> vendorAuto(String filter) {
    if (UserRuntimeView.instance().getCompany() != null) {
      return ScmLookupExtView.consignmentVendorByCompanyAccountAuto(UserRuntimeView.instance().getCompany().getId(), filter);
    } else {
      return ScmLookupView.vendorAuto(filter);
    }
  }

  public boolean consignmentIdExist() {
    if (getConsignment() != null && getConsignment().getId() != null) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Return Part.
   *
   * @return Part.
   */
  public Part getDocumentPathPart() {
    return documentPathPart;
  }

  /**
   * Set Part documentPathPart.
   *
   * @param documentPathPart.
   */
  public void setDocumentPathPart(Part documentPathPart) {
    if (this.documentPathPart == null || documentPathPart != null) {
      this.documentPathPart = documentPathPart;
    }
  }

//  public List<ConsignmentDetail> getConsignmentReceiptList(MainView main) {
//    if (consignmentReceiptList == null) {
//      try {
//        consignmentReceiptList = ConsignmentDetailService.listPagedProductEntryByAccount(main, UserRuntimeView.instance().getAccountCurrent());
//      } catch (Throwable t) {
//        main.rollback(t, "error.select");
//      } finally {
//        main.close();
//      }
//    }
//    return consignmentReceiptList;
//  }
  public void consignmentPopupClose() {
    Jsf.popupReturn(getConsignment(), getConsignment());
  }
//FIXME move to popup view

  public void showExpense(MainView main) {
    Jsf.popupForm(FileConstant.EXPENSE, getConsignment(), getConsignment().getId());
  }

  public void showExpense(Consignment consignment) {
    Jsf.popupForm(FileConstant.EXPENSE, consignment, consignment.getId());
  }
}
