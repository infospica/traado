/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.event.SelectEvent;
import spica.fin.domain.TaxCode;
import spica.scm.common.ProductRateSummary;
import spica.scm.domain.ProductCategory;
import spica.scm.service.AccountService;
import spica.scm.service.ProductCategoryService;
import spica.scm.service.ProductRateSummaryService;
import spica.scm.util.MathUtil;
import spica.scm.util.ProductUtil;
import spica.sys.UserRuntimeView;
import wawo.app.config.ViewType;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.util.StringUtil;

/**
 *
 * @author java-2
 */
@Named(value = "productRateSummaryView")
@ViewScoped
public class ProductRateSummaryView implements Serializable {

  private transient ProductCategory productCategory;
  private transient List<ProductCategory> productCategoryList;
  private transient List<ProductRateSummary> productRateSummaryList;
  private transient TaxCode prevTaxCodeId;
  private transient TaxCode taxCodeId;
  private transient List<TaxCode> taxCodeList;
  private boolean renderModifyButton;
  private boolean renderConfirmButton;

  /**
   * Creates a new instance of ProductRateSummaryView
   */
  public ProductRateSummaryView() {
  }

  public ProductCategory getProductCategory() {
    return productCategory;
  }

  public void setProductCategory(ProductCategory productCategory) {
    this.productCategory = productCategory;
  }

  public List<ProductCategory> getProductCategoryList() {
    return productCategoryList;
  }

  public void setProductCategoryList(List<ProductCategory> productCategoryList) {
    this.productCategoryList = productCategoryList;
  }

  public List<ProductRateSummary> getProductRateSummaryList() {
    return productRateSummaryList;
  }

  public void setProductRateSummaryList(List<ProductRateSummary> productRateSummaryList) {
    this.productRateSummaryList = productRateSummaryList;
  }

  public TaxCode getPrevTaxCodeId() {
    return prevTaxCodeId;
  }

  public void setPrevTaxCodeId(TaxCode prevTaxCodeId) {
    this.prevTaxCodeId = prevTaxCodeId;
  }

  public List<TaxCode> getTaxCodeList() {
    return taxCodeList;
  }

  public void setTaxCodeList(List<TaxCode> taxCodeList) {
    this.taxCodeList = taxCodeList;
  }

  public TaxCode getTaxCodeId() {
    return taxCodeId;
  }

  public void setTaxCodeId(TaxCode taxCodeId) {
    this.taxCodeId = taxCodeId;
  }

  public boolean isRenderModifyButton() {
    return renderModifyButton;
  }

  public void setRenderModifyButton(boolean renderModifyButton) {
    this.renderModifyButton = renderModifyButton;
  }

  public boolean isRenderConfirmButton() {
    return renderConfirmButton;
  }

  public void setRenderConfirmButton(boolean renderConfirmButton) {
    this.renderConfirmButton = renderConfirmButton;
  }

  public String switchProductRateSummaryView(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
        } else if (ViewType.list.toString().equals(viewType)) {
          setRenderConfirmButton(false);
          setRenderModifyButton(true);
          setProductRateSummaryList(null);
          setPrevTaxCodeId(null);
          setProductCategory(null);
          setTaxCodeId(null);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  public List<ProductCategory> lookupProductCategory() {
    return ScmLookupExtView.lookupProductCategory();
  }

  public List<TaxCode> lookupTaxCode() {
    return ScmLookupExtView.lookupPurchaseTaxCode(UserRuntimeView.instance().getCompany(), getPrevTaxCodeId());
  }

  public void productCategorySelectEvent(SelectEvent event) {
    setPrevTaxCodeId(null);
    setTaxCodeId(null);
    setRenderModifyButton(true);
    setRenderConfirmButton(false);
    setProductRateSummaryList(null);
    ProductCategory pc = (ProductCategory) event.getObject();
    if (pc != null) {
      setPrevTaxCodeId(pc.getPurchaseTaxCodeId());
    }
  }

  public void taxCodeSelectEvent(SelectEvent event) {
    setRenderModifyButton(true);
    setRenderConfirmButton(false);
    setProductRateSummaryList(null);
  }

  public String actionModifyProductCategoryTaxCode(MainView main) {
    try {
      Double mrpLte, valuePts, valuePtr;
      productRateSummaryList = ProductRateSummaryService.getProductRateSummaryList(main, getProductCategory());
      if (!StringUtil.isEmpty(productRateSummaryList)) {
        for (ProductRateSummary productRateSummary : productRateSummaryList) {
          // finds latest mrplte, ptr and pts value
          if (productRateSummary.getPtsDerivationCriteria() != null && productRateSummary.getPtsDerivationCriteria().equals(AccountService.DERIVE_PTS_FROM_MRP_PTR)) {
            mrpLte = ProductUtil.getMrpLteValue(productRateSummary.getValueMrp(), getTaxCodeId().getRatePercentage());
            valuePtr = ProductUtil.getPtrValue(productRateSummary.getPtrMarginPercentage(), mrpLte, productRateSummary.getMrpltePtrRateDerivationCriterion());
            valuePts = ProductUtil.getPtsValue(productRateSummary.getPtsMarginPercentage(), valuePtr, productRateSummary.getPtrPtsRateDerivationCriterion());
            productRateSummary.setNewPtr(MathUtil.roundOff(valuePtr, 2));
            productRateSummary.setNewPts(MathUtil.roundOff(valuePts, 2));
          }
        }
        setRenderModifyButton(false);
        setRenderConfirmButton(true);
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return null;
  }

  public String actionConfirmProductCategoryTaxCode(MainView main) {
    try {
      long productCount = ProductRateSummaryService.isProductCategoryConsumed(main, getProductCategory());
      Jsf.addCallbackParam("productCount", productCount);
      if (productCount == 0) {
        updateProductCategoryTaxCode(main);
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
    return null;
  }

  public void updateProductCategoryTaxCode(MainView main) {
    try {
      if (!StringUtil.isEmpty(getProductRateSummaryList())) {
        ProductRateSummaryService.updateProductPriceList(main, getProductRateSummaryList());
      }
      ProductRateSummaryService.updateTaxCodeModifiedFlag(main, getProductCategory());
      getProductCategory().setPurchaseTaxCodeId(getTaxCodeId());
      getProductCategory().setSalesTaxCodeId(getTaxCodeId());
      ProductCategoryService.updateByPk(main, getProductCategory());
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }

}
