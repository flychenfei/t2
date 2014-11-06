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
        console.log($content.get(0));
        console.log(data);
        $content.empty();
        var $renderContent = $(render("tmpl-LinkedInCompanyLists",{result:data.result,total:data.result.length}));
        $content.append($renderContent);
    }

    //----------------------- /private method --------------------
    
})(jQuery);