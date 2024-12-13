package com.example.slideshow.repository;

import com.example.slideshow.entity.Slideshow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlideshowRepository extends JpaRepository<Slideshow, Long> {

}