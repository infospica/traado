/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.addon.service;

import java.util.Date;
import java.util.List;
import spica.fin.domain.SmsLog;
import spica.scm.domain.CompanySettings;
import spica.sys.domain.SmsProvider;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.SqlPage;

/**
 *
 * @author sujesh
 */
public abstract class SmsUsageService {

  public static List loadCompanySmsUsages(Main main) {

    SqlPage sql = AppService.sqlPage("scm_company_settings", CompanySettings.class, main);
    sql.main("select scm_company_settings.id,scm_company_settings.company_id,scm_company_settings.sms_enabled,scm_company_settings.sms_allowed,scm_company_settings.sms_used,scm_company_settings.sms_provider_id from scm_company_settings scm_company_settings"); //Main query
    sql.join("left outer join scm_company scm_company_settingscompany_id on (scm_company_settingscompany_id.id = scm_company_settings.company_id)"); //Join Query
    sql.cond("where scm_company_settings.company_id is not null");

    return AppService.listPagedJpa(main, sql);
  }

  public static List loadSmsLogByCompanyAndDate(Main main, Integer companyId, Date fromDate, Date toDate) {
    String sql = "select * from scm_sms_log where company_id = ? and created_at::date >=?::date and created_at::date <= ?::date order by id desc";
    return AppService.list(main, SmsLog.class, sql, new Object[]{companyId, fromDate, toDate});

  }

  public static void deleteAllLogByCompanyId(Main main, Integer companyId) {
    String sql = "delete from scm_sms_log where company_id = ? ";
    AppService.deleteSql(main, SmsLog.class, sql, new Object[]{companyId});
  }

  public static void deleteAllLogByCompanyAndDate(Main main, Integer companyId, Date fromDate, Date toDate) {
    String sql = "delete from scm_sms_log where company_id = ? and created_at::date >=?::date and created_at::date <= ?::date";
    AppService.deleteSql(main, SmsLog.class, sql, new Object[]{companyId, fromDate, toDate});
  }

  public static List loadSmsProviders(Main main) {
    SqlPage sql = AppService.sqlPage("sys_sms_provider", SmsProvider.class, main);
    sql.main("select * from sys_sms_provider");
    return AppService.listPagedJpa(main, sql);
  }

  public static void insertOrUpdate(Main main, SmsProvider smsProvider) {
    if (smsProvider.getId() == null) {
      AppService.insert(main, smsProvider);
    } else {
      AppService.update(main, smsProvider);
    }
  }

}
