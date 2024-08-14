/*
 * @(#)SalesInvoiceItemView.java	1.0 Fri Oct 28 09:25:45 IST 2016 
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
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.SalesInvoiceItem;
import spica.scm.service.SalesInvoiceItemService;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.ProductDetail;

/**
 * SalesInvoiceItemView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Oct 28 09:25:45 IST 2016
 */
@Named(value = "salesInvoiceItemView")
@ViewScoped
public class SalesInvoiceItemView implements Serializable {

  private transient SalesInvoiceItem salesInvoiceItem;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesInvoiceItem> salesInvoiceItemLazyModel; 	//For lazy loading datatable.
  private transient SalesInvoiceItem[] salesInvoiceItemSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public SalesInvoiceItemView() {
    super();
  }

  /**
   * Return SalesInvoiceItem.
   *
   * @return SalesInvoiceItem.
   */
  public SalesInvoiceItem getSalesInvoiceItem() {
    if (salesInvoiceItem == null) {
      salesInvoiceItem = new SalesInvoiceItem();
    }
    return salesInvoiceItem;
  }

  /**
   * Set SalesInvoiceItem.
   *
   * @param salesInvoiceItem.
   */
  public void setSalesInvoiceItem(SalesInvoiceItem salesInvoiceItem) {
    this.salesInvoiceItem = salesInvoiceItem;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesInvoiceItem(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getSalesInvoiceItem().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setSalesInvoiceItem((SalesInvoiceItem) SalesInvoiceItemService.selectByPk(main, getSalesInvoiceItem()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadSalesInvoiceItemList(main);
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
   * Create salesInvoiceItemLazyModel.
   *
   * @param main
   */
  private void loadSalesInvoiceItemList(final MainView main) {
    if (salesInvoiceItemLazyModel == null) {
      salesInvoiceItemLazyModel = new LazyDataModel<SalesInvoiceItem>() {
        private List<SalesInvoiceItem> list;

        @Override
        public List<SalesInvoiceItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesInvoiceItemService.listPaged(main);
            main.commit(salesInvoiceItemLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesInvoiceItem salesInvoiceItem) {
          return salesInvoiceItem.getId();
        }

        @Override
        public SalesInvoiceItem getRowData(String rowKey) {
          if (list != null) {
            for (SalesInvoiceItem obj : list) {
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
    String SUB_FOLDER = "scm_sales_invoice_item/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesInvoiceItem(MainView main) {
    return saveOrCloneSalesInvoiceItem(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesInvoiceItem(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesInvoiceItem(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesInvoiceItem(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesInvoiceItemService.insertOrUpdate(main, getSalesInvoiceItem());
            break;
          case "clone":
            SalesInvoiceItemService.clone(main, getSalesInvoiceItem());
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
   * Delete one or many SalesInvoiceItem.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesInvoiceItem(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesInvoiceItemSelected)) {
        SalesInvoiceItemService.deleteByPkArray(main, getSalesInvoiceItemSelected()); //many record delete from list
        main.commit("success.delete");
        salesInvoiceItemSelected = null;
      } else {
        SalesInvoiceItemService.deleteByPk(main, getSalesInvoiceItem());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SalesInvoiceItem.
   *
   * @return
   */
  public LazyDataModel<SalesInvoiceItem> getSalesInvoiceItemLazyModel() {
    return salesInvoiceItemLazyModel;
  }

  /**
   * Return SalesInvoiceItem[].
   *
   * @return
   */
  public SalesInvoiceItem[] getSalesInvoiceItemSelected() {
    return salesInvoiceItemSelected;
  }

  /**
   * Set SalesInvoiceItem[].
   *
   * @param salesInvoiceItemSelected
   */
  public void setSalesInvoiceItemSelected(SalesInvoiceItem[] salesInvoiceItemSelected) {
    this.salesInvoiceItemSelected = salesInvoiceItemSelected;
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

  /**
   * ProductDetail autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.productDetailAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.productDetailAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ProductDetail> productDetailAuto(String filter) {
    return ScmLookupView.productDetailAuto(filter);
  }
}
