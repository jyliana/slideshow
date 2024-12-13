package com.example.slideshow.entity;

import java.util.List;

public record SlideshowRequest(String name, List<Image> images) {

}