//Student Name: Gary Mac Elhinney
//Student Number: 13465572
%%
%standalone
%{
    private static final boolean LEXICAL_ERROR = true;
    private static final int INTEGER_MAX = 65535;
    private static final int ERROR_FLAG = -1;
    private boolean hasLexerError = false;
    private TrieSymbolTable st = new TrieSymbolTable();

    private boolean isUpperCase(char c) {
        return (65 <= c && c <= 90);
    }

     private boolean checkForOverflow() {
        double tempNum = 0;
        double intMax = INTEGER_MAX/100;

        for(char c: yytext().toCharArray()) {
            double prevNum = tempNum/100;

            if((prevNum *10)+((c-'0')/100) > intMax) {
                hasLexerError = LEXICAL_ERROR;
                break;
            } else {
                tempNum = ((prevNum*100)*10)+(c-'0');
            }
        }
        return this.hasLexerError;
     }

    private String parseYytext() {
        String removeQuotes = yytext().substring(1, yytext().length()-1);
        StringBuilder finalString = new StringBuilder();
        for(int i=0; i < removeQuotes.length(); i++) {
            if(removeQuotes.charAt(i) == '~') {
                continue;
            }
            finalString.append(removeQuotes.charAt(i));
        }
        return finalString.toString();
    }
%}
%%
[\;\)\(]                {
                            if(yycharat(0) == ';') {
                                System.out.println("<semicolon, 0>");
                            } else if(yycharat(0) == ')') {
                                System.out.println("<rpar, 0>");
                            } else {
                                System.out.println("<lpar, 0>");
                            }
                        }
[0-9]+                  {
                            if(!checkForOverflow()) {
                                System.out.println("<Integer, " + yytext()+">");
                            } else {
                                System.out.println("<Error, " + ERROR_FLAG+">");
                                hasLexerError = false;
                            }
                        }

[A-Za-z][a-z]*			{
                            if(isUpperCase(yycharat(0))) {
                                System.out.println("<id, "+st.processInput(yytext(), false)+">");
                            } else {
                                System.out.println("<id, "+st.processInput(yytext(), true)+">");
                            }
                        }

[\"](([\~](.))|[^\"])*[\"]            {
                                                 System.out.println("<string, "+parseYytext()+">");
                                      }

[\"](([\~](.))|[^\"])*            { System.out.println("<Error, string not terminated before EOF");}

[\n\r\t\s]              {;}

. 				        {System.out.println("Unexpected char found: " + yycharat(0));}