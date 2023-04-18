import java.io.IOException;
import java.net.*;

public class UDPClient {
    public static void main(String[] args) throws IOException {
        // Parse the server and port from the command-line arguments
        String serverHostname = args[0];
        int serverPort = Integer.parseInt(args[1]);

        // Create a DatagramSocket object to send and receive UDP packets
        DatagramSocket socket = new DatagramSocket();

        // Read input from the console and send it to the server
        byte[] sendBuffer;
        byte[] receiveBuffer = new byte[4096];
        

        // Read input from the console and send it to the server
        while (true) {
            // Send a message to the server to establish connection
            String connectMessage = "Connect";
            sendBuffer = connectMessage.getBytes();
            InetAddress serverAddress = InetAddress.getByName(serverHostname);
            DatagramPacket connectPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, serverPort);
            socket.send(connectPacket);

            // Receive the initial message from the server
            DatagramPacket initialPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(initialPacket);
            System.out.println(new String(initialPacket.getData(), 0, initialPacket.getLength()));


            //Get the Quote
            System.out.print("Input anything for a quote:");
            String message = System.console().readLine();

            if (message == null) {
                break;
            }

            sendBuffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, serverPort);
            socket.send(packet);

            // Receive a response from the server
            DatagramPacket newPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(newPacket);
            System.out.println("Here is your quote: " + new String(newPacket.getData(), 0, newPacket.getLength()));
        }

        // Close the socket when done
        socket.close();
    }
}
