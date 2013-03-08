;(function() {
	(function($) {
		brite.registerView("FacebookEvents", {
			loadTmpl : true,
			emptyParent : true,
			parent : ".FacebookScreen-content"
		}, {
			create : function(data, config) {
				var $html = app.render("tmpl-FacebookEvents");
				var $e = $($html);
				return $e;
			},
			postDisplay : function(data, config) {
				var view = this;
				var $e = view.$el;
				view.refreshEventsList.call(view);

			},
			events : {
				"click;.postBtn" : function(e) {
					var view = this;
					var $e = view.$el;
					var name = $e.find("input[name='name']");
					var start_time = $e.find("input[name='start_time']");
					app.facebookApi.addEvent({
						name : name.val(),
						start_time : start_time.val()
					}).done(function() {
						view.refreshEventsList.call(view);
						$(".result").show(function() {
							$(".result").hide(3000);
						});
					});
				},
			},

			docEvents : {
				"DELETE_FBEVENT" : function(event, extraData) {
					var view = this;
					if (extraData && extraData.objId) {
						app.facebookApi.deleteEvent(extraData.objId).done(function(extradata) {
							if (extradata && extradata.result) {
								setTimeout((function() {
									view.refreshEventsList.call(view);
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
			refreshEventsList : function() {
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
						list : app.facebookApi.getEvents
					},
					rowAttrs : function(obj) {
						return " etag='{0}'".format(obj.etag)
					},
					columnDef : [{
						text : "Name",
						render : function(obj) {
							return obj.name;
						},
						attrs : "style='width: 400px'"
					}, {
						text : "Start time",
						render : function(obj) {
							return fixNull(obj.start_time);
						},
						attrs : "style='width: 200px'"
					}],
					opts : {
						htmlIfEmpty : "Not event found",
						withPaging : true,
						withCmdEdit : false,
						withCmdDelete : true,
						cmdDelete : "DELETE_FBEVENT"
					}
				});
			}
		});
	})(jQuery);

})();
