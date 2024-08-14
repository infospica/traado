///*
// * @(#)ProfileCategoryView.java	1.0 Tue Jun 28 12:38:19 IST 2016 
// *
// * Copyright 2000-2016 Wawo Foundation. All rights reserved.
// * Use is subject to license terms.
// *
// */
//
//package spica.scm.view;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.Map;
//import javax.faces.view.ViewScoped;
//import javax.inject.Named;
//import javax.servlet.http.Part;
//import org.primefaces.event.SelectEvent;
//import org.primefaces.model.LazyDataModel;
//import org.primefaces.model.SortOrder;
//import wawo.app.config.ViewType;
//import wawo.app.config.ViewTypeAction;
//import wawo.app.config.ViewTypes;
//import wawo.app.faces.MainView;
//import wawo.app.faces.JsfIo;
//import wawo.entity.core.AppPage;
//import wawo.entity.util.StringUtil;
//
//import spica.scm.domain.ProfileCategory;
//import spica.scm.service.ProfileCategoryService;
//
///**
// * ProfileCategoryView
// * @author	Spirit 1.2
// * @version	1.0, Tue Jun 28 12:38:19 IST 2016 
// */
//
//@Named(value="profileCategoryView")
//@ViewScoped
//public class ProfileCategoryView implements Serializable{
//
//  private transient ProfileCategory profileCategory;	//Domain object/selected Domain.
//  private transient LazyDataModel<ProfileCategory> profileCategoryLazyModel; 	//For lazy loading datatable.
//  private transient ProfileCategory[] profileCategorySelected;	 //Selected Domain Array
//  /**
//   * Default Constructor.
//   */   
//  public ProfileCategoryView() {
//    super();
//  }
// 
//  /**
//   * Return ProfileCategory.
//   * @return ProfileCategory.
//   */  
//  public ProfileCategory getProfileCategory() {
//    if(profileCategory == null) {
//      profileCategory = new ProfileCategory();
//    }
//    return profileCategory;
//  }   
//  
//  /**
//   * Set ProfileCategory.
//   * @param profileCategory.
//   */   
//  public void setProfileCategory(ProfileCategory profileCategory) {
//    this.profileCategory = profileCategory;
//  }
// 
//  /**
//   * Change view of
//   * @param main
//   * @param viewType
//   * @return 
//   */
// public String switchProfileCategory(MainView main, String viewType) {
//   //this.main = main;
//   if (!StringUtil.isEmpty(viewType)) {
//      try {
//        main.setViewType(viewType);
//        if (ViewType.newform.toString().equals(viewType)) {
//          getProfileCategory().reset();
//        } else if (ViewType.editform.toString().equals(viewType)) {
//          setProfileCategory((ProfileCategory) ProfileCategoryService.selectByPk(main, getProfileCategory()));
//        } else if (ViewType.list.toString().equals(viewType)) {
//          loadProfileCategoryList(main);
//        }
//      } catch (Throwable t) {
//        main.rollback(t);
//      } finally{
//        main.close();
//      }
//    }
//    return null;
//  } 
//  
//  /**
//   * Create profileCategoryLazyModel.
//   * @param main
//   */
//  private void loadProfileCategoryList(final MainView main) {
//    if (profileCategoryLazyModel == null) {
//      profileCategoryLazyModel = new LazyDataModel<ProfileCategory>() {
//      private List<ProfileCategory> list;      
//      @Override
//      public List<ProfileCategory> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
//        try {
//          AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
//          list = ProfileCategoryService.listPaged(main);
//          main.commit(profileCategoryLazyModel, first, pageSize);
//        } catch (Throwable t) {
//          main.rollback(t, "error.list");
//          return null;
//        } finally{
//          main.close();
//        }
//        return list;
//      }
//      @Override
//      public Object getRowKey(ProfileCategory profileCategory) {
//        return profileCategory.getId();
//      }
//      @Override
//        public ProfileCategory getRowData(String rowKey) {
//          if (list != null) {
//            for (ProfileCategory obj : list) {
//              if (rowKey.equals(obj.getId().toString())) {
//                return obj;
//              }
//            }
//          }
//          return null;
//        }
//      };
//    }
//  }
//
//  private void uploadFiles() {
//    String SUB_FOLDER = "scm_profile_category/";	
//  }
//  
//  /**
//   * Insert or update.
//   * @param main
//   * @return the page to display.
//   */
//  public String saveProfileCategory(MainView main) {
//    return saveOrCloneProfileCategory(main, "save");
//  }
//
//  /**
//   * Duplicate or clone a record.
//   *
//   * @param main
//   * @return
//   */
//  public String cloneProfileCategory(MainView main) {
//    main.setViewType("newform");
//    return saveOrCloneProfileCategory(main, "clone"); 
//  }
//
//  /**
//   * Save or clone.
//   *
//   * @param main
//   * @param key
//   * @return
//   */
//  private String saveOrCloneProfileCategory(MainView main, String key) {
//    try {
//      uploadFiles(); //File upload
//      if (null != key) {
//        switch (key) {
//          case "save":
//            ProfileCategoryService.insertOrUpdate(main, getProfileCategory());
//            break;
//          case "clone":
//            ProfileCategoryService.clone(main, getProfileCategory());
//            break;
//        }
//        main.commit("success." + key);
//        main.setViewType(ViewTypes.editform); // Change to ViewTypes.list to navigate to list page
//      }
//    } catch (Throwable t) {
//      main.rollback(t, "error."+ key);
//    } finally {
//      main.close();
//    }
//    return null;
//  }
//
//  
//  /**
//   * Delete one or many ProfileCategory.
//   *
//   * @param main
//   * @return the page to display.
//   */
//  public String deleteProfileCategory(MainView main) {
//    try {
//      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(profileCategorySelected)) {
//        ProfileCategoryService.deleteByPkArray(main, getProfileCategorySelected()); //many record delete from list
//        main.commit("success.delete");
//        profileCategorySelected = null;
//      } else {
//        ProfileCategoryService.deleteByPk(main, getProfileCategory());  //individual record delete from list or edit form
//        main.commit("success.delete");
//        if ("editform".equals(main.getViewType())){
//          main.setViewType(ViewTypes.newform);
//        }
//      }
//    } catch (Throwable t) {
//      main.rollback(t, "error.delete");
//    } finally {
//      main.close();
//    }
//    return null;
//  }
//
//  /**
//   * Return LazyDataModel of ProfileCategory.
//   * @return
//   */
//  public LazyDataModel<ProfileCategory> getProfileCategoryLazyModel() {
//    return profileCategoryLazyModel;
//  }
//
// /**
//  * Return ProfileCategory[].
//  * @return 
//  */
//  public ProfileCategory[] getProfileCategorySelected() {
//    return profileCategorySelected;
//  }
//  
//  /**
//   * Set ProfileCategory[].
//   * @param profileCategorySelected 
//   */
//  public void setProfileCategorySelected(ProfileCategory[] profileCategorySelected) {
//    this.profileCategorySelected = profileCategorySelected;
//  }
// 
//
//
//}
