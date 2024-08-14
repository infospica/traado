/*
 * @(#)CustomerTradeProfileView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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

import spica.scm.domain.CustomerTradeProfile;
import spica.scm.service.CustomerTradeProfileService;
import spica.scm.domain.Customer;
import spica.scm.domain.TradeProfile;

/**
 * CustomerTradeProfileView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "customerTradeProfileView")
@ViewScoped
public class CustomerTradeProfileView implements Serializable {

  private transient CustomerTradeProfile customerTradeProfile;	//Domain object/selected Domain.
  private transient LazyDataModel<CustomerTradeProfile> customerTradeProfileLazyModel; 	//For lazy loading datatable.
  private transient CustomerTradeProfile[] customerTradeProfileSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public CustomerTradeProfileView() {
    super();
  }

  /**
   * Return CustomerTradeProfile.
   *
   * @return CustomerTradeProfile.
   */
  public CustomerTradeProfile getCustomerTradeProfile() {
    if (customerTradeProfile == null) {
      customerTradeProfile = new CustomerTradeProfile();
    }
    return customerTradeProfile;
  }

  /**
   * Set CustomerTradeProfile.
   *
   * @param customerTradeProfile.
   */
  public void setCustomerTradeProfile(CustomerTradeProfile customerTradeProfile) {
    this.customerTradeProfile = customerTradeProfile;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCustomerTradeProfile(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getCustomerTradeProfile().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setCustomerTradeProfile((CustomerTradeProfile) CustomerTradeProfileService.selectByPk(main, getCustomerTradeProfile()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCustomerTradeProfileList(main);
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
   * Create customerTradeProfileLazyModel.
   *
   * @param main
   */
  private void loadCustomerTradeProfileList(final MainView main) {
    if (customerTradeProfileLazyModel == null) {
      customerTradeProfileLazyModel = new LazyDataModel<CustomerTradeProfile>() {
        private List<CustomerTradeProfile> list;

        @Override
        public List<CustomerTradeProfile> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CustomerTradeProfileService.listPaged(main);
            main.commit(customerTradeProfileLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CustomerTradeProfile customerTradeProfile) {
          return customerTradeProfile.getId();
        }

        @Override
        public CustomerTradeProfile getRowData(String rowKey) {
          if (list != null) {
            for (CustomerTradeProfile obj : list) {
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
    String SUB_FOLDER = "scm_customer_trade_profile/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCustomerTradeProfile(MainView main) {
    return saveOrCloneCustomerTradeProfile(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCustomerTradeProfile(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCustomerTradeProfile(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCustomerTradeProfile(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CustomerTradeProfileService.insertOrUpdate(main, getCustomerTradeProfile());
            break;
          case "clone":
            CustomerTradeProfileService.clone(main, getCustomerTradeProfile());
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
   * Delete one or many CustomerTradeProfile.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCustomerTradeProfile(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(customerTradeProfileSelected)) {
        CustomerTradeProfileService.deleteByPkArray(main, getCustomerTradeProfileSelected()); //many record delete from list
        main.commit("success.delete");
        customerTradeProfileSelected = null;
      } else {
        CustomerTradeProfileService.deleteByPk(main, getCustomerTradeProfile());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CustomerTradeProfile.
   *
   * @return
   */
  public LazyDataModel<CustomerTradeProfile> getCustomerTradeProfileLazyModel() {
    return customerTradeProfileLazyModel;
  }

  /**
   * Return CustomerTradeProfile[].
   *
   * @return
   */
  public CustomerTradeProfile[] getCustomerTradeProfileSelected() {
    return customerTradeProfileSelected;
  }

  /**
   * Set CustomerTradeProfile[].
   *
   * @param customerTradeProfileSelected
   */
  public void setCustomerTradeProfileSelected(CustomerTradeProfile[] customerTradeProfileSelected) {
    this.customerTradeProfileSelected = customerTradeProfileSelected;
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
   * TradeProfile autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.tradeProfileAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.tradeProfileAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<TradeProfile> tradeProfileAuto(String filter) {
    return ScmLookupView.tradeProfileAuto(filter);
  }
}
