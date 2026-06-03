package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

import java.util.HashSet;

public class SemAnalyzer extends VisitorAdaptor {

    private boolean errorDetected = false;
    Logger log = Logger.getLogger(getClass());

    private Obj currentProgram;
    private Struct currentType;
    private int constant;
    private Struct constantType;
    private Struct boolType;
    private boolean mainHappened = false;
    private Obj currentMethod;

    private Struct currentEnumType = null;
    private String currentEnumName = null;
    private int nextEnumValue = 0;
    private HashSet<Integer> enumValues = new HashSet<>();
    int nVars;

    public SemAnalyzer() {
        Obj boolObj = Tab.find("bool");
        if (boolObj == Tab.noObj) {
            boolType = new Struct(Struct.Bool);
            Tab.insert(Obj.Type, "bool", boolType);
        } else {
            boolType = boolObj.getType();
        }
    }

    public void report_error(String message, SyntaxNode info) {
        errorDetected = true;
        StringBuilder msg = new StringBuilder(message);
        int line = (info == null) ? 0 : info.getLine();
        if (line != 0) msg.append(" na liniji ").append(line);
        log.error(msg.toString());
    }

    public void report_info(String message, SyntaxNode info) {
        StringBuilder msg = new StringBuilder(message);
        int line = (info == null) ? 0 : info.getLine();
        if (line != 0) msg.append(" na liniji ").append(line);
        log.info(msg.toString());
    }

    public boolean passed() {
        return !errorDetected;
    }

    //PROGRAM

    @Override
    public void visit(ProgramName programName) {
        currentProgram = Tab.insert(Obj.Prog, programName.getI1(), Tab.noType);
        Tab.openScope();
        report_info("Program: " + programName.getI1(), programName);
    }

    @Override
    public void visit(Program program) {
        nVars = Tab.currentScope().getnVars();
        Tab.chainLocalSymbols(currentProgram);
        Tab.closeScope();
        currentProgram = null;
        if (!mainHappened)
            report_error("Program nema main metodu", program);
    }

    //TYPE

    @Override
    public void visit(Type type) {
        Obj typeObj = Tab.find(type.getI1());
        if (typeObj == Tab.noObj) {
            report_error("Nepostojeci tip podatka: " + type.getI1(), type);
            currentType = Tab.noType;
        } else if (typeObj.getKind() != Obj.Type) {
            report_error("Simbol " + type.getI1() + " nije tip", type);
            type.struct = currentType = Tab.noType;
        } else {
            type.struct = currentType = typeObj.getType();
        }
    }

    //CONST DECLARATION

    @Override
    public void visit(ConDecl conDecl) {
        Obj conObj = Tab.find(conDecl.getI1());
        if (conObj != Tab.noObj) {
            report_error("Simbol " + conDecl.getI1() + " je vec definisan", conDecl);
        } else {
            if (constantType.assignableTo(currentType)) {
                conObj = Tab.insert(Obj.Con, conDecl.getI1(), currentType);
                conObj.setAdr(constant);
                report_info("Deklarisana konstanta: " + conDecl.getI1(), conDecl);
            } else {
                report_error("Tip konstante " + conDecl.getI1() + " se ne poklapa", conDecl);
            }
        }
    }

    @Override public void visit(Constant_n c) { constant = c.getN1(); constantType = Tab.intType; }
    @Override public void visit(Constant_c c) { constant = c.getC1(); constantType = Tab.charType; }
    @Override public void visit(Constant_b c) { constant = c.getB1(); constantType = boolType; }

    //VAR DEDCLARATION

    @Override
    public void visit(VarDecl_var varDecl_var) {
        Obj varObj = (currentMethod == null)
            ? Tab.find(varDecl_var.getI1())
            : Tab.currentScope().findSymbol(varDecl_var.getI1());
        if (varObj != null && varObj != Tab.noObj) {
            report_error("Simbol " + varDecl_var.getI1() + " je vec definisan", varDecl_var);
        } else {
            Struct actualType = (currentType.getKind() == Struct.Enum) ? Tab.intType : currentType;
            varObj = Tab.insert(Obj.Var, varDecl_var.getI1(), actualType);
            report_info("Deklarisana promenljiva: " + varDecl_var.getI1(), varDecl_var);
        }
    }

