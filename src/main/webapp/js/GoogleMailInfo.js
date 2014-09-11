;(function() {

	/**
	 * View: MailInfo
	 *
	 */
	brite.registerView("GoogleMailInfo", {
		parent : "body"
	}, {
		
		create : function(data, config) {
			var view = this;
			var dfd = $.Deferred();
			var createDfd = $.Deferred();
			data = data || {};
			view.id = data.id;
			if (data.id) {
				if(data.type == 'rest'){
					app.googleApi.getMailRest(data.id).done(function(data) {
						dfd.resolve(data.result);
					});
				}else{
					app.googleApi.getMail(data.id).done(function(data) {
						dfd.resolve(data.result);
					});
				}
				
			} else {
				dfd.resolve({});
			}
			
			$.when(dfd).done(function(mail) {
				if (!mail || !mail.attachments || mail.attachments.length == 0) {
					mail.hideAttachments = true;
				}
                if (!mail || !mail.cc || mail.cc.length == 0) {
                    mail.hideCC = true;
                }
				var recDate = new Date(mail.date);
                mail.sendDate = recDate.format("yyyy-MM-dd hh:mm:ss")
				var $html = app.render("tmpl-GoogleMailInfo",mail);
				//show a screen to prevent use click other places
				view.$screen = $("<div class='notTransparentScreen'></div>").appendTo("body");
				createDfd.resolve($html);
			});

			return createDfd.promise();
		},
		
		events : {
	 		"btap; .btnClose": function(){
	 			var view = this;
	 			view.close();
	 		},
	 		"btap; .attachment": function(e){
	 			var view = this;
	 			var $attachment = $(e.currentTarget);
	 			var attachmentId = $attachment.attr("data-attachment-id");
	 			var name = $attachment.attr("data-attachment-name");
	 			var messageId = view.id;
	 			if(view.type == 'rest'){
		 			window.open(contextPath+"/gmailrest/attachment?messageId="+messageId+"&attachmentId="+attachmentId+"&name="+name);
	 			}else{
		 			window.open(contextPath+"/gmail/attachment?messageId="+messageId+"&attachmentId="+attachmentId+"&name="+name);
	 			}
	 		}
		},

		close : function(update) {
			var view = this;
			var $e = view.$el;

			$e.bRemove();
			view.$screen.remove();
		}
	});

	// --------- View Private Methods --------- //

	// --------- /View Private Methods --------- //

})();
