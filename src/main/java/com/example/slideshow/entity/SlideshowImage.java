package com.example.slideshow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "slideshow_image")
@Getter
@Setter
@NoArgsConstructor
public class SlideshowImage {

  @EmbeddedId
  private SlideshowImageId id = new SlideshowImageId();

  @ManyToOne
  @MapsId("slideshowId")
  @JoinColumn(name = "slideshow_id")
  private Slideshow slideshow;

  @ManyToOne
  @MapsId("imageId")
  @JoinColumn(name = "image_id")
  private Image image;

  @Column(name = "added_date")
  private LocalDateTime addedDate;

  public SlideshowImage(Slideshow slideshow, Image image, LocalDateTime addedDate) {
    this.slideshow = slideshow;
    this.image = image;
    this.addedDate = addedDate;
  }

}