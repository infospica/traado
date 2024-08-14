/*
 * @(#)ProdDetailStatusView.java	1.0 Tue May 10 17:16:17 IST 2016 
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
import spica.scm.domain.ProductDetailStatus;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.service.ProdDetailStatusService;

/**
 * ProdDetailStatusView
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue May 10 17:16:17 IST 2016
 */
@Named(value = "prodDetailStatusView")
@ViewScoped
public class ProdDetailStatusView implements Serializable {

  private transient ProductDetailStatus prodDetailStatus;	//Domain object/selected Domain.
  private transient LazyDataModel<ProductDetailStatus> prodDetailStatusLazyModel; 	//For lazy loading datatable.
  private transient ProductDetailStatus[] prodDetailStatusSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public ProdDetailStatusView() {
    super();
  }

  /**
   * Return ProdDetailStatus.
   *
   * @return ProdDetailStatus.
   */
  public ProductDetailStatus getProdDetailStatus() {
    if (prodDetailStatus == null) {
      prodDetailStatus = new ProductDetailStatus();
    }
    return prodDetailStatus;
  }

  /**
   * Set ProdDetailStatus.
   *
   * @param prodDetailStatus.
   */
  public void setProdDetailStatus(ProductDetailStatus prodDetailStatus) {
    this.prodDetailStatus = prodDetailStatus;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProdDetailStatus(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getProdDetailStatus().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setProdDetailStatus((ProductDetailStatus) ProdDetailStatusService.selectByPk(main, getProdDetailStatus()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadProdDetailStatusList(main);
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
   * Create prodDetailStatusLazyModel.
   *
   * @param main
   */
  private void loadProdDetailStatusList(final MainView main) {
    if (prodDetailStatusLazyModel == null) {
      prodDetailStatusLazyModel = new LazyDataModel<ProductDetailStatus>() {
        private List<ProductDetailStatus> list;

        @Override
        public List<ProductDetailStatus> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProdDetailStatusService.listPaged(main);
            main.commit(prodDetailStatusLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProductDetailStatus prodDetailStatus) {
          return prodDetailStatus.getId();
        }

        @Override
        public ProductDetailStatus getRowData(String rowKey) {
          if (list != null) {
            for (ProductDetailStatus obj : list) {
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
    String SUB_FOLDER = "scm_prod_detail_status/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProdDetailStatus(MainView main) {
    return saveOrCloneProdDetailStatus(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProdDetailStatus(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProdDetailStatus(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProdDetailStatus(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProdDetailStatusService.insertOrUpdate(main, getProdDetailStatus());
            break;
          case "clone":
            ProdDetailStatusService.clone(main, getProdDetailStatus());
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
   * Delete one or many ProdDetailStatus.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProdDetailStatus(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(prodDetailStatusSelected)) {
        ProdDetailStatusService.deleteByPkArray(main, getProdDetailStatusSelected()); //many record delete from list
        main.commit("success.delete");
        prodDetailStatusSelected = null;
      } else {
        ProdDetailStatusService.deleteByPk(main, getProdDetailStatus());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProdDetailStatus.
   *
   * @return
   */
  public LazyDataModel<ProductDetailStatus> getProdDetailStatusLazyModel() {
    return prodDetailStatusLazyModel;
  }

  /**
   * Return ProdDetailStatus[].
   *
   * @return
   */
  public ProductDetailStatus[] getProdDetailStatusSelected() {
    return prodDetailStatusSelected;
  }

  /**
   * Set ProdDetailStatus[].
   *
   * @param prodDetailStatusSelected
   */
  public void setProdDetailStatusSelected(ProductDetailStatus[] prodDetailStatusSelected) {
    this.prodDetailStatusSelected = prodDetailStatusSelected;
  }

}
