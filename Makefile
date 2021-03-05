.phony: build

build:
	@echo "Building image ..."
	mvn clean package
	docker build
	@echo "Finished building 1/3 images"

startup:
	docker-compose up --build -d
