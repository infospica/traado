/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.util;

import java.util.Calendar;
import java.util.Date;
import spica.scm.common.ProductDetailSales;
import spica.scm.domain.Account;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyAddress;
import spica.scm.domain.Customer;
import spica.scm.domain.CustomerAddress;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.StockSaleable;
import spica.scm.domain.StockStatus;
import spica.scm.service.StockStatusService;
import spica.sys.SystemConstants;
import wawo.entity.util.StringUtil;

/**
 *
 * @author java-2
 */
public abstract class SalesInvoiceUtil {

  public static final Integer PROFORMA_ITEM_NEW = 1;
  public static final Integer PROFORMA_ITEM_MODIFIED = 2;
  public static final Integer PROFORMA_ITEM_DELETED = 3;
  public static final Integer PROFORMA_ITEM_FREE_QTY_DELETED = 4;
  public static final Integer PROFORMA_ITEM_QTY_INCREMENTED = 5;
  public static final Integer PROFORMA_ITEM_QTY_DECREMENTED = 6;
  public static final Integer PROFORMA_ITEM_FREE_QTY_INCREMENTED = 7;
  public static final Integer PROFORMA_ITEM_FREE_QTY_DECREMENTED = 8;
  public static final Integer PROFORMA_ITEM_FREE_QTY_NEW = 9;

  public static final Integer PROFORMA_MODIFIED = 1;
  public static final Integer REQUESTED_FEW_ITEMS_NOT_AVAILABLE = 1;
  public static final Integer REQUESTED_ITEMS_SOLD = 0;
  public static final int PRODUCT_EMPTY_QTY = 0;
  public static final int PRODUCT_PRIMARY_QTY = 1;
  public static final int PRODUCT_SECONDARY_QTY = 2;
  public static final int PRODUCT_TERTIARY_QTY = 3;
  public static final int PRODUCT_FREE_QTY = 4;

  public static final int INTER_STATE_SALES = 1;
  public static final int INTRA_STATE_SALES = 2;
  public static final int INTER_NATIONAL_SALES = 3;

  public static final SalesInvoiceItem isProformaItemModified(SalesInvoiceItem salesInvoiceItem) {
    Integer productQuantity = null;
    Integer productFreeQuantity = null;

    if (salesInvoiceItem.getId() == null && salesInvoiceItem.getProformaItemStatus() == null) {
      salesInvoiceItem.setProformaItemStatus(SalesInvoiceUtil.PROFORMA_ITEM_NEW);
    } else {
      if (salesInvoiceItem.getProformaItemStatus() == null || !PROFORMA_ITEM_NEW.equals(salesInvoiceItem.getProformaItemStatus())) {

        if (salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProductQty() >= 0) {
          if ((salesInvoiceItem.getOldProductQty() != null)) {
            if (salesInvoiceItem.getProductQty() == 0) {
              productQuantity = 0;
            } else {
              if ((salesInvoiceItem.getProductQty() > salesInvoiceItem.getOldProductQty())) {
                productQuantity = salesInvoiceItem.getProductQty() - salesInvoiceItem.getOldProductQty();
              } else if ((salesInvoiceItem.getProductQty() < salesInvoiceItem.getOldProductQty())) {
                productQuantity = salesInvoiceItem.getProductQty() - salesInvoiceItem.getOldProductQty();
              }

              if (productQuantity != null && productQuantity != 0) {
                salesInvoiceItem.setProformaProductQtyVariation(productQuantity);
                salesInvoiceItem.setProformaItemStatus(PROFORMA_ITEM_MODIFIED);
              }
            }
          }
        }

        if (salesInvoiceItem.getProductQtyFree() != null && salesInvoiceItem.getProductQtyFree() >= 0) {
          if ((salesInvoiceItem.getOldFreeQty() != null)) {
            if (salesInvoiceItem.getProductQtyFree() == 0) {
              productFreeQuantity = 0;
              salesInvoiceItem.setProformaItemStatus(PROFORMA_ITEM_FREE_QTY_DELETED);
            } else {
              productFreeQuantity = salesInvoiceItem.getProductQtyFree() - salesInvoiceItem.getOldFreeQty();
              if (productFreeQuantity != 0) {
                salesInvoiceItem.setProformaFreeQtyVariation(productFreeQuantity);
                salesInvoiceItem.setProformaItemStatus(PROFORMA_ITEM_MODIFIED);
              } else {
                productFreeQuantity = salesInvoiceItem.getProductQtyFree();
              }
            }
          } else {
            productFreeQuantity = salesInvoiceItem.getProductQtyFree();
            salesInvoiceItem.setProformaFreeQtyVariation(productFreeQuantity);
            salesInvoiceItem.setProformaItemStatus(PROFORMA_ITEM_MODIFIED);
          }
        }

        if ((productQuantity != null && productQuantity == 0) && (productFreeQuantity != null && productFreeQuantity == 0)) {
          salesInvoiceItem.setProformaItemStatus(PROFORMA_ITEM_DELETED);
        }
      }
    }

    return salesInvoiceItem;
  }

