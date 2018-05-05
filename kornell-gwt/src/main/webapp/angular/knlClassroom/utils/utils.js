'use strict';

var app = angular.module('knlClassroom');

app.factory('knlUtils', [
	'$rootScope',
	'$timeout',
	'$sce',
	'$location',
	function($rootScope, $timeout, $sce, $location) {
		var knlUtils = {};

        knlUtils.doLMSGetValueSanitized = function(key){
            $rootScope.isDebug && console.log('getAttribute', key);
            try {
                var value = doLMSGetValue(key);
                return value && value != 'null' ? value : null;
            } catch(err) {
                console.log("Error attempting to get SCORM attribute.", key);
                return null;
            }
        };

        knlUtils.setAttribute = function(key, value){
            $rootScope.isDebug && console.log('setAttribute', key, value);
            try {
                doLMSSetValue(key, value);
            } catch(err) {
                console.log("Error attempting to save SCORM attribute.");
            }
        };

        knlUtils.doLMSSetBooleanValueSanitized = function(key, value){
            value = (value !== null && value !== undefined) ? value : false;
            knlUtils.setAttribute(key, '' + value);
        };

        knlUtils.getKnlAttributeName = function(key){
            return 'knl.' + key;
        };

        knlUtils.setKnlAttribute = function(key, value){
            knlUtils.setAttribute(knlUtils.getKnlAttributeName(key), value);
        };

        knlUtils.getActionAttributeName = function(key){
            return knlUtils.getKnlAttributeName('action.' + key);
        };

        knlUtils.setActionAttribute = function(key, value){
            knlUtils.setAttribute(knlUtils.getActionAttributeName(key), value);
        };

        knlUtils.getLectureAttributeName = function(key){
            return knlUtils.getLectureAttributeNameWithIndex($rootScope.lectureUUID, key);
        };

        knlUtils.getLectureAttributeNameWithIndex = function(lectureUUID, key){
            return knlUtils.getKnlAttributeName('lecture.' + lectureUUID + '.' + key);
        };

        knlUtils.setLectureAttribute = function(key, value){
            knlUtils.setAttribute(knlUtils.getLectureAttributeName(key), value);
        };

        knlUtils.next = function(){
            knlUtils.setActionAttribute('next', 'true');
        };

        knlUtils.getAttemptAttributeName = function(key){
            return knlUtils.getLectureAttributeName('attempt.' + knlUtils.getCurrentAttemptIndex() + '.' + key);
        };

        knlUtils.setAttemptAttribute = function(key, value){
            knlUtils.setAttribute(knlUtils.getAttemptAttributeName(key), value);
        };

        knlUtils.getQuestionAttributeName = function(questionIndex, key){
            return knlUtils.getAttemptAttributeName('question.' + questionIndex + '.' + key);
        };

        knlUtils.setQuestionAttribute = function(questionIndex, key, value){
            knlUtils.setAttribute(knlUtils.getQuestionAttributeName(questionIndex, key), value);
        };

        knlUtils.getOptionAttributeName = function(questionIndex, optionIndex, key){
            return knlUtils.getQuestionAttributeName(questionIndex, 'option.' + optionIndex + '.' + key);
        };

        knlUtils.setOptionAttribute = function(questionIndex, optionIndex, key, value){
            knlUtils.setAttribute(knlUtils.getOptionAttributeName(questionIndex, optionIndex, key), value);
        };

        knlUtils.getAttemptCount = function() {
            return knlUtils.doLMSGetValueSanitized(knlUtils.getLectureAttributeName('attempt._count')) || 0;
        };

        knlUtils.getLastAttemptIndex = function() {
            return knlUtils.getAttemptCount() - 1;
        };

        knlUtils.getCurrentAttemptIndex = function() {
            return knlUtils.getLastAttemptIndex() + 1;
        };

        knlUtils.initFinalExam = function(){
            var initialState = {
                score: knlUtils.doLMSGetValueSanitized('cmi.core.score.raw'),
                isApproved: knlUtils.doLMSGetValueSanitized(knlUtils.getLectureAttributeName('isApproved')),
                currentScore: knlUtils.doLMSGetValueSanitized(knlUtils.getLectureAttributeName('currentScore'))
            }
            return initialState;
        };

        knlUtils.saveExamAttempt = function(score) {
            var question, option;
            for (var questionIndex = 0; questionIndex < $rootScope.lecture.questions.length; questionIndex++){
                question = $rootScope.lecture.questions[questionIndex];
                for (var optionIndex = 0; optionIndex < question.options.length; optionIndex++){
                    option = question.options[optionIndex];
                    option.expected && knlUtils.setOptionAttribute(questionIndex, optionIndex, 'expected', 'true');
                    option.selected && knlUtils.setOptionAttribute(questionIndex, optionIndex, 'selected', 'true');
                }
                knlUtils.setQuestionAttribute(questionIndex, 'option._count', question.options.length);
                knlUtils.setQuestionAttribute(questionIndex, 'correct', question.correct ? 'true' : 'false');
                knlUtils.setQuestionAttribute(questionIndex, 'time', question.time);
            }
            knlUtils.setAttemptAttribute('question._count', $rootScope.lecture.questions.length);
            knlUtils.setAttemptAttribute('score', score);
            knlUtils.setLectureAttribute('attempt._count', (knlUtils.getCurrentAttemptIndex() + 1));
        };

        knlUtils.isApproved = function(){
            var isApproved = true, lectureIndex = 0;
            angular.forEach($rootScope.classroomInfo.modules, function(module){
                angular.forEach(module.lectures, function(lecture){
                    if(lecture.type == 'finalExam'){
                        isApproved = isApproved && 
                            (knlUtils.doLMSGetValueSanitized(knlUtils.getLectureAttributeNameWithIndex(lectureIndex, 'isApproved')) === 'true');
                    }
                    lectureIndex++;
                });
            });
            return isApproved;
        };

        knlUtils.saveApproved = function($cmiScoreRaw){
            knlUtils.setLectureAttribute('currentScore', cmiScoreRaw);
            knlUtils.setLectureAttribute('isApproved', 'true');
            knlUtils.setLectureAttribute('attempt.correct_index', knlUtils.getLastAttemptIndex());
        };
        
        knlUtils.shuffleArray = function(array) {
            for (var i = array.length - 1; i > 0; i--) {
                var j = Math.floor(Math.random() * (i + 1));
                var temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
            return array;
        };
		
		return knlUtils;
	}
]);
