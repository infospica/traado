/*
 * 
 * Copyright 2015-2024 Infospica. All rights reserved.
 * Use is subject to license terms.
 */
package spica.addon.view;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.inject.Named;
import org.apache.commons.codec.binary.Base64;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.CroppedImage;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.shaded.commons.io.FilenameUtils;
import spica.addon.common.ImageScaler;
import spica.sys.domain.Gallery;
import spica.addon.service.GalleryService;
import spica.scm.domain.Product;
import spica.sys.SystemConstants;
import spica.sys.UserRuntimeView;
import spica.sys.domain.GalleryType;
import wawo.app.config.ViewType;
import wawo.app.faces.Jsf;
import wawo.app.faces.MainView;

/**
 *
 * @author sujesh
 */
@Named(value = "galleryView")
@ViewScoped
public class GalleryView implements Serializable {

  private Gallery gallery;
  private Gallery selectedGallery;
  private List<Gallery> galleries;
  private CroppedImage croppedImage;
  private String newImageName;
  // private String path;
  private StreamedContent image;

  @PostConstruct
  public void init() {
    gallery = new Gallery();
    galleries = null;
    Product product = (Product) Jsf.popupParentValue(Product.class);
    switchToGallery(product);
    Jsf.getMain().setViewType(ViewType.list.toString());
  }

  public void handleFileUpload(FileUploadEvent event) {
    MainView main = Jsf.getMain();
    try (InputStream input = event.getFile().getInputstream()) {
      uploadImage(main, input, event.getFile().getFileName(), event.getFile().getSize());
      loadImages(main);
    } catch (Throwable t) {
      main.rollback(t, "error.save");
    } finally {
      main.close();
    }
  }

  public void uploadImage(MainView main, InputStream inputStream, String imageName, long size) {
    try {
      resizeImage(inputStream, imageName, size);
      getGallery().setId(null);
      getGallery().setFileName(imageName);
      GalleryService.insertOrUpdate(main, getGallery());
      galleries = null;
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  private void resizeImage(InputStream inputStream, String imageName, long size) throws Exception {
    BufferedImage image = ImageIO.read(inputStream); // load image 
    byte[] imageBytes = ImageScaler.scaleAll(image, getGallery().getEntityId() + "_" + new Date().getTime(), FilenameUtils.getExtension(imageName));
    getGallery().setContent(Base64.encodeBase64String(imageBytes));
  }

  public void loadImages(MainView main) {
    try {
      if (galleries == null) {
        galleries = GalleryService.loadGalleryByEntity(main, getGallery(), UserRuntimeView.instance().getCompany().getId());
        for (Gallery gallery : galleries) {
          StreamedContent imageContnet = new DefaultStreamedContent(new ByteArrayInputStream(Base64.decodeBase64(gallery.getContent().getBytes())), "image/png");
          gallery.setStreamedContent(imageContnet);
        }
      }
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void deleteImage(MainView main, Gallery gallery) {
    try {
      GalleryService.deleteImageById(main, gallery.getId(), gallery.getCompanyId().getId());//deleteImageByEntity(main, entityId, entityType, UserRuntimeView.instance().getCompany().getId());
      reset();
      main.commit("success.delete");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public void updateImage(MainView main) {
    try {
      GalleryService.insertOrUpdate(main, gallery);
      main.commit("success.save");
    } catch (Throwable t) {
      main.rollback(t, "error.delete");
    } finally {
      main.close();
    }
  }

  public void reset() {
    galleries = null;
    MainView main = Jsf.getMain();
    try {
      loadImages(main);
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public Gallery getGallery() {
    return gallery;
  }

  public List<Gallery> getGalleries() {
    return galleries;
  }

  private void switchToGallery(Product product) {
//    if (obj instanceof Product) {
//      Product product = (Product) obj;
    gallery = new Gallery();
    gallery.setEntityId(product.getId());
    gallery.setGalleryType(new GalleryType(SystemConstants.GALLERY_PRODUCT_TYPE));
//    }
    gallery.setCompanyId(UserRuntimeView.instance().getCompany());
  }

  public void crop() {
    if (croppedImage == null) {
      return;
    }
    MainView main = Jsf.getMain();
    try {
      getGallery().setContent(Base64.encodeBase64String(croppedImage.getBytes()));
      GalleryService.insertOrUpdate(main, getGallery());
      reset();
    } catch (Throwable t) {
      main.rollback(t, "error.select");
    } finally {
      main.close();
    }
  }

  public void setNewImageName(String newImageName) {
    this.newImageName = newImageName;
  }

//  private String getRandomImageName() {
//    int i = (int) (Math.random() * 100000);
//
//    return String.valueOf(i);
//  }
  public enum Type {
    PRODUCT(1);
    private final int typeCode;

    Type(int typeCode) {
      this.typeCode = typeCode;
    }

    public int getTypeCode() {
      return this.typeCode;
    }
  }

  public Gallery getSelectedGallery() {
    return selectedGallery;
  }

  public void setSelectedGallery(Gallery selectedGallery) {
    this.selectedGallery = selectedGallery;
    image = new DefaultStreamedContent(new ByteArrayInputStream(selectedGallery.getContent().getBytes()));
  }

  public CroppedImage getCroppedImage() {
    return croppedImage;
  }

  public void setCroppedImage(CroppedImage croppedImage) {
    this.croppedImage = croppedImage;
  }

  public String getNewImageName() {
    return newImageName;
  }

//  public String getPath() {
//    String filePath = null;
//    String file = "/images/abc.jpg";
//    URL url = getClass().getClassLoader().getResource(file);
//    try {
//      Path path = Paths.get(url.toURI());
//      filePath = path.toAbsolutePath().toString();
//    } catch (URISyntaxException ex) {
////      Logger.getLogger(PdfUtil.class.getName()).log(Level.SEVERE, null, ex);
//    }
//    return filePath;
//  }
  public void cropImage(Gallery gallery) {
    setSelectedGallery(gallery);
  }

  public boolean isExceed() {
    if (getGalleries() != null) {
      return getGalleries().size() < SystemConstants.GALLERY_MAX_FILES ? false : true;
    } else {
      return false;
    }
  }

}
