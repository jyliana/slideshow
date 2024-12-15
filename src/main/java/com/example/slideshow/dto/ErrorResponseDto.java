package com.example.slideshow.dto;

import java.time.LocalDateTime;

public record ErrorResponseDto(String apiPath, String errorCode, String errorMsg, LocalDateTime errorTime) {

}
