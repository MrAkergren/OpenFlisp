package se.openflisp.gui.swing.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;

import javax.swing.JButton;

import se.openflisp.sls.Input;
import se.openflisp.sls.Output;
import se.openflisp.sls.Signal;
import se.openflisp.sls.Signal.State;

@SuppressWarnings("serial")
public class WireView extends JButton {	
	private GeneralPath wire = new GeneralPath();
	private Signal.State wireState;
	private boolean isSelected = false;

	WireView(){
		setContentAreaFilled(false);

	}

	/**
	 * Controls the state of the signal from the component
	 */

	public void wireState(Signal.State s){
		wireState = s;
	}


	/**
	 * Set this curve
	 */
	public void drawCurveTo(Point point){	 
		wire.quadTo( wire.getCurrentPoint().getX() + ((point.x - wire.getCurrentPoint().getX())/2), wire.getCurrentPoint().getY() + ((point.y - wire.getCurrentPoint().getY())), point.x, point.y);
		repaint();
		revalidate();
	}

	/**
	 * Move this curve
	 */
	public void moveCurve(int x, int y) {
		wire.moveTo(x, y);
	}

	/**
	 * Reset wire
	 */
	public void reset() {
		wire.reset();
	}

	/**
	 * Paint wire between signals
	 */
	public void drawBetweenSignals(Input input, Output output) {


	}


	/**
	 * Custom paint method so our button looks like a signal
	 */
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		if (getModel().isArmed() ) {
			g2.setColor(Color.BLUE);
		} else {
			g2.setColor(Color.RED);

		}

		g2.draw(wire);
	}

	/**
	 * Paints the border around the signal
	 */
	protected void paintBorder(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(6));
		if(getModel().isArmed() && isSelected){
			isSelected = false;
		}
		else if (getModel().isArmed()){
			isSelected = true;
		}
		if(isSelected){
			g2.setColor(Color.CYAN);
			g2.draw(wire);			
		}
		else{
			g2.setColor(Color.BLACK);
			g2.draw(wire);
		}
		if(wireState == Signal.State.HIGH){
			g2.setColor(Color.BLUE);
		}
		else{
			g2.setColor(Color.WHITE);
		}
		g2.setStroke(new BasicStroke(4));
		g2.draw(wire);
	}

	/**
	 * Will decide if the given x and y - values are within our button
	 * @param	x	the x value
	 * @param	y	the y value
	 */
	@SuppressWarnings("static-access")
	public boolean contains(int x, int y) {
		return wire.intersects(x-5,y-5,10,10);
	}
	
	public boolean isSelected(){
		return isSelected;
	}
}
