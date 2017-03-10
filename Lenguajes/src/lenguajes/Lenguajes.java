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
    public static boolean isAlreadyToken(String tokenNo)
    {
        if (tokenNos.contains(tokenNo)){
            return true;
        }
        else
        {
            return false;
        }
    }
     static int estado = -1;
          
     // Bandera me indica si ya paso por esa palabra reservada para que no se repita
     static boolean tokens = false;
     // Bandera que indica si encontro algun error
     static boolean error = false;
     static boolean operador = false;
     static String aux = "";
     static int cSimple = 1;
     static int cDoble = 1;
     static int parentesis = 0;
     static ArrayList<String> tokenNos = new ArrayList<String>();
     
    
    public static void scanLine(String line, String lineNO){
        Methods m = new Methods();
        Boolean isSpace = false;
        char charat;
        
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
                        System.out.println("Error en la linea: " + lineNO + " columna:" + i);
                        System.out.println("El archivo inicio de mala manera");
                        error = true;
                        i = line.length();
                    } // Solo cuando cambia de estado a 0 inicia el switch 
                    else {
                        switch (estado) {

                            // inicia buscando el id de "TOKENS"
                            case 0: {
                                if (comparador.matches("[a-z]|[A-Z]|_|\\d") && m.isTOKENS(aux)==false) {
                                    estado = 0;
                                    aux += comparador;
                                }else if (comparador.matches("\\s")) {
                                    if (m.isTOKENS(aux)) {
                                        estado = 1;
                                        tokens = true;
                                        aux="";
                                        
                                    }
                                    
                                } else  {
                                  System.out.println("Error en la linea: " + lineNO + " columna:" + i + "No viene reservada Tokens");                                    
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
                                        aux="";
                                        
                                    }
                                    else{
                                        //estado = estadoConjunto
                                    }
                                }
                                break;
                            }
                            // estado 2: busca el numero del token
                            case 2: {
                                
                                
                                if (comparador.matches("\\d")) {
                                    estado = 2;
                                    aux += comparador;
                                } else if (comparador.matches("\\s")) {
                                    //TODO guardar numero de token
                                    //TODO activar flag de esperar igual
                                    estado = 2;
                                    
                                
                                } else if (comparador.matches("=")){
                                    estado=3;
                                    if (isAlreadyToken(aux)){
                                        
                                    }else
                                    {
                                     tokenNos.add(aux);
                                     aux="";
                                    }
                                    
                                }
                                else {
                                    System.out.println("Error en la linea: " + lineNO + " columna:" + i + "id de token incorrecto");                                    
                                    error = true;
                                    i = line.length();
                                }
                            }
                            break;
                            case 3: //expresiones regulares
                                 if (comparador.matches("[a-z]|[A-Z]|_|\\d")){
                                     estado = 3;
                                     aux+=comparador;
                                     operador=false;
                                     
                                 }
                                 else if (comparador.matches("\\s")) {
                                     //guardar conjunto para comprobar
                                     estado = 3;
                                     aux+=" ";
                                     
                                     
                                     
                                 }
                                 else if(comparador.matches("\\*|\\+|\\?")){
                                     // comprobar si existe
                                     //guardar nombre del conjunto tambien
                                     
                                     if(!operador){
                                         estado = 3;
                                         aux+=comparador;
                                         operador=true;
                                     }
                                     else{
                                         System.out.println("Error en la linea: " + lineNO + " columna:" + i + "no pueden venir 2 operadores seguidos");                                    
                                         error = true;
                                         i = line.length();
                                     }
                                     
                                 } else if (comparador.matches(";")){
                                     estado = 1;
                                     //guardar aux
                                     operador=false;
                                 } else if (comparador.matches("\\(")){
                                     estado=4;
                                     aux+=comparador;
                                     parentesis++;
                                     operador=false;
                                 } else if(comparador.matches("\"")){
                                     estado=5;
                                     aux+=comparador;
                                     operador=false;
                                 } else if (comparador.matches("\'")){
                                     estado=6;
                                     aux+=comparador;
                                     operador=false;
                                 }
                                 
                                break;
                            case 4:
                                if (comparador.matches("[a-z]|[A-Z]|_|\\d")){
                                     estado = 4;
                                     aux+=comparador;
                                     operador=false;
                                     
                                 }else if (comparador.matches("\\(")){
                                     aux+=comparador;
                                     estado=4;
                                     operador=false;
                                     parentesis++;
                                 }
                                 else if (comparador.matches("\\)")&& parentesis!=0){
                                     aux+=comparador;
                                     parentesis--;
                                     operador=false;
                                     estado=4;
                                 }else if (comparador.matches("\\)")&& parentesis==0){
                                     aux+=comparador;
                                     operador=false;
                                     estado=3;
                                 } else if (comparador.matches("\\s")) {
                                     //guardar conjunto para comprobar
                                     estado = 3;
                                     operador=false;
                                     aux+=" ";
                                    
                                 }else if(comparador.matches("\\*|\\+|\\?")){
                                     // comprobar si existe
                                     if(!operador){
                                         estado = 4;
                                         aux+=comparador;
                                         operador=true;
                                     }
                                     else{
                                         System.out.println("Error en la linea: " + lineNO + " columna:" + i + "no pueden venir 2 operadores seguidos");                                    
                                         error = true;
                                         i = line.length();
                                     }
                                     
                                 } else if (comparador.matches(";")&& parentesis==0){
                                     estado = 1;
                                     //guardar aux
                                 } else if(comparador.matches("\"")){
                                     estado=5;
                                     aux+=comparador;
                                     operador=false;
                                 } else if (comparador.matches("\'")){
                                     estado=6;
                                     aux+=comparador;
                                     operador=false;
                                 }
                                 else {
                                     System.out.println("Error en la linea: " + lineNO + " columna:" + i + "no cerro todos los parentesis");                                    
                                         error = true;
                                         i = line.length();
                                 }
                                
                                
                                break;
                            case 5:
                                
                                if (comparador.matches("[a-z]|[A-Z]|\\d")||cDoble!=0){
                                     estado = 5;
                                     aux+=comparador;
                                     cDoble--;
                                     
                                 } else if (comparador.matches("\"")){
                                     estado = 3;
                                     aux+=comparador;
                                     cDoble++;
                                 } else if (comparador.matches("[a-z]|[A-Z]|\\d")||cDoble==0){
                                     System.out.println("Error en la linea: " + lineNO + " columna:" + i + "mas de un caracter");                                    
                                         error = true;
                                         i = line.length();
                                 }
                                 else{
                                      System.out.println("Error en la linea: " + lineNO + " columna:" + i + "no cerro comillas");                                    
                                         error = true;
                                         i = line.length();
                                 }
                                
                                break;
                            case 6:
                                if (comparador.matches("[a-z]|[A-Z]|\\d")||cSimple!=0){
                                     estado = 6;
                                     aux+=comparador;
                                     cSimple--;
                                     
                                 } else if (comparador.matches("\'")){
                                     estado = 3;
                                     aux+=comparador;
                                     cSimple++;
                                 } else if (comparador.matches("[a-z]|[A-Z]|\\d")||cSimple==0){
                                     System.out.println("Error en la linea: " + lineNO + " columna:" + i + "mas de un caracter");                                    
                                         error = true;
                                         i = line.length();
                                 }
                                 else{
                                      System.out.println("Error en la linea: " + lineNO + " columna:" + i + "no cerro comillas");                                    
                                         error = true;
                                         i = line.length();
                                 }
                                break;
                                
                                
                                
                            
                            
                            default: {
                                System.out.println("Error en la linea: " + lineNO + " columna:" + i);
                                error = true;
                                i = line.length();
                                break;
                            }
                        }
                    }
                }
    }
    public static void getScan(char entry){
        
        switch (entry){
            case '(':
                
            break;
            case ')':
                
            break;
            case '{':
                
            break;
            case '}':
                
            break;
            
            default:
                
            break;
            
        }
        
    }
    public static void readFile(){
        String path = "C:\\Users\\Rodrigo\\Documents\\Lenguajes\\tronadores-lite\\pruebaR.PRO";
        FileInputStream inputstream;
        Scanner sc;
        String line = "";
        
        try{
            inputstream = new FileInputStream(path);
            sc = new Scanner(inputstream, "UTF-8");
            int lineNo=1;
            while (sc.hasNextLine()){
                line = sc.nextLine();
                scanLine(line,String.valueOf(lineNo));
                lineNo++;
            }
           if (inputstream != null) {
                inputstream.close();
            }
           if (sc != null) {
                sc.close();
            }
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }
    
    
}
