/*** line 11: variable "parm" is already defined within method "foo"
 * COMP 520
 * Identification
 */
class fail10 { 	
    public static void main(String[] args) {}
    
    public void foo(int parm) {
        int x = 0;
        {
            int parm = 4;
        }
    }
}

