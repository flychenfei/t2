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
         	},
         	
         	"click;.sendMsg" : function() {
         		var screen_name = $(".screen_name").val();
         		var text = $(".text").val();
         		if(screen_name.length > 0 && text.length>0) {
         			app.twitterApi.sendMsg({screen_name : screen_name, text : text}).pipe(function(data) {
          				var message = data.result;
          				var messages = [];
          				messages[0] = message;
          				var $html = app.render("message-template", {messages : messages});
          	            var $e = $($html);
          	            $(".send-container").html($e);
          			})
         		}
         	}
         	
         }
         
     });
})();