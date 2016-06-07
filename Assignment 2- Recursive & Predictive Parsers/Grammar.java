package Assignment2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

//ctrl alt shift t
public class Grammar {
        private ArrayList<Production> productions;
        private ArrayList<Integer> terminals;

        public Grammar(String grammarFile) {
                this.productions = new ArrayList<>();
                this.terminals = new ArrayList<>();
                this.readGrammar(grammarFile);


        }

        private void readGrammar(String fileName) {
                String production = "";
                String[] tmp;
                try{
                        BufferedReader inputStream = new BufferedReader(new FileReader(fileName));
                        while((production = inputStream.readLine()) != null) {
                                tmp = production.split(" ::=");
                                if(tmp.length == 1){
                                        this.productions.add(new Production(Integer.parseInt(tmp[0])));
                                } else {
                                        this.productions.add(new Production(Integer.parseInt(tmp[0]), tmp[1]));
                                }
                        }
                        inputStream.close();
                } catch(IOException e){
                        e.printStackTrace();
                }
        }

        public ArrayList<Integer> getTerminals() {
                return this.terminals;
        }

        public ArrayList<Production> getProductionsFor(int productionNum) {
                ArrayList<Production> tmp = new ArrayList<>();
                for(Production p : this.productions) {
                        if(p.getLHS() == productionNum) {
                                tmp.add(p);
                        }
                }
                return tmp;
        }
        public ArrayList<Production> getProductions() {
                return this.productions;
        }

        public class Production {
                //list of productions, deal with epsilon last for each production. return 0
                private ArrayList<Integer> rules;
                private int lhsNT;

                public Production(int lhsNt){
                        this.rules = new ArrayList<>();
                        rules.add(0);
                        this.lhsNT = lhsNt;
                }

                public Production(int lhsNT, String rhsProds) {
                        this.rules = new ArrayList<>();
                        this.lhsNT = lhsNT;
                        this.getRules(rhsProds);
                        this.getTerminals();
                }

                private void getRules(String production) {
                        String[] prods = production.trim().split("\\s+");

                        for(int i=0; i < prods.length; i++) {
                                rules.add(Integer.parseInt(prods[i]));
                        }
                }

                private void getTerminals() {
                        for(int SYM : rules) {
                                if(isTerminal(SYM) && !terminals.contains(SYM)) {
                                        terminals.add(SYM);
                                }
                        }
                }
                private boolean isTerminal(int SYM) {
                        if(SYM < 320) {
                                return true;
                        }
                        return false;
                }

                public ArrayList<Integer> getRHS() {
                        return this.rules;
                }

                public int getLHS() {
                        return this.lhsNT;
                }

                @Override
                public String toString() {
                        return lhsNT+" ::= "+ getRHS();
                }
        }
}
