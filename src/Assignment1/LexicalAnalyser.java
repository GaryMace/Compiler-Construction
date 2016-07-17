//Student Name: Gary Mac ELhinney
//Student Number: 13465572
package Assignment1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**Improvements::
 * The Trie symbol table processToken method can leave currentNode unchanged, causing it to recognise
 * Tarry as if it were Try. **RESOLVED**
 *
 * The isPrevInputValid method ought to have a flag parameter, and it ought to have
 * setIdentifierEnd wrapped into it releiving the Trie’s client of that resposibility.
 * The lexer features putback.
 *
 * The use of wasStringFinished in driver looks very ad hoc.
 *
 * Why does ‘lexer' in state 0 put back a delimiter instead of processing it immediately?
 *
 * Case 3 would not require putback if state 5’s logic were moved there.
 *
 * I don’t understand why checkForOverflow needs divide by 100 and not 10.
 *
 * The JFlex is excellent apart from the same 100 vs 10 issue.
 *
 * The Report is excellent. It is better not to put code into a report however. It would be good to see the outputs
 * for the sample inputs provided.
 *
 */
public class LexicalAnalyser {
    private static final boolean LEXICAL_ERROR = true;
    private static final int INTEGER_MAX = 65535;
    private static final int ERROR_FLAG = -1;
    private static final int ID_FLAG = 0;
    private static final int INTEGER_FLAG = 1;
    private static final int LPAR_FLAG = 2;
    private static final int RPAR_FLAG = 3;
    private static final int SEMIC_FLAG = 4;
    private static final int SKIP_TOKEN_FLAG = 5;
    private static final int END_OF_STRING_FLAG = 6;
    private static final int UNEXPECTED_CHAR_FOUND = 7;
    private Vector<Character> stringAttribute;
    private boolean isUppercaseIdentifier;
    private boolean hasLexerError;
    private TrieSymbolTable table;
    private char unexpectedChar;
    private boolean putBack;
    private int attribute;
    private int tempNum;
    private int STATE;
    private char c;

    public LexicalAnalyser() {
        hasLexerError = false;
        stringAttribute = new Vector<>();
        table = new TrieSymbolTable();
        STATE = 0;
        tempNum = 0;
        attribute = 0;
        isUppercaseIdentifier = false;
    }

    /**
     * Reads in a single character at a time and passes it to the lexer method
     *
     * @param file File to read characters from.
     */
    public void driver(String file) {
        int asciiCharFromFile;
        try {
            BufferedReader inputStream = new BufferedReader(new FileReader( file ));
            while ((asciiCharFromFile = readChar( inputStream )) != -1) {
                c = (char) asciiCharFromFile;
                checkFlag( lexer(c) );
            }
            checkFlag( lexer(' ') );  //Ensures last token in file is printed

            if (STATE == 3) {
                System.out.println("<Error, string not terminated before EOF");
            }

        } catch (IOException e) {
        }

    }

    /**
     * Stops program missing a character in the case that 'c' currently holds invalid
     * char for current DFA path
     *
     * @param br Input stream we want to read from
     * @return Either an error or ascii version of char from file
     */
    public int readChar(BufferedReader br) {
        try {
            if(putBack) {
                putBack = false;
                return c;
            } else {
                return br.read();
            }
        } catch (IOException e) {
        }
        return -1;
    }

