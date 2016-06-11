/** 
* Student Name: Gary Mac Elhinney
* Student Number: 13465572
*
* Edited: Just the same as all other included AST node files I simply added 
* methods setName() and getName() and introducted a private String variable called "name" 
**/
public class ASTIDFactor extends SimpleNode {
  private String name;
  public ASTIDFactor(int id) {
    super(id);
  }

  public ASTIDFactor(Parser p, int id) {
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
    return this.name;
  }
}
/* JavaCC - OriginalChecksum=4cbc8bf03076cd4340142e9164686ff2 (do not edit this line) */
