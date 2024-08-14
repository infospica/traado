/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.util;

import java.util.logging.Logger;
import spica.scm.common.ProductPackingUnits;
import spica.scm.domain.Account;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductPriceListVo;
import spica.scm.domain.SalesReturnItem;
import spica.scm.service.AccountService;
import spica.scm.view.AccountUtil;
import spica.sys.SystemConstants;

/**
 *
 * @author java-2
 */
public abstract class ProductUtil {

  private final static Logger LOGGER = Logger.getLogger(ProductUtil.class.getName());

  /**
   * Method to generate the product packing details.
   *
   * @return
   */
  public static final String getProductPackingInfo(ProductPackingUnits productPacking) {
    StringBuilder packing = new StringBuilder();
    if (productPacking != null) {
      if (productPacking.getPackTertiary() != null) {
        if (productPacking.getPackTertiarySecondaryQty() != null) {
          if (productPacking.getPackSecondary() != null && productPacking.getPackPrimary() != null && productPacking.getPackSecondaryPrimaryQty() != null && productPacking.getPackTertiarySecondaryQty() != null) {
            packing.append("1 ").append(productPacking.getPackTertiary().getTitle()).append(" = ").append(productPacking.getPackTertiarySecondaryQty()).append(" ")
                    .append(productPacking.getPackSecondary().getTitle())
                    .append(" = ").append((productPacking.getPackTertiarySecondaryQty() * productPacking.getPackSecondaryPrimaryQty())).append(" ").append(productPacking.getPackPrimary().getTitle());
          }
        }
      } else if (productPacking.getPackSecondary() != null) {
        if (productPacking.getPackSecondaryPrimaryQty() != null) {
          if (productPacking.getPackPrimary() != null) {
            packing.append("1 ").append(productPacking.getPackSecondary().getTitle()).append(" = ").append(productPacking.getPackSecondaryPrimaryQty()).append(" ")
                    .append(productPacking.getPackPrimary().getTitle());
          }
        }
      } else if (productPacking.getPackPrimary() != null) {
        packing.append(productPacking.getPackPrimary().getTitle());
      }
    }
    return packing.toString();
  }

  /**
   *
   * @param packDimension
   * @return
   */
  public static final String getPackDimensionTitle(Integer packDimension) {
    String title = "";
    if (packDimension != null) {
      if (packDimension == 1) {
        title = "1 (x)";
      } else if (packDimension == 2) {
        title = "2 (S.T)";
      } else if (packDimension == 3) {
        title = "3 (S.S.T)";
      }
    }
    return title;
  }

  /**
   *
   * @param productEntryDetail
   * @return
   */
  public static final Double getProductLandingPrice(ProductEntryDetail productEntryDetail) { //Require Modification
    double schemeDiscount = 0.0;
    double productDiscount = 0.0;
    double invoiceDiscount = 0.0;
    double serviceRate = 0.0;
    if (productEntryDetail.getValueRate() != null) {
      double landingPrice = productEntryDetail.getValueRate();
      if ((productEntryDetail.getSchemeDiscountReplacement() == null) || (productEntryDetail.getSchemeDiscountReplacement() != null && SystemConstants.DISCOUNT_NON_REPLACEMENT.equals(productEntryDetail.getSchemeDiscountReplacement()))) {
        schemeDiscount = productEntryDetail.getSchemeDiscountValueDerived() == null ? 0 : productEntryDetail.getSchemeDiscountValueDerived();
      }
      productDiscount = productEntryDetail.getProductDiscountValueDerived() == null ? 0 : productEntryDetail.getProductDiscountValueDerived();
      if (productEntryDetail.getValueServiceRateDerived() != null && productEntryDetail.getValueServiceRateDerived() > 0) {
        serviceRate = productEntryDetail.getValueServiceRateDerived();
      }
      invoiceDiscount = productEntryDetail.getInvoiceDiscountValueDerived() == null ? 0 : productEntryDetail.getInvoiceDiscountValueDerived();
      return landingPrice - schemeDiscount - productDiscount - invoiceDiscount + serviceRate;
    }
    return null;
  }

