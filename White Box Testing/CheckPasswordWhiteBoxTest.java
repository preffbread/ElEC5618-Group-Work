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
        // The main method works like a tiny manual test runner:
        // it executes all six independent paths one by one and prints
        // a compact summary at the end for demonstration.
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
        // Simulate the branch where a cached password already exists,
        // so the method only needs to compare the two buffers.
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
        // Same outer branch as TC1, but this time the comparison should fail
        // and the method should immediately return false.
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
        // This case forces the "decrypt failed" path by returning null.
        // It represents an incorrect password or failed decryption attempt.
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
        // This case simulates a successful decryption in the old format:
        // decrypted text already starts with "<node ", so no XML check is needed.
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
        // Here the decrypted text does not start with "<node ",
        // so the method must rely on the XML well-formedness check.
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
        // This path is identical to TC5 up to the XML check,
        // but the validation now fails and should return false.
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
        // These fields let us fully control the branch decisions from the test code
        // without having to instantiate the real FreeMind environment.
        StringBuffer password;
        String encryptedContent;
        String stubDecryptResult;
        boolean stubIsWellFormedXml;

        public boolean checkPassword(StringBuffer givenPassword) {
            // Branch 1: cached password exists, so compare directly.
            if (password != null) {
                if (!equalsBuffer(givenPassword, password)) {
                    log("Wrong password supplied (cached!=given).");
                    return false;
                }
                return true;
            }

            // Branch 2: no cached password, so simulate decryption first.
            String decryptedNode = decryptXml(encryptedContent, givenPassword);
            if (decryptedNode == null) {
                log("Wrong password supplied (deciphered text is null).");
                return false;
            }

            // Branch 3: if the decrypted result is not the old "<node " style,
            // the method falls back to an XML well-formedness check.
            if (!decryptedNode.startsWith("<node ")) {
                if (!isWellFormedXml(decryptedNode)) {
                    log("Wrong password supplied (malformed deciphered text).");
                    return false;
                }
            }

            // On success, the provided password becomes the cached password
            // for future direct comparisons.
            this.password = givenPassword;
            return true;
        }

        private String decryptXml(String encryptedString, StringBuffer pwd) {
            // In the stub version we do not decrypt anything for real.
            // The test preloads the value that should be "returned" here.
            return stubDecryptResult;
        }

        private boolean isWellFormedXml(String xml) {
            // Again, this is intentionally controlled by the test case
            // so that each white-box path can be isolated.
            return stubIsWellFormedXml;
        }

        private boolean equalsBuffer(StringBuffer left, StringBuffer right) {
            // This reproduces the character-by-character comparison idea
            // used by the original logic, instead of relying on object identity.
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
            // The stub logger makes failures visible during demos
            // without needing the original logging framework.
            System.out.println("[stub-log] " + message);
        }
    }
}
