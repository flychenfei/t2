;(function() {
	(function($) {
		brite.registerView("FacebookFeeds", {
			loadTmpl : true,
			emptyParent : true,
			parent : ".FacebookScreen-content"
		}, {
			create : function(data, config) {
				var $html = app.render("tmpl-FacebookFeeds");
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
					if (value == "") {
						return
					};
					app.facebookApi.addPost(value).done(function() {
						view.refreshPostsList.call(view);
						$e.find(".post").val('');
					});
				},
			},

			docEvents : {
				"DELETE_FBFEED" : function(event, extraData) {
					var view = this;
					if (extraData && extraData.objId) {
						app.facebookApi.deleteFeed(extraData.objId).done(function(extradata) {
							if (extradata && extradata.result) {
								setTimeout((function() {
									view.refreshPostsList.call(view);
									$(".result").show(function() {
										$(".result").hide(3000);
									});
								}), 100);
							}
						});
					}
				}
			},

			daoEvents : {
			},
			refreshPostsList : function() {
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
						list : app.facebookApi.getPosts
					},
					rowAttrs : function(obj) {
						return " etag='{0}'".format(obj.etag)
					},
					columnDef : [{
						text : "News",
						render : function(obj) {
							return "<a href='#'  data-value='" + obj.fbid + "'>" + fixNull(obj.story) + " " + fixNull(obj.message) + "</a>"
						},
						attrs : "style='width: 400px'"

					}, {
						text : "Type",
						render : function(obj) {
							return fixNull(obj.type);
						},
						attrs : "style='width: 200px'"
					}, {
						text : "Created time",
						render : function(obj) {
							return fixNull(obj.created_time);
						},
						attrs : "style='width: 300px'"
					}],
					opts : {
						htmlIfEmpty : "Not news found",
						withPaging : true,
						withCmdEdit : false,
						withCmdDelete : true,
						cmdDelete : "DELETE_FBFEED"
					}
				});
			}
		});
	})(jQuery);

})(); 