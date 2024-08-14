/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.addon.view;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import spica.constant.ReportConstant;
import spica.addon.model.CalendarReport;
import spica.reports.model.FilterParameters;
import spica.addon.service.CalendarReportService;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;

/**
 *
 * @author sujesh
 */
@Named(value = "calendarReportView")
@ViewScoped
public class CalendarReportView implements Serializable {

  private CalendarReport calendarReport;
  private List<CalendarReport> calendarReportList;
  private FilterParameters filterParameters;

  private ScheduleEvent event = new DefaultScheduleEvent();
  private ScheduleModel lazyEventModel;

  @PostConstruct
  public void init() {

    lazyEventModel = new LazyScheduleModel() {
      @Override
      public void loadEvents(Date start, Date end) {
        setStartEndDate(start, end);
        getCalendarElements();
      }
    };
  }

  public void reset() {
    //company = null;
  }

  private void getCalendarElements() {
    if (lazyEventModel != null) {
      MainView main = Jsf.getMain();
      try {
        calendarReportList = CalendarReportService.selectCalendarReportByType(main, UserRuntimeView.instance().getCompany().getId(), getFilterParameters());
        if (calendarReportList != null) {
          for (CalendarReport report : calendarReportList) {
            String partyName = report.getAmount() == null ? report.getPartyName() : report.getPartyName() + "(" + report.getAmount() + ")";
            DefaultScheduleEvent obj = new DefaultScheduleEvent(partyName, report.getDate(), report.getDate(), report);
            obj.setDescription(partyName);
            if (report.getStatus() == SystemConstants.DRAFT) {
              obj.setStyleClass("draft");
            } else {
              obj.setStyleClass("confirm");
            }
            lazyEventModel.addEvent(obj);
          }
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
  }

  public CalendarReport getCalendarReport() {
    return calendarReport;
  }

  public List<CalendarReport> getCalendarReportList() {
    return calendarReportList;
  }

  public void setCalendarReportList(List<CalendarReport> calendarReportList) {
    this.calendarReportList = calendarReportList;
  }

  public FilterParameters getFilterParameters() {
    if (filterParameters == null) {
      filterParameters = new FilterParameters();
      filterParameters.setFilterType(ReportConstant.SALES);
    }
    return filterParameters;
  }

  public ScheduleEvent getEvent() {
    return event;
  }

  public void setEvent(ScheduleEvent event) {
    this.event = event;
  }

  public void onEventSelect(SelectEvent selectEvent) {
    event = (ScheduleEvent) selectEvent.getObject();
    CalendarReport report = (CalendarReport) event.getData();
    if (filterParameters.getFilterType().equals(ReportConstant.SALES)) {
      Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesInvoiceView().replaceFirst("/scm", ""), report.getId(), report.getId());
    } else if (filterParameters.getFilterType().equals(ReportConstant.SALES_ORDER)) {
      Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesOrderView().replaceFirst("/scm", ""), report.getId(), report.getId());
    } else if (filterParameters.getFilterType().equals(ReportConstant.SALES_RETURN)) {
      Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesReturnView().replaceFirst("/scm", ""), report.getId(), report.getId());
    } else if (filterParameters.getFilterType().equals(ReportConstant.PURCHASE_ENTRY)) {
      Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getProductEntryView().replaceFirst("/scm", ""), report.getId(), report.getId());
    } else if (filterParameters.getFilterType().equals(ReportConstant.PURCHASE_ORDER)) {
      Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getPurchaseOrderView().replaceFirst("/scm", ""), report.getId(), report.getId());
    } else if (filterParameters.getFilterType().equals(ReportConstant.PURCHASE_RETURN)) {
      Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getPurchaseReturnView().replaceFirst("/scm", ""), report.getId(), report.getId());
    } else if (filterParameters.getFilterType().equals(ReportConstant.SALES_SERVICE)) {
      Jsf.popupForm(UserRuntimeView.instance().getTaxCalculator().getSalesServiceInvoiceView().replaceFirst("/scm", ""), report.getId(), report.getId());
    }
  }

  public ScheduleModel getLazyEventModel() {
    return lazyEventModel;
  }

  public void setLazyEventModel(ScheduleModel lazyEventModel) {
    this.lazyEventModel = lazyEventModel;
  }

  private void setStartEndDate(Date start, Date end) {
    getFilterParameters().setFromDate(start);
    getFilterParameters().setToDate(end);
  }
}
