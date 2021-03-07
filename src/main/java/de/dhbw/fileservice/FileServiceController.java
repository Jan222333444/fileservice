package de.dhbw.fileservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.fileservice.entity.DocumentEntity;
import de.dhbw.fileservice.entity.DocumentRepository;
import de.dhbw.fileservice.entity.MetaDataEntity;
import de.dhbw.fileservice.entity.MetaDataRepository;
import de.dhbw.fileservice.models.TextExtractionResult;
import de.dhbw.fileservice.models.TextExtractionResultWrapper;
import de.dhbw.fileservice.text_extractor.ExcelTextExtractor;
import de.dhbw.fileservice.text_extractor.WordTextExtractor;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class FileServiceController {

    private final IStorageService storageService;

    @Autowired
    private DocumentRepository documentArchiveRepository;

    @Autowired
    private MetaDataRepository metaDataRepository;

    public FileServiceController() {
        storageService = new StorageService();
    }


    @GetMapping("api/getText/{id}")
    public ResponseEntity getPlainTextOfFile(@PathVariable int id) {
        try {
            Optional<DocumentEntity> entity = documentArchiveRepository.findById(id);
            if (entity.isPresent()) {
                DocumentEntity documentEntity = entity.get();
                if (documentEntity.getPath().split("\\.")[1].equals("docx")) {
                    WordTextExtractor extractor = new WordTextExtractor();
                    TextExtractionResult result = new TextExtractionResult();
                    result.extractedText = extractor.extractText("/storage/" + documentEntity.getPath());
                    result.document_id = documentEntity.getId();
                    String resultString = new ObjectMapper().writeValueAsString(result);
                    return new ResponseEntity(resultString, HttpStatus.OK);
                } else if (documentEntity.getPath().split("\\.")[1].equals("xlsx")) {
                    ExcelTextExtractor extractor = new ExcelTextExtractor();
                    TextExtractionResult result = new TextExtractionResult();
                    result.extractedCells = extractor.extractText("/storage/" + documentEntity.getPath());
                    result.document_id = documentEntity.getId();
                    String resultString = new ObjectMapper().writeValueAsString(result);
                    return new ResponseEntity(resultString, HttpStatus.OK);
                }
                return new ResponseEntity("", HttpStatus.BAD_REQUEST);

            } else {
                return new ResponseEntity("", HttpStatus.NOT_FOUND);
            }
        } catch (IOException exception) {
            return new ResponseEntity("", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("api/getTexts/{ids}")
    public ResponseEntity getTexts(@PathVariable String ids) {
        String[] idArray = ids.split(",");
        TextExtractionResultWrapper wrapper = new TextExtractionResultWrapper();
        wrapper.textExtractionResults = new TextExtractionResult[idArray.length];
        int i = 0;
        for(String id : idArray){
            Optional<DocumentEntity> entity = documentArchiveRepository.findById(Integer.parseInt(id));
            if(entity.isPresent()){
                DocumentEntity document = entity.get();
                if(document.getPath().split("\\.")[1].equals("docx")){
                    WordTextExtractor extractor = new WordTextExtractor();
                    TextExtractionResult result = new TextExtractionResult();
                    result.document_id = document.getId();
                    result.extractedText = extractor.extractText("/storage/"+document.getPath());
                    wrapper.textExtractionResults[i] = result;
                }else if (document.getPath().split("\\.")[1].equals("xlsx")){
                    ExcelTextExtractor extractor = new ExcelTextExtractor();
                    TextExtractionResult result = new TextExtractionResult();
                    result.document_id = document.getId();
                    result.extractedCells = extractor.extractText("/storage/" + document.getPath());
                    wrapper.textExtractionResults[i] = result;
                }
                i++;
            }

        }
        try {
            return new ResponseEntity(new ObjectMapper().writeValueAsString(wrapper), HttpStatus.OK);
        } catch (JsonProcessingException e) {
            return new ResponseEntity("", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("api/addDocument")
    public ResponseEntity addDocument(@RequestParam("file") MultipartFile file) {
        try {

            if (file == null || file.isEmpty()) {
                return new ResponseEntity("", HttpStatus.BAD_REQUEST);
            }
            String path = storageService.store(file);

            String name = file.getOriginalFilename();
            DocumentEntity document = new DocumentEntity();
            document.setPath(path);
            document.setName(name);
            documentArchiveRepository.save(document);
            FileInputStream fileInputStream = (FileInputStream) file.getInputStream();
            if (path.split("\\.")[1].equals("docx")) {
                XWPFDocument wordDocument = new XWPFDocument(fileInputStream);
                XWPFWordExtractor extractor = new XWPFWordExtractor(wordDocument);
                String creator = extractor.getDocument().getProperties().getCoreProperties().getCreator();
                Date created = extractor.getDocument().getProperties().getCoreProperties().getCreated();
                MetaDataEntity metaDataEntity = new MetaDataEntity();
                metaDataEntity.setKey("Author");
                metaDataEntity.setValue(creator);
                metaDataEntity.setDocument(document);
                metaDataRepository.save(metaDataEntity);
                MetaDataEntity createdData = new MetaDataEntity();
                createdData.setDocument(document);
                createdData.setKey("Created");
                createdData.setValue(created.toString());
                metaDataRepository.save(createdData);
            } else if (path.split("\\.")[1].equals("xlsx")) {
                XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                POIXMLProperties properties = workbook.getProperties();
                MetaDataEntity metaDataEntity = new MetaDataEntity();
                metaDataEntity.setDocument(document);
                metaDataEntity.setKey("Author");
                metaDataEntity.setValue(properties.getCoreProperties().getCreator());
                metaDataRepository.save(metaDataEntity);
                MetaDataEntity createdData = new MetaDataEntity();
                createdData.setDocument(document);
                createdData.setKey("Created");
                createdData.setValue(properties.getCoreProperties().getCreated().toString());
                metaDataRepository.save(createdData);
            }

            String out = "{\n";
            out += "    \"status\":200\n";
            out += "}";
            ResponseEntity response = new ResponseEntity(out, HttpStatus.CREATED);
            return response;
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("api/addMetadata")
    public ResponseEntity addMetadata(@RequestParam("key") String key, @RequestParam("value") String value, @RequestParam("id") int id) {
        Optional<DocumentEntity> optional = documentArchiveRepository.findById(id);
        if(optional.isPresent()) {
            DocumentEntity document = optional.get();
            MetaDataEntity metaDataEntity = new MetaDataEntity();
            metaDataEntity.setKey(key);
            metaDataEntity.setValue(value);
            metaDataEntity.setDocument(document);
            metaDataRepository.save(metaDataEntity);
            return new ResponseEntity("", HttpStatus.CREATED);
        }

        return new ResponseEntity("", HttpStatus.NOT_IMPLEMENTED);

    }

    @GetMapping("api/getDocument/{id}")
    public void getDocument(HttpServletRequest request, HttpServletResponse response, @PathVariable int id) throws IOException {
        Optional<DocumentEntity> documentEntity = documentArchiveRepository.findById(id);
        if (documentEntity.isPresent()) {
            DocumentEntity document = documentEntity.get();
            FileOutputStream stream = new FileOutputStream("/storage/" + document.getPath());
            Path file = Paths.get("storage", document.getPath());
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.addHeader("Content-Disposition", "attachment; filename=" + document.getName());
            Files.copy(file, response.getOutputStream());
            response.getOutputStream().flush();
        }
    }
}
