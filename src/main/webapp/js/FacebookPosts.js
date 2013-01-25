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
				"click;img,a" : function(e) {
					var view = this;
					var $e = view.$el;
					var id = $(e.currentTarget).attr("data-value");
					var d = {
						fbid : id
					};
					app.getFacebookFriendDetail(d).done(function(data) {
						var $html = app.render("tmpl-FacebookContact-detail", data.result);
						$(".Contact-detail").find(".modal-body").html($html);
						$(".Contact-detail").show();

					})
				},
				"btap;.close" : function(e) {
					var view = this;
					var $e = view.$el;
					var $div = $(e.currentTarget).closest(".modal");
					$div.hide();
				},
				"btap;.formSearch" : function(e) {
					var view = this;
					view.refreshPostsList.call(view);
				},
				
				"keyup":function(e){
					if(e.which == 13){
	                    var view = this;
						view.refreshPostsList.call(view);
                	}
				}
			},

			docEvents : {
				"DELETE_FBCONTACT" : function(event, extraData) {
					var view = this;
					if (extraData && extraData.objId) {
						app.deleteFBContact(extraData.objId).done(function(extradata) {
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
