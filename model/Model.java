package model;

import java.util.Stack;


/** A model for a calculator.
 * <p>
 * This calculator supports the four basic functions,
 * base changes, variable precision.
 * @author Theodore Norvell
 *
 */
public class Model {
	  
	private Stack<Double> stackedOperands = new Stack<Double>() ;
	private Stack<Op> pendingOperators = new Stack<Op>() ;
	
	private boolean afterPoint = false ;
	private int digitsAfterPoint = 0 ;
	enum State { NODIGITS, DIGITS, RESULT } ;
	// The states work like this
	// NODIGITS. The state after an operation has just been entered.
	//           A number is expected. No digits of the number have been entered.
	// DIGITS. The state after a digit or point has been entered. Either more
	//         digits or an operator are acceptable
	// RESULT. The state after a ) or =. Basically the same as DIGITS, except
	//         that if a digit is entered it replaces the current top of stack
	//         rather than being appended to it. 
	// The DIGITS state has two substates.
	//     * When afterPoint is false, no decimal point has been entered for the number.
	//     * When afterPoint is true, the decimal point has been entered and in, this substate,
	//     
	
	State state = State.RESULT ;
	// invariant (state == State.NODIGITS ==> pendingOperators.size()-parCount == stackedOperands.size())
	//       and (state == State.DIGITS   ==> pendingOperators.size()-parCount+1 == stackedOperands.size())
	//       and (state == State.RESULT   ==> pendingOperators.size()-parCount+1 == stackedOperands.size())
	// where parCount is the number of left parentheses on the pendingOperators stack.
	
	private int base = 10 ; // Invariant 0 <= base && base <= 36 
	private int precision = 10 ;  // Invariant precision >= 0
	
	/**
	 * Start a new operand or add digit to the current operand.
	 * @param d
	 */
	public void digit( int d ) {
		assert 0 <= d && d < base ;
		switch( state ) {
			case NODIGITS: case RESULT : {
				startNewNumber(0.0) ;
			} // fall through
			case DIGITS: {
				double operand = stackedOperands.pop() ;
				if( afterPoint ) {
					digitsAfterPoint += 1 ;
					double dd = d ;
					for( int i=0 ; i < digitsAfterPoint ; ++i )
						dd /= base ;
					operand += dd ;	}
				else {
					operand = base*operand + d ; }
				stackedOperands.push( operand ) ;
			}
		}
	}
	
	private void startNewNumber(double x) {
		stackedOperands.push(x) ;
		afterPoint = false ;
		digitsAfterPoint = 0 ;
		state = State.DIGITS ;
	}
	
	/** React to any key other than a digit.
	 * This includes operations such as negate, add, subtract, multiply, and divide and 
	 * other keys, such as clear, the decimal point, equals key, and parentheses.
	 * @param op
	 */
	public void operation( Op op ) {
		switch( op ) {
		case CLEAR : {
			stackedOperands.clear() ;
			pendingOperators.clear() ;
			state = State.RESULT ; }
		break ;
		case NEGATE : 
			if( ! stackedOperands.isEmpty() ) {
				double x = - stackedOperands.pop();
				stackedOperands.push( x ) ;
			}
		break ;
		case ADD: case SUBTRACT : case MULTIPLY: case DIVIDE : {
			switch( state ) {
			case NODIGITS :
				stackedOperands.push(0.0) ;
				state = State.RESULT ;
			// fall through
			case DIGITS : case RESULT :
				while( !pendingOperators.isEmpty()
				&& pendingOperators.peek().precedence() >= op.precedence() ) {
					doPendingOperation() ;
				}
				pendingOperators.push(op) ;
				state = State.NODIGITS ;
			} }
		break ;
		case LEFT_PAR: {
			switch( state ) {
			case RESULT :
			case DIGITS :
				state = State.NODIGITS ;
			// fall through
			case NODIGITS :
				pendingOperators.push(Op.LEFT_PAR) ;
				break ;
			} }
		break ;
		case RIGHT_PAR: {
			switch( state ) {
			case NODIGITS :
				// Ignore
				break ;
			case DIGITS : case RESULT :
				while( !pendingOperators.isEmpty()
						&& pendingOperators.peek()!= Op.RIGHT_PAR ) {
					doPendingOperation() ; }
				if( !pendingOperators.isEmpty() ) pendingOperators.pop() ;
				state = State.RESULT ;
			} }
		break ;
		case EQUAL : {
			switch( state ) {
			case NODIGITS :
				// Ignore.
				break ;
			case DIGITS : case RESULT :
				while( !pendingOperators.isEmpty() ) {
					doPendingOperation() ; }
				state = State.RESULT ;
				break ;
			} }
		break ;
		case POINT : {
			switch( state ) {
			case RESULT :
			case NODIGITS :
				startNewNumber( 0.0 ) ;
			// fall through
			case DIGITS : 
				if( !afterPoint ) {
					afterPoint = true ;
					digitsAfterPoint = 0 ;	}
				break ;
			} }
		break ;
		default :
			assert false ; } }
	
