;(function() {

	/**
	 * View: Dropdown
	 *
	 */
    (function ($) {
        brite.registerView("Dropdown",  {parent:"body"}, {
            create:function (data, config) {
                data = data || {};
                var $target = data.$target;
                var left=0,top=0;
                if($target){
                  left = $target.offset().left;
                  top = $target.offset().top + $target.height() + 1;
                }
                var $html = app.render("tmpl-Dropdown",{left:left,top:top});
                this.$screen = $("<div class='transparentScreen'></div>").appendTo("body");
               	var $e = $($html);
                return $e;
            },
            postDisplay:function (data, config) {
                var view = this;
                var $e = view.$el;
                var list = data.list || [];
                
                view.$screen.on("btap",function(){
                  close.call(view);
                });
                
                show.call(view,list);
            },
            events:{
              "btap;.item":function(e){
                var view = this;
                var $e = view.$el;
                var $li = $(e.currentTarget);
                var name = $li.attr("data-name");
                
                hide.call(view);
                  $e.trigger("DO_ON_DROP_DOWN_CLICK", name);
                

                 close.call(view);
              }
            }
        });
        
        
        function close(){
          var view = this;
          var $e = view.$el;
          
          $(document).trigger("DO_ON_DROPDOWN_CLOSE");
          
          if(view.$screen){
            view.$screen.bRemove();
          }
          $e.bRemove();
        }
        
        function hide(){
          var view = this;
          var $e = view.$el;
          
          if(view.$screen){
            view.$screen.hide();
          }
          $e.hide();
        }
        
        function show(list){
          var view = this;
          var $e = view.$el;
          var $main = $e.find(".Dropdown-main");
          $e.show();
          $main.empty();
          
          for(var i = 0;i < list.length; i++){
            var $item = app.render("tmpl-Dropdown-item",list[i]);
            $main.append($item);
          }
        }
    })(jQuery);


})();