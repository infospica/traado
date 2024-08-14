/*
 * @(#)SalesAgentCommissionReceiptDetailView.java	1.0 Mon Dec 18 16:22:07 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.SalesAgentCommissionReceiptDetail;
import spica.scm.service.SalesAgentCommissionReceiptDetailService;
import spica.scm.domain.SalesAgentCommissionReceipt;
import spica.scm.domain.SalesInvoice;

/**
 * SalesAgentCommissionReceiptDetailView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Dec 18 16:22:07 IST 2017
 */
@Named(value = "salesAgentCommissionReceiptDetailView")
@ViewScoped
public class SalesAgentCommissionReceiptDetailView implements Serializable {

  private transient SalesAgentCommissionReceiptDetail salesAgentCommissionReceiptDetail;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesAgentCommissionReceiptDetail> salesAgentCommissionReceiptDetailLazyModel; 	//For lazy loading datatable.
  private transient SalesAgentCommissionReceiptDetail[] salesAgentCommissionReceiptDetailSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public SalesAgentCommissionReceiptDetailView() {
    super();
  }

  /**
   * Return SalesAgentCommissionReceiptDetail.
   *
   * @return SalesAgentCommissionReceiptDetail.
   */
  public SalesAgentCommissionReceiptDetail getSalesAgentCommissionReceiptDetail() {
    if (salesAgentCommissionReceiptDetail == null) {
      salesAgentCommissionReceiptDetail = new SalesAgentCommissionReceiptDetail();
    }
    return salesAgentCommissionReceiptDetail;
  }

  /**
   * Set SalesAgentCommissionReceiptDetail.
   *
   * @param salesAgentCommissionReceiptDetail.
   */
  public void setSalesAgentCommissionReceiptDetail(SalesAgentCommissionReceiptDetail salesAgentCommissionReceiptDetail) {
    this.salesAgentCommissionReceiptDetail = salesAgentCommissionReceiptDetail;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesAgentCommissionReceiptDetail(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getSalesAgentCommissionReceiptDetail().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setSalesAgentCommissionReceiptDetail((SalesAgentCommissionReceiptDetail) SalesAgentCommissionReceiptDetailService.selectByPk(main, getSalesAgentCommissionReceiptDetail()));
        } else if (main.isList()) {
          loadSalesAgentCommissionReceiptDetailList(main);
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
   * Create salesAgentCommissionReceiptDetailLazyModel.
   *
   * @param main
   */
  private void loadSalesAgentCommissionReceiptDetailList(final MainView main) {
    if (salesAgentCommissionReceiptDetailLazyModel == null) {
      salesAgentCommissionReceiptDetailLazyModel = new LazyDataModel<SalesAgentCommissionReceiptDetail>() {
        private List<SalesAgentCommissionReceiptDetail> list;

        @Override
        public List<SalesAgentCommissionReceiptDetail> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesAgentCommissionReceiptDetailService.listPaged(main);
            main.commit(salesAgentCommissionReceiptDetailLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesAgentCommissionReceiptDetail salesAgentCommissionReceiptDetail) {
          return salesAgentCommissionReceiptDetail.getId();
        }

        @Override
        public SalesAgentCommissionReceiptDetail getRowData(String rowKey) {
          if (list != null) {
            for (SalesAgentCommissionReceiptDetail obj : list) {
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
    String SUB_FOLDER = "scm_sales_agent_commission_receipt_detail/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesAgentCommissionReceiptDetail(MainView main) {
    return saveOrCloneSalesAgentCommissionReceiptDetail(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesAgentCommissionReceiptDetail(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneSalesAgentCommissionReceiptDetail(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesAgentCommissionReceiptDetail(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesAgentCommissionReceiptDetailService.insertOrUpdate(main, getSalesAgentCommissionReceiptDetail());
            break;
          case "clone":
            SalesAgentCommissionReceiptDetailService.clone(main, getSalesAgentCommissionReceiptDetail());
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
   * Delete one or many SalesAgentCommissionReceiptDetail.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesAgentCommissionReceiptDetail(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(salesAgentCommissionReceiptDetailSelected)) {
        SalesAgentCommissionReceiptDetailService.deleteByPkArray(main, getSalesAgentCommissionReceiptDetailSelected()); //many record delete from list
        main.commit("success.delete");
        salesAgentCommissionReceiptDetailSelected = null;
      } else {
        SalesAgentCommissionReceiptDetailService.deleteByPk(main, getSalesAgentCommissionReceiptDetail());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (main.isEdit()) {
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
   * Return LazyDataModel of SalesAgentCommissionReceiptDetail.
   *
   * @return
   */
  public LazyDataModel<SalesAgentCommissionReceiptDetail> getSalesAgentCommissionReceiptDetailLazyModel() {
    return salesAgentCommissionReceiptDetailLazyModel;
  }

  /**
   * Return SalesAgentCommissionReceiptDetail[].
   *
   * @return
   */
  public SalesAgentCommissionReceiptDetail[] getSalesAgentCommissionReceiptDetailSelected() {
    return salesAgentCommissionReceiptDetailSelected;
  }

  /**
   * Set SalesAgentCommissionReceiptDetail[].
   *
   * @param salesAgentCommissionReceiptDetailSelected
   */
  public void setSalesAgentCommissionReceiptDetailSelected(SalesAgentCommissionReceiptDetail[] salesAgentCommissionReceiptDetailSelected) {
    this.salesAgentCommissionReceiptDetailSelected = salesAgentCommissionReceiptDetailSelected;
  }

  /**
   * SalesAgentCommissionReceipt autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.salesAgentCommissionReceiptAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.salesAgentCommissionReceiptAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<SalesAgentCommissionReceipt> salesAgentCommissionReceiptAuto(String filter) {
    return ScmLookupView.salesAgentCommissionReceiptAuto(filter);
  }

  /**
   * SalesInvoice autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.salesInvoiceAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.salesInvoiceAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<SalesInvoice> salesInvoiceAuto(String filter) {
    return ScmLookupView.salesInvoiceAuto(filter);
  }
}
