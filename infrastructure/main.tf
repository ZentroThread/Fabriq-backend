terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0.1"
    }
  }
  backend "s3" {
    bucket         = "fabriq-app-terraform-state-12345"
    key            = "global/s3/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "fabriq-terraform-locks"
    encrypt        = true
  }
}

provider "docker" {
  host = "npipe:////./pipe/docker_engine" # Windows Pipe
}


resource "docker_network" "fabriq_net" {
  name = "fabriq-network"
}

resource "docker_image" "redis" {
  name         = "redis:7-alpine"
  keep_locally = true
}

resource "docker_container" "redis" {
  name    = "fabriq-redis"
  image   = docker_image.redis.image_id
  restart = "always"

  ports {
    internal = 6379
    external = 6379
  }

  networks_advanced {
    name = docker_network.fabriq_net.name
  }

  # Redis Volume (Persist data)
  volumes {
    container_path = "/data"
    volume_name    = docker_volume.redis_data.name
  }
}

resource "docker_volume" "redis_data" {
  name = "redis_data"
}

resource "docker_image" "zookeeper" {
  name         = "confluentinc/cp-zookeeper:7.5.0"
  keep_locally = true
}

resource "docker_container" "zookeeper" {
  name    = "zookeeper"
  image   = docker_image.zookeeper.image_id
  restart = "always"

  ports {
    internal = 2181
    external = 2181
  }

  env = local.zookeeper_environment_variables

  networks_advanced {
    name = docker_network.fabriq_net.name
  }
}

resource "docker_image" "kafka" {
  name         = "confluentinc/cp-kafka:7.5.0"
  keep_locally = true
}

resource "docker_container" "kafka" {
  name    = "kafka"
  image   = docker_image.kafka.image_id
  restart = "always"

  depends_on = [docker_container.zookeeper]

  ports {
    internal = 9092
    external = 9092
  }

  # Mount the JAAS file from your current directory
  volumes {
    host_path      = "${path.cwd}/kafka_server_jaas.conf"
    container_path = "/etc/kafka/kafka_server_jaas.conf"
  }

  env = local.kafka_environment_variables

  networks_advanced {
    name = docker_network.fabriq_net.name
  }

}