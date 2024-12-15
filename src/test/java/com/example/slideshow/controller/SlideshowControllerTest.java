package com.example.slideshow.controller;

import com.example.slideshow.config.BaseConfigurationTest;
import com.example.slideshow.dto.SlideshowDto;
import com.example.slideshow.entity.Image;
import com.example.slideshow.entity.Slideshow;
import com.example.slideshow.repository.ImageRepository;
import com.example.slideshow.repository.SlideshowImageRepository;
import com.example.slideshow.repository.SlideshowRepository;
import lombok.SneakyThrows;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

class SlideshowControllerTest extends BaseConfigurationTest {
  private static final String REST_API = "/slideShow";

  @Autowired
  private SlideshowRepository slideshowRepository;
  @Autowired
  private SlideshowImageRepository slideshowImageRepository;
  @Autowired
  private ImageRepository imageRepository;

  @BeforeEach
  public void setup() {
    super.setup();
  }

  @AfterEach
  public void cleanUp() {
    slideshowImageRepository.deleteAll();
    imageRepository.deleteAll();
    slideshowRepository.deleteAll();
  }

  @Test
  @SneakyThrows
  void testAddSlideshow_200Response() {
    // Given
    var image1 = new Image("http://example.com/image_forest.jpg", 5);
    var image2 = new Image("http://example.com/image_field.jpg", 10);
    imageRepository.save(image1);
    imageRepository.save(image2);

    var uriString = fromUriString(REST_API)
            .path("/addSlideshow")
            .queryParam("name", "Vacation")
            .toUriString();

    var payload = objectMapper.writeValueAsString(List.of(1L, 2L));

    // When
    var result = mockMvc.perform(MockMvcRequestBuilders.post(uriString)
            .content(payload)
            .contentType(APPLICATION_JSON));

    // Then
    result
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name", is("Vacation")))
            .andExpect(jsonPath("$.images").isArray())
            .andExpect(jsonPath("$.images.[0].id").exists())
            .andExpect(jsonPath("$.images.[0].url", is("http://example.com/image_forest.jpg")))
            .andExpect(jsonPath("$.images.[0].duration", is(5)))
            .andExpect(jsonPath("$.images.[0].addedDate").exists())
            .andExpect(jsonPath("$.images.[1].id").exists())
            .andExpect(jsonPath("$.images.[1].url", is("http://example.com/image_field.jpg")))
            .andExpect(jsonPath("$.images.[1].duration", is(10)))
            .andExpect(jsonPath("$.images.[1].addedDate").exists());

    var response = result.andReturn().getResponse().getContentAsString();
    var slideshowDto = objectMapper.readValue(response, SlideshowDto.class);

    var slideshowFromDb = slideshowRepository.findById(slideshowDto.id()).orElse(null);

    assertSoftly((all) -> {
      assertThat(slideshowFromDb).isNotNull();
      assertThat(slideshowFromDb.getId()).isEqualTo(slideshowDto.id());
      assertThat(slideshowFromDb.getImages().size()).isEqualTo(2);
      assertThat(slideshowFromDb.getImages().getFirst().getUrl()).isEqualTo("http://example.com/image_forest.jpg");
      assertThat(slideshowFromDb.getImages().getFirst().getDuration()).isEqualTo(5);
      assertThat(slideshowFromDb.getImages().getLast()).isNotNull();
    });
  }

  @Test
  @SneakyThrows
  void testAddSlideshowWithNonexistentImages_404Response() {
    // Given
    var uriString = fromUriString(REST_API)
            .path("/addSlideshow")
            .queryParam("name", "Vacation")
            .toUriString();

    var payload = objectMapper.writeValueAsString(List.of(1L, 2L));

    // When
    var result = mockMvc.perform(MockMvcRequestBuilders.post(uriString)
            .content(payload)
            .contentType(APPLICATION_JSON));

    // Then
    result
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.apiPath", Is.is("uri=/slideShow/addSlideshow")))
            .andExpect(jsonPath("$.errorCode", Is.is(HttpStatus.NOT_FOUND.getReasonPhrase())))
            .andExpect(jsonPath("$.errorMsg", startsWith("No valid images found for the provided IDs.")))
            .andExpect(jsonPath("$.errorTime").exists());
  }

  @Test
  @SneakyThrows
  void testGetSlideshow_200Response() {
    // Given
    var image1 = new Image("http://example.com/image_forest.jpg", 5);
    var image2 = new Image("http://example.com/image_field.jpg", 10);
    var slideshow = new Slideshow("Vacation");
    var re1 = imageRepository.save(image1);
    var re2 = imageRepository.save(image2);
    slideshow.setImages(List.of(re1, re2));
    slideshowRepository.save(slideshow);

    var uriString = fromUriString(REST_API)
            .path("/getSlideshow/{id}")
            .uriVariables(Map.of("id", "1"))
            .toUriString();

    // When
    var result = mockMvc.perform(MockMvcRequestBuilders.get(uriString)
            .contentType(APPLICATION_JSON));

    // Then
    result
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name", is("Vacation")))
            .andExpect(jsonPath("$.images").isArray())
            .andExpect(jsonPath("$.images.[0].id").exists())
            .andExpect(jsonPath("$.images.[0].url", is("http://example.com/image_forest.jpg")))
            .andExpect(jsonPath("$.images.[0].duration", is(5)))
            .andExpect(jsonPath("$.images.[0].addedDate").exists())
            .andExpect(jsonPath("$.images.[1].id").exists())
            .andExpect(jsonPath("$.images.[1].url", is("http://example.com/image_field.jpg")))
            .andExpect(jsonPath("$.images.[1].duration", is(10)))
            .andExpect(jsonPath("$.images.[1].addedDate").exists());
  }

}