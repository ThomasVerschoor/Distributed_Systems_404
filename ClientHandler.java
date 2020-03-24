import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

class ClientHandler extends Thread{
    final DataInputStream dataIn;
    final DataOutputStream dataOut;
    final Socket socket;

    ClientHandler(Socket socket, DataInputStream dataIn, DataOutputStream dataOut) {    //Elke keer als er een nieuwe client wil verbinden
        this.dataIn = dataIn;                                                           // maken we een nieuwe instantie van ClientHandler
        this.dataOut = dataOut;
        this.socket = socket;
    }

    @Override
    public void run(){
        String toReturn;
        while(true){
            try{
                String received = dataIn.readUTF();
                if(received.equals("Exit")){
                    System.out.println("Client " + this.socket + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.socket.close();
                    System.out.println("Connection closed");
                    break;
                }
                int recievedNumber = Integer.parseInt(received);
                int calculatedNumber = recievedNumber*2;
                toReturn = Integer.toString(calculatedNumber);
                dataOut.writeUTF(toReturn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try{
            this.dataIn.close();
            this.dataOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
