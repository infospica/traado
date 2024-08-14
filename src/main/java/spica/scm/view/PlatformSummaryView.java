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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;
import spica.fin.domain.DebitCreditNote;
import spica.fin.domain.DebitCreditNoteItem;
import spica.fin.domain.DebitCreditNotePlatform;
import spica.scm.domain.Account;
import spica.scm.domain.Platform;
import spica.scm.domain.PlatformDescription;
import spica.scm.domain.PlatformSource;
import spica.scm.domain.ProductEntry;
import spica.scm.service.PlatformService;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewTypes;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 * PlatformView
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Apr 20 15:04:02 IST 2017
 */
@Named(value = "platformSummaryView")
@ViewScoped
public class PlatformSummaryView implements Serializable {

  private transient Platform platform;	//Domain object/selected Domain.
  private transient Platform[] platformCreditSelected;	 //Selected Domain Array
  private transient Platform[] platformDebitSelected;
  private PlatformSource[] selectSource;
  private PlatformDescription[] selectedDebitDescription;
  private PlatformDescription[] selectedCreditDescription;
  private transient LinkedHashMap<Integer, Platform> selectedPlatform;
  private transient List<DebitCreditNoteItem> debitCreditNoteItemList;
  private Integer selectedStatus;
  private int selectedSummary;
  private TreeNode rootCredit;
  private TreeNode rootDebit;

  private DebitCreditNote debitCreditNote;

  private transient TreeNode[] platformCreditTreeSelected;	 //Selected Domain Array
  private transient TreeNode[] platformDebitTreeSelected;
  private transient Account account;
  private transient boolean debitNote;
  private transient boolean creditNote;

  @PostConstruct
  public void init() {
    debitCreditNote = (DebitCreditNote) Jsf.popupParentValue(DebitCreditNote.class);
    if (debitCreditNote != null) {
      debitNote = SystemConstants.DEBIT_NOTE.equals(debitCreditNote.getInvoiceType());
      creditNote = SystemConstants.CREDIT_NOTE.equals(debitCreditNote.getInvoiceType());
      debitCreditNote.setPlatformList(null);
      debitCreditNote.setDebitCreditNoteItemList(null);
      setSelectedSummary(SystemConstants.PLATFORM_SUMMARY_VIEW);
    }
  }

  /**
   * Default Constructor.
   */
  public PlatformSummaryView() {
    super();
  }

  public LinkedHashMap<Integer, Platform> getSelectedPlatform() {
    if (selectedPlatform == null) {
      selectedPlatform = new LinkedHashMap<>();
    }
    return selectedPlatform;
  }

  public void setSelectedPlatform(LinkedHashMap<Integer, Platform> selectedPlatform) {
    this.selectedPlatform = selectedPlatform;
  }

  public DebitCreditNote getDebitCreditNote() {
    return debitCreditNote;
  }

  public void setDebitCreditNote(DebitCreditNote debitCreditNote) {
    this.debitCreditNote = debitCreditNote;
  }

  public boolean isDebitNote() {
    return debitNote;
  }

  public void setDebitNote(boolean debitNote) {
    this.debitNote = debitNote;
  }

  public boolean isCreditNote() {
    return creditNote;
  }

  public void setCreditNote(boolean creditNote) {
    this.creditNote = creditNote;
  }

  public List<DebitCreditNoteItem> getDebitCreditNoteItemList() {
    if (debitCreditNoteItemList == null) {
      debitCreditNoteItemList = new ArrayList<>();
    }
    return debitCreditNoteItemList;
  }

