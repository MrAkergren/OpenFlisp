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

import se.openflisp.sls.Component;
import se.openflisp.sls.Signal;
import se.openflisp.sls.component.AndGate;
import se.openflisp.sls.component.ConstantGate;
import se.openflisp.sls.component.Gate;
import se.openflisp.sls.component.NandGate;
import se.openflisp.sls.component.NotGate;
import se.openflisp.sls.component.OrGate;

/**	
 * A Factory for creating ComponentViews 
 * 
 * @author Daniel Svensson <daniel@dsit.se>
 * @version 1.0
 */


public class ComponentFactory {
	private static int gateNum;
	private static int inputNum;
	
	/**
	 * Creates a componentView given an Identifier
	 * @param identifier		the identifier
	 * @return	componentView	the view for this component
	 */
	public static GateView createGateFromIdentifier(String identifier) {
		try {
			if(identifier.equals("AndGate")) {
				AndGate andGate = new AndGate("AndGate" + Integer.toString(gateNum++));
				andGate.getInput("input" + Integer.toString(inputNum++));
				return new GateView(andGate);
			}
			else if(identifier.equals("NotGate")) { 
				NotGate notGate = new NotGate("NotGate" + Integer.toString(gateNum++));
				notGate.getInput("input" + Integer.toString(inputNum++));
				return new GateView(notGate);
			}
			else if(identifier.equals("ConstantOneGate")) { 
				ConstantGate constantGate = new ConstantGate("ConstantGate" + Integer.toString(gateNum++), Signal.State.HIGH);
				return new GateView(constantGate);
			}
			else if(identifier.equals("ConstantZeroGate")) { 
				ConstantGate constantGate = new ConstantGate("ConstantGate" + Integer.toString(gateNum++), Signal.State.LOW);
				return new GateView(constantGate);
			}
			else if(identifier.equals("NandGate")) { 
				NandGate nandGate = new NandGate("ConstantGate" + Integer.toString(gateNum++));
				nandGate.getInput("input" + Integer.toString(inputNum++));
				return new GateView(nandGate);
			}
			else if(identifier.equals("OrGate")) { 
				OrGate orGate = new OrGate("ConstantGate" + Integer.toString(gateNum++));
				orGate.getInput("input" + Integer.toString(inputNum++));
				return new GateView(orGate);
			}
			
		} catch (Exception e) {
			System.out.println("Something went wrong when creating a ComponentView");
			return null;
		}
		return null;
	}
	
	/**
	 * Creates a componentView given a component
	 * @param component		the component
	 * @return	componentView	the view for this component
	 */
	public static GateView createGateFromComponent(Component component) {
		try {
			if(component instanceof Gate) 
				return new GateView(component);
		} catch (Exception e) {
			System.out.println("Something went wrong when creating a ComponentView");
			return null;
		}
		return null;
	}
}
