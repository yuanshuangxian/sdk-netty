package com.fqserver.lang.compress;

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
public class ZLib {
    private static final int COMPRESS_LEVEL = Deflater.BEST_SPEED;
    private static final int BUFFER_SIZE = 1024;

    public static final ZLibC INSTANCE = new ZLibC(COMPRESS_LEVEL, BUFFER_SIZE);

    /**
     * 压缩
     * 
     * @param data
     *            待压缩数据
     * @return byte[] 压缩后的数据
     * @throws IOException
     */
    public static byte[] compress(final byte[] data) throws IOException {
        return INSTANCE.compress(data);
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
        INSTANCE.compress(data, os);
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
        return INSTANCE.compress(is);
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
        INSTANCE.compress(is, os);
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
        return INSTANCE.decompress(data);
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
        INSTANCE.decompress(data, os);
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
        return INSTANCE.decompress(is);
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
        INSTANCE.decompress(is, os);
    }

    public static class ZLibC implements ICompress {
        private int zipLevel = Deflater.BEST_SPEED;
        private int bufferSize = 1024;

        public ZLibC(int zipLevel, int bufferSize) {
            this.zipLevel = zipLevel;
            this.bufferSize = bufferSize;
        }

        public ZLibC(int zip_level) {
            this(zip_level, 1024);
        }

        public ZLibC() {
            this(3, 1024);
        }

        /**
         * 压缩
         * 
         * @param data
         *            待压缩数据
         * @return byte[] 压缩后的数据
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
         * 压缩
         * 
         * @param data
         *            待压缩数据
         * @param os
         *            压缩过的输出流
         * @throws IOException
         */
        @Override
        public void compress(final byte[] data, final OutputStream os) throws IOException {

            Deflater def = new Deflater(zipLevel);
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
        @Override
        public byte[] compress(final InputStream is) throws IOException {
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
        @Override
        public void compress(final InputStream is, final OutputStream os) throws IOException {

            Deflater def = new Deflater(zipLevel);
            DeflaterOutputStream dos = new DeflaterOutputStream(os, def);
            try {
                final byte[] buf = new byte[bufferSize];

                int n = -1;
                while ((n = is.read(buf, 0, bufferSize)) != -1) {
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
         * 解压缩
         * 
         * @param data
         *            待解压缩的数据
         * @param os
         *            解压缩后的数据流
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
         * 解压缩
         * 
         * @param is
         *            压缩的数据输入流
         * @return byte[] 解压缩后的数据
         * @throws IOException
         */
        @Override
        public byte[] decompress(final InputStream is) throws IOException {
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
        @Override
        public void decompress(final InputStream is, final OutputStream os) throws IOException {

            InflaterInputStream iis = new InflaterInputStream(is);
            try {
                final byte[] buf = new byte[bufferSize];

                int n = -1;
                while ((n = iis.read(buf, 0, bufferSize)) != -1) {
                    os.write(buf, 0, n);
                }

            }
            finally {
                iis.close();
            }
        }
    }
}