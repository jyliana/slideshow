package com.example.slideshow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "slideshows")
@Getter
@Setter
@NoArgsConstructor
public class Slideshow {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String name;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "slideshow_image",
          joinColumns = @JoinColumn(name = "slideshow_id"),
          inverseJoinColumns = @JoinColumn(name = "image_id"))
  private List<Image> images = new ArrayList<>();

  @CreationTimestamp
  private LocalDateTime createdAt;


  public Slideshow(String name) {
    this.name = name;
  }

}