/*
 * @(#)ProdEntDetRtnStatusView.java	1.0 Thu Sep 08 18:33:15 IST 2016 
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

import spica.scm.domain.ProdEntDetRtnStatus;
import spica.scm.service.ProdEntDetRtnStatusService;

/**
 * ProdEntDetRtnStatusView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Sep 08 18:33:15 IST 2016
 */
@Named(value = "prodEntDetRtnStatusView")
@ViewScoped
public class ProdEntDetRtnStatusView implements Serializable {

  private transient ProdEntDetRtnStatus prodEntDetRtnStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<ProdEntDetRtnStatus> prodEntDetRtnStatusLazyModel; 	//For lazy loading datatable.
  private transient ProdEntDetRtnStatus[] prodEntDetRtnStatusSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public ProdEntDetRtnStatusView() {
    super();
  }

  /**
   * Return ProdEntDetRtnStatus.
   *
   * @return ProdEntDetRtnStatus.
   */
  public ProdEntDetRtnStatus getProdEntDetRtnStatus() {
    if (prodEntDetRtnStatus == null) {
      prodEntDetRtnStatus = new ProdEntDetRtnStatus();
    }
    return prodEntDetRtnStatus;
  }

  /**
   * Set ProdEntDetRtnStatus.
   *
   * @param prodEntDetRtnStatus.
   */
  public void setProdEntDetRtnStatus(ProdEntDetRtnStatus prodEntDetRtnStatus) {
    this.prodEntDetRtnStatus = prodEntDetRtnStatus;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProdEntDetRtnStatus(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getProdEntDetRtnStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setProdEntDetRtnStatus((ProdEntDetRtnStatus) ProdEntDetRtnStatusService.selectByPk(main, getProdEntDetRtnStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadProdEntDetRtnStatusList(main);
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
   * Create prodEntDetRtnStatusLazyModel.
   *
   * @param main
   */
  private void loadProdEntDetRtnStatusList(final MainView main) {
    if (prodEntDetRtnStatusLazyModel == null) {
      prodEntDetRtnStatusLazyModel = new LazyDataModel<ProdEntDetRtnStatus>() {
        private List<ProdEntDetRtnStatus> list;

        @Override
        public List<ProdEntDetRtnStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProdEntDetRtnStatusService.listPaged(main);
            main.commit(prodEntDetRtnStatusLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProdEntDetRtnStatus prodEntDetRtnStatus) {
          return prodEntDetRtnStatus.getId();
        }

        @Override
        public ProdEntDetRtnStatus getRowData(String rowKey) {
          if (list != null) {
            for (ProdEntDetRtnStatus obj : list) {
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
    String SUB_FOLDER = "scm_prod_ent_det_rtn_status/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProdEntDetRtnStatus(MainView main) {
    return saveOrCloneProdEntDetRtnStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProdEntDetRtnStatus(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProdEntDetRtnStatus(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProdEntDetRtnStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProdEntDetRtnStatusService.insertOrUpdate(main, getProdEntDetRtnStatus());
            break;
          case "clone":
            ProdEntDetRtnStatusService.clone(main, getProdEntDetRtnStatus());
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
   * Delete one or many ProdEntDetRtnStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProdEntDetRtnStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(prodEntDetRtnStatusSelected)) {
        ProdEntDetRtnStatusService.deleteByPkArray(main, getProdEntDetRtnStatusSelected()); //many record delete from list
        main.commit("success.delete");
        prodEntDetRtnStatusSelected = null;
      } else {
        ProdEntDetRtnStatusService.deleteByPk(main, getProdEntDetRtnStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProdEntDetRtnStatus.
   *
   * @return
   */
  public LazyDataModel<ProdEntDetRtnStatus> getProdEntDetRtnStatusLazyModel() {
    return prodEntDetRtnStatusLazyModel;
  }

  /**
   * Return ProdEntDetRtnStatus[].
   *
   * @return
   */
  public ProdEntDetRtnStatus[] getProdEntDetRtnStatusSelected() {
    return prodEntDetRtnStatusSelected;
  }

  /**
   * Set ProdEntDetRtnStatus[].
   *
   * @param prodEntDetRtnStatusSelected
   */
  public void setProdEntDetRtnStatusSelected(ProdEntDetRtnStatus[] prodEntDetRtnStatusSelected) {
    this.prodEntDetRtnStatusSelected = prodEntDetRtnStatusSelected;
  }

}