  public static final Double getProductCompanyLandingRate(ProductEntryDetail productEntryDetail) { //Require Modification
    double schemeDiscount = 0.0;
    double productDiscount = 0.0;
    double invoiceDiscount = 0.0;
    double cashDiscount = 0.0;
    if (productEntryDetail.getValueRate() != null) {
      double landingPrice = productEntryDetail.getValueRate();
      schemeDiscount = productEntryDetail.getSchemeDiscountValueDerived() == null ? 0 : productEntryDetail.getSchemeDiscountValueDerived();
      productDiscount = productEntryDetail.getProductDiscountValueDerived() == null ? 0 : productEntryDetail.getProductDiscountValueDerived();
      invoiceDiscount = productEntryDetail.getInvoiceDiscountValueDerived() == null ? 0 : productEntryDetail.getInvoiceDiscountValueDerived();
      cashDiscount = productEntryDetail.getCashDiscountValueDerived() == null ? 0 : productEntryDetail.getCashDiscountValueDerived();
      return landingPrice - schemeDiscount - productDiscount - invoiceDiscount - cashDiscount;
    }
    return null;
  }

  /**
   *
   * @param productEntryDetail
   * @return
   */
  public static final Double getActualSellingPriceDerived(ProductEntryDetail productEntryDetail) {
    if (productEntryDetail.getValuePts() != null) {
      double actualSellingPrice = productEntryDetail.getValuePts();

      if (productEntryDetail.getSchemeDiscountValueDerived() != null && productEntryDetail.getSchemeDiscountValueDerived() > 0) {
        if (productEntryDetail.getIsSchemeDiscountToCustomer() != null && productEntryDetail.getIsSchemeDiscountToCustomer().equals(SystemConstants.DISCOUNT_FOR_CUSTOMER)) {
          actualSellingPrice -= (actualSellingPrice * (productEntryDetail.getSchemeDiscountPercentage() / 100));
        }
      }

      if (productEntryDetail.getProductDiscountValueDerived() != null && productEntryDetail.getProductDiscountValueDerived() > 0) {
        if (productEntryDetail.getIsLineDiscountToCustomer() != null && productEntryDetail.getIsLineDiscountToCustomer().equals(SystemConstants.DISCOUNT_FOR_CUSTOMER)) {
          actualSellingPrice -= (actualSellingPrice * (productEntryDetail.getDiscountPercentage() / 100));
        }
      }
      return MathUtil.roundOff(actualSellingPrice, 2);
    }
    return null;
  }

  /**
   *
   * @param rateCriterion
   * @param ptsValue
   * @param landingPrice
   * @return
   */
  public static final Double getSsMargin(Integer rateCriterion, Double ptsValue, Double landingPrice) {
    Double ssMargin = 0.0;
    if (rateCriterion != null && rateCriterion == 1) {
      // Mark Up
      ssMargin = 100 * ((ptsValue / landingPrice) - 1);
    } else if (rateCriterion != null && rateCriterion == 2) {
      // Mark Down
      ssMargin = 100 * (1 - (landingPrice / ptsValue));
    }
    return ssMargin;
  }

  /**
   *
   * @param rateCriterion
   * @param ptsValue
   * @param marginPercentage
   * @return
   */
  public static final Double getExpectedLandingRate(Integer rateCriterion, Double ptsValue, Double marginPercentage, Account account) {
    Double expectedLandingRate = 0.0;
    if (AccountUtil.isSuperStockiest(account) || account.getCompanyTradeProfileId() == null) {
      if (rateCriterion != null && ptsValue != null) {
        expectedLandingRate = getMarkUpMarkDownValue(ptsValue, marginPercentage, rateCriterion);
      }
    } else {
      expectedLandingRate = ptsValue;
    }
    return expectedLandingRate;
  }

