/*
 * @(#)CurrencyView.java	1.0 Fri Apr 28 12:24:49 IST 2017 
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
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.Currency;
import spica.scm.service.CurrencyService;

/**
 * CurrencyView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Apr 28 12:24:49 IST 2017
 */
@Named(value = "currencyView")
@ViewScoped
public class CurrencyView implements Serializable {

  private transient Currency currency;	//Domain object/selected Domain.
  private transient LazyDataModel<Currency> currencyLazyModel; 	//For lazy loading datatable.
  private transient Currency[] currencySelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public CurrencyView() {
    super();
  }

  /**
   * Return Currency.
   *
   * @return Currency.
   */
  public Currency getCurrency() {
    if (currency == null) {
      currency = new Currency();
    }
    return currency;
  }

  /**
   * Set Currency.
   *
   * @param currency.
   */
  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCurrency(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getCurrency().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setCurrency((Currency) CurrencyService.selectByPk(main, getCurrency()));
        } else if (ViewTypes.isList(viewType)) {
          loadCurrencyList(main);
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
   * Create currencyLazyModel.
   *
   * @param main
   */
  private void loadCurrencyList(final MainView main) {
    if (currencyLazyModel == null) {
      currencyLazyModel = new LazyDataModel<Currency>() {
        private List<Currency> list;

        @Override
        public List<Currency> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CurrencyService.listPaged(main);
            main.commit(currencyLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Currency currency) {
          return currency.getId();
        }

        @Override
        public Currency getRowData(String rowKey) {
          if (list != null) {
            for (Currency obj : list) {
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
    String SUB_FOLDER = "fin_currency/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCurrency(MainView main) {
    return saveOrCloneCurrency(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCurrency(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCurrency(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCurrency(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CurrencyService.insertOrUpdate(main, getCurrency());
            break;
          case "clone":
            CurrencyService.clone(main, getCurrency());
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
   * Delete one or many Currency.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCurrency(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(currencySelected)) {
        CurrencyService.deleteByPkArray(main, getCurrencySelected()); //many record delete from list
        main.commit("success.delete");
        currencySelected = null;
      } else {
        CurrencyService.deleteByPk(main, getCurrency());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (ViewTypes.isEdit(main.getViewType())) {
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
   * Return LazyDataModel of Currency.
   *
   * @return
   */
  public LazyDataModel<Currency> getCurrencyLazyModel() {
    return currencyLazyModel;
  }

  /**
   * Return Currency[].
   *
   * @return
   */
  public Currency[] getCurrencySelected() {
    return currencySelected;
  }

  /**
   * Set Currency[].
   *
   * @param currencySelected
   */
  public void setCurrencySelected(Currency[] currencySelected) {
    this.currencySelected = currencySelected;
  }

}
