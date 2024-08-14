/*
 * @(#)CompanyBankView.java	1.0 Wed Mar 30 12:35:25 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.CompanyBank;
import spica.scm.service.CompanyBankService;
import spica.scm.domain.Company;
import spica.scm.domain.Account;
import spica.scm.domain.Bank;
import spica.scm.domain.CompanyBankContact;
import spica.scm.domain.Status;
import spica.scm.service.CompanyBankContactService;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * CompanyBankView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "companyBankView")
@ViewScoped
public class CompanyBankView implements Serializable {

  private transient CompanyBank companyBank;	//Domain object/selected Domain.
  private transient LazyDataModel<CompanyBank> companyBankLazyModel; 	//For lazy loading datatable.
  private transient CompanyBank[] companyBankSelected;	 //Selected Domain Array
  private int activeIndex = 0; //tab active index, reset to 0 when new records
  private List<CompanyBankContact> companyBankContactList;

  /**
   * Default Constructor.
   */
  public CompanyBankView() {
    super();
  }

  /**
   * Return CompanyBank.
   *
   * @return CompanyBank.
   */
  public CompanyBank getCompanyBank() {
    if (companyBank == null) {
      companyBank = new CompanyBank();
    }
    return companyBank;
  }

  /**
   * Set CompanyBank.
   *
   * @param companyBank.
   */
  public void setCompanyBank(CompanyBank companyBank) {
    this.companyBank = companyBank;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCompanyBank(MainView main, String viewType) {
    //this.main = main;
    companyBankContactList = null;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        getCompanyBank().setCompanyId(parent);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          activeIndex = 0;
          getCompanyBank().reset();
          getCompanyBank().setCompanyId(parent);
//          getCompanyBank().setBankAccountTypeId(BankAccountTypeService.selectBankAccountTypeBySortOrder(main));
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCompanyBank((CompanyBank) CompanyBankService.selectByPk(main, getCompanyBank()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCompanyBankList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }
  private Company parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (Company) Jsf.popupParentValue(Company.class);
    getCompanyBank().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Create companyBankLazyModel.
   *
   * @param main
   */
  private void loadCompanyBankList(final MainView main) {
    if (companyBankLazyModel == null) {
      companyBankLazyModel = new LazyDataModel<CompanyBank>() {
        private List<CompanyBank> list;

        @Override
        public List<CompanyBank> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CompanyBankService.listPaged(main);
            main.commit(companyBankLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CompanyBank companyBank) {
          return companyBank.getId();
        }

        @Override
        public CompanyBank getRowData(String rowKey) {
          if (list != null) {
            for (CompanyBank obj : list) {
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
    String SUB_FOLDER = "scm_company_bank/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCompanyBank(MainView main) {
    return saveOrCloneCompanyBank(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCompanyBank(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCompanyBank(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCompanyBank(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CompanyBankService.insertOrUpdate(main, getCompanyBank());
//            CompanyBankService.makeDefault(main, getCompanyBank());
            break;
          case "clone":
            CompanyBankService.clone(main, getCompanyBank());
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
   * Delete one or many CompanyBank.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCompanyBank(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(companyBankSelected)) {
        CompanyBankService.deleteByPkArray(main, getCompanyBankSelected()); //many record delete from list
        main.commit("success.delete");
        companyBankSelected = null;
      } else {
        CompanyBankService.deleteByPk(main, getCompanyBank());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CompanyBank.
   *
   * @return
   */
  public LazyDataModel<CompanyBank> getCompanyBankLazyModel() {
    return companyBankLazyModel;
  }

  /**
   * Return CompanyBank[].
   *
   * @return
   */
  public CompanyBank[] getCompanyBankSelected() {
    return companyBankSelected;
  }

  /**
   * Set CompanyBank[].
   *
   * @param companyBankSelected
   */
  public void setCompanyBankSelected(CompanyBank[] companyBankSelected) {
    this.companyBankSelected = companyBankSelected;
  }

  /**
   * Company autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.companyAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.companyAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Company> companyAuto(String filter) {
    return ScmLookupView.companyAuto(filter);
  }

  /**
   * Account autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.accountAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.accountAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Account> accountAuto(String filter) {
    return ScmLookupView.accountAuto(filter);
  }

  /**
   * BankAccountType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.bankAccountTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.bankAccountTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
//  public List<BankAccountType> bankAccountTypeAuto(String filter) {
//     return ScmLookupView.bankAccountTypeAuto(filter);
//  }

  /**
   * Bank autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.bankAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.bankAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Bank> bankAuto(String filter) {
    return ScmLookupExtView.bankAutoFilterByCompanyCountry(UserRuntimeView.instance().getCompany().getCountryId().getId(), filter);
  }

  /**
   * Return all bank contact of a company.
   *
   * @param main
   * @return
   */
  public List<CompanyBankContact> getVendorBankContactsList(MainView main) {
    if (companyBankContactList == null) {
      try {
        companyBankContactList = CompanyBankContactService.contactListByCompanyBank(main, getCompanyBank());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return companyBankContactList;
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void companyBankContactDialogClose() {
    Jsf.popupReturn(parent, null);
  }
  
  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }
  
  public void accountTypeSelectEvent(SelectEvent event) {
  }
}
