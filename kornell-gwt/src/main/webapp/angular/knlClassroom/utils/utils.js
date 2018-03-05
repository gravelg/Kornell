'use strict';

var app = angular.module('knlClassroom');

app.factory('knlUtils', [
	'$rootScope',
	'$timeout',
	'$sce',
	'$location',
	function($rootScope, $timeout, $sce, $location) {
		var knlUtils = {};

        knlUtils.initFinalExam = function(){
            var initialState = {
                score: knlUtils.doLMSGetValueSanitized('cmi.core.score.raw'),
                isApproved: knlUtils.doLMSGetValueSanitized(knlUtils.getSlideAttributeName('isApproved')),
                currentScore: knlUtils.doLMSGetValueSanitized(knlUtils.getSlideAttributeName('currentScore'))
            }
            if(initialState.isApproved !== 'true'){
                knlUtils.setSlideAttribute('type', $rootScope.slide.type);
                knlUtils.setActionAttribute('nextEnabled', 'false');
            }
            return initialState;
        };

        knlUtils.saveExamAttempt = function(score) {
            var question, option;
            for (var questionIndex = 0; questionIndex < $rootScope.slide.questions.length; questionIndex++){
                question = $rootScope.slide.questions[questionIndex];
                for (var optionIndex = 0; optionIndex < question.options.length; optionIndex++){
                    option = question.options[optionIndex];
                    option.expected && knlUtils.setOptionAttribute(questionIndex, optionIndex, 'expected', 'true');
                    option.selected && knlUtils.setOptionAttribute(questionIndex, optionIndex, 'selected', 'true');
                }
                knlUtils.setQuestionAttribute(questionIndex, 'option._count', question.options.length);
                knlUtils.setQuestionAttribute(questionIndex, 'correct', question.correct ? 'true' : 'false');
                knlUtils.setQuestionAttribute(questionIndex, 'time', question.time);
            }
            knlUtils.setAttemptAttribute('question._count', $rootScope.slide.questions.length);
            knlUtils.setAttemptAttribute('score', score);
            knlUtils.setSlideAttribute('attempt._count', (knlUtils.getCurrentAttemptIndex() + 1));
        };

        knlUtils.doLMSGetValueSanitized = function(key){
            try {
                var value = doLMSGetValue(key);
                return value && value != 'null' ? value : null;
            } catch(err) {
                console.log("Error attempting to save SCORM attribute.");
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

        knlUtils.getSlideAttributeName = function(key){
            return knlUtils.getSlideAttributeNameWithIndex($rootScope.slideUUID, key);
        };

        knlUtils.getSlideAttributeNameWithIndex = function(slideUUID, key){
            return knlUtils.getKnlAttributeName('slide.' + slideUUID + '.' + key);
        };

        knlUtils.setSlideAttribute = function(key, value){
            knlUtils.setAttribute(knlUtils.getSlideAttributeName(key), value);
        };

        knlUtils.getAttemptAttributeName = function(key){
            return knlUtils.getSlideAttributeName('attempt.' + knlUtils.getCurrentAttemptIndex() + '.' + key);
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
            return knlUtils.doLMSGetValueSanitized(knlUtils.getSlideAttributeName('attempt._count')) || 0;
        };

        knlUtils.getLastAttemptIndex = function() {
            return knlUtils.getAttemptCount() - 1;
        };

        knlUtils.getCurrentAttemptIndex = function() {
            return knlUtils.getLastAttemptIndex() + 1;
        };

        knlUtils.next = function(){
            knlUtils.setActionAttribute('next', 'true');
        };

        knlUtils.isApproved = function(){
            var isApproved = true, slideIndex = 0;
            angular.forEach($rootScope.classroomInfo.topics, function(topic){
                angular.forEach(topic.slides, function(slide){
                    if(slide.type == 'finalExam'){
                        isApproved = isApproved && 
                            (knlUtils.doLMSGetValueSanitized(knlUtils.getSlideAttributeNameWithIndex(slideIndex, 'isApproved')) === 'true');
                    }
                    slideIndex++;
                });
            });
            return isApproved;
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
