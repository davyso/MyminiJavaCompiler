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
import miniJava.AbstractSyntaxTrees.Statement;
import miniJava.AbstractSyntaxTrees.ThisRef;
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.VarDecl;
import miniJava.AbstractSyntaxTrees.VarDeclStmt;
import miniJava.AbstractSyntaxTrees.Visitor;
import miniJava.AbstractSyntaxTrees.WhileStmt;
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
		
		Iterator<MethodDecl> methodDeclIterator = cd.methodDeclList.iterator();
		while(methodDeclIterator.hasNext()){
			MethodDecl methodDecl = methodDeclIterator.next();
			
			// TODO Handle occurrence of "main" method
			int codeAddr_main = Machine.nextInstrAddr();
			Machine.emit(Op.LOADL,-1);		// -1 on stack (= no class descriptior)
			Machine.emit(Op.LOAD, 0);		// 1 on stack (# of fields in class "Counter")
			
			
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
		
		Iterator<Statement> statementIterator = md.statementList.iterator();
		while(statementIterator.hasNext()){
			Statement statement = statementIterator.next();
			
			
			statement.visit(this, null);
			
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
		Machine.emit(Op.PUSH, 1);

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
		
//		Machine.emit(Op.PUSH, 1);
		stmt.varDecl.visit(this, null);
		stmt.initExp.visit(this, null);
		
		return null;
	}

	@Override
	public Object visitAssignStmt(AssignStmt stmt, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCallStmt(CallStmt stmt, Object arg) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBinaryExpr(BinaryExpr expr, Object arg) {
		// TODO Auto-generated method stub
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
		int int_val = Integer.parseInt(num.spelling);
		Machine.emit(Op.LOADL, int_val);
		
		// TODO: By default, print value of a int terminal. Omit if necessary.
		Machine.emit(Prim.putintnl);
		
		return null;
	}

	@Override
	public Object visitBooleanLiteral(BooleanLiteral bool, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

}
