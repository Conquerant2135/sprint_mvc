package mg.conquerant.sprintmvc.core.mapping;

import java.io.InputStream;

import jakarta.servlet.http.Part;

public class FileParts {

    private FileParts() {
    }

    static FileContainer toContainer(Part filepart) {
        FileContainer fileContainer = new FileContainer();

        fileContainer.setFilename(filepart.getSubmittedFileName());
        fileContainer.setMimeType(filepart.getContentType());

        try (InputStream data = filepart.getInputStream()) {
            fileContainer.setData(data.readAllBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileContainer;
    }
}