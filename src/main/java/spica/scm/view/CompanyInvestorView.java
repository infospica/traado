/*
 * @(#)CompanyInvestorView.java	1.0 Fri Oct 21 18:55:30 IST 2016 
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

import spica.scm.domain.CompanyInvestor;
import spica.scm.service.CompanyInvestorService;
import spica.scm.domain.Company;
import spica.scm.domain.UserProfile;
import spica.scm.domain.Designation;
import spica.scm.domain.Status;
import spica.scm.service.DesignationService;

/**
 * CompanyInvestorView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Oct 21 18:55:30 IST 2016
 */
@Named(value = "companyInvestorView")
@ViewScoped
public class CompanyInvestorView implements Serializable {

  private transient CompanyInvestor companyInvestor;	//Domain object/selected Domain.
  private transient LazyDataModel<CompanyInvestor> companyInvestorLazyModel; 	//For lazy loading datatable.
  private transient CompanyInvestor[] companyInvestorSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public CompanyInvestorView() {
    super();
  }

  /**
   * Return CompanyInvestor.
   *
   * @return CompanyInvestor.
   */
  public CompanyInvestor getCompanyInvestor() {
    if (companyInvestor == null) {
      companyInvestor = new CompanyInvestor();
    }
    return companyInvestor;
  }

  /**
   * Set CompanyInvestor.
   *
   * @param companyInvestor.
   */
  public void setCompanyInvestor(CompanyInvestor companyInvestor) {
    this.companyInvestor = companyInvestor;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCompanyInvestor(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getCompanyInvestor().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCompanyInvestor((CompanyInvestor) CompanyInvestorService.selectByPk(main, getCompanyInvestor()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCompanyInvestorList(main);
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
   * Create companyInvestorLazyModel.
   *
   * @param main
   */
  private void loadCompanyInvestorList(final MainView main) {
    if (companyInvestorLazyModel == null) {
      companyInvestorLazyModel = new LazyDataModel<CompanyInvestor>() {
        private List<CompanyInvestor> list;

        @Override
        public List<CompanyInvestor> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CompanyInvestorService.listPaged(main);
            main.commit(companyInvestorLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CompanyInvestor companyInvestor) {
          return companyInvestor.getId();
        }

        @Override
        public CompanyInvestor getRowData(String rowKey) {
          if (list != null) {
            for (CompanyInvestor obj : list) {
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
    String SUB_FOLDER = "scm_company_investor/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCompanyInvestor(MainView main) {
    return saveOrCloneCompanyInvestor(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCompanyInvestor(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCompanyInvestor(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCompanyInvestor(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CompanyInvestorService.insertOrUpdate(main, getCompanyInvestor());
            break;
          case "clone":
            CompanyInvestorService.clone(main, getCompanyInvestor());
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
   * Delete one or many CompanyInvestor.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCompanyInvestor(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(companyInvestorSelected)) {
        CompanyInvestorService.deleteByPkArray(main, getCompanyInvestorSelected()); //many record delete from list
        main.commit("success.delete");
        companyInvestorSelected = null;
      } else {
        CompanyInvestorService.deleteByPk(main, getCompanyInvestor());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CompanyInvestor.
   *
   * @return
   */
  public LazyDataModel<CompanyInvestor> getCompanyInvestorLazyModel() {
    return companyInvestorLazyModel;
  }

  /**
   * Return CompanyInvestor[].
   *
   * @return
   */
  public CompanyInvestor[] getCompanyInvestorSelected() {
    return companyInvestorSelected;
  }

  /**
   * Set CompanyInvestor[].
   *
   * @param companyInvestorSelected
   */
  public void setCompanyInvestorSelected(CompanyInvestor[] companyInvestorSelected) {
    this.companyInvestorSelected = companyInvestorSelected;
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
   * UserProfile autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.userProfileAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.userProfileAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<UserProfile> userProfileAuto(String filter) {
    return ScmLookupView.userProfileAuto(filter);
  }

  /**
   * Designation autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.designationAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.designationAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Designation> designationAuto(String filter) {
    return ScmLookupView.designationAuto(filter);
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

  public List<Designation> selectDesignationByCompanyContextInvestor(MainView main) {
    return DesignationService.selectDesignationByCompanyContextInvestor(main);
  }

}
