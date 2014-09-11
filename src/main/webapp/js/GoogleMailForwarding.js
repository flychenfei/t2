;(function() {

	/**
	 * View: GoogleMailForwarding
	 *
	 */
	brite.registerView("GoogleMailForwarding", {
		loadTmpl : true,
		parent : "body"
	}, {
		
		create : function(data, config) {
			var view = this;
			data = data || {};
			view.type = data.type;
			var dfd = $.Deferred();
			var $html = app.render("tmpl-GoogleMailForwarding",data);
			//show a screen to prevent use click other places
			view.$screen = $("<div class='notTransparentScreen'></div>").appendTo("body"); 
			return $html;
		},
		
		events : {
	 		"btap; .addAttachment": function(){
	 			var view = this;
	 			var $e = view.$el;
	 			$e.find(".attachments").append("<div class='attachmentItem'><input type='file' name='attachments' /> <span class='removeAttachment icon-remove'></span></div>");
	 		}, 
	 		"btap; .removeAttachment": function(event){
	 			var view = this;
	 			var $e = view.$el;
	 			var $btn = $(event.currentTarget).closest(".attachmentItem").remove();
	 		}, 
	 		"btap; .btnClose": function(){
	 			var view = this;
	 			view.close();
	 		}, 
	 		"btap; .btnCreate": function(){
	 			forwardingMail.call(this);
	 			view.close();
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
	function forwardingMail() {
		var view = this;
		var $e = view.$el;

		var content = $e.find("textarea[name='content']").val();
		var to = $e.find("input[name='to']").val();
		var cc = $e.find("input[name='cc']").val();
		var subject = $e.find("input[name='subject']").val();
		
		var files = [];
		$e.find("input[name='attachments']").each(function(){
			files.push($(this)[0].files[0]);
		});
		
		app.googleApi.sendMail({
			to : to,
			cc : cc,
			subject : subject,
			content : content
		},files).done(function() {
			$(document).trigger("DO_REFRESH_MAIL");
			view.close();

		});


	}

	// --------- /View Private Methods --------- //

})();
