/*
 * @(#)AddressTypeView.java	1.0 Wed Mar 30 12:35:25 IST 2016 
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

import spica.scm.domain.AddressType;
import spica.scm.service.AddressTypeService;

/**
 * AddressTypeView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 12:35:25 IST 2016
 */
@Named(value = "addressTypeView")
@ViewScoped
public class AddressTypeView implements Serializable {

  private transient AddressType addressType;	//Domain object/selected Domain.
  private transient LazyDataModel<AddressType> addressTypeLazyModel; 	//For lazy loading datatable.
  private transient AddressType[] addressTypeSelected;	 //Selected Domain Array

  /**
   * Default Constructor.
   */
  public AddressTypeView() {
    super();
  }

  /**
   * Return AddressType.
   *
   * @return AddressType.
   */
  public AddressType getAddressType() {
    if (addressType == null) {
      addressType = new AddressType();
    }
    return addressType;
  }

  /**
   * Set AddressType.
   *
   * @param addressType.
   */
  public void setAddressType(AddressType addressType) {
    this.addressType = addressType;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchAddressType(MainView main, String viewType) {
    //this.main = main;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType) && !main.hasError()) {
          getAddressType().reset();
        } else if (ViewType.editform.toString().equals(viewType) && !main.hasError()) {
          setAddressType((AddressType) AddressTypeService.selectByPk(main, getAddressType()));
        } else if (ViewType.list.toString().equals(viewType)) {
          loadAddressTypeList(main);
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
   * Create addressTypeLazyModel.
   *
   * @param main
   */
  private void loadAddressTypeList(final MainView main) {
    if (addressTypeLazyModel == null) {
      addressTypeLazyModel = new LazyDataModel<AddressType>() {
        private List<AddressType> list;

        @Override
        public List<AddressType> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = AddressTypeService.listPaged(main);
            main.commit(addressTypeLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(AddressType addressType) {
          return addressType.getId();
        }

        @Override
        public AddressType getRowData(String rowKey) {
          if (list != null) {
            for (AddressType obj : list) {
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
    String SUB_FOLDER = "scm_address_type/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveAddressType(MainView main) {
    return saveOrCloneAddressType(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneAddressType(MainView main) {
    main.setViewType("newform");
    return saveOrCloneAddressType(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneAddressType(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            AddressTypeService.insertOrUpdate(main, getAddressType());
            break;
          case "clone":
            AddressTypeService.clone(main, getAddressType());
            break;
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
   * Delete one or many AddressType.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteAddressType(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(addressTypeSelected)) {
        AddressTypeService.deleteByPkArray(main, getAddressTypeSelected()); //many record delete from list
        main.commit("success.delete");
        addressTypeSelected = null;
      } else {
        AddressTypeService.deleteByPk(main, getAddressType());  //individual record delete from list or edit form
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
   * Return LazyDataModel of AddressType.
   *
   * @return
   */
  public LazyDataModel<AddressType> getAddressTypeLazyModel() {
    return addressTypeLazyModel;
  }

  /**
   * Return AddressType[].
   *
   * @return
   */
  public AddressType[] getAddressTypeSelected() {
    return addressTypeSelected;
  }

  /**
   * Set AddressType[].
   *
   * @param addressTypeSelected
   */
  public void setAddressTypeSelected(AddressType[] addressTypeSelected) {
    this.addressTypeSelected = addressTypeSelected;
  }
}
