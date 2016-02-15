package info.faljse.SDNotify.io;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Copyright (C) D-Bus Java, freedesktop.org
 *  Modified for SDNotify by Martin Kunz <martin.michael.kunz@gmail.com>
 *
 *  @see <a href="https://github.com/faljse/SDNotify">https://github.com/faljse/SDNotify</a>
 *  @see <a href="https://www.freedesktop.org/wiki/Software/DBusBindings/">https://www.freedesktop.org/wiki/Software/DBusBindings/</a>
 *
 *  The D-Bus Java implementation is licensed to you under your choice of the
 *  Academic Free License version 2.1, or the GNU Lesser/Library General Public License
 *  version 2.
 */

public class NativeDomainSocket {
    private static final Logger log = Logger.getLogger(NativeDomainSocket.class.getName());
    public static final int AF_UNIX = 1;
    public static final int SOCK_DGRAM = 2;

    private static Linux_C_lib_DirectMapping clib = new Linux_C_lib_DirectMapping();
    private int socket;

    public NativeDomainSocket() throws LastErrorException {
        socket = clib.socket(AF_UNIX, SOCK_DGRAM, 0);
    }

    public void connect(SockAddr addr) throws LastErrorException {
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

    public void bind(SockAddr sockaddr) throws LastErrorException {
        clib.bind(socket, sockaddr, sockaddr.size());
    }

    public void sendCredentialByte(byte data) throws IOException {
        byte[] b = new byte[1];
        send(b, 1);
    }

    public static class SockAddr extends Structure implements Structure.ByReference {
        public short family = AF_UNIX;
        public byte[] addr = new byte[108];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"family", "addr"});
        }

        public SockAddr(String name) {
            System.arraycopy(name.getBytes(), 0, addr, 0, name.length());
        }
    }

    static class Linux_C_lib_DirectMapping {
        native public int fcntl(int fd, int cmd, int arg) throws LastErrorException;
        native public int ioctl(int fd, int cmd, byte[] arg) throws LastErrorException;
        native public int ioctl(int fd, int cmd, Pointer p) throws LastErrorException;
        native public int open(String path, int flags) throws LastErrorException;
        native public int close(int fd) throws LastErrorException;
        native public int write(int fd, Buffer buffer, int count) throws LastErrorException;
        native public int read(int fd, Buffer buffer, int count) throws LastErrorException;
        native public int socket(int domain, int type, int protocol) throws LastErrorException;
        native public int connect(int sockfd, SockAddr sockaddr, int addrlen) throws LastErrorException;
        native public int bind(int sockfd, SockAddr sockaddr, int addrlen) throws LastErrorException;
        native public int accept(int sockfd, SockAddr rem_addr, Pointer opt) throws LastErrorException;
        native public int listen(int sockfd, int channel) throws LastErrorException;
        native public int getsockopt(int s, int level, int optname, byte[] optval, IntByReference optlen);
        native public int setsockopt(int s, int level, int optname, byte[] optval, int optlen);
        native public int recv(int s, Buffer buf, int len, int flags) throws LastErrorException;
        native public int recvfrom(int s, Buffer buf, int len, int flags, SockAddr from, IntByReference fromlen);
        native public int send(int s, Buffer msg, int len, int flags) throws LastErrorException;

        static {
            try {
                Native.register("c");
            } catch (Exception e) {
                log.log(Level.WARNING, "Native.register(\"c\") failed", e);
            }
        }
    }
}
