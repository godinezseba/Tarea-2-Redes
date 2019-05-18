package Redes;

// entrada y salida de datos
import java.io.PrintStream;
import java.util.Scanner;

// archivos
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;

// sockets
import java.net.Socket;

public class EntradaSalida {
    final protected Scanner entradaDatos;
    final protected PrintStream salidaDatos;
    final protected Socket socket; 


    public EntradaSalida(Scanner entradaDatos, PrintStream salidaDatos, Socket socket) {
        this.entradaDatos = entradaDatos;
        this.salidaDatos = salidaDatos;
        this.socket = socket;
    }
    
    /**
     * Funcion para el envio de archivos
     * @param mensajeterminal mensaje que se corta para obtener el nombre del archivo
     */
    public void EnvioArchivo(String mensajeterminal, boolean enviarmensaje){
        String archivo = mensajeterminal.substring(4);
        
        File file = new File(archivo);
        // se checkea que el archivo existe
        if(file.exists()){ // el archivo existe
            
            if (enviarmensaje) {
                // envio el mensaje
                salidaDatos.println(mensajeterminal);
            }
            
            try {
                // variables a usar
                byte[] bytearray = new byte[(int)file.length()];
                // guardo el archivo en un arreglo
                DataInputStream bis = new DataInputStream(new FileInputStream(archivo));
                bis.readFully(bytearray, 0, bytearray.length);
                
                // imprimo por pantalla el largo de este
                System.out.print("Tamaño archivo: ");
                System.out.println(file.length());
                // envio los datos
                // envio el largo del archivo
                salidaDatos.println(file.length());
                salidaDatos.flush();
                
                // envio el archivo
                for (int i = 0; i < file.length(); i++) {
                    salidaDatos.println(bytearray[i]);
                    salidaDatos.flush();
                }
                // cierro lo que no necesito
                bis.close();
                // termino de enviar el archivo
            } catch (Exception e) {
                System.err.println("Error en el envio del archivo");
                //salidaDatos.println("Error al enviar el archivo " + mensaje);
            }
        } else {
            System.out.println("Error: el archivo no existe");
        }
    }

    /**
     * Funcion que recibe un archivo
     * @param mensajeterminal mensaje que se corta para obtener el nombre del archivo
     * @throws Exception puede fallar a la hora de escribir o leer un mensaje
     */
    public void ReciboArchivo(String mensajeterminal) throws Exception{
        mensajeterminal = mensajeterminal.substring(4);
        
        // variable del archivo de llegada
        FileOutputStream fos = new FileOutputStream(mensajeterminal);
        System.out.println(mensajeterminal);   
        // recibo el tamaño del archivo a trabajar:
        long tamaño = Long.parseLong(entradaDatos.nextLine());
        System.out.print("Tamaño archivo: ");
        System.out.println(tamaño);

        // guardo en el archivo a medida que llega
        for (int i = 0; i < tamaño; i++) {
            fos.write(Byte.parseByte(entradaDatos.nextLine()));
            // imprimo cada 1 MB
            if((tamaño-i) % 1000000 == 0) System.out.println("Queda: " + ((tamaño-i)-1000000) + "MB");
        }
        
        fos.close();
        System.out.println("Termine de recibir el archivo");
    }
}