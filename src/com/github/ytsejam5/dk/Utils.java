package com.github.ytsejam5.dk;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class Utils {

	private Utils() {
	}

	public static boolean isBlank(String pattern) {
		return pattern == null || pattern.matches("\\s*") ? true : false;
	}

	public static String urlEncode(String pattern) {
		try {
			return URLEncoder.encode(pattern, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	public static String xmlEncode(String pattern) {
		if (pattern == null) {
			return "";
		}

		pattern = pattern.replaceAll("&", "&amp;");
		pattern = pattern.replaceAll("\"", "&quot;");
		pattern = pattern.replaceAll("<", "&lt;");
		pattern = pattern.replaceAll(">", "&gt;");

		return pattern;
	}

	public static String formatDate(Date date) {
		return new SimpleDateFormat("YYYY/MM/DD").format(date);
	}

	public static String trim(String pattern, int length) {
		return pattern.length() > length ? pattern.subSequence(0, length)
				+ "..." : pattern;
	}

	public static String join(List<String> list, String pattern) {
		if (list == null) {
			return "";
		}

		StringBuilder buffer = new StringBuilder();

		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String value = (String) iterator.next();
			buffer.append(value);

			if (iterator.hasNext()) {
				buffer.append(pattern);
			}
		}

		return buffer.toString();
	}

	public static String join(String[] array, String pattern) {
		if (array == null) {
			return "";
		}

		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < array.length; i++) {
			buffer.append(array[i]);

			if (i < array.length - 1) {
				buffer.append(pattern);
			}
		}

		return buffer.toString();
	}

	public static boolean hasQueryText(String query) {
		String[] criterias = query.split("\\s+");
		for (int i = 0; i < criterias.length; i++) {
			if (!criterias[i].matches(".+:.+")) {
				return true;
			}
		}

		return false;
	}

	public static String dom2string(Document document) {
		String string = null;
		StringWriter out = null;

		try {
			out = new StringWriter();

			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.transform(new DOMSource(document), new StreamResult(out));

			string = out.toString();

		} catch (TransformerException e) {
			throw new IllegalStateException(e);

		} finally {
			if (out != null) {
				try {
					out.close();

				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}

		return string;
	}

	public static String encodeHTMLAndHighlight(String pattern, String tag) {
		String snippetText = pattern;
		snippetText = snippetText.replaceAll("<\\?xml[^>]+>\\s*", "");
		snippetText = snippetText.replaceAll("\\s*</?search:match[^>]*>\\s*", "");
		snippetText = snippetText.replaceAll("\\s*</?search:snippet[^>]*>\\s*", "");
		snippetText = snippetText.replaceAll("<search:highlight/>", "");
		snippetText = xmlEncode(snippetText);
		snippetText = snippetText.replaceAll("&lt;(/?)search:highlight&gt;", "<$1" + tag + ">");

		return snippetText;
	}

	public static String resourceAsString(String resource, Object[] params) {
		String content = Utils.readAsString(Utils.class.getResource(resource));
		return MessageFormat.format(content, params);
	}

	public static String readAsString(URL url) {
		InputStreamReader reader = null;
		StringWriter writer = null;

		try {
			reader = new InputStreamReader(url.openStream());
			writer = new StringWriter();

			char[] buffer = new char[1024];
			for (int length = -1; (length = reader.read(buffer)) > 0;) {
				writer.write(buffer, 0, length);
			}

		} catch (IOException e) {
			throw new IllegalStateException(e);

		} finally {
			close(reader);
			close(writer);
		}

		return writer.toString();
	}

	public static void close(Reader reader) {
		if (reader != null) {
			try {
				reader.close();

			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public static void close(Writer writer) {
		if (writer != null) {
			try {
				writer.close();

			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}
}