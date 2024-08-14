/*
 * @(#)RoleService.java	1.0 Wed Mar 30 11:28:07 IST 2016 
 *
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 *
 */
package spica.scm.service;

import java.util.List;
import spica.scm.domain.Company;
import spica.scm.domain.RoleMenuPrivilege;
import spica.sys.domain.Menu;
import spica.sys.domain.MenuPrivilage;
import spica.sys.domain.Privilage;
import spica.sys.domain.Role;
import spica.sys.domain.RoleMenu;
import spica.sys.service.RoleMenuService;
import spica.sys.view.PrivilegeChecked;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.app.faces.MainView;
import wawo.entity.core.AppDb;
import wawo.entity.core.SqlPage;
import wawo.entity.util.StringUtil;

/**
 * RoleService
 *
 * @author	Spirit 1.2
 * @version	1.0, Wed Mar 30 11:28:07 IST 2016
 */
public abstract class RoleService {

  public static final int PRIVILEGE_ADD = 1;
  public static final int PRIVILEGE_MODIFY = 2;
  public static final int PRIVILEGE_VIEW = 3;
  public static final int PRIVILEGE_DELETE = 4;
  public static final int PRIVILEGE_CONFIRM = 5;
  public static final int PRIVILEGE_RENEW = 6;
  public static final int PRIVILEGE_PRINT = 7;
  public static final int PRIVILEGE_EXPORT = 8;
  public static final int PRIVILEGE_CANCEL = 9;
  public static final int PRIVILEGE_MODIFY_TAX = 10;
  public static final int USER_ROLE = 3;
  public static final int PRIVILEGE_RESET_DREFT = 11;
  public static final int PRIVILEGE_EDIT_FIELD = 12;
  public static final Role ADMIN = new Role(1);
  public static final Role DIRECTOR = new Role(13);

  /**
   * Role paginated query.
   *
   * @param main
   * @return SqlPage
   */
  private static SqlPage getRoleSqlPaged(Main main) {
    SqlPage sql = AppService.sqlPage("sec_role", Role.class, main);
    sql.main("select sec_role.id,sec_role.title,sec_role.role_type,sec_role.created_by,sec_role.modified_by,sec_role.created_at,sec_role.modified_at,sec_role.is_admin from sec_role sec_role"); //Main query
    sql.count("select count(sec_role.id) from sec_role sec_role"); //Count query
    sql.join(""); //Join Query
    sql.orderBy("sec_role.role_type");

    sql.string(new String[]{"sec_role.title", "sec_role.created_by", "sec_role.modified_by", "sec_role.role_type"}); //String search or sort fields
    sql.number(new String[]{"sec_role.id", "sec_role.role_type"}); //Number search or sort fields
    sql.date(new String[]{"sec_role.created_at", "sec_role.modified_at"});  //Date search or sort fields
    return sql;
  }

  /**
   * Return List of Role.
   *
   * @param main
   * @return List of Role
   */
  public static final List<Role> listPaged(Main main, Integer companyId) {
    SqlPage sql = getRoleSqlPaged(main);
    sql.clearParam();
    // if (!main.getAppUser().isRoot()) {
    sql.cond("where company_id=? or company_id is null");
    sql.param(companyId);
    // }
    return AppService.listPagedJpa(main, sql);
  }

  /**
   * Return list of Role based on condition
   *
   * @param main
   * @return List<Menu>
   */
  public static final List<Menu> listRelatedMenu(Main main, Role role) {
    return AppService.list(main, Menu.class, "select id,title from sec_menu where id IN (select sec_role_menu.menu_id from sec_role_menu where sec_role_menu.role_id=?)", new Object[]{role.getId()});
  }

  /**
   * Select Role by key.
   *
   * @param main
   * @param role
   * @return Role
   */
  public static final Role selectByPk(Main main, Role role) {
    return (Role) AppService.find(main, Role.class, role.getId());
  }

  public static void insertArray(Main main, List<Menu> menuSelected, Role role) {
    Privilage p = new Privilage();
    p.setId(3);
    if (menuSelected != null) {
      RoleMenu bg;
      for (Menu menu : menuSelected) {  //Reinserting
        bg = new RoleMenu();
        if (menu.getParentId() != null) {
          bg.setPrivilageId(p);
        }
        bg.setRoleId(role);
        bg.setMenuId(menu);
        if (!AppService.exist(main, "select id from sec_role_menu where role_id=? and menu_id=?", new Object[]{role.getId(), menu.getId()})) {
          RoleMenuService.insert(main, bg);
        }

      }
    }
  }

  /**
   * Insert Role.
   *
   * @param main
   * @param role
   */
  public static final void insert(Main main, Role role, Company company) {
    role.setCompanyId(company);
    AppService.insert(main, role);

  }

