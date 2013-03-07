package com.britesnow.samplesocial.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.restfb.BinaryAttachment;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.Facebook;
import com.restfb.Parameter;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.Event;
import com.restfb.types.FacebookType;
import com.restfb.types.Note;
import com.restfb.types.Page;
import com.restfb.types.User;

public class FacebookService {
    public User getMyInformation(String accessToken) {
        return getUserInformation(accessToken, "me");
    }

    public User getUserInformation(String accessToken, String userId) {
        User user = new DefaultFacebookClient(accessToken).fetchObject(userId, User.class);
        return user;
    }

    public List getAllFriends(String accessToken) {
        return getUserAllFriends(accessToken, "me");
    }

    public List getUserAllFriends(String accessToken, String userId) {
        Connection<User> friends = new DefaultFacebookClient(accessToken).fetchConnection(userId + "/friends", User.class);
        return friends.getData();
    }

    public List getFriendsByPage(String accessToken, String query, Integer limit, Integer offset) {
        String fql = "SELECT name,uid,email FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me())";
        if (StringUtils.isNotBlank(query)) {
            fql += " and name='" + query + "'";
        }
        List<FqlUser> users = new DefaultFacebookClient(accessToken).executeQuery(fql, FqlUser.class);
        return users;
    }

    public boolean deleteObject(String accessToken, String messageId) {
        boolean result = new DefaultFacebookClient(accessToken).deleteObject(messageId);
        return result;
    }

    public FetchObjectsResults fetchObjects(String accessToken) {
        FetchObjectsResults fetchObjectsResults = new DefaultFacebookClient(accessToken).fetchObjects(Arrays.asList("me", "cocacola"), FetchObjectsResults.class);
        return fetchObjectsResults;
    }

    public List getObjectList(String accessToken, String userId, String object, String type, String typeValue,
                            Integer limit, Integer offset) {
        Connection<JsonObject> myFeed = new DefaultFacebookClient(accessToken).fetchConnection(userId + "/" + object, JsonObject.class, Parameter.with(type, typeValue), Parameter.with("limit", limit), Parameter.with("offset", offset));
        List ls = myFeed.getData();
        List ls2 = new ArrayList();
        for (int i = 0; i < ls.size(); i++) {
            JsonObject ob = (JsonObject) ls.get(i);
            if (StringUtils.isNotBlank(type)) {
                if (!type.equals(ob.get(type))) {
                    continue;
                }
            }
            Iterator it = ob.keys();
            Map m = new HashMap();
            while (it.hasNext()) {
                String key = (String) it.next();
                Object value = (Object) ob.get(key);
                if (value instanceof String) {
                    m.put(key, value);
                }
            }
            ls2.add(m);
        }
        return ls2;
    }

    public List getPhotoList(String accessToken, String userId, Integer limit, Integer offset) {
        Connection<JsonObject> myFeed = new DefaultFacebookClient(accessToken).fetchConnection(userId + "/albums", JsonObject.class, Parameter.with("fields", "photos"), Parameter.with("limit", limit), Parameter.with("offset", offset));
        List ls = myFeed.getData();
        List ls2 = new ArrayList();
        for (int i = 0; i < ls.size(); i++) {
            JsonObject ob = (JsonObject) ls.get(i);
            JsonObject m = (JsonObject) ob.get("photos");
            JsonArray aa = (JsonArray) m.get("data");
            for (int j = 0; j < aa.length(); j++) {
                JsonObject mm = (JsonObject) aa.get(j);
                Map mmm = new HashMap();
                mmm.put("picture", mm.get("picture"));
                ls2.add(mmm);
            }
        }
        return ls2;
    }

    public List getNotesList(String accessToken, String userId, Integer limit, Integer offset) {
        Connection<Note> result = new DefaultFacebookClient(accessToken).fetchConnection(userId + "/notes", Note.class, Parameter.with("limit", limit), Parameter.with("offset", offset));
        List ls = result.getData();
        for (int i = 0; i < ls.size(); i++) {
            Note note = (Note) ls.get(i);
            Map m = new HashMap();
            m.put("message", note.getMessage());
            m.put("subject", note.getSubject());
            ls.set(i, m);
        }
        return ls;
    }

    public List getEventsList(String accessToken, String userId, Integer limit, Integer offset) {
        Connection<Event> result = new DefaultFacebookClient(accessToken).fetchConnection(userId + "/events", Event.class, Parameter.with("limit", limit), Parameter.with("offset", offset));
        List ls = result.getData();
        for (int i = 0; i < ls.size(); i++) {
            Event e = (Event) ls.get(i);
            Map m = new HashMap();
            m.put("name", e.getName());
            m.put("start_time", e.getStartTime().toString());
            ls.set(i, m);
        }
        return ls;
    }

    public List getQuestionsList(String accessToken, String userId, Integer limit, Integer offset) {
        Connection<JsonObject> result = new DefaultFacebookClient(accessToken).fetchConnection(userId + "/questions", JsonObject.class, Parameter.with("limit", limit), Parameter.with("offset", offset));
        List ls = result.getData();
        return ls;
    }

    public String publish(String accessToken, String type, String userId, String message) {
        FacebookType result = new DefaultFacebookClient(accessToken).publish(userId + "/" + type, FacebookType.class, Parameter.with("message", message));
        return result.getId();
    }

    public String publishFeed(String accessToken, String userId, String message) {
        return publish(accessToken, "feed", userId, message);
    }

    public boolean deleteFeed(String accessToken, String messageId) {
        boolean result = new DefaultFacebookClient(accessToken).deleteObject(messageId);
        return result;
    }

    public String publishNote(String accessToken, String userId, String subject, String note) {
        FacebookType result = new DefaultFacebookClient(accessToken).publish(userId + "/notes", FacebookType.class, Parameter.with("subject", subject), Parameter.with("message", note));
        return result.getId();
    }

    public boolean deleteNote(String accessToken, String messageId) {
        boolean result = new DefaultFacebookClient(accessToken).deleteObject(messageId);
        return result;
    }

    public String publishPhoto(String accessToken, String userId, String message, InputStream is) {
        FacebookType result = new DefaultFacebookClient(accessToken).publish(userId + "/photos", FacebookType.class, BinaryAttachment.with("userId", is), Parameter.with("message", message));
        return result.getId();
    }

    public boolean deletePhoto(String accessToken, String messageId) {
        boolean result = new DefaultFacebookClient(accessToken).deleteObject(messageId);
        return result;
    }

    public String publishEvent(String accessToken, String userId, String name, String startTime) {
        FacebookType result = new DefaultFacebookClient(accessToken).publish(userId + "/events", FacebookType.class, Parameter.with("name", name), Parameter.with("start_time", startTime));
        return result.getId();
    }

    public boolean deleteEvent(String accessToken, String messageId) {
        boolean result = new DefaultFacebookClient(accessToken).deleteObject(messageId);
        return result;
    }

    public static class FqlUser {
        @Facebook("uid")
        String id;
        @Facebook
        String uid;

        @Facebook
        String name;
        @Facebook
        String email;
        @Facebook
        String hometownname;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return name + "  " + uid;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getHometownname() {
            return hometownname;
        }

        public void setHometownname(String hometownname) {
            this.hometownname = hometownname;
        }
    }

    public static class FetchObjectsResults {
        @Facebook
        User me;

        @Facebook(value = "cocacola")
        Page page;
    }

}
