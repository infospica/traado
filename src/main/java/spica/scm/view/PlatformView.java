/*
 * @(#)PlatformView.java	1.0 Thu Apr 20 15:04:02 IST 2017
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import spica.scm.common.PlatformData;
import spica.scm.common.PlatformSummary;
import spica.scm.common.SelectItem;
import spica.scm.domain.Account;
import spica.scm.domain.Company;
import spica.scm.domain.Platform;
import spica.scm.domain.PlatformDescription;
import spica.scm.domain.PlatformSource;
import spica.scm.domain.ProductEntry;
import spica.scm.export.ExcelSheet;
import spica.scm.service.PlatformService;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 * PlatformView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 20 15:04:02 IST 2017
 */
@Named(value = "platformView")
@ViewScoped
public class PlatformView implements Serializable {

  private transient Platform platform;	//Domain object/selected Domain.
  private transient LazyDataModel<Platform> platformCreditLazyModel; 	//For lazy loading datatable.
  private transient LazyDataModel<Platform> platformDebitLazyModel; 	//For lazy loading datatable.
  private transient Platform[] platformCreditSelected;	 //Selected Domain Array
  private transient Platform[] platformDebitSelected;
  private transient PlatformSource[] selectSource;
  private transient PlatformDescription[] selectedDebitDescription;
  private transient PlatformDescription[] selectedCreditDescription;
  private transient Integer selectedStatus;
  private transient Long creditTotalRecords;
  private transient Long debitTotalRecords;
  //private int selectedYear;
  private int selectedSummary;
  private transient TreeNode rootCredit;
  private transient TreeNode rootDebit;
  private transient TreeNode rootFund;

  private transient TreeNode[] platformCreditTreeSelected;
  private transient TreeNode[] creditTreeSelected;//Selected Domain Array
  private transient TreeNode[] debitTreeSelected;
  private transient TreeNode[] platformDebitTreeSelected;

  private transient Double toRealize;
  private transient Double fund;
  private transient Double realized;
  private transient Account account;
  private transient Integer viewType;
  private transient ProductEntry productEntrySelected;
  private transient Double debitSelectedAmount;
  private transient Double creditSelectedAmount;

  /**
   * Default Constructor.
   */
  public PlatformView() {
    super();
  }

  /**
   * Return Platform.
   *
   * @return Platform.
   */
  public Platform getPlatform() {
    if (platform == null) {
      platform = new Platform();
    }
    return platform;
  }

  /**
   * Set Platform.
   *
   * @param platform.
   */
  public void setPlatform(Platform platform) {
    this.platform = platform;
  }

