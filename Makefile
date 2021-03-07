.phony: init
init:
	@echo "Initialising..."
	mkdir "storage"
	make build


.phony: build
build:
	@echo "Building image ..."
	mvn clean package -DskipTests
	docker-compose build
	@echo "Finished building 1/3 images"

.phony: startup
startup:
	docker-compose up --build -d

.phony: test
test:
	docker-compose up -d
	mvn test
	docker-compose down


