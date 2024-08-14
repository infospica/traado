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
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import spica.scm.common.SelectItem;
import spica.scm.domain.Account;
import spica.scm.domain.Platform;
import spica.scm.domain.PlatformDescription;
import spica.scm.domain.PlatformSource;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.TradingVariationLog;
import spica.scm.service.TradingVariationLogService;
import spica.sys.FileConstant;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.config.AppConfig;
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
@Named(value = "tradingVariationLogView")
@ViewScoped
public class TradingVariationLogView implements Serializable {

  private transient TradingVariationLog tradingVariationLog;	//Domain object/selected Domain.
  private transient LazyDataModel<TradingVariationLog> tradingVariationCreditLazyModel; 	//For lazy loading datatable.
  private transient LazyDataModel<TradingVariationLog> tradingVariationDebitLazyModel; 	//For lazy loading datatable.
  private transient Platform[] platformCreditSelected;	 //Selected Domain Array
  private transient Platform[] platformDebitSelected;
  private transient PlatformSource selectSource;
  private transient PlatformDescription[] selectedDescription;
//  private transient Integer selectedStatus;
  private transient Long creditTotalRecords;
  private transient Long debitTotalRecords;
  //private int selectedYear;
  private int selectedSummary;
  private transient TreeNode rootCredit;
  private transient TreeNode rootDebit;
  private transient TreeNode[] platformCreditTreeSelected;	 //Selected Domain Array
  private transient TreeNode[] platformDebitTreeSelected;
  private transient Account account;

  /**
   * Default Constructor.
   */
  public TradingVariationLogView() {
    super();
  }

  /**
   * Return Platform.
   *
   * @return Platform.
   */
  public TradingVariationLog getTradingVariationLog() {
    if (tradingVariationLog == null) {
      tradingVariationLog = new TradingVariationLog();
    }
    return tradingVariationLog;
  }

