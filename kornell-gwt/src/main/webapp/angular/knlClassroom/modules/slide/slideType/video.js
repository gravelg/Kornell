'use strict';

var app = angular.module('knlClassroom');

app.controller('VideoSlideController', [
	'$scope',
	'$rootScope',
	'$timeout',
	'$sce',
	'$location',
	function($scope, $rootScope, $timeout, $sce, $location) {

		var init = function(){
			$scope.sce = $sce;	
			$scope.contentPath = $rootScope.classroomPath + 'videos/'+$scope.slide.id;

	  		$scope.sources = [];
	  		angular.forEach($scope.classroomInfo.availableVideoSizes || [''], function(availableVideoSize){
	  			$scope.sources.push({
			        src: $scope.contentPath+'_'+availableVideoSize+'p.mp4',
			        type: 'video/mp4',
			        label: availableVideoSize+'p',
			        res: availableVideoSize
			      });
	  		});

			initializeVideo();
			initializeEvents();
		};
		
		var initializeVideo = function(){
	    	$scope.videoElementId = 'knl_video_single';
		    $scope.vidObj = videojs($scope.videoElementId);
		    
		    // hide the current loaded poster
		    $('div.vjs-poster').hide();
		    
		    $scope.vidObj.ready(function() {
		    	resetVideoState();
		        setupResolutionSwitcher();
				$scope.vidObj.remember($scope.slideUUID);
				$timeout(function(){
					getVidApiElement().attr('poster',$scope.contentPath+'.png');
				});
			});
		};

	    var resetVideoState = function(){
		    var setupOpt = {
			    controls: true,
			    'autoplay' : false,
			    'preload' : 'auto',
			    'example_option': true, 
			    'fluid': true
			};
			// hide the video UI
			getVidApiElement().hide();
			// and stop it from playing
			$scope.vidObj.pause();
			// reset the UI states
			getVidElement().removeClass('vjs-playing').addClass('vjs-paused');
			getVidApiElement().attr('setup', JSON.stringify(setupOpt));
			// load the new sources
			$scope.vidObj.load();
			getVidApiElement().show();
	    };

	    var setupResolutionSwitcher = function(){
			$scope.vidObj.videoJsResolutionSwitcher({
			  	ui: true,
			    default: getSource().res, // Default resolution [{Number}, 'low', 'high'],
			    dynamicLabel: false
			});

			document.currentVideoJsSource = getSource();
			$scope.vidObj.updateSrc($scope.sources);  
	    };

		var getSource = function(){
			var w = Math.max(parent.document.documentElement.clientWidth, parent.window.innerWidth || 0),
				videoResIndex = (w <= 600 ? 0 : 
					(w < 1280 ? 1 : 2));
			return $scope.sources[videoResIndex];
		};

		var initializeEvents = function() {
			// courseClassDetailsShown event handler
			document.courseClassDetailsShownEventHandler = function(courseClassDetailsShownEvent) {
			    courseClassDetailsShownEvent.detail.courseClassDetailsShown &&
			        document.getElementById('knl_video_single') &&
			        $scope.vidObj.pause();
			}
			// listen for courseClassDetailsShown event
			parent.document.addEventListener('courseClassDetailsShown', document.courseClassDetailsShownEventHandler, false);
			
			//callback for resize event
			var callback = function() {
				if(!window) return;
				if(document.currentVideoJsSource && document.currentVideoJsSource.label != getSource().label){
					$scope.vidObj.currentResolution(getSource().label);
					document.currentVideoJsSource = getSource();
				}
			};

			//resize event on parent window
		    if (parent.window == null || typeof(parent.window) == 'undefined') return;
		    if (parent.window.addEventListener) {
		        parent.window.addEventListener('resize', callback, false);
		    } else if (parent.window.attachEvent) {
		        parent.window.attachEvent('onresize', callback);
		    } else {
		        parent.window['onresize'] = callback;
		    }
		};

	    var getVidElement = function(){
	    	return $('#'+$scope.videoElementId);
	    };

	    var getVidApiElement = function(){
	    	return $('#'+$scope.videoElementId+'_html5_api');
	    };

	    init();

	}
]);
