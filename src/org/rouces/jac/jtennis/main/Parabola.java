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
package org.rouces.jac.jtennis.main;

import org.rouces.jac.jtennis.algebra.*;

/**
 * La clase ofrece la posibilidad de crear instancias que modelen la trayectoria
 * de la pelota entre dos choques perturbadores, si se define este como
 * cualquier evento que altere la trayectoria parab�lica natural de una pelota
 * ideal en el vac�o.
 * 
 */
public class Parabola {

	/* Gravedad */
	static public double g = -10;

	/* Posici�n y velocidad de la pelota al inicio de la par�bola */
	private Vect3 posicionIni, velocidadIni;

	/* Tiempo total de vida de la par�bola y tiempo virtual consumido.
	 Mientras la posici�n de la pelota en el juego no pertenezca a�n a
	 una par�bola determinada, la segunda valdr� cero, y cuando la segunda
	 supere a la primera, la par�bola habr� caducado */
	private double tiempoDuracionParabola, tiempoConsumido;

	/* Determina si el evento que pone fin a la par�bola tiene relevancia
	desde un punto de vista directo en la puntuaci�n de la jugada. Si esto
	es as� es porque golpea contra el suelo, no contra la red, y su valor ser�
	distinto dependiendo de en qu� lado del campo golpee y si lo hace dentro o
	fuera del �rea v�lida. Si el evento no tiene relevancia, valdr� cero */
	private int relevanciaPuntuacion;

	/**
	 * Constructor de una par�bola en funci�n de la posici�n inicial de la
	 * pelota que la describir�, su velocidad inicial y el campo de juego, cuyos
	 * par�metros espaciales determinar�n cu�ndo llege a su fin por la acci�n de
	 * un choque perturbador
	 */
	public Parabola(Vect3 posicionIni, Vect3 velocidadIni, Campo campo) {

		this.posicionIni = posicionIni;
		this.velocidadIni = velocidadIni;

		//###### �Cu�ndo chocar�a contra el suelo? ######
		double tiempoChoqueSuelo1 = -(velocidadIni.dameZ() + Math.sqrt(Math.pow(velocidadIni.dameZ(), 2) - 2 * g * (posicionIni.dameZ() - Bola.RADIO))) / g;
		double tiempoChoqueSuelo2 = -(velocidadIni.dameZ() - Math.sqrt(Math.pow(velocidadIni.dameZ(), 2) - 2 * g * (posicionIni.dameZ() - Bola.RADIO))) / g;
		double tiempoChoqueSuelo = Math.max(tiempoChoqueSuelo1, tiempoChoqueSuelo2);
		//El resultado v�lido tiene que ser positivo. En este caso s�lo hay uno positivo, luego es el mayor.

		//###### �Cu�ndo chocar�a contra la red? ...si lo hace ######
		double tiempoChoqueRed = -posicionIni.dameY() / velocidadIni.dameY();
		if (!((posicionIni.dameZ() + velocidadIni.dameZ() * tiempoChoqueRed + g * Math.pow(tiempoChoqueRed, 2) / 2 < campo.dameAltoRed() + Bola.RADIO)
				&& (posicionIni.dameZ() + velocidadIni.dameZ() * tiempoChoqueRed + g * Math.pow(tiempoChoqueRed, 2) / 2 > 0) && (Math.abs(posicionIni.dameX()
				+ velocidadIni.dameX() * tiempoChoqueRed) < campo.dameAnchoRed())))
			tiempoChoqueRed = 100000;
		if (tiempoChoqueRed <= 0)
			tiempoChoqueRed = 100000;
		//el 100000 equivale a que no habr� choque contra la red
		//System.out.println(tiempoChoqueRed);

		//###### Este ser� el tiempo de vida pr�ctico de la par�bola
		//###### como representante de la trayectoria (entre choque y choque)
		tiempoDuracionParabola = Math.min(tiempoChoqueSuelo, tiempoChoqueRed);
		//System.out.println(tiempoDuracionParabola);

		// Se eval�a si el final de esta par�bola tendr�, de llegar a darse,
		// repercusi�n sobre el �ndice de puntuaci�n de la jugada
		if (tiempoChoqueSuelo == tiempoDuracionParabola) {
			if ((Math.abs(damePosicionFin().dameX()) <= campo.dameLimX()) && (Math.abs(damePosicionFin().dameY()) <= campo.dameLimY()))
				relevanciaPuntuacion = 1;
			else
				relevanciaPuntuacion = 3;
			relevanciaPuntuacion = relevanciaPuntuacion * (int) (damePosicionFin().dameY() / Math.abs(damePosicionFin().dameY())); //marca el signo
		} else
			relevanciaPuntuacion = 0;

		tiempoConsumido = 0;

	}

	/**
	 * Devuelve la posicion de la pelota al final de la par�bola. Sirve para la
	 * construcci�n de la nueva par�bola en la cola de trayectorias, no para la
	 * representaci�n gr�fica
	 */
	public Vect3 damePosicionFin() {
		return damePosicion(tiempoDuracionParabola);
	}

