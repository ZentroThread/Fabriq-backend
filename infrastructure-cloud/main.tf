terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "s3" {
    bucket         = "fabriq-app-terraform-state-eu-12345"
    key            = "prod/terraform.tfstate"
    region         = "eu-north-1"
    dynamodb_table = "fabriq-terraform-locks"
    encrypt        = true
  }
}

provider "aws" {
  region = "eu-north-1"
}

data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"]

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

resource "aws_security_group" "fabriq_sg" {
  name        = "fabriq-production-sg"
  description = "Security group for Fabriq App"

  # SSH (Port 22)
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTP (Port 80)
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTPS (Port 443)
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Backend API (Port 8081)
  ingress {
    from_port   = 8081
    to_port     = 8081
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Kafka (Port 9092)
  ingress {
    from_port   = 9092
    to_port     = 9092
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_eip" "fabriq_eip" {
  instance = aws_instance.app_server.id
  domain   = "vpc"

  tags = {
    Name = "Fabriq-Production-EIP"
  }
}

# --- EC2 INSTANCE ---
resource "aws_instance" "app_server" {
  # Use the AMI we found automatically
  ami           = data.aws_ami.ubuntu.id
  instance_type = "t3.micro"

  vpc_security_group_ids = [aws_security_group.fabriq_sg.id]

  # *** YOUR KEY NAME (NO .pem) ***
  key_name = "fabriq-key"

  user_data = <<-EOF
                #!/bin/bash
                # Wait for the system to finish its own updates first
                while fuser /var/lib/dpkg/lock >/dev/null 2>&1 ; do sleep 1; done

                # Standard Docker installation for Ubuntu
                apt-get update -y
                apt-get install -y apt-transport-https ca-certificates curl software-properties-common
                curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
                echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

                apt-get update -y
                apt-get install -y docker-ce docker-ce-cli containerd.io

                # Enable and start
                systemctl enable docker
                systemctl start docker

                # Permissions
                usermod -aG docker ubuntu

                # 2. Clone the Repository
                # If private, use: https://<TOKEN>@github.com/username/repo.git
                cd /home/ubuntu
                git clone https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git fabriq-app
                chown -R ubuntu:ubuntu fabriq-app

                #3. Start the App
                cd fabriq-app
                # We use 'sudo -u ubuntu' so docker runs with the right user context
                sudo -u ubuntu docker compose up -d
                EOF
  tags = {
    Name = "Fabriq-Production-Stockholm"
  }
}

output "server_ip" {
  value = aws_eip.fabriq_eip.public_ip
}