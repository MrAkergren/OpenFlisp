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

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;

import se.openflisp.sls.Signal;

/**	
 * A basic model for viewing signals
 * 
 * @author Daniel Svensson <daniel@dsit.se>
 * @version 1.0
 */
@SuppressWarnings("serial")
public abstract class SignalView extends JButton {
	protected Signal signal;
	protected Dimension btnSize = new Dimension(50,20);
	protected int arcLength = 20;

	SignalView(Signal signal){
		setPreferredSize(btnSize);
		this.signal = signal;
		setContentAreaFilled(false);
	}
	abstract protected void paintComponent(Graphics g);
	abstract protected void paintBorder(Graphics g);
	abstract public boolean contains(int x, int y);
}
