/*
 * @(#)CompanyBranchView.java	1.0 Fri Jun 10 11:11:45 IST 2016
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
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.CompanyBranch;
import spica.scm.service.CompanyBranchService;
import spica.scm.domain.Company;
import spica.scm.domain.Country;
import spica.scm.domain.State;
import spica.scm.domain.District;
import spica.scm.domain.Status;
import spica.scm.domain.Territory;
import wawo.app.faces.Jsf;

/**
 * CompanyBranchView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jun 10 11:11:45 IST 2016
 */
@Named(value = "companyBranchView")
@ViewScoped
public class CompanyBranchView implements Serializable {

  private transient CompanyBranch companyBranch;	//Domain object/selected Domain.
  private transient LazyDataModel<CompanyBranch> companyBranchLazyModel; 	//For lazy loading datatable.
  private transient CompanyBranch[] companyBranchSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public CompanyBranchView() {
    super();
  }

  /**
   * Return CompanyBranch.
   *
   * @return CompanyBranch.
   */
  public CompanyBranch getCompanyBranch() {
    if (companyBranch == null) {
      companyBranch = new CompanyBranch();
    }
    return companyBranch;
  }

  /**
   * Set CompanyBranch.
   *
   * @param companyBranch.
   */
  public void setCompanyBranch(CompanyBranch companyBranch) {
    this.companyBranch = companyBranch;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCompanyBranch(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getCompanyBranch().reset();
          getCompanyBranch().setCompanyId(parent);
          getCompanyBranch().setCountryId(getCompanyBranch().getCompanyId().getCountryId());
          getCompanyBranch().setStateId(getCompanyBranch().getCompanyId().getStateId());
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCompanyBranch((CompanyBranch) CompanyBranchService.selectByPk(main, getCompanyBranch()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCompanyBranchList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }
  private Company parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Company) Jsf.popupParentValue(Company.class);
    getCompanyBranch().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Create companyBranchLazyModel.
   *
   * @param main
   */
  private void loadCompanyBranchList(final MainView main) {
    if (companyBranchLazyModel == null) {
      companyBranchLazyModel = new LazyDataModel<CompanyBranch>() {
        private List<CompanyBranch> list;

        @Override
        public List<CompanyBranch> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CompanyBranchService.listPaged(main);
            main.commit(companyBranchLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CompanyBranch companyBranch) {
          return companyBranch.getId();
        }

        @Override
        public CompanyBranch getRowData(String rowKey) {
          if (list != null) {
            for (CompanyBranch obj : list) {
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
    String SUB_FOLDER = "scm_company_branch/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCompanyBranch(MainView main) {
    return saveOrCloneCompanyBranch(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCompanyBranch(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCompanyBranch(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCompanyBranch(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CompanyBranchService.insertOrUpdate(main, getCompanyBranch());
            break;
          case "clone":
            CompanyBranchService.clone(main, getCompanyBranch());
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
   * Delete one or many CompanyBranch.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCompanyBranch(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(companyBranchSelected)) {
        CompanyBranchService.deleteByPkArray(main, getCompanyBranchSelected()); //many record delete from list
        main.commit("success.delete");
        companyBranchSelected = null;
      } else {
        CompanyBranchService.deleteByPk(main, getCompanyBranch());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CompanyBranch.
   *
   * @return
   */
  public LazyDataModel<CompanyBranch> getCompanyBranchLazyModel() {
    return companyBranchLazyModel;
  }

  /**
   * Return CompanyBranch[].
   *
   * @return
   */
  public CompanyBranch[] getCompanyBranchSelected() {
    return companyBranchSelected;
  }

  /**
   * Set CompanyBranch[].
   *
   * @param companyBranchSelected
   */
  public void setCompanyBranchSelected(CompanyBranch[] companyBranchSelected) {
    this.companyBranchSelected = companyBranchSelected;
  }

  /**
   * Company autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.companyAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.companyAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Company> companyAuto(String filter) {
    return ScmLookupView.companyAuto(filter);
  }

  /**
   * Country autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.countryAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.countryAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Country> countryAuto(String filter) {
    return ScmLookupView.countryAuto(filter);
  }

  /**
   * State autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.stateAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.stateAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<State> stateAuto(String filter) {
    if (getCompanyBranch().getCountryId() != null && getCompanyBranch().getCountryId().getId() != null) {
      return ScmLookupExtView.addressStateAuto(getCompanyBranch().getCountryId().getId(), filter);
    }
    return null;
  }

  /**
   * District autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.districtAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.districtAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<District> districtAuto(String filter) {
    if (getCompanyBranch().getStateId() != null && getCompanyBranch().getStateId().getId() != null) {
      return ScmLookupExtView.addressDistrictAuto(getCompanyBranch().getStateId().getId(), filter);
    }
    return null;
  }

  /**
   * Territory autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.territoryAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.territoryAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Territory> territoryAuto(String filter) {
    if (getCompanyBranch().getDistrictId() != null && getCompanyBranch().getDistrictId().getId() != null) {
      return ScmLookupExtView.addressTerritoryAuto(getCompanyBranch().getCompanyId().getId(), getCompanyBranch().getDistrictId().getId(), filter);
    }
    return null;
  }

  public void companyBranchPopupClose() {
    Jsf.popupReturn(parent, null);
  }

  public void clearStateDistrict() {
    getCompanyBranch().setStateId(null);
    getCompanyBranch().setDistrictId(null);
    getCompanyBranch().setTerritoryId(null);
  }

  public void clearDistrict() {
    getCompanyBranch().setDistrictId(null);
    getCompanyBranch().setTerritoryId(null);
  }

  public void clearTerritory() {
    getCompanyBranch().setTerritoryId(null);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }
}
