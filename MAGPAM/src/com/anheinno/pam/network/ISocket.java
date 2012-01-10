/**
 * ISocket.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author wangxun
 * @date 2011-08-02
 * @description Interface for socket
 */
public interface ISocket {
	
	public void Socket(String dstName, Object... params) throws UnknownHostException, IOException;
	
	public void setKeepAlive(boolean keepAlive) throws SocketException;

	public void setSoTimeout(int timeout) throws SocketException;

	public void close() throws IOException;

	public void shutdownInput() throws IOException;

	public void shutdownOutput() throws IOException;
	
	public InputStream getInputStream() throws IOException;
	
	public OutputStream getOutputStream() throws IOException; 

	public enum TYPE{
		ANDROID{
			@Override
			public String toString(){
				return "ANDROID";
			}
		}
	}
	
}
