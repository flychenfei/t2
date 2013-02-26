;
(function ($) {

    brite.registerView("FoursquareVenuesCategories",{parent:".FoursquareScreen-content",emptyParent:true}, {
        create: function (data, config) {
            if(data && data.search) {
                this.search = data.search;
            }else{
                this.search = app.foursquareApi.venuesCategories;
            }
            return app.render("tmpl-FoursquareVenuesCategories");
        },

        postDisplay: function (data, config) {
            var view = this;
            showCategorys.call(view);
        },

        events: {
        },

        docEvents: {

        },

        daoEvents: {
        }
    });
    function showCategorys() {
        var view = this;
        return brite.display("DataTable", ".FoursquareVenuesCategories", {
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
                    text: "Icon",
                    render: function (obj) {
                        return "<img src={0} />".format(obj.icon);
                    },
                    attrs: "style='width: 5%'"

                },
                {
                    text: "Name",
                    render: function (obj) {
                        return obj.name
                    },
                    attrs: "style='width: 25%'"
                },
                {
                    text: "Plural Name",
                    render: function (obj) {
                        return obj.pluralName
                    }
                }
            ],
            opts: {
                htmlIfEmpty: "Not Categories found",
                withPaging: false,
                withCmdDelete: false
            }
        });
    }
})(jQuery);