/** 
* Student Name: Gary Mac Elhinney
* Student Number: 13465572 
*
* Edited: Just the same as all other included AST node files I simply added 
* methods setName() and getName() and introducted a private String variable called "name" 
**/
public class ASTPRODTerm extends SimpleNode {
  private String name;
  public ASTPRODTerm(int id) {
    super(id);
  }

  public ASTPRODTerm(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }

  public void setName(String s) {
    this.name = s;
  }

  public String getName() {
    return name;
  }
}
/* JavaCC - OriginalChecksum=4a2542a21965208f9d81bfcb3d122ddf (do not edit this line) */
