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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import se.openflisp.sls.event.CircuitListener;
import se.openflisp.sls.event.ComponentListener;

import se.openflisp.sls.simulation.Circuit2D;
import se.openflisp.sls.Component;
import se.openflisp.sls.Input;
import se.openflisp.sls.Output;
import se.openflisp.sls.Signal;
import	se.openflisp.gui.swing.components.ComponentView;
import se.openflisp.gui.swing.util.*;

/**	
 * The Board for simulating gates 
 * 
 * @author Daniel Svensson <daniel@dsit.se>
 * @version 1.0
 */
@SuppressWarnings("serial")
public class SimulationBoard extends JPanel{
	
	// For drag and drop support
	private DropTarget dropTarget;
	
	// The circuit we are simulating
	private Circuit2D circuit;
	
	// In order to match component with componentViews
	private Map<Component, ComponentView> components;
	
	// A panel containing the components
	private JPanel componentLayer;
	
	// A panel containing the background
	private JPanel backgroundPanel;

	/**
	 * Creates the simulation board
	 */
	public SimulationBoard() {
		
		// Handle drop events
		this.dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetListener() {
			@Override
			public void drop(DropTargetDropEvent dropEvent) {
				// Try to converte the transferable to a string and send it to ComponentFactory for creation
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
		
		this.backgroundPanel = new BackgroundPanel();
		this.backgroundPanel.setOpaque(true);
		
		// This will add the panels to our layeredPane in order to make a transparent components
		this.components = new HashMap<Component, ComponentView>();
		this.add(backgroundPanel, new Integer(0), 0);
		this.add(componentLayer, new Integer(1),0);
		
		// Set a listener on the circuit
		this.circuit.getEventDelegator().addListener(circtuitHandler);
		
		//This is a test method
		//simulationTest();
	}
	
	/**
	 * Adds a component to the simulation board
	 * @param component		the component to be added
	 */
	public void addComponent(ComponentView component) {
		component.setOpaque(false);
		this.componentLayer.add(component);
		this.components.put(component.component, component);
	}
	
	/**
	 * This is a test
	 */
	public void simulationTest() {
		/*
		NotGate g = new NotGate("a");
		g.getInput("input1");
		this.circuit.addComponent(g);
		ConstantGate ett = new ConstantGate("ett", Signal.State.HIGH);
		ett.getOutput().connect(g.getInput("input1"));
		
		ComponentView view = new GateView(ett);
		view.setOpaque(false);
		this.componentLayer.add(view);
		view.setBounds(200,200,200,100);
		this.components.put(ett, view);
		
		view = new GateView(g);
		view.setOpaque(false);
		this.componentLayer.add(view);
		view.setBounds(400,400,200,100);
		
		this.components.put(g, view);
		*/
	}
	
	/**
	 * We need to override the painting
	 */
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		this.backgroundPanel.setBounds(0,0,this.getWidth(),this.getHeight());
		this.componentLayer.setBounds(0,0,this.getWidth(),this.getHeight());
		super.paintComponent(g2);	
	}
    
	/**
	 * This is the listener for all components
	 */
    protected final ComponentListener swingHandler = new ComponentListener() {
        @Override
        public void onSignalChange(Component component, Signal signal) {
        	
        }

		@Override
		public void onSignalConnection(Input input, Output output) {

			
		}

		@Override
		public void onSignalDisconnection(Input input, Output output) {
			
		}
    };
    
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
			SimulationBoard.this.components.get(component).setBounds(to.x,to.y,ComponentView.componentSize,ComponentView.componentSize/2);	
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
