package com.britesnow.samplesocial.web;


import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gdata.data.contacts.GroupMembershipInfo;
import fi.foyt.foursquare.api.entities.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.model.ContactInfo;
import com.britesnow.samplesocial.oauth.OauthException;
import com.britesnow.samplesocial.service.GContactService;
import com.britesnow.samplesocial.service.GoogleAuthService;
import com.britesnow.samplesocial.web.annotation.WebObject;
import com.britesnow.snow.util.Pair;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleContactHandlers {
	private static Logger log = LoggerFactory.getLogger(GoogleContactHandlers.class);
	@Inject
	private GContactService gContactService;

	@Inject
	private GoogleAuthService googleAuthService;


	// --------- Create --------- //
	/**
	 * create or update contact
	 * @param user   user
	 * @param contact  contact info
	 * @return
	 * @throws Exception
	 */
	@WebPost("/gcontact/create")
	public WebResponse createContact(@WebUser User user, @WebObject ContactInfo contact) {
		boolean result = true;
		try {
			if (contact.getId() == null) {
				//create contact
				gContactService.createContact(contact);
			} else {
				//update contact
				gContactService.updateContactEntry(contact);
			}

		} catch (Exception e) {
			log.warn("create contact fail", e);
			return WebResponse.fail(e);
		}
		return WebResponse.success(result);
	}

	/**
	 * create or update group
	 * @param user   user
	 * @param groupId   groupId
	 * @param groupName   groupName
	 * @param etag Etag
	 * @return
	 * @throws Exception
	 */
	@WebPost("/ggroup/create")
	public WebResponse createGroup(@WebUser User user, @WebParam("groupId") String groupId,
								   @WebParam("groupName") String groupName, @WebParam("etag") String etag) {
		boolean result = true;
		try {
			if (groupId == null) {
				//create group
				gContactService.createContactGroupEntry(groupName);
			} else {
				//update group
				gContactService.updateContactGroupEntry(groupId, etag, groupName);
			}

		} catch (Exception e) {
			log.warn(String.format("create Group %s fail", groupName), e);
			return WebResponse.fail(e);
		}
		return WebResponse.success(result);
	}

	// --------- /Create --------- //

	// --------- Delete --------- //
	/**
	 * delete contact
	 * @param user   user
	 * @param contactId   contactId
	 * @param etag etag
	 * @return
	 * @throws Exception
	 */
	@WebPost("/gcontact/delete")
	public WebResponse deleteContact(@WebUser User user, @WebParam("contactId") String contactId, @WebParam("etag") String etag) {
		boolean result = false;
		if (user != null) {
			try {
				gContactService.deleteContact(contactId, etag);
				result = true;
			} catch (Exception e) {
				log.warn(String.format("delete contact %s fail", contactId), e);
				return WebResponse.fail(e);
			}
		}
		return WebResponse.success(result);
	}

	/**
	 * delete group
	 * @param user   user
	 * @param groupId   groupId
	 * @param etag Etag
	 * @return
	 * @throws Exception
	 */
	@WebPost("/ggroup/delete")
	public WebResponse deleteGroup(@WebUser User user, @WebParam("groupId") String groupId, @WebParam("etag") String etag) {
		boolean result = false;
		if (user != null) {
			try {
				gContactService.deleteGroup(groupId, etag);
				result = true;
			} catch (Exception e) {
				log.warn(String.format("delete group %s fail", groupId), e);
				return WebResponse.fail(e);
			}
		}
		return WebResponse.success(result);
	}
	// --------- /Delete --------- //

	// --------- getData --------- //
	/**
	 * get contacts with groupId
	 * @param user   auth user
	 * @param groupId  group id
	 * @param pageSize   page size
	 * @param pageIndex  page size
	 * @param rc
	 * @return
	 * @throws Exception
	 */
	@WebGet("/gcontact/list")
	public WebResponse getContacts(@WebUser User user, @WebParam("groupId") String groupId,
								   @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex,
								   RequestContext rc) throws Exception {
		Pair<List<ContactEntry>, Integer> contactPair = gContactService.getContactResults(groupId, pageIndex * pageSize + 1, pageSize);
		Pair<List<ContactGroupEntry>, Integer> groupPair = gContactService.getGroupResults();
		List<ContactEntry> list = contactPair.getFirst();
		List<ContactGroupEntry> groupList = groupPair.getFirst();
		List<ContactInfo> infos = new ArrayList<ContactInfo>();
		for (ContactEntry contact : list) {
			ContactInfo contactInfo = ContactInfo.from(contact);
			List<GroupMembershipInfo> groupMembershipInfos = contact.getGroupMembershipInfos();
			if(groupMembershipInfos != null){
				List<String> groups = new ArrayList();
				for(GroupMembershipInfo groupMembershipInfo : groupMembershipInfos){
					for(ContactGroupEntry groupEntry : groupList){
						if(groupEntry.getId().equals(groupMembershipInfo.getHref())){
							groups.add(groupEntry.getTitle().getPlainText());
						}
					}
				}
				contactInfo.setGroups(groups);
			}
			infos.add(contactInfo);
		}

		return WebResponse.success(infos).setResultCount(contactPair.getSecond());
	}

	/**
	 * search contact
	 * @param user   user
	 * @param contactName  contact name
	 * @param pageSize   page size
	 * @param pageIndex page index
	 * @param rc
	 * @return
	 * @throws Exception
	 */
	@WebGet("/gcontact/search")
	public WebResponse searchContacts(@WebUser User user, @WebParam("contactName") String contactName,
									  @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") Integer pageIndex,
									  RequestContext rc) throws Exception {
		Pair<List<ContactEntry>, Integer> contactPair = gContactService.searchContactResults(contactName, pageIndex * pageSize + 1, pageSize);
		Pair<List<ContactGroupEntry>, Integer> groupPair = gContactService.getGroupResults();
		List<ContactEntry> list = contactPair.getFirst();
		List<ContactGroupEntry> groupList = groupPair.getFirst();
		List<ContactInfo> infos = new ArrayList<ContactInfo>();
		for (ContactEntry contact : list) {
			ContactInfo contactInfo = ContactInfo.from(contact);
			List<GroupMembershipInfo> groupMembershipInfos = contact.getGroupMembershipInfos();
			if(groupMembershipInfos != null){
				List<String> groups = new ArrayList();
				for(GroupMembershipInfo groupMembershipInfo : groupMembershipInfos){
					for(ContactGroupEntry groupEntry : groupList){
						if(groupEntry.getId().equals(groupMembershipInfo.getHref())){
							groups.add(groupEntry.getTitle().getPlainText());
						}
					}
				}
				contactInfo.setGroups(groups);
			}
			infos.add(contactInfo);
		}

		return WebResponse.success(infos).setResultCount(contactPair.getSecond());
	}

	/**
	 * get contact info with contactId
	 * @param user   user
	 * @param contactId   contactId
	 * @param etag etag
	 * @return
	 * @throws Exception
	 */
	@WebGet("/gcontact/get")
	public WebResponse getContact(@WebParam("contactId") String contactId,
								  @WebParam("etag") String etag, @WebUser User user) {
		Map m = new HashMap();
		if (user != null && contactId != null) {
			try {
				ContactEntry entry = gContactService.getContactEntry(contactId);
				List<GroupMembershipInfo> groupMembershipInfos = entry.getGroupMembershipInfos();
				ContactInfo contactInfo = ContactInfo.from(entry);
				if(groupMembershipInfos != null){
					List<String> groups = new ArrayList<>();
					for(GroupMembershipInfo groupMembershipInfo : groupMembershipInfos){
						groups.add(groupMembershipInfo.getHref());

						}
					contactInfo.setGroups(groups);
				}


				return WebResponse.success(contactInfo);
			} catch (Exception e) {
				log.warn(String.format("get contact %s fail", contactId), e);
				m.put("result", false);
			}
		}
		throw new OauthException(googleAuthService.getAuthorizationUrl());
	}

	/**
	 * list group
	 * @param user   auth user
	 * @param m  map
	 * @return
	 * @throws Exception
	 */
	@WebGet("/ggroup/list")
	public Object getGroups(@WebModel Map m, @WebUser User user, RequestContext rc) throws Exception {
		Pair<List<ContactGroupEntry>, Integer> pair = gContactService.getGroupResults();
		return WebResponse.success(pair.getFirst()).set("result_count", pair.getSecond());
	}

	/**
	 * list group
	 * @param user   auth user
	 * @param m  map
	 * @return
	 * @throws Exception
	 */
	@WebGet("/ggroup/grouplist")

	public WebResponse getGrouplist(@WebModel Map m, @WebUser User user, @WebParam("pageSize") Integer pageSize,
	                             @WebParam("pageIndex") Integer pageIndex,RequestContext rc) throws Exception {
				Pair<List<ContactGroupEntry>, Integer> pair = gContactService.getGrouplist(pageIndex * pageSize + 1, pageSize);
		return WebResponse.success(pair.getFirst()).set("result_count", pair.getSecond());
	}
	// --------- /getData --------- //
}
