import com.printerservice.service.SnmpManager;
import com.printerservice.service.impl.SnmpManagerImpl;
import org.junit.Test;
import org.snmp4j.MessageException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class SnmpManagerTest {

    @Test
    public void createManager(){
        final SnmpManagerImpl snmpManager = new SnmpManagerImpl("153.19.121.167", "161", "1.3.6.1.2.1.1.1.0", "public");
        assertEquals("153.19.121.167", snmpManager.getIpAddress());
        assertEquals("161", snmpManager.getPort());
        assertEquals("1.3.6.1.2.1.1.1.0", snmpManager.getMIB());
        assertEquals("public", snmpManager.getCommunity());
        assertEquals(1, snmpManager.getSnmpVersion());
    }

    @Test
    public void canRunManager() throws IOException {
        final SnmpManagerImpl snmpManager = new SnmpManagerImpl("153.19.121.167", "161", "1.3.6.1.2.1.1.1.0", "public");
        assertNotNull(snmpManager.runSnmpManager());
    }

    @Test(expected = MessageException.class)
    public void cantRunManager() throws IOException {
        final SnmpManagerImpl snmpManager = new SnmpManagerImpl("153.19.121.167", "80", "1.3.6.1.2.1.1.1.0", "public");
        snmpManager.runSnmpManager();
    }

}
