package com.example.slideshow.repository;

import com.example.slideshow.entity.SlideshowImage;
import com.example.slideshow.entity.SlideshowImageId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SlideshowImageRepository extends JpaRepository<SlideshowImage, SlideshowImageId> {

  Optional<List<SlideshowImage>> findBySlideshowId(Long slideshowId);

  Optional<SlideshowImage> findBySlideshowIdAndImageId(Long slideshowId, Long imageId);

}
