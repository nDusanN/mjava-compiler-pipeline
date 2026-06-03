package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.mj.runtime.Code;

public class CodeGenerator extends VisitorAdaptor {
    private int mainPc;
    int getMainPc() { return this.mainPc; }

    @Override
    public void visit(MethodName methodName) {
        methodName.obj.setAdr(Code.pc);
        if (methodName.getI1().equalsIgnoreCase("main")) this.mainPc = Code.pc;
        Code.put(Code.enter);
        Code.put(methodName.obj.getLevel());
        Code.put(methodName.obj.getLocalSymbols().size());
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    @Override
    public void visit(DesignatorStatement_assign d) {
        Code.store(d.getDesignator().obj);
    }

    @Override
    public void visit(DesignatorStatement_inc d) {
        Obj obj = d.getDesignator().obj;
        if (obj.getKind() == Obj.Elem) Code.put(Code.dup2);
        Code.load(obj); Code.loadConst(1); Code.put(Code.add); Code.store(obj);
    }

    @Override
    public void visit(DesignatorStatement_dec d) {
        Obj obj = d.getDesignator().obj;
        if (obj.getKind() == Obj.Elem) Code.put(Code.dup2);
        Code.load(obj); Code.loadConst(1); Code.put(Code.sub); Code.store(obj);
    }

    @Override
    public void visit(Statement_print1 s) {
        Struct t = s.getExpr().struct;
        Code.loadConst(0);
        if (t != null && t.equals(Tab.charType)) Code.put(Code.bprint);
        else Code.put(Code.print);
    }

    @Override
    public void visit(Statement_print2 s) {
        Struct t = s.getExpr().struct;
        Code.loadConst(s.getN2());
        if (t != null && t.equals(Tab.charType)) Code.put(Code.bprint);
        else Code.put(Code.print);
    }

    @Override
    public void visit(Statement_return s) { Code.put(Code.exit); Code.put(Code.return_); }

    @Override
    public void visit(Statement_read s) {
        Obj obj = s.getDesignator().obj;
        if (obj.getType().equals(Tab.charType)) Code.put(Code.bread);
        else Code.put(Code.read);
        Code.store(obj);
    }

    @Override
    public void visit(AddopTermList_add a) {
        if (a.getAddop() instanceof Addop_plus) Code.put(Code.add);
        else Code.put(Code.sub);
    }

    @Override
    public void visit(MulopFactorList_mul m) {
        if (m.getMulop() instanceof Mulop_mul) Code.put(Code.mul);
        else if (m.getMulop() instanceof Mulop_div) Code.put(Code.div);
        else Code.put(Code.rem);
    }

    @Override
    public void visit(Factor f) {
        if (f.getUnary() instanceof Unary_minus) Code.put(Code.neg);
    }

    @Override public void visit(FactorSub_n f) { Code.loadConst(f.getN1()); }
    @Override public void visit(FactorSub_c f) { Code.loadConst(f.getC1()); }
    @Override public void visit(FactorSub_b f) { Code.loadConst(f.getB1()); }

    @Override
    public void visit(FactorSub_des f) {
        Designator d = f.getDesignator();
        if (d instanceof Designator_l) {
            Code.put(Code.arraylength);
        } else {
            Code.load(d.obj);
        }
    }

    @Override
    public void visit(FactorSub_new f) {
        Code.put(Code.newarray);
        Struct t = f.getType().struct;
        if (t != null && t.equals(Tab.charType)) Code.put(0); else Code.put(1);
    }

    @Override
    public void visit(FactorSub_meth f) {
        Obj m = f.getDesignator().obj;
        if (!m.getName().equals("ord") && !m.getName().equals("chr")) {
            Code.put(Code.call); Code.put2(m.getAdr() - Code.pc);
        }
    }

    @Override
    public void visit(FactorSub_metha f) {
        Obj m = f.getDesignator().obj;
        String name = m.getName();
        if (name.equals("ord") || name.equals("chr")) {
        } else if (name.equals("len")) {
            Code.put(Code.arraylength);
        } else {
            Code.put(Code.call); Code.put2(m.getAdr() - Code.pc);
        }
    }

    @Override
    public void visit(DesignatorArrayName d) { Code.load(d.obj); }

    @Override
    public void visit(Designator_l d) {
        Code.load(d.obj);
    }

    @Override
    public void visit(Expr_simple e) { e.struct = e.getSimpleExpr().struct; }

    private int ternaryFalseJumpAdr = -1;
    private int ternaryEndJumpAdr   = -1;

    @Override
    public void visit(CondFact_rel c) {
        Code.putFalseJump(getRelOp(c.getRelop()), 0);
        ternaryFalseJumpAdr = Code.pc - 2;
    }

    @Override
    public void visit(CondFact_single c) {
        Code.loadConst(0);
        Code.putFalseJump(Code.ne, 0);
        ternaryFalseJumpAdr = Code.pc - 2;
    }

    @Override public void visit(TernaryThen t) { }

    @Override
    public void visit(TernaryElse t) {
        Code.putJump(0);
        ternaryEndJumpAdr = Code.pc - 2;
        if (ternaryFalseJumpAdr != -1) { Code.fixup(ternaryFalseJumpAdr); ternaryFalseJumpAdr = -1; }
    }

    @Override
    public void visit(Expr_ternary e) {
        if (ternaryEndJumpAdr != -1) { Code.fixup(ternaryEndJumpAdr); ternaryEndJumpAdr = -1; }
        e.struct = e.getExpr().struct;
    }

    private int getRelOp(SyntaxNode r) {
        if (r instanceof Relop_eq) return Code.eq;
        if (r instanceof Relop_ne) return Code.ne;
        if (r instanceof Relop_lt) return Code.lt;
        if (r instanceof Relop_le) return Code.le;
        if (r instanceof Relop_gt) return Code.gt;
        if (r instanceof Relop_ge) return Code.ge;
        return Code.eq;
    }
}