  public static StockSaleable getStockSaleableBlocked(StockSaleable stockSaleable) {
    StockSaleable stockSaleableBlocked = new StockSaleable();

    stockSaleableBlocked.setAccountId(stockSaleable.getAccountId());
    stockSaleableBlocked.setBlockedAt(new Date());
    stockSaleableBlocked.setProductDetailId(stockSaleable.getProductDetailId());
    stockSaleableBlocked.setQuantityIn(stockSaleable.getQuantityOut());
    stockSaleableBlocked.setRefProductEntryDetailId(stockSaleable.getRefProductEntryDetailId());
    stockSaleableBlocked.setSalesInvoiceItemId(stockSaleable.getSalesInvoiceItemId());
    stockSaleableBlocked.setStockSaleableId(stockSaleable);
    stockSaleableBlocked.setStockType(stockSaleable.getStockType());
    stockSaleableBlocked.setStockStatusId(new StockStatus(StockStatusService.STOCK_STATUS_BLOCKED));
    return stockSaleableBlocked;
  }

  public static StockSaleable getStockSaleable(SalesInvoiceItem salesInvoiceItem, Integer stockType) {
    StockSaleable stockSaleable = new StockSaleable();

    stockSaleable.setEntryDate(salesInvoiceItem.getSalesInvoiceId().getInvoiceEntryDate());
    stockSaleable.setAccountId(salesInvoiceItem.getAccountId());

    stockSaleable.setProductDetailId(salesInvoiceItem.getProductDetailId());
    stockSaleable.setSalesInvoiceItemId(salesInvoiceItem);

    stockSaleable.setRefProductEntryDetailId(salesInvoiceItem.getProductEntryDetailId());
    stockSaleable.setStockType(stockType);

    if (SystemConstants.STOCK_TYPE_SALEABLE.equals(stockType)) {
      stockSaleable.setStockSaleableId(new StockSaleable(salesInvoiceItem.getProductDetailSales().getStockId()));
      stockSaleable.setStockStatusId(new StockStatus(salesInvoiceItem.getProductDetailSales().getStockStatusId()));
    } else if (SystemConstants.STOCK_TYPE_FREE.equals(stockType)) {
      stockSaleable.setStockSaleableId(new StockSaleable(salesInvoiceItem.getProductDetailSales().getFreeStockId()));
      stockSaleable.setStockStatusId(new StockStatus(salesInvoiceItem.getProductDetailSales().getFreeStockStatusId()));
    }
    return stockSaleable;
  }

