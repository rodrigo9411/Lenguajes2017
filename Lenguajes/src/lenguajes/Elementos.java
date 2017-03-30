/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lenguajes;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rodrigo
 */
public class Elementos { //Clase para calcular first, last y follow
    public boolean errorFound = false;
    public String texto = "";
    public boolean anulable = false;
    public ArrayList<Integer> first = new ArrayList<Integer>();
    public ArrayList<Integer> last = new ArrayList<Integer>();
    
    public void calcularFirstLast(char tipo, Elementos izquierda, Elementos derecha)
        {
            switch (tipo)
            {
                case '.':
                    concatenar(izquierda, derecha);
                    break;
                case '*':
                    cuantificador('*', izquierda);
                    break;
                case '+':
                    cuantificador('+', izquierda);
                    break;
                case '?':
                    cuantificador('?', izquierda);
                    break;
                case '|':
                    o(izquierda, derecha);
                    break;
            }
        }
    
     public void concatenar(Elementos izquierda, Elementos derecha)
        {
            //FIRST
            if (izquierda.anulable)
            {
                first = unirFirsts(izquierda.first, derecha.first);
            }
            else
            {
                first = izquierda.first;
            }
            //LAST
            if (derecha.anulable)
            {
                last = unirFirsts(izquierda.last, derecha.last);
            }
            else
            {
                last = derecha.last;
            }
            //ANULABLE
            if (izquierda.anulable && derecha.anulable)
            {
                anulable = true;
            }
        }

        public void cuantificador(char cuant, Elementos hijo)
        {
            Elementos nodoActual = new Elementos();
            first = hijo.first;
            last = hijo.last;
            if (cuant=='+')
            {
                if (hijo.anulable)
                {
                    anulable = true;
                }
            }
            else
            {
                anulable = true;
            }
        }

        public void o(Elementos izquierda, Elementos derecha)
        {
            first = unirFirsts(izquierda.first, derecha.first);
            last = unirLasts(izquierda.last, derecha.last);
            if (izquierda.anulable || derecha.anulable)
            {
                anulable = true;
            }
            else
            {
                anulable = false;
            }
        }

        public ArrayList<Integer> unirFirsts(ArrayList<Integer> f1, ArrayList<Integer> f2)
        {
            
            for (int i = 0; i < f2.size(); i++)
            {
                f1.add(f2.get(i));
            }
            return f1;

        }

        public ArrayList<Integer> unirLasts(ArrayList<Integer> l1, ArrayList<Integer> l2)
        {
            
            for (int i = 0; i < l2.size(); i++)
            {
                l1.add(l2.get(i));
            }
            return l1;

        }
    
}
