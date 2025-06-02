FROM ubuntu:latest
LABEL authors="gabal"

ENTRYPOINT ["top", "-b"]