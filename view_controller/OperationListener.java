package view_controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.Model;
import model.Op;

public class OperationListener implements ActionListener {
	private Calculator view ;
	private Model model ;
	private Op op ;
	
	OperationListener( Calculator view, Model model, Op op) {
		this.view = view ;
		this.model = model ;
		this.op = op ;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		model.operation( op ) ;
		view.refresh();

	}

}
