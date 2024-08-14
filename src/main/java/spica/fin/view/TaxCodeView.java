/*
 * @(#)TaxCodeView.java	1.0 Mon Aug 08 17:59:15 IST 2016 
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
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.domain.Country;
import spica.scm.domain.State;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.fin.domain.TaxCode;
import spica.fin.service.TaxCodeService;
import spica.scm.domain.Status;
import spica.fin.domain.TaxHead;
import spica.scm.view.ScmLookupExtView;
import spica.scm.view.ScmLookupView;
import spica.sys.UserRuntimeView;

/**
 * TaxCodeView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 08 17:59:15 IST 2016
 */
@Named(value = "taxCodeView")
@ViewScoped
public class TaxCodeView implements Serializable {

  private transient TaxCode taxCode;	//Domain object/selected Domain.
  private transient LazyDataModel<TaxCode> taxCodeLazyModel; 	//For lazy loading datatable.
  private transient TaxCode[] taxCodeSelected;	 //Selected Domain Array
  private transient Country country;

  /**
   * Default Constructor.
   */
  public TaxCodeView() {
    super();
  }

  /**
   * Return TaxCode.
   *
   * @return TaxCode.
   */
  public TaxCode getTaxCode() {
    if (taxCode == null) {
      taxCode = new TaxCode();
    }
    if (taxCode.getCompanyId() == null) {
      taxCode.setCompanyId(UserRuntimeView.instance().getCompany());
      taxCode.setCountryId(taxCode.getCompanyId().getCountryId());
    }
    return taxCode;
  }

  /**
   * Set TaxCode.
   *
   * @param taxCode.
   */
  public void setTaxCode(TaxCode taxCode) {
    this.taxCode = taxCode;
  }

  public Country getCountry() {
    return country;
  }

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
  public String switchTaxCode(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          getTaxCode().reset();
//          if (getTaxCode().getCompanyId() == null) {
//            main.error("company.required");
//            main.setViewType(ViewType.list.toString());
//          }

        } else if (main.isEdit() && !main.hasError()) {
          setTaxCode((TaxCode) TaxCodeService.selectByPk(main, getTaxCode()));
          setCountry(getTaxCode().getTaxHeadId().getCountryTaxRegimeId().getCountryId());
        } else if (main.isList()) {
          setCountry(null);
          getTaxCode().setCompanyId(null);
          loadTaxCodeList(main);
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
   * Create taxCodeLazyModel.
   *
   * @param main
   */
  private void loadTaxCodeList(final MainView main) {
    if (taxCodeLazyModel == null) {
      taxCodeLazyModel = new LazyDataModel<TaxCode>() {
        private List<TaxCode> list;

        @Override
        public List<TaxCode> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = TaxCodeService.listPaged(main, getTaxCode().getCompanyId());
            main.commit(taxCodeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(TaxCode taxCode) {
          return taxCode.getId();
        }

        @Override
        public TaxCode getRowData(String rowKey) {
          if (list != null) {
            for (TaxCode obj : list) {
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
    String SUB_FOLDER = "scm_tax_code/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveTaxCode(MainView main) {
    return saveOrCloneTaxCode(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneTaxCode(MainView main) {
    main.setViewType("newform");
    return saveOrCloneTaxCode(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneTaxCode(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            TaxCodeService.insertOrUpdate(main, getTaxCode());
            break;
          case "clone":
            TaxCodeService.clone(main, getTaxCode());
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
   * Delete one or many TaxCode.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteTaxCode(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(taxCodeSelected)) {
        TaxCodeService.deleteByPkArray(main, getTaxCodeSelected()); //many record delete from list
        main.commit("success.delete");
        taxCodeSelected = null;
      } else {
        TaxCodeService.deleteByPk(main, getTaxCode());  //individual record delete from list or edit form
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
   * Return LazyDataModel of TaxCode.
   *
   * @return
   */
  public LazyDataModel<TaxCode> getTaxCodeLazyModel() {
    return taxCodeLazyModel;
  }

  /**
   * Return TaxCode[].
   *
   * @return
   */
  public TaxCode[] getTaxCodeSelected() {
    return taxCodeSelected;
  }

  /**
   * Set TaxCode[].
   *
   * @param taxCodeSelected
   */
  public void setTaxCodeSelected(TaxCode[] taxCodeSelected) {
    this.taxCodeSelected = taxCodeSelected;
  }

  /**
   * Status autocomplete filter.
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
   * State autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.stateAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.stateAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<State> stateAuto(String filter) {
    if (getCountry() != null) {
      return ScmLookupExtView.addressStateAuto(getCountry().getId(), filter);
    }
    return null;
  }

  public List<TaxHead> taxHeadAuto() {
    List<TaxHead> list = ScmLookupExtView.selectTaxHeadByRegime(getTaxCode().getCompanyId().getCountryTaxRegimeId());
    if (list != null && list.size() == 1) {
      getTaxCode().setTaxHeadId(list.get(0));
      setTaxHeadDetails(getTaxCode().getTaxHeadId());
    }
    return list;
  }

  public void taxHeadSelectEvent(SelectEvent event) {
    TaxHead taxHead = (TaxHead) event.getObject();
    setTaxHeadDetails(taxHead);
  }

  /**
   *
   * @param taxHead
   */
  private void setTaxHeadDetails(TaxHead taxHead) {
    if (taxHead != null) {
      setCountry(taxHead.getCountryTaxRegimeId().getCountryId());
      getTaxCode().setAbatementPercentage(taxHead.getAbatementPercentage());
    }
  }
}
