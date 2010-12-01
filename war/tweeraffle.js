var MAX_TWEETS = 10;
var TWEETS_PER_PAGE = 5;
var STATE_WINNER = 'winner';
var STATE_LOSER = 'loser';
var STATE_HAT = 'hat';

var entrants = new Array();
var tweets = new Object();
var hashtag = "";
var raffleOpening  = new Date();
var tweetTotal = 0;
var entrantsTotal = 0;
var winnersTotal = 0;
var losersTotal = 0;
var firstDraw = false;
var loading = false;

function pickAWinner(){
	var winningIndex = Math.floor(Math.random()*entrants.length);
	var winner = entrants[winningIndex];
	entrants.splice(winningIndex, 1);
	var winningTweet = tweets[winner];
	winningTweet.state = STATE_WINNER;

	addTweet(winningTweet);
	
	$('#entrant-' + winningTweet.id).remove();
	entrantsTotal--;
		
	refreshHat();	
	updateHatContentsStatus();
}

function refillTheHat(page){
	 if (page == null){
	 	 if (loading) return;
	 	 loading = true;
		 firstDraw = true;
		 entrants = new Array();
		 entrantsTotal = 0;
		 $('#entrants').text('');
		 page = 1;
		 tweetTotal = 0;
		 
		 //Empty the hat
		 for (var i in tweets) {
		 	var tweet = tweets[i];
		 	if (tweet != null && tweet.state == STATE_HAT) {
		 		tweets[i] = null;
		 	}
		 }
	 }

	tweetsTooOld=false;
	
	var url="http://search.twitter.com/search.json?q=%23"+hashtag+"&rpp="+TWEETS_PER_PAGE+"&page="+page+"&callback=?";
	
	$.getJSON(url,function(json){
		$(json.results).each(function(){
			var tTime=new Date(Date.parse(this.created_at));

			if(tTime<raffleOpening){	
				tweetsTooOld = true;
				return;
			}else{
				tweetTotal++;
				
				if (tweets[this.from_user] == null){
					this.state = STATE_HAT;
					tweets[this.from_user] = this;
					entrantsTotal++;
					entrants.push(this.from_user);
					addTweetToEntrantsList(this);
				}
				
				updateHatContentsStatus();
			}
		});
		
		if (json.results.length < TWEETS_PER_PAGE || tweetsTooOld || TWEETS_PER_PAGE*(page+1) > MAX_TWEETS) {
			refreshHat();
			loading = false;
		}else{
			refillTheHat(page + 1);
		}
	});

	return(false);
}

function updateHatContentsStatus(){
	updateStatusVariable('numberOfTweets', 'tweet', tweetTotal);
	updateStatusVariable('numberOfEntrants', 'entrant', entrantsTotal);
	updateStatusVariable('numberOfWinners', 'winner', winnersTotal);
	updateStatusVariable('numberOfLosers', 'loser', losersTotal);
}

function updateStatusVariable(id, singular, variable){
	var description = singular;
	var number = variable;
	if (variable != 1) description = description + 's';
	if (number == 0) number = 'No';
	$('#' + id).text(number + ' ' + description);
}

function refreshHat(){
	if (entrantsTotal == 0) {
		$('#pick').css('display', 'none');
		$('#hatStatus').text('Nobody has entered the raffle, please try again later...');
		$('#hatStatus').toggleClass('full', 'no-entrants');
	} else {
		$('#pick').css('display', 'inline');
		$('#hatStatus').text('The hat is full');
		$('#hatStatus').toggleClass('full', 'no-entrants');
	}
}

$(function(){
	var tweets = $('#tweets');
	raffleId = getData('raffleId');
	hashtag = getData('hashtag');
	raffleOpening = getData('open');
	
	loadWinningTweets();

	$('#fill').click(function() {
		refillTheHat();
	});
	$('#pick').click(function() {
		pickAWinner();
	});
});

function getData(key){
	return $('#' + key).text();
}

function addTweet(tweet, store){
	if (store == null) {
		store = true;
	}
	if (tweet.state == STATE_WINNER) {
		winnersTotal++;
	} else {
		losersTotal++;
	}
	if (store) {
	 	storeTweet(tweet);
	} else {
		tweets[tweet.from_user] = tweet;
	}
	addTweetToList(tweet, true);
}

function loadWinningTweets(){
	var url=raffleId + "/tweet";
	
	$.getJSON(url,function(json){
		$(json.winningTweets).each(function(){
			addTweet(this, false);
		});
		updateHatContentsStatus();
	});
}

function addTweetToEntrantsList(tweet){
	var tweetDiv='<div class="entrant" id="entrant-' + tweet.id + '"><a target="_blank" href="http://twitter.com/'+tweet.from_user+'"><img width="48" height="48" alt="'+tweet.from_user+' on Twitter" src="'+tweet.profile_image_url+'" /></a></div>';	
	$('#entrants').prepend(tweetDiv);
}

