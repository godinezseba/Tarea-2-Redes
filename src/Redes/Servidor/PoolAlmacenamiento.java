package Redes.Servidor;

// Estructuras de datos
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.HashMap;
import java.util.List;

// para archivos
import Redes.ArchivoAlma;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

// threads
import java.lang.Thread;

public class PoolAlmacenamiento{
    private final int nHebras;
    private final ListaHebras[] Hebras;
    private final LinkedBlockingQueue<ProcesosAlmacenamiento> Cola;
    public  LinkedList<String> Archivos;
    // para el manejo del archivo almacenamiento
    private ArchivoAlma alma;

    public PoolAlmacenamiento(int nHebras, LinkedList<String> Archivos){
        this.nHebras = nHebras;
        Cola = new LinkedBlockingQueue<ProcesosAlmacenamiento>();
        Hebras = new ListaHebras[nHebras];
        this.Archivos = Archivos;
        this.alma = new ArchivoAlma();
        for (int i=0; i < this.nHebras; i++){
            Hebras[i] = new ListaHebras();
            Hebras[i].start();  
        }
    }

    private HashMap<String, ListaHebras> getDisponibles(){
        HashMap<String, ListaHebras> disponibles = new HashMap<String, ListaHebras>();
        String temp;
        for (ListaHebras var : Hebras) {
            temp = var.getIp();
            if(temp != null){
                disponibles.put(temp, var);
            }
        }

        return disponibles;
    }

    public LinkedList<String> funcionls(){                                  
        LinkedList<String> disponibles = new LinkedList<String>();
        HashMap<String, ListaHebras> hebrasdisp = this.getDisponibles();
        ListaHebras var;
        // diccionario que te mostre antes
        // la idea es crear una clase similar a ArchivoLog
        HashMap<String,List<String>> archivos = alma.getDict();        //archivo = [ips], ...

        for (String texto : archivos.keySet()) {                   //texto in archivos
            this.Archivos.clear(); // lista que almacena las partes que estan
            // reseteamos la lista Archivos
            for (int i = 0; i < archivos.get(texto).size(); i++) {
                var = hebrasdisp.get(archivos.get(texto).get(i)); // obtengo la hebra de esa ip
                if(var != null){
                    var.getls(texto + ".parte" + i); //archivo + parte + i esima parte en la lista de ips
                } else{ // significa que una de las ip no esta por lo tanto se termina este ciclo
                    break;
                }
            }
            // if listaArchivo == archivos.get(text)
            if (this.Archivos.size() == (archivos.get(texto)).size()){
                disponibles.add(texto); // agregar texto a la lista disponibles
            }
        }
        return disponibles;
    }

    public long funcionGet(String archivo){

        HashMap<String,List<String>> archivos = alma.getDict(); // archivo = [ip1, ip2..], ..
        List<String> ips = archivos.get(archivo);               // ips del archivo 
        HashMap<String, ListaHebras> hebrasdisp = this.getDisponibles();
        ListaHebras var;

        // primero obtenemos todas las partes de las maquinas

        for (int i = 0; i < ips.size(); i++){        //obtengo el total de bytes de todas las partes del archivo

            var = hebrasdisp.get(ips.get(i));
            if (var != null) {
                var.getGet(archivo + ".parte" + i);
            }
            else{ // no se podra obtener todo el archivo, por lo que lo ideal seria eliminar las cosas antes de terminar
                System.out.println("Error al intentar comunicarce con una maquina");
                return -1;
            }
        }

        // ahora vemos el tamaño del archivo y se lo entregamos a procesoCliente
        long total = 0;

        for (int i = 0; i < ips.size(); i++) {
            total += new File(archivo + ".parte" + i).length();
        }
        return total;
        // parte = 0;
        // try {

        //     File file = new File(archivo);  //creo el archivo con el nombre del archivo

        //     byte[] bytearray = new byte[(int)file.length()];
        //     DataInputStream bis = new DataInputStream(new FileInputStream(archivo));
        //     bis.readFully(bytearray, 0, bytearray.length);

        //     for(i = 0; i < total; i++){

        //         if (i == 64000) { //termino de leer una parte, luego leo otra
        //             fos.close();
        //             ipdisponible.get(parte%ipdisponible.size()).getGet(archivo+ ".parte" + parte);
        //             parte ++;
        //             temp = new File(archivo + ".parte" + parte);
        //             fos = new FileOutputStream(temp);
        //         }
        //         file.write(bytearray[i]);
        //     }
        //     bis.close();
        //     file.close();

        // } catch (Exception e){
        //     System.out.println("error al obtener el archivo");
        //     e.printStackTrace();
        // }


    }

