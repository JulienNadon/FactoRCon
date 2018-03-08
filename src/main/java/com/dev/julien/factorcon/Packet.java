package com.dev.julien.factorcon;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;


/**
 * A quick implementation of  ource RCON Protocol
 * https://developer.valvesoftware.com/wiki/Source_RCON_Protocol
 */
public class Packet {
    private final byte[] end = new byte[]{0x00};
    private byte[] Size = new byte[4];
    private byte[] ID = new byte[4];
    private byte[] Type = new byte[4];
    private byte[] body;
    private int id;

    public int getId() {
        return id;
    }

    public enum PacketType {
        SERVERDATA_AUTH(3), SERVERDATA_AUTH_RESPONSE(2), SERVERDATA_EXECCOMMAND(2), SERVERDATA_RESPONSE_VALUE(2);
        private int value;

        PacketType(int i) {
            this.value = i;
        }
    }

    public Packet(String command) {
        this(PacketType.SERVERDATA_EXECCOMMAND, 3, "/" + command);
    }


    public Packet(PacketType packetType, int id, String body) {
        this.id = id;
        this.body = toNullTerminatedAsciiByteArray(body);
        this.ID = toID(id);
        this.Type = toType(packetType);
        this.Size = calculateSize();
    }

    private byte[] toNullTerminatedAsciiByteArray(String string) {
        CharsetEncoder enc = Charset.forName("ISO-8859-1").newEncoder();

        int len = string.length();
        byte newString[] = new byte[len + 1];
        ByteBuffer byteBuffer = ByteBuffer.wrap(newString);
        enc.encode(CharBuffer.wrap(string), byteBuffer, true);
        newString[len] = 0; //ASCII null char
        return newString;
    }

    private byte[] toType(PacketType packetType) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(packetType.value).array();
    }


    private byte[] toID(int id) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(id).array();
    }

    private byte[] calculateSize() {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ID.length + Type.length + end.length + body.length).array();
    }

    public byte[] getPacket() {
        byte[] packet = new byte[Size.length + 4];
        packet = ArrayUtils.addAll(Size, ID);
        packet = ArrayUtils.addAll(packet, Type);
        packet = ArrayUtils.addAll(packet, body);
        packet = ArrayUtils.addAll(packet, end);
        return packet;
    }
}