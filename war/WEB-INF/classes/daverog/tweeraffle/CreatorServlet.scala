package daverog.tweeraffle

import java.io.IOException

import javax.persistence.EntityManager
import javax.servlet.http._
import java.net.MalformedURLException
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.IOException
import java.util.GregorianCalendar
import java.util.Calendar


class CreatorServlet extends HttpServlet {
	
	override def doPost(req:HttpServletRequest, resp:HttpServletResponse) {
		val hashtag = req.getParameter("hashtag");
		val age = req.getParameter("age").toInt;
		
		val raffle = new Raffle
		raffle.hashtag = hashtag
		val openTime = (new GregorianCalendar);
		openTime.add(Calendar.MINUTE, -age)
		raffle.open = openTime.getTime
		
		val entityManager:EntityManager = EntityManagerCreator.create
		
		try {
			entityManager.persist(raffle);
	    } finally {
	        entityManager.close();
	    }
		
		resp.setContentType("text/plain");
		resp.sendRedirect("/" + raffle.getId);
	}
	
}
