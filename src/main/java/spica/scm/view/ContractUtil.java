/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.view;

import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import spica.scm.domain.Contract;

/**
 *
 * @author java-2
 */
@Named(value = "contractUtilView")
@ApplicationScoped
public class ContractUtil implements Serializable {
//FIXME better way to use this class

  private final static Integer FACTORY_DIRECT = 1;
  private final static Integer GOODS_RECIEVED = 1;
  private final static Integer PURCHASE_INVOICE = 2;
  private final static Integer SALES = 3;
  private final static Integer COLLECTION = 4;
  private final static Integer LR_DATE = 5;

  private final static String TXT_GOODS_RECIEVED = "Goods Recieved";
  private final static String TXT_PURCHASE_INVOICE = "Purchase Invoice";
  private final static String TXT_SALES = "Sales";
  private final static String TXT_COLLECTION = "Collection";
  private final static String TXT_LR_DATE = "LR Date";

  private static final int CONTRACT_STATUS_ACTIVE = 1;
  private static final int CONTRACT_STATUS_DRAFT = 2;
  private static final int CONTRACT_STATUS_RENEW = 3;
  private static final int CONTRACT_STATUS_INACTIVE = 4;

  public ContractUtil() {
  }

  public static int getContractStatusDraft() {
    return CONTRACT_STATUS_DRAFT;
  }

  public static int getContractStatusActive() {
    return CONTRACT_STATUS_ACTIVE;
  }

  public static int getContractStatusRenew() {
    return CONTRACT_STATUS_RENEW;
  }

  public static int getContractStatusInactive() {
    return CONTRACT_STATUS_INACTIVE;
  }

  public static boolean isFactoryDirect(Contract contract) {
    return (FACTORY_DIRECT.equals(contract.getIsFactoryDirect()));
  }

  public static boolean isExciseDutyApplicable(Contract contract) {
    return isFactoryDirect(contract);
  }

  public static String getPurchaseCreditApplicableFrom(Contract contract) {
    if (GOODS_RECIEVED.equals(contract.getPurchaseCreditdaysAppliFrom())) {
      return TXT_GOODS_RECIEVED;
    } else if (PURCHASE_INVOICE.equals(contract.getPurchaseCreditdaysAppliFrom())) {
      return TXT_PURCHASE_INVOICE;
    } else if (SALES.equals(contract.getPurchaseCreditdaysAppliFrom())) {
      return TXT_SALES;
    } else if (COLLECTION.equals(contract.getPurchaseCreditdaysAppliFrom())) {
      return TXT_COLLECTION;
    } else if (LR_DATE.equals(contract.getPurchaseCreditdaysAppliFrom())) {
      return TXT_LR_DATE;
    }
    return null;
  }

  public Double contractMarginPercentage(Contract contract) {
    return contract != null ? contract.getMarginPercentage() : null;
  }

  public String contractMarginPercentageAsString(Contract contract) {
    return contract != null ? contract.getMarginPercentage() == null ? "" : contract.getMarginPercentage() + " %" : null;
  }

  public Double contractVendorReservePercentage(Contract contract) {
    return contract != null ? contract.getVendorReservePercent() : null;
  }

  public String contractVendorReservePercentageAsString(Contract contract) {
    return contract != null ? contract.getVendorReservePercent() == null ? "" : contract.getVendorReservePercent() + " %" : null;
  }

  public Double contractPtrMarginPercentage(Contract contract) {
    return contract != null ? contract.getPtrMarginPercent() : null;
  }

  public String contractPtrMarginPercentageAsString(Contract contract) {
    return contract != null ? contract.getPtrMarginPercent() == null ? "" : contract.getPtrMarginPercent() + " %" : null;
  }

  public Double contractPtsMarginPercentage(Contract contract) {
    return contract != null ? contract.getPtsMarginPercent() : null;
  }

  public String contractPtsMarginPercentageAsString(Contract contract) {
    return contract != null ? contract.getPtsMarginPercent() == null ? "" : contract.getPtsMarginPercent() + " %" : null;
  }
}
