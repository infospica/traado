/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.util;

import java.util.Date;
import spica.constant.AccountingConstant;
import spica.fin.domain.TaxCode;
import spica.scm.common.ProductDetailSales;
import spica.scm.domain.Account;
import spica.scm.domain.ProductDetail;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductFreeQtyScheme;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.domain.StockPreSale;
import spica.scm.domain.StockSaleable;
import spica.scm.domain.StockStatus;
import spica.scm.service.StockStatusService;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;

/**
 *
 * @author java-2
 */
public class SalesInvoiceItemUtil {

  public static final Integer QUANTITY_SCHEME = 1;
  public static final Integer FREE_SCHEME = 2;

  public static final ProductFreeQtyScheme getProductFreeQtyScheme(Integer id) {
    ProductFreeQtyScheme productFreeQtyScheme = new ProductFreeQtyScheme();
    productFreeQtyScheme.setId(id);
    return productFreeQtyScheme;
  }

  /**
   *
   * @param productDetailSales
   * @return
   */
  public static final SalesInvoiceItem getSalesInvoiceItem(ProductDetailSales productDetailSales) {
    SalesInvoiceItem salesInvoiceItem = new SalesInvoiceItem();
    double invoiceDisc = productDetailSales.getInvoiceDiscountValue() == null ? 0 : productDetailSales.getInvoiceDiscountValue();
//    double lineDisc = productDetailSales.getProductDiscountValue() == null ? 0 : productDetailSales.getProductDiscountValue();
    double pts = productDetailSales.getPricelistPts() == null ? 0 : productDetailSales.getPricelistPts();
    salesInvoiceItem.setInvoiceDiscountActual(MathUtil.roundOff(invoiceDisc, 2));
    //  salesInvoiceItem.setProductDiscountActual(MathUtil.roundOff(lineDisc, 2));
    salesInvoiceItem.setValueProdPieceSelling(pts);
    salesInvoiceItem.setSecToPriQuantityActual(productDetailSales.getSecToPriQuantity());
    salesInvoiceItem.setTerToSecQuantityActual(productDetailSales.getTerToSecQuantity());
    salesInvoiceItem.setProductHash(productDetailSales.getProductHash());
    // Sets product selling price
    salesInvoiceItem.setValueProdPieceSelling(productDetailSales.getPricelistPts());
    salesInvoiceItem.setProdPieceSellingForced(productDetailSales.getPricelistPts());
    salesInvoiceItem.setValuePts(productDetailSales.getPricelistPts());
    salesInvoiceItem.setValuePtr(productDetailSales.getPricelistPtr());

    return salesInvoiceItem;
  }

