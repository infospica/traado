/*
 * @(#)ConsignmentView.java	1.0 Fri Jul 22 10:57:43 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.domain.Company;
import spica.scm.domain.Consignment;
import spica.scm.domain.ConsignmentCommodity;
import spica.scm.domain.ConsignmentDetail;
import spica.scm.domain.ConsignmentDocType;
import spica.scm.domain.ConsignmentReceiptType;
import spica.scm.domain.ConsignmentStatus;
import spica.scm.domain.ConsignmentType;
import spica.scm.domain.Customer;
import spica.scm.domain.PurchaseOrder;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesOrder;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.TransportMode;
import spica.scm.domain.Transporter;
import spica.scm.domain.Vendor;
import spica.scm.service.AccountGroupDocPrefixService;
import spica.scm.service.ConsignmentCommodityService;
import spica.scm.service.ConsignmentDetailService;
import spica.scm.service.ConsignmentDocTypeService;
import spica.scm.service.ConsignmentReferenceService;
import spica.scm.service.ConsignmentService;
import spica.scm.service.ConsignmentStatusService;
import spica.scm.service.PrefixTypeService;
import spica.scm.service.PurchaseOrderService;
import spica.scm.service.SalesOrderService;
import spica.sys.FileConstant;
import spica.sys.SecurityRuntimeView;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.config.AppConfig;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.JsfIo;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * ConsignmentView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:43 IST 2016
 */
@Named(value = "consignmentView")
@ViewScoped
public class ConsignmentView implements Serializable {

  private transient Consignment consignment;	//Domain object/selected Domain.
  private transient LazyDataModel<Consignment> consignmentLazyModel; 	//For lazy loading datatable.
  private transient Consignment[] consignmentSelected;	 //Selected Domain Array
  private int activeIndex = 0; //tab active index, reset to 0 when new records
  private List<ConsignmentCommodity> consignmentCommodityList;
  private ConsignmentDetail consignmentDetail = null;
  private transient Part exitDocumentFilePathPart;
  private transient Part exitDocument2FilePathPart;
  private transient Part entryDocumentFilePathPart;
  private transient Part receiptFilePathPart;
  private transient Part receiptReturnFilePathPart;
  private transient TransportMode oldTransportMode;
  private List<PurchaseOrder> POList;
  private List<PurchaseReturn> PRList;
  private List<SalesOrder> SOList;
  private List<SalesReturn> SRList;
  private Long receipt;
  private String rowStyleClass;
  private int flag = 0;
  private int rStatus = 1;
  private Integer deliveredStatus;
  private boolean hideList = true;
  //private boolean needReset = true;
  private transient PurchaseReturn purchaseReturn;
  private transient SalesInvoice salesInvoice;
  private transient boolean customerEditable;

  /**
   * Default Constructor.
   */
  public ConsignmentView() {
    super();
  }

  @PostConstruct
  public void init() {
    String id = Jsf.getParameter("id");
    if (id != null) {
      getConsignment().setId(Integer.valueOf(id));
    } else {
      purchaseReturn = (PurchaseReturn) Jsf.popupParentValue(PurchaseReturn.class);
      salesInvoice = (SalesInvoice) Jsf.popupParentValue(SalesInvoice.class);
    }

  }

  /**
   * Return Consignment.
   *
   * @return Consignment.
   */
  public Consignment getConsignment() {
    if (consignment == null) {
      consignment = new Consignment();
    }
    if (consignment.getCompanyId() == null) {
      consignment.setCompanyId(UserRuntimeView.instance().getCompany());
      consignment.setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
    }
    return consignment;
  }

  public Integer getDeliveredStatus() {
    return deliveredStatus;
  }

  public void setDeliveredStatus(Integer deliveredStatus) {
    this.deliveredStatus = deliveredStatus;
  }

  /**
   * Set Consignment.
   *
   * @param consignment.
   */
  public void setConsignment(Consignment consignment) {
    this.consignment = consignment;
  }

  public boolean isHideList() {
    return hideList;
  }

