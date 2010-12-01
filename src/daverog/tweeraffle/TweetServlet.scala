package daverog.tweeraffle

import java.io.IOException


import javax.persistence.EntityManager;
import javax.servlet.http._
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Date
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.logging.Logger
import com.google.appengine.api.datastore.Text
import scala.collection.JavaConversions.asBuffer
import com.google.appengine.api.datastore.KeyFactory.stringToKey

trait TweetServlet {
	def postTweet(entityManager:EntityManager, raffle:Raffle, req:HttpServletRequest, resp:HttpServletResponse) 
	def deleteTweet(entityManager:EntityManager, raffle:Raffle, req:HttpServletRequest, resp:HttpServletResponse) 
	def getTweetJson(entityManager:EntityManager, raffle:Raffle, resp:HttpServletResponse) 
}

trait TweetServletComponent {
	val tweetServlet: TweetServlet 
}

trait TweetServletComponentImpl extends TweetServletComponent {
	class TweetServletImpl extends TweetServlet {
	
		val logger = Logger.getLogger(classOf[TweetServlet].getName());
		
		def postTweet(entityManager:EntityManager, raffle:Raffle, req:HttpServletRequest, resp:HttpServletResponse) {
			var tweet = loadTweet(entityManager, raffle, req);
			
			if (tweet == null) {
				tweet = new WinningTweet
				raffle.getWinningTweets.add(tweet);
			}
			
			tweet.raffle = raffle
			tweet.tweeter = req.getParameter("tweeter")
			tweet.json = new Text(req.getParameter("json"))
			entityManager.persist(tweet);
		}

		def deleteTweet(entityManager:EntityManager, raffle:Raffle, req:HttpServletRequest, resp:HttpServletResponse) {
			val tweet = loadTweet(entityManager, raffle, req);
			
			if (tweet == null) throw new IllegalArgumentException("Cannot delete a non-existant tweet")
			
			raffle.getWinningTweets.remove(tweet);
			entityManager.remove(tweet);
		}
		
		def getTweetJson(entityManager:EntityManager, raffle:Raffle, resp:HttpServletResponse) {
	       	resp.setContentType("application/json")
			val writer = resp.getWriter;
			writer.write("{ \"winningTweets\" : [")
			
			for (tweet <- asBuffer(raffle.getWinningTweets)) {
				writer.write(tweet.getJsonString)
				writer.write(",");
			}
			
			writer.write("]}")
		}
		
		def loadTweet(entityManager:EntityManager, raffle:Raffle, req:HttpServletRequest): WinningTweet = {
			val tweeter = req.getParameter("tweeter");
			for (tweet <- asBuffer(raffle.getWinningTweets)) {
				logger.warning(tweet.tweeter + " vs. " + tweeter + tweet.tweeter.equals(tweeter))
				if (tweet.tweeter.equals(tweeter)) return tweet;
			}
			return null;
		}
	}
}
