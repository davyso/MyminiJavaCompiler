package miniJava.SyntacticAnalyzer;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;

public class Parser {
	
	private Scanner scanner;
	private ErrorReporter reporter;
	private Token token;
	private boolean trace = true;
	
	public Parser(Scanner scanner, ErrorReporter reporter) {
		this.scanner = scanner;
		this.reporter = reporter;
	}

	
	class SyntaxError extends Error {
		private static final long serialVersionUID = 1L;
	}
	
	
	public void parse() {
		token = scanner.scan();
		try {
			ClassDeclList prog = parseProgram();
			AST ast = new Package(prog, null);
			ASTDisplay ast_disp = new ASTDisplay();
			ast_disp.showTree(ast);
		}
		catch (SyntaxError e) {}
	}

	
	// NEW Program: Method return type changed from void to ClassDeclList
	private ClassDeclList parseProgram(){
		ClassDeclList cdl = new ClassDeclList();
		while(token.kind == TokenKind.LINECOMMENT || token.kind == TokenKind.BLOCKCOMMENT){
			token = scanner.scan();
		}
		while(token.kind == TokenKind.CLASS){
//			System.out.println("loop");
			ClassDecl cd = parseClassDeclaration();
			cdl.add(cd);
		}
		accept(TokenKind.EOT);
		return cdl;
	}

	//NEW ClassDeclaration: Method return type changed from void to ClassDecl
	private ClassDecl parseClassDeclaration(){
		
		FieldDeclList fdl = new FieldDeclList();
		MethodDeclList mdl = new MethodDeclList();

		
		accept(TokenKind.CLASS);
		String class_name = token.spelling;
		accept(TokenKind.ID);
		accept(TokenKind.LCURLY);
		while(token.kind == TokenKind.LINECOMMENT || token.kind == TokenKind.BLOCKCOMMENT){
			token = scanner.scan();
		}

		switch(token.kind){
		case RCURLY:
			acceptIt();
			return new ClassDecl(class_name, fdl, mdl, null);
		
		case PUBLIC: case PRIVATE: case STATIC: case INT: case BOOLEAN: case ID: case VOID:
//			FieldDeclList fdl = new FieldDeclList();
//			MethodDeclList mdl = new MethodDeclList();
			
			
			// Declarators
			boolean isPrivate = false;
			boolean isStatic = false;
			Type t = null;
			String field_name;
			if(token.kind == TokenKind.PUBLIC || token.kind == TokenKind.PRIVATE){
				isPrivate = (token.kind == TokenKind.PRIVATE);
				acceptIt();
			}
			if(token.kind == TokenKind.STATIC){
				isStatic = true;
//				System.out.println("hello");
				acceptIt();
			}
			if(token.kind == TokenKind.VOID) {
				acceptIt();
				t = new BaseType(TypeKind.VOID, null);
			} else {
				System.out.println(token.spelling);

				t = parseType();
			}

			
			
			field_name = token.spelling;
			accept(TokenKind.ID);
	
			if(token.kind == TokenKind.LPAREN){
				
				ParameterDeclList pdl = new ParameterDeclList();
				StatementList sl = new StatementList();
				Expression expr = null;
				FieldDecl fd = new FieldDecl(isPrivate, isStatic, t, field_name, null);

				acceptIt();
				if(token.kind == TokenKind.INT 
				|| token.kind == TokenKind.BOOLEAN 
				|| token.kind == TokenKind.ID){
					pdl = parseParameterList();
				}
				accept(TokenKind.RPAREN);
				accept(TokenKind.LCURLY);
				while(isStartOfStatement(token.kind)){
					Statement s = parseStatement();
					sl.add(s);
				}
				
				if(token.kind == TokenKind.RETURN){
					acceptIt();
					
//					expr = parseExpression();
					expr = parseS();

					
					accept(TokenKind.SEMICOLON);
				}
				accept(TokenKind.RCURLY);
				
				// NEW ClassDeclaration: Stores MethodDeclList
				MethodDecl md = new MethodDecl(fd, pdl, sl, expr, null);
				mdl.add(md);

			}
			else if(token.kind == TokenKind.SEMICOLON){
				acceptIt();
				// NEW ClassDeclaration: Stores FieldDeclList
				FieldDecl fd = new FieldDecl(isPrivate, isStatic, t, field_name, null);
				fdl.add(fd);
			}
			while(isStartOfDeclarators(token.kind)){
				
				
				boolean next_isPrivate = false;
				boolean next_isStatic = false;
				Type next_t = null;
				String next_field_name;
				if(token.kind == TokenKind.PUBLIC || token.kind == TokenKind.PRIVATE) {
					next_isPrivate = (token.kind == TokenKind.PRIVATE);
					acceptIt();
				}
				if(token.kind == TokenKind.STATIC) {
					next_isStatic = true;
					acceptIt();
				}
				if(token.kind == TokenKind.VOID) {
					acceptIt();
					next_t = new BaseType(TypeKind.VOID, null);
				} else {
					next_t = parseType();
				}

				
				
				next_field_name = token.spelling;
				accept(TokenKind.ID);
				if(token.kind == TokenKind.LPAREN){
					
					
					ParameterDeclList next_pdl = new ParameterDeclList();
					StatementList next_sl = new StatementList();
					Expression next_expr = null;
					FieldDecl next_fd = new FieldDecl(next_isPrivate, next_isStatic, next_t, next_field_name, null);

					
					acceptIt();
					if(token.kind == TokenKind.INT 
					|| token.kind == TokenKind.BOOLEAN 
					|| token.kind == TokenKind.ID){
						next_pdl = parseParameterList();
					}
					accept(TokenKind.RPAREN);
					accept(TokenKind.LCURLY);
					while(isStartOfStatement(token.kind)){
						Statement next_s = parseStatement();
						next_sl.add(next_s);
					}
					
					if(token.kind == TokenKind.RETURN){
						acceptIt();
						next_expr = parseExpression();
						accept(TokenKind.SEMICOLON);
					}
					accept(TokenKind.RCURLY);

					// NEW ClassDeclaration: Stores MethodDeclList
					MethodDecl next_md = new MethodDecl(next_fd, next_pdl, next_sl, next_expr, null);
					mdl.add(next_md);
					
				}else if(token.kind == TokenKind.SEMICOLON){
					acceptIt();
					// NEW ClassDeclaration: Stores FieldDeclList
					FieldDecl next_fd = new FieldDecl(next_isPrivate, next_isStatic, next_t, next_field_name, null);
					fdl.add(next_fd);
					
				}
			}
			accept(TokenKind.RCURLY);
			// NEW Statement: Returns ClassDecl obj
			return new ClassDecl(class_name, fdl, mdl, null);
		default:
			// error msg
			return null;
		}
	}
	
	
	
