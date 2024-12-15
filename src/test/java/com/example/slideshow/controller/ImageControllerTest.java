package com.example.slideshow.controller;

import com.example.slideshow.config.BaseConfigurationTest;
import com.example.slideshow.entity.Image;
import com.example.slideshow.repository.ImageRepository;
import lombok.SneakyThrows;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

class ImageControllerTest extends BaseConfigurationTest {
  private static final String REST_API = "/images";

  @Autowired
  private ImageRepository imageRepository;

  @BeforeEach
  public void setup() {
    super.setup();
  }

  @AfterEach
  public void cleanUp() {
    imageRepository.deleteAll();
  }

  @Test
  @SneakyThrows
  void testAddImage_200Response() {
    // Given
    var uriString = fromUriString(REST_API)
            .path("/addImage")
            .queryParam("url", "http://example.com/image.jpg")
            .queryParam("duration", "10")
            .toUriString();

    // When
    var result = mockMvc.perform(MockMvcRequestBuilders.post(uriString)
            .contentType(APPLICATION_JSON));

    // Then
    result
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.url", is("http://example.com/image.jpg")))
            .andExpect(jsonPath("$.duration", is(10)))
            .andExpect(jsonPath("$.slideshows").isArray());

    var response = result.andReturn().getResponse().getContentAsString();
    var image = objectMapper.readValue(response, Image.class);

    var createdImage = imageRepository.findById(image.getId()).orElseThrow();
    assertSoftly((all) -> {
      assertThat(createdImage).isNotNull();
      assertThat(createdImage.getId()).isEqualTo(image.getId());
      assertThat(createdImage.getUrl()).isEqualTo("http://example.com/image.jpg");
      assertThat(createdImage.getDuration()).isEqualTo(10);
      assertThat(createdImage.getSlideshows()).isNotNull();
    });
  }

  @Test
  @SneakyThrows
  void testAddImageWithInvalidUrl_415Response() {
    // Given
    var uriString = fromUriString(REST_API)
            .path("/addImage")
            .queryParam("url", "http://example.com/image.com")
            .queryParam("duration", "10")
            .toUriString();

    // When
    var result = mockMvc.perform(MockMvcRequestBuilders.post(uriString)
            .contentType(APPLICATION_JSON));

    // Then
    result
            .andExpect(status().isUnsupportedMediaType())
            .andExpect(jsonPath("$.apiPath", Is.is("uri=/images/addImage")))
            .andExpect(jsonPath("$.errorCode", Is.is(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase())))
            .andExpect(jsonPath("$.errorMsg", startsWith("Invalid image URL")))
            .andExpect(jsonPath("$.errorTime").exists());
  }

  @Test
  @SneakyThrows
  void testGetImage_200Response() {
    // Given
    var newImage = new Image("http://example.com/image.jpg", 5);
    imageRepository.save(newImage);

    var uriString = fromUriString(REST_API)
            .path("/getImage/{id}")
            .uriVariables(Map.of("id", "1"))
            .toUriString();

    // When
    var result = mockMvc.perform(MockMvcRequestBuilders.get(uriString)
            .contentType(APPLICATION_JSON));

    // Then
    result
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.url", is("http://example.com/image.jpg")))
            .andExpect(jsonPath("$.duration", is(5)))
            .andExpect(jsonPath("$.slideshows").isArray());

    var response = result.andReturn().getResponse().getContentAsString();
    var foundedImage = objectMapper.readValue(response, Image.class);

    var image = imageRepository.findById(newImage.getId()).orElseThrow();
    assertSoftly((all) -> {
      assertThat(image).isNotNull();
      assertThat(image.getId()).isEqualTo(foundedImage.getId());
      assertThat(image.getUrl()).isEqualTo("http://example.com/image.jpg");
      assertThat(image.getDuration()).isEqualTo(5);
      assertThat(image.getSlideshows()).isNotNull();
    });
  }

  @Test
  @SneakyThrows
  void testDeleteImage_201Response() {
    // Given
    var newImage = new Image("http://example.com/image.jpg", 5);
    imageRepository.save(newImage);

    var uriString = fromUriString(REST_API)
            .path("/deleteImage/{id}")
            .uriVariables(Map.of("id", "1"))
            .toUriString();

    // When
    var result = mockMvc.perform(MockMvcRequestBuilders.delete(uriString)
            .contentType(APPLICATION_JSON));

    // Then
    result
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.id").doesNotExist());

    var image = imageRepository.findById(newImage.getId());

    assertThat(image.isEmpty()).isTrue();
  }

  @Test
  @SneakyThrows
  void testSearchImage_200Response() {
    // Given
    var image1 = new Image("http://example.com/image_forest.jpg", 5);
    var image2 = new Image("http://example.com/image_field.jpg", 10);
    var image3 = new Image("http://example.com/image_sky.jpg", 3);
    imageRepository.save(image1);
    imageRepository.save(image2);
    imageRepository.save(image3);

    var uriString = fromUriString(REST_API)
            .path("/search")
            .queryParam("keyword", "forest")
            .queryParam("duration", "10")
            .toUriString();

    // When
    var result = mockMvc.perform(MockMvcRequestBuilders.get(uriString)
            .contentType(APPLICATION_JSON));

    // Then
    result
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].url", containsStringIgnoringCase("forest")))
            .andExpect(jsonPath("$.[0].duration", is(5)))
            .andExpect(jsonPath("$.[1].url", containsStringIgnoringCase("field")))
            .andExpect(jsonPath("$.[1].duration", is(10)))
            .andExpect(jsonPath("$.[2]").doesNotExist());
  }

}