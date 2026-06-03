// generated with ast extension for cup
// version 0.8
// 10/2/2026 21:46:42


package rs.ac.bg.etf.pp1.ast;

public class EnumDeclMore_e extends EnumDeclMore {

    public EnumDeclMore_e () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("EnumDeclMore_e(\n");

        buffer.append(tab);
        buffer.append(") [EnumDeclMore_e]");
        return buffer.toString();
    }
}
