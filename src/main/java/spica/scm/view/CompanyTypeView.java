/*
 * @(#)CompanyTypeView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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

import spica.scm.domain.CompanyType;
import spica.scm.service.CompanyTypeService;

/**
 * CompanyTypeView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "companyTypeView")
@ViewScoped
public class CompanyTypeView implements Serializable {

  private transient CompanyType companyType;	//Domain object/selected Domain.
  private transient LazyDataModel<CompanyType> companyTypeLazyModel; 	//For lazy loading datatable.
  private transient CompanyType[] companyTypeSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public CompanyTypeView() {
    super();
  }

  /**
   * Return CompanyType.
   *
   * @return CompanyType.
   */
  public CompanyType getCompanyType() {
    if (companyType == null) {
      companyType = new CompanyType();
    }
    return companyType;
  }

  /**
   * Set CompanyType.
   *
   * @param companyType.
   */
  public void setCompanyType(CompanyType companyType) {
    this.companyType = companyType;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCompanyType(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getCompanyType().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCompanyType((CompanyType) CompanyTypeService.selectByPk(main, getCompanyType()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCompanyTypeList(main);
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
   * Create companyTypeLazyModel.
   *
   * @param main
   */
  private void loadCompanyTypeList(final MainView main) {
    if (companyTypeLazyModel == null) {
      companyTypeLazyModel = new LazyDataModel<CompanyType>() {
        private List<CompanyType> list;

        @Override
        public List<CompanyType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CompanyTypeService.listPaged(main);
            main.commit(companyTypeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CompanyType companyType) {
          return companyType.getId();
        }

        @Override
        public CompanyType getRowData(String rowKey) {
          if (list != null) {
            for (CompanyType obj : list) {
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
    String SUB_FOLDER = "scm_company_type/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCompanyType(MainView main) {
    return saveOrCloneCompanyType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCompanyType(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCompanyType(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCompanyType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CompanyTypeService.insertOrUpdate(main, getCompanyType());
            break;
          case "clone":
            CompanyTypeService.clone(main, getCompanyType());
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
   * Delete one or many CompanyType.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCompanyType(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(companyTypeSelected)) {
        CompanyTypeService.deleteByPkArray(main, getCompanyTypeSelected()); //many record delete from list
        main.commit("success.delete");
        companyTypeSelected = null;
      } else {
        CompanyTypeService.deleteByPk(main, getCompanyType());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CompanyType.
   *
   * @return
   */
  public LazyDataModel<CompanyType> getCompanyTypeLazyModel() {
    return companyTypeLazyModel;
  }

  /**
   * Return CompanyType[].
   *
   * @return
   */
  public CompanyType[] getCompanyTypeSelected() {
    return companyTypeSelected;
  }

  /**
   * Set CompanyType[].
   *
   * @param companyTypeSelected
   */
  public void setCompanyTypeSelected(CompanyType[] companyTypeSelected) {
    this.companyTypeSelected = companyTypeSelected;
  }

}
