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

    public LinkedList<String> funcionls(){                                  
        LinkedList<String> disponibles = new LinkedList<String>();
        int cont;
        // diccionario que te mostre antes
        // la idea es crear una clase similar a ArchivoLog
        HashMap<String,List<String>> archivos = alma.getDict();        //archivo = [ips], ...

        for (String texto : archivos.keySet()) {                   //texto in archivos
            cont = 0;
            // reseteamos la lista Archivos
            for(String ip : archivos.get(texto)){                 //ip in ips de archivos
                for (ListaHebras var : Hebras) {                  //var  in hebras
                    if (ip.equals(var.getIp())){                 //si calza la ip con la de la hebra
                        cont +=1;
                        var.getls(texto + ".part" + archivos.get(texto).indexOf(ip));  //archivo + parte + i esima parte en la lista de ips
                        break;
                    }
                }

            }
            if (cont == (archivos.get(texto)).size()){
                disponibles.add(texto);
            }
            // if listaArchivo == archivos.get(text)
            // agregar texto a la lista disponibles

        }

        return disponibles;
    }

    public void funcionGet(String archivo){
        HashMap<String,List<String>> archivos = alma.getDict(); // igual que antes
        List<String> ips = archivos.get(archivo);
        // recorremos la lista cada hebra obtendra una parte y aqui las juntamos
        for(String ip : ips){
            for (ListaHebras var : Hebras) {
                // if ip == var.getIp
                // var.getget
                // break
            }
            // agregar parte al archivo
        }
        // deberiamos tener todo el archivo
    }

    public void funcionPut(String archivo){
        archivo = archivo.substring(4);
        alma.setFile(archivo); // para generar una linea con el nombre del archivo
        // ver que hebras tenemos disponibles
        LinkedList<ListaHebras> ipdisponible = new LinkedList<ListaHebras>();
        for(ListaHebras var : Hebras){
            if (var.getIp() != null) {
                ipdisponible.add(var);
            }
        }
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
            alma.setIP(archivo, ipdisponible.get(parte).getIp());

            // guardo temporalmente el archivo y lo envio
            int i;
            for (i = 0; i < file.length(); i++) {
                if (i%total == 0 && i != 0) { // termino el archivo temp y genero otro
                    fos.close();
                    ipdisponible.get(parte%ipdisponible.size()).getPut(archivo+ ".parte" + parte);
                    parte ++;
                    temp = new File(archivo + ".parte" + parte);
                    fos = new FileOutputStream(temp);
                    alma.setIP(archivo, ipdisponible.get(parte%ipdisponible.size()).getIp());
                }
                fos.write(bytearray[i]);
            }
            if (i%total != 0) {
                fos.close();
                ipdisponible.get(parte%ipdisponible.size()).getPut(archivo+ ".parte" + parte);
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

    public void funcionDelete(String archivo){
        // quizas sea similar a put
        // eliminarlo del archivo
        // ArchivoAlma.delete(file)
        List<String> ips = alma.delete(archivo);
        if(ips != null){
            String ip;
            for (int i = 0; i < ips.size(); i++) {
                ip = ips.get(i);
                System.out.println(ip);
                for (ListaHebras var : Hebras) {
                    if( ip.equals(var.getIp())){
                        var.getDelete(archivo + ".parte" + i);
                        break;
                    }
                }
                
            }
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
                return this.proceso.socket.getInetAddress().toString(); // retorna sin puerto
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