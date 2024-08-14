/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.function.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import spica.constant.AccountingConstant;
import spica.fin.domain.TaxCode;
import spica.scm.common.InvoiceGroup;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.function.CessProcessor;
import spica.scm.function.SalesInvoiceProcessor;
import spica.scm.util.MathUtil;
import spica.sys.SystemConstants;
import spica.sys.SystemRuntimeConfig;
import wawo.entity.util.StringUtil;

/**
 *
 * @author anoop
 */
public class SalesInvoiceProcessorImpl implements SalesInvoiceProcessor {

  @Override
  public void processSalesInvoice(SalesInvoice salesInvoice, List<SalesInvoiceItem> salesInvoiceItems, boolean draftView) {

    Double invoiceNetValue = 0.0;
    Double invoiceGoodsValue = 0.0;
    Double invoiceDiscount = 0.0;
    Double cashDiscount = 0.0;
    Double assessableValue = 0.0;
    Double groupAssessableValue = 0.0;
    Double invoiceIgstValue = 0.0, invoiceCgstValue = 0.0, invoiceSgstValue = 0.0;
    Double lineNetValue = 0.0;
    Double valueTaxable = 0.0;
    Double goodsValue = 0.0;
    Double productAssasableValue = 0.0;
    Double grandTotal = 0.0;
    Double serviceNetAmount = 0.0, serviceNetIgst = 0.0, serviceNetSgst = 0.0, serviceNetCgst = 0.0;
    Double service2NetAmount = 0.0, service2NetIgst = 0.0, service2NetSgst = 0.0, service2NetCgst = 0.0;
    Double keralaFloodCessValue = 0.0;
    int decimalPlaces = salesInvoice.getDecimalPrecision() != null ? salesInvoice.getDecimalPrecision() : 2;

    Integer productNetQty = 0;

    HashMap<Integer, InvoiceGroup> invoiceGroupMap = new HashMap<>();
    InvoiceGroup invoiceGroup;

    Double invoiceValue = 0.0;
    Double invoiceRoundOff = 0.0;
    boolean cashDiscountApplied = false;
    double schemeDiscountValue = 0.0;
    double productDiscountValue = 0.0;
    boolean schemeApplicable = false;
    double productSoldRate = 0.0;
    Integer saleableQty = 0;
    Double freeDiscountValue = 0.0;
    Date KERALA_FLOOD_CESS_START_DATE = null;

    int groupCount;

    salesInvoice.setServiceValueCgst(null);
    salesInvoice.setServiceValueIgst(null);
    salesInvoice.setServiceValueSgst(null);
    salesInvoice.setServiceTaxCodeId(null);
    salesInvoice.setService2Cgst(null);
    salesInvoice.setService2Igst(null);
    salesInvoice.setService2Sgst(null);
    salesInvoice.setService2TaxCodeId(null);
    salesInvoice.setTcsNetValue(null);
    salesInvoice.setTcsTaxCodeId(null);
    if (salesInvoice != null && salesInvoice.getServiceId() != null && salesInvoice.getFreightRate() != null && salesInvoice.getFreightRate() > 0) {
      serviceNetAmount = salesInvoice.getFreightRate();
      if (salesInvoice.getServiceId().getSalesTaxCodeId() != null) {
        salesInvoice.setServiceTaxCodeId(salesInvoice.getServiceId().getSalesTaxCodeId());
        if (salesInvoice.getServiceTaxCodeId().getRatePercentage() > 0) {
          if (!StringUtil.isEmpty(salesInvoice.getServiceTaxCodeId().getTaxCodeList()) && salesInvoice.getIsSalesInterstate() == (SystemConstants.INTRASTATE.intValue()) && salesInvoice.getSezZone().intValue() == 0) {
            for (TaxCode tax : salesInvoice.getServiceTaxCodeId().getTaxCodeList()) {
              if (AccountingConstant.TAX_TYPE_CGST == tax.getTaxType()) {
                serviceNetCgst = (MathUtil.roundOff((serviceNetAmount * (tax.getRatePercentage() / 100)), 2));
              } else if (AccountingConstant.TAX_TYPE_SGST == tax.getTaxType()) {
                serviceNetSgst = (MathUtil.roundOff((serviceNetAmount * (tax.getRatePercentage() / 100)), 2));
              }
            }
            serviceNetIgst = serviceNetCgst + serviceNetSgst;
          } else {
            serviceNetIgst = (MathUtil.roundOff((serviceNetAmount * (salesInvoice.getServiceId().getSalesTaxCodeId().getRatePercentage() / 100)), 2));
          }
          salesInvoice.setServiceValueIgst(serviceNetIgst);
          salesInvoice.setServiceValueCgst(serviceNetCgst);
          salesInvoice.setServiceValueSgst(serviceNetSgst);
        }
      }
    }
//    service 2 calculations
    if (salesInvoice != null && salesInvoice.getServices2Id() != null && salesInvoice.getService2Rate() != null && salesInvoice.getService2Rate() > 0) {
      service2NetAmount = salesInvoice.getService2Rate();
      if (salesInvoice.getServices2Id().getSalesTaxCodeId() != null) {
        salesInvoice.setService2TaxCodeId(salesInvoice.getServices2Id().getSalesTaxCodeId());
        if (salesInvoice.getService2TaxCodeId().getRatePercentage() > 0) {
          if (!StringUtil.isEmpty(salesInvoice.getService2TaxCodeId().getTaxCodeList()) && salesInvoice.getIsSalesInterstate() == (SystemConstants.INTRASTATE.intValue()) && salesInvoice.getSezZone().intValue() == 0) {
            for (TaxCode tax : salesInvoice.getService2TaxCodeId().getTaxCodeList()) {
              if (AccountingConstant.TAX_TYPE_CGST == tax.getTaxType()) {
                service2NetCgst = (MathUtil.roundOff((service2NetAmount * (tax.getRatePercentage() / 100)), 2));
              } else if (AccountingConstant.TAX_TYPE_SGST == tax.getTaxType()) {
                service2NetSgst = (MathUtil.roundOff((service2NetAmount * (tax.getRatePercentage() / 100)), 2));
              }
            }
            service2NetIgst = service2NetCgst + service2NetSgst;
          } else {
            service2NetIgst = (MathUtil.roundOff((service2NetAmount * (salesInvoice.getServices2Id().getSalesTaxCodeId().getRatePercentage() / 100)), 2));
          }
          salesInvoice.setService2Cgst(service2NetCgst);
          salesInvoice.setService2Sgst(service2NetSgst);
          salesInvoice.setService2Igst(service2NetIgst);
        }
      }
    }

    for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItems) {
      salesInvoiceItem.setTcsValue(null);
      salesInvoiceItem.setTcsValueDerived(null);
      if ((salesInvoiceItem.getProductQty() != null && salesInvoiceItem.getProductQty() >= 0)
              && (salesInvoiceItem.getProdPieceSellingForced() != null
              && salesInvoiceItem.getProdPieceSellingForced() > 0)) {

        if (salesInvoiceItem.getProductDetailSales() != null) {
          salesInvoiceItem.setQuantityOrFree(salesInvoiceItem.getProductDetailSales().getQtyOrFree());
        }

        /**
         * Temporary fix, in future this code doesn't needed.
         */
        if (salesInvoiceItem.getQuantityOrFree() == null) {
          salesInvoiceItem.setQuantityOrFree(SystemConstants.QUANTITY_SCHEME);
        }

        schemeApplicable = salesInvoiceItem.getQuantityOrFree() == SystemConstants.FREE_SCHEME;
        productSoldRate = salesInvoiceItem.getProdPieceSellingForced();

        //Finds line total
        if (schemeApplicable) {
          saleableQty = salesInvoiceItem.getProductQty();
          lineNetValue = saleableQty * salesInvoiceItem.getProdPieceSellingForced();
        } else {
          saleableQty = salesInvoiceItem.getProductQty() + (salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree());
          lineNetValue = saleableQty * salesInvoiceItem.getProdPieceSellingForced();
        }

        // Sets line net amount
        goodsValue = MathUtil.roundOff((saleableQty * salesInvoiceItem.getProdPieceSellingForced()), 2);
        invoiceGoodsValue += goodsValue;
        salesInvoiceItem.setValueGoods(goodsValue);
        valueTaxable = goodsValue;

        salesInvoiceItem.setInvoiceDiscountDerived(null);
        salesInvoiceItem.setCashDiscountValueDerived(null);

        //Applying Free Scheme Discount
        salesInvoiceItem.setSchemeDiscountDerived(null);
        if (!schemeApplicable) {
          if (salesInvoiceItem.getProductQtyFree() != null && salesInvoiceItem.getProductQtyFree() > 0) {

            freeDiscountValue = salesInvoiceItem.getProductQtyFree() * salesInvoiceItem.getProdPieceSellingForced();
            salesInvoiceItem.setSchemeDiscountValue(freeDiscountValue);
            salesInvoiceItem.setSchemeDiscountPercentage((freeDiscountValue * 100) / lineNetValue);
            lineNetValue -= freeDiscountValue;
            valueTaxable = lineNetValue;
            salesInvoiceItem.setSchemeDiscountDerived(freeDiscountValue / saleableQty);
            productSoldRate -= salesInvoiceItem.getSchemeDiscountDerived();
          } else if ((salesInvoiceItem.getSchemeDiscountValue() != null && salesInvoiceItem.getSchemeDiscountValue() > 0)
                  || (salesInvoiceItem.getSchemeDiscountPercentage() != null && salesInvoiceItem.getSchemeDiscountPercentage() > 0)) {

            if (SystemConstants.DISCOUNT_IN_PERCENTAGE.equals(salesInvoiceItem.getIsSchemeDiscountInPercentage())) {
              if (salesInvoiceItem.getSchemeDiscountPercentage() != null) {
                schemeDiscountValue = ((salesInvoiceItem.getSchemeDiscountPercentage() / 100) * lineNetValue);
                salesInvoiceItem.setSchemeDiscountValue(MathUtil.roundOff(schemeDiscountValue, 2));
              }
            } else {
              schemeDiscountValue = salesInvoiceItem.getSchemeDiscountValue() == null ? 0 : salesInvoiceItem.getSchemeDiscountValue();
              if (schemeDiscountValue > 0) {
                salesInvoiceItem.setSchemeDiscountPercentage((schemeDiscountValue * 100) / lineNetValue);
              }
            }

            if (schemeDiscountValue > 0) {
              lineNetValue -= schemeDiscountValue;
              valueTaxable = lineNetValue;
              salesInvoiceItem.setSchemeDiscountDerived(schemeDiscountValue / saleableQty);
              productSoldRate -= salesInvoiceItem.getSchemeDiscountDerived();
            }

          }
        }

        //Applying line discount.
        salesInvoiceItem.setProductDiscountDerived(null);
        if ((salesInvoiceItem.getProductDiscountValue() != null && salesInvoiceItem.getProductDiscountValue() > 0)
                || (salesInvoiceItem.getProductDiscountPercentage() != null && salesInvoiceItem.getProductDiscountPercentage() > 0)) {

          if (SystemConstants.DISCOUNT_IN_PERCENTAGE.equals(salesInvoiceItem.getIsProductDiscountInPercentage())) {
            if (salesInvoiceItem.getProductDiscountPercentage() != null) {
              productDiscountValue = ((MathUtil.roundOff(salesInvoiceItem.getProductDiscountPercentage(), 2) / 100) * lineNetValue);
              salesInvoiceItem.setProductDiscountValue(MathUtil.roundOff(productDiscountValue, 2));
            }
          } else {
            productDiscountValue = salesInvoiceItem.getProductDiscountValue() == null ? 0 : salesInvoiceItem.getProductDiscountValue();
            if (productDiscountValue > 0) {
              salesInvoiceItem.setProductDiscountPercentage(MathUtil.roundOff(((salesInvoiceItem.getProductDiscountValue() * 100) / lineNetValue), 2));
            }
          }

          if (productDiscountValue > 0) {
            lineNetValue -= productDiscountValue;
            valueTaxable = lineNetValue;
            salesInvoiceItem.setProductDiscountDerived(productDiscountValue / saleableQty);
            productSoldRate -= salesInvoiceItem.getProductDiscountDerived();
          }
        }

        lineNetValue = MathUtil.roundOff(lineNetValue, 2);
        salesInvoiceItem.setValueSale(lineNetValue);
        salesInvoiceItem.setValueTaxable(valueTaxable);
        salesInvoiceItem.setValueProdPieceSold(productSoldRate);

        // Calculating invoice net amount;
        invoiceNetValue += lineNetValue;

        productNetQty += (salesInvoiceItem.getProductQty() + (schemeApplicable ? 0 : salesInvoiceItem.getProductQtyFree() != null ? salesInvoiceItem.getProductQtyFree() : 0));

        if (invoiceGroupMap.containsKey(salesInvoiceItem.getTaxCodeId().getId())) {
          InvoiceGroup group = invoiceGroupMap.get(salesInvoiceItem.getTaxCodeId().getId());
          group.setInvoiceNetAmount(MathUtil.roundOff(group.getInvoiceNetAmount() + lineNetValue, 2));
          group.setInvoiceGoodsAmount(MathUtil.roundOff(group.getInvoiceGoodsAmount() + goodsValue, 2));
          group.setAssessableValue(group.getInvoiceNetAmount());
          group.setProductQuantity(group.getProductQuantity() + 1);
          invoiceGroupMap.put(salesInvoiceItem.getTaxCodeId().getId(), group);
        } else {
          InvoiceGroup group = new InvoiceGroup();
          group.setInvoiceNetAmount(MathUtil.roundOff(lineNetValue, 2));
          group.setTaxCode(salesInvoiceItem.getTaxCodeId());
          group.setAssessableValue(MathUtil.roundOff(group.getInvoiceNetAmount(), 2));
          group.setInvoiceGoodsAmount(MathUtil.roundOff(goodsValue, 2));
          group.setProductQuantity(1);
          invoiceGroupMap.put(salesInvoiceItem.getTaxCodeId().getId(), group);
        }
      }
    }