  /**
   *
   * @param salesInvoiceItemId
   * @return
   */
  public static final SalesInvoiceItem getSalesInvoiceItem(SalesInvoiceItem salesInvoiceItemId) {
    SalesInvoiceItem salesInvoiceItem = new SalesInvoiceItem();

    salesInvoiceItem.setBatchNo(salesInvoiceItemId.getBatchNo());
    salesInvoiceItem.setExpiryDate(salesInvoiceItemId.getExpiryDate());
    salesInvoiceItem.setSalesInvoiceId(salesInvoiceItemId.getSalesInvoiceId());
    salesInvoiceItem.setProductDetailId(salesInvoiceItemId.getProductDetailId());

    salesInvoiceItem.setValueProdPieceSelling(salesInvoiceItemId.getValueProdPieceSelling());
    salesInvoiceItem.setValueProdPieceSold(salesInvoiceItemId.getValueProdPieceSold());
    salesInvoiceItem.setProdPieceSellingForced(salesInvoiceItemId.getProdPieceSellingForced());
    salesInvoiceItem.setValuePts(salesInvoiceItemId.getValuePts());
    salesInvoiceItem.setValuePtr(salesInvoiceItemId.getValuePtr());
    salesInvoiceItem.setValueMrp(salesInvoiceItemId.getValueMrp());
    salesInvoiceItem.setActualSellingPriceDerived(salesInvoiceItemId.getActualSellingPriceDerived());
    salesInvoiceItem.setHsnCode(salesInvoiceItemId.getHsnCode());

    salesInvoiceItem.setSchemeDiscountActual(salesInvoiceItemId.getSchemeDiscountActual());
    salesInvoiceItem.setSchemeDiscountValue(salesInvoiceItemId.getSchemeDiscountValue());
    salesInvoiceItem.setSchemeDiscountPercentage(salesInvoiceItemId.getSchemeDiscountPercentage());
    salesInvoiceItem.setIsSchemeDiscountInPercentage(salesInvoiceItemId.getIsSchemeDiscountInPercentage());

    salesInvoiceItem.setProductDiscountActual(salesInvoiceItemId.getProductDiscountActual());
    salesInvoiceItem.setProductDiscountValue(salesInvoiceItemId.getProductDiscountValue());
    salesInvoiceItem.setProductDiscountPercentage(salesInvoiceItemId.getProductDiscountPercentage());
    salesInvoiceItem.setIsProductDiscountInPercentage(salesInvoiceItemId.getIsProductDiscountInPercentage());

    salesInvoiceItem.setInvoiceDiscountActual(salesInvoiceItemId.getInvoiceDiscountActual());
    salesInvoiceItem.setInvoiceDiscountDerived(salesInvoiceItemId.getInvoiceDiscountDerived());
    salesInvoiceItem.setInvoiceDiscountValue(salesInvoiceItemId.getInvoiceDiscountValue());

    salesInvoiceItem.setCashDiscountValueDerived(salesInvoiceItemId.getCashDiscountValueDerived());
    salesInvoiceItem.setCashDiscountValue(salesInvoiceItemId.getCashDiscountValue());

    salesInvoiceItem.setSecToPriQuantityActual(salesInvoiceItemId.getSecToPriQuantityActual());
    salesInvoiceItem.setTerToSecQuantityActual(salesInvoiceItemId.getTerToSecQuantityActual());
    salesInvoiceItem.setQuantityOrFree(salesInvoiceItemId.getQuantityOrFree());
    salesInvoiceItem.setProductHash(salesInvoiceItemId.getProductHash());

    //salesInvoiceItem.setAccountId(salesInvoiceItemId.getProductDetailId().getAccountId());
    salesInvoiceItem.setTaxCodeId(salesInvoiceItemId.getTaxCodeId());
    salesInvoiceItem.setValueGoods(salesInvoiceItemId.getValueGoods());
    salesInvoiceItem.setValueSale(salesInvoiceItemId.getValueSale());
    salesInvoiceItem.setValueTaxable(salesInvoiceItemId.getValueTaxable());
    salesInvoiceItem.setValueVat(salesInvoiceItemId.getValueVat());
    salesInvoiceItem.setValueCgst(salesInvoiceItemId.getValueCgst());
    salesInvoiceItem.setValueSgst(salesInvoiceItemId.getValueSgst());
    salesInvoiceItem.setValueIgst(salesInvoiceItemId.getValueIgst());
    salesInvoiceItem.setProductDiscountDerived(salesInvoiceItemId.getProductDiscountDerived());
    salesInvoiceItem.setSchemeDiscountDerived(salesInvoiceItemId.getSchemeDiscountDerived());
    salesInvoiceItem.setProductEntryDetailId(salesInvoiceItemId.getProductEntryDetailId());

    salesInvoiceItem.setProformaItemStatus(salesInvoiceItemId.getProformaItemStatus());
    salesInvoiceItem.setSalesOrderItemId(salesInvoiceItemId.getSalesOrderItemId());
    salesInvoiceItem.setIsTaxCodeModified(salesInvoiceItemId.getIsTaxCodeModified());
    salesInvoiceItem.setSortOrder(salesInvoiceItemId.getSortOrder());
    salesInvoiceItem.setServiceFreightValue(salesInvoiceItemId.getServiceFreightValue());
    salesInvoiceItem.setServiceFreightValueDerived(salesInvoiceItemId.getServiceFreightValueDerived());
    salesInvoiceItem.setService2Value(salesInvoiceItemId.getService2Value());
    salesInvoiceItem.setService2ValueDerived(salesInvoiceItemId.getService2ValueDerived());
    salesInvoiceItem.setKeralaFloodCessValue(salesInvoiceItemId.getKeralaFloodCessValue());
    salesInvoiceItem.setKeralaFloodCessValueDerived(salesInvoiceItemId.getKeralaFloodCessValueDerived());
    salesInvoiceItem.setProductQty(salesInvoiceItemId.getProductQty());
    salesInvoiceItem.setMarginValueDeviationDer(salesInvoiceItemId.getMarginValueDeviationDer());
    salesInvoiceItem.setTcsValue(salesInvoiceItemId.getTcsValue());
    salesInvoiceItem.setTcsValueDerived(salesInvoiceItemId.getTcsValueDerived());
    return salesInvoiceItem;
  }

