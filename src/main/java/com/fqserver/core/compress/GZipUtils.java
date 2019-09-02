package com.fqserver.core.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;

import com.fqserver.lang.java.io.UnicodeInputStream;



/**
 * GZIP工具
 * 
 * @author lefay
 */
public abstract class GZipUtils {

    private static final int COMPRESS_LEVEL = Deflater.BEST_SPEED;

    public static final int BUFFER_SIZE = 1024;
    public static final String EXT = ".gz";

    /**
     * 数据压缩
     * 
     * @param data
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
     * 数据压缩
     * 
     * @param data
     * @throws IOException
     */
    public static void compress(final byte[] data, final OutputStream os) throws IOException {

        SpeedGZIPOutputStream gzip = new SpeedGZIPOutputStream(os, COMPRESS_LEVEL);
        try {
            gzip.write(data, 0, data.length);
            gzip.finish();
        }
        finally {
            gzip.flush();
            gzip.close();
        }
    }

    /**
     * 文件压缩
     * 
     * @param file
     * @throws IOException
     */
    public static void compress(final File file) throws IOException {
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
    public static void compress(final File file, final boolean delete) throws IOException {
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
    public static void compress(final InputStream is, final OutputStream os) throws IOException {

        SpeedGZIPOutputStream gzip = new SpeedGZIPOutputStream(os, COMPRESS_LEVEL);
        try {
            final byte[] buf = new byte[BUFFER_SIZE];
            int n = -1;
            while ((n = is.read(buf, 0, buf.length)) != -1) {
                gzip.write(buf, 0, n);
            }
            gzip.finish();
        }
        finally {
            gzip.flush();
            gzip.close();
        }
    }

    /**
     * 文件压缩
     * 
     * @param path
     * @throws IOException
     */
    public static void compress(final String path) throws IOException {
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
    public static void compress(final String path, final boolean delete) throws IOException {
        File file = new File(path);
        compress(file, delete);
    }

    /**
     * 数据解压缩
     * 
     * @param data
     * @throws IOException
     */
    public static byte[] decompress(final byte[] data) throws IOException {
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
    public static void decompress(final byte[] data, final OutputStream os) throws IOException {

        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try {
            decompress(bis, os);
        }
        finally {
            bis.close();
        }
    }

    /**
     * 文件解压缩
     * 
     * @param file
     * @throws IOException
     */
    public static void decompress(final File file) throws IOException {
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
    public static void decompress(final File file, final boolean delete) throws IOException {
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
    public static void decompress(final InputStream is, final OutputStream os) throws IOException {

        GZIPInputStream gzip = new GZIPInputStream(is);
        try {
            final byte[] buf = new byte[BUFFER_SIZE];
            int n = -1;
            while ((n = gzip.read(buf, 0, buf.length)) != -1) {
                os.write(buf, 0, n);
            }
        }
        finally {
            gzip.close();
        }
    }

    /**
     * 文件解压缩
     * 
     * @param path
     * @throws IOException
     */
    public static void decompress(final String path) throws IOException {
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
    public static void decompress(final String path, final boolean delete) throws IOException {
        File file = new File(path);
        decompress(file, delete);
    }

}