  /**
   *
   * @param rateCriterion
   * @param expectedLandingRate
   * @param vendorMarginPercentage
   * @return
   */
  public static final Double getExpectedVendorLandingRate(Integer rateCriterion, Double expectedLandingRate, Double vendorMarginPercentage) {
    Double expectedVendorLandingRate = 0.0;
    if (rateCriterion != null) {
      expectedVendorLandingRate = getMarkUpMarkDownValue(expectedLandingRate, vendorMarginPercentage, rateCriterion);
    }
    return expectedVendorLandingRate;
  }

  /**
   *
   * @param netRate
   * @param taxRatePercentange
   * @return
   */
  public static final Double getTaxRate(Double netRate, Double taxRatePercentange) {
    Double taxRate = null;
    double rate = 0.0;
    if (netRate != null && taxRatePercentange != null) {
      rate = netRate * (100 / (100 + taxRatePercentange));
      taxRate = netRate - rate;
    }
    return taxRate;
  }

  /**
   *
   * @param mrpValue
   * @param taxRatePercentage
   * @return
   */
  public static final double getMrpLteValue(Double mrpValue, Double taxRatePercentage) {
    double mrpLteValue = 0;
    if (mrpValue != null && taxRatePercentage != null) {
      mrpLteValue = ((mrpValue * 100) / (100 + taxRatePercentage));
    }
    return mrpLteValue;
  }

  /**
   *
   * @param marginPercentage
   * @param mrpValue
   * @param rateDerivationCriteria
   * @return
   */
  public static final double getPtrValue(Double marginPercentage, Double mrpValue, Integer rateDerivationCriteria) {
    double ptrValue = 0;
    if (marginPercentage != null && mrpValue != null && rateDerivationCriteria != null) {
      ptrValue = getMarkUpMarkDownValue(mrpValue, marginPercentage, rateDerivationCriteria);
    }
    return ptrValue;
  }

  /**
   *
   * @param marginPercentage
   * @param ptrValue
   * @param rateDerivationCriteria
   * @return
   */
  public static final double getPtsValue(Double marginPercentage, Double ptrValue, Integer rateDerivationCriteria) {
    double ptsValue = 0;
    if (marginPercentage != null && ptrValue != null && rateDerivationCriteria != null) {
      ptsValue = getMarkUpMarkDownValue(ptrValue, marginPercentage, rateDerivationCriteria);
    }
    return ptsValue;
  }

  /**
   *
   * @param marginPercentage
   * @param ptsValue
   * @param rateDerivationCriteria
   * @return
   */
  public static final double getSsValue(Double marginPercentage, Double ptsValue, Integer rateDerivationCriteria) {
    double ssValue = 0;
    if (marginPercentage != null && ptsValue != null && rateDerivationCriteria != null) {
      ssValue = getMarkUpMarkDownValue(ptsValue, marginPercentage, rateDerivationCriteria);
    }
    return ssValue;
  }

  /**
   *
   * @param value
   * @param marginPercentage
   * @param rateDerivationCriteria
   * @return
   */
  private static final double getMarkUpMarkDownValue(double value, double marginPercentage, int rateDerivationCriteria) {
    double markUpMarkDownValue = 0.0;
    if (rateDerivationCriteria == SystemConstants.RATE_DERIVATION_MARK_UP) {
      markUpMarkDownValue = getProductMarkUpValue(marginPercentage, value);
    } else if (rateDerivationCriteria == SystemConstants.RATE_DERIVATION_MARK_DOWN) {
      markUpMarkDownValue = getProductMarkDownValue(marginPercentage, value);
    }
    return markUpMarkDownValue;
  }

