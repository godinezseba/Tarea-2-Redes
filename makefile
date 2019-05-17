all: compileclient compileserver

compileclient: jarclient
	mv Cliente.jar ./Cliente
compileserver: jarserver
	mv Servidor.jar ./Servidor

jarclient: classesClient
	jar cfm Cliente.jar manifest2.mf -C build/classes/Cliente .

jarserver: classesServer
	jar cfm Servidor.jar manifest1.mf -C build/classes/Servidor .

classesServer: dir
	javac -sourcepath src/ -d build/classes/Servidor src/Redes/Servidor/*.java -Xlint

classesClient: dir
	javac -sourcepath src/ -d build/classes/Cliente src/Redes/Cliente/*.java -Xlint

dir:
	if [ ! -d build/classes ]; then mkdir -p build/classes; fi
	if [ ! -d build/classes/Servidor ]; then mkdir -p build/classes/Servidor; fi
	if [ ! -d build/classes/Cliente ]; then mkdir -p build/classes/Cliente; fi
	if [ ! -d build/jar ]; then mkdir -p build/jar; fi
clean:
	if [ -d build ]; then rm build -R; fi

runserver:
	java -jar Servidor.jar 

runclient:
	java -jar Cliente.jar 
