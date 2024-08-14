/*
 * @(#)RoleView.java	1.0 Wed Mar 30 11:28:07 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import spica.scm.service.RoleService;
import spica.sys.UserRuntimeService;
import spica.sys.UserRuntimeView;
import spica.sys.domain.Menu;
import spica.sys.domain.MenuPrivilage;
import spica.sys.domain.Role;
import spica.sys.view.PrivilegeChecked;
import spica.sys.view.SysUtil;
import wawo.app.config.ViewType;
import wawo.app.config.ViewTypes;
import wawo.app.faces.MainView;
import wawo.entity.core.AppPage;
import wawo.entity.util.StringUtil;
import wawo.entity.util.UniqueCheck;

/**
 * RoleView
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 11:28:07 IST 2016
 */
@Named(value = "roleView")
@ViewScoped
public class RoleView implements Serializable {

  private transient Role role;	//Domain object/selected Domain.
  private transient LazyDataModel<Role> roleLazyModel; 	//For lazy loading datatable.
  private transient Role[] roleSelected;	 //Selected Domain Array
  // private List<Menu> menuSelected; // 1 List to hold the relation selection
  private transient TreeNode menuNode;
  // private List<Menu> menus;
  private transient TreeNode[] selectedMenuNodes;
  private int activeIndex = 0; //tab active index, reset to 0 when new records
  private List<Menu> roleMenuPrivilegeList = null;

  /**
   * Default Constructor.
   */
  public RoleView() {
    super();
  }

  /**
   * Return Role.
   *
   * @return Role.
   */
  public Role getRole() {
    if (role == null) {
      role = new Role();
    }
    return role;
  }

  /**
   * Set Role.
   *
   * @param role.
   */
  public void setRole(Role role) {
    this.role = role;
  }

  public int getActiveIndex() {
    return activeIndex;
  }

  public void setActiveIndex(int activeIndex) {
    this.activeIndex = activeIndex;
  }

  /**
   * Change view of
   *
   * @param main
   * @param viewType
   * @return
   */
  public String switchRole(MainView main, String viewType) {
    //this.main = main;
    roleMenuPrivilegeList = null;
    if (!StringUtil.isEmpty(viewType)) {
      try {
        main.setViewType(viewType);
        if (ViewType.newform.toString().equals(viewType)) {
          getRole().reset();
          activeIndex = 0;
          selectedMenuNodes = null;
          loadMenuTree(main);
        } else if (ViewType.editform.toString().equals(viewType)) {
          setRole((Role) RoleService.selectByPk(main, getRole()));
          selectedMenuNodes = null;
          loadMenuTree(main);
          addExistingMenus(RoleService.listRelatedMenu(main, getRole()));
        } else if (ViewType.list.toString().equals(viewType)) {
          activeIndex = 0;
          this.menuNode = null;
          loadRoleList(main);
        }
      } catch (Throwable t) {
        main.rollback(t);
      } finally {
        main.close();
      }
    }
    return null;
  }

  private void loadMenuTree(MainView main) {
    if (this.menuNode == null) {
      menuNode = new DefaultTreeNode(new Menu(), null);
      this.addFullMenuTree(menuNode, UserRuntimeService.listMenuAndChildTree(main.em(), main.getAppUser(), UserRuntimeView.instance().getCompany().getId()));
    } else {
      SysUtil.resetTree(menuNode.getChildren());
    }
  }

  private void addFullMenuTree(TreeNode root, List<Menu> menus) {
    if (menus != null && !menus.isEmpty()) {
      for (Menu m : menus) {
        TreeNode n = new DefaultTreeNode(m, root);
        List<Menu> c = m.getParentIdMenu();
        if (c != null && !c.isEmpty()) {
          this.addFullMenuTree(n, c);
        }
      }
    }
  }

  private void addExistingMenus(List<Menu> menus) {
    int i = 0;
    if (menus != null && !menus.isEmpty()) {
      selectedMenuNodes = new DefaultTreeNode[menus.size()];
      menuNode.setExpanded(false);
      for (Menu m : menus) {
        traverse(menuNode.getChildren(), i, m.getId());
        i++;
      }
    }
  }

  private void traverse(List<TreeNode> nodes, int i, int id) {
    if (nodes != null && !nodes.isEmpty()) {
      for (TreeNode t : nodes) {
        if (((Menu) t.getData()).getId() == id) {
          t.setSelected(true);
          SysUtil.expandTree(t);
          selectedMenuNodes[i] = t;
          break;
        } else {
          traverse(t.getChildren(), i, id);
        }
      }
    }
  }