	/** Set the base for data entry and display.
	 * Precondition 0 <= base and base <= 36
	 * @param base
	 */
	public void setBase( int base ) {
		assert 0 <= base && base <= 36 ;
		this.base = base ;
	}
	
	/** Set the number of digits after the point to display. 
	 * Note that all calculations are done using 64 bit floating point; the precision
	 * is only used for display.
	 * <p>
	 * Precondition: precision >= 0
	 * @param precision
	 */
	public void setPrecision( int precision ) {
		assert precision >= 0 ;
		this.precision = precision ;
	}
	
	/** Get the current output value */
	public String getResult() {
		double v = stackedOperands.isEmpty() ? 0.0 : stackedOperands.peek();
		if( state == State.RESULT || state==State.NODIGITS ) 
			return doubleToString( v, base, precision, true ) ;
		else if( afterPoint )
			return doubleToString( v, base, digitsAfterPoint, true ) ;
		else 
			return doubleToString( v, base, 0, false ) ;
	}
	
	private String doubleToString( double x, int base, int prec, boolean showDecimal ) {
		if( x == Double.NaN ) return "error" ;
		if( x == Double.POSITIVE_INFINITY ) return "+ oo" ;
		if( x == Double.POSITIVE_INFINITY ) return "- oo" ;
		StringBuffer buf = new StringBuffer() ;
		if( x < 0 ) {x = -x ; buf.append('-') ; }
		// There is a problem with this approach.
		// When precision is high, the following loop
		// can overflow x.  Need a better method.
		for( int i=0 ; i < prec ; ++i ) x *= base ;
		x = Math.rint(x) ;
		format( x, buf, base, prec, showDecimal ) ;
		return buf.toString();
	}
	
	private void format( double n, StringBuffer buf, int base, int prec, boolean showDecimal ) {
		assert n == Math.floor(n) ;
		if( n == 0.0 && prec < 0 ) return ;
		double rest = Math.floor(n / base ) ;
		format( rest , buf, base, prec-1, showDecimal ) ;
		int digit = (int) (n - rest*base) ;
		assert digit == n - rest*base ;
		assert 0 <= digit && digit < base ;
		buf.append( digitToChar( digit ) ) ;
		if( prec == 0 && showDecimal) buf.append( '.' ) ;
	}
	
	private char digitToChar( int digit ) {
		if( digit < 10 ) return (char)('0' + digit) ;
		else return (char)('A' + digit - 10 ) ;
	}
	
	private void doPendingOperation() {
		assert state == State.DIGITS || state==State.RESULT ;
		assert ! pendingOperators.isEmpty() ;
		Op op = pendingOperators.pop();
		if( op == Op.LEFT_PAR ) return ;
		assert stackedOperands.size() > 1 ;
		double y = stackedOperands.pop();
		double x = stackedOperands.pop();
		try {
			switch( op ) {
				case ADD : x += y ; break ;
				case SUBTRACT: x -= y ; break ;
				case MULTIPLY: x *= y ; break ;
				case DIVIDE: x *= y ; break ;
				default: assert false ;
			}
		} catch( ArithmeticException e){
			x = Double.NaN ; }
		stackedOperands.push(x) ;
	}
}
