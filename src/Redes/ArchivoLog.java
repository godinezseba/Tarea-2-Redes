package Redes;

//archivos
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

// tiempo
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

// excepciones
import java.io.IOException;



public class ArchivoLog {
    private BufferedWriter bw;
    private FileWriter fw;
    private File log;

    private DateFormat hourdateFormat;
    private Date date;
    private String contenido;
    private String ip;
    
    public ArchivoLog() throws IOException{
        this.date = null;
        this.bw = null;
        this.fw = null;
        this.contenido = null;
        this.ip = null;

        this.hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        this.log = new File("log.txt");

        if (this.log.exists() == false) {
            contenido = "DATE TIME                 EVENT                DESCRIPTION";
            fw = new FileWriter(log.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(contenido);
            bw.newLine();

            this.cerrar();
        }

    }

    public ArchivoLog(String ip){
        this.date = null;
        this.bw = null;
        this.fw = null;
        this.contenido = null;

        this.hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        this.log = new File("log.txt");

        this.ip = ip;
    }

    public void nuevaConexcion(String ip) throws IOException{
        date = new Date();
        contenido = hourdateFormat.format(date) + "       connection           " + ip + " Conexion entrante ";
        fw = new FileWriter(log.getAbsoluteFile(), true);
        bw = new BufferedWriter(fw);
        bw.write(contenido);
        bw.newLine();

        this.cerrar();
    }

    public void errorConexion(String ip) throws IOException{
        date = new Date();
        //"DATE TIME                 EVENT                DESCRIPTION";
        contenido = hourdateFormat.format(date) + "     error                Conexion rechazada por " + ip;
        fw = new FileWriter(log.getAbsoluteFile(), true);
        bw = new BufferedWriter(fw);
        bw.write(contenido + ip);
        bw.newLine();

        this.cerrar();
    }

    public void nuevoComando(String mensaje) throws IOException{
        date = new Date();
        contenido = hourdateFormat.format(date) + "       command              " + ip + " " + mensaje;
        fw = new FileWriter(log.getAbsoluteFile(), true);
        bw = new BufferedWriter(fw);
        bw.write(contenido);
        bw.newLine();

        this.cerrar();
    }

    public void respuestaComando() throws IOException{
        date = new Date();
        contenido = hourdateFormat.format(date) + "       response             " + "servidor envia respuesta a " + ip;
        fw = new FileWriter(log.getAbsoluteFile(), true);
        bw = new BufferedWriter(fw);
        bw.write(contenido);
        bw.newLine();

        this.cerrar();
    }

    private void cerrar(){
        try {
            if (bw != null)
                bw.close();
            if (fw != null)
                fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}