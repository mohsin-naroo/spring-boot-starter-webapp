(function() {
  'use strict';

  function UserController($http, $location, $rootScope) {
    var self = this;

    self.user = {};
    $rootScope.alert = {};

    self.login = function() {
      $http.post('../api/v1/token',
          'username=' + self.user.name + '&password=' + self.user.password, {
            headers : {
              "Content-Type" : "application/x-www-form-urlencoded"
            }
          }).then(self.loginSuccess, self.loginFailure);
    }

    self.loginSuccess = function(response) {
      $rootScope.auth = { token: response.data };
      $rootScope.alert = {};
      $location.path('/person');
    }

    self.loginFailure = function(response) {
      $rootScope.alert = { type: 'danger', message: 'User not found' };
    }

    self.logout = function() {
      $http.post('../api/v1/token/revoke').then(self.logoutSuccess, self.logoutFailure);
    }

    self.logoutSuccess = function(response) {
      $rootScope.auth = {};
      $location.path('/');
    }

    self.logoutFailure = function(response) {
      $rootScope.alert = { type: 'danger', message: 'User logout failed' };
    }
  }

  angular.module('WebApp').controller('UserController', [ '$http', '$location', '$rootScope', UserController ]);

}());