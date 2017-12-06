'use strict';

var app = angular.module('knl');

app.directive('confirmClick', [
    '$timeout',
    '$document',
    function ($timeout, $document) {
      return {
        scope: {},
        link: function (scope, element, attrs) {
          var actionText, promise, textWidth;
          actionText = element.html();
          scope.$watch('confirmingAction', function (newVal, oldVal) {
            var body, clone;
            if (newVal === oldVal && oldVal === false) {
              clone = element.clone();
              clone.css({
                left: '-9999px',
                position: 'absolute'
              });
              body = $document[0].body;
              body.appendChild(clone[0]);
              textWidth = clone[0].offsetWidth + 'px';
              body.removeChild(clone[0]);
            }
            scope.setTexts();
          });
          scope.setTexts = function(){
            if (scope.confirmingAction) {
              element.text(attrs.confirmMessage);
              element.css({ maxWidth: '300px' });
              return element.addClass('confirming');
            } else {
              element.html(actionText);
              element.css({ maxWidth: textWidth });
              return element.removeClass('confirming');
            }
          };
          return element.bind('click', function () {
            if(scope.preventDoubleClick ||
              (element[0].attributes.disabled && element[0].attributes.disabled.value == 'disabled')) return;
            scope.preventDoubleClick = true;
            $timeout(function(){
              scope.preventDoubleClick = false;
            },50);
            if (!scope.confirmingAction) {
              scope.$apply(function () {
                return scope.confirmingAction = true;
              });
              return promise = $timeout(function () {
                return scope.confirmingAction = false;
              }, 3000);
            } else {
              $timeout.cancel(promise);
              element.removeClass('confirming');
              scope.$apply(function () {
                scope.confirmingAction = false;
                scope.setTexts(false);
              });
              return scope.$parent.$apply(attrs.confirmClick);
            }
          });
        }
      };
    }
]);