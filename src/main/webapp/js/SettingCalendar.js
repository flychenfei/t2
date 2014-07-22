;
(function () {

    /**
     * Component: CreateTable
     */
    (function ($) {

        brite.registerView("SettingCalendar", {
            loadTmpl:true,
            parent:".MainScreen",
            emptyParent:false
        }, {
            create:function (opts, config) {
            	view = this;
            	opts = opts || {};
            	var dfd = $.Deferred();
                app.googleApi.getSetting(opts).done(function(data){
	                var html = app.render("tmpl-SettingCalendar",data.result||{});
	                var $e = $(html);
	                dfd.resolve($e);
	        	});
	        	return dfd.promise();
            },
            postDisplay:function (data, config) {
                var view = this;
                var $e = view.$el;
                var mainScreen = view.mainScreen = $e.bComponent("MainScreen");
            },
			events:{
				"btap; .btnClose": function(){
		 			var view = this;
		 			view.close();
		 		}
			},
            close:function () {
                var $e = this.$el;
                $e.bRemove();
            },

        })
    })(jQuery);
})();
