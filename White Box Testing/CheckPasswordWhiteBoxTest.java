/**
 * White-box test skeleton for:
 * freemind.modes.mindmapmode.EncryptedMindMapNode.checkPassword(StringBuffer)
 *
 * Target method location:
 * D:\Git\ElEC5618-Group-Work\freemind\freemind\freemind\modes\mindmapmode\EncryptedMindMapNode.java
 *
 * Cyclomatic Complexity:
 * V(G) = 6
 *
 * Decision points used in the derivation:
 * 1. if (password != null)
 * 2. if (!equals(givenPassword, password))
 * 3. if (decryptedNode == null)
 * 4. if (!decryptedNode.startsWith("<node "))
 * 5. if (!HtmlTools.getInstance().isWellformedXml(decryptedNode))
 *
 * Therefore:
 * V(G) = 5 + 1 = 6
 *
 * Planned independent paths:
 * P1: cached password exists and matches -> true
 * P2: cached password exists but does not match -> false
 * P3: no cached password and decryptXml returns null -> false
 * P4: no cached password and decrypted text starts with "<node " -> true
 * P5: no cached password, text does not start with "<node ", but XML is well formed -> true
 * P6: no cached password, text does not start with "<node ", and XML is not well formed -> false
 */
public class CheckPasswordWhiteBoxTest {

    public static void main(String[] args) {
        CheckPasswordWhiteBoxTest test = new CheckPasswordWhiteBoxTest();

        boolean t1 = test.testCachedPasswordMatches();
        boolean t2 = test.testCachedPasswordMismatch();
        boolean t3 = test.testDecryptReturnsNull();
        boolean t4 = test.testDecryptReturnsNodePrefix();
        boolean t5 = test.testDecryptReturnsWellFormedXml();
        boolean t6 = test.testDecryptReturnsMalformedXml();

        System.out.println();
        System.out.println("=== Summary ===");
        System.out.println("TC1: " + t1);
        System.out.println("TC2: " + t2);
        System.out.println("TC3: " + t3);
        System.out.println("TC4: " + t4);
        System.out.println("TC5: " + t5);
        System.out.println("TC6: " + t6);
    }

    /**
     * TC1 / Path P1:
     * password != null and equals(...) == true
     * Expected result: true
     */
    public boolean testCachedPasswordMatches() {
        StubEncryptedMindMapNode node = new StubEncryptedMindMapNode();
        node.password = new StringBuffer("abc123");

        boolean actual = node.checkPassword(new StringBuffer("abc123"));
        boolean expected = true;
        boolean passed = (actual == expected);

        System.out.println("TC1 cached password matches -> expected true, actual "
                + actual + ", passed=" + passed);
        return passed;
    }

    /**
     * TC2 / Path P2:
     * password != null and equals(...) == false
     * Expected result: false
     */
    public boolean testCachedPasswordMismatch() {
        StubEncryptedMindMapNode node = new StubEncryptedMindMapNode();
        node.password = new StringBuffer("abc123");

        boolean actual = node.checkPassword(new StringBuffer("wrong"));
        boolean expected = false;
        boolean passed = (actual == expected);

        System.out.println("TC2 cached password mismatch -> expected false, actual "
                + actual + ", passed=" + passed);
        return passed;
    }

    /**
     * TC3 / Path P3:
     * password == null and decryptXml(...) returns null
     * Expected result: false
     */
    public boolean testDecryptReturnsNull() {
        StubEncryptedMindMapNode node = new StubEncryptedMindMapNode();
        node.stubDecryptResult = null;

        boolean actual = node.checkPassword(new StringBuffer("guess"));
        boolean expected = false;
        boolean passed = (actual == expected);

        System.out.println("TC3 decrypt returns null -> expected false, actual "
                + actual + ", passed=" + passed);
        return passed;
    }

    /**
     * TC4 / Path P4:
     * password == null and decrypted text starts with "<node "
     * Expected result: true
     */
    public boolean testDecryptReturnsNodePrefix() {
        StubEncryptedMindMapNode node = new StubEncryptedMindMapNode();
        node.stubDecryptResult = "<node TEXT=\"demo\"></node>";

        boolean actual = node.checkPassword(new StringBuffer("guess"));
        boolean expected = true;
        boolean passed = (actual == expected);

        System.out.println("TC4 decrypt returns node prefix -> expected true, actual "
                + actual + ", passed=" + passed);
        return passed;
    }

    /**
     * TC5 / Path P5:
     * password == null, decrypted text does not start with "<node ",
     * and XML is well formed
     * Expected result: true
     */
    public boolean testDecryptReturnsWellFormedXml() {
        StubEncryptedMindMapNode node = new StubEncryptedMindMapNode();
        node.stubDecryptResult = "<map><node TEXT=\"demo\"></node></map>";
        node.stubIsWellFormedXml = true;

        boolean actual = node.checkPassword(new StringBuffer("guess"));
        boolean expected = true;
        boolean passed = (actual == expected);

        System.out.println("TC5 decrypt returns well-formed XML -> expected true, actual "
                + actual + ", passed=" + passed);
        return passed;
    }

    /**
     * TC6 / Path P6:
     * password == null, decrypted text does not start with "<node ",
     * and XML is not well formed
     * Expected result: false
     */
    public boolean testDecryptReturnsMalformedXml() {
        StubEncryptedMindMapNode node = new StubEncryptedMindMapNode();
        node.stubDecryptResult = "not-xml-content";
        node.stubIsWellFormedXml = false;

        boolean actual = node.checkPassword(new StringBuffer("guess"));
        boolean expected = false;
        boolean passed = (actual == expected);

        System.out.println("TC6 decrypt returns malformed XML -> expected false, actual "
                + actual + ", passed=" + passed);
        return passed;
    }

    /**
     * Minimal stub used to make each path independently controllable.
     *
     * This is not the real FreeMind implementation.
     */
    static class StubEncryptedMindMapNode {
        StringBuffer password;
        String encryptedContent;
        String stubDecryptResult;
        boolean stubIsWellFormedXml;

        public boolean checkPassword(StringBuffer givenPassword) {
            if (password != null) {
                if (!equalsBuffer(givenPassword, password)) {
                    log("Wrong password supplied (cached!=given).");
                    return false;
                }
                return true;
            }

            String decryptedNode = decryptXml(encryptedContent, givenPassword);
            if (decryptedNode == null) {
                log("Wrong password supplied (deciphered text is null).");
                return false;
            }

            if (!decryptedNode.startsWith("<node ")) {
                if (!isWellFormedXml(decryptedNode)) {
                    log("Wrong password supplied (malformed deciphered text).");
                    return false;
                }
            }

            this.password = givenPassword;
            return true;
        }

        private String decryptXml(String encryptedString, StringBuffer pwd) {
            return stubDecryptResult;
        }

        private boolean isWellFormedXml(String xml) {
            return stubIsWellFormedXml;
        }

        private boolean equalsBuffer(StringBuffer left, StringBuffer right) {
            if (left == null || right == null) {
                return left == right;
            }
            if (left.length() != right.length()) {
                return false;
            }
            for (int i = 0; i < left.length(); i++) {
                if (left.charAt(i) != right.charAt(i)) {
                    return false;
                }
            }
            return true;
        }

        private void log(String message) {
            System.out.println("[stub-log] " + message);
        }
    }
}
