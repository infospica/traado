/*
 * @(#)AgentCommissionRangeView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.SelectItem;
import spica.scm.domain.AgentCommissionRange;
import spica.scm.domain.SalesAgentContract;
import spica.scm.service.AgentCommissionRangeService;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;
import wawo.app.faces.Jsf;

/**
 * AgentCommissionRangeView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "agentCommissionRangeView")
@ViewScoped
public class AgentCommissionRangeView implements Serializable {

  private transient AgentCommissionRange agentCommissionRange;	//Domain object/selected Domain.
  private transient LazyDataModel<AgentCommissionRange> agentCommissionRangeLazyModel; 	//For lazy loading datatable.
  private transient AgentCommissionRange[] agentCommissionRangeSelected;	 //Selected Domain Array
  private List<AgentCommissionRange> agentCommissionRangeList;

  /**
   * Default Constructor.
   */
  public AgentCommissionRangeView() {
    super();
  }

  /**
   * Return AgentCommissionRange.
   *
   * @return AgentCommissionRange.
   */
  public AgentCommissionRange getAgentCommissionRange() {
    if (agentCommissionRange == null) {
      agentCommissionRange = new AgentCommissionRange();
    }
    return agentCommissionRange;
  }

  /**
   * Set AgentCommissionRange.
   *
   * @param agentCommissionRange.
   */
  public void setAgentCommissionRange(AgentCommissionRange agentCommissionRange) {
    this.agentCommissionRange = agentCommissionRange;
  }

  private SalesAgentContract parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (SalesAgentContract) Jsf.popupParentValue(SalesAgentContract.class);
    getAgentCommissionRange().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAgentCommissionRange(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getAgentCommissionRange().reset();
          getAgentCommissionRange().setSalesAgentContractId(parent);
        } else if (ViewType.editform.toString().equals(viewType)) {
          setAgentCommissionRange((AgentCommissionRange) AgentCommissionRangeService.selectByPk(main, getAgentCommissionRange()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadAgentCommissionRangeList(main);
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
   * Create agentCommissionRangeLazyModel.
   *
   * @param main
   */
  private void loadAgentCommissionRangeList(final MainView main) {
    if (agentCommissionRangeLazyModel == null) {
      agentCommissionRangeLazyModel = new LazyDataModel<AgentCommissionRange>() {
        private List<AgentCommissionRange> list;

        @Override
        public List<AgentCommissionRange> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = AgentCommissionRangeService.listPaged(main);
            main.commit(agentCommissionRangeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AgentCommissionRange agentCommissionRange) {
          return agentCommissionRange.getId();
        }

        @Override
        public AgentCommissionRange getRowData(String rowKey) {
          if (list != null) {
            for (AgentCommissionRange obj : list) {
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
    String SUB_FOLDER = "scm_agent_commission_range/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveAgentCommissionRange(MainView main) {
    return saveOrCloneAgentCommissionRange(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAgentCommissionRange(MainView main) {
    main.setViewType("newform");
    return saveOrCloneAgentCommissionRange(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAgentCommissionRange(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            AgentCommissionRangeService.insertOrUpdate(main, getAgentCommissionRange());
            break;
          case "clone":
            AgentCommissionRangeService.clone(main, getAgentCommissionRange());
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
   * Delete one or many AgentCommissionRange.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAgentCommissionRange(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(agentCommissionRangeSelected)) {
        AgentCommissionRangeService.deleteByPkArray(main, getAgentCommissionRangeSelected()); //many record delete from list
        main.commit("success.delete");
        agentCommissionRangeSelected = null;
      } else {
        AgentCommissionRangeService.deleteByPk(main, getAgentCommissionRange());  //individual record delete from list or edit form
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
   * Return LazyDataModel of AgentCommissionRange.
   *
   * @return
   */
  public LazyDataModel<AgentCommissionRange> getAgentCommissionRangeLazyModel() {
    return agentCommissionRangeLazyModel;
  }

  /**
   * Return AgentCommissionRange[].
   *
   * @return
   */
  public AgentCommissionRange[] getAgentCommissionRangeSelected() {
    return agentCommissionRangeSelected;
  }

  /**
   * Set AgentCommissionRange[].
   *
   * @param agentCommissionRangeSelected
   */
  public void setAgentCommissionRangeSelected(AgentCommissionRange[] agentCommissionRangeSelected) {
    this.agentCommissionRangeSelected = agentCommissionRangeSelected;
  }

  public void agentCommissionRangeDialogClose() {
    Jsf.returnDialog(null);
  }

  public List<AgentCommissionRange> getAgentCommissionRangeList(MainView main) {
    if (agentCommissionRangeList == null) {
      try {
        agentCommissionRangeList = AgentCommissionRangeService.getAgentCommissionList(main, parent);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return agentCommissionRangeList;
  }

  public void onRowEdit(RowEditEvent event) {
    MainView main = Jsf.getMain();
    try {
      agentCommissionRange = (AgentCommissionRange) event.getObject();
      AgentCommissionRangeService.insertOrUpdate(main, agentCommissionRange);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public String deleteAgentCommissionRange(MainView main, AgentCommissionRange agentCommissionRange) {
    if (agentCommissionRange.getId() == null) {
      agentCommissionRangeList.remove(agentCommissionRange);
    } else {
      try {
        AgentCommissionRangeService.deleteAgentCommissionRange(main, agentCommissionRange);
        main.commit("success.delete");
        agentCommissionRangeList.remove(agentCommissionRange);
      } catch (Throwable t) {
        main.rollback(t, "error.delete");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public String addNewCommissionRange() {
    if (agentCommissionRangeList != null && (agentCommissionRangeList.isEmpty() || agentCommissionRangeList.get(0).getId() != null)) {
      AgentCommissionRange tfr = new AgentCommissionRange();
      tfr.setSalesAgentContractId(parent);
      if (agentCommissionRangeList.isEmpty()) {
//        tfr.setRangeTo(0);
        tfr.setRangeFrom(0);
      } else {
        tfr.setRangeFrom(agentCommissionRangeList.get(0).getRangeTo() + 1);
      }
      agentCommissionRangeList.add(0, tfr);
    }
    return null;

  }

  public void commissionRangePopupClose() {
    Jsf.popupClose(parent);
  }

  public void ajaxBehaviorEventHandler(AjaxBehaviorEvent event) {
  }

  public List<SelectItem> getListCommissionValueType() {
    List<SelectItem> listYesNo = new ArrayList<>();
    SelectItem si1 = new SelectItem();
    si1.setItemLabel("Fixed");
    si1.setIntValue(1);
    listYesNo.add(si1);
    SelectItem si2 = new SelectItem();
    si2.setItemLabel("Percentage");
    si2.setIntValue(2);
    listYesNo.add(si2);
    return listYesNo;
  }
}
