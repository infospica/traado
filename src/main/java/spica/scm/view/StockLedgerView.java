/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import javax.faces.event.AjaxBehaviorEvent;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.Account;
import spica.scm.domain.Company;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.StockLedger;
import spica.scm.service.StockLedgerService;
import static spica.sys.SystemRuntimeConfig.SDF_YYYY_MM_DD;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author fify
 */
@Named(value = "stockLedgerView")
@ViewScoped
public class StockLedgerView implements Serializable {

  private transient List<StockLedger> stockLedgerList;
  private transient Date selectedFromDate;
  private transient Date selectedToDate;
  private transient Integer currentStock;
  private transient AccountGroup accountGroup;
  private transient Account account;
  private transient Product product;
  private transient ProductBatch productBatch;
  private transient Integer openStockTotal;
  private transient Integer qtyInTotal;
  private transient Integer qtyOutTotal;

  public List<StockLedger> getStockLedgerList(MainView main) {

    if (product != null && StringUtil.isEmpty(stockLedgerList)) {
      try {
        openStockTotal = 0;
        qtyInTotal = 0;
        qtyOutTotal = 0;

        Company company = UserRuntimeView.instance().getCompany();
        selectedFromDate = company.getCurrentFinancialYear().getStartDate();
        stockLedgerList = StockLedgerService.getStockLedgerList(main, SDF_YYYY_MM_DD.format(selectedFromDate), SDF_YYYY_MM_DD.format(selectedToDate), accountGroup, account, product, productBatch);
        HashMap<Integer, Integer> map = new HashMap<>();
        for (StockLedger list : stockLedgerList) {
          if (map.get(list.getProdId()) == null) {
            Integer qty = 0;
            if (list.getOpeningStock() != null) {
              qty = list.getOpeningStock();
              qtyInTotal += qty;
            } else if (list.getQuantityAvailable() != null) {
              qty = list.getQuantityAvailable();
            } else {
              if (list.getQuantityIn() != null) {
                qty = list.getQuantityIn();
                qtyInTotal += qty;
              }
            }
            map.put(list.getProdId(), qty);
            list.setQuantityAvailable(qty);
          } else {
            if (list.getOpeningStock() != null) {
              list.setQuantityAvailable(map.get(list.getProdId()) + list.getOpeningStock());
              qtyInTotal += list.getOpeningStock();
            } else if (list.getQuantityIn() != null) {
              list.setQuantityAvailable(map.get(list.getProdId()) + list.getQuantityIn());
              qtyInTotal += list.getQuantityIn();
            } else if (list.getQuantityOut() != null) {
              list.setQuantityAvailable(map.get(list.getProdId()) - list.getQuantityOut());
              qtyOutTotal += list.getQuantityOut();
            }
            map.put(list.getProdId(), list.getQuantityAvailable());
          }

        }
        currentStock = qtyInTotal - qtyOutTotal;
        Jsf.update("currentStockDiv");
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return stockLedgerList;
  }

  /**
   *
   * @param filter
   * @return
   */
  public void submitForm() {
    stockLedgerList = null;
  }

  public void accountGroupSelectEvent(SelectEvent event) {
    AccountGroup accountGroup = (AccountGroup) event.getObject();
    if (accountGroup != null) {
      if (accountGroup.getId() != null) {
        setAccountGroup(accountGroup);
        setAccount(null);
        setProduct(null);
        setProductBatch(null);
        stockLedgerList = null;
      }
    }
  }

  public List<Account> accountAuto(String filter) {
    if (accountGroup != null && accountGroup.getId() != null) {
      MainView main = Jsf.getMain();
      try {
        return ScmLookupExtView.accountByAccountGroupProfile(main, accountGroup, filter);
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  public void accountSelectEvent(SelectEvent event) {
    Account account = (Account) event.getObject();
    if (account != null) {
      if (account.getId() != null) {
        setAccount(account);
        setProduct(null);
        setProductBatch(null);
        stockLedgerList = null;
      }
    }
  }

  public List<Product> productAuto(String filter) {
    if (UserRuntimeView.instance().getCompany() != null && accountGroup != null && accountGroup.getId() != null && account == null) {
      return ScmLookupExtView.productByAccountGroupAuto(filter, UserRuntimeView.instance().getCompany().getId(), accountGroup.getId());
    } else if (UserRuntimeView.instance().getCompany() != null && account != null && account.getId() != null) {
      return ScmLookupExtView.productAutoByBrand(filter, UserRuntimeView.instance().getCompany().getId(), accountGroup.getId(), account.getId());
    }
    return null;
  }

  public void productSelectEvent(SelectEvent event) {
    product = (Product) event.getObject();
    if (product != null) {
      if (product.getId() != null) {
        setProductBatch(null);
        stockLedgerList = null;
      }
    }
  }

  public List<ProductBatch> productBatchAuto(String filter) {

    if (UserRuntimeView.instance().getCompany() != null && product != null && product.getId() != null) {
      return ScmLookupExtView.productBatchAuto(filter, product);
    }
    return null;
  }

  public void reset() {
    accountGroup = null;
    account = null;
    product = null;
    productBatch = null;
    qtyInTotal = null;
    qtyOutTotal = null;
    currentStock = null;
  }

  public void productBatchSelectEvent(SelectEvent event) {
    productBatch = (ProductBatch) event.getObject();
    stockLedgerList = null;
  }

  public void productBatchChangeEvent(AjaxBehaviorEvent event) {
    productBatch = null;
    stockLedgerList = null;
  }

  public void selectedDateFromEvent(SelectEvent event) {
    selectedFromDate = (Date) event.getObject();
    stockLedgerList = null;
  }

  public void selectedDateToEvent(SelectEvent event) {
    selectedToDate = (Date) event.getObject();
    stockLedgerList = null;
  }

  public void setStockLedgerList(List<StockLedger> stockLedgerList) {
    this.stockLedgerList = stockLedgerList;
    product = null;
  }

  public Date getSelectedFromDate() {
    if (selectedFromDate == null) {
      Calendar c = Calendar.getInstance();   // this takes current date
      c.set(Calendar.DAY_OF_MONTH, 1);
      selectedFromDate = c.getTime();
    }
    return selectedFromDate;
  }

  public void setSelectedFromDate(Date selectedFromDate) {
    this.selectedFromDate = selectedFromDate;
  }

  public Date getSelectedToDate() {
    if (selectedToDate == null) {
      selectedToDate = new Date();
    }
    return selectedToDate;

  }

  public void setSelectedToDate(Date selectedToDate) {
    this.selectedToDate = selectedToDate;
  }

  public AccountGroup getAccountGroup() {
    return accountGroup;
  }

  public void setAccountGroup(AccountGroup accountGroup) {
    this.accountGroup = accountGroup;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public ProductBatch getProductBatch() {
    return productBatch;
  }

  public void setProductBatch(ProductBatch productBatch) {
    this.productBatch = productBatch;
  }

  public Integer getOpenStockTotal() {
    return openStockTotal;
  }

  public Integer getQtyInTotal() {
    return qtyInTotal;
  }

  public Integer getQtyOutTotal() {
    return qtyOutTotal;
  }

  public Integer getCurrentStock() {
    return currentStock;
  }

  public void setCurrentStock(Integer currentStock) {
    this.currentStock = currentStock;
  }
}
