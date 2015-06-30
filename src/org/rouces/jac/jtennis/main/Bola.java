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
 * La clase Bola ofrece la posibilidad de crear una instancia que represente la
 * pelota del juego. Sus variables físicas se almacenan en un array cíclico de
 * objetos parábola, y además guarda una variable referida a la memoria de qué
 * es lo último que ha golpeado, que servirá para determinar cuándo se gana una
 * jugada y quién es el ganador. La clase pelota, a diferencia de la de Jugador,
 * se instanciará cada vez que una jugada termine y empiece otra, algo que se ve
 * reflejado en su constructor.
 */
public class Bola {

	/** Sólo tiene relevancia en el motor lógico, no en la visualización */
	public static final double RADIO = 0.1;

	private Parabola[] trayectoria; //array trayectorias (a hacer cíclico)
	private int i; //índice del array (a hacer cíclico mediante módulos)
	private final int LIM = 10; //longitud del ciclo

	private int puntuacionJugada; /* Esta variable se encarga de almacenar dónde han sido los últimos golpes,
									con la finalidad de que un método externo pueda distinguir cuándo la jugada ha sido ganada (por esto, sólo
									varía ante golpes contra suelo o raqueta, ya que la red solo tiene repercusión indirecta).
									Sus transformaciones, inducidas por otros objetos, han de ser las siguientes:
									 
									Cuando la pelota ha chocado dentro del campo, del lado del jugador de signo s:   +s
									Cuando la pelota ha chocado fuera del campo, del lado del jugador de signo s:    +s*3
									Cuando la pelota ha chocado contra la raqueta del jugador de signo s:            /5+s*7
									 
									Se puede observar que si se define que cuando la pelota parte de la raqueta de un jugador de signo s,
									la variable adquiere el valor s*6, las transformaciones anteriores definen un sistema coherente y cerrado
									(mientras la jugada no sea ganada por nadie), cuya tabla de valores puede servir para determinar quien gana el
									juego.
									 
									s*-7  ,  s*-9  ,  s*-3  ,  s*4  ,  s*2  ,  s*-8    ->    jugador de signo s gana la jugada                          
									*/

	private boolean esUnSaque;
	private boolean esUnGolpeManual;

	/**
	 * Constructor que coloca la pelota para el saque. El parámetro de entrada
	 * indica qué jugador saca: 1 ó -1, y el booleano si huboMediaFalta
	 */
	public Bola(Jugador jugadorQueSaca, Campo campo, boolean esUnSaque) {

		trayectoria = new Parabola[LIM];
		i = LIM;
		/* No se iguala a 0 para evitar acceso a -1. Que este parámetro exceda LIM no es un problema ya que el
		acceso se hará siempre a través de una operación resto */

		// Velocidad con la que el jugador lanza la pelota hacia arriba con la mano en el saque.
		Vect3 velocidadManualSaque = new Vect3(0, jugadorQueSaca.dameSigno() * 0.001, 3);

		/* Conjunto de órdenes similares a las del método redefineTrayectoria, con la diferencia de que la posición
		inicial 'parte de fuera', por lo que sirve para el momento en el que se construye la pelota y no hay autoreferencia
		alguna sobre su posición. La idea era colocarlo como método sobrecargado con un parámetro extra, pero el
		constructor no nos deja llamar a métodos del propio objeto con el mismo nombre (o eso nos ha parecido) */
		{
			Vect3 manoAlzada = Vect3.suma(jugadorQueSaca.damePosicion(), new Vect3(0, -jugadorQueSaca.dameSigno() * 0.01, Jugador.ALTURAMAX / 2 + 0.1));
			trayectoria[i % LIM] = new Parabola(manoAlzada, velocidadManualSaque, campo);
			//trayectoria[i%LIM].dameVelocidadFinModificada(campo).imprimeVector();

			for (int j = 1; j < LIM; j++) {
				trayectoria[(i + j) % LIM] = new Parabola(trayectoria[(i + j - 1) % LIM].damePosicionFin(),
						trayectoria[(i + j - 1) % LIM].dameVelocidadFinModificada(campo), campo);
				//trayectoria[(i+j-1)%LIM].damePosicionFin().imprimeVector();
			}
		}

		puntuacionJugada = -jugadorQueSaca.dameSigno() * 6;
		/* Ajuste para que lo correcto sea que este jugador golpee la pelota,
		haciendo como si viniera golpeada por el otro */
		this.esUnSaque = esUnSaque;
		esUnGolpeManual = true;

	}

