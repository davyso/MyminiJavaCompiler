/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.Token;

public class Identifier extends Terminal {
	
	/* traverse the AST to link this attribute to the corresponding
	 * declaration node of to report an error when there is no such 
	 * declaration
	 */
	Declaration decl; // or method to traverse?

	public Identifier (Token t) {
		super (t);
	}

	public <A,R> R visit(Visitor<A,R> v, A o) {
		return v.visitIdentifier(this, o);
	}

}