    /**
     * DFA representation of Lexer. Branches to appropriate part of code on some character input
     *
     * @param c Char from input stream
     * @return Code explaining how current char was processed
     */
    private int lexer(char c) {
        switch (STATE) {
            case 0:
                if (isWhiteSpace(c)) {
                    STATE = 0;
                    return SKIP_TOKEN_FLAG;
                } else if (isDigit(c)) {
                    STATE = 1;
                    tempNum = c - '0';
                    attribute = tempNum;
                    return SKIP_TOKEN_FLAG;
                } else if (isUpperCase(c) || isLowerCase(c)) {
                    STATE = 2;
                    if (isUpperCase(c)) {
                        isUppercaseIdentifier = true;
                        table.processToken(c, false);
                    } else {
                        table.processToken(c, true);
                    }
                    return SKIP_TOKEN_FLAG;
                } else if (c == '"') {
                    STATE = 3;
                    return SKIP_TOKEN_FLAG;
                } else if (isDelimeter(c)) {
                    STATE = 6;
                    putBack();              //Why put back? instead of handling it immediately.
                    return SKIP_TOKEN_FLAG;
                }
                break;

            case 1:
                if (isDigit(c) && !hasLexerError) {
                    STATE = 1;
                    if (!checkForOverflow(c)) {
                        tempNum = (tempNum * 10) + (c - '0');
                        return SKIP_TOKEN_FLAG;
                    } else {
                        return SKIP_TOKEN_FLAG;
                    }
                } else if (isDigit(c) && hasLexerError) {
                    STATE = 1;
                    return SKIP_TOKEN_FLAG;
                } else {
                    STATE = 0;      //If not digit, put back and accept currNum
                    putBack();
                    if (hasLexerError) {
                        hasLexerError = false;
                        attribute = -1;
                        return INTEGER_FLAG;
                    }
                    attribute = tempNum;
                    tempNum = 0;
                    hasLexerError = false;
                    return INTEGER_FLAG;
                }

            case 2:
                if (isLowerCase(c)) {
                    if (isUppercaseIdentifier) {
                        table.processToken(c, false);
                        if(table.STATIC_INPUT_FLAG) {
                            STATE = 0;
                            attribute = ERROR_FLAG;
                            return ERROR_FLAG;
                        }
                    } else {
                        table.processToken(c, true);
                    }
                    STATE = 2;
                    return SKIP_TOKEN_FLAG;
                } else {
                    int id;
                    putBack();
                    STATE = 0;

                    if (!isUppercaseIdentifier) {
                        table.setIdentifierEnd();
                    }
                    isUppercaseIdentifier = false;
                    if ((id = table.isPrevInputValid()) != TrieSymbolTable.INVALID_WORD_ERROR) {
                        attribute = id;
                        return ID_FLAG;
                    } else {
                        attribute = ERROR_FLAG;
                        return ERROR_FLAG;
                    }
                }

            case 3:
                if (c == '~') {
                    STATE = 4;
                    return SKIP_TOKEN_FLAG;
                } else if (c == '"') {
                    STATE = 5;
                    return SKIP_TOKEN_FLAG;
                }
                stringAttribute.add(c);
                STATE = 3;
                return SKIP_TOKEN_FLAG;

            case 4:
                stringAttribute.add(c);
                STATE = 3;
                return SKIP_TOKEN_FLAG;

            case 5:
                putBack();
                STATE = 0;
                return END_OF_STRING_FLAG;

            case 6:
                STATE = 0;
                attribute = 0;
                if (c == ';') {
                    return SEMIC_FLAG;
                } else if (c == '(') {
                    return LPAR_FLAG;
                } else {
                    return RPAR_FLAG;
                }
        }

        unexpectedChar = c;
        return UNEXPECTED_CHAR_FOUND;
    }

    private boolean checkForOverflow(char c) {
        double prevNum = tempNum / 100;
        double intMax = INTEGER_MAX / 100;

        if ((prevNum * 10) + ((c - '0') / 100) > intMax) {
            hasLexerError = LEXICAL_ERROR;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Prints message describing how current char was handled
     *
     * @param flag Code used to determine what message is printed
     */
    private void checkFlag(int flag) {
        switch (flag) {
            case ERROR_FLAG:
                System.out.println("<error, " + attribute + ">");
                break;
            case ID_FLAG:
                System.out.println("<id, " + attribute + ">");
                break;
            case INTEGER_FLAG:
                System.out.println("<integer, " + attribute + ">");
                break;
            case LPAR_FLAG:
                System.out.println("<lpar, " + attribute + ">");
                break;
            case RPAR_FLAG:
                System.out.println("<rpar, " + attribute + ">");
                break;
            case SEMIC_FLAG:
                System.out.println("<semicolon, " + attribute + ">");
                break;
            case END_OF_STRING_FLAG:
                System.out.println("<string, " + stringFromVector(stringAttribute) + ">");
                stringAttribute.clear();
                break;
            case UNEXPECTED_CHAR_FOUND:
                System.out.println("Unexpected char found: " + unexpectedChar);
                break;
        }
        attribute = 0;
    }

    private String stringFromVector(Vector<Character> vector) {
        StringBuilder str = new StringBuilder();
        for (char c : vector) {
            str.append(c);
        }
        return str.toString();
    }

    private void putBack() {
        putBack = true;
    }

    private boolean isUpperCase(char c) {
        return (65 <= c && c <= 90);
    }

    private boolean isLowerCase(char c) {
        return (97 <= c && c <= 122);
    }

    private boolean isDigit(char c) {
        return (48 <= c && c <= 57);
    }

    private boolean isWhiteSpace(char c) {
        return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
    }

    private boolean isDelimeter(char c) {
        return (c == ')' || c == '(' || c == ';');
    }

}
