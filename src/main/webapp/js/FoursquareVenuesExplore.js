;
(function ($) {

    brite.registerView("FoursquareVenuesExplore",{parent:".FoursquareScreen-content",emptyParent:true}, {
        create: function (data, config) {
            if(data && data.search) {
                this.search = data.search;
            }else{
                this.search = app.foursquareApi.venuesExplore();
            }
            return app.render("tmpl-FoursquareVenuesExplore");
        },

        postDisplay: function (data, config) {
            var view = this;
            venuesExplore.call(view);
        },

        events: {
        },

        docEvents: {

        },

        daoEvents: {
        }
    });
    function venuesExplore() {
        var view = this;
        return brite.display("DataTable", ".FoursquareVenuesExplore", {
            dataProvider: {list: view.search},
            onDone: function(data){

                obj = data.result;
                if(obj.groups && obj.groups.length >0 && obj.groups[0].items && obj.groups[0].items.length > 0){
                    data.result = obj.groups[0].items;
                }
            },
            columnDef: [
                {
                    text: "#",
                    render: function (obj, idx) {
                        return idx + 1
                    },
                    attrs: "style='width: 5%;cursor: pointer'"
                },
                {
                    text: "Category",
                    render: function (obj) {
                            return obj.category;
                    },
                    attrs: "style='width: 15%'"

                },
                {
                    text: "Name",
                    render: function (obj) {
                        return obj.name
                    },
                    attrs: "style='width: 10%'"
                },
                {
                    text: "Location",
                    render: function (obj) {
                        return obj.location;
                    },
                    attrs: "style='width: 30%'"
                }
            ],
            opts: {
                htmlIfEmpty: "Not Venues found",
                withPaging: false,
                withCmdDelete: false
            }
        });
    }
})(jQuery);