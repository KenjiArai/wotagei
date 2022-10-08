<!DOCTYPE html>

<html>
	<head>
		<meta charset="utf-8">
		<title>WotageiWebScreen</title>
	</head>
	<body>
		<style>
			body {
				background-color : #ffcc33;
				margin:0px;
				text-align:center;
				/* CSS中では長さの単位にdipを使えない */
			}
			
		</style>
		

		<script type="text/javascript" src="swfobject.js"></script>    
  		<div id="ytapiplayer">
    		You need Flash player 8+ and JavaScript enabled to view this video.
  		</div>

 		<script type="text/javascript">
 			var ytplayer;
			function onYouTubePlayerReady(playerId) {
				ytplayer = document.getElementById("myytplayer");
				ytplayer.addEventListener("onStateChange", "onytplayerStateChange");
			}
			
			function onytplayerStateChange(newState) {
				droid.onCatchPlayerStateChange(ytplayer.getPlayerState());
			}
			function getPlayerStateChange(newState, runnablesKey) {
			   droid.onCatchPlayerStateChange(newState, runnablesKey);
			}
			function getCurrentTime(runnablesKey) {
			   droid.onCatchCurrentTimeRequest(ytplayer.getCurrentTime(), runnablesKey);
			}
			
	    	function play() {
    			if (ytplayer) {
    				ytplayer.playVideo();
	    		}
	    	}
	    	function pause() {
	    		if (ytplayer) {
	    			ytplayer.pauseVideo();
	    		}
    		}
	    	function stop() {
    			if (ytplayer) {
    				ytplayer.stopVideo();
	    		}
    		}
			
    		var params = { allowScriptAccess: "always" };
    		var atts = { id: "myytplayer" };
			swfobject.embedSWF(	"http://www.youtube.com/v/<?php echo $_GET['tag']; ?>?enablejsapi=1&playerapiid=ytplayer", 
								"ytapiplayer", 
								"<?php echo $_GET['width']; ?>", 
								"<?php echo $_GET['height']; ?>", 
								"8", null, null, params, atts);
            
    	</script>
    </body>
</html>
		