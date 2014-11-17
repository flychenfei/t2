package com.britesnow.samplesocial.web;


import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import com.britesnow.samplesocial.dao.GmailAnalyticsDao;
import com.britesnow.samplesocial.entity.User;
import com.britesnow.samplesocial.mail.MailInfo;
import com.britesnow.samplesocial.service.GmailRestService;
import com.britesnow.snow.util.Pair;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.handler.annotation.WebResourceHandler;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GoogleRestEmailHandlers {
	
    @Inject
    GmailRestService gmailRestService;
    
    @Inject
    GmailAnalyticsDao gmailAnalyticsDao;

    @WebGet("/gmailrest/search")
    public WebResponse search(@WebUser User user,
                        @WebParam("subject") String subject, @WebParam("from") String from,
                        @WebParam("to") String to,@WebParam("body") String body,
                        @WebParam("startDate") String startDate, @WebParam("endDate") String endDate,
                        @WebParam("startReceivedDate") String startReceivedDate, @WebParam("endReceivedDate") String endReceivedDate,
                        @WebParam("label") String label,@WebParam("hasAttachment") String hasAttachment,
                        @WebParam("attachmentName") String attachmentName,@WebParam("cc") String cc,
                        @WebParam("list") String list, @WebParam("hasCircle") String hasCircle , 
                        @WebParam("circle") String circle , @WebParam("chatContent") String chatContent ,
                        @WebParam("unread") String unread, @WebParam("category") String category , 
                        @WebParam("deliveredTo") String deliveredTo , @WebParam("rfc822msgid") String rfc822msgid ,
                        @WebParam("minSize") Integer minSize,@WebParam("maxSize") Integer maxSize,
                        @WebParam("pageSize") Integer pageSize, @WebParam("pageIndex") String pageIndex) throws Exception {
        
        if(minSize == null ){
        	minSize = 0;
        }
        if(maxSize == null){
        	maxSize = Integer.MAX_VALUE;
        }
        if("false".equalsIgnoreCase(hasAttachment)){
        	hasAttachment = null;
        }
        if("false".equalsIgnoreCase(unread)){
        	unread = null;
        }
        if("false".equalsIgnoreCase(hasCircle)){
        	hasCircle = null;
        }
        String regex = "^.*[\\s\\(\\){}\\|].*$";
        if(circle !=null){
	        circle = circle.replace("\"", "\\\"");
	        if( circle.matches(regex) ){
	        	circle = "\""+ circle + "\"";
	        }
        }
        
    	Pair<String, List<MailInfo>> pair = gmailRestService.gmailSearch(subject, from, to, body,
    			startDate, endDate, startReceivedDate, endReceivedDate, label, hasAttachment,
    			attachmentName , cc , list,  hasCircle ,  circle ,  chatContent , unread,
    			category , deliveredTo , rfc822msgid , minSize, maxSize, pageIndex, pageSize);
        List<MailInfo> mailInfos = pair.getSecond();
        WebResponse result = WebResponse.success(mailInfos);
        result.set("nextPageToken", pair.getFirst());
        return result;
    }
    
    @WebPost("/gmailrest/delete")
    public WebResponse deleteEmail(@WebUser User user,
                              @WebParam("id") String id, RequestContext rc) throws Exception {
        gmailRestService.deleteEmail(id);
        return WebResponse.success(true);
    }
    
    @WebPost("/gmailrest/trash")
    public WebResponse trashEmail(@WebUser User user,
                            @WebParam("id") String id, RequestContext rc) throws Exception {
        gmailRestService.trashEmail(id);
        return WebResponse.success(true);
    }

    @WebPost("/gmailrest/untrash")
    public WebResponse untrashEmail(@WebUser User user,
                                  @WebParam("id") String id, RequestContext rc) throws Exception {
        gmailRestService.untrashEmail(id);
        return WebResponse.success(true);
    }

    @WebPost("/gmailrest/send")
    public WebResponse sendMail(@WebUser User user,
                           @WebModel Map m, @WebParam("subject") String subject, @WebParam("cc") String cc,
                           @WebParam("content") String content, @WebParam("to") String to, @WebParam("files") FileItem[] attachments,RequestContext rc) throws Exception {
    	gmailRestService.sendMail(subject, content, to, cc, attachments);
        return WebResponse.success();
    }
    
    @WebPost("/gmailrest/forward")
    public WebResponse forwardMail(@WebUser User user,
                           @WebModel Map m, @WebParam("subject") String subject, @WebParam("cc") String cc,
                           @WebParam("content") String content, @WebParam("to") String to, @WebParam("files") FileItem[] attachments,RequestContext rc) throws Exception {
        gmailRestService.sendMail(subject, content, to, cc, attachments);
        return WebResponse.success();
    }
    
    @WebGet("/gmailrest/get")
    public WebResponse getEmail(@WebUser User user, @WebParam("id") String id) throws Exception {
        MailInfo info = gmailRestService.getEmail(id);
        return WebResponse.success(info);
    }

    @WebPost("/gmailrest/insert")
    public WebResponse insertEmail(@WebUser User user, @WebModel Map m, @WebParam("subject") String subject, @WebParam("cc") String cc,
                                   @WebParam("content") String content, @WebParam("to") String to, @WebParam("files") FileItem[] attachments,RequestContext rc) throws Exception {
        gmailRestService.insertMessage(subject, content, to, cc, attachments);
        return WebResponse.success();
    }

    @WebPost("/gmailrest/import")
    public WebResponse importEmail(@WebUser User user, @WebModel Map m, @WebParam("subject") String subject, @WebParam("cc") String cc,
                                   @WebParam("content") String content, @WebParam("to") String to, @WebParam("files") FileItem[] attachments,RequestContext rc) throws Exception {
        gmailRestService.importMessage(subject, content, to, cc, attachments);
        return WebResponse.success();
    }

    @WebPost("/gmailrest/updateLabels")
    public WebResponse updateLabels(@WebUser User user,
                           @WebModel Map m, @WebParam("id") String messageId, @WebParam("addLabels") String addLabelsStr,@WebParam("removeLabels") String removeLabelsStr,  RequestContext rc) throws Exception {
        String[] addLabels = new String[0];
        String[] removeLabels = new String[0];
        if(addLabelsStr != null && !addLabelsStr.equals("")){
            addLabels = addLabelsStr.split(",");
        }
        if(removeLabelsStr != null && !removeLabelsStr.equals("")){
            removeLabels = removeLabelsStr.split(",");
        }
        gmailRestService.updateLabels(messageId, addLabels, removeLabels);
        return WebResponse.success();
    }
    
    @WebGet("/gmailrest/labels/list")
    public WebResponse listLabels(@WebUser User user, @WebParam("force") Boolean force) throws Exception {
        if(force == null){
            force = false;
        }
        List<Map> labels = gmailRestService.listLabels(force);
        WebResponse result = WebResponse.success(labels);
        return result;
    }
    
    @WebPost("/gmailrest/labels/delete")
    public WebResponse deleteLabel(@WebUser User user,
                              @WebParam("id") String id, RequestContext rc) throws Exception {
        gmailRestService.deleteLabel(id);
        return WebResponse.success(true);
    }
    
    @WebPost("/gmailrest/labels/save")
    public WebResponse saveLabel(@WebUser User user,
                           @WebModel Map m, @WebParam("id") String id, @WebParam("name") String name, RequestContext rc) throws Exception {
        gmailRestService.saveLabel(id, name);
        return WebResponse.success();
    }
    
    @WebGet("/gmailrest/labels/get")
    public WebResponse getLabel(@WebUser User user, @WebParam("id") String id) throws Exception {
        Map info = gmailRestService.getLabel(id);
        return WebResponse.success(info);
    }
    
    @WebGet("/gmailrest/getThreadMails")
    public WebResponse getThreadMails(@WebUser User user, @WebParam("id") String id) throws Exception {
        return WebResponse.success(gmailRestService.getThreadMails(id));
    }
    
    @WebResourceHandler(matches = "/gmailrest/attachment")
    public void getAttachment(@WebUser User user, @WebParam("messageId") String messageId, @WebParam("attachmentId") String attachmentId, @WebParam("name") String name, RequestContext rc) throws Exception {
        byte[] data = gmailRestService.getAttachment(messageId, attachmentId);
        HttpServletResponse res = rc.getRes();
        res.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes()));
        res.setContentType("application/octet-stream");
        OutputStream out = res.getOutputStream();
        out.write(data);
        out.flush();
        out.close();
    }
    
    @WebGet("/gmailrest/gmailAnalytics")
    public WebResponse getGmailAnalytics(@WebUser User user, @WebParam("pageIndex") Integer pageIndex, @WebParam("pageSize") Integer pageSize) throws Exception {
    	 List results = gmailAnalyticsDao.getGmailAnalyticsList(user, pageIndex, pageSize);
    	 Long count = gmailAnalyticsDao.countAnalyticsByUsername(user.getUsername());
         return WebResponse.success(results).set("result_count", count);
    }
}
