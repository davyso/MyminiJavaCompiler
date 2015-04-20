/*** line 14: method "f" parameter 1 type "int" incompatible with argument type "void" 
 * COMP 520
 * Type checking
 */
class Fail43 {
    public static void main(String[] args) {
        A a = new A();
        a.g();
    } 
}

class A{
    public void g() {
	int x = f(g());
    }
    
    public int f(int x) { 
        return 5;
    }
}

