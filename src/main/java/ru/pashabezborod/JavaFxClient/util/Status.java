package ru.pashabezborod.JavaFxClient.util;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Status {
    NO_SUCH_STATUS(0),
    OK(200),
    BAD_REQUEST(400);

    private final int status;

    Status(int status) {
        this.status = status;
    }

    public static Status getByInt(int status) {
       return Arrays.stream(Status.values())
               .filter(st -> st.getStatus() == status)
               .findAny()
               .orElse(Status.NO_SUCH_STATUS);
    }
}
