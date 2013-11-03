App = Ember.Application.create()

App.Router.map () ->
  this.resource 'monitor', ->
    this.resource 'request', path: '/:request_id'

#App.Requests = Ember.MutableArray.create()

App.RequestModel = DS.Model.extend
  body: DS.attr()
  headers: DS.attr()

App.RequestId = 1

# Index
App.IndexRoute = Ember.Route.extend
  activate: (controller) ->
    $('.top-bg').animate
      height: 500
    $('.start').fadeIn()

App.IndexController = Ember.Controller.extend
  setupController: (controller) ->

# Monitor
App.MonitorRoute = Ember.Route.extend
  activate: (controller) ->
    $('.top-bg').animate
      height: 200

    $('.start').fadeOut()

App.MonitorController = Ember.ArrayController.extend

  content: []

  sortProperties: ['id']
  sortAscending: false

  init: ->
    @._super()
    self = @

    content = @get 'content'

    $.getJSON config.baseUrl + "monitor/request", (data) ->
      if data.success
        self.websocket = websocket = new WebSocket config.liveUrl + "?id=" + data.session
        self.set 'sessionid', data.session

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
          store = self.get 'store'

          data.id = ++App.RequestId
          data.name = "Request " + data.id
          data.headers = for key, value of data.headers
            name: key, value: value

          self.pushObject data
          store.push 'request', data

# Request
App.RequestRoute = Ember.Route.extend
  model: (params) ->
    store = @get 'store'
    request = store.find 'request', params.request_id
    request


window.App = App