	// change name to start of declarators
	private boolean isStartOfDeclarators(TokenKind tk){
		while(token.kind == TokenKind.LINECOMMENT || token.kind == TokenKind.BLOCKCOMMENT){
			token = scanner.scan();
		}
		return (token.kind == TokenKind.PUBLIC
				|| token.kind == TokenKind.PRIVATE
				|| token.kind == TokenKind.STATIC
				|| token.kind == TokenKind.INT
				|| token.kind == TokenKind.BOOLEAN
				|| token.kind == TokenKind.ID
				|| token.kind == TokenKind.VOID);
	}
	
	// NEW REMOVE? Declarators: Method return type changed from void to 
	private void parseDeclarators() {
		if(token.kind == TokenKind.PUBLIC || token.kind == TokenKind.PRIVATE)
			acceptIt();
		if(token.kind == TokenKind.STATIC)
			acceptIt();
		if(token.kind == TokenKind.VOID) {
			acceptIt();
			return;
		} else {
			parseType();
		}
	}
	
	
	// NEW Type: Method return type changed from void to Type
	private Type parseType() throws SyntaxError {
		Token tk = null;
		switch(token.kind) {
		case INT:
			acceptIt();
			if(token.kind == TokenKind.LBRACK){
				acceptIt();
				accept(TokenKind.RBRACK);
				// NEW Type: Returns ArrayType obj (int), not void
				return new ArrayType(new BaseType(TypeKind.INT, null), null);
			}
			// NEW Type: Returns BaseType obj (int), not void
			return new BaseType(TypeKind.INT, null);
		case ID:
			tk = token;
			acceptIt();
			if(token.kind == TokenKind.LBRACK){
				acceptIt();
				accept(TokenKind.RBRACK);
				// NEW Type: Returns ArrayType obj (class), not void
				return new ArrayType(new ClassType(new Identifier(tk), null), null);
			}
			// NEW Type: Returns ClassType obj, not void
			return new ClassType(new Identifier(tk), null);
		case BOOLEAN:
			acceptIt();
			// NEW Type: Returns BaseType obj (boolean), not void
			return new BaseType(TypeKind.BOOLEAN, null);
		default:
			parseError("Invalud Term - expecting INT, ID, BOOLEAN but found " + token.kind);
			
			// NEW Type: Default return type is null
			return null;
		}
	}
	
