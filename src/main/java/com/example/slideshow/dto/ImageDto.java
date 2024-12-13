package com.example.slideshow.dto;

import java.util.List;

public record ImageDto(Long id, String url, Integer duration, List<SlideshowDto> slideshows) {

  public record SlideshowDto(Long id, String name) {
  }

}
