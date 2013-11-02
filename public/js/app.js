App = Ember.Application.create();

App.Router.map(function() {
  this.resource('monitor', function() {
    this.resource('request', { path: '/:request_id' });
  });
});

App.RequestModel = Ember.Object.extend({
  name: '',
  id: 0
});


App.IndexRoute = Ember.Route.extend({
  activate: function() {
    $('.top-bg').animate({ height: 500 });
    $('.start').fadeIn();
  },
  model: function() {
    return ['red', 'yellow', 'blue'];
  }
});

App.IndexController = Ember.Controller.extend({
  setupController: function(controller) {

  }
});

/**
 *
 */
App.MonitorRoute = Ember.Route.extend({
	activate: function(controller) {
    $('.top-bg').animate({ height: 200 });
    $('.start').fadeOut();
	}
});

App.MonitorController = Ember.ArrayController.extend({

  init: function() {
    var me = this;
    this.set('content', []);

    Ember.$.getJSON(config.baseUrl + "monitor/request", function(data){
      if (data.success) {

        var websocket = new WebSocket(config.liveUrl + "?id=" + data.session);
        me.set('sessionid', data.session);

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

              console.log(me, data);

            me.set('requestContent', data.content);

            me.pushObject({
              name: 'test'
            });

            me.set('content', []);
        };

        me.websocket = websocket;
      }
    });
  }
});


App.RequestRoute = Ember.Route.extend({
  model: function(params) {
    var store = this.get('store');


  }
});

App.RequestController = Ember.Route.extend({

});