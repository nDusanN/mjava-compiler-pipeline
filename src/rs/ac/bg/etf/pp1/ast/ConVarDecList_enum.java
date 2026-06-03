// generated with ast extension for cup
// version 0.8
// 10/2/2026 21:46:42


package rs.ac.bg.etf.pp1.ast;

public class ConVarDecList_enum extends ConVarDecList {

    private ConVarDecList ConVarDecList;
    private EnumDecList EnumDecList;

    public ConVarDecList_enum (ConVarDecList ConVarDecList, EnumDecList EnumDecList) {
        this.ConVarDecList=ConVarDecList;
        if(ConVarDecList!=null) ConVarDecList.setParent(this);
        this.EnumDecList=EnumDecList;
        if(EnumDecList!=null) EnumDecList.setParent(this);
    }

    public ConVarDecList getConVarDecList() {
        return ConVarDecList;
    }

    public void setConVarDecList(ConVarDecList ConVarDecList) {
        this.ConVarDecList=ConVarDecList;
    }

    public EnumDecList getEnumDecList() {
        return EnumDecList;
    }

    public void setEnumDecList(EnumDecList EnumDecList) {
        this.EnumDecList=EnumDecList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConVarDecList!=null) ConVarDecList.accept(visitor);
        if(EnumDecList!=null) EnumDecList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConVarDecList!=null) ConVarDecList.traverseTopDown(visitor);
        if(EnumDecList!=null) EnumDecList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConVarDecList!=null) ConVarDecList.traverseBottomUp(visitor);
        if(EnumDecList!=null) EnumDecList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConVarDecList_enum(\n");

        if(ConVarDecList!=null)
            buffer.append(ConVarDecList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(EnumDecList!=null)
            buffer.append(EnumDecList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConVarDecList_enum]");
        return buffer.toString();
    }
}
