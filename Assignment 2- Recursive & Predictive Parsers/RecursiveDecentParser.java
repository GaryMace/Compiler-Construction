package Assignment2;

import java.util.ArrayList;

public class RecursiveDecentParser {
        private static final boolean SUCCESS = true;
        private static final boolean FAILURE = false;
        private static final int START_SYMBOL = 320;
        private static final int START_POS = 0;
        private static final int EOF_SENTINAL = 99;
        private static final int EPSILON = 0;
        private int depth;
        private int rhsMatchingAttempts;
        private int rhsContinuingMatches;
        private int rhsMatchingDiscards;
        private int rhsMatched;
        private static int NEXT_POS;
        private Grammar gr;
        private TokenStream ts;

        public RecursiveDecentParser(String grammarFile, String tokenFile) {
                gr = new Grammar(grammarFile);
                ts = new TokenStream(tokenFile);
                depth = 0;
                rhsMatched = 0;
                rhsMatchingAttempts = 0;
                rhsMatchingDiscards = 0;
                rhsContinuingMatches = 0;
                this.RDP(START_SYMBOL, START_POS);
                if(NEXT_POS == 0 || ts.getTokenStream()[NEXT_POS-1][0] != EOF_SENTINAL)
                        this.invalidSentence();
                this.printResults();
        }

        private Result RDP(int NT, int inputStreamPos) {
                boolean failedProduction;
                int SYMcount;

                for(Grammar.Production pr : gr.getProductionsFor(NT)) {
                        failedProduction = false;
                        NEXT_POS = inputStreamPos;
                        rhsMatchingAttempts++;
                        SYMcount = 0;
                        ArrayList<Boolean> results = new ArrayList<>();
                        for(int i =0; i < pr.getRHS().size(); i++) {
                                int SYM = pr.getRHS().get(i);
                                printInfoAtDepth(pr, SYM);
                                if(SYM == EPSILON) {
                                        rhsMatchingAttempts--;
                                        continue;
                                }
                                if(SYM < 320) {
                                        if(ts.getTokenStream()[NEXT_POS][0] == SYM) {
                                                SYMcount++;
                                                printTerminalFound();
                                                NEXT_POS++;
                                        } else {
                                                failedProduction = true;
                                                break;
                                        }
                                } else {    //It's a non-terminal #explore!
                                        depth++;
                                        Result outcome = RDP(SYM, NEXT_POS);
                                        depth--;
                                        results.add(outcome.getOutCome());
                                        if(outcome.getOutCome()) {
                                                SYMcount++;
                                                //if subproblem yields a successful match
                                                rhsContinuingMatches++;
                                                NEXT_POS = outcome.getCurrPos();
                                        } else {
                                                if(rhsContinuingMatches > 0 &&
                                                        (i-1 >=0 && results.get(i-1) == SUCCESS)) {
                                                        rhsMatched=0;   //disregard this derivation
                                                        //since a sub problem could have multiple successes
                                                        rhsMatchingDiscards+= rhsContinuingMatches;
                                                        rhsContinuingMatches = 0;
                                                }
                                                failedProduction = true;
                                                break;
                                        }
                                }
                        }
                        //if all elements of RHS matched then one full RHS was matched
                        if(SYMcount == pr.getRHS().size()) {
                                rhsMatched++;
                        }
                        if(!failedProduction) {
                                return new Result(SUCCESS, NEXT_POS);
                        }
                }
                return new Result(FAILURE, NEXT_POS);
        }

        private void invalidSentence() {
                System.out.println("{No derivation for input sentence exists; INPUT SENTENCE INVALID}");
                System.exit(1);
        }

        private void printInfoAtDepth(Grammar.Production pr, int SYM) {
                for(int i=0; i < depth; i++) {
                        System.out.print("\t");
                }
                System.out.println("{Depth: "+depth+
                        "; CurrNT: "+pr.getLHS()+
                        "; Term to find: "+ts.getTokenStream()[NEXT_POS][0]+
                        "; Curr Production: " +pr.getRHS()+
                        "; Now Exploring: "+SYM+
                        " }"
                );
        }

        private void printTerminalFound() {
                for(int i=0; i < depth; i++) {
                        System.out.print("\t");
                }
                System.out.println("{Depth: "+depth+
                        "; TERMINAL FOUND!"+
                        " }"
                );
        }

        private void printResults() {
                System.out.println("{Number RHS parser tried to match: "+rhsMatchingAttempts +
                        "; Number RHS matched then discarded: "+rhsMatchingDiscards+
                        "; Number RHS matched in correct derivation: "+rhsMatched+" }");
        }

        private class Result {
                private boolean outCome;
                private int currPos;

                public Result(boolean outCome, int currPos){
                        this.outCome = outCome;
                        this.currPos = currPos;
                }
                public boolean getOutCome() {
                        return this.outCome;
                }
                public int getCurrPos() {
                        return this.currPos;
                }
        }

}
