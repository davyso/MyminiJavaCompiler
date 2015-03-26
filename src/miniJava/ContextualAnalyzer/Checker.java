package miniJava.ContextualAnalyzer;

import java.util.Iterator;

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
	
	public void hell(ErrorReporter reporter){
		
	}

	@Override
	public Object visitPackage(Package prog, Object arg) {
		
		idTable.openScope();
		
		Iterator<ClassDecl> classDeclIterator = prog.classDeclList.iterator();
		
		while(classDeclIterator.hasNext()){
			ClassDecl classDecl = classDeclIterator.next();
			classDecl.visit(this, null);
		}
		
		idTable.closeScope();
		return null;
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, Object arg) {
		
		idTable.enter(cd.name, cd);
		System.out.println("Class Declared: " + cd.name);
		
		// TODO
		idTable.openScope();
		
		Iterator<FieldDecl> fieldDeclIterator = cd.fieldDeclList.iterator();		
		while(fieldDeclIterator.hasNext()){
			FieldDecl fieldDecl = fieldDeclIterator.next();
			fieldDecl.visit(this, null);
		}
		
		Iterator<MethodDecl> methodDeclIterator = cd.methodDeclList.iterator();
		while(methodDeclIterator.hasNext()){
			MethodDecl methodDecl = methodDeclIterator.next();
			methodDecl.visit(this, null);
		}
		
		idTable.closeScope();
		return null;
	}

	
	// TODO private and static?
	@Override
	public Object visitFieldDecl(FieldDecl fd, Object arg) {
		
		idTable.enter(fd.name, fd);
		System.out.println("\tField Declared: " + fd.name);
		
		return null;
	}

	// TODO Allow for main method call; private and static?
	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg) {
		
		idTable.enter(md.name, md);
		System.out.println("\tMethod Declared: " + md.name);
		
		// Parameters
		idTable.openScope();
		Iterator<ParameterDecl> parameterDeclIterator = md.parameterDeclList.iterator();
		while(parameterDeclIterator.hasNext()){
			ParameterDecl parameterDecl = parameterDeclIterator.next();
			parameterDecl.visit(this, null);
		}
		
		
		// Statements
		idTable.openScope();
		Iterator<Statement> statementIterator = md.statementList.iterator();
		while(statementIterator.hasNext()){
			Statement statement = statementIterator.next();
			statement.visit(this, null);
		}
		idTable.closeScope();

		// TODO Do a check where if it is void, returnExp is null??? Statement list would prob take care of it
		// Maybe need this stuff for typechecking with return statement
//		md.returnExp;
		
		
		idTable.closeScope();
		return null;
	}

	@Override
	// TODO Finished?
	public Object visitParameterDecl(ParameterDecl pd, Object arg) {
		
		idTable.enter(pd.name, pd);
		System.out.println("\t\tParameter Declared: " + pd.name);
		
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
		
		Iterator<Statement> statementIterator = stmt.sl.iterator();
		while(statementIterator.hasNext()){
			Statement statement = statementIterator.next();
			statement.visit(this, null);
		}
		
		return null;
	}

	
	// TODO Finished? What about visitVardecl()?
	@Override
	public Object visitVardeclStmt(VarDeclStmt stmt, Object arg) {
		
		idTable.enter(stmt.varDecl.name, stmt.varDecl);
		indentNtimes(idTable.level-1);
		System.out.println("Statement Var Declared: " + stmt.varDecl.name);
		
		return null;
	}

	@Override
	public Object visitAssignStmt(AssignStmt stmt, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO Type checking in expression stmt.argList
	@Override
	public Object visitCallStmt(CallStmt stmt, Object arg) {

		Declaration decl = (Declaration) stmt.methodRef.visit(this, null);
		indentNtimes(idTable.level-1);
		System.out.println("Applied Occur.: " + decl.name);
		
		// ExprList; make sure that the placements of args correspond the datatypes
		// Use for type checking
		Iterator<Expression> exprIterator = stmt.argList.iterator();
		while(exprIterator.hasNext()){
			Expression expr = exprIterator.next();
			expr.visit(this, null);
		}
		
		return null;
	}

	@Override
	public Object visitIfStmt(IfStmt stmt, Object arg) {
		
		// TODO visit cond for type checking; must a logic expression
		stmt.cond.visit(this, null);
		
		// then statement
		idTable.openScope();
		stmt.thenStmt.visit(this, null);
		idTable.closeScope();
		
		// else statement
		if(stmt.elseStmt != null){
			idTable.openScope();
			stmt.elseStmt.visit(this, null);
			idTable.closeScope();
		}
		return null;
	}

	// TODO Finished? What about cond?
	@Override
	public Object visitWhileStmt(WhileStmt stmt, Object arg) {
		
		// TODO visit cond for type checking; must a logic expression
		stmt.cond.visit(this, null);
		
		idTable.openScope();		
		stmt.body.visit(this, null); // Statement
		idTable.closeScope();
		
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

	// Looks for id's declaration in idTable and return it.
	// If not null, it will update the decl field of the identifier
	@Override
	public Object visitIdRef(IdRef ref, Object arg) {
		Declaration decl = idTable.retrieve(ref.id.spelling);
		if(decl != null){
			ref.id.decl = decl;
		}
		return decl;
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
	
	public static void indentNtimes(int x){
		for(int i=0; i<x; i++){
			System.out.print("\t");
		}
	}
	
}

