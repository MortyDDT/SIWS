package it.uniroma3.siw.upload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.io.File;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.web.multipart.MultipartFile;

/* THIS CLASS IS USED EXCLUSIVELY FOR UPLOADING FILES IN THE CHOSEN FOLDER IN THE FILE SYSTEM */

public class FileUploadUtil {

	public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
		
		while (Files.exists(Paths.get(uploadDir)))								// se il path era usato precedentemente cancellalo + il vecchio file
			FileUtils.deleteDirectory(new File(uploadDir));	

		Path uploadPath = Paths.get(uploadDir);

		if(!Files.exists(uploadPath))
			Files.createDirectories(uploadPath);
		
		try (InputStream inputStream = multipartFile.getInputStream()) {
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ioe) {
			throw new IOException("Could not save image file: " + fileName, ioe);
		}
	}
}