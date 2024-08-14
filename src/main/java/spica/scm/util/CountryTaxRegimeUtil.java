/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.util;

import spica.scm.domain.ServiceCommodity;
import spica.scm.domain.Company;
import spica.scm.domain.ProductCategory;
import spica.fin.domain.TaxCode;

/**
 *
 * @author java-2
 */
public class CountryTaxRegimeUtil {

  public static final Integer VAT_REGIME = 1;
  public static final Integer GST_REGIME = 2;  

  private CountryTaxRegimeUtil() {
  }

  /**
   * Method to get the String value of Commodity Tax.
   *
   * @param commodityTax
   * @param countryTaxRegime
   * @return
   */
  public static final String getCommodityTaxAsString(ServiceCommodity commodity, Integer countryTaxRegime) {
    StringBuilder pvalue = new StringBuilder("");
    StringBuilder svalue = new StringBuilder("");
    if (commodity != null && countryTaxRegime != null) {
      if (GST_REGIME.equals(countryTaxRegime)) {
        //Purchase GST TaxCode
        pvalue.append("Purchase GST - ").append(getTaxCodeAsString(commodity.getPurchaseTaxCodeId()));
        //Sales GST TaxCode
        svalue.append("Sales GST - ").append(getTaxCodeAsString(commodity.getSalesTaxCodeId()));
      }
    }

    return (pvalue.append(svalue)).toString();
  }

  /**
   *
   * @param productCategoryTax
   * @param countryTaxRegime
   * @return
   */
  public static final String getProductCategoryTaxAsString(ProductCategory productCategory, Integer countryTaxRegime) {
    StringBuilder rvalue = new StringBuilder("");
    if (productCategory != null && countryTaxRegime != null) {
      if (GST_REGIME.equals(countryTaxRegime)) {
        //Purchase GST TaxCode
        rvalue.append("Purchase GST - ").append(getTaxCodeAsString(productCategory.getPurchaseTaxCodeId()));
        //Sales GST TaxCode
        rvalue.append("Sales GST - ").append(getTaxCodeAsString(productCategory.getSalesTaxCodeId()));
      }
    }
    return rvalue.toString();
  }

  /**
   * Method to get the String value of TaxCode.
   *
   * @param taxCode
   * @return
   */
  private static String getTaxCodeAsString(TaxCode taxCode) {
    StringBuilder rvalue = new StringBuilder("");
    if (taxCode != null) {
      rvalue.append(taxCode.getRatePercentage()).append("% ");
      if (taxCode.getAbatementPercentage() != null) {
        rvalue.append(", Abatement Percentage ").append(taxCode.getAbatementPercentage()).append("% ; ");
      } else {
        rvalue.append("; ");
      }
    }
    return rvalue.toString();
  }

  /**
   *
   * @param company
   * @return
   */
  public static final boolean isVatRegime(Company company) {
    if (company != null && company.getCountryTaxRegimeId() != null) {
      return (VAT_REGIME.equals(company.getCountryTaxRegimeId().getRegime()));
    }
    return false;
  }

  /**
   *
   * @param company
   * @return
   */
  public static final boolean isGstRegime(Company company) {
    if (company != null && company.getCountryTaxRegimeId() != null) {
      return (GST_REGIME.equals(company.getCountryTaxRegimeId().getRegime()));
    }
    return false;
  }

}
