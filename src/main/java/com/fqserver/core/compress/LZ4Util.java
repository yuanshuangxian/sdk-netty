package com.fqserver.core.compress;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fqserver.lang.java.io.UnicodeInputStream;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.lz4.LZ4SafeDecompressor;

/**
 * GZIP工具
 * 
 * @author lefay
 */
public abstract class LZ4Util {
    static final LZ4Factory factory = LZ4Factory.fastestInstance();

    static final LZ4Compressor c = factory.fastCompressor();
    static final LZ4FastDecompressor fd = factory.fastDecompressor();
    static final LZ4SafeDecompressor sd = factory.safeDecompressor();

    public static final int BUFFER_SIZE = 1024;
    public static final String EXT = ".lz4";

    public static final int DECOMPRESS_RATIO = 5;

    /**
     * 数据压缩
     * 
     * @param data
     * @throws IOException
     */
    public static byte[] compress(byte[] data) throws IOException {
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
     * 数据压缩
     * 
     * @param data
     * @throws IOException
     */
    public static void compress(byte[] data, OutputStream os) throws IOException {
        os.write(c.compress(data));
    }

    /**
     * 文件压缩
     * 
     * @param file
     * @throws IOException
     */
    public static void compress(File file) throws IOException {
        compress(file, true);
    }

    /**
     * 文件压缩
     * 
     * @param file
     * @param delete
     *            是否删除原始文件
     * @throws IOException
     */
    public static void compress(File file, boolean delete) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        InputStream in = new UnicodeInputStream(fis, "utf-8");
        FileOutputStream fos = new FileOutputStream(file.getPath() + EXT);

        try {
            compress(in, fos);
        }
        finally {
            fis.close();

            fos.flush();
            fos.close();

            if (delete) {
                file.delete();
            }
        }
    }

    /**
     * 数据压缩
     * 
     * @param is
     * @param os
     * @throws IOException
     */
    public static void compress(InputStream is, OutputStream os) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(is.available());
        final byte[] buf = new byte[BUFFER_SIZE];
        int n = -1;
        while ((n = is.read(buf, 0, buf.length)) != -1) {
            bos.write(buf, 0, n);
        }
        os.write(c.compress(bos.toByteArray()));
    }

    /**
     * 文件压缩
     * 
     * @param path
     * @throws IOException
     */
    public static void compress(String path) throws IOException {
        compress(path, true);
    }

    /**
     * 文件压缩
     * 
     * @param path
     * @param delete
     *            是否删除原始文件
     * @throws IOException
     */
    public static void compress(String path, boolean delete) throws IOException {
        File file = new File(path);
        compress(file, delete);
    }

    /**
     * 数据解压缩
     * 
     * @param data
     * @throws IOException
     */
    public static byte[] decompress(byte[] data, int destLen) throws IOException {
        byte[] b = null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream(destLen);
        try {
            decompress(data, destLen, bos);
            b = bos.toByteArray();
        }
        finally {
            bos.flush();
            bos.close();
        }
        return b;
    }

    /**
     * 数据解压缩
     * 
     * @param data
     * @throws IOException
     */
    public static void decompress(byte[] data, int destLen, OutputStream os) throws IOException {
        os.write(fd.decompress(data, 0, destLen));
    }

    /**
     * 数据解压缩
     * 
     * @param data
     * @throws IOException
     */
    public static byte[] decompress(byte[] data) throws IOException {
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
     * 数据解压缩
     * 
     * @param data
     * @throws IOException
     */
    public static void decompress(byte[] data, OutputStream os) throws IOException {
        os.write(sd.decompress(data, data.length * DECOMPRESS_RATIO));
    }

    /**
     * 文件解压缩
     * 
     * @param file
     * @throws IOException
     */
    public static void decompress(File file) throws IOException {
        decompress(file, true);
    }

    /**
     * 文件解压缩
     * 
     * @param file
     * @param delete
     *            是否删除原始文件
     * @throws IOException
     */
    public static void decompress(File file, boolean delete) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(file.getPath().replace(EXT, ""));
        try {
            decompress(fis, fos);
        }
        finally {
            fis.close();

            fos.flush();
            fos.close();

            if (delete) {
                file.delete();
            }
        }
    }

    /**
     * 数据解压缩
     * 
     * @param is
     * @param os
     * @throws IOException
     */
    public static void decompress(InputStream is, OutputStream os) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(is.available());
        final byte[] buf = new byte[BUFFER_SIZE];
        int n = -1;
        while ((n = is.read(buf, 0, buf.length)) != -1) {
            bos.write(buf, 0, n);
        }
        os.write(sd.decompress(bos.toByteArray(), is.available() * DECOMPRESS_RATIO));
    }

    /**
     * 文件解压缩
     * 
     * @param path
     * @throws IOException
     */
    public static void decompress(String path) throws IOException {
        decompress(path, true);
    }

    /**
     * 文件解压缩
     * 
     * @param path
     * @param delete
     *            是否删除原始文件
     * @throws IOException
     */
    public static void decompress(String path, boolean delete) throws IOException {
        File file = new File(path);
        decompress(file, delete);
    }

}
