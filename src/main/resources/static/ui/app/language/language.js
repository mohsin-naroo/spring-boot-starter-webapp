(function() {
  'use strict';

  function LanguageService($resource) {
    return $resource('../api/v1/languages/:id', { id: '@id' });
  }

  angular.module('WebApp').factory('LanguageService', [ '$resource', LanguageService ]);

}());