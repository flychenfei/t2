;(function() {

	/**
	 * View: MailSend
	 *
	 */
	brite.registerView("GoogleMailSend", {
		loadTmpl : true,
		parent : "body"
	}, {
		
		create : function(mail, config) {
			var view = this;
			var dfd = $.Deferred();
            console.log(mail);
			var $html = app.render("tmpl-GoogleMailSend",mail);
			//show a screen to prevent use click other places
			view.$screen = $("<div class='notTransparentScreen'></div>").appendTo("body"); 
			return $html;
		},
		
		events : {
	 		"btap; .btnClose": function(){
	 			var view = this;
	 			view.close();
	 		}, 
	 		"btap; .btnCreate": function(){
	 			sendMail.call(this);
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
	function sendMail() {
		var view = this;
		var $e = view.$el;

		var content = $e.find("textarea[name='content']").val();
		var to = $e.find("input[name='to']").val();
		var subject = $e.find("input[name='subject']").val();
		
		// if mail id exist do update,else do create
		app.googleApi.sendMail({to:to,subject:subject,content:content}).done(function() {
			$(document).trigger("DO_REFRESH_MAIL");
			view.close();
		}); 


	}

	// --------- /View Private Methods --------- //

})();