  /**
   *
   * @param productEntry
   * @param productEntryDetail
   * @param rateDerivationCriteria
   * @return
   */
  public static final double calculateExpectedRate(ProductEntry productEntry, ProductEntryDetail productEntryDetail, Integer rateDerivationCriteria) {
    double vendorReservePercentage = productEntry.getVendorReservePercentage() == null ? 0 : productEntry.getVendorReservePercentage();
    double expectedRate = productEntryDetail.getValueSs();
    if (vendorReservePercentage > 0) {
      expectedRate = getMarkUpMarkDownValue(expectedRate, vendorReservePercentage, rateDerivationCriteria);
    }
    return expectedRate;
  }

  /**
   * Method to find the mark down value.
   *
   * @param marginPercentage
   * @param value
   * @return
   */
  public static final double getProductMarkDownValue(double marginPercentage, double value) {
    double mMarkDownValue = 0.0;
    if (marginPercentage != 0) {
      mMarkDownValue = (value * (100 - marginPercentage) / 100);
    } else {
      mMarkDownValue = value;
    }
    return mMarkDownValue;
  }

  /**
   * Method to find the markup value.
   *
   * @param marginPercentage
   * @param value
   * @return
   */
  public static final double getProductMarkUpValue(double marginPercentage, double value) {
    double markUpValue = 0.0;
    if (marginPercentage != 0) {
      markUpValue = value * (100 / (100 + (marginPercentage)));
    } else {
      markUpValue = value;
    }
    return markUpValue;
  }

  public static final double getExpectedPts(double marginPercatage, double value, int rateCriteria) {
    double expectedPts = 0.0;
    if (marginPercatage != 0) {
      if (rateCriteria == SystemConstants.RATE_DERIVATION_MARK_DOWN) {
        // Mark Down
        expectedPts = value / (1 - (marginPercatage / 100));
      } else {
        // Mark Up
        expectedPts = value * (1 + (marginPercatage / 100));
      }
    } else {
      expectedPts = value;
    }
    return expectedPts;
  }

  /**
   *
   * @param productEntry
   * @param productEntryDetail
   * @return
   */
  public static final void getPtsValueFromRate(ProductEntry productEntry, ProductEntryDetail productEntryDetail) {
    Double ptsValue = null;
    if (AccountService.DERIVE_PTS_FROM_LANDING_RATE.equals(productEntry.getAccountId().getPtsDerivationCriteria())) {
      if (productEntryDetail.getValueRatePerProdPieceDer() != null) {
        ptsValue = ProductUtil.getExpectedPts(productEntryDetail.getMarginPercentage(), productEntryDetail.getValueRatePerProdPieceDer(), productEntryDetail.getPtsSsRateDerivationCriterion());
        productEntryDetail.setValuePts(ptsValue);
      }
    } else if (AccountService.DERIVE_PTS_FROM_RATE.equals(productEntry.getAccountId().getPtsDerivationCriteria()) && productEntryDetail.getValuePts() == null && productEntryDetail.getValueRate() != null) {
      if (productEntry.getVendorReservePercentage() != null) {
        ptsValue = ProductUtil.getExpectedPts(productEntry.getVendorReservePercentage(), productEntryDetail.getValueRate(), productEntryDetail.getPtsSsRateDerivationCriterion());
        ptsValue = ProductUtil.getExpectedPts(productEntryDetail.getMarginPercentage(), ptsValue, productEntryDetail.getPtsSsRateDerivationCriterion());
        productEntryDetail.setValuePts(MathUtil.roundOff(ptsValue, 2));
        productEntryDetail.setValuePtsPerProdPiece(productEntryDetail.getValuePts());
      } else {
        if (productEntryDetail.getMarginPercentage() == null) {
          ptsValue = productEntryDetail.getValueRate();
        } else {
          ptsValue = ProductUtil.getExpectedPts(productEntryDetail.getMarginPercentage(), productEntryDetail.getValueRate(), productEntryDetail.getPtsSsRateDerivationCriterion());
        }
        productEntryDetail.setValuePts(MathUtil.roundOff(ptsValue, 2));
        productEntryDetail.setValuePtsPerProdPiece(productEntryDetail.getValuePts());
      }
    }
    //return ptsValue;
  }

