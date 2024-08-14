/*
 * @(#)SecPrivilageView.java	1.0 Fri Feb 03 19:15:30 IST 2017 
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

import spica.sys.domain.Privilage;
import spica.scm.service.PrivilageService;

/**
 * SecPrivilageView
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 03 19:15:30 IST 2017 
 */

@Named(value="PrivilageView")
@ViewScoped
public class PrivilageView implements Serializable{

  private transient Privilage Privilage;	//Domain object/selected Domain.
  private transient LazyDataModel<Privilage> PrivilageLazyModel; 	//For lazy loading datatable.
  private transient Privilage[] PrivilageSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public PrivilageView() {
    super();
  }
 
  /**
   * Return Privilage.
   * @return Privilage.
   */  
  public Privilage getPrivilage() {
    if(Privilage == null) {
      Privilage = new Privilage();
    }
    return Privilage;
  }   
  
  /**
   * Set Privilage.
   * @param Privilage.
   */   
  public void setPrivilage(Privilage Privilage) {
    this.Privilage = Privilage;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchPrivilage(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getPrivilage().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setPrivilage((Privilage) PrivilageService.selectByPk(main, getPrivilage()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadPrivilageList(main);
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
   * Create secPrivilageLazyModel.
   * @param main
   */
  private void loadPrivilageList(final MainView main) {
    if (PrivilageLazyModel == null) {
      PrivilageLazyModel = new LazyDataModel<Privilage>() {
      private List<Privilage> list;      
      @Override
      public List<Privilage> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = PrivilageService.listPaged(main);
          main.commit(PrivilageLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(Privilage Privilage) {
        return Privilage.getId();
      }
      @Override
        public Privilage getRowData(String rowKey) {
          if (list != null) {
            for (Privilage obj : list) {
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
    String SUB_FOLDER = "sec_privilage/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String savePrivilage(MainView main) {
    return saveOrClonePrivilage(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePrivilage(MainView main) {
    main.setViewType("newform");
    return saveOrClonePrivilage(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePrivilage(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PrivilageService.insertOrUpdate(main, getPrivilage());
            break;
          case "clone":
            PrivilageService.clone(main, getPrivilage());
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
   * Delete one or many Privilage.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePrivilage(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(PrivilageSelected)) {
        PrivilageService.deleteByPkArray(main, getPrivilageSelected()); //many record delete from list
        main.commit("success.delete");
        PrivilageSelected = null;
      } else {
        PrivilageService.deleteByPk(main, getPrivilage());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())){
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
   * Return LazyDataModel of Privilage.
   * @return
   */
  public LazyDataModel<Privilage> getPrivilageLazyModel() {
    return PrivilageLazyModel;
  }

 /**
  * Return Privilage[].
  * @return 
  */
  public Privilage[] getPrivilageSelected() {
    return PrivilageSelected;
  }
  
  /**
   * Set SecPrivilage[].
   * @param secPrivilageSelected 
   */
  public void setPrivilageSelected(Privilage[] PrivilageSelected) {
    this.PrivilageSelected = PrivilageSelected;
  }
 


}
