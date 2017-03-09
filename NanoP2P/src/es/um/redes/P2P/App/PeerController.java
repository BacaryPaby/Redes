package es.um.redes.P2P.App;

import java.net.InetSocketAddress;

import es.um.redes.P2P.PeerTracker.Client.Reporter;
import es.um.redes.P2P.PeerTracker.Message.Message;
import es.um.redes.P2P.PeerTracker.Message.MessageConf;
import es.um.redes.P2P.PeerTracker.Message.MessageFileInfo;
import es.um.redes.P2P.util.FileInfo;
import es.um.redes.P2P.util.PeerDatabase;

public class PeerController implements PeerControllerIface {
	/**
	 * The shell associated to this controller.
	 */
	private PeerShellIface shell;
	private Reporter client;

	private byte currentCommand;

	public PeerController() {
		shell = new PeerShell();
	}

	public PeerController(Reporter client) {
		shell = new PeerShell();
		this.client = client;
	}

	public byte getCurrentCommand() {
		return currentCommand;
	}

	public void setCurrentCommand(byte command) {
		currentCommand = command;
	}

	public void readCommandFromShell() {
		shell.readCommand();
		setCurrentCommand(shell.getCommand());
		setCurrentCommandArguments(shell.getCommandArguments());
	}

	public void publishSharedFilesToTracker() {
		setCurrentCommand(PeerCommands.COM_ADDSEED);
		processCurrentCommand();
	}

	public void removeSharedFilesFromTracker() {
		setCurrentCommand(PeerCommands.COM_QUIT);
		processCurrentCommand();
	}

	public void getConfigFromTracker() {
		setCurrentCommand(PeerCommands.COM_CONFIG);
		processCurrentCommand();
	}

	public boolean shouldQuit() {
		return currentCommand == PeerCommands.COM_QUIT;
	}

	@Override
	public void setCurrentCommandArguments(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processCurrentCommand() {
		Message m = createMessageFromCurrentCommand();
		Message response = client.conversationWithTracker(m);
		processMessageFromTracker(response);
	}

	@Override
	public Message createMessageFromCurrentCommand() {
		Message m = null;
		switch (currentCommand) {
		case 9:
			byte[] buf = new byte[Message.MAX_UDP_PACKET_LENGTH];
			m = new MessageConf(buf);
			break;
		case 1:
			PeerDatabase dataBase = new PeerDatabase("/home/alumno");
			FileInfo[] fileList = dataBase.getLocalSharedFiles();
			m = new MessageFileInfo((byte) 1, (byte) 1, 3454, fileList);
		}
		return m;
	}

	/**
	 * @param response Respuesta del tracker
	 */
	@Override
	public void processMessageFromTracker(Message response) {
		String code = response.getOpCodeString();
		switch (code) {
		case "GET_CONF":
			short cod = ((MessageConf) response).getChunkSize();
			((MessageConf) response).setChunkSize(cod);
			break;
		case "ADD_SEED_ACK":
			//hacer algo con el ack
		}

	}

	@Override
	public void recordQueryResult(FileInfo[] fileList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void printQueryResult() {
		// TODO Auto-generated method stub

	}

	@Override
	public FileInfo[] lookupQueryResult(String hashSubstr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void downloadFileFromSeeds(InetSocketAddress[] seedList, String targetFileHash) {
		// TODO Auto-generated method stub

	}

}