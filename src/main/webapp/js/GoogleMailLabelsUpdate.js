;(function() {

	/**
	 * View: MailInfo
	 *
	 */
	brite.registerView("GoogleMailLabelsUpdate", {
		parent : "body"
	}, {
		
		create : function(data, config) {
			var view = this;
			var dfd = $.Deferred();
			var createDfd = $.Deferred();
			data = data || {};
			view.id = data.id;
			if (data.id) {
				app.googleApi.getMailRest(data.id).done(function(data) {
					var mail = data.result;
					app.googleApi.listLabelsRest().done(function(labelsRet){
						var labels = labelsRet.result;
						for(var i = 0; i < labels.length; i++){
							for(var j = 0; j < mail.folderIds.length; j++){
								if(mail.folderIds[j] == labels[i].id){
									labels[i].checked = true;
									break;
								}
							}
						}
						mail.labels = labels;
						dfd.resolve(mail);
					});
				}); 

			} else {
				dfd.resolve({});
			}
			
			$.when(dfd).done(function(mail) {
				view.obj = mail;
				var $html = app.render("tmpl-GoogleMailLabelsUpdate",mail);
				//show a screen to prevent use click other places
				view.$screen = $("<div class='notTransparentScreen'></div>").appendTo("body");
				createDfd.resolve($html);
			});

			return createDfd.promise();
		},
		
		events : {
	 		"btap; .btnClose": function(){
	 			var view = this;
	 			view.close();
	 		},
	 		"btap; .btnUpdate": function(e){
	 			var view = this;
	 			var $e = view.$el;
	 			var $btn = $(e.currentTarget);
	 			var addLabels = [];
	 			var removeLabels = [];
	 			var newCheckedLabels = [];
	 			$e.find("input[name='label']:checked").each(function(){
	 				var val = $(this).val();
	 				if($.inArray(val, view.obj.folderIds) == -1){
	 					addLabels.push(val);
	 				}
	 				newCheckedLabels.push(val);
	 			});
	 			
	 			for(var i = 0; i < view.obj.folderIds.length; i++){
	 				if($.inArray(view.obj.folderIds[i], newCheckedLabels) == -1){
	 					removeLabels.push(view.obj.folderIds[i]);
	 				}
	 			}
	 			var opts = {
	 				id : view.id,
	 				addLabels : addLabels.join(","),
	 				removeLabels : removeLabels.join(",")
	 			};
	 			console.log(opts);
	 			app.googleApi.updateLabelsRest(opts).done(function(){
	 				$(document).trigger("DO_REFRESH_CURRENT_PAGE");
	 				view.close();
	 			});
	 		}
		},

		close : function(update) {
			var view = this;
			var $e = view.$el;

			$e.bRemove();
			view.$screen.remove();
		}
	});


})();
