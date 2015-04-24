package miniJava.CodeGenerator;


import java.util.Iterator;

import mJAM.Machine;
import mJAM.Machine.Op;
import miniJava.AbstractSyntaxTrees.ArrayType;
import miniJava.AbstractSyntaxTrees.AssignStmt;
import miniJava.AbstractSyntaxTrees.BaseType;
import miniJava.AbstractSyntaxTrees.BinaryExpr;
import miniJava.AbstractSyntaxTrees.BlockStmt;
import miniJava.AbstractSyntaxTrees.BooleanLiteral;
import miniJava.AbstractSyntaxTrees.CallExpr;
import miniJava.AbstractSyntaxTrees.CallStmt;
import miniJava.AbstractSyntaxTrees.ClassDecl;
import miniJava.AbstractSyntaxTrees.ClassType;
import miniJava.AbstractSyntaxTrees.Expression;
import miniJava.AbstractSyntaxTrees.FieldDecl;
import miniJava.AbstractSyntaxTrees.IdRef;
import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.IfStmt;
import miniJava.AbstractSyntaxTrees.IndexedRef;
import miniJava.AbstractSyntaxTrees.IntLiteral;
import miniJava.AbstractSyntaxTrees.LiteralExpr;
import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.AbstractSyntaxTrees.NewArrayExpr;
import miniJava.AbstractSyntaxTrees.NewObjectExpr;
import miniJava.AbstractSyntaxTrees.Operator;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.AbstractSyntaxTrees.ParameterDecl;
import miniJava.AbstractSyntaxTrees.QualifiedRef;
import miniJava.AbstractSyntaxTrees.RefExpr;
import miniJava.AbstractSyntaxTrees.Reference;
import miniJava.AbstractSyntaxTrees.Statement;
import miniJava.AbstractSyntaxTrees.ThisRef;
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.AbstractSyntaxTrees.VarDeclStmt;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.AbstractSyntaxTrees.WhileStmt;
import miniJava.SyntacticAnalyzer.TokenKind;
import mJAM.Machine.Op;
import mJAM.Machine.Reg;
import mJAM.Machine.Prim;

public class Encoder implements Visitor<Object, Object>{
	
	int patchAddr_Call_main = -1;
	
	public Encoder(){
		
	}

	public void encode(Package ast){
		ast.visit(this, null);
	}
	
	@Override
	public Object visitPackage(Package prog, Object arg) {
		
		Machine.initCodeGen();
		System.out.println("Generating code...");
		
		// TODO static fields
//		Machine.emit(Op.LOADL,0);

		
		Machine.emit(Op.LOADL, 0);
		
		// record instr addr where "main" is called
		patchAddr_Call_main = Machine.nextInstrAddr();
		
		Machine.emit(Op.CALL, Reg.CB, -1);
		Machine.emit(Op.HALT, 0, 0, 0);
		
		
		Iterator<ClassDecl> classDeclIterator = prog.classDeclList.iterator();
		while(classDeclIterator.hasNext()){
			ClassDecl classDecl = classDeclIterator.next();
			classDecl.visit(this, null);
		}
		
		
		return null;
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, Object arg) {
		
		// TODO: Apply this simple method to others
		for(FieldDecl fd : cd.fieldDeclList){
			fd.visit(this, null);
		}
		
		
		Iterator<MethodDecl> methodDeclIterator = cd.methodDeclList.iterator();
		while(methodDeclIterator.hasNext()){
			MethodDecl methodDecl = methodDeclIterator.next();
			
			// TODO Handle occurrence of "main" method
			int codeAddr_main = Machine.nextInstrAddr();
//			Machine.emit(Op.LOADL,-1);		// -1 on stack (= no class descriptior)
//			Machine.emit(Op.LOAD, 0);		// 1 on stack (# of fields in class "Counter")
			
			
			methodDecl.visit(this, null);
			
			Machine.emit(Op.RETURN, 0, 0, 1);
			
			Machine.patch(patchAddr_Call_main, codeAddr_main);
		}
		
		return null;
	}

