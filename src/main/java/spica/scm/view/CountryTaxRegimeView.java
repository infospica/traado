/*
 * @(#)CountryTaxView.java	1.0 Tue Jul 04 14:43:25 IST 2017 
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
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.SelectItem;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.Country;
import spica.scm.domain.CountryTaxRegime;
import spica.fin.domain.TaxProcessor;
import spica.scm.service.CountryTaxRegimeService;

/**
 * CountryTaxView
 *
 * @author	Spirit 1.2
 * @version	1.0, Tue Jul 04 14:43:25 IST 2017
 */
@Named(value = "countryTaxRegimeView")
@ViewScoped
public class CountryTaxRegimeView implements Serializable {

  private transient CountryTaxRegime countryTaxRegime;	//Domain object/selected Domain.
  private transient LazyDataModel<CountryTaxRegime> countryTaxRegimeLazyModel; 	//For lazy loading datatable.
  private transient CountryTaxRegime[] countryTaxRegimeSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public CountryTaxRegimeView() {
    super();
  }

  /**
   * Return CountryTaxRegime.
   *
   * @return CountryTaxRegime.
   */
  public CountryTaxRegime getCountryTaxRegime() {
    if (countryTaxRegime == null) {
      countryTaxRegime = new CountryTaxRegime();
    }
    return countryTaxRegime;
  }

  /**
   * Set CountryTaxRegime.
   *
   * @param countryTax.
   */
  public void setCountryTaxRegime(CountryTaxRegime countryTaxRegime) {
    this.countryTaxRegime = countryTaxRegime;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCountryTax(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getCountryTaxRegime().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setCountryTaxRegime((CountryTaxRegime) CountryTaxRegimeService.selectByPk(main, getCountryTaxRegime()));
        } else if (main.isList()) {
          loadCountryTaxList(main);
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
   * Create countryTaxRegimeLazyModel.
   *
   * @param main
   */
  private void loadCountryTaxList(final MainView main) {
    if (countryTaxRegimeLazyModel == null) {
      countryTaxRegimeLazyModel = new LazyDataModel<CountryTaxRegime>() {
        private List<CountryTaxRegime> list;

        @Override
        public List<CountryTaxRegime> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CountryTaxRegimeService.listPaged(main);
            main.commit(countryTaxRegimeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CountryTaxRegime countryTaxRegime) {
          return countryTaxRegime.getId();
        }

        @Override
        public CountryTaxRegime getRowData(String rowKey) {
          if (list != null) {
            for (CountryTaxRegime obj : list) {
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
    String SUB_FOLDER = "scm_country_tax/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCountryTax(MainView main) {
    return saveOrCloneCountryTax(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCountryTax(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneCountryTax(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCountryTax(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CountryTaxRegimeService.insertOrUpdate(main, getCountryTaxRegime());
            break;
          case "clone":
            CountryTaxRegimeService.clone(main, getCountryTaxRegime());
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
   * Delete one or many CountryTaxRegime.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCountryTax(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(countryTaxRegimeSelected)) {
        CountryTaxRegimeService.deleteByPkArray(main, getCountryTaxRegimeSelected()); //many record delete from list
        main.commit("success.delete");
        countryTaxRegimeSelected = null;
      } else {
        CountryTaxRegimeService.deleteByPk(main, getCountryTaxRegime());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CountryTaxRegime.
   *
   * @return
   */
  public LazyDataModel<CountryTaxRegime> getCountryTaxRegimeLazyModel() {
    return countryTaxRegimeLazyModel;
  }

  /**
   * Return CountryTaxRegime[].
   *
   * @return
   */
  public CountryTaxRegime[] getCountryTaxRegimeSelected() {
    return countryTaxRegimeSelected;
  }

  /**
   * Set CountryTaxRegime[].
   *
   * @param countryTaxSelected
   */
  public void setCountryTaxRegimeSelected(CountryTaxRegime[] countryTaxRegimeSelected) {
    this.countryTaxRegimeSelected = countryTaxRegimeSelected;
  }

  /**
   * Country autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.countryAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.countryAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Country> countryAuto(String filter) {
    return ScmLookupView.countryAuto(filter);
  }

  public List<TaxProcessor> taxProcessorAuto(String filter) {
    return ScmLookupView.taxProcessorAuto(filter);
  }

  /**
   * Method to get the list available regime.
   *
   * @return
   */
  public List<SelectItem> selectRegime() {
    int index = 1;
    List<SelectItem> selectItemList = new ArrayList<>();
    SelectItem item = null;
    for (String regime : CountryTaxRegimeService.REGIME) {
      item = new SelectItem(regime, index);
      selectItemList.add(item);
      index++;
    }
    return selectItemList;
  }

  public String displayRegime(Integer regimeId) {
    String regime = "";
    if (regimeId != null) {
      regime = CountryTaxRegimeService.REGIME[regimeId - 1];
    }
    return regime;
  }

}
