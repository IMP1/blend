package jog;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Network {
	
	private final static String HANDSHAKE_CONNECT = "!o/#";
	private final static String HANDSHAKE_DISCONNECT = "#o/!";
	
	private static ArrayList<Server> serverInstances = new ArrayList<Server>();
	private static ArrayList<Client> clientInstances = new ArrayList<Client>();
	
	public static void dispose() {
		for (Client c : clientInstances) {
			c.quit();
		}
		for (Server s : serverInstances) {
			s.quit();
		}
	}
	
	public interface ClientEventHandler {
		public void onMessage(String message);
	}
	
	public interface ServerEventHandler {
		public void onMessage(String sender, String message);
		public void onConnect(String address);
		public void onDisconnect(String address);
	}
	
	public static class Client extends Thread {
		
		private Socket socket;
		private BufferedReader in;
		private BufferedWriter out;
		private ClientEventHandler handler;
		private boolean closed;
		
		private Client(String address, int port, ClientEventHandler handler) {
			super();
			try {
				socket = new Socket(address, port);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				this.handler = handler;
				closed = false;
			} catch (IOException e) {
				closed = true;
			}
			if (!closed) super.start();
		}

		@Override
		public void run() {
			while (!closed) {
				try {
					for (String line = in.readLine(); line != null && !closed; line = in.readLine()) {
						handler.onMessage(line);
					}
				} catch (SocketException e) {
					closed = true;
				} catch (IOException e) {
					e.printStackTrace();
					closed = true;
				}
			}
		}
		
		public void send(String message) {
			if (closed) {
				System.err.println("[Network] (Client) Trying to send from closed client.");
				return;
			}
			try {
				out.write(message + "\r\n");
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void quit() {
			if (closed) return;
			closed = true;
			send(HANDSHAKE_DISCONNECT);
			try {
				out.close();
				in.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("[Network] (Client) Closed client.");
		}
		
	}
	
	public static class Server extends Thread {
		
		private class ClientListener extends Thread {
			
			private String name;
			private Socket socket;
			private BufferedReader in;
			private boolean isClosed;
			
			private ClientListener(String name, Socket socket) {
				super();
				try {
					this.name = name;
					this.socket = socket;
					this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					isClosed = false;
				} catch (IOException e) {
					isClosed = true;
				}
			}
			
			@Override
			public void run() {
				while (!isClosed) {
					try {
						if (isClosed) System.out.println("It's CLOSED! :O");
						for (String line = in.readLine(); line != null; line = in.readLine()) {
							handler.onMessage(name, line);
							if (line.contains(HANDSHAKE_DISCONNECT)) {
								isClosed = true;
							}
						}
					} catch (IOException e) {
//						e.printStackTrace();
						isClosed = true;
					}
				}
				removeClient(name);
			}
			
		}
		
		private ServerSocket socket;
		private ArrayList<ClientListener> clientReaders;
		private HashMap<String, BufferedWriter> clientWriters;
		private ServerEventHandler handler;
		private boolean closed;
		private int port;
		
		private Server(int port, ServerEventHandler handler) {
			super();
			try {
				socket = new ServerSocket(port);
				socket.setReuseAddress(true);
				clientReaders = new ArrayList<ClientListener>();
				clientWriters = new HashMap<String, BufferedWriter>();
				this.handler = handler;
				this.port = port;
				closed = false;
			} catch (BindException e) {
				System.err.println("[Network] (Server) Address (most likely the port) is already being, or has recently been, used.");
				closed = true;
			} catch (IOException e) {
				e.printStackTrace();
				closed = true;
			}
			super.start();
		}
		
		public boolean connected() {
			return !closed;
		}
		
		public String getAddress() {
			if (closed) {
				throw new NullPointerException("Server could not open and so has no address. Check if it's conencted first.");
			}
			return socket.getInetAddress().getHostAddress();
		}
		
		public int getPort() {
			return port;
		}
		
		public String[] getClients() {
			String[] clients = new String[clientReaders.size()];
			for (int i = 0; i < clients.length; i ++) {
				clients[i] = clientReaders.get(i).name;
			}
			return clients;
		}
		
		@Override
		public void run() {
			while (!closed) {
				try {
					Socket client = socket.accept();
					String name = client.getInetAddress().getHostAddress();
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
					ClientListener listener = new ClientListener(name, client);
					listener.start();
					clientReaders.add(listener);
					clientWriters.put(name, writer);
					System.out.println("[Network] (Server) connected to \"" + name + "\".");
					handler.onConnect(name);
					send(name, HANDSHAKE_CONNECT);
				} catch (SocketException e) {
					e.printStackTrace();
					// This is thrown when the socket.accept() is 
					// interrupted by the socket being closed.
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void nameClient(String oldName, String newName) {
			if (!clientWriters.containsKey(oldName)) return;
			clientWriters.put(newName, clientWriters.get(oldName));
			clientWriters.remove(oldName);
			for (ClientListener client : clientReaders) {
				if (client.name == oldName) client.name = newName;
			}
		}
		
		public void send(String address, String message) {
			System.out.println("[Network] (Server) Sending message \"" + message + "\" to " + address + ".");
			if (!clientWriters.containsKey(address)) {
				System.err.println("[Network] (Server) There is no client \"" + address + "\".");
				return;
			}
			try {
				clientWriters.get(address).write(message + "\r\n");
				clientWriters.get(address).flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void broadcast(String message) {
			for (String client : clientWriters.keySet()) {
				send(client, message);
			}
		}
		
		public void quit() {
			if (closed) return;
			closed = true;
			try {
				for (BufferedWriter client : clientWriters.values()) {
					client.write(HANDSHAKE_DISCONNECT + "\r\n");
					client.close();
				}
				for (ClientListener client : clientReaders) {
					client.in.close();
					client.socket.close();
				}
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("[Network] (Server) Closed server.");
		}
		
		private void removeClient(String address) {
			if (!clientWriters.containsKey(address)) {
				System.err.println("[Network] (Server) There is no client '" + address + "' to close.");
				return;
			}
			try {
				System.out.print("[Network] (Server) Sending disconnect handshake... ");
				clientWriters.get(address).write(HANDSHAKE_DISCONNECT + "\r\n");
				System.out.println("Success!");
			} catch (IOException e) {
				System.err.println("Failure!");
			}
			try {
				System.out.print("[Network] (Server) Attempting to close bufferedWriter... ");
				clientWriters.get(address).close();
				System.out.println("Success!");
			} catch (IOException e) {
				System.err.println("Failure!");
			}
			ClientListener client = null;
			for (ClientListener c : clientReaders) {
				if (c.name == address) {
					client = c;
				}
			}
			try {
				System.out.print("[Network] (Server) Attempting to close bufferedReader and socket... ");
				client.in.close();
				client.socket.close();
				System.out.println("Success!");
			} catch (IOException e) {
				System.err.println("Failure!");
			}
			clientWriters.remove(address);
			clientReaders.remove(client);
			handler.onDisconnect(address);
			System.out.println("[Network] (Server) Client removed.");
		}
		
	}
	
	public static Server newServer(int port, ServerEventHandler handler) {
		Server s = new Server(port, handler); 
		serverInstances.add(s);
		return s;
	}
	
	public static Client newClient(String address, int port, ClientEventHandler handler) {
		Client c = new Client(address, port, handler);
		clientInstances.add(c);
		return c;
	}
	
}
