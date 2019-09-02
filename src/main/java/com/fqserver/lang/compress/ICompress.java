package com.fqserver.lang.compress;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ICompress {

    /**
     * 数据压缩
     * 
     * @param data
     * @throws IOException
     */
    byte[] compress(byte[] data) throws IOException;

    /**
     * 数据压缩
     * 
     * @param data
     * @throws IOException
     */
    void compress(byte[] data, OutputStream os) throws IOException;

    /**
     * 压缩
     * 
     * @param is
     *            待压缩的数据输入流
     * @return byte[] 压缩后的数据
     * @throws IOException
     */
    byte[] compress(InputStream is) throws IOException;

    /**
     * 数据压缩
     * 
     * @param is
     * @param os
     * @throws IOException
     */
    void compress(InputStream is, OutputStream os) throws IOException;

    /**
     * 数据解压缩
     * 
     * @param data
     * @throws IOException
     */
    byte[] decompress(byte[] data) throws IOException;

    /**
     * 数据解压缩
     * 
     * @param data
     * @throws IOException
     */
    void decompress(byte[] data, OutputStream os) throws IOException;

    /**
     * 解压缩
     * 
     * @param is
     *            压缩的数据输入流
     * @return byte[] 解压缩后的数据
     * @throws IOException
     */
    byte[] decompress(InputStream is) throws IOException;

    /**
     * 数据解压缩
     * 
     * @param is
     * @param os
     * @throws IOException
     */
    void decompress(InputStream is, OutputStream os) throws IOException;

}