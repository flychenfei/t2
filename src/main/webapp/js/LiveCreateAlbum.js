;
(function () {

	(function ($) {

		brite.registerView("LiveCreateAlbum", {
			loadTmpl:true,
			parent:"body",
			emptyParent:false
		}, {
			create:function (data, config) {
				var view = this;
				data = data || {};
				if(data) {
					this.albumId = data.id;
				}
				
				var dfd = $.Deferred();
				var createDfd = $.Deferred();
				if(view.albumId){
					app.liveAlbumApi.getAlbum(view.albumId).done(function(extraData) {
						dfd.resolve(extraData.result);
					});
				}else{
					dfd.resolve({});
				}
				
				dfd.done(function(obj){
					html = app.render("tmpl-LiveCreateAlbum",obj||{});
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
				var album = {};
				$controls.each(function(idx, obj){
					var $this = $(this);
					album[$this.attr("name")] = $this.val();
				});
				var input = $e.find("input[name='name']");
				if (input.val() == "") {
					input.focus();
					input.closest("div").addClass("has-error").find("span").html("Please enter valid album name.");
				} else {
					app.liveAlbumApi.saveAlbum({id: view.albumId, albumJson: JSON.stringify(album)}).done(function (extraData) {
						setTimeout((function () {
							$(document).trigger("DO_REFRESH_ALBUM");
						}), 5000);
						view.close();
					});

				}
			},

			events:{
				"btap; .createAlbumBtn":function () {
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
