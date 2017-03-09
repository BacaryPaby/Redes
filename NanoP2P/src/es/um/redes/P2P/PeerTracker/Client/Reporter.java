package es.um.redes.P2P.PeerTracker.Client;

import java.io.IOException;
import java.net.*;

import es.um.redes.P2P.App.Tracker;
import es.um.redes.P2P.PeerTracker.Message.Message;

public class Reporter implements ReporterIface {

	/**
	 * Tracker hostname, used for establishing connection
	 */
	private String trackerHostname;

	/**
	 * UDP socket for communication with tracker
	 */
	private DatagramSocket peerTrackerSocket;

	/***
	 * 
	 * @param tracker
	 *            Tracker hostname or IP
	 */
	public Reporter(String tracker) {
		trackerHostname = tracker;
		try {
			peerTrackerSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			System.err.println("Reporter cannot create datagram socket for communication with tracker");
			System.exit(-1);
		}
	}

	public void end() {
		// Close datagram socket with tracker
		peerTrackerSocket.close();
	}

	@Override
	public boolean sendMessageToTracker(DatagramSocket socket, Message request, InetSocketAddress trackerAddress) {
		byte[] buf = request.toByteArray();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, trackerAddress);
		try {
			socket.send(packet);
			// socket.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		// socket.close();
		return false;
	}

	@Override
	public Message receiveMessageFromTracker(DatagramSocket socket) {
		byte[] buf = new byte[Message.MAX_UDP_PACKET_LENGTH];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Message m = null;
		m.fromByteArray(buf);
		// socket.close();
		return m;
	}

	@Override
	public Message conversationWithTracker(Message request) {
		Message m = null;
		if (sendMessageToTracker(peerTrackerSocket, request, new InetSocketAddress(Tracker.TRACKER_PORT)))
			m = receiveMessageFromTracker(peerTrackerSocket);
		return m;
	}

}
