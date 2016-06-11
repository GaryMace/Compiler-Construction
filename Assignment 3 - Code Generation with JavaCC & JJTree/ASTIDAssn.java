/** 
* Student Name: Gary Mac Elhinney
* Student Number: 13465572
*
* Edited: Just the same as all other included AST node files I simply added 
* methods setName() and getName() and introducted a private String variable called "name" 
**/
public class ASTIDAssn extends SimpleNode {
  private String name;
  public ASTIDAssn(int id) {
    super(id);
  }

  public ASTIDAssn(Parser p, int id) {
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
/* JavaCC - OriginalChecksum=f7f076b6fa636fda1ab1af352feb6385 (do not edit this line) */
