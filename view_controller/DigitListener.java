package view_controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.Model;

public class DigitListener implements ActionListener {
	
	private final Calculator view ;
	private final Model model ;
	private final int digit ;
	
	DigitListener( Calculator view, Model model, int digit ) {
		this.view = view ;
		this.model = model ;
		this.digit = digit ;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		model.digit(digit) ;
		view.refresh();
	}

}
