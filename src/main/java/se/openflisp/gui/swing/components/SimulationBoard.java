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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import se.openflisp.sls.event.CircuitListener;
import se.openflisp.sls.event.ComponentListener;
import se.openflisp.sls.event.ListenerContext;
import se.openflisp.sls.simulation.Circuit2D;
import se.openflisp.sls.Component;
import se.openflisp.sls.Input;
import se.openflisp.sls.Output;
import se.openflisp.sls.Signal;
import	se.openflisp.gui.swing.components.ComponentView;

/**	
 * The Board for simulating gates 
 * 
 * @author Daniel Svensson <daniel@dsit.se>
 * @version 1.0
 */
@SuppressWarnings("serial")
public class SimulationBoard extends JPanel {

	// For drag and drop support
	private DropTarget dropTarget;

	// The circuit we are simulating
	public Circuit2D circuit;

	// In order to match component with componentViews
	private Map<Component, ComponentView> components;

	// A panel containing the components
	private JPanel componentLayer;

	// A panel containing the background
	private JPanel backgroundPanel;

	// A panel containing wires
	private WirePanel wirePanel;

	// We need this point when moving components
	Point point;

	//TEMP
	private boolean clicked = false;

	/**
	 * Creates the simulation board
	 */
	public SimulationBoard() {

		// Handle drop events
		this.dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetListener() {
			@Override
			public void drop(DropTargetDropEvent dropEvent) {
				// Try to convert the transferable to a string and send it to ComponentFactory for creation
				try {
					Transferable tr = dropEvent.getTransferable();
					String identifier;
					identifier = (String)tr.getTransferData(DataFlavor.stringFlavor);
					GateView view = ComponentFactory.createGateFromIdentifier(identifier);

					if (view != null) {
						// Add the components to circuit and move it
						SimulationBoard.this.circuit.addComponent(view.component);
						SimulationBoard.this.circuit.setComponentLocation(view.component, new Point(dropEvent.getLocation().x, dropEvent.getLocation().y));
					}
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void dropActionChanged(DropTargetDragEvent arg0) {
			}

			@Override
			public void dragEnter(DropTargetDragEvent arg0) {
			}

			@Override
			public void dragExit(DropTargetEvent arg0) {
			}

			@Override
			public void dragOver(DropTargetDragEvent arg0) {
			}

		}, true, null);

		// We need absolute positioning
		this.setLayout(null);

		// For drag and drop support
		this.setDropTarget(dropTarget);

		// Create the circuit and start simulation
		this.circuit = new Circuit2D();
		this.circuit.getSimulation().start();

		// Instantiate the componentLayer and set opaque
		this.componentLayer = new ComponentLayer();
		this.componentLayer.setLayout(null);
		this.componentLayer.setOpaque(false);

		this.wirePanel = new WirePanel(this);
		this.wirePanel.setLayout(null);
		this.wirePanel.setOpaque(false);

		this.backgroundPanel = new BackgroundPanel();
		this.backgroundPanel.setOpaque(true);

		// This will add the panels to our layeredPane in order to make a transparent components
		this.components = new HashMap<Component, ComponentView>();
		this.add(backgroundPanel, new Integer(0), 0);
		this.add(wirePanel, new Integer(1), 0);
		this.add(componentLayer, new Integer(2),0);		

		// Set a listener on the circuit
		this.circuit.getEventDelegator().addListener(ListenerContext.SWING,circtuitHandler);

	}

	/**
	 * Adds a component to the simulation board
	 * @param component		the component to be added
	 */
	public void addComponent(ComponentView component) {
		component.setOpaque(false);
		this.componentLayer.add(component);
		this.components.put(component.component, component);

		for( SignalView view : ((GateView) component).outputSignals) {
			view.addPropertyChangeListener(wirePanel);
		}

		for( SignalView view : ((GateView) component).inputSignals) {
			view.addPropertyChangeListener(wirePanel);
		}

		if (component instanceof GateView) {
			JPanel identifierPanel = ((GateView) component).getIdentifierPane();
			component.addMouseMotionListener(new MouseMotionAdapter()  {


				@Override
				public void mouseMoved(MouseEvent e) {
					// TODO Auto-generated method stub
				}

				@Override
				public void mouseDragged(MouseEvent mouseevent) {
					if (mouseevent.getComponent().getComponentAt(mouseevent.getPoint()) instanceof JLabel && SimulationBoard.this.point != null) {
						Point delta = new Point(mouseevent.getX() - SimulationBoard.this.point.x, 
								mouseevent.getY() - SimulationBoard.this.point.y );
						Point curLocation = SimulationBoard.this.circuit.getComponentLocation(((GateView)mouseevent.getSource()).component);

						SimulationBoard.this.circuit.setComponentLocation(((GateView)mouseevent.getSource()).component,
								new Point(curLocation.x + delta.x, curLocation.y + delta.y));
					}
				}
			});
			component.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent mouseevent) {
					SimulationBoard.this.point = mouseevent.getPoint();
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					// TODO Auto-generated method stub
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					// TODO Auto-generated method stub
				}

				@Override
				public void mousePressed(MouseEvent mouseevent) {
					SimulationBoard.this.point = mouseevent.getPoint();
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
				}
			});
		}
	}

	/**
	 * We need to override the painting
	 */
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		this.backgroundPanel.setBounds(0,0,this.getWidth(),this.getHeight());
		this.componentLayer.setBounds(0,0,this.getWidth(),this.getHeight());
		this.wirePanel.setBounds(0,0,this.getWidth(),this.getHeight());
		super.paintComponent(g2);	
	}

	/**
	 * Listener for the circuit
	 */
	protected final CircuitListener circtuitHandler = new CircuitListener() {
		/**
		 * Will add a new component to the circuit
		 * @param Component		the component to be added
		 */
		@Override
		public void onComponentAdded(Component component) {
			addComponent(ComponentFactory.createGateFromComponent(component));
			SimulationBoard.this.repaint();
			SimulationBoard.this.revalidate();
		}

		@Override
		public void onComponentRemoved(Component component) {

		}

		/**
		 * Repaints the component when it has moved
		 * 
		 */
		@Override
		public void onComponentMoved(Component component, Point from, Point to) {
			SimulationBoard.this.components.get(component).setBounds(to.x,to.y,ComponentView.componentSize*2,ComponentView.componentSize);
			SimulationBoard.this.wirePanel.ComponentMoved(component, from, to);
		}
	};



	/**
	 * 	Creates a grid in the background layer
	 */
	public class BackgroundPanel extends JPanel {
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0xCC, 0xCC, 0xCC));
			g2.drawRect(0, 0, getWidth()-1, getHeight());
			g2.setColor(new Color(0xCD, 0xCD, 0xCD));
			paintGrid(g2,this.getWidth(),this.getHeight());    		
		}

		public void paintGrid(Graphics g, int gridWidth, int gridHeight) {
			for(int i=1; i<gridWidth; i=i+10)
			{
				g.drawLine(i, 0,      i,      gridHeight);          
			}      

			for(int i=1; i<gridHeight; i=i+10)
			{      
				g.drawLine(0, i, gridWidth, i);          
			} 
		}
	}

	public class ComponentLayer extends JPanel {
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			super.paintComponent(g2);
		}
	}
}