  public static Double getValueServiceRateDerived(Double productNetValue, Double productsGoodsValue, Double serviceNetAmount, Double lineQty) {
    Double derivedValue = null;
    if (productsGoodsValue != null && productsGoodsValue > 0 && serviceNetAmount != null && lineQty != null && lineQty > 0) {
      derivedValue = (((productNetValue / productsGoodsValue) * serviceNetAmount) / lineQty);
    }
    return derivedValue;
  }

  public static final Double getValueRatePerProdPiece(ProductEntryDetail productEntryDetail) {
    double valueRatePerProdPiece = productEntryDetail.getLandingPricePerPieceCompany() != null ? productEntryDetail.getLandingPricePerPieceCompany() : 0.0;
    if (productEntryDetail.getLandingPricePerPieceCompany() != null) {
      if (productEntryDetail.getSchemeDiscountReplacement() != null && productEntryDetail.getSchemeDiscountReplacement() == SystemConstants.DISCOUNT_REPLACEMENT) {
//        valueRatePerProdPiece=(freeQty*rate/totalQty)+land rate
        valueRatePerProdPiece = (productEntryDetail.getProductQuantityFree() * productEntryDetail.getValueRate()
                / (productEntryDetail.getProductQuantity() + productEntryDetail.getProductQuantityFree())) + valueRatePerProdPiece;
      } else {
        valueRatePerProdPiece = productEntryDetail.getLandingPricePerPieceCompany();
      }
      if (productEntryDetail.getProductDiscountReplacement() != null && productEntryDetail.getProductDiscountReplacement() == SystemConstants.DISCOUNT_REPLACEMENT) {
        valueRatePerProdPiece = valueRatePerProdPiece + (productEntryDetail.getProductDiscountValueDerived() != null ? productEntryDetail.getProductDiscountValueDerived() : 0);
      }
    }
    return valueRatePerProdPiece;
  }

  public static final Double getActualPtsDerived(SalesReturnItem salesReturnItem) {
    if (salesReturnItem.getValueRate() != null) {
      double actualSellingPrice = salesReturnItem.getValueRate();

      if (salesReturnItem.getSchemeDiscountValueDerived() != null && salesReturnItem.getSchemeDiscountValueDerived() > 0) {
        actualSellingPrice -= (actualSellingPrice * (salesReturnItem.getSchemeDiscountPercentage() / 100));
      }

      if (salesReturnItem.getProductDiscountValueDerived() != null && salesReturnItem.getProductDiscountValueDerived() > 0) {
        actualSellingPrice -= (actualSellingPrice * (salesReturnItem.getProductDiscountPercentage() / 100));
      }
      return MathUtil.roundOff(actualSellingPrice, 2);
    }
    return null;
  }

