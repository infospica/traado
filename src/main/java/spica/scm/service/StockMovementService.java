/*
 * @(#)StockMovementService.java	1.0 Thu Mar 29 12:11:05 IST 2018 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import spica.scm.common.StockAdjustmentDetail;
import spica.scm.common.StocktoExpiry;
import spica.scm.domain.Account;
import spica.scm.domain.AccountGroup;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.Company;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductEntryDetail;
import wawo.app.common.AppService;
import wawo.app.Main;
import wawo.entity.core.SqlPage;
import spica.scm.domain.StockMovement;
import spica.scm.domain.StockMovementItem;
import spica.scm.validate.StockMovementIs;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.util.StringUtil;

/**
 * StockMovementService
 *
 * @author	Spirit 1.2
 * @version	1.0, Thu Mar 29 12:11:05 IST 2018
 */
public abstract class StockMovementService {

  /**
   * StockMovement paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getStockMovementSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("scm_stock_movement", StockMovement.class, main);
    sql.main("select scm_stock_movement.id,scm_stock_movement.company_id,scm_stock_movement.account_id,scm_stock_movement.reference_no,scm_stock_movement.entry_date,scm_stock_movement.description,"
            + "scm_stock_movement.stock_movement_type,scm_stock_movement.status_id,scm_stock_movement.created_by,scm_stock_movement.modified_by,scm_stock_movement.created_at,scm_stock_movement.modified_at, "
            + "scm_stock_movement.confirmed_at,scm_stock_movement.confirmed_by from scm_stock_movement scm_stock_movement "); //Main query
    sql.count("select count(scm_stock_movement.id) as total from scm_stock_movement scm_stock_movement "); //Count query
    sql.join("left outer join scm_company scm_stock_movementcompany_id on (scm_stock_movementcompany_id.id = scm_stock_movement.company_id) left outer join scm_account scm_stock_movementaccount_id on (scm_stock_movementaccount_id.id = scm_stock_movement.account_id)"); //Join Query

    sql.string(new String[]{"scm_stock_movementcompany_id.company_name", "scm_stock_movementaccount_id.account_code", "scm_stock_movement.reference_no", "scm_stock_movement.description", "scm_stock_movement.created_by", "scm_stock_movement.modified_by"}); //String search or sort fields
    sql.number(new String[]{"scm_stock_movement.id", "scm_stock_movement.status_id"}); //Numeric search or sort fields
    sql.date(new String[]{"scm_stock_movement.entry_date", "scm_stock_movement.created_at", "scm_stock_movement.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of StockMovement.
   *
   * @param main
   * @return List of StockMovement
   */
  public static final List<StockMovement> listPaged(Main main) {
    return AppService.listPagedJpa(main, getStockMovementSqlPaged(main));
  }

  /**
   *
   * @param main
   * @param account
   * @return
   */
  public static final List<StockMovement> listPagedByMovementType(Main main, Integer stockMovementType, AccountingFinancialYear accountingFinancialYear) {
    SqlPage sql = getStockMovementSqlPaged(main);
    sql.cond(" where scm_stock_movement.company_id=? and scm_stock_movement.financial_year_id=? ");
    sql.param(UserRuntimeView.instance().getCompany().getId());
    sql.param(UserRuntimeView.instance().getCompany().getCurrentFinancialYear().getId());
    if (stockMovementType != null) {
      sql.cond(" and scm_stock_movement.stock_movement_type = ? ");
      sql.param(stockMovementType);
    }
    sql.orderBy("  scm_stock_movement.entry_date desc ");
//    scm_stock_movement.account_id = ?
//    sql.param(account.getId());
    return AppService.listPagedJpa(main, sql);
  }

//  /**
//   * Return list of StockMovement based on condition
//   * @param main
//   * @return List<StockMovement>
//   */
//  public static final List<StockMovement> listPagedBySomeCondition(Main main) {
//     SqlPage sql = getStockMovementSqlPaged(main);
//     sql.cond("where 1=? and 2=?");
//     sql.param("valueof1");
//     sql.param("valueof2");
//     return AppService.listPagedJpa(main, sql); // For pagination in view
//   //  return AppService.listAllJpa(main, sql); // Return the full records
//  }
  /**
   * Select StockMovement by key.
   *
   * @param main
   * @param stockMovement
   * @return StockMovement
   */
  public static final StockMovement selectByPk(Main main, StockMovement stockMovement) {
    return (StockMovement) AppService.find(main, StockMovement.class, stockMovement.getId());
  }

