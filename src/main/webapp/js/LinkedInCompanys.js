;
(function ($) {
    brite.registerView("LinkedInCompanys",{parent:".LinkedInScreen-content",emptyParent:true}, {
        create: function (data, config) {
        	return app.render("tmpl-LinkedInCompanys");
        },

        postDisplay: function () {
        	var view = this;
            showFollowedCompanys.call(view);
        },
        events:{
        	"btap; .followed-companys":function () {
                var view = this;
                showFollowedCompanys.call(view);
        	},
        	"btap; .suggests-followed-companys":function () {
                var view = this;
                showSuggestsFollowedCompanys.call(view);
            },
        	"btap; .StartFollowing":function (e) {
                var view = this;
                var param = {};
                param.companyId = $(e.target).closest("tr").attr("data-companyId");
                app.linkedInApi.StartFollowingCompany(param).done(function (result) {
                    if(result.success === true){
                        showSuggestsFollowedCompanys.call(view);
                    }else{
                    	alert("Following failed!");
                    }
                });
        	},
        	"btap; .StopFollowing":function (e) {
        		var view = this;
                var param = {};
                param.companyId = $(e.target).closest("tr").attr("data-companyId");
                app.linkedInApi.StopFollowingCompany(param).done(function (result) {
                    if(result.success === true){
                    	showFollowedCompanys.call(view);
                    }else{
                    	alert("Stop following failed!");
                    }
                });
        	},
        	"btap; .companyUpdates":function (e) {
                var view = this;
                var companyId = $(e.target).closest("tr").attr("data-companyId");
                showCompanysUpdates.call(view,companyId);
            },
            "btap; .commentUpdates":function (e) {
                var view = this;
                var updateKey = $(e.target).closest("tr").attr("data-updateKey");
                brite.display("InputValue", ".MainScreen",
                    {title:'Commenting On Updates',
                    fields:[{label: "Comment", name: 'comment'}],callback:function(data){
                            var params = {};
                            params.updateKey = updateKey;
                            params.comment = data.comment;
                            app.linkedInApi.CommentCompanyUpdates(params).done(function (result) {
                                if(result.success === true){
                                    alert("Commenting On Updates success!");
                                }else{
                                    alert("Commenting On Updates failed!");
                                }
                            });
                    }});
            },
            "btap; .likeUpdates":function (e) {
                var view = this;
                var $operator = $(e.target);
                var param = {};
                param.updateKey = $operator.closest("tr").attr("data-updateKey");
                param.like = $operator.attr("value");
                app.linkedInApi.LikeCompanyUpdates(param).done(function (result) {
                    if(result.success === true){
                        alert("Operator success!");
                        if(param.like == "like"){
                            $operator.attr("value","dislike");
                            $operator.text("dislike");
                        }else{
                            $operator.attr("value","like");
                            $operator.text("like");
                        }
                    }else{
                        alert("Operator failed!");
                    }
                });
            }
        }
    });
    
	//----------------------- private method --------------------
    function showFollowedCompanys() {
        var view = this;
        app.linkedInApi.followedCompanys().done(function (result) {
                if(result.success === true){
                    var renderResult = renderdata(result.result,true);
                    showCompanys.call(view,{result:renderResult});
                }else{
                    alert("Can't get the followed Companys!");
                }
        });
    }
    
    function showSuggestsFollowedCompanys() {
       var view = this;
        app.linkedInApi.suggestsFollowedCompanys().done(function (result) {
                if(result.success === true){
                    var renderResult = renderdata(result.result,false);
                    showCompanys.call(view,{result:renderResult});
                }else{
                    alert("Can't get the suggests following Companys!");
                }
        });
    }
    
    function renderdata(result,following) {
        var view = this;
        var renderResult = [];
        for (var i = 0; i < result.length; i++) {
            var item = result[i];
            var rowData = {};
            rowData.number = i+1;
            rowData.id = item.id;
            rowData.name = item.name;
            rowData.universalName = item.universalName;
            rowData.websiteUrl = item.websiteUrl;
            rowData.description = item.description;
            rowData.numFollowers = item.numFollowers;
            var locations = item.locations;
            if(locations && locations.values && locations.values.length > 0){
                var value = locations.values[0];
                rowData.location = "{0}-{1}(postalCode:{2})".format(value.address.city,value.address.street1,value.address.postalCode);
            }else{
                rowData.location = "NONE";
            }
            rowData.isfollowing = following;
            renderResult.push(rowData);
        };
        return renderResult;
    }

    function showCompanys(data){
        var view = this;
        var $content = view.$el.find(".LinkedInCompanys-body");
        $content.empty();
        var $renderContent = $(render("tmpl-LinkedInCompanyLists",{result:data.result,total:data.result.length}));
        $content.append($renderContent);
    }

    function showCompanysUpdates(companyId) {
        var view = this;
        brite.display("DataTable", ".LinkedInCompanys-body",{
            dataProvider: {list: function(params){
                params.companyId = companyId;
                return app.linkedInApi.CompanyUpdates(params);
            }},
            rowAttrs: function (obj) {
                return "data-updateKey='{0}' data-companyCommentId='{1}'".format(obj.updateKey, obj.updateContent.companyStatusUpdate.share.id)
            },
            columnDef: [
                        {
                            text: "#",
                            render: function (obj, idx) {
                                return idx + 1;
                            },
                            attrs: "style='width: 5%'"
                        },
                        {
                            text: "CompanyName",
                            render: function (obj) {
                                return obj.updateContent.company.name;
                            },
                            attrs: "style='width: 10%'"

                        },
                        {
                            text: "Comment Theme",
                            render: function (obj) {
                                return obj.updateContent.companyStatusUpdate.share.comment;
                            },
                            attrs: "style='width: 20%'"

                        },
                        {
                            text: "Content Description",
                            render: function (obj) {
                                return obj.updateContent.companyStatusUpdate.share.content.description;
                            },
                            attrs: "style='width: 25%'"

                        },
                        {
                            text: "eyebrowUrl",
                            render: function (obj) {
                                var strUrl = "<span><a href=\""+obj.updateContent.companyStatusUpdate.share.content.eyebrowUrl+"\" target=\"_blank\">view</a></span>";
                                return strUrl;
                            },
                            attrs: "style='width: 1%'"
                        },
                        {
                            text: "Some Authority",
                            render: function (obj) {
                                var authStr = "";
                                if(obj.isCommentable === true){
                                    authStr = authStr.concat("<span class=\"word-break span-block\">isCommentable: true</span>");
                                }else{
                                    authStr = authStr.concat("<span class=\"word-break span-block\">isCommentable: false</span>");
                                }
                                if(obj.isLikable === true){
                                    authStr = authStr.concat("<span class=\"word-break span-block\">isLikable: true</span>");
                                }else{
                                    authStr = authStr.concat("<span class=\"word-break span-block\">isLikable: false</span>");
                                }
                                return authStr;
                            },
                            attrs: "style='width: 5%;'"
                        },
                        {
                            text: "Visibility",
                            render: function (obj) {
                                return obj.updateContent.companyStatusUpdate.share.visibility.code;
                            },
                            attrs: "style='width: 5%'"
                        },
                        {
                            text: "operator",
                            render: function (obj) {
                                var operStr = "";
                                if(obj.isCommentable === true){
                                    operStr = operStr.concat("<span><a src=\"#\" class=\"commentUpdates cursor\">Commenting</a></span>");
                                }
                                if(obj.isLikable === true){
                                    if(obj.isLiked === true){
                                        operStr = operStr.concat("<span><a src=\"#\" class=\"likeUpdates cursor\" value=\"like\">like</a></span>");
                                    }else{
                                        operStr = operStr.concat("<span><a src=\"#\" class=\"likeUpdates cursor\" value=\"dislike\">dislike</a></span>");
                                    }
                                }
                                if (operStr.length < 1) {
                                    operStr = "No Operator!";
                                };
                                return operStr;
                            },
                            attrs: "style='width: 5%;'"
                        }

                    ],
            opts: {
                htmlIfEmpty: "Not Company Updates found",
                withPaging: true,
                withCmdDelete:false
            }
        });
    }
    //----------------------- /private method --------------------
    
})(jQuery);