	// NEW ParameterList: Method return type changed from void to ParameterDeclList
	private ParameterDeclList parseParameterList() throws SyntaxError{
		
		// NEW ParameterList: Instantiate ParameterDeclList variable pdl
		ParameterDeclList pdl = new ParameterDeclList();
		
		// NEW ParameterList: Result of parseType added to pdl
		Type t = parseType();
		String param_name = token.spelling;

		ParameterDecl pd = new ParameterDecl(t, param_name, null);
		pdl.add(pd);
		
		accept(TokenKind.ID);
		while(token.kind == TokenKind.COMMA){
			acceptIt();
			
			// NEW ParameterList: Results of additional parseType added to pdl
			String next_type_name = token.spelling;
			Type t_next = parseType();
			ParameterDecl pd_next = new ParameterDecl(t_next, next_type_name, null);
			pdl.add(pd_next);
			
			accept(TokenKind.ID);
		}
		
		// NEW ParameterList: Returns ParameterDeclList, not void
		return pdl;
	}
	
	// NEW ArgumentList: Method return type changed from void to ExprList
	private ExprList parseArgumentList() {
		ExprList el = new ExprList();
//		Expression e = parseExpression();
		Expression e = parseS();

		el.add(e);
		while(token.kind == TokenKind.COMMA){
			acceptIt();
//			Expression e_next = parseExpression();
			Expression e_next = parseS();

			
			el.add(e_next);
		}
		// NEW ArgumentList: Returns ExprList obj	
		return el;
	}
	
	
	// NEW Reference: Method return type changed from void to Reference
	private Reference parseReference(){
		Reference temp = null;
		
		if(token.kind == TokenKind.THIS){
			temp = new ThisRef(null);
			acceptIt();
		}
		else if(token.kind == TokenKind.ID){
			temp = new IdRef(new Identifier(token), null);
			acceptIt();
		}
		
		while(token.kind == TokenKind.PERIOD){
			acceptIt();
			temp = new QualifiedRef(temp, new Identifier(token), null);
			accept(TokenKind.ID);
		}
		
		// NEW Reference: Forced left associativity through while loop
		return temp;
	}
	
	// NEW IxReference: Method return type changed from void to Reference
	private Reference parseIxReference() {
		Reference temp = parseReference();
//		Reference temp = null;
		
		
		// NEW IxReference: Change grammar
		if(token.kind == TokenKind.LBRACK){
			acceptIt();
			
//			Expression tempExpr = parseExpression();
			Expression tempExpr = parseS();

			
			accept(TokenKind.RBRACK);
			// NEW IxReference: Returns IndexedRef obj
			return new IndexedRef(temp, tempExpr, null);
		}
		
//		if(token.kind == TokenKind.THIS){
//			temp = new ThisRef(null);
//			acceptIt();
//		}
//		else if(token.kind == TokenKind.ID){
//			temp = new IdRef(new Identifier(token), null);
//			acceptIt();
//		}
//		while(token.kind == TokenKind.PERIOD || token.kind == TokenKind.LBRACK){
//			//
////			acceptIt();
////			temp = new QualifiedRef(temp, new Identifier(token), null);
////			accept(TokenKind.ID);
//			//
//			
//			if(token.kind == TokenKind.PERIOD){
//				acceptIt();
//				temp = new QualifiedRef(temp, new Identifier(token), null);
//				accept(TokenKind.ID);
//			}
//			else{	// TokenKind.LBRACK
//				acceptIt();
//				Expression tempExpr = parseExpression();
//				accept(TokenKind.RBRACK);
//
//				temp = new IndexedRef(temp, tempExpr, null);
//			}
//		}
		
		
		// NEW IxReference: Returns Reference obj (IdRef, QualifiedRef, ThisRef)
		return temp;
	}
	