    invoiceNetValue = MathUtil.roundOff(invoiceNetValue, 2);
    salesInvoice.setInvoiceAmountNet(invoiceNetValue);

    groupCount = invoiceGroupMap.size();
    Double groupDiscount = 0.0;

    if (groupCount > 0) {

      try {
        KERALA_FLOOD_CESS_START_DATE = SystemRuntimeConfig.SDF_YYYY_MM_DD.parse("2019-08-01");
      } catch (ParseException ex) {
        Logger.getLogger(SalesInvoiceProcessorImpl.class.getName()).log(Level.SEVERE, null, ex);
      }

      // Apply invoice discount
      if (((salesInvoice.getInvoiceAmountDiscount() != null && salesInvoice.getInvoiceAmountDiscount() > 0)
              || (salesInvoice.getInvoiceAmtDiscPercent() != null && salesInvoice.getInvoiceAmtDiscPercent() > 0)) && invoiceNetValue > 0) {

        if (salesInvoice.getInvoiceAmountDiscount() != null) {
          invoiceDiscount = salesInvoice.getInvoiceAmountDiscount();
        } else if (salesInvoice.getInvoiceAmtDiscPercent() != null) {
          invoiceDiscount = (invoiceNetValue * (salesInvoice.getInvoiceAmtDiscPercent() / 100));
        }

        if (invoiceDiscount != null && invoiceDiscount > 0) {
          for (Map.Entry<Integer, InvoiceGroup> invoiceGroupSet : invoiceGroupMap.entrySet()) {

            invoiceGroup = invoiceGroupSet.getValue();

            if (groupCount == 1) {
              invoiceGroup.setInvoiceDiscount(invoiceDiscount);
              groupAssessableValue = invoiceNetValue - invoiceDiscount;
              invoiceGroup.setAssessableValue(MathUtil.roundOff(groupAssessableValue, 2));
              productAssasableValue += groupAssessableValue;
            }

            for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItems) {
              if (salesInvoiceItem != null && salesInvoiceItem.getTaxCodeId() != null) {
                schemeApplicable = salesInvoiceItem.getQuantityOrFree() == SystemConstants.FREE_SCHEME;
                if (invoiceGroup.getTaxCode().getId().equals(salesInvoiceItem.getTaxCodeId().getId())) {

                  if (invoiceGroup.getInvoiceDiscount() == null) {
                    groupDiscount = (invoiceGroup.getInvoiceNetAmount() / invoiceNetValue) * invoiceDiscount;
                    invoiceGroup.setInvoiceDiscount(MathUtil.roundOff(groupDiscount, 2));
                    groupAssessableValue = invoiceGroup.getInvoiceNetAmount() - groupDiscount;
                    invoiceGroup.setAssessableValue(MathUtil.roundOff(groupAssessableValue, 2));
                    productAssasableValue += groupAssessableValue;
                  }

                  Double lineInvoiceDisc = (salesInvoiceItem.getValueSale() / invoiceGroup.getInvoiceNetAmount()) * invoiceGroup.getInvoiceDiscount();
                  Double unitDiscount = null;
                  lineInvoiceDisc = MathUtil.roundOff(lineInvoiceDisc, 2);
                  if (schemeApplicable) {
                    unitDiscount = lineInvoiceDisc / salesInvoiceItem.getProductQty();
                  } else {
                    unitDiscount = lineInvoiceDisc / (salesInvoiceItem.getProductQty() + (salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree()));
                  }
                  salesInvoiceItem.setValueTaxable(MathUtil.roundOff(salesInvoiceItem.getValueTaxable() - lineInvoiceDisc, 2));
                  salesInvoiceItem.setInvoiceDiscountValue(lineInvoiceDisc);
                  salesInvoiceItem.setInvoiceDiscountDerived(unitDiscount);
                  salesInvoiceItem.setValueProdPieceSold(salesInvoiceItem.getValueProdPieceSold() - salesInvoiceItem.getInvoiceDiscountDerived());
                }
              }
            }
          }
        }
      } else {
        productAssasableValue = invoiceNetValue;
      }

