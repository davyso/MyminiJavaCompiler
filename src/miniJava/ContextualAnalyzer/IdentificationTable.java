package miniJava.ContextualAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;

import miniJava.AbstractSyntaxTrees.Declaration;

public class IdentificationTable {
		
	public int level;
	private ArrayList<HashMap<String, Declaration>> listOfHashMaps;
	
	// constructor
	public IdentificationTable(){
		// Make an empty id table
		level = 0;	// outermost scope
		listOfHashMaps = new ArrayList<HashMap<String, Declaration>>();
	}
	
	// Call on defining occurrence of an id
	// Add an entry to the identification table, associating identifier "id"
	// with declaration "attr", which is a pointer to subtree in AST.
	public void enter(String id, Declaration attr){		
		
		// Nesting of scopes based on level
		// 1. class names
		// 2. member names within a class
		// 3. parameters names within a method
		// 4+ local variable names in successively nested scopes within a method.
				
			
		// if we are in level 4+ and there is a duplicate in level 3+,
		// we have a problem		
		if(level >= 4){
			
			for(int i=2; i<=level-1; i++){		// level 3 <=> 3rd hash map which is an index of 2

				HashMap<String, Declaration> tempMap = listOfHashMaps.get(i);
				if(tempMap.containsKey(id)){
					System.err.println("***There is a duplicate of \""
							+ id + "\" in scope " + (i+1));
				}
			}
		}
		
		// Add new entry of <id, decl> into list of hash maps
		// TODO Right now I am storing the duplicate, what should I do with it?
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
		
		// TODO how did you handle method calls if you deleted hash maps 
		// as a way to limit scopes within the identification table?
		// Possible solution: dont delete the hashmaps that contain 
		// classes (listOfHashMaps[0]) and members (listOfHashMaps[1])

		
		HashMap<String, Declaration> newMap = new HashMap<String, Declaration>();
		listOfHashMaps.add(newMap);
		level++;
	}
	
	
	// Remove the highest scope level from the identification table,
	// and all entries belonging to it.	
	public void closeScope(){
		
		// TODO how did you handle method calls if you deleted hash maps 
		// as a way to limit scopes within the identification table?
		// Possible solution: dont delete the hashmaps that contain 
		// classes (listOfHashMaps[0]) and members (listOfHashMaps[1])
		listOfHashMaps.remove(level-1);
		level--;
	}
	
}