  public static StockSaleable getStockOutSaleable(SalesInvoiceItem salesInvoiceItem) {
    StockSaleable stockSaleable = new StockSaleable();
    stockSaleable.setAccountId(salesInvoiceItem.getAccountId());
    stockSaleable.setProductDetailId(salesInvoiceItem.getProductDetailId());
    stockSaleable.setEntryDate(salesInvoiceItem.getSalesInvoiceId().getInvoiceEntryDate());
    stockSaleable.setRefProductEntryDetailId(salesInvoiceItem.getProductEntryDetailId());
    stockSaleable.setStockSaleableId(new StockSaleable(salesInvoiceItem.getStockId()));
    stockSaleable.setStockStatusId(new StockStatus(StockStatusService.STOCK_STATUS_SALEABLE));
    stockSaleable.setStockType(StockStatusService.STOCK_TYPE_SALEABLE);
    return stockSaleable;
  }

  public static StockSaleable getStockInFreeSaleable(SalesInvoiceItem salesInvoiceItem, StockSaleable parentStockSaleable) {
    StockSaleable stockSaleable = new StockSaleable();
    stockSaleable.setAccountId(salesInvoiceItem.getAccountId());
    stockSaleable.setProductDetailId(salesInvoiceItem.getProductDetailId());
    stockSaleable.setEntryDate(salesInvoiceItem.getSalesInvoiceId().getInvoiceEntryDate());
    stockSaleable.setRefProductEntryDetailId(salesInvoiceItem.getProductEntryDetailId());
    stockSaleable.setStockSaleableId(parentStockSaleable);
    stockSaleable.setStockStatusId(new StockStatus(StockStatusService.STOCK_STATUS_SALEABLE));
    stockSaleable.setStockType(StockStatusService.STOCK_TYPE_FREE);
    return stockSaleable;
  }

  public static StockSaleable getStockOutFreeSaleable(SalesInvoiceItem salesInvoiceItem, StockSaleable parentStockSaleable) {
    StockSaleable stockSaleable = new StockSaleable();
    stockSaleable.setAccountId(salesInvoiceItem.getAccountId());
    stockSaleable.setProductDetailId(salesInvoiceItem.getProductDetailId());
    stockSaleable.setSalesInvoiceItemId(salesInvoiceItem);
    stockSaleable.setEntryDate(salesInvoiceItem.getSalesInvoiceId().getInvoiceEntryDate());
    stockSaleable.setRefProductEntryDetailId(salesInvoiceItem.getProductEntryDetailId());
    stockSaleable.setStockSaleableId(parentStockSaleable);
    stockSaleable.setStockStatusId(new StockStatus(StockStatusService.STOCK_STATUS_SALEABLE));
    stockSaleable.setStockType(StockStatusService.STOCK_TYPE_FREE);
    return stockSaleable;
  }

