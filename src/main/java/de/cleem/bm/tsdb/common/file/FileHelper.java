package de.cleem.bm.tsdb.common.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileHelper {

    public static void write(final File file, byte[] content) throws FileException {

        if(file==null){
            throw new FileException("File to write is NULL");
        }
        else if(file.exists()&&!file.canWrite()){
            throw new FileException("Can not write to File "+file.getAbsolutePath());
        }
        else if(file.isDirectory()){
            throw new FileException("Can not write to File, target is Directory "+file.getAbsolutePath());
        }

        try {

            Files.write(file.toPath(), content);

        } catch (IOException e) {
            throw new FileException(e);
        }

    }

    public static byte[] read(final File file) throws FileException {

        if(file==null){
            throw new FileException("File to write is NULL");
        }
        else if(!file.exists()){
            throw new FileException("Can not read File "+file.getAbsolutePath());
        }
        else if(file.isDirectory()){
            throw new FileException("Can not read File, target is Directory "+file.getAbsolutePath());
        }
        else if(!file.canRead()){
            throw new FileException("Can not read File, target is not readable "+file.getAbsolutePath());
        }

        try {

            return Files.readAllBytes(file.toPath());

        } catch (IOException e) {
            throw new FileException(e);
        }

    }

}
