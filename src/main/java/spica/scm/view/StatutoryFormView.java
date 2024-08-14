/*
 * @(#)StatutoryFormView.java	1.0 Thu Jun 16 11:34:12 IST 2016 
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

import spica.scm.domain.StatutoryForm;
import spica.scm.service.StatutoryFormService;

/**
 * StatutoryFormView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Jun 16 11:34:12 IST 2016
 */
@Named(value = "statutoryFormView")
@ViewScoped
public class StatutoryFormView implements Serializable {

  private transient StatutoryForm statutoryForm;	//Domain object/selected Domain.
  private transient LazyDataModel<StatutoryForm> statutoryFormLazyModel; 	//For lazy loading datatable.
  private transient StatutoryForm[] statutoryFormSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public StatutoryFormView() {
    super();
  }

  /**
   * Return StatutoryForm.
   *
   * @return StatutoryForm.
   */
  public StatutoryForm getStatutoryForm() {
    if (statutoryForm == null) {
      statutoryForm = new StatutoryForm();
    }
    return statutoryForm;
  }

  /**
   * Set StatutoryForm.
   *
   * @param statutoryForm.
   */
  public void setStatutoryForm(StatutoryForm statutoryForm) {
    this.statutoryForm = statutoryForm;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchStatutoryForm(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getStatutoryForm().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setStatutoryForm((StatutoryForm) StatutoryFormService.selectByPk(main, getStatutoryForm()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadStatutoryFormList(main);
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
   * Create statutoryFormLazyModel.
   *
   * @param main
   */
  private void loadStatutoryFormList(final MainView main) {
    if (statutoryFormLazyModel == null) {
      statutoryFormLazyModel = new LazyDataModel<StatutoryForm>() {
        private List<StatutoryForm> list;

        @Override
        public List<StatutoryForm> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = StatutoryFormService.listPaged(main);
            main.commit(statutoryFormLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(StatutoryForm statutoryForm) {
          return statutoryForm.getId();
        }

        @Override
        public StatutoryForm getRowData(String rowKey) {
          if (list != null) {
            for (StatutoryForm obj : list) {
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
    String SUB_FOLDER = "scm_statutory_form/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveStatutoryForm(MainView main) {
    return saveOrCloneStatutoryForm(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneStatutoryForm(MainView main) {
    main.setViewType("newform");
    return saveOrCloneStatutoryForm(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneStatutoryForm(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            StatutoryFormService.insertOrUpdate(main, getStatutoryForm());
            break;
          case "clone":
            StatutoryFormService.clone(main, getStatutoryForm());
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
   * Delete one or many StatutoryForm.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteStatutoryForm(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(statutoryFormSelected)) {
        StatutoryFormService.deleteByPkArray(main, getStatutoryFormSelected()); //many record delete from list
        main.commit("success.delete");
        statutoryFormSelected = null;
      } else {
        StatutoryFormService.deleteByPk(main, getStatutoryForm());  //individual record delete from list or edit form
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
   * Return LazyDataModel of StatutoryForm.
   *
   * @return
   */
  public LazyDataModel<StatutoryForm> getStatutoryFormLazyModel() {
    return statutoryFormLazyModel;
  }

  /**
   * Return StatutoryForm[].
   *
   * @return
   */
  public StatutoryForm[] getStatutoryFormSelected() {
    return statutoryFormSelected;
  }

  /**
   * Set StatutoryForm[].
   *
   * @param statutoryFormSelected
   */
  public void setStatutoryFormSelected(StatutoryForm[] statutoryFormSelected) {
    this.statutoryFormSelected = statutoryFormSelected;
  }

}
