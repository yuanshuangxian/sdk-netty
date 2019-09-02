package com.fqserver.core.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * ZLib压缩工具
 * 
 * @author lefay
 */
public abstract class ZLibUtils {
    private static final int BUFFER_SIZE = 1024;
    private static final int COMPRESS_LEVEL = Deflater.BEST_SPEED;

    /**
     * 压缩
     * 
     * @param data
     *            待压缩数据
     * @return byte[] 压缩后的数据
     * @throws IOException
     */
    public static byte[] compress(final byte[] data) throws IOException {
        byte[] b = null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            compress(data, bos);
            b = bos.toByteArray();

        }
        finally {
            bos.flush();
            bos.close();
        }

        return b;
    }

    /**
     * 压缩
     * 
     * @param data
     *            待压缩数据
     * @param os
     *            压缩过的输出流
     * @throws IOException
     */
    public static void compress(final byte[] data, final OutputStream os) throws IOException {

        Deflater def = new Deflater(COMPRESS_LEVEL);
        DeflaterOutputStream dos = new DeflaterOutputStream(os, def);
        try {
            dos.write(data, 0, data.length);
            dos.finish();
        }
        finally {
            def.end();

            dos.flush();
            dos.close();
        }
    }

    /**
     * 压缩
     * 
     * @param is
     *            待压缩的数据输入流
     * @return byte[] 压缩后的数据
     * @throws IOException
     */
    public static byte[] compress(final InputStream is) throws IOException {
        byte[] b = null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream(is.available());

        try {
            compress(is, bos);
            b = bos.toByteArray();
        }
        finally {
            bos.flush();
            bos.close();
        }
        return b;
    }

    /**
     * 压缩
     * 
     * @param is
     *            待压缩数据输入流
     * @param os
     *            压缩过的输出流
     * @throws IOException
     */
    public static void compress(final InputStream is, final OutputStream os) throws IOException {

        Deflater def = new Deflater(COMPRESS_LEVEL);
        DeflaterOutputStream dos = new DeflaterOutputStream(os, def);
        try {
            final byte[] buf = new byte[BUFFER_SIZE];

            int n = -1;
            while ((n = is.read(buf, 0, BUFFER_SIZE)) != -1) {
                dos.write(buf, 0, n);
            }
            dos.finish();
        }
        finally {
            def.end();

            dos.flush();
            dos.close();
        }
    }

    /**
     * 解压缩
     * 
     * @param data
     *            待压缩的数据
     * @return byte[] 解压缩后的数据
     * @throws IOException
     * @throws DataFormatException
     */
    public static byte[] decompress(final byte[] data) throws IOException, DataFormatException {
        byte[] b = null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);

        try {
            decompress(data, bos);
            b = bos.toByteArray();
        }
        finally {
            bos.flush();
            bos.close();
        }
        return b;
    }

    /**
     * 解压缩
     * 
     * @param data
     *            待解压缩的数据
     * @param os
     *            解压缩后的数据流
     * @throws IOException
     * @throws DataFormatException
     */
    public static void decompress(final byte[] data, final OutputStream os) throws IOException,
            DataFormatException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try {
            decompress(bis, os);
        }
        finally {
            bis.close();
        }
    }

    /**
     * 解压缩
     * 
     * @param is
     *            压缩的数据输入流
     * @return byte[] 解压缩后的数据
     * @throws IOException
     */
    public static byte[] decompress(final InputStream is) throws IOException {
        byte[] b = null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream(is.available());

        try {
            decompress(is, bos);
            b = bos.toByteArray();
        }
        finally {
            bos.flush();
            bos.close();
        }
        return b;
    }

    /**
     * 解压缩
     * 
     * @param is
     *            压缩过的数据输入流
     * @param os
     *            解压缩后的数据输出流
     * @throws IOException
     */
    public static void decompress(final InputStream is, final OutputStream os) throws IOException {

        InflaterInputStream iis = new InflaterInputStream(is);
        try {
            final byte[] buf = new byte[BUFFER_SIZE];

            int n = -1;
            while ((n = iis.read(buf, 0, BUFFER_SIZE)) != -1) {
                os.write(buf, 0, n);
            }

        }
        finally {
            iis.close();
        }
    }

}