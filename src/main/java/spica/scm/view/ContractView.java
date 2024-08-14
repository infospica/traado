/*
 * @(#)ContractView.java	1.0 Thu Apr 07 11:31:23 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import spica.scm.util.AccountContractUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.SelectItem;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.app.faces.JsfIo;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.Contract;
import spica.scm.service.ContractService;
import spica.scm.domain.Account;
import spica.scm.domain.ClaimFrequency;
import spica.scm.domain.Claimable;
import spica.scm.domain.ContractCommissionType;
import spica.scm.domain.ContractCommissionOn;
import spica.scm.domain.ContractCommissionSlab;
import spica.scm.domain.PurchaseCreditOn;
import spica.scm.domain.ContractStatus;
import spica.scm.service.ClaimableService;
import spica.scm.service.ContractCommissionSlabService;
import wawo.app.faces.Jsf;
import spica.scm.domain.Period;
import spica.scm.domain.ContractBenefitType;
import spica.scm.domain.ContractCommiByRange;
import spica.scm.service.ContractCommiByRangeService;

/**
 * ContractView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 07 11:31:23 IST 2016
 */
@Named(value = "contractView")
@ViewScoped
public class ContractView implements Serializable {

  private final int TRADE_TYPE_SS = 4;
  private final int TRADE_TYPE_CSA = 2;
  private final int TRADE_TYPE_CF = 3;
  private final int TRADE_TYPE_MARKETER = 1;

  private transient Contract contract;	//Domain object/selected Domain.
  private transient LazyDataModel<Contract> contractLazyModel; 	//For lazy loading datatable.
  private transient Contract[] contractSelected;	 //Selected Domain Array
  private transient Part documentCopyPathPart;
  private transient Account parentAccount;
  private List<ContractCommissionSlab> contractCommissionSlabList;
  private transient Contract activeContract;
  private List<ContractCommiByRange> contractCommiByRangeList;
  private transient List<SelectItem> listPointOfPurchase;
  private int activeIndex;
  private double totalMargin;

  @PostConstruct
  public void init() {
    activeContract = (Contract) Jsf.popupParentValue(Contract.class);
    if (activeContract != null) {
      setParentAccount(activeContract.getAccountId());
    }
    Jsf.getMain().setViewType(ViewTypes.list);
  }

  /**
   * Default Constructor.
   */
  public ContractView() {
    super();
  }

  public Account getParentAccount() {
    return parentAccount;
  }

  public void setParentAccount(Account parentAccount) {
    this.parentAccount = parentAccount;
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }

  public double getTotalMargin() {
    return totalMargin;
  }

  public void setTotalMargin(double totalMargin) {
    this.totalMargin = totalMargin;
  }

  /**
   * Return Contract.
   *
   * @return Contract.
   */
  public Contract getContract() {
    if (contract == null) {
      contract = new Contract();
    }
    return contract;
  }

  /**
   * Set Contract.
   *
   * @param contract.
   */
  public void setContract(Contract contract) {
    this.contract = contract;
  }

  public Contract getActiveContract() {
    return activeContract;
  }

