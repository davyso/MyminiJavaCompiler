package miniJava.ContextualAnalyzer;

import miniJava.AbstractSyntaxTrees.Declaration;

public class IdentificationTable {
	// Scope follows the nested block structure
	
	// field for a hash map?
	
	private int level;
	
	// constructor
	public IdentificationTable(){
		// Make an empty id table
		
		level = 0;
	}
	
	// Call on defining occurrence of an id
	public void enter(String id, Declaration attr){
		// Add an entry to the identification table, associating identifier "id"
		// with declaration "attr", which is a pointer to subtree in AST.
	}
	
	// Call on applied occurrence of an id
	public Declaration retrieve(String id){
		// Return the declaration associated with identifier "id" in the
		// identification table. If there are several entries for "id",
		// return the declaration from the entry at the highest scope level.
		// If there is no entry for "id", return null.
		
		
		
		// TODO if entry for id not found, return an error
		
		// TODO Subject to change
		return null;
	}
	
	
	public void openScope(){
		// Add a new highest scope level to the identification table.
		level++;
	}
	
	public void closeScope(){
		// Remove the highest scope level from the identification table,
		// and all entries belonging to it.
		level--;
	}
	
}
