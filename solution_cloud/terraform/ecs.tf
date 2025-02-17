resource "aws_cloudwatch_log_group" "franchise" {
  name              = "/ecs/franchise"
  retention_in_days = 30
}

resource "aws_ecs_cluster" "main" {
  name = "franchise-cluster"
}

resource "null_resource" "docker_login" {
  depends_on = [aws_ecr_repository.franchise]

  provisioner "local-exec" {
    command = <<-EOT
      aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${aws_ecr_repository.franchise.repository_url}
    EOT
  }

  triggers = {
    always_run = timestamp()
  }
}

resource "null_resource" "docker_tag" {
  depends_on = [null_resource.docker_login]

  provisioner "local-exec" {
    command = <<-EOT
      docker tag franchise-ms:latest ${aws_ecr_repository.franchise.repository_url}:latest
    EOT
  }

  triggers = {
    always_run = timestamp()
  }
}

resource "null_resource" "docker_push" {
  depends_on = [null_resource.docker_tag]

  provisioner "local-exec" {
    command = <<-EOT
      docker push ${aws_ecr_repository.franchise.repository_url}:latest
    EOT
  }

  triggers = {
    always_run = timestamp()
  }
}

resource "aws_ecs_task_definition" "franchise" {
  depends_on = [null_resource.docker_push]

  family                   = "franchise"
  requires_compatibilities = ["FARGATE"]
  network_mode            = "awsvpc"
  cpu                     = 1024
  memory                  = 2048

  container_definitions = jsonencode([
    {
      name  = "franchise-ms"
      image = "${aws_ecr_repository.franchise.repository_url}:latest"
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
          protocol      = "tcp"
        }
      ]
      environment = [
        {
          name  = "MYSQL_HOST"
          value = aws_rds_cluster.aurora.endpoint
        },
        {
          name  = "MYSQL_USER"
          value = aws_rds_cluster.aurora.master_username
        },
        {
          name  = "MYSQL_PASSWORD"
          value = random_password.db_password.result
        }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = aws_cloudwatch_log_group.franchise.name
          awslogs-region        = "us-east-1"
          awslogs-stream-prefix = "ecs"
        }
      }
    }
  ])

  execution_role_arn = aws_iam_role.ecs_execution_role.arn
  task_role_arn      = aws_iam_role.ecs_task_role.arn
}

resource "aws_ecs_service" "franchise" {
  depends_on = [aws_ecs_task_definition.franchise, aws_rds_cluster_instance.aurora_instance]

  name            = "franchise-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.franchise.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = aws_subnet.private[*].id
    security_groups = [aws_security_group.ecs_tasks.id]
    assign_public_ip = true
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.franchise.arn
    container_name   = "franchise-ms"
    container_port   = 8080
  }
}