angular.module('FemsApp', [
  'FemsApp.settings',
  'FemsApp.controllers',
  'FemsApp.services',
  'FemsApp.services.rest',
  'ngRoute',
  'ngResource',
  'ngMaterial'
]).config(['$routeProvider', '$httpProvider', function($routeProvider) {
  $routeProvider.
  	when('/start', {templateUrl: 'partials/start.html', title: 'Start', simpleHeader: true}).
  	when('/setting', {redirectTo: '/setting/network'}).
  	when('/setting/network', {templateUrl: 'partials/setting.network.html', controller: 'NetworkSettingController', title: 'Netzwerkeinstellungen', simpleHeader: true}).
	otherwise({redirectTo: '/start'});
}]).run(['$location', '$rootScope', function($location, $rootScope) {
	var original = $location.path;
	$rootScope.$on('$routeChangeSuccess', function (event, current, previous) {
        $rootScope.title = current.$$route.title;
        $rootScope.simpleHeader = current.$$route.simpleHeader;
        $rootScope.path = $location.path().split('/');
        $rootScope.section = $rootScope.path[1];
        $rootScope.page = $rootScope.path[2];
    });
    $rootScope.asArray = function (object) {
        return $.isArray(object) ? object : object ? [ object ] : [] ;
    }
    $rootScope.itemUpdates = {};
    $rootScope.data = [];
    $rootScope.navigateToRoot = function() {
        $location.path('');
    }
    $rootScope.navigateFromRoot = function(path) {
        $location.path(path);
    }
}]);