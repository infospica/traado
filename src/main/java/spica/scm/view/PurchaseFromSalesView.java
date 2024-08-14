/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import spica.scm.common.ProductSummary;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.ProductEntry;
import spica.scm.domain.ProductEntryDetail;
import spica.scm.domain.ProductPreset;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesInvoiceItem;
import spica.scm.service.ProductPresetService;
import spica.scm.service.PurchaseFromSalesService;
import spica.scm.service.SalesInvoiceItemService;
import spica.scm.tax.TaxCalculator;
import spica.sys.SystemRuntimeConfig;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

/**
 *
 * @author godson
 */
@Named(value = "purchaseFromSalesView")
@ViewScoped
public class PurchaseFromSalesView implements Serializable {

  private ProductEntry productEntry;
  private transient TaxCalculator taxCalculator;
  private transient List<SalesInvoice> salesInvoiceList;
  private transient List<Product> productList;
  private transient SalesInvoice salesInvoiceSelected;
  private transient LazyDataModel<SalesInvoice> salesInvoiceLazyModel; 	//For lazy loading datatable.

  @PostConstruct
  public void init() {
    productEntry = (ProductEntry) Jsf.dialogParent(ProductEntry.class);

  }

  public ProductEntry getProductEntry() {
    return productEntry;
  }

  public void setProductEntry(ProductEntry productEntry) {
    this.productEntry = productEntry;
  }

  public TaxCalculator getTaxCalculator() {
    if (productEntry.getTaxProcessorId() != null) {
      taxCalculator = (SystemRuntimeConfig.getTaxCalculator(productEntry.getTaxProcessorId().getProcessorClass()));
    }
    return taxCalculator;
  }

  public void setTaxCalculator(TaxCalculator taxCalculator) {
    this.taxCalculator = taxCalculator;
  }

  public List<SalesInvoice> getSalesInvoiceList() {
    return salesInvoiceList;
  }

  public void setSalesInvoiceList(List<SalesInvoice> salesInvoiceList) {
    this.salesInvoiceList = salesInvoiceList;
  }

  public SalesInvoice getSalesInvoiceSelected() {
    return salesInvoiceSelected;
  }

  public void setSalesInvoiceSelected(SalesInvoice salesInvoiceSelected) {
    this.salesInvoiceSelected = salesInvoiceSelected;
  }

  public LazyDataModel<SalesInvoice> getSalesInvoiceLazyModel() {
    return salesInvoiceLazyModel;
  }

  public void setSalesInvoiceLazyModel(LazyDataModel<SalesInvoice> salesInvoiceLazyModel) {
    this.salesInvoiceLazyModel = salesInvoiceLazyModel;
  }

  public List<Product> getProductList() {
    return productList;
  }

  public void setProductList(List<Product> productList) {
    this.productList = productList;
  }

