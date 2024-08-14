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
import spica.fin.domain.TaxCode;
import spica.fin.service.TaxCodeService;
import spica.scm.common.ProductSummary;
import spica.scm.domain.Company;
import spica.scm.domain.Product;
import spica.scm.domain.ProductBatch;
import spica.scm.domain.PurchaseReturn;
import spica.scm.domain.PurchaseReturnItemReplica;
import spica.scm.domain.SalesInvoice;
import spica.scm.domain.SalesReturn;
import spica.scm.domain.SalesReturnItem;
import spica.scm.service.ProductBatchService;
import spica.scm.service.ProductService;
import spica.scm.service.PurchaseReturnItemService;
import spica.scm.service.SalesReturnFromPurReturnService;
import spica.scm.tax.TaxCalculator;
import spica.sys.SystemConstants;
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
@Named(value = "salesReturnFromPurReturnView")
@ViewScoped
public class SalesReturnFromPurReturnView implements Serializable {

  private transient SalesReturn salesReturn;
  private transient TaxCalculator taxCalculator;
  private transient List<PurchaseReturn> purchaseReturnList;
  private transient PurchaseReturn purchaseReturnSelected;
  private transient LazyDataModel<PurchaseReturn> purchaseReturnLazyModel; 	//For lazy loading datatable.

  @PostConstruct
  public void init() {
    salesReturn = (SalesReturn) Jsf.dialogParent(SalesReturn.class);
    if (salesReturn.getTaxProcessorId() != null) {
      taxCalculator = (SystemRuntimeConfig.getTaxCalculator(salesReturn.getTaxProcessorId().getProcessorClass()));
    }
  }

  public SalesReturn getSalesReturn() {
    return salesReturn;
  }

  public void setSalesReturn(SalesReturn salesReturn) {
    this.salesReturn = salesReturn;
  }

  public TaxCalculator getTaxCalculator() {
    return taxCalculator;
  }

  public void setTaxCalculator(TaxCalculator taxCalculator) {
    this.taxCalculator = taxCalculator;
  }

  public List<PurchaseReturn> getPurchaseReturnList() {
    return purchaseReturnList;
  }

  public void setPurchaseReturnList(List<PurchaseReturn> purchaseReturnList) {
    this.purchaseReturnList = purchaseReturnList;
  }

  public PurchaseReturn getPurchaseReturnSelected() {
    return purchaseReturnSelected;
  }

  public void setPurchaseReturnSelected(PurchaseReturn purchaseReturnSelected) {
    this.purchaseReturnSelected = purchaseReturnSelected;
  }

  public LazyDataModel<PurchaseReturn> getPurchaseReturnLazyModel() {
    return purchaseReturnLazyModel;
  }

  public void setPurchaseReturnLazyModel(LazyDataModel<PurchaseReturn> purchaseReturnLazyModel) {
    this.purchaseReturnLazyModel = purchaseReturnLazyModel;
  }

