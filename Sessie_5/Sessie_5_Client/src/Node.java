
import static java.lang.StrictMath.abs;

public class Node {

    private String hostname;
    private String IP;

    private int currentID;
    private int previousID;
    private int nextID;


    public Node(String hostname, String IP){
        this.hostname = hostname;
        this.currentID = hashCode();
        this.IP = IP;
    }

    public void setPreviousID(int previousID) {
        this.previousID = previousID;
    }

    public void setNextID(int nextID) {
        this.nextID = nextID;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getCurrentID() {
        return currentID;
    }

    public int getNextID() { return nextID; }

    public int getPreviousID() { return previousID; }

    public String getHostname() {
        return hostname;
    }

    public String getIP() {
        return IP;
    }

    @Override
    public int hashCode() {
        long max = 2147483647;
        long min = -2147483647;

        double result = (hostname.hashCode()+max)*(327680d/(max+abs(min)));

        return (int) result;
    }
}
