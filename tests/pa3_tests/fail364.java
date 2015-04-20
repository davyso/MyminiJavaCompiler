/*** line 8: member "foo" has private accesss in class "F04"
 * COMP 520
 * Identification
 */
class fail64 { 	
    public static void main(String[] args) {
	F04 c = new F04();
	c.foo();
    }
}

class F04 {
    public F04 next;
    private void foo() {}
}
