'use strict';

var app = angular.module('knlClassroom');

app.controller('TextLectureController', [
	'$scope',
	'$sce',
	function($scope, $sce) {

		$scope.sce = $sce;

	}
]);
