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

    // Bandera me indica si ya paso por esa palabra reservada para que no se repita
    static boolean tokens = false;
    // Bandera me indica si ya paso por esa palabra reservada para que no se repita
    static boolean acciones = false;
    // Bandera que indica si encontro algun error
    static boolean error = false;
    static boolean operador = false;
    static boolean tokenNum = false;
    static String aux = ""; //Sirve para manejar la variable de ID
    static String num = ""; //Sirve para manejar el ID Numerico, acciones lo usa
    static int cSimple = 1;
    static int cDoble = 1;
    static int parentesis = 0;
    static ArrayList<String> tokenNos = new ArrayList<String>();

    public static void scanLine(String line, String lineNO) {
        Methods m = new Methods();
        Boolean isSpace = false;
        char charat;
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
                            estado = 0;
                            aux += comparador;
                        } else if (comparador.matches("\\s")) {
                            if (m.isTOKENS(aux)) {
                                estado = 1;
                                tokens = true;
                                aux = "";
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
                        } else if (comparador.matches("\\s")) {
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

                            } else {
                                tokenNos.add(aux);
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
                            aux += " ";

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
                            //guardar aux
                            operador = false;
                            aux = "";
                        } else if (comparador.matches("\\(")) {
                            estado = 4;
                            aux += comparador;
                            parentesis++;
                            operador = false;
                        } else if (comparador.matches("\"")) {
                            estado = 5;
                            aux += comparador;
                            operador = false;
                        } else if (comparador.matches("\'")) {
                            estado = 6;
                            aux += comparador;
                            operador = false;
                        }

                        break;
                    case 4:
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
                            aux += " ";

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
                        } else {
                            int e = i + 1;
                            System.out.println("Error en la linea: " + lineNO + " columna: " + e + " no cerro todos los parentesis");
                            error = true;
                            i = line.length();
                        }

                        break;
                    case 5:

                        if (comparador.matches("[a-z]|[A-Z]|\\d") || cDoble != 0) {
                            estado = 5;
                            aux += comparador;
                            cDoble--;

                        } else if (comparador.matches("\"")) {
                            estado = 3;
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
                    case 6:
                        if (comparador.matches("[a-z]|[A-Z]|\\d") || cSimple != 0) {
                            estado = 6;
                            aux += comparador;
                            cSimple--;

                        } else if (comparador.matches("\'")) {
                            estado = 3;
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
                            if (m.isERROR(aux)) {
                                estado = 30;
                            } else {
                                estado = 12;
                            }
                            // para guardar el nombre creare un dict dentro de un dict que estara en una lista
                        } else if (line.charAt(i) == '(') {
                            // para guardar el nombre creare un dict dentro de un dict que estara en una lista
                            if (m.isERROR(aux)) {
                                estado = 30;
                            } else {
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
                            estado = 22;
                        } else if (comparador.matches("[a-z]|[A-Z]|_|\\d")) {
                            aux += comparador;
                            estado = 21;
                        } else if (line.charAt(i) == '"') {
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
                            estado = 24;
                        } else if (comparador.matches("[a-z]|[A-Z]|_|\\d")) {
                            aux += comparador;
                            estado = 23;
                        } else if (line.charAt(i) == '\'') {
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
                            estado = 16;
                        } else if (line.charAt(i) == '}') {
                            //GUARDA
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
    }

    public static void readFile() {
        String path = "C:\\Users\\Rodrigo\\Documents\\Lenguajes\\tronadores-lite\\pruebaR.PRO";
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