  public Account getAccount() {
    account = UserRuntimeView.instance().getAccount();
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchPlatform(MainView main, String viewType) {
    Jsf.execute("toTop();");
    //this.main = main;
    selectSource = null;
    //  selectedDescription = null;
    selectedSummary = SystemConstants.PLATFORM_SUMMARY_VIEW;
    selectedStatus = SystemRuntimeConfig.PLATFORM_STATUS_NEW.getId();
    resetTree();
    if (!StringUtil.isEmpty(viewType)) {
      try {
        UserRuntimeView.instance().getAccountCurrent();
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getPlatform().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setPlatform((Platform) PlatformService.selectByPk(main, getPlatform()));
        } else if (ViewTypes.isList(viewType)) {
          setSelectedCreditDescription(null);
          setSelectedDebitDescription(null);
          loadPlatformCreditList(main);
          loadPlatformDebitList(main);
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
   * Create platformLazyModel.
   *
   * @param main
   */
  private void loadPlatformCreditList(final MainView main) {
    //  countYear();
    if (platformCreditLazyModel == null) {
      platformCreditLazyModel = new LazyDataModel<Platform>() {
        private List<Platform> list;

        @Override
        public List<Platform> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (getAccount() != null) {
              //making total records to null cause these is value in first fetch
              main.getPageData().setTotalRecords(creditTotalRecords);
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = PlatformService.listPagedCredit(main, getAccount(), getSelectSource(), getSelectedCreditDescription(), getSelectedStatus(), getSelectedSummary(), null, productEntrySelected, false);
              main.commit(platformCreditLazyModel, first, pageSize);
              creditTotalRecords = main.getPageData().getTotalRecords();

            }
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Platform platform) {
          return platform.getId();
        }

        @Override
        public Platform getRowData(String rowKey) {
          if (list != null) {
            for (Platform obj : list) {
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

  private void loadPlatformDebitList(final MainView main) {
    if (platformDebitLazyModel == null) {
      platformDebitLazyModel = new LazyDataModel<Platform>() {
        private List<Platform> list;

        @Override
        public List<Platform> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (getAccount() != null) {
              main.getPageData().setTotalRecords(getDebitTotalRecords());
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = PlatformService.listPagedDebit(main, getAccount(), getSelectSource(), getSelectedDebitDescription(), getSelectedStatus(), getSelectedSummary(), null, productEntrySelected, false);
              main.commit(platformDebitLazyModel, first, pageSize);
              setDebitTotalRecords(main.getPageData().getTotalRecords());
            }

          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Platform platform) {
          return platform.getId();
        }

        @Override
        public Platform getRowData(String rowKey) {
          if (list != null) {
            for (Platform obj : list) {
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
    String SUB_FOLDER = "scm_platform/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String savePlatform(MainView main) {
    return saveOrClonePlatform(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String clonePlatform(MainView main) {
    main.setViewType("newform");
    return saveOrClonePlatform(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrClonePlatform(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            PlatformService.insertOrUpdate(main, getPlatform());
            break;
          case "clone":
            PlatformService.clone(main, getPlatform());
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
   *
   * @param main
   */
  public void platformSelfAdjustPopup(MainView main) {
    PlatformData pd = new PlatformData();
    try {
      if (!StringUtil.isEmpty(getPlatformCreditSelected()) && !StringUtil.isEmpty(getPlatformDebitSelected()) || creditTreeSelected.length > 0 && debitTreeSelected.length > 0) {
        //if (getPlatformCreditTreeSelected().length == 0 && getPlatformDebitTreeSelected().length == 0) {
        if (creditTreeSelected != null && debitTreeSelected != null) {
          for (TreeNode tc : creditTreeSelected) {
            if (tc.getData() != null) {
              Platform p = (Platform) tc.getData();
              if (p.getLevel() != null && (p.getLevel() == SystemConstants.FIRST_LEVEL || p.getLevel() == SystemConstants.SECOND_LEVEL
                      || p.getLevel() == SystemConstants.THIRD_LEVEL)) {
                List<Platform> creditList = PlatformService.listPagedCredit(main, getAccount(), getSelectSource(), getSelectedCreditDescription(), getSelectedStatus(), SystemConstants.PLATFORM_DETAILED_VIEW, p, productEntrySelected, true);
                pd.getCreditList().addAll(creditList);
              } else {
                if (p.getId() != null && p.getLevel() == SystemConstants.FOURTH_LEVEL) {
                  if (!pd.getCreditList().contains(p)) {
                    pd.getCreditList().add(p);
                  }
                }
              }
            }
          }
          for (TreeNode tc : debitTreeSelected) {
            if (tc.getData() != null) {
              Platform p = (Platform) tc.getData();
              if (p.getLevel() != null && (p.getLevel() == SystemConstants.FIRST_LEVEL || p.getLevel() == SystemConstants.SECOND_LEVEL
                      || p.getLevel() == SystemConstants.THIRD_LEVEL)) {
                List<Platform> debitList = PlatformService.listPagedDebit(main, getAccount(), getSelectSource(), getSelectedDebitDescription(), getSelectedStatus(), SystemConstants.PLATFORM_DETAILED_VIEW, p, productEntrySelected, true);
                pd.getDebitList().addAll(debitList);
              } else {
                if (p.getId() != null && p.getLevel() == SystemConstants.FOURTH_LEVEL) {
                  if (!pd.getDebitList().contains(p)) {
                    pd.getDebitList().add(p);
                  }
                }
              }
            }
          }
        }
        if (!StringUtil.isEmpty(getPlatformCreditSelected()) && !StringUtil.isEmpty(getPlatformDebitSelected())) {
          pd.setCreditList(Arrays.asList(getPlatformCreditSelected()));
          pd.setDebitList(Arrays.asList(getPlatformDebitSelected()));
        }
      } else {
        main.error("select.both");
        return;
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    pd.setSettlementType(SystemRuntimeConfig.PLATFORM_SETTLEMENT_TYPE_SELF);
    Jsf.popupForm(FileConstant.PLATFORM_SETTLEMENT, pd); // opens a new form if id is null else edit
  }

  public void platformIssueCreditPopup() {
    if (StringUtil.isEmpty(getPlatformCreditSelected())) {
      Jsf.error("select.credit");
      return;
    }
    PlatformData pd = new PlatformData();
    pd.setCreditList(Arrays.asList(getPlatformCreditSelected()));
    pd.setDebitList(Arrays.asList(getPlatformDebitSelected()));
    pd.setSettlementType(SystemRuntimeConfig.PLATFORM_SETTLEMENT_TYPE_CREDIT_NOTE);
    Jsf.popupForm(FileConstant.PLATFORM_SETTLEMENT, pd); // opens a new form if id is null else edit

  }

  public void platformIssueDebitPopup() {
    if (StringUtil.isEmpty(getPlatformDebitSelected())) {
      Jsf.error("select.debit");
      return;
    }
    PlatformData pd = new PlatformData();
    pd.setCreditList(Arrays.asList(getPlatformCreditSelected()));
    pd.setDebitList(Arrays.asList(getPlatformDebitSelected()));
    pd.setSettlementType(SystemRuntimeConfig.PLATFORM_SETTLEMENT_TYPE_DEBIT_NOTE);
    Jsf.popupForm(FileConstant.PLATFORM_SETTLEMENT, pd); // opens a new form if id is null else edit

  }

  public void debitDescriotionFilterEvent() {
    if (selectedSummary == SystemConstants.PLATFORM_SUMMARY_VIEW) {
      rootDebit = null;
    } else if (selectedSummary == SystemConstants.PLATFORM_DETAILED_VIEW) {
      creditTotalRecords = null;
      debitTotalRecords = null;
      Jsf.getMain().getPageData().reset();
    }
  }

  public void creditDescriotionFilterEvent() {
    if (selectedSummary == SystemConstants.PLATFORM_SUMMARY_VIEW) {
      rootCredit = null;
    } else if (selectedSummary == SystemConstants.PLATFORM_DETAILED_VIEW) {
      creditTotalRecords = null;
      debitTotalRecords = null;
      Jsf.getMain().getPageData().reset();
    }
  }

  public void platformPopupReturned() {
    resetTree();
  }

  public void sourceFilter(AjaxBehaviorEvent event) {
    // selectSource = (PlatformSource) event.getSource();
    resetTree();
  }

  public void descriptionFilter(AjaxBehaviorEvent event) {
    // selectedDescription = (PlatformDescription) event.getObject();
    resetTree();
  }

//  public void statusFilter(SelectEvent event) {
//    selectedStatus = (Integer) event.getObject();
//    resetTree();
//  }
  public void statusFilter() {
    resetTree();
  }

  public void summaryFilter() {
    //selectedSummary = (int) event.getObject();
    resetTree();
  }

  public void resetTree() {
    creditSelectedAmount = 0.0;
    debitSelectedAmount = 0.0;
    creditTreeSelected = null;
    debitTreeSelected = null;
    if (selectedSummary == SystemConstants.PLATFORM_SUMMARY_VIEW) {
      rootCredit = null;
      rootDebit = null;

    } else if (selectedSummary == SystemConstants.PLATFORM_DETAILED_VIEW) {
      //    platformCreditLazyModel = null;
      //    platformDebitLazyModel = null;
      creditTotalRecords = null;
      debitTotalRecords = null;
      Jsf.getMain().getPageData().reset();
    } else if (selectedSummary == SystemConstants.PLATFORM_FUND_VIEW) {
      rootFund = null;
    }
  }

  /**
   * Return LazyDataModel of Platform.
   *
   * @return
   */
  public LazyDataModel<Platform> getPlatformCreditLazyModel() {
    return platformCreditLazyModel;
  }

  public LazyDataModel<Platform> getPlatformDebitLazyModel() {
    return platformDebitLazyModel;
  }

  public Platform[] getPlatformCreditSelected() {
    return platformCreditSelected;
  }

  public void setPlatformCreditSelected(Platform[] platformCreditSelected) {
    this.platformCreditSelected = platformCreditSelected;
  }

  public Platform[] getPlatformDebitSelected() {
    return platformDebitSelected;
  }

  public void setPlatformDebitSelected(Platform[] platformDebitSelected) {
    this.platformDebitSelected = platformDebitSelected;
  }

  public PlatformSource[] getSelectSource() {
    return selectSource;
  }

  public void setSelectSource(PlatformSource[] selectSource) {
    this.selectSource = selectSource;
  }

  public PlatformDescription[] getSelectedDebitDescription() {
    return selectedDebitDescription;
  }

  public void setSelectedDebitDescription(PlatformDescription[] selectedDebitDescription) {
    this.selectedDebitDescription = selectedDebitDescription;
  }

  public PlatformDescription[] getSelectedCreditDescription() {
    return selectedCreditDescription;
  }

  public void setSelectedCreditDescription(PlatformDescription[] selectedCreditDescription) {
    this.selectedCreditDescription = selectedCreditDescription;
  }

  public Integer getSelectedStatus() {
    return selectedStatus;
  }

  public void setSelectedStatus(Integer selectedStatus) {
    this.selectedStatus = selectedStatus;
  }

  public int getSelectedSummary() {
    return selectedSummary;
  }

  public void setSelectedSummary(int selectedSummary) {
    this.selectedSummary = selectedSummary;
  }

  public TreeNode[] getPlatformCreditTreeSelected() {
    return platformCreditTreeSelected;
  }

  public void setPlatformCreditTreeSelected(TreeNode[] platformCreditTreeSelected) {
    this.platformCreditTreeSelected = platformCreditTreeSelected;
  }

  public TreeNode[] getPlatformDebitTreeSelected() {
    return platformDebitTreeSelected;
  }

  public void setPlatformDebitTreeSelected(TreeNode[] platformDebitTreeSelected) {
    this.platformDebitTreeSelected = platformDebitTreeSelected;
  }

//  private Double calculateAmount(TreeNode[] platformTreeSelected, boolean isDebit) {
//    List<Platform> processed = new ArrayList();
//    Double selectedAmount = null;
//    if (platformTreeSelected != null) {
//      selectedAmount = 0.0;
//      for (TreeNode t1 : platformTreeSelected) {
//        Platform p = (Platform) t1.getData();
//        if (p.getLevel() != null && p.getLevel().intValue() == SystemConstants.FIRST_LEVEL) {
//          if (!processed.contains(p)) {
//            boolean error = false;
//            if (t1.getChildren() != null) {
//              for (TreeNode child : t1.getChildren()) {
//                if (processed.contains(child)) {
//                  error = true;
//                  break;
//                }
//              }
//            }
//            if (!error) {
//              processed.add(p);
//              selectedAmount += isDebit ? p.getDebitAmountRequired() : p.getCreditAmountRequired();
//            }
//          }
//        } else if (p.getLevel() != null && (p.getLevel().intValue() == SystemConstants.SECOND_LEVEL
//                || p.getLevel().intValue() == SystemConstants.THIRD_LEVEL || p.getLevel().intValue() == SystemConstants.FOURTH_LEVEL)) {
//          if (!processed.contains(t1.getParent() != null ? t1.getParent().getData() : null)) {
//            boolean error = false;
//            if (t1.getChildren() != null) {
//              for (TreeNode child : t1.getChildren()) {
//                if (processed.contains(child)) {
//                  error = true;
//                  break;
//                }
//              }
//            }
//            if (!error) {
//              processed.add(p);
//              selectedAmount += isDebit ? p.getDebitAmountRequired() : p.getCreditAmountRequired();
//            }
//          }
//        }
//      }
//    }
//    return selectedAmount;
//  }
  private void calculateAmount(TreeNode[] platformTreeSelected, boolean isDebit) {
    if (platformTreeSelected != null) {
      if (isDebit) {
        debitSelectedAmount = 0.0;
      } else {
        creditSelectedAmount = 0.0;
      }
      for (TreeNode t1 : platformTreeSelected) {
        Platform p = (Platform) t1.getData();
        if (p != null) {
          if (isDebit) {
            debitSelectedAmount += p.getDebitAmountRequired() != null ? p.getDebitAmountRequired() : 0.0;
          } else {
            creditSelectedAmount += p.getCreditAmountRequired() != null ? p.getCreditAmountRequired() : 0.0;
          }
        }
      }
    }
  }

  public Double getToRealize() {
    return toRealize;
  }

  public void setToRealize(Double toRealize) {
    this.toRealize = toRealize;
  }

  public Double getFund() {
    return fund;
  }

  public void setFund(Double fund) {
    this.fund = fund;
  }

  public Double getRealized() {
    return realized;
  }

  public void setRealized(Double realized) {
    this.realized = realized;
  }

  public TreeNode getRootFund(MainView main) {
    List<Platform> fundListRealize = null;
    List<Platform> fundList = null;
    try {
      if (rootFund == null) {
        rootFund = new CheckboxTreeNode();
        if (getAccount() != null) {
          fundList = PlatformService.listPagedFund(main, getAccount(), getSelectSource(), getSelectedDebitDescription(), getSelectedStatus(), SystemConstants.PLATFORM_FUND_VIEW, null);

          rootFund = new CheckboxTreeNode(new Platform(), null);
          fund = 0.0;
          realized = 0.0;
          for (Platform pc : fundList) {
            //  pc.setIsParent(true);
            fund += pc.getCreditAmountRequired();
            pc.setLevel(SystemConstants.FIRST_LEVEL);
            TreeNode t1 = new CheckboxTreeNode(pc, rootFund);
            if (pc.getPlatformDescId().getId() != 4) {
              new CheckboxTreeNode(new Platform(), t1);
            }
            pc.setDisplayText(pc.getPlatformDescId().getTitle() + " (Fund)");
            // pc.getPlatformDescId().setTitle("Realized11");
            fundListRealize = PlatformService.listPagedFund(main, getAccount(), getSelectSource(), getSelectedDebitDescription(), getSelectedStatus(), 4, pc);
            if (StringUtil.isEmpty(fundListRealize)) {
              Platform plt = new Platform();
              plt.setDisplayText("Realized");
              plt.setLevel(SystemConstants.FIRST_LEVEL);
              new CheckboxTreeNode(plt, rootFund);
              //   new CheckboxTreeNode(plt, t2);
            } else {
              for (Platform pl : fundListRealize) {
                realized += pc.getCreditAmountRequired();
                pl.setDisplayText("Realized");
                pl.setLevel(SystemConstants.FIRST_LEVEL);
                TreeNode t2 = new CheckboxTreeNode(pl, rootFund);
                new CheckboxTreeNode(new Platform(), t2);
              }
            }
          }
          toRealize = fund - realized;
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return rootFund;
  }

  public void setRootFund(TreeNode rootFund) {
    this.rootFund = rootFund;
  }

  /**
   *
   * @param main
   * @return
   */
  public TreeNode getRootCredit(MainView main) {
    try {
      if (rootCredit == null) {
        rootCredit = new CheckboxTreeNode();
        if (getAccount() != null) {
          List<Platform> creditList = PlatformService.listPagedCredit(main, getAccount(), getSelectSource(), getSelectedCreditDescription(), getSelectedStatus(), SystemConstants.PLATFORM_SUMMARY_VIEW, null, productEntrySelected, false);
          rootCredit = createNode(creditList);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return rootCredit;
  }

  public void setRootCredit(TreeNode rootCredit) {
    this.rootCredit = rootCredit;
  }

  /**
   *
   * @param main
   * @return
   */
  public TreeNode getRootDebit(MainView main) {
    try {
      if (rootDebit == null) {
        rootDebit = new CheckboxTreeNode();
        if (getAccount() != null) {
          List<Platform> debitList = PlatformService.listPagedDebit(main, getAccount(), getSelectSource(), getSelectedDebitDescription(), getSelectedStatus(), SystemConstants.PLATFORM_SUMMARY_VIEW, null, productEntrySelected, false);
          rootDebit = createNode(debitList);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return rootDebit;
  }

  public void setRootDebit(TreeNode rootDebit) {
    this.rootDebit = rootDebit;
  }

  public TreeNode createNode(List<Platform> nodeList) {
    TreeNode node = new CheckboxTreeNode(new Platform(), null);
    for (Platform pc : nodeList) {
      pc.setLevel(SystemConstants.FIRST_LEVEL);
      setPlatformTitle(pc);
      TreeNode t1 = new CheckboxTreeNode(pc, node);
    }
    return node;
  }

  public void onNodeExpandCredit(NodeExpandEvent event) {
    TreeNode parent = event.getTreeNode();
    Platform p = (Platform) parent.getData();
    parent.getChildren().clear();
    MainView main = Jsf.getMain();
    try {
      if (getAccount() != null && p.getLevel() != SystemConstants.FOURTH_LEVEL) {
        List<Platform> creditList = PlatformService.listPagedCredit(main, getAccount(), getSelectSource(), getSelectedCreditDescription(), getSelectedStatus(), 4, p, productEntrySelected, false);
        createNodeOnExpand(creditList, parent, false);
        if (parent != null && creditTreeSelected != null && parent.getChildren() != null) {
          for (TreeNode selected : creditTreeSelected) {
            if (selected.equals(parent)) {
              if (isTreeNodeSelected(parent, creditTreeSelected)) {
                creditTreeSelected = removeSelected(parent, creditTreeSelected);
              }
              for (TreeNode child : parent.getChildren()) {
                creditTreeSelected = addSelected(child, creditTreeSelected);
              }
            }
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void onNodeCollapseCredit(NodeCollapseEvent event) {
    TreeNode parent = event.getTreeNode();
    Platform p = (Platform) parent.getData();
    if (p != null && p.getLevel() != null && p.getLevel().intValue() != SystemConstants.FOURTH_LEVEL) {
      if (parent != null && creditTreeSelected != null && parent.getChildren() != null) {
        if (!isTreeNodeSelected(parent, creditTreeSelected)) {
          if (isAllChildSelected(parent, creditTreeSelected)) {
            creditTreeSelected = addSelected(parent, creditTreeSelected);
            creditTreeSelected = removeTreeChild(parent, creditTreeSelected, true);
          } else {
            creditTreeSelected = removeTreeChild(parent, creditTreeSelected, true);
          }
        }
      }
    }
    calculateAmount(creditTreeSelected, false);
  }

  public void onNodeExpandFund(NodeExpandEvent event) {
    TreeNode parent = event.getTreeNode();
    Platform p = (Platform) parent.getData();
    parent.getChildren().clear();
    MainView main = Jsf.getMain();
    try {
      if (getAccount() != null) {
        List<Platform> fundList = PlatformService.listPagedFund(main, getAccount(), getSelectSource(), getSelectedDebitDescription(), getSelectedStatus(), 5, p);
        createNodeOnExpand(fundList, parent, false);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void onNodeExpandDebit(NodeExpandEvent event) {
    TreeNode parent = event.getTreeNode();
    Platform p = (Platform) parent.getData();
    parent.getChildren().clear();
    MainView main = Jsf.getMain();
    try {
      if (getAccount() != null && p.getLevel() != SystemConstants.FOURTH_LEVEL) {
        List<Platform> debitList = PlatformService.listPagedDebit(main, getAccount(), getSelectSource(), getSelectedDebitDescription(), getSelectedStatus(), 4, p, productEntrySelected, false);
        createNodeOnExpand(debitList, parent, true);
        if (parent != null && debitTreeSelected != null && parent.getChildren() != null) {
          for (TreeNode selected : debitTreeSelected) {
            if (selected.equals(parent)) {
              if (isTreeNodeSelected(parent, debitTreeSelected)) {
                debitTreeSelected = removeSelected(parent, debitTreeSelected);
              }
              for (TreeNode child : parent.getChildren()) {
                debitTreeSelected = addSelected(child, debitTreeSelected);
              }
            }
          }
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void onNodeCollapseDebit(NodeCollapseEvent event) {
    TreeNode parent = event.getTreeNode();
    Platform p = (Platform) parent.getData();
    if (p != null && p.getLevel() != null && p.getLevel().intValue() != SystemConstants.FOURTH_LEVEL) {
      if (parent != null && debitTreeSelected != null && parent.getChildren() != null) {
        if (!isTreeNodeSelected(parent, debitTreeSelected)) {
          if (isAllChildSelected(parent, debitTreeSelected)) {
            debitTreeSelected = addSelected(parent, debitTreeSelected);
            debitTreeSelected = removeTreeChild(parent, debitTreeSelected, true);
          } else {
            debitTreeSelected = removeTreeChild(parent, debitTreeSelected, true);
          }
        }
      }
    }
    calculateAmount(debitTreeSelected, true);
  }

  public void createNodeOnExpand(List<Platform> nodeList, TreeNode parent, boolean isDebit) {

    for (Platform platform : nodeList) {
      if (parent.getRowKey().length() == 1) {
        platform.setLevel(SystemConstants.SECOND_LEVEL);
        setPlatformTitle(platform);
        TreeNode tn = new CheckboxTreeNode(platform, parent);
        if (parent.getRowKey().length() == 1 && !SystemRuntimeConfig.INVOICE_DIFFERENCE.getId().equals(platform.getPlatformDescId().getId())) {
          TreeNode t = new CheckboxTreeNode(new Platform(), tn);
        }
      } else if ((parent.getRowKey().length() == 3 || parent.getRowKey().length() == 4) && platform.getProductEntryDetailId() != null) {
        platform.setLevel(SystemConstants.THIRD_LEVEL);
        setPlatformTitle(platform);
        TreeNode tn = new CheckboxTreeNode("3", platform, parent);
        if (!SystemRuntimeConfig.INVOICE_DIFFERENCE.getId().equals(platform.getPlatformDescId().getId())) {
          TreeNode t = new CheckboxTreeNode(new Platform(), tn);
          //    t.setSelectable(false);
        }
      } else if (parent.getRowKey().length() >= 5 && platform.getProductDetailId() != null) {
        platform.setLevel(SystemConstants.FOURTH_LEVEL);
        setPlatformTitle(platform);
        TreeNode tn = new CheckboxTreeNode("3", platform, parent);
        if (!SystemRuntimeConfig.INVOICE_DIFFERENCE.getId().equals(platform.getPlatformDescId().getId())) {
          TreeNode t = new CheckboxTreeNode(new Platform(), tn);
          //    t.setSelectable(false);
        }
      }
    }
  }

  public void showItems(Platform p) {
    if (p.getProductEntryId() != null) {
      ProductEntry pe = new ProductEntry();
      pe.setId(p.getProductEntryId().getId());
      Jsf.popupList(FileConstant.PRODUCT_MARGIN, pe);
    }
  }

  private void setPlatformTitle(Platform platForm) {
    String title = null;
    if (platForm != null) {
      if (platForm.getLevel() == SystemConstants.FIRST_LEVEL) {
        title = platForm.getPlatformDescId().getTitle();
      } else if (platForm.getLevel() == SystemConstants.SECOND_LEVEL) {
        title = platForm.getProductEntryId().getAccountInvoiceNo();
      } else if (platForm.getLevel() == SystemConstants.THIRD_LEVEL) {
        if (platForm.getSalesReturnId() != null) {
          title = platForm.getSalesReturnId().getInvoiceNo();
        } else if (platForm.getSalesInvoiceId() != null) {
          title = platForm.getSalesInvoiceId().getInvoiceNo();
        } else if (platForm.getPurchaseReturnId() != null) {
          title = platForm.getPurchaseReturnId().getInvoiceNo();
        } else if (platForm.getDebitCreditNoteId() != null) {
          title = platForm.getDebitCreditNoteId().getInvoiceNo();
        } else if (platForm.getProductMrpAdjustmentId() != null) {
          title = "MRP Adjustment";
        }
      } else if (platForm.getLevel() == SystemConstants.FOURTH_LEVEL) {
        if (platForm.getProductDetailId() != null) {
          title = platForm.getProductDetailId().getProductBatchId().getProductId().getProductName();
        }
      }
      platForm.setTitle(title);
    }
  }

  public List<SelectItem> getPlatformSummaryFilters() {
    List<SelectItem> platformSummaryFilters = new ArrayList<>();
    platformSummaryFilters.add(new SelectItem("Summary", SystemConstants.PLATFORM_SUMMARY_VIEW));
    platformSummaryFilters.add(new SelectItem("Detail", SystemConstants.PLATFORM_DETAILED_VIEW));
    platformSummaryFilters.add(new SelectItem("Fund", SystemConstants.PLATFORM_FUND_VIEW));
    return platformSummaryFilters;
  }

  public List<SelectItem> getPlatformStatusFilters() {
    List<SelectItem> platformStatusFilters = new ArrayList<>();
    platformStatusFilters.add(new SelectItem("New", SystemRuntimeConfig.PLATFORM_STATUS_NEW.getId()));
    platformStatusFilters.add(new SelectItem("Processed", SystemRuntimeConfig.PLATFORM_STATUS_PROCESSED.getId()));
    return platformStatusFilters;
  }

  public List<ProductEntry> getProductEntryList(MainView main) {
    List<ProductEntry> productEntryList = null;
    try {
      productEntryList = PlatformService.getProductEntryList(main, getAccount());
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return productEntryList;
  }

  public Long getCreditTotalRecords() {
    return creditTotalRecords;
  }

  public void setCreditTotalRecords(Long creditTotalRecords) {
    this.creditTotalRecords = creditTotalRecords;
  }

  public Long getDebitTotalRecords() {
    return debitTotalRecords;
  }

  public void setDebitTotalRecords(Long debitTotalRecords) {
    this.debitTotalRecords = debitTotalRecords;
  }

  public List<PlatformDescription> lookupPlatformDescription() {
    return ScmLookupExtView.lookupPlatformDescription();
  }

  public Integer getViewType() {
    return viewType;
  }

  public void setViewType(Integer viewType) {
    this.viewType = viewType;
  }

  public ProductEntry getProductEntrySelected() {
    return productEntrySelected;
  }

  public void setProductEntrySelected(ProductEntry productEntrySelected) {
    this.productEntrySelected = productEntrySelected;
  }

  private TreeNode[] addSelected(TreeNode node, TreeNode[] nodeArray) {
    if (nodeArray == null) {
      nodeArray = new TreeNode[0];
    }
    TreeNode[] newarr = new TreeNode[nodeArray.length + 1];
    if (node != null && nodeArray != null) {
      for (int i = 0; i < nodeArray.length; i++) {
        newarr[i] = nodeArray[i];
      }
      newarr[nodeArray.length] = node;
    }
    return newarr;
  }

  private TreeNode[] removeSelected(TreeNode node, TreeNode[] nodeArray) {
    TreeNode[] newarr = null;
    if (nodeArray.length == 0) {
      newarr = new TreeNode[0];
    } else {
      newarr = new TreeNode[nodeArray.length - 1];
    }
    int j = 0;
    if (node != null && nodeArray != null) {
      for (int i = 0; i < nodeArray.length; i++) {
        if (!nodeArray[i].equals(node) && newarr.length != 0 && j < newarr.length) {
          newarr[j++] = nodeArray[i];
        }
      }
    }
    return newarr;
  }

  public String levelColor(Integer level, boolean isDebit) {
    if (level != null) {
      if (level.intValue() == SystemConstants.FIRST_LEVEL) {
        if (isDebit) {
          return "#fd4921";
        } else {
          return "#09a77b";
        }
      } else if (level.intValue() == SystemConstants.SECOND_LEVEL) {
        return "#fdb81e";
      } else if (level.intValue() == SystemConstants.THIRD_LEVEL) {
        return "#337ab7";
      } else if (level.intValue() == SystemConstants.FOURTH_LEVEL) {
        return "#000";
      }
    }
    return "#000";
  }

//  private TreeNode[] selection(TreeNode[] selectedTreeNode) {
//    if (selectedTreeNode != null) {
//      for (TreeNode selected : selectedTreeNode) {
//        if (selected.getChildren() != null) {
//          for (TreeNode child : selected.getChildren()) {
//            if (!isSelected(selectedTreeNode, child)) {
//              selectedTreeNode = addSelected(child, selectedTreeNode);
//            }
//          }
//        }
//      }
//    }
//    return selectedTreeNode;
//  }
//
//  private boolean isSelected(TreeNode[] selectedTreeNode, TreeNode node) {
//    for (TreeNode selected : selectedTreeNode) {
//      if (selected.equals(node)) {
//        return true;
//      }
//    }
//    return false;
//  }
  public Double getDebitSelectedAmount() {

    return debitSelectedAmount;
  }

  public void setDebitSelectedAmount(Double debitSelectedAmount) {
    this.debitSelectedAmount = debitSelectedAmount;
  }

  public Double getCreditSelectedAmount() {
    return creditSelectedAmount;
  }

  public void setCreditSelectedAmount(Double creditSelectedAmount) {
    this.creditSelectedAmount = creditSelectedAmount;
  }

  public void debitNodeSelectEvent(NodeSelectEvent event) {
    TreeNode t = event.getTreeNode();
    if (t != null) {
      debitTreeSelected = removeTreeParent(t, debitTreeSelected);
      debitTreeSelected = removeTreeChild(t, debitTreeSelected, true);
      debitTreeSelected = addExpandedChild(event.getTreeNode(), debitTreeSelected);
    }
    calculateAmount(this.debitTreeSelected, true);
  }

  public void debitNodeUnSelectEvent(NodeUnselectEvent event) {
    TreeNode t = event.getTreeNode();
    if (t != null) {
      Platform p = (Platform) t.getData();
      if (t.getData() != null && p.getLevel() != null && p.getLevel().intValue() != SystemConstants.FIRST_LEVEL) {
        debitTreeSelected = removeAndSelectParent(t, debitTreeSelected, t.getParent(), 1);
      }
      debitTreeSelected = removeTreeChild(t, debitTreeSelected, true);
      if (isTreeNodeSelected(t, debitTreeSelected)) {
        debitTreeSelected = removeSelected(t, debitTreeSelected);
      }
      calculateAmount(this.debitTreeSelected, true);
    }
  }

  public void creditNodeSelectEvent(NodeSelectEvent event) {
    TreeNode t = event.getTreeNode();
    if (t != null) {
      creditTreeSelected = removeTreeParent(t, creditTreeSelected);
      creditTreeSelected = removeTreeChild(t, creditTreeSelected, true);
      creditTreeSelected = addExpandedChild(event.getTreeNode(), creditTreeSelected);
    }
    calculateAmount(this.creditTreeSelected, false);
  }

  public void creditNodeUnSelectEvent(NodeUnselectEvent event) {
    TreeNode t = event.getTreeNode();
    if (t != null) {
      Platform p = (Platform) t.getData();
      if (t.getData() != null && p.getLevel() != null && p.getLevel().intValue() != SystemConstants.FIRST_LEVEL) {
        creditTreeSelected = removeAndSelectParent(t, creditTreeSelected, t.getParent(), 1);
      }
      creditTreeSelected = removeTreeChild(t, creditTreeSelected, true);
      if (isTreeNodeSelected(t, creditTreeSelected)) {
        creditTreeSelected = removeSelected(t, creditTreeSelected);
      }
      calculateAmount(this.creditTreeSelected, false);
    }
  }

  private TreeNode[] removeTreeChild(TreeNode node, TreeNode[] selectedArrays, boolean select) {
    if (selectedArrays != null && selectedArrays.length != 0) {
      if (select) {
        if (node.getChildren() != null) {
          Platform p = (Platform) node.getData();
          if (p != null && p.getLevel() != null && p.getLevel().intValue() != SystemConstants.FOURTH_LEVEL) {
            for (TreeNode selected : selectedArrays) {
              if (node.getChildren().contains(selected)) {
                if (isTreeNodeSelected(selected, selectedArrays)) {
                  selectedArrays = removeSelected(selected, selectedArrays);
                }
              }
            }
            for (TreeNode child : node.getChildren()) {
              selectedArrays = removeTreeChild(child, selectedArrays, select);
            }
          }
        }
      }
    }
    return selectedArrays;
  }

  private TreeNode[] removeTreeParent(TreeNode node, TreeNode[] selectedArrays) {
    if (node.getChildren() != null && selectedArrays != null) {
      Platform p = (Platform) node.getData();
      if (p != null && p.getLevel() != null && p.getLevel().intValue() != SystemConstants.FIRST_LEVEL) {
        for (TreeNode parent : selectedArrays) {
          if (parent.equals(node.getParent())) {
            if (isTreeNodeSelected(parent, selectedArrays)) {
              selectedArrays = removeSelected(parent, selectedArrays);
              break;
            }
          }
          selectedArrays = removeTreeParent(node.getParent(), selectedArrays);
        }
      }
    }
    return selectedArrays;
  }

  private boolean isTreeNodeSelected(TreeNode node, TreeNode[] selectedArray) {
    if (selectedArray != null && node != null) {
      for (TreeNode selected : selectedArray) {
        if (selected.equals(node)) {
          return true;
        }
      }
      return false;
    }
    return false;
  }

  private boolean isAllChildSelected(TreeNode node, TreeNode[] selectedArray) {
    if (node.getChildren() != null && node.getChildren().size() > 0) {
      if (selectedArray != null && node != null) {
        for (TreeNode child : node.getChildren()) {
          if (!isTreeNodeSelected(child, selectedArray)) {
            if (isAllChildSelected(child, selectedArray)) {
              return true;
            } else {
              return false;
            }
          }
        }
        return true;
      }
    } else if (isTreeNodeSelected(node, selectedArray)) {
      return true;
    } else {
      return false;
    }
    return false;
  }

  private TreeNode[] removeAndSelectParent(TreeNode node, TreeNode[] selectedArrays, TreeNode firstParent, int count) {
    if (node != null && node.getParent() != null) {
      Platform p = (Platform) node.getData();
      if (isTreeNodeSelected(node.getParent(), selectedArrays)) {
        selectedArrays = removeSelected(node.getParent(), selectedArrays);
        for (TreeNode child : node.getParent().getChildren()) {
          selectedArrays = addSelected(child, selectedArrays);
        }
        selectedArrays = removeAndSelectParent(node, selectedArrays, firstParent, 1);
        if (count > 1) {
          for (TreeNode child : firstParent.getChildren()) {
            selectedArrays = addSelected(child, selectedArrays);
          }
        }
      } else {
        selectedArrays = removeAndSelectParent(node.getParent(), selectedArrays, firstParent, count + 1);
      }

    }
    return selectedArrays;
  }

  private TreeNode[] addExpandedChild(TreeNode node, TreeNode[] selectedArrays) {
    if (node.isExpanded()) {
      for (TreeNode child : node.getChildren()) {
        selectedArrays = addExpandedChild(child, selectedArrays);
      }
    } else {
      selectedArrays = addSelected(node, selectedArrays);
    }
    return selectedArrays;
  }

  public void export(MainView main) {
    try {
      List<PlatformSummary> platformSummaryList = PlatformService.getPlatformDetailList(main, getCompany(), getAccount(), productEntrySelected);
      ExcelSheet.createPlatform(main, platformSummaryList, getCompany(), getAccount(), productEntrySelected);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public Company getCompany() {
    return UserRuntimeView.instance().getCompany();
  }
}
