/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.scm.util;

import spica.scm.domain.AccountStatus;
import spica.scm.domain.ContractStatus;
import wawo.entity.util.StringUtil;

/**
 *
 * @author java-2
 */
public abstract class AccountContractUtil {

  private static final String FIXED_AMOUNT = "1";
  private static final String FIXED_PERCENTAGE = "2";
  private static final String SLAB_AMOUNT = "3";
  private static final String SLAB_PERCENTAGE = "4";
  
  public static final int C_FORM = 1;
  public static final int F_FORM = 2;
  
  public static final int COMMODITY_MOVEMENT_TYPE_STOCK_TRANSFER = 1;
  public static final int COMMODITY_MOVEMENT_TYPE_PURCHASE = 2; 
  

  public static final int CONTRACT_STATUS_ACTIVE = 1;
  public static final int CONTRACT_STATUS_DRAFT = 2;
  public static final int CONTRACT_STATUS_RENEW = 3;
  public static final int CONTRACT_STATUS_INACTIVE = 4;
  
  public static final String CONTRACT_STATUS_ACTIVE_LABEL = "Active";
  public static final String CONTRACT_STATUS_INACTIVE_LABEL = "Inactive";
  public static final String CONTRACT_STATUS_DRAFT_LABEL = "Draft";
  public static final String CONTRACT_STATUS_RENEW_LABEL = "Renew";
  
  public static final int ACCOUNT_STATUS_DRAFT = 2;
  public static final int ACCOUNT_STATUS_ACTIVE = 1;  

  public static boolean isFixedCommissionType(String commissionType) {
    if (!StringUtil.isEmpty(commissionType)) {
      if (FIXED_AMOUNT.equals(commissionType)) {
        return true;
      } else {
        return FIXED_PERCENTAGE.equals(commissionType);
      }
    }
    return false;
  }

  public static boolean isSlabCommissionType(String commissionType) {
    if (!StringUtil.isEmpty(commissionType)) {
      if (SLAB_AMOUNT.equals(commissionType)) {
        return true;
      } else {
        return SLAB_PERCENTAGE.equals(commissionType);
      }
    }
    return false;
  }

  public static boolean isCommissionByAmount(String commissionType) {
    if (!StringUtil.isEmpty(commissionType)) {
      if (SLAB_AMOUNT.equals(commissionType) || FIXED_AMOUNT.equals(commissionType)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isCommissionByPercentage(String commissionType) {
    if (!StringUtil.isEmpty(commissionType)) {
      if (FIXED_PERCENTAGE.equals(commissionType) || SLAB_PERCENTAGE.equals(commissionType)) {
        return true;
      }
    }
    return false;
  }

  public static ContractStatus getContractStatusIntance(int status) {
    ContractStatus cs = new ContractStatus();
    cs.setId(status);
    return cs;
  }
  
  public static AccountStatus getAccountStatusIntance(int status) {
    AccountStatus as = new AccountStatus();
    as.setId(status);
    return as;
  }

  public static String getContractStatus(ContractStatus status) {
    if(status != null && status.getId() != null){
     return status.getId() == 1 ? CONTRACT_STATUS_ACTIVE_LABEL : status.getId() == 3 ? CONTRACT_STATUS_RENEW_LABEL : status.getId() == 4 ?  CONTRACT_STATUS_INACTIVE_LABEL : CONTRACT_STATUS_DRAFT_LABEL;
    }    
    return "";
  }
}
