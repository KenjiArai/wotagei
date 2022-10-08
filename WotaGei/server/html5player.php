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
		<div id="player"></div>
		<script> 
			//Load player api asynchronously.
			var tag = document.createElement('script'); 
			tag.src = "http://www.youtube.com/player_api"; 
			var firstScriptTag = document.getElementsByTagName('script')[0]; 
			firstScriptTag.parentNode.insertBefore(tag, firstScriptTag); 
			var done = false; 
			var player;
			function onYouTubePlayerAPIReady() { 
				player = new YT.Player('player', {
					height: <?php echo "'".$_GET['height']."'"; ?>,
					width: <?php echo "'".$_GET['width']."'"; ?>, 
					videoId: <?php echo "'".$_GET['tag']."'"; ?>, 
					events: { 
						'onReady': onPlayerReady, 
						'onStateChange': onPlayerStateChange 
					} 
				});
			}
			function onPlayerReady(event) {
				droid.onCatchPlayerReady(player.getDuration());
			}
			function onPlayerStateChange(event) { 
				droid.onCatchPlayerStateChange(event.target.getPlayerState(), player.getCurrentTime());
			}
			function getCurrentTime(runnablesKey) {
				droid.onCatchCurrentTimeRequest(player.getCurrentTime(), runnablesKey);
			}
			function stop() {
				player.stopVideo();
			}
			function play() {
				player.playVideo();
			}
			function pause() {
				player.pauseVideo();
			}
		</script>
		<div id="bottom"></div>
	</body>
</html>				