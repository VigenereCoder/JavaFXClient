package ru.pashabezborod.JavaFxClient.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.springframework.web.client.RestClientException;
import ru.pashabezborod.JavaFxClient.Models.NewPasswordRequest;
import ru.pashabezborod.JavaFxClient.services.CryptManager;
import ru.pashabezborod.JavaFxClient.services.LoginService;
import ru.pashabezborod.JavaFxClient.services.MainService;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MainController {
    @FXML
    private ComboBox<String> combo_box;
    @FXML
    private TextField get_password_field;
    @FXML
    private PasswordField update_password_field;
    @FXML
    private TextField new_name_field;
    @FXML
    private PasswordField new_password_field;
    @Setter
    private long cookie;

    @Setter
    private String userName;
    private MainService mainService;
    @Setter
    private LoginService loginService;

    @Setter
    private CryptManager cryptManager;
    @Setter
    private LoginController loginController;

    public void onComboBoxAction() {
        String passName = combo_box.getValue();
        try {
            if (passName != null) {
                Map.Entry<String, Integer> entry = mainService.getPasswordAndHash(cookie, passName).entrySet().iterator().next();
                get_password_field.setText(cryptManager.encrypt(entry.getKey()));
                get_password_field.setStyle(checkHash(entry.getKey(), entry.getValue()) ?
                        "-fx-text-inner-color: black;" : "-fx-text-inner-color: red;");
                checkHash(entry.getKey(), entry.getValue());
            }
        } catch (RestClientException e) {
            if (e.getMessage() != null && e.getMessage().contains("Log in first"))
                onLogOutButtonPressed();
        }
    }

    public void onCopyButton() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(get_password_field.getText()), null);
    }

    public void onUpdateButtonPressed() {
        try {
            mainService.updatePass(NewPasswordRequest.builder()
                    .hash(update_password_field.getText().hashCode())
                    .passwordName(combo_box.getValue())
                    .userName(userName)
                    .password(cryptManager.crypt(update_password_field.getText()))
                    .cookie(cookie)
                    .build());
            get_password_field.setText(update_password_field.getText());
            update_password_field.setText("");
        } catch (RestClientException e) {
            if (e.getMessage() != null && e.getMessage().contains("Log in first"))
                onLogOutButtonPressed();
        }
    }

    public void onDeletePasswordButtonPressed() {
        try {
            mainService.deletePassword(NewPasswordRequest.builder()
                    .passwordName(combo_box.getValue())
                    .cookie(cookie)
                    .build());
            updateComboBox();
        } catch (RestClientException e) {
            if (e.getMessage() != null && e.getMessage().contains("Log in first"))
                onLogOutButtonPressed();
        }
    }

    public void onAddPasswordButtonPressed() {
        try {
            mainService.addNewPassword(NewPasswordRequest.builder()
                    .passwordName(new_name_field.getText())
                    .password(cryptManager.crypt(new_password_field.getText()))
                    .cookie(cookie)
                    .hash(new_password_field.getText().hashCode())
                    .build());
            new_password_field.setText("");
            new_name_field.setText("");
        } catch (RestClientException e) {
            if (e.getMessage() != null && e.getMessage().contains("Log in first"))
                onLogOutButtonPressed();
        }
        updateComboBox();
    }

    public void onDeleteUserButtonPressed() {
        loginService.onDeleteUser(cookie);
        cookie = 0L;
        loginController.onLoggedOut();
    }

    public void onCustomCryptButtonPressed() {
    }

    public void onLogOutButtonPressed() {
        loginService.onLogOut(cookie);
        userName = "";
        cryptManager = null;
        cookie = 0L;
        loginController.onLoggedOut();
    }

    public void initialize(Properties appProperties) {
        if (mainService == null)
            mainService = new MainService(appProperties);
        updateComboBox();
    }

    private void updateComboBox() {
        try {
            List<String> passwordsList = mainService.getAllPasswords(cookie);
            combo_box.getItems().clear();
            combo_box.getItems().setAll(passwordsList);
            if (!passwordsList.isEmpty()) {
                combo_box.setValue(passwordsList.get(0));
                onComboBoxAction();
            }
            else {
                combo_box.setPromptText("No passwords yet");
                get_password_field.setText("");
                get_password_field.setPromptText("password");
            }
        } catch (RestClientException e) {
            if (e.getMessage() != null && e.getMessage().contains("Log in first"))
                onLogOutButtonPressed();
        }
    }

    private boolean checkHash(String password, int hash) {
        password = cryptManager.encrypt(password);
        return password.hashCode() == hash;
    }
}
