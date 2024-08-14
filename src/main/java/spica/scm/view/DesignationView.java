/*
 * @(#)DesignationView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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

import spica.scm.domain.Designation;
import spica.scm.service.DesignationService;
import spica.scm.domain.Department;
import wawo.app.faces.Jsf;

/**
 * DesignationView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "designationView")
@ViewScoped
public class DesignationView implements Serializable {
  
  private transient Designation designation;	//Domain object/selected Domain.
  private transient LazyDataModel<Designation> designationLazyModel; 	//For lazy loading datatable.
  private transient Designation[] designationSelected;	 //Selected Domain Array
  
  @PostConstruct
  public void init() {
    Integer designationId = (Integer) Jsf.popupParentValue(Integer.class);
    getDesignation().setId(designationId);
  }

  /**
   * Default Constructor.
   */  
  public DesignationView() {
    super();
  }

  /**
   * Return Designation.
   *
   * @return Designation.
   */  
  public Designation getDesignation() {
    if (designation == null) {
      designation = new Designation();
    }
    return designation;
  }

  /**
   * Set Designation.
   *
   * @param designation.
   */  
  public void setDesignation(Designation designation) {
    this.designation = designation;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchDesignation(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getDesignation().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setDesignation((Designation) DesignationService.selectByPk(main, getDesignation()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadDesignationList(main);
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
   * Create designationLazyModel.
   *
   * @param main
   */
  private void loadDesignationList(final MainView main) {
    if (designationLazyModel == null) {
      designationLazyModel = new LazyDataModel<Designation>() {
        private List<Designation> list;        
        
        @Override
        public List<Designation> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = DesignationService.listPaged(main);
            main.commit(designationLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }
        
        @Override
        public Object getRowKey(Designation designation) {
          return designation.getId();
        }
        
        @Override
        public Designation getRowData(String rowKey) {
          if (list != null) {
            for (Designation obj : list) {
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
    String SUB_FOLDER = "scm_designation/";    
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveDesignation(MainView main) {
    return saveOrCloneDesignation(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneDesignation(MainView main) {
    main.setViewType("newform");
    return saveOrCloneDesignation(main, "clone");    
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneDesignation(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            DesignationService.insertOrUpdate(main, getDesignation());
            break;
          case "clone":
            DesignationService.clone(main, getDesignation());
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
   * Delete one or many Designation.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteDesignation(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(designationSelected)) {
        DesignationService.deleteByPkArray(main, getDesignationSelected()); //many record delete from list
        main.commit("success.delete");
        designationSelected = null;
      } else {
        DesignationService.deleteByPk(main, getDesignation());  //individual record delete from list or edit form
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
   * Return LazyDataModel of Designation.
   *
   * @return
   */
  public LazyDataModel<Designation> getDesignationLazyModel() {
    return designationLazyModel;
  }

  /**
   * Return Designation[].
   *
   * @return
   */
  public Designation[] getDesignationSelected() {
    return designationSelected;
  }

  /**
   * Set Designation[].
   *
   * @param designationSelected
   */
  public void setDesignationSelected(Designation[] designationSelected) {
    this.designationSelected = designationSelected;
  }

  /**
   * Department autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.departmentAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.departmentAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Department> departmentAuto(String filter) {
    return ScmLookupView.departmentAuto(filter);
  }
}
