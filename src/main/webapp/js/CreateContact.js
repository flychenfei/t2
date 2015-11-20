;
(function () {

	(function ($) {

		brite.registerView("CreateContact", {
			loadTmpl:true,
			parent:".MainScreen",
			emptyParent:false
		}, {
			// --------- View Interface Implement--------- //
			create:function (data, config) {
				var view = this;
				if(data) {
					view.contractId = data.id;
					view.groupId = data.groupId;
				}
				return app.render("tmpl-CreateContact",data||{});
			},

			postDisplay:function (data, config) {
				var view = this;
				var mainScreen = view.mainScreen = view.$el.bComponent("MainScreen");
				showGroups.call(view);
			},

			close:function () {
				var view = this;
				view.$el.bRemove();
			},

			submit:function () {
				var view = this;
				var $controls = view.$el.find(".controls input,.controls textarea");
				data = {};
				$controls.each(function(idx, obj){
					var $this = $(this);
					data[$this.attr("name")] = $this.val();
				});
				data.id = view.contractId;
				var $email = view.$el.find("input[name='email']");
				var $group = view.$el.find(".grouplist option:selected");
				data.groupId = $group.attr("data-id");
				//check if have email
				if ($email.val() == "") {
					$email.focus();
					$email.closest("div").addClass("has-error").find("span").html("Please enter valid contact name.");
				} else {
					app.googleApi.createContact(data).done(function (extraData) {
						setTimeout((function () {
							$(document).trigger("DO_REFRESH_CONTACT");
						}), 5000);
						view.close();
					});

				}
			},
			// --------- /View Interface Implement--------- //

			// --------- Events--------- //
			events:{
				//event for create/update button
				"btap; .createContactBtn":function () {
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

				//event for cancle button
				"btap; .cancelBtn":function () {
					var view = this;
					view.close();
				},

				//event for when blur check the format.
				"blur; .controls input": function (event) {
					var view = this;
					var $input = $(event.currentTarget);
					var $name = $input.attr("name");
					var $controls = $input.closest("div");
					//check email format
					if($name == "email"){
						var emailRex = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/g;
						if($input.val() && !emailRex.test($input.val())){
						   $controls.addClass("has-error").find("span").html('username must be in an email format "yourname@yourcompany.com"');
						} else {
							$controls.removeClass("has-error").find("span").html("");
						}
					}
					//check phone number
					if($name == "phone"){
						var phoneRex = new RegExp("^[0-9]*$");
						if($input.val() && !phoneRex.test($input.val())){
						   $controls.addClass("has-error").find("span").html("the phone should be numeric character");
						} else {
							$controls.removeClass("has-error").find("span").html("");
						}
					}

					//check birthday format
					if($name == "bir"){
						if($input.val() && !$input.val().match(/^((?:19|20)\d\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/)){
						   $controls.addClass("has-error").find("span").html('birthday must be an format "YYYY-MM-DD"');
						} else {
							$controls.removeClass("has-error").find("span").html("");
						}
					}
				},

				//event for remove the error message when type
				"keyup; .controls input": function (event) {
					var $input = $(event.currentTarget);
					var $controls = $input.parent();
					$controls.removeClass("has-error");
					var $errorMsg = $controls.find("span");
					$errorMsg.text("");
				},
			}
			// --------- Events--------- //
		});
	
	//show groups
	function showGroups(){
		var view = this;
		app.googleApi.getGroups().done(function(result){
			var groups = result.result;
			for(var i = 0; i < groups.length; i ++){
				var groupId = groups[i].id;
				if(groupId == view.groupId){
					groups[i].isSelected = true;
					break;
				}
			}
			var $grouplist = view.$el.find(".grouplist").empty();
			$grouplist.append(render("tmpl-CreateContact-groups", {groups: groups}));
		});
	}

	})(jQuery);
})();
