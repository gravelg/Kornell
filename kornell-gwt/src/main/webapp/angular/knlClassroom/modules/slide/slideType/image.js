'use strict';

var app = angular.module('knlClassroom');

app.controller('ImageSlideController', [
	'$scope',
	'$sce',
	function($scope, $sce) {

		$scope.sce = $sce;
		if($scope.slide.id && $scope.slide.id.indexOf('/') == 0 && parent.location.hostname === 'localhost'){
			$scope.prefixURL = 'http://localhost:8888';
		}
	}
]);
