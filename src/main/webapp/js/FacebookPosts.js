;(function() {
	(function($) {
		brite.registerView("FacebookPosts", {
			loadTmpl : true,
			emptyParent : true,
			parent : ".FacebookScreen-content"
		}, {
			create : function(data, config) {
				var $html = app.render("tmpl-FacebookPosts");
				var $e = $($html);
				return $e;
			},
			postDisplay : function(data, config) {
				var view = this;
				var $e = view.$el;
				view.refreshPostsList.call(view);
				
			},
			events : {
			},

			docEvents : {
				
			},

			daoEvents : {
			},
			refreshPostsList : function() {
				var view = this;
				var $e = view.$el;
				if (!$e) {
					return;
				};
				brite.display("DataTable", ".listItem", {
					dataProvider : {
						list : app.getFBPosts
					},
					rowAttrs : function(obj) {
						return " etag='{0}'".format(obj.etag)
					},
					columnDef : [{
						text : "News",
						render : function(obj) {
							return "<a href='#'  data-value='" + obj.fbid + "'>" + obj.story + "</a>"
						},
						attrs : "style='width: 400px'"

					}],
					opts : {
						htmlIfEmpty : "Not news found",
						withPaging : true,
						withCmdEdit : false,
						withCmdDelete : false
					}
				});
			}
		});
	})(jQuery);

})();
