;(function() {
	(function($) {
		brite.registerView("FacebookNotes", {
			loadTmpl : true,
			emptyParent : true,
			parent : ".FacebookScreen-content"
		}, {
			create : function(data, config) {
				var $html = app.render("tmpl-FacebookNotes");
				var $e = $($html);
				return $e;
			},
			postDisplay : function(data, config) {
				var view = this;
				var $e = view.$el;
				view.refreshNotesList.call(view);

			},
			events : {
				"click;.postBtn" : function(e) {
					var view = this;
					var $e = view.$el;
					var $subject = $e.find("input[name='subject']");
					var $msg = $e.find("input[name='msg']");
					app.facebookApi.addNote({
						msg : $msg.val(),
						subject:$subject.val()
					}).done(function() {
						view.refreshNotesList.call(view);
						$(".result").show(function() {
							$(".result").hide(3000);
						});
					});
				},
			},

			docEvents : {

			},

			daoEvents : {
			},
			refreshNotesList : function() {
				var view = this;
				var $e = view.$el;
				if (!$e) {
					return;
				};
				function fixNull(v) {
					if (v) {
						return v;
					};
					return "";
				}


				brite.display("DataTable", ".listItem", {
					dataProvider : {
						list : app.facebookApi.getNotes
					},
					rowAttrs : function(obj) {
						return " etag='{0}'".format(obj.etag)
					},
					columnDef : [ {
						text : "Subject",
						render : function(obj) {
							return obj.subject;
						},
						attrs : "style='width: 200px'"
					}, {
						text : "Message",
						render : function(obj) {
							return obj.message;
						},
						attrs : "style='width: 400px'"
					}, {
						text : "Created time",
						render : function(obj) {
							return fixNull(obj.createdTime);
						},
						attrs : "style='width: 200px'"
					}],
					opts : {
						htmlIfEmpty : "Not note found",
						withPaging : true,
						withCmdEdit : false,
						withCmdDelete : false
					}
				});
			}
		});
	})(jQuery);

})();
