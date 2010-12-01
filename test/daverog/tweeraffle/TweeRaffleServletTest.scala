package daverog.tweeraffle

import org.scalatest.junit.JUnitSuite
import _root_.org.junit.Test
import _root_.org.junit.Before

import java.io._
import java.util._
import freemarker.template._
import org.mockito._
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.hamcrest.collection.IsMapContaining.hasEntry
import org.mockito.internal.util.reflection.Whitebox._
import org.junit.Assert._
import javax.servlet.http._
import javax.persistence.EntityManager

class TweeRaffleServletTest extends JUnitSuite {

    @Mock var resp: HttpServletResponse = _
    @Mock var req: HttpServletRequest = _
    @Mock var renderer: FreemarkerRenderer = _
    
    @Spy var servlet: TweeRaffleServlet = new TweeRaffleServlet()

    @Before
    def setUp() {
        MockitoAnnotations.initMocks(this);
        
        setInternalState(servlet, "freemarkerRenderer", renderer)
    }

    @Test
    def twoSlashesResultsInA404() {
        when(req.getRequestURI).thenReturn("/eggy/bread")
        
        servlet.doGet(req, resp)
        
        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND)
    }
    
    @Test
    def rootSlashResultsInIndexTemplate() {
        when(req.getRequestURI).thenReturn("/")
        
        servlet.doGet(req, resp)
        
        verify(resp).setContentType("application/xhtml+xml")
        verify(renderer).render(anyObject[HashMap[String, Object]](), anyString(), anyObject[Writer]())
    }
       
    @Test
    def aHashtagFollowingTheSlashThatDoesNotExistResultsInA404() {
        when(req.getRequestURI).thenReturn("/keydoesnotexist")
        doReturn(null).when(servlet).loadRaffleByKeyString("keydoesnotexist");
        
        servlet.doGet(req, resp)
        
        verify(resp).sendError(HttpServletResponse.SC_NOT_FOUND)
    }
    
    @Test
    def aHashtagFollowingTheSlashThatFindsAnExistingRaffleResultsAFreemarkerTemplateBeingRenderedWithRaffleInTheObjectMap() {
    	val raffle = new Raffle()
    	when(req.getRequestURI).thenReturn("/keyexists")
    	doReturn(raffle).when(servlet).loadRaffleByKeyString("keyexists");
    	
    	servlet.doGet(req, resp)
    	
    	verify(resp).setContentType("application/xhtml+xml")
    	verify(renderer).render(argThat(hasEntry[String, Object]("raffle", raffle)), anyString(), anyObject())
    }

}