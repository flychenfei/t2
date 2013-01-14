;
(function ($) {

    brite.registerView("GoogleContacts",{parent:".MainScreen-main"},{
        create: function (data, config) {
            $(".MainScreen-main").empty();
            return $("#tmpl-GoogleContacts").render();
        },

        postDisplay: function (data, config) {
            showContacts();
        },

        events: {
        },

        docEvents: {
            "DELETE_CONTACT": function(event, extraData) {
                console.log("delete contacts");
                if (extraData && extraData.objId) {
                    var contactId = getContactId(extraData.objId);
                    var etag = $(extraData.event.currentTarget).closest("tr").attr("etag");
                    app.deleteContact(contactId, etag).done(function (extradata) {
                        if (extradata && extradata.result) {
                            setTimeout((function () {
                                showContacts();
                            }), 3000);

                        }
                    });
                }

            }
        },

        daoEvents: {
        }
    });
    function getGroupId(url) {
        var myregexp = /http:\/\/www.google.com\/m8\/feeds\/groups\/(.+)\/base\/(.+)/;
        var match = myregexp.exec(url);
        if (match != null) {
            result = match[2];
        } else {
            result = "";
        }
        return result;
    }
    function getContactId(url) {
        var myregexp = /http:\/\/www.google.com\/m8\/feeds\/contacts\/(.+)\/base\/(.+)/;
        var match = myregexp.exec(url);
        if (match != null) {
            result = match[2];
        } else {
            result = "";
        }
        return result;
    }

    function showContacts() {
        brite.display("DataTable", ".contacts-container", {
            dataProvider: {list: app.getContacts},
            rowAttrs: function (obj) {
                return " etag='{0}'".format(obj.etag)
            },
            columnDef: [
                {
                    text: "#",
                    render: function (obj, idx) {
                        return idx + 1
                    },
                    attrs: "style='width: 10%'"
                },
                {
                    text: "Emails",
                    render: function (obj) {
                        return obj.email
                    },
                    attrs: "style='width: 400px'"

                },
                {
                    text: "Full Name",
                    render: function (obj) {
                        return obj.fullName
                    },
                    attrs: "style='width: 25%'"
                },
                {
                    text: "Group",
                    render: function (obj) {
                        return getGroupId(obj.groupId)
                    }
                }
            ],
            opts: {
                htmlIfEmpty: "Not contacts found",
                withPaging: true,
                cmdDelete: "DELETE_CONTACT"
            }
        });
    }
})(jQuery);