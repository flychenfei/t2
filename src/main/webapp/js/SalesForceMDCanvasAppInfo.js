;(function() {

	/**
	 * View: SalesForceMDCanvasAppInfo
	 *
	 */
	brite.registerView("SalesForceMDCanvasAppInfo", {
		loadTmpl : true,
		emptyParent: true,
		parent : ".SalesForceScreen-content"
	}, {
		
		create : function(data, config) {
			var view = this;
			data = data || {};

			return app.render("tmpl-SalesForceMDCanvasAppInfo");
		},
		
		events : {
		  "btap;.btnCreate":function(){
		    saveApp.call(this);
		  }
		}

	});

	// --------- View Private Methods --------- //
  function saveApp(){
    var view = this;
    var $e = view.$el;
    
    var contactMail = $e.find("input[name='contactMail']").val();
    var canvasUrl = $e.find("input[name='canvasUrl']").val();
    var appName = $e.find("input[name='appName']").val();
    
    var params = {
      canvasUrl:canvasUrl,
      contactMail:contactMail,
      appName:appName
    };
    
    app.sf.saveCavnasApp(params).done(function(){
      alert("create success!");
    });
  }
	// --------- /View Private Methods --------- //

})();
