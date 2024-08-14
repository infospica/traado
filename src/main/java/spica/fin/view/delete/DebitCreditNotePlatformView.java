/*
 * @(#)DebitCreditNotePlatformView.java	1.0 Tue Feb 27 12:40:23 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */

package spica.fin.view.delete;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.fin.domain.DebitCreditNote;
import spica.fin.domain.DebitCreditNotePlatform;
import spica.fin.service.delete.DebitCreditNotePlatformService;
import spica.scm.domain.Platform;
import spica.scm.view.ScmLookupView;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;


/**
 * DebitCreditNotePlatformView
 * @author	Spirit 1.2
 * @version	1.0, Tue Feb 27 12:40:23 IST 2018 
 */

@Named(value="debitCreditNotePlatformView")
@ViewScoped
public class DebitCreditNotePlatformView implements Serializable{

  private transient DebitCreditNotePlatform debitCreditNotePlatform;	//Domain object/selected Domain.
  private transient LazyDataModel<DebitCreditNotePlatform> debitCreditNotePlatformLazyModel; 	//For lazy loading datatable.
  private transient DebitCreditNotePlatform[] debitCreditNotePlatformSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public DebitCreditNotePlatformView() {
    super();
  }
 
  /**
   * Return DebitCreditNotePlatform.
   * @return DebitCreditNotePlatform.
   */  
  public DebitCreditNotePlatform getDebitCreditNotePlatform() {
    if(debitCreditNotePlatform == null) {
      debitCreditNotePlatform = new DebitCreditNotePlatform();
    }
    return debitCreditNotePlatform;
  }   
  
  /**
   * Set DebitCreditNotePlatform.
   * @param debitCreditNotePlatform.
   */   
  public void setDebitCreditNotePlatform(DebitCreditNotePlatform debitCreditNotePlatform) {
    this.debitCreditNotePlatform = debitCreditNotePlatform;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchDebitCreditNotePlatform(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getDebitCreditNotePlatform().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setDebitCreditNotePlatform((DebitCreditNotePlatform) DebitCreditNotePlatformService.selectByPk(main, getDebitCreditNotePlatform()));
        } else if (main.isList()) {
          loadDebitCreditNotePlatformList(main);
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
   * Create debitCreditNotePlatformLazyModel.
   * @param main
   */
  private void loadDebitCreditNotePlatformList(final MainView main) {
    if (debitCreditNotePlatformLazyModel == null) {
      debitCreditNotePlatformLazyModel = new LazyDataModel<DebitCreditNotePlatform>() {
      private List<DebitCreditNotePlatform> list;      
      @Override
      public List<DebitCreditNotePlatform> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = DebitCreditNotePlatformService.listPaged(main);
          main.commit(debitCreditNotePlatformLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(DebitCreditNotePlatform debitCreditNotePlatform) {
        return debitCreditNotePlatform.getId();
      }
      @Override
        public DebitCreditNotePlatform getRowData(String rowKey) {
          if (list != null) {
            for (DebitCreditNotePlatform obj : list) {
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
    String SUB_FOLDER = "fin_debit_credit_note_platform/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveDebitCreditNotePlatform(MainView main) {
    return saveOrCloneDebitCreditNotePlatform(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneDebitCreditNotePlatform(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneDebitCreditNotePlatform(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneDebitCreditNotePlatform(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            DebitCreditNotePlatformService.insertOrUpdate(main, getDebitCreditNotePlatform());
            break;
          case "clone":
            DebitCreditNotePlatformService.clone(main, getDebitCreditNotePlatform());
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
   * Delete one or many DebitCreditNotePlatform.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteDebitCreditNotePlatform(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(debitCreditNotePlatformSelected)) {
        DebitCreditNotePlatformService.deleteByPkArray(main, getDebitCreditNotePlatformSelected()); //many record delete from list
        main.commit("success.delete");
        debitCreditNotePlatformSelected = null;
      } else {
        DebitCreditNotePlatformService.deleteByPk(main, getDebitCreditNotePlatform());  //individual record delete from list or edit form
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
   * Return LazyDataModel of DebitCreditNotePlatform.
   * @return
   */
  public LazyDataModel<DebitCreditNotePlatform> getDebitCreditNotePlatformLazyModel() {
    return debitCreditNotePlatformLazyModel;
  }

 /**
  * Return DebitCreditNotePlatform[].
  * @return 
  */
  public DebitCreditNotePlatform[] getDebitCreditNotePlatformSelected() {
    return debitCreditNotePlatformSelected;
  }
  
  /**
   * Set DebitCreditNotePlatform[].
   * @param debitCreditNotePlatformSelected 
   */
  public void setDebitCreditNotePlatformSelected(DebitCreditNotePlatform[] debitCreditNotePlatformSelected) {
    this.debitCreditNotePlatformSelected = debitCreditNotePlatformSelected;
  }
 


 /**
  * DebitCreditNote autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.debitCreditNoteAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.debitCreditNoteAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<DebitCreditNote> debitCreditNoteAuto(String filter) {
    return null;//ScmLookupView.debitCreditNoteAuto(filter);
  }
 /**
  * ScmPlatform autocomplete filter.
  * <pre>
  * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
  * If your list is smaller in size and is cached you can use.
  * <o:converter list="#{ScmLookupView.scmPlatformAuto(null)}" converterId="omnifaces.ListConverter"  />
  * Note:- ScmLookupView.scmPlatformAuto(null) Should be implemented to return full values from cache if the filter is null
  * </pre>
  * @param filter
  * @return
  */
  public List<Platform> scmPlatformAuto(String filter) {
    return null;//ScmLookupView.scmPlatformAuto(filter);
  }
}
