/*** line 8: member "x" has private access in "F02"
 * COMP 520
 * Identification
 */
class fail62 { 	
    public static void main(String[] args) {
	F02 c = new F02();
	int y = c.x;
    }
}

class F02 {
    public F02 next;
    private int x;
}
