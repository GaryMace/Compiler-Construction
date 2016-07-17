package Assignment2;

//Author: Gary Mac Elhinney
//Student Number: 13465572
public class RecursiveDecentParser {
        private static final boolean SUCCESS = true;
        private static final boolean FAILURE = false;
        private static final int START_SYMBOL = 320;
        private static final int START_POS = 0;
        private static final int EOF_SENTINEL = 99;
        private static final int EPSILON = 0;
        private int depth;
        private int rhsMatchingAttempts;
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
                this.RDP(START_SYMBOL, START_POS);
                if(NEXT_POS == 0 || ts.getTokenStream()[NEXT_POS-1][0] != EOF_SENTINEL)
                        this.invalidSentence();
                this.printResults();
        }

        private Result RDP(int NT, int inputStreamPos) {
                Result depthResult = new Result(true, inputStreamPos);
                boolean failedProduction;
                int SYMcount;
                int SYM=0;

                for(Grammar.Production pr : gr.getProductionsFor(NT)) {
                        failedProduction = false;
                        SYMcount=0;
                        NEXT_POS = inputStreamPos;
                        rhsMatchingAttempts++;
                        for(int i =0; i < pr.getRHS().size(); i++) {
                                SYM = pr.getRHS().get(i);
                                printInfoAtDepth(pr, SYM);
                                if(SYM == EPSILON) {
                                        rhsMatchingAttempts--;
                                        depthResult.nullify();
                                        SYMcount++;
                                        continue;
                                }
                                if(SYM < 320) {
                                        if(ts.getTokenStream()[NEXT_POS++][0] == SYM) {
                                                printTerminalFound();
                                                SYMcount++;
                                        } else {
                                                rhsMatchingDiscards += depthResult.getMatches();
                                                depthResult.reset();
                                                failedProduction = true;
                                                break;
                                        }
                                } else {    //It's a non-terminal #explore!
                                        depth++;
                                        Result outcome = RDP(SYM, NEXT_POS);
                                        depth--;
                                        if (outcome.getOutCome()) {
                                                SYMcount++;
                                                //if sub-problem yields a successful match
                                                if(!outcome.wasNullified()) {
                                                        depthResult.addMatch(outcome.getMatches());
                                                }
                                                NEXT_POS = outcome.getCurrPos();

                                        } else {
                                                rhsMatched = 0;   //disregard this derivation
                                                //since a sub problem could have multiple successes
                                                rhsMatchingDiscards += depthResult.getMatches();
                                                depthResult.reset();
                                                failedProduction = true;
                                                break;
                                        }
                                }
                        }
                        if(SYMcount == pr.getRHS().size() && !failedProduction) {
                                rhsMatched = depthResult.getMatches();
                                isMatchedFully(SYM);
                        }
                        //if all elements of RHS matched then one full RHS was matched
                        if(!failedProduction) {
                                depthResult.setCurrPos(NEXT_POS);
                                depthResult.setOutCome(SUCCESS);
                                return depthResult;
                        }
                }
                depthResult.setOutCome(FAILURE);
                depthResult.setCurrPos(NEXT_POS);
                return depthResult;
        }

        private void invalidSentence() {
                System.out.println("{No derivation for input sentence exists; INPUT SENTENCE INVALID}");
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
        //If we're looking for SENTINEL and current SYM is SENTINEL
        private void isMatchedFully(int SYM) {
                if(ts.getTokenStream()[NEXT_POS-1][0] == EOF_SENTINEL && SYM == EOF_SENTINEL) {
                        rhsMatched+=1;
                }
        }

        private void printResults() {
                System.out.println("{Number RHS parser tried to match: "+rhsMatchingAttempts +
                        "; Number RHS matched then discarded: "+rhsMatchingDiscards+
                        "; Number RHS matched in correct derivation: "+rhsMatched+" }");
        }

        private class Result {
                private boolean outCome;
                private boolean wasNullified;
                private int numMatchesBelow;
                private int currPos;

                public Result(boolean outCome, int currPos){
                        this.outCome = outCome;
                        this.currPos = currPos;
                        this.numMatchesBelow = 0;
                        this.wasNullified = false;
                }
                public void setOutCome(boolean outcome) {
                        this.outCome = outcome;
                }

                public boolean getOutCome() {
                        return this.outCome;
                }

                public void setCurrPos(int pos) {
                        this.currPos = pos;
                }

                public int getCurrPos() {
                        return this.currPos;
                }

                public void nullify() {
                        this.wasNullified = true;
                }

                public boolean wasNullified() {
                        return this.wasNullified;
                }

                public void addMatch(int matches) {
                        this.numMatchesBelow += matches+1;
                }

                public int getMatches() {
                        return this.numMatchesBelow;
                }

                public void reset() {
                        this.numMatchesBelow = 0;
                }
        }
}
