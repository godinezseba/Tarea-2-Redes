package Redes;

//estructuras de datos
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
//archivos
import java.io.File;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;


public class ArchivoAlma{
    final File file;
    
    public ArchivoAlma(){
        this.file = new File("almacenamiento.txt");
        try {
            file.createNewFile();
        } catch (Exception e) {
            System.out.println("Error al intentar crear el archivo almacenamiento");
            e.printStackTrace();
        }
    }
    
    /**
     * Metodo para sobreescribir el archivo almacenamiento
     * @param nuevo diccionario con el que se quiere sobreescribir el archivo
     */
    private void escribirHash(HashMap<String, List<String>> nuevo){
        file.delete();
        try {
            file.createNewFile();
        } catch (Exception e) {
            System.out.println("Error al intentar crear el archivo almacenamiento");
            e.printStackTrace();
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String archivo : nuevo.keySet()) {
                writer.write(archivo);
                for (String ip : nuevo.get(archivo)) {
                    writer.write(" - " + ip);
                }
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error al intentar escribir sobre almacenamiento");
            e.printStackTrace();
        }
    }

    /**
     * Metodo para escribir sobre almacenamiento un nuevo archivo
     * @param archivo el archivo a guardar
     */
    public void setFile(String archivo){
        HashMap<String,List<String>> archivos = this.getDict();
        archivos.put(archivo, new LinkedList<String>());
        this.escribirHash(archivos);
    }

    /**
     * Metodo para agregar una ip al final de la lista de nuestro archivo
     * @param archivo archivo al que se le agregara una nueva ip
     * @param ip ip a agregar al final
     */
    public void setIP(String archivo, String ip){
        HashMap<String,List<String>> archivos = this.getDict();
        LinkedList<String> temp = new LinkedList<String>(archivos.get(archivo));
        temp.add(ip);
        archivos.put(archivo, temp);
        this.escribirHash(archivos);
    }

    /**
     * Se obtiene un diccionario a partir de los datos del archivo almacenamiento
     * @return Diccionario donde:
     * key: nombre archivo
     * valor: lista con el orden en que esta guardado este archivo en las maquinas
     */
    public HashMap<String, List<String>> getDict(){
        HashMap<String,List<String>> archivos = new HashMap<String, List<String>>();
        String linea;
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                linea = sc.nextLine();
                List<String> items = Arrays.asList(linea.split("\\s*-\\s"));
                archivos.put(items.get(0), items.subList(1, items.size()));
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error al leer el archivo almacenamiento");
            e.printStackTrace();
        }

        return archivos;
    }

    /**
     * Se elimina un archivo de la lista
     * @param archivo archivo a eliminar de la lista
     */
    public void delete(String archivo){
        HashMap<String,List<String>> archivos = this.getDict();

        archivos.remove(archivo);

        this.escribirHash(archivos);
    }
}