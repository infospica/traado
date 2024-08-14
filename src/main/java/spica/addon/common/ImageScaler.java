/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spica.addon.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

/**
 *
 * @author sujesh
 */
public class ImageScaler {

  public static byte[] scaleAll(BufferedImage image, String imageName, String imageType) throws Exception {
    return scale(image, ImageSize.WXH_256X256, imageName, imageType);
  }

  public static byte[] scale(BufferedImage image, ImageSize size, String imageName, String imageType) throws Exception {
    byte[] imageInByte = null;
    ImageResolution res = size.toImageResolution();
    BufferedImage scaledImage = null;

    // pixel
    if (res.getPixel() > 0) {
      scaledImage = Scalr.resize(image, Method.QUALITY, Mode.AUTOMATIC, res.getPixel(), Scalr.OP_ANTIALIAS);
    } // width x height
    else if (res.getWidth() > 0 && res.getHeigth() > 0) {
      scaledImage = Scalr.resize(image, Method.QUALITY, Mode.AUTOMATIC, res.getWidth(), res.getHeigth(), Scalr.OP_ANTIALIAS);
    } // source
    else if (res.getWidth() < 0 && res.getHeigth() < 0 && res.getPixel() < 0) {
      scaledImage = image;
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(scaledImage, imageType, baos);
    baos.flush();
    imageInByte = baos.toByteArray();
    baos.close();

    return imageInByte;
  }
}
