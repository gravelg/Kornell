'use strict';

var app = angular.module('knlClassroom');

app.controller('ImageSlideController', [
	'$scope',
	'$sce',
	function($scope, $sce) {

		$scope.sce = $sce;

	}
]);
