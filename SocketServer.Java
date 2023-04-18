import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

public class SocketServer {

    // ExecutorService for handling multiple TCP client connections.
    static ExecutorService tcpExec;
    static ExecutorService udpExec; 

    // List of quotes from the Princess Bride movie.
    static List<String> quotes = Arrays.asList(
        "Hello. My name is Inigo Montoya. You killed my father. Prepare to die.",
        "As you wish.",
        "You keep using that word. I do not think it means what you think it means.",
        "Inconceivable!",
        "Mawage. Mawage is wot bwings us togeder today.",
        "Another One - DJ Khalid", 
        "Say Hello To My Little Friend - Scareface (Film)", 
        "I Did Do That, I Did Do That - I Think You Should Leave (TV Show)", 
        "WHAAAAAAAAAZZZZUPPPPPPPPP - Haunted House (Film)"
    );

    static Set<String> sentQuotes = new HashSet<>();

    // Get a random quote from the list.
    static String getRandomQuote() {
        String quote;
        do {
            int index = new Random().nextInt(quotes.size());
            quote = quotes.get(index);
        } while (sentQuotes.contains(quote));
        sentQuotes.add(quote);
        if (sentQuotes.size() == quotes.size()) {
            sentQuotes.clear();
        }
        return quote;
    }

    // Handle TCP request.
    public static void handleTCPRequest(Socket socket) {
        try {
            System.out.println("Handling TCP client requesdt");

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // Send short message after connection has been established.
            String shortMessage = "Connected to the Quote Server (TCP).\n";
            out.write(shortMessage.getBytes());

            // Send a random quote of the day.
            String quote = getRandomQuote() + "\n";
            out.write(quote.getBytes());

            in.close();
            out.close();
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Handle UDP request.
    public static void handleUDPRequest(DatagramPacket packet, DatagramSocket socket) {
        try {
            System.out.println("Handling UDP client request");
            
            //saving the IP address of the sent packet
            InetAddress clientAddress = packet.getAddress();
            
            //Saving the port number which client sent the datagram packet
            int clientPort = packet.getPort();

            // Send short message after receiving a datagram.
            String shortMessage = "Connected to the Quote Server (UDP).\n";
            DatagramPacket messagePacket = new DatagramPacket(shortMessage.getBytes(), shortMessage.length(), clientAddress, clientPort);
            socket.send(messagePacket);

            // Send a random quote of the day.
            String quote = getRandomQuote() + "\n";
            DatagramPacket quotePacket = new DatagramPacket(quote.getBytes(), quote.length(), clientAddress, clientPort);
            socket.send(quotePacket);

        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }

    public static void main(String... args) throws Exception {
        int port = 17;

        // Create a server socket for TCP connections.
        ServerSocket tcpServer = new ServerSocket(port);
        tcpExec = Executors.newFixedThreadPool(5);

        // Start listening for TCP connections in a separate thread.
        new Thread(() -> {
                try {
                    while (true) {
                            Socket socket = tcpServer.accept();
                            tcpExec.submit(() -> handleTCPRequest(socket));  
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
    }).start();
        

        // Creating a sever socket for UDP connection to connect to 
        DatagramSocket udpServer = new DatagramSocket(port);
        udpExec = Executors.newFixedThreadPool(5);

        //Start listening to UDP Connections
        new Thread(() -> {
            try {
                while (true) {
                        //buff is used to store the data
                        byte[] buf = new byte[4096];
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        udpServer.receive(packet);
                        udpExec.submit(() -> handleUDPRequest(packet, udpServer));
                }
                } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}