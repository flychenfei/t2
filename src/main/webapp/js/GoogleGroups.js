;
(function ($) {

	brite.registerView("GoogleGroups",{
		parent:".GoogleScreen-content",
		emptyParent:true
	}, {
		// --------- View Interface Implement--------- //
		create: function (data, config) {
			return app.render("tmpl-GoogleGroups");
		},

		postDisplay: function (data, config) {
			var view = this;
			showGroups.call(view);
		},
		// --------- /View Interface Implement--------- //

		// --------- Events--------- //
		events: {
			//event for group add button
			"click;.btnAdd":function(e){
				brite.display("CreateGroup",null,null);
			},

			//event for group edit icon
			"click; .glyphicon-edit": function(event){
				var view = this;
				if(view.$el.find(".listItem").hasClass("deleting")){
					return;
				}
				var $row = $(event.currentTarget).closest("tr");
				var title = $row.attr("data-title");
				var etag = $row.attr("data-etag");
				var $id = $row.attr("data-obj_id");
				var groupId = getGroupId($id);
				brite.display("CreateGroup", null, {groupId:groupId, title:title, etag:etag});
			},

			//event for group delete icon
			"click; .glyphicon-remove": function(event){
				var view = this;
				var $listItem = view.$el.find(".listItem");
				
				if($listItem.hasClass("deleting")){
					return;
				}

				$listItem.addClass("deleting");
				var $row = $(event.currentTarget).closest("tr");
				var etag = $row.attr("data-etag");
				var $id = $row.attr("data-obj_id");
				var groupId = getGroupId($id);

				var dfd = $.Deferred();
				app.googleApi.deleteGroup(groupId, etag).done(function (extradata) {
					if (extradata && extradata.result) {
						setTimeout((function () {
							showGroups();
							setTimeout((function(){
								dfd.resolve();
							}), 6000);
						}), 3000);
					}
					dfd.done(function(){
						$listItem.removeClass("deleting");
					});
				});
		 	},

		 	//event for click group name
		 	"click; .groupName": function(event){
		 		var view = this;
		 		var $tr = $(event.currentTarget).closest("tr");
		 		var $id = $tr.attr("data-obj_id");
		 		brite.display("GoogleContacts",null,{groupId:$id});
		 	}
		},

		// --------- /Events--------- //
		// --------- Document Events--------- //
		docEvents: {
			//refresh groups
			"DO_REFRESH_GROUPS":function(){
				var view = this;
				showGroups.call(view);
			},

			//edit group
			"EDIT_GROUP":function(event, extraData){
				if (extraData && extraData.objId) {
					var groupId = getGroupId(extraData.objId);
					var $row = $(extraData.event.currentTarget).closest("tr");
					var title = $row.attr("data-title");
					var etag = $row.attr("data-etag");
					brite.display("CreateGroup", null, {groupId:groupId, title:title, etag:etag})
				}
			},

			//delete group
			"DELETE_GROUP": function(event, extraData){
				if (extraData && extraData.objId) {
					var groupId = getGroupId(extraData.objId);
					var etag = $(extraData.event.currentTarget).closest("tr").attr("data-etag");
					app.googleApi.deleteGroup(groupId, etag).done(function (extradata) {
						if (extradata && extradata.result) {
							setTimeout((function () {
								showGroups();
							}), 3000);
						}
					});
				}
			}
		},
		// --------- Document Events--------- //
	});
	// --------- Private Methods --------- //
	//show groups in the DataTable
	function showGroups() {
		var groups = app.googleApi.getGrouplist;
		return brite.display("DataTable", ".groups-container", {
			dataProvider: {list: groups},
			rowAttrs: function(obj){ 
				return "data-type='Group' data-etag='{0}' data-title='{1}'".format(obj.etag, obj.title.text)},
			columnDef:[
				{
					text:"#",
					render: function(obj, idx){return idx + 1},
					attrs:"style='width: 10%'"
				},
				{
					text:"Title",
					attrs: "class='groupName' data-cmd='DO_REFRESH_CONTACT' style='cursor:pointer;width:40%' ",
					render:function(obj){return obj.title.text}
				},
				{
					text:"Etag",
					render:function(obj){return obj.etag}
				},
				{
					text:"Edit",
					attrs:"style='width: 60px; cursor:pointer; text-align: center;'",
					render:function(obj){
						return obj.systemGroup ? "" : "<div class='glyphicon glyphicon-edit'/>";
					},
				},
				{
					text:"Delete",
					attrs:"style='width: 60px; cursor:pointer; text-align: center;'",
					render:function(obj){
						return obj.systemGroup ? "" : "<div class='glyphicon glyphicon-remove'/>";
					},
				}
			],
			opts:{
				htmlIfEmpty: "Not Groups found",
				withPaging: true,
				withCmdDelete:false
			}
		});
	}

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
	// --------- Private Methods --------- //
})(jQuery);