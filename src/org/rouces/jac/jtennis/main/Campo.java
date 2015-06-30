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


import java.util.Stack;

import org.rouces.jac.jtennis.algebra.*;

/**
 * La clase campo modela los parámetros físicos de un campo de tenis (incluyendo suelo, red y otros posibles objetos en el futuro),
 * y ofrece la posibilidad de que sus instancias difieran entre sí en algunas variables, cuyos posibles valores, por limitarse a lo
 * que está estipulado como posibles tipos de campo, pertenecen a la clase campo.
 * Más allá de las medidas definidas en los atributos, el centro del suelo del campo se considerará situado en el punto (0,0,0) del
 * espacio, el vector (1,0,0) será paralelo a la línea de red, el (0,1,0) será perpendicular al anterior y paralelo también al suelo
 * del campo, y el (0,0,1) será perpendicular a dicho campo, apuntando hacia arriba. El (1,0,0) apuntará a la derecha mirado desde el
 * espacio definido positivo por el (0,1,0).
 *
 * En todas las clases del juego, a no ser que se especifique lo contrario, las medidas de espacio y tiempo virtuales
 * estarán definidas en metros y segundos respectivamente. Así se simplificará la asignación previa de valores y la implementación
 * de cálculos, y se evitarán desproporciones accidentales.
 */
public class Campo{
    
    //suelo
    final private double ANCHO = 8;   //extremo a extremo
    final private double LARGO = 22;  //extremo a extremo
    private int tipo;                 //(determina color y coef. elastico del suelo)
    final static private double COEFSUELO1 = 0.6; //más duro
    final static private double COEFSUELO2 = 0.7;
    final static private double COEFSUELO3 = 0.8; //más blando
    
    //parámetros meramente gráficos
    static private double longitudLineaMedia = 12;
    static private double anchoMargenDobles = 2;
    static private double semiAnchoPalos = 0.13;
    static private double sA = 0.05; //semiancho de las líneas del suelo
    final private double ANCHO_EXTERIOR = 16;   //extremo a extremo
    final private double LARGO_EXTERIOR = 27;  //extremo a extremo
    
    final private double coefSuelo;
    
    
    //red
    final private double ALTORED = 1.1;
    final private double COEFRED = 0.3;
    static public double margenFlexibleRed = 0.2; //anchura de la parte de la red que cede y permite el 'efecto match point'.
    static private double extraRed = 1; //anchura extra de la red
    
    
    /** Constructor del campo según su tipo */
    public Campo(int tipo){
        this.tipo=tipo;
        switch (tipo) {
            case 1:
                coefSuelo = COEFSUELO1;
                break;
            case 2:
                coefSuelo = COEFSUELO2;
                break;
            default:
                coefSuelo = COEFSUELO3;
        }
    }
    
    public double dameLimX(){
        return ANCHO/2;
    }
    
    public double dameLimY(){
        return LARGO/2;
    }
    
    
    public double dameCoefSuelo(){
        return coefSuelo;
    }
    
    public double dameAltoRed(){
        return ALTORED;
    }
    
    public double dameAnchoRed(){
        return dameLimX()+anchoMargenDobles+extraRed;
    }
    
    public double dameCoefRed(){
        return COEFRED;
    }
    
    
    
    
    
    
    
    
    /** Devuelve una pila de puntos que, cogidos de cuatro en cuatro,
     * son rectángulos que representan cada línea gruesa del suelo del campo
     * Usamos polígonos y no rectas para que conste la lejanía al proyectar */
    public Stack damePuntosClaveSuelo(){
        
        Stack puntos = new Stack();
        
        //Empezamos por el vértice más positivo y recorremos en el
        //sentido de las agujas del reloj mirando desde arriba. 
        //Cuidado con los saltos cruzados!
        
        //Linea central (sin simetría)
        puntos.push( new Vect3(sA ,longitudLineaMedia/2 ,0) );
        puntos.push( new Vect3(-sA,longitudLineaMedia/2 ,0) );
        puntos.push( new Vect3(-sA ,-longitudLineaMedia/2,0) );
        puntos.push( new Vect3(sA,-longitudLineaMedia/2,0) );
        
        
        //Resto del suelo (cuatro cuadrantes, dos simetrías axiales)
        for (int u=-1; u<3; u+=2) for (int v=-1; v<3; v+=2) {
            
            //Cuadrante u,v
            
            //perpendicular a la red, interior
            puntos.push( new Vect3((dameLimX()+sA)*u,(dameLimY())*v ,0) );
            puntos.push( new Vect3((dameLimX()-sA)*u,(dameLimY())*v ,0) );
            puntos.push( new Vect3((dameLimX()-sA)*u,0,0) );
            puntos.push( new Vect3((dameLimX()+sA)*u,0,0) );
            //perpendicular a la red, exterior
            puntos.push( new Vect3((dameLimX()+anchoMargenDobles+sA)*u,(dameLimY())*v ,0) );
            puntos.push( new Vect3((dameLimX()+anchoMargenDobles-sA)*u,(dameLimY())*v ,0) );
            puntos.push( new Vect3((dameLimX()+anchoMargenDobles-sA)*u,0,0) );
            puntos.push( new Vect3((dameLimX()+anchoMargenDobles+sA)*u,0,0) );
            double chapuza=0;
            if (v==-1) chapuza=sA*2;  //chapuza para evitar que las líneas lejanas mueran en el interpolado
            //paralela a la red, corta
            puntos.push( new Vect3((dameLimX())*u,((longitudLineaMedia/2)+sA+chapuza/2)*v ,0) );
            puntos.push( new Vect3(0             ,((longitudLineaMedia/2)+sA+chapuza/2)*v ,0) );
            puntos.push( new Vect3(0             ,((longitudLineaMedia/2)-sA-chapuza/2)*v ,0) );
            puntos.push( new Vect3((dameLimX())*u,((longitudLineaMedia/2)-sA-chapuza/2)*v ,0) );
            //paralela a la red, larga
            puntos.push( new Vect3((dameLimX()+anchoMargenDobles+sA)*u,(dameLimY()+sA+chapuza)*v  ,0) );
            puntos.push( new Vect3(0                               ,(dameLimY()+sA+chapuza)*v  ,0) );
            puntos.push( new Vect3(0                               ,(dameLimY()-sA-chapuza)*v  ,0) );
            puntos.push( new Vect3((dameLimX()+anchoMargenDobles+sA)*u,(dameLimY()-sA-chapuza)*v  ,0) );
            
            
        }
        return puntos;
    }
    
    
   /** Devuelve una pila de puntos que, cogidos de cuatro en cuatro,
     * son rectángulos que representan los palos de la red
     * Usamos polígonos y no rectas para que conste la lejanía al proyectar */
    public Stack damePuntosClavePalos(){
        
        Stack puntos = new Stack();
        
        //Empezamos por el vértice más positivo y recorremos en el
        //sentido de las agujas del reloj mirando desde el jugador positivo. 
        //Cuidado con los saltos cruzados!
        
        for (int u=-1; u<3; u+=2){
            //mitad u
            puntos.push( new Vect3( (dameAnchoRed()+2*semiAnchoPalos)*u , 0 , dameAltoRed() ) );
            puntos.push( new Vect3( (dameAnchoRed()+2*semiAnchoPalos)*u , 0 , 0 ) );
            puntos.push( new Vect3( (dameAnchoRed())*u , 0 , 0 ) );
            puntos.push( new Vect3( (dameAnchoRed())*u , 0 , dameAltoRed() ) );
        }
        return puntos;
    }
    
    
    
