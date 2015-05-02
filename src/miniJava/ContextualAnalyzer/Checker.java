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
//		Iterator<ClassDecl> classDeclIterator = prog.classDeclList.iterator();
		Queue<ClassDecl> classDeclQueue = new LinkedList<ClassDecl>();

		// Add all classes decls first and then visit each class AST subtree (BFS)
//		while(classDeclIterator.hasNext()){
//			ClassDecl classDecl = classDeclIterator.next();
//			idTable.enter(classDecl.name, classDecl);
//			classDeclQueue.add(classDecl);
//		}
		
		for(ClassDecl classDecl : prog.classDeclList){
			idTable.enter(classDecl.name, classDecl);
//			classDecl.
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
			fieldType.className.decl = idTable.retrieve(fieldType.className.spelling);
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
		
		boolean isVoid = (md.type.typeKind == TypeKind.VOID);
		if(isVoid && md.returnExp != null){
			reporter.reportError("*** Void method shouldn't have return statement");
		}
		if(!isVoid && md.returnExp == null){
			reporter.reportError("*** Non-void method should have return statement");
		}
		
		
		// Parameters
		idTable.openScope();
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
		
//		idTable.enter(pd.name, pd);
		if(pd.type instanceof ClassType){
			ClassType paramType = (ClassType) pd.type;
//			paramType.className.decl = idTable.retrieve(paramType.className.spelling);
			paramType.className.decl = null;

			if(idTable.retrieve(paramType.className.spelling) instanceof ClassDecl){
				paramType.className.decl = idTable.retrieve(paramType.className.spelling);
			}
			if(paramType.className.decl == null){
				reporter.reportError("*** Undeclared class " + paramType.className.spelling);
			}
		}
		idTable.enter(pd.name, pd);

		System.out.println("\t\tParameter Declared: " + pd.name);
		
		return null;
	}

	@Override
	public Object visitVarDecl(VarDecl decl, Object arg) {
		
		Type declType = null;
		
		idTable.enter(decl.name, decl);
		
		// INT | BOOLEAN
		if(decl.type.typeKind == TypeKind.INT 
				|| decl.type.typeKind == TypeKind.BOOLEAN){
			
//			idTable.enter(decl.name, decl);
			indentNtimes(idTable.level-1);
			System.out.println("Statement Var Declared: " + decl.name);
			
		}
		// CLASS
		else if (decl.type.typeKind == TypeKind.CLASS){
			String className = ((ClassType) decl.type).className.spelling;			
			Declaration classDecl = idTable.retrieve(className);
			if(classDecl != null){
//				idTable.enter(decl.name, decl);
				indentNtimes(idTable.level-1);
				System.out.println("Statement Var Declared: " + decl.name);
			}
				
			// else error???
		}
		// ARRAY
		else if (decl.type.typeKind == TypeKind.ARRAY){
			Type arrType = ((ArrayType) decl.type).eltType;
			if(arrType.typeKind == TypeKind.INT){
//				idTable.enter(decl.name, decl);
				indentNtimes(idTable.level-1);
				System.out.println("Statement Var[] Declared: " + decl.name);
			}
			else if(arrType.typeKind == TypeKind.CLASS){
				String className = ((ClassType) arrType).className.spelling;
				Declaration classDecl = idTable.retrieve(className);
				if(classDecl != null){
//					idTable.enter(decl.name, decl);
					indentNtimes(idTable.level-1);
					System.out.println("Statement Var[] Declared: " + decl.name);
				}
				
				// else error?
			}
			else{
				// spit error?
			}
		}
		
		return decl.type;
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
		
		String localDeclName = stmt.varDecl.name;
		Type stmtVarDeclType = (Type) stmt.varDecl.visit(this, null);
		// TODO newObjExpr and newArrExpr | all other expressions
		Object stmtExprType = stmt.initExp.visit(this, localDeclName);
		
		if(stmtVarDeclType instanceof ClassType && stmtExprType instanceof ClassType){
			Identifier varDeclId = ((ClassType) stmtVarDeclType).className;
			Identifier exprId = ((ClassType) stmtExprType).className;

			if(!varDeclId.equals(exprId)){
				reporter.reportError("Variable declaration imcompatible with initial value type");
			}
		}
		
		return null;
	}

	@Override
	public Object visitAssignStmt(AssignStmt stmt, Object arg) {
		if(stmt.ref.decl == null){
			reporter.reportError("*** Stmt ref is not declared");
		}
		if(stmt.ref.decl instanceof MethodDecl){
			reporter.reportError("*** Cannot assign value to methods");
		}
//		TypeKind stmtRefType = (TypeKind) stmt.ref.visit(this, null);
		Type stmtExprType = (Type) stmt.val.visit(this, null);
		
		// TODO
		
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
				reporter.reportError("*** Variable declaration not permitted in single line else stmt");
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
				
		if(stmt.body instanceof VarDeclStmt){
			reporter.reportError("*** Variable declaration not permitted in single line while stmt");
		}
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
		
		Type l_type = (Type) expr.left.visit(this, null);
		Type r_type = (Type) expr.right.visit(this, null);
//		TypeKind leftTypeKind = null;
//		TypeKind rightTypeKind = null;
		
		if(l_type == null || r_type == null){
			reporter.reportError("*** Type can't be determined because ref wasn't declared");
			return new BaseType(TypeKind.ERROR, null);
		}
		if(l_type.typeKind == TypeKind.ERROR || r_type.typeKind == TypeKind.ERROR){
			return new BaseType(TypeKind.ERROR, null);
		}

		
//		if(expr.left.visit(this, null) instanceof Type && expr.right.visit(this, null) instanceof Type){
//			l_type = (Type) expr.left.visit(this, arg);
//			r_type = (Type) expr.right.visit(this, arg);
//			leftTypeKind = l_type.typeKind;
//			rightTypeKind = r_type.typeKind;
//		}
//		else{
//			leftTypeKind = (TypeKind) expr.left.visit(this, null);
//			rightTypeKind = (TypeKind) expr.right.visit(this, null);
//		}
		
//		if(!leftTypeKind.equals(rightTypeKind)){
//			reporter.reportError("*** Left and right expressions aren't of same typekind");
//		}
		
		if(!l_type.equals(r_type)){
			reporter.reportError("*** Both expressions aren't of the same type");
			return new BaseType(TypeKind.ERROR, null);
		}
		if(l_type instanceof ClassType && r_type instanceof ClassType){
			if(((ClassType) l_type).className.equals(((ClassType) r_type).className)){
				reporter.reportError("*** Even though both expr are class types, they aren't same Ids");
				return new BaseType(TypeKind.ERROR, null);
			}
		}
		if(l_type instanceof BaseType && r_type instanceof BaseType){
			if(l_type.typeKind != r_type.typeKind){
				reporter.reportError("*** Even though both expr are base types, they aren't same typekinds");
				return new BaseType(TypeKind.ERROR, null);
			}
		}

		
		Type resultType = null;
//		if(leftTypeKind == TypeKind.ERROR || rightTypeKind == TypeKind.ERROR){
//			return TypeKind.ERROR;
//		}
//		
//		if(leftTypeKind != TypeKind.BOOLEAN || leftTypeKind != TypeKind.INT || leftTypeKind != TypeKind.CLASS
//			|| rightTypeKind != TypeKind.BOOLEAN || rightTypeKind != TypeKind.INT || rightTypeKind != TypeKind.CLASS){
//			reporter.reportError("*** Expressions aren't base types or class types");
//		}
		
		
		
		switch(expr.operator.kind){
				
					
		// Unique to int
		case PLUS:	case MINUS:
		case TIMES:	case DIVIDE:
		case LT:	case GT:
		case LTEQ:	case GTEQ:
						
//			if((leftTypeKind == TypeKind.INT) && (rightTypeKind == TypeKind.INT)){
//				return TypeKind.INT;
//			}
//			else
//				return TypeKind.ERROR;
			if((l_type.typeKind == TypeKind.INT) && (r_type.typeKind == TypeKind.INT)){
				return TypeKind.INT;
			}
			else
				return TypeKind.ERROR;

			
		// Used for int and boolean
		case AND:	case OR:
						
//			if((leftTypeKind == TypeKind.INT) && (rightTypeKind == TypeKind.INT)){
//				return TypeKind.INT;
//			}
//			else if((leftTypeKind == TypeKind.BOOLEAN) && (rightTypeKind == TypeKind.BOOLEAN)){
//				return TypeKind.BOOLEAN;
//			}
//			else
//				return TypeKind.ERROR;
			if((l_type.typeKind == TypeKind.INT) && (r_type.typeKind == TypeKind.INT)){
				return TypeKind.INT;
			}
			else if((l_type.typeKind == TypeKind.BOOLEAN) && (r_type.typeKind == TypeKind.BOOLEAN)){
				return TypeKind.BOOLEAN;
			}
			else
				return TypeKind.ERROR;

		
		// Used for int, boolean and classes
		case EQUAL:	case NOTEQUAL:
//			if((leftTypeKind == TypeKind.INT) && (rightTypeKind == TypeKind.INT)){
//				return TypeKind.INT;
//			}
//			else if((leftTypeKind == TypeKind.BOOLEAN) && (rightTypeKind == TypeKind.BOOLEAN)){
//				return TypeKind.BOOLEAN;
//			}
//			else if((leftTypeKind == TypeKind.CLASS) && (rightTypeKind == TypeKind.CLASS)){
//				Identifier leftId = ((ClassType) l_type).className;
//				Identifier rightId = ((ClassType) r_type).className;
//				
//				if(!leftId.equals(rightId)){
//					reporter.reportError("*** Left and right expressions aren't same class type");
//				}
//
//			}
//			else
//				return TypeKind.ERROR;
			if((l_type.typeKind == TypeKind.INT) && (r_type.typeKind == TypeKind.INT)){
				return new BaseType(TypeKind.INT, null);
			}
			else if((l_type.typeKind == TypeKind.BOOLEAN) && (r_type.typeKind == TypeKind.BOOLEAN)){
				return new BaseType(TypeKind.BOOLEAN, null);
			}
			else if((l_type.typeKind == TypeKind.CLASS) && (r_type.typeKind == TypeKind.CLASS)){
				Identifier leftId = ((ClassType) l_type).className;
				Identifier rightId = ((ClassType) r_type).className;
				
				if(!leftId.equals(rightId)){
					reporter.reportError("*** Left and right expressions aren't same class type");
					return new BaseType(TypeKind.ERROR, null);
				}
				return new ClassType(leftId, null);

			}
			else
				return new BaseType(TypeKind.ERROR, null);

		}
		
		return resultType;
	}

	@Override
	public Object visitRefExpr(RefExpr expr, Object arg) {
//		expr.ref.visit(this, arg);
//		Type exprType = (Type) expr.ref.visit(this, null);
//		if(expr.ref.visit(this,null) instanceof Type){
//			
//		}
		
//		return expr.ref.visit(this, null);
		if(expr.ref.decl != null){
			return expr.ref.decl.type;
		}
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
		Type typeLitExpr = null;
		
		// BOOLEAN
		if(expr.lit.kind==TokenKind.TRUE || expr.lit.kind==TokenKind.FALSE){
//			typeLitExpr = TypeKind.BOOLEAN;
			typeLitExpr = new BaseType(TypeKind.BOOLEAN, null);
		}
		// INT
		else if (expr.lit.kind==TokenKind.NUM){
//			typeLitExpr = TypeKind.INT;
			typeLitExpr = new BaseType(TypeKind.INT, null);

		}
		else{
//			typeLitExpr = TypeKind.ERROR;
			typeLitExpr = new BaseType(TypeKind.ERROR, null);
		}

//		expr.exprType = typeLitExpr; // necessary since it is the leaf?
		return typeLitExpr;
		
		
		
	}

	@Override
	public Object visitNewObjectExpr(NewObjectExpr expr, Object arg) {
		return expr.classtype;
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
		
		if(arg != null){
			if(((String) arg).equals(ref.id.spelling)){
				reporter.reportError("*** Variable declaration cannot reference variable being declared");
			}
		}
		
		Declaration decl = idTable.retrieve(ref.id.spelling);
//		System.out.println(decl.name);
//		System.out.println(decl);
		if(decl != null){
			ref.id.decl = decl;
			return decl.type;
		}
		
		reporter.reportError("*** Reference not declared");
		return TypeKind.ERROR;
		
		
		
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
		return id.decl;
	}

	@Override
	public Object visitOperator(Operator op, Object arg) {
		return null;
	}

	@Override
	public Object visitIntLiteral(IntLiteral num, Object arg) {
		return null;
	}

	@Override
	public Object visitBooleanLiteral(BooleanLiteral bool, Object arg) {
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

