package daverog.tweeraffle

import org.scalatest.junit.JUnitSuite
import _root_.org.junit.Test
import _root_.org.junit.Before

import java.io._
import java.util._
import freemarker.template._
import org.mockito._
import org.mockito.Mockito._
import org.mockito.internal.util.reflection.Whitebox._
import org.junit.Assert._
import org.junit._

import org.powermock.reflect.internal.WhiteboxImpl.invokeMethod

import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest

class FreemarkerRendererRendererTest extends JUnitSuite {

    @Mock var config: Configuration = _
    @Mock var template: Template = _
    
    val renderer = ComponentRegistry.freemarkerRenderer 

    @Before
    def setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    def rendersFreemarkerLoop() {
        val writer = new StringWriter();

        val testDir = "./testdata/"
        	renderer.setDirectoryForTemplateLoading(testDir)

      	renderer.render(new HashMap, "test", writer)
        
        val expected = "winterspringsummerautumn"
        
        assertEquals(writer.toString.getClass, expected.getClass)
        assertEquals(writer.toString, expected)
    }

    @Test
    def addsFTLtoTemplateName() {
    	setInternalState(renderer, "cfg", config);

        when(config.getTemplate("sample.ftl")).thenReturn(template)

        val writer = new StringWriter();
        renderer.render(new HashMap, "sample", writer)
    }

}