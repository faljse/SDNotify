# SDNotify 

SDNotify implements the [systemd](https://www.freedesktop.org/wiki/Software/systemd/) 
[notification protocol](https://www.freedesktop.org/software/systemd/man/sd_notify.html) in Java.

The Notify protocol uses datagram unix sockets, which are not accessible via Java;
Therefore SDNotify includes a JNA wrapper of the socket API.

SDNotify is available via maven:
[central.sonatype.com](https://central.sonatype.com/artifact/info.faljse/SDNotify/1.5/overview)
```xml
<dependency>
    <groupId>info.faljse</groupId>
    <artifactId>SDNotify</artifactId>
    <version>1.6</version>
</dependency>
```

## Basic usage
SDNotify is initialized at the first call to any send* function.

If the systemd environment isn't available, or the initialization fails a warning message is logged.
All further calls to SDNotify are ignored.
```java
import info.faljse.SDNotify.SDNotify;
public class SDTest {
    public static void main(String[] args) {
        initMyServer();
        SDNotify.sendNotify(); //notify: ready
    }
}
````

## Status text
`systemctl status` will print this string
```java
SDNotify.sendStatus("No space left on device");
```


## Watchdog, etc.
If a watchdog is configured systemd will kill the process 
when `SDNotify.sendWatchdog()` isn't called every n seconds.
`isWatchdogEnabled()` and `getWatchdogFrequency()` may be used to
determine if and at what interval `sendWatchdog()` should be called.

Also available:
`sendReloading()`, `sendStopping()`, `sendErrno()`, `sendBusError()` , `sendMainPID()` - see [sd_notify](https://www.freedesktop.org/software/systemd/man/sd_notify.html) manpage for details.

There is also `sendRaw()` for unsupported/unknown/future functions.

## Sample .service file
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

[Install]
WantedBy=multi-user.target
```

## Dependencies
* [jna](https://github.com/java-native-access/jna)
* [slf4j](https://www.slf4j.org/)
