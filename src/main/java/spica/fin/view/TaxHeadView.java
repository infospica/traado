/*
 * @(#)TaxHeadView.java	1.0 Sat Jul 22 17:33:03 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.SelectItem;
import spica.scm.domain.CountryTaxRegime;
import spica.scm.domain.Status;
import spica.fin.domain.TaxHead;
import spica.scm.service.CountryTaxRegimeService;
import spica.fin.service.TaxHeadService;
import spica.scm.util.CountryTaxRegimeUtil;
import spica.scm.view.ScmLookupView;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * TaxHeadView
 *
 * @author	Spirit 1.2
 * @version	1.0, Sat Jul 22 17:33:03 IST 2017
 */
@Named(value = "taxHeadView")
@ViewScoped
public class TaxHeadView implements Serializable {

  private transient TaxHead taxHead;	//Domain object/selected Domain.
  private transient LazyDataModel<TaxHead> taxHeadLazyModel; 	//For lazy loading datatable.
  private transient TaxHead[] taxHeadSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public TaxHeadView() {
    super();
  }

  /**
   * Return TaxHead.
   *
   * @return TaxHead.
   */
  public TaxHead getTaxHead() {
    if (taxHead == null) {
      taxHead = new TaxHead();
    }
    return taxHead;
  }

  /**
   * Set taxHead.
   *
   * @param taxHead.
   */
  public void setTaxHead(TaxHead taxHead) {
    this.taxHead = taxHead;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchTaxHead(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getTaxHead().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setTaxHead((TaxHead) TaxHeadService.selectByPk(main, getTaxHead()));
        } else if (main.isList()) {
          loadTaxHeadList(main);
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
   * Create taxHeadLazyModel.
   *
   * @param main
   */
  private void loadTaxHeadList(final MainView main) {
    if (taxHeadLazyModel == null) {
      taxHeadLazyModel = new LazyDataModel<TaxHead>() {
        private List<TaxHead> list;

        @Override
        public List<TaxHead> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = TaxHeadService.listPaged(main);
            main.commit(taxHeadLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(TaxHead taxHead) {
          return taxHead.getId();
        }

        @Override
        public TaxHead getRowData(String rowKey) {
          if (list != null) {
            for (TaxHead obj : list) {
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
    String SUB_FOLDER = "tax_head/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveTaxHead(MainView main) {
    return saveOrCloneTaxHead(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTaxHead(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneTaxHead(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTaxHead(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            TaxHeadService.insertOrUpdate(main, getTaxHead());
            break;
          case "clone":
            TaxHeadService.clone(main, getTaxHead());
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
   * Delete one or many TaxHead.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTaxHead(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(taxHeadSelected)) {
        TaxHeadService.deleteByPkArray(main, getTaxHeadSelected()); //many record delete from list
        main.commit("success.delete");
        taxHeadSelected = null;
      } else {
        TaxHeadService.deleteByPk(main, getTaxHead());  //individual record delete from list or edit form
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
   * Return LazyDataModel of TaxHead.
   *
   * @return
   */
  public LazyDataModel<TaxHead> getTaxHeadLazyModel() {
    return taxHeadLazyModel;
  }

  /**
   * Return TaxHead[].
   *
   * @return
   */
  public TaxHead[] getTaxHeadSelected() {
    return taxHeadSelected;
  }

  /**
   * Set taxHead[].
   *
   * @param taxHeadSelected
   */
  public void setTaxHeadSelected(TaxHead[] taxHeadSelected) {
    this.taxHeadSelected = taxHeadSelected;
  }

  /**
   * ScmCountryTax autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.countryTaxAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.countryTaxAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<CountryTaxRegime> countryTaxAuto(String filter) {
    return ScmLookupView.countryTaxRegimeAuto(filter);
  }

  /**
   * ScmStatus autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.statusAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.statusAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }

  /**
   * Method to get the list of Product Tax Applicable attributes.
   *
   * For Example, Product Category, Product Category and HSN & SAC.
   *
   * @return
   */
  public List<SelectItem> selectAppliedOn() {
    int index = 1;
    List<SelectItem> selectItemList = new ArrayList<>();
    SelectItem item = null;
    for (String regime : TaxHeadService.APPLIED_ON) {
      if (CountryTaxRegimeUtil.isGstRegime(UserRuntimeView.instance().getCompany())) {
        if (index == TaxHeadService.HSN_AND_SAC) {
          item = new SelectItem(regime, index);
          selectItemList.add(item);
        }
      } else if (CountryTaxRegimeUtil.isVatRegime(UserRuntimeView.instance().getCompany())) {
        if (index != TaxHeadService.HSN_AND_SAC) {
          item = new SelectItem(regime, index);
          selectItemList.add(item);
        }
      }
      index++;
    }
    return selectItemList;
  }

  /**
   * Method to get the list of Tax Applicable Computed Values.
   *
   * Eg. MRP, Goods Value, Net Value, Tax
   *
   * @return
   */
  public List<SelectItem> selectComputedOn() {
    int index = 1;
    List<SelectItem> selectItemList = new ArrayList<>();
    SelectItem item = null;
    for (String regime : TaxHeadService.COMPUTED_ON) {
      item = new SelectItem(regime, index);
      selectItemList.add(item);
      index++;
    }
    return selectItemList;
  }

  /**
   *
   * @param regimeId
   * @return
   */
  public String displayRegime(Integer regimeId) {
    String regime = "";
    if (regimeId != null) {
      regime = CountryTaxRegimeService.REGIME[regimeId - 1];
    }
    return regime;
  }

  /**
   *
   * @param applicableId
   * @return
   */
  public String displayAppliedOn(Integer applicableId) {
    String appliedOn = "";
    if (applicableId != null) {
      appliedOn = TaxHeadService.APPLIED_ON[applicableId - 1];
    }
    return appliedOn;
  }

  /**
   *
   * @param computedId
   * @return
   */
  public String displayComputedOn(Integer computedId) {
    String computedOn = "";
    if (computedId != null) {
      computedOn = TaxHeadService.COMPUTED_ON[computedId - 1];
    }
    return computedOn;
  }
}
