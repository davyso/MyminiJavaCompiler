/*** line 7: binary operator "==" requires arguments to have the same type.
 * COMP 520
 * Type Checking
 */
class fail35 { 	
    public static void main(String[] args) {
        boolean c = (new A() == new B());
    }
}

class A {
    int y;
}

class B {
    int x;
}
