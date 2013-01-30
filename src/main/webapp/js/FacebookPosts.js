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
				"click;.postBtn" : function(e) {
					var view = this;
					var $e = view.$el;
					var value = $e.find(".post").val();
					app.addPost(value).done(function(){
						view.refreshPostsList.call(view);
						$e.find(".post").empty();
					});
				},
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
				function fixNull(v){
					if (v) {
						return v;
					};
					return  "";
				}
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
							return "<a href='#'  data-value='" + obj.fbid + "'>" + fixNull(obj.story)+" "+fixNull(obj.message) + "</a>"
						},
						attrs : "style='width: 400px'"

					},{
						text : "Type",
						render : function(obj) {
							return fixNull(obj.type);
						}
					},{
						text : "Created time",
						render : function(obj) {
							return fixNull(obj.created_time);
						}
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
