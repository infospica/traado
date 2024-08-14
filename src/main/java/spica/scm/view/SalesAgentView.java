/*
 * @(#)SalesAgentView.java	1.0 Thu Oct 20 11:33:28 IST 2016
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.addon.notification.Notify;
import spica.scm.common.SelectItem;
import spica.scm.common.SelectItemInteger;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Company;
import spica.scm.domain.Department;
import spica.scm.domain.Designation;
import spica.scm.domain.SalesAgent;
import spica.scm.domain.SalesAgentContract;
import spica.scm.domain.SalesAgentContractComm;
import spica.scm.domain.Status;
import spica.scm.domain.Territory;
import spica.scm.service.AccountGroupService;
import spica.scm.service.DepartmentService;
import spica.scm.service.DesignationService;
import spica.scm.service.SalesAgentContractCommService;
import spica.scm.service.SalesAgentContractService;
import spica.scm.service.SalesAgentService;
import spica.scm.service.TerritoryService;
import spica.scm.service.UserProfileService;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.config.AppFactory;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppEm;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * SalesAgentView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Oct 20 11:33:28 IST 2016
 */
@Named(value = "salesAgentView")
@ViewScoped
public class SalesAgentView implements Serializable {

  private transient SalesAgent salesAgent;	//Domain object/selected Domain.
  private transient LazyDataModel<SalesAgent> salesAgentLazyModel; 	//For lazy loading datatable.
  private transient SalesAgent[] salesAgentSelected;	 //Selected Domain Array
  private Company company;
  private List<Company> companySelected1;
  private List<Account> accountList;
  private SalesAgentContract salesAgentContract;
  private transient int currentRow;
  private transient List<SalesAgent> salesAgentList;
  private transient List<AccountGroup> accountGroupList;
  private transient AccountGroup defaultAccountGroup;
  private transient List<Territory> territoryList;
  private transient Territory defaultTerritory;
  private transient AccountGroup accountGroup;
  private transient Territory territory;
  private transient Integer verifyOtp;

  public List<Company> getCompanySelected1() {
    return companySelected1;
  }

  public void setCompanySelected1(List<Company> companySelected1) {
    this.companySelected1 = companySelected1;
  }

  /**
   * Default Constructor.
   */
  public SalesAgentView() {
    super();
  }

  @PostConstruct
  public void init() {
    Integer id = Jsf.getParameterInt("id");
    getSalesAgent().setId(id);
  }

  public SalesAgent getSalesAgent() {
    if (salesAgent == null) {
      salesAgent = new SalesAgent();
    }
    return salesAgent;
  }

  /**
   * Set SalesAgent.
   *
   * @param salesAgent.
   */
  public void setSalesAgent(SalesAgent salesAgent) {
    this.salesAgent = salesAgent;
  }

