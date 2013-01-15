package com.britesnow.samplesocial.exception;


@SuppressWarnings("serial")
public class JsonAuthException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JsonAuthException() {
        super("User auth fail, need login again");
    }
}