	// NEW Statement: Method return type changed from void to StatementList
	private Statement parseStatement(){
//		System.out.println(token.kind);
		Statement tempStmt = null;
		switch(token.kind){
		
		// { Statement* }
		case LCURLY:
			StatementList sl = new StatementList();
			
			acceptIt();
			while(token.kind == TokenKind.LCURLY 
				|| token.kind == TokenKind.INT
				|| token.kind == TokenKind.ID
				|| token.kind == TokenKind.THIS
				|| token.kind == TokenKind.IF
				|| token.kind == TokenKind.WHILE){
				Statement s = parseStatement();
				sl.add(s);
			}
			accept(TokenKind.RCURLY);
			// NEW Statement: Returns BlockStatement obj
			return new BlockStmt(sl, null);
		
		case INT:
//			System.out.println(token.kind + "  Here!");
			BaseType bt = new BaseType(TypeKind.INT, null);
			Type t = bt;
			acceptIt();
			if(token.kind == TokenKind.LBRACK){
				acceptIt();
				accept(TokenKind.RBRACK);
				t = new ArrayType(bt, null);
			}
//			System.out.println(token.kind);
			String var_name = token.spelling;
			VarDecl vd = new VarDecl(t, var_name, null);
			accept(TokenKind.ID);
			accept(TokenKind.ASSIGN);
			//TODO
//			System.out.println("fun");
			
//			Expression e = parseExpression();
			Expression e = parseS();

			accept(TokenKind.SEMICOLON);
			// NEW Statement: Returns VarDeclStmt obj (C)
			return new VarDeclStmt(vd, e, null);
		
		case BOOLEAN:
			acceptIt();
//			System.out.println(token.kind);
			BaseType bool_t = new BaseType(TypeKind.BOOLEAN, null);
			
			String var_name1 = token.spelling;
			VarDecl vd1 = new VarDecl(bool_t, var_name1, null);
			accept(TokenKind.ID);
			accept(TokenKind.ASSIGN);
			
//			Expression e1 = parseExpression();
			Expression e1 = parseS();

			accept(TokenKind.SEMICOLON);
			// NEW Statement: Returns VarDeclStmt obj (D)
			return new VarDeclStmt(vd1, e1, null);
			
		case ID:
			Identifier id = new Identifier(token);
			ClassType ct= new ClassType(id, null);
			String var_name2 = null;
			acceptIt();
			
			switch(token.kind){
			case LBRACK:
				
				acceptIt();
				if(token.kind == TokenKind.RBRACK){
					acceptIt();
					ArrayType at = new ArrayType(ct, null);
					var_name = token.spelling;
					accept(TokenKind.ID);
					VarDecl vd2 = new VarDecl(at, var_name2, null);
					accept(TokenKind.ASSIGN);
					
//					Expression e2 = parseExpression();
					Expression e2 = parseS();

					
					accept(TokenKind.SEMICOLON);
					// NEW Statement: Returns VarDeclStatement (A1)
					tempStmt = new VarDeclStmt(vd2, e2, null);
				}
				else{
					IdRef idR = new IdRef(id, null);
					
//					Expression idxRefExp = parseExpression();
					Expression idxRefExp = parseS();

					
					accept(TokenKind.RBRACK);
					IndexedRef idxR = new IndexedRef(idR, idxRefExp, null);
					
					
					//
//					accept(TokenKind.ASSIGN);
					//
					
					if(token.kind==TokenKind.ASSIGN){
						acceptIt();
						
//						Expression expr = parseExpression();
						Expression expr = parseS();

						accept(TokenKind.SEMICOLON);
						// NEW Statement: Returns AssignStmt (A3)
						tempStmt = new AssignStmt(idxR, expr, null);

					}
					else if(token.kind==TokenKind.LPAREN){
						ExprList el1 = null;
						acceptIt();
						if(token.kind == TokenKind.THIS
						|| token.kind == TokenKind.ID
						|| token.kind == TokenKind.UNOP
						|| token.kind == TokenKind.MINUS
						|| token.kind == TokenKind.LPAREN
						|| token.kind == TokenKind.NUM
						|| token.kind == TokenKind.TRUE
						|| token.kind == TokenKind.FALSE
						|| token.kind == TokenKind.NEW
						|| token.kind == TokenKind.INT){
							el1 = parseArgumentList();
						}
						accept(TokenKind.RPAREN);
						accept(TokenKind.SEMICOLON);
						// NEW Statement: Returns CallStmt (A4a)
						return new CallStmt(idxR, el1, null);

					}
					
					
//					Expression expr = parseExpression();
//					accept(TokenKind.SEMICOLON);
//					// Statement: Returns AssignStmt (A3)
//					tempStmt = new AssignStmt(idxR, expr, null);

				}
				return tempStmt;
				
			case ID:
				var_name = token.spelling;
				acceptIt();
				VarDecl vd2 = new VarDecl(ct, var_name, null);
				accept(TokenKind.ASSIGN);
				
				
//				Expression e2 = parseExpression();
				Expression e2 = parseS();

				
				accept(TokenKind.SEMICOLON);
				// NEW Statement: Returns VarDeclStatement (A2)
				return new VarDeclStmt(vd2, e2, null);
				
			case PERIOD:
				IdRef idR = new IdRef(id, null);
				
				acceptIt();
//				QualifiedRef qR = new QualifiedRef(idR, new Identifier(token), null);
				QualifiedRef qR = new QualifiedRef(idR, new Identifier(token), null);

				
				accept(TokenKind.ID);
				while(token.kind == TokenKind.PERIOD){
					acceptIt();
					qR = new QualifiedRef(qR, new Identifier(token), null);
					accept(TokenKind.ID);
				}
				ExprList el1 = null;
//				if(token.kind == TokenKind.LPAREN){
//					acceptIt();
//					if(token.kind == TokenKind.THIS
//					|| token.kind == TokenKind.ID
//					|| token.kind == TokenKind.UNOP
//					|| token.kind == TokenKind.MINUS
//					|| token.kind == TokenKind.LPAREN
//					|| token.kind == TokenKind.NUM
//					|| token.kind == TokenKind.TRUE
//					|| token.kind == TokenKind.FALSE
//					|| token.kind == TokenKind.NEW
//					|| token.kind == TokenKind.INT){
//						el1 = parseArgumentList();
//					}
//					accept(TokenKind.RPAREN);
//					accept(TokenKind.SEMICOLON);
//					// NEW Statement: Returns CallStmt (A4a)
//					return new CallStmt(qR, el1, null);
//				}
				Reference ref = qR;
				Expression expr1 = null;
				if(token.kind == TokenKind.LBRACK){
					acceptIt();
					
//					expr1 = parseExpression();
					expr1 = parseS();

					accept(TokenKind.RBRACK);
					ref = new IndexedRef(qR, expr1, null);
				}
				//TODO
//				IndexedRef idxR = new IndexedRef(qR, expr1, null);
				//
//				accept(TokenKind.ASSIGN);
				//
				if(token.kind==TokenKind.ASSIGN){
					acceptIt();
					
//					Expression expr = parseExpression();
					Expression expr = parseS();

					
					accept(TokenKind.SEMICOLON);
					
					// NEW Statement: Returns AssignStmt (A3)
					return new AssignStmt(ref, expr, null);

				}
				else if(token.kind==TokenKind.LPAREN){
					acceptIt();
					if(token.kind == TokenKind.THIS
					|| token.kind == TokenKind.ID
					|| token.kind == TokenKind.UNOP
					|| token.kind == TokenKind.MINUS
					|| token.kind == TokenKind.LPAREN
					|| token.kind == TokenKind.NUM
					|| token.kind == TokenKind.TRUE
					|| token.kind == TokenKind.FALSE
					|| token.kind == TokenKind.NEW
					|| token.kind == TokenKind.INT){
						el1 = parseArgumentList();
					}
					accept(TokenKind.RPAREN);
					accept(TokenKind.SEMICOLON);
					// NEW Statement: Returns CallStmt (A4a)
					return new CallStmt(qR, el1, null);
				}
				
				
			case ASSIGN:
				IdRef idRef1 = new IdRef(id, null);
				acceptIt();
				
				
//				Expression expr2 = parseExpression();
				Expression expr2 = parseS();
				
				accept(TokenKind.SEMICOLON);
				// NEW Statement: Returns AssignStmt (A3)

				return new AssignStmt(idRef1, expr2, null);
				
			case LPAREN:
				IdRef idRef = new IdRef(id, null);
				ExprList el = new ExprList();
				acceptIt();
				if(token.kind == TokenKind.THIS
				|| token.kind == TokenKind.ID
				|| token.kind == TokenKind.UNOP
				|| token.kind == TokenKind.MINUS
				|| token.kind == TokenKind.LPAREN
				|| token.kind == TokenKind.NUM
				|| token.kind == TokenKind.TRUE
				|| token.kind == TokenKind.FALSE
				|| token.kind == TokenKind.NEW
				|| token.kind == TokenKind.INT){
					el = parseArgumentList();
				}
				accept(TokenKind.RPAREN);
				accept(TokenKind.SEMICOLON);
				// NEW Statement: Returns CallStmt (A4b)
				return new CallStmt(idRef, el, null);
				
			default:
				// error message
				return null;
			}
			
			
		case THIS: 
			
//			Reference r = parseReference();
//			switch(token.kind){
//			case ASSIGN:
//				acceptIt();
//				Expression e2 = parseExpression();
//				accept(TokenKind.SEMICOLON);
//				// NEW Statement: Returns AssignStmt obj (B1a)
//				return new AssignStmt(r, e2, null);
//				
//			case LBRACK:
//				acceptIt();
//				Expression e3 = parseExpression();
//				accept(TokenKind.RBRACK);
//				r = new IndexedRef(r , e3, null);
//				accept(TokenKind.ASSIGN);
//				Expression e4 = parseExpression();
//				accept(TokenKind.SEMICOLON);
//				// NEW Statement: Returns AssignStmt obj (B1b)
//				return new AssignStmt(r, e4, null);
//			
//			case LPAREN:
//				acceptIt();
//				ExprList el = null;
//				if(token.kind == TokenKind.THIS
//				|| token.kind == TokenKind.ID
//				|| token.kind == TokenKind.UNOP
//				|| token.kind == TokenKind.MINUS
//				|| token.kind == TokenKind.LPAREN
//				|| token.kind == TokenKind.NUM
//				|| token.kind == TokenKind.TRUE
//				|| token.kind == TokenKind.FALSE
//				|| token.kind == TokenKind.NEW
//				|| token.kind == TokenKind.INT){
//					el = parseArgumentList();
//				}
//				accept(TokenKind.RPAREN);
//				accept(TokenKind.SEMICOLON);
//				// NEW Statement: Returns CallStmt (B2)
//				return new CallStmt(r, el, null);
//			default:
//				return null;
//			}
			
			Reference r = parseIxReference();
			switch(token.kind){
			case ASSIGN:
				acceptIt();
				
//				Expression e4 = parseExpression();
				Expression e4 = parseS();

				
				accept(TokenKind.SEMICOLON);
				// NEW Statement: Returns AssignStmt obj (B1)
				return new AssignStmt(r, e4, null);
				
			case LPAREN:
				acceptIt();
				ExprList el = null;
				if(token.kind == TokenKind.THIS
				|| token.kind == TokenKind.ID
				|| token.kind == TokenKind.UNOP
				|| token.kind == TokenKind.MINUS
				|| token.kind == TokenKind.LPAREN
				|| token.kind == TokenKind.NUM
				|| token.kind == TokenKind.TRUE
				|| token.kind == TokenKind.FALSE
				|| token.kind == TokenKind.NEW
				|| token.kind == TokenKind.INT){
					el = parseArgumentList();
				}
				accept(TokenKind.RPAREN);
				accept(TokenKind.SEMICOLON);
				// NEW Statement: Returns CallStmt (B2)
				return new CallStmt(r, el, null);

			default:
				return null;
			}
			
		
		case IF:
			acceptIt();
			accept(TokenKind.LPAREN);
			
//			Expression cond = parseExpression();
			Expression cond = parseS();

			
			accept(TokenKind.RPAREN);
			Statement thenStmt = parseStatement();
			Statement elseStmt = null;	//XXX Should leave blank or include null?
			if(token.kind == TokenKind.ELSE){
				acceptIt();
				elseStmt = parseStatement();
			}
			// NEW Statement: Returns IfStmt obj
			return new IfStmt(cond, thenStmt, elseStmt, null);
			
		case WHILE:
			acceptIt();
			accept(TokenKind.LPAREN);
			
//			Expression condWhile = parseExpression();
			Expression condWhile = parseS();

			accept(TokenKind.RPAREN);
			Statement body = parseStatement();
			// NEW Statement: Returns WhileStmt obj
			return new WhileStmt(condWhile, body, null);
			
		default:
			// error msg
			return null;
		}
	}
	private boolean isStartOfStatement(TokenKind tk){
		while(token.kind == TokenKind.LINECOMMENT || token.kind == TokenKind.BLOCKCOMMENT){
			token = scanner.scan();
		}

		return (token.kind == TokenKind.LCURLY
				|| token.kind == TokenKind.INT
				|| token.kind == TokenKind.BOOLEAN
				|| token.kind == TokenKind.ID
				|| token.kind == TokenKind.THIS
				|| token.kind == TokenKind.IF
				|| token.kind == TokenKind.WHILE);
	}
	
	
	// NEW Expression: Method return type changed from void to Expression
	private Expression parseExpression(){
		Expression tempExpr = null;
		
		switch(token.kind){
		
		case THIS: case ID:
//			Reference tempRef = parseReference();
			Reference tempRef = parseIxReference();

			// NEW Expression: Returns RefExpr obj
			tempExpr = new RefExpr(tempRef, null);
			// | IxReference
//			if(token.kind == TokenKind.LBRACK){
//				acceptIt();
//				tempExpr = parseExpression();
//				IndexedRef idxR = new IndexedRef(tempRef, tempExpr, null);
//				accept(TokenKind.RBRACK);
//				tempExpr = new RefExpr(idxR, null);
//			}
			// | Reference (ArgumentList?) changed to IxReference (ArgumentList?)
			
			
			
//			else if(token.kind == TokenKind.LPAREN) {
			if(token.kind == TokenKind.LPAREN) {
	
				ExprList el;
				acceptIt();
				if(token.kind == TokenKind.RPAREN){
					acceptIt();
					el = null;
					tempExpr = new CallExpr(tempRef, el, null);
				}
				else {
					el = parseArgumentList();
					accept(TokenKind.RPAREN);
					tempExpr = new CallExpr(tempRef, el, null);
				}
				// NEW Expression: Returns CallExpr obj
			}
			break;
		
//		// | (Expression)
//		case LPAREN:
//			acceptIt();
//			tempExpr = parseExpression();
//			accept(TokenKind.RPAREN);
//			break;
		
		// | num | true | false
		case NUM: case TRUE: case FALSE:
			Terminal terminal = null;
			if(token.kind==TokenKind.NUM){
				terminal = new IntLiteral(token);
			}
			else{
				terminal = new BooleanLiteral(token);
			}
			acceptIt();
			
			// NEW Expression: Returns LiteralExpr obj
			tempExpr = new LiteralExpr(terminal, null);
			break;
		
		// | new ( id() | int [Expression] | id [Expression] )
		case NEW:
			acceptIt();
			if(token.kind == TokenKind.ID){
				Identifier id = new Identifier(token);
				acceptIt();
				if(token.kind == TokenKind.LBRACK){			// id [express]
					acceptIt();
					tempExpr = parseExpression();
					accept(TokenKind.RBRACK);
					
					// NEW Expression: Returns NewArrayExpr obj (id)
					ClassType ct = new ClassType(id, null);
					tempExpr = new NewArrayExpr(ct, tempExpr, null);
				}
				else if(token.kind == TokenKind.LPAREN){	// id()
					acceptIt();
					accept(TokenKind.RPAREN);
					
					// NEW Expression: Returns NewObjectExpr obj
					ClassType ct = new ClassType(id, null);
					tempExpr = new NewObjectExpr(ct, null);
				}else{
					//error msg
				}
			}
			else if(token.kind == TokenKind.INT){			// int [express]
				acceptIt();
				accept(TokenKind.LBRACK);
				tempExpr = parseExpression();
				accept(TokenKind.RBRACK);
				// NEW Expression: Returns NewArrayExpr obj (int)
				BaseType bt = new BaseType(TypeKind.INT, null);
				tempExpr = new NewArrayExpr(bt, tempExpr, null);
			}
			else {
				// error msg
			}
			break;
			
		case UNOP: case MINUS:
			Operator op = new Operator(token);
			acceptIt();
			tempExpr = parseExpression();
			// NEW Expresssion: Returns UnaryExpr obj
//			System.out.println("hi");
			tempExpr = new UnaryExpr(op, tempExpr, null);
			break;
		
		default:
			parseError("Invalid Term - expecting expression stuff but found " + token.kind);
		}
		
		// TODO
		// | Expression binop Expression
		while(isBinOp(token) || token.kind == TokenKind.MINUS){
			Token oper = token;
//			int precedence
			Operator op = new Operator(token);
			acceptIt();
			Expression tempExpr2 = parseExpression();
			// NEW Expression: Returns BinaryExpr obj
			tempExpr = new BinaryExpr(op, tempExpr, tempExpr2, null);
		}
		
		return tempExpr;
		
	}
	
