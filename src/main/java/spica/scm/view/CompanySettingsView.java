/*
 * @(#)CompanySettingsView.java	1.0 Mon Mar 11 16:27:36 IST 2019 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.codec.binary.Base64;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;
import spica.fin.service.CompanyFinancialYearService;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.CompanySettings;
import spica.scm.service.CompanySettingsService;
import spica.scm.domain.Company;
import spica.scm.domain.CompanyFinancialYear;
import spica.scm.service.PrefixTypeService;
import spica.sys.UserRuntimeView;
import spica.sys.domain.SmsProvider;
import wawo.app.faces.Jsf;

/**
 * CompanySettingsView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Mar 11 16:27:36 IST 2019
 */
@Named(value = "companySettingsView")
@ViewScoped
public class CompanySettingsView implements Serializable {

  private transient CompanySettings companySettings;	//Domain object/selected Domain.
  private transient LazyDataModel<CompanySettings> companySettingsLazyModel; 	//For lazy loading datatable.
  private transient CompanySettings[] companySettingsSelected;	 //Selected Domain Array
  private transient int tabIndex;
  private transient List<CompanyFinancialYear> financialYearList;
  private UploadedFile file;
  private List<SmsProvider> serviceProviderList;
  private transient boolean enableClosingofTrade;

  /**
   * Default Constructor.
   */
  public CompanySettingsView() {
    super();
  }

  /**
   * Return CompanySettings.
   *
   * @return CompanySettings.
   */
  public CompanySettings getCompanySettings() {
    if (companySettings == null) {
      companySettings = new CompanySettings();
    }
    return companySettings;
  }

