/*
 * @(#)PlatformSettlementView.java	1.0 Thu Apr 20 15:04:02 IST 2017
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.PlatformData;
import spica.scm.domain.Platform;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.app.faces.JsfIo;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.PlatformSettlement;
import spica.scm.domain.PlatformSettlementItem;
import spica.scm.service.PlatformSettlementService;
import spica.scm.domain.PlatformSettlementType;
import spica.scm.service.PlatformService;
import spica.scm.service.PlatformSettlementItemService;
import spica.sys.FileConstant;
import spica.sys.SystemRuntimeConfig;
import wawo.app.faces.Jsf;

/**
 * PlatformSettlementView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 20 15:04:02 IST 2017
 */
@Named(value = "platformSettlementView")
@ViewScoped
public class PlatformSettlementView implements Serializable {

  private transient PlatformSettlement platformSettlement;	//Domain object/selected Domain.
  private transient LazyDataModel<PlatformSettlement> platformSettlementLazyModel; 	//For lazy loading datatable.
  private transient PlatformSettlement[] platformSettlementSelected;	 //Selected Domain Array
  private transient Part settlementDocPart;

  /**
   * Default Constructor.
   */
  public PlatformSettlementView() {
    super();
  }
  private PlatformData parent;

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    getPlatformSettlement().setId(Jsf.getParameterInt("id"));
    if (getPlatformSettlement().getId() == null) {
      parent = (PlatformData) Jsf.popupParentValue(PlatformData.class);
      calculateTotal();
    }
  }

  public PlatformData getParent() {
    return parent;
  }

  public void setParent(PlatformData parent) {
    this.parent = parent;
  }

  /**
   * Return PlatformSettlement.
   *
   * @return PlatformSettlement.
   */
  public PlatformSettlement getPlatformSettlement() {
    if (platformSettlement == null) {
      platformSettlement = new PlatformSettlement();
    }
    return platformSettlement;
  }

  /**
   * Set PlatformSettlement.
   *
   * @param platformSettlement.
   */
  public void setPlatformSettlement(PlatformSettlement platformSettlement) {
    this.platformSettlement = platformSettlement;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPlatformSettlement(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getPlatformSettlement().reset();
          if (parent != null && getPlatformSettlement().getId() == null) {
            platformSettlement.setSettlementTypeId(parent.getSettlementType());
          }
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setPlatformSettlement((PlatformSettlement) PlatformSettlementService.selectByPk(main, getPlatformSettlement()));
          parent = new PlatformData();
          parent.setCreditList(new ArrayList<>());
          parent.setDebitList(new ArrayList<>());
          for (PlatformSettlementItem psi : getPlatformSettlement().getPlatformSettlementIdPlatformSettlementItem()) {
            if (psi.getPlatformId().getCreditAmountRequired() == null) {
              parent.getDebitList().add(psi.getPlatformId());
            }
            if (psi.getPlatformId().getDebitAmountRequired() == null) {
              parent.getCreditList().add(psi.getPlatformId());
            }
          }
          calculateTotal();
        } else if (ViewTypes.isList(viewType)) {
          loadPlatformSettlementList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  private void calculateTotal() {
    Double credit = 0.0;

    if (parent != null) {
      for (Platform p : parent.getCreditList()) {
        credit += p.getCreditAmountRequired();
      }
      Double debit = 0.0;
      for (Platform p : parent.getDebitList()) {
        debit += p.getDebitAmountRequired();
      }
      parent.setTotalDebit(debit);
      parent.setTotalCredit(credit);
    }
  }

  /**
   * Create platformSettlementLazyModel.
   *
   * @param main
   */
  private void loadPlatformSettlementList(final MainView main) {
    if (platformSettlementLazyModel == null) {
      platformSettlementLazyModel = new LazyDataModel<PlatformSettlement>() {
        private List<PlatformSettlement> list;

        @Override
        public List<PlatformSettlement> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = PlatformSettlementService.listPaged(main);
            main.commit(platformSettlementLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(PlatformSettlement platformSettlement) {
          return platformSettlement.getId();
        }

        @Override
        public PlatformSettlement getRowData(String rowKey) {
          if (list != null) {
            for (PlatformSettlement obj : list) {
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
    String SUB_FOLDER = "scm_platform_settlement/";
    if (settlementDocPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(settlementDocPart, getPlatformSettlement().getSettlementDoc(), SUB_FOLDER);
      getPlatformSettlement().setSettlementDoc(JsfIo.getDbPath(settlementDocPart, SUB_FOLDER));
      settlementDocPart = null;	//import to set as null.
    }
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  Platform balanceToPlatform;

  public String savePlatformSettlement(MainView main) {
    try {

      List<Platform> processed = new ArrayList();

      for (Platform d : parent.getDebitList()) {
        for (Platform c : parent.getCreditList()) {
          if (!processed.contains(c)) {
            if (StringUtil.equalsDouble(d.getDebitAmountRequired(), c.getCreditAmountRequired())) {
              getPlatformSettlement().addPlatformSettlementIdPlatformSettlementItem(new PlatformSettlementItem(getPlatformSettlement(), d));
              getPlatformSettlement().addPlatformSettlementIdPlatformSettlementItem(new PlatformSettlementItem(getPlatformSettlement(), c));
              processed.add(c);
              balanceToPlatform = null;
              break;
            } else if (d.getDebitAmountRequired() > c.getCreditAmountRequired()) {
              //  getPlatformSettlement().addPlatformSettlementIdPlatformSettlementItem(new PlatformSettlementItem(getPlatformSettlement(), d));
              getPlatformSettlement().addPlatformSettlementIdPlatformSettlementItem(new PlatformSettlementItem(getPlatformSettlement(), c));
              d.setDebitAmountRequired(d.getDebitAmountRequired() - c.getCreditAmountRequired());
              processed.add(c);
              balanceToPlatform = d;
            } else if (d.getDebitAmountRequired() < c.getCreditAmountRequired()) {
              getPlatformSettlement().addPlatformSettlementIdPlatformSettlementItem(new PlatformSettlementItem(getPlatformSettlement(), d));
              processed.add(d);
              //  getPlatformSettlement().addPlatformSettlementIdPlatformSettlementItem(new PlatformSettlementItem(getPlatformSettlement(), c));
              c.setCreditAmountRequired(c.getCreditAmountRequired() - d.getDebitAmountRequired());
              balanceToPlatform = c;
              break;
            }
          }
        }
      }
      getPlatformSettlement().setStatus(1);
      PlatformSettlementService.insertOrUpdate(main, getPlatformSettlement());
      //saveOrClonePlatformSettlement(main, "save");
      if (balanceToPlatform != null) {
        processed.add(balanceToPlatform);
        PlatformSettlementItemService.insertOrUpdate(main, new PlatformSettlementItem(getPlatformSettlement(), balanceToPlatform));  // Part settled

        Platform p = new Platform(balanceToPlatform);
       // p.setAccountId(balanceToPlatform.getAccountId());
        //  p.setCreatedBy(balanceToPlatform.getCreatedBy());
//        p.setCreditAmountRequired(balanceToPlatform.getCreditAmountRequired());
//        p.setCustomerId(balanceToPlatform.getCustomerId());
//        p.setDebitAmountRequired(balanceToPlatform.getDebitAmountRequired());
//        p.setParentId(balanceToPlatform);
        p.setPlatformDescId(SystemRuntimeConfig.PLATFORM_SELF_ADJUST_DIFFERENCE);
        p.setSourceId(SystemRuntimeConfig.PLATFORM);
     //   p.setDocumentNo(balanceToPlatform.getDocumentNo());
//        p.setPlatformSettlementId(balanceToPlatform.getPlatformSettlementId());
     //   p.setProductEntryDetailId(balanceToPlatform.getProductEntryDetailId());
     //   p.setProductEntryId(balanceToPlatform.getProductEntryId());
     //   p.setSalesInvoiceId(balanceToPlatform.getSalesInvoiceId());
     //   p.setSalesInvoiceItemId(balanceToPlatform.getSalesInvoiceItemId());

        p.setStatusId(SystemRuntimeConfig.PLATFORM_STATUS_NEW);
        PlatformService.insertOrUpdate(main, p);

      }
      PlatformService.updatePlatformStatus(main, processed, SystemRuntimeConfig.PLATFORM_STATUS_PROCESSED.getId());

      main.commit("success.save");
      // setPlatformSettlement((PlatformSettlement) PlatformSettlementService.selectByPk(main, getPlatformSettlement()));
      main.setViewType(ViewTypes.editform);
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.getPageData().reset();
      main.close();
    }
    // return saveOrClonePlatformSettlement(main, "save");
    return null;
  }

  public void platformSettlementPopup(PlatformSettlement platformSettlement) {
    Jsf.popupForm(FileConstant.PLATFORM_SETTLEMENT, platformSettlement, platformSettlement.getId()); // opens a new form if id is null else edit
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePlatformSettlement(MainView main) {
    main.setViewType("newform");
    return saveOrClonePlatformSettlement(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePlatformSettlement(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PlatformSettlementService.insertOrUpdate(main, getPlatformSettlement());
            break;
          case "clone":
            PlatformSettlementService.clone(main, getPlatformSettlement());
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
   * Delete one or many PlatformSettlement.
   *
   * @param main
   * @return the page to display.
   */
  public String deletePlatformSettlement(MainView main) {
    try {
      if (ViewTypes.isList(main.getViewType()) && !StringUtil.isEmpty(platformSettlementSelected)) {
        PlatformSettlementService.deleteByPkArray(main, getPlatformSettlementSelected()); //many record delete from list
        main.commit("success.delete");
        platformSettlementSelected = null;
      } else {
        PlatformSettlementService.deleteByPk(main, getPlatformSettlement());  //individual record delete from list or edit form
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
   * Return LazyDataModel of PlatformSettlement.
   *
   * @return
   */
  public LazyDataModel<PlatformSettlement> getPlatformSettlementLazyModel() {
    return platformSettlementLazyModel;
  }

  /**
   * Return PlatformSettlement[].
   *
   * @return
   */
  public PlatformSettlement[] getPlatformSettlementSelected() {
    return platformSettlementSelected;
  }

  /**
   * Set PlatformSettlement[].
   *
   * @param platformSettlementSelected
   */
  public void setPlatformSettlementSelected(PlatformSettlement[] platformSettlementSelected) {
    this.platformSettlementSelected = platformSettlementSelected;
  }

  /**
   * Return Part.
   *
   * @return Part.
   */
  public Part getSettlementDocPart() {
    return settlementDocPart;
  }

  /**
   * Set Part settlementDocPart.
   *
   * @param settlementDocPart.
   */
  public void setSettlementDocPart(Part settlementDocPart) {
    if (this.settlementDocPart == null || settlementDocPart != null) {
      this.settlementDocPart = settlementDocPart;
    }
  }

  /**
   * PlatformSettlementType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.platformSettlementTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.platformSettlementTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<PlatformSettlementType> platformSettlementTypeAuto(String filter) {
    return ScmLookupView.platformSettlementTypeAuto(filter);
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void platformSettlementPopupClose() {
    Jsf.popupReturn(parent, null);
  }

  public Double getDrBalance() {
      return getParent().getTotalCredit() - getParent().getTotalDebit();
  }
  public Double getCrBalance() {
      return getParent().getTotalDebit() - getParent().getTotalCredit();
  }
}
