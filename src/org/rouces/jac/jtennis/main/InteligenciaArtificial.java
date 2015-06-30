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
 * La clase Inteligencia Artificial ofrece la posibilidad de crear instancias
 * que, mediante métodos adecuados, gobiernan el comportamiento del jugador
 * correspondiente a la máquina de forma que este no se diferencie del
 * correspondiente al jugador humano. Su inteligencia se basa en la capacidad de
 * predicción que permite acceder al ciclo de parábolas de la pelota. El método
 * dameVectorSalida() es mejorable.
 */
public class InteligenciaArtificial {

	public static final double DEMORA_SAQUE = 2;

	private Campo campo;
	private Bola bola;
	private Jugador ego, contrario;
	private int capacidad;
	private int estadoDeSaque;

	//habilidades que dependen de la capacidad
	private double umbralDecision;
	private double precision;

	private double tamanoDeFranjaRedProhibida;

	private Vect2 miPosicion, primerBote, segundoBote;
	private Parabola parabolaActual, parabolaSiguiente;
	private boolean noQuieroGolpear;

	public InteligenciaArtificial(Campo campo, Bola bola, Jugador ego, Jugador contrario, int dificultad) {

		this.campo = campo;
		this.bola = bola;
		this.contrario = contrario;
		this.ego = ego;
		capacidad = dificultad;
		switch (capacidad) {
		case 1:
			umbralDecision = -2;
			break;
		case 2:
			umbralDecision = 0;
			break;
		case 3:
			umbralDecision = 2;
			break;
		}
		switch (capacidad) {
		case 1:
			precision = 2;
			break;
		case 2:
			precision = 1;
			break;
		case 3:
			precision = 0.5;
			break;
		}
		tamanoDeFranjaRedProhibida = 2;
		noQuieroGolpear = false;
	}

