/*
 * @(#)BankAccountTypeView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */

package spica.fin.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.BankAccountType;
import spica.fin.service.BankAccountTypeService;

/**
 * BankAccountTypeView
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016 
 */

@Named(value="bankAccountTypeView")
@ViewScoped
public class BankAccountTypeView implements Serializable{

  private transient BankAccountType bankAccountType;	//Domain object/selected Domain.
  private transient LazyDataModel<BankAccountType> bankAccountTypeLazyModel; 	//For lazy loading datatable.
  private transient BankAccountType[] bankAccountTypeSelected;	 //Selected Domain Array
  /**
   * Default Constructor.
   */   
  public BankAccountTypeView() {
    super();
  }
 
  /**
   * Return BankAccountType.
   * @return BankAccountType.
   */  
  public BankAccountType getBankAccountType() {
    if(bankAccountType == null) {
      bankAccountType = new BankAccountType();
    }
    return bankAccountType;
  }   
  
  /**
   * Set BankAccountType.
   * @param bankAccountType.
   */   
  public void setBankAccountType(BankAccountType bankAccountType) {
    this.bankAccountType = bankAccountType;
  }
 
  /**
   * Change view of
   * @param main
   * @param viewType
   * @return 
   */
 public String switchBankAccountType(MainView main, String viewType) {
   //this.main = main;
   if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getBankAccountType().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setBankAccountType((BankAccountType) BankAccountTypeService.selectByPk(main, getBankAccountType()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadBankAccountTypeList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally{
        main.close();
      }
    }
    return null;
  } 
  
  /**
   * Create bankAccountTypeLazyModel.
   * @param main
   */
  private void loadBankAccountTypeList(final MainView main) {
    if (bankAccountTypeLazyModel == null) {
      bankAccountTypeLazyModel = new LazyDataModel<BankAccountType>() {
      private List<BankAccountType> list;      
      @Override
      public List<BankAccountType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        try {
          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
          list = BankAccountTypeService.listPaged(main);
          main.commit(bankAccountTypeLazyModel, first, pageSize);
        } catch (Throwable t) {
          main.rollback(t, "error.list");
          return null;
        } finally{
          main.close();
        }
        return list;
      }
      @Override
      public Object getRowKey(BankAccountType bankAccountType) {
        return bankAccountType.getId();
      }
      @Override
        public BankAccountType getRowData(String rowKey) {
          if (list != null) {
            for (BankAccountType obj : list) {
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
    String SUB_FOLDER = "scm_bank_account_type/";	
  }
  
  /**
   * Insert or update.
   * @param main
   * @return the page to display.
   */
  public String saveBankAccountType(MainView main) {
    return saveOrCloneBankAccountType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneBankAccountType(MainView main) {
    main.setViewType("newform");
    return saveOrCloneBankAccountType(main, "clone"); 
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneBankAccountType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            BankAccountTypeService.insertOrUpdate(main, getBankAccountType());
            break;
          case "clone":
            BankAccountTypeService.clone(main, getBankAccountType());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error"+ key);
    } finally {
      main.close();
    }
    return null;
  }

  
  /**
   * Delete one or many BankAccountType.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteBankAccountType(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(bankAccountTypeSelected)) {
        BankAccountTypeService.deleteByPkArray(main, getBankAccountTypeSelected()); //many record delete from list
        main.commit("success.delete");
        bankAccountTypeSelected = null;
      } else {
        BankAccountTypeService.deleteByPk(main, getBankAccountType());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())){
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
   * Return LazyDataModel of BankAccountType.
   * @return
   */
  public LazyDataModel<BankAccountType> getBankAccountTypeLazyModel() {
    return bankAccountTypeLazyModel;
  }

 /**
  * Return BankAccountType[].
  * @return 
  */
  public BankAccountType[] getBankAccountTypeSelected() {
    return bankAccountTypeSelected;
  }
  
  /**
   * Set BankAccountType[].
   * @param bankAccountTypeSelected 
   */
  public void setBankAccountTypeSelected(BankAccountType[] bankAccountTypeSelected) {
    this.bankAccountTypeSelected = bankAccountTypeSelected;
  }
}
