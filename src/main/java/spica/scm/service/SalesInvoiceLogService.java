/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.service;

import java.util.Date;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceLog;
import spica.sys.UserRuntimeView;
import wawo.app.Main;
import wawo.app.common.AppService;

/**
 *
 * @author manee
 */
public class SalesInvoiceLogService {

  public static final void insert(Main main, SalesInvoiceLog salesInvoiceLog) {
    AppService.insert(main, salesInvoiceLog);
  }

  public static final void createSalesInvoiceLogDraft(Main main, SalesInvoice salesInvoice) {
    SalesInvoiceLog salesInvoiceLog = new SalesInvoiceLog(salesInvoice);
    salesInvoiceLog.setDraftedAt(new Date());
    if (UserRuntimeView.instance().getAppUser() != null) {
      salesInvoiceLog.setDraftedBy(UserRuntimeView.instance().getAppUser().getName());
    }
    salesInvoiceLog.setDescription("DRAFTED");
    insert(main, salesInvoiceLog);
  }

  public static final void createSalesInvoiceLogConfirm(Main main, SalesInvoice salesInvoice) {
    SalesInvoiceLog salesInvoiceLog = new SalesInvoiceLog(salesInvoice);
    salesInvoiceLog.setDescription("CONFIRMED");
    insert(main, salesInvoiceLog);
  }

  public static final void createSalesInvoiceLogDelete(Main main, SalesInvoice salesInvoice) {
    SalesInvoiceLog salesInvoiceLog = null;
    if (salesInvoice.getCompanyId() == null) {
      salesInvoiceLog = new SalesInvoiceLog(SalesInvoiceService.selectByPk(main, salesInvoice));
    } else {
      salesInvoiceLog = new SalesInvoiceLog(salesInvoice);
    }
    if (salesInvoiceLog != null) {
      salesInvoiceLog.setDeletedAt(new Date());
      if (UserRuntimeView.instance().getAppUser() != null) {
        salesInvoiceLog.setDeletedBy(UserRuntimeView.instance().getAppUser().getName());
      }
      salesInvoiceLog.setDescription("DELETED");
      insert(main, salesInvoiceLog);
    }
  }
}