	/**
	 * Método que actualiza las variables que determinan la posición de la
	 * pelota, para un transcurso de tiempo virtual t en un campo determinado
	 */
	public void actualizaBola(double t, Campo campo) {

		//System.out.println(puntuacionJugada);
		trayectoria[i % LIM].actualizaTiempo(t);
		double exceso = trayectoria[i % LIM].excesoTiempo();

		if (exceso > 0) { /* Entonces la parábola marcada por el índice ha caducado */

			//Audio
			double fuerzaGolpe = Math.abs(trayectoria[i % LIM].dameVelocidad().dameZ());
			//System.out.println(fuerzaGolpe);
			if (fuerzaGolpe > 1)
				Audio.reproduce(3, fuerzaGolpe / 15);
			//Math.atan(fuerzaGolpe/3)

			puntuacionJugada += trayectoria[i % LIM].dameRelevanciaPuntuacion();
			/* Se cambia la variable de puntuacionJugada en función de si el fin de la parábola consumida supone algo relevante
			al respecto (choque suelo) */

			trayectoria[i % LIM] = new Parabola(trayectoria[(i - 1) % LIM].damePosicionFin(), trayectoria[(i - 1) % LIM].dameVelocidadFinModificada(campo),
					campo);
			/* Se crea una nueva a partir de los resultados finales de la última calculada, que es la anterior en el ciclo */

			i++;
			trayectoria[i % LIM].actualizaTiempo(exceso);
			/* Se actualiza el índice para que apunte a la parábola siguiente, y se ajusta el tiempo virtual de vida de la parábola recién creada
			    en función de lo que le corresponde del último periodo de evaluación*/

			if (i == (LIM * 30))
				i = LIM; // Evita que el índice se vaya de madre en hipotéticas partidas largas. No se iguala a 0 para evitar acceso a -1

		}

	}

	/** Método que devuelve la posición de la pelota */
	public Vect3 damePosicion() {
		Vect3 posicionAlterada = trayectoria[i % LIM].damePosicion();
		//posicionAlterada.ponZ(posicionAlterada.dameZ()+0.1);
		return posicionAlterada;
	}

	/** Método que devuelve la posición de la sombra vertical de la pelota */
	public Vect3 damePosicionSombra() {
		return new Vect3(trayectoria[i % LIM].damePosicion().dameX(), trayectoria[i % LIM].damePosicion().dameY(), 0);
	}

	/** Método que devuelve la velocidad de la pelota */
	public Vect3 dameVelocidad() {
		return trayectoria[i % LIM].dameVelocidad();
	}

	/** Método que devuelve la puntuación de la jugada */
	public int damePuntuacionJugada() {
		return puntuacionJugada;
	}

	/**
	 * Método que devuelve si la pelota está en estado de saque, para gestionar
	 * las medias faltas
	 */
	public boolean dameSiEsSaque() {
		return esUnSaque;
	}

	/**
	 * Método que escribe si la pelota está en estado de saque, para gestionar
	 * las medias faltas
	 */
	public void ponSiEsSaque(boolean condicion) {
		esUnSaque = condicion;
	}

	/**
	 * Método que devuelve si la pelota ha sido lanzada manualmente, para
	 * gestionar las medias faltas
	 */
	public boolean dameSiEsGolpeManual() {
		return esUnGolpeManual;
	}

	/**
	 * Método que escribe si la pelota ha sido lanzada manualmente, para
	 * gestionar las medias faltas
	 */
	public void ponSiEsGolpeManual(boolean condicion) {
		esUnGolpeManual = condicion;
	}

	/** Método que escribe la puntuación de la jugada */
	public void ponPuntuacionJugada(int puntuacionJugada) {
		this.puntuacionJugada = (byte) puntuacionJugada;
		return;
	}

	/**
	 * Método que redefine todo el ciclo de parábolas previstas para una pelota.
	 * Es necesario para cuando la pelota es afectada por un suceso no previsto
	 * a priori, no determinista dentro de la lógica física del juego, que da
	 * lugar a una reacción en cadena de cambios
	 */
	public void reDefineTrayectoria(Vect3 velocidadIni, Campo campo) {
		trayectoria[i % LIM] = new Parabola(trayectoria[i % LIM].damePosicion(), velocidadIni, campo);
		for (int j = 1; j < LIM; j++) {
			trayectoria[(i + j) % LIM] = new Parabola(trayectoria[(i + j - 1) % LIM].damePosicionFin(),
					trayectoria[(i + j - 1) % LIM].dameVelocidadFinModificada(campo), campo);
		}
	}

	/** Método para ser llamado por la inteligencia artificial */
	public Parabola dameParabola(int adelanto) {
		return trayectoria[(i + adelanto) % LIM];
	}

}
