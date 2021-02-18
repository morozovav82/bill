FROM frolvlad/alpine-java:jdk8.202.08-slim
COPY target/bill-2.0.jar /
CMD ["java", "-jar", "bill-2.0.jar"]
