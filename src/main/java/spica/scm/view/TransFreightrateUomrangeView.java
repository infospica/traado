/*
 * @(#)TransFreightrateUomrangeView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.TransFreightrateUomrange;
import spica.scm.domain.TransporterFreightRate;
import spica.scm.service.TransFreightrateUomrangeService;
import wawo.app.faces.Jsf;

/**
 * TransFreightrateUomrangeView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "transFreightrateUomrangeView")
@ViewScoped
public class TransFreightrateUomrangeView implements Serializable {

  private transient TransFreightrateUomrange transFreightrateUomrange;	//Domain object/selected Domain.
  private transient LazyDataModel<TransFreightrateUomrange> transFreightrateUomrangeLazyModel; 	//For lazy loading datatable.
  private transient TransFreightrateUomrange[] transFreightrateUomrangeSelected;	 //Selected Domain Array
  private List<TransFreightrateUomrange> transFreightrateUomrangeList;

  /**
   * Default Constructor.
   */
  public TransFreightrateUomrangeView() {
    super();
  }

  /**
   * Return TransFreightrateUomrange.
   *
   * @return TransFreightrateUomrange.
   */
  public TransFreightrateUomrange getTransFreightrateUomrange() {
    if (transFreightrateUomrange == null) {
      transFreightrateUomrange = new TransFreightrateUomrange();
    }
    return transFreightrateUomrange;
  }

  /**
   * Set TransFreightrateUomrange.
   *
   * @param transFreightrateUomrange.
   */
  public void setTransFreightrateUomrange(TransFreightrateUomrange transFreightrateUomrange) {
    this.transFreightrateUomrange = transFreightrateUomrange;
  }

  private TransporterFreightRate parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (TransporterFreightRate) Jsf.popupParentValue(TransporterFreightRate.class);
    getTransFreightrateUomrange().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchTransFreightrateUomrange(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getTransFreightrateUomrange().reset();
          getTransFreightrateUomrange().setTransporterFreightRateId(parent);
        } else if (ViewType.editform.toString().equals(viewType)) {
          setTransFreightrateUomrange((TransFreightrateUomrange) TransFreightrateUomrangeService.selectByPk(main, getTransFreightrateUomrange()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadTransFreightrateUomrangeList(main);
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
   * Create transFreightrateUomrangeLazyModel.
   *
   * @param main
   */
  private void loadTransFreightrateUomrangeList(final MainView main) {
    if (transFreightrateUomrangeLazyModel == null) {
      transFreightrateUomrangeLazyModel = new LazyDataModel<TransFreightrateUomrange>() {
        private List<TransFreightrateUomrange> list;

        @Override
        public List<TransFreightrateUomrange> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = TransFreightrateUomrangeService.listPaged(main);
            main.commit(transFreightrateUomrangeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(TransFreightrateUomrange transFreightrateUomrange) {
          return transFreightrateUomrange.getId();
        }

        @Override
        public TransFreightrateUomrange getRowData(String rowKey) {
          if (list != null) {
            for (TransFreightrateUomrange obj : list) {
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
    String SUB_FOLDER = "scm_trans_freightrate_uomrange/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveTransFreightrateUomrange(MainView main) {
    return saveOrCloneTransFreightrateUomrange(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTransFreightrateUomrange(MainView main) {
    main.setViewType("newform");
    return saveOrCloneTransFreightrateUomrange(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTransFreightrateUomrange(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            TransFreightrateUomrangeService.insertOrUpdate(main, getTransFreightrateUomrange());
            break;
          case "clone":
            TransFreightrateUomrangeService.clone(main, getTransFreightrateUomrange());
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
   * Delete one or many TransFreightrateUomrange.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTransFreightrateUomrange(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(transFreightrateUomrangeSelected)) {
        TransFreightrateUomrangeService.deleteByPkArray(main, getTransFreightrateUomrangeSelected()); //many record delete from list
        main.commit("success.delete");
        transFreightrateUomrangeSelected = null;
      } else {
        TransFreightrateUomrangeService.deleteByPk(main, getTransFreightrateUomrange());  //individual record delete from list or edit form
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
   * Return LazyDataModel of TransFreightrateUomrange.
   *
   * @return
   */
  public LazyDataModel<TransFreightrateUomrange> getTransFreightrateUomrangeLazyModel() {
    return transFreightrateUomrangeLazyModel;
  }

  /**
   * Return TransFreightrateUomrange[].
   *
   * @return
   */
  public TransFreightrateUomrange[] getTransFreightrateUomrangeSelected() {
    return transFreightrateUomrangeSelected;
  }

  /**
   * Set TransFreightrateUomrange[].
   *
   * @param transFreightrateUomrangeSelected
   */
  public void setTransFreightrateUomrangeSelected(TransFreightrateUomrange[] transFreightrateUomrangeSelected) {
    this.transFreightrateUomrangeSelected = transFreightrateUomrangeSelected;
  }

  public void transFreightrateUomrangeDialogClose() {
    Jsf.returnDialog(null);
  }

  public List<TransFreightrateUomrange> getTransporterFrieghtUomRangeList(MainView main) {
    if (transFreightrateUomrangeList == null) {
      try {
        transFreightrateUomrangeList = TransFreightrateUomrangeService.getTransporterRateList(main, parent);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return transFreightrateUomrangeList;
  }

  public void onRowEdit(RowEditEvent event) {
    MainView main = Jsf.getMain();
    try {
      transFreightrateUomrange = (TransFreightrateUomrange) event.getObject();
      TransFreightrateUomrangeService.insertOrUpdate(main, transFreightrateUomrange);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public String deleteTtransFreightrateUomrange(MainView main, TransFreightrateUomrange transFreightrateUomrange) {
    if (transFreightrateUomrange.getId() == null) {
      transFreightrateUomrangeList.remove(transFreightrateUomrange);
    } else {
      try {
        TransFreightrateUomrangeService.deleteTransFreightrateUomrange(main, transFreightrateUomrange);
        main.commit("success.delete");
        transFreightrateUomrangeList.remove(transFreightrateUomrange);
      } catch (Throwable t) {
        main.rollback(t, "error.delete");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public String addNewFreightrateUomrange() {
    if (transFreightrateUomrangeList != null && (transFreightrateUomrangeList.isEmpty() || transFreightrateUomrangeList.get(0).getId() != null)) {
      TransFreightrateUomrange tfr = new TransFreightrateUomrange();
      tfr.setTransporterFreightRateId(parent);
      if (transFreightrateUomrangeList.isEmpty()) {
//        tfr.setRangeTo(0);
        tfr.setRangeFrom(0);
      } else {
        tfr.setRangeFrom(transFreightrateUomrangeList.get(0).getRangeTo() + 1);
      }
      transFreightrateUomrangeList.add(0, tfr);
    }
    return null;

  }

  public void uomRangePopupClose() {
    Jsf.popupClose(parent);
  }
}
