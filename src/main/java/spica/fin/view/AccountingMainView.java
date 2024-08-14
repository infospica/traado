/*
 * @(#)AccountingMainView.java	1.0 Fri Feb 24 15:58:33 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.fin.view;

import java.io.Serializable;
import java.util.Date;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import spica.constant.AccountingConstant;
import spica.fin.common.FilterObjects;
import spica.scm.domain.AccountingFinancialYear;
import spica.scm.domain.Company;
import wawo.app.faces.MainView;

/**
 * AccountingMainView
 *
 * @author	Spirit 1.2
 * @version	1.0, Fri Feb 24 15:58:33 IST 2017
 */
@Named(value = "accountingMainView")
@SessionScoped
public class AccountingMainView extends FilterObjects implements Serializable {

  /**
   * Default Constructor.
   */
  public AccountingMainView() {
    super();
  }

  public void init(MainView main, Company company) {
    main.getPageData().setPageSize(AccountingConstant.PAGE_SIZE);
    super.init(company.getCurrentFinancialYear());
  }

  public boolean backOrNext(int pos, AccountingFinancialYear f, boolean isFrom) {
    if (isFrom) {
      Date moved = wawo.entity.util.DateUtil.moveMonthsToFirsttDay(getFromDate(), pos);
      if (moved.compareTo(f.getStartDate()) >= 0 && moved.compareTo(f.getEndDate()) <= 0) {
        super.setFromDate(moved);
        return true;
      }
    } else {
      Date moved = wawo.entity.util.DateUtil.moveMonthsToLastDay(getToDate(), pos);

      if (moved.compareTo(f.getStartDate()) >= 0 && moved.compareTo(f.getEndDate()) <= 0) {
        super.setToDate(moved);
        return true;
      }
    }
    return false;
  }

}
