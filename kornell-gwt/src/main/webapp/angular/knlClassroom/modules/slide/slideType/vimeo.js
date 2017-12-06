'use strict';

var app = angular.module('knlClassroom');

app.controller('VimeoSlideController', [
	'$scope',
	'$sce',
	function($scope, $sce) {

		$scope.sce = $sce;
		$scope.videoLink =  $sce.trustAsResourceUrl("https://player.vimeo.com/video/" + $scope.slide.id);

	}
]);