  /**
   * Set CompanySettings.
   *
   * @param companySettings.
   */
  public void setCompanySettings(CompanySettings companySettings) {
    this.companySettings = companySettings;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchCompanySettings(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        setFinancialYearList(null);
        if (main.isNew() && !main.hasError()) {
          getCompanySettings().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setCompanySettings(CompanySettingsService.selectByPk(main, companySettings));
          setEnableClosingofTrade(companySettings.getEnableCloseOfTrade() == null ? false : (companySettings.getEnableCloseOfTrade().intValue() == 1 ? true : false));

          companySettings.setCompulsorySalesAgent(companySettings.getCompulsorySalesAgent() == null ? 0 : companySettings.getCompulsorySalesAgent());
          companySettings.setEnableCloseOfTrade(companySettings.getEnableCloseOfTrade() == null ? 0 : companySettings.getEnableCloseOfTrade());
          companySettings.setLimitReturnBySales(companySettings.getLimitReturnBySales() == null ? 0 : companySettings.getLimitReturnBySales());
          companySettings.setSplitSalesReturn(companySettings.getSplitSalesReturn() == null ? 0 : companySettings.getSplitSalesReturn());
          companySettings.setReturnSplitForGstFiling(companySettings.getReturnSplitForGstFiling() == null ? 0 : companySettings.getReturnSplitForGstFiling());
          companySettings.setPrintHorizontalLine(companySettings.getPrintHorizontalLine() == null ? 0 : companySettings.getPrintHorizontalLine());
          companySettings.setHeaderOnAllPages(companySettings.getHeaderOnAllPages() == null ? 0 : companySettings.getHeaderOnAllPages());
          companySettings.setInvoiceWise(companySettings.getInvoiceWise() == null ? 0 : companySettings.getInvoiceWise());
          companySettings.setProductGallery(companySettings.getProductGallery() == null ? 0 : companySettings.getProductGallery());
          companySettings.setServiceBusiness(companySettings.getServiceBusiness() == null ? 0 : companySettings.getServiceBusiness());
          companySettings.setSmsAllowed(companySettings.getSmsAllowed() == null ? 0 : companySettings.getSmsAllowed());
          companySettings.setSmsUsed(companySettings.getSmsUsed() == null ? 0 : companySettings.getSmsUsed());
          companySettings.setPurchaseDocPrefix(CompanySettingsService.selectPurchasePrefix(main, companySettings, PrefixTypeService.PRODUCT_ENTRY_INVOICE_PREFIX_ID, UserRuntimeView.instance().getCompany().getCurrentFinancialYear()));
          companySettings.setPurchaseReturnDocPrefix(CompanySettingsService.selectPurchasePrefix(main, companySettings, PrefixTypeService.PURCHASE_RETURN_INVOICE_PREFIX_ID, UserRuntimeView.instance().getCompany().getCurrentFinancialYear()));
          serviceProviderList = CompanySettingsService.selectServiceProviders(main);

        } else if (main.isList()) {
          loadCompanySettingsList(main);
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create companySettingsLazyModel.
   *
   * @param main
   */
  private void loadCompanySettingsList(final MainView main) {
    if (companySettingsLazyModel == null) {
      companySettingsLazyModel = new LazyDataModel<CompanySettings>() {
        private List<CompanySettings> list;

        @Override
        public List<CompanySettings> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = CompanySettingsService.listPaged(main);
            main.commit(companySettingsLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(CompanySettings companySettings) {
          return companySettings.getId();
        }

        @Override
        public CompanySettings getRowData(String rowKey) {
          if (list != null) {
            for (CompanySettings obj : list) {
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

  private void uploadFiles() {
    String SUB_FOLDER = "scm_company_settings/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveCompanySettings(MainView main) {
    if (main.isNew()) {
      getCompanySettings().setCompanyId(UserRuntimeView.instance().getCompany());
    }
    return saveOrCloneCompanySettings(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneCompanySettings(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneCompanySettings(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneCompanySettings(MainView main, String key) {
    try {
      // uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            CompanySettingsService.insertOrUpdate(main, getCompanySettings());
            break;
          case "clone":
            CompanySettingsService.clone(main, getCompanySettings());
            break;
        }
        if (getCompanySettings().getCompanyId().getId().intValue() == UserRuntimeView.instance().getCompany().getId()) //to prevent from multiple session usage
        {
          UserRuntimeView.instance().getCompany().setCompanySettings(getCompanySettings());
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error." + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many CompanySettings.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteCompanySettings(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(companySettingsSelected)) {
        CompanySettingsService.deleteByPkArray(main, getCompanySettingsSelected()); //many record delete from list
        main.commit("success.delete");
        companySettingsSelected = null;
      } else {
        CompanySettingsService.deleteByPk(main, getCompanySettings());  //individual record delete from list or edit form
        main.commit("success.delete");
        if (main.isEdit()) {
          main.setViewType(ViewTypes.newform);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Return LazyDataModel of CompanySettings.
   *
   * @return
   */
  public LazyDataModel<CompanySettings> getCompanySettingsLazyModel() {
    return companySettingsLazyModel;
  }

  /**
   * Return CompanySettings[].
   *
   * @return
   */
  public CompanySettings[] getCompanySettingsSelected() {
    return companySettingsSelected;
  }

  /**
   * Set CompanySettings[].
   *
   * @param companySettingsSelected
   */
  public void setCompanySettingsSelected(CompanySettings[] companySettingsSelected) {
    this.companySettingsSelected = companySettingsSelected;
  }

  /**
   * Company autocomplete filter.
   * <pre>
   * This method fetch based on query condition and on wawo.LookupIntConverter fetch the object for selection.
   * If your list is smaller in size and is cached you can use.
   * <o:converter list="#{ScmLookupView.companyAuto(null)}" converterId="omnifaces.ListConverter"  />
   * Note:- ScmLookupView.companyAuto(null) Should be implemented to return full values from cache if the filter is null
   * </pre>
   *
   * @param filter
   * @return
   */
  public List<Company> companyAuto(String filter) {
    return ScmLookupView.companyAuto(filter);
  }

  public int getTabIndex() {
    return tabIndex;
  }

  public void setTabIndex(int tabIndex) {
    this.tabIndex = tabIndex;
  }

  public void handleFileUpload(FileUploadEvent event) {
    MainView main = Jsf.getMain();
    try (InputStream input = event.getFile().getInputstream()) {
      byte[] imageBytes = new byte[(int) event.getFile().getSize()];
      input.read(imageBytes, 0, imageBytes.length);
      getCompanySettings().setFileContent(Base64.encodeBase64String(imageBytes));
      getCompanySettings().setFileName(event.getFile().getFileName());
      if (main.isEdit()) {
        CompanySettingsService.insertOrUpdate(main, getCompanySettings());
        main.commit("success.save");
      }
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void deleteFileUpload(MainView main) {
    try {
      getCompanySettings().setFileContent(null);
      getCompanySettings().setFileName(null);
      CompanySettingsService.insertOrUpdate(main, getCompanySettings());
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public boolean isEnableClosingofTrade() {
    return enableClosingofTrade;
  }

  public void setEnableClosingofTrade(boolean enableClosingofTrade) {
    this.enableClosingofTrade = enableClosingofTrade;
  }

  public List<SmsProvider> getServiceProviderList() {
    return serviceProviderList;
  }

  public List<CompanyFinancialYear> getFinancialYearList(MainView main) {
    try {
      if (financialYearList == null) {
        financialYearList = CompanySettingsService.getFinancialYearList(main, companySettings.getCompanyId());
      }
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
    return financialYearList;
  }

  public void setFinancialYearList(List<CompanyFinancialYear> financialYearList) {
    this.financialYearList = financialYearList;
  }

  public boolean isCurrentFinancialYear(Integer isCurrentfy) {
    if (isCurrentfy != null && isCurrentfy.intValue() == 1) {
      return true;
    } else {
      return false;
    }
  }

  public void currentFinancialYearChangeListener(MainView main, CompanyFinancialYear companyFinancialYear) {
    try {
      setFinancialYearList(null);
      CompanyFinancialYearService.updateCurrentFinancialYear(main, companyFinancialYear);
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }
}
