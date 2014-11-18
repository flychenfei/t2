;
(function () {

	(function ($) {

		brite.registerView("LiveCreateFolder", {
			loadTmpl:true,
			parent:"body",
			emptyParent:false
		}, {
			create:function (data, config) {
				var view = this;
				data = data || {};
				if(data) {
					this.folderId = data.id || "";
					this.parentId = data.parendId;
				}
				var dfd = $.Deferred();
				var createDfd = $.Deferred();
				if(view.folderId){
					app.liveFolderApi.getFolder(view.folderId).done(function(extraData) {
						dfd.resolve(extraData.result);
					});
				}else{
					dfd.resolve({});
				}

				dfd.done(function(obj){
					html = app.render("tmpl-LiveCreateFolder",obj||{});
					var $e = $(html);
					createDfd.resolve($e);
				});
				return createDfd.promise();
			},
			postDisplay:function (data, config) {
				var view = this;
				var $e = view.$el;
				$e.find("form").find("input[type=text]").focus();
			},

			close:function () {
				this.$el.remove();
			},

			submit:function () {
				var view = this;
				var $e = this.$el;
				var $controls = $e.find(".controls input,.controls textarea");
				var folder = {};
				$controls.each(function(idx, obj){
					var $this = $(this);
					folder[$this.attr("name")] = $this.val();
				});
				var input = $e.find("input[name='name']");
				if (input.val() == "") {
					input.focus();
					input.closest("div").addClass("has-error").find("span").html("Please enter valid folder name.");
				} else {
					app.liveFolderApi.saveFolder({id: view.folderId, parentId: view.parentId, folderJson: JSON.stringify(folder)}).done(function (extraData) {
						setTimeout((function () {
							$(document).trigger("DO_REFRESH_FOLDER");
						}), 5000);
						view.close();
					});
				}
			},

			events:{
				"btap; .createFolderBtn":function () {
					var view = this;
					var $e = view.$el;
					view.submit();
				},
				"keydown": function (e) {
					var view = this;
					if (e.keyCode == 27) {
						view.close();
					}else if (e.keyCode == 13) {
						view.submit();
					}
				},
				"btap; .cancelBtn":function () {
					var view = this;
					view.close();
				}
			}
		})
	})(jQuery);
})();
