'use strict';

var app = angular.module('knlClassroom');

app.controller('BubbleSlideController', [
	'$scope',
	'$sce',
	function($scope, $sce) {

		$scope.sce = $sce;

	}
]);
