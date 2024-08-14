/*
 * @(#)CompanyReferenceView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
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

import spica.scm.domain.CompanyReference;
import spica.scm.service.CompanyReferenceService;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyReferenceDoc;
import spica.scm.service.CompanyReferenceDocService;
import wawo.app.faces.Jsf;

/**
 * CompanyReferenceView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "companyReferenceView")
@ViewScoped
public class CompanyReferenceView implements Serializable {

  private transient CompanyReference companyReference;	//Domain object/selected Domain.
  private transient LazyDataModel<CompanyReference> companyReferenceLazyModel; 	//For lazy loading datatable.
  private transient CompanyReference[] companyReferenceSelected;	 //Selected Domain Array
  private List<CompanyReferenceDoc> companyReferenceDocList;
  private int activeIndex = 0; //tab active index, reset to 0 when new records

  /**
   * Default Constructor.
   */
  public CompanyReferenceView() {
    super();
  }

  /**
   * Return CompanyReference.
   *
   * @return CompanyReference.
   */
  public CompanyReference getCompanyReference() {
    if (companyReference == null) {
      companyReference = new CompanyReference();
    }
    return companyReference;
  }

  /**
   * Set CompanyReference.
   *
   * @param companyReference.
   */
  public void setCompanyReference(CompanyReference companyReference) {
    this.companyReference = companyReference;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCompanyReference(MainView main, String viewType) {
    //this.main = main;
    companyReferenceDocList = null;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getCompanyReference().reset();
          getCompanyReference().setCompanyId(parent);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCompanyReference((CompanyReference) CompanyReferenceService.selectByPk(main, getCompanyReference()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCompanyReferenceList(main);
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
    getCompanyReference().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Create companyReferenceLazyModel.
   *
   * @param main
   */
  private void loadCompanyReferenceList(final MainView main) {
    if (companyReferenceLazyModel == null) {
      companyReferenceLazyModel = new LazyDataModel<CompanyReference>() {
        private List<CompanyReference> list;

        @Override
        public List<CompanyReference> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CompanyReferenceService.listPaged(main);
            main.commit(companyReferenceLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CompanyReference companyReference) {
          return companyReference.getId();
        }

        @Override
        public CompanyReference getRowData(String rowKey) {
          if (list != null) {
            for (CompanyReference obj : list) {
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
    String SUB_FOLDER = "scm_company_reference/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCompanyReference(MainView main) {
    return saveOrCloneCompanyReference(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCompanyReference(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCompanyReference(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCompanyReference(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CompanyReferenceService.insertOrUpdate(main, getCompanyReference());
            break;
          case "clone":
            CompanyReferenceService.clone(main, getCompanyReference());
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
   * Delete one or many CompanyReference.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCompanyReference(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(companyReferenceSelected)) {
        CompanyReferenceService.deleteByPkArray(main, getCompanyReferenceSelected()); //many record delete from list
        main.commit("success.delete");
        companyReferenceSelected = null;
      } else {
        CompanyReferenceService.deleteByPk(main, getCompanyReference());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CompanyReference.
   *
   * @return
   */
  public LazyDataModel<CompanyReference> getCompanyReferenceLazyModel() {
    return companyReferenceLazyModel;
  }

  /**
   * Return CompanyReference[].
   *
   * @return
   */
  public CompanyReference[] getCompanyReferenceSelected() {
    return companyReferenceSelected;
  }

  /**
   * Set CompanyReference[].
   *
   * @param companyReferenceSelected
   */
  public void setCompanyReferenceSelected(CompanyReference[] companyReferenceSelected) {
    this.companyReferenceSelected = companyReferenceSelected;
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
   * Return all company reference of a company.
   *
   * @param main
   * @return
   */
  public List<CompanyReferenceDoc> getCompanyReferenceDocList(MainView main) {
    if (companyReferenceDocList == null) {
      try {
        companyReferenceDocList = CompanyReferenceDocService.referenceDocListByCompany(main, getCompanyReference());
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }

    return companyReferenceDocList;
  }

  /**
   * Opening add new page in a dialog.
   */
  public void companyReferenceDocNewDialog() {
    Jsf.openDialog("/scm/company_reference_doc.xhtml", getCompanyReference());
  }

  /**
   * Closing the dialog.
   *
   * @param event
   */
  public void companyReferenceDocDialogReturn(SelectEvent event) {
    Jsf.closeDialog(getCompanyReference());
    companyReferenceDocList = null; // Reset to null to fetch updated list
  }

  /**
   * Opening edit page in a dialog.
   *
   * @param id
   */
  public void companyReferenceDocEditDialog(Integer id) {
    Jsf.openDialog("/scm/company_reference_doc.xhtml", getCompanyReference(), id);
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
  public void companyReferenceDocPopupClose() {
    Jsf.popupReturn(parent, null);
  }
}
