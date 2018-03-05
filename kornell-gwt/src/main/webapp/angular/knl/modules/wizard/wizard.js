'use strict';

var app = angular.module('knl');

app.controller('WizardController', [
  '$scope',
  '$rootScope',
  '$timeout',
  '$window',
  '$sce',
  '$location',
  '$uibModal',
  'toaster',
  'FileUploader',
  'SLIDE_TYPES',
  function($scope, $rootScope, $timeout, $window, $sce, $location, $uibModal, toaster, FileUploader, SLIDE_TYPES) {

    $scope.domain = window.location.host.indexOf('localhost:') >= 0 ? '*' : parent.location;

    parent.postMessage({type: "wizardReady", message: ""}, $scope.domain);
    
    var uploader = $scope.uploader = new FileUploader();

    uploader.onAfterAddingFile = function(fileItem) {
      fileItem.disableMultipart = true;

      var allowedExtensions = $scope.selectedNode.type === 'video' ? 'mpg, mpeg, mp4, mov' : 'jpg, jpeg, gif, png',
        fileName = fileItem.file && fileItem.file.name,
        fileParts = fileName && fileName.split('.'),
        fileExtension = fileParts && fileParts.length > 1 && fileParts[1],
        isAllowedExtension = fileExtension && allowedExtensions.indexOf(fileExtension) >= 0;

      if(isAllowedExtension){
        if(uploader.queue.length > 1){
          uploader.queue.splice(0, 1);
        }
        uploader.requestUploadPath = false;
        fileItem.file.name = $rootScope.uuid() + '.' + fileExtension;
        parent.postMessage({type: "requestUploadPath", message: fileItem.file.name}, $scope.domain);
      } else {
        toaster.pop("error", "Erro", "Extensão do arquivo inválida. Extensões aceitas: " + allowedExtensions + '.');
      }
    };

    uploader.onBeforeUploadItem = function(item) {
      if(!uploader.requestUploadPath) {
        toaster.pop("error", "Erro", "Erro ao tentar fazer o upload do arquivo. Tente novamente mais tarde ou entre em contato com o suporte.");
        return;
      }
      if(uploader.queue.length == 1){
        uploader.queue[0].url = uploader.requestUploadPath;
        uploader.queue[0].uploader.url = uploader.requestUploadPath;
        uploader.queue[0].method = 'PUT';
        uploader.queue[0].fullURL = uploader.requestUploadPath.split('.s3.amazonaws.com')[1].split('?AWS')[0];
        //item.removeAfterUpload = true;

      }
      console.info('onBeforeUploadItem', uploader.queue);
    };

    uploader.onSuccessItem = function(fileItem, response, status, headers) {
      toaster.pop("success", "Sucesso", "Upload concluído com sucesso.");
      $scope.selectedNode.id = fileItem.fullURL;
      uploader.queue = [];
    };

    uploader.onErrorItem = function(fileItem, response, status, headers) {
      toaster.pop("error", "Erro", "Erro ao tentar fazer o upload do arquivo. Tente novamente mais tarde ou entre em contato com o suporte.");
    };

    window.addEventListener('message',function(event) {
      if(event.data.type === 'classroomJsonLoad'){
        $scope.root = JSON.parse(event.data.message);
        $scope.initWizard();
      } else if(event.data.type === 'classroomJsonNew') {
        $scope.newTree(event.data.message);
        $scope.initWizard();
      } else if(event.data.type === 'responseUploadPath') {
        console.log(event.data);
          uploader.requestUploadPath = event.data.message;
      }
    },false);
    
    $scope.initWizard = function(){

      $scope.uiTreeOptions = {
        accept: function (src, dest, destIndex) {
          switch(dest.depth()){
            case 1:
              return (src.$modelValue.itemType === 'topic');
            case 2:
              return (src.$modelValue.itemType === 'slide');
            default:
              return false;
          }
        },
        dropped: function (event) {
        }
      };

      $scope.colorPickeroptions = {
        format: 'hex', 
        alpha: false
      };

      angular.element($window).bind('resize', function(){
        $scope.calculateLimitToDots();
        $scope.$digest();
      });

      $scope.questionPickerSortableOptions = {
        stop: function(e, ui) { 
          var item = ui.item[0],
            newPosition = parseInt(ui.item[0].innerText - 1);
          if(newPosition <= $scope.selectedQuestionIndex && $scope.oldPosition > $scope.selectedQuestionIndex){
            $scope.selectQuestion($scope.selectedQuestionIndex + 1);
          } else if(newPosition >= $scope.selectedQuestionIndex && $scope.oldPosition < $scope.selectedQuestionIndex){
            $scope.selectQuestion($scope.selectedQuestionIndex - 1);
          } else if($scope.oldPosition === $scope.selectedQuestionIndex){
            $scope.selectQuestion(newPosition);
          }
        },
        update: function(e, ui) {
          $scope.oldPosition = parseInt(ui.item[0].innerText - 1);
        }
      };
      
      $scope.blankQuestion = {
        text: 'At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium?',
        options: [
          {
            text: "Voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati",
            expected: true
          },
          {
            text: "Cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi",
            expected: false
          },
          {
            text: "Et harum quidem rerum facilis est et expedita distinctio",
            expected: false
          },
          {
            text: "Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil",
            expected: false
          }
        ]
      };
      
      $scope.data = [$scope.root];

      $scope.calculateLimitToDots();

      $scope.verifyTree();
      $scope.savedRoot = angular.copy($scope.root);

      $scope.$watch('root', function() {
        $scope.verifyTreeHelper();
      }, true);

      $timeout(function(){
        $scope.goToNode($scope.lastSlideUUID || $scope.lastTopicUUID);
      });
      
    };

    $scope.saveTree = function() {
      $scope.savedRoot = angular.copy($scope.root);
      $scope.verifyTree();
      var contents = decodeURI(Base64.decode(localStorage.KNLwp));
      parent.postMessage({type: "wizardSave", message: contents}, $scope.domain);
    };

    $scope.discardTree = function() {
      $scope.root = angular.copy($scope.savedRoot);
      $scope.data = [$scope.root];
      $scope.goToNode($scope.selectedNode.uuid);
      toaster.pop("success", "Sucesso", "As alterações foram descartadas com sucesso.");
    };

    $scope.verifyTreeHelper = function() {
      if($scope.verifyTimer){
        $timeout.cancel($scope.verifyTimer);
      }
      $scope.verifyTimer = $timeout($scope.verifyTree, 500);
    };

    $scope.verifyTree = function() {
        var verifySavedStatus = function(node) {
          var concatenateUUIDs = function(array) {
            var uuids = "";
            angular.forEach(array, function(obj){
              uuids += obj.uuid + ",";
            });
            return uuids;
          };

          if($scope.savedRoot) {
            var o1 = angular.copy(node),
                o2 = angular.copy($scope.getNodeByUUID(node.uuid, true));
            if(o2){
              delete o1.isUnsaved;
              delete o2.isUnsaved;
              if(o1.itemType === 'root'){
                o1.topics = concatenateUUIDs(o1.topics);
                o2.topics = concatenateUUIDs(o2.topics);
              } else if(o1.itemType === 'topic'){
                o1.slides = concatenateUUIDs(o1.slides);
                o2.slides = concatenateUUIDs(o2.slides);
                o1.count = o2.count = 0;
              } else if(o1.itemType === 'slide'){
                o1.count = o2.count = o1.totalSlidesCount = o2.totalSlidesCount = 0;
              }
            }
            if(!angular.equals(o1, o2)){
              node.isUnsaved = true;
              $scope.treeIsUnsaved = true;
            } else {
              delete node.isUnsaved;
            }
          }
        };

        $scope.treeIsUnsaved = false;
        $scope.hasVimeoSlides = false;
        $scope.hasYoutubeSlides = false;
        $scope.hasVideoSlides = false;
        $scope.hasFinalExamSlide = false;
        var tCount = 0, totalSlidesCount = 0;
        $scope.root.uuid = $scope.root.uuid || $rootScope.uuid();
        angular.forEach($scope.root.topics, function(topic){
          topic.uuid = topic.uuid || $rootScope.uuid();
          $scope.lastTopicUUID = topic.uuid;
          topic.parentUUID = $scope.root.uuid;
          topic.count = tCount++;
          var sCount = 0;
          angular.forEach(topic.slides, function(slide){
            slide.uuid = slide.uuid || $rootScope.uuid();
            $scope.lastSlideUUID = slide.uuid;
            slide.parentUUID = topic.uuid;
            slide.count = sCount++;
            slide.totalSlidesCount = totalSlidesCount++;
            if(slide.type === 'vimeo'){
              $scope.hasVimeoSlides = true;
            }
            if(slide.type === 'video'){
              $scope.hasVideoSlides = true;
            }
            if(slide.type === 'youtube'){
              $scope.hasYoutubeSlides = true;
            }
            if(slide.type === 'bubble'){
              $scope.lastBubbleColor2 = (slide.color2 && slide.color2.length) ? slide.color2 : $scope.lastBubbleColor2;
            }
            if(slide.type === 'finalExam'){
              $scope.hasFinalExamSlide = true;
            }
            verifySavedStatus(slide);
          });
          verifySavedStatus(topic);
        });
        verifySavedStatus($scope.root);
        localStorage.KNLwp = Base64.encode(encodeURI(JSON.stringify($scope.root)));
    };

    $scope.getNodeByUUID = function(uuid, getFromSavedRoot){
      var found;
      var tree = getFromSavedRoot ? $scope.savedRoot : $scope.root;
      if(tree.uuid === uuid) found = tree;
      angular.forEach(tree.topics, function(topic){
        if(topic.uuid === uuid) found = topic;
        angular.forEach(topic.slides, function(slide){
          if(slide.uuid === uuid) found = slide;      
        });
      });
      return found;
    };

    $scope.getSlideNameByType = function(type){
      for(var i = 0; i < SLIDE_TYPES.length; i++){
        if(SLIDE_TYPES[i].type == type){
          return SLIDE_TYPES[i].name;
        }
      }
    };

    $scope.newSlide = function(scope){
      var nodeData = scope.$modelValue;
      var modalInstance = $uibModal.open({
        animation: true,
        component: 'addSlideModal',
        resolve: {
          hasFinalExamSlide: function() {
            return $scope.hasFinalExamSlide
          }
        }
      });

      modalInstance.result.then(
        function (selectedType) {
          var slideName = 'Slide ' + (nodeData.count + 1) + '.' + (nodeData.slides.length + 1) + ': ' + $scope.getSlideNameByType(selectedType);
          var slide = {
            itemType: 'slide',
            title: slideName,
            type: selectedType,
            uuid: $rootScope.uuid()
          }
          if(slide.type == 'bubble') {
            slide.text = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.';
            slide.id = 'goncaloprado.png';
            slide.text2 = 'Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.';
            slide.id2 = 'goncaloprado.png';
            slide.color2 = $scope.lastBubbleColor2 || '3D3D40';
          } else if(slide.type == 'youtube') {
            slide.id = 's1mczCFXcSY';
          } else if(slide.type == 'vimeo') {
            slide.id = '242792908';
          } else if(slide.type == 'video') {

          } else if(slide.type == 'question') {
            slide.text = $scope.blankQuestion.text;;
            slide.isMultiple = false;
            slide.shuffleQuestions = false;
            slide.options = angular.copy($scope.blankQuestion.options);
          } else if(slide.type == 'finalExam') {
            slide.questions = [angular.copy($scope.blankQuestion), angular.copy($scope.blankQuestion), angular.copy($scope.blankQuestion)];
          } else if(slide.type == 'text') {
            slide.text = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.';
            slide.showWell = true;
          } else if(slide.type == 'image') {
            slide.id = 'https://static.pexels.com/photos/355988/pexels-photo-355988.jpeg';
          }          
          nodeData.slides.push(slide);
          $scope.goToNode(slide.uuid);
        },
        function(){

        }
      );
    };

    $scope.newQuestion = function(selectedQuestion) {
      $scope.selectedNode.questions.push(angular.copy($scope.blankQuestion));
      $scope.selectQuestion($scope.selectedNode.questions.length - 1);
    };

    $scope.removeQuestion = function(index) {
      $scope.selectedNode.questions.splice(index, 1);
      $scope.selectQuestion(0);
    };

    $scope.newOption = function(selectedQuestion) {
      selectedQuestion.options.push({text: 'Quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet'});
    };

    $scope.newTree = function(courseTitle){
      $scope.root = {
        title: courseTitle,
        availableVideoSizes: "144,360,720",
        colorBackground: "EBEBEB",
        colorFont: "0284B5",
        colorTheme: "0284B5",
        colorTitle: "EBEBEB",
        itemType: "root",
        paddingTopIframe: 56,
        topics: [],
        uuid: $rootScope.uuid()
      };
      $scope.newTopic();
    }

    $scope.newTopic = function(goToTopic){
      var newTopic = {
        itemType: 'topic',
        title: 'Tópico ' + ($scope.root.topics.length + 1),
        uuid: $rootScope.uuid(),
        slides: []
      };
      $scope.root.topics.push(newTopic);
      if(goToTopic){
        $scope.goToNode(newTopic.uuid);
      }
    };

    $scope.edit = function (scope) {
      $scope.selectedNode = scope.$modelValue;
      $scope.selectedNodeSaved = $scope.getNodeByUUID($scope.selectedNode.uuid, true);
      $scope.selectedNodeScope = scope;
      $rootScope.selectedTab = 'edit';
      $scope.previewURL = $sce.trustAsResourceUrl(
        "knlClassroom/index.html#!/slide" + 
        "?preview=1" + 
        "&uuid=" + $scope.selectedNode.uuid +
        "&classroomPath=/../knl/classroom");
      var editPanel = $('#editPanel').get(0)
      editPanel && editPanel.scrollIntoView();
    };

    $scope.remove = function () {
      if($scope.selectedNodeScope){
        var node = $scope.selectedNode,
            parentUUID = node.parentUUID,
            parentArrayAttribute = node.itemType + 's', //topics or slides
            parentArray = $scope.getNodeByUUID(parentUUID)[parentArrayAttribute];
        if(node.count == 0 && parentArray.length == 1){
          // go to parent if it's the last
          $scope.goToNode(parentUUID);
        } else if(node.count == 0){
          // if it's the first, go to the next
          $scope.goToNode(parentArray[node.count + 1].uuid);
        } else {
          // otherwise go to the previous
          $scope.goToNode(parentArray[node.count - 1].uuid);
        }
        $scope.selectedNodeScope.remove();
      }

    };

    $scope.goToNode = function(uuid){
      $timeout(function(){
        $('#nodeEdit_'+uuid)[0] && $('#nodeEdit_'+uuid)[0].click();
      });
    };

    $scope.isMultipleClicked = function(slide){
      if(!slide.isMultiple){
        angular.forEach(slide.options, function(option, i){
          if(option.expected){
            $scope.optionClicked(slide, option, i);
          } else if (i === (slide.options.length - 1)) {
            $scope.optionClicked(slide, slide.options[0], 0);
          } else {
            option.expected = false;
          }
        });
      }
    };

    $scope.optionClicked = function(slide, option, index){
      if(!slide.isMultiple && option.expected){
        angular.forEach(slide.options, function(o, i){
          if(i != index){
            o.expected = false;
          }
        });
      } else {
        slide.options[index].expected = false;
      }
    };

    $scope.selectQuestion = function(index) {
      $scope.selectedQuestionIndex = index;
      $scope.selectedQuestion = $scope.selectedNode.questions[index];
    };

    $scope.toggleOptions = function() {
      angular.forEach($scope.selectedNode.questions, function(question){
        $scope.toggleOption(question);
      });
    };

    $scope.toggleOption = function(question, index){
      var selectedIndexes = [];
      angular.forEach(question.options, function(o, i){
        if(o.expected) {
          selectedIndexes.push(i);
        }
      });
      // select multiple was toggled
      if(index === undefined && !$scope.selectedNode.isMultiple){
        // toggle first option if none was clicked
        if(!selectedIndexes.length){
          question.options[0].expected = true;
        } else {
          // if there's more than one selected, keep only one if it's not multiple choice      
          angular.forEach(question.options, function(o, i){
            question.options[i].expected = (i === selectedIndexes[0]);
          });
        }
      } else {
        if(!$scope.selectedNode.isMultiple && question.options[index].expected){
          // if it's not multiple, only the clicked option should be selected
          angular.forEach(question.options, function(o, i){
              o.expected = (i === index);
          });
        } else if(!$scope.selectedNode.isMultiple){
          // if it's not multiple, make sure that one is always selected
          question.options[index].expected = true;
        }
      }
    };

    $scope.trimVimeoURL = function(){
      $scope.selectedNode.id = 
        $scope.selectedNode.id.indexOf('vimeo.com/') >= 0 ?
        $scope.selectedNode.id.split('vimeo.com/')[1] :
        $scope.selectedNode.id;
    };

    $scope.trimYouTubeURL = function(){
      var stripIdFromVideoURL = function(url){
        var r, rx = /^.*(?:(?:youtu\.be\/|v\/|vi\/|u\/\w\/|embed\/)|(?:(?:watch)?\?v(?:i)?=|\&v(?:i)?=))([^#\&\?]*).*/;
        try {
          return url.match(rx)[1];
        } catch(err){
          return url;
        }
      }
      $scope.selectedNode.id = stripIdFromVideoURL($scope.selectedNode.id);
    };

    $scope.calculateLimitToDots = function(){
      if($window.innerWidth > 1800) {
        $scope.limitToDots = 45;
      } else if($window.innerWidth > 1600) {
        $scope.limitToDots = 35;
      } else if($window.innerWidth > 1400) {
        $scope.limitToDots = 30;
      } else if($window.innerWidth > 1200) {
        $scope.limitToDots = 25;
      } else if($window.innerWidth > 1100) {
        $scope.limitToDots = 20;
      } else if($window.innerWidth > 991) {
        $scope.limitToDots = 15;
      } else if($window.innerWidth > 600) {
        $scope.limitToDots = 100;
      } else if($window.innerWidth > 450) {
        $scope.limitToDots = 500;
      } else {
        $scope.limitToDots = 35;
      }
      $scope.innerWidth = $window.innerWidth;
    };

//$scope.root = {"title":"KNL","availableVideoSizes":"144,360,720","colorBackground":"EBEBEB","colorFont":"0284B5","colorTheme":"0284B5","colorTitle":"EBEBEB","itemType":"root","paddingTopIframe":56,"topics":[{"itemType":"topic","title":"Tópico 1","uuid":"65370d61-67c0-4a78-86bc-2f1c5730b5e5","slides":[{"itemType":"slide","title":"Slide 1.1: Imagem","type":"image","uuid":"ca10d6da-1952-4830-9f9a-2c34a3485146","id":"https://static.pexels.com/photos/355988/pexels-photo-355988.jpeg","$$hashKey":"object:57","parentUUID":"65370d61-67c0-4a78-86bc-2f1c5730b5e5","count":0,"totalSlidesCount":0}],"parentUUID":"4849e1fc-ea88-4247-be80-155052f0fb0e","count":0,"$$hashKey":"object:19"}],"uuid":"4849e1fc-ea88-4247-be80-155052f0fb0e","$$hashKey":"object:13"};
//$scope.initWizard();
  }
]);


app.component('addSlideModal', {
  templateUrl: 'addSlideModal.html',
  bindings: {
    resolve: '<',
    close: '&',
    dismiss: '&'
  },
  controller: [
    'SLIDE_TYPES',
    function (SLIDE_TYPES) {
      var $ctrl = this;

      $ctrl.$onInit = function () {
          $ctrl.SLIDE_TYPES = angular.copy(SLIDE_TYPES);
          if($ctrl.resolve.hasFinalExamSlide){
            $ctrl.SLIDE_TYPES.splice($ctrl.SLIDE_TYPES.indexOf("finalExam"),1);
          }
      };

      $ctrl.ok = function () {
        if($ctrl.slideType && $ctrl.slideType !== 'slideTypeSeparator'){
          $ctrl.close({$value: $ctrl.slideType});
        }
      };

      $ctrl.cancel = function () {
        $ctrl.dismiss({$value: 'cancel'});
      };
    }
  ]
});