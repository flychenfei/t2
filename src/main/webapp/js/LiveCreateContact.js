;
(function () {

	(function ($) {

		brite.registerView("LiveCreateContact", {
			loadTmpl:true,
			parent:"body",
			emptyParent:false
		}, {
			create:function (data, config) {
				var view = this;
				data = data || {};
				if(data) {
					this.contactId = data.id;
				}
				
				var dfd = $.Deferred();
				var createDfd = $.Deferred();
				
				if(view.contactId){
					app.liveContactApi.getContact(view.contactId).done(function(extraData) {
						dfd.resolve(extraData.result);
					});
				}else{
					dfd.resolve({});
				}
				
				dfd.done(function(obj){
					html = app.render("tmpl-LiveCreateContact",obj||{});
					var $e = $(html);
					createDfd.resolve($e);
				});
				return createDfd.promise();
			},
			postDisplay:function (data, config) {
				var view = this;
				var $e = view.$el;
				$e.find("form").find("input[type=text]").focus();
			},

			close:function () {
				this.$el.remove();
			},

			submit:function () {
				var view = this;
				var $e = this.$el;
				var $controls = $e.find(".controls input,.controls textarea");
				var contact = {};
				$controls.each(function(idx, obj){
					var $this = $(this);
					contact[$this.attr("name")] = $this.val();
				});
				var input = $e.find("input[name='first_name']");
				if (input.val() == "") {
					input.focus();
					input.closest("div").addClass("has-error").find("span").html("Please enter valid contact first name.");
				} else {
					app.liveContactApi.saveContact({id: view.contactId, contactJson: JSON.stringify(contact)}).done(function (extraData) {
						setTimeout((function () {
							$(document).trigger("DO_REFRESH_CONTACT");
						}), 5000);
						view.close();
					});

				}
			},

			events:{
				"btap; .createContactBtn":function () {
					var view = this;
					var $e = view.$el;
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
				"btap; .cancelBtn":function () {
					var view = this;
					view.close();
				}
			}
		})
	})(jQuery);
})();
