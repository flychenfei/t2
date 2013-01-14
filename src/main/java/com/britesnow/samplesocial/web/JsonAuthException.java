package com.britesnow.samplesocial.web;


public class JsonAuthException extends RuntimeException {
    public JsonAuthException() {
        super("User auth fail, need login again");
    }
}
