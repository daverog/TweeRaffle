package daverog.tweeraffle
/**
 * Cake pattern used from http://jonasboner.com/2008/10/06/real-world-scala-dependency-injection-di.html
 *
 */
object ComponentRegistry extends FreemarkerRendererComponentImpl with TweetServletComponentImpl {  
  
   val freemarkerRenderer: FreemarkerRenderer = new FreemarkerRendererImpl
   val tweetServlet: TweetServlet = new TweetServletImpl
   
}  