  /**
   *
   * @param salesInvoiceItem
   * @param productDetailSales
   * @param stockStatusId
   * @param stockType
   * @param stockId
   * @return
   */
  public static final StockPreSale getStockPreSale(SalesInvoice salesInvoice, SalesInvoiceItem salesInvoiceItem, ProductDetailSales productDetailSales, Integer stockId, Integer stockStatusId, Integer stockType) {
    StockPreSale stockPreSale = new StockPreSale();
    stockPreSale.setProductDetailId(salesInvoiceItem.getProductDetailId());
    Account account = new Account();
    account.setId(productDetailSales.getAccountId());
    stockPreSale.setAccountId(account);
    StockStatus stockStatus = new StockStatus();
    stockStatus.setId(stockStatusId);
    stockPreSale.setStockStatusId(stockStatus);
    StockSaleable stockSaleable = new StockSaleable();
    stockSaleable.setId(stockId);
    stockPreSale.setStockSaleableId(stockSaleable);
    ProductEntryDetail productEntryDetail = new ProductEntryDetail();
    productEntryDetail.setId(productDetailSales.getProductEntryDetailId());
    stockPreSale.setProductEntryDetailId(productEntryDetail);
    stockPreSale.setSalesOrderItemId(salesInvoiceItem.getSalesOrderItemId());
    stockPreSale.setBlockedAt(new Date());
    stockPreSale.setStockType(stockType);
    stockPreSale.setQuantityOut(salesInvoiceItem.getProductQty());
    stockPreSale.setSalesInvoiceId(salesInvoice);
    stockPreSale.setSalesInvoiceItemId(salesInvoiceItem);
    return stockPreSale;
  }

  public static final StockPreSale getStockPreSale(SalesInvoice salesInvoice, StockSaleable stockSaleable) {
    StockPreSale stockPreSale = new StockPreSale();
    stockPreSale.setProductDetailId(stockSaleable.getProductDetailId());
    stockPreSale.setAccountId(stockSaleable.getAccountId());
    stockPreSale.setStockStatusId(stockSaleable.getStockStatusId());
    stockPreSale.setStockSaleableId(stockSaleable.getStockSaleableId());
    stockPreSale.setProductEntryDetailId(stockSaleable.getRefProductEntryDetailId());
    stockPreSale.setBlockedAt(new Date());
    stockPreSale.setStockType(stockSaleable.getStockType());
    stockPreSale.setQuantityOut(stockSaleable.getQuantityOut().intValue());
    stockPreSale.setSalesInvoiceId(salesInvoice);
    stockPreSale.setSalesInvoiceItemId(stockSaleable.getSalesInvoiceItemId());
    return stockPreSale;
  }

