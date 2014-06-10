import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class EthernetPort implements Port {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    EthernetPort(String host, int port) {
        System.out.printf("EthernetPort to host %s, port %d\n", host, port);
        try {
            this.socket = new java.net.Socket(host, port);
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
