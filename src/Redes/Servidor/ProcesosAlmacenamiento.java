package Redes.Servidor;

// entrada y salida
import Redes.EntradaSalida;

import java.util.LinkedList;
import java.util.Scanner;
import java.io.PrintStream;

// archivos
import java.io.File;

// excepciones
import java.io.IOException;

// hebras y sockets
import java.net.Socket;

public class ProcesosAlmacenamiento implements Runnable{
    // variables que entrega el server
    final Scanner entradaDatos;
    final PrintStream salidaDatos;
    final Socket socket;
    // para envio de archivos
    private EntradaSalida redes;
    // para manejo con el server
    private String ip;
    public String opcion;
    public LinkedList<String> Archivos;

    public ProcesosAlmacenamiento(Socket socket, Scanner entradaDatos, PrintStream salidaDatos, LinkedList<String> Archivos){
        this.entradaDatos = entradaDatos;
        this.salidaDatos = salidaDatos;
        this.socket = socket;
        this.redes = new EntradaSalida(entradaDatos, salidaDatos, socket);
        this.ip = socket.getRemoteSocketAddress().toString();
        this.opcion = "";
        this.Archivos = Archivos;
    }

    public String getIp(){
        return this.ip;
    }

    public void setOpcion(String opcion){
            this.opcion = opcion;
    }

    public void run(){
        String mensaje; // respuesta del almacenamiento
        if(this.opcion.equals("check <file>")){
            salidaDatos.println("check <file>");
 
            mensaje = entradaDatos.nextLine();
            // PUEDE CAMBIAR
            // agrego la ip a la lista si esa parte esta
            if (mensaje.equals("true")) {
                Archivos.addLast(ip);
                // pienso en que el pool vea que las listas sean del mismo tamaño,
                // de ser asi, tenemos todo el archivo
            }
        } else if(this.opcion.equals("get <file>")){
            // algo similar a lo que sale en Cliente get
            // guardamos en el server de forma temporal el archivo.parte<i>
            // la idea es que el pool los junte de a poco
        } else if(this.opcion.equals("put <file>")){
            // algo similar a lo que sale en Cliente put
            // deberia estar de forma temporal el archivo.parte<i>
        } else if(this.opcion.matches("^delete [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){
            // algo similar a lo que sale en Cliente delete
            // y el pool elimina este elemento del archivo
            salidaDatos.println(opcion);
            mensaje = entradaDatos.nextLine();
            System.out.println(mensaje);
        } else if(this.opcion.equals("Exit")){
            salidaDatos.println("Exit");
        } else{
            System.out.println("Opcion invalida");
            System.out.println(opcion);
        }
        this.opcion = "";
    }
}