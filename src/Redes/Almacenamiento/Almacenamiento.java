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


    	Socket socket = new Socket("localhost", 1234);
    	Scanner entradaDatos = new Scanner(socket.getInputStream()); // entrada
        PrintStream salidaDatos = new PrintStream(socket.getOutputStream()); // salida

        EntradaSalida alm = new EntradaSalida(entradaDatos,salidaDatos,socket);






        String mensaje, mensajeterminal;
        Scanner inputterminal;
        
        
        // recibo un mensaje
        //mensaje = entradaDatos.nextLine();
        //System.out.println(mensaje);  

        // envio un mensaje
        //salidaDatos.println("Cliente: Respuesta recibida"); 


        inputterminal = new Scanner(System.in);
        mensajeterminal = inputterminal.nextLine();

        while(true){

            // GET
            if(mensajeterminal.matches("^get [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){       
                mensaje = mensajeterminal.substring(4);
                alm.EnvioArchivo(mensajeterminal, true);              //se envia una parte del archivo xx 

            }
            // PUT
            else if(mensajeterminal.matches("^put [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){
            	mensaje = mensajeterminal.substring(4);
                alm.ReciboArchivo(mensajeterminal);               //Se recibe una parte del archivo xx
            }


            else if(mensajeterminal.matches("^delete [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){

                mensaje = mensajeterminal.substring(7);
                
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
            else if (mensajeterminal.matches("^ls [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){							
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

            }
        }
        
        System.out.println("Fin de la conexión");
        inputterminal.close();
        entradaDatos.close();
        salidaDatos.close();
        socket.close();
    }
}