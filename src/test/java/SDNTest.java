import info.faljse.SDNotify.SDNotify;
import info.faljse.SDNotify.jna.CLibrary;
import org.junit.Test;

public class SDNTest {
    @Test
    public void getPidReturnsInteger() {
        int pid = CLibrary.clib.getpid();
        assert (pid > 1);
    }

    @Test
    public void availableReturnsFalse() {
        assert (SDNotify.isAvailable() == false);
    }

}
