;(function() {
	(function($) {
		brite.registerView("FacebookContacts", {
			loadTmpl : true,
			emptyParent : true,
			parent : ".FacebookScreen-content"
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
				"click;img,.contactDetail" : function(e) {
					var view = this;
					var $e = view.$el;
					var id = $(e.currentTarget).attr("data-value");
					var d = {
						fbid : id
					};
					var $html = $(app.render("tmpl-FacebookContact-detail",{}));
						$(".Contact-detail-dialog").find(".modal-body").html($html);
						$(".Contact-detail-dialog").show();
						$html.find("li").click(function(){
							$html.find("li").removeClass("active");
							$(this).addClass("active");	
							var type = $(this).attr("data-type");
							if(type == "info"){
								$(".Contact-detail-dialog").find(".infotable").show();
								$(".Contact-detail-dialog").find(".feedList").hide();
								$(".Contact-detail-dialog").find(".feedPublish").hide();
							}
							if(type == "feed"){
								$(".Contact-detail-dialog").find(".infotable").hide();
								$(".Contact-detail-dialog").find(".feedList").show();
								$(".Contact-detail-dialog").find(".feedPublish").hide();
							}
							if(type == "publish"){
								$(".Contact-detail-dialog").find(".infotable").hide();
								$(".Contact-detail-dialog").find(".feedList").hide();
								$(".Contact-detail-dialog").find(".feedPublish").show();
							}
						});

					// app.facebookApi.getFriendDetail(d).done(function(data) {
						// var $html = app.render("tmpl-FacebookContact-detail", data.result);
						// $(".Contact-detail").find(".modal-body").html($html);
						// $(".Contact-detail").show();
// 
					// })
				},
				"btap;.publishFeed2Friend" : function(e) {
					var view = this;
					var $e = view.$el;
					var $file = $e.find("input[type='file']");
					var value = $file[0].files[0];
					app.facebookApi.publishPhoto({},value).done(function(){
						$(".result").show(function() {
							$(".result").hide(3000);
						});
					});
				},
				"btap;.close" : function(e) {
					var view = this;
					var $e = view.$el;
					var $div = $(e.currentTarget).closest(".modal");
					$div.hide();
				},
				"btap;.formSearch" : function(e) {
					var view = this;
					view.refreshContactsList.call(view);
				},

				"keyup":function(e){
					if(e.which == 13){
	                    var view = this;
						view.refreshContactsList.call(view);
                	}
				}
			},

			docEvents : {
				"DELETE_FBCONTACT" : function(event, extraData) {
					var view = this;
					if (extraData && extraData.objId) {
						app.facebookApi.deleteContact(extraData.objId).done(function(extradata) {
							if (extradata && extradata.result) {
								setTimeout((function() {
									view.refreshContactsList.call(view);
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
			refreshContactsList : function() {
				var view = this;
				var $e = view.$el;
				if (!$e) {
					return;
				};
				brite.display("DataTable", ".listItem", {
					dataProvider : {
						list : app.facebookApi.getContacts
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
							return "<img src='http://graph.facebook.com/" + obj.fbid + "/picture' data-value='" + obj.fbid + "'/>"
						},
						attrs : "style='width: 10%'"
					}, {
						text : "Name",
						render : function(obj) {
							return "<a href='#' class='contactDetail'  data-value='" + obj.fbid + "'>" + obj.name + "</a>"
						},
						attrs : "style='width: 400px'"

					}, {
						text : "Email",
						render : function(obj) {
							return obj.email
						},
						attrs : "style='width: 25%'"
					}],
					opts : {
						htmlIfEmpty : "Not contacts found",
						withPaging : true,
						withCmdEdit : false,
						cmdDelete : "DELETE_FBCONTACT",
						dataOpts :{query:$e.find(".search-query").val()}
					}
				});
			}
		});
	})(jQuery);

})();