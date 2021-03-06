package seq;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Scanner;

import connector.Connector;
import daoimpl.MySQLOperatoerDAO;
import daoimpl.MySQLProduktBatchDAO;
import daoimpl.MySQLRaavareBatchDAO;
import daoimpl.MySQLProduktBatchKompDAO;
import daoimpl.MySQLReceptDAO;
import daoimpl.MySQLReceptKompDAO;
import daointerfaces.DALException;
import daointerfaces.IProduktBatchDAO;
import daointerfaces.IProduktBatchKompDAO;
import daointerfaces.IRaavareBatchDAO;
import daointerfaces.IReceptKompDAO;
import dto.OperatoerDTO;
import dto.ProduktBatchDTO;
import dto.ProduktBatchKompDTO;
import dto.RaavareBatchDTO;
import dto.ReceptDTO;
import dto.ReceptKompDTO;


public class Controller {

	private OperatoerDTO Opr_PB = null;

	private WeightSocket weightConnection;
	private Scanner input = new Scanner(System.in);
	private String ip;
	private int port;
	private IRaavareBatchDAO raavareBatchDB;
	private IProduktBatchKompDAO produktBatchKompDB;
	private IProduktBatchDAO produktBatchDB;
	private IReceptKompDAO receptKompDB;
	private List<ReceptKompDTO> receptKompDBList;
	private int pbId;

	public Controller(){
		weightConnection = new WeightSocket();
		raavareBatchDB = new MySQLRaavareBatchDAO();
		produktBatchKompDB = new MySQLProduktBatchKompDAO();
		produktBatchDB = new MySQLProduktBatchDAO();
		receptKompDB = new MySQLReceptKompDAO();
		
	}

	public void run(){
		connectToWeight();
		
	}

	private void connectToWeight(){
		try
		{
			System.out.println("Indtast ip:");
			ip = input.next();
			System.out.println("Indtast port:");
			port = Integer.parseInt((input.next()));
			weightConnection.connect(ip, port);
		}
		catch (IOException IO)
		{
			System.out.println("Forkert ip/port");
			connectToWeight();
		}
		catch (NumberFormatException Ne){
			System.out.println("Forkert ip/port");
			connectToWeight();
		}
		connectToDB();
		sekvens();
	}

