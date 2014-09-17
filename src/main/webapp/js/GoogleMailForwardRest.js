;(function() {

	/**
	 * View: GoogleMailForwardRest
	 *
	 */
	brite.registerView("GoogleMailForwardRest", {
		loadTmpl : true,
		parent : "body"
	}, {

		create : function(data, config) {
			var view = this;
			data = data || {};
			view.type = data.type;
			var dfd = $.Deferred();
			var $html = app.render("tmpl-GoogleMailForwardRest",data);
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
	 		"btap; .addCC": function(){
	 			var view = this;
	 			var $e = view.$el;
	 			$e.find(".cc").append("<div class='ccItem'><input type='text' name='cc' /> <span class='removeCC icon-remove'></span></div>");
	 		},
	 		"btap; .removeCC": function(event){
	 			var view = this;
	 			var $e = view.$el;
	 			$(event.currentTarget).closest(".ccItem").remove();
	 		},
	 		"btap; .btnClose": function(){
	 			var view = this;
	 			view.close();
	 		},
	 		"btap; .btnCreate": function(){
	 			forwardMail.call(this);
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
	function forwardMail() {
		var view = this;
		var $e = view.$el;
		var haveNullVal = false;
		//remove the alert message
		$e.find(".alert").remove();
		var content = $e.find("textarea[name='content']").val();
		if(content == ""){
			$e.find("textarea[name='content']").closest('.controls').append('<div class="alert alert-danger" role="alert">Enter Content</div>');
			haveNullVal = true;
		}
		var to = $e.find("input[name='to']").val();
		if(to == ""){
			$e.find("input[name='to']").closest('.controls').append('<div class="alert alert-danger" role="alert">Enter To</div>');
			haveNullVal = true;
		}
		var cc = "";
		$e.find("input[name='cc']").each(function(i){
			var val = $(this).val();
			if(i > 0){
				cc = cc + "," + val;
			}else{
				cc = val;
			}
		});
		var subject = $e.find("input[name='subject']").val();
		if(subject == ""){
			$e.find("input[name='subject']").closest('.controls').append('<div class="alert alert-danger" role="alert">Enter Subject</div>');
			haveNullVal = true;
		}
		if(haveNullVal)return;
		var files = [];
		$e.find("input[name='attachments']").each(function(){
			files.push($(this)[0].files[0]);
		});
		// if mail id exist do update,else do create
		app.googleApi.forwardMailRest({
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
