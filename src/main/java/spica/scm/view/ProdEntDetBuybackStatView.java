/*
 * @(#)ProdEntDetBuybackStatView.java	1.0 Thu Sep 08 18:33:15 IST 2016 
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

import spica.scm.domain.ProdEntDetBuybackStat;
import spica.scm.service.ProdEntDetBuybackStatService;

/**
 * ProdEntDetBuybackStatView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Sep 08 18:33:15 IST 2016
 */
@Named(value = "prodEntDetBuybackStatView")
@ViewScoped
public class ProdEntDetBuybackStatView implements Serializable {

  private transient ProdEntDetBuybackStat prodEntDetBuybackStat;	//Domain object/selected Domain.
  private transient LazyDataModel<ProdEntDetBuybackStat> prodEntDetBuybackStatLazyModel; 	//For lazy loading datatable.
  private transient ProdEntDetBuybackStat[] prodEntDetBuybackStatSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public ProdEntDetBuybackStatView() {
    super();
  }

  /**
   * Return ProdEntDetBuybackStat.
   *
   * @return ProdEntDetBuybackStat.
   */
  public ProdEntDetBuybackStat getProdEntDetBuybackStat() {
    if (prodEntDetBuybackStat == null) {
      prodEntDetBuybackStat = new ProdEntDetBuybackStat();
    }
    return prodEntDetBuybackStat;
  }

  /**
   * Set ProdEntDetBuybackStat.
   *
   * @param prodEntDetBuybackStat.
   */
  public void setProdEntDetBuybackStat(ProdEntDetBuybackStat prodEntDetBuybackStat) {
    this.prodEntDetBuybackStat = prodEntDetBuybackStat;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchProdEntDetBuybackStat(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getProdEntDetBuybackStat().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setProdEntDetBuybackStat((ProdEntDetBuybackStat) ProdEntDetBuybackStatService.selectByPk(main, getProdEntDetBuybackStat()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadProdEntDetBuybackStatList(main);
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
   * Create prodEntDetBuybackStatLazyModel.
   *
   * @param main
   */
  private void loadProdEntDetBuybackStatList(final MainView main) {
    if (prodEntDetBuybackStatLazyModel == null) {
      prodEntDetBuybackStatLazyModel = new LazyDataModel<ProdEntDetBuybackStat>() {
        private List<ProdEntDetBuybackStat> list;

        @Override
        public List<ProdEntDetBuybackStat> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = ProdEntDetBuybackStatService.listPaged(main);
            main.commit(prodEntDetBuybackStatLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(ProdEntDetBuybackStat prodEntDetBuybackStat) {
          return prodEntDetBuybackStat.getId();
        }

        @Override
        public ProdEntDetBuybackStat getRowData(String rowKey) {
          if (list != null) {
            for (ProdEntDetBuybackStat obj : list) {
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
    String SUB_FOLDER = "scm_prod_ent_det_buyback_stat/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveProdEntDetBuybackStat(MainView main) {
    return saveOrCloneProdEntDetBuybackStat(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneProdEntDetBuybackStat(MainView main) {
    main.setViewType("newform");
    return saveOrCloneProdEntDetBuybackStat(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneProdEntDetBuybackStat(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ProdEntDetBuybackStatService.insertOrUpdate(main, getProdEntDetBuybackStat());
            break;
          case "clone":
            ProdEntDetBuybackStatService.clone(main, getProdEntDetBuybackStat());
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
   * Delete one or many ProdEntDetBuybackStat.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteProdEntDetBuybackStat(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(prodEntDetBuybackStatSelected)) {
        ProdEntDetBuybackStatService.deleteByPkArray(main, getProdEntDetBuybackStatSelected()); //many record delete from list
        main.commit("success.delete");
        prodEntDetBuybackStatSelected = null;
      } else {
        ProdEntDetBuybackStatService.deleteByPk(main, getProdEntDetBuybackStat());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ProdEntDetBuybackStat.
   *
   * @return
   */
  public LazyDataModel<ProdEntDetBuybackStat> getProdEntDetBuybackStatLazyModel() {
    return prodEntDetBuybackStatLazyModel;
  }

  /**
   * Return ProdEntDetBuybackStat[].
   *
   * @return
   */
  public ProdEntDetBuybackStat[] getProdEntDetBuybackStatSelected() {
    return prodEntDetBuybackStatSelected;
  }

  /**
   * Set ProdEntDetBuybackStat[].
   *
   * @param prodEntDetBuybackStatSelected
   */
  public void setProdEntDetBuybackStatSelected(ProdEntDetBuybackStat[] prodEntDetBuybackStatSelected) {
    this.prodEntDetBuybackStatSelected = prodEntDetBuybackStatSelected;
  }

}
