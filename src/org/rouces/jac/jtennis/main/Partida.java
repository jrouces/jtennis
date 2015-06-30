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
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;

import javax.swing.*;

import org.rouces.jac.jtennis.algebra.*;

import java.util.Stack;

/**
 * La clase Partida gestiona el bucle principal del juego desempeñando el rol de
 * escuchador de un Timer, el pintado 2d y la gestión de eventos teclado/ratón
 */
public class Partida extends Canvas implements ActionListener {

	Partida esto;
	private JFrame ventana;
	private JPanel panelConfirmacion;
	private Timer timer;
	private int numeroSets, tipoCampo, dificultad;

	private Campo campo;
	private Jugador jugadorPositivo; //Hombre
	private Jugador jugadorNegativo; //Máquina
	private Bola bola;
	private InteligenciaArtificial inteligenciaNegativo;

	private Proyeccion proyeccion;
	private Vect3 vectorEjeJugador;
	//private boolean camaraFija; //por si se desea añadir una forma de seleccionar un modo de juego con cámara fija
	private double alturaCamara;

	private final int X = 400; //semiancho pantalla
	private final int Y = 300; //semialto pantalla

	private double periodo; /* Tiempo virtual en segundos que ha de pasar entre
							ldos evaluaciones del juego. Las distancias, aceleraciones y
							demás medidas del juego en general se han puesto en metros, segundos y unidades derivadas
							para una fácil implementación e interpretación del código
							*/
	private double tempo; /* Tiempo real en segundos que ha de pasar entre dos evaluaciones
							del juego. si es igual al tiempo virtual, los movimientos del juego serán realistas
							desde una óptica terrestre. sin embargo podrá diferir de este si es necesario ralentizar
							el juego en pos de una mayor facilidad de juego
							*/
	private double tiempoEspera; /* Tiempo virtual de espera entre el momento en el que una jugada
									es ganada y el momento en el que se dispone lo necesario para una nueva */
	private double tiempoEsperado; /* Contador hasta tiempoEspera */

	private long marcador = 0; //Propósito general

	private Vect2 direccion; //direccion en memoria de movimiento del jugador humano (jugador positivo)
	private double mouseApretado; //variable que guarda el estado del ratón. permite una acción continua manteniendo pulsado un botón

	private int estadoDeSaque; // 1 si saca el jugador positivo, -1 si lo hace el negativo y 0 durante el transcurso de la partida
	private boolean secondChance; //indica si existe una segunda oportunidad debido a las medias faltas
	private double tiempoRestanteHastaSaqueMaquina;

