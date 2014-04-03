import javax.comm.CommPort;
import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * @author abdulvaliev
 */
public class SerialPort {

    private CommPort port;

    /** The input stream. <b>The subclass must initialize this variable.</b> */
    public final InputStream inputStream;

    /** The output stream. <b>The subclass must initialize this variable.</b> */
    public final OutputStream outputStream;

    /** open the serial port. */
    public SerialPort(final String name) throws IOException, PortInUseException, NoSuchPortException {
        System.out.println("Port is found:" + name);

        CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(name);
        port = portId.open(this.toString(), 3000);
        outputStream = port.getOutputStream();
        inputStream = port.getInputStream();

        System.out.println("Port is opened:" + name);
    }

    public boolean isOpen(final CommPortIdentifier portId){
        return portId.getCurrentOwner() != null;
    }

    public void close() throws IOException {
        if (port != null) {
            port.close();
            port = null;
        }
    }
}
