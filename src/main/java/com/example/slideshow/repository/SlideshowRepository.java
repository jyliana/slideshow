package com.example.slideshow.repository;

import com.example.slideshow.entity.Slideshow;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface SlideshowRepository extends JpaRepository<Slideshow, Long> {

  @Modifying
  @Transactional
  @Query(value = "INSERT INTO slideshow_image (slideshow_id, image_id, added_date) VALUES (:slideshowId, :imageId, :addedDate)", nativeQuery = true)
  void saveSlideshowImage(@Param("slideshowId") Long slideshowId,
                          @Param("imageId") Long imageId,
                          @Param("addedDate") LocalDateTime addedDate);

}