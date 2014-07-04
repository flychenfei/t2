;
(function ($) {
    brite.registerView("GoogleDocs",{parent:".GoogleScreen-content",emptyParent:true}, {
        create: function (data, config) {
        	 if(data && data.results) {
                 this.results = data.results;
             }else{
                 this.results = app.googleDocsApi.getDocsList;
             }
            return app.render("tmpl-GoogleDocs");
        },

        postDisplay: function (data, config) {
            var view = this;
            showDocs.call(view);
        },

        events: {
          "click;.btnSearch":function(e){
        	  brite.display("InputValue", ".MainScreen", {
                  title: 'Search Doc',
                  fields: [
                      {label:"FileName", name:'title', mandatory:true}
                  ],
                  callback: function (params) {
                      brite.display("GoogleDocs",".GoogleScreen-content",{
                    	  results: function(opts){
                             opts = opts||[];
                              $.extend(opts, params)
                             return app.googleDocsApi.searchDocs(opts)
                         }
                      });
                  }});
          }
        },

        docEvents: {
            "DO_REFRESH_DOCS":function(){
                 var view = this;
                 showDocs.call(view);
             },
            "DELETE_DOC": function(event, extraData){
            	var parma = {};
            	parma.resourceId = $(extraData.event.currentTarget).closest("tr").attr("data-resourceId");
            	parma.etag = $(extraData.event.currentTarget).closest("tr").attr("data-etag");
                app.googleDocsApi.deleteDoc(parma).done(function (success) {
                    if(success){
                    	alert("Delete success");
                    }else{
                    	alert("Delete fail");
                    }
                    brite.display("GoogleDocs",".GoogleScreen-content");
                });
            }
        },

        daoEvents: {
        }
    });
    function showDocs() {
    	var view = this;
        return brite.display("DataTable", ".docs-container", {
        	dataProvider: {list: view.results},
        	rowAttrs: function (obj) {
                return " data-resourceId='{0}' data-etag='{1}'".format(obj.resourceId,obj.etag)
            },
            columnDef:[
                {
                    text:"#",
                    render: function(obj, idx){return idx + 1},
                    attrs:"style='width: 10%'"
                },
                {
                    text:"FileName",
                    attrs: "style='width:30%'",
                    render:function(obj){return obj.name}

                },
                {
                    text:"Last UpdateTime",
                    attrs: "style='width:30%'",
                    render:function(obj){return obj.updateTime}

                },
                {
                    text:"Type",
                    attrs: "style='width:20%'",
                    render:function(obj){return obj.type}

                }
            ],
            opts:{
                htmlIfEmpty: "Not Docs found",
                withPaging: true,
                cmdDelete: "DELETE_DOC"
            }
        });
    }
    
})(jQuery);