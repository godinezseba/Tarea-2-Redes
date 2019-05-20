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
        // checkeo que la conexcion siga abierta
        try {
            this.salidaDatos.println("ip");
            // veo si recibo respuesta
            String mensaje = this.entradaDatos.nextLine();
            if (mensaje.equals(this.ip)) {
                return this.socket.getInetAddress().toString();
            } else{
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error al intentar recibir señal de la maquina: " + this.ip);
            try {
                salidaDatos.close();
                entradaDatos.close();
                socket.close();
            } catch (IOException er) {
                System.out.println("Error al cerrar la conexion con la maquina: " + this.ip);
            }
            return null;
        }
    }

    public void setOpcion(String opcion){
            this.opcion = opcion;
    }

    public void run(){
        String mensaje; // respuesta del almacenamiento
        if(this.opcion.matches("^check [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){
            salidaDatos.println(opcion);
 
            mensaje = entradaDatos.nextLine();
            // PUEDE CAMBIAR
            // agrego la ip a la lista si esa parte esta
            if (mensaje.equals("true")) {
                Archivos.addLast(ip);
                // pienso en que el pool vea que las listas sean del mismo tamaño,
                // de ser asi, tenemos todo el archivo
            }
        } else if(this.opcion.matches("^get [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){
            // algo similar a lo que sale en Cliente get
            // guardamos en el server de forma temporal el archivo.parte<i>
            // la idea es que el pool los junte de a poco
            salidaDatos.println(opcion);
                
            try{
                redes.ReciboArchivo(opcion);
            }catch(Exception e){
                System.err.println("Error al recibir el archivo: " + e);
            }
        } else if(this.opcion.matches("^put [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){
            // algo similar a lo que sale en Cliente put
            // deberia estar de forma temporal el archivo.parte<i>
            redes.EnvioArchivo(opcion, true);
            // elimino este archivo temporal
            File file = new File(opcion.substring(4));
            file.delete();
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