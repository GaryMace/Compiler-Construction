/* Generated By:JJTree: Do not edit this line. ASTBOOLID.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package Assignment3;public
class ASTBOOLID extends SimpleNode {
  private String name;

  public ASTBOOLID(int id) {
    super(id);
  }

  public ASTBOOLID(Parser p, int id) {
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
/* JavaCC - OriginalChecksum=4856a8de30f26a2667ec5ec07c43dbc6 (do not edit this line) */