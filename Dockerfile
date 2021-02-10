FROM frolvlad/alpine-java:jdk8.202.08-slim
COPY target/bill-1.1.jar /
CMD ["java", "-jar", "bill-1.1.jar"]
