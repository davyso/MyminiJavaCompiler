package miniJava.SyntacticAnalyzer;

import java.io.*;

import miniJava.ErrorReporter;

public class Scanner {

	private InputStream inputStream;
	private ErrorReporter reporter;
	
	private char currentChar;
	private StringBuilder currentSpelling;
		
	
	public Scanner(InputStream inputStream, ErrorReporter reporter) {
		this.inputStream = inputStream;
		this.reporter = reporter;
		
		readChar();
	}
	
	public Token scan() {
		
		while (currentChar == ' ' || currentChar == '\t'
				|| currentChar =='\n' || currentChar == '\r')
			skipIt();
		
		currentSpelling = new StringBuilder();
		TokenKind kind = scanToken();
		
		return new Token(kind, currentSpelling.toString());
	}
	
	private TokenKind scanToken() {
		
		switch (currentChar) {
		
		case '{':
			takeIt();
			return(TokenKind.LCURLY);
			
		case '}':
			takeIt();
			return(TokenKind.RCURLY);
		
		case '(': 
			takeIt();
			return(TokenKind.LPAREN);

		case ')':
			takeIt();
			return(TokenKind.RPAREN);
			
		case '[':
			takeIt();
			return(TokenKind.LBRACK);
			
		case ']':
			takeIt();
			return(TokenKind.RBRACK);
			
		case ',':
			takeIt();
			return(TokenKind.COMMA);
			
		case ';':
			takeIt();
			return(TokenKind.SEMICOLON);

		case '0': case '1': case '2': case '3': case '4':
		case '5': case '6': case '7': case '8': case '9':
			while (isDigit(currentChar))
				takeIt();
			return(TokenKind.NUM);

		
		// ids and keywords
		case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
		case 'G': case 'H': case 'I': case 'J': case 'K': case 'L':
		case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
		case 'S': case 'T': case 'U': case 'V': case 'W': case 'X':
		case 'Y': case 'Z':
		case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
		case 'g': case 'h': case 'i': case 'j': case 'k': case 'l':
		case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
		case 's': case 't': case 'u': case 'v': case 'w': case 'x':
		case 'y': case 'z':
			while(isAlphanumeric(currentChar) || currentChar == '_')
				takeIt();
			
			// compare for keywords
			switch(currentSpelling.toString()) {
			case "class":
				return (TokenKind.CLASS);
			case "return":
				return (TokenKind.RETURN);
			case "public":
				return (TokenKind.PUBLIC);
			case "private":
				return (TokenKind.PRIVATE);
			case "static":
				return (TokenKind.STATIC);
			case "void":
				return (TokenKind.VOID);
			case "int":
				return (TokenKind.INT);
			case "boolean":
				return (TokenKind.BOOLEAN);
			case "this":
				return (TokenKind.THIS);
			case "if":
				return (TokenKind.IF);
			case "else":
				return (TokenKind.ELSE);
			case "while":
				return (TokenKind.WHILE);
			case "true":
				return (TokenKind.TRUE);
			case "false":
				return (TokenKind.FALSE);
			case "new":
				return (TokenKind.NEW);
			default:
				return (TokenKind.ID);
			}
					
		// OP
		case '>': 						// <, >, <=, >=
			takeIt();
			if(isEqualSign(currentChar)){
				takeIt();
				return (TokenKind.GTEQ);
			}
			return (TokenKind.GT);
		case '<':	
			takeIt();
			if(isEqualSign(currentChar)){
				takeIt();
				return (TokenKind.LTEQ);
			}
			return (TokenKind.LT);

			
		case '+': 							// +, *
			takeIt();
			return(TokenKind.PLUS);
		case'*':
			takeIt();
			return(TokenKind.TIMES);

			
		case '&':									// &&
			takeIt();
			if(currentChar == '&'){
				takeIt();
				return (TokenKind.AND);
			}
			return (TokenKind.ERROR);
			
		case '|':
			takeIt();
			if(currentChar == '|'){
				takeIt();
				return (TokenKind.OR);
			}
			return (TokenKind.ERROR);
			
		case '/':
			takeIt();
			if(currentChar == '/'){
				takeIt();
				while(currentChar != eolUnix && currentChar!= eolWindows && currentChar != '\u0003'){
					
//					System.out.println(currentChar);
					takeIt();
				}
				return (TokenKind.LINECOMMENT);
				
			}else if(currentChar == '*'){
				takeIt();
				// (?s) is equivalent to DOTALL attribute; "." does not include \n
				while(!currentSpelling.toString().matches("(?s)\\/\\*.*\\*\\/")){
					if(currentChar == '\u0003'){
						return (TokenKind.ERROR);
					}
					takeIt();
				}
				return (TokenKind.BLOCKCOMMENT);

			}else{
				return (TokenKind.DIVIDE);
			}
			
		case '-':									// -
			takeIt();

			if(currentChar == '-'){
				return (TokenKind.ERROR);
			}
			
			return(TokenKind.MINUS);
			
		case '!':									// !, !=
			takeIt();
			if(isEqualSign(currentChar)){
				takeIt();
				return (TokenKind.NOTEQUAL);
			}
			return (TokenKind.UNOP);
		case '=':									// =, ==
			takeIt();
			if(isEqualSign(currentChar)){
				takeIt();
				return (TokenKind.EQUAL);
			}
			return (TokenKind.ASSIGN);
		
		case '.':
			takeIt();
			return (TokenKind.PERIOD);
			
//		case '$':
//			return(TokenKind.EOT);
			
		case '\u0003':
//			System.out.println("Hit");
			return(TokenKind.EOT);

		default:
			scanError("Unrecognized character '" + currentChar + "' in input");
			return(TokenKind.ERROR);
		
		}
		
	}
	
	
	private void takeIt() {
		currentSpelling.append(currentChar);
		nextChar();
	}
	
