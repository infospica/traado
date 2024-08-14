/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.reports.model;

import java.io.Serializable;

/**
 *
 * @author godson
 */
public class DocumentSummary implements Serializable {

  private String documentNature;
  private String slFrom;
  private String slTo;
  private Integer totalNo;
  private Integer cancelledNo;

  public String getDocumentNature() {
    return documentNature;
  }

  public void setDocumentNature(String documentNature) {
    this.documentNature = documentNature;
  }

  public String getSlFrom() {
    return slFrom;
  }

  public void setSlFrom(String slFrom) {
    this.slFrom = slFrom;
  }

  public String getSlTo() {
    return slTo;
  }

  public void setSlTo(String slTo) {
    this.slTo = slTo;
  }

  public Integer getTotalNo() {
    return totalNo;
  }

  public void setTotalNo(Integer totalNo) {
    this.totalNo = totalNo;
  }

  public Integer getCancelledNo() {
    return cancelledNo;
  }

  public void setCancelledNo(Integer cancelledNo) {
    this.cancelledNo = cancelledNo;
  }

}