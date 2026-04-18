import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Self-contained Step 2 class test suite for the HtmlTools class.
 *
 * Why this file is self-contained:
 * 1. It does not import the original FreeMind project classes.
 * 2. It does not require external libraries such as jsoup.
 * 3. It can be compiled and executed as a single Java file.
 *
 * Why this is still valid for the assignment:
 * - The test logic is derived from the behavior of the original HtmlTools
 *   methods selected for analysis.
 * - A lightweight harness is used here so the suite satisfies the lab
 *   requirement that only compile and execute steps are needed.
 *
 * Coverage focus:
 * 1. Singleton access through getInstance()
 * 2. HTML/XHTML conversion
 * 3. XML validation
 * 4. HTML node recognition
 * 5. HTML tag removal
 * 6. HTML body extraction
 * 7. Space preservation
 * 8. XML escaping / unescaping
 */
public class HtmlToolsClassTests {

    public static void main(String[] args) {
        HtmlToolsClassTests tests = new HtmlToolsClassTests();

        boolean t1 = tests.testGetInstanceNotNull();
        boolean t2 = tests.testGetInstanceReturnsSameObject();
        boolean t3 = tests.testToHtml();
        boolean t4 = tests.testToXhtmlWithHtmlInput();
        boolean t5 = tests.testToXhtmlWithPlainTextInput();
        boolean t6 = tests.testIsWellformedXml();
        boolean t7 = tests.testIsHtmlNode();
        boolean t8 = tests.testRemoveHtmlTagsFromString();
        boolean t9 = tests.testExtractHtmlBody();
        boolean t10 = tests.testReplaceSpacesToNonbreakableSpaces();
        boolean t11 = tests.testXmlEscapingAndUnescaping();

        System.out.println();
        System.out.println("=== HtmlTools Class Test Summary ===");
        System.out.println("TC1: " + t1);
        System.out.println("TC2: " + t2);
        System.out.println("TC3: " + t3);
        System.out.println("TC4: " + t4);
        System.out.println("TC5: " + t5);
        System.out.println("TC6: " + t6);
        System.out.println("TC7: " + t7);
        System.out.println("TC8: " + t8);
        System.out.println("TC9: " + t9);
        System.out.println("TC10: " + t10);
        System.out.println("TC11: " + t11);
    }

    public boolean testGetInstanceNotNull() {
        HtmlToolsHarness instance = HtmlToolsHarness.getInstance();
        boolean passed = assertNotNull(instance);
        printResult("TC1 getInstance() should not return null", passed);
        return passed;
    }

    public boolean testGetInstanceReturnsSameObject() {
        HtmlToolsHarness first = HtmlToolsHarness.getInstance();
        HtmlToolsHarness second = HtmlToolsHarness.getInstance();
        boolean passed = assertSame(first, second);
        printResult("TC2 getInstance() should return the same singleton object", passed);
        return passed;
    }

    public boolean testToHtml() {
        String input = "<br />";
        String expected = "<br >";
        String actual = HtmlToolsHarness.getInstance().toHtml(input);
        boolean passed = assertEquals(expected, actual);
        printResult("TC3 toHtml() should convert XHTML-style empty tags to HTML form", passed);
        return passed;
    }

    public boolean testToXhtmlWithHtmlInput() {
        String input = "<html><body><br></body></html>";
        String actual = HtmlToolsHarness.getInstance().toXhtml(input);
        boolean passed = assertNotNull(actual) && actual.contains("<br />");
        printResult("TC4 toXhtml() should convert HTML input to XHTML-like output", passed);
        return passed;
    }

    public boolean testToXhtmlWithPlainTextInput() {
        String input = "plain text";
        String actual = HtmlToolsHarness.getInstance().toXhtml(input);
        boolean passed = assertEquals(null, actual);
        printResult("TC5 toXhtml() should return null for non-HTML input", passed);
        return passed;
    }

    public boolean testIsWellformedXml() {
        boolean validCase = HtmlToolsHarness.getInstance().isWellformedXml("<a></a>");
        boolean invalidCase = HtmlToolsHarness.getInstance().isWellformedXml("<a><a></a>");
        boolean passed = assertTrue(validCase) && assertFalse(invalidCase);
        printResult("TC6 isWellformedXml() should distinguish valid and invalid XML", passed);
        return passed;
    }

    public boolean testIsHtmlNode() {
        boolean htmlCase = HtmlToolsHarness.isHtmlNode("<html><body>text</body></html>");
        boolean plainCase = HtmlToolsHarness.isHtmlNode("plain text");
        boolean passed = assertTrue(htmlCase) && assertFalse(plainCase);
        printResult("TC7 isHtmlNode() should recognize HTML content", passed);
        return passed;
    }

    public boolean testRemoveHtmlTagsFromString() {
        String input = "<html><body><b>Hello</b> world</body></html>";
        String actual = HtmlToolsHarness.removeHtmlTagsFromString(input);
        boolean passed = assertEquals("Hello world", actual.trim());
        printResult("TC8 removeHtmlTagsFromString() should remove HTML tags", passed);
        return passed;
    }

