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
 * La clase Jugador modela a un jugador como una esfera de radio constante,
 * posici�n variable, apoyada en el suelo, y con la opci�n de golpear la pelota
 * (redefinir su ciclo de par�bolas) mediante unas reglas definidas cuando esta
 * penetra su superficie.
 */
public class Jugador {

	private Vect3 posicion;
	private Vect2 velocidadActual;
	private Vect2 posicionRaqueta, velocidadRaqueta; // Vectores posici�n y velocidad de la raqueta en coordenadas esf�ricas. Origen en centro de jugador
	private double elevacionRaqueta; // Angulo vectorlanzamiento-suelo

	private double velocidad; // M�dulo m�ximo para la velocidadActual

	private boolean yaHaGolpeado;

	final public static double RADIO = 1;
	final public static double ALTURAMAX = 3;
	// Par�metros del cilindro que modela el espacio de acci�n del jugador

	/* Signo del jugador, equivalente al signo de la componente Y de todos los puntos de su campo */
	private int signo;

	/* Hitos acumulados en la partida */
	private int jugadasGanadas; //  1=15  2=30  3=40  4,5,6..=deuce, servicio o victoria
	private int setsGanados;

	/* Buffer de los �ltimos puntos en los que se ha situado el puntero del rat�n
	 * sirve para asignar a la velocidad de la raqueta la media parcial de los TAM
	 * �ltimos vectores de movimiento, y suavizar as� el control por rat�n */
	private Vect2[] puntosRaton;
	private final int TAM = 10; // Longitud del buffer
	private Vect2 puntoNuevo; //Relacionado con la actualizaci�n del buffer de rat�n

	/** Constructor de jugadores segun su signo: (-1) � (1) */
	public Jugador(int signo, Campo campo) {

		this.signo = signo;
		teletransportaJugador(campo);

		velocidadActual = new Vect2(0, 0);
		posicionRaqueta = new Vect2(0, 0);
		velocidadRaqueta = new Vect2(0, 0);
		elevacionRaqueta = 0.4;
		velocidad = 4;

		puntosRaton = new Vect2[TAM];
		for (int i = 0; i < TAM; i++)
			puntosRaton[i] = new Vect2(0, 0);
		puntoNuevo = new Vect2(0, 0);
	}

	//** Utilizado cuando el jugador es humano */
	public void golpeaBola(Bola bola, Campo campo) {
		Vect3 diferencia = Vect3.suma(bola.damePosicion(), Vect3.mult(-1, posicion));
		/* Condici�n que eval�a si la pelota est� dentro del radio de acci�n del jugador, segun el modelo cil�ndrico */
		if ((new Vect2(diferencia.dameX(), diferencia.dameY()).norma() <= RADIO) && (bola.damePosicion().dameZ() < ALTURAMAX) && (yaHaGolpeado == false)) {
			//System.out.println("jugador golpea");
			Vect3 direccionDisparo;
			//Sistema de tiro sobre plano; variaci�n de pendiente. abandonado
			/*
			direccionDisparo = new Vect3(
			        velocidadRaqueta.dameX(),
			        -velocidadRaqueta.dameY()*Math.cos(elevacionRaqueta),
			        velocidadRaqueta.dameY()*Math.sin(elevacionRaqueta)
			        );
			 */
			//Sistema de tiro sobre cono; variaci�n de apertura
			direccionDisparo = new Vect3(velocidadRaqueta.dameX() * Math.cos(elevacionRaqueta), -velocidadRaqueta.dameY() * Math.cos(elevacionRaqueta),
					velocidadRaqueta.norma() * Math.sin(elevacionRaqueta));
			bola.reDefineTrayectoria(direccionDisparo, campo);
			/* Cambia la variable puntuacionJugada del objeto bola segun las reglas
			definidas en la documentaci�n de su clase */
			bola.ponPuntuacionJugada(bola.damePuntuacionJugada() / 5 + signo * 7);
			yaHaGolpeado = true;
			bola.ponSiEsGolpeManual(false);
			//Audio
			double fuerzaGolpe = velocidadRaqueta.norma();
			///System.out.println(fuerzaGolpe);
			Audio.reproduce(4, (fuerzaGolpe - 5) / 10);
		}
		;
		if (bola.damePosicion().dameY() * signo < 0)
			yaHaGolpeado = false;
		return;
	}