  /**
   * Create roleLazyModel.
   *
   * @param main
   */
  private void loadRoleList(final MainView main) {
    if (roleLazyModel == null) {
      roleLazyModel = new LazyDataModel<Role>() {
        private List<Role> list;

        @Override
        public List<Role> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
          try {
            AppPage.move(main.getPageData(), first, pageSize, sortField, sortOrder.name());
            list = RoleService.listPaged(main, UserRuntimeView.instance().getCompany().getId());
            main.commit(roleLazyModel, first, pageSize);
          } catch (Throwable t) {
            main.rollback(t, "error.list");
            return null;
          } finally {
            main.close();
          }
          return list;
        }

        @Override
        public Object getRowKey(Role role) {
          return role.getId();
        }

        @Override
        public Role getRowData(String rowKey) {
          if (list != null) {
            for (Role obj : list) {
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
    String SUB_FOLDER = "sec_role/";
  }

  /**
   * Insert or update.
   *
   * @param main
   * @return the page to display.
   */
  public String saveRole(MainView main) {
    return saveOrCloneRole(main, "save");
  }

  /**
   * Duplicate or clone a record.
   *
   * @param main
   * @return
   */
  public String cloneRole(MainView main) {
    main.setViewType("newform");
    return saveOrCloneRole(main, "clone");
  }

  /**
   * Save or clone.
   *
   * @param main
   * @param key
   * @return
   */
  private String saveOrCloneRole(MainView main, String key) {
    try {
      uploadFiles(); //File upload
      if (null != key) {
        List<Menu> menuSelected = null;
        if ("save".equals(key) || "clone".equals(key)) {
          menuSelected = new ArrayList<>();
          if (selectedMenuNodes != null) {
            try (UniqueCheck uc = new UniqueCheck()) {
              Menu menu;
              for (TreeNode m : selectedMenuNodes) {
                menu = (Menu) m.getData();
                if (menu.getParentId() != null && !uc.exist(menu.getParentId())) {
                  menuSelected.add(menu.getParentId());
                }
                menuSelected.add(menu);
              }
            }
          }
        }
        switch (key) {
          case "save":
            RoleService.insertOrUpdate(main, getRole(), menuSelected, UserRuntimeView.instance().getCompany());
            setActiveIndex(1);
            break;
          case "clone":
            RoleService.clone(main, getRole(), menuSelected, UserRuntimeView.instance().getCompany());
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
   * Delete one or many Role.
   *
   * @param main
   * @return the page to display.
   */
  public String deleteRole(MainView main) {
    try {
      if ("list".equals(main.getViewType()) && !StringUtil.isEmpty(roleSelected)) {
        RoleService.deleteByPkArray(main, getRoleSelected()); //many record delete from list
        main.commit("success.delete");
        roleSelected = null;
      } else {
        RoleService.deleteByPk(main, getRole());  //individual record delete from list or edit form
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
   * Return LazyDataModel of Role.
   *
   * @return
   */
  public LazyDataModel<Role> getRoleLazyModel() {
    return roleLazyModel;
  }

  /**
   * Return Role[].
   *
   * @return
   */
  public Role[] getRoleSelected() {
    return roleSelected;
  }

  /**
   * Set Role[].
   *
   * @param roleSelected
   */
  public void setRoleSelected(Role[] roleSelected) {
    this.roleSelected = roleSelected;
  }

  public TreeNode getMenuNode() {
    return menuNode;
  }

  public void setMenuNode(TreeNode menuNode) {
    this.menuNode = menuNode;
  }

  public TreeNode[] getSelectedMenuNodes() {
    return selectedMenuNodes;
  }

  public void setSelectedMenuNodes(TreeNode[] selectedMenuNodes) {
    this.selectedMenuNodes = selectedMenuNodes;
  }

  public void onNodeSelect(NodeSelectEvent event) {
  }

  public List<Menu> getRoleMenuPrivilageList(MainView main) {
    if (StringUtil.isEmpty(roleMenuPrivilegeList)) {
      try {
        if (RoleService.getMenuCount(main, getRole()) != 0) {
          roleMenuPrivilegeList = new ArrayList<>();
          roleMenuPrivilegeList = RoleService.getRoleMenuPrivilageList(main, getRole());
        }
      } catch (Throwable t) {
        main.rollback(t, "error.select");
      } finally {
        main.close();
      }
    }
    return roleMenuPrivilegeList;
  }

  public String savePrivilage(MainView main) {
    try {
      if (!roleMenuPrivilegeList.isEmpty()) {
        RoleService.savePrivilage(main, roleMenuPrivilegeList, getRole());
        main.commit("success.save");
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
    return null;
  }

  public void listenerEvent(AjaxBehaviorEvent event) {

  }

  public void selectAllPrentMenuPrivilage(ValueChangeEvent event) {
    Integer[] curr = (Integer[]) event.getNewValue();
    Integer[] old = (Integer[]) event.getOldValue();
    Integer parentId = null, prevParentId = null;
    if (!StringUtil.isEmpty(curr)) {
      parentId = curr[0];
    }
    if (!StringUtil.isEmpty(old)) {
      prevParentId = old[0];
    }
    boolean isCheck = parentId != null;
    if (prevParentId != null || parentId != null) {
      for (Menu m : roleMenuPrivilegeList) {
        if (isCheck) {
          if (m.getParentId().getId().intValue() == parentId) {
            resetPrev(m.getPrivChecked(), m.getMenuIdmenuPrivilage(), true);
          }
        } else {
          if (m.getParentId().getId().intValue() == prevParentId) {
            resetPrev(m.getPrivChecked(), m.getMenuIdmenuPrivilage(), false);
          }
        }
      }
    }
  }

  public void resetPrev(PrivilegeChecked p, List<MenuPrivilage> mpList, boolean flag) {
    int id;
    for (MenuPrivilage mp : mpList) {
      if (mp.getPrivilageId() != null) {
        id = mp.getPrivilageId().getId();
        if (RoleService.PRIVILEGE_ADD == id) {
          p.setAdd(flag);
        }
        if (RoleService.PRIVILEGE_MODIFY == id) {
          p.setModify(flag);
        }
        if (RoleService.PRIVILEGE_VIEW == id) {
          p.setView(flag);
        }
        if (RoleService.PRIVILEGE_DELETE == id) {
          p.setDelete(flag);
        }
        if (RoleService.PRIVILEGE_CONFIRM == id) {
          p.setConfirm(flag);
        }
        if (RoleService.PRIVILEGE_RENEW == id) {
          p.setRenew(flag);
        }
        if (RoleService.PRIVILEGE_PRINT == id) {
          p.setPrint(flag);
        }
        if (RoleService.PRIVILEGE_EXPORT == id) {
          p.setExport(flag);
        }
        if (RoleService.PRIVILEGE_CANCEL == id) {
          p.setCancel(flag);
        }
      }
    }
  }

}
