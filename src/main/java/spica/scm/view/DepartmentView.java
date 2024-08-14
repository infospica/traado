/*
 * @(#)DepartmentView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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
import javax.servlet.http.Part;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypeAction;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.app.faces.JsfIo;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.Department;
import spica.scm.domain.SystemContext;
import spica.scm.service.DepartmentService;
import wawo.app.faces.Jsf;

/**
 * DepartmentView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "departmentView")
@ViewScoped
public class DepartmentView implements Serializable {
  
  private transient Department department;	//Domain object/selected Domain.
  private transient LazyDataModel<Department> departmentLazyModel; 	//For lazy loading datatable.
  private transient Department[] departmentSelected;	 //Selected Domain Array

  @PostConstruct
  public void init() {
    Integer departmentId = (Integer) Jsf.popupParentValue(Integer.class);
    getDepartment().setId(departmentId);
  }

  /**
   * Default Constructor.
   */
  public DepartmentView() {
    super();
  }

  /**
   * Return Department.
   *
   * @return Department.
   */
  public Department getDepartment() {
    if (department == null) {
      department = new Department();
    }
    return department;
  }

  /**
   * Set Department.
   *
   * @param department.
   */
  public void setDepartment(Department department) {
    this.department = department;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchDepartment(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getDepartment().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setDepartment((Department) DepartmentService.selectByPk(main, getDepartment()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadDepartmentList(main);
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
   * Create departmentLazyModel.
   *
   * @param main
   */
  private void loadDepartmentList(final MainView main) {
    if (departmentLazyModel == null) {
      departmentLazyModel = new LazyDataModel<Department>() {
        private List<Department> list;
        
        @Override
        public List<Department> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = DepartmentService.listPaged(main);
            main.commit(departmentLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }
        
        @Override
        public Object getRowKey(Department department) {
          return department.getId();
        }
        
        @Override
        public Department getRowData(String rowKey) {
          if (list != null) {
            for (Department obj : list) {
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
    String SUB_FOLDER = "scm_department/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveDepartment(MainView main) {
    return saveOrCloneDepartment(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneDepartment(MainView main) {
    main.setViewType("newform");
    return saveOrCloneDepartment(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneDepartment(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            DepartmentService.insertOrUpdate(main, getDepartment());
            break;
          case "clone":
            DepartmentService.clone(main, getDepartment());
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
   * Delete one or many Department.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteDepartment(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(departmentSelected)) {
        DepartmentService.deleteByPkArray(main, getDepartmentSelected()); //many record delete from list
        main.commit("success.delete");
        departmentSelected = null;
      } else {
        DepartmentService.deleteByPk(main, getDepartment());  //individual record delete from list or edit form
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
   * Return LazyDataModel of Department.
   *
   * @return
   */
  public LazyDataModel<Department> getDepartmentLazyModel() {
    return departmentLazyModel;
  }

  /**
   * Return Department[].
   *
   * @return
   */
  public Department[] getDepartmentSelected() {
    return departmentSelected;
  }

  /**
   * Set Department[].
   *
   * @param departmentSelected
   */
  public void setDepartmentSelected(Department[] departmentSelected) {
    this.departmentSelected = departmentSelected;
  }
  
  public List<SystemContext> systemContextAuto(String filter) {
    return ScmLookupView.systemContextAuto(filter);
  }
  
}
