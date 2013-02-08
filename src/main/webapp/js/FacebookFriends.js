;(function() {

	(function($) {
		brite.registerView("FacebookFriends", {
			loadTmpl : true,
			emptyParent : true,
			parent : ".FacebookScreen-content"
		}, {
			create : function(data, config) {
				var $html = app.render("tmpl-FacebookFriends");
				var $e = $($html);
				return $e;
			},
			postDisplay : function(data, config) {
				var view = this;
				var $e = view.$el;
				view.refreshFriendsList.call(view);
			},
			events : {
				"click;img,a" : function(e) {
					var view = this;
					var $e = view.$el;
					var $tr = $(e.currentTarget).closest("tr");
					var id = $tr.attr("data-obj_id");
					var d = {
						fbid : id
					};
					app.facebookApi.getFriendDetail(d).done(function(data) {
						var $html = app.render("tmpl-FacebookFriend-detail", data.result);
						$(".Friend-detail").find(".modal-body").html($html);
						$(".Friend-detail").show();

					})
				},
				"click;.close" : function(e) {
					var view = this;
					var $e = view.$el;
					var $div = $(e.currentTarget).closest(".modal");
					$div.hide();
				},
				"btap;.formSearch" : function(e) {
					var view = this;
					view.refreshFriendsList.call(view);
				},

				"keyup":function(e){
					if(e.which == 13){
	                    var view = this;
						view.refreshFriendsList.call(view);
                	}
				}
			},

			docEvents : {
				"ADD_FBCONTACT" : function(event, extraData) {
					var view = this;
					if (extraData && extraData.objId) {
						app.facebookApi.addContact(null, extraData.objId).done(function(extradata) {
							if (extradata && extradata.result) {
								setTimeout((function() {
									view.refreshFriendsList.call(view);
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
			refreshFriendsList : function() {
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
						list : app.facebookApi.getFriends
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
						text : "Picture",
						render : function(obj, idx) {
							return "<img src='http://graph.facebook.com/" + obj.uid + "/picture'/>"
						},
						attrs : "style='width: 10%'"
					}, {
						text : "Name",
						render : function(obj) {
							return "<a href='#'>" + obj.name + "</a>"
						},
						attrs : "style='width: 400px'"

					}, {
						text : "Email",
						render : function(obj) {
							return fixNull(obj.email)
						},
						attrs : "style='width: 25%'"
					}],
					opts : {
						htmlIfEmpty : "Not friend found",
						withPaging : true,
						withCmdDelete : false,
						cmdEdit : "ADD_FBCONTACT",
						dataOpts :{query:$e.find(".search-query").val()}
					}
				});
			}
		});
	})(jQuery);

})();