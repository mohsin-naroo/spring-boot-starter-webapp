(function() {
  'use strict';

  function PersonService($resource) {
    return $resource('../api/v1/persons/:id', { id: '@id' });
  }

  angular.module('WebApp').factory('PersonService', [ '$resource', PersonService ]);

  function PersonController($q, $routeParams, $rootScope, personService, countryService, languageService) {
    var self = this;
    self.person = {};
    self.country = {};
    self.language = {};
    self.persons = [];
    self.countries = [];
    self.languages = [];
    self.countriesMap = {};
    self.languagesMap = {};
    $rootScope.alert = {};

    self.init = function() {
      if ($routeParams.id) {
        self.single();
      } else {
        self.list();
      }
    }

    self.single = function() {
      self.person = {};
      self.country = {id: '', name: ''};
      self.language = {id: '', name: ''};
      self.countries = [self.country];
      self.languages = [self.language];
      var promises = [countryService.get().$promise, languageService.get().$promise];
      if ($routeParams.id != 0) {
        promises.push(personService.get({ id: $routeParams.id }).$promise);
      }
      $q.all(promises).then(
        function(results) {
          self.countries = self.countries.concat(results[0].content);
          for (var index = 0; index < self.countries.length; index++) {
            self.countriesMap[self.countries[index].id] = self.countries[index];
          }
          self.languages = self.languages.concat(results[1].content);
          for (var index = 0; index < self.languages.length; index++) {
            self.languagesMap[self.languages[index].id] = self.languages[index];
          }
          if (results.length > 2) {
            self.person = results[2];
            self.country = self.countriesMap[self.person.countryId];
            self.language = self.languagesMap[self.person.languageId];
          }
        }, function(response) {
          console.log(response);
        });
    }

    self.list = function() {
      var params = {};
      if ($routeParams.page) {
        params.page = $routeParams.page;
      }
      if ($routeParams.size) {
        params.size = $routeParams.size;
      }
      $q.all([ personService.get(params).$promise, countryService.get().$promise, languageService.get().$promise ]).then(
        function(results) {
          self.persons = results[0].content;
          self.countries = results[1].content;
          for (var index = 0; index < self.countries.length; index++) {
            self.countriesMap[self.countries[index].id] = self.countries[index];
          }
          self.languages = results[2].content;
          for (var index = 0; index < self.languages.length; index++) {
            self.languagesMap[self.languages[index].id] = self.languages[index];
          }
          for (var index = 0; index < self.persons.length; index++) {
            self.persons[index].country = self.countriesMap[self.persons[index].countryId];
            self.persons[index].language = self.languagesMap[self.persons[index].languageId];
          }
        }, function(response) {
          console.log(response);
        });
    }

    self.save = function() {
      $rootScope.alert = {};
      self.person.countryId = self.country.id;
      self.person.languageId = self.language.id;
      personService.save(self.person).$promise.then(function(result) {
        self.person = result;
        $rootScope.alert = { type: 'success', message: 'Record saved successfully' };
      }, function(response) {
        console.log(response);
        $rootScope.alert = { type: 'danger', message: 'Error savind record: ' + response.data.error };
      });
    }

    self.init();
  }

  angular.module('WebApp').controller('PersonController',
      [ '$q', '$routeParams', '$rootScope', 'PersonService', 'CountryService', 'LanguageService', PersonController ]);

}());