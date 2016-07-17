package Assignment3;

import java.util.Stack;
import java.util.Vector;

public class Data {
        public static final int INT_OPERATION_FLAG = 0;
        public static final int BOOL_OPERATION_FLAG = 1;
        private int regID;
        private int lblID;
        private Vector<String> code;
        private Vector<Integer> labels;
        private Stack<Integer> regs;
        private Vector<String> INTRegs;
        private Vector<String> BOOLRegs;
        private boolean isConditional;
        private int conditionalRegIndex;        //I'm very aware how horrible this coding hack is..
        private int flag;

        public Data() {
                regID = 0;
                lblID = 0;
                code = new Vector<>();
                labels = new Vector<>();
                regs = new Stack<>();
                INTRegs = new Vector<>();
                BOOLRegs = new Vector<>();
                isConditional = false;
                conditionalRegIndex = 0;
                flag = 0;
        }

        public Vector<String> code() {
                return code;
        }

        public void addCode(String code) {
                this.code.add("["+code+"]");
        }

        public Vector<String> getCode () {
                return this.code;
        }

        public Stack<Integer> regs() {
                return regs;
        }

        public int newReg() {
                regs.push(regID++);
                return regs.peek();
        }
        public Vector<String> getINTRegs() {
                return INTRegs;
        }

        public Vector<String> getBOOLRegs() {
                return BOOLRegs;
        }

        public void addINTReg(String s) {
                this.INTRegs.add(s);
        }

        public void addBOOLReg(String s) {
                this.BOOLRegs.add(s);
        }

        public int newLabel() {
                this.labels.add(lblID);
                return lblID++;
        }

        public Vector<Integer> getLabels() {
                return this.labels;
        }

        public boolean isConditional() {
                return isConditional;
        }

        public void setConditional(boolean conditional) {
                isConditional = conditional;
        }

        public void setFlag(int flagVal) {
                this.flag = flagVal;
        }
        public int flag() {
                return this.flag;
        }

        public int getConditionalRegIndex() {
                return conditionalRegIndex;
        }

        public void setConditionalRegIndex(int conditionalRegIndex) {
                this.conditionalRegIndex = conditionalRegIndex;
        }

}
