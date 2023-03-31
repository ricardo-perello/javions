package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class MessageParserTest {
    @Test
    public void testDePArser(){
        ByteString bs0 = ByteString.ofHexadecimalString("8D4D222800B985F7F53FAB33CE76");
        RawMessage rawMessage0 = new RawMessage(0,bs0);
        Message m = MessageParser.parse(rawMessage0);
        assertNull(m);

        ByteString bs5 = ByteString.ofHexadecimalString("8D4D222828B985F7F53FAB33CE76");
        RawMessage rawMessage5 = new RawMessage(0,bs5);
        Message m5 = MessageParser.parse(rawMessage0);
        assertNull(m5);

        ByteString bs23 = ByteString.ofHexadecimalString("8D4D2228B8B985F7F53FAB33CE76");
        RawMessage rawMessage23= new RawMessage(0,bs23);
        Message m23 = MessageParser.parse(rawMessage0);
        assertNull(m23);
    }

}
