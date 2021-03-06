(function ($) {

    brite.registerView("FoursquareUsersBadges", {parent: ".FoursquareScreen-content", emptyParent: true}, {
        create: function (data, config) {
            if (data && data.search) {
                this.search = data.search;
            } else {
                this.search = app.foursquareApi.usersBadges;
            }
            return app.render("tmpl-FoursquareUsersBadges");
        },

        postDisplay: function (data, config) {
            var view = this;
            showBadges.call(view);
        },

        events: {},

        docEvents: {},

        daoEvents: {}
    });
    function showBadges() {
        var view = this;
        return brite.display("DataTable", ".FoursquareUsersBadges", {
            dataProvider: {list: view.search},
            columnDef: [
                {
                    text: "#",
                    render: function (obj, idx) {
                        return idx + 1
                    },
                    attrs: "style='width: 5%'"
                },
                {
                    text: "Image",
                    render: function (obj) {
                        return "<img src={0} />".format(obj.image.prefix + "57" + obj.image.name);
                    },
                    attrs: "style='width: 5%'"
                },
                {
                    text: "Name",
                    render: function (obj) {
                        return obj.name;
                    },
                    attrs: "style='width: 25%'"

                },
                {
                    text: "Description",
                    render: function (obj) {
                        return obj.description
                    }
                }
            ],
            opts: {
                htmlIfEmpty: "Not Badges found",
                withPaging: false,
                withCmdDelete: false
            }
        });
    }
})(jQuery);