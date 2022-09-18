package ru.pashabezborod.JavaFxClient.Models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class Password {
    private String passName;
    private String password;
    private long hash;
}
