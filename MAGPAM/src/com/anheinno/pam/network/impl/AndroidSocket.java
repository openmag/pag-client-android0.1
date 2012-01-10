/**
 * AndroidSocket.java
 * 
 * @Copyright 2007-2011 Anhe-Inno BeiJing Inc...
 */
package com.anheinno.pam.network.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.Socket;
import com.anheinno.pam.network.ISocket;

/**
 * @author wangxun
 * @date 2011-08-02
 * @description Socket for android platform
 */
public final class AndroidSocket implements ISocket {
	
	private Socket _socket;

	@Override
	public void Socket(String dstName, Object... params)
			throws UnknownHostException, IOException {
		
		int port = 0;
		if(params[0] instanceof Integer){
			port = (Integer)params[0];
		}
		_socket = new Socket(dstName, port);
		_socket.setTcpNoDelay(true);
		
	}
	
	@Override
	public void setKeepAlive(boolean keepAlive) throws SocketException {
		
		if(null != _socket){
			_socket.setKeepAlive(keepAlive);
		}
		
	}

	@Override
	public void setSoTimeout(int timeout) throws SocketException {
		
		if(null != _socket){
			_socket.setSoTimeout(timeout);
		}
		
	}

	@Override
	public void close() throws IOException {
		
		if(null != _socket){
			_socket.close();
		}
	
	}

	@Override
	public void shutdownInput() throws IOException {
		
		if(null != _socket){
			_socket.shutdownInput();
		}
		
	}

	@Override
	public void shutdownOutput() throws IOException {
		
		if(null != _socket){
			_socket.shutdownOutput();
		}
		
	}

	@Override
	public InputStream getInputStream() throws IOException {
		
		if(null != _socket){
			return _socket.getInputStream();
		}
		
		return null;
		
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		
		if(null != _socket){
			return _socket.getOutputStream();
		}
		
		return null;
		
	}

}
