package ru.pashabezborod.JavaFxClient.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginServiceResponse {
    private Status status;
    private String message;
}
