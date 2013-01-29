;(function() {

	/**
	 * View: MailInfo
	 *
	 */
	brite.registerView("GoogleMailInfo", {
		parent : "body"
	}, {
		
		create : function(data, config) {
            console.log(data);
			var view = this;
			var dfd = $.Deferred();
			var createDfd = $.Deferred();
			data = data || {};
			if (data.id) {
				app.googleApi.getMail(data.id).done(function(data) {
					dfd.resolve(data.result);
				});
			} else {
				dfd.resolve({});
			}
			
			$.when(dfd).done(function(mail) {
				console.log(mail);
				var $html = app.render("tmpl-GoogleMailInfo",mail);
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
	 		}
		},

		close : function(update) {
			var view = this;
			var $e = view.$el;

			$e.bRemove();
			view.$screen.remove();
		}
	});

	// --------- View Private Methods --------- //

	// --------- /View Private Methods --------- //

})();
