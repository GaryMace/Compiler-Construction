package Assignment2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TokenStream {
        public static final int MAX_TOKENS = 1000;
        private int TKN_STREAM_INDEX;
        private int[][] tokenStream;

        public TokenStream(String fileName) {
                TKN_STREAM_INDEX = 0;
                tokenStream = new int[MAX_TOKENS][2];
                this.readTokenStream(fileName);
        }
        private void readTokenStream(String fileName) {
                String tokenLine = "";
                int previ =0;
                int i;
                try{
                        BufferedReader inputStream = new BufferedReader(new FileReader(fileName));
                        while((tokenLine = inputStream.readLine()) != null) {
                                String [] tmp = tokenLine.trim().split("\\s+");
                                if(tmp.length > 1) {
                                        for(i= 0;i < tmp.length; i++) {
                                                if(i != 0 && (previ+i)%2 == 0) {
                                                        TKN_STREAM_INDEX++;
                                                }
                                                tokenStream[TKN_STREAM_INDEX][(previ+i)%2] = Integer.parseInt(tmp[i]);
                                        }
                                        previ += i;
                                }
                        }
                        inputStream.close();
                } catch(IOException e) {
                        e.printStackTrace();
                }
        }

        public int[][] getTokenStream() {
                return this.tokenStream;
        }
}
