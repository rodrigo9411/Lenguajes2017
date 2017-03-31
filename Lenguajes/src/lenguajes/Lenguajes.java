/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lenguajes;

import java.util.Scanner;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.*;

/**
 *
 * @author Rodrigo
 */
public class Lenguajes {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        readFile();

    }

    public static boolean isAlreadyToken(String tokenNo) {
        if (tokenNos.contains(tokenNo)) {
            return true;
        } else {
            return false;
        }
    }
    static int estado = -1;

    // En este diccionario de diccionario se guardaran las acciones
    static public Map<String, Map<String, String>> acciones_diccionario = new HashMap<>();
    // En este diccionario se guarda cada uno de los ids de las acciones
    static public Map<String, String> action;
    //Variable que guarda el nombre de la accion
    static public String name_action = "";
    // Variable que guarda los ID's de la accion
    static public String name_id_action = "";
    // Variable que guarda el numero del ID de la accion
    static String num = ""; //Sirve para manejar el ID Numerico, acciones lo usa

    // Bandera me indica si ya paso por esa palabra reservada para que no se repita
    static boolean tokens = false;
    // Bandera me indica si ya paso por esa palabra reservada para que no se repita
    static boolean acciones = false;
    // Bandera que indica si encontro algun error
    static boolean error = false;
    static boolean operador = false;
    static boolean tokenNum = false;
    static String aux = ""; //Sirve para manejar la variable de ID    
    static int cSimple = 1;
    static int cDoble = 1;
    static int parentesis = 0;
    static String nodoActual;
    static ArrayList<String> tokenNos = new ArrayList<String>();
    static ArrayList<String> expresionesRegulares = new ArrayList<String>();
    static boolean nuevaLinea = false; //revisar bien lo de nueva linea

    public static void scanLine(String line, String lineNO) {
        Methods m = new Methods();
        Boolean isSpace = false;
        char charat;
        nodoActual = "";
        //Hola soy el nuevo commit
        for (int i = 0; i < line.length(); i++) {
            // Variable para comparar con Regex o patrones
            String comparador = "" + line.charAt(i);

            // Si no ha iniciado (estado = -1) toma la primera linea 
            // y busca la letra correcta para acumularlas y formar un id
            if (estado == -1 && comparador.matches("[a-z]|[A-Z]|_")) {
                estado = 0;
                aux += comparador;
            } else if (estado == -1 && comparador.matches("\\s")) {
                estado = -1;
            } else if (estado == -1) {
                int e = i + 1;
                System.out.println("Error en la linea: " + lineNO + " columna: " + e);
                System.out.println("El archivo inicio de mala manera");
                error = true;
                i = line.length();
            } // Solo cuando cambia de estado a 0 inicia el switch 
            else {
                switch (estado) {

                    // inicia buscando el id de "TOKENS"
                    case 0: {
                        if (comparador.matches("[a-z]|[A-Z]|_|\\d") && m.isTOKENS(aux) == false) {
                            nuevaLinea = false;
                            estado = 0;
                            aux += comparador;
                        } else if (comparador.matches("\\s") || nuevaLinea) {
                            nuevaLinea = false;
                            if (m.isTOKENS(aux)) {
                                estado = 1;
                                tokens = true;
                                aux = comparador;
                            } else if (m.isACCIONES(aux)) {
                                estado = 10;
                                acciones = true;
                                aux = "";
                            }

                        } else {
                            int e = i + 1;
                            System.out.println("Error en la linea: " + lineNO + " columna: " + e + " No viene reservada Tokens");
                            error = true;
                            i = line.length();
                        }

                        break;
                    }

                    // estado 1: si reconoce tokens comienza a buscar token
                    case 1: {

                        if (comparador.matches("[a-z]|[A-Z]")) {
                            estado = 1;
                            aux += comparador;
                        } else if (comparador.matches("\\s") || nuevaLinea) {
                            if (m.isTOKEN(aux)) {
                                estado = 2;
                                aux = "";

                            } else {
                                //estado = estadoConjunto
                            }
                        }
                        break;
                    }
                    // estado 2: busca el numero del token
                    case 2: {

                        if (comparador.matches("\\d") && tokenNum == false) {
                            estado = 2;
                            aux += comparador;
                        } else if (comparador.matches("\\s")) {
                            //TODO guardar numero de token
                            tokenNum = true;
                            estado = 2;

                        } else if (comparador.matches("=")) {
                            estado = 3;
                            tokenNum = false;
                            if (isAlreadyToken(aux)) {
                                int e = i + 1;
                                System.out.println("Error en la linea: " + lineNO + " columna: " + e + " id de token ya declarado");
                                error = true;
                                i = line.length();
                            } else {
                                tokenNos.add(aux);
                                nodoActual = aux;
                                aux = "";
                            }

                        } else {
                            int e = i + 1;
                            System.out.println("Error en la linea: " + lineNO + " columna: " + e + " id de token incorrecto");
                            error = true;
                            i = line.length();
                        }
                    }
                    break;
                    case 3: //expresiones regulares
                        if (comparador.matches("[a-z]|[A-Z]|_|\\d")) {
                            estado = 3;
                            aux += comparador;
                            operador = false;

                        } else if (comparador.matches("\\s")) {
                            //guardar conjunto para comprobar
                            estado = 3;
                            aux += comparador;

                        } else if (comparador.matches("\\*|\\+|\\?")) {
                            // comprobar si existe
                            //guardar nombre del conjunto tambien

                            if (!operador) {
                                estado = 3;
                                aux += comparador;
                                operador = true;
                            } else {
                                int e = i + 1;
                                System.out.println("Error en la linea: " + lineNO + " columna: " + e + " no pueden venir 2 operadores seguidos");
                                error = true;
                                i = line.length();
                            }

                        } else if (comparador.matches(";")) {
                            estado = 1;
                            expresionesRegulares.add(aux);
                            operador = false;
                            aux = "";
                        } else if (comparador.matches("\\(")) {
                            //Guardar nombre tambien
                            estado = 4;
                            aux += comparador;
                            parentesis++;
                            operador = false;
                        } else if (comparador.matches("\"")) {
                            //Guardar nombre tambien
                            estado = 5;
                            aux += comparador;
                            operador = false;
                        } else if (comparador.matches("\'")) {
                            //Guardar nombre tambien
                            estado = 6;
                            aux += comparador;
                            operador = false;
                        } else if (comparador.matches("\\|")) {
                            //Guardar nombre tambien
                            estado = 3;
                            aux += comparador;
                            operador = false;
                        }

                        break;
                    case 4://parentesis
                        if (comparador.matches("[a-z]|[A-Z]|_|\\d")) {
                            estado = 4;
                            aux += comparador;
                            operador = false;

                        } else if (comparador.matches("\\(")) {
                            aux += comparador;
                            estado = 4;
                            operador = false;
                            parentesis++;
                        } else if (comparador.matches("\\)") && parentesis != 0) {
                            aux += comparador;
                            parentesis--;
                            operador = false;
                            estado = 4;
                        } else if (comparador.matches("\\)") && parentesis == 0) {
                            aux += comparador;
                            operador = false;
                            estado = 3;
                        } else if (comparador.matches("\\s")) {
                            //guardar conjunto para comprobar
                            estado = 3;
                            operador = false;
                            aux += comparador;

                        } else if (comparador.matches("\\*|\\+|\\?")) {
                            // comprobar si existe
                            if (!operador) {
                                estado = 4;
                                aux += comparador;
                                operador = true;
                            } else {
                                int e = i + 1;
                                System.out.println("Error en la linea: " + lineNO + " columna: " + e + " no pueden venir 2 operadores seguidos");
                                error = true;
                                i = line.length();
                            }

                        } else if (comparador.matches(";") && parentesis == 0) {
                            estado = 1;
                            expresionesRegulares.add(aux);
                            //guardar aux
                            aux = "";
                        } else if (comparador.matches("\"")) {
                            estado = 5;
                            aux += comparador;
                            operador = false;
                        } else if (comparador.matches("\'")) {
                            estado = 6;
                            aux += comparador;
                            operador = false;
                        } else if (comparador.matches("\\|")) {
                            //Guardar nombre tambien
                            estado = 4;
                            aux += comparador;
                            operador = false;
                        } else {
                            int e = i + 1;
                            System.out.println("Error en la linea: " + lineNO + " columna: " + e + " no cerro todos los parentesis");
                            error = true;
                            i = line.length();
                        }

                        break;
                    case 5://comillas dobles

                        if (comparador.matches("[a-z]|[A-Z]|\\d") || cDoble != 0) {
                            estado = 5;
                            aux += comparador;
                            cDoble--;

                        } else if (comparador.matches("\"") && parentesis == 0) {
                            estado = 3;
                            aux += comparador;
                            cDoble++;
                        } else if (comparador.matches("\"") && parentesis != 0) {
                            estado = 4;
                            aux += comparador;
                            cDoble++;
                        } else if (comparador.matches("[a-z]|[A-Z]|\\d") || cDoble == 0) {
                            int e = i + 1;
                            System.out.println("Error en la linea: " + lineNO + " columna: " + e + " no cerro comillas");
                            error = true;
                            i = line.length();
                        } else {
                            int e = i + 1;
                            System.out.println("Error en la linea: " + lineNO + " columna: " + e + " no cerro comillas");
                            error = true;
                            i = line.length();
                        }

                        break;
                    case 6://comillas simples
                        if (comparador.matches("[a-z]|[A-Z]|\\d") || cSimple != 0) {
                            estado = 6;
                            aux += comparador;
                            cSimple--;

                        } else if (comparador.matches("\'") && parentesis == 0) {
                            estado = 3;
                            aux += comparador;
                            cSimple++;
                        } else if (comparador.matches("\'") && parentesis != 0) {
                            estado = 4;
                            aux += comparador;
                            cSimple++;
                        } else if (comparador.matches("[a-z]|[A-Z]|\\d") || cSimple == 0) {
                            int e = i + 1;
                            System.out.println("Error en la linea: " + lineNO + " columna: " + e + " no cerro comilla");
                            error = true;
                            i = line.length();
                        } else {
                            int e = i + 1;
                            System.out.println("Error en la linea: " + lineNO + " columna: " + e + " no cerro comilla");
                            error = true;
                            i = line.length();
                        }
                        break;

                    // Voy a tomar los "case" del 10 en adelante
                    // estado 10: si reconoce acciones sigue el id de la accion
                    case 10: {
                        if (comparador.matches("[a-z]|[A-Z]|_")) {
                            estado = 11;
                            aux = comparador;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // aun busca reconocer el id de la accion o el parentesis; en caso reconosca el ID se asegura que no sea el de ERROR
                    case 11: {
                        if (comparador.matches("[a-z]|[A-Z]|_|\\d")) {
                            estado = 11;
                            aux += comparador;
                        } else if (comparador.matches("\\s")) {
                            name_action = aux; //guarda el nombre de action
                            if (m.isERROR(aux)) {
                                // Estado 30 busca el error
                                estado = 30;
                            } else {
                                // Sigue quitando los espacios
                                estado = 12;
                            }
                            // para guardar el nombre creare un dict dentro de un dict que estara en una lista
                        } else if (line.charAt(i) == '(') {
                            name_action = aux; //guarda el nombre de action
                            // para guardar el nombre creare un dict dentro de un dict que estara en una lista
                            if (m.isERROR(aux)) {
                                // Estado 30 busca el error
                                estado = 30;
                            } else {
                                //Sigue con la busqueda del cierre del parentesis                                                                
                                estado = 13;
                            }
                            break;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // revisa que se abra el parentesis o se come el espacio
                    case 12: {
                        if (comparador.matches("\\s")) {
                            estado = 12;
                        } else if (line.charAt(i) == '(') {
                            estado = 13;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // revisa el cierre del parentesis con espacio
                    case 13: {
                        if (comparador.matches("\\s")) {
                            estado = 13;
                        } else if (line.charAt(i) == ')') {
                            estado = 14;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // Revisa si abre parentesis
                    case 14: {
                        if (line.charAt(i) == '{') {
                            // Instancia el diccionario de IDs para que se pueda usar
                            action = new HashMap<>();
                            // Agrega al diccionario de diccionarios de acciones el nuevo diccionario de acciones
                            acciones_diccionario.putIfAbsent(name_action, action);
                            estado = 15;
                        } else if (comparador.matches("\\s")) {
                            estado = 14;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // Se come los espacios y revisa por un numero
                    case 15: {
                        if (comparador.matches("\\s")) {
                            estado = 15;
                        } else if (comparador.matches("\\d")) {
                            estado = 16;
                            num = "" + comparador;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // Si viene un numero se queda aqui y si viene espacio se mueve al 17
                    // Si viene el = pasa al 18
                    case 16: {
                        if (comparador.matches("\\d")) {
                            estado = 16;
                            num += comparador;
                        } else if (comparador.matches("\\s")) {
                            estado = 17;
                        } else if (line.charAt(i) == '=') {
                            // Guarda en variable para luego agregar 
                            // al dict el numero de accion
                            estado = 18;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // Espera que venga el = sino error
                    // Se come los espacios
                    case 17: {
                        if (line.charAt(i) == '=') {
                            // Guarda en variable para luego agregar 
                            // al dict el numero de accion
                            estado = 18;
                        } else if (comparador.matches("\\s")) {
                            estado = 17;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // Aqui espera que se abran comillas, ya sean simples o dobles
                    // se come los espacios
                    case 18: {
                        if (comparador.matches("\\s")) {
                            estado = 18;
                        } else if (line.charAt(i) == '\'') {
                            estado = 20;
                        } else if (line.charAt(i) == '"') {
                            estado = 19;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // se come los espacios espera un tipo de ID dentro de la doble comilla
                    case 19: {
                        if (comparador.matches("\\s")) {
                            estado = 19;
                        } else if (comparador.matches("[a-z]|[A-Z]|_")) {
                            aux = comparador;
                            estado = 21;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // se come los espacios espera un tipo de ID dentro de la comilla simple
                    case 20: {
                        if (comparador.matches("\\s")) {
                            estado = 20;
                        } else if (comparador.matches("[a-z]|[A-Z]|_")) {
                            aux = comparador;
                            estado = 23;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // salta con los espacios espera un tipo de ID dentro de la doble comilla
                    case 21: {
                        if (comparador.matches("\\s")) {
                            name_id_action = aux; // guarda el id de la accion
                            estado = 22;
                        } else if (comparador.matches("[a-z]|[A-Z]|_|\\d")) {
                            aux += comparador;
                            estado = 21;
                        } else if (line.charAt(i) == '"') {
                            name_id_action = aux; // guarda el id de la accion
                            estado = 25;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // se come los espacios espera el cierre de la doble comilla
                    case 22: {
                        if (comparador.matches("\\s")) {
                            estado = 22;
                        } else if (line.charAt(i) == '"') {
                            estado = 25;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // salta con los espacios espera un tipo de ID dentro de la comilla simple
                    case 23: {
                        if (comparador.matches("\\s")) {
                            name_id_action = aux; // guarda el id de la accion
                            estado = 24;
                        } else if (comparador.matches("[a-z]|[A-Z]|_|\\d")) {
                            aux += comparador;
                            estado = 23;
                        } else if (line.charAt(i) == '\'') {
                            name_id_action = aux; // guarda el id de la accion
                            estado = 25;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // se come los espacios espera el cierre de la comilla simple
                    case 24: {
                        if (comparador.matches("\\s")) {
                            estado = 24;
                        } else if (line.charAt(i) == '\'') {
                            estado = 25;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // en este punto es donde guarda en el diccionario de acciones el numero de accion y su contenido
                    case 25: {
                        if (comparador.matches("\\s")) {
                            estado = 25;
                        } else if (comparador.matches("\\d")) {
                            // GUARDA                            
                            action.putIfAbsent(num, name_id_action);
                            num = "" + comparador;
                            estado = 16;
                        } else if (line.charAt(i) == '}') {
                            //GUARDA
                            action.putIfAbsent(num, name_id_action);
                            estado = 26;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // se come los vacios y salta hasta que lea un inicio de ID
                    case 26: {
                        if (comparador.matches("\\s")) {
                            estado = 26;
                        } else if (comparador.matches("[a-z]|[A-Z]|_")) {
                            aux = comparador;
                            estado = 11;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // ---------------------------------------------------------
                    // Inicia en el estado 30 los conjuntos
                    case 30: {
                        if (comparador.matches("\\s")) {
                            estado = 30;
                        } else if (line.charAt(i) == '{') {
                            estado = 31;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // busca si es un chr o una '
                    case 31: {
                        if (comparador.matches("\\s")) {
                            estado = 31;
                        } else if (line.charAt(i) == '\'') {
                            estado = 32;
                        } else if (line.charAt(i) == 'c' || line.charAt(i) == 'C') {
                            estado = 41;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // toma la ruta de la comilla simple
                    case 32: {
                        if (comparador.matches("\\w")) {
                            estado = 33;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // revisa si se cierra la comilla
                    case 33: {
                        if (line.charAt(i) == '\'') {
                            estado = 34;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    // revisa si repite con un + o si viene un . o si se cierra } y se come los espacios
                    case 34: {
                        if (comparador.matches("\\s")) {
                            estado = 34;
                        } else if (line.charAt(i) == '.') {
                            estado = 36;
                        } else if (line.charAt(i) == '+') {
                            estado = 31;
                        } else if (line.charAt(i) == '}') {
                            estado = 35;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    case 36: {
                        if (line.charAt(i) == '.') {
                            estado = 37;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    case 37: {
                        if (line.charAt(i) == '\'') {
                            estado = 37;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    case 38: {
                        if (comparador.matches("\\w")) {
                            estado = 39;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    case 39: {
                        if (line.charAt(i) == '\'') {
                            estado = 40;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    case 40: {
                        if (line.charAt(i) == '+') {
                            estado = 31;
                        } else if (line.charAt(i) == '}') {
                            estado = 35;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    case 41: {
                        if (line.charAt(i) == 'h' || line.charAt(i) == 'H') {
                            estado = 42;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    case 42: {
                        if (line.charAt(i) == 'r' || line.charAt(i) == 'R') {
                            estado = 43;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    case 43: {
                        if (line.charAt(i) == '(') {
                            estado = 44;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    case 44: {
                        if (comparador.matches("\\d")) {
                            estado = 45;
                        } else if (comparador.matches("\\s")) {
                            estado = 44;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }
                    
                    case 45: {
                        if (comparador.matches("\\d")) {
                            estado = 45;
                        } else if (comparador.matches("\\s")) {
                            estado = 46;
                        } else if (line.charAt(i) == ')') {
                            estado = 47;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }
                    
                    case 46:{
                        if (comparador.matches("\\s")){
                            estado = 46;
                        } else if (line.charAt(i) == ')'){
                            estado = 47;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }
                    
                    case 47:{
                        if (comparador.matches("\\s")){
                            estado = 47;
                        } else if (line.charAt(i) == '+'){
                            estado = 31;
                        } else if (line.charAt(i) == '}'){
                            estado = 35;
                        } else if (line.charAt(i) == '.'){
                            estado = 48;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }
                    
                    case 48:{
                        if (line.charAt(i) == '.'){
                            estado = 49;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }
                    
                    case 49:{
                        if (line.charAt(i) == 'c' || line.charAt(i) == 'C'){
                            estado = 50;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }
                    
                    case 50: {
                        if (line.charAt(i) == 'h' || line.charAt(i) == 'H') {
                            estado = 51;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    case 51: {
                        if (line.charAt(i) == 'r' || line.charAt(i) == 'R') {
                            estado = 52;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    } 
                    
                    case 52: {
                        if (line.charAt(i) == '(') {
                            estado = 53;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }
                    
                    case 53:{
                        if (comparador.matches("\\s")) {
                            estado = 53;
                        } else if (comparador.matches("\\d")) {
                            estado = 54;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }
                    
                    case 54:{
                        if (comparador.matches("\\s")) {
                            estado = 55;
                        } else if (comparador.matches("\\d")) {
                            estado = 54;
                        } else if (line.charAt(i) == ')') {
                            estado = 56;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }
                    
                    case 55:{
                        if (comparador.matches("\\s")) {
                            estado = 55;
                        } else if (line.charAt(i) == ')') {
                            estado = 56;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }
                    
                    case 56:{
                        if (comparador.matches("\\s")) {
                            estado = 56;
                        } else if (line.charAt(i) == '+') {
                            estado = 31;
                        } else if (line.charAt(i) == '}') {
                            estado = 31;
                        } else {
                            System.out.println("Error en la linea: " + lineNO + " columna:" + (i + 1));
                            error = true;
                            i = line.length();
                        }
                        break;
                    }

                    default: {
                        int e = i + 1;
                        System.out.println("Error en la linea: " + lineNO + " columna: " + e);
                        error = true;
                        i = line.length();
                        break;
                    }
                }
            }
        }
        nuevaLinea = true;
        if (!error) {
            scanErs(expresionesRegulares);
        }
    }

    public static void scanErs(ArrayList<String> ers) {

    }

    public static void readFile() {
        String path = "C:\\Users\\Rodrigo\\Documents\\Lenguajes\\PRUEBAS DE LENGUAJES\\FACILITO.txt";
        FileInputStream inputstream;
        Scanner sc;
        String line = "";

        try {
            inputstream = new FileInputStream(path);
            sc = new Scanner(inputstream, "UTF-8");
            int lineNo = 1;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                scanLine(line, String.valueOf(lineNo));
                lineNo++;
            }
            if (inputstream != null) {
                inputstream.close();
            }
            if (sc != null) {
                sc.close();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
