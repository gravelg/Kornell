'use strict';

var app = angular.module('knlClassroom');

app.controller('QuestionSlideController', [
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
			for (var i = 0; i < $scope.slide.options.length; i++){
				var option = $scope.slide.options[i];
				option.selected = option.selected || false;
				option.expected = option.expected || false;
				if(option.selected == option.expected){
					questionCorrect++;
				}
			}
			$scope.answerCorrect = $scope.answerCorrect || questionCorrect == $scope.slide.options.length;
		};

		$scope.clear = function(index){
			if(!$scope.slide.isMultiple){
				for (var i = 0; i < $scope.slide.options.length; i++){
					var option = $scope.slide.options[i];
					option.selected = (index === i);
				}
			}
			$scope.answerCorrect = null;
		};

        if($scope.slide.shuffleQuestions){
            $scope.slide.questions = knlUtils.shuffleArray($scope.slide.options);
        }

	}
]);
