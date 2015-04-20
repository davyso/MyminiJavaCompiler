/*** line 7: binary operator "+" expecting "int" type, received "boolean" type
 * COMP 520
 * Type checking (should return single error, not multiple errors)
 */
class Fail42 {
    public static void main(String[] args) {
        int x = 2 + (3 + (4 + (5 + (1 != 0))));
    }
}


