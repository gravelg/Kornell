'use strict';

var app = angular.module('knlClassroom', [
    'ngRoute',
    'ngSanitize'
]);

app.config([
    '$routeProvider', 
    '$locationProvider', 
    function($routeProvider, $locationProvider) {
        $routeProvider.when('/lecture', {
            templateUrl: 'modules/lecture/lecture.html',
            controller: 'LectureController'
        })
        .otherwise({
            redirectTo: '/lecture'
        });
}]);

app.run([
    '$rootScope',
    '$location',
    '$http',
    '$timeout',
    function($rootScope, $location, $http, $timeout) {

        if($location.$$search && $location.$$search.preview === "1"){
            window.isPreview = true;
        }
        
        $rootScope.isDebug = (window.location.host.startsWith("localhost"));
        
        $rootScope.$on('$locationChangeSuccess', function(next, current) {
            //ga('send', 'pageview', location.hash.substring(2, location.hash.length));
            window.scrollTo(0, 0);
        });

    }
]);