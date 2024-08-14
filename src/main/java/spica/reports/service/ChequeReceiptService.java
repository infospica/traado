/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.reports.service;

import java.util.Date;
import java.util.List;
import spica.reports.model.ChequeReceipt;
import spica.scm.domain.Company;
import spica.sys.SystemRuntimeConfig;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author sujesh
 */
public abstract class ChequeReceiptService {

  public static final List seleChequeReceiptListByDate(Main main, Date date, Company company) {
    String sql = "select t1.cheque_no,t1.cheque_date,t1.id,t4.name as drawee,t1.amount ,t2.created_at,t3.title as party_name \n"
            + "from fin_cheque_entry t1,fin_accounting_transaction_detail_item t2,fin_accounting_ledger t3,scm_bank t4\n"
            + "where t1.status_id = 6 and t2.cheque_entry_id = t1.id \n"
            + "AND t1.company_id = ? \n"
            + "AND t2.created_at::date = ?::date   \n"
            + "AND t1.ledger_id = t3.id AND t2.document_type_id=16 \n"
            + "AND t1.bank_id = t4.id \n"
            + "order by t2.created_at desc";
    main.clear();
    main.param(company.getId());
    main.param(SystemRuntimeConfig.SDF_YYYY_MM_DD.format(date));
    return AppDb.getList(main.dbConnector(), ChequeReceipt.class, sql, main.getParamData().toArray());
  }
}
