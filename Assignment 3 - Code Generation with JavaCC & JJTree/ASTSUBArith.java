/** 
* Student Name: Gary Mac Elhinney
* Student Number: 13465572 
*
* Edited: Just the same as all other included AST node files I simply added 
* methods setName() and getName() and introducted a private String variable called "name" 
**/
public class ASTSUBArith extends SimpleNode {
  private String name;
  public ASTSUBArith(int id) {
    super(id);
  }

  public ASTSUBArith(Parser p, int id) {
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

}
/* JavaCC - OriginalChecksum=1363ed61ab7bef5cb0dfa004af2c65eb (do not edit this line) */
