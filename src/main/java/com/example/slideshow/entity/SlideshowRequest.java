package com.example.slideshow.entity;

import lombok.Data;

import java.util.List;

@Data
public class SlideshowRequest {
  private String name;
  private List<Image> images;
}