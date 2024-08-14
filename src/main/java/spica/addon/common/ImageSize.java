/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.addon.common;

/**
 *
 * @author sujesh
 */
public enum ImageSize {
  // width x height
  WXH_64X64("WXH_64X64"),
  WXH_128X128("WXH_128X128"),
  WXH_256X256("WXH_256X256"),
  WXH_512X512("WXH_512X512"),
  // pixel
  P_250("P_250"),
  P_500("P_500"),
  P_1000("P_1000"),
  // source
  SOURCE("SOURCE");

  private final String val;

  private ImageSize(String s) {
    val = s;
  }

  public String toString() {
    return this.val;
  }

  public ImageResolution toImageResolution() {
    ImageResolution res = null;

    switch (this) {
      case WXH_64X64:
        res = new ImageResolution(64, 64);
        break;

      case WXH_128X128:
        res = new ImageResolution(128, 128);
        break;

      case WXH_256X256:
        res = new ImageResolution(256, 256);
        break;

      case WXH_512X512:
        res = new ImageResolution(512, 512);
        break;

      case P_250:
        res = new ImageResolution(250);
        break;

      case P_500:
        res = new ImageResolution(500);
        break;

      case P_1000:
        res = new ImageResolution(1000);
        break;

      case SOURCE:
        res = new ImageResolution(-1, -1, -1);
        break;
    }

    return res;
  }
}
