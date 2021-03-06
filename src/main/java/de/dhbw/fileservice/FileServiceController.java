package de.dhbw.fileservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@RestController
public class FileServiceController {

    private final IStorageService storageService;

    public FileServiceController(){
        storageService = new StorageService();
    }


    @GetMapping("api/getText/{id}")
    public String getPlainTextOfFile(@PathVariable String id){
        return id;
    }

    @PostMapping("api/addDocument")
    public ResponseEntity addDocument(@RequestParam("file") MultipartFile file) throws IOException {
        try {

            if(file == null || file.isEmpty()){
                return new ResponseEntity("",HttpStatus.BAD_REQUEST);
            }
            String path = storageService.store(file);

            String name = file.getOriginalFilename();
            String out = "{\n";
            out += "\"status\":200\n";
            out += "}";
            ResponseEntity response = new ResponseEntity(out,HttpStatus.OK);
            return response;
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
