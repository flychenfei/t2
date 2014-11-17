(function(){
	
	brite.registerView("LiveContacts",{emptyParent:true, parent:".LiveScreen-content"},{
		create: function(data,config){
			return render("tmpl-LiveContacts");
		},
		postDisplay: function () {
			var view = this;
			showContacts.call(view);
		},
		events:{
			"click;.btnAdd":function(e){
				brite.display("LiveCreateContact", null, {id: null});
			},
			"click;.btnFriends":function(){
				showContacts(true);
			}
			
		},
        docEvents: {
            "DO_REFRESH_CONTACT":function(){
                 var view = this;
                 showContacts.call(view);
            },
            "EDIT_CONTACT": function(event, extraData){
                if (extraData && extraData.objId) {
                    brite.display("LiveCreateContact", null, {id: extraData.objId});
                }
            }
         }
	});

	function showContacts(isFriend) {
		var view = this;
		var listFunction = function(){
			var opt = {};
			if(typeof isFriend != 'undefined'){
				opt.isFriend = isFriend;
			}
			return app.liveContactApi.getUserContactlist(opt).pipe(function(result){
				return {result: result.result.data};
			});
		}
		brite.display("DataTable", ".contactLists", {
			dataProvider: {list: listFunction},
			columnDef: [
				{
					text: "#",
					render: function (obj, idx) {
						return idx + 1;
					},
					attrs: "style='width: 10%'"
				},
				{
					text: "Full Name",
					render: function (obj) {
						return obj.name;
					},
					attrs: "style='width: 25%'"
				},
				{
					text: "Updated Time",
					render: function (obj) {
						return obj.updated_time;
					},
					attrs: "style='width: 200px'"

				},
				{
					text: "Birthday Day",
					render: function (obj) {
						return obj.birth_day;
					},
					attrs: "style='width: 10%'"
				},
				{
					text: "Birthday Month",
					render: function (obj) {
						return obj.birth_month;
					},
					attrs: "style='width: 10%'"
				},
				{
					text: "Friend",
					render: function (obj) {
						return obj.is_friend;
					},
					attrs: "style='width: 10%'"
				},
				{
					text: "Favorite",
					render: function (obj) {
						return obj.is_favorite;
					},
					attrs: "style='width: 10%'"
				}
			],
			opts: {
				htmlIfEmpty: "Not contacts found",
				withPaging: false,
				withCmdDelete: false,
				cmdEdit: "EDIT_CONTACT"
			}
		});
	}
})();