	public Vect2 dameDondeIr() {
		int signo = ego.dameSigno();
		//System.out.println(campo.dameLimY());
		Vect2 posicionPorDefecto;// = new Vect2(0,-campo.dameLimY()/1.5);
		double miX = contrario.damePosicion().dameX() / 5;
		if (miX > campo.dameLimX() / 2)
			miX = campo.dameLimX() / 2;
		if (miX < -campo.dameLimX() / 2)
			miX = -campo.dameLimX() / 2;
		double miY = signo * campo.dameLimY() / 3 - contrario.damePosicion().dameY() / 3;
		//miY = signo*campo.dameLimY();
		posicionPorDefecto = new Vect2(miX, miY); //este cálculo de la posicion por defecto es mas estético, para que el juego sea dinámico, que eficiente
		//posicionPorDefecto = new Vect2(0,-campo.dameLimY()/1.5);
		Vect2 destino = posicionPorDefecto;
		//posicionPorDefecto.imprimeVector();
		//System.out.println("va");

		//Calcula primer y segundo bote sólo cuando esté en el campo contrario, para evitar confusión tras el primer bote
		if (-bola.damePosicion().dameY() * signo > 0) {
			parabolaActual = bola.dameParabola(0);
			parabolaSiguiente = bola.dameParabola(1);
			primerBote = new Vect2(parabolaActual.damePosicionFin().dameX(), parabolaActual.damePosicionFin().dameY());
			segundoBote = new Vect2(parabolaSiguiente.damePosicionFin().dameX(), parabolaSiguiente.damePosicionFin().dameY());
		}

		noQuieroGolpear = false;

		//Actúa a partir del momento especificado por la capacidad, simulando distintos grados de previsión
		if ((bola.damePosicion().dameY() < umbralDecision) && (bola.dameVelocidad().dameY() * ego.dameSigno() > 0)) {
			miPosicion = new Vect2(ego.damePosicion().dameX(), ego.damePosicion().dameY());
			Vect2 puntoOrtogonal = dameOrtogonal(primerBote, segundoBote, miPosicion); //Cálculo del punto más cercano en la recta de trayectoria
			if (puntoOrtogonal.dameY() * signo < primerBote.dameY() * signo)
				puntoOrtogonal = primerBote; //Reajustes. Tratará de evitar el voleo, lo cual no significa que no lo juegue
			if (puntoOrtogonal.dameY() * signo > segundoBote.dameY() * signo)
				puntoOrtogonal = segundoBote; //Trata de evitar varios botes en el campo propio
			if ((parabolaActual != null) && (parabolaSiguiente != null)) { //Reajuste para evitar vaselinas de segundo bote
				double max2 = parabolaSiguiente.damePosicion(parabolaSiguiente.dameTiempoMitad()).dameZ();
				if (max2 > Jugador.ALTURAMAX) {
					Vect3 fronteraAlturaSig1 = parabolaSiguiente.damePosicion(parabolaSiguiente.dameTiempoAltura(Jugador.ALTURAMAX).dameX());
					Vect3 fronteraAlturaSig2 = parabolaSiguiente.damePosicion(parabolaSiguiente.dameTiempoAltura(Jugador.ALTURAMAX).dameY());
					if ((puntoOrtogonal.dameY() * signo > fronteraAlturaSig1.dameY() * signo)
							&& (puntoOrtogonal.dameY() * signo < fronteraAlturaSig2.dameY() * signo)) {
						Vect2 fronteraAlturaSig1Bis = new Vect2(fronteraAlturaSig1.dameX(), fronteraAlturaSig1.dameY());
						Vect2 fronteraAlturaSig2Bis = new Vect2(fronteraAlturaSig2.dameX(), fronteraAlturaSig2.dameY());
						double distancia1 = Vect2.suma(fronteraAlturaSig1Bis, Vect2.mult(-1, puntoOrtogonal)).norma();
						double distancia2 = Vect2.suma(fronteraAlturaSig2Bis, Vect2.mult(-1, puntoOrtogonal)).norma();
						if (distancia1 < distancia2)
							puntoOrtogonal = fronteraAlturaSig1Bis;
						else
							puntoOrtogonal = fronteraAlturaSig2Bis;
					}
				}
			}

			if ((capacidad != 1) && (bola.dameSiEsGolpeManual() == false)) { //Si el contrario la lanza fuera, no golpeo
				if ((primerBote.dameY() * signo > campo.dameLimY()) || (Math.abs(primerBote.dameX()) > campo.dameLimX())) {
					puntoOrtogonal = posicionPorDefecto;
					noQuieroGolpear = true;
				}
			}
			destino = puntoOrtogonal;
			if (bola.dameSiEsGolpeManual() == true)
				destino = miPosicion; //si saco, no me voy a recoger champiñones
		}
		return destino;
	}

	public void mueve(Vect2 destino, double tiempo) {
		Vect2 posicion = new Vect2(ego.damePosicion().dameX(), ego.damePosicion().dameY());
		Vect2 direccion = Vect2.suma(destino, Vect2.mult(-1, posicion));
		if (direccion.norma() > 0.1) {
			ego.ponVelocidad(direccion);
			ego.mueveJugador(tiempo);
		}
	}

	public void mueve(double tiempo) {
		try {
			mueve(dameDondeIr(), tiempo);
		} catch (Exception e) {
			System.out.println(e.toString());
			System.out.println("No se pudo mover al contrario");
		}
	}

