/*** line 9: cannot access non-static member "f" in static context "D"
 * COMP 520
 * Identification
 */
class D { 	
    public static void main(String[] args) { }

    public int f(){
        return D.f();
    }
}
