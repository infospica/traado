/*
 * @(#)DesignationContextView.java	1.0 Fri Jun 17 17:57:40 IST 2016 
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

import spica.scm.domain.DesignationContext;
import spica.scm.service.DesignationContextService;
import spica.scm.domain.Designation;
import spica.scm.domain.SystemContext;

/**
 * DesignationContextView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jun 17 17:57:40 IST 2016
 */
@Named(value = "designationContextView")
@ViewScoped
public class DesignationContextView implements Serializable {

  private transient DesignationContext designationContext;	//Domain object/selected Domain.
  private transient LazyDataModel<DesignationContext> designationContextLazyModel; 	//For lazy loading datatable.
  private transient DesignationContext[] designationContextSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public DesignationContextView() {
    super();
  }

  /**
   * Return DesignationContext.
   *
   * @return DesignationContext.
   */
  public DesignationContext getDesignationContext() {
    if (designationContext == null) {
      designationContext = new DesignationContext();
    }
    return designationContext;
  }

  /**
   * Set DesignationContext.
   *
   * @param designationContext.
   */
  public void setDesignationContext(DesignationContext designationContext) {
    this.designationContext = designationContext;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchDesignationContext(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getDesignationContext().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setDesignationContext((DesignationContext) DesignationContextService.selectByPk(main, getDesignationContext()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadDesignationContextList(main);
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
   * Create designationContextLazyModel.
   *
   * @param main
   */
  private void loadDesignationContextList(final MainView main) {
    if (designationContextLazyModel == null) {
      designationContextLazyModel = new LazyDataModel<DesignationContext>() {
        private List<DesignationContext> list;

        @Override
        public List<DesignationContext> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = DesignationContextService.listPaged(main);
            main.commit(designationContextLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(DesignationContext designationContext) {
          return designationContext.getId();
        }

        @Override
        public DesignationContext getRowData(String rowKey) {
          if (list != null) {
            for (DesignationContext obj : list) {
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
    String SUB_FOLDER = "scm_designation_context/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveDesignationContext(MainView main) {
    return saveOrCloneDesignationContext(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneDesignationContext(MainView main) {
    main.setViewType("newform");
    return saveOrCloneDesignationContext(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneDesignationContext(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            DesignationContextService.insertOrUpdate(main, getDesignationContext());
            break;
          case "clone":
            DesignationContextService.clone(main, getDesignationContext());
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
   * Delete one or many DesignationContext.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteDesignationContext(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(designationContextSelected)) {
        DesignationContextService.deleteByPkArray(main, getDesignationContextSelected()); //many record delete from list
        main.commit("success.delete");
        designationContextSelected = null;
      } else {
        DesignationContextService.deleteByPk(main, getDesignationContext());  //individual record delete from list or edit form
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
   * Return LazyDataModel of DesignationContext.
   *
   * @return
   */
  public LazyDataModel<DesignationContext> getDesignationContextLazyModel() {
    return designationContextLazyModel;
  }

  /**
   * Return DesignationContext[].
   *
   * @return
   */
  public DesignationContext[] getDesignationContextSelected() {
    return designationContextSelected;
  }

  /**
   * Set DesignationContext[].
   *
   * @param designationContextSelected
   */
  public void setDesignationContextSelected(DesignationContext[] designationContextSelected) {
    this.designationContextSelected = designationContextSelected;
  }

  /**
   * Designation autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.designationAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.designationAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Designation> designationAuto(String filter) {
    return ScmLookupView.designationAuto(filter);
  }

  /**
   * SystemContext autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.systemContextAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.systemContextAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<SystemContext> systemContextAuto(String filter) {
    return ScmLookupView.systemContextAuto(filter);
  }
}