  /**
   * Set Platform.
   *
   * @param platform.
   */
  public void setTradingVariationLog(TradingVariationLog tradingVariationLog) {
    this.tradingVariationLog = tradingVariationLog;
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
  public String switchTradingVariationLog(MainView main, String viewType) {
    //this.main = main;
    selectSource = null;
    //  selectedDescription = null;
    selectedSummary = SystemConstants.PLATFORM_SUMMARY_VIEW;
    selectSource = SystemRuntimeConfig.PURCHASE;
//    selectedDescription = new PlatformDescription[]{SystemRuntimeConfig.VENDOR_RESERVE, SystemRuntimeConfig.MARGIN_DIFFERENCE};
    if (!StringUtil.isEmpty(viewType)) {
      try {
        UserRuntimeView.instance().getAccountCurrent();
        main.setViewType(viewType);
        if (ViewTypes.isNew(viewType) && !main.hasError()) {
          getTradingVariationLog().reset();
        } else if (ViewTypes.isEdit(viewType) && !main.hasError()) {
          setTradingVariationLog((TradingVariationLog) TradingVariationLogService.selectByPk(main, getTradingVariationLog()));
        } else if (ViewTypes.isList(viewType)) {
          loadTradingVariationLogCreditList(main);
          loadTradingVariationLogDebitList(main);
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
  private void loadTradingVariationLogCreditList(final MainView main) {
    //  countYear();
    if (tradingVariationCreditLazyModel == null) {
      tradingVariationCreditLazyModel = new LazyDataModel<TradingVariationLog>() {
        private List<TradingVariationLog> list;

        @Override
        public List<TradingVariationLog> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (getAccount() != null) {
              //making total records to null cause these is value in first fetch
              main.getPageData().setTotalRecords(creditTotalRecords);
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = TradingVariationLogService.listPagedCredit(main, getAccount(), getSelectSource(), getSelectedSummary(), null);
              main.commit(tradingVariationCreditLazyModel, first, pageSize);
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
        public Object getRowKey(TradingVariationLog tradingVariationLog) {
          return tradingVariationLog.getId();
        }

        @Override
        public TradingVariationLog getRowData(String rowKey) {
          if (list != null) {
            for (TradingVariationLog obj : list) {
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

  private void loadTradingVariationLogDebitList(final MainView main) {
    if (tradingVariationDebitLazyModel == null) {
      tradingVariationDebitLazyModel = new LazyDataModel<TradingVariationLog>() {
        private List<TradingVariationLog> list;

        @Override
        public List<TradingVariationLog> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (getAccount() != null) {
              main.getPageData().setTotalRecords(getDebitTotalRecords());
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = TradingVariationLogService.listPagedDebit(main, getAccount(), getSelectSource(), getSelectedSummary(), null);
              main.commit(tradingVariationDebitLazyModel, first, pageSize);
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
        public Object getRowKey(TradingVariationLog tradingVariationLog) {
          return tradingVariationLog.getId();
        }

        @Override
        public TradingVariationLog getRowData(String rowKey) {
          if (list != null) {
            for (TradingVariationLog obj : list) {
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

  public void summaryFilter(SelectEvent event) {
    selectedSummary = (int) event.getObject();
  }

  /**
   * Return LazyDataModel of Platform.
   *
   * @return
   */
  public LazyDataModel<TradingVariationLog> getTradingVariationCreditLazyModel() {
    return tradingVariationCreditLazyModel;
  }

  public LazyDataModel<TradingVariationLog> getTradingVariationDebitLazyModel() {
    return tradingVariationDebitLazyModel;
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

  public PlatformSource getSelectSource() {
    return selectSource;
  }

  public void setSelectSource(PlatformSource selectSource) {
    this.selectSource = selectSource;
  }

  public int getSelectedSummary() {
    return selectedSummary;
  }

  public void setSelectedSummary(int selectedSummary) {
    this.selectedSummary = selectedSummary;
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
          List<TradingVariationLog> creditList = TradingVariationLogService.listPagedCredit(main, getAccount(), getSelectSource(), SystemConstants.PLATFORM_SUMMARY_VIEW, null);
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
          List<TradingVariationLog> debitList = TradingVariationLogService.listPagedDebit(main, getAccount(), getSelectSource(), SystemConstants.PLATFORM_SUMMARY_VIEW, null);
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

  public TreeNode createNode(List<TradingVariationLog> nodeList) {
    TreeNode node = new CheckboxTreeNode(new Platform(), null);
    for (TradingVariationLog tradingVariationLog : nodeList) {
      tradingVariationLog.setLevel(SystemConstants.FIRST_LEVEL);
      setTradingVariationTitle(tradingVariationLog);
      TreeNode t1 = new CheckboxTreeNode(tradingVariationLog, node);
    }
    return node;
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

  public void showItems(TradingVariationLog p) {
    if (p.getProductEntryId() != null) {
      ProductEntry pe = new ProductEntry();
      pe.setId(p.getProductEntryId().getId());
      Jsf.popupList(FileConstant.PRODUCT_MARGIN, pe);
    }
  }

  public void onNodeExpandCredit(NodeExpandEvent event) {
    TreeNode parent = event.getTreeNode();
    TradingVariationLog p = (TradingVariationLog) parent.getData();
    parent.getChildren().clear();
    MainView main = Jsf.getMain();
    try {
      if (getAccount() != null) {
        List<TradingVariationLog> creditList = TradingVariationLogService.listPagedCredit(main, getAccount(), getSelectSource(), 4, p);
        createNodeOnExpand(creditList, parent);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void onNodeExpandDebit(NodeExpandEvent event) {
    TreeNode parent = event.getTreeNode();
    TradingVariationLog p = (TradingVariationLog) parent.getData();
    parent.getChildren().clear();
    MainView main = Jsf.getMain();
    try {
      if (getAccount() != null) {
        List<TradingVariationLog> debitList = TradingVariationLogService.listPagedDebit(main, getAccount(), getSelectSource(), 4, p);
        createNodeOnExpand(debitList, parent);
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void createNodeOnExpand(List<TradingVariationLog> nodeList, TreeNode parent) {
    for (TradingVariationLog tradingVariationLog : nodeList) {
      if (parent.getRowKey().length() == 1) {
        tradingVariationLog.setLevel(SystemConstants.SECOND_LEVEL);
        setTradingVariationTitle(tradingVariationLog);
        TreeNode tn = new CheckboxTreeNode(tradingVariationLog, parent);
        if (parent.getRowKey().length() == 1 && !SystemRuntimeConfig.INVOICE_DIFFERENCE.getId().equals(tradingVariationLog.getPlatformDescId().getId())) {
          TreeNode t = new CheckboxTreeNode(new Platform(), tn);
        }
      } else if (parent.getRowKey().length() == 3 && tradingVariationLog.getProductDetailId() != null) {
        tradingVariationLog.setLevel(SystemConstants.THIRD_LEVEL);
        setTradingVariationTitle(tradingVariationLog);
        TreeNode tn = new CheckboxTreeNode("3", tradingVariationLog, parent);
        if (parent.getRowKey().length() == 1 && !SystemRuntimeConfig.INVOICE_DIFFERENCE.getId().equals(tradingVariationLog.getPlatformDescId().getId())) {
          TreeNode t = new CheckboxTreeNode(new Platform(), tn);
          //    t.setSelectable(false);
        }
      }
    }
  }

  private void setTradingVariationTitle(TradingVariationLog tradingVariationLog) {
    String title = null;
    if (tradingVariationLog != null) {
      if (tradingVariationLog.getLevel() == SystemConstants.FIRST_LEVEL) {
        title = tradingVariationLog.getPlatformDescId().getTitle();
      } else if (tradingVariationLog.getLevel() == SystemConstants.SECOND_LEVEL) {
        if (tradingVariationLog.getProductEntryId() != null) {
          title = tradingVariationLog.getProductEntryId().getAccountInvoiceNo();
        }
      } else if (tradingVariationLog.getLevel() == SystemConstants.THIRD_LEVEL) {
        if (tradingVariationLog.getProductDetailId() != null) {
          title = tradingVariationLog.getProductDetailId().getProductBatchId().getProductId().getProductName();
        }
      }
      tradingVariationLog.setTitle(title);
    }
  }

  public List<SelectItem> getTradingVariationLogSummaryFilters() {
    List<SelectItem> tradingVariationLog = new ArrayList<>();
    tradingVariationLog.add(new SelectItem("Summary", SystemConstants.PLATFORM_SUMMARY_VIEW));
    tradingVariationLog.add(new SelectItem("Detail", SystemConstants.PLATFORM_DETAILED_VIEW));
    return tradingVariationLog;
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

}
