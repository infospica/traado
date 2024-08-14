/*
 * @(#)SupplierGroupView.java	1.0 Thu Dec 21 15:36:33 IST 2017 
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

import spica.scm.domain.SupplierGroup;
import spica.scm.service.SupplierGroupService;
import spica.scm.domain.Status;
import spica.sys.UserRuntimeView;

/**
 * SupplierGroupView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Dec 21 15:36:33 IST 2017
 */
@Named(value = "supplierGroupView")
@ViewScoped
public class SupplierGroupView implements Serializable {

  private transient SupplierGroup supplierGroup;	//Domain object/selected Domain.
  private transient LazyDataModel<SupplierGroup> supplierGroupLazyModel; 	//For lazy loading datatable.
  private transient SupplierGroup[] supplierGroupSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public SupplierGroupView() {
    super();
  }

  /**
   * Return SupplierGroup.
   *
   * @return SupplierGroup.
   */
  public SupplierGroup getSupplierGroup() {
    if (supplierGroup == null) {
      supplierGroup = new SupplierGroup();
    }
    return supplierGroup;
  }

  /**
   * Set SupplierGroup.
   *
   * @param supplierGroup.
   */
  public void setSupplierGroup(SupplierGroup supplierGroup) {
    this.supplierGroup = supplierGroup;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSupplierGroup(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getSupplierGroup().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setSupplierGroup((SupplierGroup) SupplierGroupService.selectByPk(main, getSupplierGroup()));
        } else if (main.isList()) {
          loadSupplierGroupList(main);
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
   * Create supplierGroupLazyModel.
   *
   * @param main
   */
  private void loadSupplierGroupList(final MainView main) {
    if (supplierGroupLazyModel == null) {
      supplierGroupLazyModel = new LazyDataModel<SupplierGroup>() {
        private List<SupplierGroup> list;

        @Override
        public List<SupplierGroup> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (UserRuntimeView.instance().getCompany() != null) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = SupplierGroupService.listPagedByCompany(main, UserRuntimeView.instance().getCompany());
              main.commit(supplierGroupLazyModel, first, pageSize);
            }
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SupplierGroup supplierGroup) {
          return supplierGroup.getId();
        }

        @Override
        public SupplierGroup getRowData(String rowKey) {
          if (list != null) {
            for (SupplierGroup obj : list) {
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
    String SUB_FOLDER = "scm_supplier_group/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSupplierGroup(MainView main) {
    return saveOrCloneSupplierGroup(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSupplierGroup(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneSupplierGroup(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSupplierGroup(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            if (main.isNew()) {
              getSupplierGroup().setCompanyId(UserRuntimeView.instance().getCompany());
            }
            //getSupplierGroup().setCode(getSupplierGroup().getCode().toUpperCase());
            SupplierGroupService.insertOrUpdate(main, getSupplierGroup());
            break;
          case "clone":
            SupplierGroupService.clone(main, getSupplierGroup());
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
   * Delete one or many SupplierGroup.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSupplierGroup(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(supplierGroupSelected)) {
        SupplierGroupService.deleteByPkArray(main, getSupplierGroupSelected()); //many record delete from list
        main.commit("success.delete");
        supplierGroupSelected = null;
      } else {
        SupplierGroupService.deleteByPk(main, getSupplierGroup());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (main.isEdit()) {
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
   * Return LazyDataModel of SupplierGroup.
   *
   * @return
   */
  public LazyDataModel<SupplierGroup> getSupplierGroupLazyModel() {
    return supplierGroupLazyModel;
  }

  /**
   * Return SupplierGroup[].
   *
   * @return
   */
  public SupplierGroup[] getSupplierGroupSelected() {
    return supplierGroupSelected;
  }

  /**
   * Set SupplierGroup[].
   *
   * @param supplierGroupSelected
   */
  public void setSupplierGroupSelected(SupplierGroup[] supplierGroupSelected) {
    this.supplierGroupSelected = supplierGroupSelected;
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
}
