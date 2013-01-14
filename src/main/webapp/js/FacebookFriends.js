;(function() {

	(function($) {
		brite.registerView("FacebookFriends", {
			loadTmpl : true,
			emptyParent : true,
			parent : ".MainScreen-main"
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
				"click;.icon-edit" : function(e) {
					var view = this;
					var $e = view.$el;
					var $tr = $(e.currentTarget).closest("tr");
					var id = $tr.attr("data-obj_id");
					var d = {
						fbid : id
					};
					$.ajax({
						type : "POST",
						url : contextPath + "/addFacebookContact.do",
						data : d,
						dataType : "json"
					}).done(function() {
						$(".result").show(function() {
							$(".result").hide(3000);
						});
					})
				},
				"click;img" : function(e) {
					var view = this;
					var $e = view.$el;
					var $tr = $(e.currentTarget).closest("tr");
					var id = $tr.attr("data-obj_id");
					var d = {
						fbid : id
					};
					app.getFacebookFriendDetail(d).done(function(data) {
						console.log(data)
						var $html = app.render("tmpl-FacebookFriend-detail", data.result);
						console.log($html);
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
				
			},

			docEvents : {
			},

			daoEvents : {
			},
			refreshFriendsList : function() {
				var view = this;
				var $e = view.$el;
				if (!$e) {
					return;
				};

				brite.display("DataTable", ".listItem", {
					dataProvider : {
						list : app.getFBFriends
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
							return "<img src='http://graph.facebook.com/" + obj.id + "/picture'/>"
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
							return obj.hometownName
						},
						attrs : "style='width: 25%'"
					}],
					opts : {
						htmlIfEmpty : "Not friend found",
						withPaging : true,
						withCmdDelete : false,
						cmdEdit : "EDIT_CONTACT"
					}
				});
			}
		});
	})(jQuery);

})();
