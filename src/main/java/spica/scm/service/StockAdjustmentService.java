/*
 * @(#)StockAdjustmentService.java	1.0 Wed Mar 21 14:37:41 IST 2018
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import spica.constant.AccountingConstant;
import spica.scm.common.StockExcess;
import spica.scm.common.StockAdjustmentDetail;
import spica.scm.domain.Account;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.SalesInvoice;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.StockAdjustment;
import spica.scm.domain.StockAdjustmentItem;
import spica.scm.domain.StockNonSaleable;
import spica.scm.domain.StockSaleable;
import spica.scm.validate.StockAdjustmentIs;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.entity.core.AppDb;
import wawo.entity.util.StringUtil;

/**
 * StockAdjustmentService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 21 14:37:41 IST 2018
 */
public abstract class StockAdjustmentService {

  private static boolean saleableAdjustmentDone = false;
  private static boolean damagedAdjustmentDone = false;

  /**
   * StockAdjustment paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getStockAdjustmentSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_stock_adjustment", StockAdjustment.class, main);
    sql.main("select scm_stock_adjustment.id,scm_stock_adjustment.stock_adjustment_type,scm_stock_adjustment.financial_year_id,scm_stock_adjustment.company_id,scm_stock_adjustment.account_id,scm_stock_adjustment.reference_no,"
            + "scm_stock_adjustment.entry_date,scm_stock_adjustment.status_id,scm_stock_adjustment.created_by,scm_stock_adjustment.modified_by,scm_stock_adjustment.created_at,"
            + "scm_stock_adjustment.modified_at,scm_stock_adjustment.confirmed_at,scm_stock_adjustment.confirmed_by from scm_stock_adjustment scm_stock_adjustment "); //Main query
    sql.count("select count(scm_stock_adjustment.id) as total from scm_stock_adjustment scm_stock_adjustment "); //Count query
    sql.join("left outer join scm_company scm_stock_adjustmentcompany_id on (scm_stock_adjustmentcompany_id.id = scm_stock_adjustment.company_id) left outer join scm_account scm_stock_adjustmentaccount_id on (scm_stock_adjustmentaccount_id.id = scm_stock_adjustment.account_id)"); //Join Query

    sql.string(new String[]{"scm_stock_adjustmentcompany_id.company_name", "scm_stock_adjustmentaccount_id.account_code", "scm_stock_adjustment.reference_no", "scm_stock_adjustment.created_by", "scm_stock_adjustment.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_stock_adjustment.id", "scm_stock_adjustment.status_id", "scm_stock_adjustment.reference_no"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_stock_adjustment.entry_date", "scm_stock_adjustment.created_at", "scm_stock_adjustment.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of StockAdjustment.
   *
   * @param main
   * @return List of StockAdjustment
   */
  public static final List<StockAdjustment> listPaged(Main main) {
    return AppService.listPagedJpa(main, getStockAdjustmentSqlPaged(main));
  }

