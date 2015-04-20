/*** binary operator "<" expecting "int" type received "boolean" type
 * COMP 520
 * Type checking
 */
class fail46 { 	
    public static void main(String[] args) {
    	int x = 1;
    	if (true < false)
    		x = 2;
    }

}
