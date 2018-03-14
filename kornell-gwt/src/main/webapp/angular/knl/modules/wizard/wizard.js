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
  'FileUploader',
  'LECTURE_TYPES',
  function($scope, $rootScope, $timeout, $window, $sce, $location, $uibModal, FileUploader, LECTURE_TYPES) {

    $rootScope.postMessageToParentFrame("wizardReady", "");

    window.addEventListener('message',function(event) {
      if(event.data.type === 'classroomJsonLoad'){
        $scope.root = JSON.parse(event.data.message);
        $scope.initWizard();
      } else if(event.data.type === 'classroomJsonNew') {
        $scope.newTree(event.data.message);
        $scope.initWizard();
      } else if(event.data.type === 'classroomJsonSaved'){
        $scope.blockPublishButton = false;
      }
    },false);
    
    $scope.initWizard = function(){

      $scope.uiTreeOptions = {
        accept: function (src, dest, destIndex) {
          switch(dest.depth()){
            case 1:
              return (src.$modelValue.itemType === 'module');
            case 2:
              return (src.$modelValue.itemType === 'lecture');
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
        if($scope.treeIsUnsaved) {
          $scope.saveTree();
        }
      }, true);

      $timeout(function(){
        $scope.goToNode($scope.lastLectureUUID || $scope.lastModuleUUID);
      });
      
    };

    $scope.publishTree = function(){
      if($scope.blockPublishButton) {
        return;
      }
      var publishTreeMessage = "Suas alterações até o momento foram salvas automaticamente, mas ainda não foram publicadas.<br><br>" +
        "Tem certeza que deseja publicá-las? O conteúdo será imediatamente acessível para todos os participantes de turmas que utilizam essa versão. Essa operação não poderá ser desfeita.";
      var publishTreeCallback = function(){
        $scope.blockPublishButton = true;
        $scope.savedRoot = angular.copy($scope.root);
        $scope.verifyTree();
        var contents = localStorage.KNLwp;
        $rootScope.postMessageToParentFrame("wizardPublish", contents);
      };
      $scope.confirmModal(publishTreeMessage, publishTreeCallback);
    };

    $scope.discardTree = function() {
      //TODO
      if($scope.blockPublishButton) {
        return;
      }
      var discardTreeMessage = "Tem certeza que deseja descartar TODAS as alterações desde a última publicação? Essa operação não poderá ser desfeita.";
      var discardTreeCallback = function(){
        $scope.blockPublishButton = true;
        $scope.root = angular.copy($scope.savedRoot);
        $scope.data = [$scope.root];
        $scope.goToNode($scope.selectedNode.uuid);
        $rootScope.postMessageToParentFrame("wizardDiscard", "");
      };
      $scope.confirmModal(discardTreeMessage, discardTreeCallback);
    };

    $scope.confirmModal = function(message, callback) {
      var modalInstance = $uibModal.open({
        animation: true,
        component: 'confirmModal',
        resolve: {
          message: function() {
            return message;
          }
        }
      });

      modalInstance.result.then(
        function (selectedType) {
          callback();
        },
        function(){
        }
      );
    };

    $scope.saveTree = function() {
      $scope.blockPublishButton = true;
      $scope.savedRoot = angular.copy($scope.root);
      $scope.verifyTree();
      var contents = localStorage.KNLwp;
      $rootScope.postMessageToParentFrame("wizardSave", contents);
    };

    $scope.verifyTreeHelper = function() {
      if($scope.verifyTimer){
        $timeout.cancel($scope.verifyTimer);
      }
      $scope.verifyTimer = $timeout($scope.verifyTree, 1000);
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
                o1.modules = concatenateUUIDs(o1.modules);
                o2.modules = concatenateUUIDs(o2.modules);
              } else if(o1.itemType === 'module'){
                o1.lectures = concatenateUUIDs(o1.lectures);
                o2.lectures = concatenateUUIDs(o2.lectures);
                o1.count = o2.count = 0;
              } else if(o1.itemType === 'lecture'){
                o1.count = o2.count = o1.totalLecturesCount = o2.totalLecturesCount = 0;
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
        $scope.hasVimeoLectures = false;
        $scope.hasYoutubeLectures = false;
        $scope.hasVideoLectures = false;
        $scope.hasFinalExamLecture = false;
        var tCount = 0, totalLecturesCount = 0;
        $scope.root.uuid = $scope.root.uuid || $rootScope.uuid();
        //ensure backwards compatibility
        $scope.root.modules = $scope.root.modules || $scope.root.topics;
        delete $scope.root.topics;
        angular.forEach($scope.root.modules, function(module){
          module.uuid = module.uuid || $rootScope.uuid();
          //ensure backwards compatibility
          module.itemType = "module";
          $scope.lastModuleUUID = module.uuid;
          module.parentUUID = $scope.root.uuid;
          module.count = tCount++;
          var sCount = 0;
          //ensure backwards compatibility
          module.lectures = module.lectures || module.slides; delete module.slides;
          angular.forEach(module.lectures, function(lecture){
            lecture.uuid = lecture.uuid || $rootScope.uuid();
            //ensure backwards compatibility
            lecture.itemType = "lecture";
            $scope.lastLectureUUID = lecture.uuid;
            lecture.parentUUID = module.uuid;
            lecture.count = sCount++;
            lecture.totalLecturesCount = totalLecturesCount++;
            if(lecture.type === 'vimeo'){
              $scope.hasVimeoLectures = true;
            }
            if(lecture.type === 'video'){
              $scope.hasVideoLectures = true;
            }
            if(lecture.type === 'youtube'){
              $scope.hasYoutubeLectures = true;
            }
            if(lecture.type === 'bubble'){
              $scope.lastBubbleColor2 = (lecture.color2 && lecture.color2.length) ? lecture.color2 : $scope.lastBubbleColor2;
            }
            if(lecture.type === 'finalExam'){
              $scope.hasFinalExamLecture = true;
            }
            verifySavedStatus(lecture);
          });
          verifySavedStatus(module);
        });
        verifySavedStatus($scope.root);
        localStorage.KNLwp = Base64.encode(encodeURI(JSON.stringify($scope.root)));
    };

    $scope.getNodeByUUID = function(uuid, getFromSavedRoot){
      var found;
      var tree = getFromSavedRoot ? $scope.savedRoot : $scope.root;
      if(tree.uuid === uuid) found = tree;
      angular.forEach(tree.modules, function(module){
        if(module.uuid === uuid) found = module;
        angular.forEach(module.lectures, function(lecture){
          if(lecture.uuid === uuid) found = lecture;      
        });
      });
      return found;
    };

    $scope.getLectureNameByType = function(type){
      for(var i = 0; i < LECTURE_TYPES.length; i++){
        if(LECTURE_TYPES[i].type == type){
          return LECTURE_TYPES[i].name;
        }
      }
    };

    $scope.newLecture = function(scope){
      var nodeData = scope.$modelValue;
      var modalInstance = $uibModal.open({
        animation: true,
        component: 'addLectureModal',
        resolve: {
          hasFinalExamLecture: function() {
            return $scope.hasFinalExamLecture
          }
        }
      });

      modalInstance.result.then(
        function (selectedType) {
          var lectureName = 'Aula ' + (nodeData.count + 1) + '.' + (nodeData.lectures.length + 1) + ': ' + $scope.getLectureNameByType(selectedType);
          var lecture = {
            itemType: 'lecture',
            title: lectureName,
            type: selectedType,
            uuid: $rootScope.uuid()
          }
          if(lecture.type == 'bubble') {
            lecture.text = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.';
            lecture.id = 'goncaloprado.png';
            lecture.text2 = 'Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.';
            lecture.id2 = 'goncaloprado.png';
            lecture.color2 = $scope.lastBubbleColor2 || '3D3D40';
          } else if(lecture.type == 'youtube') {
            lecture.id = 's1mczCFXcSY';
          } else if(lecture.type == 'vimeo') {
            lecture.id = '242792908';
          } else if(lecture.type == 'video') {

          } else if(lecture.type == 'question') {
            lecture.text = $scope.blankQuestion.text;;
            lecture.isMultiple = false;
            lecture.shuffleQuestions = false;
            lecture.options = angular.copy($scope.blankQuestion.options);
          } else if(lecture.type == 'finalExam') {
            lecture.questions = [angular.copy($scope.blankQuestion), angular.copy($scope.blankQuestion), angular.copy($scope.blankQuestion)];
          } else if(lecture.type == 'text') {
            lecture.text = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.';
            lecture.showWell = true;
          } else if(lecture.type == 'image') {
            lecture.id = 'https://static.pexels.com/photos/355988/pexels-photo-355988.jpeg';
          }          
          nodeData.lectures.push(lecture);
          $scope.goToNode(lecture.uuid);
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
        modules: [],
        uuid: $rootScope.uuid()
      };
      $scope.newModule();
    }

    $scope.newModule = function(goToModule){
      var newModule = {
        itemType: 'module',
        title: 'Módulo ' + ($scope.root.modules.length + 1),
        uuid: $rootScope.uuid(),
        lectures: []
      };
      $scope.root.modules.push(newModule);
      if(goToModule){
        $scope.goToNode(newModule.uuid);
      }
    };

    $scope.edit = function (scope) {
      $rootScope.selectedNode = scope.$modelValue;
      $scope.selectedNodeSaved = $scope.getNodeByUUID($scope.selectedNode.uuid, true);
      $scope.selectedNodeScope = scope;
      $rootScope.selectedTab = 'edit';
      $scope.previewURL = $sce.trustAsResourceUrl(
        "knlClassroom/index.html#!/lecture" + 
        "?preview=1" + 
        "&uuid=" + $scope.selectedNode.uuid);
      var editPanel = $('#editPanel').get(0)
      editPanel && editPanel.scrollIntoView();
    };

    $scope.remove = function () {
      if($scope.selectedNodeScope){
        var node = $scope.selectedNode,
            parentUUID = node.parentUUID,
            parentArrayAttribute = node.itemType + 's', //modules or lectures
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

    $scope.isMultipleClicked = function(lecture){
      if(!lecture.isMultiple){
        angular.forEach(lecture.options, function(option, i){
          if(option.expected){
            $scope.optionClicked(lecture, option, i);
          } else if (i === (lecture.options.length - 1)) {
            $scope.optionClicked(lecture, lecture.options[0], 0);
          } else {
            option.expected = false;
          }
        });
      }
    };

    $scope.optionClicked = function(lecture, option, index){
      if(!lecture.isMultiple && option.expected){
        angular.forEach(lecture.options, function(o, i){
          if(i != index){
            o.expected = false;
          }
        });
      } else {
        lecture.options[index].expected = false;
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
      if($scope.selectedNode.id.indexOf('vimeo.com/') >= 0){
        $rootScope.sendKornellNotification("success", "O ID do vídeo foi extraído da URL com sucesso");
        $scope.selectedNode.id = $scope.selectedNode.id.split('vimeo.com/')[1];
      }
    };

    $scope.trimYouTubeURL = function(){
      var stripIdFromVideoURL = function(url){
        var r, rx = /^.*(?:(?:youtu\.be\/|v\/|vi\/|u\/\w\/|embed\/)|(?:(?:watch)?\?v(?:i)?=|\&v(?:i)?=))([^#\&\?]*).*/;
        try {
          var url =  url.match(rx)[1];
          $rootScope.sendKornellNotification("success", "O ID do vídeo foi extraído da URL com sucesso");
          return url;
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

//$scope.root = {"title":"KNL","availableVideoSizes":"144,360,720","colorBackground":"EBEBEB","colorFont":"0284B5","colorTheme":"0284B5","colorTitle":"EBEBEB","itemType":"root","paddingTopIframe":56,"modules":[{"itemType":"module","title":"Módulo 1","uuid":"65370d61-67c0-4a78-86bc-2f1c5730b5e5","lectures":[{"itemType":"lecture","title":"Lecture 1.1: Imagem","type":"image","uuid":"ca10d6da-1952-4830-9f9a-2c34a3485146","id":"https://static.pexels.com/photos/355988/pexels-photo-355988.jpeg","$$hashKey":"object:57","parentUUID":"65370d61-67c0-4a78-86bc-2f1c5730b5e5","count":0,"totalLecturesCount":0}],"parentUUID":"4849e1fc-ea88-4247-be80-155052f0fb0e","count":0,"$$hashKey":"object:19"}],"uuid":"4849e1fc-ea88-4247-be80-155052f0fb0e","$$hashKey":"object:13"};
//$scope.initWizard();
  }
]);


app.component('confirmModal', {
  templateUrl: 'confirmModal.html',
  bindings: {
    resolve: '<',
    close: '&',
    dismiss: '&'
  },
  controller: [
    function () {
      var $ctrl = this;

      $ctrl.$onInit = function () {
      };

      $ctrl.ok = function () {
          $ctrl.close({$value: 'ok'});
      };

      $ctrl.cancel = function () {
          $ctrl.dismiss({$value: 'cancel'});
      };
    }
  ]
});


app.component('addLectureModal', {
  templateUrl: 'addLectureModal.html',
  bindings: {
    resolve: '<',
    close: '&',
    dismiss: '&'
  },
  controller: [
    'LECTURE_TYPES',
    function (LECTURE_TYPES) {
      var $ctrl = this;

      $ctrl.$onInit = function () {
          $ctrl.LECTURE_TYPES = angular.copy(LECTURE_TYPES);
          if($ctrl.resolve.hasFinalExamLecture){
            $ctrl.LECTURE_TYPES.splice($ctrl.LECTURE_TYPES.indexOf("finalExam"),1);
          }
      };

      $ctrl.ok = function () {
        if($ctrl.lectureType && $ctrl.lectureType !== 'lectureTypeSeparator'){
          $ctrl.close({$value: $ctrl.lectureType});
        }
      };

      $ctrl.cancel = function () {
        $ctrl.dismiss({$value: 'cancel'});
      };
    }
  ]
});


app.controller('FileController', [
  '$scope',
  '$rootScope',
  'FileUploader',
  function($scope, $rootScope, FileUploader) {
    
    var uploader = $scope.uploader = new FileUploader();

    uploader.onAfterAddingFile = function(fileItem) {
      fileItem.disableMultipart = true;
      
      //TODO 'mpg, mpeg, mp4, mov, mkv'
      var allowedExtensions = $rootScope.selectedNode.type === 'video' ? 'mp4' : 'jpg, jpeg, gif, png',
        fileName = fileItem.file && fileItem.file.name,
        fileParts = fileName && fileName.split('.'),
        fileExtension = fileParts && fileParts.length > 1 && fileParts[fileParts.length - 1],
        isAllowedExtension = fileExtension && allowedExtensions.indexOf(fileExtension) >= 0;

      if(isAllowedExtension){
        if(uploader.queue.length > 1){
          uploader.queue.splice(0, 1);
        }
        uploader.requestUploadPath = false;
        fileItem.file.name = $rootScope.uuid() + '.' + fileExtension;
        $rootScope.postMessageToParentFrame("requestUploadPath", fileItem.file.name);
      } else {
        $rootScope.sendKornellNotification("error", "Extensão do arquivo inválida. Extensões aceitas: " + allowedExtensions + '.');
        uploader.queue = [];
      }
    };

    uploader.onBeforeUploadItem = function(item) {
      if(!uploader.requestUploadPath) {
        $rootScope.sendKornellNotification("error", "Erro ao tentar fazer o upload do arquivo. Tente novamente mais tarde ou entre em contato com o suporte.");
        return;
      }
      if(uploader.queue.length == 1){
        uploader.queue[0].url = uploader.requestUploadPath;
        uploader.queue[0].uploader.url = uploader.requestUploadPath;
        uploader.queue[0].method = 'PUT';
        uploader.queue[0].fullURL = uploader.requestUploadPath.split('.s3.amazonaws.com')[1].split('?AWS')[0];
        //item.removeAfterUpload = true;
      }
    };

    uploader.onSuccessItem = function(fileItem, response, status, headers) {
      $rootScope.sendKornellNotification("success", "Upload concluído com sucesso.");
      $scope.modelAttribute = $scope.modelAttribute || 'id';
      $rootScope.selectedNode[$scope.modelAttribute] = fileItem.fullURL;
      uploader.queue = [];
    };

    uploader.onErrorItem = function(fileItem, response, status, headers) {
      $rootScope.sendKornellNotification("error", "Erro ao tentar fazer o upload do arquivo. Tente novamente mais tarde ou entre em contato com o suporte.");
    };

    window.addEventListener('message',function(event) {
      if(event.data.type === 'responseUploadPath') {
          uploader.requestUploadPath = event.data.message;
      }
    },false);
  }
]);

