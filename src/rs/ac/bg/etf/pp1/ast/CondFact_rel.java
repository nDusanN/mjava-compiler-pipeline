// generated with ast extension for cup
// version 0.8
// 10/2/2026 21:46:42


package rs.ac.bg.etf.pp1.ast;

public class CondFact_rel extends CondFact {

    private SimpleExpr SimpleExpr;
    private Relop Relop;
    private SimpleExpr SimpleExpr1;

    public CondFact_rel (SimpleExpr SimpleExpr, Relop Relop, SimpleExpr SimpleExpr1) {
        this.SimpleExpr=SimpleExpr;
        if(SimpleExpr!=null) SimpleExpr.setParent(this);
        this.Relop=Relop;
        if(Relop!=null) Relop.setParent(this);
        this.SimpleExpr1=SimpleExpr1;
        if(SimpleExpr1!=null) SimpleExpr1.setParent(this);
    }

    public SimpleExpr getSimpleExpr() {
        return SimpleExpr;
    }

    public void setSimpleExpr(SimpleExpr SimpleExpr) {
        this.SimpleExpr=SimpleExpr;
    }

    public Relop getRelop() {
        return Relop;
    }

    public void setRelop(Relop Relop) {
        this.Relop=Relop;
    }

    public SimpleExpr getSimpleExpr1() {
        return SimpleExpr1;
    }

    public void setSimpleExpr1(SimpleExpr SimpleExpr1) {
        this.SimpleExpr1=SimpleExpr1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(SimpleExpr!=null) SimpleExpr.accept(visitor);
        if(Relop!=null) Relop.accept(visitor);
        if(SimpleExpr1!=null) SimpleExpr1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(SimpleExpr!=null) SimpleExpr.traverseTopDown(visitor);
        if(Relop!=null) Relop.traverseTopDown(visitor);
        if(SimpleExpr1!=null) SimpleExpr1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(SimpleExpr!=null) SimpleExpr.traverseBottomUp(visitor);
        if(Relop!=null) Relop.traverseBottomUp(visitor);
        if(SimpleExpr1!=null) SimpleExpr1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CondFact_rel(\n");

        if(SimpleExpr!=null)
            buffer.append(SimpleExpr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Relop!=null)
            buffer.append(Relop.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(SimpleExpr1!=null)
            buffer.append(SimpleExpr1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CondFact_rel]");
        return buffer.toString();
    }
}
