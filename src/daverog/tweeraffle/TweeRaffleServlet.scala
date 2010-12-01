package daverog.tweeraffle

import java.io.IOException


import javax.persistence.EntityManager
import javax.servlet.http._
import java.net.MalformedURLException
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.IOException
import java.io.File
import java.io.Writer
import java.io.OutputStreamWriter
import java.util._
import freemarker.template._
import java.util.logging.Logger
import org.apache.commons.lang.StringUtils
import com.google.appengine.api.datastore.KeyFactory.stringToKey
import scala.collection.JavaConversions.asBuffer
import com.google.appengine.api.datastore.Text

class TweeRaffleServlet extends HttpServlet {

	val logger = Logger.getLogger(classOf[TweeRaffleServlet].getName());
	var freemarkerRenderer = ComponentRegistry.freemarkerRenderer
	var tweetServlet = ComponentRegistry.tweetServlet 
	var entityManager = EntityManagerCreator.create
	
    override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
		resp.setContentType("application/xhtml+xml")
		
		if (req.getRequestURI == "/") {
			freemarkerRenderer.render(null, "index", resp.getWriter)
        	return
		}
		
		try{
			val raffle = loadRaffleByKeyString(getUriPart(req, 1))
			
	        if (raffle == null) {
	            resp.sendError(HttpServletResponse.SC_NOT_FOUND)
	            return
	        } 
		
			try{
				val option = getUriPart(req, 2)
				
				option match {
					case "tweet" => {
						tweetServlet.getTweetJson(entityManager, raffle, resp)
						return
					}
				}
			} catch {
				case e:IllegalArgumentException => {
					val objectMap: HashMap[String, Object] = new HashMap()
			        objectMap.put("raffle", raffle)
			        freemarkerRenderer.render(objectMap, "raffle", resp.getWriter)
			        return;
				}
			}
		}catch {
			case e:IllegalArgumentException => {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND)
				return
			}
		}
    }
	
	override def doPost(req:HttpServletRequest, resp:HttpServletResponse) {
		try{
			val raffle = loadRaffleByKeyString(getUriPart(req, 1))
			
	        if (raffle == null) {
	            resp.sendError(HttpServletResponse.SC_NOT_FOUND)
	            return
	        } 
		
			val option = getUriPart(req, 2)
				
			option match {
				case "tweet" => {
					tweetServlet.postTweet(entityManager, raffle, req, resp)
					return
				}
				case "tweet-delete" => {
					tweetServlet.deleteTweet(entityManager, raffle, req, resp)
					return
				}
				case _ => {
					 resp.sendError(HttpServletResponse.SC_NOT_FOUND)
					 return
				}
			} 
		}catch {
			case e:IllegalArgumentException => {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND)
				return
			}
		}
	}

	
	def getUriPart(req: HttpServletRequest, partNumber: Int): String = {
		val urlParts = req.getRequestURI.split("/")
		
		if(partNumber >= urlParts.length) {
			throw new IllegalArgumentException("There are only " + urlParts.length + " URL parts, but part " + partNumber + " is needed");
		}
		
		urlParts(partNumber)
	}
	
	def loadRaffleByKeyString(id: String) = {
		entityManager.find(classOf[Raffle], stringToKey(id))
	}
	
}
