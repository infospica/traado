/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.scm.print;

/**
 *
 * @author sujesh
 */
public class CellProperty {

  private String text;
  private String splitLabel[];
  private int alignment;
  private float width;
  private boolean topBorder;
  private boolean bottomBorder;
  private boolean leftBorder;
  private boolean rightBorder;
  private boolean labelSplit;
  private boolean portrait;
  private boolean landscape;

  public CellProperty() {
  }

  public CellProperty(String text, float width, boolean labelSplit, boolean portrait, boolean landscape) {
    this.width = width;
    this.labelSplit = labelSplit;
    this.portrait = portrait;
    this.landscape = landscape;
    if (labelSplit) {
      setSplitLabel(text.split("\\s+"));
      text = splitLabel[0] + "\n" + splitLabel[1];
    }
    this.text = text;
  }

  public CellProperty(String text, int alignment) {
    this.text = text;
    this.alignment = alignment;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public float getWidth() {
    return width;
  }

  public void setWidth(float width) {
    this.width = width;
  }

  public boolean isTopBorder() {
    return topBorder;
  }

  public void setTopBorder(boolean topBorder) {
    this.topBorder = topBorder;
  }

  public boolean isBottomBorder() {
    return bottomBorder;
  }

  public void setBottomBorder(boolean bottomBorder) {
    this.bottomBorder = bottomBorder;
  }

  public boolean isLeftBorder() {
    return leftBorder;
  }

  public void setLeftBorder(boolean leftBorder) {
    this.leftBorder = leftBorder;
  }

  public boolean isRightBorder() {
    return rightBorder;
  }

  public void setRightBorder(boolean rightBorder) {
    this.rightBorder = rightBorder;
  }

  public String[] getSplitLabel() {
    return splitLabel;
  }

  public void setSplitLabel(String[] splitLabel) {
    this.splitLabel = splitLabel;
  }

  public boolean isLabelSplit() {
    return labelSplit;
  }

  public void setLabelSplit(boolean labelSplit) {
    this.labelSplit = labelSplit;
  }

  public boolean isPortrait() {
    return portrait;
  }

  public void setPortrait(boolean portrait) {
    this.portrait = portrait;
  }

  public boolean isLandscape() {
    return landscape;
  }

  public void setLandscape(boolean landscape) {
    this.landscape = landscape;
  }

  public int getAlignment() {
    return alignment;
  }

  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }

}