    @Override
    public void visit(VarDecl_array varDecl_array) {
        Obj varObj = (currentMethod == null)
            ? Tab.find(varDecl_array.getI1())
            : Tab.currentScope().findSymbol(varDecl_array.getI1());
        if (varObj != null && varObj != Tab.noObj) {
            report_error("Simbol " + varDecl_array.getI1() + " je vec definisan", varDecl_array);
        } else {
            Struct elemType = (currentType.getKind() == Struct.Enum) ? Tab.intType : currentType;
            varObj = Tab.insert(Obj.Var, varDecl_array.getI1(), new Struct(Struct.Array, elemType));
            report_info("Deklarisan niz: " + varDecl_array.getI1(), varDecl_array);
        }
    }

    //ENUM

    @Override
    public void visit(EnumName enumName) {
        if (Tab.find(enumName.getI1()) != Tab.noObj) {
            report_error("Enum " + enumName.getI1() + " je vec definisan", enumName);
            currentEnumType = null; currentEnumName = null; return;
        }
        currentEnumType = new Struct(Struct.Enum);
        Tab.insert(Obj.Type, enumName.getI1(), currentEnumType);
        Tab.openScope();
        currentEnumName = enumName.getI1();
        nextEnumValue = 0;
        enumValues.clear();
        report_info("Deklarisan enum: " + enumName.getI1(), enumName);
    }

    @Override
    public void visit(EnumDecl_id enumDecl_id) {
        if (currentEnumType == null) return;
        if (Tab.currentScope().findSymbol(enumDecl_id.getI1()) != null) {
            report_error("Konstanta " + enumDecl_id.getI1() + " vec definisana", enumDecl_id); return;
        }
        if (enumValues.contains(nextEnumValue)) {
            report_error("Vrednost " + nextEnumValue + " vec iskoriscena", enumDecl_id); return;
        }
        Obj con = Tab.insert(Obj.Con, enumDecl_id.getI1(), Tab.intType);
        con.setAdr(nextEnumValue);
        currentEnumType.getMembersTable().insertKey(con);
        enumValues.add(nextEnumValue);
        report_info("Enum: " + currentEnumName + "." + enumDecl_id.getI1() + "=" + nextEnumValue, enumDecl_id);
        nextEnumValue++;
    }

    @Override
    public void visit(EnumDecl_as enumDecl_as) {
        if (currentEnumType == null) return;
        if (Tab.currentScope().findSymbol(enumDecl_as.getI1()) != null) {
            report_error("Konstanta " + enumDecl_as.getI1() + " vec definisana", enumDecl_as); return;
        }
        int value = enumDecl_as.getN2();
        if (enumValues.contains(value)) {
            report_error("Vrednost " + value + " vec iskoriscena", enumDecl_as); return;
        }
        Obj con = Tab.insert(Obj.Con, enumDecl_as.getI1(), Tab.intType);
        con.setAdr(value);
        currentEnumType.getMembersTable().insertKey(con);
        enumValues.add(value);
        report_info("Enum: " + currentEnumName + "." + enumDecl_as.getI1() + "=" + value, enumDecl_as);
        nextEnumValue = value + 1;
    }

    @Override
    public void visit(EnumDecList enumDecList) {
        if (currentEnumType != null) {
            Tab.chainLocalSymbols(currentEnumType);
            Tab.closeScope();
        }
        currentEnumType = null; currentEnumName = null;
    }

    //METHOD

    @Override
    public void visit(MethodName methodName) {
        if (methodName.getI1().equalsIgnoreCase("main")) mainHappened = true;
        methodName.obj = currentMethod = Tab.insert(Obj.Meth, methodName.getI1(), Tab.noType);
        Tab.openScope();
        report_info("Deklarisana metoda: " + methodName.getI1(), methodName);
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        Tab.chainLocalSymbols(currentMethod);
        Tab.closeScope();
        currentMethod = null;
    }

    //DESIGNATORS

    @Override
    public void visit(Designator_i designator_i) {
        Obj obj = Tab.find(designator_i.getI1());
        if (obj == Tab.noObj) {
            report_error("Simbol " + designator_i.getI1() + " nije definisan", designator_i);
            designator_i.obj = Tab.noObj;
        } else {
            designator_i.obj = obj;
            if (obj.getKind() == Obj.Con)
                report_info("Upotreba konstante: " + designator_i.getI1() + "=" + obj.getAdr(), designator_i);
            else
                report_info("Upotreba promenljive: " + designator_i.getI1(), designator_i);
        }
    }