  public Company getCompany() {
    if (company == null) {
      company = UserRuntimeView.instance().getCompany();
    }
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public int getCurrentRow() {
    return currentRow;
  }

  public void setCurrentRow(int currentRow) {
    this.currentRow = currentRow;
  }

  public List<AccountGroup> getAccountGroupList() {
    return accountGroupList;
  }

  public void setAccountGroupList(List<AccountGroup> accountGroupList) {
    this.accountGroupList = accountGroupList;
  }

  public AccountGroup getDefaultAccountGroup() {
    return defaultAccountGroup;
  }

  public void setDefaultAccountGroup(AccountGroup defaultAccountGroup) {
    this.defaultAccountGroup = defaultAccountGroup;
  }

  public List<Territory> getTerritoryList() {
    return territoryList;
  }

  public void setTerritoryList(List<Territory> territoryList) {
    this.territoryList = territoryList;
  }

  public Territory getDefaultTerritory() {
    return defaultTerritory;
  }

  public void setDefaultTerritory(Territory defaultTerritory) {
    this.defaultTerritory = defaultTerritory;
  }

  public LazyDataModel<SalesAgent> getSalesAgentLazyModel() {
    return salesAgentLazyModel;
  }

  public void setSalesAgentLazyModel(LazyDataModel<SalesAgent> salesAgentLazyModel) {
    this.salesAgentLazyModel = salesAgentLazyModel;
  }

  public SalesAgent[] getSalesAgentSelected() {
    return salesAgentSelected;
  }

  public void setSalesAgentSelected(SalesAgent[] salesAgentSelected) {
    this.salesAgentSelected = salesAgentSelected;
  }

  public List<Account> getAccountList() {
    return accountList;
  }

  public void setAccountList(List<Account> accountList) {
    this.accountList = accountList;
  }

  public List<SalesAgent> getSalesAgentList() {
    return salesAgentList;
  }

  public void setSalesAgentList(List<SalesAgent> salesAgentList) {
    this.salesAgentList = salesAgentList;
  }

  public String switchSalesAgent(MainView main, String viewType) {
    getCompany().setFlag(2);
    setSalesAgentContract(null);
    if (!StringUtil.isEmpty(viewType)) {
      try {
        if (UserRuntimeView.instance().getCompany() == null) {
          main.error("company.required");
          main.setViewType(ViewTypes.list);
          return null;
        }
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getSalesAgent().reset();

          if (isSalesAgentView()) {
            addNewSalesAgentCommision();
            Department dept = new Department();
            dept.setId(UserProfileService.DEPARTMENT_ID);
            getSalesAgent().setDepartmentId(dept);
            getSalesAgent().setCurrencyId(UserRuntimeView.instance().getCompany().getCurrencyId());
          }

        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {

          setSalesAgent((SalesAgent) SalesAgentService.selectByPk(main, getSalesAgent()));

          if (isSalesAgentView()) {
            setSalesAgentContract((SalesAgentContract) SalesAgentContractService.listPagedBySalesAgent(main, getSalesAgent()));
            setAccountGroupList(SalesAgentService.selectAccountGroupBySalesAgent(main, getSalesAgent()));
            setTerritoryList(SalesAgentService.selectTerritoryBySalesAgent(main, getSalesAgent()));

            if (getSalesAgentContract().getSalesAgentContractComm() != null) {
              Collections.sort(getSalesAgentContract().getSalesAgentContractComm());
            }

            if (!StringUtil.isEmpty(getAccountGroupList())) {
              setDefaultAccountGroup(getAccountGroupList().get(0));
            }

            if (!StringUtil.isEmpty(getTerritoryList())) {
              setDefaultTerritory(getTerritoryList().get(0));
            }
          }

        } else if (ViewTypes.isList(viewType)) {
          setAccountGroupList(null);
          setDefaultAccountGroup(null);
          setTerritoryList(null);
          setDefaultTerritory(null);
          setSalesAgentContract(null);
          loadSalesAgentList(main);
          // setCompany(null);
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
   * Create userProfileLazyModel.
   *
   * @param main
   */
  private void loadSalesAgentList(final MainView main) {
    if (salesAgentLazyModel == null) {
      salesAgentLazyModel = new LazyDataModel<SalesAgent>() {
        private List<SalesAgent> list;

        @Override
        public List<SalesAgent> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            if (getCompany() != null && getCompany().getId() != null) {
              if (company.getFlag() == 2) {
                list = SalesAgentService.listPagedByCompany(main, UserRuntimeView.instance().getCompany(), getAccountGroup(), getTerritory());
              }
            }
            main.commit(salesAgentLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesAgent salesAgent) {
          return salesAgent.getId();
        }

        @Override
        public SalesAgent getRowData(String rowKey) {
          if (list != null) {
            for (SalesAgent obj : list) {
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
    String SUB_FOLDER = "scm_user_profile/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveSalesAgent(MainView main) {
    return saveOrCloneSalesAgent(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneSalesAgent(MainView main) {
    main.setViewType("newform");
    return saveOrCloneSalesAgent(main, "clone");
  }

  private boolean isSalesAgentView() {
    return (getCompany() != null && getCompany().getFlag() == 2);
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneSalesAgent(MainView main, String key) {
    try {
      uploadFiles(); //File upload

      SystemRuntimeConfig.initRole(main);
      if (null != key) {
        if (key.equals("save") || key.equals("clone")) {

          if (getSalesAgentContract() != null && isSalesAgentView()) {
            if (getSalesAgentContract().getCommissionByRange() == 2) {
              getSalesAgentContract().setSalesAgentContractComm(null);
            } else {
              getSalesAgentContract().setCommissionValuePercentage(null);
              getSalesAgentContract().setCommissionValueFixed(null);
            }
            if (getSalesAgentContract().getCommissionType() == 1) { //fixed
              getSalesAgentContract().setCommissionValuePercentage(null);
            } else {
              getSalesAgentContract().setCommissionValueFixed(null);
            }
            getSalesAgent().addSalesAgentContract(getSalesAgentContract());
          }
          getSalesAgent().setSalesAgentType(1);
          getSalesAgent().setCompanyId(UserRuntimeView.instance().getCompany());

          if (key.equals("save")) {
            SalesAgentService.insertOrUpdate(main, getSalesAgent(), getAccountGroupList(), getTerritoryList());
          } else if (key.equals("clone")) {
            SalesAgentService.clone(main, getSalesAgent(), getAccountGroupList(), getTerritoryList());
          }
          main.commit("success." + key);
        }
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  public void updateSalesAgent(MainView main) {
    try {
      SalesAgentService.updateByPk(main, salesAgent);
      main.commit();
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }

  }

  /**
   * Delete one or many
   *
   * @param main
   * @return the page to display.
   */
  public String deleteSalesAgent(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(salesAgentSelected)) {
        SalesAgentService.deleteByPkArray(main, getSalesAgentSelected()); //many record delete from list
        main.commit("success.delete");
        salesAgentSelected = null;
      } else {
        SalesAgentService.deleteByPk(main, getSalesAgent());  //individual record delete from list or edit form
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

  public List<Department> departmentAuto(String filter) {
    return ScmLookupView.departmentAuto(filter);
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
   *
   * @param main
   */
  public void insertSalesAgentContact(MainView main) {
    try {
      if (company != null) {
        if (company.getFlag() == 2) {
          SalesAgentContractService.insertArray(main, getSalesAgentSelected(), UserRuntimeView.instance().getCompany());
        }
        Jsf.popupReturn(company, null);
      }
      main.commit();
    } catch (Throwable t) {
      main.rollback(t, "error.list");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param main
   * @return
   */
  public List<Designation> selectDesignationByDepartment(MainView main) {
    try {
      return DesignationService.selectDesignationByLevel(main, getSalesAgent().getDepartmentId().getId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void departmentSelectEvent(SelectEvent event) {
    Department depart = (Department) event.getObject();
    getSalesAgent().setDepartmentId(depart);
  }

  public void ajaxBehaviorEventHandler(AjaxBehaviorEvent event) {
  }

  public void commissionValueSelectEventHandler(AjaxBehaviorEvent event) {
    getSalesAgentContract().setCommissionValueFixed(null);
    getSalesAgentContract().setCommissionValuePercentage(null);
  }

  public SalesAgentContract getSalesAgentContract() {
    if (salesAgentContract == null) {
      salesAgentContract = new SalesAgentContract();
    }
    return salesAgentContract;
  }

  public void setSalesAgentContract(SalesAgentContract salesAgentContract) {
    this.salesAgentContract = salesAgentContract;
  }

  public List<SelectItemInteger> getSelectItemYesNo() {
    List<SelectItemInteger> listYesNo = new ArrayList<>();
    SelectItemInteger si1 = new SelectItemInteger();
    si1.setItemLabel("Yes");
    si1.setIntValue(1);
    listYesNo.add(si1);
    SelectItemInteger si2 = new SelectItemInteger();
    si2.setItemLabel("No");
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

  public void ajaxBehaviorEventHandler() {
  }

  public boolean isFixedCommissionByRange() {
    if (getSalesAgentContract() != null && getSalesAgentContract().getCommissionType() != null && getSalesAgentContract().getCommissionByRange() != null) {
      return (getSalesAgentContract().getCommissionType() == 1 && getSalesAgentContract().getCommissionByRange() == 2);
    }
    return false;
  }

  public boolean isPercentageCommissionByRange() {
    if (getSalesAgentContract() != null && getSalesAgentContract().getCommissionType() != null && getSalesAgentContract().getCommissionByRange() != null) {
      return (getSalesAgentContract().getCommissionType() == 2 && getSalesAgentContract().getCommissionByRange() == 2);
    }
    return false;
  }

  public boolean isCommisionByRange() {
    if (getSalesAgentContract() != null) {
      return (getSalesAgentContract().getCommissionByRange() != null && getSalesAgentContract().getCommissionByRange() == 1);
    }
    return false;
  }

  public boolean isFixedCommissionValue() {
    return (getSalesAgentContract().getCommissionType() == 1);
  }

  public boolean isPercentageCommissionValue() {
    return (getSalesAgentContract().getCommissionType() == 2);
  }

  /**
   *
   * @param main
   * @param salesAgentContractComm
   */
  public void saveCommission(MainView main, SalesAgentContractComm salesAgentContractComm) {
    try {
      boolean isNew = salesAgentContractComm.getId() == null;
      if (isNew) {
        getSalesAgentContract().getSalesAgentContractComm().add(salesAgentContractComm);
      } else {
        SalesAgentContractCommService.insertOrUpdate(main, salesAgentContractComm);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @return
   */
  public String addNewSalesAgentCommision() {
    if (StringUtil.isEmpty(getSalesAgentContract().getSalesAgentContractComm())) {
      actionAddNewCommissionRange(null);
    } else {
      actionAddNewCommissionRange(getSalesAgentContract().getSalesAgentContractComm().get(getSalesAgentContract().getSalesAgentContractComm().size() - 1));
    }
    return null;
  }

  /**
   *
   * @param main
   * @param selectedCommissionRange
   */
  public void deleteContractCommissionRange(MainView main, SalesAgentContractComm selectedCommissionRange) {
    try {
      if (getSalesAgentContract().getSalesAgentContractComm() != null) {
        if (getSalesAgentContract() != null && getSalesAgentContract().getId() != null) {
          if (selectedCommissionRange.getId() != null) {
            SalesAgentContractCommService.deleteByPk(main, selectedCommissionRange);
            main.commit();
          }
          getSalesAgentContract().getSalesAgentContractComm().remove(selectedCommissionRange);
        } else {
          getSalesAgentContract().getSalesAgentContractComm().remove(selectedCommissionRange);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param main
   * @param filter
   * @return
   */
//  public List<Services> salesAgentServicesAuto(MainView main, String filter) {
//    if (getServicesList() != null && !getServicesList().isEmpty()) {
//      if (defaultServices == null) {
//        defaultServices = ServicesService.selectByPk(main, getServicesList().get(0));
//      }
//      return ScmLookupExtView.salesAgentServicesByServicesAuto(filter);
//    } else {
//      return ScmLookupExtView.salesAgentServicesByServicesAuto1(filter);
//    }
//  }
  /**
   * Primefaces ajax itemSelect event handler. This method removes duplicate Account objects from the accoutList object.
   *
   * @param event
   */
//  public void onServicesSelect(SelectEvent event) {
//    Set<Services> tempSet = new HashSet<>();
//    List<Services> tempList;
//    if (getServicesList().size() > 1) {
//      for (Services serv : getServicesList()) {
//        tempSet.add(serv);
//      }
//      tempList = new ArrayList<>(tempSet);
//      setServicesList(tempList);
//    }
//  }
  /**
   *
   * @param event
   */
  public void onServicesUnSelect(UnselectEvent event) {
//    if (getServicesList().size() == 1) {
//      getServicesList().clear();
//      setDefaultServices(null);
//    }
  }

  /**
   *
   * @param main
   */
  public void actionInsertOrUpdateSalesAgentCommissionRange(MainView main) {
    try {
      Integer rowIndex = Jsf.getParameterInt("rownumber");
      setCurrentRow(rowIndex);
      if (getSalesAgentContract() != null && getSalesAgentContract().getSalesAgentContractComm() != null && !getSalesAgentContract().getSalesAgentContractComm().isEmpty()) {
        SalesAgentContractComm salesAgentContractComm = getSalesAgentContract().getSalesAgentContractComm().get(rowIndex);
        if (getSalesAgentContract().getId() != null) {
          salesAgentContractComm.setSalesAgentContractId(getSalesAgentContract());
          SalesAgentContractCommService.insertOrUpdate(main, salesAgentContractComm);
        } else {
          getSalesAgentContract().getSalesAgentContractComm().get(rowIndex).setSalesAgentContractId(getSalesAgentContract());
        }

        if (rowIndex == getSalesAgentContract().getSalesAgentContractComm().size() - 1) {
          actionAddNewCommissionRange(salesAgentContractComm);
        }
      }

    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param commitionRange
   */
  public void actionAddNewCommissionRange(SalesAgentContractComm commitionRange) {
    SalesAgentContractComm salesAgentContractComm = new SalesAgentContractComm();
    if (commitionRange != null) {
      if (commitionRange.getRangeTo() != null) {
        salesAgentContractComm.setRangeFrom(commitionRange.getRangeTo() + 1);
      }
    }
    if (!getSalesAgentContract().getSalesAgentContractComm().isEmpty()) {
      Collections.sort(getSalesAgentContract().getSalesAgentContractComm());
    }
    salesAgentContractComm.setSalesAgentContractId(getSalesAgentContract());
    getSalesAgentContract().getSalesAgentContractComm().add(salesAgentContractComm);
  }

  /**
   *
   * @param salesAgentContractComm
   * @param rangeTo
   * @return
   */
  private boolean isValidToRange(SalesAgentContractComm salesAgentContractComm, Integer rangeTo) {
    return ((salesAgentContractComm.getRangeFrom() != null) && (rangeTo > salesAgentContractComm.getRangeFrom()));
  }

  /**
   *
   * @param salesAgentContractCommList
   * @param rowIndex
   * @param rangeTo
   * @return
   */
  private int isRangeDuplicated(List<SalesAgentContractComm> salesAgentContractCommList, Integer rowIndex, Integer rangeTo) {
    String range1;
    String range2;
    int rcount;
    int index1 = 0;
    int index2;

    for (SalesAgentContractComm salesAgentContract : salesAgentContractCommList) {
      if (rowIndex == index1) {
        range1 = salesAgentContract.getRangeFrom() + "" + rangeTo;
      } else {
        range1 = salesAgentContract.getRangeFrom() + "" + salesAgentContract.getRangeTo();
      }
      rcount = 0;
      index2 = 0;
      index1++;
      for (SalesAgentContractComm sacc : salesAgentContractCommList) {
        if (index2 == rowIndex) {
          range2 = sacc.getRangeFrom() + "" + rangeTo;
        } else {
          range2 = sacc.getRangeFrom() + "" + salesAgentContract.getRangeTo();
        }
        if (range1.equals(range2)) {
          rcount++;
        }
        if (rcount > 1) {
          return index2;
        }
        index2++;
      }
    }
    return -1;
  }

  /**
   *
   * @param rowIndex
   * @return
   */
  public boolean rangeToRequired(int rowIndex) {
    List<SalesAgentContractComm> list = getSalesAgentContract().getSalesAgentContractComm();
    if (list != null) {
      if (list.size() == 1) {
        return false;
      } else if ((list.size() - 1) != rowIndex) {
        return true;
      }
    }
    return false;
  }

  /**
   *
   * @param context
   * @param component
   * @param value
   * @throws ValidatorException
   */
  public void validateRangeFrom(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    Integer intValue = (Integer) value;
    int rowIndex = Integer.parseInt(component.getClientId().split(":")[1]);
    SalesAgentContractComm prevSalesAgentContractComm;
    if (intValue != null && rowIndex != 0) {
      prevSalesAgentContractComm = getSalesAgentContract().getSalesAgentContractComm().get(rowIndex - 1);
      if (prevSalesAgentContractComm.getRangeTo() != null && intValue <= prevSalesAgentContractComm.getRangeTo()) {
        Jsf.error(component, "error.invalid.rangefrom");
      }
    }
  }

  /**
   *
   * @param context
   * @param component
   * @param value
   * @throws ValidatorException
   */
  public void validateRangeTo(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    Integer intValue = (Integer) value;
    int rowIndex = Integer.parseInt(component.getClientId().split(":")[1]);
    SalesAgentContractComm salesAgentContractCom = getSalesAgentContract().getSalesAgentContractComm().get(rowIndex);

    if (intValue != null) {
      if (!isValidToRange(salesAgentContractCom, intValue)) {
        Jsf.error(component, "error.invalid.rangeto");
      }
    }

    int repeatIndex = isRangeDuplicated(getSalesAgentContract().getSalesAgentContractComm(), rowIndex, intValue);
    if (repeatIndex == rowIndex) {
      Jsf.error(component, "error.range.duplicated");
    }
  }

  public List<SalesAgent> getSalesAgentList(MainView main) {
    try {
      if (UserRuntimeView.instance().getCompany() != null) {
        salesAgentList = SalesAgentService.listPagedByCompany(main, UserRuntimeView.instance().getCompany(), getAccountGroup(), getTerritory());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return salesAgentList;
  }

  public List<AccountGroup> accountGroupBySalesAgentAuto(String filter) {

    MainView main = Jsf.getMain();
    try {
      if (getAccountGroupList() != null && !getAccountGroupList().isEmpty()) {
        if (defaultAccountGroup == null) {
          defaultAccountGroup = AccountGroupService.selectByPk(main, getAccountGroupList().get(0));
        }
      }
      return ScmLookupExtView.accountGroupBySalesAgentAuto(filter, UserRuntimeView.instance().getCompany().getId());
//      } else {
//        return ScmLookupExtView.accountGroupBySalesAgentAuto1(filter);
//      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;

  }

  public void onAccountGroupSelect(SelectEvent event) {
    Set<AccountGroup> tempSet = new HashSet<>();
    List<AccountGroup> tempList;
    if (getAccountGroupList().size() > 1) {
      for (AccountGroup acntgrp : getAccountGroupList()) {
        tempSet.add(acntgrp);
      }
      tempList = new ArrayList<>(tempSet);
      setAccountGroupList(tempList);
    }
  }

  /**
   *
   * @param event
   */
  public void onAccountGroupUnSelect(UnselectEvent event) {
    if (getAccountGroupList().size() == 1) {
      getAccountGroupList().clear();
      setDefaultAccountGroup(null);
    }
  }

  public void salesAgentValidation(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    List<AccountGroup> list = (List<AccountGroup>) value;
    String validateParam = (String) Jsf.getParameter("validateSalesAgents");
    if (!StringUtil.isEmpty(validateParam)) {
      if (list != null && list.size() <= 1) {
        Jsf.error(component, "error.sales.agent.exist");
      } else if (list != null && list.size() > 1) {
        AppEm em = null;
        try {
          em = AppFactory.getNewEm();
          long exist = SalesAgentService.isSalesAgentExist(em, getSalesAgent().getId(), list);
          if (exist > 0) {
            Jsf.error(component, "error.sales.agent.exist");
          }
        } finally {
          if (em != null) {
            em.close();
          }
        }
      }
    }
  }

  public List<Territory> territoryBySalesAgentAuto(String filter) {

    List<Territory> territoryList = null;
    MainView main = Jsf.getMain();
    try {
      if (getTerritoryList() != null && !getTerritoryList().isEmpty()) {
        if (defaultTerritory == null) {
          defaultTerritory = TerritoryService.selectByPk(main, getTerritoryList().get(0));
        }
      }
      territoryList = ScmLookupExtView.territoryBySalesAgentAuto(filter, UserRuntimeView.instance().getCompany().getId());
      //   } else {
//        return ScmLookupExtView.territoryBySalesAgentAuto1(filter);
      // }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return territoryList;
  }

  public void onTerritorySelect(SelectEvent event) {
    Set<Territory> tempSet = new HashSet<>();
    List<Territory> tempList;
    if (getTerritoryList().size() > 1) {
      for (Territory ter : getTerritoryList()) {
        tempSet.add(ter);
      }
      tempList = new ArrayList<>(tempSet);
      setTerritoryList(tempList);
    }
  }

  /**
   *
   * @param event
   */
  public void onTerritoryUnSelect(UnselectEvent event) {
    if (getTerritoryList().size() == 1) {
      getTerritoryList().clear();
      setDefaultTerritory(null);
    }
  }

  public void territoryValidation(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    MainView main = Jsf.getMain();
    try {
      List<Territory> list = (List<Territory>) value;
      String validateParam = (String) Jsf.getParameter("validateSTerritory");
      if (!StringUtil.isEmpty(validateParam)) {
        if (list != null && list.size() <= 1) {
          Jsf.error(component, "error.territory.exist");
        } else if (list != null && list.size() > 1) {

          long exist = SalesAgentService.isTerritoryExist(main.em(), getSalesAgent().getId(), list);
          if (exist > 0) {
            Jsf.error(component, "error.territory.exist");
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void verifyPhone(MainView main) {
    try {
      Notify.otpSalesAgent(main, UserRuntimeView.instance().getCompany().getCompanySettings(), getSalesAgent());
      SalesAgentService.insertOrUpdate(main, getSalesAgent(), null, null);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    main.close();

  }

  public void verifyOtp(MainView main) {
    try {
      if (getVerifyOtp() != null && getSalesAgent().getOtp().intValue() == getVerifyOtp()) {
        getSalesAgent().setPhoneVerified(1);
        SalesAgentService.insertOrUpdate(main, getSalesAgent(), null, null);
        setVerifyOtp(null);
        main.commit("success.save");
      } else {
        main.error("invalid.otp");
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void resendOtp(MainView main) {
    try {
      Notify.otpSalesAgent(main, UserRuntimeView.instance().getCompany().getCompanySettings(), getSalesAgent());
      setVerifyOtp(null);
      main.commit("success.send.sms");
    } catch (Throwable t) {
      main.rollback(t, "error.send.sms");
    } finally {
      main.close();
    }

  }

  public void disableSMS(MainView main) {
    try {
      getSalesAgent().setPhoneVerified(0);
      getSalesAgent().setOtp(null);
      SalesAgentService.insertOrUpdate(main, getSalesAgent(), null, null);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void gstinChangeEventHandler() {
    try {
      getSalesAgent().setPanNo(getSalesAgent().getGstNo().substring(2, 12));
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  public void createUserCode() {
    String code = (getSalesAgent().getName()).replaceAll("\\s+", "");
    getSalesAgent().setUserCode(code.toLowerCase());
  }

  public List getSalesAgentAccountGroup(MainView main) {
    try {
      return SalesAgentService.selectAgentAccountGroupByCompany(main, UserRuntimeView.instance().getCompany().getId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public List<Territory> getSalesAgentTerritory(MainView main) {
    try {
      return SalesAgentService.selectAgentTerritoryByCompany(main, UserRuntimeView.instance().getCompany().getId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public List<Department> selectCompanyDepartment(MainView main) {
    try {
      return DepartmentService.selectCompanyDepartment(main, company.getFlag());
    } finally {
      main.close();
    }
  }

  public AccountGroup getAccountGroup() {
    return accountGroup;
  }

  public void setAccountGroup(AccountGroup accountGroup) {
    this.accountGroup = accountGroup;
  }

  public Territory getTerritory() {
    return territory;
  }

  public void setTerritory(Territory territory) {
    this.territory = territory;
  }

  public void setCompanyFlag(Integer flag) {
    getCompany().setFlag(flag.intValue());
  }

  public Integer getVerifyOtp() {
    return verifyOtp;
  }

  public void setVerifyOtp(Integer verifyOtp) {
    this.verifyOtp = verifyOtp;
  }

}