  /**
   * Insert StockMovement.
   *
   * @param main
   * @param stockMovement
   */
  public static final void insert(Main main, StockMovement stockMovement) {
    StockMovementIs.insertAble(main, stockMovement);  //Validating
    AppService.insert(main, stockMovement);

  }

  /**
   * Update StockMovement by key.
   *
   * @param main
   * @param stockMovement
   * @return StockMovement
   */
  public static final StockMovement updateByPk(Main main, StockMovement stockMovement) {
    StockMovementIs.updateAble(main, stockMovement); //Validating
    return (StockMovement) AppService.update(main, stockMovement);
  }

  /**
   * Insert or update StockMovement
   *
   * @param main
   * @param stockMovement
   */
  public static void insertOrUpdate(Main main, StockMovement stockMovement) {
    if (stockMovement.getId() == null) {
      insert(main, stockMovement);
    } else {
      updateByPk(main, stockMovement);
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param stockMovement
   */
  public static void clone(Main main, StockMovement stockMovement) {
    stockMovement.setId(null); //Set to null for insert
    insert(main, stockMovement);
  }

  /**
   * Delete StockMovement.
   *
   * @param main
   * @param stockMovement
   */
  public static final void deleteByPk(Main main, StockMovement stockMovement) {
    StockMovementIs.deleteAble(main, stockMovement); //Validation
    List<StockMovementItem> stockMovementItemList = StockMovementItemService.getStockMovementItemForExpiry(main, stockMovement);
    for (StockMovementItem stockMovementItem : stockMovementItemList) {
      StockMovementItemService.deleteByPk(main, stockMovementItem);
    }
    AppService.delete(main, StockMovement.class, stockMovement.getId());
  }

  /**
   * Delete Array of StockMovement.
   *
   * @param main
   * @param stockMovement
   */
  public static final void deleteByPkArray(Main main, StockMovement[] stockMovement) {
    for (StockMovement e : stockMovement) {
      deleteByPk(main, e);
    }
  }

  /**
   *
   * @param main
   * @param stockMovement
   * @param stockMovementItemList
   */
  public static void insertOrUpdateStockMovement(Main main, StockMovement stockMovement, List<StockMovementItem> stockMovementItemList) {
    List<StockAdjustmentDetail> stockAdjustmentDetailList = null;
    Integer stockMovementQty = null, maxMovementQty;
    boolean isDamagedToSaleable = false;

    if (SystemConstants.CONFIRMED.equals(stockMovement.getStatusId())) {
      stockMovement.setConfirmedAt(new java.util.Date());
      stockMovement.setConfirmedBy(main.getAppUser().getLogin());
      if (stockMovement.getAccountId() != null) {
        stockMovement.setReferenceNo(AccountGroupDocPrefixService.getNextPurchasePrefixSequence(main, stockMovement.getAccountId(), PrefixTypeService.STOCK_MOVEMENT_PREFIX_ID, false, stockMovement.getFinancialYearId()));
        AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, stockMovement.getAccountId(), PrefixTypeService.STOCK_MOVEMENT_PREFIX_ID, false, stockMovement.getFinancialYearId());
      } else if (stockMovement.getAccountGroupId() != null) {
        stockMovement.setReferenceNo(AccountGroupDocPrefixService.getNextSalesPrefixSequence(main, stockMovement.getAccountGroupId(), PrefixTypeService.STOCK_MOVEMENT_PREFIX_ID, false, stockMovement.getFinancialYearId()));
        AccountGroupDocPrefixService.updateSalesPrefixSequence(main, stockMovement.getAccountGroupId(), PrefixTypeService.STOCK_MOVEMENT_PREFIX_ID, false, stockMovement.getFinancialYearId());
      }
    }
    if (SystemConstants.DRAFT.equals(stockMovement.getStatusId())) {
      if (stockMovement.getAccountId() != null) {
        AccountGroupDocPrefixService.updatePurchasePrefixSequence(main, stockMovement.getAccountId(), PrefixTypeService.STOCK_MOVEMENT_PREFIX_ID, true, stockMovement.getFinancialYearId());
      } else if (stockMovement.getAccountGroupId() != null) {
        AccountGroupDocPrefixService.updateSalesPrefixSequence(main, stockMovement.getAccountGroupId(), PrefixTypeService.STOCK_MOVEMENT_PREFIX_ID, true, stockMovement.getFinancialYearId());
      }
    }

    insertOrUpdate(main, stockMovement);

    if (!StringUtil.isEmpty(stockMovementItemList)) {
      for (StockMovementItem stockMovementItem : stockMovementItemList) {
        if (stockMovementItem.getProductDetailId() != null && (stockMovementItem.getQuantitySaleableActual() != null || stockMovementItem.getQuantityDamagedActual() != null)) {
          stockMovementItem.setStockMovementId(stockMovement);
          if (stockMovementItem.getProductDetailId().getAccountId() != null) {
            stockMovementItem.setAccountId(stockMovementItem.getProductDetailId().getAccountId());
          }
          StockMovementItemService.insertOrUpdate(main, stockMovementItem);
        }
      }
    }

    if (SystemConstants.CONFIRMED.equals(stockMovement.getStatusId())) {
      isDamagedToSaleable = (stockMovement.getStockMovementType() == SystemConstants.STOCK_MOVEMENT_DAMAGED_TO_SALEABLE);

      for (StockMovementItem stockMovementItem : stockMovementItemList) {
        stockMovementQty = null;
        if (stockMovementItem.getProductDetailId() != null && (stockMovementItem.getQuantitySaleableActual() != null || stockMovementItem.getQuantityDamagedActual() != null)) {

          stockAdjustmentDetailList = AppDb.getList(main.dbConnector(), StockAdjustmentDetail.class,
                  "SELECT * FROM getStockByAccountGroupOrAccountForAdjustment(?,?,?) where product_detail_id = ? order by product_entry_detail_id desc",
                  new Object[]{stockMovement.getAccountGroupId() != null ? stockMovement.getAccountGroupId().getId() : null,
                    stockMovement.getAccountId() != null ? stockMovement.getAccountId().getId() : null,
                    stockMovementItem.getProductId().getId(), stockMovementItem.getProductDetailId().getId()});

          for (StockAdjustmentDetail stockAdjustmentDetail : stockAdjustmentDetailList) {
            StockMovementItem movementItem = new StockMovementItem(stockMovementItem);
            movementItem.setProductEntryDetailId(new ProductEntryDetail(stockAdjustmentDetail.getProductEntryDetailId()));
            Account account = AccountService.selectById(main, stockAdjustmentDetail.getAccountId());
            movementItem.setAccountId(account);

            if (isDamagedToSaleable) {
              stockMovementQty = stockMovementItem.getQuantitySaleableActual() == null ? 0 : stockMovementQty == null ? stockMovementItem.getQuantitySaleableActual() : stockMovementQty;
              maxMovementQty = stockAdjustmentDetail.getQuantityDamaged() == null ? 0 : stockAdjustmentDetail.getQuantityDamaged();
              movementItem.setStockId(stockAdjustmentDetail.getStockNonId());
            } else {
              stockMovementQty = stockMovementItem.getQuantityDamagedActual() == null ? 0 : stockMovementQty == null ? stockMovementItem.getQuantityDamagedActual() : stockMovementQty;
              maxMovementQty = stockAdjustmentDetail.getQuantitySaleable() == null ? 0 : stockAdjustmentDetail.getQuantitySaleable();
              movementItem.setStockId(stockAdjustmentDetail.getStockId());
            }
            stockMovementQty = updateStock(main, movementItem, stockMovementQty, maxMovementQty, isDamagedToSaleable);
          }
          StockMovementItemService.deleteByPk(main, stockMovementItem);
        }
      }
    }
  }

  /**
   *
   * @param main
   * @param stockMovementItem
   * @param adjustmentQty
   * @param currentQty
   * @param isDamagedToSaleable
   * @return
   */
  private static Integer updateStock(Main main, StockMovementItem stockMovementItem, Integer adjustmentQty, Integer currentQty, boolean isDamagedToSaleable) {

    if (adjustmentQty != 0 && currentQty != 0) {
      if (adjustmentQty <= currentQty) {
        if (isDamagedToSaleable) {
          stockMovementItem.setQuantitySaleableActual(adjustmentQty);
        } else {
          stockMovementItem.setQuantityDamagedActual(adjustmentQty);
        }
        StockMovementItemService.insertOrUpdate(main, stockMovementItem);

        if (isDamagedToSaleable) {
          StockNonSaleableService.stockMovementDamagedToSaleable(main, stockMovementItem);
        } else {
          StockSaleableService.stockMovementSaleableToDamaged(main, stockMovementItem);
        }
        adjustmentQty = 0;
      } else {

        if (isDamagedToSaleable) {
          stockMovementItem.setQuantitySaleableActual(currentQty);
        } else {
          stockMovementItem.setQuantityDamagedActual(currentQty);
        }

        StockMovementItemService.insertOrUpdate(main, stockMovementItem);

        if (isDamagedToSaleable) {
          StockNonSaleableService.stockMovementDamagedToSaleable(main, stockMovementItem);
        } else {
          StockSaleableService.stockMovementSaleableToDamaged(main, stockMovementItem);
        }

        adjustmentQty -= currentQty;
      }
    }
    return adjustmentQty;
  }

  /**
   *
   * @param main
   * @param productDetailId
   * @return
   */
  public static StockAdjustmentDetail selectStockAdjustmentDetailByProductDetail(Main main, StockMovement stockMovement, ProductDetail productDetailId) {
    StockAdjustmentDetail stockAdjustmentDetail = null;
    if (productDetailId != null) {
      stockAdjustmentDetail = (StockAdjustmentDetail) AppDb.single(main.dbConnector(), StockAdjustmentDetail.class, "SELECT product_detail_id,sum(quantity_in) quantity_in,"
              + "sum(quantity_saleable) quantity_saleable,sum(quantity_damaged) quantity_damaged "
              + "FROM getStockByAccountGroupOrAccountForAdjustment(?,?,?) where product_detail_id = ? group by product_detail_id",
              new Object[]{stockMovement.getAccountGroupId() != null ? stockMovement.getAccountGroupId().getId() : null,
                stockMovement.getAccountId() != null ? stockMovement.getAccountId().getId() : null,
                productDetailId.getProductBatchId().getProductId().getId(), productDetailId.getId()});
    }
    return stockAdjustmentDetail;
  }

  public static List<StocktoExpiry> getExpiredProductList(Main main, Company company, AccountGroup accountgroup, Account account, Date expiryDate) {
    if (expiryDate != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(expiryDate);
      return AppDb.getList(main.dbConnector(), StocktoExpiry.class, "select product_name,batch_no,expiry_date_actual as expiry_date,pack_size,mrp_value,"
              + "SUM(COALESCE(quantity_saleable,0)+COALESCE(quantity_free_saleable,0)) as quantity,purchase_rate,pts_rate \n"
              + "from moveexpirytononsaleable(?,?,?,?,?,?) \n"
              + "group by product_name,batch_no,expiry_date_actual,pack_size,mrp_value,purchase_rate,pts_rate order by product_name",
              new Object[]{company.getId(), accountgroup != null ? accountgroup.getId() : null, account != null ? account.getId() : null,
                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)});
    }
    return null;
  }

