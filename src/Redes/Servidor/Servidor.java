package Redes.Servidor;
import java.util.LinkedList;
// entrada y salida
import java.util.Scanner;
import java.io.PrintStream;

// archivos
import Redes.ArchivoLog;

// excepciones
import java.io.IOException;

// sockets y hebras
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor{
    public static void main(String[] args) throws IOException{
        // creo las variables basicas a usar por el server
        Socket socket = null;
        Scanner entradaDatos = null;
        PrintStream salidaDatos = null;
        ServerSocket serversocket = null;
        PoolHebras piscinaClient = null; // piscina de hebras para el cliente
        PoolAlmacenamiento piscinaAlma = null; // piscina de hebras para el almacenamiento
        String mensaje; // mensaje que recibo de la conexion entrante
        LinkedList<String> Archivos = new LinkedList<String>(); // lista con los archivos disponibles


        //Archivo log
        String ip = null;
        ArchivoLog log = null;
        try {
            log = new ArchivoLog();
        } catch (Exception e) {
            System.err.println("Error al crear el archivo: " + e);
            System.exit(-1);
        }
         
        // creo el socket
        try{
            serversocket = new ServerSocket(1234); 
        } catch(IOException e){
            System.err.println("No se pudo abrir el puerto");
            System.exit(-1);
        }

        // INICIO DEL THREADPOOL ALMACENAMIENTO
        piscinaAlma = new PoolAlmacenamiento(10, Archivos);

        // INICIO DEL THREADPOOL CLIENTES
        piscinaClient = new PoolHebras(10);

        // recibo entradas
        while (true) {
            System.out.println("Esperando...");

            try {
                socket = serversocket.accept(); // entrada de un cliente

                ip = socket.getRemoteSocketAddress().toString();
                System.out.println("Nueva Conexion " + ip);
                
                // creo las variables de entrada y salida de datos
                entradaDatos = new Scanner(socket.getInputStream());
                salidaDatos = new PrintStream(socket.getOutputStream()); 

                //HANDSHAKE
                // ENVIO UN MENSAJE
                salidaDatos.println("Hola");
                // RECIBO UN MENSAJE
                mensaje = entradaDatos.nextLine();
                System.out.println(ip + " " + mensaje);

                if (mensaje.equals("Cliente")) {
                    log.nuevaConexcion(ip);
                    // ahora la hebra trabaja con el cliente
                    piscinaClient.ejecutar(new ProcesosCliente(socket, entradaDatos, salidaDatos, piscinaAlma));
                    //piscina.ejecutar(new ProcesosCliente(socket, entradaDatos, salidaDatos));
                } else if (mensaje.equals("Almacenamiento")) {
                    // ahora la hebra trabaja con el almacenamiento
                    piscinaAlma.ejecutar(new ProcesosAlmacenamiento(socket, entradaDatos, salidaDatos, Archivos));
                } else{
                    System.out.println(ip + " " + "ERROR AL REALIZAR EL HANDSHAKE");
                    try {
                        entradaDatos.close();
                        salidaDatos.close();
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {

                System.err.println("Error en la entrada de un cliente");
                e.printStackTrace();
                log.errorConexion(ip);

                socket.close();
            }
        }
        // termino del servidor
        // System.out.println("Fin de la ejecución");
        // serversocket.close();
    }
}