      // Applying Cash Discount.
      if (salesInvoice.getCashDiscountValue() != null && salesInvoice.getCashDiscountValue() > 0 && invoiceNetValue > 0) {

        cashDiscount = salesInvoice.getCashDiscountValue();

        for (Map.Entry<Integer, InvoiceGroup> invoiceGroupSet : invoiceGroupMap.entrySet()) {

          invoiceGroup = invoiceGroupSet.getValue();

          if (groupCount == 1) {
            invoiceGroup.setCashDiscount(cashDiscount);

            if (salesInvoice.getCashDiscountTaxable() != null && salesInvoice.getCashDiscountTaxable().intValue() == SystemConstants.CASH_DISCOUNT_TAXABLE) {
              groupAssessableValue = productAssasableValue - salesInvoice.getCashDiscountValue();
            } else {
              groupAssessableValue = productAssasableValue;
            }
            invoiceGroup.setAssessableValue(groupAssessableValue);
          }

          for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItems) {
            if (salesInvoiceItem != null && salesInvoiceItem.getTaxCodeId() != null) {
              schemeApplicable = salesInvoiceItem.getQuantityOrFree() == SystemConstants.FREE_SCHEME;
              if (invoiceGroup.getTaxCode().getId().equals(salesInvoiceItem.getTaxCodeId().getId())) {

                if (invoiceGroup.getCashDiscount() == null) {

                  groupDiscount = (invoiceGroup.getAssessableValue() / productAssasableValue) * salesInvoice.getCashDiscountValue();
                  invoiceGroup.setCashDiscount(MathUtil.roundOff(groupDiscount, 2));
                  if (salesInvoice.getCashDiscountTaxable() != null) {
                    groupAssessableValue = invoiceGroup.getAssessableValue() - groupDiscount;
                  } else {
                    groupAssessableValue = invoiceGroup.getAssessableValue();
                  }
                  invoiceGroup.setAssessableValue(MathUtil.roundOff(groupAssessableValue, 2));
                }

                Double lineCashDisc = (salesInvoiceItem.getValueSale() / invoiceGroup.getInvoiceNetAmount()) * invoiceGroup.getCashDiscount();
                Double unitDiscount = null;
                lineCashDisc = MathUtil.roundOff(lineCashDisc, 2);
                if (schemeApplicable) {
                  unitDiscount = lineCashDisc / salesInvoiceItem.getProductQty();
                } else {
                  unitDiscount = lineCashDisc / (salesInvoiceItem.getProductQty() + (salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree()));
                }
                if (salesInvoice.getCashDiscountTaxable() != null && salesInvoice.getCashDiscountTaxable().intValue() == SystemConstants.CASH_DISCOUNT_TAXABLE) {
                  salesInvoiceItem.setValueTaxable(MathUtil.roundOff(salesInvoiceItem.getValueTaxable() - lineCashDisc, 2));
                }
                salesInvoiceItem.setCashDiscountValue(lineCashDisc);
                salesInvoiceItem.setCashDiscountValueDerived(unitDiscount);
                salesInvoiceItem.setValueProdPieceSold(salesInvoiceItem.getValueProdPieceSold() - salesInvoiceItem.getCashDiscountValueDerived());
              }
            }
          }
        }
      }
