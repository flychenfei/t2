package com.britesnow.samplesocial.feed;


import java.util.HashMap;
import java.util.List;

import com.britesnow.samplesocial.dao.GmailAnalyticsDao;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.service.GmailRestService;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.services.gmail.model.Message;
import com.google.inject.Inject;

public class FeedGmailAnalyticsJob implements Runnable {

	@Inject
	private GmailAnalyticsDao gmailAnalyticsDao;

	@Inject
	private GmailRestService gmailRestService;
	
	private User user;

	public void init(User user) {
		this.user = user;
	}

	@Override
	public void run() {
		try {
			HashMap datas = gmailRestService.gmailMessageList(null, 50);
			String start = datas.get("start").toString();
			while(!Strings.isNullOrEmpty(start)){
				storageGmailAnalytics((List<Message>)datas.get("values"));
				datas = gmailRestService.gmailMessageList(null, 50);
				start = datas.get("start").toString();
			}
		} catch (Exception e) {
			System.out.printf("the ID %s user's task has occur an error and field!\n",user.getId());
			e.printStackTrace();
		}
		
	}

	private boolean storageGmailAnalytics(List<Message> messages){
		for(int i = 0, j = messages.size(); i < j; i++){
			//storage data
			
		}
		
		return true;
	}
}


