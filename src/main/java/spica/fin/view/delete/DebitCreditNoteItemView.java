/*
 * @(#)DebitCreditNoteItemView.java	1.0 Tue Feb 27 12:40:23 IST 2018 
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
import spica.fin.domain.DebitCreditNoteItem;
import spica.fin.domain.TaxCode;
import spica.fin.service.DebitCreditNoteItemService;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * DebitCreditNoteItemView
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue Feb 27 12:40:23 IST 2018
 */
@Named(value = "debitCreditNoteItemView")
@ViewScoped
public class DebitCreditNoteItemView implements Serializable {

  private transient DebitCreditNoteItem debitCreditNoteItem;	//Domain object/selected Domain.
  private transient LazyDataModel<DebitCreditNoteItem> debitCreditNoteItemLazyModel; 	//For lazy loading datatable.
  private transient DebitCreditNoteItem[] debitCreditNoteItemSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public DebitCreditNoteItemView() {
    super();
  }

  /**
   * Return DebitCreditNoteItem.
   *
   * @return DebitCreditNoteItem.
   */
  public DebitCreditNoteItem getDebitCreditNoteItem() {
    if (debitCreditNoteItem == null) {
      debitCreditNoteItem = new DebitCreditNoteItem();
    }
    return debitCreditNoteItem;
  }

  /**
   * Set DebitCreditNoteItem.
   *
   * @param debitCreditNoteItem.
   */
  public void setDebitCreditNoteItem(DebitCreditNoteItem debitCreditNoteItem) {
    this.debitCreditNoteItem = debitCreditNoteItem;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchDebitCreditNoteItem(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getDebitCreditNoteItem().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setDebitCreditNoteItem((DebitCreditNoteItem) DebitCreditNoteItemService.selectByPk(main, getDebitCreditNoteItem()));
        } else if (main.isList()) {
          loadDebitCreditNoteItemList(main);
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
   * Create debitCreditNoteItemLazyModel.
   *
   * @param main
   */
  private void loadDebitCreditNoteItemList(final MainView main) {
    if (debitCreditNoteItemLazyModel == null) {
      debitCreditNoteItemLazyModel = new LazyDataModel<DebitCreditNoteItem>() {
        private List<DebitCreditNoteItem> list;

        @Override
        public List<DebitCreditNoteItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = DebitCreditNoteItemService.listPaged(main);
            main.commit(debitCreditNoteItemLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(DebitCreditNoteItem debitCreditNoteItem) {
          return debitCreditNoteItem.getId();
        }

        @Override
        public DebitCreditNoteItem getRowData(String rowKey) {
          if (list != null) {
            for (DebitCreditNoteItem obj : list) {
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
    String SUB_FOLDER = "fin_debit_credit_note_item/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveDebitCreditNoteItem(MainView main) {
    return saveOrCloneDebitCreditNoteItem(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneDebitCreditNoteItem(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneDebitCreditNoteItem(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneDebitCreditNoteItem(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            DebitCreditNoteItemService.insertOrUpdate(main, getDebitCreditNoteItem());
            break;
          case "clone":
            DebitCreditNoteItemService.clone(main, getDebitCreditNoteItem());
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
   * Delete one or many DebitCreditNoteItem.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteDebitCreditNoteItem(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(debitCreditNoteItemSelected)) {
        DebitCreditNoteItemService.deleteByPkArray(main, getDebitCreditNoteItemSelected()); //many record delete from list
        main.commit("success.delete");
        debitCreditNoteItemSelected = null;
      } else {
        DebitCreditNoteItemService.deleteByPk(main, getDebitCreditNoteItem());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (main.isEdit()) {
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
   * Return LazyDataModel of DebitCreditNoteItem.
   *
   * @return
   */
  public LazyDataModel<DebitCreditNoteItem> getDebitCreditNoteItemLazyModel() {
    return debitCreditNoteItemLazyModel;
  }

  /**
   * Return DebitCreditNoteItem[].
   *
   * @return
   */
  public DebitCreditNoteItem[] getDebitCreditNoteItemSelected() {
    return debitCreditNoteItemSelected;
  }

  /**
   * Set DebitCreditNoteItem[].
   *
   * @param debitCreditNoteItemSelected
   */
  public void setDebitCreditNoteItemSelected(DebitCreditNoteItem[] debitCreditNoteItemSelected) {
    this.debitCreditNoteItemSelected = debitCreditNoteItemSelected;
  }

  /**
   * DebitCreditNote autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.debitCreditNoteAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.debitCreditNoteAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<DebitCreditNote> debitCreditNoteAuto(String filter) {
    return null;//ScmLookupView.debitCreditNoteAuto(filter);
  }

  /**
   * ScmTaxCode autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.scmTaxCodeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.scmTaxCodeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<TaxCode> scmTaxCodeAuto(String filter) {
    return null;//ScmLookupView.scmTaxCodeAuto(filter);
  }
}