  public static StockPreSale getStockPreSaleConvertInOut(StockPreSale stockPreSale, int freeInOrOut, int stockType) {
    StockPreSale stock = new StockPreSale();
    stock.setProductDetailId(stockPreSale.getProductDetailId());
    stock.setAccountId(stockPreSale.getAccountId());
    stock.setStockStatusId(stockPreSale.getStockStatusId());
    stock.setStockSaleableId(stockPreSale.getStockSaleableId());
    stock.setProductEntryDetailId(stockPreSale.getProductEntryDetailId());
    stock.setBlockedAt(new Date());
    stock.setStockType(stockType);
    stockPreSale.setSalesInvoiceId(stockPreSale.getSalesInvoiceId());
    stockPreSale.setSalesInvoiceItemId(stockPreSale.getSalesInvoiceItemId());
    if (freeInOrOut == SystemConstants.STOCK_SALEABLE_AS_FREE_IN) {
      stock.setQuantityIn(stockPreSale.getQuantityOut());
    } else if (freeInOrOut == SystemConstants.STOCK_SALEABLE_AS_FREE_OUT) {
      stock.setQuantityOut(stockPreSale.getQuantityOut());
    }
    return stock;
  }

  public static final SalesInvoiceItem mergeSalesOrderItemFreeQty(SalesInvoiceItem salesInvoiceItem, Integer productFreeQty, ProductDetailSales productDetailSales) {
    if (salesInvoiceItem == null) {
      salesInvoiceItem = getSalesInvoiceItem(productDetailSales);;
    }
    if (productFreeQty != null && productFreeQty > 0) {
      if (productDetailSales.getQuantityFreeAvailable() != null && productFreeQty <= productDetailSales.getQuantityFreeAvailable()) {
        salesInvoiceItem.setProductQtyFree(productFreeQty);
        productDetailSales.setQuantityFreeAvailable(0);
      } else if (productDetailSales.getQuantityFreeAvailable() != null && productFreeQty > productDetailSales.getQuantityFreeAvailable()) {
        salesInvoiceItem.setProductQtyFree(productDetailSales.getQuantityFreeAvailable());
        productDetailSales.setQuantityFreeAvailable(productFreeQty - productDetailSales.getQuantityFreeAvailable());
      }
    }
    return salesInvoiceItem;
  }

  public static final StockPreSale getSaleableToFree(Integer productFreeQty, SalesInvoice salesInvoice, SalesInvoiceItem salesInvoiceItem, ProductDetailSales productDetailSales, Integer stockId, Integer stockType) {
    if (salesInvoiceItem == null) {
      salesInvoiceItem = getSalesInvoiceItem(productDetailSales);
    }
    StockPreSale stockPreSale = null;
    if (productDetailSales.getQuantityAvailable() != null && productFreeQty <= productDetailSales.getQuantityAvailable()) {
      stockPreSale = getStockPreSale(salesInvoice, salesInvoiceItem, productDetailSales, stockId, 1, stockType);
      stockPreSale.setQuantityOut(productFreeQty);
      productDetailSales.setQuantityAvailable(productDetailSales.getQuantityAvailable() - productFreeQty);
      stockPreSale.setSalesInvoiceId(salesInvoice);
      stockPreSale.setSalesInvoiceItemId(salesInvoiceItem);
    } else if (productDetailSales.getQuantityAvailable() != null && productFreeQty > productDetailSales.getQuantityAvailable()) {
      stockPreSale = getStockPreSale(salesInvoice, salesInvoiceItem, productDetailSales, stockId, 1, stockType);
      stockPreSale.setQuantityOut(productDetailSales.getQuantityAvailable());
      stockPreSale.setSalesInvoiceId(salesInvoice);
      productDetailSales.setQuantityAvailable(0);
    }
    return stockPreSale;
  }