  public static List<StocktoExpiry> getExpiredProductforMovement(Main main, Company company, AccountGroup accountgroup, Account account, Date expiryDate) {
    if (expiryDate != null) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(expiryDate);
      return AppDb.getList(main.dbConnector(), StocktoExpiry.class, "select * from moveexpirytononsaleable(?,?,?,?,?,?) ",
              new Object[]{company.getId(), accountgroup != null ? accountgroup.getId() : null, account != null ? account.getId() : null,
                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)});
    }
    return null;
  }

  public static List<StocktoExpiry> getMovedExpiryProducts(Main main, List<StockMovementItem> stockMovementItemList) {
    List<StocktoExpiry> stocktoExpiryList = null;
    String sql = "select p.product_name,pb.batch_no,pb.expiry_date_actual as expiry_date,CONCAT(p.pack_size,' ',pu.symbol) as pack_size,pb.value_mrp as mrp_value,\n"
            + "COALESCE(ped.purchase_rate_per_piece,ped.value_rate_per_prod_piece_der) as purchase_rate,\n"
            + "ped.value_pts as pts_rate,ss.quantity_out as quantity\n"
            + "from scm_product p\n"
            + "left join scm_product_batch pb on pb.product_id = p.id \n"
            + "left join scm_product_detail pd on pb.id =pd.product_batch_id\n"
            + "left join scm_product_unit pu on pu.id= p.product_unit_id\n"
            + "left join scm_product_entry_detail ped on ped.product_detail_id = pd.id\n"
            + "left join scm_stock_saleable ss on ss.ref_product_entry_detail_id = ped.id and ss.stock_status_id=?\n";
    String placeHolders = "";
    ArrayList<Object> params = new ArrayList<>();
    ArrayList<Object> pIdList = new ArrayList<>();
    ArrayList<Object> pdIdList = new ArrayList<>();
    ArrayList<Object> pedIdList = new ArrayList<>();
    int i = 1;
    for (StockMovementItem item : stockMovementItemList) {
      pIdList.add(item.getProductId().getId());
      pdIdList.add(item.getProductDetailId().getId());
      pedIdList.add(item.getProductEntryDetailId().getId());
      placeHolders += "?";
      if (i != stockMovementItemList.size()) {
        placeHolders += ",";
      }
      i++;
    }
    params.add(StockStatusService.STOCK_STATUS_EXPIRED);
    params.addAll(pIdList);
    params.addAll(pdIdList);
    params.addAll(pedIdList);

    String cond = "where p.id in(" + placeHolders + ") and pd.id in(" + placeHolders + ") and ped.id in(" + placeHolders + ") ";
    sql = "select product_name,batch_no,expiry_date,pack_size,mrp_value,purchase_rate,pts_rate,SUM(quantity) as quantity from (" + sql + cond + ") tab "
            + "group by product_name,batch_no,expiry_date,pack_size,mrp_value,purchase_rate,pts_rate";
    stocktoExpiryList = AppDb.getList(main.dbConnector(), StocktoExpiry.class, sql, params.toArray());
    return stocktoExpiryList;
  }

  public static List<StockAdjustmentDetail> selectStockAdjustmentDetailByProduct(Main main, StockMovement stockMovement, StockMovementItem movementItem, String filter) {
    List<StockAdjustmentDetail> stockAdjustmentDetailList = null;
    stockAdjustmentDetailList = AppDb.getList(main.dbConnector(), StockAdjustmentDetail.class, "SELECT product_detail_id,product_batch_id,"
            + "sum(quantity_in) quantity_in,account_id,batch_no,mrp_value,expiry_date_actual,"
            + "sum(quantity_saleable) quantity_saleable,sum(quantity_damaged) quantity_damaged "
            + "FROM getStockByAccountGroupOrAccountForAdjustment(?,?,?) where batch_no like UPPER(?) group by product_detail_id,account_id,batch_no,mrp_value,expiry_date_actual,product_batch_id",
            new Object[]{stockMovement.getAccountGroupId() != null ? stockMovement.getAccountGroupId().getId() : null,
              stockMovement.getAccountId() != null ? stockMovement.getAccountId().getId() : null, movementItem.getProductId().getId(),
              "%" + filter.toUpperCase() + "%"});
    return stockAdjustmentDetailList;
  }

  public static boolean isStockMovementEditable(MainView main, StockMovement updateStockMovement) {
    StockMovement stockMovement = selectByPk(main, updateStockMovement);
    if (stockMovement.getModifiedAt() == null) {
      return true;
    } else if (stockMovement.getModifiedAt() != null && updateStockMovement.getModifiedAt() == null) {
      return false;
    } else {
      return isPastModifiedDate(stockMovement.getModifiedAt(), updateStockMovement.getModifiedAt());
    }
  }

  private static boolean isPastModifiedDate(Date currentDate, Date modifiedDate) {
    return !currentDate.after(modifiedDate);
  }
}
