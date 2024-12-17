package com.example.slideshow.it.config;


import com.example.slideshow.SlideshowApplication;
import com.example.slideshow.repository.ImageRepository;
import com.example.slideshow.repository.SlideshowImageRepository;
import com.example.slideshow.repository.SlideshowRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SlideshowApplication.class)
public class BaseConfigurationTest {

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected WebApplicationContext applicationContext;

  protected MockMvc mockMvc;

  @Autowired
  protected JdbcTemplate jdbcTemplate;

  @Autowired
  protected SlideshowRepository slideshowRepository;

  @Autowired
  protected SlideshowImageRepository slideshowImageRepository;

  @Autowired
  protected ImageRepository imageRepository;

  @ClassRule
  public static PostgreSQLTestContainer testContainer = PostgreSQLTestContainer.getInstance();

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
    dynamicPropertyRegistry.add("spring.datasource.url", testContainer::getJdbcUrl);
    dynamicPropertyRegistry.add("spring.datasource.username", testContainer::getUsername);
    dynamicPropertyRegistry.add("spring.datasource.password", testContainer::getPassword);
  }

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
            .alwaysDo(print())
            .build();
  }

  @AfterEach
  @Transactional
  public void cleanUp() {
    slideshowImageRepository.deleteAll();
    imageRepository.deleteAll();
    slideshowRepository.deleteAll();
    jdbcTemplate.execute("TRUNCATE slideshow_image RESTART IDENTITY CASCADE");
    jdbcTemplate.execute("TRUNCATE images RESTART IDENTITY CASCADE");
    jdbcTemplate.execute("TRUNCATE slideshows RESTART IDENTITY CASCADE");
  }

  @BeforeAll
  public static void initAll() {
    testContainer.start();
  }

  @AfterAll
  public static void afterAll() {
    testContainer.stop();
  }


}