/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.addon.common;

/**
 *
 * @author sujesh
 */
public class ImageResolution {

  private int width = 0;
  private int heigth = 0;
  private int pixel = 0;

  public ImageResolution(int pixel) {
    this.pixel = pixel;
  }

  public ImageResolution(int width, int heigth) {
    this.width = width;
    this.heigth = heigth;
  }

  public ImageResolution(int width, int heigth, int pixel) {
    this.width = width;
    this.heigth = heigth;
    this.pixel = pixel;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeigth() {
    return heigth;
  }

  public void setHeigth(int heigth) {
    this.heigth = heigth;
  }

  public int getPixel() {
    return pixel;
  }

  public void setPixel(int pixel) {
    this.pixel = pixel;
  }

  @Override
  public String toString() {
    return "ImageResolution [width=" + width + ", heigth=" + heigth + ", pixel=" + pixel + "]";
  }
}
