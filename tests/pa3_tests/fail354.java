/*** line 9: cannot access non-static member "y" in static context "C"
 * COMP 520
 * Identification
 */
class C { 	
    public static void main(String[] args) { }

    public void f() {
        int x = C.y;
    }

    public int y;
}
