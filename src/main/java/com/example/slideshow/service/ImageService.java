package com.example.slideshow.service;

import com.example.slideshow.dto.ImageDto;
import com.example.slideshow.entity.Image;
import com.example.slideshow.exception.IllegalMediaTypeException;
import com.example.slideshow.exception.ResourceNotFoundException;
import com.example.slideshow.repository.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ImageService {

  private ImageRepository imageRepository;

  public Image addImage(String url, Integer duration) {
    if (!isValidImageUrl(url)) {
      throw new IllegalMediaTypeException("Invalid image URL");
    }

    var image = new Image();
    image.setUrl(url);
    image.setDuration(duration);

    return imageRepository.save(image);
  }

  public ImageDto getImage(Long id) {
    Optional<Image> foundedImage = imageRepository.findById(id);
    var image = foundedImage.orElseThrow(() -> new ResourceNotFoundException("Image not found"));

    return mapToImageDto(image);
  }

  public void deleteImage(Long id) {
    imageRepository.deleteById(id);
  }

  public List<ImageDto> searchImages(String keyword, Long duration) {
    var images = imageRepository.findAllByUrlContainingIgnoreCaseOrDuration(keyword, duration);
    return images.stream().map(this::mapToImageDto).toList();
  }

  private boolean isValidImageUrl(String url) {
    return url.matches(".*\\.(jpg|jpeg|png|gif|bmp).*");
  }

  private ImageDto mapToImageDto(Image image) {
    var slideshowDTOs = image.getSlideshows().stream()
            .map(slideshow -> new ImageDto.SlideshowDto(slideshow.getId(), slideshow.getName()))
            .toList();

    return new ImageDto(image.getId(), image.getUrl(), image.getDuration(), slideshowDTOs);
  }

}