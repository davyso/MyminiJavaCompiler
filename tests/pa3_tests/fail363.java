/*** line 8: member "also" has private access in class "F03"
 * COMP 520
 * Identification
 */
class fail63 { 	
    public static void main(String[] args) {
	F03 c = new F03();
	c.next.also.x = 3;
    }
}

class F03 {
    public F03 next;
    private F03 also;
    public int x;
}