  public static SalesInvoiceItem getSalesInvoiceItem(SalesInvoiceItem salesInvoiceItem, ProductDetailSales productDetailSales, Integer quantity) {
    SalesInvoiceItem item = new SalesInvoiceItem();
    item.setSalesInvoiceId(salesInvoiceItem.getSalesInvoiceId());
    if (productDetailSales.getProductDetailId() != null) {
      item.setProductDetailId(new ProductDetail(productDetailSales.getProductDetailId()));
    }

    if (productDetailSales.getAccountId() != null) {
      item.setAccountId(new Account(productDetailSales.getAccountId()));
    }

    item.setProductEntryDetailId(new ProductEntryDetail(productDetailSales.getProductEntryDetailId()));
    item.setMarginValueDeviationDer(productDetailSales.getMarginValueDeviationDer());
    item.setLandingRate(productDetailSales.getLandingRate());
    item.setBatchNo(salesInvoiceItem.getBatchNo());
    item.setExpiryDate(salesInvoiceItem.getExpiryDate());
    item.setProductQty(quantity);
    item.setProductFreeQtyScheme(salesInvoiceItem.getProductFreeQtyScheme());
    item.setProductDetailSales(productDetailSales);
    item.setValueProdPieceSelling(salesInvoiceItem.getValueProdPieceSelling());
    item.setValueProdPieceSold(salesInvoiceItem.getValueProdPieceSold());
    item.setProdPieceSellingForced(salesInvoiceItem.getProdPieceSellingForced());
    item.setServiceFreightValue(salesInvoiceItem.getServiceFreightValue());
    item.setServiceFreightValueDerived(salesInvoiceItem.getServiceFreightValueDerived());
    item.setService2Value(salesInvoiceItem.getService2Value());
    item.setService2ValueDerived(salesInvoiceItem.getService2ValueDerived());
    item.setKeralaFloodCessValue(salesInvoiceItem.getKeralaFloodCessValue());
    item.setKeralaFloodCessValueDerived(salesInvoiceItem.getKeralaFloodCessValueDerived());
    item.setHsnCode(salesInvoiceItem.getHsnCode());
    item.setProductDiscountValue(getProductDiscount(salesInvoiceItem.getProductDiscountValue(), quantity, salesInvoiceItem.getProductQty()));
    item.setProductDiscountPercentage(salesInvoiceItem.getProductDiscountPercentage());
    item.setSchemeDiscountValue(salesInvoiceItem.getSchemeDiscountValue());
    item.setSchemeDiscountPercentage(salesInvoiceItem.getSchemeDiscountPercentage());
    item.setSchemeDiscountActual(salesInvoiceItem.getSchemeDiscountActual());
    item.setIsSchemeDiscountInPercentage(salesInvoiceItem.getIsSchemeDiscountInPercentage());
    item.setTcsValue(salesInvoiceItem.getTcsValue());
    item.setTcsValueDerived(salesInvoiceItem.getTcsValueDerived());
    item.setSchemeDiscountDerived(salesInvoiceItem.getSchemeDiscountDerived());
    item.setProductDiscountDerived(salesInvoiceItem.getProductDiscountDerived());
    item.setIsProductDiscountInPercentage(salesInvoiceItem.getIsProductDiscountInPercentage());

    item.setInvoiceDiscountDerived(salesInvoiceItem.getInvoiceDiscountDerived());
    item.setInvoiceDiscountValue(salesInvoiceItem.getInvoiceDiscountValue());
    item.setCashDiscountValue(salesInvoiceItem.getCashDiscountValue());
    item.setCashDiscountValueDerived(salesInvoiceItem.getCashDiscountValueDerived());
    item.setQuantityOrFree(salesInvoiceItem.getQuantityOrFree());

    item.setInvoiceDiscountActual(salesInvoiceItem.getInvoiceDiscountActual());
    item.setProductDiscountActual(salesInvoiceItem.getProductDiscountActual());
    item.setSecToPriQuantityActual(salesInvoiceItem.getSecToPriQuantityActual());
    item.setTerToSecQuantityActual(salesInvoiceItem.getTerToSecQuantityActual());

    item.setProductHash(salesInvoiceItem.getProductHash());
    item.setIsTaxCodeModified(salesInvoiceItem.getIsTaxCodeModified());

    item.setActualSellingPriceDerived(productDetailSales.getActualPts());
    item.setValuePts(salesInvoiceItem.getValuePts());
    item.setValueMrp(salesInvoiceItem.getValueMrp());
    item.setValuePtr(salesInvoiceItem.getValuePtr());
    item.setValueVat(salesInvoiceItem.getValueVat());
    item.setValueCgst(salesInvoiceItem.getValueCgst());
    item.setValueSgst(salesInvoiceItem.getValueSgst());
    item.setValueIgst(salesInvoiceItem.getValueIgst());
    item.setTaxCodeId(salesInvoiceItem.getTaxCodeId());
    item.setValueGoods(salesInvoiceItem.getValueGoods());
    item.setValueSale(salesInvoiceItem.getValueSale());
    item.setValueTaxable(salesInvoiceItem.getValueTaxable());

    item.setStockId(productDetailSales.getStockId());
    item.setStockStatusId(productDetailSales.getStockStatusId());
    item.setProformaItemStatus(salesInvoiceItem.getProformaItemStatus());
    item.setItemDeleted(salesInvoiceItem.getItemDeleted());
    item.setSortOrder(salesInvoiceItem.getSortOrder());
    return item;
  }

