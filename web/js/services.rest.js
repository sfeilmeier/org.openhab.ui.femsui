angular.module('FemsApp.services.rest', []).factory('networkSettingService', function($resource) {
    return $resource('/rest/fems/setting/network', {}, {
        get: {
        	method : 'GET'
        },
        save : {
            method : 'PUT'
        },
    });
});