  /**
   * Update Role by key.
   *
   * @param main
   * @param role
   * @return Role
   */
  public static final Role updateByPk(Main main, Role role) {
    return (Role) AppService.update(main, role);
  }

  /**
   * Insert or update Role
   *
   * @param main
   * @param Role
   */
  public static void insertOrUpdate(Main main, Role role, List<Menu> menuSelected, Company company) {
    if (role.getId() == null) {
      insert(main, role, company);
    } else {

      if (!StringUtil.isEmpty(menuSelected)) {
        StringBuilder p = new StringBuilder("");
        for (Menu menu : menuSelected) {
          if (p.length() == 0) {
            p.append(menu.getId());
          } else {
            p.append(",").append(menu.getId());
          }
        }
        AppService.deleteSql(main, RoleMenu.class, "delete from sec_role_menu where role_id=? and menu_id not in (select * from unnest(array[" + p + "]))", new Object[]{role.getId()}); //Deleting based on book id
      }
      updateByPk(main, role);
    }
    if (menuSelected != null) {
      insertArray(main, menuSelected, role);   //Inserting all the relation records.
    }
  }

  /**
   * Clone and existing object
   *
   * @param main
   * @param role
   */
  public static void clone(Main main, Role role, List<Menu> menuSelected, Company company) {
    role.setId(null); //Set to null for insert
    insertOrUpdate(main, role, menuSelected, company);
  }

  /**
   * Delete Role.
   *
   * @param main
   * @param role
   */
  public static final void deleteByPk(Main main, Role role) {
    AppService.deleteSql(main, RoleMenu.class, "delete from sec_role_menu sec_role_menu where sec_role_menu.role_id=?", new Object[]{role.getId()});
//    AppService.deleteSql(main, UserRole.class, "delete from sec_user_role sec_user_role where sec_user_role.role_id=?", new Object[]{role.getId()});
    AppService.delete(main, Role.class, role.getId());
  }

  /**
   * Delete Array of Role.
   *
   * @param main
   * @param role
   */
  public static final void deleteByPkArray(Main main, Role[] role) {
    for (Role e : role) {
      deleteByPk(main, e);
    }
  }

  public static Long getMenuCount(Main main, Role role) {
    if (role.getId() == null) {
      return new Long(0);
    }
    return main.em().count("select count(id) from sec_role_menu where sec_role_menu.role_id = ?", new Object[]{role.getId()});
  }

  public static List<Menu> getRoleMenuPrivilageList(MainView main, Role role) {
    String sql = "select * from sec_menu where id in(select menu_id from sec_role_menu where role_id=?) and parent_id is not null order by parent_id";
    List<Menu> list = AppService.list(main, Menu.class, sql, new Object[]{role.getId()});
    int id;
    for (Menu m : list) {
      for (RoleMenu rm : m.getMenuIdRoleMenu()) {
        if (rm.getPrivilageId() != null) {
          id = rm.getPrivilageId().getId();
          privilegeChecked(main, id, m, role);
        }
      }
      for (MenuPrivilage mp : m.getMenuIdmenuPrivilage()) {
        if (mp.getPrivilageId() != null) {
          id = mp.getPrivilageId().getId();
          privilegeCheckedRndered(id, m.getPrivCheckedRendered());
        }
      }
    }
    return list;
  }

  public static void savePrivilage(Main main, List<Menu> roleMenuPrivilegeList, Role role) {
    AppService.deleteSql(main, RoleMenuPrivilege.class, "delete from sec_role_menu where role_id=? and privilage_id is not null", new Object[]{role.getId()});
    for (Menu m : roleMenuPrivilegeList) {
      if (m.getPrivChecked().getAdd()) {
        setPrivilege(main, role.getId(), m, PRIVILEGE_ADD);
      }
      if (m.getPrivChecked().getModify()) {
        setPrivilege(main, role.getId(), m, PRIVILEGE_MODIFY);
      }
      if (m.getPrivChecked().getView()) {
        setPrivilege(main, role.getId(), m, PRIVILEGE_VIEW);
      }
      if (m.getPrivChecked().getDelete()) {
        setPrivilege(main, role.getId(), m, PRIVILEGE_DELETE);
      }
      if (m.getPrivChecked().getConfirm()) {
        setPrivilege(main, role.getId(), m, PRIVILEGE_CONFIRM);
      }
      if (m.getPrivChecked().getRenew()) {
        setPrivilege(main, role.getId(), m, PRIVILEGE_RENEW);
      }
      if (m.getPrivChecked().getPrint()) {
        setPrivilege(main, role.getId(), m, PRIVILEGE_PRINT);
      }
      if (m.getPrivChecked().getExport()) {
        setPrivilege(main, role.getId(), m, PRIVILEGE_EXPORT);
      }
      if (m.getPrivChecked().getCancel()) {
        setPrivilege(main, role.getId(), m, PRIVILEGE_CANCEL);
      }
      if (m.getPrivChecked().getResetToDraft()) {
        setPrivilege(main, role.getId(), m, PRIVILEGE_RESET_DREFT);
      }
      if (m.getPrivChecked().getEditField()) {
        setPrivilege(main, role.getId(), m, PRIVILEGE_EDIT_FIELD);
      }
    }
  }

