package com.swe.gateway.util;

/**
 * @author cbw
 */
public class ConvertUtil {
    /**
     * 将short转换为byte数组
     * @return byte数组
     */
    public static byte[] shortToByteArray(short s) {
        byte[] shortBuf = new byte[2];
        for(int i=0;i<2;i++) {
            int offset = (shortBuf.length - 1 -i)*8;
            shortBuf[i] = (byte)((s>>>offset)&0xff);
        }
        return shortBuf;
    }

    /**
     * 将指定的byte数组从指定索引起的两个字节交换位置并转换成对应的short类型值
     * @param bytes 指定的byte数组
     * @param startIndex 开始索引
     * @return 对应的short类型值
     */
    public static short getShort(byte[] bytes, int startIndex)
    {

        // res = InversionByte(res);
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
        byte[] temp = new byte[2];
        temp[0] = bytes[startIndex + 1];
        temp[1] = bytes[startIndex];
        short targets = (short)((temp[0] & 0xff) | ((temp[1] << 8) & 0xff00)); // | 表示按位或
        return targets;
    }
}
