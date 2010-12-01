package daverog.tweeraffle

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.IOException
import java.io.File
import java.io.Writer
import java.io.OutputStreamWriter
import java.util._
import freemarker.template._

trait FreemarkerRenderer {
	def setDirectoryForTemplateLoading(directory: String)
	def render(model:Map[String, Object], templateName:String, outputWriter:Writer)
}

trait FreemarkerRendererComponent {
	val freemarkerRenderer: FreemarkerRenderer 
}

trait FreemarkerRendererComponentImpl extends FreemarkerRendererComponent {
	class FreemarkerRendererImpl extends FreemarkerRenderer {
		var cfg = new Configuration() {
	    	setDirectoryForTemplateLoading(new File("."))
	    	setObjectWrapper(new DefaultObjectWrapper())
		}
		
		def setDirectoryForTemplateLoading(directory: String) {
			cfg.setDirectoryForTemplateLoading(new File(directory))
		}
	    
		def render(model:Map[String, Object], templateName:String, outputWriter:Writer) = {
	        cfg.getTemplate(templateName + ".ftl").process(model, outputWriter)
		}
	}
}