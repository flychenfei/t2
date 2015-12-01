;(function() {

	/**
	 * View: GoogleMailSend
	 * Desctiption: the page for mail send
	 */
	brite.registerView("GoogleMailSend", {
		loadTmpl : true,
		parent : "body"
	}, {

		create : function(data, config) {
			var view = this;
			data = data || {};
			view.type = data.type;
			var $html = app.render("tmpl-GoogleMailSend",data);
			//show a screen to prevent use click other places
			view.$screen = $("<div class='notTransparentScreen'></div>").appendTo("body");
			return $html;
		},

		events : {
			// event for add attachment
			"btap; .addAttachment": function(){
				var view = this;
				view.$el.find(".attachments").append("<div class='attachmentItem'><input type='file' name='attachments' /> <span class='removeAttachment glyphicon glyphicon-remove'></span></div>");
			},

			// event for remove attachment
			"btap; .removeAttachment": function(event){
				$(event.currentTarget).closest(".attachmentItem").remove();
			},

			// event for click to add CC
			"btap; .addCC": function(){
				var view = this;
				view.$el.find(".cc").append("<div class='ccItem'><input type='text' name='cc' /> <span class='removeCC glyphicon glyphicon-remove'></span></div>");
			},

			// event for remove the CC
			"btap; .removeCC": function(event){
				$(event.currentTarget).closest(".ccItem").remove();
			},

			// event for close the page
			"btap; .btnClose": function(){
				var view = this;
				view.close();
			},

			// event for insert mail
			"btap; .btnInsert": function(){
				sendMail.call(this, true, false);
			},

			// event for import mail
			"btap; .btnImport": function(){
				sendMail.call(this, false, true);
			},

			// event for send mail
			"btap; .btnCreate": function(){
				sendMail.call(this);
			}
		},

		close : function(update) {
			var view = this;
			view.$el.bRemove();
			view.$screen.remove();
		}
	});

	// --------- View Private Methods --------- //
	function sendMail(isInsert, isImport) {
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

		if(isInsert){
			app.googleApi.insertMailRest({
				to : to,
				cc : cc,
				subject : subject,
				content : content
			},files).done(function() {
				$(document).trigger("DO_REFRESH_MAIL");
				view.close();
			});
		}else if(isImport){
			app.googleApi.importMailRest({
				to : to,
				cc : cc,
				subject : subject,
				content : content
			},files).done(function() {
				$(document).trigger("DO_REFRESH_MAIL");
				view.close();
			});
		}else{
			// if mail id exist do update,else do create
			if (view.type == 'rest') {
				app.googleApi.sendMailRest({
					to : to,
					cc : cc,
					subject : subject,
					content : content
				},files).done(function() {
					$(document).trigger("DO_REFRESH_MAIL");
					view.close();
				});
			} else {
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
		}
	}
	// --------- /View Private Methods --------- //
})();
