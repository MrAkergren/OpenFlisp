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
package se.openflisp.gui.swing.util;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;


/**	
 * A help class that draws the "wire" that the signal will travel trough between the components
 * 
 * @author Oskar Åkergren oskar.akergren@gmail.com
 * @version 1.0
 */

public class Wire{
	public static void drawWire(Graphics g, Point p1, Point p2){

		g.setColor(Color.RED);
		g.fillOval((int)p1.getX() - 6, (int)p1.getY() - 6, 12, 12);
		g.fillOval((int)p2.getX() - 6, (int)p2.getY() - 6, 12, 12);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.black);
		GeneralPath path = new GeneralPath();
		path.moveTo(p1.getX(), p1.getY());
		path.lineTo(p2.getX(), p2.getY());
		g2d.draw(path);
	}
}