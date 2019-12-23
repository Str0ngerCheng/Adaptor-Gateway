package com.swe.gateway.util;

/**
 * @author cbw
 */
public class CRCUtil {


    /**
     * 计算CRC16校验码
     * @param crcbuf 需计算数据，包含需要填入校验码的两位字节
     * @return 返回已经计算好的整个校验数据
     */
    public static byte[] CRCCalc(byte[] crcbuf)
    {
        int crc = 0xffff;
        int len = crcbuf.length - 2;
        for (int n = 0; n < len; n++)
        {
            byte i;
            crc = crc ^ crcbuf[n];
            for (i = 0; i < 8; i++)
            {
                int TT;
                TT = crc & 1;
                crc = crc >> 1;
                crc = crc & 0x7fff;
                if (TT == 1)
                    crc = crc ^ 0xa001;
                crc = crc & 0xffff;
            }
        }
        crcbuf[len + 1] = (byte)((crc >> 8) & 0xff);
        crcbuf[len] = (byte)((crc & 0xff));
        return crcbuf;
    }

    /**
     * 进行CRC校验
     * @param buffer 需要校验的数据
     * @return 返回true为校验成功
     */
    public static Boolean CRCCheck(byte[] buffer)
    {
        int CRC = 0xFFFF & 0x0000ffff, temp = 0xA001 & 0x0000ffff;
        for (int k = 0; k < buffer.length; k++)
        {
            CRC ^= buffer[k];
            for (int i = 0; i < 8; i++)
            {
                int j = CRC & 1;
                CRC >>= 1;
                CRC &= 0x7FFF;
                if (j == 1)
                    CRC ^= temp;
            }
        }
        return (CRC == 0) ? true : false;
    }

    public static String getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length-2; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);
    }
}
