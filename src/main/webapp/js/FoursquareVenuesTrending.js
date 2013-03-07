;
(function ($) {

    brite.registerView("FoursquareVenuesTrending",{parent:".FoursquareScreen-content",emptyParent:true}, {
        create: function (data, config) {
            if(data && data.search) {
                this.search = data.search;
            }else{
                this.search = app.foursquareApi.searchUser;
            }
            return app.render("tmpl-FoursquareVenuesTrending");
        },

        postDisplay: function (data, config) {
            var view = this;
            venuesTrending.call(view);
        },

        events: {
        },

        docEvents: {

        },

        daoEvents: {
        }
    });
    function venuesTrending() {
        var view = this;
        return brite.display("DataTable", ".FoursquareVenuesTrending", {
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
                    text: "Url",
                    render: function (obj) {
                        return "<href a='{0}'>{0}</href>".format(obj.url);
                    },
                    attrs: "style='width: 25%'"
                },
                {
                    text: "Location",
                    render: function (obj) {
                        return obj.location;
                    }
                }
            ],
            opts: {
                htmlIfEmpty: "Not VenuesTrending found",
                withPaging: false,
                withCmdDelete: false
            }
        });
    }
})(jQuery);