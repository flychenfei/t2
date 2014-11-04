var app = app || {};
(function() {
	app.googleDriveApi = {
        fileList: function(param){
            param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/filelist", param);
        },
        searchFile: function(param){
        	param = param||{};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/search", param);
        },
        trashFile: function(param){
        	param = param || {};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/trashFile", param);
        },
        untrashFile: function(param){
        	param = param || {};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/untrashFile", param);
        },
        deleteFile: function(param){
        	param = param || {};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/deleteFile", param);
        },
        trashList: function(param){
        	param = param || {};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/trashlist", param);
        },
        childList: function(param){
        	param = param || {};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/childList", param);
        },
        copyFile: function(param){
        	param = param || {};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/copyFile", param);
        },
        restoreTrash: function(param){
        	param = param || {};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/restoreTrash", param);
        },
        emptyTrash: function(param){
        	param = param || {};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/emptyTrash", param);
        },
        createFolder: function(param){
        	param = param || {};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/createFolder", param);
        },
        previousList: function(param){
        	param = param || {};
        	param.pageSize = param.pageSize || 10;
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/previousList", param);
        },
        patchFile: function(param){
        	param = param || {};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/patchFile", param);
        },
        touchFile: function(param){
        	param = param || {};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/touchFile", param);
        },
        foldersInfo: function(param){
        	param = param || {};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/foldersInfo", param);
        },
        moveFile: function(param){
        	param = param || {};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/moveFile", param);
        },
        updateStarred: function(param){
        	param = param || {};
            param.method = "Get";
            return app.getJsonData(contextPath + "/googleDrive/updateStarred", param);
        }
        
        
	};
})();
