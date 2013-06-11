package controller;

import java.sql.Timestamp;
import java.util.ArrayList;

import daoimpl.MySQLProduktBatchDAO;
import daoimpl.MySQLProduktBatchKompDAO;
import daoimpl.MySQLRaavareDAO;
import daoimpl.MySQLReceptDAO;
import daoimpl.MySQLReceptKompDAO;
import daointerfaces.DALException;
import daointerfaces.IProduktBatchDAO;
import daointerfaces.IProduktBatchKompDAO;
import daointerfaces.IRaavareDAO;
import daointerfaces.IReceptDAO;
import daointerfaces.IReceptKompDAO;
import dto.OperatoerDTO;
import dto.ProduktBatchDTO;
import dto.RaavareDTO;
import dto.ReceptDTO;
import dto.ReceptKompDTO;

public class ProduktAdministration {
	private IProduktBatchDAO produktBatchDAO;
	private IProduktBatchKompDAO produktBatchKomDAO;
	private IReceptDAO receptDAO;
	private IReceptKompDAO receptKompDAO;
	private IRaavareDAO raavareDAO;
	
	private String error ="";
	private String succes = "";
	private String handling ="";

	private int receptId = 0;
	private String receptNavn = "";
	private ArrayList<ReceptKompDTO> receptKomp = null;
	
	private String produktbatchId = "";
	
	
	public ProduktAdministration()
	{
		produktBatchDAO = new MySQLProduktBatchDAO();
		produktBatchKomDAO = new MySQLProduktBatchKompDAO();
		receptDAO = new MySQLReceptDAO();
		receptKompDAO = new MySQLReceptKompDAO();
		raavareDAO = new MySQLRaavareDAO();
	}
	public void deleteSucErr()
	{
		succes = "";
		error = "";
		handling = "";
	}
	public void delete()
	{
		produktbatchId = "";
	}
	
	//////////////////////Udfoer handling/////////////////////////
	public void udfoerHandling() throws DALException
	{
		succes = "";
		error = "";
		
		try
		{
			if (handling.equals("Opret produktbatch"))
			{
				createProduktbatch(produktbatchId);
			}
			else
				System.out.println("Ukendt handling: " + handling);
		}
		finally
		{
			handling = null;
		}
	}
	
	private void createProduktbatch(String id) throws DALException
	{
		//int pbId, int status, ReceptDTO recept, Timestamp datoStart, Timestamp datoSlut,OperatoerDTO opr
		int receptId = Integer.parseInt(id);
		int batchId = unusedId();
		ProduktBatchDTO produktBatch = new ProduktBatchDTO(batchId,0,receptDAO.getRecept(receptId),null,null,null);
		produktBatchDAO.createProduktBatch(produktBatch);
		
		succes = "Produktbatch med id: "+batchId+ " er nu oprettet.";
	}
	private int unusedId() throws DALException {
		ArrayList<ProduktBatchDTO> produktBatch = produktBatchDAO.getProduktBatchList();
		boolean emptyId;
		for(int b = 1; b < 999999999; b++) {
			emptyId = true;
			for(int c = 0; c < produktBatch.size(); c++) {
				if(b == produktBatch.get(c).getPbId()) {
					emptyId = false;
					break;
				}
			}
			if(emptyId){
				return b;
			}
		}
		return 0;
	}
	
	public ArrayList<RaavareDTO> getRaavare() throws DALException 
	{
		return raavareDAO.getRaavareList();
	}
	public ArrayList<ReceptDTO> getRecepter() throws DALException 
	{
		return receptDAO.getReceptList();
	}
	
	public ArrayList<ReceptKompDTO> getReceptKomp() throws DALException 
	{
		return receptKomp;
	}
	public String getError()
	{
		return error;
	}
	public String getSucces()
	{
		return succes;
	}
	public int getReceptId() {
		return receptId;
	}
	public void setReceptId(int receptId) {
		this.receptId = receptId;
	}
	public String getReceptNavn() {
		return receptNavn;
	}
	public void setReceptNavn(String receptNavn) {
		this.receptNavn = receptNavn;
	}
	public void setHandling(String handling)
	{
		this.handling = handling;
	}
	public void setProduktbatchId(String produktbatchId) {
		this.produktbatchId = produktbatchId;
	}
}