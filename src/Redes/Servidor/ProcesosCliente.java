package Redes.Servidor;

// entrada y salida
import Redes.EntradaSalida;

import java.util.LinkedList;
import java.util.Scanner;
import java.io.PrintStream;

// archivos
import java.io.File;
import Redes.ArchivoLog;

// excepciones
import java.io.IOException;

// hebras y sockets
import java.net.Socket;


public class ProcesosCliente implements Runnable{
    // variables que entrega el server
    final Scanner entradaDatos;
    final PrintStream salidaDatos;
    final Socket socket;
    // para envio de archivos
    private EntradaSalida redes;
    // para escribir en el log
    private ArchivoLog log;
    private String ip;
    private PoolAlmacenamiento almacenamiento;
    
    public ProcesosCliente(Socket socket, Scanner entradaDatos, PrintStream salidaDatos, PoolAlmacenamiento almacenamiento){
        this.entradaDatos = entradaDatos;
        this.salidaDatos = salidaDatos;
        this.socket = socket;
        this.almacenamiento = almacenamiento;
        this.redes = new EntradaSalida(entradaDatos, salidaDatos, socket);
        this.ip = socket.getRemoteSocketAddress().toString();
        this.log = new ArchivoLog(ip);
    }

    public String getIp(){
        return this.ip;
    }
    
    public void run() {
        String mensaje;

        ip = socket.getRemoteSocketAddress().toString();
        
        //ciclo mientras recibo mensajes
        while (true) {
            try {
                // recibo mensaje
                mensaje = entradaDatos.nextLine();
                 
                System.out.println(ip + " " + mensaje);
                log.nuevoComando(mensaje);

                // Exit, termina el cliente
                if(mensaje.equals("Exit")){
                    this.socket.close();
                    break;
                }
                // LS
                else if (mensaje.equals("ls")) {
                    // LO QUE USABAMOS ANTES
                    // File folder = new File(".");
                    // File[] ListOfFiles = folder.listFiles();
                    // // entrego la cantidad de mensajes que enviare para imprimirlos
                    // salidaDatos.println(String.valueOf(ListOfFiles.length));
                    
                    // for (int i = 0; i < ListOfFiles.length; i++){
                    //     if(ListOfFiles[i].isFile()){
                    //         salidaDatos.println("Archivo "+ ListOfFiles[i].getName());
                    //     }
                    //     else if(ListOfFiles[i].isDirectory()){
                    //         salidaDatos.println("Carpeta " + ListOfFiles[i].getName());
                    //     }
                    // }
                    
                    // como lo veo yo:
                    
                    LinkedList<String> archivos = almacenamiento.funcionls();
                    // entrego la cantidad de mensajes que enviare para imprimirlos
                    salidaDatos.println(String.valueOf(archivos.size()));
                    
                    for (String archivo : archivos){
                        salidaDatos.println(archivo);
                    }

                    log.respuestaComando();
                } 
                // GET
                else if(mensaje.matches("^get [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){ // comando get  

                    redes.EnvioArchivo(mensaje, false);

                    log.respuestaComando();
                }
                // DELETE
                else if(mensaje.matches("^delete [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){ // comando delete
                    mensaje = mensaje.substring(7);
                    
                    // elimina el archivo
                    almacenamiento.funcionDelete(mensaje);
                    salidaDatos.println("Archivo " + mensaje + " eliminado");

                    log.respuestaComando();
                    
                }
                // PUT
                else if(mensaje.matches("^put [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){ // comando put

                    try{
                        redes.ReciboArchivo(mensaje);
                    }catch(Exception e){
                        System.err.println("Error al recibir el archivo: " + e);
                        e.printStackTrace();
                    }
                    try {
                        almacenamiento.funcionPut(mensaje);
                    } catch (Exception e) {
                        System.err.println("Error al dividir el archivo: " + e);
                        e.printStackTrace();
                    }             
                    log.respuestaComando();
                // comandos rechazados
                }else{ 

                    salidaDatos.println("Mensaje no valido: " + mensaje);

                }
                
            } catch (Exception e) {
                System.err.println("No se pudo obtener el mensaje");
                e.printStackTrace();
                System.out.println(e.getMessage());
                break;
            }
        }
        try {
            this.entradaDatos.close();
            this.salidaDatos.close();
            this.socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}