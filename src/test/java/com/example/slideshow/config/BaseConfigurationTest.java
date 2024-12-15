package com.example.slideshow.config;


import com.example.slideshow.SlideshowApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

  @BeforeAll
  public static void initAll() {
    testContainer.start();
  }

  @AfterAll
  public static void afterAll() {
    testContainer.stop();
  }


}