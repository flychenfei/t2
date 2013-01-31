package com.britesnow.samplesocial.web;

import com.google.common.base.Strings;

import java.util.HashMap;

public class WebResponse extends HashMap{
    private static final long serialVersionUID = 1L;
    public static final String SUCCESS = "success";
    public static final String RESULT = "result";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String THROWABLE = "t";

    public static final String RESULT_COUNT = "result_count";


    
    private WebResponse(){
    }
    
    private WebResponse(Throwable t) {
        this.put(THROWABLE, t);
        this.setSuccess(false);
    }    

    public static WebResponse success(){
        WebResponse wr = new WebResponse();
        wr.setSuccess(true);
        return wr;
    }
    
    public static WebResponse success(Object result){
        WebResponse wr = WebResponse.success();
        wr.setResult(result);
        return wr;
    }

    public WebResponse set(String key, Object val) {
        this.put(key, val);
        return this;
    }
    public WebResponse setResultCount(Number val) {
        this.put(RESULT_COUNT, val);
        return this;
    }


    public static WebResponse fail(){
        WebResponse wr = new WebResponse();
        wr.setSuccess(false);
        return wr;
    }
    public static WebResponse fail(String message) {
        WebResponse wr = WebResponse.fail();
        wr.setErrorMessage(message);
        return wr;
    }
    
    public static WebResponse fail(Throwable t){
        WebResponse wr = new WebResponse(t);
        return wr;
    }

    public Boolean getSuccess() {
        return (Boolean) this.get(SUCCESS);
    }
    public void setSuccess(Boolean success) {
        this.put(SUCCESS, success);
    }


    public Object getResult() {
        return this.get(RESULT);
    }
    public void setResult(Object result) {
        this.put(RESULT, result);
    }

    public String getErrorCode() {
        Throwable t = (Throwable) get(THROWABLE);
        if (t != null) {
            return t.getMessage();
        }
        return null;
    }
    
    public String getErrorMessage() {
        return (String) this.get(ERROR_MESSAGE);
    }
    public void setErrorMessage(String errorMessage) {
        if (!Strings.isNullOrEmpty(errorMessage)){
            put(ERROR_MESSAGE, errorMessage);
        }else{
            put(ERROR_MESSAGE, null);
        }
        
    }

}
