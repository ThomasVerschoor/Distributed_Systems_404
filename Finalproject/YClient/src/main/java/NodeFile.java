import static java.lang.StrictMath.abs;

public class NodeFile {

    private String filename;
    private int hash;

    public NodeFile(String filename){
        this.filename = filename;
        this.hash = hashCode();
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public int hashCode(){
        long max = 2147483647;
        long min = -2147483647;

        double result = (filename.hashCode()+max)*(327680d/(max+abs(min)));

        return (int) result;
    }

}
