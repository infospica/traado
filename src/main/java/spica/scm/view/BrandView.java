/*
 * @(#)BrandView.java	1.0 Mon Aug 21 14:02:20 IST 2017 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;

import spica.scm.domain.Brand;
import spica.scm.service.BrandService;
import spica.sys.UserRuntimeView;
import wawo.app.faces.Jsf;

/**
 * BrandView
 *
 * @author	Spirit 1.2
 * @version	1.0, Mon Aug 21 14:02:20 IST 2017
 */
@Named(value = "brandView")
@ViewScoped
public class BrandView implements Serializable {

  private transient Brand brand;	//Domain object/selected Domain.
  private transient LazyDataModel<Brand> brandLazyModel; 	//For lazy loading datatable.
  private transient Brand[] brandSelected;	 //Selected Domain Array

  @PostConstruct
  public void init() {
    brand = (Brand) Jsf.popupParentValue(Brand.class);
    //getGenre().setId(Jsf.getParameterInt("id"));
  }

  /**
   * Default Constructor.
   */
  public BrandView() {
    super();
  }

  /**
   * Return Brand.
   *
   * @return Brand.
   */
  public Brand getBrand() {
    if (brand == null) {
      brand = new Brand();
    }
    return brand;
  }

  /**
   * Set Brand.
   *
   * @param brand.
   */
  public void setBrand(Brand brand) {
    this.brand = brand;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchBrand(MainView main, String viewType) {
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (main.isNew() && !main.hasError()) {
          if (UserRuntimeView.instance().getCompany() == null) {
            main.error("company.required");
            main.setViewType(ViewTypes.list);
            return null;
          }
          getBrand().reset();
        } else if (main.isEdit() && !main.hasError()) {
          setBrand((Brand) BrandService.selectByPk(main, getBrand()));
        } else if (main.isList()) {
          loadBrandList(main);
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
   * Create brandLazyModel.
   *
   * @param main
   */
  private void loadBrandList(final MainView main) {
    if (brandLazyModel == null) {
      brandLazyModel = new LazyDataModel<Brand>() {
        private List<Brand> list;

        @Override
        public List<Brand> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            if (UserRuntimeView.instance().getCompany() != null) {
              AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
              list = BrandService.listPagedByCompany(main, UserRuntimeView.instance().getCompany());
              main.commit(brandLazyModel, first, pageSize);
            }
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Brand brand) {
          return brand.getId();
        }

        @Override
        public Brand getRowData(String rowKey) {
          if (list != null) {
            for (Brand obj : list) {
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
    String SUB_FOLDER = "scm_brand/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveBrand(MainView main) {
    return saveOrCloneBrand(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneBrand(MainView main) {
    main.setViewType(ViewTypes.newform);
    return saveOrCloneBrand(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneBrand(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        switch (key) {
          case "save":
            if (UserRuntimeView.instance().getCompany() != null) {
              getBrand().setCompanyId(UserRuntimeView.instance().getCompany());
            }
            getBrand().setCode(getBrand().getCode().toUpperCase());
            BrandService.insertOrUpdate(main, getBrand());
            break;
          case "clone":
            BrandService.clone(main, getBrand());
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
   * Delete one or many Brand.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteBrand(MainView main) {
    try {
      if (main.isList() && !StringUtil.isEmpty(brandSelected)) {
        BrandService.deleteByPkArray(main, getBrandSelected()); //many record delete from list
        main.commit("success.delete");
        brandSelected = null;
      } else {
        BrandService.deleteByPk(main, getBrand());  //individual record delete from list or edit form
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
   * Return LazyDataModel of Brand.
   *
   * @return
   */
  public LazyDataModel<Brand> getBrandLazyModel() {
    return brandLazyModel;
  }

  /**
   * Return Brand[].
   *
   * @return
   */
  public Brand[] getBrandSelected() {
    return brandSelected;
  }

  /**
   * Set Brand[].
   *
   * @param brandSelected
   */
  public void setBrandSelected(Brand[] brandSelected) {
    this.brandSelected = brandSelected;
  }
}
