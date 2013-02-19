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
      			app.twitterApi.getDirectMsg().pipe(function(data) {
      				var messages = JSON.parse(data.result);
      				var $html = app.render("message-template", {messages : messages});
      	            var $e = $($html);
      	            $(".direct-messages-container").html($e);
      			})
         	},
         	
        	"click;.showMsg" : function() {
         		var msg_id = $(".msg_id").val();
         		if(msg_id.length > 0 ) {
         			app.twitterApi.showMsg({msg_id : msg_id}).pipe(function(data) {
         				var messages = data.result;
         				var $html = app.render("message-template", {messages : messages});
         	            var $e = $($html);
         	            $(".message-container").html($e);
         			})
         		}
         	},
         	
         	"click;.getSendMsg" : function() {
         		app.twitterApi.getSendMsg().pipe(function(data) {
      				var messages = JSON.parse(data.result);
      				var $html = app.render("message-template", {messages : messages});
      	            var $e = $($html);
      	            $(".send-messages-container").html($e);
      			})
         	}
         	
         }
         
     });
})();