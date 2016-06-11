/** 
* Student Name: Gary Mac Elhinney
* Student Number: 134655722 
*
* Edited: Just the same as all other included AST node files I simply added 
* methods setName() and getName() and introducted a private String variable called "name" 
**/
public class ASTLSTNComp extends SimpleNode {
  private String name;
  public ASTLSTNComp(int id) {
    super(id);
  }

  public ASTLSTNComp(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }

  public void setName(String s) {
    this.name =s;
  }
  public String getName() {
    return this.name;
  }
}
/* JavaCC - OriginalChecksum=7a2f982379457c8d4250d96ca9edd02f (do not edit this line) */
