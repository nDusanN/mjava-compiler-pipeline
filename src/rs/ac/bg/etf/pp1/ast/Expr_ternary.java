// generated with ast extension for cup
// version 0.8
// 10/2/2026 21:46:42


package rs.ac.bg.etf.pp1.ast;

public class Expr_ternary extends Expr {

    private CondFact CondFact;
    private TernaryThen TernaryThen;
    private Expr Expr;
    private TernaryElse TernaryElse;
    private Expr Expr1;

    public Expr_ternary (CondFact CondFact, TernaryThen TernaryThen, Expr Expr, TernaryElse TernaryElse, Expr Expr1) {
        this.CondFact=CondFact;
        if(CondFact!=null) CondFact.setParent(this);
        this.TernaryThen=TernaryThen;
        if(TernaryThen!=null) TernaryThen.setParent(this);
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
        this.TernaryElse=TernaryElse;
        if(TernaryElse!=null) TernaryElse.setParent(this);
        this.Expr1=Expr1;
        if(Expr1!=null) Expr1.setParent(this);
    }

    public CondFact getCondFact() {
        return CondFact;
    }

    public void setCondFact(CondFact CondFact) {
        this.CondFact=CondFact;
    }

    public TernaryThen getTernaryThen() {
        return TernaryThen;
    }

    public void setTernaryThen(TernaryThen TernaryThen) {
        this.TernaryThen=TernaryThen;
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public TernaryElse getTernaryElse() {
        return TernaryElse;
    }

    public void setTernaryElse(TernaryElse TernaryElse) {
        this.TernaryElse=TernaryElse;
    }

    public Expr getExpr1() {
        return Expr1;
    }

    public void setExpr1(Expr Expr1) {
        this.Expr1=Expr1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(CondFact!=null) CondFact.accept(visitor);
        if(TernaryThen!=null) TernaryThen.accept(visitor);
        if(Expr!=null) Expr.accept(visitor);
        if(TernaryElse!=null) TernaryElse.accept(visitor);
        if(Expr1!=null) Expr1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(CondFact!=null) CondFact.traverseTopDown(visitor);
        if(TernaryThen!=null) TernaryThen.traverseTopDown(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
        if(TernaryElse!=null) TernaryElse.traverseTopDown(visitor);
        if(Expr1!=null) Expr1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(CondFact!=null) CondFact.traverseBottomUp(visitor);
        if(TernaryThen!=null) TernaryThen.traverseBottomUp(visitor);
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        if(TernaryElse!=null) TernaryElse.traverseBottomUp(visitor);
        if(Expr1!=null) Expr1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Expr_ternary(\n");

        if(CondFact!=null)
            buffer.append(CondFact.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(TernaryThen!=null)
            buffer.append(TernaryThen.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(TernaryElse!=null)
            buffer.append(TernaryElse.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expr1!=null)
            buffer.append(Expr1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Expr_ternary]");
        return buffer.toString();
    }
}
