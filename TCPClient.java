import java.io. *;
import java.net. *;
import java.util.logging. *;
import java.util.Scanner;



//Java TCPClient host Port 
public class TCPClient{
    //intalize logger
    private static final Logger logger = Logger.getLogger("TCPClient");
    public static void main (String... args) {
        ConsoleHandler handler = new ConsoleHandler();
        logger.addHandler(handler);
        // Determine the logging level based on the command-line arguments
        Level logLevel = args.length > 2 && args[2].equals("-v") ? Level.INFO : Level.WARNING;
        logger.setLevel(logLevel);  

        // Parse the server and port from the command-line arguments
        String host = args[0];
        int port = Integer.valueOf(args[1]);
        

        //Creating a new Socket object that connects to the server at specifed host and port
        //try and catch block to ensure that we proply close a socket when we done
        try (Socket socket = new Socket(host, port)) {
            logger.info("Connected to server " + host + " on port " + port);
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            ConsoleInputHandler consoleHandler = new ConsoleInputHandler(out);
            Thread consoleThread = new Thread(consoleHandler);
            consoleThread.start();

            // Start the SocketHandler thread to handle incoming data from socket
            SocketHandler socketHandler = new SocketHandler(in);
            Thread socketThread = new Thread(socketHandler);
            socketThread.start();
            
            try{
                consoleThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try{
                socketThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            socket.close();
        } catch(IOException ex) {
            logger.log(Level.SEVERE, "Error connecting to server", ex);

        }
}

    private static class ConsoleInputHandler implements Runnable {
        private OutputStream out;

        public ConsoleInputHandler(OutputStream out) {
            this.out = out;
        }

        public void run() {
            Scanner scanner = new Scanner(System.in);
            try {
                while (true) {
                    String line = scanner.nextLine();
                    
                    //need to convert to bytes for the network
                    out.write(line.getBytes());
                    out.write('\n');

                    //This forca sends information to server after the line is read
                    out.flush();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error sending data to server", ex);
            }
        }
    }

    private static class SocketHandler implements Runnable {
        private InputStream in;

        public SocketHandler(InputStream in) {
            this.in = in;
        }

        public void run() {
            try {
                int readChar = 0;
                while ((readChar = in.read()) != -1) {
                    System.out.write(readChar);
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error receiving data from server", ex);
            }
        }
    }
 }
