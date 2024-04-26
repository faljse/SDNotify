package info.faljse.SDNotify.jna;

import com.sun.jna.LastErrorException;
import com.sun.jna.Platform;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import com.sun.jna.ptr.IntByReference;
import info.faljse.SDNotify.io.NativeDomainSocket;

import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright (C) 2016 Zsombor Welker, zsombor.welker@webvalto.hu
 *
 * @author <a href="mailto:zsombor.welker@webvalto.hu">Zsombor Welker</a>
 *         <a href="mailto:martin.michael.kunz@gmail.com">Martin Kunz</a>
 *         <p>
 *         This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Lesser General Public License as published by
 *         the Free Software Foundation, either version 2 of the License, or
 *         (at your option) any later version.
 *         <p>
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Lesser General Public License for more details.
 *         <p>
 *         You should have received a copy of the GNU Lesser General Public License
 *         along with this program.  If not, see http://www.gnu.org/licenses/.
 * @see <a href="https://github.com/faljse/SDNotify">https://github.com/faljse/SDNotify</a>
 */
public class CLibrary {
    public static final int AF_UNIX = 1;
    public static final int SOCK_DGRAM = 2;
    private static final Logger log = Logger.getLogger(NativeDomainSocket.class.getName());
    public static final CLibrary clib=new CLibrary();

    static {
        try {
            Native.register(Platform.isWindows() ? "msvcrt" : "c");
        } catch (Exception e) {
            log.log(Level.WARNING, "Native.register(\"c\") failed", e);
        }
    }

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
    native public int getpid() throws LastErrorException;

    public static class SockAddr extends Structure implements Structure.ByReference {
        public short family = AF_UNIX;
        public byte[] addr = new byte[108];

        public SockAddr(String name) {
            System.arraycopy(name.getBytes(StandardCharsets.US_ASCII), 0, addr, 0, name.length());
        }

        @Override
        protected List<String> getFieldOrder() {
            ArrayList a = new ArrayList();
            a.add("family");
            a.add("addr");
            return Collections.unmodifiableList(a);
        }
    }

}
