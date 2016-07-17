package Assignment3;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class ParserDumpVisitor implements ParserVisitor {
        private int indent = 0;

        private String indentString() {
                StringBuffer sb =  new StringBuffer();
                for(int i=0; i < indent; ++i) {
                        sb.append(' ');
                }
                return sb.toString();
        }

        private void createOutputFile(Data parserData) {
                FileWriter writer;
                try {
                        writer = new FileWriter("generated-code.txt");
                        for(String code : parserData.getCode()) {
                                writer.append(code+"\n");
                        }
                        writer.close();
                } catch(IOException e) {
                        e.printStackTrace();
                }
        }

        private void checkIfConditionalCodeNeeded(Data packet) {
                Vector<Integer> labels = packet.getLabels();
                if(!packet.isConditional() && labels.size() > 0) {
                        packet.addCode("label, LBL"+labels.get(labels.size()-2)+", null, null");
                        packet.addCode("load, t"+packet.newReg()+", TRUE, null");
                        packet.addCode("jump, LBL"+packet.newLabel()+", null, null");
                        packet.addCode("label, LBL"+labels.get(labels.size()-2)+", null, null");
                        packet.addCode("load, t"+packet.regs().peek()+", FALSE, null");
                        packet.addCode("label, LBL"+labels.get(labels.size()-1)+", null, null");
                }
        }

        private Data errorChecks(ASTIDAssn node, Data childData){

                if(!childData.getBOOLRegs().contains(node.getName())
                        && !childData.getINTRegs().contains(node.getName())) {
                        childData.addCode("error, var used wasn't declared");

                } else if(childData.flag() == Data.INT_OPERATION_FLAG) {
                        if(childData.getBOOLRegs().contains(node.getName())) {
                                childData.addCode("error, INT expression assigned to BOOL var");
                        } else if(childData.getINTRegs().contains(node.getName())){
                                int reg = childData.regs().pop();
                                childData.addCode("load, "+node.getName()+", t"+reg+", null");
                        }
                } else {
                        if(node.jjtGetNumChildren() < 4
                                && childData.getBOOLRegs().contains(node.getName()) ) {
                                childData.addCode("load, "+node.getName()+", t"+childData.regs().pop()+", null");

                        } else if(node.jjtGetNumChildren() < 4
                                && !childData.getBOOLRegs().contains(node.getName()) ){
                                childData.addCode("error, INT var assigned to BOOL expression");

                        } else if(node.jjtGetNumChildren() >= 4 &&
                                childData.getBOOLRegs().contains(node.getName()) ){
                                childData.addCode("error, BOOL var assigned to conditional expression");

                        } else {
                                childData.addCode("load, "+node.getName()+", t"+childData.regs().pop()+", null");
                        }
                }
                return childData;
        }

        public Object visit(SimpleNode node, Object data) {
                return null;
        }

        public Object visit(ASTBlock node, Object data) {
                ++indent;
                Data childData = (Data) node.childrenAccept(this, data);
                --indent;
                this.createOutputFile(childData);

                return childData;
        }

        public Object visit(ASTIDAssn node, Object data) {
                Data packet = (Data) data;

                if(node.jjtGetNumChildren() < 4) {
                        packet.setConditional(false);
                } else {
                        packet.setConditional(true);
                }
                ++indent;
                Data childData = (Data) node.childrenAccept(this, data);
                --indent;

                checkIfConditionalCodeNeeded(packet);
                return errorChecks(node, childData);
        }

        public Object visit(ASTSUMArith node, Object data) {

                ++indent;
                Data childData = (Data) node.childrenAccept(this, data);
                --indent;

                int firstReg = childData.regs().pop();
                int secondReg = childData.regs().pop();

                childData.addCode("sum, t"+childData.newReg()+", t"+firstReg+", t"+secondReg);

                childData.setFlag(Data.INT_OPERATION_FLAG);
                return childData;
        }

        public Object visit(ASTSUBArith node, Object data) {
                Data packet = (Data) data;

                ++indent;
                Data childData = (Data) node.childrenAccept(this, data);
                --indent;

                int secondReg = childData.regs().pop();
                int firstReg = childData.regs().pop();

                int reg = childData.newReg();
                childData.addCode("subtract, t"+reg+", t"+firstReg+", t"+secondReg);

                packet.setFlag(Data.INT_OPERATION_FLAG);
                return childData;
        }

        public Object visit(ASTNUMFactor node, Object data) {
                Data packet = (Data) data;
                packet.setFlag(Data.INT_OPERATION_FLAG); //Do this here for assigns like "tom := 2"

                packet.addCode("loadliteral, t"+packet.newReg()+", "+node.getName()+", null");
                return packet;
        }

        public Object visit(ASTPRODTerm node, Object data) {
                ++indent;
                Data childData = (Data) node.childrenAccept(this, data);
                --indent;

                int secondReg = childData.regs().pop();
                int firstReg = childData.regs().pop();

                childData.addCode("multiply, t"+childData.newReg()+", t"+firstReg+", t"+secondReg);
                childData.setFlag(Data.INT_OPERATION_FLAG);
                return childData;
        }

        public Object visit(ASTINTID node, Object data) {
                Data packet = (Data) data;

                packet.addINTReg(node.getName());
                return packet;
        }

        public Object visit(ASTBOOLID node, Object data) {
                Data packet = (Data) data;

                packet.addBOOLReg(node.getName());
                return packet;
        }

        public Object visit(ASTIDFactor node, Object data) {
                Data packet = (Data) data;
                int newReg = packet.newReg();

                if(packet.getINTRegs().contains(node.getName())) {
                        packet.getINTRegs().add(packet.getINTRegs().indexOf(node.getName()), "t"+newReg);
                } else {
                        packet.getBOOLRegs().add(packet.getBOOLRegs().indexOf(node.getName()), "t"+newReg);
                }
                packet.addCode("load, t"+newReg+", "+node.getName()+", null");
                return packet;
        }

        public Object visit(ASTEQUComp node, Object data) {

                ++indent;
                Data packet = (Data) node.childrenAccept(this, data);
                --indent;

                int parameter2 = packet.regs().pop();
                int parameter1 = packet.regs().pop();
                packet.addCode("jumpequal, LBL" + packet.newLabel()+", t"+parameter1+" , t"+parameter2);

                packet.addCode("jump, LBL"+packet.newLabel()+", null, null");
                packet.regs().push(parameter1);
                packet.regs().push(parameter2);


                packet.setFlag(Data.BOOL_OPERATION_FLAG);
                return packet;
        }

        public Object visit(ASTGRTNComp node, Object data) {

                ++indent;
                Data packet = (Data) node.childrenAccept(this, data);
                --indent;

                int parameter2 = packet.regs().pop();
                int parameter1 = packet.regs().pop();
                packet.addCode("jumpmore, LBL"+ packet.newLabel() +", t"+parameter1+" , t"+parameter2);

                packet.addCode("jump, LBL"+ packet.newLabel() +", null, null");
                packet.regs().push(parameter1);
                packet.regs().push(parameter2);

                packet.setFlag(Data.BOOL_OPERATION_FLAG);
                return packet;

        }

        public Object visit(ASTLSTNComp node, Object data) {

                ++indent;
                Data packet = (Data) node.childrenAccept(this, data);
                --indent;

                int parameter2 = packet.regs().pop();
                int parameter1 = packet.regs().pop();
                packet.addCode("jumpless, LBL" + packet.newLabel() +", t"+parameter1+" , t"+parameter2);

                packet.addCode("jump, LBL"+ packet.newLabel() +", null, null");
                packet.regs().push(parameter1);
                packet.regs().push(parameter2);

                packet.setFlag(Data.BOOL_OPERATION_FLAG);
                return packet;
        }

        public Object visit(ASTIFCond node, Object data) {
                Data packet = (Data) data;
                Vector<Integer> labels = packet.getLabels();
                packet.addCode("label, LBL"+labels.get(labels.size()-2)+", null, null");

                ++indent;
                Data childData = (Data) node.childrenAccept(this, data);
                --indent;
                childData.setConditionalRegIndex(childData.getCode().size()-1);
                childData.addCode("jump, LBL"+ packet.newLabel() +", null, null");

                //Since we always expect conditional statements to return an integer
                childData.setFlag(Data.INT_OPERATION_FLAG);
                return childData;
        }

        public Object visit(ASTELSECond node, Object data) {
                Data packet = (Data) data;
                Vector<Integer> labels = packet.getLabels();
                packet.addCode("label, LBL"+labels.get(labels.size()-2)+", null, null");

                ++indent;
                Data childData = (Data) node.childrenAccept(this, data);
                --indent;

                this.fixConditionalRegister(childData);

                packet.addCode("label, LBL"+labels.get(labels.size()-1)+", null, null");
                return childData;
        }

        /**Gets the register from the if part of a condition that is storing the answer and changes it to be
         * the same register as the else part. (I'm not ok with this approach but I had a hard time coming up
         * with an alternative)
         *
         * @param childData The data we're dealing with
         */
        public void fixConditionalRegister(Data childData) {
                //ConditionalRegIndex: Answer register-code from the "if" part of conditional assignment
                String newRegCode = childData.getCode().get( childData.getConditionalRegIndex() );
                StringBuilder newCode = new StringBuilder();
                int i;
                for(i=0; i < newRegCode.length(); i++) {
                        char c = newRegCode.charAt(i);

                        //Finds first instance of t.. i.e the result register
                        if(c == ',') {
                                i++;
                                //sub-stringing i+1 wont work for 2+ digit registers keep reading till reg consumed
                                while(newRegCode.charAt(i) != ',') {
                                        i++;
                                }
                                newCode.append(", ");
                                newCode.append("t"+childData.regs().peek());
                                newCode.append(',');    //append the missing comma
                                break;
                        }
                        newCode.append(c);
                }

                newCode.append(newRegCode.substring(i+1, newRegCode.length()) ); //Just copy the rest of string

                childData.getCode().remove(childData.getConditionalRegIndex());
                childData.getCode().add(childData.getConditionalRegIndex(), newCode.toString());
        }
}