  public static final StockPreSale getFreeToSaleable(Integer productQty, SalesInvoice salesInvoice, SalesInvoiceItem salesInvoiceItem, ProductDetailSales productDetailSales, Integer stockId, Integer stockType) {
    StockPreSale stockPreSale = null;
    if (salesInvoiceItem == null) {
      salesInvoiceItem = getSalesInvoiceItem(productDetailSales);
    }
    if (productDetailSales.getQuantityFreeAvailable() != null && productQty <= productDetailSales.getQuantityFreeAvailable()) {
      stockPreSale = getStockPreSale(salesInvoice, salesInvoiceItem, productDetailSales, stockId, StockStatusService.STOCK_STATUS_SALEABLE, stockType);
      stockPreSale.setQuantityOut(productQty);
      productDetailSales.setQuantityFreeAvailable(productDetailSales.getQuantityFreeAvailable() - productQty);
      stockPreSale.setSalesInvoiceId(salesInvoice);
      stockPreSale.setSalesInvoiceItemId(salesInvoiceItem);
    } else if (productDetailSales.getQuantityFreeAvailable() != null && productQty > productDetailSales.getQuantityFreeAvailable()) {
      stockPreSale = getStockPreSale(salesInvoice, salesInvoiceItem, productDetailSales, stockId, StockStatusService.STOCK_STATUS_SALEABLE, stockType);
      stockPreSale.setQuantityOut(productDetailSales.getQuantityFreeAvailable());
      productDetailSales.setQuantityFreeAvailable(0);
      stockPreSale.setSalesInvoiceId(salesInvoice);
      stockPreSale.setSalesInvoiceItemId(salesInvoiceItem);
    }
    return stockPreSale;
  }

  /**
   *
   * @param salesOrderItem
   * @param productDetailSales
   * @return
   */
  public static final SalesInvoiceItem mergeSalesOrderItemToSalesInvoiceItem(Integer productQty, ProductDetailSales productDetailSales) {

    SalesInvoiceItem salesInvoiceItem = getSalesInvoiceItem(productDetailSales);

    if (productQty > 0) {
      if (productDetailSales.getQuantityAvailable() != null && productQty <= productDetailSales.getQuantityAvailable()) {
        salesInvoiceItem.setProductQty(productQty);
        salesInvoiceItem.setProductDetailId(new ProductDetail(productDetailSales.getProductDetailId()));
        productDetailSales.setQuantityAvailable(productDetailSales.getQuantityAvailable() - productQty);
      } else if (productDetailSales.getQuantityAvailable() != null && productQty > productDetailSales.getQuantityAvailable()) {
        salesInvoiceItem.setProductQty(productDetailSales.getQuantityAvailable());
        salesInvoiceItem.setProductDetailId(new ProductDetail(productDetailSales.getProductDetailId()));
        productDetailSales.setQuantityAvailable(0);
      }
    }
    return salesInvoiceItem;
  }

