;(function() {

	/**
	 * View: SalesForceContacts
	 *
	 */
    (function ($) {
        brite.registerView("SalesForceContacts",  {loadTmpl:true,emptyParent:true,parent:".SalesForceScreen-content"}, {
            create:function (data, config) {
                var $html = app.render("tmpl-SalesForceContacts",data);
                var $e = $($html);
                return $e;
            },
            postDisplay:function (data, config) {
                var view = this;
                var $e = view.$el;
                
                refresh.call(view);
            },
            events:{
            	"click;.btnAdd":function(e){
            		brite.display("SalesForceContactInfo",null,{id:null});
            	},
            },

            docEvents:{
            	"DO_REFRESH_CONTACT":function(){
            		refresh.call(this);
            	},
            	"DELETE_CONTACT": function(event, extraData) {
            	  var view = this;
                if (extraData && extraData.objId) {
                  app.sf.deleteContact(extraData.objId).done(function() {
                    refresh.call(view);
                  });
                }
              },
              "EDIT_CONTACT": function(event, extraData){
            	  var view = this;
                if (extraData && extraData.objId) {
                  brite.display("SalesForceContactInfo",null,{id:extraData.objId});
                }
              }
            },

            daoEvents:{
            }
        });
        
        function refresh(){
        	var view = this;
        	var $e = view.$el;
           
           brite.display("DataTable", ".dataTableContainer", {
            dataProvider: {list: app.sf.listContacts},
            dataType:"Contacts",
            idKey:"Id",
            columnDef: [
                {
                    text: "#",
                    render: function (obj, idx) {
                        return idx + 1
                    },
                    attrs: "style='width: 10%'"
                },
                {
                    text: "Name",
                    render: function (obj) {
                        return obj.Name
                    },
                    attrs: "style='width: 400px'"

                },
            ],
            opts: {
                htmlIfEmpty: "Not contacts found",
                withPaging: true,
                cmdDelete: "DELETE_CONTACT",
                cmdEdit: "EDIT_CONTACT"
            }
        });
           
      }
    })(jQuery);


})();
