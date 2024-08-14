/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import spica.scm.common.ProductStockSummary;
import spica.sys.FileConstant;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;
import spica.scm.common.ProductStockDetail;
import spica.scm.service.ProductStockSummaryService;

/**
 *
 * @author keerthana
 */
@Named(value = "productStockSummaryView")
@ViewScoped

public class ProductStockSummaryView implements Serializable {

  private transient List<ProductStockSummary> productStockSummaryList;
  private transient int saleable;
  private transient int blocked;
  private transient int damaged;
  private transient int expired;
  private transient int excess;
  private transient int saleableDetail;
  private transient int blockedDetail;
  private transient int damagedDetail;
  private transient int expiredDetail;
  private transient int excessDetail;

  public void setProductStockSummaryList(List<ProductStockSummary> productStockSummaryList) {
    this.productStockSummaryList = productStockSummaryList;
  }

  public void setProductStockDetailList(List<ProductStockDetail> productStockDetailList) {
    this.productStockDetailList = productStockDetailList;
  }
  private transient List<ProductStockDetail> productStockDetailList;

  private transient ProductStockSummary productStockSummary = null;

  public List<ProductStockSummary> getProductStockSummaryList(MainView main) {
    try {
      if (StringUtil.isEmpty(productStockSummaryList)) {
        productStockSummaryList = ProductStockSummaryService.selectProductStockSummaryList(main, UserRuntimeView.instance().getAccountGroup());
      }
      saleable = 0;
      blocked = 0;
      damaged = 0;
      expired = 0;
      excess = 0;
      if (!StringUtil.isEmpty(productStockSummaryList)) {
        for (ProductStockSummary list : productStockSummaryList) {
          saleable += list.getSaleable() != null ? list.getSaleable() : 0;
          blocked += list.getBlocked() != null ? list.getBlocked() : 0;
          damaged += list.getDamaged() != null ? list.getDamaged() : 0;
          expired += list.getExpired() != null ? list.getExpired() : 0;
          excess += list.getExcess() != null ? list.getExcess() : 0;
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return productStockSummaryList;
  }

  public List<ProductStockDetail> getProductStockDetailList(MainView main) {
    try {
      if (StringUtil.isEmpty(productStockDetailList)) {
        productStockDetailList = ProductStockSummaryService.selectProductStockDetailList(main, getProductStockSummary());
      }
      saleableDetail = 0;
      blockedDetail = 0;
      damagedDetail = 0;
      expiredDetail = 0;
      excessDetail = 0;
      if (!StringUtil.isEmpty(productStockDetailList)) {
        for (ProductStockDetail detail : productStockDetailList) {
          saleableDetail += detail.getSaleable() != null ? detail.getSaleable() : 0;
          blockedDetail += detail.getBlocked() != null ? detail.getBlocked() : 0;
          damagedDetail += detail.getDamaged() != null ? detail.getDamaged() : 0;
          expiredDetail += detail.getExpired() != null ? detail.getExpired() : 0;
          excessDetail += detail.getExcess() != null ? detail.getExcess() : 0;
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return productStockDetailList;
  }

  public String productStockDetailPopup(ProductStockSummary productStockSummary) {
    Jsf.popupList(FileConstant.PRODUCT_STOCK_DETAIL, productStockSummary);
    return null;

  }

  /**
   * Getting the parent object and id for edit.
   */
  @PostConstruct
  public void init() {
    productStockSummary = (ProductStockSummary) Jsf.popupParentValue(ProductStockSummary.class);

  }

  public ProductStockSummaryView() {
  }

  public ProductStockSummary getProductStockSummary() {
    return productStockSummary;
  }

  public void setProductStockSummary(ProductStockSummary productStockSummary) {
    this.productStockSummary = productStockSummary;
  }

  public void productStockDetailPopupClose() {
    Jsf.popupReturn(productStockDetailList, null);
  }

  public int getSaleable() {
    return saleable;
  }

  public void setSaleable(int saleable) {
    this.saleable = saleable;
  }

  public int getBlocked() {
    return blocked;
  }

  public void setBlocked(int blocked) {
    this.blocked = blocked;
  }

  public int getDamaged() {
    return damaged;
  }

  public void setDamaged(int damaged) {
    this.damaged = damaged;
  }

  public int getExpired() {
    return expired;
  }

  public void setExpired(int expired) {
    this.expired = expired;
  }

  public int getExcess() {
    return excess;
  }

  public void setExcess(int excess) {
    this.excess = excess;
  }

  public int getSaleableDetail() {
    return saleableDetail;
  }

  public void setSaleableDetail(int saleableDetail) {
    this.saleableDetail = saleableDetail;
  }

  public int getBlockedDetail() {
    return blockedDetail;
  }

  public void setBlockedDetail(int blockedDetail) {
    this.blockedDetail = blockedDetail;
  }

  public int getDamagedDetail() {
    return damagedDetail;
  }

  public void setDamagedDetail(int damagedDetail) {
    this.damagedDetail = damagedDetail;
  }

  public int getExpiredDetail() {
    return expiredDetail;
  }

  public void setExpiredDetail(int expiredDetail) {
    this.expiredDetail = expiredDetail;
  }

  public int getExcessDetail() {
    return excessDetail;
  }

  public void setExcessDetail(int excessDetail) {
    this.excessDetail = excessDetail;
  }

}
