'use strict';

var app = angular.module('knlClassroom');

app.controller('ImageLectureController', [
	'$scope',
	'$sce',
	function($scope, $sce) {

		$scope.sce = $sce;
	}
]);
