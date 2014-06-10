import javax.comm.CommPortIdentifier;
import java.io.*;
import java.util.Enumeration;

public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("Start");
            System.out.println("sun.arch.data.model: " + System.getProperty("sun.arch.data.model"));
            Enumeration e = CommPortIdentifier.getPortIdentifiers();
            while (e.hasMoreElements()) {
                CommPortIdentifier id = (CommPortIdentifier)e.nextElement();
                System.out.println("Available: " + id.getName());
            }
            Port p = new EthernetPort("10.13.13.161", 1024);
            //Port p = new EthernetServerPort(4444);
            //Port p = new SerialPort("COM8");
            PortFeedbackLoop feedbackLoop = new DefaultPortFeedbackLoop();
            feedbackLoop.start(p.getInputStream());
            UserInputLoop inputLoop = new HEXUserInputLoop(); //  new ASCIIUserInputLoop();
            inputLoop.start(p.getOutputStream()); // blocks forever
            p.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("Goodbye!");
    }


    ///////// USER INPUT //////////////

    static abstract class UserInputLoop {
        void start(OutputStream os) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("> ");
                String input = br.readLine();
                sendBytes(getBytesFromInputLine(input), os);
                System.out.println();
            }
        }

        private void sendBytes(byte[] bytes, OutputStream os) throws IOException {
            os.write(bytes);
            os.flush();
        }

        abstract byte[] getBytesFromInputLine(String inputLine);
    }



    static class ASCIIUserInputLoop extends UserInputLoop {

        byte[] getBytesFromInputLine(String inputLine) {
            try {
                return inputLine.getBytes("US-ASCII");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }



    static class HEXUserInputLoop extends UserInputLoop {
        byte[] getBytesFromInputLine(String inputLine) {
            return hexStringToByteArray(inputLine);
        }

        public static byte[] hexStringToByteArray(String s) {
            int len = s.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
            }
            return data;
        }
    }


    ///////////// PORT FEEDBACK //////////////////


    static  abstract class PortFeedbackLoop {
        public void start(final InputStream is) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            char[] bytes = readBytesWithTO(is, 5000);
                            if (bytes.length > 0) {
                                System.out.println(interpretFeedbackBytes(bytes));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }

        /**
         * Read some number of bytes from InputStream, blocks for no more then toMillis + some constant time. Returns immediately when any data
         * available or on timeout.
         * Some further data may be available in InputStream just after this call returns.
         * On timeout empty array will be returned.
         */
        private static char[] readBytesWithTO(InputStream is, long toMillis) throws IOException {
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start <= toMillis) {
                int available = is.available();
                if (available > 0) {
                    char[] bytes = new char[available];
                    for (int i = 0; i < available; i++) {
                        bytes[i] = (char) is.read();
                    }
                    return bytes;
                } else {
                    try {
                        Thread.sleep(30);
                    } catch(InterruptedException e) {
                        Thread.currentThread().interrupt(); // restoring the interrupt, someone may be interested in it, we just ignore.
                    }
                }
            }
            return new char[0]; // timeout
        }

        abstract String interpretFeedbackBytes(char[] bytes);
    }


    /**
     * Output as hex and as text (in default encoding).
     */
    static class DefaultPortFeedbackLoop extends PortFeedbackLoop {
        @Override
        String interpretFeedbackBytes(char[] bytes) {
            return "reading: (length = " + bytes.length + ")"
                    + "\n as HEX: " + charArrayAsHex(bytes)
                    + "\n as text: " + new String(bytes);
        }

        final private static char[] hexArray = "0123456789ABCDEF".toCharArray();

        private static String charArrayAsHex(char[] bytes) {
            StringBuilder bld = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                char current = bytes[i];
                char lsp = hexArray[current & 0x0F];  // lowest 4 bits
                char msp = hexArray[current >>> 4];   // greatest 4 bits
                bld.append(msp).append(lsp);
            }
            return bld.toString();
        }
    }
}
