/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import spica.scm.domain.Consignment;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.SalesInvoice;
import spica.scm.service.ConsignmentCommodityService;
import spica.scm.service.ConsignmentReferenceService;
import spica.scm.service.PurchaseReturnService;
import spica.scm.service.SalesInvoiceService;
import spica.sys.SystemConstants;
import wawo.app.config.ViewType;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author java-3
 */
@Named(value = "consignmentCommodityInvoiceView")
@ViewScoped
public class ConsignmentCommodityInvoiceView implements Serializable {

  private Consignment parent;
  private List<PurchaseReturn> invList;
  private List<SalesInvoice> salesInvoiceList;
  private transient PurchaseReturn[] purchaseReturnInvSelected;	 //Selected Domain Array
  private transient SalesInvoice[] salesInvoiceSelected;	 //Selected Domain Array

  public ConsignmentCommodityInvoiceView() {
    super();
  }

  @PostConstruct
  public void init() {
    parent = (Consignment) Jsf.popupParentValue(Consignment.class);
  }

  public String switchConsignmentCommodityInvoice(MainView main, String viewType) {
    //this.main = main;
    invList = null;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.list.toString().equals(viewType)) {
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  public String insertConsignmentPR(MainView main) {
    try {
      ConsignmentCommodityService.insertPurchaseReturnItems(main, getPurchaseReturnInvSelected(), parent);
      ConsignmentReferenceService.insertArrayInvoice(main, getPurchaseReturnInvSelected(), parent);
      Jsf.popupReturn(parent, null);
      main.commit("success.select");
    } catch (Throwable t) {
      main.rollback(t, "error.list");
    } finally {
      main.close();
    }
    return null;
  }

  public String insertConsignmentSales(MainView main) {
    try {
      ConsignmentCommodityService.insertArraySales(main, getSalesInvoiceSelected(), parent);
      //ConsignmentReferenceService.insertArraySalesInvoice(main, getSalesInvoiceSelected(), parent);
      Jsf.popupReturn(parent, null);
      main.commit("success.select");
    } catch (Throwable t) {
      main.rollback(t, "error.list");
    } finally {
      main.close();
    }
    return null;
  }

  public List<PurchaseReturn> getPRInvoiceList(MainView main) {
    try {
      invList = PurchaseReturnService.selectPurchaseReturnInvoice(main, getParent().getAccountId(), SystemConstants.CONFIRMED_AND_PACKED);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return invList;
  }

  public List<SalesInvoice> getSalesInvoiceList(MainView main) {
    try {
      salesInvoiceList = SalesInvoiceService.selectConfirmedAndPackedSalesInvoice(main, parent.getAccountGroupId());
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return salesInvoiceList;
  }

  public PurchaseReturn[] getPurchaseReturnInvSelected() {
    return purchaseReturnInvSelected;
  }

  public void setPurchaseReturnInvSelected(PurchaseReturn[] purchaseReturnInvSelected) {
    this.purchaseReturnInvSelected = purchaseReturnInvSelected;
  }

  public SalesInvoice[] getSalesInvoiceSelected() {
    return salesInvoiceSelected;
  }

  public void setSalesInvoiceSelected(SalesInvoice[] salesInvoiceSelected) {
    this.salesInvoiceSelected = salesInvoiceSelected;
  }

  public Consignment getParent() {
    return parent;
  }

  public void setParent(Consignment parent) {
    this.parent = parent;
  }
}
