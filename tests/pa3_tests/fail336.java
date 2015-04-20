/*** line 12: no value returned from non-void method
 * COMP 520
 * Type checking
 */
class T36 {
    public static void main(String [] args) {
        T36 h = new T36();
        int x = h.foo();
    }

    // missing return stmt
    public int foo() {
        int y = 3;
    }
}

