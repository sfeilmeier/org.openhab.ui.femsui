angular.module('FemsApp.settings', []).controller('NetworkSettingController', function($scope, toastService, networkSettingService) {	
	networkSettingService.get(function(networkSetting) {
		console.log(networkSetting)
		$scope.networkSetting = networkSetting
	})

	$scope.save = function() {
		networkSettingService.save($scope.networkSetting, function(response) {
			toastService.showSuccessToast('Netzwerk-Einstellungen gespeichert.')
		})
	}
	
});
