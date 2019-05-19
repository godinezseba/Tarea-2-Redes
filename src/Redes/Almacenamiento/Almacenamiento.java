package Redes.Almacenamiento;

// entrada y salida
import java.util.Scanner;
import Redes.EntradaSalida;
import java.io.PrintStream;

// archivos
import java.io.File;

// excepciones
import java.io.IOException;

// hebras y sockets
import java.net.Socket;

public class Almacenamiento {
    public static void main(String[] args) throws IOException {


    	Socket socket = new Socket("localhost", 1234); // 192.168.0.19
    	Scanner entradaDatos = new Scanner(socket.getInputStream()); // entrada
        PrintStream salidaDatos = new PrintStream(socket.getOutputStream()); // salida

        EntradaSalida alm = new EntradaSalida(entradaDatos,salidaDatos,socket);

        String mensaje;
        
        // HANDSHAKE
        // recibo un mensaje
        mensaje = entradaDatos.nextLine();
        System.out.println(mensaje);  

        // envio un mensaje
        salidaDatos.println("Almacenamiento"); 

        while(true){
            mensaje = entradaDatos.nextLine();
            System.out.println(mensaje);
            // GET
            if(mensaje.matches("^get [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){       
                
                alm.EnvioArchivo(mensaje, true);              //se envia una parte del archivo xx 

            }
            // PUT
            else if(mensaje.matches("^put [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){
            	try {
                    alm.ReciboArchivo(mensaje);               //Se recibe una parte del archivo xx
                } catch (Exception e) {
                    System.out.println("Error al recibir el archivo");
                    e.printStackTrace();
                }
            }
            // DELETE
            else if(mensaje.matches("^delete [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){

                mensaje = mensaje.substring(7);
                
                // elimina el archivo
                File file = new File("./"+mensaje);
                if (file.delete()){ 
                    salidaDatos.println("Se elimino " + mensaje);  //Se elimino una parte del archivo xx
                }
                else {
                    salidaDatos.println("Error al eliminar el archivo " + mensaje);
                }
            }
            //ver si existe el archivo para llevarlo al ls
            else if (mensaje.matches("^ls [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){							
            	mensaje = mensaje.substring(3);

                File folder = new File(".");
                File[] ListOfFiles = folder.listFiles();
               
                for (int i = 0; i < ListOfFiles.length; i++){
                    if(ListOfFiles[i].getName() == mensaje){
                        salidaDatos.println("1");
                        break;
                    }
                    else {
                        salidaDatos.println("0");
                    }
                }

            } else{
                System.out.println("Mensaje invalido: " + mensaje);
                salidaDatos.println("Mensaje invalido");
            }
        }
        
        // System.out.println("Fin de la conexión");
        // entradaDatos.close();
        // salidaDatos.close();
        // socket.close();
    }
}