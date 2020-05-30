import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimedUpdates extends Thread{
    private ArrayList<String> fileList = new ArrayList<>();
    private ArrayList<String> updateList = new ArrayList<>();
    private String path;
    private Logger logger;
    private boolean running = true;

    public TimedUpdates(Logger logger){
        this.logger = logger;
        path = System.getProperty("user.dir");
        System.out.println(path);
        //TODO: Windows - Linux change
        path = path+"\\nodeFiles"; //For windows
        //path = path.concat("/nodeFiles"); //For Linux
    }

    @Override
    public void run() {
        logger.log(Level.INFO,"["+TimedUpdates.currentThread().getId()+" | "+TimedUpdates.currentThread().getName()+"] starting replication filecheck Thread");
        int amount = 0;
        while (running){
            updateFiles();
            try {
                TimedUpdates.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (amount == 0){
                System.out.println("Replication start process complete");
                amount++;
            }
        }
    }

    private void updateFiles() {
        ArrayList <String> update = scanFiles();
        ArrayList <String> current = fileList;
        boolean notChanged = update.equals(current);
        if (!notChanged){
            logger.log(Level.INFO,"["+TimedUpdates.currentThread().getId()+" | "+TimedUpdates.currentThread().getName()+"] Filecheck Thread detected a change in nodeFiles");
            System.out.println("Detected a change in nodeFiles directory");
            changes(current);
        }
        fileList = update;
    }

    private void changes(ArrayList<String> fileList) {
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (fileList.contains(child.getName())){    //file is still there
                    fileList.remove(child.getName());
                } else {
                    System.out.println("File(s) added: "+child.getName()); //file is added
                    logger.log(Level.INFO,"["+TimedUpdates.currentThread().getId()+" | "+TimedUpdates.currentThread().getName()+"] Client added file(s): "+child.getName());
                    updateList.add(child.getName());
                    YClient.replicateFile(child.getName());
                }
            }
            if (!fileList.isEmpty()) {  //file had been deleted
                logger.log(Level.INFO,"["+TimedUpdates.currentThread().getId()+" | "+TimedUpdates.currentThread().getName()+"] Client deleted file(s): "+fileList.toString());
                System.out.println("File(s) deleted: " + fileList.toString());
                updateList.clear();
                for (int i = 0 ; i < fileList.size() ; i++){
                    String fileName = fileList.get(i);
                    YClient.deleteFile(fileName);
                }
            }
        }
    }

    private ArrayList<String> scanFiles() {
        try (Stream<Path> walk = Files.walk(Paths.get(path))) {
            ArrayList<String> fileList = new ArrayList<>();
            List<String> result = walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
            //remove full directory, so that only the filename remains and add to "fileList"
            for (String x : result) {
                x = x.replaceAll("[\\/|\\\\|\\*|\\:|\\||\"|\'|\\<|\\>|\\{|\\}|\\?|\\%|,]", "");
                String[] parts = x.split("nodeFiles"); //add directory name here
                String file = parts[1];
                fileList.add(file);
            }
            return fileList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
