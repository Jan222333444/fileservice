FROM openjdk:11
ADD target/fileservice-0.0.1-SNAPSHOT.jar /
RUN cd /
EXPOSE 8080
CMD java -jar fileservice-0.0.1-SNAPSHOT.jar