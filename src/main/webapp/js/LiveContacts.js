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
			
		}
	});

	function showContacts() {
		var view = this;
		var listFunction = function(){
			return app.liveOutLookApi.getUserContactlist().pipe(function(result){
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
					attrs: "style='width: 400px'"

				},
			],
			opts: {
				htmlIfEmpty: "Not contacts found",
				withPaging: false,
				withCmdDelete: false,
				withCmdEdit: false
			}
		});
	}
})();