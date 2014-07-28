package com.britesnow.samplesocial.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.britesnow.snow.util.Pair;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Reminders;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.stream.Stream;

@Singleton
public class GoogleCalendarEventsService {
    private static Logger log = LoggerFactory.getLogger(GoogleCalendarEventsService.class);

    @Inject
    GoogleAuthService authService;
    
    public Pair<String, List<Map>> listEvents(String pageIndex, int pageSize,String startDate, String endDate, String calendarId){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        com.google.api.services.calendar.Calendar.Events.List list = null;
        
        Optional<String> optional = Optional.ofNullable(calendarId);
        calendarId = optional.orElse("primary");
        optional = Optional.of(calendarId);
        calendarId = optional.filter(x -> !"".equals(x)).orElse("primary");
      
        try {
           list = getCalendarService().events().list(calendarId).setMaxResults(pageSize).setOrderBy("startTime").setSingleEvents(true);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        if(startDate != null && !startDate.equals("")){
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
        
        if(endDate != null && !endDate.equals("")){
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
            
            List<Map> eventList = items.stream().map(event -> {
                Map eventMap = new HashMap();
                eventMap.put("summary", event.getSummary());
                eventMap.put("id", event.getId());
                eventMap.put("date", event.getStart());
                eventMap.put("location", event.getLocation());
                eventMap.put("status", event.getStatus());
                eventMap.put("calendarId", event.getOrganizer().getEmail());
                return eventMap;
            }).collect(Collectors.toList());
            
            pageToken = events.getNextPageToken();
           return new Pair<String, List<Map>>(pageToken, eventList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public  List<Map> listEventsByCalendars(String pageIndex, int pageSize,String startDate, String endDate, String[] calendarIds) throws IOException{
        final List<Map> eventList = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        if (calendarIds != null) {
            CalendarList entries =  getCalendarService().calendarList().list().execute();
            final List<CalendarListEntry> calendarEntries = entries.getItems();
            BatchRequest batch = getCalendarService().batch();
            JsonBatchCallback<Events> callback = new JsonBatchCallback<Events>() {
                
                public void onSuccess(Events events, HttpHeaders responseHeaders) {
                    List<Event> items = events.getItems();
                    
                    List<Map> curEvents = items.stream().map(event -> {
                        Map eventMap = new HashMap();
                        eventMap.put("summary", event.getSummary());
                        eventMap.put("id", event.getId());
                        eventMap.put("date", event.getStart());
                        eventMap.put("location", event.getLocation());
                        eventMap.put("status", event.getStatus());
                        calendarEntries.stream().filter(entry -> entry.getId().equals(event.getOrganizer().getEmail())).forEach(entry -> eventMap.put("backgroundColor", entry.getBackgroundColor()));
                        return eventMap;
                    }).collect(Collectors.toList());
                    
                    eventList.addAll(curEvents);
                }

                public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
                    log.warn("Error Message: " + e.getMessage());
                }
            };
            for (String calendarId : calendarIds) {
                com.google.api.services.calendar.Calendar.Events.List list = getCalendarService().events().list(calendarId).setMaxResults(pageSize).setOrderBy("startTime").setSingleEvents(true);

                if(startDate != null && !startDate.equals("")){
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
                
                if(endDate != null && !endDate.equals("")){
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
                list.queue(batch, callback);
            }
            batch.execute();
        }
        return eventList;
    }
    
    public  List<Map> listFreeBusy(String startDate, String endDate, String[] calendarIds) throws IOException{
        List<Map> eventList = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
            for (String calendarId : calendarIds) {
                FreeBusyRequest request = new FreeBusyRequest();

                if(startDate != null && !startDate.equals("")){
                    DateTime minTime = null;
                    Date min = null;
                    try {
                        min = sdf.parse(startDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    minTime = new DateTime(min.getTime());
                    request.setTimeMin(minTime);
                }
                
                if(endDate != null && !endDate.equals("")){
                    Date max = null;
                    try {
                        max = sdf.parse(endDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    DateTime maxTime = new DateTime(max.getTime());
                    request.setTimeMax(maxTime);
                }
                
                FreeBusyRequestItem item = new FreeBusyRequestItem();
                List<FreeBusyRequestItem> lt = new ArrayList();
                lt.add(item.setId(calendarId));
                request.setItems(lt);
                
                FreeBusyResponse fb = getCalendarService().freebusy().query(request).execute();
                Iterator<TimePeriod> list = fb.getCalendars().get(calendarId).getBusy().iterator();
                for(Iterator<TimePeriod> ite = list; ite.hasNext();){
                    Map map = new HashMap();
                    TimePeriod timePeriod = ite.next();
                    map.put("start", timePeriod.getStart().getValue());
                    map.put("end", timePeriod.getEnd().getValue());
                    eventList.add(map);
                }
            }
        return eventList;
    }
    
    public Map getEvent(String eventId,String calendarId){
        try {
            if(calendarId == null || calendarId.equals("")){
                calendarId = "primary";
            }
            Map eventMap = new HashMap();
            Event event = null;
            event = getCalendarService().events().get(calendarId, eventId).execute();
            
            eventMap.put("id", event.getId());
            eventMap.put("summary", event.getSummary());
            eventMap.put("reminders", event.getReminders());
            eventMap.put("iCalUID", event.getICalUID());
            if(event.getEnd() != null && event.getEnd().getDateTime() != null){
                eventMap.put("endTime", new Date(event.getEnd().getDateTime().getValue()).getTime());
            }
            if(event.getStart() != null && event.getStart().getDateTime() != null){
                eventMap.put("startTime", new Date(event.getStart().getDateTime().getValue()).getTime());
            }
            eventMap.put("location", event.getLocation());
            eventMap.put("calendarId", event.getOrganizer().getEmail());
            StringBuilder inviters = new StringBuilder();
            int i = 0;
            if(event.getAttendees() != null){
                for(EventAttendee eventAtte : event.getAttendees()){
                    if(i != 0){
                        inviters.append(",");
                    }
                    inviters.append(eventAtte.getEmail());
                    i++;
                }
            }
            eventMap.put("inviters", inviters.toString());
            return eventMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void saveEvent(String eventId, String summary, String location, String startTime,
                            String endTime, Integer min,String calendarId,String[] inviter){
        try {
            boolean create = false;
            Event event = null;
            if(calendarId == null || calendarId.equals("")){
                calendarId = "primary";
            }
            if(eventId == null || "".equals(eventId)){
                event = new Event();
                create = true;
            }else{
                event = getCalendarService().events().get(calendarId, eventId).execute();
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
            
            if(inviter != null && inviter.length !=0){
                List<EventAttendee> attendees = Stream.of(inviter).map(inv -> {
                    EventAttendee eventAtte = new EventAttendee();
                    eventAtte.setEmail(inv);
                    return eventAtte;
                }).collect(Collectors.toList());
                event.setAttendees(attendees);
            }
            
            if(create){
                getCalendarService().events().insert(calendarId, event).execute();
            }else{
                getCalendarService().events().update(calendarId, event.getId(), event).execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void saveCopyEvent(String summary, String location, String startTime,
                            String endTime, Integer min,String copyTo,String iCalUID){
        try {
            Event event = new Event();
            event.setSummary(summary);
            event.setLocation(location);
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            if (startTime != null && !startTime.equals("")) {
                Date start = null;
                try {
                    start = sdf.parse(startTime);
                } catch (ParseException e) {
                }
                if(start == null){
                    start = new Date();
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
                }
                if(end == null){
                    end = new Date();
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
            

            
            event.setICalUID(iCalUID);
            getCalendarService().events().calendarImport(copyTo, event).execute();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteEvent(String eventId, String calendarId){
        try {
            if(calendarId == null || calendarId.equals("")){
                calendarId = "primary";
            }
            getCalendarService().events().delete(calendarId, eventId).execute();
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
