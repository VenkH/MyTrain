// SessionStorage中用到的key
SESSION_ORDER = "SESSION_ORDER";
SESSION_TICKET_PARAMS = "SESSION_TICKET_PARAMS";
SESSION_ALL_TRAIN = "SESSION_ALL_TRAIN";
SESSION_ALL_STATION = "SESSION_ALL_STATION";


SessionStorage = {
    get: function (key) {
        var v = sessionStorage.getItem(key);
        if (v && typeof(v) !== "undefined" && v !== "undefined") {
            return JSON.parse(v);
        }
    },
    set: function (key, data) {
        sessionStorage.setItem(key, JSON.stringify(data));
    },
    remove: function (key) {
        sessionStorage.removeItem(key);
    },
    clearAll: function () {
        sessionStorage.clear();
    }
};
