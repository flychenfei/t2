package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.britesnow.snow.util.Pair;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.Event.Reminders;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleCalendarService {

    @Inject
    GoogleAuthService authService;
    
    public Pair<String, List<Map>> listEvents(String pageIndex, int pageSize,String startDate, String endDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        com.google.api.services.calendar.Calendar.Events.List list = null;
        try {
            list = getCalendarService().events().list("primary").setMaxResults(pageSize).setOrderBy("startTime").setSingleEvents(true);
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
        
        if(pageIndex != null && !pageIndex.equals("") && !pageIndex.equals("0")){
            list.setPageToken(pageIndex);
        }
        
        String pageToken = null;
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
            pageToken = events.getNextPageToken();
           return new Pair<String, List<Map>>(pageToken, eventList);
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
            eventMap.put("reminders", event.getReminders());
            if(event.getEnd() != null){
                eventMap.put("endTime", new Date(event.getEnd().getDateTime().getValue()).getTime());
            }
            if(event.getStart() != null){
                eventMap.put("startTime", new Date(event.getStart().getDateTime().getValue()).getTime());
            }
            eventMap.put("location", event.getLocation());
            return eventMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void saveEvent(String eventId, String summary, String location, String startTime, String endTime, Integer min){
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
            if (min != null && min > 0){
                EventReminder eventReminder = new EventReminder();
                eventReminder.setMethod("email");
                eventReminder.setMinutes(min);
                List<EventReminder> eventReminders = new ArrayList();
                eventReminders.add(eventReminder);
                
                Reminders reminders = new Reminders();
                reminders.setOverrides(eventReminders);
                reminders.setUseDefault(false);
                
                event.setReminders(reminders);
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
