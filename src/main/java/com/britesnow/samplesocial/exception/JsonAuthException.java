package com.britesnow.samplesocial.exception;


@SuppressWarnings("serial")
public class JsonAuthException extends RuntimeException {
    public JsonAuthException() {
        super("User auth fail, need login again");
    }
}
