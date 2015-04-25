package miniJava.ContextualAnalyzer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenKind;

public class Checker implements Visitor<Object, Object>{
	
	private IdentificationTable idTable;
	private ErrorReporter reporter;
	private int numOfMainMethod;
	
	public Checker(ErrorReporter reporter){
		this.reporter = reporter;
		this.idTable = new IdentificationTable();
		this.numOfMainMethod = 0;
		//establishStdEnvironment()?
	}
	
	public void check(Package ast){
		ast.visit(this, null);
	}
	
	public void hell(ErrorReporter reporter){
		
	}

	@Override
	public Object visitPackage(Package prog, Object arg) {
		
		// Quick traverse through the classes and its members
//		prog.classDeclList
		
		
		// TODO: New idea - if classes and its member are consistent through whole program
		// then ...
		// pre-traverse the AST and enter the classes and their members into the id table
		// There should already by predefined Hash table for level 1 and 2
		// Never closeScope or openScope for class decl or member decls. By doing this,
		// they are public by default to only package. 
		
		// 
		idTable.openScope();
		Iterator<ClassDecl> classDeclIterator = prog.classDeclList.iterator();
		Queue<ClassDecl> classDeclQueue = new LinkedList<ClassDecl>();

		// Add all classes decls first and then visit each class AST subtree (BFS)
		while(classDeclIterator.hasNext()){
			ClassDecl classDecl = classDeclIterator.next();
			idTable.enter(classDecl.name, classDecl);
			classDeclQueue.add(classDecl);
		}
		
		while(classDeclQueue.peek() != null){
			ClassDecl classDecl = classDeclQueue.remove();
			classDecl.visit(this, null);
		}
		
		// Error reporting on possible duplicates
		if(idTable.duplicateEntryExists){
			reporter.reportError("*** Duplicate entry: " + idTable.duplicateEntryName);
		}
		// Error reporting on possible missing
		if(idTable.missingEntryExists){
			reporter.reportError("*** Missing entry: " + idTable.missingEntryName);
		}

		if(numOfMainMethod == 0) 
			reporter.reportError("*** Sourcefile lacks as suitable main method");
		if(numOfMainMethod > 1)
			reporter.reportError("*** Sourcefule has more than one sutiable main method");

		idTable.closeScope();
		return null;
		
		
		// new
		
		// traverse to gather class and member decl
//		Iterator<ClassDecl> classDeclQuickIter = prog.classDeclList.iterator();
//		while(classDeclQuickIter.hasNext()){
//			ClassDecl classDecl = classDeclQuickIter.next();
//			Queue queue = new LinkedList();
//		}
//		
//		
//		Iterator<ClassDecl> classDeclIterator = prog.classDeclList.iterator();
//		while(classDeclIterator.hasNext()){
//			ClassDecl classDecl = classDeclIterator.next();
//			classDecl.visit(this, null);
//		}
//		return null;
		
		
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, Object arg) {
		
//		idTable.enter(cd.name, cd);
		System.out.println("Class Declared: " + cd.name);
		
		idTable.openScope();
		
		// Add all field decls first and then visit each field AST subtree (BFS)

		Iterator<FieldDecl> fieldDeclIterator = cd.fieldDeclList.iterator();	
		Queue<FieldDecl> fieldDeclQueue = new LinkedList<FieldDecl>();
		while(fieldDeclIterator.hasNext()){
			FieldDecl fieldDecl = fieldDeclIterator.next();
			idTable.enter(fieldDecl.name, fieldDecl);
			fieldDeclQueue.add(fieldDecl);
		}
		
//		// Error reporting on possible duplicates
//		if(idTable.duplicateEntryExists){
//			reporter.reportError("*** Duplicate entry: " + idTable.duplicateEntryName);
//		}
		
		
		while(fieldDeclQueue.peek() != null){
			FieldDecl fieldDecl = fieldDeclQueue.remove();
			fieldDecl.visit(this, null);
		}
		
		// Add all methods decls first and then visit each method AST subtree (BFS)
		Iterator<MethodDecl> methodDeclIterator = cd.methodDeclList.iterator();
//		while(methodDeclIterator.hasNext()){
//			MethodDecl methodDecl = methodDeclIterator.next();
//			methodDecl.visit(this, null);
//		}
		
		Queue<MethodDecl> methodDeclQueue = new LinkedList<MethodDecl>();
//		while(methodDeclIterator.hasNext()){
//			MethodDecl methodDecl = methodDeclIterator.next();
//			idTable.enter(methodDecl.name, methodDecl);
//			methodDeclQueue.add(methodDecl);
////			methodDecl.visit(this, null);
//		}
//		boolean noMainMethod = true;
		for(MethodDecl methodDecl : cd.methodDeclList){
			idTable.enter(methodDecl.name, methodDecl);
			
//			System.out.println(methodDecl.name.equals("main"));
			if(methodDecl.name.equals("main")) {
				numOfMainMethod++;
				
//				System.out.println(methodDecl.type.typeKind);
				
				if(methodDecl.isPrivate){
					reporter.reportError("*** Sourcefile lacks a suitable main method: should be public");
				}
				if(!methodDecl.isStatic){
					reporter.reportError("*** Sourcefile lacks a suitable main method: should be static");
				}
				if(methodDecl.parameterDeclList.size() != 1){
					reporter.reportError("*** Sourcefile lacks a sutiable main method: should only have one arg");
				}
				if(methodDecl.type.typeKind != TypeKind.VOID){
					reporter.reportError("*** Sourcefule lacks a stuiable main method: should by type void");
				}
				else{
					ArrayType mainArgType = new ArrayType(new ClassType(
							new Identifier(new Token(TokenKind.CLASS, "String")), null), null);
////					System.out.println(((ArrayType)methodDecl.parameterDeclList.get(0).type).equals(mainArgType));
//					System.out.println(((ClassType)((ArrayType)methodDecl.parameterDeclList.get(0).type).eltType).className.spelling.equals("String"));
					boolean isMainArgTypeString = ((ClassType)((ArrayType)methodDecl.parameterDeclList.get(0).type).eltType).className.spelling.equals("String");

//					if(!(methodDecl.parameterDeclList.get(0).type.equals(mainArgType))){
					if(!isMainArgTypeString){

						reporter.reportError("*** Sourcefile lacks a suitable main method: should only have 'String[] args' as arg");
					}
				}
				
			}
//				System.out.println("hello");
			
			methodDeclQueue.add(methodDecl);
		}
		
//		// Error reporting on possible duplicates
//		if(idTable.duplicateEntryExists){
//			reporter.reportError("*** Duplicate entry: " + idTable.duplicateEntryName);
//		}

		
		while(methodDeclQueue.peek() != null){
			MethodDecl methodDecl = methodDeclQueue.remove();
			methodDecl.visit(this, null);
		}
		
		
		idTable.closeScope();
		return null;
	}

	
	// TODO private and static?
	@Override
	public Object visitFieldDecl(FieldDecl fd, Object arg) {
		
//		idTable.enter(fd.name, fd);
//		System.out.println("\tField Declared: " + fd.name);
		
		if(fd.type instanceof ClassType){
			ClassType fieldType = (ClassType) fd.type;
			if(fieldType.className.decl == null){
				reporter.reportError("*** Undeclared class " + fieldType.className.spelling);
			}
		}
		if(fd.type.typeKind == TypeKind.VOID){
			// TODO: Parse error, not contextual analysis error
			reporter.reportError("*** Incorrect Type (void) for a field");
		}
		
		return null;
	}

