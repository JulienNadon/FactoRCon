package com.dev.julien.factorcon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;

public class FactorioServer {

    private String hostname;
    private int port;
    private Socket socket;

    public FactorioServer(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    private void connect() throws IOException {
        socket = new Socket(hostname, port);
    }

    public void authenticate(String RCON_PASSWORD) throws Exception {
        Packet authPacket = new Packet(Packet.PacketType.SERVERDATA_AUTH, 8, RCON_PASSWORD);
        connect();
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(authPacket.getPacket());
        byte[] response = read();
        if (ByteBuffer.wrap(response).order(ByteOrder.LITTLE_ENDIAN).getInt() == authPacket.getId()) {
        } else throw new Exception("Server failed to autheticate");

    }

    public void send(Packet packet) {

        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(packet.getPacket());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendAndRecieveBody(Packet packet) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(packet.getPacket());
            byte[] response = read();
            return new String(Arrays.copyOfRange(response, 8, response.length), Charset.forName("ISO-8859-1"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private byte[] read() throws IOException {
        InputStream inputStream = socket.getInputStream();
        byte[] incomeingSize = new byte[4];
        for (int i = 0; i < incomeingSize.length; i++) {
            incomeingSize[i] = (byte) inputStream.read();
        }

        int incomeSize = ByteBuffer.wrap(incomeingSize).order(ByteOrder.LITTLE_ENDIAN).getInt();
        byte[] response = new byte[incomeSize];
        for (int j = 0; j < incomeSize; j++) {
            response[j] = (byte) inputStream.read();
        }
        return response;
    }

    public void disconnect() throws IOException {
        socket.close();
    }
}


