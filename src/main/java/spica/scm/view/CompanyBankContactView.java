/*
 * @(#)CompanyBankContactView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
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
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.CompanyBankContact;
import spica.scm.service.CompanyBankContactService;
import spica.scm.domain.CompanyBank;
import spica.scm.domain.Account;
import spica.scm.domain.Designation;
import spica.scm.domain.Status;
import spica.scm.service.DesignationService;
import wawo.app.faces.Jsf;

/**
 * CompanyBankContactView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "companyBankContactView")
@ViewScoped
public class CompanyBankContactView implements Serializable {

  private transient CompanyBankContact companyBankContact;	//Domain object/selected Domain.
  private transient LazyDataModel<CompanyBankContact> companyBankContactLazyModel; 	//For lazy loading datatable.
  private transient CompanyBankContact[] companyBankContactSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public CompanyBankContactView() {
    super();
  }

  /**
   * Return CompanyBankContact.
   *
   * @return CompanyBankContact.
   */
  public CompanyBankContact getCompanyBankContact() {
    if (companyBankContact == null) {
      companyBankContact = new CompanyBankContact();
    }
    return companyBankContact;
  }

  /**
   * Set CompanyBankContact.
   *
   * @param companyBankContact.
   */
  public void setCompanyBankContact(CompanyBankContact companyBankContact) {
    this.companyBankContact = companyBankContact;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCompanyBankContact(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        getCompanyBankContact().setCompanyBankId(parent);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getCompanyBankContact().reset();
          getCompanyBankContact().setCompanyBankId(parent);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCompanyBankContact((CompanyBankContact) CompanyBankContactService.selectByPk(main, getCompanyBankContact()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCompanyBankContactList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }
  private CompanyBank parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (CompanyBank) Jsf.popupParentValue(CompanyBank.class);
    getCompanyBankContact().setId(Jsf.getParameterInt("id"));
  }

  public CompanyBank getParent() {
    return parent;
  }

  public void setParent(CompanyBank parent) {
    this.parent = parent;
  }

  /**
   * Create companyBankContactLazyModel.
   *
   * @param main
   */
  private void loadCompanyBankContactList(final MainView main) {
    if (companyBankContactLazyModel == null) {
      companyBankContactLazyModel = new LazyDataModel<CompanyBankContact>() {
        private List<CompanyBankContact> list;

        @Override
        public List<CompanyBankContact> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CompanyBankContactService.listPaged(main, parent);
            main.commit(companyBankContactLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CompanyBankContact companyBankContact) {
          return companyBankContact.getId();
        }

        @Override
        public CompanyBankContact getRowData(String rowKey) {
          if (list != null) {
            for (CompanyBankContact obj : list) {
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
    String SUB_FOLDER = "scm_company_bank_contact/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCompanyBankContact(MainView main) {
    return saveOrCloneCompanyBankContact(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCompanyBankContact(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCompanyBankContact(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCompanyBankContact(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CompanyBankContactService.insertOrUpdate(main, getCompanyBankContact());
//            CompanyBankContactService.makeDefault(main, getCompanyBankContact());
            break;
          case "clone":
            CompanyBankContactService.clone(main, getCompanyBankContact());
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
   * Delete one or many CompanyBankContact.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCompanyBankContact(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(companyBankContactSelected)) {
        CompanyBankContactService.deleteByPkArray(main, getCompanyBankContactSelected()); //many record delete from list
        main.commit("success.delete");
        companyBankContactSelected = null;
      } else {
        CompanyBankContactService.deleteByPk(main, getCompanyBankContact());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CompanyBankContact.
   *
   * @return
   */
  public LazyDataModel<CompanyBankContact> getCompanyBankContactLazyModel() {
    return companyBankContactLazyModel;
  }

  /**
   * Return CompanyBankContact[].
   *
   * @return
   */
  public CompanyBankContact[] getCompanyBankContactSelected() {
    return companyBankContactSelected;
  }

  /**
   * Set CompanyBankContact[].
   *
   * @param companyBankContactSelected
   */
  public void setCompanyBankContactSelected(CompanyBankContact[] companyBankContactSelected) {
    this.companyBankContactSelected = companyBankContactSelected;
  }

  /**
   * CompanyBank autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.companyBankAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.companyBankAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<CompanyBank> companyBankAuto(String filter) {
    return ScmLookupView.companyBankAuto(filter);
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
   * Designation autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.designationAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.designationAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Designation> designationAuto(String filter) {
    return ScmLookupView.designationAuto(filter);
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void companyBankContactPopupClose() {
    Jsf.popupClose(parent);
  }

  public List<Status> statusAuto(String filter) {
    return ScmLookupView.statusAuto(filter);
  }

  public List<Designation> selectDesignationByCompanyContext(MainView main) {
    try {
      return DesignationService.selectDesignationByCompanyContext(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }
}
