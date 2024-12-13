package com.example.slideshow.dto;

import java.time.LocalDateTime;
import java.util.List;


public record SlideshowDto(Long id, String name, List<ImageDto> images) {

    public record ImageDto(Long id, String url, Integer duration, LocalDateTime addedDate) {

  }
}