/**
 * 
 */
package view_controller;

import javax.swing.*;

import model.Model;
import model.Op;

import java.awt.* ;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Theodore S. Norvell
 *
 */
public class Calculator extends JFrame {
	
	private static final long serialVersionUID = -8171162363931446552L;
	private final Model model = new Model() ;
	private final JLabel valueLabel = new JLabel() ;
	private final HashMap<String, JButton> buttons = new HashMap<String, JButton>() ;
	public void clickButton( String name ) {
		try {
			SwingUtilities.invokeAndWait( new Runnable() {
				@Override public void run() {
					if( ! buttons.containsKey( name ))
						throw new AssertionError( "No key named "+name) ;
					JButton button = buttons.get( name ) ;
					button.doClick(200);
				}
			});
		} catch (InvocationTargetException e) {
			throw new AssertionError( e ) ;
		} catch (InterruptedException e) {
			throw new AssertionError( e ) ;
		}
	}
	private final HashMap<String, int[]> placement = new HashMap<String, int[]>() ;

	{
		  placement.put( "(", new int[] {0, 1}) ;
		  placement.put( ")", new int[] {1, 1}) ;
		  placement.put( "c", new int[] {2, 1}) ;
		  placement.put( "n", new int[] {3, 1}) ;
		  placement.put( "7", new int[] {0, 2}) ;
		  placement.put( "8", new int[] {1, 2}) ;
		  placement.put( "9", new int[] {2, 2}) ;
		  placement.put( "/", new int[] {3, 2}) ;
		  placement.put( "4", new int[] {0, 3}) ;
		  placement.put( "5", new int[] {1, 3}) ;
		  placement.put( "6", new int[] {2, 3}) ;
		  placement.put( "*", new int[] {3, 3}) ;
		  placement.put( "1", new int[] {0, 4}) ;
		  placement.put( "2", new int[] {1, 4}) ;
		  placement.put( "3", new int[] {2, 4}) ;
		  placement.put( "-", new int[] {3, 4}) ;
		  placement.put( "0", new int[] {0, 5}) ;
		  placement.put( ".", new int[] {1, 5}) ;
		  placement.put( "=", new int[] {2, 5}) ;
		  placement.put( "+", new int[] {3, 5}) ;
	}

	private Calculator() {
		setLayout( new GridBagLayout() ) ;
		GridBagConstraints place = new GridBagConstraints();
		place.gridx = 0 ; place.gridy = 0 ;
		place.gridwidth = GridBagConstraints.REMAINDER ; place.gridheight = 1 ;
		add( valueLabel, place ) ;
		for(int i = 0 ; i < 10 ; ++ i ) {
			String name = Integer.toString(i) ;
			JButton digitButton = new JButton( name ) ;
			if( buttons.containsKey(name) ) throw new AssertionError() ;
			buttons.put(name, digitButton) ;
			if( ! placement.containsKey(name) ) throw new AssertionError() ;
			place.gridx = placement.get(name)[0] ; place.gridy = placement.get(name)[1];
			place.gridwidth = 1 ; place.gridheight = 1 ;
			add( digitButton, place ) ;
			ActionListener listener = new DigitListener( this, model, i) ;
			digitButton.addActionListener( listener ) ;
		}
		for( Op op : Op.values() ) {
			String name = op.toString() ;
			JButton opButton = new JButton( name ) ;
			if( buttons.containsKey(name) ) throw new AssertionError() ;
			buttons.put(name, opButton) ;
			if( ! placement.containsKey(name) ) throw new AssertionError() ;
			place.gridx = placement.get(name)[0] ; place.gridy = placement.get(name)[1];
			place.gridwidth = 1 ; place.gridheight = 1 ;
			add( opButton, place ) ;
			ActionListener listener = new OperationListener(this, model, op) ;
			opButton.addActionListener( listener ) ;
		}
		setSize(300,300) ;
		setVisible(true) ;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;

		model.setPrecision(4);
		refresh() ;
	}

	void refresh() {
		valueLabel.setText( model.getResult()) ;
	}

	public String getValue( ) {
		final AtomicReference<String> ref = new AtomicReference<String>() ;
		try {
			SwingUtilities.invokeAndWait( new Runnable() {
				@Override public void run() {
					ref.set( valueLabel.getText() );
				}
			});
		} catch (InvocationTargetException e) {
			throw new AssertionError( e ) ;
		} catch (InterruptedException e) {
			throw new AssertionError( e ) ;
		}
		return ref.get();
	}
	
	public static Calculator startUp( ) {
		final AtomicReference<Calculator> ref = new AtomicReference<Calculator>() ;
		try {
			SwingUtilities.invokeAndWait( new Runnable() {
				@Override public void run() {
					Calculator frame = new Calculator() ;
					ref.set( frame );
				}
			});
		} catch (InvocationTargetException e) {
			throw new AssertionError( e ) ;
		} catch (InterruptedException e) {
			throw new AssertionError( e ) ;
		}
		return ref.get();
	}
	
	public static void close( final JFrame frame ) {
		try {
			SwingUtilities.invokeAndWait( new Runnable() {
				@Override public void run() {
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				}
			});
		} catch (InvocationTargetException e) {
			throw new AssertionError( e ) ;
		} catch (InterruptedException e) {
			throw new AssertionError( e ) ;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater( new Runnable () {
			@Override public void run() {
				new Calculator() ;
			}
		} ) ;

	}

}
