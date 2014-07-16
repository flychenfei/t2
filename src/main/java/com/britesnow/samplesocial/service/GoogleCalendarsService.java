package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.britesnow.snow.util.Pair;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Acl;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.AclRule.Scope;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleCalendarsService {

    @Inject
    GoogleAuthService authService;
    
    public Pair<String, List<Map>> CalendarList(Integer pageSize){
        
        String pageToken = null;
        try {
            CalendarList calendarList = getCalendarsService().calendarList().list().setPageToken(pageToken).setMaxResults(pageSize).execute();
            List<CalendarListEntry> items = calendarList.getItems();
            List<Map> eventList = new ArrayList();
            for (CalendarListEntry calendarListEntry : items) {
                Map eventMap = new HashMap();
            
                eventMap.put("id", calendarListEntry.getId());
                eventMap.put("summary", calendarListEntry.getSummary());
                eventMap.put("accessRole", calendarListEntry.getAccessRole());
                eventMap.put("primary", calendarListEntry.getPrimary());

                eventList.add(eventMap);
            }
            pageToken = calendarList.getNextPageToken();
           return new Pair<String, List<Map>>(pageToken, eventList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Map getCalendar(String calendarId){
        try {
            Map eventMap = new HashMap();
            com.google.api.services.calendar.model.Calendar calendar = getCalendarsService().calendars().get(calendarId).execute();getClass();
            
            eventMap.put("id", calendar.getId());
            eventMap.put("summary", calendar.getSummary());

            return eventMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void saveCalendar(String calendarId,String summary){
        try {
            com.google.api.services.calendar.model.Calendar calendar = null;
            if(calendarId == null || "".equals(calendarId)){
                calendar = new com.google.api.services.calendar.model.Calendar();
                calendar.setSummary(summary);
                getCalendarsService().calendars().insert(calendar).execute();
            }else{
                calendar = getCalendarsService().calendars().get(calendarId).execute();
                calendar.setSummary(summary);
                getCalendarsService().calendars().update(calendarId, calendar).execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void deleteCalendar(String calendarId){
        try {
            getCalendarsService().calendars().delete(calendarId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void saveShareCalendar(String calendarId, String role, String scopeType, String scopeValue) {
        // TODO Auto-generated method stub
       try{
           AclRule rule = new AclRule();
           Scope scope = new Scope();
           
           scope.setType(scopeType);
           scope.setValue(scopeValue);
           rule.setScope(scope);
           rule.setRole(role);
           
           getCalendarsService().acl().insert(calendarId, rule).execute();
       } catch (IOException e) {
           e.printStackTrace();
           }
    }
    
    public List getShareCalendar(String calendarId) {
        // TODO Auto-generated method stub
        try{
            Acl acl = getCalendarsService().acl().list(calendarId).execute();
            List list = new ArrayList();
            for (AclRule rule : acl.getItems()) {
              list.add(rule.getScope().getValue());
            }     
            return list;
        }catch(IOException e){
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