	private boolean isBinOp(Token tok){
		TokenKind t_kind = tok.kind;
		return (t_kind==TokenKind.OR
				|| t_kind==TokenKind.AND
				|| t_kind==TokenKind.EQUAL
				|| t_kind==TokenKind.NOTEQUAL
				|| t_kind==TokenKind.LTEQ
				|| t_kind==TokenKind.LT
				|| t_kind==TokenKind.GT
				|| t_kind==TokenKind.GTEQ
				|| t_kind==TokenKind.PLUS
				|| t_kind==TokenKind.TIMES
				|| t_kind==TokenKind.DIVIDE);
	}
	
	private Expression parseS(){
		return parseE();
	}
	
	private Expression parseE(){
		Expression exp = parseA();
		while(token.kind==TokenKind.OR){
			Operator op = new Operator(token);
			acceptIt();
			Expression exp2 = parseA();
			exp = new BinaryExpr(op, exp, exp2, null);
		}
		return exp;
	}

	
	private Expression parseA(){
		Expression exp = parseB();
		while(token.kind==TokenKind.AND){
			Operator op = new Operator(token);
			acceptIt();
			Expression exp2 = parseB();
			exp = new BinaryExpr(op, exp, exp2, null);
		}
		return exp;
	}
	
	private Expression parseB(){
		Expression exp = parseC();
		while(token.kind==TokenKind.EQUAL || token.kind==TokenKind.NOTEQUAL){
			Operator op = new Operator(token);
			acceptIt();
			Expression exp2 = parseC();
			exp = new BinaryExpr(op, exp, exp2, null);
		}
		return exp;
	}
	
