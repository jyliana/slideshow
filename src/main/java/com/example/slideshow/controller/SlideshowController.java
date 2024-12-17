package com.example.slideshow.controller;

import com.example.slideshow.dto.SlideshowDto;
import com.example.slideshow.entity.ProofOfPlayEvent;
import com.example.slideshow.request.SlideshowRequest;
import com.example.slideshow.service.ProofOfPlayEventService;
import com.example.slideshow.service.SlideshowService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/slideShow")
@AllArgsConstructor
public class SlideshowController {

  private SlideshowService slideshowService;
  private ProofOfPlayEventService proofOfPlayEventService;

  @PostMapping("/addSlideshow")
  public ResponseEntity<SlideshowDto> addSlideshow(@RequestBody SlideshowRequest request) {
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(slideshowService.addSlideshow(request.name(), request.imagesIds()));
  }

  @GetMapping("/getSlideshow/{id}")
  public ResponseEntity<SlideshowDto> getSlideshow(@PathVariable Long id) {
    return ResponseEntity.ok(slideshowService.getSlideshow(id));
  }

  @DeleteMapping("/deleteSlideshow/{id}")
  public ResponseEntity<Void> deleteSlideshow(@PathVariable Long id) {
    slideshowService.deleteSlideshow(id);
    return ResponseEntity.accepted().build();
  }

  @GetMapping("/{id}/slideshowOrder")
  public ResponseEntity<List<SlideshowDto.ImageDto>> getOrderedImages(@PathVariable Long id) {
    return ResponseEntity.ok(slideshowService.getOrderedImages(id));
  }

  @GetMapping("/{id}/proof-of-play/{imageId}")
  public ResponseEntity<ProofOfPlayEvent> recordEvent(@PathVariable Long id, @PathVariable Long imageId) {
    return ResponseEntity.ok(proofOfPlayEventService.recordEvent(id, imageId));
  }

}