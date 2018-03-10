'use strict';

var app = angular.module('knl', [
    'ngRoute',
    'ui.tree',
    'ngAnimate', 
    'ngSanitize',
    'ui.bootstrap',
    'color.picker',
    'ui.slimscroll',
    'ui.sortable',
    'textAngular',
    'angularFileUpload'
]);

app.config([
    '$routeProvider', 
    '$locationProvider', 
    '$provide', 
    function($routeProvider, $locationProvider, $provide) {
        $routeProvider.when('/wizard', {
            templateUrl: 'knl/modules/wizard/wizard.html',
            controller: 'WizardController'
        })
        .otherwise({
            redirectTo: '/wizard'
        });

        $provide.decorator('ColorPickerOptions', function($delegate) {
            var options = angular.copy($delegate);
            options.inputClass = 'form-control';
            return options;
        });
    }
]);

app.run([
    '$rootScope',
    '$location',
    '$http',
    '$timeout',
    function($rootScope, $location, $http, $timeout) {

        $rootScope.domain = window.location.host.indexOf('localhost:') >= 0 ? '*' : parent.location;

        $rootScope.$on('$locationChangeSuccess', function(next, current) {
            //ga('send', 'pageview', location.hash.substring(2, location.hash.length));
            window.scrollTo(0, 0);
        });

        $rootScope.toasterOptions = {
            'close-button':true, 
            'position-class': 'toast-top-center', 
            'time-out':{ 
                'toast-success': 2500, 
                'toast-warning': 2500, 
                'toast-error': 0 
            } 
        };

        $rootScope.uuid = function() {
          return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
          });
        };


        $rootScope.sendNotification = function(notificationType, message){
            parent.postMessage({
                type: "kornellNotification", 
                notificationType: notificationType, 
                message: message
            }, $rootScope.domain);
        };
    }
]);