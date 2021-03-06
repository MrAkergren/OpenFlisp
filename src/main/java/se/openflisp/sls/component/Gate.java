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
package se.openflisp.sls.component;

import se.openflisp.sls.Component;
import se.openflisp.sls.Output;
import se.openflisp.sls.Signal;
import se.openflisp.sls.event.ComponentEventDelegator;

/**
 * Class representing a logical gate in a Sequential Logical Circuit.
 * A gate can have many inputs and only one output.
 * Extends the class Component.
 * 
 * @author Pär Svedberg <rockkuf@gmail.com>
 * @author Anton Ekberg <anton.ekberg@gmail.com>
 * @version 1.0
 */
public abstract class Gate extends Component {

	/**
	 * The single output for a logical gate.
	 */
	public static final String OUTPUT = "Q";
	
	/**
	 * {@inheritDoc}
	 */
	public Gate(String identifier) {
		super(identifier);
		this.getOutput();
	}

	/**
	 * {@inheritDoc}
	 */
	public Gate(String identifier, ComponentEventDelegator delegator) {
		super(identifier, delegator);
		this.getOutput();
	}
	
	/**
	 * Gets the single output of the gate.
	 * 
	 * @return the gate's output via its superclass' method
	 */
	public Output getOutput() {
		return this.getOutput(Gate.OUTPUT);
	}
	
	/**
	 * Evaluate the gate's different inputs and provide a output signal.
	 * 
	 * If it is unable to decide a signal for the given inputs the method
	 * must provide {@link Signal.State#FLOATING}.
	 * 
	 * @return the evaluated signal for the given inputs
	 */
	protected abstract Signal.State evaluateOutput();
	
	/**
	 * Initiate a number minimum number of inputs.
	 * 
	 * If the the Gate already have a number of inputs the method will
	 * not create more than the specified amount.
	 * 
	 * @param inputCount		minimum amount of inputs that should be initiated
	 */
	public void initiateInputs(int inputCount) {
		for (int inputID = 0; inputID < inputCount; inputID++) {
			this.getInput(Integer.toString(inputID));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void evaluate() {
		this.setOutputState(Gate.OUTPUT, this.evaluateOutput());
	}
}