	@Override
	public Object visitFieldDecl(FieldDecl fd, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg) {
		
//		Iterator<Statement> statementIterator = md.statementList.iterator();
//		while(statementIterator.hasNext()){
//			Statement statement = statementIterator.next();
//			
//			
//			statement.visit(this, null);
//			
//		}
		
		
		
		for(Statement s : md.statementList){
			s.visit(this, null);
		}
		
		return null;
	}

	@Override
	public Object visitParameterDecl(ParameterDecl pd, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitVarDecl(VarDecl decl, Object arg) {
		return null;
	}

	@Override
	public Object visitBaseType(BaseType type, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitClassType(ClassType type, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitArrayType(ArrayType type, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBlockStmt(BlockStmt stmt, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitVardeclStmt(VarDeclStmt stmt, Object arg) {
		
		// Allocate space for 1 word
//		Machine.emit(Op.PUSH, 1);
		stmt.varDecl.visit(this, null);
		stmt.initExp.visit(this, null);			// Push the value of expression onto "stack"
//		Machine.emit(Op.STORE, Reg.LB, 0);		// STORE 0[LB]: store a val in the local var at address d relative to frame base
		
		return null;
	}

	@Override
	public Object visitAssignStmt(AssignStmt stmt, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCallStmt(CallStmt stmt, Object arg) {
		// TODO For now, use only for PRINT statement
		if(stmt.argList.size() == 1 /*and it is a print SysOut stmt*/){
//			Machine.emit(LOADL, stmt.argList.);
			
			
//			if(((RefExpr) stmt.argList.get(0)).ref instanceof IdRef){
//				Identifier id = ((IdRef) ((RefExpr) stmt.argList.get(0)).ref).id;
				Machine.emit(Op.LOAD, Reg.LB, 3);
				Machine.emit(Prim.putintnl);
//			}
						
		}
		
		return null;
	}

	@Override
	public Object visitIfStmt(IfStmt stmt, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitWhileStmt(WhileStmt stmt, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitUnaryExpr(UnaryExpr expr, Object arg) {
		
		expr.expr.visit(this, null);
		
		switch(expr.operator.kind){
		case MINUS:
			Machine.emit(Prim.neg);
			break;
		case UNOP:
			Machine.emit(Prim.not);
			break;
		}
		return null;
	}

	@Override
	public Object visitBinaryExpr(BinaryExpr expr, Object arg) {
		expr.left.visit(this, null);
		expr.right.visit(this, null);
		
		switch(expr.operator.kind){
		case PLUS:
			Machine.emit(Prim.add);
			break;
		case MINUS:
			Machine.emit(Prim.sub);
			break;
		case TIMES:
			Machine.emit(Prim.mult);
			break;
		case DIVIDE:
			Machine.emit(Prim.div);
			break;
		case LT:
			Machine.emit(Prim.lt);
			break;
		case GT:
			Machine.emit(Prim.gt);
			break;
		case EQUAL:
			Machine.emit(Prim.eq);
			break;
		case LTEQ:
			Machine.emit(Prim.le);
			break;
		case GTEQ:
			Machine.emit(Prim.ge);
			break;
		case NOTEQUAL:
			Machine.emit(Prim.ne);
			break;
		case OR:
			Machine.emit(Prim.or);
			break;
		case AND:
			Machine.emit(Prim.and);
			break;
		}
		return null;
	}

	@Override
	public Object visitRefExpr(RefExpr expr, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCallExpr(CallExpr expr, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLiteralExpr(LiteralExpr expr, Object arg) {
		expr.lit.visit(this, null);
		return null;
	}

	@Override
	public Object visitNewObjectExpr(NewObjectExpr expr, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitNewArrayExpr(NewArrayExpr expr, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitQualifiedRef(QualifiedRef ref, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIndexedRef(IndexedRef ref, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIdRef(IdRef ref, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitThisRef(ThisRef ref, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIdentifier(Identifier id, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitOperator(Operator op, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIntLiteral(IntLiteral num, Object arg) {
		// Pushes the literal onto "stack", or address location of stack top on memory
		Machine.emit(Op.LOADL, Integer.parseInt(num.spelling));		
		return null;
	}

	@Override
	public Object visitBooleanLiteral(BooleanLiteral bool, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

}
