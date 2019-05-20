# Tarea 2 Redes

## Integrantes:
- Sebastian Godinez, 201673520-8
- Alexander Ruz, 201673613-1

## Compilar:
Al ejecutar el comando `make` por terminal se crearan 3 archivos '.jar', uno corresponde a 'Cliente.jar' con el cual se pueden hacer consultas al servidor, el otro es 'Almacenamiento.jar' que almacena fragmentos de los archivos y el otro es 'Servidor.jar' el cual como su nombre lo indica sirve para ejecutar el servidor.

Si se desea compilar solo uno de los archivos se puede ejecutar una de las siguientes lineas:
- compilar solo el Servidor: `make compileserver`
- compilar solo el Cliente: `make compileclient`
- compilar solo el Almacenamiento: `make compilealma`

Para hacer un poco mas visible el interactuar de las maquinas, por defecto se mueven a carpetas con sus respectivos nombres que contienen un makefile con el cual se pueden ejecutar los archivos '.jar'.

## Ejecutar:
Tal como se menciono antes una vez se esta en las carpetas de la parte que se desee ejecutar simplemente ejecutar `make`, de no estar en Linux o no estar en la carpeta que esta el makefile, se puede ejecutar uno de los siguientes comandos:
- ejecutar el Servidor: `java -jar Servidor.jar`
- ejecutar el Cliente: `java -jar Cliente.jar`
- ejecutar el Almacenamiento: `java -jar Almacenamiento.jar`

Tambiense puede hacer uso del makefile dispuesto en esta misma carpeta, simplemente hacer: `make run[server|client|alma]`, dependiendo de lo que se desea ejecutar.

## Consideraciones:
- Si se desea ejecutar el Cliente desde un computador externo o una red externa, cambiar el **puerto** en el archivo 'src/Cliente/Cliente.java', linea 27 donde dice localhost.
- Si se desea ejecutar el Cliente desde un computador externo o una red externa, cambiar el **puerto** en el archivo 'src/Almacenamiento/Almacenamiento.java', linea 21 donde dice localhost.
- Si se desea cambiar el puerto, para el caso del Cliente y el Almacenamiento son las mismas lineas mencionadas antes y para el servidor es en el archivo 'src/Servidor.java', linea 52.
- Las operaciones asumen que el archivo deseado existe, en cualquier otro caso tira error.
- Los nombres de los archivos pueden contener solo numeros o letras.
- Para terminar el *Servidor* terminar el proceso por consola.
- Para terminar un *Almacenamiento* terminar el proceso por consola.
- Para terminar un *Cliente* se puede ejecutar el comando Exit.
- Consideramos que 64KB son 64*1024 bytes, dado que en varios cursos se a tratado asi.