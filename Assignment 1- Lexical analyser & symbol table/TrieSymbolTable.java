//Student Name: Gary Mac ELhinney
//Student Number: 13465572
package Assignment1;

import java.util.HashMap;

public class TrieSymbolTable {
    public static final int INVALID_WORD_ERROR = -1;
    private static final boolean FAILED_TO_FIND_STATIC_INPUT = true;
    private static boolean STATIC_INPUT_FLAG = false;
    private final Node first = new Node(0, false); //Permanent reference to root node of Trie
    private Node currentNode;
    private int nodeIndex;

    public TrieSymbolTable() {
        currentNode = first;
        nodeIndex = 1;

        initializeIdentifiers();
    }

    /**
     * (Dynamic: adds char to Trie at unique index if it doesn't already exist)
     * (Static: Moves through Trie provided a path exists)
     *
     * @param c    token to process
     * @param flag either dynamic or static processing
     */
    public void processToken(char c, boolean flag) {
        if (flag) {  //dynamic
            if (currentNode.getChildren().get(c) == null) {
                currentNode = addNewNodeToTrie(currentNode, c, false);
            } else {
                currentNode = currentNode.getChildren().get(c);
            }
        } else {  //static
            if (currentNode.getChildren().get(c) != null) {
                currentNode = currentNode.getChildren().get(c);
            } else {
                STATIC_INPUT_FLAG = FAILED_TO_FIND_STATIC_INPUT;
            }
        }
    }

    /**
     * (2)
     * Dynamic: return Node ID if already accepting else make it accepting then return ID
     * Static: return -1 if currentNode is NOT accepting
     *
     * @return ID of acceptin node or error
     */
    public int isPrevInputValid() {
        if (currentNode.isIdentifier()) {
            int id = currentNode.getIndex();
            currentNode = first;
            return id;
        } else {
            currentNode = first;
            return INVALID_WORD_ERROR;
        }
    }

    /**
     * Calls methods (1) and (2) to process full string input
     *
     * @param str  String to process
     * @param flag Either dynamic or static
     * @return
     */
    public int processInput(String str, boolean flag) {
        currentNode = first;
        STATIC_INPUT_FLAG = false;

        for (char c : str.toCharArray()) {
            processToken(c, flag);
        }
        if (!STATIC_INPUT_FLAG && flag) {
            this.setIdentifierEnd();
        }

        return isPrevInputValid();
    }

    //Adds a new node to the Trie with given parameters
    private Node addNewNodeToTrie(Node currentNode, char c, boolean isIdentifier) {
        Node newNode = new Node(this.nodeIndex++, isIdentifier);
        currentNode.addChild(c, newNode);

        return newNode;
    }

    //Leave all nodes except root node for garbage collector to deal with.
    //No reference remains for them in memory
    public void reset() {
        first.getChildren().clear();
        nodeIndex = 1;
        initializeIdentifiers();
    }

    //Make currentNode accepting for current Input
    public int setIdentifierEnd() {
        this.currentNode.setIdentifier();
        return this.currentNode.getIndex();
    }

    private void initializeIdentifiers() {
        String[] identifiers = {
            "Private",
            "Public",
            "Protected",
            "Static",
            "Primary",
            "Integer",
            "Exception",
            "Try"
        };
        for (String str : identifiers) {
            this.processInput(str, true);
        }
    }

    //Node class used to simulate states in Trie
    private class Node {
        private int index;
        private HashMap<Character, Node> children;
        private boolean isIdentifier;

        public Node(int index, boolean identifier) {
            this.isIdentifier = identifier;
            this.children = new HashMap<>();
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public boolean isIdentifier() {
            return isIdentifier;
        }

        public void setIdentifier() {
            this.isIdentifier = true;
        }

        public HashMap<Character, Node> getChildren() {
            return this.children;
        }

        public void addChild(char token, Node child) {
            children.put(token, child);
        }
    }
}