/*
 * @(#)SalesAgentCommisionClaimView.java	1.0 Mon Jun 05 13:13:48 IST 2017 
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
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.SalesAgentCommisionClaim;
import spica.scm.service.SalesAgentCommisionClaimService;
import spica.scm.domain.UserProfile;
import spica.scm.service.UserProfileService;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * SalesAgentCommisionClaimView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Jun 05 13:13:48 IST 2017
 */
@Named(value = "salesAgentCommisionClaimView")
@ViewScoped
public class SalesAgentCommisionClaimView implements Serializable {

  private transient SalesAgentCommisionClaim salesAgentCommisionClaim;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesAgentCommisionClaim> salesAgentCommisionClaimLazyModel; 	//For lazy loading datatable.
  private transient SalesAgentCommisionClaim[] salesAgentCommisionClaimSelected;	 //Selected Domain Array
  private transient List<UserProfile> userProfileList;
  private int selectedYear;
  private int selectedMonth;

  /**
   * Default Constructor.
   */
  public SalesAgentCommisionClaimView() {
    super();
  }

  /**
   * Return SalesAgentCommisionClaim.
   *
   * @return SalesAgentCommisionClaim.
   */
  public SalesAgentCommisionClaim getSalesAgentCommisionClaim() {
    if (salesAgentCommisionClaim == null) {
      salesAgentCommisionClaim = new SalesAgentCommisionClaim();
    }
    return salesAgentCommisionClaim;
  }

  /**
   * Set SalesAgentCommisionClaim.
   *
   * @param salesAgentCommisionClaim.
   */
  public void setSalesAgentCommisionClaim(SalesAgentCommisionClaim salesAgentCommisionClaim) {
    this.salesAgentCommisionClaim = salesAgentCommisionClaim;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesAgentCommisionClaim(MainView main, String viewType) {
    //this.main = main;
    selectedMonth = UserRuntimeView.instance().getCurrentMonth();
    selectedYear = UserRuntimeView.instance().getCurrentYear();
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getSalesAgentCommisionClaim().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setSalesAgentCommisionClaim((SalesAgentCommisionClaim) SalesAgentCommisionClaimService.selectByPk(main, getSalesAgentCommisionClaim()));
        } else if (ViewTypes.isList(viewType)) {
          loadSalesAgentCommisionClaimList(main);
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
   * Create salesAgentCommisionClaimLazyModel.
   *
   * @param main
   */
  private void loadSalesAgentCommisionClaimList(final MainView main) {
    if (salesAgentCommisionClaimLazyModel == null) {
      salesAgentCommisionClaimLazyModel = new LazyDataModel<SalesAgentCommisionClaim>() {
        private List<SalesAgentCommisionClaim> list;

        @Override
        public List<SalesAgentCommisionClaim> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesAgentCommisionClaimService.listPaged(main);
            main.commit(salesAgentCommisionClaimLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesAgentCommisionClaim salesAgentCommisionClaim) {
          return salesAgentCommisionClaim.getId();
        }

        @Override
        public SalesAgentCommisionClaim getRowData(String rowKey) {
          if (list != null) {
            for (SalesAgentCommisionClaim obj : list) {
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
    String SUB_FOLDER = "scm_sales_agent_commision_claim/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesAgentCommisionClaim(MainView main) {
    return saveOrCloneSalesAgentCommisionClaim(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesAgentCommisionClaim(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesAgentCommisionClaim(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesAgentCommisionClaim(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesAgentCommisionClaimService.insertOrUpdate(main, getSalesAgentCommisionClaim());
            break;
          case "clone":
            SalesAgentCommisionClaimService.clone(main, getSalesAgentCommisionClaim());
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
   * Delete one or many SalesAgentCommisionClaim.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesAgentCommisionClaim(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(salesAgentCommisionClaimSelected)) {
        SalesAgentCommisionClaimService.deleteByPkArray(main, getSalesAgentCommisionClaimSelected()); //many record delete from list
        main.commit("success.delete");
        salesAgentCommisionClaimSelected = null;
      } else {
        SalesAgentCommisionClaimService.deleteByPk(main, getSalesAgentCommisionClaim());  //individual record delete from list or edit form
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

  public void yearFilter(SelectEvent event) {
    selectedYear = (int) event.getObject();
  }

  public void monthFilter(SelectEvent event) {
    selectedMonth = (int) event.getObject();
  }

  /**
   * Return LazyDataModel of SalesAgentCommisionClaim.
   *
   * @return
   */
  public LazyDataModel<SalesAgentCommisionClaim> getSalesAgentCommisionClaimLazyModel() {
    return salesAgentCommisionClaimLazyModel;
  }

  /**
   * Return SalesAgentCommisionClaim[].
   *
   * @return
   */
  public SalesAgentCommisionClaim[] getSalesAgentCommisionClaimSelected() {
    return salesAgentCommisionClaimSelected;
  }

  /**
   * Set SalesAgentCommisionClaim[].
   *
   * @param salesAgentCommisionClaimSelected
   */
  public void setSalesAgentCommisionClaimSelected(SalesAgentCommisionClaim[] salesAgentCommisionClaimSelected) {
    this.salesAgentCommisionClaimSelected = salesAgentCommisionClaimSelected;
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
//  public List<UserProfile> scmUserProfileAuto(String filter) {
//    return ScmLookupView.userProfileAuto(filter);
//  }
  public List<UserProfile> scmUserProfileAuto(String filter) {
    return ScmLookupExtView.userProfileAuto(filter, UserRuntimeView.instance().getCompany().getId());
  }

  public List<UserProfile> getUserProfileList(MainView main) {
    userProfileList = UserProfileService.listPagedBySalesAgent(main, UserRuntimeView.instance().getCompany());
    return userProfileList;
  }

  public String newClaim(MainView main, UserProfile userProfile) {
    Jsf.popupForm(SalesAgentCommisionClaimService.COMMISION_POPUP, null, userProfile.getId());
    return null;
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
}