  /**
   *
   * @param main
   * @param productDetailId
   * @return
   */
//  public static ProductDetail getProductDetail(Integer productDetailId) {
//    ProductDetail productDetail = new ProductDetail();
//    productDetail.setId(productDetailId);
//    return productDetail;
//  }
  /**
   *
   * @param salesInvoiceItem
   */
  public static void calculateSalesInvoiceItemValues(SalesInvoiceItem salesInvoiceItem) {
    boolean schemeApplicable = isFreeScheme(salesInvoiceItem.getQuantityOrFree());
    boolean cashDiscountTaxable = (salesInvoiceItem.getSalesInvoiceId().getCashDiscountTaxable() != null && salesInvoiceItem.getSalesInvoiceId().getCashDiscountTaxable().equals(SystemRuntimeConfig.CASH_DISCOUNT_TAXABLE));
    Integer soldQty = 0;
    Integer productQty = salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty();
    Integer freeQty = salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree();
    if (schemeApplicable) {
      soldQty = productQty;
    } else {
      soldQty = productQty + freeQty;
    }

    double schemeDiscDerived = salesInvoiceItem.getSchemeDiscountDerived() == null ? 0 : salesInvoiceItem.getSchemeDiscountDerived();
    double productDiscDerived = salesInvoiceItem.getProductDiscountDerived() == null ? 0 : salesInvoiceItem.getProductDiscountDerived();
    double invoiceDiscDerived = salesInvoiceItem.getInvoiceDiscountDerived() == null ? 0 : salesInvoiceItem.getInvoiceDiscountDerived();
    double cashDiscDerived = salesInvoiceItem.getCashDiscountValueDerived() == null ? 0 : salesInvoiceItem.getCashDiscountValueDerived();
    double service1valueDerived = salesInvoiceItem.getServiceFreightValueDerived() == null ? 0 : salesInvoiceItem.getServiceFreightValueDerived();
    double service2valueDerived = salesInvoiceItem.getService2ValueDerived() == null ? 0 : salesInvoiceItem.getService2ValueDerived();
    double kfCessvalueDerived = salesInvoiceItem.getKeralaFloodCessValueDerived() == null ? 0 : salesInvoiceItem.getKeralaFloodCessValueDerived();
    double tcsvalueDerived = salesInvoiceItem.getTcsValueDerived() == null ? 0 : salesInvoiceItem.getTcsValueDerived();

    double productDiscoutValue = productDiscDerived * soldQty;
    double invoiceDiscoutValue = invoiceDiscDerived * soldQty;
    double cashDiscoutValue = cashDiscDerived * soldQty;
    double schemeDiscountValue = schemeDiscDerived * soldQty;
    double goodValue = salesInvoiceItem.getProdPieceSellingForced() * soldQty;
    double service1value = service1valueDerived * soldQty;
    double service2value = service2valueDerived * soldQty;
    double kfCessvalue = kfCessvalueDerived * soldQty;
    double tcsvalue = tcsvalueDerived * soldQty;

    double salesValue = goodValue - schemeDiscountValue - productDiscoutValue;
    double taxableValue = salesValue - invoiceDiscoutValue;
    if (cashDiscountTaxable) {
      taxableValue -= cashDiscoutValue;
    }

    salesInvoiceItem.setSchemeDiscountValue(schemeDiscountValue == 0 ? null : schemeDiscountValue);
    salesInvoiceItem.setCashDiscountValue(cashDiscoutValue == 0 ? null : cashDiscoutValue);
    salesInvoiceItem.setProductDiscountValue(productDiscoutValue == 0 ? null : productDiscoutValue);
    salesInvoiceItem.setInvoiceDiscountValue(invoiceDiscoutValue == 0 ? null : invoiceDiscoutValue);
    salesInvoiceItem.setServiceFreightValue(service1value == 0 ? null : service1value);
    salesInvoiceItem.setService2Value(service2value == 0 ? null : service2value);
    salesInvoiceItem.setKeralaFloodCessValue(kfCessvalue == 0 ? null : kfCessvalue);
    salesInvoiceItem.setTcsValue(tcsvalue == 0 ? null : tcsvalue);

    salesInvoiceItem.setValueGoods(goodValue);
    salesInvoiceItem.setValueSale(salesValue);
    salesInvoiceItem.setValueTaxable(taxableValue);
    salesInvoiceItem.setValueIgst(taxableValue * salesInvoiceItem.getTaxCodeId().getRatePercentage() / 100);
    salesInvoiceItem.setRateDiffPerPiece(ProductUtil.getRateDifference(salesInvoiceItem.getProductEntryDetailId().getMarginPercentage(),
            (salesInvoiceItem.getValueProdPieceSold() - salesInvoiceItem.getActualSellingPriceDerived()), salesInvoiceItem.getProductEntryDetailId().getPtsSsRateDerivationCriterion()));
    if (salesInvoiceItem.getKeralaFloodCessValueDerived() != null) {
      salesInvoiceItem.setKeralaFloodCessValue(salesInvoiceItem.getKeralaFloodCessValueDerived() * soldQty);
    }
    if (salesInvoiceItem.getServiceFreightValueDerived() != null) {
      salesInvoiceItem.setServiceFreightValue(salesInvoiceItem.getServiceFreightValueDerived() * soldQty);
    }
    if (salesInvoiceItem.getService2ValueDerived() != null) {
      salesInvoiceItem.setService2Value(salesInvoiceItem.getService2ValueDerived() * soldQty);
    }
    for (TaxCode taxCode : salesInvoiceItem.getTaxCodeId().getTaxCodeList()) {
      if (AccountingConstant.TAX_TYPE_CGST == taxCode.getTaxType()) {
        salesInvoiceItem.setValueCgst(taxableValue * taxCode.getRatePercentage() / 100);
      } else if (AccountingConstant.TAX_TYPE_SGST == taxCode.getTaxType()) {
        salesInvoiceItem.setValueSgst(taxableValue * taxCode.getRatePercentage() / 100);
      }
    }
  }

