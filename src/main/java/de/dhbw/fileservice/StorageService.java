package de.dhbw.fileservice;

import de.dhbw.fileservice.entity.DocumentEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.Stream;

public class StorageService implements IStorageService{
    @Override
    public void init() {

    }

    @Override
    public String store(MultipartFile file) throws IOException {
        UUID uuid = UUID.randomUUID();
        String[] parts = file.getOriginalFilename().split("\\.");
        String ending = parts[parts.length-1];
        String name = uuid.toString() + "." + ending;
        File save = new File("/storage/"+ name);
        file.transferTo(save);
        return name;
    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    @Override
    public FileInputStream load(String filename) throws FileNotFoundException {

        return new FileInputStream(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        return null;
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void delete(DocumentEntity documentEntity) throws IOException {
        File file = new File("/storage/"+documentEntity.getPath());
        file.delete();
    }
}
