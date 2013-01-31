package com.britesnow.samplesocial.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.britesnow.samplesocial.util.Client;
import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Singleton;

@Singleton
public class SalesForceService {
    private  static final String SF_URL = "https://ap1.salesforce.com/services/data/v26.0";
    private  static final String SF_QUERY_URL = SF_URL+"/query?";
    
    
    /**
     * @return JSONArray results
     *         Long result_count
     */
    public Map listContacts(String token, int pageIndex, int pageSize){
        Map resultMap = new HashMap();
        String url = SF_QUERY_URL;
        String sql = "SELECT Id, Name FROM Contact LIMIT "+pageSize+"OFFSET "+pageIndex;
        String csql = "SELECT count(Id) FROM Contact ";
        sql = Client.encode(sql);
        csql = Client.encode(csql);
        
        String params = "q="+sql;
        HttpMethod method = new GetMethod(url+params);
        addCommonHeader(method,token);
        String response = Client.send(method);
        JSONObject opts = (JSONObject) JsonUtil.toMapAndList(response);
        resultMap.put("result", opts.get("records"));
        
        params = "q="+csql;
        method = new GetMethod(url+params);
        addCommonHeader(method,token);
        response = Client.send(method);
        JSONObject copts = (JSONObject) JsonUtil.toMapAndList(response);
        JSONObject countObj = (JSONObject) ((JSONArray)copts.get("records")).get(0);
        resultMap.put("result_count", countObj.get("expr0"));
        return resultMap;
    }
    
    /**
     * @return JSONObject contact
     */
    public JSONObject getContact(String token, String contactId){
        String url = SF_QUERY_URL;
        String sql = "SELECT Id, Name FROM Contact WHERE Id='"+contactId+"'";
        sql = Client.encode(sql);
        String params = "q="+sql;
        HttpMethod method = new GetMethod(url+params);
        addCommonHeader(method,token);
        String response = Client.send(method);
        JSONObject opts = (JSONObject) JsonUtil.toMapAndList(response);
        JSONArray array = (JSONArray) opts.get("records");
        return (JSONObject) array.get(0);
    }
    
    /**
     * save or update contact
     */
    public void saveContact(String token, String id,String name){
        String url = null;
        Map map = new HashMap();
        map.put("lastName", name);
        PostMethod method = null;
        if(id != null && id.length() > 0){
            url = SF_URL+"/sobjects/Contact/"+id;
            method = new PostMethod(url+"?_HttpMethod=PATCH");
        }else{
            url = SF_URL+"/sobjects/Contact/";
            method = new PostMethod(url);
        }
        addCommonHeader(method,token);
        method.addRequestHeader("Content-Type","application/json");
        try {
            method.setRequestEntity(new StringRequestEntity(JSONObject.fromObject(map).toString(),
                "application/json", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try{
            Client.send(method);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * save or update contact
     */
    public void deleteContact(String token, String id){
        String url = null;
        PostMethod method = null;
        if(id != null && id.length() > 0){
            url = SF_URL+"/sobjects/Contact/"+id;
            method = new PostMethod(url+"?_HttpMethod=DELETE");
            addCommonHeader(method,token);
            try{
                Client.send(method);
            }catch(Exception e){
                
            }
        }
    }
    
    
    private void addCommonHeader(HttpMethod method,String token){
        if(method != null){
            method.addRequestHeader("Authorization", "OAuth "+token);
            method.addRequestHeader("X-PrettyPrint", "1");
        }
    }

}
