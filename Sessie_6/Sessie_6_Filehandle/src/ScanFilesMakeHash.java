import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScanFilesMakeHash {

    String path = "C:\\\\Users\\\\thoma\\\\Documents\\\\Distributed_Systems_404\\\\Sessie_5\\\\Sessie_5_Client\\\\FileDirect";

        public ArrayList<String> searchFiles2 () {
            ArrayList<String> filesList = new ArrayList<>();
            try (Stream<Path> walk = Files.walk(Paths.get(path))) {
                List<String> result = walk.filter(Files::isRegularFile)
                        .map(x -> x.toString()).collect(Collectors.toList());
                //result.forEach(System.out::println);
                //System.out.println(result);
                for ( String x : result){
                    x = x.replaceAll(
                            "[\\/|\\\\|\\*|\\:|\\||\"|\'|\\<|\\>|\\{|\\}|\\?|\\%|,]",
                            "");
                    //System.out.println(x);
                    String[] parts = x.split("FileDirect");
                    String file =parts[1];
                    filesList.add(file);
                    //System.out.println(file);
                }

                return filesList;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }
    }
