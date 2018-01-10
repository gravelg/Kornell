'use strict';

var app = angular.module('knl');

app.constant('SLIDE_TYPES', [
  {
    type: "text",
    name: "Texto"
  },
  {
    type: "bubble",
    name: "Balões"
  },
  {
    type: "image",
    name: "Imagem"
  },
  {
    type: "slideTypeSeparator",
    name: "-----------------------------------------"
  },
  {
    type: "video",
    name: "Vídeo"
  },
  {
    type: "youtube",
    name: "YouTube"
  },
  {
    type: "vimeo",
    name: "Vimeo"
  },
  {
    type: "slideTypeSeparator",
    name: "-----------------------------------------"
  },
  {
    type: "question",
    name: "Exercício de Aprendizado"
  },
  {
    type: "finalExam",
    name: "Avaliação Final"
  }
]);

