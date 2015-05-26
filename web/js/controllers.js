angular.module('FemsApp.controllers', []).controller('BodyController', function($rootScope, $scope, toastService) {
	$scope.scrollTop = 0;
	$(window).scroll(function() {
		$scope.$apply(function (scope) {
			$scope.scrollTop = $('body').scrollTop();
		});
	});
	$scope.isBigTitle = function() {
		return $scope.scrollTop < 80 && !$rootScope.simpleHeader;
    }
    $scope.setTitle = function(title) {
    	$rootScope.title = title;
	}
    $scope.subtitles = [];
    $scope.setSubtitle = function(args) {
    	$scope.subtitles = [];
    	$.each(args, function(i, subtitle) {
			$scope.subtitles.push(subtitle);
		})
	}
    $scope.setHeaderText = function(headerText) {
    	$scope.headerText = headerText;
	}

}).controller('NavController', function($scope, $location) {
    $scope.opened = null;
    $scope.open = function(viewLocation) {
    	$scope.opened = viewLocation;
    }
    $scope.isActive = function(viewLocation) {
        var active = (viewLocation === $location.path().split('/')[1]);
        return active || $scope.opened === viewLocation;
    }
    $scope.isSubActive = function(viewLocation) {
        var active = (viewLocation === $location.path().split('/')[2]);
        return active;
    }
    $scope.$on('$routeChangeSuccess', function() {
        $('body').removeClass('sml-open');
        $('.mask').remove();
        $scope.opened = null;
    });
});