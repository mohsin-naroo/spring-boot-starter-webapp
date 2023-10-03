(function() {
  'use strict';

  function CountryService($resource) {
    return $resource('../api/v1/countries/:id', { id: '@id' });
  }

  angular.module('WebApp').factory('CountryService', [ '$resource', CountryService ]);

}());