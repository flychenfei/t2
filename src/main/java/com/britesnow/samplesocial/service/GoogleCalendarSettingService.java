package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.britesnow.snow.util.Pair;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Setting;
import com.google.api.services.calendar.model.Settings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleCalendarSettingService {

    @Inject
    GoogleAuthService authService;
    
    public Pair<String, List<Map>> CalendarSetting(Integer pageSize){
        
        Settings settings;
        String pageToken = null;
        try {
            settings = getCalendarsService().settings().list().setMaxResults(pageSize).execute();
            List<Map> list = settings.getItems().stream().map(setting -> {
                Map map = new HashMap();
            map.put("id", setting.getId());
            map.put("value", setting.getValue());
            map.put("etag", setting.getEtag());
            map.put("kind", setting.getKind());
            return map;
            }).collect(Collectors.toList());
            pageToken = settings.getNextPageToken();
            return new Pair<String, List<Map>>(pageToken, list);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
    
    public Map getCalendar(String settingId){
        try {
            Map map = new HashMap();
            Setting setting = getCalendarsService().settings().get(settingId).execute();
            
            map.put("id", setting.getId());
            map.put("value", setting.getValue());
            map.put("etag", setting.getEtag());
            map.put("kind", setting.getKind());

            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private Calendar getCalendarsService(){
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        GoogleCredential credential = new GoogleCredential().setAccessToken(authService.getSocialIdEntity().getToken());
        Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential).setApplicationName("Gmail Test").build();
        return service;
    }




}