	/** Sobrecarga orientada al control m�quina */
	public void golpeaBola(Vect3 direccionDisparo, Bola bola, Campo campo) {
		Vect3 diferencia = Vect3.suma(bola.damePosicion(), Vect3.mult(-1, posicion));
		/* Condici�n que eval�a si la pelota est� dentro del radio de acci�n del jugador, segun el modelo cil�ndrico */
		if ((new Vect2(diferencia.dameX(), diferencia.dameY()).norma() <= RADIO) && (bola.damePosicion().dameZ() < ALTURAMAX) && (yaHaGolpeado == false)) {
			bola.reDefineTrayectoria(direccionDisparo, campo);
			/* Cambia la variable puntuacionJugada del objeto bola segun las reglas
			definidas en la documentaci�n de su clase */
			bola.ponPuntuacionJugada(bola.damePuntuacionJugada() / 5 + signo * 7);
			yaHaGolpeado = true;
			bola.ponSiEsGolpeManual(false);
			//Audio
			double fuerzaGolpe = direccionDisparo.norma();
			///System.out.println(fuerzaGolpe);
			Audio.reproduce(4, (fuerzaGolpe - 5) / 10);
		}
		;
		if ((bola.damePosicion().dameY() * signo) < 0)
			yaHaGolpeado = false;
		return;
	}

	/**
	 * M�todo que hace moverse al jugador en una direcci�n determinada, durante
	 * un tiempo virtual determinado, en funci�n de su velocidad m�xima pero sin
	 * tener en cuenta el par�metro vectorial velocidad actual
	 */
	private void mueveJugador(Vect2 direccion, double tiempo) {

		if (direccion.norma() != 0) {
			direccion = Vect2.mult(velocidad / direccion.norma(), direccion);
			//direccion normalizada con velocidad

			posicion = new Vect3(posicion.dameX() + direccion.dameX() * tiempo, posicion.dameY() + direccion.dameY() * tiempo, posicion.dameZ());

			//evita que atraviese red
			if (posicion.dameY() * signo < RADIO + 1)
				posicion.ponY((RADIO + 1) * signo);

		}
	}

	/**
	 * M�todo que hace moverse al jugador en funci�n de su velocidad almacenada,
	 * durante un tiempo virtual determinado, en funci�n de su velocidad actual
	 */
	public void mueveJugador(double tiempo) {
		mueveJugador(velocidadActual, tiempo);
	}

	/** M�todo que 'teletransporta' al jugador la posici�n de partida */
	public void teletransportaJugador(Campo campo) {
		posicion = new Vect3((signo) * campo.dameLimX() * 0.7, (signo) * campo.dameLimY(), ALTURAMAX / 2);
	}

	/* M�todo que registra una nueva velocidad actual */
	public void ponVelocidad(Vect2 velocidad) {
		velocidadActual = velocidad;
	}

	public Vect3 damePosicion() {
		return posicion;
	}

	public Vect3 damePosicionSombra() {
		return new Vect3(posicion.dameX(), posicion.dameY(), 0);
	}

	public int dameJugadasGanadas() {
		return jugadasGanadas;
	}

	public int dameSetsGanados() {
		return setsGanados;
	}

	public void ponPuntoRaton(Vect2 puntoNuevoo) {
		this.puntoNuevo = puntoNuevoo;
	}

	public void gestionaRaqueta(Vect2 direccionHaciaLaQueMira) {
		for (int h = TAM - 2; h >= 0; h--) {
			puntosRaton[h + 1] = puntosRaton[h];
		}
		puntosRaton[0] = puntoNuevo;
		velocidadRaqueta = Vect2.suma(puntosRaton[0], Vect2.mult(-1, puntosRaton[TAM - 1]));
		velocidadRaqueta = Vect2.mult((0.1 / (double) TAM), velocidadRaqueta);
		Vect2 velocidadPorDefecto = Vect2.mult(0.7, direccionHaciaLaQueMira);
		velocidadRaqueta = Vect2.suma(velocidadPorDefecto, velocidadRaqueta);
		;
		//transformaci�n no lineal para hacer m�s manejable el disparo
		//9/direccionHaciaLaQueMira.norma()

		//velocidadRaqueta.imprimeVector();
	}

	public Vect2 dameVelocidadRaqueta() {
		return velocidadRaqueta;
	}

	public void cambiaInclinacionTiroRaqueta(double incremento) {
		elevacionRaqueta = elevacionRaqueta + incremento;
		if (elevacionRaqueta < 0)
			elevacionRaqueta = 0;
		if (elevacionRaqueta > 1)
			elevacionRaqueta = 1;
	}

	public double dameInclinacionTiroRaqueta() {
		return elevacionRaqueta;
	}

	public void ponJugadasGanadas(int jugadasGanadas) {
		this.jugadasGanadas = jugadasGanadas;
	}

	public void ponSetsGanados(int setsGanados) {
		this.setsGanados = setsGanados;
	}

	public int dameSigno() {
		return signo;
	}

	public void ponYaHaGolpeado(boolean condicion) {
		yaHaGolpeado = condicion;
	}

	private void narcisismo() {
		narcisismo();
	}
}