function addTweetToList(tweet){
	var class = 'loser';
	var swapText = 'Make winner';
	var displayDiv = 'losers';
	if (tweet.state == STATE_WINNER){
		class = 'winner';
		swapText = 'Make loser';
		displayDiv = 'winners';
	}

	var since = getSinceDescriptionFromTweetTime(tweet.created_at);
	var tweetBy='<a class="tweet-user" target="_blank" href="http://twitter.com/'+tweet.from_user+'">@'+tweet.from_user+'</a> <span class="tweet-time">'+since+'</span>';
	tweetBy=tweetBy+ '<a class="tweet-view" target="_blank" href="http://twitter.com/'+tweet.from_user+'/statuses/'+tweet.id+'">View Tweet</a>';
	var tweetLeft =     '<div class="tweet-left"><a target="_blank" href="http://twitter.com/'+tweet.from_user+'"><img width="48" height="48" alt="'+tweet.from_user+' on Twitter" src="'+tweet.profile_image_url+'" /></a></div>';
	var tweetRight =    '<div class="tweet-right"><p class="text">'+tweet.text.linkify().linkuser().linktag().replace(/<a/g,'<a target="_blank"')+'<br />'+tweetBy+'</p></div><br style="clear: both;" />';
	var tweetControls = '<div class="tweet-controls"><span class="action" onclick="swapWinnerLoser(\''+tweet.from_user+'\');">'+swapText+'</span> | <span class="action backInHat" onclick="backInHat(\''+tweet.from_user+'\');">Back in the hat</span></div>';
	var tweetDiv= '<div class="'+class+'" id="tweet-'+tweet.from_user+'">'+tweetLeft+tweetRight+tweetControls+'</div>';
	
	$('#' + displayDiv).prepend(tweetDiv);
}

function backInHat(tweeter){
	//Remove from winners/losers		
	$('#tweet-' + tweeter).remove();	
	
	var tweet = tweets[tweeter];
	
	if (tweet.state == STATE_WINNER) {
		winnersTotal--;
	} else {
		losersTotal--;
	}
	
	tweet.state == STATE_HAT;
	
	deleteTweet(tweet);
	entrantsTotal++;
	addTweetToEntrantsList(tweet);
	updateHatContentsStatus();
}

function swapWinnerLoser(tweeter){
	//Remove from winners/losers		
	$('#tweet-' + tweeter).remove();	
	
	var tweet = tweets[tweeter];
	
	if (tweet.state == STATE_WINNER) {
		winnersTotal--;
		losersTotal++;
		tweet.state = STATE_LOSER;
	} else {
		losersTotal--;
		winnersTotal++;
		tweet.state = STATE_WINNER;
	}
	
	storeTweet(tweet);
	addTweetToList(tweet);
	updateHatContentsStatus();
}

function storeTweet(tweet){
	var url= raffleId + '/tweet';
	
	var data = new Object();
	data['tweeter'] = tweet.from_user;
	data['json'] = JSON.stringify(tweet);
	
	$.post(url, data, function(data) {});
}

function deleteTweet(tweet){
	var url= raffleId + '/tweet-delete';
	
	var data = new Object();
	data['tweeter'] = tweet.from_user;
	
	$.post(url, data, function(data) {});
}


/*
 * Following methods, courtesy of TwitStream - A jQuery plugin for the Twitter Search API
 * Version 1.2
 * http://kjc-designs.com/TwitStream
 * Copyright (c) 2009 Noah Cooper
 * Licensed under the GNU General Public License <http://www.gnu.org/licenses/>
 */

function getSinceDescriptionFromTweetTime(timeString){
	var timeOfTweet=new Date(Date.parse(timeString));
	var currentTime=new Date();
	var sinceMin=Math.round((currentTime-timeOfTweet)/60000);
				
	if(sinceMin==0){
		var sinceSec=Math.round((currentTime-timeOfTweet)/1000);
		if(sinceSec<10)
			return 'less than 10 seconds ago';
		else if(sinceSec<20)
			return 'less than 20 seconds ago';
		else
			return 'half a minute ago';
	}
	else if(sinceMin==1){
		var sinceSec=Math.round((currentTime-timeOfTweet)/1000);
		if(sinceSec==30)
			return 'half a minute ago';
		else if(sinceSec<60)
			return 'less than a minute ago';
		else
			return '1 minute ago';
	}
	else if(sinceMin<45)
		return sinceMin+' minutes ago';
	else if(sinceMin>44&&sinceMin<60)
		return 'about 1 hour ago';
	else if(sinceMin<1440){
		var sinceHr=Math.round(sinceMin/60);
		if(sinceHr==1)
			return 'about 1 hour ago';
		else
			return 'about '+sinceHr+' hours ago';
	}
	else if(sinceMin>1439&&sinceMin<2880)
		return '1 day ago';
	else{
		var sinceDay=Math.round(sinceMin/1440);
		return sinceDay+' days ago';
	}
}

String.prototype.linkify=function(){
	return this.replace(/[A-Za-z]+:\/\/[A-Za-z0-9-_]+\.[A-Za-z0-9-_:%&;\?\/.=]+/g,function(m){
		return m.link(m);
	});
};
String.prototype.linkuser=function(){
	return this.replace(/[@]+[A-Za-z0-9-_]+/g,function(u){
		return u.link("http://twitter.com/"+u.replace("@",""));
	});
};
String.prototype.linktag=function(){
	return this.replace(/[]+[A-Za-z0-9-_]+/,function(t){
		return t;
	});
};