  public static boolean isFreeScheme(Integer schemeType) {
    return (FREE_SCHEME.equals(schemeType));
  }

  /**
   *
   * @param salesInvoiceItem
   * @param fieldType
   */
  public static void setCurrentAvailableQty(SalesInvoiceItem salesInvoiceItem, int fieldType) {
    int availableQty = 0;
    Integer productFreeQtyAvailable;
    Integer productQtyAvailable;
    Integer requestedProductFreeQty;
    Integer requestedProductQty;
    Integer possibleProductFreeQty;
    Integer possibleProductQty;
    try {
      if (salesInvoiceItem != null && salesInvoiceItem.getProductDetailSales() != null) {
//        if (fieldType == 0) {
//          if (salesInvoiceItem.getProductQty() == null) {
//            salesInvoiceItem.setProductQty(salesInvoiceItem.getProductQtyFree() == null ? 1 : salesInvoiceItem.getProductQtyFree() == 0 ? 1 : 0);
//          }
//          if (salesInvoiceItem.getProductQtyFree() == null) {
//            salesInvoiceItem.setProductQtyFree(salesInvoiceItem.getProductQtyFree() == null ? (salesInvoiceItem.getProductQty()==0?1:0) : salesInvoiceItem.getProductQtyFree());
//          }
//        }
        productQtyAvailable = salesInvoiceItem.getProductDetailSales().getQuantityAvailable() == null ? 0 : salesInvoiceItem.getProductDetailSales().getQuantityAvailable();
        productFreeQtyAvailable = salesInvoiceItem.getProductDetailSales().getQuantityFreeAvailable() == null ? 0 : salesInvoiceItem.getProductDetailSales().getQuantityFreeAvailable();
        availableQty = (productQtyAvailable + productFreeQtyAvailable);
        requestedProductQty = salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty();
        requestedProductFreeQty = salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree();
        salesInvoiceItem.setSecondaryQuantity(salesInvoiceItem.getProductQty() == null ? null : salesInvoiceItem.getSecondaryQuantity());
        switch (fieldType) {
          case SalesInvoiceUtil.PRODUCT_PRIMARY_QTY:
            possibleProductFreeQty = availableQty - requestedProductQty;
            if (possibleProductFreeQty == 0) {
              salesInvoiceItem.setProductQtyFree(possibleProductFreeQty);
              if (salesInvoiceItem.getProductQtyFree() != null && salesInvoiceItem.getProductQtyFree() > 0) {
                salesInvoiceItem.setSchemeDiscountValue(null);
                salesInvoiceItem.setSchemeDiscountPercentage(null);
              }
            }
            salesInvoiceItem.setCurrentAvailableFreeQty(possibleProductFreeQty);
            break;
          case SalesInvoiceUtil.PRODUCT_FREE_QTY:
            possibleProductQty = availableQty - requestedProductFreeQty;
            if (possibleProductQty == 0) {
              salesInvoiceItem.setProductQty(possibleProductQty);
            }
            salesInvoiceItem.setCurrentAvailableQty(possibleProductQty);
            break;
          default:
            salesInvoiceItem.setCurrentAvailableFreeQty(availableQty);
            salesInvoiceItem.setCurrentAvailableQty(availableQty);
            break;
        }
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

}
