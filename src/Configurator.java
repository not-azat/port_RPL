class Configurator {
    private Main.UserInputLoop userInputLoop;
    private Port port;

    Configurator(String[] args) throws Exception {
        try {
            if ("HEX".equalsIgnoreCase(args[0])) {
                userInputLoop = new Main.HEXUserInputLoop();
            } else if ("ASCII".equalsIgnoreCase(args[0])) {
                userInputLoop = new Main.ASCIIUserInputLoop();
            }

            if ("SERIAL".equalsIgnoreCase(args[1])) {
                String portName = args[2];
                port = new SerialPort(portName);
            } else if ("TCP".equalsIgnoreCase(args[1])) {
                String host = args[2];
                int portNum = Integer.parseInt(args[3]);
                port = new EthernetPort(host, portNum);
            } else if ("TCPSERVER".equalsIgnoreCase(args[1])) {
                int portNum = Integer.parseInt(args[2]);
                port = new EthernetServerPort(portNum);
            } else {
                throw new Exception("wrong configuration parameters");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new Exception("wrong configuration parameters");
        }
    }

    Port getPort() {
        return port;
    }

    Main.UserInputLoop getUserInputLoop() {
        return userInputLoop;
    }
}