  public void setDebitCreditNoteItemList(List<DebitCreditNoteItem> debitCreditNoteItemList) {
    this.debitCreditNoteItemList = debitCreditNoteItemList;
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
    return UserRuntimeView.instance().getAccount();
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
    //this.main = main;
    selectSource = null;
    //  selectedDescription = null;
    selectedSummary = SystemConstants.PLATFORM_SUMMARY_VIEW;
    // selectedYear = currentYear;
    selectedStatus = null;
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
          setDebitCreditNoteItemList(null);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
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

  public void platformPopupReturned() {
    resetTree();
  }

  public void sourceFilter(AjaxBehaviorEvent event) {
    resetTree();
  }

  public void descriptionFilter(AjaxBehaviorEvent event) {
    resetTree();
  }

  public void statusFilter(SelectEvent event) {
    selectedStatus = (Integer.parseInt((String) event.getObject()));
    resetTree();
  }

  public void summaryFilter(SelectEvent event) {
    selectedSummary = (int) event.getObject();
    resetTree();
  }

  public void resetTree() {
    rootCredit = null;
    rootDebit = null;
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

  /**
   *
   * @param main
   * @return
   */
  public TreeNode getRootCredit(MainView main) {
    try {
      if (rootCredit == null) {
        if (getAccount() != null) {
          List<Platform> creditList = PlatformService.selectPlatformByAccount(main, getAccount(), getSelectSource(), getSelectedCreditDescription(), getSelectedStatus(), SystemConstants.PLATFORM_SUMMARY_VIEW, null, PlatformService.PLATFORM_CREDIT);
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
        if (getAccount() != null) {
          List<Platform> debitList = PlatformService.selectPlatformByAccount(main, getAccount(), getSelectSource(), getSelectedDebitDescription(), getSelectedStatus(), SystemConstants.PLATFORM_SUMMARY_VIEW, null, PlatformService.PLATFORM_DEBIT);
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

  /**
   *
   * @param nodeList
   * @param parent
   */
  public void createNodeOnExpand(List<Platform> nodeList, TreeNode parent) {
    for (Platform platform : nodeList) {
      if (parent.getRowKey().length() == 1) {
        platform.setLevel(SystemConstants.SECOND_LEVEL);
        setPlatformTitle(platform);
        TreeNode tn = new CheckboxTreeNode(platform, parent);
        if (parent.getRowKey().length() == 1 && !SystemRuntimeConfig.INVOICE_DIFFERENCE.getId().equals(platform.getPlatformDescId().getId())) {
          TreeNode t = new CheckboxTreeNode(new Platform(), tn);
        }
      } else if (parent.getRowKey().length() >= 3 && platform.getProductDetailId() != null) {
        platform.setLevel(SystemConstants.THIRD_LEVEL);
        setPlatformTitle(platform);
        TreeNode tn = new CheckboxTreeNode("3", platform, parent);
        if (parent.getRowKey().length() == 1 && !SystemRuntimeConfig.INVOICE_DIFFERENCE.getId().equals(platform.getPlatformDescId().getId())) {
          TreeNode t = new CheckboxTreeNode(new Platform(), tn);
        }
      }
    }
  }

  public TreeNode createNode(List<Platform> nodeList) {
    TreeNode root = new CheckboxTreeNode(new Platform(), null);
    for (Platform pc : nodeList) {
      pc.setLevel(1);
      setPlatformTitle(pc);
      TreeNode t1 = new CheckboxTreeNode(pc, root);
    }
    return root;
  }

  public void onNodeExpandCredit(NodeExpandEvent event) {
    TreeNode parent = event.getTreeNode();
    addChildNodes(parent, false);
  }

  public void onNodeExpandDebit(NodeExpandEvent event) {
    TreeNode parent = event.getTreeNode();
    addChildNodes(parent, true);
  }

  /**
   *
   * @param parent
   * @param isDebit
   */
  private void addChildNodes(TreeNode parent, boolean isDebit) {
    List<Platform> platformList = null;
    Platform platform = (Platform) parent.getData();
    parent.getChildren().clear();
    try {
      if (getAccount() != null && platform.getLevel() != SystemConstants.THIRD_LEVEL) {
        platformList = getChildPlatformList(platform, isDebit);
        createNodeOnExpand(platformList, parent);
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /**
   *
   * @param platForm
   */
  private void setPlatformTitle(Platform platForm) {
    String title = null;
    if (platForm != null) {
      if (platForm.getLevel() == SystemConstants.FIRST_LEVEL) {
        title = platForm.getPlatformDescId().getTitle();
      } else if (platForm.getLevel() == SystemConstants.SECOND_LEVEL) {
        if (platForm.getProductEntryId() != null) {
          title = platForm.getProductEntryId().getAccountInvoiceNo();
        } else if (platForm.getSalesInvoiceId() != null) {
          title = platForm.getSalesInvoiceId().getInvoiceNo();
        } else if (platForm.getSalesReturnId() != null) {
          title = platForm.getSalesReturnId().getAccountInvoiceNo();
        } else if (platForm.getDebitCreditNoteId() != null) {
          title = platForm.getDebitCreditNoteId().getInvoiceNo();
        }
      } else if (platForm.getLevel() == SystemConstants.THIRD_LEVEL) {
        if (platForm.getProductDetailId() != null) {
          title = platForm.getProductDetailId().getProductBatchId().getProductId().getProductName();
        }
      }
      platForm.setTitle(title);
    }
  }

  public void showItems(Platform platform) {
    if (platform.getProductEntryId() != null) {
      ProductEntry pe = new ProductEntry();
      pe.setId(platform.getProductEntryId().getId());
      Jsf.popupList(FileConstant.PRODUCT_MARGIN, pe);
    }
  }

  public void creditDescriotionFilterEvent() {
    rootCredit = null;
  }

  public void debitDescriotionFilterEvent() {
    rootDebit = null;
  }

  public void platformPopupClose() {
    Jsf.popupReturn(getDebitCreditNote(), getDebitCreditNote());
  }

  /**
   *
   * @param platform
   * @return
   */
  private String getReferenceInvoiceNumber(Platform platform) {
    String invoiceNo = "";
    if (platform.getProductEntryId() != null) {
      invoiceNo = platform.getProductEntryId().getAccountInvoiceNo();
    } else if (platform.getSalesInvoiceId() != null) {
      invoiceNo = platform.getSalesInvoiceId().getInvoiceNo();
    } else if (platform.getSalesReturnId() != null) {
      invoiceNo = platform.getSalesReturnId().getAccountInvoiceNo();
    } else {
      invoiceNo = platform.getPlatformDescId().getTitle();
    }
    return invoiceNo;
  }

  private Date getReferenceInvoiceDate(Platform platform) {
    Date invoiceDate = null;
    if (platform.getProductEntryId() != null) {
      invoiceDate = platform.getProductEntryId().getProductEntryDate();
    } else if (platform.getSalesInvoiceId() != null) {
      invoiceDate = platform.getSalesInvoiceId().getInvoiceEntryDate();
    } else if (platform.getSalesReturnId() != null) {
      invoiceDate = platform.getSalesReturnId().getEntryDate();
    }
    return invoiceDate;
  }

  /**
   *
   * @param platform
   * @param isDebit
   * @return
   */
  private List<Platform> getChildPlatformList(Platform platform, boolean isDebit) {
    List<Platform> platformList = null;
    MainView main = Jsf.getMain();
    try {
      if (isDebit) {
        platformList = PlatformService.selectPlatformByAccount(main, getAccount(), getSelectSource(), getSelectedDebitDescription(), getSelectedStatus(), 4, platform, PlatformService.PLATFORM_DEBIT);
      } else {
        platformList = PlatformService.selectPlatformByAccount(main, getAccount(), getSelectSource(), getSelectedDebitDescription(), getSelectedStatus(), 4, platform, PlatformService.PLATFORM_CREDIT);
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return platformList;
  }

  /**
   *
   * @param main
   */
  public void selectPlatform() {
    List<DebitCreditNotePlatform> debitCreditNotePlatformList = new ArrayList<>();
    processSelect(debitCreditNotePlatformList, getPlatformDebitTreeSelected());
    List<Platform> platformList = new ArrayList<>(getSelectedPlatform().values());
    getDebitCreditNote().setPlatformList(platformList);
    getDebitCreditNote().setDebitCreditNotePlatformList(debitCreditNotePlatformList);
    getDebitCreditNote().setDebitCreditNoteItemList(getDebitCreditNoteItemList());
    Jsf.popupReturn(getDebitCreditNote(), getDebitCreditNote());
  }

  private void addCrDrItem(List<DebitCreditNotePlatform> debitCreditNotePlatformList, Platform platform) {
    if (!getSelectedPlatform().containsKey(platform.getId())) {
      getSelectedPlatform().put(platform.getId(), platform);
      double platformValue = 0.0;
      // String platformDesc = "";
      DebitCreditNoteItem debitCreditNoteItem = null;
      platformValue = debitNote ? platform.getDebitAmountRequired() : platform.getCreditAmountRequired();
      debitCreditNoteItem = new DebitCreditNoteItem(debitCreditNote, platform.getTitle(), getReferenceInvoiceNumber(platform), platformValue);
      debitCreditNoteItem.setDescription(platform.getPlatformDescId().getTitle());
      debitCreditNoteItem.setRefInvoiceNo(platform.getDocumentNo());
      debitCreditNoteItem.setTaxableValue(platformValue);
      //debitCreditNoteItem.setRefInvoiceDate(getReferenceInvoiceDate(platform));
      debitCreditNoteItem.setRefInvoiceDate(null);
      debitCreditNoteItem.setProductId(platform.getProductDetailId().getProductPresetId().getProductId());
      debitCreditNoteItem.setHsnSacCode(platform.getProductDetailId().getProductPresetId().getProductId().getHsnCode());
      debitCreditNoteItem.setTitle(debitCreditNoteItem.getProductId().getProductName());
      debitCreditNotePlatformList.add(new DebitCreditNotePlatform(debitCreditNote, debitCreditNoteItem, platform));
      getDebitCreditNoteItemList().add(debitCreditNoteItem);
    }
  }

  private void fecthThirdLevel(List<DebitCreditNotePlatform> debitCreditNotePlatformList, Platform platform) {
    List<Platform> pList = getChildPlatformList(platform, debitNote);
    if (pList != null) {
      for (Platform p : pList) {
        addCrDrItem(debitCreditNotePlatformList, p);
      }
    }
  }

  private void processSelect(List<DebitCreditNotePlatform> debitCreditNotePlatformList, TreeNode[] selected) {

    if (!StringUtil.isEmpty(selected)) {
      for (TreeNode treeNode : selected) {
        Platform platform = (Platform) treeNode.getData();
        if (platform != null && platform.getId() != null) {
          if (!getSelectedPlatform().containsKey(platform.getId())) {
            if (platform.getLevel() == SystemConstants.FIRST_LEVEL || platform.getLevel() == SystemConstants.SECOND_LEVEL) {
              if (treeNode.isExpanded() && treeNode.getChildCount() > 0) {
                processSelect(debitCreditNotePlatformList, treeNode.getChildren().toArray(new TreeNode[treeNode.getChildCount()]));
              } else {

                if (platform.getLevel() == SystemConstants.FIRST_LEVEL) {
                  List<Platform> pList = getChildPlatformList(platform, debitNote);
                  if (pList != null) {
                    for (Platform p : pList) {
                      fecthThirdLevel(debitCreditNotePlatformList, p);
                    }
                  }
                }
                if (platform.getLevel() == SystemConstants.SECOND_LEVEL) {
                  fecthThirdLevel(debitCreditNotePlatformList, platform);
                }
              }

            } else if (platform.getLevel() == SystemConstants.THIRD_LEVEL) {
              addCrDrItem(debitCreditNotePlatformList, platform);
            }
          }
        }
      }

    }

  }

  public TreeNode[] prepareChildNode(TreeNode[] treeNode) {
    List<TreeNode> treeList = new ArrayList<>();
    for (TreeNode tn : treeNode) {
      if (isNode(tn)) {
        treeList.add(tn);
      } else {
        prepareChildNode(tn.getChildren().toArray(new TreeNode[tn.getChildren().size()]));
      }
    }

    return treeList.toArray(new TreeNode[treeList.size()]);
  }

  public boolean isNode(TreeNode treeNode) {
    if (StringUtils.isNumeric(treeNode.getType())) {
      return true;
    } else if (treeNode.getChildren().size() == 0) {
      return true;
    }
    return false;
  }

  public void getChildren(List<TreeNode> children, List<TreeNode> treeList) {
    for (TreeNode cn : children) {
      if (isNode(cn)) {
        treeList.add(cn);
      }
    }
  }

  public List<PlatformDescription> lookupPlatformDescription() {
    return ScmLookupExtView.lookupPlatformDescription();
  }
}
