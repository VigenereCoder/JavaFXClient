package ru.pashabezborod.JavaFxClient.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.pashabezborod.JavaFxClient.Models.User;
import ru.pashabezborod.JavaFxClient.util.LoginServiceResponse;
import ru.pashabezborod.JavaFxClient.util.Status;
import java.util.Objects;
import java.util.Properties;

@RequiredArgsConstructor
public class LoginService {
    private final Properties appProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public LoginServiceResponse onCreateNewUser(User user) {
        RequestEntity<User> requestEntity = RequestEntity
                .post(appProperties.getProperty("server_url") + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(user, User.class);
        try {
            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
            return LoginServiceResponse.builder()
                    .status(Status.getByInt(response.getStatusCodeValue()))
                    .message(response.getBody())
                    .build();
        } catch (HttpClientErrorException e) {
            return LoginServiceResponse.builder()
                    .status(Status.getByInt(e.getRawStatusCode()))
                    .message(Objects.requireNonNull(e.getMessage())
                            .substring(e.getMessage().indexOf("\"") + 1 , e.getMessage().length() - 1))
                    .build();
        }
    }

    public LoginServiceResponse onLogIn(User user) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(appProperties.getProperty("server_url") + "/login")
                .queryParam("name", user.getName())
                .queryParam("password", user.getPassword());
        String url = builder.build().toUriString();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return LoginServiceResponse.builder()
                    .message(response.getBody())
                    .status(Status.OK)
                    .build();
        } catch (HttpClientErrorException e) {
            return LoginServiceResponse.builder()
                    .status(Status.getByInt(e.getRawStatusCode()))
                    .message(Objects.requireNonNull(e.getMessage())
                            .substring(e.getMessage().indexOf("\"") + 1 , e.getMessage().length() - 1))
                    .build();
        }
    }

    public void onLogOut(long cookie) {
        try {
            restTemplate.delete(appProperties.getProperty("server_url") + "/login/cookie?cookie=" + cookie);
        } catch (RestClientException e) {
            e.printStackTrace(); //TODO
        }
    }

    public void onDeleteUser(long cookie) {
        try {
            restTemplate.delete(appProperties.getProperty("server_url") + "/login/user?cookie=" + cookie);
        } catch (RestClientException e) {
            e.printStackTrace(); //TODO
        }
    }
}
