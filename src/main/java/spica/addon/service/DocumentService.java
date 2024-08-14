/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.addon.service;

import spica.scm.domain.DocumentStore;
import spica.scm.domain.SalesInvoice;
import wawo.app.Main;
import wawo.app.common.AppService;
import wawo.entity.core.AppEm;

/**
 *
 * @author sujesh
 */
public abstract class DocumentService {

  public static DocumentStore selectByEntityId(Main main, Integer entityId, Integer entityType, Integer companyId) {
    return (DocumentStore) AppService.single(main, DocumentStore.class, "select * from scm_document_store WHERE company_id=? and entity_id = ? and entity_type = ?", new Object[]{companyId, entityId, entityType});
  }

  public static final void insertOrUpdate(Main main, DocumentStore documentStore) {
    if (null == documentStore.getId()) {
      AppService.insert(main, documentStore);
    } else {
      AppService.update(main, documentStore);
    }
  }

  public static final DocumentStore selectByUniqueId(AppEm em, String uniqueId) {
    return (DocumentStore) em.single(DocumentStore.class, "select * from scm_document_store where unique_id = ?", new Object[]{uniqueId});
  }

  public static String getPathSalesInvoice(SalesInvoice salesInvoice) {
    return getPath("salesinvoice", salesInvoice.getCompanyId().getId(), salesInvoice.getCustomerId().getCustomerName(), salesInvoice.getInvoiceNo());
  }

  private static String getPath(String folder, Integer id, String name1, String name2) {
    String p = id + "/" + folder + "/";
    if (name2 != null) {
      name1 += "_" + name2;
    }
    name1 = name1.contains("/") ? name1.replaceAll("/", "-") : name1;
    name1 = name1.contains(".") ? name1.replaceAll(".", "_") : name1;
    p += name1 + ".pdf";
    p = p.replaceAll(" ", "_");
    return p;
  }

}
