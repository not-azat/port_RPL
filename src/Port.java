import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Port {
    InputStream getInputStream();
    OutputStream getOutputStream();
    void close() throws IOException;
}