	private void skipIt() {
		nextChar();
	}
	
	private boolean isEqualSign(char c) {
		return (c == '=');
	}
	private boolean isDigit(char c) {
		return (c >= '0') && (c <= '9');
	}
	
	private boolean isAlpha(char c) {
		return (c=='A') || (c=='B') || (c=='C') || (c=='D') || (c=='E') || (c=='F') ||
			(c=='G') || (c=='H') || (c=='I') || (c=='J') || (c=='K') || (c=='L') ||
			(c=='M') || (c=='N') || (c=='O') || (c=='P') || (c=='Q') || (c=='R') ||
			(c=='S') || (c=='T') || (c=='U') || (c=='V') || (c=='W') || (c=='X') ||
			(c=='Y') || (c=='Z') ||
			(c=='a') || (c=='b') || (c=='c') || (c=='d') || (c=='e') || (c=='f') ||
			(c=='g') || (c=='h') || (c=='i') || (c=='j') || (c=='k') || (c=='l') ||
			(c=='m') || (c=='n') || (c=='o') || (c=='p') || (c=='q') || (c=='r') ||
			(c=='s') || (c=='t') || (c=='u') || (c=='v') || (c=='w') || (c=='x') ||
			(c=='y') || (c=='z');
	}
	
	private boolean isAlphanumeric(char c) {
		return isDigit(c) || isAlpha(c);
	}
	
	private void scanError(String m) {
		reporter.reportError("Scan Error: " + m);
	}
	
	private final static char eolUnix = '\n';
	private final static char eolWindows = '\r';
	
	
	private void nextChar() {
//		if (currentChar != '$')
			readChar();
	}
	
	private void readChar() {
		try {
			int c = inputStream.read();
			currentChar = (char) c;
//			System.out.println(currentChar);
//			System.out.println(c);


			if(c == -1){
//				System.out.println(c);
//				System.out.println(currentChar);

//				currentChar = '$';
				currentChar = '\u0003';				
			}
//			else if (currentChar == '$') {
//				scanError("Illegal character '$' in input");
//			}
			
		} catch(IOException e) {
			scanError("I/O Exception!");
			currentChar = '$';
		}
	}
	
}
