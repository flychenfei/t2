;
(function ($) {

    brite.registerView("GoogleThreadMails",{parent:".GoogleScreen-content",emptyParent:true}, {
        create: function (data, config) {
        	data = data || {};
            return app.render("tmpl-GoogleThreadMails");
        },

        postDisplay: function (data, config) {
            var view = this;
            var $e = view.$el;
            var $mailsContainer = $e.find(".mails-container");
            app.googleApi.getThreadMailsRest(data.threadId).done(function(data){
            	var mails = data.result
            	for(var i = 0; i < mails.length; i++){
            		var mail = mails[i];
            		var recDate = new Date(mail.date);
            		mail.dateString = recDate.format("yyyy-MM-dd hh:mm:ss");
            		var $mail = $(app.render("tmpl-GoogleThreadMails-mail",mail));
            		$mailsContainer.append($mail);
            	}
            });
        }

    });
    
})(jQuery);