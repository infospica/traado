/*
 * @(#)SalesAgentCommissionReceiptView.java	1.0 Mon Dec 18 16:22:07 IST 2017 
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
import javax.servlet.http.Part;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypeAction;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.app.faces.JsfIo;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.SalesAgentCommissionReceipt;
import spica.scm.service.SalesAgentCommissionReceiptService;
import spica.scm.domain.UserProfile;

/**
 * SalesAgentCommissionReceiptView
 * @author	Spirit 1.2
 * @version	1.0, Mon Dec 18 16:22:07 IST 2017 
 */

@Named(value="salesAgentCommissionReceiptView")
@ViewScoped
public class SalesAgentCommissionReceiptView implements Serializable{

  private transient SalesAgentCommissionReceipt salesAgentCommissionReceipt;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesAgentCommissionReceipt> salesAgentCommissionReceiptLazyModel; 	//For lazy loading datatable.
  private transient SalesAgentCommissionReceipt[] salesAgentCommissionReceiptSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public SalesAgentCommissionReceiptView() {
    super();
  }
 
  /**
   * Return SalesAgentCommissionReceipt.
   * @return SalesAgentCommissionReceipt.
   */  
  public SalesAgentCommissionReceipt getSalesAgentCommissionReceipt() {
    if(salesAgentCommissionReceipt == null) {
      salesAgentCommissionReceipt = new SalesAgentCommissionReceipt();
    }
    return salesAgentCommissionReceipt;
  }   
  
  /**
   * Set SalesAgentCommissionReceipt.
   * @param salesAgentCommissionReceipt.
   */   
  public void setSalesAgentCommissionReceipt(SalesAgentCommissionReceipt salesAgentCommissionReceipt) {
    this.salesAgentCommissionReceipt = salesAgentCommissionReceipt;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchSalesAgentCommissionReceipt(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getSalesAgentCommissionReceipt().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setSalesAgentCommissionReceipt((SalesAgentCommissionReceipt) SalesAgentCommissionReceiptService.selectByPk(main, getSalesAgentCommissionReceipt()));
        } else if (main.isList()) {
          loadSalesAgentCommissionReceiptList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally{
        main.close();
      }
    }
    return null;
  } 
  
  /**
   * Create salesAgentCommissionReceiptLazyModel.
   * @param main
   */
  private void loadSalesAgentCommissionReceiptList(final MainView main) {
    if (salesAgentCommissionReceiptLazyModel == null) {
      salesAgentCommissionReceiptLazyModel = new LazyDataModel<SalesAgentCommissionReceipt>() {
      private List<SalesAgentCommissionReceipt> list;      
      @Override
      public List<SalesAgentCommissionReceipt> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = SalesAgentCommissionReceiptService.listPaged(main);
          main.commit(salesAgentCommissionReceiptLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(SalesAgentCommissionReceipt salesAgentCommissionReceipt) {
        return salesAgentCommissionReceipt.getId();
      }
      @Override
        public SalesAgentCommissionReceipt getRowData(String rowKey) {
          if (list != null) {
            for (SalesAgentCommissionReceipt obj : list) {
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
    String SUB_FOLDER = "scm_sales_agent_commission_receipt/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveSalesAgentCommissionReceipt(MainView main) {
    return saveOrCloneSalesAgentCommissionReceipt(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesAgentCommissionReceipt(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneSalesAgentCommissionReceipt(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesAgentCommissionReceipt(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            SalesAgentCommissionReceiptService.insertOrUpdate(main, getSalesAgentCommissionReceipt());
            break;
          case "clone":
            SalesAgentCommissionReceiptService.clone(main, getSalesAgentCommissionReceipt());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error."+ key);
    } finally {
      main.close();
    }
    return null;
  }

  
  /**
   * Delete one or many SalesAgentCommissionReceipt.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesAgentCommissionReceipt(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(salesAgentCommissionReceiptSelected)) {
        SalesAgentCommissionReceiptService.deleteByPkArray(main, getSalesAgentCommissionReceiptSelected()); //many record delete from list
        main.commit("success.delete");
        salesAgentCommissionReceiptSelected = null;
      } else {
        SalesAgentCommissionReceiptService.deleteByPk(main, getSalesAgentCommissionReceipt());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (main.isEdit()){
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
   * Return LazyDataModel of SalesAgentCommissionReceipt.
   * @return
   */
  public LazyDataModel<SalesAgentCommissionReceipt> getSalesAgentCommissionReceiptLazyModel() {
    return salesAgentCommissionReceiptLazyModel;
  }

 /**
  * Return SalesAgentCommissionReceipt[].
  * @return 
  */
  public SalesAgentCommissionReceipt[] getSalesAgentCommissionReceiptSelected() {
    return salesAgentCommissionReceiptSelected;
  }
  
  /**
   * Set SalesAgentCommissionReceipt[].
   * @param salesAgentCommissionReceiptSelected 
   */
  public void setSalesAgentCommissionReceiptSelected(SalesAgentCommissionReceipt[] salesAgentCommissionReceiptSelected) {
    this.salesAgentCommissionReceiptSelected = salesAgentCommissionReceiptSelected;
  }
 


 /**
  * UserProfile autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.userProfileAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.userProfileAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<UserProfile> userProfileAuto(String filter) {
    return ScmLookupView.userProfileAuto(filter);
  }
}
