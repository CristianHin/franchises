provider "aws" {
  region = "us-east-1"
}

resource "random_password" "db_password" {
  length  = 16
  special = false
}

resource "aws_db_subnet_group" "aurora" {
  name       = "franchise-db-subnet"
  subnet_ids = [aws_subnet.private[0].id, aws_subnet.private[1].id]
}

resource "aws_rds_cluster" "aurora" {
  cluster_identifier     = "franchise-db"
  engine                = "aurora-mysql"
  engine_version        = "8.0"
  database_name         = "franchise_test"
  master_username       = "admin"
  master_password       = random_password.db_password.result
  skip_final_snapshot   = true
  vpc_security_group_ids = [aws_security_group.aurora_sg.id]
  db_subnet_group_name   = aws_db_subnet_group.aurora.name

  serverlessv2_scaling_configuration {
    min_capacity = 0.5
    max_capacity = 1.0
  }
}

resource "aws_rds_cluster_instance" "aurora_instance" {
  cluster_identifier = aws_rds_cluster.aurora.id
  instance_class    = "db.serverless"
  engine            = aws_rds_cluster.aurora.engine
  engine_version    = aws_rds_cluster.aurora.engine_version
}
