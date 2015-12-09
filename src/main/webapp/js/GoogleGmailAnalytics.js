(function(){

	brite.registerView("GoogleGmailAnalytics",{parent:".GoogleScreen-content",emptyParent:true},{
        // --------- View Interface Implement--------- //
		create: function(data, config){
			return render("tmpl-GoogleGmailAnalytics");
		},

		postDisplay: function (data, config) {
			var view = this;
			showGmailAnalytics.call(view);
		},
        // --------- /View Interface Implement--------- //

        // --------- Document Events--------- //
        docEvents: {
            // show the email info
            "SHOW_INFO": function(event, extra) {
                if(extra.objId){
                    var data = {id: extra.objId, type:'rest'};
                    brite.display("GoogleMailInfo", "body", data);
                }
            }
        }
        // --------- /Document Events--------- //

	});

    // --------- Private Methods --------- //
	function showGmailAnalytics() {
        return brite.display("DataTable", ".GoogleGmailAnalytics", {
            dataProvider: {list: function(params){
                return app.googleApi.getGmailAnalytics(params);
            }},
            rowAttrs: function(obj){
                return "data-id='"+obj.messageId+"'"
            },
            idKey: 'messageId',
            columnDef:[
                {
                    text:"#",
                    render: function(obj, idx){
                        return idx + 1
                    },
                    attrs:"style='width: 5%'"
                },
                {
                    text:"Message Subject",
                    attrs: "style='width: 10%;'",
                    render:function(obj){
                        if(obj.messageSubject){
                            return "<div class='click-able'  data-cmd='SHOW_INFO'>"+obj.messageSubject+"</div>";
                        }
                        return "No Subject";
                    }
                },
                {
                    text:"Convetsation Name",
                    attrs: "style='width: 10%;'",
                    render:function(obj){
                        if(obj.conversation){
                            return obj.conversation;
                        }
                        return "No Convetsation name";
                    }

                },
                {
                    text:"Sender Time",
                    attrs: "style='width: 8%;'",
                    render:function(obj){
                        if(obj.senderTimeStamp){
                            return obj.senderTimeStamp;
                        }
                        return "No Sender Time";
                    }

                },
                {
                    text:"Recipient Time",
                    attrs: "style='width: 8%;'",
                    render:function(obj){
                        if(obj.recipientTimeStamp){
                            return obj.recipientTimeStamp;
                        }
                        return "No Recipient Time";
                    }

                },
                {
                    text:"Sender Email Address",
                    attrs: "class='normal-cell'",
                    render:function(obj){
                        return obj.senderEmailAddress
                    }
                },
                {
                    text:"Recipient Email Address",
                    attrs: "class='normal-cell'",
                    render:function(obj){
                        return obj.recipientEmailAddress
                    }
                },
                {
                    text:"Message Type",
                    attrs: "class='normal-cell'",
                    render:function(obj){
                        return obj.messageType
                    }
                },
                {
                    text:"Recipient Type",
                    attrs: "class='normal-cell'",
                    render:function(obj){
                        return obj.recipientType
                    }
                },
                {
                    text:"Attachments Number",
                    attrs: "style='width: 5%;'",
                    render:function(obj){
                        return obj.countOfAttachments
                    }
                },
                {
                    text:"Message Size",
                    attrs: "style='width: 5%;'",
                    render:function(obj){
                        return obj.messageSize
                    }
                },
                {
                    text:"Message Length",
                    attrs: "style='width: 5%;'",
                    render:function(obj){
                        return obj.messageLength
                    }
                }
            ],
            opts:{
                htmlIfEmpty: "Not Gmail Analytics found!",
                withPaging: true,
                withCmdDelete:false
            }
        });
    }
    // --------- /Private Methods --------- //
})();