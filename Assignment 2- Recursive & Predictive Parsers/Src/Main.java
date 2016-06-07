package Assignment2;

/**
 * Created by Gary on 15/03/2016.
 */
public class Main {
        public static void main(String[] args) {
                Grammar gr = new Grammar("grammar1.txt");
                TokenStream ts = new TokenStream("test1.txt");
                PredictiveParser pp = new PredictiveParser("LFgrammar.txt", "test1.txt");
                pp.printFirstSets();
                pp.printFollowSets();
                pp.printParseTable();
        }
}
