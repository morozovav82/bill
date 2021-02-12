FROM frolvlad/alpine-java:jdk8.202.08-slim
COPY target/bill-1.2.jar /
CMD ["java", "-jar", "bill-1.2.jar"]
