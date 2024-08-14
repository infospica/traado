/*
 * @(#)TradeProfileView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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

import spica.scm.domain.TradeProfile;
import spica.scm.service.TradeProfileService;

/**
 * TradeProfileView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "tradeProfileView")
@ViewScoped
public class TradeProfileView implements Serializable {

  private transient TradeProfile tradeProfile;	//Domain object/selected Domain.
  private transient LazyDataModel<TradeProfile> tradeProfileLazyModel; 	//For lazy loading datatable.
  private transient TradeProfile[] tradeProfileSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public TradeProfileView() {
    super();
  }

  /**
   * Return TradeProfile.
   *
   * @return TradeProfile.
   */
  public TradeProfile getTradeProfile() {
    if (tradeProfile == null) {
      tradeProfile = new TradeProfile();
    }
    return tradeProfile;
  }

  /**
   * Set TradeProfile.
   *
   * @param tradeProfile.
   */
  public void setTradeProfile(TradeProfile tradeProfile) {
    this.tradeProfile = tradeProfile;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchTradeProfile(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getTradeProfile().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setTradeProfile((TradeProfile) TradeProfileService.selectByPk(main, getTradeProfile()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadTradeProfileList(main);
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
   * Create tradeProfileLazyModel.
   *
   * @param main
   */
  private void loadTradeProfileList(final MainView main) {
    if (tradeProfileLazyModel == null) {
      tradeProfileLazyModel = new LazyDataModel<TradeProfile>() {
        private List<TradeProfile> list;

        @Override
        public List<TradeProfile> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = TradeProfileService.listPaged(main);
            main.commit(tradeProfileLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(TradeProfile tradeProfile) {
          return tradeProfile.getId();
        }

        @Override
        public TradeProfile getRowData(String rowKey) {
          if (list != null) {
            for (TradeProfile obj : list) {
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
    String SUB_FOLDER = "scm_trade_profile/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveTradeProfile(MainView main) {
    return saveOrCloneTradeProfile(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTradeProfile(MainView main) {
    main.setViewType("newform");
    return saveOrCloneTradeProfile(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTradeProfile(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            TradeProfileService.insertOrUpdate(main, getTradeProfile());
            break;
          case "clone":
            TradeProfileService.clone(main, getTradeProfile());
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
   * Delete one or many TradeProfile.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTradeProfile(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(tradeProfileSelected)) {
        TradeProfileService.deleteByPkArray(main, getTradeProfileSelected()); //many record delete from list
        main.commit("success.delete");
        tradeProfileSelected = null;
      } else {
        TradeProfileService.deleteByPk(main, getTradeProfile());  //individual record delete from list or edit form
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
   * Return LazyDataModel of TradeProfile.
   *
   * @return
   */
  public LazyDataModel<TradeProfile> getTradeProfileLazyModel() {
    return tradeProfileLazyModel;
  }

  /**
   * Return TradeProfile[].
   *
   * @return
   */
  public TradeProfile[] getTradeProfileSelected() {
    return tradeProfileSelected;
  }

  /**
   * Set TradeProfile[].
   *
   * @param tradeProfileSelected
   */
  public void setTradeProfileSelected(TradeProfile[] tradeProfileSelected) {
    this.tradeProfileSelected = tradeProfileSelected;
  }

}
