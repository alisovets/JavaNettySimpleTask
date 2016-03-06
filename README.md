## Nettysimpletask

This helloworld project is not a very successful attempt to perform a test task.
This is a simple implementation of a server using the netty library.
The server receives the request, generates a simple responce and collects statistics.
On the request on special uri displays statistics.
Statistics are stored in straight sets and maps.


## How to build

You require the following to build:
* Latest stable [Oracle JDK 7](http://www.oracle.com/technetwork/java/)
* Latest stable [Apache Maven](http://maven.apache.org/)

Go to the root project directory and input:

mvn install


## How start

Go to the target directory and input:

java -jar nettysimpletask-0.0.1-SNAPSHOT.jar port_number

or 

java -jar nettysimpletask-0.0.1-SNAPSHOT.jar 

for the default (8080) port number.