    public void funcionPut(String archivo){
        archivo = archivo.substring(4);
        alma.setFile(archivo); // para generar una linea con el nombre del archivo
        // ver que hebras tenemos disponibles
        HashMap<String, ListaHebras> hebrasdisp = this.getDisponibles();
        
        LinkedList<String> ipdisponible = new LinkedList<String>(hebrasdisp.keySet());

        // ir hebra por hebra mientras se divide el archivo
        int total = 64*1024; // 64KB
        int parte = 0; // parte a guardar
        try {  
            File file = new File(archivo); // archivo completo
            
            // guardo el archivo en un arreglo
            byte[] bytearray = new byte[(int)file.length()];
            DataInputStream bis = new DataInputStream(new FileInputStream(archivo));
            bis.readFully(bytearray, 0, bytearray.length);

            // para el archivo temporal
            File temp = new File(archivo + ".parte" + parte); // una parte del archivo
            FileOutputStream fos = new FileOutputStream(temp);
            alma.setIP(archivo, ipdisponible.get(parte));

            // guardo temporalmente el archivo y lo envio
            int i;
            for (i = 0; i < file.length(); i++) {
                if (i%total == 0 && i != 0) { // termino el archivo temp y genero otro
                    fos.close();
                    hebrasdisp.get(ipdisponible.get(parte%ipdisponible.size())).getPut(archivo+ ".parte" + parte);
                    parte ++;
                    temp = new File(archivo + ".parte" + parte);
                    fos = new FileOutputStream(temp);
                    alma.setIP(archivo, ipdisponible.get(parte%ipdisponible.size()));
                }
                fos.write(bytearray[i]);
            }
            if (i%total != 0) {
                fos.close();
                hebrasdisp.get(ipdisponible.get(parte%ipdisponible.size())).getPut(archivo+ ".parte" + parte);
            }
            bis.close();
            file.delete();
        } catch (Exception e) {
            System.out.println("Error al enviar el archivo a las maquinas");
            e.printStackTrace();
        }
        // mientras se guarda hacer ArchivoAlma.setIP(file, ip),
        // para guardar que esta parte esta en esta ip
        // finalmente eliminar el archivo
    }

    public String funcionDelete(String archivo){
        // hebras disponibles para eliminar el archivo
        HashMap<String, ListaHebras> hebrasdisp = this.getDisponibles();
        ListaHebras var;
        // quizas sea similar a put
        // eliminarlo del archivo
        // ArchivoAlma.delete(file)
        int count = 0;
        List<String> ips = alma.delete(archivo);
        if(ips != null){
            String ip;
            for (int i = 0; i < ips.size(); i++) {
                ip = ips.get(i);
                var = hebrasdisp.get(ip);
                if(var != null){
                    var.getDelete(archivo + ".parte" + i);
                }else{
                    System.out.println("No se pudo eliminar el contenido de la ip: " + ip);
                    count ++;
                }               
            }
            if(count == ips.size()){
                return "El archivo no pudo ser eliminado en su totalidad";
            }
            else{
                return "El archivo fue eliminado con exito";
            }
        }
        else{
            return "El archivo no existe";
        }
    }

    public void ejecutar(ProcesosAlmacenamiento proceso){
        synchronized(Cola){
            Cola.add(proceso);
            Cola.notify();
        }
    }
    public class ListaHebras extends Thread{
        ProcesosAlmacenamiento proceso;

        public ListaHebras(){
            proceso = null;
        }
        
        public String getIp(){
            if (proceso != null) {
                // return this.proceso.getIp(); // retorna con puerto
                //System.out.println(this.proceso.socket.getInetAddress());
                String ip = this.proceso.getIp();
                if (ip != null) {
                    return ip; // retorna sin puerto
                }else{
                    proceso = null;
                    return null;
                }
            }
            else{
                return null;
            }
        }

        public void getls(String arch){                   //public void getls(String arch)
            if(proceso != null){
                synchronized(proceso){
                    proceso.setOpcion("check "+ arch);   // proceso.setOpcion("check" + arch) //arch = arch .extension . part i
                    proceso.run();
                }                
            }
        }

        public void getDelete(String arch){
            if(proceso != null){
                synchronized(proceso){
                    proceso.setOpcion("delete " + arch);
                    proceso.run();
                }                
            }
        }


        public void getPut(String arch){
            if(proceso != null){
                synchronized(proceso){
                    proceso.setOpcion("put " + arch);
                    proceso.run();
                }                
            }
        }


        public void getGet(String arch){
            if (proceso != null){
                synchronized(proceso){
                    proceso.setOpcion("get " + arch);
                    proceso.run();
                }
            }
        }
    
        public void run(){
            while(true){
                //System.out.println("Ocurre");
                synchronized(Cola){
                    while(Cola.isEmpty()){
                        try{
                            Cola.wait();
                        } catch(InterruptedException e){
                            System.out.println("Ocurrio un error en la espera de la cola "+ e.getMessage());
                        }
                    }
                    proceso = Cola.poll();
                }
            }
        }
    }
}