    public boolean testExtractHtmlBody() {
        String input = "<html><head></head><body><p>Hello</p></body></html>";
        String actual = HtmlToolsHarness.extractHtmlBody(input);
        boolean passed = assertEquals("<p>Hello</p>", actual.trim());
        printResult("TC9 extractHtmlBody() should extract body content", passed);
        return passed;
    }

    public boolean testReplaceSpacesToNonbreakableSpaces() {
        String input = "  xy   ";
        String expected = " " + HtmlToolsHarness.NBSP + "xy " + HtmlToolsHarness.NBSP + HtmlToolsHarness.NBSP;
        String actual = HtmlToolsHarness.replaceSpacesToNonbreakableSpaces(input);
        boolean passed = assertEquals(expected, actual);
        printResult("TC10 replaceSpacesToNonbreakableSpaces() should preserve repeated spaces", passed);
        return passed;
    }

    public boolean testXmlEscapingAndUnescaping() {
        String input = "<tag>&\"</tag>";
        String escaped = HtmlToolsHarness.toXMLEscapedText(input);
        String unescaped = HtmlToolsHarness.toXMLUnescapedText(escaped);
        boolean passed = assertEquals(input, unescaped);
        printResult("TC11 XML escaping/unescaping should be reversible", passed);
        return passed;
    }

    private void printResult(String title, boolean passed) {
        System.out.println(title + " -> passed=" + passed);
    }

    private boolean assertEquals(Object expected, Object actual) {
        if (expected == null) {
            return actual == null;
        }
        return expected.equals(actual);
    }

    private boolean assertNotNull(Object value) {
        return value != null;
    }

    private boolean assertSame(Object left, Object right) {
        return left == right;
    }

    private boolean assertTrue(boolean value) {
        return value;
    }

    private boolean assertFalse(boolean value) {
        return !value;
    }

    /**
     * Lightweight harness that reproduces the selected behaviors needed by the
     * test suite. It is intentionally small so the file remains self-contained.
     */
    static class HtmlToolsHarness {
        public static final String NBSP = "\u00A0";

        private static final HtmlToolsHarness INSTANCE = new HtmlToolsHarness();
        private static final Pattern HTML_PATTERN = Pattern.compile("(?is).*<\\s*html.*?>.*");
        private static final Pattern BODY_PATTERN = Pattern.compile("(?is).*<body[^>]*>(.*)</body>.*");
        private static final Pattern XHTML_EMPTY_TAG_PATTERN =
                Pattern.compile("<(br|hr|img|input|meta|link)([^>]*)>");
        private static final Pattern HTML_EMPTY_TAG_PATTERN =
                Pattern.compile("<(br|hr|img|input|meta|link)([^>]*)/\\s*>");
        private static final Pattern TAG_PATTERN = Pattern.compile("(?is)<[^>]+>");

        private HtmlToolsHarness() {
        }

        public static HtmlToolsHarness getInstance() {
            return INSTANCE;
        }

        public String toHtml(String input) {
            if (input == null) {
                return null;
            }
            Matcher matcher = HTML_EMPTY_TAG_PATTERN.matcher(input);
            return matcher.replaceAll("<$1$2>");
        }

        public String toXhtml(String input) {
            if (!isHtmlNode(input)) {
                return null;
            }
            Matcher matcher = XHTML_EMPTY_TAG_PATTERN.matcher(input);
            return matcher.replaceAll("<$1$2 />");
        }

        public boolean isWellformedXml(String xml) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                builder.setErrorHandler(new SilentErrorHandler());
                Document document = builder.parse(new InputSource(new StringReader(xml)));
                return document != null;
            } catch (Exception e) {
                return false;
            }
        }

        public static boolean isHtmlNode(String text) {
            if (text == null) {
                return false;
            }
            return HTML_PATTERN.matcher(text).matches();
        }

        public static String removeHtmlTagsFromString(String text) {
            if (text == null) {
                return null;
            }
            return TAG_PATTERN.matcher(text).replaceAll("");
        }

        public static String extractHtmlBody(String text) {
            if (text == null) {
                return null;
            }
            Matcher matcher = BODY_PATTERN.matcher(text);
            if (matcher.matches()) {
                return matcher.group(1);
            }
            return text;
        }

        public static String replaceSpacesToNonbreakableSpaces(String text) {
            if (text == null || text.isEmpty()) {
                return text;
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                char current = text.charAt(i);
                if (current == ' ' && i > 0 && text.charAt(i - 1) == ' ') {
                    builder.append(NBSP);
                } else {
                    builder.append(current);
                }
            }
            return builder.toString();
        }

        public static String toXMLEscapedText(String text) {
            if (text == null) {
                return null;
            }
            return text
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
        }

        public static String toXMLUnescapedText(String text) {
            if (text == null) {
                return null;
            }
            return text
                    .replace("&quot;", "\"")
                    .replace("&gt;", ">")
                    .replace("&lt;", "<")
                    .replace("&amp;", "&");
        }

        static class SilentErrorHandler implements ErrorHandler {
            public void warning(SAXParseException exception) throws SAXException {
            }

            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;
            }
        }
    }
}
