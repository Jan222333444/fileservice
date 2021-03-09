package de.dhbw.fileservice;

import de.dhbw.fileservice.entity.DocumentEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface IStorageService {

    void init();

    String store(MultipartFile file) throws IOException;

    Stream<Path> loadAll();

    FileInputStream load(String filename) throws FileNotFoundException;

    Resource loadAsResource(String filename);

    void deleteAll();

    void delete(DocumentEntity documentEntity) throws IOException;

}
