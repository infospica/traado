/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.service;

import java.util.ArrayList;
import java.util.List;
import spica.reports.model.FilterParameters;
import spica.reports.model.NearExpiryStockReport;
import spica.sys.SystemConstants;
import wawo.app.Main;
import wawo.entity.core.AppDb;

/**
 *
 * @author godson
 */
public class NearExpiryStockReportService {

  public static List<NearExpiryStockReport> getNearExpiryStockReportList(Main main, FilterParameters filterParameters) {
    String sql = null;
    if (filterParameters.getReportType()!=null && Integer.parseInt(filterParameters.getReportType()) == SystemConstants.SUMMARY.intValue()) {
      sql = "select product_id ,product_name,hsn_code,pack_size,sum(mrp_value) mrp_value,expiry_days,purchase_rate,sum(quantity_saleable) quantity_saleable, sum(quantity_free_saleable) quantity_free_saleable\n"
              + ",expiry_date_actual,batch_no \n"
              + "FROM getStockNearExpiryList(?,?,?)\n"
              + "where quantity_saleable > 0 \n"
              + "group by product_id ,product_name,hsn_code,pack_size,expiry_days,purchase_rate,expiry_date_actual,batch_no ";
    } else {
      sql = "SELECT * FROM getStockNearExpiryList(?,?,?) where quantity_saleable > 0 ;";
    }
    ArrayList<Object> params = new ArrayList<>();
    if (filterParameters.getAccountGroup() != null && filterParameters.getAccountGroup().getId() != null) {
      params.add(filterParameters.getAccountGroup().getId());
    } else {
      params.add(null);
    }
    if (filterParameters.getAccount() != null && filterParameters.getAccount().getId() != null) {
      params.add(filterParameters.getAccount().getId());
    } else {
      params.add(null);
    }
//    int days = (int)TimeUnit.DAYS.convert(new Date().getTime()-filterParameters.getFromDate().getTime(), TimeUnit.MILLISECONDS);
    params.add(filterParameters.getFilterOption());
    return AppDb.getList(main.dbConnector(), NearExpiryStockReport.class, sql, params.toArray());
  }
}
