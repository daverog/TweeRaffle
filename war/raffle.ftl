<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>TweeRaffle</title>
<script type="text/javascript" src="jquery-1.4.3.min.js"></script>
<script type="text/javascript" src="tweeraffle.js"></script>
<link rel="stylesheet" href="tweeraffle.css" />
</head>
<body>
<h1></h1>
<div id="data" style="display:none;">
	<div id="raffleId">${raffle.id}</div>
	<div id="hashtag">${raffle.hashtag}</div>
	<div id="open">${raffle.open?string("dd MMM yyyy hh:mm:ss zzz")}</div>
</div>
<div id="fill">Fill the raffle hat!</div>
<div id="pick" style="display: none">Pick a winner!</div>
<div id="hatStatus" class="no-entrants">The hat is empty</div>
<div id="status">
	<span id="numberOfTweets"/>
	<span id="numberOfEntrants"/>
	<span id="numberOfWinners"/>
	<span id="numberOfLosers"/>
</div>
<div id="entrants"/>
<div id="winners"/>
<div id="losers"/>
</body>
</html>