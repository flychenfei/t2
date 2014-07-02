package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleCalendarService {

    @Inject
    GoogleAuthService authService;
    
    public List<Map> listEvents(int pageIndex, int pageSize){
        
        try {
            Events events = getCalendarService().events().list("primary").setMaxResults(pageSize).execute();
            List<Event> items = events.getItems();
            List<Map> eventList = new ArrayList();
            for (Event event : items) {
                Map eventMap = new HashMap();
                eventMap.put("summary", event.getSummary());
                eventMap.put("id", event.getId());
                eventMap.put("date", event.getStart());
                eventMap.put("location", event.getLocation());
                eventMap.put("status", event.getStatus());
                eventList.add(eventMap);
            }
            
            return eventList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Map getEvent(String eventId){
        try {
            Map eventMap = new HashMap();
            Event event = getCalendarService().events().get("primary", eventId).execute();
            eventMap.put("id", event.getId());
            eventMap.put("summary", event.getSummary());
            return eventMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void createEvent(String summary){
        try {
            getCalendarService().events().quickAdd("primary", summary).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void updateEvent(String eventId, String summary){
        try {
            Event event = getCalendarService().events().get("primary", eventId).execute();
            event.setSummary(summary);
            getCalendarService().events().update("primary", event.getId(), event).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void deleteEvent(String eventId){
        try {
            getCalendarService().events().delete("primary", eventId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Calendar getCalendarService(){
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        GoogleCredential credential = new GoogleCredential().setAccessToken(authService.getSocialIdEntity().getToken());
        Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential).setApplicationName("Gmail Test").build();
        return service;
    }

}
