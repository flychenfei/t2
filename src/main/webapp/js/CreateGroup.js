;
(function () {

	/**
	 * Component: CreateTable
	 */
	(function ($) {

		brite.registerView("CreateGroup", {
			parent:".MainScreen",
			emptyParent:false
		}, {
			// --------- View Interface Implement--------- //
			create:function (data, config) {
				var view = this;
				if(data) {
					view.groupId = data.groupId;
					view.etag = data.etag;
				}
				return app.render("tmpl-CreateGroup",data||{});
			},

			postDisplay:function (data, config) {
				var view = this;
				var mainScreen = view.mainScreen = view.$el.bComponent("MainScreen");
			},

			close:function () {
				var view = this;
				view.$el.bRemove();
			},

			submit:function () {
				var view = this;
				var dfd;
				var $input = view.$el.find("input[name='name']");
				//check if have name
				if ($input.val() == "") {
					$input.focus();
					$input.closest("div").addClass("has-error").find("span").html("Please enter valid group name.");
				} else {
					if(view.groupId) {
						//create group
						dfd = app.googleApi.createGroup({groupId:view.groupId,etag:view.etag, groupName: $input.val()})
					}else{
						//updated group
						dfd = app.googleApi.createGroup({groupName: $input.val()});
					}
					dfd.done(function (extraData) {
						setTimeout((function () {
							$(document).trigger("DO_REFRESH_GROUPS");
						}), 5000);
						view.close();
					});

				}
			},
			// --------- View Interface Implement--------- //

			// --------- Events--------- //
			events:{
				//event for add/update button
				"btap; .createGroupBtn":function () {
					var view = this;
					view.submit();
				},

				"keydown": function (e) {
					var view = this;
					if (e.keyCode == 27) {
						view.close();
					}else if (e.keyCode == 13) {
						view.submit();
					}
				},

				//event for cancel button
				"btap; .cancelBtn":function () {
					var view = this;
					view.close();
				}
			}
			// --------- Events--------- //
		})
	})(jQuery);
})();
