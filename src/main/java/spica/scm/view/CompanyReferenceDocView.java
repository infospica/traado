/*
 * @(#)CompanyReferenceDocView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypeAction;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.app.faces.JsfIo;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.CompanyReferenceDoc;
import spica.scm.service.CompanyReferenceDocService;
import spica.scm.domain.CompanyReference;
import wawo.app.faces.Jsf;

/**
 * CompanyReferenceDocView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "companyReferenceDocView")
@ViewScoped
public class CompanyReferenceDocView implements Serializable {

  private transient CompanyReferenceDoc companyReferenceDoc;	//Domain object/selected Domain.
  private transient LazyDataModel<CompanyReferenceDoc> companyReferenceDocLazyModel; 	//For lazy loading datatable.
  private transient CompanyReferenceDoc[] companyReferenceDocSelected;	 //Selected Domain Array
  private transient Part documentPathPart;

  /**
   * Default Constructor.
   */
  public CompanyReferenceDocView() {
    super();
  }

  /**
   * Return CompanyReferenceDoc.
   *
   * @return CompanyReferenceDoc.
   */
  public CompanyReferenceDoc getCompanyReferenceDoc() {
    if (companyReferenceDoc == null) {
      companyReferenceDoc = new CompanyReferenceDoc();
    }
    return companyReferenceDoc;
  }

  /**
   * Set CompanyReferenceDoc.
   *
   * @param companyReferenceDoc.
   */
  public void setCompanyReferenceDoc(CompanyReferenceDoc companyReferenceDoc) {
    this.companyReferenceDoc = companyReferenceDoc;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCompanyReferenceDoc(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        getCompanyReferenceDoc().setCompanyReferenceId(parent);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getCompanyReferenceDoc().reset();
          getCompanyReferenceDoc().setCompanyReferenceId(parent);
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setCompanyReferenceDoc((CompanyReferenceDoc) CompanyReferenceDocService.selectByPk(main, getCompanyReferenceDoc()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadCompanyReferenceDocList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }
  private CompanyReference parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    parent = (CompanyReference) Jsf.popupParentValue(CompanyReference.class);
    getCompanyReferenceDoc().setId(Jsf.getParameterInt("id"));
  }

  public CompanyReference getParent() {
    return parent;
  }

  public void setParent(CompanyReference parent) {
    this.parent = parent;
  }

  /**
   * Create companyReferenceDocLazyModel.
   *
   * @param main
   */
  private void loadCompanyReferenceDocList(final MainView main) {
    if (companyReferenceDocLazyModel == null) {
      companyReferenceDocLazyModel = new LazyDataModel<CompanyReferenceDoc>() {
        private List<CompanyReferenceDoc> list;

        @Override
        public List<CompanyReferenceDoc> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CompanyReferenceDocService.listPaged(main, parent);
            main.commit(companyReferenceDocLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CompanyReferenceDoc companyReferenceDoc) {
          return companyReferenceDoc.getId();
        }

        @Override
        public CompanyReferenceDoc getRowData(String rowKey) {
          if (list != null) {
            for (CompanyReferenceDoc obj : list) {
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

  private void uploadFiles() throws IOException {
    String SUB_FOLDER = "scm_company_reference_doc/";
    if (documentPathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(documentPathPart, getCompanyReferenceDoc().getDocumentFilePath(), SUB_FOLDER);
      getCompanyReferenceDoc().setDocumentFilePath(JsfIo.getDbPath(documentPathPart, SUB_FOLDER));
      documentPathPart = null;	//import to set as null.
    }
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCompanyReferenceDoc(MainView main) {
    return saveOrCloneCompanyReferenceDoc(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCompanyReferenceDoc(MainView main) {
    main.setViewType("newform");
    return saveOrCloneCompanyReferenceDoc(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCompanyReferenceDoc(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CompanyReferenceDocService.insertOrUpdate(main, getCompanyReferenceDoc());
            break;
          case "clone":
            CompanyReferenceDocService.clone(main, getCompanyReferenceDoc());
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
   * Delete one or many CompanyReferenceDoc.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCompanyReferenceDoc(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(companyReferenceDocSelected)) {
        CompanyReferenceDocService.deleteByPkArray(main, getCompanyReferenceDocSelected()); //many record delete from list
        main.commit("success.delete");
        companyReferenceDocSelected = null;
      } else {
        CompanyReferenceDocService.deleteByPk(main, getCompanyReferenceDoc());  //individual record delete from list or edit form
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
   * Return LazyDataModel of CompanyReferenceDoc.
   *
   * @return
   */
  public LazyDataModel<CompanyReferenceDoc> getCompanyReferenceDocLazyModel() {
    return companyReferenceDocLazyModel;
  }

  /**
   * Return CompanyReferenceDoc[].
   *
   * @return
   */
  public CompanyReferenceDoc[] getCompanyReferenceDocSelected() {
    return companyReferenceDocSelected;
  }

  /**
   * Set CompanyReferenceDoc[].
   *
   * @param companyReferenceDocSelected
   */
  public void setCompanyReferenceDocSelected(CompanyReferenceDoc[] companyReferenceDocSelected) {
    this.companyReferenceDocSelected = companyReferenceDocSelected;
  }

  /**
   * Return Part.
   *
   * @return Part.
   */
  public Part getDocumentPathPart() {
    return documentPathPart;
  }

  /**
   * Set Part documentPathPart.
   *
   * @param documentPathPart.
   */
  public void setDocumentPathPart(Part documentPathPart) {
    if (this.documentPathPart == null || documentPathPart != null) {
      this.documentPathPart = documentPathPart;
    }
  }

  /**
   * CompanyReference autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.companyReferenceAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.companyReferenceAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<CompanyReference> companyReferenceAuto(String filter) {
    return ScmLookupView.companyReferenceAuto(filter);
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void companyReferenceDocPopupClose() {
    Jsf.popupClose(parent);
  }
}
