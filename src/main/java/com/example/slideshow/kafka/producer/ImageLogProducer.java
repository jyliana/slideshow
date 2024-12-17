package com.example.slideshow.kafka.producer;

import com.example.slideshow.dto.ImageDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ImageLogProducer {

  private KafkaTemplate<String, String> kafkaTemplate;


  public void logDeletingImage(Long id) {
    var message = String.format("The image with %d has been deleted", id);
    publishAndLog(id, message);
  }

  public void logAddingImage(ImageDto image) {
    var message = String.format("The next image %s has been created", image.toString());
    publishAndLog(image.id(), message);
  }

  private void publishAndLog(Long id, String message) {
    publish(id, message);
    log.info(message);
  }

  private void publish(Long id, String message) {
    kafkaTemplate.send("t-add-delete-image", id.toString(), message);
  }

}