  public void setHideList(boolean hideList) {
    this.hideList = hideList;
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

  public Long getReceipt() {
    return receipt;
  }

  public void setReceipt(Long receipt) {
    this.receipt = receipt;
  }

  public TransportMode getOldTransportMode() {
    return oldTransportMode;
  }

  public void setOldTransportMode(TransportMode oldTransportMode) {
    this.oldTransportMode = oldTransportMode;
  }

  public String getRowStyleClass() {
    if (isPurchaseConsignment()) {
      rowStyleClass = "col-md-12";
    } else if (isPurchaseReturnConsignment()) {
      rowStyleClass = "col-md-6";
    }
    return rowStyleClass;
  }

  public void setRowStyleClass(String rowStyleClass) {
    this.rowStyleClass = rowStyleClass;
  }

  public int getFlag() {
    return flag;
  }

  public void setFlag(int flag) {
    this.flag = flag;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  private Integer getConsignmentTypeId(MainView main) {
    String consignmentTypePath = main.getViewPath();
    if (consignmentTypePath.equals(ConsignmentService.PURCHASE_CONSIGNMENT_URL)) {
      return SystemConstants.CONSIGNMENT_TYPE_PURCHASE;
    } else if (consignmentTypePath.equals(ConsignmentService.PURCHASE_RETURN_CONSIGNMENT_URL)) {
      return SystemConstants.CONSIGNMENT_TYPE_PURCHASE_RETURN;
    } else if (consignmentTypePath.equals(ConsignmentService.SALES_CONSIGNMENT_URL)) {
      return SystemConstants.CONSIGNMENT_TYPE_SALES;
    } else if (consignmentTypePath.equals(ConsignmentService.salesReturnConsignment)) {
      return SystemConstants.CONSIGNMENT_TYPE_SALES_RETURN;
    } else if (consignmentTypePath.equals(ConsignmentService.PURCHASE_RETURN_URL)) {
      return SystemConstants.CONSIGNMENT_TYPE_PURCHASE_RETURN;
    } else {
      return null;
    }
  }

  /**
   *
   * @param id
   */
  private void setConsignmentType(Integer id) {
    ConsignmentType ct = new ConsignmentType();
    ct.setId(id);
    getConsignment().setConsignmentTypeId(ct);
  }

  public void handleTransportMode(SelectEvent evt) {
    TransportMode tmode = (TransportMode) evt.getObject();
    if (getConsignmentDetail().getId() != null) {
      if (getConsignmentDetail().getTransportModeId() != null && !(getConsignmentDetail().getTransportModeId().getId().equals(getOldTransportMode().getId()))) {
        Jsf.execute("PF('cfmTransportMode').show();");
      }
    }
  }

  public void resetTransportMode(int yesNo) {
    if (yesNo == 1) {
      setOldTransportMode(getConsignmentDetail().getTransportModeId());
    } else {
      getConsignmentDetail().setTransportModeId(getOldTransportMode());
    }

  }

  public boolean isCustomerEditable() {
    return customerEditable;
  }

  public void setCustomerEditable(boolean customerEditable) {
    this.customerEditable = customerEditable;
  }

  /**
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchConsignment(MainView main, String viewType) {
    consignmentCommodityList = null;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
//        if (parent != null) {
//          ConsignmentCommodity cc = ConsignmentCommodityService.ConsignmentByReturnId(main, parent);
//          if (cc != null) {
//            getConsignment().setId(cc.getConsignmentId().getId());
//            activeIndex = 2;
//            viewType = "editform";
//            getConsignmentCommodityList(main);
//          }
//        }
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          if (purchaseReturn != null || salesInvoice != null) {
            getConsignment().reset();
          }
          setCustomerEditable(false);
          activeIndex = 0;
          setFlag(0);
          //main.setViewType(ViewTypes.list);
          if (UserRuntimeView.instance().getAccountCurrent() == null) {
            // main.error("account.required");
            // main.setViewType();
            //return switchConsignment(main, ViewTypes.list);
            //return null;
          }
          main.setViewType(viewType);

          if (!UserRuntimeView.instance().getAppUser().isRoot()) {

            if (SecurityRuntimeView.isVendor()) {
              getConsignment().setVendorId(UserRuntimeView.instance().getAppUser().getUserProfileId().getVendorId());
            }
            if (SecurityRuntimeView.isCompany()) {
              getConsignment().setVendorId(UserRuntimeView.instance().getAccount().getVendorId());
              getConsignment().setCompanyId(UserRuntimeView.instance().getAppUser().getUserProfileId().getCompanyId());
            }
            if (SecurityRuntimeView.isCustomer()) {
              getConsignment().setCustomerId(UserRuntimeView.instance().getAppUser().getUserProfileId().getCustomerId());
              getConsignment().setCompanyId(UserRuntimeView.instance().getCompany());
            }
          }

          setConsignmentType(getConsignmentTypeId(main));

          if (getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_SALES)) {
            if (salesInvoice != null) {
              getConsignment().setAccountGroupId(salesInvoice.getAccountGroupId());
              getConsignment().setCustomerId(salesInvoice.getCustomerId());
            } else {
              getConsignment().setAccountGroupId(UserRuntimeView.instance().getAccountGroup());
            }
            getConsignment().setCompanyId(UserRuntimeView.instance().getCompany());
          } else if (getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_SALES_RETURN)) {
            if (getConsignment().getAccountGroupId() == null) {
              getConsignment().setAccountGroupId(UserRuntimeView.instance().getAccountGroup());
            }
            getConsignment().setCompanyId(UserRuntimeView.instance().getCompany());
          } else {
            getConsignment().setAccountId(UserRuntimeView.instance().getAccount());
            getConsignment().setCompanyId(UserRuntimeView.instance().getCompany());
          }

          /**
           * sets consignment number.
           */
          if (getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_PURCHASE)) {
            getConsignment().setConsignmentNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, getConsignment().getAccountId(), PrefixTypeService.PURCHASE_CONSIGNMENT_PREFIX_ID, false, getConsignment().getFinancialYearId()));
          }
          if (getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_PURCHASE_RETURN)) {
            getConsignment().setConsignmentNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, getConsignment().getAccountId(), PrefixTypeService.PURCHASE_RETURN_CONSIGNMENT_PREFIX_ID, false, getConsignment().getFinancialYearId()));
          }
          if (getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_SALES)) {
            getConsignment().setConsignmentNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, getConsignment().getAccountGroupId(), PrefixTypeService.SALES_INVOICE_CONSIGNMENT_PREFIX_ID, false, getConsignment().getFinancialYearId()));
          }
          if (getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_SALES_RETURN)) {
            getConsignment().setConsignmentNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, getConsignment().getAccountGroupId(), PrefixTypeService.SALES_RETURN_CONSIGNMENT_PREFIX_ID, false, getConsignment().getFinancialYearId()));
          }

          if (isToCompany()) {
            ConsignmentService.setToCompanyAddress(main, getConsignment());
          }
