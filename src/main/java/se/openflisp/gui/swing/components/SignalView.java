/*
 * Copyright (C) 2014- See AUTHORS file.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.openflisp.gui.swing.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D.Float;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import se.openflisp.sls.Input;
import se.openflisp.sls.Signal;
import se.openflisp.sls.component.NandGate;
import se.openflisp.sls.event.CircuitListener;
import se.openflisp.sls.event.ComponentAdapter;
import se.openflisp.sls.event.ListenerContext;

/**	
 * A basic model for viewing signals
 * 
 * @author Daniel Svensson <daniel@dsit.se>
 * @version 1.0
 */
@SuppressWarnings("serial")
public class SignalView extends JButton {

	//The signal we make a view for
	public Signal signal;

	//The size of the signal-circle
	protected static int arcLength = ComponentView.componentSize/5;

	//The size of the whole signal object
	public static Dimension btnSize = new Dimension(ComponentView.componentSize/2,arcLength); 

	//We need this to determine if a x.y coordinate is whithin this button
	private Shape shape;

	//Will listen for mouse events
	private SignalViewListener signalViewListener = new SignalViewListener();

	// Testing propertychanged support
	private PropertyChangeSupport changes;

	public se.openflisp.sls.Component component;

	public SignalView(Signal signal){
		this.component = signal.getOwner();
		setPreferredSize(btnSize);
		this.signal = signal;
		setContentAreaFilled(false);	
		this.addMouseListener(signalViewListener);
		this.addMouseMotionListener(signalViewListener);
		this.component.getEventDelegator().addListener(ListenerContext.SWING, new ComponentAdapter() {
			@Override
			public void onSignalChange(se.openflisp.sls.Component component, Signal signal) {
				if (signal == SignalView.this.signal) {
					System.out.println("signal changed!");
					SignalView.this.repaint();
					SignalView.this.revalidate();
				}
			}
		});
	}

	/**
	 * Custom paint method so our button looks like a signal
	 */
	protected void paintComponent(Graphics g) {
		if (getModel().isArmed() || signal.getState() == Signal.State.HIGH) {
			g.setColor(Color.BLUE);
		} else {
			g.setColor(getBackground());
		}
		if (signal instanceof Input) {
			g.fillOval(0,0, arcLength-1, arcLength-1);
		} else {
			g.fillOval((btnSize.width - (arcLength+1)),0, arcLength-1, arcLength-1);
		}
	}

	/**
	 * Paints the border around the signal
	 */
	protected void paintBorder(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(getForeground());
		g2.setColor(getForeground());
		g2.setStroke(new BasicStroke(4));

		if (signal instanceof Input) {
			g2.drawLine(arcLength, btnSize.height/2, btnSize.width, btnSize.height/2);
			g2.setStroke(new BasicStroke(1));
			g2.drawOval(0,0,arcLength-1,arcLength-1);
		}	else {
			if (signal.getOwner() instanceof NandGate || signal.getOwner() instanceof NandGate) {
				g2.fillOval(0,0,arcLength-1,arcLength-1);
			} else {
				//g2.drawLine();
			}
			g2.setStroke(new BasicStroke(1));
			g2.drawOval(btnSize.width - (arcLength+1),0,arcLength-1,arcLength-1);
		}
	}

	/**
	 * Will decide if the given x and y - values are within our button
	 * @param	x	the x value
	 * @param	y	the y value
	 */
	public boolean contains(int x, int y) {
		if (shape == null || !shape.getBounds().equals(getBounds())) {
			if (signal instanceof Input)
				shape = new Float(0,0,arcLength-1,arcLength-1);
			else
				shape = new Float(btnSize.width - (arcLength+1),0, arcLength-1, arcLength-1);
		}
		return shape.contains(x, y);
	}

	public void addPropertyChangeListener(PropertyChangeListener l) 
	{
		if (changes == null)
			changes = new PropertyChangeSupport(this);
		changes.addPropertyChangeListener(l);
	}


	private class SignalViewListener extends MouseAdapter implements MouseListener {
		@Override
		public void mouseDragged(MouseEvent event) {
			changes.firePropertyChange("drag", event.getX(), event.getY());
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent event) {
			changes.firePropertyChange("released", null, event);
		}
	}
}
