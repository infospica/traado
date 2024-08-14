/*
 * @(#)SalesReturnReceiptView.java	1.0 Fri Nov 24 18:28:43 IST 2017 
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
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.SalesReturnReceipt;
import spica.scm.service.SalesReturnReceiptService;
import spica.scm.domain.Customer;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * SalesReturnReceiptView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Nov 24 18:28:43 IST 2017
 */
@Named(value = "salesReturnReceiptView")
@ViewScoped
public class SalesReturnReceiptView implements Serializable {

  private transient SalesReturnReceipt salesReturnReceipt;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesReturnReceipt> salesReturnReceiptLazyModel; 	//For lazy loading datatable.
  private transient SalesReturnReceipt[] salesReturnReceiptSelected;	 //Selected Domain Array  

  @PostConstruct
  public void init() {
    salesReturnReceipt = (SalesReturnReceipt) Jsf.popupParentValue(SalesReturnReceipt.class);
    //getGenre().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Default Constructor.
   */
  public SalesReturnReceiptView() {
    super();
  }

  /**
   * Return SalesReturnReceipt.
   *
   * @return SalesReturnReceipt.
   */
  public SalesReturnReceipt getSalesReturnReceipt() {
    if (salesReturnReceipt == null) {
      salesReturnReceipt = new SalesReturnReceipt();
    }
    return salesReturnReceipt;
  }

  /**
   * Set SalesReturnReceipt.
   *
   * @param salesReturnReceipt.
   */
  public void setSalesReturnReceipt(SalesReturnReceipt salesReturnReceipt) {
    this.salesReturnReceipt = salesReturnReceipt;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesReturnReceipt(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          if (UserRuntimeView.instance().getCompany() == null) {
            main.error("company.required");
            main.setViewType(ViewTypes.list);
            return null;
          }
          getSalesReturnReceipt().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setSalesReturnReceipt((SalesReturnReceipt) SalesReturnReceiptService.selectByPk(main, getSalesReturnReceipt()));
        } else if (main.isList()) {
          loadSalesReturnReceiptList(main);
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
   * Create salesReturnReceiptLazyModel.
   *
   * @param main
   */
  private void loadSalesReturnReceiptList(final MainView main) {
    if (salesReturnReceiptLazyModel == null) {
      salesReturnReceiptLazyModel = new LazyDataModel<SalesReturnReceipt>() {
        private List<SalesReturnReceipt> list;

        @Override
        public List<SalesReturnReceipt> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (UserRuntimeView.instance().getCompany() != null) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = SalesReturnReceiptService.listPaged(main);
              main.commit(salesReturnReceiptLazyModel, first, pageSize);
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
        public Object getRowKey(SalesReturnReceipt salesReturnReceipt) {
          return salesReturnReceipt.getId();
        }

        @Override
        public SalesReturnReceipt getRowData(String rowKey) {
          if (list != null) {
            for (SalesReturnReceipt obj : list) {
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
    String SUB_FOLDER = "scm_sales_return_receipt/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesReturnReceipt(MainView main) {
    return saveOrCloneSalesReturnReceipt(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesReturnReceipt(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneSalesReturnReceipt(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesReturnReceipt(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            if (main.isNew()) {
              getSalesReturnReceipt().setCompanyId(UserRuntimeView.instance().getCompany());
            }
            SalesReturnReceiptService.insertOrUpdate(main, getSalesReturnReceipt());
            break;
          case "clone":
            SalesReturnReceiptService.clone(main, getSalesReturnReceipt());
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
   * Delete one or many SalesReturnReceipt.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesReturnReceipt(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(salesReturnReceiptSelected)) {
        SalesReturnReceiptService.deleteByPkArray(main, getSalesReturnReceiptSelected()); //many record delete from list
        main.commit("success.delete");
        salesReturnReceiptSelected = null;
      } else {
        SalesReturnReceiptService.deleteByPk(main, getSalesReturnReceipt());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SalesReturnReceipt.
   *
   * @return
   */
  public LazyDataModel<SalesReturnReceipt> getSalesReturnReceiptLazyModel() {
    return salesReturnReceiptLazyModel;
  }

  /**
   * Return SalesReturnReceipt[].
   *
   * @return
   */
  public SalesReturnReceipt[] getSalesReturnReceiptSelected() {
    return salesReturnReceiptSelected;
  }

  /**
   * Set SalesReturnReceipt[].
   *
   * @param salesReturnReceiptSelected
   */
  public void setSalesReturnReceiptSelected(SalesReturnReceipt[] salesReturnReceiptSelected) {
    this.salesReturnReceiptSelected = salesReturnReceiptSelected;
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
    return ScmLookupExtView.customerByCompany(UserRuntimeView.instance().getCompany(), filter);
  }
}