//          if (isToCustomer()) {
//            ConsignmentService.setToCustomerAddress(main, getConsignment());
//          }
          if (isFromCompany()) {
            ConsignmentService.setFromCompanyAddress(main, getConsignment());
          }
          if (isFromVendor()) {
            if (getConsignment().getVendorId() == null) {
              getConsignment().setVendorId(UserRuntimeView.instance().getAccount().getVendorId());
            }
            ConsignmentService.setFromVendorAddress(main, getConsignment());
          }
          if (isToVendor()) {
            if (getConsignment().getVendorId() == null) {
              getConsignment().setVendorId(UserRuntimeView.instance().getAccount().getVendorId());
            }
            ConsignmentService.setToVendorAddress(main, getConsignment());
          }
          if (isToCustomer()) {
            if (getConsignment().getCustomerId() != null) {
              ConsignmentService.setToCustomerAddress(main, getConsignment());
            }
          }
          if (purchaseReturn != null) {
            getConsignment().setConsignmentDate(new Date());
            saveConsignment(main, SystemConstants.CONSIGNMENT_STATUS_DRAFT);
            ConsignmentCommodityService.insertPurchaseReturnItems(main, new PurchaseReturn[]{purchaseReturn}, getConsignment());
            main.commit();
            activeIndex = 2;
          } else if (salesInvoice != null) {
            getConsignment().setConsignmentDate(new Date());
            getConsignment().setCustomerId(salesInvoice.getCustomerId());
            saveConsignment(main, SystemConstants.CONSIGNMENT_STATUS_DRAFT);
            ConsignmentCommodityService.insertArraySales(main, new SalesInvoice[]{salesInvoice}, getConsignment());
            main.commit();
            activeIndex = 2;
          }
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setConsignment((Consignment) ConsignmentService.selectByPk(main, getConsignment()));

          setConsignmentDetail(ConsignmentDetailService.selectConsignmentDetailByConsignment(main, getConsignment().getId()));
          receipt = ConsignmentService.selectReceipt(main, getConsignment());
          if (getConsignmentDetail() != null && getConsignmentDetail().getId() != null) {
            setOldTransportMode(getConsignmentDetail().getTransportModeId());
          }
          if (SystemConstants.CONSIGNMENT_TYPE_SALES.equals(getConsignment().getConsignmentTypeId().getId())) {
            setCustomerEditable(ConsignmentService.isConsignmentReferenceExist(main, getConsignment()));
          }
        } else if (ViewType.list.toString().equals(viewType)) {
//          if (SecurityRuntimeView.isCustomer()) {
//            if (ConsignmentService.customerConsignmentCount(main, UserRuntimeView.instance().getAppUser().getUserProfileId().getCustomerId()) != 0) {
//              setHideList(false);
//            }
//          } else {
//            setHideList(true);
//          }
          getConsignment().reset();
          getConsignment().setCompanyId(UserRuntimeView.instance().getCompany());
          getConsignment().setFinancialYearId(UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
          setConsignmentType(getConsignmentTypeId(main));
          if (SystemConstants.CONSIGNMENT_TYPE_PURCHASE.equals(getConsignment().getConsignmentTypeId().getId()) || SystemConstants.CONSIGNMENT_TYPE_PURCHASE_RETURN.equals(getConsignment().getConsignmentTypeId().getId())) {
            getConsignment().setAccountId(UserRuntimeView.instance().getAccount());
          } else if (SystemConstants.CONSIGNMENT_TYPE_SALES.equals(getConsignment().getConsignmentTypeId().getId()) || SystemConstants.CONSIGNMENT_TYPE_SALES_RETURN.equals(getConsignment().getConsignmentTypeId().getId())) {
            getConsignment().setAccountGroupId(UserRuntimeView.instance().getAccountGroup());
          }
          loadConsignmentList(main);
          activeIndex = 0;
          setCustomerEditable(false);
          main.getPageData().setSearchKeyWord(null);
        }
      } catch (Throwable t) {
        main.rollback(t);
        if (main.isNew()) {
          main.setViewType(ViewTypes.list);
        }
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create consignmentLazyModel.
   *
   * @param main
   */
  private void loadConsignmentList(final MainView main) {
    if (consignmentLazyModel == null) {
      consignmentLazyModel = new LazyDataModel<Consignment>() {
        private List<Consignment> list;

        @Override
        public List<Consignment> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
//            if (UserRuntimeView.instance().getAppUser().getId() == null) {
            if (SecurityRuntimeView.isVendor()) {
              list = ConsignmentService.listPagedConsignmentByCompanyAndConsignmentTypeAndVendor(main, getConsignment(), UserRuntimeView.instance().getAppUser().getUserProfileId().getVendorId().getId(), UserRuntimeView.instance().getAccountCurrent());
            } else if (SecurityRuntimeView.isCompany()) {
              if (isSalesConsignment() || isSalesReturnConsignment()) {
                list = ConsignmentService.listPagedConsignmentByCompanyAndAccountGroup(main, getConsignment(), UserRuntimeView.instance().getAppUser().getUserProfileId().getCompanyId().getId(), UserRuntimeView.instance().getAccountGroup());
              } else {
                list = ConsignmentService.listPagedConsignmentByCompanyAndConsignmentTypeAndCompany(main, getConsignment(), UserRuntimeView.instance().getAppUser().getUserProfileId().getCompanyId().getId(), UserRuntimeView.instance().getAccountCurrent());
              }
            } else if (SecurityRuntimeView.isCustomer()) {
              list = ConsignmentService.listPagedConsignmentByCompanyAndConsignmentTypeAndCustomer(main, getConsignment(), UserRuntimeView.instance().getAppUser().getUserProfileId().getCustomerId().getId(), UserRuntimeView.instance().getAccountCurrent());
            } else {
              list = ConsignmentService.listPagedConsignmentByCompanyAndConsignmentType(main, getConsignment());
            }
            main.commit(consignmentLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Consignment consignment) {
          return consignment.getId();
        }

        @Override
        public Consignment getRowData(String rowKey) {
          if (list != null) {
            for (Consignment obj : list) {
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

  public void saveConsignmentClose(MainView main, int status) {
    saveConsignment(main, status);
    Jsf.execute("parent.purchaseReturnConsignmentPopupReturned()");
    Jsf.execute("closePopup()");
  }

  /**
   * Insert or update.
   *
   * @param main
   * @param status
   * @return the page to display.
   */
  public String saveConsignment(MainView main, int status) {
    return saveOrCloneConsignment(main, "save", status);
  }

  public String saveConsignmentStatus(MainView main, int status) {
    try {
      if (status == 6) {
        // && getConsignment().getCompanyId().getTaxCriteria() == 1
        if (AccountUtil.isInterStatePurchase(getConsignment().getAccountId()) && ((getConsignment().getCompanyId() != null) || (getConsignment().getVendorId() != null && getConsignment().getVendorId().getTaxApplicable() == 1))) {
          if (!confirmWaybill() && is8F()) {
            main.error("Enter.document.detail");
            setActiveIndex(2);
            return null;
          }
        }
      }
      if (status == 7) {
        if (SecurityRuntimeView.isCompany()) {
          if (isPurchaseReturnConsignment()) {
            //&& getConsignment().getCompanyId().getTaxCriteria() == 1
            if (AccountUtil.isInterStatePurchase(getConsignment().getAccountId()) && (getConsignment().getCompanyId() != null)) {
              if (!exitDocDetail()) {
                main.error("Enter.ExitDoc");
                setActiveIndex(2);
                return null;
              }
            }
          }
          if (!hasNoReceiptDetail()) {
            main.error("Enter.receipt");
            setActiveIndex(2);
            return null;
          }
        }
      }
      if (status == 8) {
//        status = getConsignment().getConsignmentStatusId().getId();
        if (getConsignmentDetail().getReceiptReturnFilePath() == null) {
          main.error("Enter.ReceiptReturnFilePath");
          setActiveIndex(2);
          return null;
        }
      }
      if (isPurchaseReturnConsignment() && status == 7) {
        ConsignmentDocType docType = new ConsignmentDocType();
        docType.setId(8);
        consignmentDetail.setExitDocumentTypeId(docType);
        consignmentDetail.setExitDocument2TypeId(docType);
      }
      ConsignmentService.setStatus(main, status, getConsignment());
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneConsignment(MainView main) {
    main.setViewType("newform");
    return saveOrCloneConsignment(main, "clone", SystemConstants.CONSIGNMENT_STATUS_DRAFT);
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneConsignment(MainView main, String key, int status) {
    try {
//      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
//            Date consignmentDate = getConsignment().getConsignmentDate();
//            Date today = new Date();
//            int num = 0;
//            num = today.compareTo(consignmentDate);
//            if (num == 1) {
//              main.error("error.date");
//              return null;
//            }
            if (status == SystemConstants.CONSIGNMENT_STATUS_CONFIRMED) {
              getConsignmentCommodityList(main);
              if (consignmentCommodityList != null && consignmentCommodityList.isEmpty()) {
                main.error("enter.commodity");
                setActiveIndex(1);
                return "";
              }
//              if (!isReceiptEntered(getConsignment())) {
//                main.error("Enter.list.receipt");
//                main.setViewType(ViewTypes.list);
//                return null;
//              }
              if (consignmentDetail.getId() == null) {
                main.error("enter.consignment.detail");
                setActiveIndex(2);
                return "";
              }
            }
            ConsignmentService.setStatus(main, status, getConsignment());
            ConsignmentService.insertOrUpdate(main, getConsignment());
            if (main.isNew()) {
              if (getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_PURCHASE)) {
                AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, getConsignment().getAccountId(), PrefixTypeService.PURCHASE_CONSIGNMENT_PREFIX_ID, false, getConsignment().getFinancialYearId());
              }
              if (getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_PURCHASE_RETURN)) {
                AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, getConsignment().getAccountId(), PrefixTypeService.PURCHASE_RETURN_CONSIGNMENT_PREFIX_ID, false, getConsignment().getFinancialYearId());
              }
              if (getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_SALES)) {
                AccountGroupDocPrefixService.updateSalesPrefixSequence(main, getConsignment().getAccountGroupId(), PrefixTypeService.SALES_INVOICE_CONSIGNMENT_PREFIX_ID, false, getConsignment().getFinancialYearId());
              }
            }
            break;
          case "clone":
            ConsignmentService.clone(main, getConsignment());
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
   * Delete one or many Consignment.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteConsignment(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(consignmentSelected)) {
        ConsignmentService.deleteByPkArray(main, getConsignmentSelected()); //many record delete from list
        main.commit("success.delete");
        consignmentSelected = null;
      } else {
        ConsignmentService.deleteByPk(main, getConsignment());  //individual record delete from list or edit form
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
   * Return LazyDataModel of Consignment.
   *
   * @return
   */
  public LazyDataModel<Consignment> getConsignmentLazyModel() {
    return consignmentLazyModel;
  }

  /**
   * Return Consignment[].
   *
   * @return
   */
  public Consignment[] getConsignmentSelected() {
    return consignmentSelected;
  }

  /**
   * Set Consignment[].
   *
   * @param consignmentSelected
   */
  public void setConsignmentSelected(Consignment[] consignmentSelected) {
    this.consignmentSelected = consignmentSelected;
  }

  /**
   * Vendor autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.vendorAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.vendorAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Vendor> vendorAuto(String filter) {
    if (UserRuntimeView.instance().getCompany() != null) {
      return ScmLookupExtView.consignmentVendorByCompanyAuto(UserRuntimeView.instance().getCompany().getId(), filter);
    } else {
      return ScmLookupView.vendorAuto(filter);
    }
  }

  public List<Vendor> vendorAutoPR(String filter) {
    if (UserRuntimeView.instance().getAccountCurrent().getVendorId() != null) {
      return ScmLookupExtView.consignmentVendorByVatApplicable(UserRuntimeView.instance().getCompany().getId(), filter);
    } else {
      return ScmLookupView.vendorAuto(filter);
    }
  }

  /**
   * Company autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.companyAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.companyAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Company> companyAuto(String filter) {
    return ScmLookupView.companyAuto(filter);
  }

  /**
   * Customer autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.customerAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.customerAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Customer> customerAuto(String filter) {
    if (UserRuntimeView.instance().getCompany() != null) {
      return ScmLookupExtView.consignmentCustomerByCompanyAuto(UserRuntimeView.instance().getCompany().getId(), filter);
    } else {
      return ScmLookupView.customerAuto(filter);
    }
  }

  /**
   * TransportMode autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.transportModeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.transportModeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<TransportMode> transportModeAuto(String filter) {
    return ScmLookupView.transportModeAuto(filter);
  }

  /**
   * ConsignmentStatus autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.consignmentStatusAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.consignmentStatusAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ConsignmentStatus> consignmentStatusAuto(String filter) {
    return ScmLookupView.consignmentStatusAuto(filter);
  }

  /**
   * ConsignmentType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.consignmentTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.consignmentTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ConsignmentType> consignmentTypeAuto(String filter) {
    return ScmLookupView.consignmentTypeAuto(filter);
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }

  public void fromVendorDetails(SelectEvent event) {
    MainView main = Jsf.getMain();
    //TODO adress selection must be based on address type consignment
    try {
      getConsignment().setVendorId((Vendor) event.getObject());
      if (getConsignment().getVendorId() != null) {
        ConsignmentService.setFromVendorAddress(main, getConsignment());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void fromVendorDetail(SelectEvent event) {
    MainView main = Jsf.getMain();
    //TODO adress selection must be based on address type consignment
    try {
      if (getConsignment().getVendorId() != null) {
        ConsignmentService.setFromVendorAddress(main, getConsignment());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void fromCompanyDetail(SelectEvent event) {
    MainView main = Jsf.getMain();
    try {
      ConsignmentService.setFromCompanyAddress(main, getConsignment());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void fromCustomerDetail(SelectEvent event) {
    MainView main = Jsf.getMain();
    try {
      if (getConsignment().getCustomerId() != null) {
        ConsignmentService.setFromCustomerAddress(main, getConsignment());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void toCustomerDetail(SelectEvent event) {
    //setConsignment((Consignment) event.getObject());
    MainView main = Jsf.getMain();
    try {
      if (getConsignment().getCustomerId() != null) {
        ConsignmentService.setToCustomerAddress(main, getConsignment());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void toCompanyDetail(SelectEvent event) {
    MainView main = Jsf.getMain();
    try {
      ConsignmentService.setToCompanyAddress(main, getConsignment());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void toVendorDetail(SelectEvent event) {
    MainView main = Jsf.getMain();
    try {
      if (getConsignment().getVendorId() != null) {
        ConsignmentService.setToVendorAddress(main, getConsignment());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public boolean isFromVendor() {
    if (getConsignment().getConsignmentTypeId() != null) {
      return (getConsignment().getConsignmentTypeId().getId() == SystemConstants.CONSIGNMENT_TYPE_PURCHASE.intValue());
    } else {
      return false;
    }
  }

  public boolean isFromCompany() {
    if (getConsignment().getConsignmentTypeId() != null) {
      return (getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_SALES) || getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_PURCHASE_RETURN));
    } else {
      return false;
    }
  }

  public boolean isFromCustomer() {
    if (getConsignment().getConsignmentTypeId() != null) {
      return (getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_SALES_RETURN));
    } else {
      return false;
    }
  }

  public boolean isToVendor() {
    if (getConsignment().getConsignmentTypeId() != null) {
      return (getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_PURCHASE_RETURN));
    } else {
      return false;
    }
  }

  public boolean isToCompany() {
    if (getConsignment().getConsignmentTypeId() != null) {
      return (getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_SALES_RETURN) || getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_PURCHASE));
    } else {
      return false;
    }
  }

  public boolean isToCustomer() {
    if (getConsignment().getConsignmentTypeId() != null) {
      return (getConsignment().getConsignmentTypeId().getId().equals(SystemConstants.CONSIGNMENT_TYPE_SALES));
    } else {
      return false;
    }
  }

  public boolean isDraft() {
    return getConsignment().getConsignmentStatusId() != null && getConsignment().getConsignmentStatusId().getId() == SystemConstants.CONSIGNMENT_STATUS_DRAFT;
  }

  public boolean isNewConsignment() {
    return getConsignment().getConsignmentStatusId() == null;
  }

  public boolean isConfirm() {
    return getConsignment().getConsignmentStatusId() != null && getConsignment().getConsignmentStatusId().getId() == SystemConstants.CONSIGNMENT_STATUS_CONFIRMED;
  }

  public boolean isConfirmed() {
    return getConsignment().getConsignmentStatusId() != null && getConsignment().getConsignmentStatusId().getId() >= SystemConstants.CONSIGNMENT_STATUS_CONFIRMED;
  }

  public boolean isBeforeConfirm() {
    return getConsignment().getConsignmentStatusId() != null && getConsignment().getConsignmentStatusId().getId() < SystemConstants.CONSIGNMENT_STATUS_CONFIRMED;
  }

  public boolean isConfirmCancelled() {
    return getConsignment().getConsignmentStatusId() != null && getConsignment().getConsignmentStatusId().getId() == SystemConstants.CONSIGNMENT_STATUS_CENCELLED;
  }

  public boolean isConfirmWaybill() {
    return getConsignment().getConsignmentStatusId() != null && getConsignment().getConsignmentStatusId().getId() == SystemConstants.CONSIGNMENT_STATUS_CENCELLED;
  }

  public boolean isDespatched() {
    return getConsignment().getConsignmentStatusId() != null && getConsignment().getConsignmentStatusId().getId() == SystemConstants.CONSIGNMENT_STATUS_DISPATCHED;
  }

  /**
   * Return all commodity of a consignment.
   *
   * @param main
   * @return
   */
  public List<ConsignmentCommodity> getConsignmentCommodityList(MainView main) {
    if (consignmentCommodityList == null) {
      try {
        consignmentCommodityList = ConsignmentCommodityService.getConsignmentCommodityList(main, getConsignment());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return consignmentCommodityList;
  }

  public void consignmentCommodityNewPopup() {
    Jsf.popupForm(FileConstant.CONSIGNMENT_COMMODITY, getConsignment());
  }

  public void consignmentCommodityInvoiceNewPopup() {
    Jsf.popupForm(FileConstant.CONSIGNMENT_COMMODITY_INVOICE, getConsignment());
  }

  public void consignmentCommodityPopupReturned() {
    consignmentCommodityList = null; // Reset to null to fetch updated list
  }

  public String saveConsignmentDetail(MainView main) {
    try {
      uploadFiles();
      getConsignmentDetail().setConsignmentId(consignment);
//      if (isPurchaseConsignment()) {
//        getConsignmentDetail().setEntryDocumentTypeId((ConsignmentDocType) getConsignmentDocumentType(main, getConsignmentDetail().getTransportModeId().getId()));
//      } else {
//        // To Do : purchase return, sales, sales return.
//      }

      if (ConsignmentService.BY_ROAD != getConsignmentDetail().getTransportModeId().getId()) {
        getConsignmentDetail().setVehicleNo(null);
        getConsignmentDetail().setVehicleType(null);
        getConsignmentDetail().setDriverName(null);
        getConsignmentDetail().setDriverLicenseNo(null);
      }
      Date checkPostExpectedArrivalDate = getConsignmentDetail().getEntryCheckpostArrivalAt();
      Date consignmentDate = getConsignment().getConsignmentDate();
      int num = 0;
      if (checkPostExpectedArrivalDate != null) {
        num = checkPostExpectedArrivalDate.compareTo(consignmentDate);
        if (num < 0 || num == 0) {
          main.error("error.checkpostArrivalDate");
          return null;
        }
      }
      ConsignmentDetailService.insertOrUpdate(main, consignmentDetail);
      if (getConsignment().getConsignmentStatusId() != null && (getConsignment().getConsignmentStatusId().getId() != SystemConstants.CONSIGNMENT_STATUS_FULLY_DAMAGED_CANCELLED)) {
        rStatus = getConsignment().getConsignmentStatusId().getId();
        ConsignmentService.setStatus(main, rStatus, getConsignment());
      }
      main.commit("success.save");
      setFlag(1);
      setActiveIndex(2);
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  public List<ConsignmentDocType> getConsignmentDocumentType(MainView main, Integer transportModeId) {
    try {
      return ConsignmentDocTypeService.selectDocTypeByTransportMode(main, transportModeId, getConsignment().getConsigneeStateId().getId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public Consignment getConsignmentStatusLog(MainView main) {

    try {
      return ConsignmentService.selectConsignmentStatusLog(main, getConsignment().getId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
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
   * ConsignmentDocType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.consignmentDocTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.consignmentDocTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ConsignmentDocType> consignmentDocTypeAuto(String filter) {
    if (consignment.getTransportModeId().getId() != null) {
      return ScmLookupExtView.consignmentDocTypeAuto(consignment.getTransportModeId().getId(), filter);
    }
    return ScmLookupView.consignmentDocTypeAuto(filter);
  }

  /**
   * ConsignmentReceiptType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.consignmentReceiptTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.consignmentReceiptTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ConsignmentReceiptType> consignmentReceiptTypeAuto(String filter) {
    if (getConsignmentDetail().getTransportModeId() != null) {
      return ScmLookupExtView.consignmentReceiptTypeAuto(getConsignmentDetail().getTransportModeId().getId(), filter);
    }
    return ScmLookupView.consignmentReceiptTypeAuto(filter);
  }

  /**
   * Transporter autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.transporterAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.transporterAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Transporter> transporterAuto(String filter) {
    if (UserRuntimeView.instance().getCompany() != null) {
      return ScmLookupExtView.transporterByCompanyAuto(UserRuntimeView.instance().getCompany().getId(), filter);
    } else {
      return ScmLookupView.transporterAuto(filter);
    }
  }

  /**
   * Return Part.
   *
   * @return Part.
   */
  public Part getExitDocumentFilePathPart() {
    return exitDocumentFilePathPart;
  }

  /**
   * Set Part exitDocumentFilePathPart.
   *
   * @param exitDocumentFilePathPart.
   */
  public void setExitDocumentFilePathPart(Part exitDocumentFilePathPart) {
    if (this.exitDocumentFilePathPart == null || exitDocumentFilePathPart != null) {
      this.exitDocumentFilePathPart = exitDocumentFilePathPart;
    }
  }

  /**
   * Return Part.
   *
   * @return Part.
   */
  public Part getExitDocument2FilePathPart() {
    return exitDocument2FilePathPart;
  }

  /**
   * Set Part exitDocument2FilePathPart.
   *
   * @param exitDocument2FilePathPart.
   */
  public void setExitDocument2FilePathPart(Part exitDocument2FilePathPart) {
    if (this.exitDocument2FilePathPart == null || exitDocument2FilePathPart != null) {
      this.exitDocument2FilePathPart = exitDocument2FilePathPart;
    }
  }

  /**
   * Return Part.
   *
   * @return Part.
   */
  public Part getEntryDocumentFilePathPart() {
    return entryDocumentFilePathPart;
  }

  /**
   * Set Part entryDocumentFilePathPart.
   *
   * @param entryDocumentFilePathPart.
   */
  public void setEntryDocumentFilePathPart(Part entryDocumentFilePathPart) {
    if (this.entryDocumentFilePathPart == null || entryDocumentFilePathPart != null) {
      this.entryDocumentFilePathPart = entryDocumentFilePathPart;
    }
  }

  /**
   * Return Part.
   *
   * @return Part.
   */
  public Part getReceiptFilePathPart() {
    return receiptFilePathPart;
  }

  /**
   * Set Part receiptFilePathPart.
   *
   * @param receiptFilePathPart.
   */
  public void setReceiptFilePathPart(Part receiptFilePathPart) {
    if (this.receiptFilePathPart == null || receiptFilePathPart != null) {
      this.receiptFilePathPart = receiptFilePathPart;
    }
  }

  /**
   * Return Part.
   *
   * @return Part.
   */
  public Part getReceiptReturnFilePathPart() {
    return receiptReturnFilePathPart;
  }

  /**
   * Set Part receiptReturnFilePathPart.
   *
   * @param receiptReturnFilePathPart.
   */
  public void setReceiptReturnFilePathPart(Part receiptReturnFilePathPart) {
    if (this.receiptReturnFilePathPart == null || receiptReturnFilePathPart != null) {
      this.receiptReturnFilePathPart = receiptReturnFilePathPart;
    }
  }

  private void uploadFiles() throws IOException {
    String SUB_FOLDER = "scm_consignment_detail/";
    if (exitDocumentFilePathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(exitDocumentFilePathPart, getConsignmentDetail().getExitDocumentFilePath(), SUB_FOLDER);
      getConsignmentDetail().setExitDocumentFilePath(JsfIo.getDbPath(exitDocumentFilePathPart, SUB_FOLDER));
      exitDocumentFilePathPart = null;	//import to set as null.
    }
    if (exitDocument2FilePathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(exitDocument2FilePathPart, getConsignmentDetail().getExitDocument2FilePath(), SUB_FOLDER);
      getConsignmentDetail().setExitDocument2FilePath(JsfIo.getDbPath(exitDocument2FilePathPart, SUB_FOLDER));
      exitDocument2FilePathPart = null;	//import to set as null.
    }
    if (entryDocumentFilePathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(entryDocumentFilePathPart, getConsignmentDetail().getEntryDocumentFilePath(), SUB_FOLDER);
      getConsignmentDetail().setEntryDocumentFilePath(JsfIo.getDbPath(entryDocumentFilePathPart, SUB_FOLDER));
      entryDocumentFilePathPart = null;	//import to set as null.
    }
    if (receiptFilePathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(receiptFilePathPart, getConsignmentDetail().getReceiptFilePath(), SUB_FOLDER);
      getConsignmentDetail().setReceiptFilePath(JsfIo.getDbPath(receiptFilePathPart, SUB_FOLDER));
      receiptFilePathPart = null;	//import to set as null.
    }
    if (receiptReturnFilePathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(receiptReturnFilePathPart, getConsignmentDetail().getReceiptReturnFilePath(), SUB_FOLDER);
      getConsignmentDetail().setReceiptReturnFilePath(JsfIo.getDbPath(receiptReturnFilePathPart, SUB_FOLDER));
      receiptReturnFilePathPart = null;	//import to set as null.
    }
  }
//FIXME every time query might be going to db or if its called as loaded call one loader method and do the below three  inside that

  public List<PurchaseOrder> getPOList(MainView main) {
    try {
      POList = PurchaseOrderService.selectByPOConsignment(main, getConsignment());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return POList;
  }

  public List<SalesOrder> getSOList(MainView main) {
    try {
      SOList = SalesOrderService.selectBySOConsignment(main, getConsignment());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return SOList;
  }

  public List<PurchaseReturn> getPRList(MainView main) {
    try {
      //PRList = PurchaseReturnService.selectByPRConsignment(main, getConsignment());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return PRList;
  }

  public void setPOList(List<PurchaseOrder> POList) {
    this.POList = POList;
  }

  public void setSOList(List<SalesOrder> SOList) {
    this.SOList = SOList;
  }

  public void setPRList(List<PurchaseReturn> PRList) {
    this.PRList = PRList;
  }

  public void consignmentReferencePODialogReturn(SelectEvent event) {
    Jsf.closeDialog(getConsignment());
    POList = null; // Reset to null to fetch updated list
  }

  public void consignmentReferencePOSelectDialog() {
    Jsf.openDialogList("/scm/consignment/consignment_reference.xhtml", getConsignment());
  }

  public void consignmentReferencePOSelectPopup() {
    Jsf.popupList(FileConstant.CONSIGNMENT_REFERENCE, getConsignment());
  }

//  public void consignmentReferencePRSelectDialog1() {
//    Jsf.openDialogList("/scm/consignment/newxhtml.xhtml", getConsignment());
//  }
  public void consignmentReferencePRDialogReturn(SelectEvent event) {
    Jsf.closeDialog(getConsignment());
    PRList = null; // Reset to null to fetch updated list
  }

  public void consignmentReferencePRSelectDialog() {
    Jsf.openDialogList("/scm/consignment/consignment_reference.xhtml", getConsignment());
  }

  public String deleteConsignmentReferencePO(MainView main, PurchaseOrder purchaseOrder) {
    try {
      ConsignmentReferenceService.deleteConsignmentReferencePO(main, purchaseOrder, getConsignment());
      setPOList(null);
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  public String deleteConsignmentReferencePR(MainView main, PurchaseReturn purchaseReturn) {
    try {
      ConsignmentReferenceService.deleteConsignmentReferencePR(main, purchaseReturn, getConsignment());
      setPOList(null);
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  public void consignmentCommodityEditPopup(Integer id) {
    Jsf.popupForm(FileConstant.CONSIGNMENT_COMMODITY, getConsignment(), id);
  }

  public void consignmentReceiptPopup(Consignment consignment) {
    Jsf.popupForm(FileConstant.CONSIGNMENT_RECEIPT, consignment);
  }

  public void consignmentReceiptDialogReturn(SelectEvent event) {
    Jsf.closeDialog(getConsignment());
  }

  public boolean isDespatchedOrDelivered(Consignment consignment) {
    return (consignment.getConsignmentStatusId().getId() != SystemConstants.CONSIGNMENT_STATUS_DELIVERED && consignment.getConsignmentStatusId().getId() == SystemConstants.CONSIGNMENT_STATUS_DISPATCHED);
  }

  public boolean isReceiptEntered(Consignment consignment) {
    MainView main = Jsf.getMain();
    try {
      return ConsignmentService.selectReceipt(main, consignment) != 0;
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return false;
  }

  public boolean isReceiptOk() {
    return (receipt != 0);
  }

  public boolean isReceiptDetailEntered(ConsignmentCommodity consignmentCommodity) {
    MainView main = Jsf.getMain();
    try {
      return ConsignmentCommodityService.selectReceiptDetail(main, consignmentCommodity) != 0;
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return false;
  }

  /**
   * Autocomplete ajax event
   *
   * @param event
   */
  public void ajaxEventHandler(SelectEvent event) {
  }

  public boolean is8F() {
    return (getConsignmentDetail().getTransportModeId() != null && ConsignmentService.BY_ROAD == getConsignmentDetail().getTransportModeId().getId() && (getConsignment().getConsigneeStateId().getId() != getConsignment().getConsignorStateId().getId()));
  }

  public boolean is8FA() {
    return (getConsignmentDetail().getTransportModeId() != null && (ConsignmentService.BY_RAIL == getConsignmentDetail().getTransportModeId().getId() || ConsignmentService.BY_AIR == getConsignmentDetail().getTransportModeId().getId() || ConsignmentService.BY_SEA == getConsignmentDetail().getTransportModeId().getId()));
  }

  public boolean isTs() {
    return (getConsignmentDetail().getTransportModeId() != null && ConsignmentService.BY_COURIER == getConsignmentDetail().getTransportModeId().getId());
  }

  public boolean isPurchaseConsignment() {
    return Objects.equals(consignment.getConsignmentTypeId().getId(), SystemConstants.CONSIGNMENT_TYPE_PURCHASE);
  }

  public boolean isSalesConsignment() {
    return (getConsignment().getConsignmentTypeId().getId() == SystemConstants.CONSIGNMENT_TYPE_SALES.intValue());
  }

  public boolean isPurchaseReturnConsignment() {
    return Objects.equals(consignment.getConsignmentTypeId().getId(), SystemConstants.CONSIGNMENT_TYPE_PURCHASE_RETURN);
  }

  public boolean isSalesReturnConsignment() {
    return (consignment.getConsignmentTypeId().getId() == SystemConstants.CONSIGNMENT_TYPE_SALES_RETURN);
  }

  public boolean isInterState() {
    if (getConsignment().getConsignorStateId() != null && getConsignment().getConsignorStateId() != null) {
      if (getConsignment().getConsigneeStateId() != null) {
        return (!getConsignment().getConsigneeStateId().getId().equals(getConsignment().getConsignorStateId().getId()));
      }
      return false;
    }
    return false;
  }

  public String getDocumentType() {
    if (is8F()) {
      return "LR/GR - (Lorry Receipt/Goods Receipt)";
    } else if (is8FA()) {
      if (ConsignmentService.BY_RAIL == getConsignmentDetail().getTransportModeId().getId()) {
        return "RR - (Railway Receipt)";
      } else if (ConsignmentService.BY_AIR == getConsignmentDetail().getTransportModeId().getId()) {
        return "AWB - (Air Way Bill)";
      } else if (ConsignmentService.BY_SEA == getConsignmentDetail().getTransportModeId().getId()) {
        return "BL - (Bill of Lading)";
      }
    } else if (isTs()) {
      return "TS - (Transaction Slip)";
    }
    return null;
  }

  public boolean isConsignmentDetailSaved() {
    return getConsignmentDetail().getId() != null;
  }

  public boolean confirmWaybill() {
    return consignmentDetail.getEntryDocumentNo() != null && consignmentDetail.getEntryDocumentFilePath() != null;
  }

  public boolean renderReceiptDetailNote() {
//    consignmentView.consignment.consignmentStatusId.id>=7
    return getConsignment().getConsignmentStatusId() != null && getConsignment().getConsignmentStatusId().getId() >= 7;
  }

  public boolean hasNoReceiptDetail() {
    return consignmentDetail.getReceiptNo() != null && consignmentDetail.getReceiptFilePath() != null;
  }

  public boolean exitDocDetail() {
    return consignmentDetail.getExitCheckpostName() != null && consignmentDetail.getExitCheckpostArrivalAt() != null && consignmentDetail.getExitDocumentNo() != null
            && consignmentDetail.getExitDocument2No() != null && consignmentDetail.getExitDocumentFilePath() != null && consignmentDetail.getExitDocument2FilePath() != null;
  }

  public List<ConsignmentStatus> selectConsignmentStatus(MainView main) {
    try {
      return ConsignmentStatusService.selectConsignmentStatus(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public boolean renderDocumentPurchase() {
    // && getConsignment().getCompanyId().getTaxCriteria() == 1
    return getConsignment().getCompanyId() != null && AccountUtil.isInterStatePurchase(getConsignment().getAccountId());
  }

  public boolean renderDocumentPurchaseReturn() {
    return getConsignment().getVendorId() != null && getConsignment().getVendorId().getTaxApplicable() == 1 && AccountUtil.isInterStatePurchase(getConsignment().getAccountId());
  }

  public void clearDocType(SelectEvent event) {
    getConsignmentDetail().setEntryDocumentTypeId(null);
    TransportMode tmode = (TransportMode) event.getObject();
    if (ConsignmentService.BY_ROAD == tmode.getId()) {
      List<ConsignmentDocType> consignmentDocType = null;
      MainView main = Jsf.getMain();
      try {
        consignmentDocType = ConsignmentDocTypeService.selectDocTypeByTransportMode(main, tmode.getId(), getConsignment().getConsigneeStateId().getId());
        for (ConsignmentDocType cdt : consignmentDocType) {
          if (ConsignmentService.wb8F == cdt.getId()) {
            consignmentDetail.setEntryDocumentTypeId(cdt);
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
  }

  public String getConsignmentPackingUnit(Integer packUnit) {
    if (packUnit != null) {
      return SystemRuntimeConfig.CONSIGNMENT_PACKING_UNIT[packUnit - 1];
    }
    return null;
  }

  public String getConsignmentWeightUnit(Integer weightUnit) {
    if (weightUnit != null) {
      return SystemRuntimeConfig.CONSIGNMENT_WEIGHT_UNIT[weightUnit - 1];
    }
    return null;
  }

  public void showExpense(MainView main) {
    Jsf.popupForm(FileConstant.EXPENSE, getConsignment(), getConsignment().getId());
  }
}