	private Vect2 dameObjetivoBola() {

		Vect2 objetivo = new Vect2(0, 0);

		if (capacidad == 1) {
			//Se la lanza chupada al contrario
			Vect2 otro = new Vect2(contrario.damePosicion().dameX(), contrario.damePosicion().dameY());
			Vect2 yo = new Vect2(ego.damePosicion().dameX(), ego.damePosicion().dameY());
			Vect2 yoOtro = Vect2.suma(otro, Vect2.mult(-1, yo));
			yoOtro = Vect2.mult(0.7, yoOtro);
			objetivo = Vect2.suma(yo, yoOtro);
		}
		if ((capacidad == 2) || (capacidad == 3)) {

			//Buscamos punto más alejado operando con módulos en cada coordenada de forma independiente
			//Pequeñas modificaciones segun la capacidad
			double xAlejado;
			if (contrario.damePosicion().dameX() <= 0)
				xAlejado = (contrario.damePosicion().dameX() + campo.dameLimX() - (3 - capacidad));
			else
				xAlejado = (contrario.damePosicion().dameX() - campo.dameLimX() + (3 - capacidad));
			double yAlejado = (contrario.damePosicion().dameY() + (campo.dameLimY() / (5.5 - capacidad))) % campo.dameLimY();
			//System.out.println(xAlejado+"   "+yAlejado);
			//objetivo=new Vect2(xAlejado,yAlejado);

			//Una tangente como simulación cutre de la función de densidad de una normal
			//Esto evita un determinismo flagrante
			double modificadorAleatorioX = Math.tan((Math.random() - 0.5) * 3); //evitamos los valores asintóticos, cuyos valores son demasiado grandes
			double modificadorAleatorioY = Math.tan((Math.random() - 0.5) * 3); //restringiendo el intervalo origen a (-1.5 , 1.5)
			objetivo = new Vect2(xAlejado + modificadorAleatorioX, yAlejado + modificadorAleatorioY);
			//System.out.println(modificadorAleatorioY);
		}

		//Tiene en cuenta no apuntar fuera del campo (sobre todo orientado al método de capacidad 1)
		if (Math.abs(objetivo.dameX()) > campo.dameLimX())
			objetivo.ponX((campo.dameLimX() - 0.5) * objetivo.dameX() / Math.abs(objetivo.dameX()));
		if (contrario.dameSigno() * objetivo.dameY() > campo.dameLimY())
			objetivo.ponY((campo.dameLimY() - 0.5) * contrario.dameSigno());
		if (contrario.dameSigno() * objetivo.dameY() < tamanoDeFranjaRedProhibida)
			objetivo.ponY(tamanoDeFranjaRedProhibida);

		return objetivo;
	}

