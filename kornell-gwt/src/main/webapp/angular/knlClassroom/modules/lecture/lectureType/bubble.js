'use strict';

var app = angular.module('knlClassroom');

app.controller('BubbleLectureController', [
	'$scope',
	'$sce',
	function($scope, $sce) {

		$scope.sce = $sce;

	}
]);
