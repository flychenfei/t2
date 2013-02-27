;
(function ($) {

    brite.registerView("FoursquareSearchUser",{parent:".FoursquareScreen-content",emptyParent:true}, {
        create: function (data, config) {
            if(data && data.search) {
                this.search = data.search;
            }else{
                this.search = app.foursquareApi.searchUser;
            }
            return app.render("tmpl-FoursquareSearchUser");
        },

        postDisplay: function (data, config) {
            var view = this;
            searchUser.call(view);
        },

        events: {
        },

        docEvents: {

        },

        daoEvents: {
        }
    });
    function searchUser() {
        var view = this;
        return brite.display("DataTable", ".FoursquareSearchUser", {
            dataProvider: {list: view.search},
            onDone: function(obj){obj.result = obj.result||[]},
            columnDef: [
                {
                    text: "#",
                    render: function (obj, idx) {
                        return idx + 1
                    },
                    attrs: "style='width: 5%;cursor: pointer'"
                },
                {
                    text: "First Name",
                    render: function (obj) {
                        return obj.firstName;
                    },
                    attrs: "style='width: 15%'"

                },
                {
                    text: "Last Name",
                    render: function (obj) {
                        return obj.lastName
                    },
                    attrs: "style='width: 10%'"
                },
                {
                    text: "Home City",
                    render: function (obj) {
                        return obj.homeCity
                    },
                    attrs: "style='width: 10%'"
                },
                {
                    text: "photo",
                    render: function (obj) {
                        return "<img src= '{0}' withd='100px'/>".format(obj.photo);
                    },
                    attrs: "style='width: 30%'"
                },
                {
                    text: "Gender",
                    render: function (obj) {
                        return obj.gender;
                    },
                    attrs: "style='width: 10%'"
                },
                {
                    text: "Relationship",
                    render: function (obj) {
                        return obj.relationship;
                    }
                }
            ],
            opts: {
                htmlIfEmpty: "Not User found",
                withPaging: false,
                withCmdDelete: false
            }
        });
    }
})(jQuery);