package com.fqserver.lang.compress;

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
public class GZip {

    private static final int COMPRESS_LEVEL = Deflater.BEST_SPEED;
    public static final int BUFFER_SIZE = 1024;

    public static final GZipC INSTANCE = new GZipC(COMPRESS_LEVEL, BUFFER_SIZE);

    /**
     * 数据压缩
     * 
     * @param data
     * @throws IOException
     */
    public static byte[] compress(final byte[] data) throws IOException {
        return INSTANCE.compress(data);
    }

    /**
     * 数据压缩
     * 
     * @param data
     * @throws IOException
     */
    public static void compress(final byte[] data, final OutputStream os) throws IOException {
        INSTANCE.compress(data, os);
    }

    /**
     * 文件压缩
     * 
     * @param file
     * @throws IOException
     */
    public static void compress(final File file) throws IOException {
        INSTANCE.compress(file);
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
        INSTANCE.compress(file, delete);
    }

    /**
     * 压缩
     * 
     * @param is
     *            待压缩的数据输入流
     * @return byte[] 压缩后的数据
     * @throws IOException
     */
    public static byte[] compress(InputStream is) throws IOException {
        return INSTANCE.compress(is);
    }

    /**
     * 数据压缩
     * 
     * @param is
     * @param os
     * @throws IOException
     */
    public static void compress(final InputStream is, final OutputStream os) throws IOException {
        INSTANCE.compress(is, os);
    }

    /**
     * 文件压缩
     * 
     * @param path
     * @throws IOException
     */
    public static void compress(final String path) throws IOException {
        INSTANCE.compress(path);
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
        INSTANCE.compress(path, delete);
    }

    /**
     * 数据解压缩
     * 
     * @param data
     * @throws IOException
     */
    public static byte[] decompress(final byte[] data) throws IOException {
        return INSTANCE.decompress(data);
    }

    /**
     * 数据解压缩
     * 
     * @param data
     * @throws IOException
     */
    public static void decompress(final byte[] data, final OutputStream os) throws IOException {
        INSTANCE.decompress(data, os);
    }

    /**
     * 文件解压缩
     * 
     * @param file
     * @throws IOException
     */
    public static void decompress(final File file) throws IOException {
        INSTANCE.decompress(file);
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
        INSTANCE.decompress(file, delete);
    }

    /**
     * 解压缩
     * 
     * @param is
     *            压缩的数据输入流
     * @return byte[] 解压缩后的数据
     * @throws IOException
     */
    public static byte[] decompress(InputStream is) throws IOException {
        return INSTANCE.decompress(is);
    }

    /**
     * 数据解压缩
     * 
     * @param is
     * @param os
     * @throws IOException
     */
    public static void decompress(final InputStream is, final OutputStream os) throws IOException {
        INSTANCE.decompress(is, os);
    }

    /**
     * 文件解压缩
     * 
     * @param path
     * @throws IOException
     */
    public static void decompress(final String path) throws IOException {
        INSTANCE.decompress(path);
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
        INSTANCE.decompress(path, delete);
    }

    public static class GZipC implements ICompress {
        public static final String EXT = ".gz";

        private int zipLevel = Deflater.BEST_SPEED;
        private int bufferSize = 1024;

        public GZipC(int zipLevel, int bufferSize) {
            this.zipLevel = zipLevel;
            this.bufferSize = bufferSize;
        }

        public GZipC(int zip_level) {
            this(zip_level, 1024);
        }

        public GZipC() {
            this(3, 1024);
        }

        /**
         * 数据压缩
         * 
         * @param data
         * @throws IOException
         */
        @Override
        public byte[] compress(final byte[] data) throws IOException {
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
        @Override
        public void compress(final byte[] data, final OutputStream os) throws IOException {

            SpeedGZIPOutputStream gzip = new SpeedGZIPOutputStream(os, zipLevel);
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
        public void compress(final File file) throws IOException {
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
        public void compress(final File file, final boolean delete) throws IOException {
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
         * 压缩
         * 
         * @param is
         *            待压缩的数据输入流
         * @return byte[] 压缩后的数据
         * @throws IOException
         */
        @Override
        public byte[] compress(InputStream is) throws IOException {
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
         * 数据压缩
         * 
         * @param is
         * @param os
         * @throws IOException
         */
        @Override
        public void compress(final InputStream is, final OutputStream os) throws IOException {

            SpeedGZIPOutputStream gzip = new SpeedGZIPOutputStream(os, zipLevel);
            try {
                final byte[] buf = new byte[bufferSize];
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
        public void compress(final String path) throws IOException {
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
        public void compress(final String path, final boolean delete) throws IOException {
            File file = new File(path);
            compress(file, delete);
        }

        /**
         * 数据解压缩
         * 
         * @param data
         * @throws IOException
         */
        @Override
        public byte[] decompress(final byte[] data) throws IOException {
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
        @Override
        public void decompress(final byte[] data, final OutputStream os) throws IOException {

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
        public void decompress(final File file) throws IOException {
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
        public void decompress(final File file, final boolean delete) throws IOException {
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
         * 解压缩
         * 
         * @param is
         *            压缩的数据输入流
         * @return byte[] 解压缩后的数据
         * @throws IOException
         */
        @Override
        public byte[] decompress(InputStream is) throws IOException {
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
         * 数据解压缩
         * 
         * @param is
         * @param os
         * @throws IOException
         */
        @Override
        public void decompress(final InputStream is, final OutputStream os) throws IOException {

            GZIPInputStream gzip = new GZIPInputStream(is);
            try {
                final byte[] buf = new byte[bufferSize];
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
        public void decompress(final String path) throws IOException {
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
        public void decompress(final String path, final boolean delete) throws IOException {
            File file = new File(path);
            decompress(file, delete);
        }

    }
}
