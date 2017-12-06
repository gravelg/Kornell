'use strict';

var app = angular.module('knlClassroom');

app.controller('TextSlideController', [
	'$scope',
	'$sce',
	function($scope, $sce) {

		$scope.sce = $sce;

	}
]);
