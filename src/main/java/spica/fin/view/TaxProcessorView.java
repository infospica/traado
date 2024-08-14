/*
 * @(#)TaxProcessorView.java	1.0 Mon Jul 24 18:05:03 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.fin.domain.TaxProcessor;
import spica.fin.service.TaxProcessorService;
import spica.scm.domain.Status;
import spica.scm.view.ScmLookupView;

/**
 * TaxProcessorView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Jul 24 18:05:03 IST 2017
 */
@Named(value = "taxProcessorView")
@ViewScoped
public class TaxProcessorView implements Serializable {

  private transient TaxProcessor taxProcessor;	//Domain object/selected Domain.
  private transient LazyDataModel<TaxProcessor> taxProcessorLazyModel; 	//For lazy loading datatable.
  private transient TaxProcessor[] taxProcessorSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public TaxProcessorView() {
    super();
  }

  /**
   * Return TaxProcessor.
   *
   * @return TaxProcessor.
   */
  public TaxProcessor getTaxProcessor() {
    if (taxProcessor == null) {
      taxProcessor = new TaxProcessor();
    }
    return taxProcessor;
  }

  /**
   * Set TaxProcessor.
   *
   * @param taxProcessor.
   */
  public void setTaxProcessor(TaxProcessor taxProcessor) {
    this.taxProcessor = taxProcessor;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchTaxProcessor(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getTaxProcessor().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setTaxProcessor((TaxProcessor) TaxProcessorService.selectByPk(main, getTaxProcessor()));
        } else if (main.isList()) {
          loadTaxProcessorList(main);
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
   * Create taxProcessorLazyModel.
   *
   * @param main
   */
  private void loadTaxProcessorList(final MainView main) {
    if (taxProcessorLazyModel == null) {
      taxProcessorLazyModel = new LazyDataModel<TaxProcessor>() {
        private List<TaxProcessor> list;

        @Override
        public List<TaxProcessor> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = TaxProcessorService.listPaged(main);
            main.commit(taxProcessorLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(TaxProcessor taxProcessor) {
          return taxProcessor.getId();
        }

        @Override
        public TaxProcessor getRowData(String rowKey) {
          if (list != null) {
            for (TaxProcessor obj : list) {
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
    String SUB_FOLDER = "scm_tax_processor/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveTaxProcessor(MainView main) {
    return saveOrCloneTaxProcessor(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTaxProcessor(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneTaxProcessor(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTaxProcessor(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            TaxProcessorService.insertOrUpdate(main, getTaxProcessor());
            break;
          case "clone":
            TaxProcessorService.clone(main, getTaxProcessor());
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
   * Delete one or many TaxProcessor.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTaxProcessor(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(taxProcessorSelected)) {
        TaxProcessorService.deleteByPkArray(main, getTaxProcessorSelected()); //many record delete from list
        main.commit("success.delete");
        taxProcessorSelected = null;
      } else {
        TaxProcessorService.deleteByPk(main, getTaxProcessor());  //individual record delete from list or edit form
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
   * Return LazyDataModel of TaxProcessor.
   *
   * @return
   */
  public LazyDataModel<TaxProcessor> getTaxProcessorLazyModel() {
    return taxProcessorLazyModel;
  }

  /**
   * Return TaxProcessor[].
   *
   * @return
   */
  public TaxProcessor[] getTaxProcessorSelected() {
    return taxProcessorSelected;
  }

  /**
   * Set TaxProcessor[].
   *
   * @param taxProcessorSelected
   */
  public void setTaxProcessorSelected(TaxProcessor[] taxProcessorSelected) {
    this.taxProcessorSelected = taxProcessorSelected;
  }

  /**
   * CountryTaxRegime autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.countryTaxRegimeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.countryTaxRegimeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }
}
