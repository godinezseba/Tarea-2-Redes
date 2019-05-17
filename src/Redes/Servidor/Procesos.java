package Redes.Servidor;

// entrada y salida
import Redes.EntradaSalida;
import java.util.Scanner;
import java.io.PrintStream;

// archivos
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
// excepciones
import java.io.IOException;
// hebras y sockets
import java.net.Socket;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Procesos implements Runnable{
    // variables que entrega el server
    final Scanner entradaDatos;
    final PrintStream salidaDatos;
    final Socket socket;
    // para envio de archivos
    private EntradaSalida redes;
    // para escribir en el log

    private File log;
    private String contenido;
    private BufferedWriter bw;
    private FileWriter fw;
    private String ip;
    private DateFormat hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private Date date = new Date();
    
    public Procesos(Socket socket, Scanner entradaDatos, PrintStream salidaDatos){
        this.entradaDatos = entradaDatos;
        this.salidaDatos = salidaDatos;
        this.socket = socket;
        this.redes = new EntradaSalida(entradaDatos, salidaDatos, socket);
    }

    public void run() {
        String mensaje;
        // handshake
        // envio un mensaje
        salidaDatos.println("Servidor: Hola Cliente");
        log = new File("log.txt");

        // leo un mensaje
        mensaje = entradaDatos.nextLine();
        System.out.println(mensaje);

        //ciclo mientras recibo mensajes
        while (true) {
            try {

                mensaje = entradaDatos.nextLine();
                ip = socket.getRemoteSocketAddress().toString(); 
                System.out.println(ip + " " + mensaje);
            
                // Exit, termina el cliente
                if(mensaje.equals("Exit")){
                    this.socket.close();
                    break;
                }
                // LS
                else if (mensaje.equals("ls")) {
                    date = new Date();
                    contenido = hourdateFormat.format(date) +"       command              "+ip+" ls";
                    fw = new FileWriter(log.getAbsoluteFile(), true);
                    bw = new BufferedWriter(fw);
                    bw.write(contenido);
                    bw.newLine();
                    try {
                        if (bw != null)
                            bw.close();
                        if (fw != null)
                            fw.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    
                    File folder = new File(".");
                    File[] ListOfFiles = folder.listFiles();
                    // entrego la cantidad de mensajes que enviare para imprimirlos
                    salidaDatos.println(String.valueOf(ListOfFiles.length));
                    
                    for (int i = 0; i < ListOfFiles.length; i++){
                        if(ListOfFiles[i].isFile()){
                            salidaDatos.println("Archivo "+ ListOfFiles[i].getName());
                        }
                        else if(ListOfFiles[i].isDirectory()){
                            salidaDatos.println("Carpeta " + ListOfFiles[i].getName());
                        }
                    }
                    
                    date = new Date();
                    contenido = hourdateFormat.format(date) +"       response             "+"servidor envia respuesta a "+ip;
                    fw = new FileWriter(log.getAbsoluteFile(), true);
                    bw = new BufferedWriter(fw);
                    bw.write(contenido);
                    bw.newLine();
                    try {
                        if (bw != null)
                            bw.close();
                        if (fw != null)
                            fw.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } 
                // GET
                else if(mensaje.matches("^get [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){ // comando get                    
                    // escribo en archivo
                    date = new Date();
                    contenido = hourdateFormat.format(date) +"       command              "+ip+" get "+mensaje;
                    fw = new FileWriter(log.getAbsoluteFile(), true);
                    bw = new BufferedWriter(fw);
                    bw.write(contenido);
                    bw.newLine();
                    try {
                        if (bw != null)
                            bw.close();
                        if (fw != null)
                            fw.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    redes.EnvioArchivo(mensaje, false);

                    // termino de enviar el archivo
                    date = new Date();
                    contenido = hourdateFormat.format(date) +"       response             "+"servidor envia respuesta a "+ip;
                    fw = new FileWriter(log.getAbsoluteFile(), true);
                    bw = new BufferedWriter(fw);
                    bw.write(contenido);
                    bw.newLine();
                    try {
                        if (bw != null)
                            bw.close();
                        if (fw != null)
                            fw.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                // DELETE
                else if(mensaje.matches("^delete [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){ // comando delete
                    mensaje = mensaje.substring(7);
                    //System.out.println("archivo es "+mensaje);
                    // se escribe en el log lo que se recibio
                    date = new Date();
                    contenido = hourdateFormat.format(date) +"       command              "+ip+" delete "+mensaje;
                    fw = new FileWriter(log.getAbsoluteFile(), true);
                    bw = new BufferedWriter(fw);
                    bw.write(contenido);
                    bw.newLine();
                    try {
                        if (bw != null)
                            bw.close();
                        if (fw != null)
                            fw.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    // elimina el archivo
                    File file = new File("./"+mensaje);
                    if (file.delete()){ 
                        salidaDatos.println("Se elimino " + mensaje);
                    }
                    else {
                        salidaDatos.println("Error al eliminar el archivo " + mensaje);
                    }
                    // escribe que se envia respuesta
                    date = new Date();
                    contenido = hourdateFormat.format(date) +"       response             "+"servidor envia respuesta a "+ip;
                    fw = new FileWriter(log.getAbsoluteFile(), true);
                    bw = new BufferedWriter(fw);
                    bw.write(contenido);
                    bw.newLine();
                    try {
                        if (bw != null)
                            bw.close();
                        if (fw != null)
                            fw.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    
                }
                // PUT
                else if(mensaje.matches("^put [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){ // comando put
                    // escribe en log que es un put
                    date = new Date();
                    contenido = hourdateFormat.format(date) +"       command              "+ip+" put "+ mensaje;
                    fw = new FileWriter(log.getAbsoluteFile(), true);
                    bw = new BufferedWriter(fw);
                    bw.write(contenido);
                    bw.newLine();
                    try {
                        if (bw != null)
                            bw.close();
                        if (fw != null)
                            fw.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    try{
                        redes.ReciboArchivo(mensaje);
                    }catch(Exception e){
                        System.err.println("Error al recibir el archivo: " + e);
                    }                    
                    // escribe en log la respuesta
                    date = new Date();
                    contenido = hourdateFormat.format(date) +"       response             "+"servidor envia respuesta a "+ip;
                    fw = new FileWriter(log.getAbsoluteFile(), true);
                    bw = new BufferedWriter(fw);
                    bw.write(contenido);
                    bw.newLine();
                    try {
                        if (bw != null)
                            bw.close();
                        if (fw != null)
                            fw.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }else{ 
                    salidaDatos.println("Mensaje no valido: " + mensaje);
                }
            } catch (Exception e) {
                System.err.println("No se pudo obtener el mensaje");
                break;
            }
        }
        try {
            this.entradaDatos.close();
            this.salidaDatos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}