    /** Devuelve una pila de puntos que, cogidos de cuatro en cuatro,
     * son un rectángulo que representa la red
     * Usamos polígonos y no rectas para que conste la lejanía al proyectar */
    public Stack damePuntosClaveRed(){
        
        Stack puntos = new Stack();
        
        //Empezamos por el vértice más positivo y recorremos en el
        //sentido de las agujas del reloj mirando desde el jugador positivo. 
        //Cuidado con los saltos cruzados!
        
            puntos.push( new Vect3( dameAnchoRed() , 0 , dameAltoRed() ) );
            puntos.push( new Vect3( dameAnchoRed() , 0 , 0 ) );
            puntos.push( new Vect3( -dameAnchoRed() , 0 , 0 ) );
            puntos.push( new Vect3( -dameAnchoRed() , 0 , dameAltoRed() ) );
        
        return puntos;
    }
    
    
    
    
    
   /** Devuelve una pila de puntos que, cogidos de cuatro en cuatro,
     * son un rectángulo que representa el marco exterior del campo
     * Usamos polígonos y no rectas para que conste la lejanía al proyectar */
    public Stack damePuntosClaveExterior(){
        
        Stack puntos = new Stack();
        
        //Empezamos por el vértice más positivo y recorremos en el
        //sentido de las agujas del reloj mirando desde el jugador positivo. 
        //Cuidado con los saltos cruzados!
        
            puntos.push( new Vect3( ANCHO_EXTERIOR/2 , LARGO_EXTERIOR/2 , 0 ) );
            puntos.push( new Vect3( -ANCHO_EXTERIOR/2 , LARGO_EXTERIOR/2 , 0 ) );
            puntos.push( new Vect3( -ANCHO_EXTERIOR/2 , -LARGO_EXTERIOR/2 , 0 ) );
            puntos.push( new Vect3( ANCHO_EXTERIOR/2 , -LARGO_EXTERIOR/2 , 0 ) );
        
        return puntos;
    }
    
    
    
    
    /** Ya no se usa */
    public Stack damePuntosClaveCampo(){
        
        Stack puntos = new Stack();
        
        //izq
        puntos.push( new Vect3( -dameLimX() , -dameLimY() , 0                   ) );
        puntos.push( new Vect3( -dameLimX() , 0           , 0                   ) );
        puntos.push( new Vect3( -dameLimX() , 0           , dameAltoRed()       ) );
        puntos.push( new Vect3( -dameLimX() , dameLimY()  , 0                   ) );
        //der
        puntos.push( new Vect3( dameLimX()  , -dameLimY() , 0                   ) );
        puntos.push( new Vect3( dameLimX()  , 0           , 0                   ) );
        puntos.push( new Vect3( dameLimX()  , 0           , dameAltoRed()       ) );
        puntos.push( new Vect3( dameLimX()  , dameLimY()  , 0                   ) );
        //cent
        puntos.push( new Vect3( 0  , -dameLimY() , 0                   ) );
        puntos.push( new Vect3( 0  , 0           , 0                   ) );
        puntos.push( new Vect3( 0  , 0           , dameAltoRed()       ) );
        puntos.push( new Vect3( 0  , dameLimY()  , 0                   ) );
        
        return puntos;
        
    }
    
    
    
    
    
}
