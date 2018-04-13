import com.sun.jna.Platform;
import info.faljse.SDNotify.SDNotify;
import info.faljse.SDNotify.jna.CLibrary;
import org.junit.Assume;
import org.junit.Test;

public class SDNTest {
    @Test
    public void getPidReturnsInteger() {
        Assume.assumeFalse(Platform.isWindows());
        int pid = CLibrary.clib.getpid();
        assert (pid > 1);
    }

    @Test
    public void availableReturnsFalse() {
        assert (SDNotify.isAvailable() == false);
    }

}
