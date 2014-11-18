package com.britesnow.samplesocial.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
	public static final SelectQuery<LocalDateTime> SELECT_GET_LARGEST_RECIPIENTTIMESTAMP = Query.select(LocalDateTime.class).columns("max(\"gmailanalytics.recipientTimeStamp\"");
	
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
	public List<GmailAnalytics> getGmailAnalyticsList(User user, int pageIdx, int pageSize){
		SelectQuery<GmailAnalytics> dealSelectQuery = SELECT_GET;
		Condition condition = Query.one("gmailanalytics.userId", user.getId());
		dealSelectQuery = dealSelectQuery.where(condition).offset(pageIdx * pageSize).limit(pageSize).orderBy("!id");
		return daoHelper.list(dealSelectQuery);
	}

	public LocalDateTime getGmailAnalyticsLargestTime(User user){
		SelectQuery<LocalDateTime> dealSelectQuery = SELECT_GET_LARGEST_RECIPIENTTIMESTAMP;
		Condition condition = Query.one("gmailanalytics.userId", user.getId());
		dealSelectQuery = dealSelectQuery.where(condition);
		return daoHelper.first(dealSelectQuery).orElse(null);
	}
}
