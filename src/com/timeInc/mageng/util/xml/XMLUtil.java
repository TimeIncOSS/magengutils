/*******************************************************************************
 * Copyright 2014 Time Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.timeInc.mageng.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMSource;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class for manipulating XML
 */
public class XMLUtil {
	
	private XMLUtil() {} 
	
	/**
	 * The Interface StringReplacer.
	 */
	public interface StringReplacer {
		String replace(String original, String searchString, String replaceString);
	}
	
	private static final Logger log = Logger.getLogger(XMLUtil.class);
	
	/**
	 * Apply xsl to document.
	 *
	 * @param document the document
	 * @param styleSheetFile the style sheet file
	 * @return the string
	 * @throws TransformerException the transformer exception
	 */
	public static String applyXslToDocument (Document document, File styleSheetFile) throws TransformerException {
		JDOMSource source = new JDOMSource(document);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();		
		Transformer transformer = transformerFactory.newTransformer(new StreamSource(styleSheetFile));
		StringWriter writer = new StringWriter();
		transformer.transform(source, new StreamResult(writer));
		return writer.getBuffer().toString();		
	}
	
	
	/**
	 * Builds a jdom document from a string file name.
	 *
	 * @param filename the filename
	 * @return Document
	 * @throws JDOMException the JDOM exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Document buildDocument(String filename) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder(); 
		return builder.build(new File(filename));
	}
	
	/**
	 * Prints the XML to the output property indented.
	 *
	 * @param document the document
	 * @param out the out
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void printXML(Document document, OutputStream out) throws IOException {
		 XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
         outputter.output(document, out);
	}
	
	/**
	 * Search replace text substring.
	 *
	 * @param filename the filename
	 * @param replacer the replacer
	 * @param xpathExpression the xpath expression
	 * @param searchString the search string
	 * @param replaceString the replace string
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws XPathExpressionException the x path expression exception
	 * @throws SAXException the SAX exception
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws TransformerFactoryConfigurationError the transformer factory configuration error
	 * @throws TransformerException the transformer exception
	 */
	public static void searchReplaceTextSubstring(String filename, StringReplacer replacer, 
			String xpathExpression,	String searchString, String replaceString) 
	      throws IOException, XPathExpressionException, SAXException, 
		ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		log.info("Beginning modification of " + filename);
		org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(filename));

		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodes = (NodeList)xpath.evaluate(xpathExpression, doc, XPathConstants.NODESET);
		
		/* let's rename the nodes */
		for (int idx = 0; idx < nodes.getLength(); idx++) {
			log.debug("Updating '" + nodes.item(idx).getTextContent() + "'");
			nodes.item(idx).setTextContent(
					replacer.replace(nodes.item(idx).getTextContent(), searchString, replaceString));
		}

		// Write the DOM document to the file
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.transform(new DOMSource(doc), new StreamResult(new File(filename)));
		log.info("Done modification of " + filename);
	}

	/**
	 * Transform jdom document.
	 *
	 * @param document the document
	 * @param styleSheetFile the style sheet file
	 * @return the string buffer
	 * @throws TransformerException the transformer exception
	 */
	public static StringBuffer TransformJDOMDocument (Document document, File styleSheetFile) throws TransformerException{
		JDOMSource source = new JDOMSource(document);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();		
		Transformer transformer = transformerFactory.newTransformer(new StreamSource(styleSheetFile));
		StringWriter writer = new StringWriter();
		transformer.transform(source, new StreamResult(writer));
		return writer.getBuffer();		
	}

	/**
	 * Parses the template.
	 *
	 * @param document the document
	 * @param xslPath the xsl path
	 * @return the string
	 * @throws TransformerException the transformer exception
	 */
	public static String parseTemplate(Document document, File xslPath) throws TransformerException {
		/*validate parameters */
		if(document == null) return null;
		if(xslPath == null || xslPath.length() < 1) return null;
		
		/* for valid patameters parse the dpcument*/
		return XMLUtil.TransformJDOMDocument(document,xslPath).toString();
	}	
}
