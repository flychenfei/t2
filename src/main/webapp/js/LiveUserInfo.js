(function(){
    brite.registerView("LiveUserInfo",{emptyParent:true, parent:".LiveScreen-content"},{
        create:function(data,config){
            return showUserInfo.call(this);
        },
        events:{

        }
    });

    function showUserInfo(){
        var dfd = $.Deferred();
        app.liveApi.getUserInfo().done(function(result){
            if(result.success){
                var html =  app.render("tmpl-LiveUserInfo",result.result);
                dfd.resolve(html);
            }
        });
        return dfd.promise();

    }
})();