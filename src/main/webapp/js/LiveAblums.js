(function(){
	
	brite.registerView("LiveAblums",{emptyParent:true, parent:".LiveScreen-content"},{
		create: function(data,config){
			return render("tmpl-LiveAblums");
		},
		postDisplay: function () {
			var view = this;
			showAblums.call(view);
		},
		events:{
			"click;.btnAdd":function(e){
				brite.display("LiveCreateAblum", null, {id: null});
			},
		},
		docEvents: {
            "DO_REFRESH_ABLUM":function(){
                 var view = this;
                 showAblums.call(view);
            }
         }
	});

	function showAblums() {
		var view = this;
		var listFunction = function(){
			return app.liveAblumApi.getUserAblums().pipe(function(result){
				return {result: result.result.data};
			});
		}
		brite.display("DataTable", ".ablumsLists", {
			dataProvider: {list: listFunction},
			columnDef: [
				{
					text: "#",
					render: function (obj, idx) {
						return idx + 1;
					},
					attrs: "style='width: 25px'"
				},
				{
					text: "Name",
					render: function (obj) {
						return obj.name;
					},
					attrs: "style='width: 15%'"
				},
				{
					text: "Description",
					render: function (obj) {
						return obj.description;
					},
					attrs: "style='width: 30%'"

				},
				{
					text: "Count",
					render: function (obj) {
						return obj.count;
					},
					attrs: "style='width: 25px'"
				},
				{
					text: "From Name",
					render: function (obj) {
						return obj.from.name;
					},
					attrs: "style='width: 15%'"
				},
				{
					text: "Created Time",
					render: function (obj) {
						return obj.created_time;
					},
					attrs: "style='width: 20%'"
				},
				{
					text: "Updated Time",
					render: function (obj) {
						return obj.updated_time;
					},
					attrs: "style='width: 20%'"
				}
			],
			opts: {
				htmlIfEmpty: "Not ablums found",
				withPaging: false,
				withCmdDelete: false,
				withCmdEdit: false
			}
		});
	}
})();