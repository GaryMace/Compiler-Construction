package Assignment2;

import java.util.*;

//Author: Gary Mac Elhinney
//Student Number: 13465572
public class PredictiveParser {
        private static final int EOF_SENTINEL = 99;
        private static final int START_SYMBOL = 320;
        private static final int EPSILON = 0;
        private boolean IS_LL1 = true;
        private HashMap<Integer, ArrayList<Integer>> followSets;
        private HashMap<Integer, ArrayList<Integer>> firstSets;
        private HashMap<Integer, Grammar.Production[]> parseTable;
        private ArrayList<Integer> terminals;
        private ArrayList<Integer> NT;
        private Grammar gr;
        private TokenStream ts;

        public PredictiveParser(String grammarFile, String tokenStreamFile) {
                gr = new Grammar(grammarFile);
                ts = new TokenStream(tokenStreamFile);
                followSets = new HashMap<>();
                firstSets = new HashMap<>();
                parseTable = new HashMap<>();
                terminals = new ArrayList<>();
                NT = new ArrayList<>();

                this.getFirstSets();
                this.prepareFollowSetHashtable(this.gr);
                this.buildParsingTable();
                this.isLL1Grammar();
                this.nonrecursivePredictiveParser();
        }

        private void getFirstSets() {
                this.initializeSet(firstSets);
                for(Grammar.Production pr : gr.getProductions()) {
                        firstSets.put(pr.getLHS(), this.addAllUnique(
                                firstSets.get(pr.getLHS()), firstSet(pr.getRHS(), gr)));
                        //Populates an ArrayList of NT's for a later loop, See buildParseTable()
                        if (!this.NT.contains(pr.getLHS())) {
                                this.NT.add(pr.getLHS());
                        }
                }
        }

        private ArrayList<Integer> firstSet(ArrayList<Integer> SYMSequence , Grammar gr) {
                ArrayList<Integer> firstSet = new ArrayList<>();

                if(SYMSequence.size() == 0) {
                        firstSet.add(EPSILON);
                        return firstSet;
                } else if(isTerminal(SYMSequence.get(0))) {
                        //Builds a list of all the terminals USED, not necessarily all terminals in grammar
                        if(!terminals.contains(SYMSequence.get(0)) && SYMSequence.get(0) != EPSILON) {
                                terminals.add(SYMSequence.get(0));
                        }
                        firstSet.add(SYMSequence.get(0));
                        return firstSet;
                } else {
                        int NT = SYMSequence.get(0);
                        ArrayList<Integer> F2 = new ArrayList<>();

                        for(Grammar.Production pr : gr.getProductionsFor(NT)) {
                                F2 = this.addAllUnique(firstSet(pr.getRHS(), gr), F2);
                        }
                        if(!F2.contains(EPSILON)) {
                                return F2;
                        } else {
                                F2.remove(F2.indexOf(EPSILON));
                                F2 = this.addAllUnique(firstSet(
                                        new ArrayList<Integer>(
                                                SYMSequence.subList(1, SYMSequence.size())), gr), F2);
                                return F2;
                        }
                }

        }

        private void prepareFollowSetHashtable(Grammar gr) {
                HashMap<Integer, ArrayList<Integer>> inheritors = new HashMap<>();
                HashMap<Integer, ArrayList<Integer>> followSet = new HashMap<>();
                this.initializeSet(followSet);

                for(Grammar.Production pr : gr.getProductions()) {
                        ArrayList<Integer> R = pr.getRHS();
                        int len = R.size();
                        for(int i=0; i < len; i++) {
                                int SYM = R.get(i);
                                if(!isTerminal(SYM)) {
                                        for(int F : firstSet(new ArrayList<>(R.subList(i+1, len)), gr)) {
                                                if(F == EPSILON) {
                                                        //Prevents NullPointerAcceptions, didn't use initializeSet()
                                                        //method since not all NT prods will be put in here
                                                        if(inheritors.get(pr.getLHS()) == null) {
                                                                inheritors.put(pr.getLHS(), new ArrayList<>());
                                                        }
                                                        inheritors.get(pr.getLHS()).add(SYM);
                                                } else {
                                                        if(!followSet.get(SYM).contains(F))
                                                                followSet.get(SYM).add(F);
                                                }
                                        }
                                }
                        }
                }
                boolean idle = false;
                while(!idle){
                        idle = true;
                        for(Map.Entry<Integer, ArrayList<Integer>> NT1  : inheritors.entrySet()) {
                                for(int NT2 : NT1.getValue()) {
                                        for(int T : followSet.get(NT1.getKey())) {
                                                if(!followSet.get(NT2).contains(T)) {
                                                        followSet.get(NT2).add(T);
                                                        idle = false;
                                                }
                                        }
                                }
                        }
                }
                this.followSets = followSet;
        }

