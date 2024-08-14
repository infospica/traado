/*
 * @(#)ProdFreeQtySchemeRangeView.java	1.0 Fri Jan 13 15:50:50 IST 2017 
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

import spica.scm.domain.ProdFreeQtySchemeRange;
import spica.scm.service.ProdFreeQtySchemeRangeService;
import spica.scm.domain.ProductFreeQtyScheme;

/**
 * ProdFreeQtySchemeRangeView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Jan 13 15:50:50 IST 2017
 */
@Named(value = "prodFreeQtySchemeRangeView")
@ViewScoped
public class ProdFreeQtySchemeRangeView implements Serializable {

  private transient ProdFreeQtySchemeRange prodFreeQtySchemeRange;	//Domain object/selected Domain.
  private transient LazyDataModel<ProdFreeQtySchemeRange> prodFreeQtySchemeRangeLazyModel; 	//For lazy loading datatable.
  private transient ProdFreeQtySchemeRange[] prodFreeQtySchemeRangeSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public ProdFreeQtySchemeRangeView() {
    super();
  }

  /**
   * Return ProdFreeQtySchemeRange.
   *
   * @return ProdFreeQtySchemeRange.
   */
  public ProdFreeQtySchemeRange getProdFreeQtySchemeRange() {
    if (prodFreeQtySchemeRange == null) {
      prodFreeQtySchemeRange = new ProdFreeQtySchemeRange();
    }
    return prodFreeQtySchemeRange;
  }

  /**
   * Set ProdFreeQtySchemeRange.
   *
   * @param prodFreeQtySchemeRange.
   */
  public void setProdFreeQtySchemeRange(ProdFreeQtySchemeRange prodFreeQtySchemeRange) {
    this.prodFreeQtySchemeRange = prodFreeQtySchemeRange;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProdFreeQtySchemeRange(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getProdFreeQtySchemeRange().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setProdFreeQtySchemeRange((ProdFreeQtySchemeRange) ProdFreeQtySchemeRangeService.selectByPk(main, getProdFreeQtySchemeRange()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadProdFreeQtySchemeRangeList(main);
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
   * Create prodFreeQtySchemeRangeLazyModel.
   *
   * @param main
   */
  private void loadProdFreeQtySchemeRangeList(final MainView main) {
    if (prodFreeQtySchemeRangeLazyModel == null) {
      prodFreeQtySchemeRangeLazyModel = new LazyDataModel<ProdFreeQtySchemeRange>() {
        private List<ProdFreeQtySchemeRange> list;

        @Override
        public List<ProdFreeQtySchemeRange> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProdFreeQtySchemeRangeService.listPaged(main);
            main.commit(prodFreeQtySchemeRangeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProdFreeQtySchemeRange prodFreeQtySchemeRange) {
          return prodFreeQtySchemeRange.getId();
        }

        @Override
        public ProdFreeQtySchemeRange getRowData(String rowKey) {
          if (list != null) {
            for (ProdFreeQtySchemeRange obj : list) {
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
    String SUB_FOLDER = "scm_prod_free_qty_scheme_range/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProdFreeQtySchemeRange(MainView main) {
    return saveOrCloneProdFreeQtySchemeRange(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProdFreeQtySchemeRange(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProdFreeQtySchemeRange(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProdFreeQtySchemeRange(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProdFreeQtySchemeRangeService.insertOrUpdate(main, getProdFreeQtySchemeRange());
            break;
          case "clone":
            ProdFreeQtySchemeRangeService.clone(main, getProdFreeQtySchemeRange());
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
   * Delete one or many ProdFreeQtySchemeRange.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProdFreeQtySchemeRange(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(prodFreeQtySchemeRangeSelected)) {
        ProdFreeQtySchemeRangeService.deleteByPkArray(main, getProdFreeQtySchemeRangeSelected()); //many record delete from list
        main.commit("success.delete");
        prodFreeQtySchemeRangeSelected = null;
      } else {
        ProdFreeQtySchemeRangeService.deleteByPk(main, getProdFreeQtySchemeRange());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProdFreeQtySchemeRange.
   *
   * @return
   */
  public LazyDataModel<ProdFreeQtySchemeRange> getProdFreeQtySchemeRangeLazyModel() {
    return prodFreeQtySchemeRangeLazyModel;
  }

  /**
   * Return ProdFreeQtySchemeRange[].
   *
   * @return
   */
  public ProdFreeQtySchemeRange[] getProdFreeQtySchemeRangeSelected() {
    return prodFreeQtySchemeRangeSelected;
  }

  /**
   * Set ProdFreeQtySchemeRange[].
   *
   * @param prodFreeQtySchemeRangeSelected
   */
  public void setProdFreeQtySchemeRangeSelected(ProdFreeQtySchemeRange[] prodFreeQtySchemeRangeSelected) {
    this.prodFreeQtySchemeRangeSelected = prodFreeQtySchemeRangeSelected;
  }

  /**
   * ProductFreeQtyScheme autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.productFreeQtySchemeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.productFreeQtySchemeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ProductFreeQtyScheme> productFreeQtySchemeAuto(String filter) {
    return ScmLookupView.productFreeQtySchemeAuto(filter);
  }
}
