# File Encryptor App
Some of the key features of this application is-
1. Supports encryption and decryption of any type of file
2. Provides forgot key feature to recover previously encrypted files
3. Provides storing feature for encrypted files, to ease tracking of different files
4. Provides option to show a detailed list of previous actions performed by the user

### Note
Some features will ask for a password. This is currently hardcoded to be ```FileEncDec```.

# How to directly use the app
Download a release version from the link down below and follow the steps given there-

https://github.com/SoubhikSen02/File_Encryptor/releases

Currently release version supports Windows devices only

# How to compile from source code
Make sure JDK is installed and environment variables are set up before following any of the compilation steps. To check for JDK, open CMD and type these two commands one by one-
```
javac -version
java -version
```
If command not recognized is shown for either one of the above, then JDK is not installed.

## Simple compile and run
1. Go into ```src``` folder and open CMD in this directory
2. Type the following command to compile the source files-
```
javac SoftEngProject/FileEncryptDecrypt/*.java
```
3. Type the following command to run the app-
```
java SoftEngProject.FileEncryptDecrypt.Main
```

## Compile to JAR and run
1. Go into ```src``` folder and open CMD in this directory
2. Type the following commands one by one to compile the source to JAR-
```
javac SoftEngProject/FileEncryptDecrypt/*.java
jar cfe FileEncryptor.jar SoftEngProject.FileEncryptDecrypt.Main SoftEngProject/FileEncryptDecrypt/*.class
```
3. Type the following command to run the app-
```
java -jar FileEncryptor.jar
```

## Compile to .EXE file
1. Go into ```src``` folder and open CMD in this directory
2. Type the following commands one by one to compile the source to JAR first-
```
javac SoftEngProject/FileEncryptDecrypt/*.java
jar cfe FileEncryptor.jar SoftEngProject.FileEncryptDecrypt.Main SoftEngProject/FileEncryptDecrypt/*.class
```
3. Type the following commands one by one to convert the JAR file to modular JAR file-
```
jdeps --generate-module-info . FileEncryptor.jar
javac --patch-module FileEncryptor=FileEncryptor.jar FileEncryptor/module-info.java
move FileEncryptor/module-info.class .
rmdir /s /q FileEncryptor
jar uf FileEncryptor.jar module-info.class
```
4. Type the following command to convert the modular JAR file to EXE format-
```
jpackage --name FileEncryptor --type "app-image" --module-path "%JAVA_HOME%/jmods";[current folder address] --add-modules FileEncryptor --module FileEncryptor/SoftEngProject.FileEncryptDecrypt.Main --jlink-options --compress=2
```
Make sure to replace ```[current folder address]``` part in the above command with the fully qualified address of the current directory in CMD. To get current directory address, type the command ```cd``` and use the output given for the replacing needed above.

5. Go into the ```FileEncryptor``` folder and run ```FileEncryptor.exe``` to start the app
