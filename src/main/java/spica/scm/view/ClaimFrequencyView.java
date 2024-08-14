/*
 * @(#)ClaimFrequencyView.java	1.0 Fri Jun 17 17:57:40 IST 2016 
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

import spica.scm.domain.ClaimFrequency;
import spica.scm.service.ClaimFrequencyService;

/**
 * ClaimFrequencyView
 * @author	Spirit 1.2
 * @version	1.0, Fri Jun 17 17:57:40 IST 2016 
 */

@Named(value="claimFrequencyView")
@ViewScoped
public class ClaimFrequencyView implements Serializable{

  private transient ClaimFrequency claimFrequency;	//Domain object/selected Domain.
  private transient LazyDataModel<ClaimFrequency> claimFrequencyLazyModel; 	//For lazy loading datatable.
  private transient ClaimFrequency[] claimFrequencySelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public ClaimFrequencyView() {
    super();
  }
 
  /**
   * Return ClaimFrequency.
   * @return ClaimFrequency.
   */  
  public ClaimFrequency getClaimFrequency() {
    if(claimFrequency == null) {
      claimFrequency = new ClaimFrequency();
    }
    return claimFrequency;
  }   
  
  /**
   * Set ClaimFrequency.
   * @param claimFrequency.
   */   
  public void setClaimFrequency(ClaimFrequency claimFrequency) {
    this.claimFrequency = claimFrequency;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchClaimFrequency(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getClaimFrequency().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setClaimFrequency((ClaimFrequency) ClaimFrequencyService.selectByPk(main, getClaimFrequency()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadClaimFrequencyList(main);
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
   * Create claimFrequencyLazyModel.
   * @param main
   */
  private void loadClaimFrequencyList(final MainView main) {
    if (claimFrequencyLazyModel == null) {
      claimFrequencyLazyModel = new LazyDataModel<ClaimFrequency>() {
      private List<ClaimFrequency> list;      
      @Override
      public List<ClaimFrequency> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = ClaimFrequencyService.listPaged(main);
          main.commit(claimFrequencyLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(ClaimFrequency claimFrequency) {
        return claimFrequency.getId();
      }
      @Override
        public ClaimFrequency getRowData(String rowKey) {
          if (list != null) {
            for (ClaimFrequency obj : list) {
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
    String SUB_FOLDER = "scm_claim_frequency/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveClaimFrequency(MainView main) {
    return saveOrCloneClaimFrequency(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneClaimFrequency(MainView main) {
    main.setViewType("newform");
    return saveOrCloneClaimFrequency(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneClaimFrequency(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ClaimFrequencyService.insertOrUpdate(main, getClaimFrequency());
            break;
          case "clone":
            ClaimFrequencyService.clone(main, getClaimFrequency());
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
   * Delete one or many ClaimFrequency.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteClaimFrequency(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(claimFrequencySelected)) {
        ClaimFrequencyService.deleteByPkArray(main, getClaimFrequencySelected()); //many record delete from list
        main.commit("success.delete");
        claimFrequencySelected = null;
      } else {
        ClaimFrequencyService.deleteByPk(main, getClaimFrequency());  //individual record delete from list or edit form
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
   * Return LazyDataModel of ClaimFrequency.
   * @return
   */
  public LazyDataModel<ClaimFrequency> getClaimFrequencyLazyModel() {
    return claimFrequencyLazyModel;
  }

 /**
  * Return ClaimFrequency[].
  * @return 
  */
  public ClaimFrequency[] getClaimFrequencySelected() {
    return claimFrequencySelected;
  }
  
  /**
   * Set ClaimFrequency[].
   * @param claimFrequencySelected 
   */
  public void setClaimFrequencySelected(ClaimFrequency[] claimFrequencySelected) {
    this.claimFrequencySelected = claimFrequencySelected;
  }
 


}