  public static SalesInvoiceItem getSalesInvoiceItemFree(SalesInvoiceItem salesInvoiceItem, ProductDetailSales productDetailSales, Integer quantity) {
    SalesInvoiceItem item = new SalesInvoiceItem();
    item.setSalesInvoiceId(salesInvoiceItem.getSalesInvoiceId());
    item.setProductDetailId(salesInvoiceItem.getProductDetailId());
    item.setBatchNo(salesInvoiceItem.getBatchNo());
    item.setExpiryDate(salesInvoiceItem.getExpiryDate());
    item.setProductEntryDetailId(new ProductEntryDetail(productDetailSales.getProductEntryDetailId()));
    item.setMarginValueDeviationDer(productDetailSales.getMarginValueDeviationDer());
    item.setProductQty(quantity);
    item.setProductDetailSales(productDetailSales);
    item.setValueProdPieceSelling(salesInvoiceItem.getValueProdPieceSelling());
    item.setValueProdPieceSold(salesInvoiceItem.getValueProdPieceSold());
    item.setProdPieceSellingForced(salesInvoiceItem.getProdPieceSellingForced());
    item.setProductDiscountValue(getProductDiscount(salesInvoiceItem.getProductDiscountValue(), quantity, salesInvoiceItem.getProductQty()));
    item.setProductDiscountPercentage(salesInvoiceItem.getProductDiscountPercentage());

    item.setSchemeDiscountActual(salesInvoiceItem.getSchemeDiscountActual());
    item.setSchemeDiscountPercentage(salesInvoiceItem.getSchemeDiscountPercentage());
    item.setSchemeDiscountValue(getProductDiscount(salesInvoiceItem.getSchemeDiscountValue(), quantity, salesInvoiceItem.getProductQty()));
    item.setQuantityOrFree(salesInvoiceItem.getQuantityOrFree());

    item.setInvoiceDiscountActual(salesInvoiceItem.getInvoiceDiscountActual());
    item.setProductDiscountActual(salesInvoiceItem.getProductDiscountActual());
    item.setSecToPriQuantityActual(salesInvoiceItem.getSecToPriQuantityActual());
    item.setTerToSecQuantityActual(salesInvoiceItem.getTerToSecQuantityActual());
    item.setProductHash(salesInvoiceItem.getProductHash());
    item.setValuePts(salesInvoiceItem.getValuePts());
    item.setActualSellingPriceDerived(productDetailSales.getActualPts());
    item.setValueTaxable(salesInvoiceItem.getValueTaxable());
    item.setValueMrp(salesInvoiceItem.getValueMrp());
    item.setValuePtr(salesInvoiceItem.getValuePtr());
    item.setValueVat(salesInvoiceItem.getValueVat());
    item.setValueCgst(salesInvoiceItem.getValueCgst());
    item.setValueSgst(salesInvoiceItem.getValueSgst());
    item.setValueIgst(salesInvoiceItem.getValueIgst());
    item.setTaxCodeId(salesInvoiceItem.getTaxCodeId());
    item.setValueGoods(salesInvoiceItem.getValueGoods());
    item.setValueSale(salesInvoiceItem.getValueSale());
    item.setStockId(productDetailSales.getStockId());
    item.setStockStatusId(productDetailSales.getStockStatusId());
    item.setAccountId(new Account(productDetailSales.getAccountId()));
    item.setProformaItemStatus(salesInvoiceItem.getProformaItemStatus());
    item.setItemDeleted(salesInvoiceItem.getItemDeleted());
    item.setIsTaxCodeModified(salesInvoiceItem.getIsTaxCodeModified());
    item.setSortOrder(salesInvoiceItem.getSortOrder());
    item.setServiceFreightValue(salesInvoiceItem.getServiceFreightValue());
    item.setServiceFreightValueDerived(salesInvoiceItem.getServiceFreightValueDerived());
    item.setKeralaFloodCessValue(salesInvoiceItem.getKeralaFloodCessValue());
    item.setKeralaFloodCessValueDerived(salesInvoiceItem.getKeralaFloodCessValueDerived());
    return item;
  }

