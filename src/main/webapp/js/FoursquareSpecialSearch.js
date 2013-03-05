;
(function ($) {

    brite.registerView("FoursquareSpecialSearch",{parent:".FoursquareScreen-content",emptyParent:true}, {
        create: function (data, config) {
            if(data && data.search) {
                this.search = data.search;
            }else{
                this.search = app.foursquareApi.specialSearch;
            }
            return app.render("tmpl-FoursquareSpecialSearch");
        },

        postDisplay: function (data, config) {
            var view = this;
            specialSearch.call(view);
        },

        events: {
        },

        docEvents: {

        },

        daoEvents: {
        }
    });
    function specialSearch() {
        var view = this;
        return brite.display("DataTable", ".FoursquareSpecialSearch", {
            dataProvider: {list: view.search},
            onDone: function(data){

                obj = data.result;
                if(obj.items && obj.items.length >0 ){
                    data.result = obj.items;
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
                    text: "Title",
                    render: function (obj) {
                        return obj.title
                    },
                    attrs: "style='width: 15%'"

                },
                {
                    text: "Message",
                    render: function (obj) {
                        return obj.message
                    },
                    attrs: "style='width: 45%'"
                },
                {
                    text: "Provider",
                    render: function (obj) {
                        return obj.provider;
                    },
                    attrs: "style='width: 10%'"
                },
                {
                    text: "Venue",
                    render: function (obj) {
                        return obj.venue.name;
                    }
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