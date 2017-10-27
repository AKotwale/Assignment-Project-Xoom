package com.sampleproject.utility;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import com.sampleproject.restservice.exception.DataParsingException;

/**
 * Utility Class.
 * 
 * @author atulkotwale
 *
 */
public class Utility {

	/**
	 * This method would return the count for given tag from the html content.
	 * 
	 * @param htmlContent
	 *            - html content.
	 * @param tag
	 *            - tag which needs to count.
	 * @return - count of the tag.
	 * @throws DataParsingException
	 *             - throws exception if error occurred.
	 */
	public static Long getTagCount(String htmlContent, String tag) throws DataParsingException {

		HtmlParser parser = new HtmlParser();
		Long tagcount = 0L;
		synchronized (Utility.class) {
			parser.parseHtml(new StringReader(htmlContent));
			List<String> tagList = parser.getList();
			tagcount = tagList.stream().parallel().filter(x -> x.equalsIgnoreCase(tag)).count();
		}
		return tagcount;
	}

}

/**
 * Html parser class.
 * 
 * @author atulkotwale
 *
 */
class HtmlParser {

	private ParserDelegator parserDelegator = new ParserDelegator();

	private final List<String> list = new ArrayList<String>();

	public List<String> getList() {
		return list;
	}

	private ParserCallback parserCallback = new ParserCallback() {
		public void handleText(final char[] data, final int pos) {
			// list.add(new String(data));
		}

		public void handleStartTag(Tag tag, MutableAttributeSet attribute, int pos) {

			list.add(tag.toString());
		}

		public void handleEndTag(Tag t, final int pos) {
		}

		public void handleSimpleTag(Tag t, MutableAttributeSet a, final int pos) {
			list.add(t.toString());
		}

		public void handleComment(final char[] data, final int pos) {
		}

		public void handleError(final java.lang.String errMsg, final int pos) {
		}
	};

	public synchronized void parseHtml(Reader r) {
		try {
			parserDelegator.parse(r, parserCallback, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}