	// TODO Allow for main method call; private and static?
	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg) {
		
//		idTable.enter(md.name, md);
		System.out.println("\tMethod Declared: " + md.name);
		
		// Parameters
		idTable.openScope();
//		Iterator<ParameterDecl> parameterDeclIterator = md.parameterDeclList.iterator();
//		while(parameterDeclIterator.hasNext()){
//			ParameterDecl parameterDecl = parameterDeclIterator.next();
//			parameterDecl.visit(this, null);
//		}
		for(ParameterDecl paramDecl : md.parameterDeclList){
			paramDecl.visit(this, null);
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
		if(pd.type instanceof ClassType){
			ClassType paramType = (ClassType) pd.type;
			if(paramType.className.decl == null){
				reporter.reportError("*** Undeclared class " + paramType.className.spelling);
			}
		}
		System.out.println("\t\tParameter Declared: " + pd.name);
		
		return null;
	}

	@Override
	public Object visitVarDecl(VarDecl decl, Object arg) {
		
		idTable.enter(decl.name, decl);
		
		// INT | BOOLEAN
		if(decl.type.typeKind == TypeKind.INT 
				|| decl.type.typeKind == TypeKind.BOOLEAN){
			
			idTable.enter(decl.name, decl);
			indentNtimes(idTable.level-1);
			System.out.println("Statement Var Declared: " + decl.name);
			
		}
		// CLASS
		else if (decl.type.typeKind == TypeKind.CLASS){
			String className = ((ClassType) decl.type).className.spelling;			
			Declaration classDecl = idTable.retrieve(className);
			if(classDecl != null){
				idTable.enter(decl.name, decl);
				indentNtimes(idTable.level-1);
				System.out.println("Statement Var Declared: " + decl.name);
			}
				
			// else error???
		}
		// ARRAY
		else if (decl.type.typeKind == TypeKind.ARRAY){
			Type arrType = ((ArrayType) decl.type).eltType;
			if(arrType.typeKind == TypeKind.INT){
				idTable.enter(decl.name, decl);
				indentNtimes(idTable.level-1);
				System.out.println("Statement Var[] Declared: " + decl.name);
			}
			else if(arrType.typeKind == TypeKind.CLASS){
				String className = ((ClassType) arrType).className.spelling;
				Declaration classDecl = idTable.retrieve(className);
				if(classDecl != null){
					idTable.enter(decl.name, decl);
					indentNtimes(idTable.level-1);
					System.out.println("Statement Var[] Declared: " + decl.name);
				}
				
				// else error?
			}
			else{
				// spit error?
			}
		}
		
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
				
		stmt.varDecl.visit(this, null);
		// TODO newObjExpr and newArrExpr | all other expressions
//		stmt.initExp.visit(this, null);
		
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
//		indentNtimes(idTable.level-1);
//		System.out.println("Applied Occur.: " + decl.name);
		
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
		TypeKind condType = (TypeKind) stmt.cond.visit(this, null);
		if(condType != TypeKind.BOOLEAN){
			reporter.reportError("*** Boolean expression expected in if stmt");
			// TODO include position in error msg
		}
		
		// then statement
		idTable.openScope();
		stmt.thenStmt.visit(this, null);
		idTable.closeScope();
		
		// else statement
		if(stmt.elseStmt != null){
			idTable.openScope();
			System.out.println(stmt.elseStmt);
			if(stmt.elseStmt instanceof VarDeclStmt){
				reporter.reportError("*** Variable declaration not permitted in else stmt");
			}
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

	// TODO Only type checking
	@Override
	public Object visitLiteralExpr(LiteralExpr expr, Object arg) {

        // exprType is parent (Expression) attribute 
		TypeKind typeLitExpr = null;
		
		// BOOLEAN
		if(expr.lit.kind==TokenKind.TRUE || expr.lit.kind==TokenKind.FALSE){
			typeLitExpr = TypeKind.BOOLEAN;
		}
		// INT
		else if (expr.lit.kind==TokenKind.NUM){
			typeLitExpr = TypeKind.INT;
		}
		else{
			typeLitExpr = TypeKind.ERROR;
		}

		expr.exprType = typeLitExpr; // necessary since it is the leaf?
		return typeLitExpr;
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
		ref.ref.visit(this, null);
		ref.id.visit(this, null);
		
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
//		System.out.println(decl);
		if(decl != null){
			ref.id.decl = decl;
			return decl;
		}
		
		reporter.reportError("*** Reference not declared");
		return null;
	}

	@Override
	public Object visitThisRef(ThisRef ref, Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIdentifier(Identifier id, Object arg) {
		Declaration decl = id.decl;
//		System.out.println(decl);
		if(decl == null){
			reporter.reportError("*** Id not declared");
		}
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