	/** Constructor de Partida */
	public Partida(int numeroSets, int tipoCampo, int dificultad, JFrame ventanaPrograma) {

		esto = this;
		this.numeroSets = numeroSets;
		this.tipoCampo = tipoCampo;
		this.dificultad = dificultad;
		this.ventana = ventanaPrograma;

		ventana.getContentPane().add(this);
		ventana.setVisible(true);

		campo = new Campo(tipoCampo);
		jugadorPositivo = new Jugador(1, campo); //hombre
		jugadorNegativo = new Jugador(-1, campo); //máquina
		inteligenciaNegativo = new InteligenciaArtificial(campo, bola, jugadorNegativo, jugadorPositivo, dificultad);

		Vect3 puntoProyeccion = new Vect3(0, 20, 8);
		Vect3 vectorNormalPlano = new Vect3(0, 1, 0.5);
		vectorEjeJugador = new Vect3(0, 1, 0);
		alturaCamara = 6;
		proyeccion = new Proyeccion(puntoProyeccion, vectorNormalPlano);

		periodo = 0.03;
		tempo = periodo / 1; //iguales mientras el juego discurra en tiempo real

		tiempoEspera = 10;
		tiempoEsperado = -1;
		//reiniciaJugada();
		secondChance = true;
		direccion = new Vect2(0, 0);
		mouseApretado = 0;

		// Asociación a adaptadores anónimos para el manejo en tiempo real del juego

		this.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				//System.out.println(e.getSource());
				switch (e.getKeyCode()) {
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
					if (estadoDeSaque == 1)
						estadoDeSaque = 0;
					else
						direccion.ponY(1);
					break;
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W:
					if (estadoDeSaque == 1)
						estadoDeSaque = 0;
					else
						direccion.ponY(-1);
					break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
					direccion.ponX(-1);
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					direccion.ponX(1);
					break;
				case KeyEvent.VK_K:
				case KeyEvent.VK_ESCAPE:
					Audio.reproduce(1, 0.5);
					pedirConfirmacion();
					break;
				case KeyEvent.VK_P:
					if (timer.isRunning() == true) {
						Audio.reproduce(1, 0.5);
						timer.stop();
					} else {
						Audio.reproduce(2, 0.5);
						timer.restart();
					}
					break;
				case KeyEvent.VK_PLUS:
					alturaCamara += 1;
					break;
				case KeyEvent.VK_MINUS:
					if (alturaCamara > 2)
						alturaCamara -= 1;
					break;
				case KeyEvent.VK_M:
					Audio.cambiaSilencio();
					break;
				default: //if (estadoDeSaque==1) estadoDeSaque=0;
				}
				jugadorPositivo.ponVelocidad(direccion);
				if (tiempoEsperado != -1)
					tiempoEsperado = tiempoEspera;
				;
			}

			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
					direccion.ponY(0);
					break;
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W:
					direccion.ponY(0);
					break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
					direccion.ponX(0);
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					direccion.ponX(0);
					break;
				}
				jugadorPositivo.ponVelocidad(direccion);
			}
		});

		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent ev) {
				Vect2 original = new Vect2(ev.getX(), -ev.getY());
				double cos = -vectorEjeJugador.dameY() / vectorEjeJugador.norma();
				double sin = vectorEjeJugador.dameX() / vectorEjeJugador.norma();
				//System.out.println(sin+"   "+cos);
				jugadorPositivo.ponPuntoRaton(new Vect2(-original.dameX() * cos + original.dameY() * sin, -original.dameX() * sin - original.dameY() * cos));
				//System.out.println("raton en "+ev.getX()+"  "+ev.getY());
			}
		});

		this.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent ev) {
				jugadorPositivo.cambiaInclinacionTiroRaqueta((double) (ev.getWheelRotation()) / 20);
			}
		});

		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent ev) {
				if (tiempoEsperado != -1)
					tiempoEsperado = tiempoEspera;
				else
					switch (ev.getButton()) {
					case MouseEvent.BUTTON1:
						mouseApretado = -0.03;
						break;
					case MouseEvent.BUTTON3:
						mouseApretado = 0.03;
						break;
					}
				//if (ev.getButton()==1) System.out.println("apretado mouse");
			}

			public void mouseReleased(MouseEvent ev) {
				mouseApretado = 0;
			}
		});

		//Construccion del timer (faceta EventListener)
		timer = new Timer((int) (tempo * 1000), this);
		timer.start();

		//Construcción de la faceta Canvas
		setPreferredSize(new Dimension(X * 2, Y * 2));
		setBackground(new Color(220, 220, 220, 70));
		this.requestFocusInWindow();

	}

	/** Iteraciones de la partida, captura eventos del timer */
	public void actionPerformed(ActionEvent e) {

		try {

			//para el inicio
			if (bola == null)
				reiniciaJugada();

			//Controlamos puntuación

			if (tiempoEsperado == -1)
				gestionaPuntuacion();
			//si la jugada no ha sido ganada, gestiona la puntuación
			else if (tiempoEsperado <= tiempoEspera)
				tiempoEsperado = tiempoEsperado + periodo;
			//si ha sido ganada y no ha pasado el tiempo de espera, actualizalo
			else if (haGanadoAlguienLaPartida()) {
				partidaResuelta();
			}
			//si ha pasado el tiempo de espera y la ventaja en sets de uno es irrevocable, termina y concédele la victoria
			else {
				reiniciaJugada();
			}
			//si la ventaja en sets aún no es irrevocable, reinicia la jugada

			jugadorPositivo.cambiaInclinacionTiroRaqueta(mouseApretado);
			Vect2 direccionHaciaLaQueMiraElPositivo = new Vect2(-vectorEjeJugador.dameX(), vectorEjeJugador.dameY());
			jugadorPositivo.gestionaRaqueta(direccionHaciaLaQueMiraElPositivo);

			marcador++;

			//if (camaraFija==false)
			mueveCamara();

			jugadorPositivo.mueveJugador(periodo);
			jugadorPositivo.golpeaBola(bola, campo);

			//inteligenciaNegativo.mueve(new Vect2(10,-10),periodo);

			//el tiempo de rigor que ha de pasar antes de que la máquina saque no haya pasado
			if (estadoDeSaque == -1)
				tiempoRestanteHastaSaqueMaquina -= periodo;
			if (tiempoRestanteHastaSaqueMaquina < 0)
				estadoDeSaque = 0;

			//Llamamos al objeto inteligencia del negativo, que como dispone de todas las referencias podrá manejarlo
			//Bajo la condición de que no estemos en estado de saque.
			if (estadoDeSaque == 0) {
				inteligenciaNegativo.ponBola(bola);
				inteligenciaNegativo.mueve(periodo);
				inteligenciaNegativo.golpea();
			}

			if (estadoDeSaque == 0) //Permite que la pelota quede suspendida antes de que el jugador saque
				bola.actualizaBola(periodo, campo);

			repaint();

		} catch (Exception ex) {
			System.out.println("error en el bucle principal");
			System.out.println(ex.toString());
			ex.printStackTrace();
		}

	}

	/** Lo redefinimos para evitar parpadeos */
	public void update(Graphics g) {
		paint(g);
	}

	/** Método paint con doble buffer sobre VolatileImage */
	public void paint(Graphics gC) {

		try {

			VolatileImage imagen = getGraphicsConfiguration().createCompatibleVolatileImage(800, 600);
			//BufferedImage imagen = new BufferedImage (getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR   );
			Graphics g = imagen.createGraphics();

			//g.setColor(new Color(240,240,240,70));

			//Declaramos o calculamos parámetros generales
			int rad;
			Color colorSombra = new Color(0, 0, 0, 70);
			Color colorJug, colorJug2;
			switch (tipoCampo) {
			case 1: {
				colorJug = new Color(160, 160, 205);
				colorJug2 = new Color(0, 0, 0, 150);
				break;
			}
			case 2: {
				colorJug = new Color(0, 160, 160);
				colorJug2 = new Color(0, 0, 0, 150);
				break;
			}
			default: {
				colorJug = new Color(230, 80, 0);
				colorJug2 = new Color(0, 0, 0, 150);
				break;
			}
			}
			Vect2ConDistancia posJugadorPositivo = proyeccion.proyecta(jugadorPositivo.damePosicion());
			Vect2ConDistancia posJugadorNegativo = proyeccion.proyecta(jugadorNegativo.damePosicion());
			int cont = 0;
			int[] x = new int[4];
			int[] y = new int[4];

			switch (tipoCampo) {
			case 1:
				g.setColor(new Color(0, 140, 0));
				break;
			case 2:
				g.setColor(new Color(140, 60, 60));
				break;
			case 3:
				g.setColor(new Color(0, 0, 170));
				break;

			}

			// Pintamos campo exterior
			Stack puntosClaveExterior = proyeccion.proyectaPila(campo.damePuntosClaveExterior());
			while (!puntosClaveExterior.empty()) {
				Vect2ConDistancia vect2ConDistancia = (Vect2ConDistancia) puntosClaveExterior.pop();
				x[cont] = (int) (X + vect2ConDistancia.dameX());
				y[cont] = (int) (Y + vect2ConDistancia.dameY());
				cont++;
				if (cont == 4) {
					cont = 0;
					g.fillPolygon(x, y, 4);
				}
			}
			// Pintamos lineas suelo campo
			g.setColor(new Color(255, 255, 255));
			Stack puntosClaveSuelo = proyeccion.proyectaPila(campo.damePuntosClaveSuelo());
			while (!puntosClaveSuelo.empty()) {
				Vect2ConDistancia vect2ConDistancia = (Vect2ConDistancia) puntosClaveSuelo.pop();
				x[cont] = (int) (X + vect2ConDistancia.dameX());
				y[cont] = (int) (Y + vect2ConDistancia.dameY());
				cont++;
				if (cont == 4) {
					cont = 0;
					g.fillPolygon(x, y, 4);
				}
			}
			// Pintamos palos de la red
			g.setColor(Color.black);
			Stack puntosClavePalos = proyeccion.proyectaPila(campo.damePuntosClavePalos());
			while (!puntosClavePalos.empty()) {
				Vect2ConDistancia vect2ConDistancia = (Vect2ConDistancia) puntosClavePalos.pop();
				x[cont] = (int) (X + vect2ConDistancia.dameX());
				y[cont] = (int) (Y + vect2ConDistancia.dameY());
				cont++;
				if (cont == 4) {
					cont = 0;
					g.fillPolygon(x, y, 4);
				}
			}
			// Pintamos red
			g.setColor(new Color(180, 180, 180, 170));
			Stack puntosClaveRed = proyeccion.proyectaPila(campo.damePuntosClaveRed());
			while (!puntosClaveRed.empty()) {
				Vect2ConDistancia vect2ConDistancia = (Vect2ConDistancia) puntosClaveRed.pop();
				x[cont] = (int) (X + vect2ConDistancia.dameX());
				y[cont] = (int) (Y + vect2ConDistancia.dameY());
				cont++;
				if (cont == 4) {
					cont = 0;
					g.fillPolygon(x, y, 4);
				}
			}

			// Pintamos cuerpo jugador negativo
			g.setColor(colorJug);
			rad = (int) (600 / posJugadorNegativo.dameDistancia());
			g.fillOval(X + (int) (posJugadorNegativo.dameX() - rad / 2), Y + (int) (posJugadorNegativo.dameY() - rad / 2), rad, rad);

			// Pintamos sombra jugador negativo
			g.setColor(colorSombra);
			Vect2ConDistancia posSombraJugadorNegativo = proyeccion.proyecta(jugadorNegativo.damePosicionSombra());
			rad = (int) (800 / posSombraJugadorNegativo.dameDistancia());
			g.fillOval(X + (int) (posSombraJugadorNegativo.dameX() - rad / 2), Y + (int) (posSombraJugadorNegativo.dameY() - rad / 4), rad, rad / 2);

			// Pintamos sombra bola
			g.setColor(colorSombra);
			Vect2ConDistancia posSombraBola = proyeccion.proyecta(bola.damePosicionSombra());
			rad = (int) (220 / posSombraBola.dameDistancia());
			g.fillOval(X + (int) (posSombraBola.dameX() - rad / 2), Y + (int) (posSombraBola.dameY() - rad / 4), rad, rad / 2);

			// Pintamos bola
			g.setColor(Color.orange);
			Vect2ConDistancia posBola = proyeccion.proyecta(bola.damePosicion());
			rad = (int) (200 / posBola.dameDistancia());
			g.fillOval(X + (int) (posBola.dameX() - rad / 2), Y + (int) (posBola.dameY() - rad / 2), rad, rad);

			// Pintamos sombra jugador positivo
			g.setColor(colorSombra);
			Vect2ConDistancia posSombraJugadorPositivo = proyeccion.proyecta(jugadorPositivo.damePosicionSombra());
			rad = (int) (800 / posSombraJugadorPositivo.dameDistancia());
			g.fillOval(X + (int) (posSombraJugadorPositivo.dameX() - rad / 2), Y + (int) (posSombraJugadorPositivo.dameY() - rad / 4), rad, rad / 2);

			/*//Sistema cónico pt.1 /2
			g.setColor(colorJug);
			rad = (int)(600/posJugadorPositivo.dameDistancia());
			int xPR = X+(int)(posJugadorPositivo.dameX()); //posición en la pantalla
			int yPR = Y+(int)(posJugadorPositivo.dameY()); //posicion en la pantalla
			int finDerX = (int)(xPR+rad*Math.cos(jugadorPositivo.dameInclinacionTiroRaqueta()));
			int finDerY = (int)(yPR-rad*Math.sin(jugadorPositivo.dameInclinacionTiroRaqueta()));
			int finIzqX = (int)(xPR-rad*Math.cos(jugadorPositivo.dameInclinacionTiroRaqueta()));
			int finIzqY = finDerY;
			g.drawLine( xPR , yPR , finIzqX , finIzqY );
			g.drawLine( xPR , yPR , finDerX , finDerY );
			 */
			// Pintamos cuerpo jugador positivo
			g.setColor(colorJug);
			rad = (int) (600 / posJugadorPositivo.dameDistancia());
			g.fillOval(X + (int) (posJugadorPositivo.dameX() - rad / 2), Y + (int) (posJugadorPositivo.dameY() - rad / 2), rad, rad);
			/*//Sistema cónico pt.2 /2
			rad = finDerX-finIzqX;
			g.setColor(colorJug);
			g.drawOval( finIzqX , finIzqY-(int)(rad/6) , rad , (int)(rad/3) );
			g.drawLine( xPR , yPR , xPR, finDerY-(rad/6) );
			 */

			// Pintamos indicador elevación
			rad = (int) (800 / posJugadorPositivo.dameDistancia());
			//double factor = jugadorPositivo.damePosicion().dameX()/Math.abs(jugadorPositivo.damePosicion().dameX());
			g.setColor(colorJug2);
			int angulo = (int) (Math.toDegrees(jugadorPositivo.dameInclinacionTiroRaqueta()));
			int anguloInicio1 = 0;
			int anguloInicio2 = 180;
			/*
			if (factor==1) {
			    anguloInicio = 180-angulo;
			} else {
			    anguloInicio = 0;
			}
			 */
			g.fillArc(X + (int) (posJugadorPositivo.dameX() - rad / 2), Y + (int) (posJugadorPositivo.dameY() - rad / 2), rad, rad, anguloInicio1, angulo);
			g.fillArc(X + (int) (posJugadorPositivo.dameX() - rad / 2), Y + (int) (posJugadorPositivo.dameY() - rad / 2), rad, rad, anguloInicio2, -angulo);

			// Pintamos el vector asociado a la velocidad de su raqueta)
			g.setColor(Color.yellow);
			Vect2 vectorVisual = Vect2.mult(3, jugadorPositivo.dameVelocidadRaqueta());
			int xI = X + (int) (posJugadorPositivo.dameX());
			int yI = Y + (int) (posJugadorPositivo.dameY());
			int xF = xI + (int) vectorVisual.dameX();
			int yF = yI - (int) vectorVisual.dameY();
			double semiGrosorIndicador = 2;
			Vect2 semiAncho = new Vect2(-vectorVisual.dameY(), vectorVisual.dameX());
			semiAncho = Vect2.mult(semiGrosorIndicador / semiAncho.norma(), semiAncho);
			int[] xA = new int[4];
			int[] yA = new int[4];
			xA[0] = (int) (xI + semiAncho.dameX());
			xA[1] = (int) (xI - semiAncho.dameX());
			xA[2] = (int) (xF - semiAncho.dameX());
			xA[3] = (int) (xF + semiAncho.dameX());
			yA[0] = (int) (yI - semiAncho.dameY());
			yA[1] = (int) (yI + semiAncho.dameY());
			yA[2] = (int) (yF + semiAncho.dameY());
			yA[3] = (int) (yF - semiAncho.dameY());
			g.fillPolygon(xA, yA, 4);
			//g.drawLine( xI , yI , xF , yF);

			// Pintamos rótulo del indicador de puntuación
			int bajura = 30;
			g.setColor(Color.black);
			int medio = (int) this.getSize().width / 2;
			g.setFont(new Font("Arial", 0, 17));
			g.drawString("Hombre", medio - 280, bajura);
			g.drawString("Máquina", medio + 240, bajura);

			// Pintamos indicador puntuación de jugadas
			g.setColor(Color.black);
			g.setFont(new Font("Arial", Font.BOLD, 17));
			String etiquetaPositivo, etiquetaNegativo, etiquetaComun;
			int puntuacionPositivo = jugadorPositivo.dameJugadasGanadas();
			int puntuacionNegativo = jugadorNegativo.dameJugadasGanadas();
			switch (puntuacionPositivo) {
			case 0:
				etiquetaPositivo = " 0";
				break;
			case 1:
				etiquetaPositivo = "15";
				break;
			case 2:
				etiquetaPositivo = "30";
				break;
			case 3:
				if (puntuacionNegativo < 3) {
					etiquetaPositivo = "40";
					break;
				}
			default:
				if (puntuacionNegativo > puntuacionPositivo)
					etiquetaPositivo = "Desventaja";
				else if (puntuacionNegativo == puntuacionPositivo)
					etiquetaPositivo = "Deuce";
				else
					etiquetaPositivo = "Ventaja";
				break;
			}
			switch (puntuacionNegativo) {
			case 0:
				etiquetaNegativo = " 0";
				break;
			case 1:
				etiquetaNegativo = "15";
				break;
			case 2:
				etiquetaNegativo = "30";
				break;
			case 3:
				if (puntuacionPositivo < 3) {
					etiquetaNegativo = "40";
					break;
				}
			default:
				if (puntuacionPositivo > puntuacionNegativo)
					etiquetaNegativo = "Desventaja";
				else if (puntuacionNegativo == puntuacionPositivo)
					etiquetaNegativo = "Deuce";
				else
					etiquetaNegativo = "Ventaja";
				break;
			}
			int resto = (jugadorPositivo.dameSetsGanados() + jugadorNegativo.dameSetsGanados()) % 2;

			if (((etiquetaNegativo == "Desventaja") && (resto == 1)) || ((etiquetaNegativo == "Ventaja") && (resto == 0)))
				g.drawString("Advantage Receiver", medio - 67, bajura);
			else if (((etiquetaNegativo == "Ventaja") && (resto == 1)) || ((etiquetaNegativo == "Desventaja") && (resto == 0)))
				g.drawString("Advantage Server", medio - 59, bajura);
			else if (etiquetaNegativo == "Deuce")
				g.drawString("Deuce", medio - 15, bajura);
			else {
				g.drawString(etiquetaPositivo, medio - 30, bajura);
				g.drawString(etiquetaNegativo, medio + 20, bajura);
			}

			// Pintamos indicador puntuación de sets
			int radioBolas = 15;
			Color colorBolasA = Color.ORANGE;
			Color colorBolasD = Color.GRAY;
			int numBolas = 0;
			switch (numeroSets) {
			case 1:
				numBolas = 1;
				break;
			case 3:
				numBolas = 2;
				break;
			case 5:
				numBolas = 3;
				break;
			}
			for (int i = 0; i < numBolas; i++) {
				if (jugadorPositivo.dameSetsGanados() > i)
					g.setColor(colorBolasA);
				else
					g.setColor(colorBolasD);
				g.fillOval(medio - 100 - 30 * i - radioBolas, bajura - radioBolas + 2, radioBolas, radioBolas);
			}
			for (int i = 0; i < numBolas; i++) {
				if (jugadorNegativo.dameSetsGanados() > i)
					g.setColor(colorBolasA);
				else
					g.setColor(colorBolasD);
				g.fillOval(medio + 100 + 30 * i, bajura - radioBolas + 2, radioBolas, radioBolas);
			}

			//Pintamos el notificador de media falta
			if ((bola.dameSiEsGolpeManual() == true) && ((estadoDeSaque == 1) || (estadoDeSaque == -1)) && (bola.dameSiEsSaque() == false)) {
				g.drawString("MEDIA FALTA", X - 52, bajura * 2);
			}

			// Pintamos la VolatileImage en la que está todo
			gC.drawImage(imagen, 0, 0, this);

			// Fin del paint

		} catch (Exception e) { /*System.out.println("error pintando"); System.out.println(e.toString());*/
		}
	}

	/** Mueve la cámara siguendo al jugador (redefine objeto proyección) */
	public void mueveCamara() {
		Vect3 puntoEje = new Vect3(0, -5, 0);
		double distanciaJugadorCamaraXY = 10; //10

		Vect3 vectorJugadorCamaraXY = Vect3.suma(jugadorPositivo.damePosicion(), Vect3.mult(-1, puntoEje));
		vectorEjeJugador = Vect3.suma(jugadorPositivo.damePosicion(), Vect3.mult(-1, puntoEje));
		vectorJugadorCamaraXY = Vect3.mult(distanciaJugadorCamaraXY / vectorJugadorCamaraXY.norma(), vectorJugadorCamaraXY);

		Vect3 vectorJugadorCamara = Vect3.suma(vectorJugadorCamaraXY, new Vect3(0, 0, alturaCamara));

		Vect3 puntoProyeccion = Vect3.suma(jugadorPositivo.damePosicion(), vectorJugadorCamara);
		Vect3 vectorNormalPlano = Vect3.mult(+1, puntoProyeccion);

		proyeccion = new Proyeccion(puntoProyeccion, vectorNormalPlano);
	}

	/**
	 * Gestiona la puntuación de la jugada a los tres niveles
	 * (botes,jugadas,sets)
	 */
	public void gestionaPuntuacion() {
		//System.out.println(bola.damePuntuacionJugada());
		switch (bola.damePuntuacionJugada()) {
		case -7:
		case -9:
		case -3:
		case 4:
		case 2:
		case -8: {//jugador positivo gana
			if (bola.dameSiEsSaque() == true)
				System.out.println("damesiessaque true");
			if ((bola.dameSiEsSaque() == true) && ((jugadorPositivo.dameSetsGanados() + jugadorNegativo.dameSetsGanados()) % 2 == 1)) {
				secondChance = false;
			} else {
				//Gestiono jugadas
				jugadorPositivo.ponJugadasGanadas(jugadorPositivo.dameJugadasGanadas() + 1);
				//Gestiono sets
				if ((jugadorPositivo.dameJugadasGanadas() >= 4) && (Math.abs(jugadorPositivo.dameJugadasGanadas() - jugadorNegativo.dameJugadasGanadas()) >= 2)) {
					jugadorPositivo.ponSetsGanados(jugadorPositivo.dameSetsGanados() + 1);
					jugadorPositivo.ponJugadasGanadas(0);
					jugadorNegativo.ponJugadasGanadas(0);
				}
			}
			tiempoEsperado = 0;
			break;
		}
		case 7:
		case 9:
		case 3:
		case -4:
		case -2:
		case 8: {//jugador negativo gana
			if ((bola.dameSiEsSaque() == true) && ((jugadorPositivo.dameSetsGanados() + jugadorNegativo.dameSetsGanados()) % 2 == 0)) {
				secondChance = false;
			} else {
				//Gestiono jugadas
				jugadorNegativo.ponJugadasGanadas(jugadorNegativo.dameJugadasGanadas() + 1);
				//Gestiono sets
				if ((jugadorNegativo.dameJugadasGanadas() >= 4) && (Math.abs(jugadorNegativo.dameJugadasGanadas() - jugadorPositivo.dameJugadasGanadas()) >= 2)) {
					jugadorNegativo.ponSetsGanados(jugadorNegativo.dameSetsGanados() + 1);
					jugadorPositivo.ponJugadasGanadas(0);
					jugadorNegativo.ponJugadasGanadas(0);
				}
			}
			tiempoEsperado = 0;
			break;
		}
		case 5:
		case -6: {
			if (((jugadorPositivo.dameSetsGanados() + jugadorNegativo.dameSetsGanados()) % 2 == 0) && (bola.dameSiEsGolpeManual() == false)) {
				bola.ponSiEsSaque(false);
			}
			break;
		}
		case 6:
		case -5: {
			if (((jugadorPositivo.dameSetsGanados() + jugadorNegativo.dameSetsGanados()) % 2 == 1) && (bola.dameSiEsGolpeManual() == false)) {
				bola.ponSiEsSaque(false);
			}
			break;
		}
		}
	}

	/** Reinicia la jugada conforme a las variables de la partida */
	public void reiniciaJugada() {

		jugadorPositivo.teletransportaJugador(campo);
		jugadorNegativo.teletransportaJugador(campo);
		jugadorPositivo.ponYaHaGolpeado(false);
		jugadorNegativo.ponYaHaGolpeado(false);

		if ((jugadorPositivo.dameSetsGanados() + jugadorNegativo.dameSetsGanados()) % 2 == 0) {
			bola = new Bola(jugadorPositivo, campo, secondChance);
			estadoDeSaque = 1;
		} else {
			bola = new Bola(jugadorNegativo, campo, secondChance);
			estadoDeSaque = -1;
		}
		tiempoEsperado = -1;
		tiempoRestanteHastaSaqueMaquina = InteligenciaArtificial.DEMORA_SAQUE;
		secondChance = true;
	}

	/** Devuelve si la partida está sentenciada o no */
	public boolean haGanadoAlguienLaPartida() {
		if (Math.abs(jugadorPositivo.dameSetsGanados() - jugadorNegativo.dameSetsGanados()) > numeroSets - jugadorPositivo.dameSetsGanados()
				- jugadorNegativo.dameSetsGanados())
			return true;
		else
			return false;
	}

	/** Devuelve el signo del ganador, asumiendo que la partida está sentenciada */
	public int signoGanadorDePartida() {
		if (jugadorPositivo.dameSetsGanados() > jugadorNegativo.dameSetsGanados())
			return 1;
		else
			return -1;
	}

	/**
	 * Establece de forma completa y opaca la entrada a un interfaz de
	 * confirmación para finalizar la partida, y también su salida, de modo que
	 * regresa al punto de partida
	 */
	public void pedirConfirmacion() {

		timer.stop();

		Dimension dimensionBoton = new Dimension(150, 60);
		JButton botonSalir = new JButton("Salir");
		JButton botonVolver = new JButton("Volver");

		botonSalir.setFont(new Font("Arial", Font.BOLD, 20));
		botonVolver.setFont(new Font("Arial", Font.BOLD, 20));
		botonSalir.setContentAreaFilled(false);
		botonVolver.setContentAreaFilled(false);
		botonSalir.setOpaque(true);
		botonVolver.setOpaque(true);
		botonSalir.setPreferredSize(dimensionBoton);
		botonVolver.setPreferredSize(dimensionBoton);
		botonSalir.setFocusPainted(false);
		botonVolver.setFocusPainted(false);

		// Los dos escuchadores anónimos de los dos botones
		botonSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Audio.reproduce(2, 0.5);
				finalizarPartida();
			}
		});
		botonVolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Audio.reproduce(2, 0.5);
				ventana.getContentPane().remove(panelConfirmacion);
				timer.restart();
				esto.setVisible(true);
				esto.requestFocusInWindow();
			}
		});

		JLabel pregunta = new JLabel("¿Qué deseas hacer?");
		pregunta.setFont(new Font("Arial", Font.BOLD, 40));

		JPanel panelBotonesConfirmacion = new JPanel();
		panelConfirmacion = new JPanel();
		panelConfirmacion.setLayout(new BoxLayout(panelConfirmacion, BoxLayout.Y_AXIS));

		panelBotonesConfirmacion.add(botonVolver);
		panelBotonesConfirmacion.add(Box.createRigidArea(new Dimension(100, 60)));
		panelBotonesConfirmacion.add(botonSalir);

		panelConfirmacion.add(Box.createRigidArea(new Dimension(100, 150)));
		panelConfirmacion.add(pregunta);
		panelConfirmacion.add(Box.createRigidArea(new Dimension(100, 100)));
		panelConfirmacion.add(panelBotonesConfirmacion);

		ventana.getContentPane().add(panelConfirmacion);

		this.setVisible(false);
		ventana.setVisible(true);

	}

	/**
	 * Limpia la ventana y establece de forma completa y opaca la entrada a un
	 * interfaz que comunica quien es el ganador y ofrece una salida
	 */
	public void partidaResuelta() {

		timer.stop();
		ventana.getContentPane().removeAll();

		JLabel etiquetaResolucion;
		if (signoGanadorDePartida() == 1)
			etiquetaResolucion = new JLabel("HAS GANADO");
		else
			etiquetaResolucion = new JLabel("HAS PERDIDO");
		etiquetaResolucion.setFont(new Font("Arial", Font.BOLD, 40));

		Dimension dimensionBoton = new Dimension(200, 100);
		JButton botonMenu = new JButton("Menu");
		botonMenu.setFont(new Font("Arial", Font.BOLD, 25));
		botonMenu.setContentAreaFilled(false);
		botonMenu.setOpaque(true);
		botonMenu.setPreferredSize(dimensionBoton);
		botonMenu.setFocusPainted(false);

		botonMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Audio.reproduce(2, 0.5);
				finalizarPartida();
			}
		});

		JPanel panelEtiqueta = new JPanel();
		JPanel panelBoton = new JPanel();

		panelEtiqueta.setLayout(new BoxLayout(panelEtiqueta, BoxLayout.X_AXIS));
		panelBoton.setLayout(new BoxLayout(panelBoton, BoxLayout.X_AXIS));

		JPanel panelResolucion = new JPanel();
		panelResolucion.setLayout(new BoxLayout(panelResolucion, BoxLayout.Y_AXIS));

		panelEtiqueta.add(etiquetaResolucion);
		panelBoton.add(botonMenu);

		panelResolucion.add(Box.createRigidArea(new Dimension(100, 200)));
		panelResolucion.add(panelEtiqueta);
		panelResolucion.add(Box.createRigidArea(new Dimension(100, 100)));
		panelResolucion.add(panelBoton);

		ventana.getContentPane().add(panelResolucion);
		ventana.setVisible(true);

	}

	/**
	 * Limpia la ventana y vuelve a crear un menú. No llama a más métodos de
	 * partida, de modo que supone la muerte del objeto partida
	 */
	public void finalizarPartida() {

		ventana.getContentPane().removeAll();
		new Menu(ventana);

	}

}
