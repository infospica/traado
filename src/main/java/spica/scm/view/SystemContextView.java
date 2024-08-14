/*
 * @(#)SystemContextView.java	1.0 Fri Jun 17 17:57:40 IST 2016 
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

import spica.scm.domain.SystemContext;
import spica.scm.service.SystemContextService;

/**
 * SystemContextView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jun 17 17:57:40 IST 2016
 */
@Named(value = "systemContextView")
@ViewScoped
public class SystemContextView implements Serializable {

  private transient SystemContext systemContext;	//Domain object/selected Domain.
  private transient LazyDataModel<SystemContext> systemContextLazyModel; 	//For lazy loading datatable.
  private transient SystemContext[] systemContextSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public SystemContextView() {
    super();
  }

  /**
   * Return SystemContext.
   *
   * @return SystemContext.
   */
  public SystemContext getSystemContext() {
    if (systemContext == null) {
      systemContext = new SystemContext();
    }
    return systemContext;
  }

  /**
   * Set SystemContext.
   *
   * @param systemContext.
   */
  public void setSystemContext(SystemContext systemContext) {
    this.systemContext = systemContext;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSystemContext(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getSystemContext().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setSystemContext((SystemContext) SystemContextService.selectByPk(main, getSystemContext()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadSystemContextList(main);
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
   * Create systemContextLazyModel.
   *
   * @param main
   */
  private void loadSystemContextList(final MainView main) {
    if (systemContextLazyModel == null) {
      systemContextLazyModel = new LazyDataModel<SystemContext>() {
        private List<SystemContext> list;

        @Override
        public List<SystemContext> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SystemContextService.listPaged(main);
            main.commit(systemContextLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SystemContext systemContext) {
          return systemContext.getId();
        }

        @Override
        public SystemContext getRowData(String rowKey) {
          if (list != null) {
            for (SystemContext obj : list) {
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
    String SUB_FOLDER = "scm_system_context/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSystemContext(MainView main) {
    return saveOrCloneSystemContext(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSystemContext(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSystemContext(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSystemContext(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SystemContextService.insertOrUpdate(main, getSystemContext());
            break;
          case "clone":
            SystemContextService.clone(main, getSystemContext());
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
   * Delete one or many SystemContext.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSystemContext(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(systemContextSelected)) {
        SystemContextService.deleteByPkArray(main, getSystemContextSelected()); //many record delete from list
        main.commit("success.delete");
        systemContextSelected = null;
      } else {
        SystemContextService.deleteByPk(main, getSystemContext());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SystemContext.
   *
   * @return
   */
  public LazyDataModel<SystemContext> getSystemContextLazyModel() {
    return systemContextLazyModel;
  }

  /**
   * Return SystemContext[].
   *
   * @return
   */
  public SystemContext[] getSystemContextSelected() {
    return systemContextSelected;
  }

  /**
   * Set SystemContext[].
   *
   * @param systemContextSelected
   */
  public void setSystemContextSelected(SystemContext[] systemContextSelected) {
    this.systemContextSelected = systemContextSelected;
  }

}
