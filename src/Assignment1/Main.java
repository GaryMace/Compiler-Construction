//Student Name: Gary Mac ELhinney
//Student Number: 13465572
package Assignment1;

public class Main {

    public static void main(String[] args) {
        TrieSymbolTable table = new TrieSymbolTable();

        System.out.println("1) Trie test:");
        //table.reset();

        //Expected output : {7,12,19,25,29,36,45,48}
        String[] test = {
            "Private", "Public", "Protected", "Static", "Primary", "Integer", "Exception", "Try"
        };

        for (String str : test) {
            System.out.println(
                "Check if ID \"" + str + "\" is in Trie: " + table.processInput(str, false));
        }
        System.out.println("\n");
        for (char c : "hello".toCharArray()) {
            table.processToken(c, true);
        }
        table.setIdentifierEnd();

        //expected 5
        System.out.println(
            "Return ID for dynamic process of <hello>: " + table.isPrevInputValid());

        //expected 9 (4 additional nodes)
        System.out.println(
            "Return ID for dynamic process of <gary>: " + table.processInput("gary", true));

        //expected 8
        System.out.println(
            "Return ID for dynamic process of <gar>: " + table.processInput("gar", true));

        System.out.println(
            "Return ID for static process of <spider>: " + table.processInput("spider", false));

        System.out.println("Arthur test:"+table.processInput("Tarry", false));

        System.out.println("\n\n2)Lexical analyser test:");
        LexicalAnalyser la = new LexicalAnalyser();
        la.driver("test.txt");
    }
}