  public static SalesInvoiceItem getSalesInvoiceFreeItem(SalesInvoiceItem salesInvoiceItem, Integer inOut) {
    SalesInvoiceItem item = new SalesInvoiceItem();
    item.setSalesInvoiceId(salesInvoiceItem.getSalesInvoiceId());
    item.setProductDetailId(salesInvoiceItem.getProductDetailId());
    item.setProductEntryDetailId(salesInvoiceItem.getProductEntryDetailId());
    item.setBatchNo(salesInvoiceItem.getBatchNo());
    item.setExpiryDate(salesInvoiceItem.getExpiryDate());
    item.setProductQty(0);
    item.setMarginValueDeviationDer(salesInvoiceItem.getMarginValueDeviationDer());
    //item.setProductFreeQtyScheme(salesInvoiceItem.getProductFreeQtyScheme());
    item.setProductHash(salesInvoiceItem.getProductHash());
    item.setProductQtyFree(salesInvoiceItem.getProductQty());
    item.setValueProdPieceSelling(salesInvoiceItem.getValueProdPieceSelling());
    item.setValueProdPieceSold(salesInvoiceItem.getValueProdPieceSold());
    item.setProdPieceSellingForced(salesInvoiceItem.getProdPieceSellingForced());
    item.setInvoiceDiscountActual(salesInvoiceItem.getInvoiceDiscountActual());
    item.setProductDiscountActual(salesInvoiceItem.getProductDiscountActual());
    item.setSecToPriQuantityActual(salesInvoiceItem.getSecToPriQuantityActual());
    item.setTerToSecQuantityActual(salesInvoiceItem.getTerToSecQuantityActual());
    item.setQuantityOrFree(salesInvoiceItem.getQuantityOrFree());
    item.setValuePts(salesInvoiceItem.getValuePts());
    item.setValuePtr(salesInvoiceItem.getValuePtr());
    item.setActualSellingPriceDerived(salesInvoiceItem.getActualSellingPriceDerived());
    item.setFreeInOut(inOut);
    item.setAccountId(salesInvoiceItem.getAccountId());
    item.setProductDetailSales(salesInvoiceItem.getProductDetailSales());
    item.setProformaItemStatus(salesInvoiceItem.getProformaItemStatus());
    item.setItemDeleted(salesInvoiceItem.getItemDeleted());
    item.setIsTaxCodeModified(salesInvoiceItem.getIsTaxCodeModified());
    item.setSortOrder(salesInvoiceItem.getSortOrder());
    item.setServiceFreightValue(salesInvoiceItem.getServiceFreightValue());
    item.setServiceFreightValueDerived(salesInvoiceItem.getServiceFreightValueDerived());
    item.setKeralaFloodCessValue(salesInvoiceItem.getKeralaFloodCessValue());
    item.setKeralaFloodCessValueDerived(salesInvoiceItem.getKeralaFloodCessValueDerived());
    return item;
  }

