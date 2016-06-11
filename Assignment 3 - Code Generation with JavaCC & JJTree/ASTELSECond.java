/** 
* Student Name: Gary Mac Elhinney
* Student Number: 13465572
*
* Edited: Just the same as all other included AST node files I simply added 
* methods setName() and getName() and introducted a private String variable called "name" 
**/
public class ASTELSECond extends SimpleNode {
  private String name;
  public ASTELSECond(int id) {
    super(id);
  }

  public ASTELSECond(Parser p, int id) {
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
/* JavaCC - OriginalChecksum=be04916c340c2c6c67534095f4bd7972 (do not edit this line) */
