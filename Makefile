.phony: init
init:
	@echo "Initialising..."
	mkdir "storage"
	make build


.phony: build
build:
	@echo "Building image ..."
	docker-compose run db -d
	mvn clean package
	docker build
	@echo "Finished building 1/3 images"

.phony: startup
startup:
	docker-compose up --build -d
