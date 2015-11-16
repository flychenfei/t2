;
(function ($) {

	brite.registerView("GoogleContacts",{
		parent:".GoogleScreen-content",
		emptyParent:true
	},{
		// --------- View Interface Implement--------- //
		create: function (data, config) {
			if(data && data.search) {
				this.search = data.search;
			}else{
				this.search = app.googleApi.getContacts;
			}
			return app.render("tmpl-GoogleContacts");
		},

		postDisplay: function (data, config) {
			var view = this;
			showContacts.call(view);
		},
		// --------- /View Interface Implement--------- //

		// --------- Events--------- //
		events: {
			//event for contact add button
			"click;.btnAdd":function(e) {
				brite.display("CreateContact", null, {
					id : null
				});
			},

			//event for search contacts button
			"btap; .inputValueBtn":function () {
				var view = this;
				var input = view.$el.find("input[name='contactName']");
				//check if have conatct name
				if (input.val() == "") {
					input.focus();
					input.closest("div").addClass("error").find("span").html("Please enter value.");
				} else {
					var contactName = input.val();
					view.search = function(opts) {
						opts = opts || {};
						opts.contactName = contactName;
						return app.googleApi.searchContact(opts);
					};
					showContacts.call(view);
					input.closest("div").removeClass("error");
					input.closest("div").find("span").html("");
				}
			},
		},
		// --------- Events--------- //

		// --------- Document Events--------- //       
		docEvents: {
			//refresh conatct
			"DO_REFRESH_CONTACT":function(){
				var view = this;
				showContacts.call(view);
			},

			//delete contact
			"DELETE_CONTACT": function(event, extraData) {
				var view = this;
				var $listItem = view.$el.find(".listItem");
				if($listItem.hasClass("deleting")){
					return;
				}
				var $listItem = view.$el.find(".listItem");
				$listItem.addClass("deleting");

				if (extraData && extraData.objId) {
					var contactId = getContactId(extraData.objId);
					var etag = $(extraData.event.currentTarget).closest("tr").attr("etag");
					var dfd = $.Deferred();
					app.googleApi.deleteContact(contactId, etag).done(function (extradata) {
						if (extradata && extradata.result) {
							setTimeout((function () {
								$(document).trigger("DO_REFRESH_CONTACT");
								setTimeout((function(){
									dfd.resolve();
								}), 3000);
							}), 3000);
						}
						dfd.done(function () {
							$listItem.removeClass("deleting");
						});
					});
				}

			},

			//edit contact
			"EDIT_CONTACT": function(event, extraData){
				var view = this;
				if(view.$el.find(".listItem").hasClass("deleting")){
					return;
				}
				if (extraData && extraData.objId) {
					var contactId = getContactId(extraData.objId);
					var etag = $(extraData.event.currentTarget).closest("tr").attr("etag");

					app.googleApi.getContact({contactId:contactId, etag:etag}).done(function (data) {
						if(data && data.result){
							if(data.result.id) {
								data.result.id = getContactId(data.result.id);
							}
							brite.display("CreateContact", null, data.result);
						}
					});
				}
			}
		},
		// --------- /Document Events--------- //
	});

	// --------- Private Methods --------- //
	//get groupId
	function getGroupId(url) {
		var myregexp = /http:\/\/www.google.com\/m8\/feeds\/groups\/(.+)\/base\/(.+)/;
		var match = myregexp.exec(url);
		if (match != null) {
			result = match[2];
		} else {
			result = "";
		}
		return result;
	}

	//get contactId
	function getContactId(url) {
		var myregexp = /http:\/\/www.google.com\/m8\/feeds\/contacts\/(.+)\/base\/(.+)/;
		var match = myregexp.exec(url);
		if (match != null) {
			result = match[2];
		} else {
			result = "";
		}
		return result;
	}

	//show contacts in the DataTable
	function showContacts() {
		var view = this;
		brite.display("DataTable", ".contacts-container", {
			dataProvider: {list: view.search},
			rowAttrs: function (obj) {
				return " etag='{0}'".format(obj.etag)
			},
			columnDef: [
				{
					text: "#",
					render: function (obj, idx) {
						return idx + 1
					},
					attrs: "style='width: 10%'"
				},
				{
					text: "Emails",
					render: function (obj) {
						return obj.email
					},
					attrs: "style='width: 400px'"

				},
				{
					text: "Full Name",
					render: function (obj) {
						return (typeof obj.fullName == "undefined") ? "" : obj.fullName;
					},
					attrs: "style='width: 25%'"
				},
				{
					text: "Group",
					render: function (obj) {
						return getGroupId(obj.groupId)
					}
				},
				{
					text:"Edit",
					attrs:"style='width: 60px; cursor:pointer; text-align: center;'",
					render:function(obj){
						return "<div class='glyphicon glyphicon-edit' data-cmd='EDIT_CONTACT'/>";
					},
				},
				{
					text:"Delete",
					attrs:"style='width: 60px;cursor:pointer; text-align: center;'",
					render:function(obj){
						return "<div class='glyphicon glyphicon-remove' data-cmd='DELETE_CONTACT'/>";
					},
				},
			],
			opts: {
				htmlIfEmpty: "Not contacts found",
				withPaging: true,
				withCmdDelete:false
			}
		});
	}
	// --------- Private Methods --------- //
})(jQuery);