    @Override
    public void visit(Designator_d designator_d) {
        Obj enumObj = Tab.find(designator_d.getI1());
        if (enumObj == Tab.noObj) {
            report_error("Enum " + designator_d.getI1() + " nije definisan", designator_d);
            designator_d.obj = Tab.noObj; return;
        }
        if (enumObj.getKind() != Obj.Type || enumObj.getType().getKind() != Struct.Enum) {
            report_error(designator_d.getI1() + " nije enum tip", designator_d);
            designator_d.obj = Tab.noObj; return;
        }
        Obj member = enumObj.getType().getMembersTable().searchKey(designator_d.getI2());
        if (member == null) {
            report_error("Enum " + designator_d.getI1() + " nema " + designator_d.getI2(), designator_d);
            designator_d.obj = Tab.noObj;
        } else {
            designator_d.obj = member;
            report_info("Enum: " + designator_d.getI1() + "." + designator_d.getI2() + "=" + member.getAdr(), designator_d);
        }
    }

    @Override
    public void visit(Designator_l designator_l) {
        Obj arrObj = Tab.find(designator_l.getI1());
        if (arrObj == Tab.noObj) {
            report_error("Niz " + designator_l.getI1() + " nije definisan", designator_l);
            designator_l.obj = Tab.noObj; return;
        }
        if (arrObj.getType().getKind() != Struct.Array) {
            report_error(designator_l.getI1() + " nije niz", designator_l);
            designator_l.obj = Tab.noObj;
        } else {
            designator_l.obj = arrObj;
            report_info("Pristup duzini niza: " + designator_l.getI1() + ".length", designator_l);
        }
    }

    @Override
    public void visit(DesignatorArrayName designatorArrayName) {
        Obj arrObj = Tab.find(designatorArrayName.getI1());
        if (arrObj == Tab.noObj) {
            report_error("Niz " + designatorArrayName.getI1() + " nije definisan", designatorArrayName);
            designatorArrayName.obj = Tab.noObj;
        } else if (arrObj.getType().getKind() != Struct.Array) {
            report_error(designatorArrayName.getI1() + " nije niz", designatorArrayName);
            designatorArrayName.obj = Tab.noObj;
        } else {
            designatorArrayName.obj = arrObj;
        }
    }

    @Override
    public void visit(Designator_a designator_a) {
        Obj arrObj = designator_a.getDesignatorArrayName().obj;
        if (arrObj == Tab.noObj) { designator_a.obj = Tab.noObj; return; }
        if (!designator_a.getExpr().struct.equals(Tab.intType)) {
            report_error("Indeks niza mora biti int", designator_a);
            designator_a.obj = Tab.noObj;
        } else {
            designator_a.obj = new Obj(Obj.Elem, arrObj.getName(), arrObj.getType().getElemType());
            report_info("Pristup elementu: " + arrObj.getName() + "[]", designator_a);
        }
    }

    //FACTORS

    @Override public void visit(FactorSub_n f) { f.struct = Tab.intType; }
    @Override public void visit(FactorSub_c f) { f.struct = Tab.charType; }
    @Override public void visit(FactorSub_b f) { f.struct = boolType; }

    @Override
    public void visit(FactorSub_des factorSub_des) {
        factorSub_des.struct = factorSub_des.getDesignator().obj.getType();
    }

    @Override
    public void visit(FactorSub_new factorSub_new) {
        if (!factorSub_new.getExpr().struct.equals(Tab.intType)) {
            report_error("Velicina niza mora biti int", factorSub_new);
            factorSub_new.struct = Tab.noType;
        } else {
            factorSub_new.struct = new Struct(Struct.Array, currentType);
        }
    }

    @Override
    public void visit(FactorSub_expr factorSub_expr) {
        factorSub_expr.struct = factorSub_expr.getExpr().struct;
    }

    @Override
    public void visit(Factor factor) {
        if (factor.getUnary() instanceof Unary_minus) {
            if (factor.getFactorSub().struct.equals(Tab.intType))
                factor.struct = Tab.intType;
            else {
                report_error("Unary minus samo za int", factor);
                factor.struct = Tab.noType;
            }
        } else {
            factor.struct = factor.getFactorSub().struct;
        }
    }

    //ACT PARS METHODS

    @Override
    public void visit(FactorSub_metha factorSub_metha) {
        Obj methObj = factorSub_metha.getDesignator().obj;
        String name = methObj.getName();
        if (name.equals("ord")) {
            factorSub_metha.struct = Tab.intType;
        } else if (name.equals("chr")) {
            factorSub_metha.struct = Tab.charType;
        } else if (name.equals("len")) {
            factorSub_metha.struct = Tab.intType;
        } else {
            factorSub_metha.struct = methObj.getType();
        }
    }

