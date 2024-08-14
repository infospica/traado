/*
 * @(#)SalesAgentCommisionView.java	1.0 Mon Jun 05 13:13:48 IST 2017 
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

import spica.scm.domain.SalesAgentCommision;
import spica.scm.service.SalesAgentCommisionService;
import spica.scm.domain.UserProfile;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.Contract;
import spica.scm.service.UserProfileService;
import spica.sys.UserRuntimeView;

/**
 * SalesAgentCommisionView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Jun 05 13:13:48 IST 2017
 */
@Named(value = "salesAgentCommisionView")
@ViewScoped
public class SalesAgentCommisionView implements Serializable {

  private transient SalesAgentCommision salesAgentCommision;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesAgentCommision> salesAgentCommisionLazyModel; 	//For lazy loading datatable.
  private transient SalesAgentCommision[] salesAgentCommisionSelected;	 //Selected Domain Array
  private transient List<UserProfile> userProfileList;
  private int selectedYear;
  private int selectedMonth;
  private UserProfile userProfile;
  private Double total;

  /**
   * Default Constructor.
   */
  public SalesAgentCommisionView() {
    super();
  }

  /**
   * Return SalesAgentCommision.
   *
   * @return SalesAgentCommision.
   */
  public SalesAgentCommision getSalesAgentCommision() {
    if (salesAgentCommision == null) {
      salesAgentCommision = new SalesAgentCommision();
    }
    return salesAgentCommision;
  }

  /**
   * Set SalesAgentCommision.
   *
   * @param salesAgentCommision.
   */
  public void setSalesAgentCommision(SalesAgentCommision salesAgentCommision) {
    this.salesAgentCommision = salesAgentCommision;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesAgentCommision(MainView main, String viewType) {
    //this.main = main;
    selectedMonth = UserRuntimeView.instance().getCurrentMonth();
    selectedYear = UserRuntimeView.instance().getCurrentYear();

    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getSalesAgentCommision().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setSalesAgentCommision((SalesAgentCommision) SalesAgentCommisionService.selectByPk(main, getSalesAgentCommision()));
          // setSalesAgentCommision((SalesAgentCommision) SalesAgentCommisionService.selectByUserProfile(main, userProfile, selectedMonth, selectedYear));
        } else if (ViewTypes.isList(viewType)) {
          loadSalesAgentCommisionList(main);
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
   * Create salesAgentCommisionLazyModel.
   *
   * @param main
   */
  private void loadSalesAgentCommisionList(final MainView main) {
    if (salesAgentCommisionLazyModel == null) {
      salesAgentCommisionLazyModel = new LazyDataModel<SalesAgentCommision>() {
        private List<SalesAgentCommision> list;

        @Override
        public List<SalesAgentCommision> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesAgentCommisionService.listPaged(main, userProfile, selectedMonth, selectedYear);
            total = 0.0;
            for (SalesAgentCommision salesAgentCommision : list) {
              salesAgentCommision.setPayableValue(salesAgentCommision.getCommisionProvisionedValue());
              salesAgentCommision.setPayablePercent(salesAgentCommision.getCommisionProvisionedPercent());
              total += salesAgentCommision.getCommisionProvisionedValue();
            }
            main.commit(salesAgentCommisionLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesAgentCommision salesAgentCommision) {
          return salesAgentCommision.getId();
        }

        @Override
        public SalesAgentCommision getRowData(String rowKey) {
          if (list != null) {
            for (SalesAgentCommision obj : list) {
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
    String SUB_FOLDER = "scm_sales_agent_commision/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesAgentCommision(MainView main) {
    return saveOrCloneSalesAgentCommision(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesAgentCommision(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesAgentCommision(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesAgentCommision(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesAgentCommisionService.insertOrUpdate(main, getSalesAgentCommision());
            break;
          case "clone":
            SalesAgentCommisionService.clone(main, getSalesAgentCommision());
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
   * Delete one or many SalesAgentCommision.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesAgentCommision(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(salesAgentCommisionSelected)) {
        SalesAgentCommisionService.deleteByPkArray(main, getSalesAgentCommisionSelected()); //many record delete from list
        main.commit("success.delete");
        salesAgentCommisionSelected = null;
      } else {
        SalesAgentCommisionService.deleteByPk(main, getSalesAgentCommision());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (ViewTypes.isEdit(main.getViewType())) {
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

  public void calculatePayableValue(SalesAgentCommision salesAgentCommision) {
    salesAgentCommision.setPayableValue(salesAgentCommision.getPayablePercent() * salesAgentCommision.getSalesInvoiceId().getInvoiceAmount() / 100);
  }

  public void calculatePayablePercent(SalesAgentCommision salesAgentCommision) {
    salesAgentCommision.setPayablePercent(salesAgentCommision.getPayableValue() / salesAgentCommision.getSalesInvoiceId().getInvoiceAmount() * 100);
  }

  /**
   * Return LazyDataModel of SalesAgentCommision.
   *
   * @return
   */
  public LazyDataModel<SalesAgentCommision> getSalesAgentCommisionLazyModel() {
    return salesAgentCommisionLazyModel;
  }

  /**
   * Return SalesAgentCommision[].
   *
   * @return
   */
  public SalesAgentCommision[] getSalesAgentCommisionSelected() {
    return salesAgentCommisionSelected;
  }

  /**
   * Set SalesAgentCommision[].
   *
   * @param salesAgentCommisionSelected
   */
  public void setSalesAgentCommisionSelected(SalesAgentCommision[] salesAgentCommisionSelected) {
    this.salesAgentCommisionSelected = salesAgentCommisionSelected;
  }

  /**
   * ScmUserProfile autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.scmUserProfileAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.scmUserProfileAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<UserProfile> scmUserProfileAuto(String filter) {
    return ScmLookupView.userProfileAuto(filter);
  }

  /**
   * ScmSalesInvoice autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.scmSalesInvoiceAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.scmSalesInvoiceAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<SalesInvoice> scmSalesInvoiceAuto(String filter) {
    return ScmLookupView.salesInvoiceAuto(filter);
  }

  /**
   * ScmContract autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.scmContractAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.scmContractAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Contract> scmContractAuto(String filter) {
    return ScmLookupView.contractAuto(filter);
  }

  public List<UserProfile> getUserProfileList(MainView main) {
    userProfileList = UserProfileService.listPagedBySalesAgent(main, UserRuntimeView.instance().getCompany());
    userProfile = userProfileList.get(0);
    return userProfileList;
  }

  public void setUserProfileList(List<UserProfile> userProfileList) {
    this.userProfileList = userProfileList;
  }

  public int getSelectedYear() {
    return selectedYear;
  }

  public void setSelectedYear(int selectedYear) {
    this.selectedYear = selectedYear;
  }

  public int getSelectedMonth() {
    return selectedMonth;
  }

  public void setSelectedMonth(int selectedMonth) {
    this.selectedMonth = selectedMonth;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

}
