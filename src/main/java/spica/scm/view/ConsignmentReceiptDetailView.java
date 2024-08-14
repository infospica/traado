/*
 * @(#)ConsignmentReceiptDetailView.java	1.0 Fri Jul 22 10:57:43 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */

package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;

import spica.scm.domain.ConsignmentReceiptDetail;
import spica.scm.service.ConsignmentReceiptDetailService;
import spica.scm.domain.ConsignmentCommodity;
import spica.scm.service.ConsignmentCommodityService;
import wawo.app.faces.Jsf;

/**
 * ConsignmentReceiptDetailView
 * @author	Spirit 1.2
 * @version	1.0, Fri Jul 22 10:57:43 IST 2016 
 */

@Named(value="consignmentReceiptDetailView")
@ViewScoped
public class ConsignmentReceiptDetailView implements Serializable{

  private transient ConsignmentReceiptDetail consignmentReceiptDetail;	//Domain object/selected Domain.
//  private transient LazyDataModel<ConsignmentReceiptDetail> consignmentReceiptDetailLazyModel; 	//For lazy loading datatable.
//  private transient ConsignmentReceiptDetail[] consignmentReceiptDetailSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public ConsignmentReceiptDetailView() {
    super();
  }
 
  /**
   * Return ConsignmentReceiptDetail.
   * @return ConsignmentReceiptDetail.
   */  
  public ConsignmentReceiptDetail getConsignmentReceiptDetail() {
    if(consignmentReceiptDetail == null) {
      consignmentReceiptDetail = new ConsignmentReceiptDetail();
    }
    return consignmentReceiptDetail;
  }   
  
  /**
   * Set ConsignmentReceiptDetail.
   * @param consignmentReceiptDetail.
   */   
  public void setConsignmentReceiptDetail(ConsignmentReceiptDetail consignmentReceiptDetail) {
    this.consignmentReceiptDetail = consignmentReceiptDetail;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchConsignmentReceiptDetail(MainView main, String viewType) {
   //this.main = main;
//   if (!StringUtil.isEmpty(viewType)) {
//      try {
//        main.setViewType(viewType);
//        if (ViewType.newform.toString().equals(viewType)) {
//          getConsignmentReceiptDetail().reset();
//        } else if (ViewType.editform.toString().equals(viewType)) {
//          setConsignmentReceiptDetail((ConsignmentReceiptDetail) ConsignmentReceiptDetailService.selectByPk(main, getConsignmentReceiptDetail()));
//        } else if (ViewType.list.toString().equals(viewType)) {
//          loadConsignmentReceiptDetailList(main);
//        }
//      } catch (Throwable t) {
//        main.rollback(t);
//      } finally{
//        main.close();
//      }
//    }
    return null;
  } 
 private ConsignmentCommodity parent;
 @PostConstruct
  public void init() {
    MainView main = Jsf.getMain();
    try {
       parent = (ConsignmentCommodity) Jsf.popupParentValue(ConsignmentCommodity.class);
      if (parent != null) {
        consignmentReceiptDetail= ConsignmentCommodityService.receiptPlanDetails(main, parent);
        if(consignmentReceiptDetail==null){
          getConsignmentReceiptDetail().setConsignmentCommodityId(parent);
           main.setViewType(ViewTypes.newform);
        } else{
           main.setViewType(ViewTypes.editform);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }
  
  
  /**
   * Create consignmentReceiptDetailLazyModel.
   * @param main
   */
//  private void loadConsignmentReceiptDetailList(final MainView main) {
//    if (consignmentReceiptDetailLazyModel == null) {
//      consignmentReceiptDetailLazyModel = new LazyDataModel<ConsignmentReceiptDetail>() {
//      private List<ConsignmentReceiptDetail> list;      
//      @Override
//      public List<ConsignmentReceiptDetail> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
//        try {
//          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
//          list = ConsignmentReceiptDetailService.listPaged(main);
//          main.commit(consignmentReceiptDetailLazyModel, first, pageSize);
//        } catch (Throwable t) {
//          main.rollback(t, "error.list");
//          return null;
//        } finally{
//          main.close();
//        }
//        return list;
//      }
//      @Override
//      public Object getRowKey(ConsignmentReceiptDetail consignmentReceiptDetail) {
//        return consignmentReceiptDetail.getId();
//      }
//      @Override
//        public ConsignmentReceiptDetail getRowData(String rowKey) {
//          if (list != null) {
//            for (ConsignmentReceiptDetail obj : list) {
//              if (rowKey.equals(obj.getId().toString())) {
//                return obj;
//              }
//            }
//          }
//          return null;
//        }
//      };
//    }
//  }
//
//  private void uploadFiles() {
//    String SUB_FOLDER = "scm_consignment_receipt_detail/";	
//  }
   public void saveConsignmentReceiptDetailClose(MainView main) {
    saveConsignmentReceiptDetail(main);
     consignmentReceiptDetailPopupClose();
     Jsf.execute("parent.consignmentCommodityPopupReturned()");
     Jsf.execute("closePopup()");
  }
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveConsignmentReceiptDetail(MainView main) {
    return saveOrCloneConsignmentReceiptDetail(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneConsignmentReceiptDetail(MainView main) {
    main.setViewType("newform");
    return saveOrCloneConsignmentReceiptDetail(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneConsignmentReceiptDetail(MainView main, String key) {
    try {
//      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ConsignmentReceiptDetailService.insertOrUpdate(main, getConsignmentReceiptDetail());
            break;
          case "clone":
            ConsignmentReceiptDetailService.clone(main, getConsignmentReceiptDetail());
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
   * Delete one or many ConsignmentReceiptDetail.
   *
   * @param main
   * @return the page to display.
   */
//  public String deleteConsignmentReceiptDetail(MainView main) {
//    try {
//      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(consignmentReceiptDetailSelected)) {
//        ConsignmentReceiptDetailService.deleteByPkArray(main, getConsignmentReceiptDetailSelected()); //many record delete from list
//        main.commit("success.delete");
//        consignmentReceiptDetailSelected = null;
//      } else {
//        ConsignmentReceiptDetailService.deleteByPk(main, getConsignmentReceiptDetail());  //individual record delete from list or edit form
//        main.commit("success.delete");
//        if ("editform".equals(main.getViewType())){
//          main.setViewType(ViewTypes.newform);
//        }
//      }
//    } catch (Throwable t) {
//      main.rollback(t, "error.delete");
//    } finally {
//      main.close();
//    }
//    return null;
//  }
  /**
   * Return LazyDataModel of ConsignmentReceiptDetail.
   *
   * @return
   */
//  public LazyDataModel<ConsignmentReceiptDetail> getConsignmentReceiptDetailLazyModel() {
//    return consignmentReceiptDetailLazyModel;
//  }
  /**
   * Return ConsignmentReceiptDetail[].
   *
   * @return
   */
//  public ConsignmentReceiptDetail[] getConsignmentReceiptDetailSelected() {
//    return consignmentReceiptDetailSelected;
//  }
  /**
   * Set ConsignmentReceiptDetail[].
   *
   * @param consignmentReceiptDetailSelected
   */
//  public void setConsignmentReceiptDetailSelected(ConsignmentReceiptDetail[] consignmentReceiptDetailSelected) {
//    this.consignmentReceiptDetailSelected = consignmentReceiptDetailSelected;
//  }
 
 /**
  * ConsignmentCommodity autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookup.consignmentCommodityAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookup.consignmentCommodityAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<ConsignmentCommodity> consignmentCommodityAuto(String filter) {
    return ScmLookupView.consignmentCommodityAuto(filter);
  }
  
  public void consignmentReceiptDetailPopupClose() {
    Jsf.popupReturn(parent,null);
  }
}