  public String switchPurchaseFromSales(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        loadPurchaseFromSalesList(main);
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create salesInvoiceLazyModel.
   *
   * @param main
   */
  private void loadPurchaseFromSalesList(final MainView main) {
    if (salesInvoiceLazyModel == null) {
      salesInvoiceLazyModel = new LazyDataModel<SalesInvoice>() {
        private List<SalesInvoice> list;

        @Override
        public List<SalesInvoice> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = PurchaseFromSalesService.listPaged(main, getProductEntry(), UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
            main.commit(salesInvoiceLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(SalesInvoice salesInvoice) {
          return salesInvoice.getId();
        }

        @Override
        public SalesInvoice getRowData(String rowKey) {
          if (list != null) {
            for (SalesInvoice obj : list) {
              if (rowKey.equals(obj.getId().toString())) {
                return obj;
              }
            }
          }
          return null;
        }
      };
    }
  }

  public void purchaseFromSalesPopupClose() {
    Jsf.popupReturn(getProductEntry(), null);
  }

  public void addProductEntryListFromSales(MainView main) {
    productEntry.setProductEntryDetailList(new ArrayList<>());
    PurchaseFromSalesService.deleteProductEntryDetailByEntry(main, productEntry);
    try {
      if (salesInvoiceSelected != null) {
        List<SalesInvoiceItem> salesInvoiceItemList = SalesInvoiceItemService.selectSalesInvoiceItemBySalesInvoice(main, salesInvoiceSelected, getTaxCalculator(), false);
        List<ProductEntryDetail> productEntryDetailList = new ArrayList<>();
        List<Product> unavailableProducts = new ArrayList<>();
        if (!StringUtil.isEmpty(salesInvoiceItemList)) {
          for (SalesInvoiceItem invoiceItem : salesInvoiceItemList) {
            ProductEntryDetail productEntryDetail = new ProductEntryDetail();
            productEntryDetail.setProductEntryId(productEntry);
            productEntryDetail.setRefSalesInvoiceId(salesInvoiceSelected.getId());
            Product product = PurchaseFromSalesService.productInCurrentComapny(main, UserRuntimeView.instance().getCompany(), invoiceItem.getProduct());
            if (product != null && product.getId() != null) {
              ProductPreset preset = ProductPresetService.selectProductPresetByProductAndAccount(main, product.getId(), getProductEntry().getAccountId());
              if (preset != null) {
                productEntryDetail.setProduct(product);
                productEntryDetail.setHsnCode(product.getHsnCode());
                ProductBatch batch = PurchaseFromSalesService.findorCreateBatch(main, product, invoiceItem.getBatchNo(), invoiceItem.getExpiryDate(), invoiceItem.getValueMrp(), invoiceItem.getProduct().getId());
                productEntryDetail.setProductBatchId(batch);
                productEntryDetail.setProductDetailId(PurchaseFromSalesService.getProductDetailFromProductAndAccount(main, batch, getProductEntry().getAccountId()));
                ProductSummary productSummary = new ProductSummary(productEntryDetail.getProduct().getId(), productEntryDetail.getProductBatchId().getId(),
                        productEntryDetail.getProduct().getProductName(), null, (productEntryDetail.getProductDetailId() != null ? productEntryDetail.getProductDetailId().getId() : null),
                        productEntryDetail.getProductBatchId().getBatchNo());
                productEntryDetail.setProductSummary(productSummary);
                getTaxCalculator().updateProductEntryDetail(main, getProductEntry().getAccountId(), productEntryDetail);
                productEntryDetail.setProductQuantity(invoiceItem.getProductQty().doubleValue());
                productEntryDetail.setProductQuantityFree(invoiceItem.getProductQtyFree());
                productEntryDetail.setSchemeDiscountPercentage(invoiceItem.getSchemeDiscountPercentage());
                productEntryDetail.setSchemeDiscountReplacement(0);
                productEntryDetail.setSchemeDiscountValue(invoiceItem.getSchemeDiscountValue());
                productEntryDetail.setSchemeDiscountValueDerived(invoiceItem.getSchemeDiscountDerived());
                productEntryDetail.setDiscountPercentage(invoiceItem.getProductDiscountPercentage());
                productEntryDetail.setDiscountValue(invoiceItem.getProductDiscountValue());
                productEntryDetail.setProductDiscountValueDerived(invoiceItem.getProductDiscountDerived());
                productEntryDetail.setInvoiceDiscountValue(invoiceItem.getInvoiceDiscountValue());
                productEntryDetail.setInvoiceDiscountValueDerived(invoiceItem.getInvoiceDiscountDerived());
                productEntryDetail.setCashDiscountValueDerived(invoiceItem.getCashDiscountValueDerived());
                productEntryDetail.setValueRate(invoiceItem.getProdPieceSellingForced());
                productEntryDetail.setIsSchemeDiscountToCustomer(1);
                productEntryDetail.setIsLineDiscountToCustomer(0);
                secondaryAndTertiaryQtyCalculation(productEntryDetail);
              } else {
                product.setProductNameChemical("Preset Missing");
                unavailableProducts.add(product);
              }
            } else {
              product = new Product(null, invoiceItem.getProduct().getProductName());
              product.setProductNameChemical("Product Missing");
              unavailableProducts.add(product);
            }
            productEntryDetailList.add(productEntryDetail);
          }
        }
        if (StringUtil.isEmpty(unavailableProducts)) {
          productEntry.setCashDiscountApplicable(salesInvoiceSelected.getCashDiscountApplicable());
          productEntry.setCashDiscountTaxable(salesInvoiceSelected.getCashDiscountTaxable());
          productEntry.setCashDiscountValue(salesInvoiceSelected.getCashDiscountValue());
          if (salesInvoiceSelected.getInvoiceAmountDiscount() != null) {
            productEntry.setIsInvoiceDiscountInPercentage(2);
            productEntry.setInvoiceAmountDiscountValue(salesInvoiceSelected.getInvoiceAmountDiscount());
            productEntry.setInvoiceAmountDiscountPerc(null);
          } else if (salesInvoiceSelected.getInvoiceAmtDiscPercent() != null) {
            productEntry.setIsInvoiceDiscountInPercentage(1);
            productEntry.setInvoiceAmountDiscountPerc(salesInvoiceSelected.getInvoiceAmtDiscPercent());
            productEntry.setInvoiceDiscountValue(null);
          } else {
            productEntry.setIsInvoiceDiscountInPercentage(null);
            productEntry.setInvoiceDiscountValue(null);
            productEntry.setInvoiceAmountDiscountValue(null);
            productEntry.setInvoiceAmountDiscountPerc(null);
          }
          if (salesInvoiceSelected.getTcsNetValue() != null) {
            productEntry.setTcsNetValue(salesInvoiceSelected.getTcsNetValue());
          }
          productEntry.setProductEntryDetailList(productEntryDetailList);
          Jsf.execute("parent.purchaseFromSalesPopupReturned()");
          Jsf.closePopup(main);
        } else {
          productEntry.setProductEntryDetailList(new ArrayList<>());
          setProductList(unavailableProducts);
          Jsf.update("productErrorTable");
          Jsf.execute("PF('productErrorDlgVar').show();");
        }
      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }

  private void secondaryAndTertiaryQtyCalculation(ProductEntryDetail productEntryDetail) {
    double totalQty = (productEntryDetail.getProductQuantity() != null ? productEntryDetail.getProductQuantity() : 0)
            + (productEntryDetail.getProductQuantityFree() != null ? productEntryDetail.getProductQuantityFree() : 0);
    if (productEntryDetail.getProductQuantity() != null) {
      if (productEntryDetail.getPackSecondary() != null && productEntryDetail.getPackSecondaryPrimaryQty() != null) {
        double sQty = (totalQty / productEntryDetail.getPackSecondaryPrimaryQty());
        productEntryDetail.setProductSecondaryQuantity(sQty);
        productEntryDetail.setValueBoxRate(productEntryDetail.getPackSecondaryPrimaryQty() * productEntryDetail.getValueRate());
      }

      if (productEntryDetail.getPackTertiary() != null && productEntryDetail.getPackTertiarySecondaryQty() != null) {
        Double tQty = productEntryDetail.getProductSecondaryQuantity() / productEntryDetail.getPackTertiarySecondaryQty();
        productEntryDetail.setProductTertairyQuantity(tQty);
      }
    }
  }
}
