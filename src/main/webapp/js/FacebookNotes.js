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
					var $file = $e.find("input[type='file']");
					var value = $file[0].files[0];
					var $msg = $e.find("input[name='msg']");
					app.facebookApi.addNote({
						msg : $msg.val()
					}, value).done(function() {
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
					columnDef : [{
						text : "Photo",
						render : function(obj) {
							return "<img src='" + obj.picture + "'/>"
						},
						attrs : "style='width: 400px'"

					}, {
						text : "Created time",
						render : function(obj) {
							return fixNull(obj.created_time);
						},
						attrs : "style='width: 300px'"
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
