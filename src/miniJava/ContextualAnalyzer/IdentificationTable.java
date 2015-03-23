package miniJava.ContextualAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;

import miniJava.AbstractSyntaxTrees.Declaration;

public class IdentificationTable {
		
	private int level;
	private ArrayList<HashMap<String, Declaration>> listOfHashMaps;
	
	// constructor
	public IdentificationTable(){
		// Make an empty id table
		level = 1;	// outermost scope
		listOfHashMaps = new ArrayList<HashMap<String, Declaration>>();
	}
	
	// Call on defining occurrence of an id
	// Add an entry to the identification table, associating identifier "id"
	// with declaration "attr", which is a pointer to subtree in AST.
	public void enter(String id, Declaration attr){		
		
		// use for loop to iterate backwards through the listOfHashTables and
		// look for duplicates in local or outer scopes
		for(int i = level-1; i>=0;i--){
			HashMap<String, Declaration> tempMap = listOfHashMaps.get(i);
			if(tempMap.containsKey(id)){
				// TODO there is a duplicate!!!
			}
		}
		
		// Add new entry
		HashMap<String, Declaration> currScopeMap = listOfHashMaps.get(level-1);
		currScopeMap.put(id, attr);
	}
	
	// Call on applied occurrence of an id
	// Return the declaration associated with identifier "id" in the
	// identification table. If there are several entries for "id",
	// return the declaration from the entry at the highest scope level.
	// If there is no entry for "id", return null.
	public Declaration retrieve(String id){

		// if decl found in local scope, return it; else go one scope back
		for(int i = level-1; i>=0;i--){
			HashMap<String, Declaration> tempMap = listOfHashMaps.get(i);
			if(tempMap.containsKey(id)){
				Declaration attr = tempMap.get(id);
				return attr;
			}
		}

		// TODO if entry for id not found, return an error or null?
		return null;
	}
	

	// Add a new highest scope level to the identification table.
	public void openScope(){
		HashMap<String, Declaration> newMap = new HashMap<String, Declaration>();
		listOfHashMaps.add(newMap);
		level++;
	}
	
	
	// Remove the highest scope level from the identification table,
	// and all entries belonging to it.	
	public void closeScope(){
		listOfHashMaps.remove(level);
		level--;
	}
	
}
