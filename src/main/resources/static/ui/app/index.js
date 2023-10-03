(function() {
  'use strict';

  angular.module('WebApp', ['ngRoute', 'ngResource', 'ng']);

  angular.module('WebApp').run(function($http, $rootScope, $route) {
    $rootScope.alert = {};
    $rootScope.alertHide = function() {
      $rootScope.alert = {};
    };
    $http.post('../api/v1/token').then(function(response) {
      $rootScope.auth = { token: response.data };
      $route.reload();
    }, function(response) {
      if (response.status && response.status == 403) {
        $http.post('../api/v1/token').then(function(response) {
          $rootScope.auth = { token: response.data };
          $route.reload();
        });
      }
    });
  });

  angular.module('WebApp').factory('BearerTokenInterceptor', function ($rootScope) {
    return {
      request: function(config) {
        config.headers = config.headers || {};
        if ($rootScope.auth && $rootScope.auth.token) {
          config.headers['Authorization'] = $rootScope.auth.token.token_type + ' ' + $rootScope.auth.token.access_token;
        }
        return config;
      }
    };
  });

  function WebAppConfig($httpProvider, $locationProvider, $routeProvider) {
    $httpProvider.interceptors.push('BearerTokenInterceptor');

    $locationProvider.hashPrefix('!');

    $routeProvider.when('/login', {
      templateUrl : 'app/user/login.html',
      controller : 'UserController as $ctrl'
    }).when('/logout', {
      templateUrl : 'app/user/logout.html',
      controller : 'UserController as $ctrl'
    }).when('/person', {
      templateUrl : 'app/person/person-list.html',
      controller : 'PersonController as $ctrl'
    }).when('/person/:id', {
      templateUrl : 'app/person/person.html',
      controller : 'PersonController as $ctrl'
    }).when('/', {
      templateUrl : 'welcome.html'
    }).otherwise('/');
  }

  angular.module('WebApp').config(['$httpProvider', '$locationProvider', '$routeProvider', WebAppConfig]);

}());