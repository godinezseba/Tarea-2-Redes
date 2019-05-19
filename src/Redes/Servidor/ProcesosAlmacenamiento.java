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
        if(this.opcion.equals("ls")){
            //salidaDatos.println("ls");
            // algo similar al ls del cliente
            // altero la lista
            Archivos.addLast("Hola");
        }
        else if(this.opcion.equals("get")){
            // algo
        }
        // ...
        opcion = "";
    }
}