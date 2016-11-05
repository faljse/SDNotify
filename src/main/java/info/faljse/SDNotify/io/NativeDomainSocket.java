package info.faljse.SDNotify.io;

import com.sun.jna.LastErrorException;
import info.faljse.SDNotify.jna.CLibrary;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import static info.faljse.SDNotify.jna.CLibrary.AF_UNIX;
import static info.faljse.SDNotify.jna.CLibrary.SOCK_DGRAM;

/**
 * Copyright (C) D-Bus Java, freedesktop.org
 * Modified for SDNotify by Martin Kunz, martin.michael.kunz@gmail.com
 * <a href="https://github.com/faljse/SDNotify">SDNotify</a>
 * <a href="https://www.freedesktop.org/wiki/Software/DBusBindings">DBusBindings</a>
 * The D-Bus Java implementation is licensed to you under your choice of the
 * Academic Free License version 2.1, or the GNU Lesser/Library General Public License
 * version 2.
 */

public class NativeDomainSocket {
    private static final Logger log = Logger.getLogger(NativeDomainSocket.class.getName());

    private static CLibrary clib = new CLibrary();
    private int socket;

    public NativeDomainSocket() throws LastErrorException {
        socket = clib.socket(AF_UNIX, SOCK_DGRAM, 0);
    }

    public void connect(CLibrary.SockAddr addr) throws LastErrorException {
        clib.connect(socket, addr, addr.size());
    }

    /**
     * Attempt to read the requested number of bytes from the associated file.
     *
     * @param buf location to store the read bytes
     * @param len number of bytes to attempt to read
     * @return number of bytes read or -1 if there is an error
     */
    public int read(byte[] buf, int len) throws LastErrorException {
        return clib.read(socket, ByteBuffer.wrap(buf, 0, len), len);
    }

    /**
     * Attempt to write the requested number of bytes to the associated file.
     *
     * @param buf    location to store the read bytes
     * @param offset the offset within buf to take data from for the write
     * @param len    number of bytes to attempt to read
     * @return number of bytes read or -1 if there is an error
     */
    public int write(byte[] buf, int offset, int len) throws LastErrorException {
        return clib.write(socket, ByteBuffer.wrap(buf, offset, len), len);
    }

    public int send(byte[] buf, int len) {
        return clib.send(socket, ByteBuffer.wrap(buf, 0, len), len, 0);
    }

    public int recv(byte[] buf, int len) throws LastErrorException {
        return clib.recv(socket, ByteBuffer.wrap(buf, 0, len), len, 0);
    }

    public void close() throws LastErrorException {
        clib.close(socket);
    }

    public NativeDomainSocket accept() {
        return this;
    }

    public void bind(CLibrary.SockAddr sockaddr) throws LastErrorException {
        clib.bind(socket, sockaddr, sockaddr.size());
    }

    public void sendCredentialByte(byte data) throws IOException {
        byte[] b = new byte[1];
        send(b, 1);
    }
}
