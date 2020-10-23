package net.crisps.cloud.framework.canal.client.transfer;

/**
 * @author Administrator
 */
public class MessageTransponders {

    public static TransponderFactory defaultMessageTransponder() {
        return new DefaultTransponderFactory();
    }

}
