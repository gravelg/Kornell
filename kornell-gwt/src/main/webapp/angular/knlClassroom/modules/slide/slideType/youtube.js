'use strict';

var app = angular.module('knlClassroom');

app.controller('YoutubeSlideController', [
	'$scope',
	'$sce',
	function($scope, $sce) {

		$scope.sce = $sce;
		$scope.videoLink =  $sce.trustAsResourceUrl("https://www.youtube.com/embed/" + $scope.slide.id + "?rel=0&amp;showinfo=0");

	}
]);
