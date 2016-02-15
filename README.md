# SDNotify
SDNotify implements the [systemd](https://www.freedesktop.org/wiki/Software/systemd/) 
[notification protocol](https://www.freedesktop.org/software/systemd/man/sd_notify.html) in Java.

The Notify protocol uses datagram unix sockets, which are not accessible via Java;
Therefore SDNotify includes a JNA wrapper of the socket API.

##Basic usage
```java
import info.faljse.systemdnotify.SDNotify;
public class SDTest {
    public static void main(String[] args) {
        initMyServer()
        SDNotify.sendNotify(); //notify: ready
    }
}
````

##Status text
`systemctl status` will print this string
```java
SDNotify.sendStatus("No space left on device");
```


##Watchdog
If a watchdog is configured systemd will kill the process 
when `SDNotify.sendWatchdog()` isn't called every n seconds.


##Sample .service file
[systemd service documentation](https://www.freedesktop.org/software/systemd/man/systemd.service.html)
```python
[Unit]
Description=My Java Server
After=syslog.target

[Service]
Type=notify
NotifyAccess=all
WorkingDirectory=/opt/myserver/
ExecStart=/usr/bin/java -server -jar myserver.jar
# WatchdogSec=30
# Restart=always
StandardOutput=syslog
StandardError=syslog

[Install]
WantedBy=multi-user.target
```
