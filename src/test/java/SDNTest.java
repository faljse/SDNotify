import com.sun.jna.Platform;
import info.faljse.SDNotify.SDNotify;
import info.faljse.SDNotify.jna.CLibrary;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

public class SDNTest {
    @Test
    public void getPidReturnsInteger() {
        Assumptions.assumeFalse(Platform.isWindows());
        int pid = CLibrary.clib.getpid();
        assert (pid > 1);
    }

    @Test
    public void availableReturnsFalse() {
        assert (SDNotify.isAvailable() == false);
    }

}
