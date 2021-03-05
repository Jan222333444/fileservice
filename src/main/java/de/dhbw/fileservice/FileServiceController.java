package de.dhbw.fileservice;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileServiceController {

    private IStorageService storageService;

    public FileServiceController(){

    }


    @GetMapping("api/getText/{id}")
    public String getPlainTextOfFile(@PathVariable String id){
        return id;
    }

    @PostMapping("api/addDocument")
    public void addDocument(@RequestParam("file") MultipartFile file){
        //storageService.store(file);
    }
}
