;
(function ($) {

    brite.registerView("FoursquareVenuesSearch",{parent:".FoursquareScreen-content",emptyParent:true}, {
        create: function (data, config) {
            if(data && data.search) {
                this.search = data.search;
            }else{
                this.search = app.foursquareApi.venuesSearch;
            }
            return app.render("tmpl-FoursquareVenuesSearch");
        },

        postDisplay: function (data, config) {
            var view = this;
            venuesSearch.call(view);
        },

        events: {
        },

        docEvents: {

        },

        daoEvents: {
        }
    });
    function venuesSearch() {
        var view = this;
        return brite.display("DataTable", ".FoursquareVenuesSearch", {
            dataProvider: {list: view.search},
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