  public static SalesInvoice getSalesInvoice(SalesInvoice salesInvoice) {
    SalesInvoice invoice = new SalesInvoice();
    invoice.setAccountGroupId(salesInvoice.getAccountGroupId());
    invoice.setAccountGroupPriceListId(salesInvoice.getAccountGroupPriceListId());
    invoice.setCommissionApplicableOn(salesInvoice.getCommissionApplicableOn());
    invoice.setCommodityId(salesInvoice.getCommodityId());
    invoice.setCompanySalesAgentCommissionPercentageActual(salesInvoice.getCompanySalesAgentCommissionPercentageActual());
    invoice.setCompanySalesAgentCommissionPercentageForced(salesInvoice.getCompanySalesAgentCommissionPercentageForced());
    invoice.setCompanySalesAgentCommissionValueActual(salesInvoice.getCompanySalesAgentCommissionValueActual());
    invoice.setCompanySalesAgentCommissionValueForced(salesInvoice.getCompanySalesAgentCommissionValueForced());
    invoice.setCustomerId(salesInvoice.getCustomerId());
    invoice.setFreightRate(salesInvoice.getFreightRate());
    invoice.setInvoiceAmount(salesInvoice.getInvoiceAmount());
    invoice.setInvoiceAmountDiscount(salesInvoice.getInvoiceAmountDiscount());
    invoice.setInvoiceAmountNet(salesInvoice.getInvoiceAmountNet());
    invoice.setInvoiceAmountVat(salesInvoice.getInvoiceAmountVat());
    invoice.setInvoiceAmountGst(salesInvoice.getInvoiceAmountGst());
    invoice.setInvoiceAmtDiscPercent(salesInvoice.getInvoiceAmtDiscPercent());
    invoice.setInvoiceDate(new Date());
    invoice.setNote(salesInvoice.getNote());
    invoice.setProductTypeId(salesInvoice.getProductTypeId());
    invoice.setProductType(StringUtil.isEmpty(salesInvoice.getProductType()) ? null : salesInvoice.getProductType());
    invoice.setRoundOff(salesInvoice.getRoundOff());
    invoice.setSalesCreditDays(salesInvoice.getSalesCreditDays());
    invoice.setTaxCodeId(salesInvoice.getTaxCodeId());
    invoice.setVendorSalesAgentPersonProfileId(salesInvoice.getVendorSalesAgentPersonProfileId());
    invoice.setSalesOrderId(salesInvoice.getSalesOrderId());
    return invoice;
  }

  public static SalesInvoiceItem cloneSalesInvoiceItem(SalesInvoiceItem salesInvoiceItem) {
    SalesInvoiceItem salesItem = new SalesInvoiceItem();
    salesItem.setBatchNo(salesInvoiceItem.getBatchNo());
    salesItem.setExpiryDate(salesInvoiceItem.getExpiryDate());
    salesItem.setSalesInvoiceId(salesInvoiceItem.getSalesInvoiceId());
    salesItem.setProductDetailId(salesInvoiceItem.getProductDetailId());
    salesItem.setProductEntryDetailId(salesInvoiceItem.getProductEntryDetailId());
    salesItem.setProductQty(salesInvoiceItem.getProductQty());
    salesItem.setProductQtyFree(salesInvoiceItem.getProductQtyFree());
    salesItem.setValueProdPieceSelling(salesInvoiceItem.getValueProdPieceSelling());
    salesItem.setValueProdPieceSold(salesInvoiceItem.getValueProdPieceSold());
    salesItem.setProdPieceSellingForced(salesInvoiceItem.getProdPieceSellingForced());
    salesItem.setProductDiscountValue(salesInvoiceItem.getProductDiscountValue());
    salesItem.setProductDiscountPercentage(salesInvoiceItem.getProductDiscountPercentage());
    salesItem.setProductFreeQtyScheme(salesInvoiceItem.getProductFreeQtyScheme());
    salesItem.setProductDiscountActual(salesInvoiceItem.getProductDiscountActual());
    salesItem.setInvoiceDiscountActual(salesInvoiceItem.getInvoiceDiscountActual());
    salesItem.setSecToPriQuantityActual(salesInvoiceItem.getSecToPriQuantityActual());
    salesItem.setTerToSecQuantityActual(salesInvoiceItem.getTerToSecQuantityActual());
    salesItem.setQuantityOrFree(salesInvoiceItem.getQuantityOrFree());
    salesItem.setProductHash(salesInvoiceItem.getProductHash());
    salesItem.setValuePts(salesInvoiceItem.getValuePts());
    salesItem.setValuePtr(salesInvoiceItem.getValuePtr());
    salesItem.setActualSellingPriceDerived(salesInvoiceItem.getActualSellingPriceDerived());
    salesItem.setItemDeleted(salesInvoiceItem.getItemDeleted());
    salesItem.setAccountId(salesInvoiceItem.getAccountId());
    salesItem.setProduct(salesInvoiceItem.getProduct());
    salesItem.setIsTaxCodeModified(salesInvoiceItem.getIsTaxCodeModified());
    salesItem.setSortOrder(salesInvoiceItem.getSortOrder());
    salesItem.setServiceFreightValue(salesInvoiceItem.getServiceFreightValue());
    salesItem.setServiceFreightValueDerived(salesInvoiceItem.getServiceFreightValueDerived());
    salesItem.setKeralaFloodCessValue(salesInvoiceItem.getKeralaFloodCessValue());
    salesItem.setKeralaFloodCessValueDerived(salesInvoiceItem.getKeralaFloodCessValueDerived());
    return salesItem;
  }

