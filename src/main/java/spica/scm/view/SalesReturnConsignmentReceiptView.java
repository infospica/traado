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
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Consignment;
import spica.scm.domain.ConsignmentDetail;
import spica.scm.domain.ConsignmentStatus;
import spica.scm.domain.Customer;
import spica.scm.domain.SalesReturn;
import spica.scm.service.ConsignmentDetailService;
import spica.scm.service.ConsignmentService;
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
@Named(value = "salesReturnConsignmentReceiptView")
@ViewScoped
public class SalesReturnConsignmentReceiptView implements Serializable {

  private transient LazyDataModel<ConsignmentDetail> consignmentReceiptLazyModel; 	//For lazy loading datatable.
  private transient ConsignmentDetail[] consignmentReceiptSelected;	 //Selected Domain Array
  private transient Consignment consignment;
  private transient Consignment selectedConsignment;
  private transient ConsignmentDetail consignmentDetail;
  private transient Part documentPathPart;
  private transient SalesReturn salesReturnId;

  /**
   * Getting the parent object and id for edit.
   */
  //private Consignment parent;
  @PostConstruct
  public void init() {
    MainView main = Jsf.getMain();
    try {
      salesReturnId = (SalesReturn) Jsf.popupParentValue(SalesReturn.class);
      if (salesReturnId != null) {
        consignment = salesReturnId.getConsignmentId();
      }
      if (consignment != null) {
        setConsignmentDetail((ConsignmentDetail) ConsignmentDetailService.selectConsignmentDetailByConsignment(main, consignment.getId()));
        if (consignmentDetail == null) {
          getConsignmentDetail().setConsignmentId(consignment);
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
  public SalesReturnConsignmentReceiptView() {
    super();
  }

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

  public SalesReturn getSalesReturnId() {
    return salesReturnId;
  }

  public void setSalesReturnId(SalesReturn salesReturnId) {
    this.salesReturnId = salesReturnId;
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
    if (!StringUtil.isEmpty(viewType) && !main.hasError()) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          if (UserRuntimeView.instance().getCompany() == null) {
            main.error("company.required");
            main.setViewType(ViewTypes.list);
            return null;
          } else if (UserRuntimeView.instance().getAccountGroup() == null) {
            main.error("account.group.required");
            if (ViewType.newform.toString().equals(viewType)) {
              main.setViewType(ViewTypes.list);
            }
            return null;
          }
          setConsignment(null);
          getConsignment();
          getConsignmentDetail().reset();
          if (salesReturnId != null) {
            getConsignment().setCompanyId(salesReturnId.getCompanyId());
          }
          setConsignorDetails(main);
          if (salesReturnId != null) {
            if (salesReturnId.getCustomerId() != null && getConsignment().getCustomerId() == null) {
              getConsignment().setCustomerId(salesReturnId.getCustomerId());
              updateConsignorDetails(main, getConsignment().getCustomerId());
            }
            if (salesReturnId.getAccountGroupId() != null && getConsignment().getAccountGroupId() == null) {
              getConsignment().setAccountGroupId(salesReturnId.getAccountGroupId());
            }
          }
        } else if (ViewType.editform.toString().equals(viewType)) {
          setConsignmentDetail((ConsignmentDetail) ConsignmentDetailService.selectByPk(main, getConsignmentDetail()));
          setConsignment(getConsignmentDetail().getConsignmentId());
        } else if (ViewType.list.toString().equals(viewType)) {
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
    if (getConsignment().getCompanyId() == null) {
      getConsignment().setCompanyId(UserRuntimeView.instance().getCompany());
    }
    ConsignmentService.setToCompanyAddress(main, getConsignment());
    if (getConsignment().getCustomerId() != null) {
      ConsignmentService.setFromCustomerAddress(main, getConsignment());
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
            if (UserRuntimeView.instance().getAccountGroup() != null) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = ConsignmentDetailService.listPagedSalesReturnConsignmentByAccount(main, UserRuntimeView.instance().getAccountGroup(), UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
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

  /**
   *
   * @param main
   */
  public void saveConsignmentReceiptDetailClose(MainView main) {
    try {
      saveConsignmentReceiptInfo(main);
      consignmentPopupClose();
      Jsf.execute("parent.consignmentReturned()");
      Jsf.execute("closePopup()");
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
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

  public String saveConsignmentReceiptInfo(MainView main) {
    try {
      uploadFiles(); //File upload
      getConsignment().setConsignmentDetail(getConsignmentDetail());
      ConsignmentService.insertOrUpdateSalesReturnConsignmentDetail(main, getConsignment());
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
   * Customer autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.customerAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.customerAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Customer> customerAuto(String filter) {
    return ScmLookupExtView.customerByCompany(getConsignment().getCompanyId() != null ? getConsignment().getCompanyId() : UserRuntimeView.instance().getCompany(), filter);
  }

  public List<ConsignmentStatus> consignmentStatus() {
    return ScmLookupExtView.lookupConsignmentReceiptStatus();
  }

  public void consignmentReceiptPopupClose() {
    Jsf.popupReturn(consignment, null);
  }

  public void customerSelectEvent(SelectEvent event) {
    MainView main = Jsf.getMain();
    try {
      Customer cust = (Customer) event.getObject();
      updateConsignorDetails(main, cust);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private void updateConsignorDetails(MainView main, Customer cust) {
    if (cust != null) {
      getConsignment().setCustomerId(cust);
      ConsignmentService.setFromCustomerAddress(main, getConsignment());
    } else {
      resetConsignorDetails();
    }
  }

  private void resetConsignorDetails() {
    if (getConsignment() != null) {
      getConsignment().setCustomerId(null);
      getConsignment().setConsignorAddress(null);
      getConsignment().setConsignorCountry(null);
      getConsignment().setConsignorCst(null);
      getConsignment().setConsignorDistrict(null);
      getConsignment().setConsignorEmail(null);
      getConsignment().setConsignorFax1(null);
      getConsignment().setConsignorFax2(null);
      getConsignment().setConsignorPhone1(null);
      getConsignment().setConsignorPhone2(null);
      getConsignment().setConsignorPhone3(null);
      getConsignment().setConsignorPin(null);
      getConsignment().setConsignorStateId(null);
      getConsignment().setConsignorTerritory(null);
      getConsignment().setConsignorTin(null);
    }
  }

  public List<Consignment> consignmentInfo(MainView main) {
    try {
      if (getConsignment().getCompanyId() != null && getConsignment().getCustomerId() != null) {
        return ConsignmentService.selectSalesReturnConsignment(main, getConsignment().getCompanyId(), getConsignment().getCustomerId());
      } else if (UserRuntimeView.instance().getCompany() != null && getConsignment().getCustomerId() != null) {
        return ConsignmentService.selectSalesReturnConsignment(main, UserRuntimeView.instance().getCompany(), getConsignment().getCustomerId());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public boolean consignmentIdExist() {
    return getConsignment() != null && getConsignment().getId() != null;
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

  public void consignmentPopupClose() {
    Jsf.popupReturn(getConsignment(), getConsignment());
  }

  public List<AccountGroup> lookupCustomerAccountGroup() {
    List<AccountGroup> list = null;
    if (getConsignment().getCustomerId() != null) {
      list = ScmLookupExtView.lookupAccountGroupByCustomer(getConsignment().getCustomerId());
    }

    if (list != null && !list.isEmpty() && getConsignment() != null && getConsignment().getAccountGroupId() == null) {
      getConsignment().setAccountGroupId(list.get(0));
    }
    return list;
  }

  public void consignmentSelectEvent(SelectEvent event) {
    Consignment cons = (Consignment) event.getObject();
    if (cons != null) {
      getConsignment().setAccountGroupId(cons.getAccountGroupId());
    } else {
      getConsignment().setAccountGroupId(null);
    }
  }
}