  public String switchSalesReturnFromPurReturn(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        loadSalesReturnFromPurReturnList(main);
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
  private void loadSalesReturnFromPurReturnList(final MainView main) {
    if (purchaseReturnLazyModel == null) {
      purchaseReturnLazyModel = new LazyDataModel<PurchaseReturn>() {
        private List<PurchaseReturn> list;

        @Override
        public List<PurchaseReturn> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = SalesReturnFromPurReturnService.listPaged(main, getSalesReturn(), UserRuntimeView.instance().getCompany().getCurrentFinancialYear());
            main.commit(purchaseReturnLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(PurchaseReturn purchaseReturn) {
          return purchaseReturn.getId();
        }

        @Override
        public PurchaseReturn getRowData(String rowKey) {
          if (list != null) {
            for (PurchaseReturn obj : list) {
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

  public void salesReturnFromPurReturnPopupClose() {
    Jsf.popupReturn(getSalesReturn(), null);
  }

  public void addSalesReturnListfromPurReturn(MainView main) {
    salesReturn.setSalesReturnItemList(new ArrayList<>());
    SalesReturnFromPurReturnService.deleteSalesReturnItemByReturnId(main, salesReturn);
    try {
      if (purchaseReturnSelected != null) {
        List<PurchaseReturnItemReplica> purchaseReturnItemReplicaList = PurchaseReturnItemService.listPurchaseReturnItemByPurchaseReturn(main, purchaseReturnSelected);
        List<SalesReturnItem> salesReturnItemList = new ArrayList<>();
        List<Product> unavailableProducts = new ArrayList<>();
        if (!StringUtil.isEmpty(purchaseReturnItemReplicaList)) {
          for (PurchaseReturnItemReplica itemReplica : purchaseReturnItemReplicaList) {
            SalesReturnItem salesReturnItem = new SalesReturnItem();
            salesReturnItem.setSalesReturnId(salesReturn);
            if (itemReplica.getRefSalesInvoiceId() == null) {
              Long salesId = SalesReturnFromPurReturnService.getRefSalesInvoiceId(main, getCompany(), itemReplica);
              if (salesId != null) {
                itemReplica.setRefSalesInvoiceId(salesId.intValue());
              }
            }
            if (itemReplica.getRefSalesInvoiceId() != null) {
              ProductSummary productSummary = SalesReturnFromPurReturnService.getProductSummaryForReturn(main, getCompany(), salesReturn.getAccountGroupId(), itemReplica);
              if (productSummary != null && productSummary.getProductId() != null) {
                salesReturnItem.setProductSummary(productSummary);
                salesReturnItem.setProductBatchSummary(productSummary);
                salesReturnItem.setProduct(ProductService.selectByPk(main, new Product(productSummary.getProductId())));
                salesReturnItem.setHsnCode(productSummary.getHsnCode());
                salesReturnItem.setProductBatchId(ProductBatchService.selectByPk(main, new ProductBatch(productSummary.getProductBatchId())));
                salesReturnItem.setProductHashCode(salesReturnItem.getProductBatchSummary().getProductCode());
                salesReturnItem.setSalesInvoiceItemIds(salesReturnItem.getProductBatchSummary().getSalesInvoiceItemIds());
                salesReturnItem.setValuePtr(productSummary.getValuePtr());
                salesReturnItem.setValuePts(productSummary.getValuePts());
                salesReturnItem.setValueMrp(productSummary.getMrpValue());
                salesReturnItem.setValueRate(productSummary.getProdPieceSellingForced());
                salesReturnItem.setSchemeDiscountValueDerived(productSummary.getSchemeDiscountDerived());
                salesReturnItem.setProductDiscountValueDerived(productSummary.getProductDiscountDerived());
                salesReturnItem.setInvoiceDiscountValueDerived(productSummary.getInvoiceDiscountDerived());
                salesReturnItem.setTaxCodeId(TaxCodeService.selectByPk(main, new TaxCode(productSummary.getGstTaxCodeId())));
                salesReturnItem.setSalesInvoiceId(new SalesInvoice(itemReplica.getRefSalesInvoiceId()));
                salesReturnItem.setRefInvoiceNo(productSummary.getInvoiceNo());
                salesReturnItem.setRefInvoiceDate(productSummary.getInvoiceDate());
                salesReturnItem.setSchemeDiscountPercentage(productSummary.getSchemeDiscountPercentage());
                salesReturnItem.setProductDiscountPercentage(productSummary.getProductDiscountPercentage());
                salesReturnItem.setProductDetailHash(productSummary.getProductDetailHash());
                if (productSummary.getProductQty() >= itemReplica.getActualQty()) {
                  if (salesReturn.getSalesReturnType().intValue() == SystemConstants.SALES_RETURN_TYPE_SALEABLE) {
                    salesReturnItem.setProductQuantity(itemReplica.getQuantityReturned().intValue());
                  } else {
                    salesReturnItem.setProductQuantityDamaged(itemReplica.getQuantityReturned());
                  }
                  if (salesReturnItem.getInvoiceDiscountValueDerived() != null) {
                    salesReturnItem.setInvoiceDiscountValue(salesReturnItem.getInvoiceDiscountValueDerived() * itemReplica.getQuantityReturned());
                  }
                }
                salesReturnItemList.add(salesReturnItem);
              } else {
                unavailableProducts.add(SalesReturnFromPurReturnService.getProductFromDetail(main, getCompany(), itemReplica));
              }
            } else {
              unavailableProducts.add(SalesReturnFromPurReturnService.getProductFromDetail(main, getCompany(), itemReplica));
            }
          }
        }
        salesReturn.setSalesReturnItemList(salesReturnItemList);
        salesReturn.setProductList(unavailableProducts);
        Jsf.execute("parent.salesReturnfromPurchaseReturnPopupReturned()");
        Jsf.closePopup(main);

      }
    } catch (Throwable t) {
      main.rollback(t);
    } finally {
      main.close();
    }
  }

  private Company getCompany() {
    return UserRuntimeView.instance().getCompany();
  }
}