  public static void productPriceListCalculation(ProductPriceListVo productPriceListVo, Account account) {

    Double valueNewPts = productPriceListVo.getValuePtsPerProdPieceSell();
    Double valueNewPtr = productPriceListVo.getValuePtrPerProdPiece();
    Double marginDeviationPerc = productPriceListVo.getMarginPercentageDeviation();
    Double valueRatePerPiece = productPriceListVo.getValueRatePerProdPieceDer();

    Double actualNewSellingPrice = valueNewPts;

    //For custom price list ignore all the discounts    
    if (productPriceListVo.getIsDefault() != null && productPriceListVo.getIsDefault().intValue() != 0) {
      if (productPriceListVo.getIsSchemeDiscountToCustomer() != null && productPriceListVo.getIsSchemeDiscountToCustomer().intValue() == 1) {
        actualNewSellingPrice = valueNewPts - (valueNewPts * (productPriceListVo.getSchemeDiscountPercentage() / 100.0));
      }

      if (productPriceListVo.getIsProductDiscountToCustomer() != null && productPriceListVo.getIsProductDiscountToCustomer().intValue() == 1) {
        actualNewSellingPrice = actualNewSellingPrice - (actualNewSellingPrice * (productPriceListVo.getProductDiscountPercentage() / 100.0));
      }
    }
    Double expectedNewLandPrice = actualNewSellingPrice;
    if ((account != null && AccountUtil.isSuperStockiest(account)) || account == null) {
      if (productPriceListVo.getMarginPercentage() != null && productPriceListVo.getMarginPercentage().intValue() != 0) {
        if (productPriceListVo.getPtsSsRateDerivationCriterion() != null && productPriceListVo.getPtsSsRateDerivationCriterion().intValue() == 1) {
          expectedNewLandPrice = actualNewSellingPrice / (1 + (productPriceListVo.getMarginPercentage() / 100.0));
        } else {
          expectedNewLandPrice = actualNewSellingPrice * (1 - (productPriceListVo.getMarginPercentage() / 100.0));
        }
      }
    }
    Double marginNewDeviationPerc = marginDeviationPerc;
    if (valueRatePerPiece > 0 && productPriceListVo.getMarginPercentage() != null) {
      if (productPriceListVo.getPtsSsRateDerivationCriterion() != null && productPriceListVo.getPtsSsRateDerivationCriterion().intValue() == 1) {
        marginNewDeviationPerc = (((actualNewSellingPrice / valueRatePerPiece) - 1) * 100.0) - productPriceListVo.getMarginPercentage();
      } else {
        marginNewDeviationPerc = ((1 - (valueRatePerPiece / actualNewSellingPrice)) * 100.0) - productPriceListVo.getMarginPercentage();
      }
    }
    Double marginNewDeviationDer = 0.0;
    if (expectedNewLandPrice != null && valueRatePerPiece != null) {
      marginNewDeviationDer = expectedNewLandPrice - valueRatePerPiece;
    } else {
      marginNewDeviationDer = 0.0;
    }

    productPriceListVo.setValuePtrPerProdPiece(valueNewPtr == null ? null : valueNewPtr);
    productPriceListVo.setValuePtsPerProdPieceSell(valueNewPts == null || valueNewPts.isInfinite() ? null : valueNewPts);
    productPriceListVo.setActualSellingPriceDerived(actualNewSellingPrice == null || actualNewSellingPrice.isInfinite() ? 0 : actualNewSellingPrice);
    productPriceListVo.setExpectedLandingRate(expectedNewLandPrice == null || expectedNewLandPrice.isInfinite() ? 0 : expectedNewLandPrice);
    productPriceListVo.setMarginValueDeviationDer(marginNewDeviationDer == null || marginNewDeviationDer.isInfinite() ? 0 : marginNewDeviationDer);
    productPriceListVo.setMarginPercentageDeviation((marginNewDeviationPerc == null || marginNewDeviationPerc.isInfinite()) ? 0 : marginNewDeviationPerc);
  }

  public static boolean isIgstTransaction(Integer interState, Integer sezZone) {
    if (interState != null && sezZone != null) {
      if (interState.intValue() == 1 || sezZone.intValue() == 1) {
        return true;
      }
    }
    return false;
  }

  public static final double getRateDifference(double marginPercentage, double value, int rateCriteria) {
    double rateDiff = 0.0;
    if (marginPercentage != 0) {
      if (rateCriteria == SystemConstants.RATE_DERIVATION_MARK_DOWN) {
        // Mark Down
        rateDiff = value * (1 - (marginPercentage / 100));
      } else {
        // Mark Up
        rateDiff = value / (1 + (marginPercentage / 100));
      }
    } else {
      rateDiff = value;
    }
    return rateDiff;
  }
}