	private Expression parseC(){
		Expression exp = parseD();
		while(token.kind==TokenKind.LTEQ || token.kind==TokenKind.LT
				|| token.kind==TokenKind.GT || token.kind==TokenKind.GTEQ){
			Operator op = new Operator(token);
			acceptIt();
			Expression exp2 = parseD();
			exp = new BinaryExpr(op, exp, exp2, null);
		}
		return exp;
	}
	
	private Expression parseD(){
		Expression exp = parseF();
		while(token.kind==TokenKind.PLUS || token.kind==TokenKind.MINUS){
			Operator op = new Operator(token);
			acceptIt();
			Expression exp2 = parseF();
			exp = new BinaryExpr(op, exp, exp2, null);
		}
		return exp;
	}
	
	private Expression parseF(){
		Expression exp = parseT();
		while(token.kind==TokenKind.TIMES || token.kind==TokenKind.DIVIDE){
			Operator op = new Operator(token);
			acceptIt();
			Expression exp2 = parseT();
			exp = new BinaryExpr(op, exp, exp2, null);
		}
		return exp;
	}
	
	private Expression parseT() {
//		Expression exp = null;
		
		if(token.kind==TokenKind.LPAREN){
			acceptIt();
			Expression exp = parseE();
			accept(TokenKind.RPAREN);
			return exp;
		}
		return parseExpression();
	}
	// NEW parsePrecedenceExpr: New method that adopts that Expr binop Expr and (Expr) 
	// grammar
	