	private void connectToDB(){
		try{
			new Connector();
			System.out.println("MySQL connection");
		}
		catch(Exception e){
			System.out.println("Connecting to MySQL server failed, trying agin in 30 secs");
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e1) {
			}
			connectToDB();	
		}
	}

	private void faerdig(int pbId) throws IOException{
		String response = "";
		try {
			produktBatchDB.updateProduktBatch(new ProduktBatchDTO(pbId, produktBatchDB.getProduktBatch(pbId).getRecept(), 2, produktBatchDB.getProduktBatch(pbId).getDatoStart(), new Timestamp(System.currentTimeMillis()), produktBatchDB.getProduktBatch(pbId).getOpr()));
		} catch (DALException e) {
		}
		for(int i = 0; i < 3; i++){
			weightConnection.sendToServer("M12 2");
			System.out.println("Modtager svar fra M12: \n" + weightConnection.recieveFromServer().toUpperCase());
		}
		weightConnection.sendToServer("K 1 ");
		System.out.println("Modtager svar fra K 1: \n" + weightConnection.recieveFromServer().toUpperCase());
		weightConnection.sendToServer("RM20 8 \"Faerdig\" \" \" \" \" ");
		System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
		while(true){		
			response = weightConnection.recieveFromServer().toUpperCase();
			System.out.println(response);
			if(response.startsWith("RM20")) {
				if(response.startsWith("RM20 A")){
					weightConnection.sendToServer("Z ");
					sekvens();
					break;
				}
				else{
					weightConnection.sendToServer("RM20 8 \"Faerdig\" \" \" \" \" ");
					System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
				}
			}
			else{
				weightConnection.sendToServer("RM20 8 \"Faerdig\" \" \" \" \" ");
				System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
			}
		}
	}

	private void afvejning(ReceptKompDTO currentReceptKomp, int pbId) throws IOException{
		// variabler kun brugt i afvejning
		String raavareNavn = "";
		double taraBeholderVægt;
		int raavareBatchSelect;
		double raavareStock = 0;
		double raavareMaengde = 0;
		double raavareTolerance = 0;
		double raavareValue = 0;
		String response = "";

		//Hiv info fra database
		try {
			raavareNavn = currentReceptKomp.getRaavare().getRaavareNavn();
			raavareTolerance = currentReceptKomp.getTolerance();
			raavareValue = currentReceptKomp.getNomNetto();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// STEP 7. 8. START
		weightConnection.sendToServer("K 3");
		System.out.println("Modtager svar fra K 3: \n" + weightConnection.recieveFromServer().toUpperCase());
		System.out.println("Operatøren kontrollerer at vægten er ubelastet og trykker ’ok’");	
		weightConnection.sendToServer("RM20 8 \"Empty the weight\" \" \" \" \" ");
		System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
		
		while(true){		
			response = weightConnection.recieveFromServer().toUpperCase();
			System.out.println(response);
			if(response.startsWith("RM20")) {
				if(response.startsWith("RM20 A")) {
					System.out.println("Vægt tømt, svar: " + response);					
					weightConnection.sendToServer("T");
					System.out.println("Modtager svar fra tarering: \n" + weightConnection.recieveFromServer().toUpperCase());
					break;
				}
				else if(response.startsWith("RM20 C")){
					System.out.println("Brugeren har afbrudt sekvensen");
					throw new IOException();
				}
				else{
					weightConnection.sendToServer("RM20 8 \"Empty the weight\" \" \" \" \" ");
					System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
				}
			}
			else{
				weightConnection.sendToServer("RM20 8 \"Empty the weight\" \" \" \" \" ");
				System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
			}
		}
		// STEP 7. 8. SLUT

		// STEP 9. 10. 11. 12. START
		System.out.println("Vægten beder om tara beholder.");
		
		weightConnection.sendToServer("RM20 8 \"Placer tarabeholder\" \" \" \" \" ");
		System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
		while(true){
			response = weightConnection.recieveFromServer().toUpperCase();
			System.out.println(response);
			if(response.startsWith("RM20")) {
				if(response.startsWith("RM20 A")) {
					System.out.println("Gemmer tarabeholderens vægt");
					weightConnection.sendToServer("S");
					while(!response.contains("kg")) {
						weightConnection.sendToServer("S");
						response = weightConnection.recieveFromServer();
					}
					System.out.println(response);
					
					taraBeholderVægt = Double.parseDouble((response.replaceAll("\"", "").replaceAll(",", ".").substring(4, response.length() - 3)));
					System.out.println("Tarabeholder placeret, svar: " + response + ", taravægt: " + taraBeholderVægt);
					
					weightConnection.sendToServer("T");
					System.out.println("Modtager svar fra tarering: \n" + weightConnection.recieveFromServer().toUpperCase());
					break;
				}
				else if(response.startsWith("RM20 C")){
					System.out.println("Brugeren har afbrudt sekvensen");
					throw new IOException();
				}
				else{
					weightConnection.sendToServer("RM20 8 \"Placer tarabeholder\" \" \" \" \" ");
					System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
				}
			}
			else{
				weightConnection.sendToServer("RM20 8 \"Placer tarabeholder\" \" \" \" \" ");
				System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
			}
		}

		// STEP 13.
		System.out.println("Vægten beder om raavarebatch nummer paa raavaren: " + raavareNavn);
		if(raavareNavn.length() > 14) {
			raavareNavn = raavareNavn.substring(0, 14);
		}
		weightConnection.sendToServer("RM20 8 \"RBnr: "+ raavareNavn + "\" \" \" \"&3\" ");
		System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
		while(true){
			response = weightConnection.recieveFromServer().toUpperCase();
			System.out.println(response);
			if(response.startsWith("RM20 A")) {
				try {
					raavareBatchSelect = Integer.parseInt(response.substring(8, response.length()-1));
					System.out.println("raavarebatch selected: " + raavareBatchSelect);
					try{
						raavareStock = raavareBatchDB.getRaavareBatch(raavareBatchSelect).getMaengde();
						System.out.println("RBStock: " + raavareStock);
						System.out.println("Minimum vægt med tol: " +(currentReceptKomp.getNomNetto() - (currentReceptKomp.getNomNetto() * raavareTolerance/100))+ " uden tol: " + currentReceptKomp.getNomNetto());
						if(currentReceptKomp.getRaavare().getRaavareId() == raavareBatchDB.getRaavareBatch(raavareBatchSelect).getRaavare().getRaavareId()) {
							if(raavareStock >= (currentReceptKomp.getNomNetto() - (currentReceptKomp.getNomNetto() * raavareTolerance/100))) {
								break;
							}
							else {
								weightConnection.sendToServer("RM20 8 \"RB min. "+ ((currentReceptKomp.getNomNetto() * raavareTolerance/100) + currentReceptKomp.getNomNetto()) + " kg" + "\" \" \" \" \" ");
								System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
							}
						}
						else {
							weightConnection.sendToServer("RM20 8 \"Forkert RB"+ "\" \" \" \"&3\" ");
							System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
						}
					}
					catch(DALException e){
						e.printStackTrace();
						weightConnection.sendToServer("RM20 8 \"RB findes ikke" + "\" \" \" \"&3\" ");
						System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
					}	
				}
				catch(NumberFormatException e){
					e.printStackTrace();
					weightConnection.sendToServer("RM20 8 \"Angiv RBnr ved tal" + "\" \" \" \"&3\" ");
					System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
				}	
			}
			else if(response.startsWith("RM20 C")){
				System.out.println("Brugeren har afbrudt sekvensen");
				throw new IOException();
			}
			else{
				weightConnection.sendToServer("RM20 8 \"RBnr: "+ raavareNavn + "\" \" \" \"&3\" ");
				System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
			}
		}
		// STEP 13 SLUT

		// STEP 14 START
		System.out.println("Vægten beder om afvejning af raavaren: " + raavareNavn);
		weightConnection.sendToServer("RM20 8 \"Afvej: " + raavareValue + " kg" + "\" \" \" \" \" ");
		System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
		while(true){
			response = weightConnection.recieveFromServer().toUpperCase();
			System.out.println(response);
			if(response.startsWith("K")) {
				if(response.equals("K C 4")) {
					weightConnection.sendToServer("S");
					while(!response.contains("kg")) {
						weightConnection.sendToServer("S");
						response = weightConnection.recieveFromServer();
					}
					raavareMaengde = Double.parseDouble((response.replaceAll("\"", "").replaceAll(",", ".").substring(4, response.length() - 3)));
					System.out.println("Raavare manengde: " + raavareMaengde);
					System.out.println("Raavare tolerance: " + raavareTolerance);
					System.out.println("Raavare value: " + raavareValue);
					if(raavareMaengde <= raavareValue + (raavareValue*raavareTolerance/100) && raavareMaengde >= raavareValue - (raavareValue*raavareTolerance/100)){
						System.out.println("Raavare afvejet korrekt, svar: " + response + ", raavareVægt: " + raavareMaengde);
						try {
							produktBatchKompDB.createProduktBatchKomp(new ProduktBatchKompDTO(pbId, raavareBatchDB.getRaavareBatch(raavareBatchSelect), taraBeholderVægt, raavareMaengde));
							System.out.println("Ny lager maengde" + (raavareStock - raavareMaengde));
							RaavareBatchDTO RBDTO = raavareBatchDB.getRaavareBatch(raavareBatchSelect);
							RBDTO.setMaengde(raavareStock-raavareMaengde);
							raavareBatchDB.updateRaavareBatch(RBDTO);
						} catch (DALException e) {	
							System.out.println("database error");
							System.out.println(e.getMessage());
							e.printStackTrace();
						}
						break;
					}
					else
					{
						weightConnection.sendToServer("RM20 8 \"Tol +-: " + raavareValue*raavareTolerance/100 + " kg" + "\" \" \" \" \" ");
						System.out.println("Modtager svar fra RM20 Tol: \n" + weightConnection.recieveFromServer().toUpperCase());
					}
				}
				else{
					weightConnection.sendToServer("RM20 8 \"Afvej: " + raavareValue + " kg" + "\" \" \" \" \" ");
					System.out.println("Modtager svar fra RM20 KC 4: \n" + weightConnection.recieveFromServer().toUpperCase());
				}
			}
			else if(response.startsWith("RM20")){
				if(response.startsWith("RM20 C")){
					System.out.println("Brugeren har afbrudt sekvensen");
					throw new IOException();
				}
			}
			else{
				weightConnection.sendToServer("RM20 8 \"Afvej: " + raavareValue + " kg" + "\" \" \" \" \" ");
				System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
			}

		}
		// STEP 14 Slut
		System.out.println("Afvejning Ok");
		
		weightConnection.sendToServer("RM20 8 \"Afvejning OK\" \" \" \" \" ");
		System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
		while(true){
			response = weightConnection.recieveFromServer().toUpperCase();
			System.out.println(response);
			if(response.startsWith("RM20")) {
				if(response.startsWith("RM20 A")){
					break;
					}
				else if(response.startsWith("RM20 C")){
					System.out.println("Brugeren har afbrudt sekvensen");
					throw new IOException();
				}
			}
			else {
			weightConnection.sendToServer("RM20 8 \"Afvejning OK\" \" \" \" \" ");
			System.out.println("Modtager svar fra RM20: \n" + weightConnection.recieveFromServer().toUpperCase());
			}
		}
	}


	/**
	 * Method which recieves a ProduktBatch_id from scale, and sends the reciet name back
	 * to the scale.
	 * @param scaleCon Connection
	 * @param message An integer which governs which message will be sent to the scale
	 * @return The PB_id from the scale
	 */
	private int recieveProductBatchId(int message) throws IOException {
		Integer PBId = null;
		if(message == 0) {
			weightConnection.sendToServer("RM20 8 \"Indtast PBId\" \" \" \"&3\" ");
		}
		else if (message == 1) {
			weightConnection.sendToServer("RM20 8 \"PBId igen: ikke tal\" \" \" \"&3\" ");
		}
		else if (message == 2){
			weightConnection.sendToServer("RM20 8 \"PBId igen: ugyldig\" \" \" \"&3\" ");
		}
		else {
			weightConnection.sendToServer("RM20 8 \"PBId igen: status\" \" \" \"&3\" ");
		}
		try {
			System.out.println(weightConnection.recieveFromServer());
			String PBIdTemp = weightConnection.recieveFromServer();
			System.out.println(PBIdTemp);
			PBIdTemp = PBIdTemp.replaceAll("\"", "");
			PBId = Integer.parseInt(PBIdTemp.substring(7, PBIdTemp.length()));

			MySQLProduktBatchDAO PBDAO = new MySQLProduktBatchDAO();
			MySQLReceptDAO RDAO = new MySQLReceptDAO();
			ProduktBatchDTO PBDTO = PBDAO.getProduktBatch(PBId);
			if(PBDTO.getStatus() == 0) {
				PBDTO.setStatus(1);
				PBDTO.setDatoStart(new Timestamp(System.currentTimeMillis()));
				PBDTO.setOpr(Opr_PB);
				PBDAO.updateProduktBatchStart(PBDTO);
				ReceptDTO RDTO = RDAO.getRecept(PBDTO.getRecept().getReceptId());
				weightConnection.sendToServer("RM20 8 \""+RDTO.getReceptNavn()+"\" \" \" \"&3\" ");
				weightConnection.recieveFromServer();
				weightConnection.recieveFromServer();
			}
			else {
				return recieveProductBatchId(3);
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
			return recieveProductBatchId(1);

		} catch (DALException e) {
			e.printStackTrace();
			return recieveProductBatchId(2);
		}
		catch (StringIndexOutOfBoundsException e) {
			throw new IOException();
		}

		return PBId;
	}

	/**
	 * Method which sends an operator_name to the scale, and waits for it to either confirm or
	 * deny the name.
	 * @param connection The connection
	 * @param message An int which governs what message will be sent to the scale
	 * @param opr The OperatoerDTO which matches the opr_id from the recieveUserId method
	 * @return The opr_id from the opr parameter
	 */
	private int validateName(int message, OperatoerDTO opr) throws IOException {



		if(message == 0){
			if(opr.getOprNavn().length() > 14) {
				weightConnection.sendToServer("RM20 8 \"" + opr.getOprNavn().substring(0, 14) + " [y/n]\" \" \" \"&1\" "); 
			}
			else {
				weightConnection.sendToServer("RM20 8 \"" + opr.getOprNavn() + " [y/n]\" \"Y\" \"&1\" ");
			}
		}
		else {
			weightConnection.sendToServer("RM20 8 \"forkert svar: [y/n]\" \"Y\" \"&1\" ");
		}
		System.out.println(weightConnection.recieveFromServer());
		String responseTemp;
		responseTemp = weightConnection.recieveFromServer().toUpperCase();
		responseTemp = responseTemp.replaceAll("\"", "");
		System.out.println(responseTemp);
		String response = responseTemp.substring(7, responseTemp.length());

		if(response.equals("N")) {
			recieveUserId(0);
		}
		else if(!response.equals("Y"))  {
			validateName(1, opr);
		}

		Opr_PB = opr;
		System.out.println(opr.getOprId());
		return opr.getOprId();
	}


	/**
	 * A method which asks for the id of the person using the scale, and calls the method 
	 * validateName
	 * @param connection Connection to the scale
	 * @param message An int governing what message will be sent to the scale
	 */
	private void recieveUserId(int message) throws IOException {

		if(message == 0) {
			weightConnection.sendToServer("RM20 8 \"Indtast OprId\" \" \" \"&3\" ");
		}
		else if(message == 1) {
			weightConnection.sendToServer("RM20 8 \"OprId igen: ikke tal\" \" \" \"&3\" ");
		}
		else {
			weightConnection.sendToServer("RM20 8 \"OprId igen: findes ikke\" \" \" \"&3\" ");
		}

		MySQLOperatoerDAO oprDAO = new MySQLOperatoerDAO();
		OperatoerDTO opr = null;
		try {
			if(!weightConnection.recieveFromServer().equals("RM20 B")) {
				System.out.println(weightConnection.recieveFromServer());
			}

			String oprTemp = weightConnection.recieveFromServer();
			oprTemp = oprTemp.replaceAll("\"", "");
			System.out.println(oprTemp);
			opr = oprDAO.getOperatoer(Integer.parseInt(oprTemp.substring(7, oprTemp.length())));
			System.out.println(opr);

			validateName(0, opr);
		}catch (DALException e) {
			e.printStackTrace();
			recieveUserId(2);
		}catch (NumberFormatException e) {
			e.printStackTrace();
			recieveUserId(1);
		}
		catch (StringIndexOutOfBoundsException e) {
			throw new IOException();
		}
	}


	private void sekvens()
	{
		pbId = 0;
		try {
			new Thread(new ConnectionCheck()).start();
			recieveUserId(0);
			pbId = recieveProductBatchId(0);
			receptKompDBList = receptKompDB.getReceptKompList(produktBatchDB.getProduktBatch(pbId).getRecept().getReceptId());
			System.out.println("size: " + receptKompDBList.size());
			for(int i = 0; i < receptKompDBList.size(); i++) {
				afvejning(receptKompDBList.get(i), pbId);
			}
			faerdig(pbId);
		} catch (IOException e) {
			conLost();

		} catch (DALException e) {
			e.printStackTrace();
		}

	}
	
	public void conLost() {
		try {
			ProduktBatchDTO pb = produktBatchDB.getProduktBatch(pbId);
			if(pb.getStatus() == 1) {
				pb.setDatoSlut(new Timestamp(System.currentTimeMillis()));
				pb.setStatus(3);
				produktBatchDB.updateProduktBatch(pb);
			}
		} catch (DALException e1) {
			e1.printStackTrace();
		}
	}
	
	
	
	
	public class ConnectionCheck implements Runnable {

		
		public void run() {
			check();
		}
		
		public void check(){
		
			InetAddress Scale = null;
			while(true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					Scale = InetAddress.getByName(ip);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				try {
					if(!Scale.isReachable(5000) && !Scale.isReachable(5000) &&
							!Scale.isReachable(5000) && !Scale.isReachable(5000)) {
						System.out.println("connection lost");
						break;
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			conLost();
		}
	}

}
