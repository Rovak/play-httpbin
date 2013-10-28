(function(){
    var websocket = new WebSocket(config.liveUrl);

    function sendMessage(obj) {
        websocket.send(JSON.stringify(obj));
    }

    websocket.onopen = function (ev) {

    };

    websocket.onclose = function (ev) {

    };

    websocket.onerror = function (ev) {

    };

    /**
     * When a new message is received
     *
     * @param  {Object} ev Data from the event
     * @return {Unit}
     */
    websocket.onmessage = function(ev) {
        var data = JSON.parse(ev.data);

        $('.results').append(data.content + "<br>");
    };
})();