;
(function ($) {

    brite.registerView("GoogleSearchMails",{parent:".GoogleScreen-content",emptyParent:true}, {
        create: function (data, config) {
            if(data && data.search) {
                this.search = data.search;
            }else{
                this.search = app.googleApi.getEmails;
            }
            return app.render("tmpl-GoogleSearchMails");
        },

        postDisplay: function (data, config) {
            var view = this;
            
            $('.datetimepicker').datetimepicker({ 
                format: 'yyyy-MM-dd', 
                language: 'en', 
                 pickDate: true, 
                 pickTime: true, 
                 inputMask: true 
            }); 
        },
        events: {
        	"btap; .inputValueBtn":function () {
                var view = this;
                var $e = view.$el;
                //view.submit();
                var result = {};
                $e.find(":text").each(function(){
                	if($(this).val() !== ""){
                	  result[$(this).attr("name")] = $(this).val();
                	}
                });
                console.log(result)	;
                brite.display("GoogleMails",".mails-container",{
                   search: function(opts){
                       opts = opts||[];
                        $.extend(opts, result)
                       return app.googleApi.searchEmails(opts)
                   }
                });
            }
        },

        docEvents: {
        	
        },
        daoEvents: {
        }
    });
})(jQuery);