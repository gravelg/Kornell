'use strict';

var app = angular.module('knlClassroom');

app.controller('LectureController', [
	'$scope',
	'$rootScope',
	'$timeout',
	'$location',
	'knlUtils',
	function($scope, $rootScope, $timeout, $location, knlUtils) {

    var classroomInfo = JSON.parse(decodeURI(Base64.decode(window.isPreview ? localStorage.KNLwp : $location.$$search.classroomInfo)));

    classroomInfo.lectures = [];
    angular.forEach(classroomInfo.modules, function(module){
        angular.forEach(module.lectures, function(lecture){
            classroomInfo.lectures.push(lecture);
        });
    });

    $rootScope.classroomInfo = classroomInfo;

    $scope.getNodeByUUID = function(uuid){
      var found;
      if($rootScope.classroomInfo.uuid === uuid) found = $rootScope.classroomInfo;
      angular.forEach($rootScope.classroomInfo.modules, function(module){
        if(module.uuid === uuid) found = module;
        angular.forEach(module.lectures, function(lecture){
          if(lecture.uuid === uuid) found = lecture;      
        });
      });
      return found;
    };

		$scope.initLecture = function(){
			if(!$scope.classroomInfo) return;

			$scope.lectureUUID = $location.$$search.uuid || 0;
			$rootScope.lectureUUID = $scope.lectureUUID;

			$scope.lecture = $scope.getNodeByUUID($scope.lectureUUID);
      $scope.module = $scope.getNodeByUUID($scope.lecture.parentUUID);
			$rootScope.lecture = $scope.lecture;

      if(knlUtils.isApproved()){
        knlUtils.setActionAttribute('prevEnabled', 'true');
        knlUtils.setActionAttribute('nextEnabled', 'true');
      }
			$rootScope.evaluateTimer(true);

			var key = 'knl.lecture.'+$scope.lectureUUID+'.type';
        var lectureType = knlUtils.doLMSGetValueSanitized(key);
      if(!lectureType){
        knlUtils.setAttribute(key, $scope.lecture.type);
      }

      if($scope.lecture.id && $scope.lecture.id.indexOf('/') == 0 && location.hostname === 'localhost'){
        $scope.prefixURL = 'http://localhost:8888';
      }
      if($scope.classroomInfo.colorBackground){
        $scope.bgStyle = 'background-color: #'+$scope.classroomInfo.colorBackground+';';
      }
			if($scope.lecture.imageBackground){
        var prefix = '';
        if($scope.lecture.imageBackground && $scope.lecture.imageBackground.indexOf('/') == 0 && location.hostname === 'localhost'){
           prefix = 'http://localhost:8888';
        }
        $scope.bgStyle += 'background: url('+prefix+$scope.lecture.imageBackground+') no-repeat center center fixed;';
      } else if($scope.classroomInfo.imageBackground){
        var prefix = '';
        if($scope.classroomInfo.imageBackground && $scope.classroomInfo.imageBackground.indexOf('/') == 0 && location.hostname === 'localhost'){
           prefix = 'http://localhost:8888';
        }
        $scope.bgStyle += 'background: url('+prefix+$scope.classroomInfo.imageBackground+') no-repeat center center fixed;';
      } 

      angular.forEach($scope.classroomInfo.modules, function(module){
          angular.forEach(module.lectures, function(lecture){
          	if($scope.lectureUUID == lecture.uuid){
          		$scope.module = module;
          	}
          });
      });
	  };

  	$scope.toggleStructureDebug = function(){
	    $scope.isToggleStructureDebug = !$scope.isToggleStructureDebug;
  	};

  	$rootScope.evaluateTimer = function(isShow){
  		if($scope.isShow != isShow){
  			$scope.isShow = isShow;
  		}
  	};

    var initializeHideShowControl = function(){
      var hidden = "hidden";

      // Standards:
      if (hidden in document)
        document.addEventListener("visibilitychange", onchange);
      else if ((hidden = "mozHidden") in document)
        document.addEventListener("mozvisibilitychange", onchange);
      else if ((hidden = "webkitHidden") in document)
        document.addEventListener("webkitvisibilitychange", onchange);
      else if ((hidden = "msHidden") in document)
        document.addEventListener("msvisibilitychange", onchange);
      // IE 9 and lower:
      else if ("onfocusin" in document) 
        document.onfocusin = document.onfocusout = onchange;
      // All others:
      else
        window.onpageshow = window.onpagehide
        = window.onfocus = window.onblur = onchange;

      function onchange (evt) {
        var v = "visible", h = "hidden", isShow = true,
            evtMap = {
              focus:v, focusin:v, pageshow:v, blur:h, focusout:h, pagehide:h
            };

        evt = evt || window.event;
        
        if (evt.type in evtMap){
          isShow = evtMap[evt.type] == "visible";
        } else {
          isShow = !this[hidden];
        }

        var scope = angular.element(document.getElementById('knlClassroomApp')).scope();
        scope.evaluateTimer(isShow);
      }

      // set the initial state (but only if browser supports the Page Visibility API)
      if( document[hidden] !== undefined )
        onchange({type: document[hidden] ? "blur" : "focus"});
    };
    initializeHideShowControl();

  	$scope.initLecture();
	}
]);
