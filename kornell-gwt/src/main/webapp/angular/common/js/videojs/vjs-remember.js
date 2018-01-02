(function(window, videojs) {
  'use strict';

  var plugin = function(slideNumber) {
    var player = this, isLoaded;

    player.on('timeupdate', time_updated);
    player.on('ended', time_updated);

    var KNL = "KNL";
    var KNL_CLASSROOM = "KNLc";

    function getPropertiesObj(key){
      var propertiesB64 = localStorage[key];
      var propertiesStr = propertiesB64 && Base64.decode(propertiesB64);
      var propertiesObj = propertiesStr && JSON.parse(propertiesStr);
      return propertiesObj || {};
    }

    function getProperty(propertyName, key){
      var propertiesObj = getPropertiesObj(key);
      return propertiesObj && propertiesObj[propertyName];
    }

    function getPositionsObj(){
      var positionsObj = getProperty(videoPositionKey, KNL_CLASSROOM) || {};
      return positionsObj;
    }

    function setPositions(positionsObj){
      var propertiesObj = getPropertiesObj(KNL_CLASSROOM);
      propertiesObj[videoPositionKey] = positionsObj;
      var propertiesStr = JSON.stringify(propertiesObj);
      var propertiesB64 = Base64.encode(propertiesStr);
      localStorage[KNL_CLASSROOM] = propertiesB64;
    }

    function getPosition(){
      return getPositionsObj()[slideNumber];
    }

    function setPosition(position){
      var positions = getPositionsObj();
      positions[slideNumber] = position;
      setPositions(positions);
    }

    function time_updated(time_update_event){
      var current_time = this.currentTime();
      var duration = this.duration();
      var time = Math.floor(current_time);

      if(time > duration || time_update_event.type === "ended") {
        time = 0;
      }

      if (isLoaded) {
        setPosition(time);
      }
    }    

    var knlSession = localStorage && getProperty('Kornell.v1.UserSession.CURRENT_SESSION', KNL);
    var knlClassroom = knlSession && getProperty('Kornell.v1.Classroom.'+knlSession+'.CURRENT_ENROLLMENT', KNL);
    var videoPositionKey = knlClassroom && 'Kornell.v1.Classroom.'+knlClassroom+'.videojs.position';

    player.ready(function() {
      var seekFunction = function() {
        if (isLoaded) return;
        isLoaded = true;
        var seek = getPosition();
        seek && player.currentTime(seek);
      };

      player.one('playing', seekFunction);
      player.one('play', seekFunction);
      player.one('loadedmetadata', seekFunction);
    });
  };

  // register the plugin
  videojs.plugin('remember', plugin);
})(window, window.videojs);