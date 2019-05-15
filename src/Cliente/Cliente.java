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
            salidaDatos.println(mensajeterminal);

            // veo como tratar la respuesta al comando
            if (mensajeterminal.equals("Exit")) {
                break;
            }
            else if(mensajeterminal.equals("ls")){
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
                mensajeterminal = mensajeterminal.substring(4);
                int bytesread;

                DataInputStream entradad = new DataInputStream(socket.getInputStream());
                fos = new FileOutputStream(mensajeterminal);
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
                mensajeterminal = mensajeterminal.substring(4); // obtengo el nombre del archivo
                // envio el mensaje
                try {
                    // variables a usar
                    File archivo = new File(mensajeterminal);
                    byte[] bytearray = new byte[(int)archivo.length()];
                    // entrada y salida
                    DataInputStream bis = new DataInputStream(new FileInputStream(archivo));
                    bis.readFully(bytearray, 0, bytearray.length);

                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    // envio los datos
                    dos.writeLong(bytearray.length);                      
                    dos.write(bytearray, 0, bytearray.length);
                    dos.flush();
                    // cierro lo que no necesito
                    bis.close();
                    // termino de enviar el archivo
                } catch (Exception e) {
                    System.err.println("Error en el envio del archivo");
                    salidaDatos.println("Error al enviar el archivo " + mensaje);
                }
            }
            else{
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