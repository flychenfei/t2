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
					view.groups = data.groups;
				}
				return app.render("tmpl-CreateContact",data||{});
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
				var $controls = view.$el.find(".controls input,.controls textarea");
				data = {};
				$controls.each(function(idx, obj){
					var $this = $(this);
					var $name = $this.attr("name");
					data[$name] = $this.val();

					//check email format
					if($name == "email"){
						checkEmail.call(view, $this);
					}
					//check phone number
					if($name == "phone"){
						checkPhone.call(view, $this);
					}

					//check birthday format
					if($name == "bir"){
						checkBirthday.call(view, $this);
					}

				});
				data.id = view.contractId;
				var $email = view.$el.find("input[name='email']");

				//checked groupid
				var groups = [];
				var $checked = view.$el.find(".dropdown-menu input:checked");
				$checked.each(function(idx, item){
				var $item = $(item);
					groups.push($item.attr("data-id"));
				});
				data.groups = groups;

				var $eachControls = view.$el.find(".form-group .controls");
				var $saveBtn = view.$el.find(".createContactBtn");
				if($eachControls.hasClass("has-error")){
					$saveBtn.addClass("disabled");
				}else{
					$saveBtn.removeClass("disabled");
				}
				//check if have email
				if ($email.val() == "") {
					$email.focus();
					$email.closest("div").addClass("has-error").find("span").html("Please enter valid contact name.");
				} else if(!$eachControls.hasClass("has-error")) {
					app.googleApi.createContact({contactsJson:JSON.stringify(data)}).done(function (extraData) {
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
					var $saveBtn = view.$el.find(".createContactBtn");
					//check email format
					if($name == "email"){
						checkEmail.call(view, $input);
					}
					//check phone number
					if($name == "phone"){
						checkPhone.call(view, $input);
					}

					//check birthday format
					if($name == "bir"){
						checkBirthday.call(view, $input);
					}

					var $eachControls = view.$el.find(".form-group .controls");
					if($eachControls.hasClass("has-error")){
						$saveBtn.addClass("disabled");
					}else{
						$saveBtn.removeClass("disabled");
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

				"click; .dropdown-toggle": function(event){
					var view = this;
					var $menu = view.$el.find(".dropdown-menu");
					if($menu.is(":visible")){
						$menu.hide();
					}else{
						showGroups.call(view);
						$menu.show();
					}
				},
			},
			// --------- Events--------- //

			// --------- Document Events--------- //  
			docEvents:{
				//when click outside the dropdown, hide the dropdown menu
				"click":function(event){
					var view = this;
					var $target = $(event.target);
					if($target.closest(".dropdown").size() == 0){
						view.$el.find(".dropdown .dropdown-menu").hide();
					}
				},
			}
			// --------- /Document Events--------- //  			
		});
	
	//show groups
	function showGroups(){
		var view = this;
		app.googleApi.getGroups().done(function(result){
			var groups = result.result;
			for(var i = 0; i < groups.length; i ++){
				var groupId = groups[i].id;
				
				if(view.groups){
					for(var j = 0; j < view.groups.length; j ++){
						if(groupId == view.groups[j]){
							groups[i].isSelected = true;
							break;
						}
					}
				}
			}
			var $grouplist = view.$el.find(".dropdown-menu").empty();
			$grouplist.append(render("tmpl-CreateContact-groups", {groups: groups}));
		});
	}

	//check email format
	function checkEmail($input){
		var view = this;
		var $controls = $input.closest("div")

		var emailRex = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/g;
		if($input.val() && !emailRex.test($input.val())){
		   $controls.addClass("has-error").find("span").html('username must be in an email format "yourname@yourcompany.com"');
		} else {
			$controls.removeClass("has-error").find("span").html("");
		}

	}

	//check phone number
	function checkPhone($input){
		var view = this;
		var $controls = $input.closest("div");
		var phoneRex = new RegExp("^[0-9]*$");
		if($input.val() && !phoneRex.test($input.val())){
		   $controls.addClass("has-error").find("span").html("the phone should be numeric character");
		} else {
			$controls.removeClass("has-error").find("span").html("");
		}
	}

	//check birthday format
	function checkBirthday($input){
		var view = this;
		var $controls = $input.closest("div");
		if($input.val() && !$input.val().match(/^((?:19|20)\d\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/)){
		   $controls.addClass("has-error").find("span").html('birthday must be an format "YYYY-MM-DD"');
		} else {
			$controls.removeClass("has-error").find("span").html("");
		}
	}

	})(jQuery);
})();
