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
        // This runner executes the three-layer interaction checks from bottom to top.
        // The goal is to show not only isolated behavior, but also how the classes
        // cooperate along one simplified content-processing chain.
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
        // Layer 1 only:
        // confirm that the lowest utility layer can convert HTML content into plain text.
        String html = "<html><body><b>Hello</b> world</body></html>";
        String actual = HtmlToolsHarness.htmlToPlain(html);
        boolean passed = assertEquals("Hello world", actual.trim());
        printResult("TC1 HtmlTools should convert HTML node text to plain text", passed);
        return passed;
    }

    public boolean testMindMapNodeUsesHtmlToolsForPlainTextContent() {
        // Layer 2 depends on Layer 1:
        // the node model should not parse HTML itself, but delegate to HtmlTools.
        MindMapNodeModelHarness node = new MindMapNodeModelHarness("<html><body><i>Task</i></body></html>");
        boolean passed = assertEquals("Task", node.getPlainTextContent().trim());
        printResult("TC2 MindMapNodeModel should use HtmlTools when returning plain text", passed);
        return passed;
    }

    public boolean testMindMapNodeSaveTxtUsesProcessedPlainText() throws Exception {
        // Still Layer 2:
        // once plain text is derived, saveTXT should write the cleaned content, not raw HTML.
        MindMapNodeModelHarness node = new MindMapNodeModelHarness("<html><body><b>Line</b></body></html>");
        StringWriter writer = new StringWriter();
        node.saveTXT(writer, 1);
        boolean passed = assertEquals("    Line\n", writer.toString());
        printResult("TC3 MindMapNodeModel saveTXT() should export processed plain text", passed);
        return passed;
    }

    public boolean testPasteActorCreatesNodeFromHtmlClipboard() {
        // Layer 3 begins here:
        // paste logic receives clipboard HTML and turns it into a newly inserted node.
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
        // This test follows the dependency chain one step further:
        // paste creates the node, then the node exposes plain text using HtmlTools.
        MindMapNodeModelHarness root = new MindMapNodeModelHarness("root");
        PasteActorHarness actor = new PasteActorHarness();

        MindMapNodeModelHarness pasted = actor.pasteHtml("<html><body>Alpha &amp; Beta</body></html>", root);
        boolean passed = assertEquals("Alpha & Beta", pasted.getPlainTextContent().trim());
        printResult("TC5 Pasted node should expose plain text through MindMapNodeModel + HtmlTools", passed);
        return passed;
    }

    public boolean testBottomUpChainFromPasteToNodeToPlainTextExport() throws Exception {
        // This is the full end-to-end bottom-up path:
        // PasteActor -> MindMapNodeModel -> HtmlTools -> exported plain text.
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
        // The console trace is intentionally simple so each test can be explained live.
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
        // Lowest layer: utility behavior used by the upper harness classes.
        private static final Pattern HTML_PATTERN = Pattern.compile("(?is).*<\\s*html.*?>.*");
        private static final Pattern TAG_PATTERN = Pattern.compile("(?is)<[^>]+>");

        private HtmlToolsHarness() {
        }

        public static boolean isHtmlNode(String text) {
            // Decide whether content should follow the HTML-processing path.
            return text != null && HTML_PATTERN.matcher(text).matches();
        }

        public static String removeHtmlTagsFromString(String text) {
            // Strip the markup and keep only the visible text content.
            if (text == null) {
                return null;
            }
            return TAG_PATTERN.matcher(text).replaceAll("");
        }

        public static String htmlToPlain(String text) {
            // This represents the core service used by MindMapNodeModel:
            // HTML input is cleaned, plain text input is returned unchanged.
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
            // Included as a small helper to mirror the kind of text sanitising
            // the original project performs before storing text.
            if (text == null) {
                return null;
            }
            return text.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
        }

        public static String unescapeHTMLUnicodeEntity(String text) {
            // Simplified entity restoration used after clipboard HTML is read.
            return unescapeBasicEntities(text);
        }

        private static String unescapeBasicEntities(String text) {
            // Only a minimal set is needed for the selected interaction scenarios.
            if (text == null) {
                return null;
            }
            return text.replace("&amp;", "&")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">");
        }
    }

    static class MindMapNodeModelHarness {
        // Middle layer: node model behavior that depends on HtmlTools for text conversion.
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
            // This is the key dependency of the node model on HtmlTools.
            return HtmlToolsHarness.htmlToPlain(text);
        }

        public void addChild(MindMapNodeModelHarness child) {
            // Keeps the parent-child relationship visible for paste tests.
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
            // Mimics the idea of exporting the node as indented plain text.
            for (int i = 0; i < depth; i++) {
                writer.write("    ");
            }
            writer.write(getPlainTextContent());
            writer.write("\n");
        }
    }

    static class PasteActorHarness {
        // Highest layer: receives clipboard-style input and inserts a new node.
        private static final Pattern BODY_PATTERN = Pattern.compile("(?is).*<body[^>]*>(.*)</body>.*");

        public MindMapNodeModelHarness pasteHtml(String textFromClipboard, MindMapNodeModelHarness target) {
            // The paste sequence is intentionally simple:
            // clean the clipboard HTML, unescape basic entities, create a node, insert it.
            String cleaned = cleanClipboardHtml(textFromClipboard);
            cleaned = HtmlToolsHarness.unescapeHTMLUnicodeEntity(cleaned);
            MindMapNodeModelHarness newNode = new MindMapNodeModelHarness(cleaned);
            target.addChild(newNode);
            return newNode;
        }

        private String cleanClipboardHtml(String html) {
            // Keep only the body part to model the preprocessing done before insertion.
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