//Service calculation in Line item
      if ((serviceNetAmount != null && serviceNetAmount > 0) || (service2NetAmount != null && service2NetAmount > 0)) {
        for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItems) {
          if (salesInvoiceItem.getValueSale() != null && (serviceNetAmount != null && serviceNetAmount > 0)) {
            schemeApplicable = salesInvoiceItem.getQuantityOrFree() == SystemConstants.FREE_SCHEME;
            if (salesInvoiceItem.getValueSale() != null && (serviceNetAmount != null && serviceNetAmount > 0)) {
              salesInvoiceItem.setServiceFreightValue(salesInvoiceItem.getValueSale() / invoiceNetValue * serviceNetAmount);
              if (schemeApplicable) {
                salesInvoiceItem.setServiceFreightValueDerived(salesInvoiceItem.getServiceFreightValue() / salesInvoiceItem.getProductQty());
              } else {
                salesInvoiceItem.setServiceFreightValueDerived(salesInvoiceItem.getServiceFreightValue()
                        / (salesInvoiceItem.getProductQty() + (salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree())));
              }
            }
            if (salesInvoiceItem.getValueSale() != null && service2NetAmount != null && service2NetAmount > 0) {
              salesInvoiceItem.setService2Value(salesInvoiceItem.getValueSale() / invoiceNetValue * service2NetAmount);
              if (schemeApplicable) {
                salesInvoiceItem.setService2ValueDerived(salesInvoiceItem.getService2Value() / salesInvoiceItem.getProductQty());
              } else {
                salesInvoiceItem.setService2ValueDerived(salesInvoiceItem.getService2Value()
                        / (salesInvoiceItem.getProductQty() + (salesInvoiceItem.getProductQtyFree() == null ? 0 : salesInvoiceItem.getProductQtyFree())));
              }
            }
          }
        }
      }

      /**
       * Find the tax values of products.
       */
      invoiceIgstValue = 0.0;
      invoiceCgstValue = 0.0;
      invoiceSgstValue = 0.0;
      for (Map.Entry<Integer, InvoiceGroup> invoiceGroupSet : invoiceGroupMap.entrySet()) {
        invoiceGroup = invoiceGroupSet.getValue();

        for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItems) {
          if (salesInvoiceItem != null && salesInvoiceItem.getTaxCodeId() != null) {
            if (invoiceGroup.getTaxCode().getId().equals(salesInvoiceItem.getTaxCodeId().getId())) {
              if (invoiceGroup.getInvoiceIgstValue() == null) {

                if (SystemConstants.INTRASTATE.equals(salesInvoice.getIsSalesInterstate()) && salesInvoice.getSezZone().intValue() == 0) {
                  for (TaxCode taxCode : invoiceGroup.getTaxCode().getTaxCodeList()) {
                    if (AccountingConstant.TAX_TYPE_CGST == taxCode.getTaxType()) {
                      invoiceGroup.setInvoiceCgstValue(MathUtil.roundOff((invoiceGroup.getAssessableValue() * (taxCode.getRatePercentage() / 100)), decimalPlaces));
                    } else if (AccountingConstant.TAX_TYPE_SGST == taxCode.getTaxType()) {
                      invoiceGroup.setInvoiceSgstValue(MathUtil.roundOff((invoiceGroup.getAssessableValue() * (taxCode.getRatePercentage() / 100)), decimalPlaces));
                    }
                  }
                  invoiceCgstValue += invoiceGroup.getInvoiceCgstValue();
                  invoiceSgstValue += invoiceGroup.getInvoiceSgstValue();
                  invoiceGroup.setInvoiceIgstValue(MathUtil.roundOff((invoiceGroup.getInvoiceCgstValue() + invoiceGroup.getInvoiceSgstValue()), decimalPlaces));
                } else {
                  invoiceGroup.setInvoiceIgstValue((MathUtil.roundOff(invoiceGroup.getAssessableValue() * (invoiceGroup.getTaxCode().getRatePercentage() / 100) / 2, decimalPlaces)) * 2);
                }
                invoiceIgstValue += invoiceGroup.getInvoiceIgstValue();

              }

              assessableValue = ((salesInvoiceItem.getValueSale() == null ? 0 : salesInvoiceItem.getValueSale()) - (salesInvoiceItem.getInvoiceDiscountValue() == null ? 0 : salesInvoiceItem.getInvoiceDiscountValue()));
              if (salesInvoice.getCashDiscountTaxable() != null && salesInvoice.getCashDiscountTaxable().intValue() == 1) {
                assessableValue -= salesInvoiceItem.getCashDiscountValue() != null ? salesInvoiceItem.getCashDiscountValue() : 0;
              }
              salesInvoiceItem.setValueIgst(MathUtil.roundOff((assessableValue / invoiceGroup.getAssessableValue()) * invoiceGroup.getInvoiceIgstValue(), decimalPlaces));
              if (SystemConstants.INTRASTATE.equals(salesInvoice.getIsSalesInterstate()) && salesInvoice.getSezZone().intValue() == 0) {
                salesInvoiceItem.setValueCgst(MathUtil.roundOff((assessableValue / invoiceGroup.getAssessableValue()) * invoiceGroup.getInvoiceCgstValue(), decimalPlaces));
                salesInvoiceItem.setValueSgst(MathUtil.roundOff((assessableValue / invoiceGroup.getAssessableValue()) * invoiceGroup.getInvoiceSgstValue(), decimalPlaces));
              }
            }
          }
        }
      }

      //Finds Kerala Flood Cess value 
      // 
      if (draftView) {
        if (salesInvoice.getCustomerId().getTradeProfile() != null && SystemConstants.TRADE_PROFILE_CONSUMER == salesInvoice.getCustomerId().getTradeProfile().getId()) {
          if (salesInvoice.getCompanyId().getSpecialCessTax() != null && salesInvoice.getCompanyId().getSpecialCessTax() > 0) {
            if (salesInvoice.getCompanyId().getSpecialCessTax() == SystemConstants.CESS_ON_INTRASTATE_SALES
                    && salesInvoice.getIsSalesInterstate().equals(SystemConstants.INTRASTATE) && salesInvoice.getSezZone().intValue() == 0) {

              // Cess Applicable for intrastate and consumer sales.
              if (salesInvoice.getInvoiceDate().compareTo(KERALA_FLOOD_CESS_START_DATE) >= 0) {
                keralaFloodCessValue = calculateKeralaFloodCessOnSales(salesInvoice, salesInvoiceItems);
              }

            } else if (SystemConstants.CESS_ON_INTERSTATE_SALES == salesInvoice.getCompanyId().getSpecialCessTax().intValue()
                    && (salesInvoice.getIsSalesInterstate().equals(SystemConstants.INTERSTATE) || salesInvoice.getSezZone().intValue() == 0)) {
              // Cess applicable only for intersate sales

            } else if (SystemConstants.CESS_ON_ALL_SALES == salesInvoice.getCompanyId().getSpecialCessTax().intValue()) {
              // Cess applicable for both interstate and intrastate and also for consumer.
            } else {
              salesInvoice.setKeralaFloodCessService1Value(null);
              salesInvoice.setKeralaFloodCessService2Value(null);
            }
          } else {
            salesInvoice.setKeralaFloodCessService1Value(null);
            salesInvoice.setKeralaFloodCessService2Value(null);
          }
        } else {
          salesInvoice.setKeralaFloodCessService1Value(null);
          salesInvoice.setKeralaFloodCessService2Value(null);
        }
      } else {
        keralaFloodCessValue = salesInvoice.getKeralaFloodCessNetValue() != null ? salesInvoice.getKeralaFloodCessNetValue() : 0;
      }

    }
    salesInvoice.setKeralaFloodCessNetValue(keralaFloodCessValue);
    if (salesInvoice.getKeralaFloodCessNetValue() == null || (salesInvoice.getKeralaFloodCessNetValue() != null && salesInvoice.getKeralaFloodCessNetValue() == 0)) {
      for (SalesInvoiceItem invoiceItem : salesInvoiceItems) {
        invoiceItem.setKeralaFloodCessValue(0.0);
        invoiceItem.setKeralaFloodCessValueDerived(0.0);
      }
    }
    assessableValue = invoiceNetValue - (invoiceDiscount == null ? 0 : invoiceDiscount);

    if (salesInvoice.getCashDiscountTaxable() != null && salesInvoice.getCashDiscountValue() != null && salesInvoice.getCashDiscountValue() > 0) {
      assessableValue = assessableValue - salesInvoice.getCashDiscountValue();
      cashDiscountApplied = true;
    }

    Double totalAssessableValue = assessableValue + serviceNetAmount + service2NetAmount;
    salesInvoice.setInvoiceAmountGoods(invoiceGoodsValue);
    salesInvoice.setInvoiceAmountNet(MathUtil.roundOff(invoiceNetValue, decimalPlaces));
    salesInvoice.setAssessableValue(MathUtil.roundOff(assessableValue, decimalPlaces));
    salesInvoice.setInvoiceAmountAssessable(MathUtil.roundOff(assessableValue, decimalPlaces));
    salesInvoice.setInvoiceAmountAssessableTotal(totalAssessableValue);
    invoiceIgstValue = (MathUtil.roundOff(invoiceIgstValue / 2, decimalPlaces)) * 2;
    salesInvoice.setInvoiceAmountIgst(invoiceIgstValue);
    if (SystemConstants.INTRASTATE.equals(salesInvoice.getIsSalesInterstate()) && salesInvoice.getSezZone().intValue() == 0) {
      salesInvoice.setInvoiceAmountCgst(MathUtil.roundOff(invoiceCgstValue, decimalPlaces));
      salesInvoice.setInvoiceAmountSgst(MathUtil.roundOff(invoiceSgstValue, decimalPlaces));
    }
    if (salesInvoice.getServiceValueIgst() != null) {
      invoiceIgstValue += salesInvoice.getServiceValueIgst();
    }
    if (salesInvoice.getService2Igst() != null) {
      invoiceIgstValue += salesInvoice.getService2Igst();
    }
    Double kfService1Value = salesInvoice.getKeralaFloodCessService1Value() != null ? salesInvoice.getKeralaFloodCessService1Value() : 0.0;
    Double kfService2Value = salesInvoice.getKeralaFloodCessService2Value() != null ? salesInvoice.getKeralaFloodCessService2Value() : 0.0;
    Double kfCessTotal = keralaFloodCessValue + kfService1Value + kfService2Value;
    salesInvoice.setInvoiceAmountIgstTotal(MathUtil.roundOff(invoiceIgstValue, decimalPlaces));

    grandTotal = MathUtil.roundOff(assessableValue + invoiceIgstValue + serviceNetAmount + service2NetAmount + kfCessTotal, decimalPlaces);
    salesInvoice.setGrandTotal(grandTotal);
    if (salesInvoice.getCashDiscountApplicable() != null && salesInvoice.getCashDiscountValue() != null && salesInvoice.getCashDiscountValue() > 0 && !cashDiscountApplied) {
      grandTotal = (salesInvoice.getGrandTotal() - salesInvoice.getCashDiscountValue());
      invoiceValue = Double.valueOf(Math.round(grandTotal));
    } else {
      invoiceValue = Double.valueOf(Math.round(salesInvoice.getGrandTotal()));
    }
    salesInvoice.setInvoiceAmountSubtotal(grandTotal);
    if (salesInvoice.getRoundOffForced() != null) {
      invoiceRoundOff = salesInvoice.getRoundOffForced();
      invoiceValue = grandTotal + invoiceRoundOff;
    } else {
      invoiceRoundOff = invoiceValue - grandTotal;
    }
    salesInvoice.setKfCessTotal(kfCessTotal);
    salesInvoice.setInvoiceValue(invoiceValue);
    salesInvoice.setInvoiceAmount(invoiceValue);
    salesInvoice.setInvoiceRoundOff(invoiceRoundOff);

    salesInvoice.setInvoiceDiscountValue(invoiceDiscount);
    salesInvoice.setInvoiceRoundOff(MathUtil.roundOff(invoiceRoundOff, decimalPlaces));
    salesInvoice.setRoundOff(salesInvoice.getInvoiceRoundOff());
    salesInvoice.setProductNetQuantity(productNetQty);
    salesInvoice.setInvoiceGroup(new ArrayList<>(invoiceGroupMap.values()));
  }

  private Double calculateKeralaFloodCessOnSales(SalesInvoice salesInvoice, List<SalesInvoiceItem> salesInvoiceItems) {

    double keralaFloodCessValue = 0.0;
    int productActualQty = 0;
    CessProcessor cessProcessor = new SalesInvoiceCessProcessorImpl();
    for (SalesInvoiceItem salesInvoiceItem : salesInvoiceItems) {
      if (salesInvoiceItem.getProduct() != null && salesInvoiceItem.getValueTaxable() != null) {

        if (!(salesInvoiceItem.getQuantityOrFree() == SystemConstants.FREE_SCHEME)) {
          productActualQty = salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty();
          if (salesInvoiceItem.getProductQtyFree() != null && salesInvoiceItem.getProductQtyFree() > 0) {
            productActualQty += salesInvoiceItem.getProductQtyFree();
          }
        } else {
          productActualQty = salesInvoiceItem.getProductQty() == null ? 0 : salesInvoiceItem.getProductQty();
        }

        if (salesInvoice.getKeralaFloodCessTaxCodeId() == null) { // only setting the first now only one cess percentage
          salesInvoice.setKeralaFloodCessTaxCodeId(salesInvoiceItem.getProduct().getProductCategoryId().getSalesCessTaxCodeId());//setting cess tax code                
        }
        if (StringUtil.isEmpty(salesInvoice.getCustomerId().getGstNo())) {
          if (salesInvoiceItem.getTaxCodeId().getRatePercentage() > 5.00) {
            salesInvoiceItem.setKeralaFloodCessValue(cessProcessor.processSalesInvoiceCess(salesInvoiceItem.getProduct().getProductCategoryId().getSalesCessTaxCodeId(),
                    salesInvoiceItem.getValueTaxable()));

            salesInvoiceItem.setKeralaFloodCessValueDerived(salesInvoiceItem.getKeralaFloodCessValue() / productActualQty);
            keralaFloodCessValue += salesInvoiceItem.getKeralaFloodCessValue();
          }
        }
      }
    }
    //For Services
    if (StringUtil.isEmpty(salesInvoice.getCustomerId().getGstNo())) {
      if (salesInvoice.getFreightRate() != null) {
        if (salesInvoice.getServiceTaxCodeId() != null && salesInvoice.getServiceTaxCodeId().getRatePercentage() > 5.00) {
          salesInvoice.setKeralaFloodCessService1Value(cessProcessor.processSalesInvoiceCess(salesInvoice.getKeralaFloodCessTaxCodeId(),
                  salesInvoice.getFreightRate()));
        }
      }
      if (salesInvoice.getService2Rate() != null) {
        if (salesInvoice.getService2TaxCodeId() != null && salesInvoice.getService2TaxCodeId().getRatePercentage() > 5.00) {
          salesInvoice.setKeralaFloodCessService2Value(cessProcessor.processSalesInvoiceCess(salesInvoice.getKeralaFloodCessTaxCodeId(),
                  salesInvoice.getService2Rate()));
        }
      }
    }
    return keralaFloodCessValue;
  }
}
