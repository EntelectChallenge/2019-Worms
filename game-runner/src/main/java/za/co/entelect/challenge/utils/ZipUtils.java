package za.co.entelect.challenge.utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class ZipUtils {

    private static final Logger LOGGER = LogManager.getLogger(ZipUtils.class);

    public static File extractZip(File zip) throws Exception {

        LOGGER.info(String.format("Extracting zip: %s", zip.getName()));
        ZipFile zipFile = new ZipFile(zip);

        String extractFilePath = String.format("%s/extracted/%s", "tournament-tmp", zip.getName().replace(".zip", ""));
        File extractedFile = new File(extractFilePath);

        zipFile.extractAll(extractedFile.getCanonicalPath());

        // Select the actual bot folder located inside the extraction folder container
        extractedFile = extractedFile.listFiles()[0];

        return extractedFile;
    }

    public static File createZip(String zipName, String directory) throws Exception {

        LOGGER.info(String.format("Creating zip: %s for directory: %s", zipName, directory));

        ZipFile zipFile = new ZipFile(new File(String.format("%s.zip", zipName)));

        File path = new File(directory);
        path = path.listFiles()[0];

        ZipParameters parameters = new ZipParameters();
        zipFile.addFolder(path, parameters);

        return zipFile.getFile();
    }
}
