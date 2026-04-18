import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Self-contained bottom-up testing suite for:
 * 1. HtmlTools
 * 2. MindMapNodeModel
 * 3. PasteActor
 *
 * Bottom-up order used in this file:
 * - Layer 1: HtmlToolsHarness
 * - Layer 2: MindMapNodeModelHarness
 * - Layer 3: PasteActorHarness
 *
 * This file is independent from the FreeMind build and can be compiled and
 * executed directly.
 */
public class BottomUpHtmlInteractionTests {

    public static void main(String[] args) throws Exception {
        BottomUpHtmlInteractionTests tests = new BottomUpHtmlInteractionTests();

        boolean t1 = tests.testHtmlToolsConvertsHtmlToPlainText();
        boolean t2 = tests.testMindMapNodeUsesHtmlToolsForPlainTextContent();
        boolean t3 = tests.testMindMapNodeSaveTxtUsesProcessedPlainText();
        boolean t4 = tests.testPasteActorCreatesNodeFromHtmlClipboard();
        boolean t5 = tests.testPasteActorStoresPlainTextViewThroughMindMapNodeModel();
        boolean t6 = tests.testBottomUpChainFromPasteToNodeToPlainTextExport();

        System.out.println();
        System.out.println("=== Bottom-Up Test Summary ===");
        System.out.println("TC1: " + t1);
        System.out.println("TC2: " + t2);
        System.out.println("TC3: " + t3);
        System.out.println("TC4: " + t4);
        System.out.println("TC5: " + t5);
        System.out.println("TC6: " + t6);
    }

    public boolean testHtmlToolsConvertsHtmlToPlainText() {
        String html = "<html><body><b>Hello</b> world</body></html>";
        String actual = HtmlToolsHarness.htmlToPlain(html);
        boolean passed = assertEquals("Hello world", actual.trim());
        printResult("TC1 HtmlTools should convert HTML node text to plain text", passed);
        return passed;
    }

    public boolean testMindMapNodeUsesHtmlToolsForPlainTextContent() {
        MindMapNodeModelHarness node = new MindMapNodeModelHarness("<html><body><i>Task</i></body></html>");
        boolean passed = assertEquals("Task", node.getPlainTextContent().trim());
        printResult("TC2 MindMapNodeModel should use HtmlTools when returning plain text", passed);
        return passed;
    }

    public boolean testMindMapNodeSaveTxtUsesProcessedPlainText() throws Exception {
        MindMapNodeModelHarness node = new MindMapNodeModelHarness("<html><body><b>Line</b></body></html>");
        StringWriter writer = new StringWriter();
        node.saveTXT(writer, 1);
        boolean passed = assertEquals("    Line\n", writer.toString());
        printResult("TC3 MindMapNodeModel saveTXT() should export processed plain text", passed);
        return passed;
    }

    public boolean testPasteActorCreatesNodeFromHtmlClipboard() {
        MindMapNodeModelHarness root = new MindMapNodeModelHarness("root");
        PasteActorHarness actor = new PasteActorHarness();

        MindMapNodeModelHarness pasted = actor.pasteHtml("<html><body><b>Paste</b></body></html>", root);
        boolean passed = assertNotNull(pasted)
                && assertEquals(1, root.getChildCount())
                && assertEquals("<html><body><b>Paste</b></body></html>", pasted.getText());
        printResult("TC4 PasteActor should create and insert a node from HTML clipboard text", passed);
        return passed;
    }

    public boolean testPasteActorStoresPlainTextViewThroughMindMapNodeModel() {
        MindMapNodeModelHarness root = new MindMapNodeModelHarness("root");
        PasteActorHarness actor = new PasteActorHarness();

        MindMapNodeModelHarness pasted = actor.pasteHtml("<html><body>Alpha &amp; Beta</body></html>", root);
        boolean passed = assertEquals("Alpha & Beta", pasted.getPlainTextContent().trim());
        printResult("TC5 Pasted node should expose plain text through MindMapNodeModel + HtmlTools", passed);
        return passed;
    }

    public boolean testBottomUpChainFromPasteToNodeToPlainTextExport() throws Exception {
        MindMapNodeModelHarness root = new MindMapNodeModelHarness("root");
        PasteActorHarness actor = new PasteActorHarness();

        MindMapNodeModelHarness pasted = actor.pasteHtml("<html><body><p>Final</p></body></html>", root);
        StringWriter writer = new StringWriter();
        pasted.saveTXT(writer, 0);

        boolean passed = assertEquals("Final\n", writer.toString());
        printResult("TC6 Bottom-up chain should work from PasteActor to node export", passed);
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

    static class HtmlToolsHarness {
        private static final Pattern HTML_PATTERN = Pattern.compile("(?is).*<\\s*html.*?>.*");
        private static final Pattern TAG_PATTERN = Pattern.compile("(?is)<[^>]+>");

        private HtmlToolsHarness() {
        }

        public static boolean isHtmlNode(String text) {
            return text != null && HTML_PATTERN.matcher(text).matches();
        }

        public static String removeHtmlTagsFromString(String text) {
            if (text == null) {
                return null;
            }
            return TAG_PATTERN.matcher(text).replaceAll("");
        }

        public static String htmlToPlain(String text) {
            if (text == null) {
                return null;
            }
            if (!isHtmlNode(text)) {
                return text;
            }
            String result = removeHtmlTagsFromString(text);
            return unescapeBasicEntities(result);
        }

        public static String makeValidXml(String text) {
            if (text == null) {
                return null;
            }
            return text.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
        }

        public static String unescapeHTMLUnicodeEntity(String text) {
            return unescapeBasicEntities(text);
        }

        private static String unescapeBasicEntities(String text) {
            if (text == null) {
                return null;
            }
            return text.replace("&amp;", "&")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">");
        }
    }

    static class MindMapNodeModelHarness {
        private final List<MindMapNodeModelHarness> children = new ArrayList<>();
        private String text;
        private MindMapNodeModelHarness parent;

        MindMapNodeModelHarness(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public String getPlainTextContent() {
            return HtmlToolsHarness.htmlToPlain(text);
        }

        public void addChild(MindMapNodeModelHarness child) {
            child.parent = this;
            children.add(child);
        }

        public int getChildCount() {
            return children.size();
        }

        public MindMapNodeModelHarness getParent() {
            return parent;
        }

        public void saveTXT(StringWriter writer, int depth) {
            for (int i = 0; i < depth; i++) {
                writer.write("    ");
            }
            writer.write(getPlainTextContent());
            writer.write("\n");
        }
    }

    static class PasteActorHarness {
        private static final Pattern BODY_PATTERN = Pattern.compile("(?is).*<body[^>]*>(.*)</body>.*");

        public MindMapNodeModelHarness pasteHtml(String textFromClipboard, MindMapNodeModelHarness target) {
            String cleaned = cleanClipboardHtml(textFromClipboard);
            cleaned = HtmlToolsHarness.unescapeHTMLUnicodeEntity(cleaned);
            MindMapNodeModelHarness newNode = new MindMapNodeModelHarness(cleaned);
            target.addChild(newNode);
            return newNode;
        }

        private String cleanClipboardHtml(String html) {
            if (html == null) {
                return null;
            }
            Matcher matcher = BODY_PATTERN.matcher(html);
            if (matcher.matches()) {
                return "<html><body>" + matcher.group(1).trim() + "</body></html>";
            }
            return html;
        }
    }
}
