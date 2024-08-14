/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.print;

import com.itextpdf.text.pdf.PdfPTable;
import java.util.List;
import java.util.Map;
import spica.scm.common.InvoiceGroup;
import spica.scm.domain.CompanySettings;
import spica.scm.domain.Manufacture;
import spica.scm.domain.SalesInvoice;

/**
 *
 * @author sujesh
 */
public interface PageFooter {

  public PdfPTable setItemQty(int totalItem, String netQuantity, Integer boxNo, Double weight, Double sDisc, Double pDisc, Double creditSettlement);

  public PdfPTable setTotalItem(int totalItem);

  public PdfPTable setInterStateTaxation(List<InvoiceGroup> invoiceGroupList);

  public PdfPTable setIntraStateTaxation(List<InvoiceGroup> invoiceGroupList);

  public PdfPTable setInterStateServiceTaxation(SalesInvoice salesInvoice);

  public PdfPTable setIntraStateServiceTaxation(SalesInvoice salesInvoice);

  public PdfPTable setSalesServicesInvoiceTaxation(List<InvoiceGroup> invoiceGroupList, boolean isIntraState);

  public PdfPTable setDebitCreidtNoteTaxation(List<InvoiceGroup> invoiceGroupList, Integer taxableInvoice, boolean intraState, Integer sezZone);

  public PdfPTable setAmountWords(double invoiceAmount, int showManufacturer, List<Manufacture> manufactureList, String bank, String AccNo, String ifsc);

  public PdfPTable setNote(String note);

  public PdfPTable setBillAmount(Map<String, String> billMap, boolean isSales);

  public PdfPTable setDeclaration(CompanySettings companySettings, String companyName, String districtName);

  public PdfPTable setWebMail(String email, String web);

}
