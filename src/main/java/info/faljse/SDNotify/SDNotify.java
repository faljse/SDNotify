package info.faljse.SDNotify;

import info.faljse.SDNotify.io.NativeDomainSocket;
import info.faljse.SDNotify.jna.CLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  Copyright (C) 2016 Martin Kunz, martin.michael.kunz@gmail.com
 *
 *  @see <a href="https://github.com/faljse/SDNotify">https://github.com/faljse/SDNotify</a>
 *  @author <a href="mailto:martin.michael.kunz@gmail.com">Martin Kunz</a>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
public class SDNotify {
    private final static Logger log = LoggerFactory.getLogger(SDNotify.class);
    private final static String NOTIFY_SOCKET = "NOTIFY_SOCKET";
    private final static String WATCHDOG_USEC = "WATCHDOG_USEC";
    private final static String WATCHDOG_PID = "WATCHDOG_PID";
    private NativeDomainSocket sd;
    private static volatile SDNotify instance;
    private volatile boolean available = false;

    private SDNotify() {
        String socketName = System.getenv().get(NOTIFY_SOCKET);
        if (socketName == null || socketName.length() == 0) {
            log.warn("Environment variable \"{}\" not set. Ignoring calls to SDNotify.", NOTIFY_SOCKET);
            return;
        }
        try {
            CLibrary.SockAddr sockAddr = new CLibrary.SockAddr(socketName);
            sd = new NativeDomainSocket();
            sd.connect(sockAddr);
        } catch (Exception e) {
            log.warn("Notify init failed", e);
        }
        available = true;
    }

    /**
     * @return true, if successfully initialized.
     */
    public static boolean isAvailable() {
        return SDNotify.getInstance().available; //available is valid after initialization.
    }

    /**
     * https://www.freedesktop.org/software/systemd/man/sd_notify.html
     * Tells the service manager that service startup is finished.
     * This is only used by systemd if the service definition file has Type=notify set.
     * Since there is little value in signaling non-readiness,
     * the only value services should send is "READY=1" (i.e. "READY=0" is not defined).
     */
    public static void sendNotify() {
        SDNotify.getInstance().sendString("READY=1");
    }

    /**
     * Tells the service manager that the service is reloading its configuration.
     * This is useful to allow the service manager to track the service's internal state,
     * and present it to the user.
     * Note that a service that sends this notification must also send a "READY=1"
     * notification when it completed reloading its configuration.
     */
    public static void sendReloading() {
        SDNotify.getInstance().sendString("RELOADING=1");
    }

    /**
     * Tells the service manager that the service is beginning its shutdown.
     * This is useful to allow the service manager to track the service's internal state,
     * and present it to the user.
     */
    public static void sendStopping() {
        SDNotify.getInstance().sendString("STOPPING=1");
    }

    /**
     * Passes a single-line UTF-8 status string back to the service manager that describes the service state.
     * This is free-form and can be used for various purposes: general state feedback,
     * fsck-like programs could pass completion percentages and failing programs could pass a
     * human-readable error message. Example: "STATUS=Completed 66% of file system check..."
     *
     * @param status single-line status string
     */
    public static void sendStatus(String status) {
        SDNotify.getInstance().sendString(String.format("STATUS=%s", status));
    }

    /**
     * If a service fails, the errno-style error code, formatted as string. Example: "ERRNO=2" for ENOENT.
     *
     * @param errno the errno-style error code, formatted as string.
     */
    public static void sendErrno(int errno) {
        SDNotify.getInstance().sendString(String.format("ERRNO=%d", errno));
    }

    /**
     * If a service fails, the D-Bus error-style error code.
     * Example: "BUSERROR=org.freedesktop.DBus.Error.TimedOut"
     *
     * @param error the D-Bus error-style error code.
     */
    public static void sendBusError(String error) {
        SDNotify.getInstance().sendString(String.format("BUSERROR=%s", error));
    }

    /**
     * The main process ID (PID) of the service,
     * in case the service manager did not fork off the process itself. Example: "MAINPID=4711"
     *
     * @param pid The main process ID (PID) of the service
     */
    public static void sendMainPID(int pid) {
        SDNotify.getInstance().sendString(String.format("MAINPID=%d", pid));
    }

    /**
     * Tells the service manager to update the watchdog timestamp.
     * This is the keep-alive ping that services need to issue in regular intervals
     * if WatchdogSec= is enabled for it.
     * See systemd.service(5) for information how to enable this functionality and
     * sd_watchdog_enabled(3) for the details of how the service can check whether the watchdog is enabled.
     */
    public static void sendWatchdog() {
        SDNotify.getInstance().sendString("WATCHDOG=1");
    }

    /**
     * Determines whether the watchdog is enabled.
     * <p>
     * Based on sd_watchdog_enabled(3), the watchdog should be enabled if
     * WATCHDOG_USEC is set and WATCHDOG_PID is not set or is equal to the
     * current process' pid.
     *
     * @return true if watchdog is enabled
     */
    public static boolean isWatchdogEnabled() {
        String watchdog_pid = System.getenv(WATCHDOG_PID);
        return isAvailable() && getWatchdogFrequency() > 0 && (isEmpty(watchdog_pid) || determinePid().equals(watchdog_pid));
    }

    /**
     * Returns the watchdog frequency in microseconds or -1 if it is not set.
     * It is recommended to call {@code sendWatchdog()} at half the time returned.
     *
     * @return the watchdog frequency in microseconds or -1 if it is not set.
     */
    public static long getWatchdogFrequency() {
        String watchdog_usec = System.getenv(WATCHDOG_USEC);
        return isAvailable() && !isEmpty(watchdog_usec) ? Long.parseLong(watchdog_usec) : -1L;
    }

    private static SDNotify getInstance() {
        if (instance == null) {
            synchronized (SDNotify.class) {
                if (instance == null)
                    instance = new SDNotify();
            }
        }
        return instance;
    }

    private void sendString(String s) {
        if (sd == null || available == false || s == null)
            return;
        sd.send(s.getBytes(), s.length());
    }

    private static String determinePid() {
        return String.valueOf(CLibrary.clib.getpid());
    }

    private static boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }
}
