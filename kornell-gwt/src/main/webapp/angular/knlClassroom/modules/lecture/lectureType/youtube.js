'use strict';

var app = angular.module('knlClassroom');

app.controller('YoutubeLectureController', [
	'$scope',
	'$sce',
	function($scope, $sce) {

		$scope.sce = $sce;
		$scope.videoLink =  $sce.trustAsResourceUrl("https://www.youtube.com/embed/" + $scope.lecture.id + "?rel=0&amp;showinfo=0");

	}
]);
