# Tarea 2 Redes

## Integrantes:
- Sebastian Godinez, 201673520-8
- Alexander Ruz, 201673613-1

## Compilar:
Al ejecutar el comando `make` por terminal se crearan 2 archivos '.jar', uno corresponde a 'Cliente.jar' con el cual se pueden hacer consultas al servidor y el otro es 'Servidor.jar' el cual como su nombre lo indica sirve para ejecutar el servidor.

Si se desea compilar solo uno de los archivos se puede ejecutar una de las siguientes lineas:
- compilar solo el Servidor: `make compileserver`
- compilar solo el Cliente: `make compileclient`

## Ejecutar:
Una vez compilada la parte que se desea ejecutar, hacer `make run[server|client]`, de no estar en Linux o bi estar en la carpeta que esta el makefile, se puede ejecutar uno de los siguientes comandos:
- ejecutar el Servidor: `java -jar Servidor.jar`
- ejecutar el Cliente: `java -jar Cliente.jar`

## Consideraciones:
- Si se desea ejecutar el Cliente desde un computador externo o una red externa, cambiar el **puerto** en el archivo 'src/Cliente.java', linea 27 donde dice localhost.
- Si se desea cambiar el puerto, para el caso del Cliente es en la misma linea que se menciono antes y para el servidor es en el archivo 'src/Servidor.java', linea 52.
- Las operaciones asumen que el archivo deseado existe, en cualquier otro caso tira error.
- Los nombres de los archivos pueden contener solo numeros o letras.
- Para terminar el servidor terminar el proceso por consola