  public void setActiveContract(Contract activeContract) {
    this.activeContract = activeContract;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchContract(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getContract().reset();
        } else if (ViewType.editform.toString().equals(viewType)) {
          setContract((Contract) ContractService.selectByPk(main, getContract()));
          if (isSuperStockiest()) {
            calculateTotalMargin(getContract().getMarginPercentage(), getContract().getVendorReservePercent());
          }
        } else if (ViewType.list.toString().equals(viewType)) {
          loadContractList(main);
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
   *
   * @param marginPercentage
   * @param vendorReservePercent
   */
  private void calculateTotalMargin(Double marginPercentage, Double vendorReservePercent) {
    vendorReservePercent = vendorReservePercent == null ? 0 : vendorReservePercent;
    setTotalMargin(marginPercentage + vendorReservePercent);
  }

  /**
   * Create contractLazyModel.
   *
   * @param main
   */
  private void loadContractList(final MainView main) {
    if (contractLazyModel == null) {
      contractLazyModel = new LazyDataModel<Contract>() {
        private List<Contract> list;

        @Override
        public List<Contract> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            //list = ContractService.listPaged(main);
            list = ContractService.listPagedContractByAccount(main, getParentAccount().getId(), getActiveContract().getId());
            main.commit(contractLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Contract contract) {
          return contract.getId();
        }

        @Override
        public Contract getRowData(String rowKey) {
          if (list != null) {
            for (Contract obj : list) {
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
    String SUB_FOLDER = "scm_contract/";
    if (documentCopyPathPart != null) { //use uploadPrivateUnique for unique file generation and uploadPublic for web page images
      JsfIo.uploadPrivate(documentCopyPathPart, getContract().getDocumentCopyPath(), SUB_FOLDER);
      getContract().setDocumentCopyPath(JsfIo.getDbPath(documentCopyPathPart, SUB_FOLDER));
      documentCopyPathPart = null;	//import to set as null.
    }
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveContract(MainView main) {
    return saveOrCloneContract(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneContract(MainView main) {
    main.setViewType("newform");
    return saveOrCloneContract(main, "clone");
  }

  private List<Claimable> claimableList;

  /**
   * Return List of objects, loading once.
   *
   * @param main
   * @return
   */
  public List<Claimable> getClaimableList(MainView main) {
    try {
      claimableList = ClaimableService.selectByContract(main, getContract());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return claimableList;
  }

  public List<ContractCommissionSlab> getContractCommissionSlabList(MainView main) {
    if (contractCommissionSlabList == null) {
      try {
        if (getContract().getId() != null) {
          contractCommissionSlabList = ContractCommissionSlabService.selectContractCommissionSlabByContract(main, getContract().getId());
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return contractCommissionSlabList;
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneContract(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            ContractService.insertOrUpdate(main, getContract());
            break;
          case "clone":
            ContractService.clone(main, getContract());
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
   * Delete one or many Contract.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteContract(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(contractSelected)) {
        ContractService.deleteByPkArray(main, getContractSelected()); //many record delete from list
        main.commit("success.delete");
        contractSelected = null;
      } else {
        ContractService.deleteByPk(main, getContract());  //individual record delete from list or edit form
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
   * Return LazyDataModel of Contract.
   *
   * @return
   */
  public LazyDataModel<Contract> getContractLazyModel() {
    return contractLazyModel;
  }

  /**
   * Return Contract[].
   *
   * @return
   */
  public Contract[] getContractSelected() {
    return contractSelected;
  }

  /**
   * Set Contract[].
   *
   * @param contractSelected
   */
  public void setContractSelected(Contract[] contractSelected) {
    this.contractSelected = contractSelected;
  }

  /**
   * Return Part.
   *
   * @return Part.
   */
  public Part getDocumentCopyPathPart() {
    return documentCopyPathPart;
  }

  /**
   * Set Part contractDocumentPathPart.
   *
   * @param documentCopyPathPart.
   */
  public void setDocumentCopyPathPart(Part documentCopyPathPart) {
    if (this.documentCopyPathPart == null || documentCopyPathPart != null) {
      this.documentCopyPathPart = documentCopyPathPart;
    }
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
   * ClaimFrequency autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.claimFrequencyAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.claimFrequencyAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ClaimFrequency> claimFrequencyAuto(String filter) {
    return ScmLookupView.claimFrequencyAuto(filter);
  }

  /**
   * ContractCommissionType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.contractCommissionTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.contractCommissionTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ContractCommissionType> contractCommissionTypeAuto(String filter) {
    return ScmLookupView.contractCommissionTypeAuto(filter);
  }

  /**
   * ContractCommissionOn autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.contractCommissionOnAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.contractCommissionOnAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ContractCommissionOn> contractCommissionOnAuto(String filter) {
    return ScmLookupView.contractCommissionOnAuto(filter);
  }

  /**
   * PurchaseCreditOn autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.purchaseCreditOnAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.purchaseCreditOnAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<PurchaseCreditOn> purchaseCreditOnAuto(String filter) {
    return ScmLookupView.purchaseCreditOnAuto(filter);
  }

  /**
   * Periods autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.periodsAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.periodsAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Period> periodsAuto(String filter) {
    return ScmLookupView.periodsAuto(filter);
  }

  /**
   * ContractBenefitType autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookup.contractBenefitTypeAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookup.contractBenefitTypeAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ContractBenefitType> contractBenefitTypeAuto(String filter) {
    return ScmLookupView.contractBenefitTypeAuto(filter);
  }

  /**
   * ContractStatus autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.contractStatusAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.contractStatusAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<ContractStatus> contractStatusAuto(String filter) {
    return ScmLookupView.contractStatusAuto(filter);
  }

  /**
   * Calling back the dialogReturn method. Can pass an object back to the returning method.
   */
  public void contractListDialogClose() {
    Jsf.returnDialog(null);
  }

  public List<ContractCommiByRange> getContractCommiByRangeList(MainView main) {
    if (contractCommiByRangeList == null) {
      try {
        if (getContract().getId() != null) {
          contractCommiByRangeList = ContractCommiByRangeService.selectContractCommissionRangeByContract(main, getContract().getId());
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return contractCommiByRangeList;
  }

  public void setContractCommiByRangeList(List<ContractCommiByRange> contractCommiByRangeList) {
    this.contractCommiByRangeList = contractCommiByRangeList;
  }

  /**
   * Closing the dialog.
   *
   * @param event
   */
  public void contractCommissionByRangeDialogReturn(SelectEvent event) {
    Jsf.closeDialog(getContract());
    setContractCommiByRangeList(null); // Reset to null to fetch updated list
    MainView main = Jsf.getMain();
    try {
      getContractCommiByRangeList(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void contractListDialogReturn(SelectEvent event) {
    Jsf.closeDialog(getContract());
  }

  public void constractListDialogClose() {
    Jsf.popupClose(contract);
  }

  public boolean isContractCreated() {
    return getContract() != null && getContract().getId() != null;
  }

  public boolean isActiveContract() {
    if (isContractCreated()) {
      return getContract().getContractStatusId().getId() == AccountContractUtil.CONTRACT_STATUS_ACTIVE;
    }
    return false;
  }

  public boolean isActivableContract() {
    if (isContractCreated()) {
      if ((getContract().getContractStatusId().getId() == AccountContractUtil.CONTRACT_STATUS_DRAFT
              || getContract().getContractStatusId().getId() == AccountContractUtil.CONTRACT_STATUS_RENEW)) {
        return true;
      }
    }
    return false;
  }

  public boolean isMarketer() {
    if (parentAccount != null && parentAccount.getCompanyTradeProfileId() != null) {
      return (parentAccount.getCompanyTradeProfileId().getId() == TRADE_TYPE_MARKETER);
    }
    return false;
  }

  /**
   *
   * @param status
   * @return
   */
  public String getContractStatus(ContractStatus status) {
    return AccountContractUtil.getContractStatus(status);
  }

  public boolean isSuperStockiest() {
    if (getContract().getAccountId() != null && getContract().getAccountId().getCompanyTradeProfileId() != null) {
      return (getContract().getAccountId().getCompanyTradeProfileId().getId() == TRADE_TYPE_SS);
    }
    return false;
  }

  /**
   *
   * @return
   */
  public List<SelectItem> getListPurchaseLevel() {
    if (isSuperStockiest()) {
      listPointOfPurchase = new ArrayList<>();
      SelectItem si1 = new SelectItem();
      si1.setItemLabel("First Purchase");
      si1.setIntValue(1);
      listPointOfPurchase.add(si1);
      SelectItem si2 = new SelectItem();
      si2.setItemLabel("Second Purchase");
      si2.setIntValue(2);
      listPointOfPurchase.add(si2);
    } else {
      listPointOfPurchase = new ArrayList<>();
      SelectItem si = new SelectItem();
      si.setItemLabel("First Purchase");
      si.setIntValue(1);
      listPointOfPurchase.add(si);
    }
    return listPointOfPurchase;
  }

  public List<SelectItem> getListYesNo() {
    List<SelectItem> listYesNo = new ArrayList<>();
    SelectItem si1 = new SelectItem();
    si1.setItemLabel("Yes");
    si1.setIntValue(1);
    listYesNo.add(si1);
    SelectItem si2 = new SelectItem();
    si2.setItemLabel("No");
    si2.setIntValue(2);
    listYesNo.add(si2);
    return listYesNo;
  }

  public List<SelectItem> getListPurchaseCreditOn() {
    List<SelectItem> list = new ArrayList<>();
    SelectItem si1 = new SelectItem();
    si1.setItemLabel("Goods Recieved");
    si1.setIntValue(1);
    list.add(si1);
    SelectItem si2 = new SelectItem();
    si2.setItemLabel("Purchase Invoice");
    si2.setIntValue(2);
    list.add(si2);
    SelectItem si3 = new SelectItem();
    si3.setItemLabel("Sales");
    si3.setIntValue(3);
    list.add(si3);
    SelectItem si4 = new SelectItem();
    si4.setItemLabel("Collection");
    si4.setIntValue(4);
    list.add(si4);
    SelectItem si5 = new SelectItem();
    si5.setItemLabel("LR Date");
    si5.setIntValue(5);
    list.add(si5);
    return list;
  }

  public boolean isFactoryDirect() {
    return (getContract() != null && getContract().getIsFactoryDirect() != null && getContract().getIsFactoryDirect() == 1);
  }

  public boolean isBenefitByCommission() {
    return (getContract().getAccountId().getCompanyTradeProfileId().getId() == TRADE_TYPE_CSA || getContract().getAccountId().getCompanyTradeProfileId().getId() == TRADE_TYPE_CF);
  }

  public boolean isBenefitByMarginPercentage() {
    return (getContract().getAccountId().getCompanyTradeProfileId().getId() == TRADE_TYPE_SS);
  }

  public boolean isFixedCommissionByRange() {
    return (getContract().getBenefitType() == 1 && getContract().getCommissionValueType() == 1 && getContract().getCommissionByRange() == 2);
  }

  public boolean isPercentageCommissionByRange() {
    return (getContract().getBenefitType() == 1 && getContract().getCommissionValueType() == 2 && getContract().getCommissionByRange() == 2);
  }

  public boolean isFixedCommissionValue() {
    return (getContract().getBenefitType() == 1 && getContract().getCommissionValueType() == 1);
  }

  public boolean isPercentageCommissionValue() {
    return (getContract().getBenefitType() == 1 && getContract().getCommissionValueType() == 2);
  }

  public boolean isCommisionByRange() {
    if (getContract() != null) {
      return (getContract().getCommissionByRange() != null && getContract().getCommissionByRange() == 1);
    }
    return false;
  }

  public List<SelectItem> getListCommissionValueType() {
    List<SelectItem> listYesNo = new ArrayList<>();
    SelectItem si1 = new SelectItem();
    si1.setItemLabel("Fixed");
    si1.setIntValue(1);
    listYesNo.add(si1);
    SelectItem si2 = new SelectItem();
    si2.setItemLabel("Percentage");
    si2.setIntValue(2);
    listYesNo.add(si2);
    return listYesNo;
  }

  public List<SelectItem> getListCommissionOn() {
    List<SelectItem> listYesNo = new ArrayList<>();
    SelectItem si1 = new SelectItem();
    si1.setItemLabel("Sales");
    si1.setIntValue(1);
    listYesNo.add(si1);
    SelectItem si2 = new SelectItem();
    si2.setItemLabel("Collection");
    si2.setIntValue(2);
    listYesNo.add(si2);
    return listYesNo;
  }

  private void setBenefitType() {
    if (isSuperStockiest()) {
      getContract().setBenefitType(2);
    } else {
      getContract().setBenefitType(1);
    }
  }

  public boolean isCommissionRangeExist() {
    return (contractCommiByRangeList != null && !contractCommiByRangeList.isEmpty());
  }
}
