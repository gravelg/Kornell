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
  'SLIDE_TYPES',
	function($scope, $rootScope, $timeout, $window, $sce, $location, $uibModal, toaster, SLIDE_TYPES) {

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

      $scope.data = [$scope.root];

      $scope.selectedNode = $scope.root;
      $timeout(function(){
        $(".knlEditSlideButton")[$(".knlEditSlideButton").length - 1].click();
      });

      $scope.calculateLimitToDots();

      $scope.verifyTree();
      $scope.savedRoot = angular.copy($scope.root);

      $scope.$watch('root', function() {
        $scope.verifyTreeHelper();
      }, true);

    };

    $scope.saveTree = function() {
      $scope.savedRoot = angular.copy($scope.root);
      $scope.verifyTree();
      toaster.pop("success", "Sucesso", "As alterações foram salvas com sucesso.");
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
              console.log(o1);
              console.log(o2);
            } else {
              delete node.isUnsaved;
            }
          }
        };

        $scope.treeIsUnsaved = false;
        $scope.hasVimeoSlides = false;
        $scope.hasYoutubeSlides = false;
        $scope.hasVideoSlides = false;
        var tCount = 0, totalSlidesCount = 0;
        $scope.root.uuid = $scope.root.uuid || $rootScope.uuid();
        angular.forEach($scope.root.topics, function(topic){
          topic.uuid = topic.uuid || $rootScope.uuid();
          topic.parentUUID = $scope.root.uuid;
          topic.count = tCount++;
          var sCount = 0;
          angular.forEach(topic.slides, function(slide){
            slide.uuid = slide.uuid || $rootScope.uuid();
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
        resolve: {}
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
            slide.text = 'At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium?';
            slide.isMultiple = false;
            slide.shuffleQuestions = false;
            slide.options = [
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
            ];
          } else if(slide.type == 'finalExam') {

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
      $scope.selectedNode.questions.push(
        {
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
        }
      );
      $scope.selectQuestion($scope.selectedNode.questions.length - 1);
    };

    $scope.removeQuestion = function(index) {
      $scope.selectedNode.questions.splice(index, 1);
      $scope.selectQuestion(0);
    };

    $scope.newOption = function(selectedQuestion) {
      selectedQuestion.options.push({text: 'Quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet'});
    };

    $scope.newTopic = function(scope){
      var nodeData = scope.$modelValue;
      var newTopic = {
        itemType: 'topic',
        title: 'Tópico ' + (nodeData.topics.length + 1),
        uuid: $rootScope.uuid(),
        slides: []
      };
      nodeData.topics.push(newTopic);
      $scope.goToNode(newTopic.uuid);
    };

    $scope.edit = function (scope) {
      $scope.selectedNode = scope.$modelValue;
      $scope.selectedNodeSaved = $scope.getNodeByUUID($scope.selectedNode.uuid, true);
      $scope.selectedNodeScope = scope;
      $rootScope.selectedTab = 'edit';
      $scope.previewURL = $sce.trustAsResourceUrl(
        "knlClassroom/#!/slide" + 
        "?preview=1" + 
        "&uuid=" + $scope.selectedNode.uuid +
        "&classroomPath=/../knl/classroom/index.html");
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
        $('#nodeEdit_'+uuid)[0].click();
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

    $scope.root = 
      {
        "itemType": "root",
        "title": "Compliance - Visão Geral",
        "expectedGrade": 60,
        "availableVideoSizes": '144,360,720',
        "colorBackground": "EBEBEB",
        "colorTheme": "0284B5",
        "colorFont": "0284B5",
        "colorTitle": "EBEBEB",
        "paddingTopIframe": 56,
        "topics": [
            {
                "itemType": "topic",
                "title": "Videoaulas",
                "slides": [
                    {
                        "itemType": "slide",
                        "type": "vimeo",
                        "title": "Módulo 1 e 2",
                        "id": "215842568",
                        "text": ""
                    },
                    {
                        "itemType": "slide",
                        "type": "vimeo",
                        "title": "Módulo 3",
                        "id": "238795928",
                        "text": ""
                    },
                    {
                        "itemType": "slide",
                        "type": "vimeo",
                        "title": "Módulo 4",
                        "id": "238796739",
                        "text": ""
                    },
                    {
                        "itemType": "slide",
                        "type": "vimeo",
                        "title": "Módulo 5",
                        "id": "215055385",
                        "text": ""
                    },
                    {
                        "itemType": "slide",
                        "type": "vimeo",
                        "title": "Módulo 6",
                        "id": "214833601",
                        "text": ""
                    },
                    {
                        "itemType": "slide",
                        "type": "vimeo",
                        "title": "Módulo 7",
                        "id": "238797285",
                        "text": ""
                    },
                    {
                        "itemType": "slide",
                        "type": "vimeo",
                        "title": "Módulo 8",
                        "id": "215057711",
                        "text": ""
                    }
                ]
            },
            {
                "itemType": "topic",
                "title": "Encerramento",
                "slides": [
                    {
                        "itemType": "slide",
                        "type": "bubble",
                        "title": "Informações Relevantes",
                        "id": "goncaloprado.png",
                        "text": "<p>Espero que tenha tido bastante atenção aos conceitos de cada vídeo. <br> Gostaria de lembrá-los que temos material complementar na Biblioteca (em <b>DETALHES</b>, no menu abaixo). <br>As informações são importantes para um bom desempenho na prova final, que será composta de 20 questões e exige 60% de acertos para habilitar a geração do certificado de conclusão.</p>",
                        "text2": "",
                        "id2": "",
                        "color2": ""
                    },
                    {
                        "itemType": "slide",
                        "type": "bubble",
                        "title": "Até a próxima",
                        "id": "goncaloprado.png",
                        "text": "<p>Parabéns pela conclusão do curso <b>Compliance - Visão Geral</b>. Foram intensas e produtivas horas de aula, espero muito que tenha aproveitado ao máximo! <br>O seu certificado já está disponível! <br>Clique em \"DETALHES\" no menu abaixo, e depois em \"Certificação\". <br>Nos vemos em breve em outros cursos da <b>Lanlink</b>.<br>Um abraço e muito obrigado!</p>",
                        "text2": "",
                        "id2": "",
                        "color2": ""
                    },
                    {
                      "itemType": "slide",
                      "type": "text",
                      "title": "Texto teste",
                      "text": "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>",
                      "showWell": true
                    },
                    {
                      "itemType": "slide",
                      "type": "image",
                      "title": "Imagem teste",
                      "text": "<p>At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium</p>",
                      "id": "https://static.pexels.com/photos/355988/pexels-photo-355988.jpeg"
                    },
                    {
                        "itemType": "slide",
                        "type": "youtube",
                        "title": "Teste YouTube",
                        "id": "ScMzIvxBSi4",
                        "text": ""
                    },
                    {
                        "itemType": "slide",
                        "type": "vimeo",
                        "title": "Módulo 1 e 2",
                        "id": "215842568",
                        "text": ""
                    },
                    {
                      "itemType": "slide",
                      "type": "question",
                      "title": "Questão de aprendizado teste",
                      "count": 10,
                      "text": "<p>At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium?</p>",
                      "isMultiple": false,
                      "shuffleQuestions": false,
                      "options": [
                        {
                          "text": "<p>Voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati</p>",
                          "expected": true
                        },
                        {
                          "text": "<p>Cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi</p>",
                          "expected": false
                        },
                        {
                          "text": "<p>Et harum quidem rerum facilis est et expedita distinctio</p>",
                          "expected": false
                        },
                        {
                          "text": "<p>Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil</p>",
                          "expected": false
                        }
                      ]
                    },
                    {
                      "itemType": "slide",
                      "type": "finalExam",
                      "title": "Prova Final",
                      "text": "",
                      "isMultiple": false,
                      "shuffleQuestions": true,
                      "questions": [
                        {
                            "text": "<p>Assinale a alternativa que corresponde ao significado da expressão Compliance:</p>",
                            "options":[
                                {
                                    "text": "<p>É oferecer produtos e serviços em conformidade com os mais altos padrões de qualidade do mercado;</p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>É o Sistema pelo qual as organizações são gerenciadas e monitoradas;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>É definir metas voltadas à sustentabilidade e ao aumento do valor econômico da organização;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>É agir de acordo com os valores, as políticas internas e externas, as leis, e as normas e procedimentos;</p>",
                                    "expected": true
                                },
                                {
                                    "text": "<p>É a gestão estratégica focada no aumento da eficiência e da lucratividade para a sustentabilidade da organização.</p>",
                                    "expected": false
                                }
                            ]
                        },
                        {
                            "text": "<p>O Sistema de Gestão de Compliance do Grupo Lanlink abrange:</p>",
                            "options":[
                                {
                                    "text": "<p>Lanlink Informática, Lanlink Serviços e Lanlink Soluções;</p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>Lanlink Informática, Lanlink Serviços, Lanlink Soluções e Trust Control;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Lanlink Informática, Lanlink Serviços, Lanlink Soluções e Parceiros Comerciais;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Lanlink Informática, Lanlink Serviços, Lanlink Soluções, Trust Control e Tasking;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Lanlink Informática, Lanlink Serviços, Lanlink Soluções, Trust Control, Tasking e Parceiros Comerciais;</p>",
                                    "expected": true
                                }
                            ]
                        },
                        {
                            "text": "<p>Assinale dentre as alternativas abaixo, aquela que apresenta a(s) maior(es) diferença(s) existente(s) entre os contextos vigentes nos escândalos do MENSALÃO e do PETROLÃO:</p>",
                            "options":[
                                {
                                    "text": "<p>Em ambos já existia a Lei Anticorrupção, com reflexos na responsabilização das empresas, a principal diferença, no entanto, é que enquanto no MENSALÃO a investigação não deu em nada, no PETROLÃO, a OPERAÇÃO LAVA JATO está indo fundo na apuração dos fatos, já tendo condenado diversas empresas, empresários e políticos, e dando uma grande contribuição para a mudança na cultura dos negócios;</p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>Em ambos já existia a Lei Anticorrupção com reflexos na responsabilização das empresas, a principal diferença, no entanto, é que enquanto no MENSALÃO os órgãos investigativos não tinham autonomia, no PETROLÃO, os mesmos contam com amplo respaldo e independência, o que explica o sucesso que vem fazendo a OPERAÇÃO LAVA JATO, que está mudando a cultura dos negócios;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Em ambos já existia regras de combate à corrupção, a principal diferença, no entanto, é que no MENSALÃO não existia uma Lei Anticorrupção que responsabilizasse as empresas; já no PETROLÃO, existe, respaldando investigações e levando à condenação de empresas, empresários e políticos na OPERAÇÃO LAVA JATO, que tem contribuído para a mudança da cultura dos negócios;</p>",
                                    "expected": true
                                },
                                {
                                    "text": "<p>Em ambos já existia regras de combate à corrupção, a principal diferença, no entanto, é que ao contrário do MENSALÃO, atualmente, quando a OPERAÇÃO LAVA JATO está investigando o escândalo do PETROLÃO, existem normas técnicas como a ISO 19.600, a ISO 37.001 e a DSC 10.000 que são base para Sistemas de Gestão de Compliance eficazes que mudam a cultura dos negócios;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Em ambos já existia regras de combate à corrupção, a principal diferença, no entanto, é que ao contrário do MENSALÃO, atualmente, quando a OPERAÇÃO LAVA JATO está investigando o escândalo do PETROLÃO, O Supremo Tribunal Federal concedeu poderes extraordinários à Justiça Federal do Paraná, que tem agido à margem da lei, condenando injustamente empresas, empresários e políticos;</p>",
                                    "expected": false
                                }
                            ]
                        }/*,
                        {
                            "text": "<p>Os valores organizacionais do Grupo Lanlink são:</p>",
                            "options":[
                                {
                                    "text": "<p>Compromisso, desenvolvimento, clima agradável, integridade, espírito de equipe, qualidade;</p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>Agilidade, clima agradável, respeito, espírito de equipe, compromisso, integridade;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Clima agradável, lucratividade, espírito de equipe, compromisso, integridade, respeito;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Integridade, respeito, compromisso, desenvolvimento, espírito de equipe, clima agradável;</p>",
                                    "expected": true
                                },
                                {
                                    "text": "<p>Respeito, espírito de equipe, integridade, clima agradável, empreendedorismo, desenvolvimento;</p>",
                                    "expected": false
                                }
                            ]
                        },
                        {
                            "text": "<p>Assinale o item que representa a Política de Compliance do Grupo Lanlink</p>",
                            "options":[
                                {
                                    "text": "<p>Garantir a Satisfação dos Clientes com a qualidade das nossas soluções, da melhoria contínua dos processos e da qualificação de colaboradores e parceiros comerciais.</p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>Vivenciar nossos valores, agindo com integridade;</p>",
                                    "expected": true
                                },
                                {
                                    "text": "<p>Ser ágil e priorizar a concretização de negócios com a flexibidade que se mostrar necessária;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Prezar pela ética, cientes de que o lucro está em primeiro lugar;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>A satisfação dos clientes é a prioridade do nosso negócio;</p>",
                                    "expected": false
                                }
                            ]
                        },
                        {
                            "text": "<p>Sobre <i>Due Diligence</i> é correto afirmar que:</p>",
                            "options":[
                                {
                                    "text": "<p>É o processo de análise prévia da reputação e posterior monitoramento das práticas de um terceiro que torna-se parceiro comercial e atua em nome do Grupo Lanlink;</p>",
                                    "expected": true
                                },
                                {   
                                    "text": "<p>É o processo voltado à análise da eficiência do processo de vendas e satisfação dos clientes com o atendimento de terceiro que faz negócios em nome do Grupo Lanlink;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>É o acompanhamento dos resultados comerciais de um terceiro credenciado como parceiro do Grupo Lanlink, objetivando garantir o cumprimento das metas;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>É a formalização de contrato de parceria, treinamento e comunicação dos padrões de Compliance do Grupo Lanlink a um terceiro credenciado para atuar em seu nome;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>É o processo de elaboração das políticas de Compliance, aprovação pela Alta Direção e análises críticas do desempenho do Sistema de Gestão de Compliance;</p>",
                                    "expected": false
                                }
                            ]
                        },
                        {
                            "text": "<p>Assinale a alternativa INCORRETA sobre os requisitos do Acordo de Leniência:</p>",
                            "options":[
                                {
                                    "text": "<p>A pessoa jurídica deve interromper a prática lesiva até a data de assinatura do acordo;</p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>A pessoa jurídica deve fornecer comprovações (informações, documentos) do ilícito;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>A pessoa jurídica deve optar por admitir ou não a culpa nos atos lesivos à Administração;</p>",
                                    "expected": true
                                },
                                {
                                    "text": "<p>A pessoa jurídica deve comprometer-se a cooperar plenamente com a investigação;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>A pessoa jurídica deve implantar ou revisar o seu Programa de Compliance;</p>",
                                    "expected": false
                                }
                            ]
                        },
                        {
                            "text": "<p>O elemento mais importante de um Sistema de Gestão de Compliance é:</p>",
                            "options":[
                                {
                                    "text": "<p>O Comprometimento da Alta Direção;</p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>O Treinamento dos colaboradores nas políticas de Compliance;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>A comunicação dos padrões de comportamento esperados aos colaboradores e terceiros;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>A existência de um Código de Conduta e Procedimentos complementares;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>A conduta das pessoas do Grupo Lanlink e seus parceiros comerciais;</p>",
                                    "expected": true
                                }
                            ]
                        },
                        {
                            "text": "<p>Assinale a alternativa em que nenhuma das condutas é admitida a colaboradores ou parceiros comerciais do Grupo Lanlink:</p>",
                            "options":[
                                {
                                    "text": "<p>Corrupção, Fazer Doação, Pagamento de Facilitação, Conflito de Interesses, Suborno;</p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>Corrupção, Pagamento de Facilitação, Denunciar Violação, Conflito de Interesses, Suborno;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Corrupção, Conflito de Interesses, Presentes, Suborno, Pagamento de Facilitação;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Corrupção, Suborno, Conflito de Interesses, Pagamento de Facilitação, Propina;</p>",
                                    "expected": true
                                },
                                {
                                    "text": "<p>Corrupção, Conceder Patrocínio, Conflito de Interesses, Suborno, Pagamento de Facilitação;</p>",
                                    "expected": false
                                }
                            ]
                        },
                        {
                            "text": "<p>Os pilares de um Sistema de Integridade ou Compliance são:</p>",
                            "options":[
                                {
                                    "text": "<p>Comprometimento da Direção, Instância Responsável, Canal de Compliance, Regras e Instrumentos e Monitoramento;</p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>Comprometimento da Direção, Regras e Instrumentos, Análise Crítica, Instância Responsável e Monitoramento;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Comprometimento da Direção, Análise de Riscos, Regras e Instrumentos, Instância Responsável e Monitoramento;</p>",
                                    "expected": true
                                },
                                {
                                    "text": "<p>Comprometimento da Direção, Instância Responsável, Auditorias Internas, Regras e Instrumentos e Monitoramento;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Comprometimento da Direção, Treinamentos Internos, Regras e Instrumentos, Instância Responsável e Monitoramento;</p>",
                                    "expected": false
                                }
                            ]
                        },
                        {
                            "text": "<p>De que maneiras o Canal de Compliance do Grupo Lanlink pode ser acessado?</p>",
                            "options":[
                                {
                                    "text": "<p>Central de Atendimento, Website Corporativo, PABX da Lanlink, Atendimento Pessoal da Liderança ou Time de Compliance, Caixa Postal; </p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>Central de Atendimento, Whatsapp, E-mail, Website Corporativo, Intranet, Atendimento Pessoal da Liderança ou Time de Compliance, Caixa Postal;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Central de Atendimento, E-mail, Website Corporativo, Intranet, Atendimento Pessoal da Liderança ou Time de Compliance, Caixa Postal;</p>",
                                    "expected": true
                                },
                                {
                                    "text": "<p>Central de Atendimento, Chamado Via SDM, Website Corporativo, Intranet, Atendimento Pessoal da Liderança ou Time de Compliance e Caixa Postal;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Central de Atendimento, Website Corporativo, Intranet, Sistema Sapiens, Atendimento Pessoal da Liderança ou Time de Compliance e Caixa Postal;</p>",
                                    "expected": false
                                }
                            ]
                        },
                        {
                            "text": "<p>Um Sistema de Gestão de Compliance deve ser desenvolvido para: - Comunicar o comprometimento da organização com a Integridade;- Identificar e mitigar riscos de violação de leis, regulamentos e políticas;- Prover mecanismo para prevenção e detecção de condutas impróprias;- Consolidar a cultura de que só se deve fazer o que é correto;– Prover referências de conduta para colaboradores e terceiros;<br>Considerando as afirmações anteriores, podemos afirmar que:</p>",
                            "options":[
                                {
                                    "text": "<p>Os itens 1, 3 e 5 estão corretos;</p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>Os itens 1, 3, 4 e 5 estão corretos;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Os itens 1, 2, 4 e 5 estão corretos;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Os itens 1, 2, 3, 4 e 5 estão corretos; </p>",
                                    "expected": true
                                },
                                {
                                    "text": "<p>Os itens 1, 2 e 5 estão corretos;</p>",
                                    "expected": false
                                }
                            ]
                        },
                        {
                            "text": "<p>Um procedimento licitatório que tinha por objeto a compra de produtos de tecnologia, foi anulado por fraude. Investigações internas do órgão licitante, identificaram que representantes das empresas ORBIS TECNOLOGIA (VENCEDORA) e LUXUS AUTOMAÇÃO COMERCIAL ofereceram propina a um membro da área técnica que julgou as propostas para que considerasse as suas documentações, que apresentavam falhas, em conformidade com os requisitos do Edital; na apuração, constatou-se que as empresas desconheciam os fatos. Diante desta situação, quem poderá ser responsabilizado?</p>",
                            "options":[
                                {
                                    "text": "<p>Somente as pessoas físicas poderão ser responsabilizadas pelos atos ilícitos praticados, uma vez que as pessoas jurídicas não tinham conhecimento destas práticas dos seus representantes; </p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>Somente o representante da licitante ORBIS TECNOLOGIA, que venceu o certame poderá ser responsabilizado, já que ao vencer a licitação com uma proposta em desacordo com o edital, caracterizou a lesão ao patrimônio público; </p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Os representantes das empresas ORBIS TECNOLOGIA e LUXUS AUTOMAÇÃO COMERCIAL poderão ser responsabilizados, pois, para caracterização da conduta, basta a tentativa de lesão ao patrimônio público; </p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Somente as empresas poderão ser responsabilizadas, pois, pelo princípio da responsabilidade objetiva da Lei Anticorrupção, as pessoas jurídicas não precisam ter conhecimento ou concordar com as práticas impróprias de quem as representa;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Os dois representantes e as duas empresas poderão ser punidos, pois, referente às pessoas físicas, basta a tentativa da vantagem indevida, e as pessoas jurídicas, são responsáveis pelos atos ilícitos dos seus representantes, mesmo que os desconheça;</p>",
                                    "expected": true
                                }
                            ]
                        },
                        {
                            "text": "<p>Quem pode acessar o Canal de Compliance da Lanlink?</p>",
                            "options":[
                                {
                                    "text": "<p>Todos os colaboradores da Lanlink;</p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>Todos os colaboradores do Grupo Lanlink e de Terceiros que atuem em seu nome;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Todos os clientes do Grupo Lanlink;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Todos os stakeholders do Grupo Lanlink;</p>",
                                    "expected": true
                                },
                                {
                                    "text": "<p>Todos os colaboradores do Grupo Lanlink;</p>",
                                    "expected": false
                                }
                            ]
                        },
                        {
                            "text": "<p>Assinale a alternativa em que nenhuma das condutas é admitida a colaboradores e parceiros comerciais do Grupo Lanlink:</p>",
                            "options":[
                                {
                                    "text": "<p>Lavagem de Dinheiro, Fraude, Uso de Laranjas, Cartel ou Conluio, Superfaturamento;</p>",
                                    "expected": true
                                },
                                {   
                                    "text": "<p>Lavagem de Dinheiro, Cartel ou Conluio, Hospitalidades, Fraude, Superfaturamento;</p>",
                                },
                                {
                                    "text": "<p>Lavagem de Dinheiro, Uso de Laranjas, Presentes, Cartel ou Conluio, Superfaturamento;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Lavagem de Dinheiro, Cartel ou Conluio, Refeições, Uso de Laranjas, Superfaturamento; </p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Lavagem de Dinheiro, Denunciar, Cartel ou Conluio, Uso de Laranjas, Superfaturamento;</p>",
                                    "expected": false
                                }
                            ]
                        },
                        {
                            "text": "<p>A existência de código de conduta, políticas e procedimentos é essencial para a efetividade de um Sistema de Gestão de Compliance (SGC); sobre essas ferramentas, é INCORRETO afirmar que:</p>",
                            "options":[
                                {
                                    "text": "<p>Comunicam os padrões de conduta esperados dos colaboradores e terceiros;</p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>São subsídios para a tomada de decisões que possam representar riscos;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>O Código de Conduta Ética e Compliance é o principal instrumento do SGC;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>São aplicáveis a todos os colaboradores e terceiros que atuam em nome do Grupo Lanlink;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>São documentos recomendáveis, mas não necessariamente exigíveis em um SGC.</p>",
                                    "expected": true
                                }
                            ]
                        },
                        {
                            "text": "<p>Quando uma empresa se envolve em um escândalo de corrupção, quem sofre impactos em sua reputação?</p>",
                            "options":[
                                {
                                    "text": "<p>Apenas reputação da empresa é atingida;</p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>Apenas as reputações da empresa e pessoas envolvidas com o o caso são atingidas;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>As reputações da empresa, colaboradores e terceiros a ela relacionados são atingidas;</p>",
                                    "expected": true
                                },
                                {
                                    "text": "<p>Apenas a reputação das pessoas envolvidas é atingida;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Apenas a reputação dos gestores da empresa é atingida;</p>",
                                    "expected": false
                                }
                            ]
                        },
                        {
                            "text": "<p>Sobre a Lei 12.846/2013 (Lei Anticorrupção), podemos dizer:</p>",
                            "options":[
                                {
                                    "text": "<p>É uma Lei Federal, aplicável aos órgãos do Poder Executivo Federal, que pune o ato ilícito praticado contra Administração Pública brasileira, caracterizado pela promessa, concessão ou financiamento de uma vantagem indevida a agente público, ou fraude em procedimentos licitatórios; </p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>É uma Lei Federal, aplicável aos órgãos dos poderes Executivo, Legislativo e Judiciário federais, que pune o ato ilícito praticado contra a Administração Pública brasileira, caracterizado pela promessa, concessão ou financiamento de uma vantagem indevida a agente público, ou fraude em procedimentos licitatórios; </p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>É uma Lei Federal, aplicável aos órgãos dos três poderes, federais, estaduais ou municipais, que pune o ato ilícito praticado contra a Administração Pública brasileira ou estrangeira, caracterizado pela promessa, concessão ou financiamento de uma vantagem indevida a agente público ou fraude em procedimentos licitatórios; </p>",
                                    "expected": true
                                },
                                {
                                    "text": "<p>É uma Lei Federal, aplicável aos órgãos do Poder Executivo, federal, estadual ou municipal, que pune o ato ilícito praticado contra a Administração Pública brasileira ou estrangeira, caracterizado pela promessa, concessão ou financiamento de uma vantagem indevida a agente público ou fraude em procedimentos licitatórios; </p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>É uma Lei Federal, aplicável aos órgãos do Poder Executivo federais, estaduais ou municipais, que pune o ato ilícito praticado contra a Administração Pública brasileira, caracterizado pela promessa, concessão ou financiamento de uma vantagem indevida a agente público ou fraude em procedimentos licitatórios;</p>",
                                    "expected": false
                                }
                            ]
                        },
                        {
                            "text": "<p>O Canal de Compliance pode ser utilizado para: Pedir Orientações, Fazer Elogios, Manifestar Críticas, Sugerir Melhorias, Relatar Preocupações ou Denunciar Violações à lei ou políticas de Compliance do Grupo Lanlink, para que as pessoas possam utilizar o canal sem nenhum tipo de temor ou constrangimento, quais são os princípios a ele aplicáveis?</p>",
                            "options":[
                                {
                                    "text": "<p>Confidencialidade, Agilidade, Não Retaliação;</p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p>Confidencialidade, Identificação Facultativa, Não Retaliação;</p>",
                                    "expected": true
                                },
                                {
                                    "text": "<p>Confidencialidade, Objetividade, Não Retaliação;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Confidencialidade, Integridade, Punição;</p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p>Confidencialidade, Autoridade, Não Retaliação;</p>",
                                    "expected": false
                                }
                            ]
                        },
                        {
                            "text": "<p>Um Ministério do Governo Federal, em Edital para aquisição de licenças de software, exigia que o licitante apresentasse uma declaração atestando estar devidamente habilitado para o fornecimento do objeto da licitação. Ocorre que uma das licitantes, que não possuía o credenciamento do fabricante para esse fornecimento, apresentou uma declaração assinada por dois representantes legais e dois diretores afirmando de maneira falsa que estava devidamente habilitada para fornecer as licenças.<br>Após recurso de outra licitante, apontando o ato impróprio, o órgão realizou diligência junto ao fabricante que confirmou formalmente que a referida empresa não tinha o credenciamento necessário para atender ao objeto da licitação, acrescentando que a mesma tinha total ciência desta condição.<br>O Edital prevê punição para o licitante que apresentar declaração falsa e estabelece para esse tipo de conduta deve ser aberto processo para declaração de inidoneidade e outras responsabilizações.<br>A Lei 12.846/2013 (Lei Anticorrupcão) prevê em seu Art 5: Constituem atos lesivos à Administração Pública...<br>(...) IV - no tocante a licitações e contratos:<br>(...) <br>b)TODO impedir, perturbar ou fraudar a realização de qualquer ato de procedimento licitatório público;<br>e o Sistema de Compliance do fabricante atesta a exigência de que todos os seus parceiros cumpram a Lei 12.846/2013.<br><b>Diante desta situação, assinale a alternativa que em sua opinião é a mais adequada:  </b></p>",
                            "options":[
                                {
                                    "text": "<p><b>Sob o ponto de vista da Administração Pública:</b> Somente as pessoas que assinaram a declaração falsa poderão ser punidas caso a empresa da qual são representantes ganhe a licitação e não entregue o objeto. <b>Sob o ponto de vista do Sistema de Compliance do Fabricante:</b> O parceiro comercial somente poderá ser punido caso vença a licitação e não entregue o objeto;</p>",
                                    "expected": false
                                },
                                {   
                                    "text": "<p><b>Sob o ponto de vista da Administração Pública:</b> Diante do resultado da diligência, a empresa deverá ser desclassificada do processo licitatório e deverá ser aberto um processo de responsabilização tanto das pessoas físicas que praticaram o ato, quanto da pessoa jurídica. <b>Sob o ponto de vista do Sistema de Compliance do fabricante:</b> Tendo sido constatada a declaração falsa de um parceiro comercial sobre sua condição de fornecer o objeto do edital em desacordo com o próprio contrato de parceria, a lei de licitações brasileira, a Lei 12.846 e as políticas de Compliance aplicáveis a parceiros, a empresa deve ser descredenciada como parceira;</p>",
                                    "expected": true
                                },
                                {
                                    "text": "<p><b>Sob o ponto de vista da Administração Pública:</b> Diante do resultado da diligência, a empresa deverá ser desclassificada do processo licitatório e deverá ser aberto um processo de responsabilização tanto das pessoas físicas que praticaram o ato, quanto da pessoa jurídica. <b>Sob o ponto de vista do Sistema de Compliance do fabricante:</b> O parceiro comercial só será passível de punição se o governo brasileiro notificar formalmente ao fabricante que o mesmo cometeu atos ilícitos em um procedimento licitatório; </p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p><b>Sob o ponto de vista da Administração Pública:</b> Somente a pessoa jurídica poderá ser punida pela aplicação do princípio da responsabilidade objetiva, relativo à Lei Anticorrupção brasileira, caso vença o certame licitatório. <b>Sob o ponto de vista do Sistema de Compliance do Fabricante:</b> O parceiro comercial somente poderá ser punido caso vença a licitação e não entregue o objeto; </p>",
                                    "expected": false
                                },
                                {
                                    "text": "<p><b>Sob o ponto de vista da Administração Pública:</b> O pregoeiro poderá simplesmente desclassificar a empresa, encerrando o assunto, pois, assim o dano ao patrimônio público não chegará a ocorrer e não há que se falar em responsabilidades para a pessoa jurídica ou pessoas físicas. <b>Sob o ponto de vista do Sistema de Compliance do Fabricante:</b> Tendo sido constatada a declaração falsa do parceiro comercial sobre sua condição de fornecer o objeto do edital em desacordo com o próprio contrato de parceria, a empresa deve ser descredenciada como parceira.</p>",
                                    "expected": false
                                }
                            ]
                        }*/
                      ]
                    }
                ]
            }
        ]
    };

    $scope.initWizard();      
	}
]);


app.component('addSlideModal', {
  templateUrl: 'addSlideModal.html',
  bindings: {
    resolve: '<',
    close: '&',
    dismiss: '&'
  },
  controller: function (SLIDE_TYPES) {
    var $ctrl = this;
    $ctrl.SLIDE_TYPES = SLIDE_TYPES;

    $ctrl.$onInit = function () {
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
});