    @Override
    public void visit(FactorSub_meth factorSub_meth) {
        factorSub_meth.struct = factorSub_meth.getDesignator().obj.getType();
    }

    //TERMS AND EXPR

    @Override
    public void visit(MulopFactorList_factor m) { m.struct = m.getFactor().struct; }

    @Override
    public void visit(MulopFactorList_mul m) {
        if (m.getMulopFactorList().struct.equals(Tab.intType) && m.getFactor().struct.equals(Tab.intType))
            m.struct = Tab.intType;
        else { report_error("Mul/div/mod operandi moraju biti int", m); m.struct = Tab.noType; }
    }

    @Override public void visit(Term term) { term.struct = term.getMulopFactorList().struct; }

    @Override
    public void visit(AddopTermList_term a) { a.struct = a.getTerm().struct; }

    @Override
    public void visit(AddopTermList_add a) {
        if (a.getAddopTermList().struct.equals(Tab.intType) && a.getTerm().struct.equals(Tab.intType))
            a.struct = Tab.intType;
        else { report_error("Add/sub operandi moraju biti int", a); a.struct = Tab.noType; }
    }

    @Override public void visit(SimpleExpr s) { s.struct = s.getAddopTermList().struct; }

    //CONDITION AND TERNARY

    @Override
    public void visit(CondFact_rel condFact_rel) {
        Struct left = condFact_rel.getSimpleExpr().struct;
        Struct right = condFact_rel.getSimpleExpr1().struct;
        if (!left.compatibleWith(right)) {
            report_error("Relacioni operandi moraju biti kompatibilni", condFact_rel);
            condFact_rel.struct = Tab.noType;
        } else {
            condFact_rel.struct = boolType;
        }
    }

    @Override
    public void visit(CondFact_single condFact_single) {
        Struct exprType = condFact_single.getSimpleExpr().struct;
        if (!exprType.equals(boolType)) {
            report_error("Uslov ternarnog mora biti bool", condFact_single);
            condFact_single.struct = Tab.noType;
        } else {
            condFact_single.struct = boolType;
        }
    }

    @Override
    public void visit(Expr_simple expr_simple) {
        expr_simple.struct = expr_simple.getSimpleExpr().struct;
    }

    @Override
    public void visit(Expr_ternary expr_ternary) {
        Struct condType = expr_ternary.getCondFact().struct;
        Struct thenType = expr_ternary.getExpr().struct;
        Struct elseType = expr_ternary.getExpr1().struct;
        if (!condType.equals(boolType)) {
            report_error("Uslov ternarnog mora biti bool", expr_ternary);
            expr_ternary.struct = Tab.noType;
        } else if (!thenType.assignableTo(elseType) && !elseType.assignableTo(thenType)) {
            report_error("Grane ternarnog moraju biti kompatibilne", expr_ternary);
            expr_ternary.struct = Tab.noType;
        } else {
            expr_ternary.struct = thenType;
        }
    }

    //DESIGNATOR STATEMENT

    @Override
    public void visit(DesignatorStatement_assign d) {
        Struct leftType = d.getDesignator().obj.getType();
        Struct rightType = d.getExpr().struct;
        if (!rightType.assignableTo(leftType))
            report_error("Tipovi u dodeli nisu kompatibilni", d);
    }
    @Override
	public void visit(DesignatorStatement_inc designatorStatement_inc) {
		int kind = designatorStatement_inc.getDesignator().obj.getKind();
		if(kind != Obj.Var && kind != Obj.Elem) 
			report_error("Inkrement neadekvatne promenljive: " + designatorStatement_inc.getDesignator().obj.getName(), designatorStatement_inc);
		else if(!designatorStatement_inc.getDesignator().obj.getType().equals(Tab.intType))
			report_error("Inkrement ne int promenljive: " + designatorStatement_inc.getDesignator().obj.getName(), designatorStatement_inc);
	}
	
	@Override
	public void visit(DesignatorStatement_dec designatorStatement_dec) {
		int kind = designatorStatement_dec.getDesignator().obj.getKind();
		if(kind != Obj.Var && kind != Obj.Elem) 
			report_error("Dekrement neadekvatne promenljive: " + designatorStatement_dec.getDesignator().obj.getName(), designatorStatement_dec);
		else if(!designatorStatement_dec.getDesignator().obj.getType().equals(Tab.intType))
			report_error("Dekrement ne int promenljive: " + designatorStatement_dec.getDesignator().obj.getName(), designatorStatement_dec);
	}
	
}