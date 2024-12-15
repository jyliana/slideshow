package com.example.slideshow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
public class Image {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String url;

  @NotNull
  @Min(1)
  private Integer duration; // In seconds

  @ManyToMany(mappedBy = "images", fetch = FetchType.EAGER)
  private List<Slideshow> slideshows = new ArrayList<>();

  public Image(String url, Integer duration) {
    this.url = url;
    this.duration = duration;
  }

}