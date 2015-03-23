package miniJava.ContextualAnalyzer;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;

public class Checker implements Visitor<Object, Object>{
	
	private IdentificationTable idTable;
	private ErrorReporter reporter;
	
	public Checker(ErrorReporter reporter){
		this.reporter = reporter;
		this.idTable = new IdentificationTable();
		//establishStdEnvironment()?
	}
	
	public void check(Package ast){
		ast.visit(this, null);
	}

	@Override
	public Object visitPackage(Package prog, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitFieldDecl(FieldDecl fd, Object arg) {
		// TODO 
		
		fd.type.visit(this, null);	// Assign to variable of datatype Type
		
		return null;
	}

	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitParameterDecl(ParameterDecl pd, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitVarDecl(VarDecl decl, Object arg) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBooleanLiteral(BooleanLiteral bool, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	// should implement Visitor in order to access the AST
	
	// all methods should take a node from the ast. Why does
	// it need an Object in Triangle???
}
