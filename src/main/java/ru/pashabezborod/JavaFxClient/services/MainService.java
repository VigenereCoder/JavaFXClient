package ru.pashabezborod.JavaFxClient.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.pashabezborod.JavaFxClient.Models.NewPasswordRequest;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@RequiredArgsConstructor
public class MainService {

    private final Properties appProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public List<String> getAllPasswords(long cookie) throws RestClientException {
        String url = getPasswordAllUrl(cookie);
        ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
        return (List<String>) response.getBody();
    }

    public Map<String, Integer> getPasswordAndHash(long cookie, String passName) throws RestClientException {
        String url = getPasswordUrl(cookie, passName);
        ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
        return (Map<String, Integer>) response.getBody();
    }

    public void updatePass(NewPasswordRequest newPasswordRequest) throws RestClientException {
        String url = getPasswordUrl();
        RequestEntity<NewPasswordRequest> entity = new RequestEntity<>(newPasswordRequest, HttpMethod.PUT, URI.create(url));
        restTemplate.exchange(entity, HttpStatus.class);
    }

    public void deletePassword(NewPasswordRequest newPasswordRequest) throws RestClientException {
        String url = getPasswordUrl();
        RequestEntity<NewPasswordRequest> entity = new RequestEntity<>(newPasswordRequest, HttpMethod.DELETE, URI.create(url));
        restTemplate.exchange(entity, HttpStatus.class);
    }

    public void addNewPassword(NewPasswordRequest newPasswordRequest) throws RestClientException {
        String url = getPasswordUrl();
        RequestEntity<NewPasswordRequest> entity = new RequestEntity<>(newPasswordRequest, HttpMethod.POST, URI.create(url));
        restTemplate.exchange(entity, HttpStatus.class);
    }

    private String getPasswordAllUrl(long cookie) {
        return UriComponentsBuilder
                .fromUriString(appProperties.getProperty("server_url") + "/password/all")
                .queryParam("cookie", cookie)
                .build()
                .toUriString();
    }

    private String getPasswordUrl(long cookie, String passName) {
        return UriComponentsBuilder
                .fromUriString(appProperties.getProperty("server_url") + "/password")
                .queryParam("cookie", cookie)
                .queryParam("passName", passName)
                .build()
                .toUriString();
    }

    private String getPasswordUrl() {
        return UriComponentsBuilder
                .fromUriString(appProperties.getProperty("server_url") + "/password")
                .build()
                .toUriString();
    }
}