	// 
	private Expression parsePrecedenceExpr(){
		
		Expression expr = parseExpression();
				
		
		return null;
	}
	
	private void acceptIt() throws SyntaxError {
		accept(token.kind);
	}
	
	private void accept(TokenKind expectedTokenKind) throws SyntaxError {
		while(token.kind == TokenKind.LINECOMMENT || token.kind == TokenKind.BLOCKCOMMENT){
			token = scanner.scan();
		}
		if(token.kind == expectedTokenKind) {
			if(trace)
				pTrace();
			token = scanner.scan();
		}
		else
			parseError("expecting '" + expectedTokenKind + 
					"' but found '" + token.kind + "'");
		
		
		
		// TODO Add this to end?
//		while(token.kind == TokenKind.LINECOMMENT || token.kind == TokenKind.BLOCKCOMMENT){
//			token = scanner.scan();
//		}

		
	}
	
	private void parseError(String e) throws SyntaxError {
		reporter.reportError("Parse error: " + e);
		throw new SyntaxError();
	}
	
	private void pTrace() {
		StackTraceElement [] stl = Thread.currentThread().getStackTrace();
		for (int i = stl.length - 1; i>0 ; i--) {
			// TODO Commented to get AST Display only
//			if(stl[i].toString().contains("parse"))
//				System.out.println(stl[i]);
		}
		// TODO Commented to get AST Display only

//		System.out.println("accepting: " + token.kind + " (\"" + token.spelling + "\")");
//		System.out.println();
	}

}