	/**
	 * Devuelve la velocidad de la pelota al final de la par�bola
	 * (inmediatamente tras el choque), en funci�n de los par�metros f�sicos del
	 * campo que entra como par�metro. Sirve para la construcci�n de la nueva
	 * par�bola en la cola de trayectorias, no para la representaci�n gr�fica
	 */
	public Vect3 dameVelocidadFinModificada(Campo campo) {
		Vect3 temp = dameVelocidad(tiempoDuracionParabola);
		if (relevanciaPuntuacion != 0) {
			temp.ponX(temp.dameX() * 0.96);
			temp.ponY(temp.dameY() * 0.96);
			if (temp.dameZ() < -0.3) { //condici�n para que la pelota 'ruede'
				temp.ponZ(-temp.dameZ() * campo.dameCoefSuelo()); //el choque con el suelo modifica la velocidad
			} else {
				temp.ponZ(-temp.dameZ());
				if (temp.norma() < 0.25)
					temp = new Vect3(0, 0, 0.3);
			}
		} else

		if (damePosicionFin().dameZ() < campo.dameAltoRed() + Bola.RADIO - Campo.margenFlexibleRed) { //el choque con la red modifica la velocidad en y
			temp.ponY(-temp.dameY() * campo.dameCoefRed());
		} else { //si pega en el margen flexible (borde superior de red), se simula efecto match point
			double fact = damePosicionFin().dameZ() - Bola.RADIO - campo.dameAltoRed() + Campo.margenFlexibleRed;
			if (temp.dameZ() < 0)
				temp.ponZ(temp.dameZ() + Math.abs(temp.dameY() / 2));
			temp.ponY(temp.dameY() / 3);
			temp.ponX(temp.dameX() / 2);
		}

		return temp;
	}

	/** Devuelve la posici�n de la pelota, seg�n la variable tiempoTranscurrido */
	public Vect3 damePosicion() {
		return new Vect3(posicionIni.dameX() + velocidadIni.dameX() * tiempoConsumido, posicionIni.dameY() + velocidadIni.dameY() * tiempoConsumido,
				posicionIni.dameZ() + velocidadIni.dameZ() * tiempoConsumido + g * Math.pow(tiempoConsumido, 2) / 2);
	}

	/** Devuelve la posici�n de la pelota, seg�n un tiempo determinado */
	public Vect3 damePosicion(double tiempo) {
		return new Vect3(posicionIni.dameX() + velocidadIni.dameX() * tiempo, posicionIni.dameY() + velocidadIni.dameY() * tiempo, posicionIni.dameZ()
				+ velocidadIni.dameZ() * tiempo + g * Math.pow(tiempo, 2) / 2);
	}

	/** Devuelve la velocidad de la pelota, seg�n la variable tiempoTranscurrido */
	public Vect3 dameVelocidad() {
		return new Vect3(velocidadIni.dameX(), velocidadIni.dameY(), velocidadIni.dameZ() + g * tiempoConsumido);
	}

	/** Devuelve la velocidad de la pelota, seg�n un tiempo determinado */
	public Vect3 dameVelocidad(double tiempo) {
		return new Vect3(velocidadIni.dameX(), velocidadIni.dameY(), velocidadIni.dameZ() + g * tiempo);
	}

	/**
	 * Actualiza el tiempo transcurrido sobre esta par�bola, para un transcurso
	 * de tiempo virtual t
	 */
	public void actualizaTiempo(double t) {
		tiempoConsumido += t;
	}

	/**
	 * Devuelve, si existe, el exceso del tiempo transcurrido de la par�bola con
	 * respecto al tiempo de duraci�n. Esto servir� para ajustar el comienzo de
	 * la siguiente par�bola en la cola
	 */
	public double excesoTiempo() {
		return tiempoConsumido - tiempoDuracionParabola; //si es negativo se considera inexistente
	}

	/**
	 * Devuelve la relevancia en la puntuaci�n de la jugada del bote p�stumo de
	 * esta par�bola
	 */
	public int dameRelevanciaPuntuacion() {
		return relevanciaPuntuacion;
	}

	/** Devuelve la mitad del tiempo de vida. Orientado a la IA */
	public double dameTiempoMitad() {
		return tiempoDuracionParabola / 2;
	}

	/**
	 * Devuelve los momentos temporales contados desde el incio de la par�bola
	 * en los que la pelota alcanzar� determinada altura. Se encapsulan en un
	 * Vect2, pero no es un vector de posici�n en el espacio. La coordenada X es
	 * la menor. Orientado a la IA
	 */
	public Vect2 dameTiempoAltura(double altura) {
		double tiempo1 = -(velocidadIni.dameZ() - Math.sqrt(Math.pow(velocidadIni.dameZ(), 2) - 2 * g * (posicionIni.dameZ() - altura))) / g;
		double tiempo2 = -(velocidadIni.dameZ() + Math.sqrt(Math.pow(velocidadIni.dameZ(), 2) - 2 * g * (posicionIni.dameZ() - altura))) / g;
		return new Vect2(tiempo1, tiempo2);
	}

}
