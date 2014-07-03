;
(function ($) {

    brite.registerView("GoogleDocs",{parent:".GoogleScreen-content",emptyParent:true}, {
        create: function (data, config) {
            return app.render("tmpl-GoogleDocs");
        },

        postDisplay: function (data, config) {
            var view = this;
            showGroups.call(view);
        },

        events: {
          "click;.btnAdd":function(e){
             brite.display("CreateDoc",null,null);
          }
        },

        docEvents: {
            "DO_REFRESH_DOCS":function(){
                 var view = this;
                 showGroups.call(view);
             },
            "DELETE_DOC": function(event, extraData){
                alert("NOT implement yet");
            }
        },

        daoEvents: {
        }
    });
    function showGroups() {
        var groups = app.googleApi.getGroups();
        return brite.display("DataTable", ".docs-container", {
        	dataProvider: {list: app.googleDocsApi.getDocsList},
            columnDef:[
                {
                    text:"#",
                    render: function(obj, idx){return idx + 1},
                    attrs:"style='width: 10%'"
                },
                {
                    text:"Name",
                    attrs: " data-cmd='DO_REFRESH_CONTACT' style='cursor:pointer;width:40%' ",
                    render:function(obj){return obj.name}

                },
                {
                    text:"CreateTime",
                    render:function(obj){return obj.createTime}

                },
                {
                    text:"Type",
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