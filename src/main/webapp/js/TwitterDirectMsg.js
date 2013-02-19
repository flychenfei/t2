(function() {
	 brite.registerView("TwitterDirectMsg",  {emptyParent:true}, {
         create:function (data, config) {
    		var $html = app.render("tmpl-TwitterDirectMsg");
            var $e = $($html);
            return $e;
         },
         postDisplay:function (data, config) {
         	
         },
         
         events:{
        	 "click;.getDirectMsg" : function() {
        		 console.log("getDirectMsg clicked");
      			app.twitterApi.getDirectMsg().pipe(function(data) {
      				var messages = JSON.parse(data.result);
      				console.log(messages);
      				var $html = app.render("message-template", {messages : messages});
      	            var $e = $($html);
      	            $(".direct-messages-container").html($e);
      			})
         	}
         	
         }
         
     });
})();