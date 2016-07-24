package bt.runtime.protocol.ext.pex;

import bt.bencoding.BEParser;
import bt.bencoding.model.BEMap;
import bt.protocol.MessageContext;
import bt.protocol.MessageHandler;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;

public class PeerExchangeMessageHandler implements MessageHandler<PeerExchange> {

    private final Collection<Class<? extends PeerExchange>> supportedTypes;

    public PeerExchangeMessageHandler() {
        supportedTypes = Collections.singleton(PeerExchange.class);
    }

    @Override
    public Collection<Class<? extends PeerExchange>> getSupportedTypes() {
        return supportedTypes;
    }

    @Override
    public Class<PeerExchange> readMessageType(ByteBuffer buffer) {
        return PeerExchange.class;
    }

    @Override
    public int decodePayload(MessageContext context, ByteBuffer buffer, int declaredPayloadLength) {

        byte[] payload = new byte[declaredPayloadLength];
        buffer.get(payload);
        try (BEParser parser = new BEParser(payload)) {
            BEMap messageContent = parser.readMap();
            PeerExchange message = PeerExchange.parse(messageContent);
            context.setMessage(message);
            return messageContent.getContent().length;
        }
    }

    @Override
    public boolean encodePayload(PeerExchange message, ByteBuffer buffer) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        message.writeTo(bos);

        byte[] payload = bos.toByteArray();
        if (buffer.remaining() < payload.length) {
            return false;
        }

        buffer.put(payload);
        return true;
    }
}