        /**Builds parsing table. In the case that the grammar is not LL(1) an error is outputted but the contents at
         * Table[X,t] are overridden. The only alternative to this would be creating a 3-D data structure but that's
         * not necessary to detect duplicate entries into the parse table.
         */
        private void buildParsingTable() {
                HashMap<Integer,Grammar.Production[]> parseTable = new HashMap<>();
                this.initializeParseTable(parseTable);
                //Both getting firstSets and followSets adds terminals into here, so we're just ordering it
                Collections.sort(terminals);
                //for each NT
                for(int X : NT) {
                        //Each production related to that NT
                        for(Grammar.Production X1 : gr.getProductionsFor(X)) {
                                if(!isTerminal(X1.getRHS().get(0))) {
                                        for(int t : firstSets.get(X1.getRHS().get(0))) {
                                                int pos = terminals.indexOf(t)+1;

                                                if(t == EPSILON) {
                                                        continue;
                                                }
                                                //Checks if curr cell in parseTable is already filled
                                                this.isLL1(parseTable, X1.getLHS(), pos);
                                                parseTable.get(X1.getLHS())[pos] = X1;
                                        }
                                } else if(X1.getRHS().get(0) != EPSILON){
                                        parseTable.get(X1.getLHS())[terminals.indexOf(X1.getRHS().get(0))+1] = X1;
                                } else {
                                        if((firstSets.get(X1.getLHS())).contains(EPSILON)) {
                                                for(int t1 : followSets.get(X1.getLHS())) {
                                                        int pos = terminals.indexOf(t1)+1;
                                                        this.isLL1(parseTable, X1.getLHS(), pos);

                                                        parseTable.get(X1.getLHS())[pos] = X1;
                                                }
                                        }
                                }
                        }
                }
                this.parseTable = parseTable;
        }

        private void nonrecursivePredictiveParser() {
                Stack<Integer> buffer = new Stack<>();
                buffer.push(START_SYMBOL);
                int prodChoices = 0;
                int NEXT_POS = 0;
                int a = ts.getTokenStream()[NEXT_POS++][0];
                int T = buffer.peek();

                while(T != EOF_SENTINEL) {
                        if(T == a) {
                                buffer.pop();
                                System.out.println("|Found: "+a+" |");
                                a = ts.getTokenStream()[NEXT_POS++][0];
                        } else if( isTerminal(T)) {
                                System.out.println("{No-valid derivation exists for input sentence}");
                                break;
                        } else if( parseTable.get(T)[terminals.indexOf(a)+1] == null) {
                                System.out.println("{No-valid derivation exists for input sentence}");
                                break;
                        } else if( parseTable.get(T)[terminals.indexOf(a)+1] != null) {
                                System.out.println("{Curr Prod: "+parseTable.get(T)[terminals.indexOf(a)+1] +
                                        "; To Find: "+a+"}");
                                prodChoices++;
                                buffer.pop();
                                ArrayList<Integer> tmp = new ArrayList(
                                        parseTable.get(T)[terminals.indexOf(a)+1].getRHS());
                                Collections.reverse(tmp);
                                for(int t : tmp) {
                                        if(t != EPSILON) {
                                                buffer.push(t);
                                        } else {
                                                prodChoices--;
                                        }
                                }
                        }
                        T = buffer.peek();
                }
                System.out.println("{Num Productions in correct derivation: "+prodChoices+"}\n");
        }

        //Helper methods //////////////////////////////////////////////

        //This is just used to initialize the ArrayLists at the positions in a hash map
        private void initializeSet(HashMap<Integer, ArrayList<Integer>> Set) {
                for(Grammar.Production pr : gr.getProductions()) {
                        Set.put(pr.getLHS(), new ArrayList<>());
                }
        }

        /**Does the same as above but we have arrays at each positions instead of ArrayLists since it's easier
         *to insert entries and keep track as to what terminal each production belongs to
         */
        private void initializeParseTable(HashMap<Integer, Grammar.Production[]> table) {
                for(Grammar.Production pr : gr.getProductions()) {
                        table.put(pr.getLHS(), new Grammar.Production[gr.getTerminals().size()]);
                }
        }

        //Takes in two lists, only adds unique elements from List1 to List2
        private ArrayList<Integer> addAllUnique(ArrayList<Integer> toAdd, ArrayList<Integer> current) {
                for(int SYM : toAdd) {
                        if(!current.contains(SYM)) {
                                current.add(SYM);
                        }
                }
                return current;
        }

        private boolean isTerminal(int SYM) {
                return (SYM < 320);
        }

        private void isLL1(HashMap<Integer, Grammar.Production[]> map, int LHS, int pos) {
                if(map.get(LHS)[pos] != null) {
                        System.out.println("Warning! Duplicate entries in Parse Table, Grammar is not LL(1)!");
                        IS_LL1 = false;
                }
        }

        private void isLL1Grammar() {
                System.out.print("Valid LL1 Grammar: ");
                if(IS_LL1)
                        System.out.print("YES");
                else
                        System.out.print("NO");
                System.out.println("\n");
        }

        //Public API /////////////////////////////////////////////////
        public void printParseTable() {
                System.out.println("Parse Table: ");
                System.out.print("---------");
                for(int t : terminals) {
                        if(t < 10) {
                                System.out.print(t+"----------------------");
                        } else {
                                System.out.print(t+"---------------------");
                        }
                }
                System.out.println();
                for(int i=0; i< parseTable.size(); i++) {
                        Grammar.Production[] tmp = parseTable.get(START_SYMBOL+i);
                        System.out.print((START_SYMBOL+i)+" |\t ");
                        for(int j=1; j < tmp.length; j++) {
                                if(tmp[j] == null) {
                                        System.out.print(String.format("%-22s|", " "));
                                } else {
                                        System.out.print(String.format("%-22s|", tmp[j]));
                                }
                        }
                        System.out.println();
                }
        }

        public void printFirstSets() {
                System.out.println("First Sets");
                for(HashMap.Entry<Integer, ArrayList<Integer>> entry : firstSets.entrySet()) {
                        System.out.println(entry.getKey()+" ::= "+ entry.getValue());
                }
                System.out.println();
        }

        public void printFollowSets() {
                System.out.println("Follow Sets");
                for(HashMap.Entry<Integer, ArrayList<Integer>> entry : followSets.entrySet()) {
                        System.out.println(entry.getKey()+" ::= "+ entry.getValue());
                }
                System.out.println();
        }
}
