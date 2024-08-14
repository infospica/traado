/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.view;

import spica.scm.util.AccountContractUtil;
import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author java-2
 */
@Named(value = "accountHandler")
@ApplicationScoped
public class AccountHandlerView implements Serializable {

  /**
   * Creates a new instance of AccountHandlerView
   */
  public AccountHandlerView() {
  }

  public boolean isFixedCommissionType(String commissionType) {
    return AccountContractUtil.isFixedCommissionType(commissionType);
  }

  public boolean isSlabCommissionType(String commissionType) {
    return AccountContractUtil.isSlabCommissionType(commissionType);
  }

  public boolean isCommissionByAmount(String commissionType) {
    return AccountContractUtil.isCommissionByAmount(commissionType);
  }

  public boolean isCommissionByPercentage(String commissionType) {
    return AccountContractUtil.isCommissionByPercentage(commissionType);
  }
}
