variable "aws_rds_db_url" {
  description = "Database URL"
  type        = string
}

variable "aws_rds_db_user" {
  description = "Database Username"
  type        = string
}

variable "aws_rds_db_password" {
  description = "Database Password"
  type        = string
  sensitive   = true # Hides it from logs
}

variable "jwt_secret_key" {
  description = "JWT Secret Key"
  type        = string
  sensitive   = true
}

variable "kafka_advertised_listener" {
  description = "The IP/Host for Kafka to advertise (use localhost for local)"
  type        = string
  default     = "localhost"
}