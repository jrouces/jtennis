/*******************************************************************************
 * Copyright (c) 2007 Jacobo Rouces <jacobo@rouces.org>.
 * 
 * This file is part of JTennis.
 * 
 * JTennis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JTennis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 *  along with JTennis.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.rouces.jac.jtennis.algebra;

/**
 * La clase Vect2ConDistancia extiende Vect2 y se utilizará como tipo de salida
 * de la proyección, ya que incluye una variable que representa la distancia a
 * la que el punto proyectado está del punto de proyección. Podrían utilizarse
 * instancias de Vect3 para este propósito, pero no sería del todo ortodoxo ya
 * que en ningún caso la distancia podría entenderse como tercera coordenada
 * para una base dada si se obtiene por la norma de un vector representado por
 * otra base.
 */
public class Vect2ConDistancia extends Vect2 implements Vector {

	private double distancia;

	public Vect2ConDistancia(double x, double y, double distancia) {
		super(x, y);
		this.distancia = distancia;
	}

	public void ponDistancia(double distancia) {
		this.distancia = distancia;
	}

	public double dameDistancia() {
		return distancia;
	}

}
