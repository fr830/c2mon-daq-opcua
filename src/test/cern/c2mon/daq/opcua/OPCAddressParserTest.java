package cern.c2mon.daq.opcua;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;

import cern.c2mon.daq.opcua.connection.common.impl.OPCUADefaultAddress;
import cern.c2mon.daq.opcua.connection.common.impl.OPCUADefaultAddressParser;
import static cern.c2mon.daq.opcua.connection.common.AbstractOPCUAAddressParser.*;

public class OPCAddressParserTest {
    
    private OPCUADefaultAddressParser parser = new OPCUADefaultAddressParser();
    
    @Test
    public void testParseAddressSingleCorrect() throws URISyntaxException {
        String addressString = URI_KEY + "=dcom://testhost:1234/testpath;"
            + USER_KEY + "=user@domain;" + PASSWORD_KEY + "=password;"
            + SERVER_TIMEOUT_KEY + "=314;" + SERVER_RETRY_TIMEOUT_KEY + "=1337";
        List<OPCUADefaultAddress> addresses =
            parser.createOPCAddressFromAddressString(addressString);
        assertEquals(1, addresses.size());
        OPCUADefaultAddress address = addresses.get(0);
        assertEquals("dcom", address.getProtocol());
        assertEquals("domain", address.getDomain());
        assertEquals("password", address.getPassword());
        assertEquals(1337, address.getServerRetryTimeout());
        assertEquals(314, address.getServerTimeout());
        assertEquals(new URI("dcom://testhost:1234/testpath"), address.getUri());
        assertEquals("dcom://testhost:1234/testpath", address.getUriString());
        assertEquals("user", address.getUser());
        assertEquals(true, address.isAliveWriteEnabled());
    }
    
    @Test
    public void testParseAddressDoubleCorrect() throws URISyntaxException {
        String addressString = URI_KEY + "=dcom://testhost:1234/testpath," 
            + "http://testhost2:1234/testpath2;" + USER_KEY + "=user@domain,user2;"
            + PASSWORD_KEY + "=password, password2;"
            + SERVER_TIMEOUT_KEY + "=314;" + SERVER_RETRY_TIMEOUT_KEY + "=1337;"
            + ALIVE_WRITER_KEY + "=false";
        List<OPCUADefaultAddress> addresses =
            parser.createOPCAddressFromAddressString(addressString);
        assertEquals(2, addresses.size());
        OPCUADefaultAddress address = addresses.get(0);
        assertEquals("dcom", address.getProtocol());
        assertEquals("domain", address.getDomain());
        assertEquals("password", address.getPassword());
        assertEquals(1337, address.getServerRetryTimeout());
        assertEquals(314, address.getServerTimeout());
        assertEquals(new URI("dcom://testhost:1234/testpath"), address.getUri());
        assertEquals("dcom://testhost:1234/testpath", address.getUriString());
        assertEquals("user", address.getUser());
        assertEquals(false, address.isAliveWriteEnabled());
        
        address = addresses.get(1);
        assertEquals("http", address.getProtocol());
        assertNull(address.getDomain());
        assertEquals("password2", address.getPassword());
        assertEquals(1337, address.getServerRetryTimeout());
        assertEquals(314, address.getServerTimeout());
        assertEquals(new URI("http://testhost2:1234/testpath2"), address.getUri());
        assertEquals("http://testhost2:1234/testpath2", address.getUriString());
        assertEquals("user2", address.getUser());
        assertEquals(false, address.isAliveWriteEnabled());
    }
    
    @Test(expected=Exception.class)
    public void testParseAddressWrongURI() {
        String addressString = URI_KEY + "=://wrongURI?," 
            + "http://testhost2:1234/testpath2;" + USER_KEY + "=user@domain,user2;"
            + PASSWORD_KEY + "=password, password2;"
            + SERVER_TIMEOUT_KEY + "=314;" + SERVER_RETRY_TIMEOUT_KEY + "=1337";
        parser.createOPCAddressFromAddressString(addressString);
    }
}