	//** Mejorable */
	private Vect3 dameVectorSalida(Vect2 destino) {

		Vect2 miPosicion = new Vect2(bola.damePosicion().dameX(), bola.damePosicion().dameY());
		Vect2 suPosicion = destino;

		//Vect2 miPosicion = new Vect2( ego.damePosicion().dameX() , ego.damePosicion().dameY() );
		//Vect2 suPosicion = new Vect2( contrario.damePosicion().dameX() , contrario.damePosicion().dameY() );

		Vect2 vectorYoEl = Vect2.suma(suPosicion, Vect2.mult(-1, miPosicion));
		double distanciaYoEl = vectorYoEl.norma();

		//punto del suelo perteneciente a la red y en medio de la recta ego-contrario, medido desde miposicion
		Vect2 puntoRed = Vect2.mult(Math.abs(miPosicion.dameY() / (miPosicion.dameY() - suPosicion.dameY())), vectorYoEl);
		//puntoRed.imprimeVector();
		double distanciaYoPuntoRed = puntoRed.norma();

		double alturaInicial = bola.damePosicion().dameZ();

		/*
		//ecuación que resume una incógnita gracias a alturaEnLaRed, partidita para hacerla inteligible
		double alturaEnLaRed=6;
		double parentesis1 = distanciaYoEl-distanciaYoPuntoRed;
		double parentesis2 = distanciaYoEl*distanciaYoPuntoRed*parentesis1;
		double denominador = alturaInicial*parentesis1 - alturaEnLaRed*distanciaYoEl;
		double normaVelocidadXY = Math.sqrt(  ((Parabola.g/2)*parentesis2+alturaEnLaRed) / denominador  );
		 
		Vect2 componenteVelocidadXY = Vect2.mult( normaVelocidadXY/distanciaYoEl , vectorYoEl );
		 
		//double componenteVelocidadZ =  ( Math.pow(normaVelocidadXY,2)*Jugador.RADIO + (Parabola.g/2)*distanciaYoEl ) / -distanciaYoEl*normaVelocidadXY ;
		double parentesis = (Parabola.g/2)*(Math.pow(distanciaYoEl/normaVelocidadXY,2))+alturaInicial;
		double componenteVelocidadZ = parentesis * normaVelocidadXY  / distanciaYoEl   ;
		 */
		//lo de arriba tiene algún fallo, y para luego empeorarlo con randoms que simulen imperfección humana,
		//prefiero hacer algo más cutre pero equivalente en cuanto a resultados

		double componenteVelocidadZ = 6 + Math.random() * 5 - 2 * alturaInicial; //aproximación baratilla
		//componenteVelocidadZ = 4;

		if ((capacidad != 1) && (Math.random() < 0.5) && (alturaInicial > Jugador.ALTURAMAX - 0.5)) { //mate; bien para saque, bien para pelota alta
			if (contrario.dameSigno() * destino.dameY() < campo.dameLimY() / 1.2)
				destino.ponY(contrario.dameSigno() * campo.dameLimY() / 1.2);
			componenteVelocidadZ = Math.random() + 3 - capacidad;
		}

		//if (alturaInicial>Jugador.ALTURAMAX-0.5)   componenteVelocidadZ=1; //aprovechar altura para mate, o saque difícil

		double radical = Math.sqrt(Math.pow(componenteVelocidadZ, 2) - 4 * (Parabola.g / 2) * alturaInicial);
		double t = (-componenteVelocidadZ - radical) / (2 * (Parabola.g / 2));

		double normaVelocidadXY = distanciaYoEl / t;

		Vect2 componenteVelocidadXY = Vect2.mult(normaVelocidadXY / distanciaYoEl, vectorYoEl);

		return new Vect3(componenteVelocidadXY.dameX(), componenteVelocidadXY.dameY(), componenteVelocidadZ);
	}

	public void golpea() {
		if (noQuieroGolpear == false) {
			//if ( Vect3.suma( ego.damePosicion() , Vect3.mult(-1,bola.damePosicion()) ).norma()  < Jugador.RADIO+1 ) {

			//Vect3 diferencia = Vect3.suma( bola.damePosicion(),Vect3.mult(-1,ego.damePosicion()) );
			//if (( new Vect2(diferencia.dameX(),diferencia.dameY()).norma() <= Jugador.RADIO )&&(bola.damePosicion().dameZ()<Jugador.ALTURAMAX)) {

			Vect2 objetivo = dameObjetivoBola();
			Vect3 vectorSalida = Vect3.suma(dameVectorSalida(objetivo), new Vect3((Math.random() - 0.5) * precision, (Math.random() - 0.5) * precision, 0));
			//Error aleatorio de muñeca, en función de la capacidad de la IA

			ego.golpeaBola(vectorSalida, bola, campo);
			//dameVectorSalida(vector).imprimeVector();
		}
	}

	private Vect2 dameOrtogonal(Vect2 pr1, Vect2 pr2, Vect2 p) {
		double r;
		double p1x = pr1.dameX();
		double p1y = pr1.dameY();
		double vx = pr1.dameX() - pr2.dameX();
		double vy = pr1.dameY() - pr2.dameY();
		double px = p.dameX();
		double py = p.dameY();
		if ((vx == 0) && (vy == 0))
			r = 0;
		else
			r = -(p1x * vx - px * vx + p1y * vy - py * vy) / (Math.pow(vx, 2) + Math.pow(vy, 2));
		return new Vect2((p1x + r * vx), (p1y + r * vy));
	}

	public void ponBola(Bola bola) {
		this.bola = bola;
	}

}
