/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import spica.scm.common.StockExcess;
import spica.scm.domain.Account;
import spica.scm.domain.Product;
import spica.scm.domain.StockAdjustment;
import spica.scm.domain.StockAdjustmentItem;
import spica.scm.service.StockAdjustmentService;
import spica.scm.service.StockExcessService;
import spica.scm.service.StockNonSaleableService;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author java-2
 */
@Named(value = "stockExcessView")
@ViewScoped
public class StockExcessView implements Serializable {

  private transient List<StockExcess> stockExcessList;
  private transient Account accountId;
  private transient Product productId;
  private transient StockExcess[] selectedStockExcess;
  private transient String note;
  private transient StockAdjustment stockAdjustment;

  /**
   * Creates a new instance of StockExcessView
   */
  public StockExcessView() {
  }

  public Account getAccountId() {
    if (accountId == null) {
      accountId = UserRuntimeView.instance().getAccount();
    }
    return accountId;
  }

  public void setAccountId(Account accountId) {
    this.accountId = accountId;
  }

  public Product getProductId() {
    return productId;
  }

  public void setProductId(Product productId) {
    this.productId = productId;
  }

  public StockExcess[] getSelectedStockExcess() {
    return selectedStockExcess;
  }

  public void setSelectedStockExcess(StockExcess[] selectedStockExcess) {
    this.selectedStockExcess = selectedStockExcess;
  }

  public List<StockExcess> getStockExcessList() {
    return this.stockExcessList;
  }

  public void setStockExcessList(List<StockExcess> stockExcessList) {
    this.stockExcessList = stockExcessList;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public StockAdjustment getStockAdjustment() {
    if (stockAdjustment == null) {
      stockAdjustment = new StockAdjustment();
    }
    return stockAdjustment;
  }

  public void setStockAdjustment(StockAdjustment stockAdjustment) {
    this.stockAdjustment = stockAdjustment;
  }

  public String switchStockExcess(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
        } else if (main.isEdit() && !main.hasError()) {
        } else if (main.isList()) {
          setAccountId(null);
          setProductId(null);
          setNote(null);
          setSelectedStockExcess(null);
          getStockAdjustment().reset();
          getStockAdjustment().setEntryDate(new Date());
          loadStockExcessList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  public String loadStockExcessList(MainView main) {
    try {
      stockExcessList = StockExcessService.selectStockExcessList(main, getAccountId(), getProductId());
    } catch (Throwable t) {
      main.rollback(t, "error.list");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   *
   * @param main
   */
  public void actionAdjustExcessStock(MainView main) {
    try {
      if (ismovable()) {
        List<StockAdjustmentItem> stockAdjustmentItemList = StockAdjustmentService.insertOrUpdateStockAdjustmentExcess(main, getSelectedStockExcess(), getStockAdjustment(SystemConstants.STOCK_ADJUSTMENT_TYPE_EXCESS, "Stock Excess", "Moving excess Quantity to saleable", SystemConstants.CONFIRMED));
        if (!StringUtil.isEmpty(stockAdjustmentItemList)) {
          StockNonSaleableService.stockAdjustmentExcessToSaleable(main, stockAdjustmentItemList);
          setStockExcessList(null);
          main.commit("success.save");
        }
      } else {
        main.error("error.field.saleable");
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  /**
   *
   * @param stockAdjustmentType
   * @param title
   * @param description
   * @param status
   * @return
   */
  private StockAdjustment getStockAdjustment(int stockAdjustmentType, String title, String description, int status) {
    getStockAdjustment().setAccountId(getAccountId());
    getStockAdjustment().setCompanyId(UserRuntimeView.instance().getCompany());
    getStockAdjustment().setStockAdjustmentType(stockAdjustmentType);
    getStockAdjustment().setReferenceNo(title);
    getStockAdjustment().setDescription(description);
    getStockAdjustment().setStatusId(status);
    return getStockAdjustment();
  }

  /**
   *
   * @param main
   * @return
   */
  public String actionFlushOutExcessStock(MainView main) {
    try {
      if (ismovable()) {
        List<StockAdjustmentItem> stockAdjustmentItemList = StockAdjustmentService.insertOrUpdateStockAdjustmentExcess(main, getSelectedStockExcess(), getStockAdjustment(SystemConstants.STOCK_ADJUSTMENT_TYPE_EXCESS, "Excess Stock Flush Out", getNote(), SystemConstants.CONFIRMED));
        if (!StringUtil.isEmpty(stockAdjustmentItemList)) {
          StockNonSaleableService.stockAdjustmentExcessFlushOut(main, stockAdjustmentItemList);
          setStockExcessList(null);
          main.commit("success.save");
        }
      } else {
        main.error("error.field.saleable");
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
    return null;
  }

  public boolean ismovable() {
    boolean movable = false;
    if (getSelectedStockExcess() != null) {
      for (StockExcess list : getSelectedStockExcess()) {
        if (list.getSaleableQty() != null) {
          movable = true;
        }
      }
    }
    return movable;
  }
}
