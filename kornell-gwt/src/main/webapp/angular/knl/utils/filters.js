'use strict';

var app = angular.module('knl');

app.filter('limitToDots', [
    function () {
        return function (input, limit) {
            if (!input || input.length <= limit) {
                return input;
            } else {
                return input.substring(0, limit) + '...';
            }
        };
    }
]);