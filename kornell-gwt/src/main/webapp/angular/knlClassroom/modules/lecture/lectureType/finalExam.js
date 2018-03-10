'use strict';

var app = angular.module('knlClassroom');

app.controller('FinalExamLectureController', [
    '$scope',
    'knlUtils',
    function($scope, knlUtils) {

        $scope.knlUtils = knlUtils;

        $scope.init = function(){
            $scope.showPanel = 'intro';
            var initialState = knlUtils.initFinalExam();
            $scope.score = initialState.score;
            $scope.isApproved = initialState.isApproved;
            $scope.currentScore = initialState.currentScore;
            if($scope.isApproved !== 'true'){
                knlUtils.setLectureAttribute('type', $scope.lecture.type);
                knlUtils.setActionAttribute('nextEnabled', 'false');
            }
            if($scope.lecture.shuffleQuestions){
                $scope.shuffleQuestions();
            }
        };      

        $scope.loadQuestion = function(currentQuestionIndex) {
            if(currentQuestionIndex && !$scope.hasAccessToQuestion(currentQuestionIndex)) return;
            $scope.saveQuestionTimer();
            $scope.currentQuestionIndex = currentQuestionIndex;
            $scope.currentQuestion = $scope.lecture.questions[$scope.currentQuestionIndex];
            $scope.questionTimer = (new Date()).getTime();
        };

        $scope.saveQuestionTimer = function(){
            if($scope.currentQuestion){
                var newTime = (new Date()).getTime();
                $scope.currentQuestion.time = $scope.currentQuestion.time + 
                    Math.round((newTime - $scope.questionTimer)/1000);
            }
        }
        
        $scope.loadNext = function(){
            $scope.loadQuestion($scope.currentQuestionIndex+1);
        };

        $scope.loadPrevious = function(){
            $scope.loadQuestion($scope.currentQuestionIndex-1);
        };

        $scope.finishTest = function(){
            $scope.saveQuestionTimer();
            $scope.cmiScoreRaw = 0;//cmi.score.raw
            $scope.cmiScoreMax = 100;//cmi.score.max
            $scope.cmiScoreMin = $scope.lecture.expectedGrade;//cmi.score.min
            $scope.lecture.expectedGrade = $scope.lecture.expectedGrade || 0;

            $scope.totalCorrect = 0;
            $scope.questionCount = $scope.lecture.questions.length;

            var question, questionOptions, questionCorrect, optionCorrect, answersInner, option;
            for (var i = 0; i < $scope.lecture.questions.length; i++){
                question = $scope.lecture.questions[i];
                questionOptions = question.options;
                optionCorrect = 0;
                for (var j = 0; j < questionOptions.length; j++){
                    option = questionOptions[j];
                    option.expected = option.expected || false;
                    option.selected = option.selected || false;
                    option.correct = (option.expected === option.selected);
                    if(option.correct){
                        optionCorrect++;
                    }
                }
                questionCorrect = (optionCorrect === questionOptions.length);
                question.correct = questionCorrect;
                if(questionCorrect){
                    $scope.totalCorrect++;
                }  
            }


            $scope.cmiScoreRaw = (($scope.totalCorrect*100)/$scope.questionCount);
            $scope.cmiScoreRaw = Math.round($scope.cmiScoreRaw);
            knlUtils.setAttribute('cmi.core.score.raw', $scope.cmiScoreRaw);
            knlUtils.setAttribute('cmi.core.score.max', 100);
            knlUtils.setAttribute('cmi.core.score.min', $scope.cmiScoreMin);

            $scope.isApproved = ($scope.cmiScoreRaw >= $scope.cmiScoreMin);
            if($scope.isApproved){
                knlUtils.setActionAttribute('nextEnabled', 'true');
            } else {
                knlUtils.setActionAttribute('nextEnabled', 'false');
            }
  
            knlUtils.saveExamAttempt($scope.cmiScoreRaw);
            if($scope.isApproved && !knlUtils.isApproved()){
                knlUtils.setLectureAttribute('currentScore', $scope.cmiScoreRaw);
                knlUtils.setLectureAttribute('isApproved', 'true');
                knlUtils.setLectureAttribute('attempt.correct_index', knlUtils.getLastAttemptIndex());
            }
            $scope.showPanel = 'result';
        };

        $scope.isQuestionAnswered = function(question){
            //questions with multiple options are always answered, since they can be all false
            var isAnswered = $scope.lecture.isMultiple;       
            for (var i = 0; i < question.options.length; i++){
                var option = question.options[i];
                isAnswered = isAnswered || option.selected;
            }
            return isAnswered;
        };

        $scope.hasAccessToQuestion = function(index){
            $scope.blah = [];
            var hasAccessToQuestion = true;
            if($scope.currentQuestion){
                if($scope.isApproved === 'true'){
                    $scope.blah[index] = 1;
                    //if is approved, access is granted
                    hasAccessToQuestion = true;
                } else if($scope.currentQuestionIndex >= index){
                    $scope.blah[index] = 2;
                    //if it's before the current question, access is granted
                    hasAccessToQuestion =  true;
                } else if(($scope.currentQuestionIndex + 1) == index){
                    $scope.blah[index] = 3;
                    //if it's the next question, only allow if current question is answered
                    hasAccessToQuestion = $scope.isQuestionAnswered($scope.currentQuestion);
                } else {
                    //otherwise allow if the previous question is answered
                    $scope.blah[index] = 4;
                    hasAccessToQuestion = $scope.isQuestionAnswered($scope.lecture.questions[index - 1]);
                }
            }
            return hasAccessToQuestion;
        };

        $scope.startTest = function(){
            $scope.showPanel = 'main';
            $scope.loadQuestion(0);

            for (var i = 0; i < $scope.lecture.questions.length; i++){
                var question = $scope.lecture.questions[i],
                    questionOptions = question.options;
                for (var j = 0; j < questionOptions.length; j++){
                    var option = questionOptions[j];
                    option.selected = false;
                }
                question.time = 0;
            }
        };

        $scope.clear = function(optionClicked){
            if(!$scope.lecture.isMultiple){
                for (var i = 0; i < $scope.currentQuestion.options.length; i++){
                    var option = $scope.currentQuestion.options[i];
                    option.selected = (optionClicked.text == option.text);
                }
            }
            $scope.answerCorrect = null;
        };
        
        $scope.shuffleQuestions = function(array) {
            $scope.lecture.questions = knlUtils.shuffleArray($scope.lecture.questions);
            for (var i = 0; i < $scope.lecture.questions.length; i++){
                var question = $scope.lecture.questions[i];
                question.options = knlUtils.shuffleArray(question.options);
            }
        };

        $scope.init();

    }
]);