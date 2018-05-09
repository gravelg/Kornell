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
                console.log("Error attempting to save SCORM attribute.", key, value);
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

        knlUtils.next = function(){
            knlUtils.setActionAttribute('next', 'true');
        };

        knlUtils.enableNext = function(enable){
            knlUtils.setActionAttribute('nextEnabled', enable ? 'true' : 'false');
        };

        knlUtils.enablePrev = function(enable){
            knlUtils.setActionAttribute('prevEnabled', enable ? 'true' : 'false');
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

        knlUtils.saveFinalExamJson = function(json){
            $rootScope.isDebug && console.log('saveFinalExamJson', json);
            json = angular.toJson(json);
            knlUtils.setAttribute(knlUtils.getKnlAttributeName('json'), json);
        };

        knlUtils.getFinalExamJson = function(){
            var json = knlUtils.doLMSGetValueSanitized(knlUtils.getKnlAttributeName('json'));
            json = angular.fromJson(json || { attempts: [] });
            $rootScope.isDebug && console.log('getFinalExamJson', json);
            return json;
        };

        knlUtils.saveExamAttempt = function(score, isApproved) {
            var json = knlUtils.getFinalExamJson();
            var attempt = {
                score: score,
                publishingUUID: $rootScope.classroomInfo.publishingUUID,
                questions: []
            };

            var questionClassroom, optionClassroom, question;
            for (var questionIndex = 0; questionIndex < $rootScope.lecture.questions.length; questionIndex++){
                questionClassroom = $rootScope.lecture.questions[questionIndex];
                question = {
                    uuid: questionClassroom.uuid,
                    time: questionClassroom.time,
                    isCorrect: questionClassroom.correct
                };
                for (var optionIndex = 0; optionIndex < questionClassroom.options.length; optionIndex++){
                    optionClassroom = questionClassroom.options[optionIndex];
                    if(optionClassroom.expected !== optionClassroom.selected){
                        question.wrongOptions = question.wrongOptions || [];
                        question.wrongOptions.push(optionClassroom.uuid);
                    }
                }
                attempt.questions.push(question);
            }
            json.attempts = json.attempts || [];
            json.attempts.push(attempt);

            if(isApproved && !json.isApproved) {
                json.currentScore = cmiScoreRaw;
                json.isApproved = true;
                json.attemptCorrectIndex = json.attempts.length - 2;
                attempt.isApproved = true;
            }
            knlUtils.saveFinalExamJson(json);
        };

        knlUtils.initFinalExam = function(){
            var json = knlUtils.getFinalExamJson(),
            initialState = {
                score: knlUtils.doLMSGetValueSanitized('cmi.core.score.raw'),
                isApproved: json.isApproved,
                currentScore: json.currentScore
            }
            return initialState;
        };

        return knlUtils;
    }
]);