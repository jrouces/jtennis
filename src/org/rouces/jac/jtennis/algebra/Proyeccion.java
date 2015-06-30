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

import java.util.Stack;

/**
 * Cada objeto de la clase Proyeccion queda definido por un punto y un plano del
 * espacio afín de tres dimensiones, y ofrece un método para calcular la
 * proyección cónica que ambos definen.
 * 
 * 
 */
public class Proyeccion {

	private Vect3 puntoProyeccion;

	private Vect3 u, v, w;
	/* La proyección se calculará a través de un cambio de referencia en el espacio afín de tres dimensiones
	  Para ello, se construirá la referencia de destino con el punto de origen en el punto de proyección y
	  una base u,v,w ortonormal tal que u y v sean paralelos al plano de proyección. Así, tras el cambio de referencia,
	  que por no ser más que una aplicación afín se descompondrá en una resta y una aplicación lineal, será sencillo
	  calcular las coordenadas de la proyección sobre el plano a través de la proporcionalidad existente con los
	  coeficientes correspondientes a u y v, considerando que el origen de la referencia en el plano esté a su vez en
	  la proyección ortogonal del punto de proyección sobre el mismo plano. Esto, en definitiva, viene a ser una cámara
	  de fotos virtual con una película antepuesta al objetivo. */

	private double distanciaPuntoPlano; //distancia del punto al plano

	private Matr3 matrizTransformacion;

	/**
	 * Constructor de un tipo de proyección, en función de unos parámetros
	 * geométricos (definidos en el código)
	 */
	public Proyeccion(Vect3 puntoProyeccion, Vect3 vectorNormalPlano) {

		this.puntoProyeccion = puntoProyeccion;

		//distancia del punto al plano, que definirá la apertura del cono de proyección y, por tanto, la distancia focal
		distanciaPuntoPlano = 0.1;

		/* los vectores de la nueva base se construyen a partir del normal del plano, de forma apropiada para que
		las coordenadas tras el cambio de base permitan un cálculo sencillo de la proyección sobre el plano */
		w = Vect3.mult(-1, vectorNormalPlano); //normal al plano
		w = Vect3.mult(1 / w.norma(), w); //lo hacemos unitario
		u = new Vect3(-w.dameY(), w.dameX(), 0); //paralelo al plano, marcará la horizontal en el plano de llegada.
		u = Vect3.mult(1 / u.norma(), u);//lo hacemos unitario
		//v = new Vect3 ( w.dameX() , -w.dameZ() , w.dameY() );           //paralelo al plano, marcará la vertical en el plano de llegada. unitario

		Vect2 xY = new Vect2(w.dameX(), w.dameY());
		v = new Vect3(w.dameX() * (w.dameZ() / xY.norma()), w.dameY() * (w.dameZ() / xY.norma()), -xY.norma());

		/* cálculo de la matriz inversa de la formada por u v w como columnas, para luego calcular las
		coordenadas de cualquier vector de R3 en función de la base u,v,w a través de una sencilla transformación lineal */
		matrizTransformacion = new Matr3(u, v, w);
		// Podemos usar la traspuesta como inversa porque es una matrix ortonormal.
		//matrizTransformacion = matrizTransformacion.inversa();
		matrizTransformacion = matrizTransformacion.traspuesta();

	}

	/**
	 * Método que calcula la proyección cónica que representa la visión desde un
	 * punto del espacio. La entrada es por tanto una instancia de Vect3, así
	 * como la salida, cuyas dos primeras coordenadas representarán al vector de
	 * dos dimensiones perteneciente al espacio de llegada, y la tercera la
	 * lejanía (distancia entre punto proyectado y punto de proyección)
	 */
	public Vect2ConDistancia proyecta(Vect3 vectorIni) {

		Vect3 vectorFin = new Vect3(0, 0, 0);

		/*aplicación afín. tenemos que representar el vector de entrada,
		segun la referencia afín formada por el
		punto de proyeccion y la base u,v,w */
		{
			/*cambio origen*/
			vectorIni = Vect3.suma(vectorIni, Vect3.mult(-1, puntoProyeccion));
			/*aplicacion lineal*/
			vectorFin = matrizTransformacion.multiplicaPorVector(vectorIni);
		}

		//se ajustan los coeficientes segun la distancia entre el punto y el plano
		double x = (vectorFin.dameX() * distanciaPuntoPlano / vectorFin.dameZ()) * 7000;
		double y = (vectorFin.dameY() * distanciaPuntoPlano / vectorFin.dameZ()) * 7000;
		double d = (vectorFin.norma()); //distancia ponderada

		return new Vect2ConDistancia(x, y, d);
	}

	public Stack proyectaPila(Stack puntosEntrada) {
		Stack puntos = new Stack();
		while (!puntosEntrada.empty()) {
			puntos.push(proyecta((Vect3) puntosEntrada.pop()));
		}
		;
		return puntos;
	}

}
