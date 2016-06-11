/** 
* Student Name: Gary Mac Elhinney
* Student Number: 13465572 
*
* Edited: Just the same as all other included AST node files I simply added 
* methods setName() and getName() and introducted a private String variable called "name" 
**/
public class ASTSUMArith extends SimpleNode {
  private String name;
  public ASTSUMArith(int id) {
    super(id);
  }

  public ASTSUMArith(Parser p, int id) {
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
/* JavaCC - OriginalChecksum=3142966308540bb5741cef236bdac09a (do not edit this line) */
