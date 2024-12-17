package com.example.slideshow.service;

import com.example.slideshow.dto.SlideshowDto;
import com.example.slideshow.entity.Image;
import com.example.slideshow.entity.Slideshow;
import com.example.slideshow.entity.SlideshowImage;
import com.example.slideshow.exception.ResourceNotFoundException;
import com.example.slideshow.repository.ImageRepository;
import com.example.slideshow.repository.SlideshowImageRepository;
import com.example.slideshow.repository.SlideshowRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class SlideshowService {

  private SlideshowRepository slideshowRepository;
  private SlideshowImageRepository slideshowImageRepository;
  private ImageRepository imageRepository;


  public SlideshowDto addSlideshow(List<Long> imageIds, String name) {
    List<Image> images = imageRepository.findAllById(imageIds);

    if (images.isEmpty()) {
      throw new ResourceNotFoundException("No valid images found for the provided IDs.");
    }

    var slideshow = new Slideshow(name);
    var savedSlideshow = saveSlideshowWithImages(slideshow, images);

    return getSlideshow(savedSlideshow.getId());
  }

  @Transactional
  private Slideshow saveSlideshowWithImages(Slideshow slideshow, List<Image> images) {
    slideshow = slideshowRepository.save(slideshow);

    // TO DO - DELETE OR NOT?
    for (Image image : images) {
      var slideshowImage = new SlideshowImage(slideshow, image);
      slideshowImageRepository.save(slideshowImage);
    }

    return slideshow;
  }

  @Transactional
  public Slideshow addSlideshowWithNewImages(List<Image> images, String name) {
    var slideshow = new Slideshow();
    slideshow.setName(name);
    slideshow.setImages(images);

    return slideshowRepository.save(slideshow);
  }

  @Transactional
  public void deleteSlideshow(Long id) {
    slideshowRepository.deleteById(id);
  }

  public List<SlideshowDto.ImageDto> getOrderedImages(Long slideshowId) {
    var slideshowImageList = slideshowImageRepository.findBySlideshowId(slideshowId)
            .orElseThrow(() -> new ResourceNotFoundException("Slideshow not found"));

    var slideshowDto = mapToSlideshowDto(slideshowImageList);

    return slideshowDto.images().stream().sorted(Comparator.comparing(SlideshowDto.ImageDto::addedDate)).toList();
  }

  public SlideshowDto getSlideshow(Long slideshowId) {
    var slideshowImageList = slideshowImageRepository.findBySlideshowId(slideshowId)
            .orElseThrow(() -> new ResourceNotFoundException("Slideshow not found."));

    if (slideshowImageList.isEmpty()) {
      throw new ResourceNotFoundException("The slideshow image list is empty.");
    }

    return mapToSlideshowDto(slideshowImageList);
  }

  private SlideshowDto mapToSlideshowDto(List<SlideshowImage> slideshowImageList) {
    var slideshow = slideshowImageList.getFirst().getSlideshow();

    var imagesDto = slideshowImageList.stream()
            .map(this::mapToImageDto)
            .toList();

    return new SlideshowDto(slideshow.getId(), slideshow.getName(), imagesDto);
  }

  private SlideshowDto.ImageDto mapToImageDto(SlideshowImage slideshowImage) {
    var image = slideshowImage.getImage();

    return new SlideshowDto.ImageDto(
            image.getId(),
            image.getUrl(),
            image.getDuration(),
            slideshowImage.getAddedDate()
    );
  }

}