;
(function ($) {

    brite.registerView("FoursquareRecentCheckins",{parent:".FoursquareScreen-content",emptyParent:true}, {
        create: function (data, config) {
            if(data && data.search) {
                this.search = data.search;
            }else{
                this.search = app.foursquareApi.recentCheckins;
            }
            return app.render("tmpl-FoursquareRecentCheckins");
        },

        postDisplay: function (data, config) {
            var view = this;
            recentCheckins.call(view);
        },

        events: {
        },

        docEvents: {

        },

        daoEvents: {
        }
    });
    function recentCheckins() {
        var view = this;
        return brite.display("DataTable", ".FoursquareRecentCheckins", {
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
                        if(obj.categories && obj.categories.length > 0){
                            return obj.categories.name;
                        }else{
                            return "";
                        }
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
                    attrs: "style='width: 10%'"
                },
                {
                    text: "Location",
                    render: function (obj) {
                        return "{address},{crossStreet},{city},{country}, {postalCode}".format(obj.location);
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