package ru.pashabezborod.JavaFxClient.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.pashabezborod.JavaFxClient.Models.User;
import ru.pashabezborod.JavaFxClient.services.CryptManager;
import ru.pashabezborod.JavaFxClient.services.LoginService;
import ru.pashabezborod.JavaFxClient.util.LoginServiceResponse;
import ru.pashabezborod.JavaFxClient.util.Status;

import java.io.IOException;
import java.util.Properties;


public class LoginController {
    @FXML
    private Label top_label;
    @FXML
    private TextField user_name_field;
    @FXML
    private PasswordField password_field;
    private Stage loginStage;
    private Stage mainStage;
    private MainController mainController;

    private LoginService loginService;
    private Properties stringsProperties;
    private Properties appProperties;

    public static void makeLoginDialog(Stage mainStage, MainController mainController) {
        try {
            final FXMLLoader loader = new FXMLLoader(LoginController.class.getClassLoader().getResource("LoginScene.fxml"));
            final Stage loginStage = new Stage();
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.initOwner(mainStage);
            loginStage.setScene(new Scene(loader.load()));

            Properties stringProperties = new Properties();
            stringProperties.load(LoginController.class.getClassLoader().getResourceAsStream("strings.properties"));
            Properties appProperties = new Properties();
            appProperties.load(LoginController.class.getClassLoader().getResourceAsStream("application.properties"));

            ((LoginController) loader.getController()).loginStage = loginStage;
            ((LoginController) loader.getController()).mainStage = mainStage;
            ((LoginController) loader.getController()).loginService = new LoginService(appProperties);
            ((LoginController) loader.getController()).stringsProperties = stringProperties;
            ((LoginController) loader.getController()).appProperties = appProperties;
            ((LoginController) loader.getController()).mainController = mainController;

            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();//TODO
        }
    }

    public void onLoggedOut() {
        top_label.setText(stringsProperties.getProperty("default_login_message"));
        top_label.setTextFill(Color.BLACK);
        clearInputFields();
        mainStage.close();
        loginStage.show();
    }

    public void onLogInButtonPressed() {
        User user = onUserInitialize();
        if (user == null) return;
        LoginServiceResponse response = loginService.onLogIn(user);
        if (response.getStatus() == Status.OK) {
            loginStage.close();
            mainController.setCookie(Long.parseLong(response.getMessage()));
            mainController.setCryptManager(
                    new CryptManager((user.getName() + user.getPassword()).repeat(3)));
            mainController.setUserName(user.getName());
            mainController.initialize(appProperties);
            mainController.setLoginService(loginService);
            mainController.setLoginController(this);
            mainStage.show();
        } else switchInfoResponse(response, user);
    }

    public void onCreateUserButtonPressed() {
        User user = onUserInitialize();
        if (user == null) return;
        LoginServiceResponse response = loginService.onCreateNewUser(user);
        switchInfoResponse(response, user);
    }

    private User onUserInitialize() {
        String name = user_name_field.getText();
        String password = password_field.getText();
        if (checkInputStringIsWrong(name) || checkInputStringIsWrong(password)) {
            onInfoResult(stringsProperties.getProperty("wrong_input"), Color.RED);
            return null;
        }
        return new User(name, password);
    }

    private void switchInfoResponse(LoginServiceResponse response, User user) {
        switch (response.getStatus()) {
            case OK ->
                    onInfoResult(String.format(stringsProperties.getProperty("add_success"), user.getName()), Color.GREEN);
            case BAD_REQUEST -> onInfoResult(response.getMessage(), Color.RED);
            case NO_SUCH_STATUS -> onInfoResult(stringsProperties.getProperty("server_error"), Color.RED);
        }
    }

    private void onInfoResult(String message, Color color) {
        top_label.setText(message);
        top_label.setTextFill(color);
        if (color == Color.RED)
            clearInputFields();
    }

    private void clearInputFields() {
        user_name_field.setText("");
        password_field.setText("");
    }

    private boolean checkInputStringIsWrong(String string) {
        if (string == null || string.isEmpty()) return true;
        return string.chars().anyMatch(Character::isWhitespace);
    }
}
