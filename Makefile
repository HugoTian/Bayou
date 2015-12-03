all:	bayou

bayou:
	javac -d bin -cp lib/commons-codec-1.9.jar src/com/cs380d/application/*.java src/com/cs380d/command/*.java src/com/cs380d/message/*.java src/com/cs380d/framework/*.java src/com/cs380d/utility/*.java

clean:
	rm  -r -f bin/* 



