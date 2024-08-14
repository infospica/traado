/*
 * @(#)CustomerPricelistView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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

import spica.scm.domain.CustomerPricelist;
import spica.scm.service.CustomerPricelistService;
import spica.scm.domain.Customer;
import spica.scm.domain.Status;

/**
 * CustomerPricelistView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "customerPricelistView")
@ViewScoped
public class CustomerPricelistView implements Serializable {

  private transient CustomerPricelist customerPricelist;	//Domain object/selected Domain.
  private transient LazyDataModel<CustomerPricelist> customerPricelistLazyModel; 	//For lazy loading datatable.
  private transient CustomerPricelist[] customerPricelistSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public CustomerPricelistView() {
    super();
  }

  /**
   * Return CustomerPricelist.
   *
   * @return CustomerPricelist.
   */
  public CustomerPricelist getCustomerPricelist() {
    if (customerPricelist == null) {
      customerPricelist = new CustomerPricelist();
    }
    return customerPricelist;
  }

  /**
   * Set CustomerPricelist.
   *
   * @param customerPricelist.
   */
  public void setCustomerPricelist(CustomerPricelist customerPricelist) {
    this.customerPricelist = customerPricelist;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCustomerPricelist(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getCustomerPricelist().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setCustomerPricelist((CustomerPricelist) CustomerPricelistService.selectByPk(main, getCustomerPricelist()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCustomerPricelistList(main);
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
   * Create customerPricelistLazyModel.
   *
   * @param main
   */
  private void loadCustomerPricelistList(final MainView main) {
    if (customerPricelistLazyModel == null) {
      customerPricelistLazyModel = new LazyDataModel<CustomerPricelist>() {
        private List<CustomerPricelist> list;

        @Override
        public List<CustomerPricelist> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CustomerPricelistService.listPaged(main);
            main.commit(customerPricelistLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CustomerPricelist customerPricelist) {
          return customerPricelist.getId();
        }

        @Override
        public CustomerPricelist getRowData(String rowKey) {
          if (list != null) {
            for (CustomerPricelist obj : list) {
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
    String SUB_FOLDER = "scm_customer_pricelist/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCustomerPricelist(MainView main) {
    return saveOrCloneCustomerPricelist(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCustomerPricelist(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCustomerPricelist(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCustomerPricelist(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CustomerPricelistService.insertOrUpdate(main, getCustomerPricelist());
            break;
          case "clone":
            CustomerPricelistService.clone(main, getCustomerPricelist());
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
   * Delete one or many CustomerPricelist.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCustomerPricelist(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(customerPricelistSelected)) {
        CustomerPricelistService.deleteByPkArray(main, getCustomerPricelistSelected()); //many record delete from list
        main.commit("success.delete");
        customerPricelistSelected = null;
      } else {
        CustomerPricelistService.deleteByPk(main, getCustomerPricelist());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CustomerPricelist.
   *
   * @return
   */
  public LazyDataModel<CustomerPricelist> getCustomerPricelistLazyModel() {
    return customerPricelistLazyModel;
  }

  /**
   * Return CustomerPricelist[].
   *
   * @return
   */
  public CustomerPricelist[] getCustomerPricelistSelected() {
    return customerPricelistSelected;
  }

  /**
   * Set CustomerPricelist[].
   *
   * @param customerPricelistSelected
   */
  public void setCustomerPricelistSelected(CustomerPricelist[] customerPricelistSelected) {
    this.customerPricelistSelected = customerPricelistSelected;
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
    return ScmLookupView.customerAuto(filter);
  }

  /**
   * Status autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.statusAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.statusAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }
}
