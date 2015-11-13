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
				if(data) {
					this.contractId = data.id;
				}
				return app.render("tmpl-CreateContact",data||{});
			},

			postDisplay:function (data, config) {
				var view = this;
				var mainScreen = view.mainScreen = view.$el.bComponent("MainScreen");
				view.$el.find("form").find("input[type=text]").focus();
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
				var input = view.$el.find("input[name='email']");

				//check if have email
				if (input.val() == "") {
					input.focus();
					input.closest("div").addClass("has-error").find("span").html("Please enter valid contact name.");
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
					var errorMsg = $controls.find("span");
					errorMsg.text("");
				},
			}
			// --------- Events--------- //
		})
	})(jQuery);
})();