  /**
   *
   * @param main
   * @param account
   * @return
   */
  public static final List<StockAdjustment> listPagedByCompany(Main main, boolean showExcessAdjustment, AccountingFinancialYear accountingFinancialYear) {
    SqlPage sql = getStockAdjustmentSqlPaged(main);
    sql.cond("where scm_stock_adjustment.company_id=? AND reverse_entry IS NULL and scm_stock_adjustment.financial_year_id=?");
    sql.param(UserRuntimeView.instance().getCompany().getId());
    sql.param(UserRuntimeView.instance().getCompany().getCurrentFinancialYear().getId());
    if (showExcessAdjustment) {
      sql.cond("and stock_adjustment_type in (?,?)");
      sql.param(SystemConstants.STOCK_ADJUSTMENT_TYPE_EXCESS);
    } else {
      sql.cond("and stock_adjustment_type in (?)");
    }
    sql.param(SystemConstants.STOCK_ADJUSTMENT_TYPE_SALEABLE);
    sql.orderBy(" scm_stock_adjustment.entry_date desc");
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of StockAdjustment based on condition
//   * @param main
//   * @return List<StockAdjustment>
//   */
//  public static final List<StockAdjustment> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getStockAdjustmentSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select StockAdjustment by key.
   *
   * @param main
   * @param stockAdjustment
   * @return StockAdjustment
   */
  public static final StockAdjustment selectByPk(Main main, StockAdjustment stockAdjustment) {
    return (StockAdjustment) AppService.find(main, StockAdjustment.class, stockAdjustment.getId());
  }

  /**
   * Insert StockAdjustment.
   *
   * @param main
   * @param stockAdjustment
   */
  public static final void insert(Main main, StockAdjustment stockAdjustment) {
    StockAdjustmentIs.insertAble(main, stockAdjustment);  //Validating
    AppService.insert(main, stockAdjustment);

  }

  /**
   * Update StockAdjustment by key.
   *
   * @param main
   * @param stockAdjustment
   * @return StockAdjustment
   */
  public static final StockAdjustment updateByPk(Main main, StockAdjustment stockAdjustment) {
    StockAdjustmentIs.updateAble(main, stockAdjustment); //Validating
    return (StockAdjustment) AppService.update(main, stockAdjustment);
  }

  /**
   * Insert or update StockAdjustment
   *
   * @param main
   * @param stockAdjustment
   */
  public static void insertOrUpdate(Main main, StockAdjustment stockAdjustment) {
    if (stockAdjustment.getId() == null) {
      insert(main, stockAdjustment);
//      if (stockAdjustment.getReverseEntry() == null && stockAdjustment.getStockAdjustmentType() == SystemConstants.STOCK_ADJUSTMENT_TYPE_SALEABLE) {
//        AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, stockAdjustment.getAccountId(), PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID, true);
//      }
    } else {
      updateByPk(main, stockAdjustment);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param stockAdjustment
   */
  public static void clone(Main main, StockAdjustment stockAdjustment) {
    stockAdjustment.setId(null); //Set to null for insert
    insert(main, stockAdjustment);
  }

  /**
   * Delete StockAdjustment.
   *
   * @param main
   * @param stockAdjustment
   */
  public static final void deleteByPk(Main main, StockAdjustment stockAdjustment) {
    StockAdjustmentIs.deleteAble(main, stockAdjustment); //Validation
    resetStockAdjustmentToDraft(main, stockAdjustment);
    AppService.deleteSql(main, StockAdjustmentItem.class, "delete from scm_stock_adjustment_item where stock_adjustment_id = ?", new Object[]{stockAdjustment.getId()});
    AppService.delete(main, StockAdjustment.class, stockAdjustment.getId());
  }

  /**
   * Delete Array of StockAdjustment.
   *
   * @param main
   * @param stockAdjustment
   */
  public static final void deleteByPkArray(Main main, StockAdjustment[] stockAdjustment) {
    for (StockAdjustment e : stockAdjustment) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param stockAdjustment
   * @param stockAdjustmentItemList
   */
  public static void insertOrUpdateStockAdjustment(Main main, StockAdjustment stockAdjustment, List<StockAdjustmentItem> stockAdjustmentItemList) {

    if (SystemConstants.CONFIRMED.equals(stockAdjustment.getStatusId())) {
      if (stockAdjustment.getReverseEntry() == null && stockAdjustment.getStockAdjustmentType() == SystemConstants.STOCK_ADJUSTMENT_TYPE_SALEABLE) {
        if (stockAdjustment.getConfirmedAt() == null) {
          if (stockAdjustment.getAccountId() != null) {
            stockAdjustment.setReferenceNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, stockAdjustment.getAccountId(), PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID, false, stockAdjustment.getFinancialYearId()));
            AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, stockAdjustment.getAccountId(), PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID, false, stockAdjustment.getFinancialYearId());
          } else if (stockAdjustment.getAccountGroupId() != null) {
            stockAdjustment.setReferenceNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, stockAdjustment.getAccountGroupId(), PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID, false, stockAdjustment.getFinancialYearId()));
            AccountGroupDocPrefixService.updateSalesPrefixSequence(main, stockAdjustment.getAccountGroupId(), PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID, false, stockAdjustment.getFinancialYearId());
          }
        }
      }
      stockAdjustment.setConfirmedAt(new Date());
      stockAdjustment.setConfirmedBy(main.getAppUser().getLogin());
    }
    if (stockAdjustment.getId() != null) {
      AppService.deleteSql(main, StockAdjustmentItem.class, "delete from scm_stock_adjustment_item where stock_adjustment_id = ?", new Object[]{stockAdjustment.getId()});
    }
    if (SystemConstants.DRAFT.equals(stockAdjustment.getStatusId())) {
      if (stockAdjustment.getReverseEntry() == null && stockAdjustment.getStockAdjustmentType() == SystemConstants.STOCK_ADJUSTMENT_TYPE_SALEABLE) {
        if (stockAdjustment.getId() == null) {
          if (stockAdjustment.getAccountId() != null) {
            AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, stockAdjustment.getAccountId(), PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID, true, stockAdjustment.getFinancialYearId());
          } else if (stockAdjustment.getAccountGroupId() != null) {
            AccountGroupDocPrefixService.updateSalesPrefixSequence(main, stockAdjustment.getAccountGroupId(), PrefixTypeService.STOCK_ADJUSTMENT_PREFIX_ID, true, stockAdjustment.getFinancialYearId());
          }
        }
      }
    }
    insertOrUpdate(main, stockAdjustment);

    for (StockAdjustmentItem stockAdjustmentItem : stockAdjustmentItemList) {
      stockAdjustmentItem.setId(null);
      if ((stockAdjustmentItem != null && stockAdjustmentItem.getProductDetailId() != null)
              && (stockAdjustmentItem.getQuantitySaleableActual() != null) || stockAdjustmentItem.getQuantityDamagedActual() != null) {
        stockAdjustmentItem.setStockAdjustmentId(stockAdjustment);
        Account account = AccountService.selectById(main, stockAdjustmentItem.getStockAdjustmentDetail().getAccountId());
        stockAdjustmentItem.setAccountId(account);
        if (SystemConstants.DRAFT.equals(stockAdjustment.getStatusId())) {
          StockAdjustmentItemService.insert(main, stockAdjustmentItem);
        }
      }
    }
    if (SystemConstants.CONFIRMED.equals(stockAdjustment.getStatusId())) {
      confirmStockAdjustment(main, stockAdjustment, stockAdjustmentItemList);
    }
  }

  private static void confirmStockAdjustment(Main main, StockAdjustment stockAdjustment, List<StockAdjustmentItem> stockAdjustmentItems) {
    List<StockAdjustmentDetail> stockAdjustmentDetailList = null;
    for (StockAdjustmentItem adjustmentItem : stockAdjustmentItems) {
      if ((adjustmentItem != null && adjustmentItem.getProductDetailId() != null)
              && (adjustmentItem.getQuantitySaleableActual() != null || adjustmentItem.getQuantityDamagedActual() != null)) {
        String sql = "SELECT * FROM getStockByAccountGroupOrAccountForAdjustment(?,?,?) where product_detail_id = ? order by product_entry_detail_id desc";
        stockAdjustmentDetailList = AppDb.getList(main.dbConnector(), StockAdjustmentDetail.class, sql,
                new Object[]{stockAdjustment.getAccountGroupId() != null ? stockAdjustment.getAccountGroupId().getId() : null,
                  stockAdjustment.getAccountId() != null ? stockAdjustment.getAccountId().getId() : null, adjustmentItem.getProductId().getId(),
                  adjustmentItem.getProductDetailId().getId()});

        if (!StringUtil.isEmpty(stockAdjustmentDetailList)) {
          doStockAdjustment(main, adjustmentItem, stockAdjustmentDetailList);
        }
      }
    }
  }

  private static void doStockAdjustment(Main main, StockAdjustmentItem stockAdjustmentItem, List<StockAdjustmentDetail> stockAdjustmentDetailList) {
    int saleableAdjustmentQty = stockAdjustmentItem.getQuantitySaleableActual() == null ? -1 : stockAdjustmentItem.getQuantitySaleableActual();
    int damagedAdjustmentQty = stockAdjustmentItem.getQuantityDamagedActual() == null ? -1 : stockAdjustmentItem.getQuantityDamagedActual();
    boolean isSaleableAdjustment = false;
    int adjustmentQty = 0, productQty = 0;
    saleableAdjustmentDone = false;
    damagedAdjustmentDone = false;
    int damagedQtyActual = stockAdjustmentItem.getQuantityDamagedActual() != null ? stockAdjustmentItem.getQuantityDamagedActual() : 0;
    int damagedQty = stockAdjustmentItem.getQuantityDamaged() != null ? stockAdjustmentItem.getQuantityDamaged() : 0;

    if (saleableAdjustmentQty >= 0) {
      adjustmentQty = saleableAdjustmentQty - (stockAdjustmentItem.getQuantityAvailable() == null ? 0 : stockAdjustmentItem.getQuantityAvailable());
      productQty = Math.abs(adjustmentQty);
      if (productQty > 0) {
        isSaleableAdjustment = true;
        saleableAdjustmentDone = true;
        if (adjustmentQty < 0) {
          Collections.reverse(stockAdjustmentDetailList);
        }
        if (damagedAdjustmentQty >= 0) {
          damagedAdjustmentDone = true;
          stockAdjustmentItem.setQuantityDamagedActual(null);
          stockAdjustmentItem.setQuantityDamaged(null);
        }
        doStockAdjustment(main, productQty, adjustmentQty, stockAdjustmentItem, stockAdjustmentDetailList, isSaleableAdjustment);
      }
    }

    if (damagedAdjustmentQty >= 0) {
      damagedAdjustmentDone = true;
      adjustmentQty = damagedAdjustmentQty - (stockAdjustmentItem.getQuantityDamaged() == null ? 0 : stockAdjustmentItem.getQuantityDamaged());
      productQty = Math.abs(adjustmentQty);
      isSaleableAdjustment = false;
      if (adjustmentQty > 0) {
        Collections.reverse(stockAdjustmentDetailList);
      }
      if (productQty > 0) {
        if (saleableAdjustmentDone) {
          StockAdjustmentItem damagedAdjustment = new StockAdjustmentItem();
          damagedAdjustment.setAccountId(stockAdjustmentItem.getAccountId());
          damagedAdjustment.setQuantityDamaged(damagedQty);
          damagedAdjustment.setQuantityDamagedActual(damagedQtyActual);
          damagedAdjustment.setProductId(stockAdjustmentItem.getProductId());
          damagedAdjustment.setProductDetailId(stockAdjustmentItem.getProductDetailId());
          damagedAdjustment.setStockAdjustmentId(stockAdjustmentItem.getStockAdjustmentId());
          damagedAdjustment.setStockAdjustmentDetail(stockAdjustmentItem.getStockAdjustmentDetail());
          doStockAdjustment(main, productQty, adjustmentQty, damagedAdjustment, stockAdjustmentDetailList, isSaleableAdjustment);
        } else {
          doStockAdjustment(main, productQty, adjustmentQty, stockAdjustmentItem, stockAdjustmentDetailList, isSaleableAdjustment);
        }
      }
    }
  }

  private static void doStockAdjustment(Main main, Integer productQty, Integer adjustmentQty, StockAdjustmentItem stockAdjustmentItem, List<StockAdjustmentDetail> stockAdjustmentDetailList, boolean isSaleableAdjustment) {
    StockAdjustmentItem adjustmentItem = null;
    boolean multiplePurhase = false;
    //boolean notDeleted = true;
    Integer availableQty = 0;
    Account account = AccountService.selectById(main, stockAdjustmentItem.getStockAdjustmentDetail().getAccountId());
    if (adjustmentQty > 0) {
      stockAdjustmentItem.setProductEntryDetailId(new ProductEntryDetail(stockAdjustmentDetailList.get(0).getProductEntryDetailId()));
      stockAdjustmentItem.getProductDetailId().setAccountId(account);
      stockAdjustmentItem.setLandRate(stockAdjustmentDetailList.get(0).getLandRate());
      stockAdjustmentItem.setAdjustedQty(adjustmentQty);
      if (isSaleableAdjustment) {
        StockAdjustmentItemService.insert(main, stockAdjustmentItem);
        StockSaleableService.adjustSaleableStock(main, stockAdjustmentItem);
      } else {
        StockAdjustmentItemService.insert(main, stockAdjustmentItem);
        StockNonSaleableService.adjustDamagedStock(main, stockAdjustmentItem);
      }
    } else {
      for (StockAdjustmentDetail stockAdjustmentDetail : stockAdjustmentDetailList) {

        if (isSaleableAdjustment) {
          availableQty = stockAdjustmentDetail.getQuantitySaleable();
        } else {
          availableQty = stockAdjustmentDetail.getQuantityDamaged();
        }

        if (availableQty != null && availableQty > 0 && productQty > 0) {
          if (productQty <= availableQty) {

            stockAdjustmentItem.setProductEntryDetailId(ProductEntryDetailService.selectByPk(main, new ProductEntryDetail(stockAdjustmentDetail.getProductEntryDetailId())));
            stockAdjustmentItem.setLandRate(stockAdjustmentDetail.getLandRate());
            stockAdjustmentItem.setAdjustedQty(productQty * -1);

            adjustmentItem = multiplePurhase ? getStockAdjustmentItem(stockAdjustmentItem, stockAdjustmentDetail, isSaleableAdjustment) : stockAdjustmentItem;
            adjustmentItem.setAccountId(account);
            adjustmentItem.getProductDetailId().setAccountId(account);
            if (isSaleableAdjustment) {
              StockAdjustmentItemService.insert(main, adjustmentItem);
              StockSaleableService.adjustSaleableStock(main, adjustmentItem);
            } else {
              StockAdjustmentItemService.insert(main, adjustmentItem);
              StockNonSaleableService.adjustDamagedStock(main, adjustmentItem);
            }
            productQty = 0;
          } else {
            stockAdjustmentItem.setProductEntryDetailId(new ProductEntryDetail(stockAdjustmentDetail.getProductEntryDetailId()));
            stockAdjustmentItem.setLandRate(stockAdjustmentDetail.getLandRate());
            stockAdjustmentItem.setAdjustedQty(availableQty * -1);
            productQty -= availableQty;
            multiplePurhase = productQty != 0;
            adjustmentItem = multiplePurhase ? getStockAdjustmentItem(stockAdjustmentItem, stockAdjustmentDetail, isSaleableAdjustment) : stockAdjustmentItem;
            adjustmentItem.setAccountId(account);
            adjustmentItem.getProductDetailId().setAccountId(account);
            StockAdjustmentItemService.insert(main, adjustmentItem);
            if (isSaleableAdjustment) {
              StockSaleableService.adjustSaleableStock(main, adjustmentItem);
            } else {
              StockNonSaleableService.adjustDamagedStock(main, adjustmentItem);
            }
          }
        }
      }
    }
  }

  private static StockAdjustmentItem getStockAdjustmentItem(StockAdjustmentItem stockAdjustmentItem, StockAdjustmentDetail stockAdjustmentDetail, boolean adjustmentType) {
    StockAdjustmentItem adjustmentItem = new StockAdjustmentItem(stockAdjustmentItem);
    adjustmentItem.setAccountId(new Account(stockAdjustmentDetail.getAccountId()));
    adjustmentItem.setProductEntryDetailId(stockAdjustmentItem.getProductEntryDetailId());
    adjustmentItem.setLandRate(stockAdjustmentDetail.getLandRate());
    adjustmentItem.setQuantityPurchased(stockAdjustmentDetail.getQuantityIn());
    if (adjustmentType) {
      adjustmentItem.setQuantityAvailable(stockAdjustmentDetail.getQuantitySaleable());
      if (!damagedAdjustmentDone) {
        adjustmentItem.setQuantityDamaged(stockAdjustmentDetail.getQuantityDamaged());
      }
    } else {
      adjustmentItem.setQuantityDamaged(stockAdjustmentDetail.getQuantityDamaged());
      if (!saleableAdjustmentDone) {
        adjustmentItem.setQuantityAvailable(stockAdjustmentDetail.getQuantitySaleable());
      }
    }
    adjustmentItem.setAdjustedQty(stockAdjustmentItem.getAdjustedQty());
    if (adjustmentType) {
      adjustmentItem.setQuantitySaleableActual(stockAdjustmentDetail.getQuantitySaleable() - Math.abs(stockAdjustmentItem.getAdjustedQty()));
    } else {
      adjustmentItem.setQuantityDamagedActual(stockAdjustmentDetail.getQuantityDamaged() - Math.abs(stockAdjustmentItem.getAdjustedQty()));
    }
    return adjustmentItem;

  }

  public static void adjustSalesInvoiceStock(Main main, SalesInvoice salesInvoice) {
    List<Account> accountList = AppService.list(main, Account.class, "select id,account_title,account_status_id from scm_account where id in(select account_id from scm_sales_invoice_item where sales_invoice_id = ?)", new Object[]{salesInvoice.getId()});
    List<StockSaleable> stockSaleables = null;
    StockAdjustment stockAdjustment = null;
    StockAdjustmentItem stockAdjustmentItem = null;

    for (Account account : accountList) {
      stockAdjustment = new StockAdjustment(salesInvoice.getCompanyId(), account, SystemConstants.STOCK_ADJUSTMENT_TYPE_SALEABLE, salesInvoice.getInvoiceNo(), salesInvoice.getInvoiceEntryDate(), "Adjusting stock after canceling sales invoice - " + salesInvoice.getInvoiceNo(), SystemConstants.CONFIRMED);
      stockAdjustment.setReverseEntry(AccountingConstant.REVERSE_ENTRY);
      insert(main, stockAdjustment);
      stockSaleables = AppService.list(main, StockSaleable.class, "select * from scm_stock_saleable where account_id = ? and sales_invoice_item_id in"
              + "(select id from scm_sales_invoice_item where sales_invoice_id = ?)", new Object[]{account.getId(), salesInvoice.getId()});

      for (StockSaleable stock : stockSaleables) {
        stockAdjustmentItem = new StockAdjustmentItem();
        stockAdjustmentItem.setStockAdjustmentId(stockAdjustment);
        stockAdjustmentItem.setProductDetailId(stock.getProductDetailId());
        stockAdjustmentItem.setProductEntryDetailId(stock.getRefProductEntryDetailId());
        stockAdjustmentItem.setStockId(stock.getId());
        stockAdjustmentItem.setAccountId(account);
        stockAdjustmentItem.setProductId(stock.getProductDetailId().getProductBatchId().getProductId());
        stockAdjustmentItem.setQuantitySaleableActual(stock.getQuantityOut().intValue());
        stockAdjustmentItem.setAdjustedQty(stock.getQuantityOut().intValue());

        StockAdjustmentItemService.insert(main, stockAdjustmentItem);

        StockSaleableService.adjustSaleableStock(main, stockAdjustmentItem);

      }
    }
  }

  /**
   *
   * @param main
   * @param stockAdjustment
   */
  public static void resetStockAdjustmentToDraft(Main main, StockAdjustment stockAdjustment) {
    String saleableSql = "delete from scm_stock_saleable where ";
    String nonSaleableSql = "delete from scm_stock_non_saleable where ";
    String cond = "";
    ArrayList<Object> params = new ArrayList<>();
    if (stockAdjustment.getAccountId() != null) {
      cond += " account_id = ? and ";
      params.add(stockAdjustment.getAccountId().getId());
    } else if (stockAdjustment.getAccountGroupId() != null) {
      cond += " account_id in(select account_id from scm_account_group_detail where account_group_id=?) and ";
      params.add(stockAdjustment.getAccountGroupId().getId());
    }
    cond += " stock_adjustment_item_id in (select id from scm_stock_adjustment_item where stock_adjustment_id = ?)";
    params.add(stockAdjustment.getId());
    AppService.deleteSql(main, StockSaleable.class, saleableSql + cond, params.toArray());
    AppService.deleteSql(main, StockNonSaleable.class, nonSaleableSql + cond, params.toArray());

    main.clear();
    main.param(stockAdjustment.getId());
    AppService.updateSql(main, StockAdjustmentItem.class, "update scm_stock_adjustment_item set product_entry_detail_id = null where stock_adjustment_id = ?", false);
    main.clear();
    stockAdjustment.setStatusId(SystemConstants.DRAFT);
    updateByPk(main, stockAdjustment);

  }

  /**
   *
   * @param main
   * @param selectedStockExcess
   * @return
   */
  public static List<StockAdjustmentItem> insertOrUpdateStockAdjustmentExcess(Main main, StockExcess[] selectedStockExcess, StockAdjustment stockAdjustment) {
    List<StockAdjustmentItem> stockAdjustmentItemList = new ArrayList<>();
    StockAdjustmentItem stockAdjustmentItem = null;
    insertOrUpdate(main, stockAdjustment);
    for (StockExcess stockExcess : selectedStockExcess) {
      if (stockExcess.getSaleableQty() != null && stockExcess.getSaleableQty() > 0) {
        stockAdjustmentItem = new StockAdjustmentItem(stockAdjustment, stockExcess);
        if (stockAdjustmentItem.getProductDetailId() != null) {
          stockAdjustmentItem.setProductDetailId(ProductDetailService.selectByPk(main, stockAdjustmentItem.getProductDetailId()));
          stockAdjustmentItem.setAccountId(stockAdjustmentItem.getProductDetailId().getAccountId());
        }
        if (stockAdjustmentItem.getProductEntryDetailId() != null) {
          stockAdjustmentItem.setProductEntryDetailId(ProductEntryDetailService.selectByPk(main, stockAdjustmentItem.getProductEntryDetailId()));
        }
        StockAdjustmentItemService.insert(main, stockAdjustmentItem);
        stockAdjustmentItemList.add(stockAdjustmentItem);
      }
    }
    return stockAdjustmentItemList;
  }

  public static List<StockAdjustmentDetail> updateStockAdjustmentDetail(Main main, StockAdjustment stockAdjustment, StockAdjustmentItem adjustmentItem, String filter) {
    String sql = "SELECT product_detail_id,product_batch_id,sum(quantity_in) quantity_in,batch_no,mrp_value,pack_size,expiry_date_actual,account_id,"
            + "sum(quantity_saleable) quantity_saleable,sum(quantity_damaged) quantity_damaged "
            + "FROM getStockByAccountGroupOrAccountForAdjustment(?,?,?) where batch_no like UPPER(?) "
            + "and product_detail_id not in(select product_detail_id from scm_stock_adjustment_item where stock_adjustment_id = ?)"
            + "group by product_detail_id,product_batch_id,batch_no,mrp_value,pack_size,expiry_date_actual,account_id";
    List<StockAdjustmentDetail> adjustmentDetailList = AppDb.getList(main.dbConnector(), StockAdjustmentDetail.class, sql,
            new Object[]{stockAdjustment.getAccountGroupId() != null ? stockAdjustment.getAccountGroupId().getId() : null,
              stockAdjustment.getAccountId() != null ? stockAdjustment.getAccountId().getId() : null, adjustmentItem.getProductId().getId(),
              "%" + filter.toUpperCase() + "%", stockAdjustment.getId()});
    return adjustmentDetailList;
  }

}
