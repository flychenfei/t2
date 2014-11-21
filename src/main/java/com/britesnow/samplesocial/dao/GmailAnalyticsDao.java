package com.britesnow.samplesocial.dao;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.j8ql.Record;
import org.j8ql.Runner;
import org.j8ql.query.Condition;
import org.j8ql.query.Query;
import org.j8ql.query.SelectQuery;

import com.britesnow.samplesocial.entity.GmailAnalytics;
import com.britesnow.samplesocial.entity.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GmailAnalyticsDao extends BaseDao<GmailAnalytics, Long>{

    
    @Inject
	private UserDao userDao;
    
	public static final SelectQuery<GmailAnalytics> SELECT_GET = Query.select(GmailAnalytics.class).columns("gmailanalytics.*");

	public Optional<GmailAnalytics> get(Long id) {
		return super.get(null, id);
	}

	public Optional<GmailAnalytics> getByUsername(String username){
		return daoHelper.first(Query.select(entityClass).where("username", username));
	}
	
	public Long countAnalyticsByUsername(String username){
		User user = userDao.getByUsername(username).orElse(null);
		Condition condition = Query.one("gmailanalytics.userId", user.getId());
		return count(condition);
	}

	/**
	 * Create a new user and return the complete user object.
	 * @param user
	 * @param gmailAnalytics
	 * @return
	 */
	public GmailAnalytics createGmailAnalytics(User user,GmailAnalytics gmailAnalytics){
		Long id = create(null, gmailAnalytics);
		gmailAnalytics = get(null,id).get();
		return gmailAnalytics;
	}
	
	/**
	 * get gmailAnalytics list by user offset pageidx*pagesize limit pagesize
	 * @param user
	 * @param pageIdx
	 * @param pageSize
	 * @return
	 */
	public List<HashMap<String,Object>> getGmailAnalyticsList(User user, int pageIdx, int pageSize){
		SelectQuery<GmailAnalytics> dealSelectQuery = SELECT_GET;
		Condition condition = Query.one("gmailanalytics.userId", user.getId());
		dealSelectQuery = dealSelectQuery.where(condition).offset(pageIdx * pageSize).limit(pageSize).orderBy("!id");
		
		List<HashMap<String,Object>> results = daoHelper.list(dealSelectQuery).stream().map(record -> {
			GmailAnalytics gmailAnalytics = record;
			HashMap<String,Object> analytics = new HashMap<String,Object>();
			analytics.put("id", gmailAnalytics.getId());
			analytics.put("messageSubject", gmailAnalytics.getMessageSubject());
			analytics.put("conversation", gmailAnalytics.getConvetsationName());
			analytics.put("senderTimeStamp", gmailAnalytics.getSenderTimeStamp()!=null?gmailAnalytics.getSenderTimeStamp().toString():"");
			analytics.put("recipientTimeStamp", gmailAnalytics.getRecipientTimeStamp()!=null?gmailAnalytics.getRecipientTimeStamp().toString():"");
			analytics.put("senderEmailAddress", gmailAnalytics.getSenderEmailAddress());
			analytics.put("recipientEmailAddress", gmailAnalytics.getRecipientEmailAddress());
			analytics.put("messageType", gmailAnalytics.getMessageType());
			analytics.put("recipientType", gmailAnalytics.getRecipientType());
			analytics.put("countOfAttachments", gmailAnalytics.getCountOfAttachments());
			analytics.put("messageSize", gmailAnalytics.getMessageSize());
			analytics.put("messageLength", gmailAnalytics.getMessageLength());
			return analytics;
		}).collect(toList());
		
		return results;
	}

	public LocalDateTime getGmailAnalyticsLargestTime(User user){
//		SelectQuery<Record> dealSelectQuery = select("gmailanalytics").columns("max(\"recipientTimeStamp\") as dateTime");
//		Condition condition = Query.one("gmailanalytics.userId", user.getId());
//		dealSelectQuery = dealSelectQuery.where(condition);
		Runner runner = daoHelper.openRunner();
		String selectQuery = "select max(\"recipientTimeStamp\") as time from gmailanalytics where \"userId\" = '"+user.getId()+"'";
		List<Record> dateTimes = runner.list(Record.class, selectQuery);
		runner.close();
		if(dateTimes.size() > 0){
			return (LocalDateTime) dateTimes.get(0).get("time");
		}
		return null;
	}
}
