package com.example.slideshow.request;

import java.util.List;

public record SlideshowRequest(String name, List<Long> imagesIds) {

}