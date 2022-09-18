package ru.pashabezborod.JavaFxClient.services;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CryptManager {

    private String crypt;
    public String crypt(String string) {

        StringBuilder codedString = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) > 32 && string.charAt(i) < 127) {
                codedString.append(codeEngSym(string.charAt(i), crypt.charAt(i % crypt.length())));
            } else codedString.append(string.charAt(i));
        }
        return codedString.toString();
    }

    public String encrypt(String string) {
        StringBuilder encodedString = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) > 32 && string.charAt(i) < 127)
                encodedString.append(unCodeEngSym(string.charAt(i), crypt.charAt(i % crypt.length())));
            else encodedString.append(string.charAt(i));
        }
        return encodedString.toString();
    }

    private char codeEngSym(char p, char k) {
        return (char) ((p - 33 + k - 33) % 94 + 33);
    }

    private char unCodeEngSym(char p, char k) {
        return (char) ((p - k + 94) % 94 + 33);
    }
}