  private static void setPrivilege(Main main, int roleId, Menu menu, int privId) {
    String sql = "INSERT INTO sec_role_menu (role_id,menu_id,privilage_id,id) VALUES (?,?,?,?)";
    main.getParamData().clearParam();
    main.param(roleId);
    main.param(menu.getId());
    main.param(privId);
    AppService.insertSql(main, RoleMenu.class, "sec_role_menu", sql, false);
  }

  public static void privilegeChecked(Main main, int id, Menu pc, Role role) {
    String sql = "select * from sec_role_menu where role_id=? and privilage_id = ? and menu_id=?";
    List<RoleMenu> list = AppService.list(main, RoleMenu.class, sql, new Object[]{role.getId(), id, pc.getId()});
    boolean checked = false;
    if (!list.isEmpty()) {
      checked = true;
    }
    privilegeChecked(id, pc.getPrivChecked(), checked);
  }

  public static void privilegeChecked(int id, PrivilegeChecked pc, boolean isChecked) {

    boolean checked = isChecked;

    if (PRIVILEGE_ADD == id) {
      pc.setAdd(checked);
    } else if (PRIVILEGE_MODIFY == id) {
      pc.setModify(checked);
    } else if (PRIVILEGE_VIEW == id) {
      pc.setView(checked);
    } else if (PRIVILEGE_DELETE == id) {
      pc.setDelete(checked);
    } else if (PRIVILEGE_CONFIRM == id) {
      pc.setConfirm(checked);
    } else if (PRIVILEGE_RENEW == id) {
      pc.setRenew(checked);
    } else if (PRIVILEGE_PRINT == id) {
      pc.setPrint(checked);
    } else if (PRIVILEGE_EXPORT == id) {
      pc.setExport(checked);
    } else if (PRIVILEGE_CANCEL == id) {
      pc.setCancel(checked);
    } else if (PRIVILEGE_RESET_DREFT == id) {
      pc.setResetToDraft(checked);
    } else if (PRIVILEGE_EDIT_FIELD == id) {
      pc.setEditField(checked);
    }
  }

  public static void privilegeCheckedRndered(int id, PrivilegeChecked pcr) {
    if (PRIVILEGE_ADD == id) {
      pcr.setAdd(Boolean.TRUE);
    } else if (PRIVILEGE_MODIFY == id) {
      pcr.setModify(Boolean.TRUE);
    } else if (PRIVILEGE_MODIFY_TAX == id) {
      pcr.setModifyTax(Boolean.TRUE);
    } else if (PRIVILEGE_VIEW == id) {
      pcr.setView(Boolean.TRUE);
    } else if (PRIVILEGE_DELETE == id) {
      pcr.setDelete(Boolean.TRUE);
    } else if (PRIVILEGE_CONFIRM == id) {
      pcr.setConfirm(Boolean.TRUE);
    } else if (PRIVILEGE_RENEW == id) {
      pcr.setRenew(Boolean.TRUE);
    } else if (PRIVILEGE_PRINT == id) {
      pcr.setPrint(Boolean.TRUE);
    } else if (PRIVILEGE_EXPORT == id) {
      pcr.setExport(Boolean.TRUE);
    } else if (PRIVILEGE_CANCEL == id) {
      pcr.setCancel(Boolean.TRUE);
    } else if (PRIVILEGE_RESET_DREFT == id) {
      pcr.setResetToDraft(Boolean.TRUE);
    } else if (PRIVILEGE_EDIT_FIELD == id) {
      pcr.setEditField(Boolean.TRUE);
    }
  }

  public static PrivilegeChecked getPermission(Main main, Integer menuId, Integer userId) {
    PrivilegeChecked pc = new PrivilegeChecked();
    boolean b = AppService.exist(main, "select privilage_id from sec_role_menu where menu_id=? and role_id in(select role_id from sec_user_role where user_id=?) and privilage_id is not null limit 1", new Object[]{menuId, userId});
    List<String> prvId = AppDb.listFirst(main.dbConnector(), "select privilage_id from sec_role_menu where menu_id=? and role_id in(select role_id from sec_user_role where user_id=?) and privilage_id is not null", new Object[]{menuId, userId});
    for (String i : prvId) {
      privilegeChecked(Integer.valueOf(i), pc, true);
    }
    return pc;
  }
}
