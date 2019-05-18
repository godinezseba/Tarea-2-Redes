package Redes.Servidor;

// entrada y salida
import Redes.EntradaSalida;
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
    private String opcion;

    public ProcesosAlmacenamiento(Socket socket, Scanner entradaDatos, PrintStream salidaDatos){
        this.entradaDatos = entradaDatos;
        this.salidaDatos = salidaDatos;
        this.socket = socket;
        this.redes = new EntradaSalida(entradaDatos, salidaDatos, socket);
        this.ip = socket.getRemoteSocketAddress().toString();
        this.opcion = "";
    }

    public String getIp(){
        return this.ip;
    }

    public void run(){
        String mensaje; // respuesta del almacenamiento

        while (true) {
            while(this.opcion.equals("")){
                try {
                    this.wait();
                } catch(InterruptedException e){
                    System.out.println("Ocurrio un error en la espera de la cola "+ e.getMessage());
                }
            }
            if(this.opcion.equals("ls")){
                salidaDatos.println("ls");
                // algo similar al ls del cliente
            }
            else if(this.opcion.equals("get")){
                // algo
            }
            // ...
            opcion = "";
        }

    }
}