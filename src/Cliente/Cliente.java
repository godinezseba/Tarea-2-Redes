package Cliente;
// entrada y salida
import java.util.Scanner;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
        // para los archivos
        FileOutputStream fos = null;

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
            if (mensajeterminal.equals("Exit")) {
                salidaDatos.println(mensajeterminal);
                break;
            }
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
            else if(mensajeterminal.matches("^get [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){
                salidaDatos.println(mensajeterminal);
                String archivo = mensajeterminal.substring(4);
                int bytesread;

                DataInputStream entradad = new DataInputStream(socket.getInputStream());
                fos = new FileOutputStream(archivo);
                long tamaño = entradad.readLong();
                System.out.print("Tamaño archivo: ");
                System.out.println(tamaño);
            
                byte[] buffer = new byte[1024];
                while (tamaño > 0 && (bytesread = entradad.read(buffer, 0, (int)Math.min(buffer.length, tamaño))) != -1) {
                    fos.write(buffer, 0, bytesread);
                    tamaño -= bytesread;
                    System.out.print("Queda: ");
                    System.out.println(tamaño);
                }
                fos.close();
                //System.out.println("Hola");
            }
            else if(mensajeterminal.matches("^put [a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*$")){
                String archivo = mensajeterminal.substring(4);
                
                File file = new File(archivo);
                // se checkea que el archivo existe
                if(file.exists()){ // el archivo existe
                    // envio el mensaje
                    salidaDatos.println(mensajeterminal);
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
                        salidaDatos.write(bytearray, 0, bytearray.length);
                        salidaDatos.flush();
                        // cierro lo que no necesito
                        bis.close();
                        // termino de enviar el archivo
                    } catch (Exception e) {
                        System.err.println("Error en el envio del archivo");
                        salidaDatos.println("Error al enviar el archivo " + mensaje);
                    }
                } else {
                    System.out.println("Error: el archivo no existe");
                }

            }
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