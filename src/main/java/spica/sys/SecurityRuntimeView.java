/*
 *  @(#)SecurityRuntimeView            30 Nov, 2016
 * 
 * Copyright 2000-2016 Wawo Foundation. All rights reserved.
 * Use is subject to license terms.
 * 
 */
package spica.sys;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import spica.sys.domain.User;
import wawo.app.config.AppConfig;

/**
 *
 * @author <a href=mailto:arun.vc@infospica.com>Arun VC</a>
 */
@Named(value = "userRole")
@ApplicationScoped
public class SecurityRuntimeView {

  public static boolean isCustomer() {
    User u = UserRuntimeView.instance().getAppUser();
    return (u != null && u.getUserProfileId() != null && u.getUserProfileId().getCustomerId() != null);
  }

  public static boolean isVendor() {
    User u = UserRuntimeView.instance().getAppUser();
    return (u != null && u.getUserProfileId() != null && u.getUserProfileId().getVendorId() != null);
  }

  public static boolean isTrasporter() {
    User u = UserRuntimeView.instance().getAppUser();
    return (u != null && u.getUserProfileId() != null && u.getUserProfileId().getTransporterId() != null);
  }

  public static boolean isCompany() {
    User u = UserRuntimeView.instance().getAppUser();
    return (u != null && u.getUserProfileId() != null && u.getUserProfileId().getCompanyId() != null && u.getUserProfileId().getVendorId() == null && u.getUserProfileId().getCustomerId() == null && u.getUserProfileId().getTransporterId() == null);
  }

  public static boolean isRoot() {
    User u = UserRuntimeView.instance().getAppUser();
    return u.getLogin().equals(AppConfig.rootUser);
  }
}
