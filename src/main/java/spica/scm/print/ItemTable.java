/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.print;

import java.io.Serializable;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author sujesh
 */
public class ItemTable implements Serializable {

  private boolean gstFlag;
  private boolean portrait;
  private float columnWidth[];
  private List<CellProperty> cellProperties;
  private List<CellProperty> data;
  private Map<String, Boolean> cellMap;// = new LinkedHashMap<>();

  public ItemTable() {
    cellProperties = new LinkedList<>();
    data = new LinkedList<>();
  }

  public List<CellProperty> getCellProperties() {
    return cellProperties;
  }

  public void setCellProperties(List<CellProperty> cellProperties) {
    this.cellProperties = cellProperties;
    generateColumnWidth();
  }

  public void generateColumnWidth() {
    List<Float> floatList = new LinkedList<>();
    cellMap = new LinkedHashMap<>();
    boolean flag = false;
    for (CellProperty cp : getCellProperties()) {
      if (cp.isLandscape() || cp.isPortrait()) {
        if (cp.isPortrait() == portrait || (cp.isLandscape() == !portrait && cp.isLandscape())) {
          floatList.add(cp.getWidth());
          flag = true;
        }
      }
      cellMap.put(cp.getText().replaceAll("\\s+", ""), flag);
      flag = false;
    }
    columnWidth = ArrayUtils.toPrimitive(floatList.toArray(new Float[floatList.size()]));
  }

  public List<CellProperty> getData() {
    return data;
  }

  public void setData(List<CellProperty> data) {
    this.data = data;
  }

  public boolean isGstFlag() {
    return gstFlag;
  }

  public void setGstFlag(boolean gstFlag) {
    this.gstFlag = gstFlag;
  }

  public float[] getColumnWidth() {
    return columnWidth;
  }

  public boolean isPortrait() {
    return portrait;
  }

  public void setPortrait(boolean portrait) {
    this.portrait = portrait;
  }

  public Map<String, Boolean> getCellMap() {
    return cellMap;
  }

}
