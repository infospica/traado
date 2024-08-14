/*
 * @(#)ConsignmentDetailView.java	1.0 Fri Jul 22 16:43:01 IST 2016 
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
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.app.faces.JsfIo;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.ConsignmentDetail;
import spica.scm.service.ConsignmentDetailService;
import spica.scm.domain.Consignment;
import spica.scm.domain.ConsignmentDocType;
import spica.scm.domain.ConsignmentReceiptType;
import spica.scm.domain.Transporter;

/**
 * ConsignmentDetailView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 16:43:01 IST 2016
 */
@Named(value = "consignmentDetailView")
@ViewScoped
public class ConsignmentDetailView implements Serializable {

  private transient ConsignmentDetail consignmentDetail;	//Domain object/selected Domain.
  private transient LazyDataModel<ConsignmentDetail> consignmentDetailLazyModel; 	//For lazy loading datatable.
  private transient ConsignmentDetail[] consignmentDetailSelected;	 //Selected Domain Array
  private transient Part exitDocumentFilePathPart;
  private transient Part exitDocument2FilePathPart;
  private transient Part entryDocumentFilePathPart;
  private transient Part receiptFilePathPart;
  private transient Part receiptReturnFilePathPart;

  /**
   * Default Constructor.
   */
  public ConsignmentDetailView() {
    super();
  }

  /**
   * Return ConsignmentDetail.
   *
   * @return ConsignmentDetail.
   */
  public ConsignmentDetail getConsignmentDetail() {
    if (consignmentDetail == null) {
      consignmentDetail = new ConsignmentDetail();
    }
    return consignmentDetail;
  }

  /**
   * Set ConsignmentDetail.
   *
   * @param consignmentDetail.
   */
  public void setConsignmentDetail(ConsignmentDetail consignmentDetail) {
    this.consignmentDetail = consignmentDetail;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchConsignmentDetail(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getConsignmentDetail().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setConsignmentDetail((ConsignmentDetail) ConsignmentDetailService.selectByPk(main, getConsignmentDetail()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadConsignmentDetailList(main);
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
   * Create consignmentDetailLazyModel.
   *
   * @param main
   */
  private void loadConsignmentDetailList(final MainView main) {
    if (consignmentDetailLazyModel == null) {
      consignmentDetailLazyModel = new LazyDataModel<ConsignmentDetail>() {
        private List<ConsignmentDetail> list;

        @Override
        public List<ConsignmentDetail> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ConsignmentDetailService.listPaged(main);
            main.commit(consignmentDetailLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ConsignmentDetail consignmentDetail) {
          return consignmentDetail.getId();
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

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveConsignmentDetail(MainView main) {
    return saveOrCloneConsignmentDetail(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneConsignmentDetail(MainView main) {
    main.setViewType("newform");
    return saveOrCloneConsignmentDetail(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneConsignmentDetail(MainView main, String key) {
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

  /**
   * Delete one or many ConsignmentDetail.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteConsignmentDetail(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(consignmentDetailSelected)) {
        ConsignmentDetailService.deleteByPkArray(main, getConsignmentDetailSelected()); //many record delete from list
        main.commit("success.delete");
        consignmentDetailSelected = null;
      } else {
        ConsignmentDetailService.deleteByPk(main, getConsignmentDetail());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ConsignmentDetail.
   *
   * @return
   */
  public LazyDataModel<ConsignmentDetail> getConsignmentDetailLazyModel() {
    return consignmentDetailLazyModel;
  }

  /**
   * Return ConsignmentDetail[].
   *
   * @return
   */
  public ConsignmentDetail[] getConsignmentDetailSelected() {
    return consignmentDetailSelected;
  }

  /**
   * Set ConsignmentDetail[].
   *
   * @param consignmentDetailSelected
   */
  public void setConsignmentDetailSelected(ConsignmentDetail[] consignmentDetailSelected) {
    this.consignmentDetailSelected = consignmentDetailSelected;
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
    return ScmLookupView.transporterAuto(filter);
  }
}