  private static Double getProductDiscount(Double discountValue, Integer lineQty, Integer totalQuantity) {
    double perPeiceDiscount;
    if (discountValue != null && totalQuantity != null && lineQty != null) {
      perPeiceDiscount = (discountValue / totalQuantity);
      return (perPeiceDiscount * lineQty);
    }
    return null;
  }

  public static Double getSalesItemSaleValue(SalesInvoiceItem salesInvoiceDetails) {
    Double lineSaleValue = null;
    if (salesInvoiceDetails.getProductQty() != null && salesInvoiceDetails.getProductQty() > 0) {
      lineSaleValue = salesInvoiceDetails.getProductQty() * salesInvoiceDetails.getProdPieceSellingForced();
    }
    return lineSaleValue;
  }

  /**
   *
   * @param company
   * @param customer
   * @return
   */
  public static final int getSalesMode(Company company, Customer customer) {
    String companyStateCode;
    String customerStateCode;

    if (customer != null) {
      companyStateCode = company.getGstNo().substring(0, 2);
      if (!StringUtil.isEmpty(customer.getGstNo())) {
        customerStateCode = customer.getGstNo().substring(0, 2);
        if (companyStateCode.equals(customerStateCode)) {
          return INTRA_STATE_SALES;
        } else {
          return INTER_STATE_SALES;
        }
      } else {
        return INTRA_STATE_SALES;
      }
    }
    return 0;
  }

  /**
   * Method to add number of days to given date.
   *
   * @param date
   * @param days
   * @return
   */
  public static final Date addDaysToDate(Date date, int days) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DATE, days);
    return calendar.getTime();
  }

  public static void setSalesInvoiceAddress(SalesInvoice salesInvoice) {
    if (salesInvoice.getCompanyAddressId() != null) {
      CompanyAddress companyAddress = salesInvoice.getCompanyAddressId();
      salesInvoice.setCompanyCountryId(companyAddress.getCountryId());
      salesInvoice.setCompanyStateId(companyAddress.getStateId());
      salesInvoice.setCompanyDistrictId(companyAddress.getDistrictId());
      salesInvoice.setCompanyTerritoryId(companyAddress.getTerritoryId());
      salesInvoice.setCompanyAddress(companyAddress.getAddress());
      salesInvoice.setCompanyPin(companyAddress.getPin());
      salesInvoice.setCompanyPhone1(companyAddress.getPhone1());
      salesInvoice.setCompanyPhone2(companyAddress.getPhone2());
      salesInvoice.setCompanyFax1(companyAddress.getFax1());
      salesInvoice.setCompanyEmail(companyAddress.getEmail());
    }

    if (salesInvoice.getCustomerAddressId() != null) {
      CustomerAddress customerAddress = salesInvoice.getCustomerAddressId();
      salesInvoice.setCustomerCountryId(customerAddress.getCountryId());
      salesInvoice.setCustomerStateId(customerAddress.getStateId());
      salesInvoice.setCustomerDistrictId(customerAddress.getDistrictId());
      salesInvoice.setCustomerTerritoryId(customerAddress.getTerritoryId());
      salesInvoice.setCustomerAddress(customerAddress.getAddress());
      salesInvoice.setCustomerPin(customerAddress.getPin());
      salesInvoice.setCustomerPhone1(customerAddress.getPhone1());
      salesInvoice.setCustomerPhone2(customerAddress.getPhone2());
      salesInvoice.setCustomerFax1(customerAddress.getFax1());
      salesInvoice.setCustomerEmail(customerAddress.getEmail());
    }
  }
}
