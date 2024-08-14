/*
 * @(#)SalesAgentContractView.java	1.0 Fri Dec 23 10:28:08 IST 2016 
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
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.SelectItem;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.SalesAgentContract;
import spica.scm.service.SalesAgentContractService;
import spica.scm.domain.Company;
import spica.sys.FileConstant;
import wawo.app.config.AppConfig;
import wawo.app.faces.Jsf;

/**
 * SalesAgentContractView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Dec 23 10:28:08 IST 2016
 */
@Named(value = "salesAgentContractView")
@ViewScoped
public class SalesAgentContractView implements Serializable {

  private transient SalesAgentContract salesAgentContract;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesAgentContract> salesAgentContractLazyModel; 	//For lazy loading datatable.
  private transient SalesAgentContract[] salesAgentContractSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public SalesAgentContractView() {
    super();
  }

  /**
   * Return SalesAgentContract.
   *
   * @return SalesAgentContract.
   */
  public SalesAgentContract getSalesAgentContract() {
    if (salesAgentContract == null) {
      salesAgentContract = new SalesAgentContract();
    }
    return salesAgentContract;
  }

  /**
   * Set SalesAgentContract.
   *
   * @param salesAgentContract.
   */
  public void setSalesAgentContract(SalesAgentContract salesAgentContract) {
    this.salesAgentContract = salesAgentContract;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchSalesAgentContract(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getSalesAgentContract().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setSalesAgentContract((SalesAgentContract) SalesAgentContractService.selectByPk(main, getSalesAgentContract()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadSalesAgentContractList(main);
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
   * Create salesAgentContractLazyModel.
   *
   * @param main
   */
  private void loadSalesAgentContractList(final MainView main) {
    if (salesAgentContractLazyModel == null) {
      salesAgentContractLazyModel = new LazyDataModel<SalesAgentContract>() {
        private List<SalesAgentContract> list;

        @Override
        public List<SalesAgentContract> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesAgentContractService.listPaged(main);
            main.commit(salesAgentContractLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesAgentContract salesAgentContract) {
          return salesAgentContract.getId();
        }

        @Override
        public SalesAgentContract getRowData(String rowKey) {
          if (list != null) {
            for (SalesAgentContract obj : list) {
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
    String SUB_FOLDER = "scm_sales_agent_contract/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesAgentContract(MainView main) {
    return saveOrCloneSalesAgentContract(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesAgentContract(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesAgentContract(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesAgentContract(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesAgentContractService.insertOrUpdate(main, getSalesAgentContract());
            break;
          case "clone":
            SalesAgentContractService.clone(main, getSalesAgentContract());
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
   * Delete one or many SalesAgentContract.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesAgentContract(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesAgentContractSelected)) {
        SalesAgentContractService.deleteByPkArray(main, getSalesAgentContractSelected()); //many record delete from list
        main.commit("success.delete");
        salesAgentContractSelected = null;
      } else {
        SalesAgentContractService.deleteByPk(main, getSalesAgentContract());  //individual record delete from list or edit form
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
   * Return LazyDataModel of SalesAgentContract.
   *
   * @return
   */
  public LazyDataModel<SalesAgentContract> getSalesAgentContractLazyModel() {
    return salesAgentContractLazyModel;
  }

  /**
   * Return SalesAgentContract[].
   *
   * @return
   */
  public SalesAgentContract[] getSalesAgentContractSelected() {
    return salesAgentContractSelected;
  }

  /**
   * Set SalesAgentContract[].
   *
   * @param salesAgentContractSelected
   */
  public void setSalesAgentContractSelected(SalesAgentContract[] salesAgentContractSelected) {
    this.salesAgentContractSelected = salesAgentContractSelected;
  }

  /**
   * Company autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.companyAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.companyAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Company> companyAuto(String filter) {
    return ScmLookupView.companyAuto(filter);
  }

  public void onRowEdit(SalesAgentContract salesAgentContract) {
    MainView main = Jsf.getMain();
    try {
      SalesAgentContractService.insertOrUpdate(main, salesAgentContract);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }
//FIXME move these with fixed labels in xhtml
  public List<SelectItem> getListCommissionOn() {
    List<SelectItem> listYesNo = new ArrayList<>();
    SelectItem si1 = new SelectItem();
    si1.setItemLabel("Sales");
    si1.setIntValue(1);
    listYesNo.add(si1);
    SelectItem si2 = new SelectItem();
    si2.setItemLabel("Collection");
    si2.setIntValue(2);
    listYesNo.add(si2);
    return listYesNo;
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

  public void ajaxEventHandler(SelectEvent event) {
  }
  
  public void ajaxBehaviorEventHandler(AjaxBehaviorEvent event) {
  }
  
  public void salesAgentContractCommissionRangeListPopup(SalesAgentContract SalesAgentContract) {
    Jsf.popupList(FileConstant.SALES_AGENT_CONTRACT_COMMISSION_RANGE, SalesAgentContract);
  }
}
