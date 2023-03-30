package model;

/** Operators for the calculator.
 * <p>Each operator has a name (accessed by toString) and a precedence,
 * however only binary operators and the left parentheses have
 * a meaningful precedence.
 * 
 * @author theo
 *
 */
public enum Op  {
	ADD( "+", 1 ),
	SUBTRACT( "-", 3),
	MULTIPLY( "*", 2),
	DIVIDE( "/", 2),
	LEFT_PAR("(",0 ),
	RIGHT_PAR(")",-1),
	EQUAL("=", -1),
	CLEAR("c", -1),
	POINT(".", -1),
	NEGATE("n", -1); 
	
	private final int prec ;
	private final String name ;
	
	private Op(String name, int prec) {
        this.name = name;
        this.prec = prec; }
	
	public String toString() { return name ; }
    public double precedence()   { assert prec >= 0 ; return prec; }
}