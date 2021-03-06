package de.dhbw.fileservice;

import de.dhbw.fileservice.entity.DocumentEntity;
import de.dhbw.fileservice.entity.DocumentRepository;
import de.dhbw.fileservice.entity.MetaDataEntity;
import de.dhbw.fileservice.entity.MetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.util.List;
import java.util.Optional;

@RestController
public class FileServiceController {

    private final IStorageService storageService;

    @Autowired
    private DocumentRepository documentArchiveRepository;

    @Autowired
    private MetaDataRepository metaDataRepository;

    public FileServiceController(){
        storageService = new StorageService();
    }


    @GetMapping("api/getText/{id}")
    public String getPlainTextOfFile(@PathVariable int id) throws IOException {
        Optional<DocumentEntity> entity = documentArchiveRepository.findById(id);
        if(entity.isPresent()){
            DocumentEntity documentEntity = entity.get();
            if(documentEntity.getName().split("\\.")[1].equals("docx")){
                XWPFDocument wordDocument = new XWPFDocument(storageService.load("/storage/"+documentEntity.getPath()));
                XWPFWordExtractor extractor = new XWPFWordExtractor(wordDocument);
                return extractor.getText();
            }
        }
        return ""+id;
    }

    @PostMapping("api/addDocument")
    public ResponseEntity addDocument(@RequestParam("file") MultipartFile file) throws IOException {
        try {

            if(file == null || file.isEmpty()){
                return new ResponseEntity("",HttpStatus.BAD_REQUEST);
            }
            String path = storageService.store(file);

            String name = file.getOriginalFilename();
            DocumentEntity document = new DocumentEntity();
            document.setPath(path);
            document.setName(name);
            documentArchiveRepository.save(document);
            FileInputStream fileInputStream = (FileInputStream) file.getInputStream();
            XWPFDocument wordDocument = new XWPFDocument(fileInputStream);
            XWPFWordExtractor extractor = new XWPFWordExtractor(wordDocument);
            //String extractedText = extractor.getText();
            String creator = extractor.getDocument().getProperties().getCoreProperties().getCreator();
            MetaDataEntity metaDataEntity = new MetaDataEntity();
            metaDataEntity.setKey("Author");
            metaDataEntity.setValue(creator);
            metaDataEntity.setDocument(document);
            metaDataRepository.save(metaDataEntity);
            String out = "{\n";
            out += "    \"status\":200\n";
            out += "}";
            ResponseEntity response = new ResponseEntity(out,HttpStatus.OK);
            return response;
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("api/addMetadata")
    public ResponseEntity addMetadata(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("id")int id){
        return new ResponseEntity("", HttpStatus.NOT_IMPLEMENTED);

    }
}
