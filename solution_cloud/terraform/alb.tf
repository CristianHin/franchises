resource "aws_lb" "franchise" {
  name               = "franchise-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = aws_subnet.public[*].id
}

resource "aws_lb_target_group" "franchise" {
  name        = "franchise-tg"
  port        = 8080
  protocol    = "HTTP"
  vpc_id      = aws_vpc.main.id
  target_type = "ip"

  health_check {
    enabled             = true
    healthy_threshold   = 2
    interval            = 30
    matcher            = "200"
    path               = "/actuator/health"
    timeout            = 5
    unhealthy_threshold = 5
  }
}

resource "aws_lb_listener" "front_end" {
  load_balancer_arn = aws_lb.franchise.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.franchise.arn
  }
}