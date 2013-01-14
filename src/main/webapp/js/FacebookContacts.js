;(function() {
	(function($) {
		brite.registerView("FacebookContacts", {
			loadTmpl : true,
			emptyParent : true,
			parent : ".MainScreen-main"
		}, {
			create : function(data, config) {
				var $html = app.render("tmpl-FacebookContacts");
				var $e = $($html);
				return $e;
			},
			postDisplay : function(data, config) {
				var view = this;
				var $e = view.$el;
				view.refreshContactsList.call(view);
			},
			events : {
				"btap;.icon-remove" : function(e) {
					var view = this;
					var $e = view.$el;
					
					var $tr = $(e.currentTarget).closest("tr");
					var id = $tr.attr("data-obj_id");
					var d = {
						id : id
					};
					$.ajax({
						type : "POST",
						url : contextPath + "/deleteFacebookContact.do",
						data : d,
						dataType : "json"
					}).done(function() {
						view.refreshContactsList.call(view);
					})
				},
			},

			docEvents : {
			},

			daoEvents : {
			},
			refreshContactsList : function() {
				var view = this;
				var $e = view.$el;
				if (!$e) {
					return;
				};
				brite.display("DataTable", ".listItem", {
					dataProvider : {
						list : app.getFBContacts
					},
					rowAttrs : function(obj) {
						return " etag='{0}'".format(obj.etag)
					},
					columnDef : [{
						text : "#",
						render : function(obj, idx) {
							return idx + 1
						},
						attrs : "style='width: 10%'"
					}, {
						text : "Name",
						render : function(obj) {
							return obj.name
						},
						attrs : "style='width: 400px'"

					}, {
						text : "Email",
						render : function(obj) {
							return obj.email
						},
						attrs : "style='width: 25%'"
					}, {
						text : "Hometown Name",
						render : function(obj) {
							return obj.hometownname
						},
						attrs : "style='width: 25%'"
					}],
					opts : {
						htmlIfEmpty : "Not contacts found",
						withPaging : true,
						withCmdEdit :false,
						cmdDelete : "DELETE_CONTACT"
					}
				});
			}
		});
	})(jQuery);

})();
