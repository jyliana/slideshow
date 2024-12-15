package com.example.slideshow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "proof_of_play")
@NoArgsConstructor
public class ProofOfPlayEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "slideshow_id", nullable = false)
  private Long slideshowId;

  @Column(name = "image_id", nullable = false)
  private Long imageId;

  @Column(name = "played_at", nullable = false)
  @CreationTimestamp
  private LocalDateTime playedAt;

  public ProofOfPlayEvent(Long slideshowId, Long imageId) {
    this.slideshowId = slideshowId;
    this.imageId = imageId;
  }

}