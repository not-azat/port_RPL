import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class EthernetServerPort implements Port {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    EthernetServerPort(int port) throws IOException {
        System.out.printf("EthernetServerPort on port %d\n", port);
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Waiting for client connection...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client accepted!");
        this.inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
