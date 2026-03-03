# locals.tf

locals {
  # We define the messy list here
  kafka_environment_variables = [
    "KAFKA_BROKER_ID=1",
    "KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181",

    # Security & JAAS
    "KAFKA_OPTS=-Djava.security.auth.login.config=/etc/kafka/kafka_server_jaas.conf -Xmx256m -Xms256m",
    "KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT,EXTERNAL:SASL_PLAINTEXT",
    "KAFKA_SASL_ENABLED_MECHANISMS=PLAIN",
    "KAFKA_SASL_MECHANISM_INTER_BROKER_PROTOCOL=PLAINTEXT",

    # Listeners
    "KAFKA_LISTENERS=EXTERNAL://0.0.0.0:9092,PLAINTEXT_INTERNAL://0.0.0.0:29092",

    # ⚠️ Dynamic Variable usage works perfectly here!
    "KAFKA_ADVERTISED_LISTENERS=EXTERNAL://${var.kafka_advertised_listener}:9092,PLAINTEXT_INTERNAL://kafka:29092",

    "KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT_INTERNAL",
    "KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1"
  ]

  zookeeper_environment_variables = [
    "ZOOKEEPER_CLIENT_PORT=2181",
    "ZOOKEEPER_TICK_TIME=2000"
  ]
}