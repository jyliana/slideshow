package com.example.slideshow.controller;

import com.example.slideshow.dto.ImageDto;
import com.example.slideshow.entity.Image;
import com.example.slideshow.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/images")
public class ImageController {

  @Autowired
  private ImageService imageService;

  @PostMapping("/addImage")
  public ResponseEntity<Image> addImage(@RequestParam String url, @RequestParam Integer duration) {
    return ResponseEntity.ok(imageService.addImage(url, duration));
  }

  @GetMapping("/getImage/{id}")
  public ResponseEntity<ImageDto> getImage(@PathVariable Long id) {
    return ResponseEntity.ok(imageService.getImage(id));
  }

  @DeleteMapping("/deleteImage/{id}")
  public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
    imageService.deleteImage(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search")
  public ResponseEntity<List<ImageDto>> searchImages(@RequestParam String keyword, @RequestParam Long duration) {
    return ResponseEntity.ok(imageService.searchImages(keyword, duration));
  }
}