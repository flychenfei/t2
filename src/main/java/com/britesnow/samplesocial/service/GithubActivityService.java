package com.britesnow.samplesocial.service;

import java.util.Collection;

import org.eclipse.egit.github.core.event.Event;
import org.eclipse.egit.github.core.service.EventService;

import com.britesnow.samplesocial.entity.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GithubActivityService {
	
	@Inject
	private YaoGithubAuthService githubAuthService;
	
	private EventService getEventService(User user){
		return new EventService(githubAuthService.createClient(user));
	}
	
	public Collection<Event> getPublicEvents(User user){
		EventService service = getEventService(user);
		return service.pagePublicEvents().next();
	}
}
