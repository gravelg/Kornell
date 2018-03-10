'use strict';

var app = angular.module('knlClassroom');

app.controller('QuestionLectureController', [
	'$scope',
	'knlUtils',
	function($scope, knlUtils) {       

		$scope.solve = function(){
			if($scope.answerCorrect){
            	knlUtils.setActionAttribute('next', 'true');
				return;
			}
			$scope.answerCorrect = false;
			var questionCorrect = 0;
			for (var i = 0; i < $scope.lecture.options.length; i++){
				var option = $scope.lecture.options[i];
				option.selected = option.selected || false;
				option.expected = option.expected || false;
				if(option.selected == option.expected){
					questionCorrect++;
				}
			}
			$scope.answerCorrect = $scope.answerCorrect || questionCorrect == $scope.lecture.options.length;
		};

		$scope.clear = function(index){
			if(!$scope.lecture.isMultiple){
				for (var i = 0; i < $scope.lecture.options.length; i++){
					var option = $scope.lecture.options[i];
					option.selected = (index === i);
				}
			}
			$scope.answerCorrect = null;
		};

        if($scope.lecture.shuffleQuestions){
            $scope.lecture.questions = knlUtils.shuffleArray($scope.lecture.options);
        }

	}
]);
