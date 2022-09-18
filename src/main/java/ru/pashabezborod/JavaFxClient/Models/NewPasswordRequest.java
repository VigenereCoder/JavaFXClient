package ru.pashabezborod.JavaFxClient.Models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewPasswordRequest {
    private String userName;
    private String passwordName;
    private String password;
    private long hash;
    private long cookie;
}
