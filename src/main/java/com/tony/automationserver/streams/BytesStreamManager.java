package com.tony.automationserver.streams;

import java.io.ByteArrayOutputStream;

public class BytesStreamManager extends StreamManager {

    private enum STATE {
        LENGTH1, LENGTH2, LENGTH3, LENGTH4, DATA
    };

    private STATE state;
    private int length;
    private int remaining;
    private ByteArrayOutputStream byteArray;
    

    public BytesStreamManager() {
        state = STATE.LENGTH1;
        length = 0;
        remaining = 0;
        byteArray = new ByteArrayOutputStream();
    }

    public void OnDataReceived(byte[] buffer, int len) 
    {
        int offset = 0;
        while (offset < len) 
        {
            switch (state) 
            {
                case LENGTH1:
                    length = (buffer[offset] << 24) & 0xFF000000;
                    offset++;
                    state = STATE.LENGTH2;
                    break;
                case LENGTH2:
                    length |= (buffer[offset] << 16) & 0x00FF0000;
                    state = STATE.LENGTH3;
                    offset++;
                    break;
                case LENGTH3:
                    length |= (buffer[offset] <<  8) & 0x0000FF00;
                    state = STATE.LENGTH4;
                    offset++;
                    break;
                case LENGTH4:
                    length |= buffer[offset] & 0x000000FF;
                    state = STATE.DATA;
                    remaining = length;
                    byteArray.reset();
                    if(length == 0)
                        state = STATE.LENGTH1;
                    offset++;
                    if (remaining == 0) {
                        state = STATE.LENGTH1;
                    }
                    break;
                case DATA:
                    int min = remaining < len - offset ? remaining : len - offset;
                    byteArray.write(buffer, offset, min);
                    remaining -= min;
                    offset += min;

                    if(remaining <= 0) {
                        state = STATE.LENGTH1;
                        if(messageReadyListener != null)
                            messageReadyListener.OnMessageReady(byteArray.toByteArray());
                        byteArray.reset();
                    }
            }
        }
    }

    public byte[] formatStream(byte[] msg){
            if(msg == null)
                return new byte[]{0, 0, 0, 0};
            byte[] r = new byte[msg.length + 4];
            int length = msg.length;
            r[0] = (byte)((length & 0xFF000000) >> 24);
            r[1] = (byte)((length & 0x00FF0000) >> 16);
            r[2] = (byte)((length & 0x0000FF00) >> 8);
            r[3] = (byte)((length & 0x000000FF));
            System.arraycopy(msg, 0, r, 4, msg.length);
            return r;
        }
    
}