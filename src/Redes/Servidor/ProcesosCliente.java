package Redes.Servidor;

// entrada y salida
import Redes.EntradaSalida;

import java.util.LinkedList;
import java.util.Scanner;
import java.io.PrintStream;

// archivos
import java.io.File;
import Redes.ArchivoLog;
import java.io.DataInputStream;
import java.io.FileInputStream;

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
                    mensaje = mensaje.substring(4);
                    long tamaño = almacenamiento.funcionGet(mensaje); // obtengo el tamaño del archivo
                    int parte = 0;
                    long suma = 0; //  suma de las partes enviadas

                    // ahora que tenemos el archivo hacemos algo "similar" a redes.EnvioArchivo(mensaje, false);
                    System.out.print("Tamaño archivo: ");
                    System.out.println(tamaño);
                    salidaDatos.println(tamaño);

                    // abrimos el primer archivo y lo empezamos a enviar
                    File file = new File(mensaje + ".parte" + parte);
                    DataInputStream bis = new DataInputStream(new FileInputStream(file));
                    byte[] bytearray = new byte[(int)file.length()];
                    bis.readFully(bytearray, 0, bytearray.length);

                    for (long i = 0; i < tamaño; i = suma) {
                        for (int j = 0; j < file.length(); j++) {
                            suma ++;
                            salidaDatos.println(bytearray[j]);
                            salidaDatos.flush();
                        }
                        // pasamos al siguiente
                        bis.close();
                        file.delete();
                        parte ++;
                        if (suma != tamaño) {
                            file = new File(mensaje + ".parte" + parte);
                            bis = new DataInputStream(new FileInputStream(file));
                            bytearray = new byte[(int)file.length()];
                            bis.readFully(bytearray, 0, bytearray.length);
                        }
                    }

                    log.respuestaComando();
                }
                // DELETE
                else if(mensaje.matches("^delete [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){ // comando delete
                    mensaje = mensaje.substring(7);
                    
                    // elimina el archivo
                    salidaDatos.println(almacenamiento.funcionDelete(mensaje)); // intenta eliminar y retorna si fue exitoso

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