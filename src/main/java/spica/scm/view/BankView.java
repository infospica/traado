/*
 * @(#)BankView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

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

import spica.scm.domain.Bank;
import spica.scm.service.BankService;
import spica.sys.UserRuntimeView;

/**
 * BankView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "bankView")
@ViewScoped
public class BankView implements Serializable {

  private transient Bank bank;	//Domain object/selected Domain.
  private transient LazyDataModel<Bank> bankLazyModel; 	//For lazy loading datatable.
  private transient Bank[] bankSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public BankView() {
    super();
  }

  /**
   * Return Bank.
   *
   * @return Bank.
   */
  public Bank getBank() {
    if (bank == null) {
      bank = new Bank();
    }
    return bank;
  }

  /**
   * Set Bank.
   *
   * @param bank.
   */
  public void setBank(Bank bank) {
    this.bank = bank;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchBank(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getBank().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setBank((Bank) BankService.selectByPk(main, getBank()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadBankList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  /**
   * Create bankLazyModel.
   *
   * @param main
   */
  private void loadBankList(final MainView main) {
    if (bankLazyModel == null) {
      bankLazyModel = new LazyDataModel<Bank>() {
        private List<Bank> list;

        @Override
        public List<Bank> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = BankService.listPaged(main, UserRuntimeView.instance().getCompany());
            main.commit(bankLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Bank bank) {
          return bank.getId();
        }

        @Override
        public Bank getRowData(String rowKey) {
          if (list != null) {
            for (Bank obj : list) {
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
    String SUB_FOLDER = "scm_bank/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveBank(MainView main) {
    return saveOrCloneBank(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneBank(MainView main) {
    main.setViewType("newform");
    return saveOrCloneBank(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneBank(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            getBank().setCountryTd(UserRuntimeView.instance().getCompany().getCountryId());
            BankService.insertOrUpdate(main, getBank());
            break;
          case "clone":
            BankService.clone(main, getBank());
            break;
        }
        main.commit("success." + key);
        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
      }
    } catch (Throwable t) {
      main.rollback(t, "error" + key);
    } finally {
      main.close();
    }
    return null;
  }

  /**
   * Delete one or many Bank.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteBank(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(bankSelected)) {
        BankService.deleteByPkArray(main, getBankSelected()); //many record delete from list
        main.commit("success.delete");
        bankSelected = null;
      } else {
        BankService.deleteByPk(main, getBank());  //individual record delete from list or edit form
        main.commit("success.delete");
        if ("editform".equals(main.getViewType())) {
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
   * Return LazyDataModel of Bank.
   *
   * @return
   */
  public LazyDataModel<Bank> getBankLazyModel() {
    return bankLazyModel;
  }

  /**
   * Return Bank[].
   *
   * @return
   */
  public Bank[] getBankSelected() {
    return bankSelected;
  }

  /**
   * Set Bank[].
   *
   * @param bankSelected
   */
  public void setBankSelected(Bank[] bankSelected) {
    this.bankSelected = bankSelected;
  }

}
