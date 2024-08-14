/*
 * @(#)CountryView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
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
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.Country;
import spica.scm.domain.Currency;
import spica.scm.service.CountryService;
import spica.scm.service.CurrencyService;
import spica.sys.UserRuntimeView;

/**
 * CountryView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "countryView")
@ViewScoped
public class CountryView implements Serializable {

  private transient Country country;	//Domain object/selected Domain.
  private transient LazyDataModel<Country> countryLazyModel; 	//For lazy loading datatable.
  private transient Country[] countrySelected;	 //Selected Domain Array
  private transient List<Currency> currencyList;

  /**
   * Default Constructor.
   */
  public CountryView() {
    super();
  }

  /**
   * Return Country.
   *
   * @return Country.
   */
  public Country getCountry() {
    if (country == null) {
      country = new Country();
    }
    return country;
  }

  /**
   * Set Country.
   *
   * @param country.
   */
  public void setCountry(Country country) {
    this.country = country;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCountry(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getCountry().reset();
          loadCurrency(main);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCountry((Country) CountryService.selectByPk(main, getCountry()));
          loadCurrency(main);
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCountryList(main);
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
   * Create countryLazyModel.
   *
   * @param main
   */
  private void loadCountryList(final MainView main) {
    if (countryLazyModel == null) {
      countryLazyModel = new LazyDataModel<Country>() {
        private List<Country> list;

        @Override
        public List<Country> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CountryService.listPaged(main);
            main.commit(countryLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Country country) {
          return country.getId();
        }

        @Override
        public Country getRowData(String rowKey) {
          if (list != null) {
            for (Country obj : list) {
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
    String SUB_FOLDER = "scm_country/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCountry(MainView main) {
    return saveOrCloneCountry(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCountry(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCountry(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCountry(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CountryService.insertOrUpdate(main, getCountry());
            break;
          case "clone":
            CountryService.clone(main, getCountry());
            break;
        }
        UserRuntimeView.instance().getCompany().setCountryId(getCountry());
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
   * Delete one or many Country.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCountry(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(countrySelected)) {
        CountryService.deleteByPkArray(main, getCountrySelected()); //many record delete from list
        main.commit("success.delete");
        countrySelected = null;
      } else {
        CountryService.deleteByPk(main, getCountry());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())) {
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
   * Return LazyDataModel of Country.
   *
   * @return
   */
  public LazyDataModel<Country> getCountryLazyModel() {
    return countryLazyModel;
  }

  /**
   * Return Country[].
   *
   * @return
   */
  public Country[] getCountrySelected() {
    return countrySelected;
  }

  /**
   * Set Country[].
   *
   * @param countrySelected
   */
  public void setCountrySelected(Country[] countrySelected) {
    this.countrySelected = countrySelected;
  }

  public void loadCurrency(MainView main) {
    currencyList = CurrencyService.loadCurrencyList(main);
  }

  public List<Currency> getCurrencyList() {
    return currencyList;
  }

}
