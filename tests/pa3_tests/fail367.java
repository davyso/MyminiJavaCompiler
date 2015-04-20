/*** line 8: qualification of reference that is not a class instance
 * COMP 520
 * Identification
 */
class F67 { 	
    public static void main(String[] args) {
        F05 c = new F05();
        c = c.x.next;
    }
}

class F05 {
    public F05 next;
    public int x;
}
