package Assignment2;

//Author: Gary Mac Elhinney
//Student Number: 13465572
public class Main {
        public static void main(String[] args) {
                String grammar = "LFgrammar.txt";              //Change grammar file here
                String tokenFile = "tokenStream.txt";                //Change tokenstream file here

                Grammar gr = new Grammar(grammar);
                System.out.println("Grammar encoding in use: ");
                for(Grammar.Production p : gr. getProductions()) {
                        System.out.println(p);
                }
                System.out.println();
                TokenStream ts = new TokenStream(tokenFile);

                System.out.println("Recursive Decent Parser: ");
                RecursiveDecentParser rdp = new RecursiveDecentParser(grammar, tokenFile);
                System.out.println();

                System.out.println("////////////////////////////////////////////////////////");
                System.out.println("Predictive Parser: ");
                PredictiveParser pp = new PredictiveParser(grammar, tokenFile);
                pp.printFirstSets();
                pp.printFollowSets();
                pp.printParseTable();
        }
}
