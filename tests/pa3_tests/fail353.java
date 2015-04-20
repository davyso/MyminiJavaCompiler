/*** line 7: cannot access non-static symbol "f" in static context
 * COMP 520
 * Identification
 */
class fail53 { 	
    public static void main(String[] args) {
        int y = f() + 3;
    }

    public int f() {
        return 7;
    }
}
