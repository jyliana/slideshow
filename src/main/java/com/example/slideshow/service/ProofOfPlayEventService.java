package com.example.slideshow.service;

import com.example.slideshow.entity.ProofOfPlayEvent;
import com.example.slideshow.exception.ResourceNotFoundException;
import com.example.slideshow.repository.ProofOfPlayEventRepository;
import com.example.slideshow.repository.SlideshowImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ProofOfPlayEventService {

  private ProofOfPlayEventRepository proofOfPlayEventRepository;
  private SlideshowImageRepository slideshowImageRepository;


  public ProofOfPlayEvent recordEvent(Long slideshowId, Long imageId) {
    var result = slideshowImageRepository.findBySlideshowIdAndImageId(slideshowId, imageId);

    if (result.isEmpty()) {
      throw new ResourceNotFoundException(String.format("Slideshow_id: %s with image_id: %s cannot be found", slideshowId, imageId));
    }

    var event = new ProofOfPlayEvent();
    event.setSlideshowId(slideshowId);
    event.setImageId(imageId);
    event.setPlayedAt(LocalDateTime.now());

    return proofOfPlayEventRepository.save(event);
  }

}
