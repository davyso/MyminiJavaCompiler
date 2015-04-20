/*** line 8: binary operator "+" expecting "int" type, received "void" type
 * COMP 520
 * Type Checking
 */
class fail31 { 	
    public static void main(String[] args) {
	fail31 f = new fail31();
	int x = 1 + f.noresult();
    }
    
    void noresult() {}
}


