package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleCalendarService {

    @Inject
    GoogleAuthService authService;
    
    public List<Map> listEvents(int pageIndex, int pageSize,String startDate, String endDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        com.google.api.services.calendar.Calendar.Events.List list = null;
        try {
            list = getCalendarService().events().list("primary").setMaxResults(pageSize);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if(startDate != null){
            DateTime minTime = null;
            Date min = null;
            try {
                min = sdf.parse(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            minTime = new DateTime(min.getTime());
            list = list.setTimeMin(minTime);
        }
        if(endDate != null){
            Date max = null;
            try {
                max = sdf.parse(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DateTime maxTime = new DateTime(max.getTime());
            list = list.setTimeMax(maxTime);
        }
        
        try {
            Events events = list.execute();
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Map eventMap = new HashMap();
            Event event = getCalendarService().events().get("primary", eventId).execute();
            eventMap.put("id", event.getId());
            eventMap.put("summary", event.getSummary());
            if(event.getEnd() != null){
                eventMap.put("endTime", sdf.format(new Date(event.getEnd().getDateTime().getValue())));
            }
            if(event.getStart() != null){
                eventMap.put("startTime", sdf.format(new Date(event.getStart().getDateTime().getValue())));
            }
            eventMap.put("location", event.getLocation());
            return eventMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void saveEvent(String eventId, String summary, String location, String startTime, String endTime){
        try {
            boolean create = false;
            Event event = null;
            if(eventId == null || "".equals(eventId)){
                event = new Event();
                create = true;
            }else{
                event = getCalendarService().events().get("primary", eventId).execute();
                create = false;
            }
            event.setSummary(summary);
            event.setLocation(location);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            if (startTime != null && !startTime.equals("")) {
                Date start = null;
                try {
                    start = sdf.parse(startTime);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                DateTime stime = new DateTime(start, TimeZone.getTimeZone("UTC"));
                event.setStart(new EventDateTime().setDateTime(stime));
            }else{
                event.setStart(new EventDateTime().setDateTime(new DateTime(new Date(), TimeZone.getTimeZone("UTC"))));
            }
            
            if (endTime != null && !endTime.equals("")) {
                Date end = null;
                try {
                    end = sdf.parse(endTime);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                DateTime etime = new DateTime(end, TimeZone.getTimeZone("UTC"));
                event.setEnd(new EventDateTime().setDateTime(etime));
            }else{
                event.setEnd(new EventDateTime().setDateTime(new DateTime(new Date(), TimeZone.getTimeZone("UTC"))));
            }
            
            if(create){
                getCalendarService().events().insert("primary", event).execute();
            }else{
                getCalendarService().events().update("primary", event.getId(), event).execute();
            }
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
