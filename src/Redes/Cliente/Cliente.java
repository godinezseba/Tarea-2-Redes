package Redes.Cliente;

// entrada y salida
import java.util.Scanner;
import Redes.EntradaSalida;
import java.io.PrintStream;

// para archivos
import java.io.FileOutputStream;

// excepciones
import java.io.IOException;

// hebras y sockets
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) throws IOException {
        String mensaje, mensajeterminal;
        Socket socket = new Socket("localhost", 1234);
        Scanner inputterminal;
        
        // entrada y salida de datos
        Scanner entradaDatos = new Scanner(socket.getInputStream()); // entrada
        PrintStream salidaDatos = new PrintStream(socket.getOutputStream()); // salida
        // para la entrada y salida de archivos
        EntradaSalida redes = new EntradaSalida(entradaDatos, salidaDatos, socket); 
        
        // recibo un mensaje
        mensaje = entradaDatos.nextLine();
        System.out.println(mensaje);  

        // envio un mensaje
        salidaDatos.println("Cliente: Respuesta recibida");      
        inputterminal = new Scanner(System.in);
        // paso de mensajes
        while(true){
            System.out.print("> ");
            mensajeterminal = inputterminal.nextLine();

            // veo como tratar la respuesta al comando
            // EXIT
            if (mensajeterminal.equals("Exit")) {
                salidaDatos.println(mensajeterminal);
                break;
            }
            // LS
            else if(mensajeterminal.equals("ls")){
                salidaDatos.println(mensajeterminal);

                int largo;
                try {
                    largo = Integer.parseInt(entradaDatos.nextLine()); 
                } catch (Exception e) {
                    System.out.println("Error al obtener el valor");
                    largo = 0;
                }

                for (int i = 0; i < largo; i++) {
                    mensaje = entradaDatos.nextLine();
                    System.out.println(mensaje);
                }
            }
            // GET
            else if(mensajeterminal.matches("^get [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){
                salidaDatos.println(mensajeterminal);
                
                try{
                    redes.ReciboArchivo(mensajeterminal);
                }catch(Exception e){
                    System.err.println("Error al recibir el archivo: " + e);
                }
            }
            // PUT
            else if(mensajeterminal.matches("^put [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){
                redes.EnvioArchivo(mensajeterminal, true);
            }
            // CUALQUIERA, incluido el DELETE
            else{
                salidaDatos.println(mensajeterminal);
                mensaje = entradaDatos.nextLine();
                System.out.println(mensaje);
            }
        }
        
        System.out.println("Fin de la conexión");
        inputterminal.close();
        entradaDatos.close();
        salidaDatos.close();
        socket.close();
    }
}