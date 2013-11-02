App = Ember.Application.create()

App.Router.map () ->
  this.resource 'monitor', ->
    this.resource 'request', path: '/:request_id'

App.RequestModel = Ember.Object.extend
  name: ''
  id: 0

App.IndexRoute = Ember.Route.extend
  activate: (controller) ->
    $('.top-bg').animate
      height: 500
    $('.start').fadeIn()
  model: ->
    ['red', 'yellow', 'blue']

App.IndexController = Ember.Controller.extend
  setupController: (controller) ->



App.MonitorRoute = Ember.Route.extend
	activate: (controller) ->
    $('.top-bg').animate
      height: 200

    $('.start').fadeOut()

App.MonitorController = Ember.ArrayController.extend

  init: ->
    me = this
    this.set 'content', []

    this.pushObject
      name: 'test'

    Ember.$.getJSON config.baseUrl + "monitor/request", (data) ->
      if data.success
        me.websocket = websocket = new WebSocket config.liveUrl + "?id=" + data.session
        me.set 'sessionid', data.session

        websocket.onopen = (ev) ->

        websocket.onclose = (ev) ->

        websocket.onerror = (ev) ->

        ###
        When a new message is received

        @param  {Object} ev Data from the event
        @return {Unit}
        ###
        websocket.onmessage = (ev) ->
          data = JSON.parse ev.data
          console.log me, data

          me.set 'requestContent', data.content

          me.pushObject
            name: 'test'

          me.set 'content', []

App.RequestRoute = Ember.Route.extend
  model: (params) ->
    store = this.get 'store'

#App.RequestController = Ember.Route.extend