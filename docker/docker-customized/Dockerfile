FROM openjdk:11-jdk-slim
RUN apt-get update && apt-get install -y wget curl unzip gnupg software-properties-common net-tools && rm -rf /var/lib/apt/lists/*
RUN wget -qO - https://packages.confluent.io/deb/7.4/archive.key | apt-key add - && add-apt-repository "deb [arch=amd64,arm64] https://packages.confluent.io/deb/7.4 stable main" && apt-get update
RUN apt-get install -y confluent-platform && rm -rf /var/lib/apt/lists/*
RUN wget -P /usr/share/java/jetty/ https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-jaas/9.4.24.v20191120/jetty-jaas-9.4.24.v20191120.jar


COPY security/jaas_config.file /etc/ksqldb/jaas_config.file
COPY security/password-file /etc/ksqldb/password-file
COPY security/KsqlServer-Props-in-jaas_config.file /etc/ksqldb/KsqlServer-Props-in-jaas_config.file
COPY ksqldb-server.properties /etc/ksqldb/ksql-server.properties


ENV KSQL_OPTS="-Djava.security.auth.login.config=/etc/ksqldb/jaas_config.file"
ENV KSQL_CLASSPATH="/usr/share/java/jetty/jetty-jaas-9.4.24.v20191120.jar"

EXPOSE 2181 29092 8088 9021

CMD ["sh", "-c", "zookeeper-server-start /etc/kafka/zookeeper.properties & kafka-server-start /etc/kafka/server.properties & ksql-server-start /etc/ksqldb/ksql-server.properties & control-center-start /etc/confluent-control-center/control-center.properties & tail -f /dev/null"]
