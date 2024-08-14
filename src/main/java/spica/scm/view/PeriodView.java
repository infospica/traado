/*
 * @(#)PeriodView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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

import spica.scm.domain.Period;
import spica.scm.service.PeriodService;

/**
 * PeriodView
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016 
 */

@Named(value="periodView")
@ViewScoped
public class PeriodView implements Serializable{

  private transient Period period;	//Domain object/selected Domain.
  private transient LazyDataModel<Period> periodLazyModel; 	//For lazy loading datatable.
  private transient Period[] periodSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public PeriodView() {
    super();
  }
 
  /**
   * Return Period.
   * @return Period.
   */  
  public Period getPeriod() {
    if(period == null) {
      period = new Period();
    }
    return period;
  }   
  
  /**
   * Set Period.
   * @param period.
   */   
  public void setPeriod(Period period) {
    this.period = period;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchPeriod(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getPeriod().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setPeriod((Period) PeriodService.selectByPk(main, getPeriod()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadPeriodList(main);
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
   * Create periodLazyModel.
   * @param main
   */
  private void loadPeriodList(final MainView main) {
    if (periodLazyModel == null) {
      periodLazyModel = new LazyDataModel<Period>() {
      private List<Period> list;      
      @Override
      public List<Period> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = PeriodService.listPaged(main);
          main.commit(periodLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(Period period) {
        return period.getId();
      }
      @Override
        public Period getRowData(String rowKey) {
          if (list != null) {
            for (Period obj : list) {
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
    String SUB_FOLDER = "scm_period/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String savePeriod(MainView main) {
    return saveOrClonePeriod(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePeriod(MainView main) {
    main.setViewType("newform");
    return saveOrClonePeriod(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePeriod(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PeriodService.insertOrUpdate(main, getPeriod());
            break;
          case "clone":
            PeriodService.clone(main, getPeriod());
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
   * Delete one or many Period.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePeriod(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(periodSelected)) {
        PeriodService.deleteByPkArray(main, getPeriodSelected()); //many record delete from list
        main.commit("success.delete");
        periodSelected = null;
      } else {
        PeriodService.deleteByPk(main, getPeriod());  //individual record delete from list or edit form
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
   * Return LazyDataModel of Period.
   * @return
   */
  public LazyDataModel<Period> getPeriodLazyModel() {
    return periodLazyModel;
  }

 /**
  * Return Period[].
  * @return 
  */
  public Period[] getPeriodSelected() {
    return periodSelected;
  }
  
  /**
   * Set Period[].
   * @param periodSelected 
   */
  public void setPeriodSelected(Period[] periodSelected) {
    this.periodSelected = periodSelected;
  }
 


}
