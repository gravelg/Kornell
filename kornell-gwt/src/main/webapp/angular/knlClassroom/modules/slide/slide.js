'use strict';

var app = angular.module('knlClassroom');

app.controller('SlideController', [
	'$scope',
	'$rootScope',
	'$timeout',
	'$location',
	'knlUtils',
	function($scope, $rootScope, $timeout, $location, knlUtils) {

    var classroomInfo = JSON.parse(decodeURI(Base64.decode(window.isPreview ? localStorage.KNLwp : localStorage.KNLw)));

    classroomInfo.slides = [];
    angular.forEach(classroomInfo.topics, function(topic){
        angular.forEach(topic.slides, function(slide){
            classroomInfo.slides.push(slide);
        });
    });

    $rootScope.classroomInfo = classroomInfo;

    $scope.getNodeByUUID = function(uuid){
      var found;
      if($rootScope.classroomInfo.uuid === uuid) found = $rootScope.classroomInfo;
      angular.forEach($rootScope.classroomInfo.topics, function(topic){
        if(topic.uuid === uuid) found = topic;
        angular.forEach(topic.slides, function(slide){
          if(slide.uuid === uuid) found = slide;      
        });
      });
      return found;
    };

		$scope.initSlide = function(){
			if(!$scope.classroomInfo) return;

			$scope.slideUUID = $location.$$search.uuid || 0;
			$rootScope.slideUUID = $scope.slideUUID;

			$scope.slide = $scope.getNodeByUUID($scope.slideUUID);
      $scope.topic = $scope.getNodeByUUID($scope.slide.parentUUID);
			$rootScope.slide = $scope.slide;

      if(knlUtils.isApproved()){
        knlUtils.setActionAttribute('prevEnabled', 'true');
        knlUtils.setActionAttribute('nextEnabled', 'true');
      }
			$rootScope.evaluateTimer(true);

			var key = 'knl.slide.'+$scope.slideUUID+'.type';
        var slideType = doLMSGetValue(key);
      if(!slideType){
        knlUtils.setAttribute(key, $scope.slide.type);
      }

			if($scope.classroomInfo.colorBackground){
				$scope.bgStyle = 'background-color: #'+$scope.classroomInfo.colorBackground+';';
			}


      angular.forEach($scope.classroomInfo.topics, function(topic){
          angular.forEach(topic.slides, function(slide){
          	if($scope.slideUUID == slide.uuid){
          		$scope.topic = topic;
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

  	$scope.initSlide();
	}
]);
