package miniJava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import miniJava.AbstractSyntaxTrees.Package;
import miniJava.ContextualAnalyzer.Checker;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

// Designed by dhsomo (David)
//

public class Compiler {

	public static void main(String[] args) {
		
		InputStream inputStream = null;
		if (args.length == 0) {
			System.out.println("Enter Expression");
			inputStream = System.in;
		}
		else {
			try {
				inputStream = new FileInputStream(args[0]);
			} catch (FileNotFoundException e) {
				System.out.println("Input file " + args[0] + " not found");
				System.exit(1);
			}		
		}

		ErrorReporter reporter = new ErrorReporter();
		Package myAST;
		
		// Syntactic Analysis
		Scanner scanner = new Scanner(inputStream, reporter);
		Parser parser = new Parser(scanner, reporter);

				
		System.out.println("Syntactic analysis ... ");
		myAST = parser.parse();
		System.out.println("Syntactic analysis complete:  ");
//		if (reporter.hasErrors()) {
//			System.out.println("INVALID arithmetic expression");
//			System.exit(4);
//		}
//		else {
//			System.out.println("valid arithmetic expression");
//			System.exit(0);
//		}

		
		// Contextual Analysis
		Checker contxtChecker = new Checker(reporter);

		System.out.println("Contextual analysis ... ");
		contxtChecker.check(myAST);
		System.out.println("Contextual analysis complete:  ");
		
		
		
		// *** TODO Change this error handling??? ***
		if (reporter.hasErrors()) {
			System.out.println("INVALID arithmetic expression");
			System.exit(4);
		}
		else {
			System.out.println("valid arithmetic expression");
			System.exit(0);
		}
		// *******************************************

	}

}
