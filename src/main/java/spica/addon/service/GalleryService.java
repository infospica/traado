/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.addon.service;

import java.util.List;
import spica.sys.domain.Gallery;
import wawo.app.Main;
import wawo.app.common.AppService;

/**
 *
 * @author sujesh
 */
public abstract class GalleryService {

  public static List<Gallery> loadGalleryByEntity(Main main, Gallery gallery, Integer companyId) {
    return AppService.list(main, Gallery.class, " SELECT  * FROM sys_gallery WHERE entity_id = ? AND gallery_type = ? AND company_id = ? ", new Object[]{gallery.getEntityId(), gallery.getGalleryType().getId(), companyId});
  }

  public static void deleteImageByEntity(Main main, Integer entityId, Integer galleryType, Integer companyId) {
    AppService.deleteSql(main, Gallery.class, "DELETE FROM sys_gallery WHERE entity_id = ? AND gallery_type = ? AND company_id = ?", new Object[]{entityId, galleryType, companyId});
  }

  public static void deleteImageById(Main main, Integer id, Integer companyId) {
    AppService.deleteSql(main, Gallery.class, "DELETE FROM sys_gallery WHERE id = ? AND company_id = ?", new Object[]{id, companyId});
  }

  public static void deleteImageByPrimaryKey(Main main, Gallery gallery) {
    AppService.delete(main, Gallery.class, gallery);
  }

  public static void insertOrUpdate(Main main, Gallery gallery) {
    if (gallery.getId() == null) {
      AppService.insert(main, gallery);
    } else {
      AppService.update(main, gallery);
    }

  }

}
