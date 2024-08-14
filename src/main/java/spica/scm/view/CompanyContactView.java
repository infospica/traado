/*
 * @(#)CompanyContactView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
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

import spica.scm.domain.CompanyContact;
import spica.scm.service.CompanyContactService;
import spica.scm.domain.Company;
import spica.scm.domain.Account;
import spica.scm.domain.Designation;
import spica.scm.service.DesignationService;
import wawo.app.faces.Jsf;

/**
 * CompanyContactView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "companyContactView")
@ViewScoped
public class CompanyContactView implements Serializable {

  private transient CompanyContact companyContact;	//Domain object/selected Domain.
  private transient LazyDataModel<CompanyContact> companyContactLazyModel; 	//For lazy loading datatable.
  private transient CompanyContact[] companyContactSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public CompanyContactView() {
    super();
  }

  /**
   * Return CompanyContact.
   *
   * @return CompanyContact.
   */
  public CompanyContact getCompanyContact() {
    if (companyContact == null) {
      companyContact = new CompanyContact();
    }
    return companyContact;
  }

  /**
   * Set CompanyContact.
   *
   * @param companyContact.
   */
  public void setCompanyContact(CompanyContact companyContact) {
    this.companyContact = companyContact;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCompanyContact(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getCompanyContact().reset();
          getCompanyContact().setCompanyId(parent);
        } else if (ViewType.editform.toString().equals(viewType)) {
          setCompanyContact((CompanyContact) CompanyContactService.selectByPk(main, getCompanyContact()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCompanyContactList(main);
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
    getCompanyContact().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Create companyContactLazyModel.
   *
   * @param main
   */
  private void loadCompanyContactList(final MainView main) {
    if (companyContactLazyModel == null) {
      companyContactLazyModel = new LazyDataModel<CompanyContact>() {
        private List<CompanyContact> list;

        @Override
        public List<CompanyContact> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CompanyContactService.listPaged(main);
            main.commit(companyContactLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CompanyContact companyContact) {
          return companyContact.getId();
        }

        @Override
        public CompanyContact getRowData(String rowKey) {
          if (list != null) {
            for (CompanyContact obj : list) {
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
    String SUB_FOLDER = "scm_company_contact/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCompanyContact(MainView main) {
    return saveOrCloneCompanyContact(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCompanyContact(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCompanyContact(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCompanyContact(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CompanyContactService.insertOrUpdate(main, getCompanyContact());
//            CompanyContactService.makeDefault(main, getCompanyContact());
            break;
          case "clone":
            CompanyContactService.clone(main, getCompanyContact());
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
   * Delete one or many CompanyContact.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCompanyContact(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(companyContactSelected)) {
        CompanyContactService.deleteByPkArray(main, getCompanyContactSelected()); //many record delete from list
        main.commit("success.delete");
        companyContactSelected = null;
      } else {
        CompanyContactService.deleteByPk(main, getCompanyContact());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CompanyContact.
   *
   * @return
   */
  public LazyDataModel<CompanyContact> getCompanyContactLazyModel() {
    return companyContactLazyModel;
  }

  /**
   * Return CompanyContact[].
   *
   * @return
   */
  public CompanyContact[] getCompanyContactSelected() {
    return companyContactSelected;
  }

  /**
   * Set CompanyContact[].
   *
   * @param companyContactSelected
   */
  public void setCompanyContactSelected(CompanyContact[] companyContactSelected) {
    this.companyContactSelected = companyContactSelected;
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

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void companyContactPopupClose() {
    Jsf.popupReturn(parent, null);
  }

  public void onRowEdit(CompanyContact companyContact) {
    MainView main = Jsf.getMain();
    try {
      CompanyContactService.insertOrUpdate(main, companyContact);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    }
  }

}
