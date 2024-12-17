package com.example.slideshow.it.controller;

import com.example.slideshow.it.config.BaseConfigurationTest;
import com.example.slideshow.dto.SlideshowDto;
import com.example.slideshow.entity.Image;
import com.example.slideshow.entity.Slideshow;
import lombok.SneakyThrows;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

  @BeforeEach
  public void setup() {
    super.setup();
  }

  @Test
  @SneakyThrows
  void testAddSlideshow_200Response() {
    // Given
    imageRepository.save(new Image("http://example.com/image_forest.jpg", 5));
    imageRepository.save(new Image("http://example.com/image_field.jpg", 10));

    String jsonBody = """
            {
               "name":"Vacation",
               "imagesIds":[1, 2]
            }
            """;

    var uriString = fromUriString(REST_API)
            .path("/addSlideshow")
            .toUriString();

    // When
    var result = mockMvc.perform(MockMvcRequestBuilders.post(uriString)
            .content(jsonBody)
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
            .toUriString();

    String jsonBody = """
            {
               "name":"Vacation",
               "imagesIds":[1, 2]
            }
            """;

    // When
    var result = mockMvc.perform(MockMvcRequestBuilders.post(uriString)
            .content(jsonBody)
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
    var image1 = imageRepository.save(new Image("http://example.com/image_forest.jpg", 5));
    var image2 = imageRepository.save(new Image("http://example.com/image_field.jpg", 10));
    var slideshow = new Slideshow("Vacation");

    slideshow.setImages(List.of(image1, image2));
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

  @Test
  @SneakyThrows
  void testDeleteSlideshow_202Response() {
    // Given
    var image1 = imageRepository.save(new Image("http://example.com/image_forest.jpg", 5));
    var image2 = imageRepository.save(new Image("http://example.com/image_field.jpg", 10));
    var slideshow = new Slideshow("Vacation");

    slideshow.setImages(List.of(image1, image2));
    slideshowRepository.save(slideshow);

    var uriString = fromUriString(REST_API)
            .path("/deleteSlideshow/{id}")
            .uriVariables(Map.of("id", "1"))
            .toUriString();

    // When
    var result = mockMvc.perform(MockMvcRequestBuilders.delete(uriString)
            .contentType(APPLICATION_JSON));

    // Then
    result
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.id").doesNotExist());

    var slideExists = slideshowRepository.existsById(slideshow.getId());
    var slideshowImageExists = slideshowImageRepository.findBySlideshowId(slideshow.getId()).orElse(null);

    assertThat(slideExists).isFalse();
    assertThat(slideshowImageExists.isEmpty()).isTrue();
  }

  @Test
  @SneakyThrows
  void testGetSlideshowOrder_200Response() {
    // Given
    var image1 = imageRepository.save(new Image("http://example.com/image_forest.jpg", 5));
    var image2 = imageRepository.save(new Image("http://example.com/image_field.jpg", 10));
    var image3 = imageRepository.save(new Image("http://example.com/image_sea.jpg", 3));
    var slideshow = new Slideshow("Vacation");

    slideshow.setImages(List.of(image1, image2, image3));
    slideshowRepository.save(slideshow);

    var uriString = fromUriString(REST_API)
            .path("/{id}/slideshowOrder")
            .uriVariables(Map.of("id", "1"))
            .toUriString();

    // When
    var result = mockMvc.perform(MockMvcRequestBuilders.get(uriString)
            .contentType(APPLICATION_JSON));

    // Then
    result
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id").exists())
            .andExpect(jsonPath("$.[0].url", is("http://example.com/image_forest.jpg")))
            .andExpect(jsonPath("$.[0].duration", is(5)))
            .andExpect(jsonPath("$.[0].addedDate").exists())
            .andExpect(jsonPath("$.[1].id").exists())
            .andExpect(jsonPath("$.[1].url", is("http://example.com/image_field.jpg")))
            .andExpect(jsonPath("$.[1].duration", is(10)))
            .andExpect(jsonPath("$.[1].addedDate").exists())
            .andExpect(jsonPath("$.[2].id").exists())
            .andExpect(jsonPath("$.[2].url", is("http://example.com/image_sea.jpg")))
            .andExpect(jsonPath("$.[2].duration", is(3)))
            .andExpect(jsonPath("$.[2].addedDate").exists());

    var response = result.andReturn().getResponse().getContentAsString();
    List<SlideshowDto.ImageDto> slideshowDtoList = objectMapper
            .readerForListOf(SlideshowDto.ImageDto.class)
            .readValue(response);
  }

  @Test
  @SneakyThrows
  void testGetProof_Of_Play_200Response() {
    // Given
    var image = imageRepository.save(new Image("http://example.com/image_forest.jpg", 5));
    var slideshow = new Slideshow("Vacation");

    slideshow.setImages(List.of(image));
    slideshowRepository.save(slideshow);

    var uriString = fromUriString(REST_API)
            .path("/{id}/proof-of-play/{imageId}")
            .uriVariables(Map.of("id", "1", "imageId", "1"))
            .toUriString();

    // When
    var result = mockMvc.perform(MockMvcRequestBuilders.get(uriString)
            .contentType(APPLICATION_JSON));

    // Then
    result
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.slideshowId", is(1)))
            .andExpect(jsonPath("$.imageId", is(1)))
            .andExpect(jsonPath("$.playedAt").exists());
  }

  @Test
  @SneakyThrows
  void testGetProof_Of_PlayNonExistentEntities_404Response() {
    // Given
    var uriString = fromUriString(REST_API)
            .path("/{id}/proof-of-play/{imageId}")
            .uriVariables(Map.of("id", "1", "imageId", "1"))
            .toUriString();

    // When
    var result = mockMvc.perform(MockMvcRequestBuilders.get(uriString)
            .contentType(APPLICATION_JSON));

    // Then
    result
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.apiPath", Is.is("uri=/slideShow/1/proof-of-play/1")))
            .andExpect(jsonPath("$.errorCode", Is.is(HttpStatus.NOT_FOUND.getReasonPhrase())))
            .andExpect(jsonPath("$.errorMsg", startsWith("Slideshow_id: 1 with image_id: 1 cannot be found")))
            .andExpect(jsonPath("$.errorTime").exists());
  }

}