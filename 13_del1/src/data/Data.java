package data;
import java.util.ArrayList;


public class Data implements IOperatoerDAO {

	private ArrayList<User> personer;

	public Data()
	{
		personer = new ArrayList<User>();

		// Tilføj personer
		personer.add(new OperatoerDTO(1, "Ib Olsen", "112233-4455","12"));
		personer.add(new OperatoerDTO(2, "Ole Jensen", "112233-4455","12Qwerty"));
		personer.add(new OperatoerDTO(3, "Eva Hansen", "112233-4455","12Qwerty"));
		personer.add(new OperatoerDTO(4, "Peter Jensen", "112233-4455","12Qwerty"));
		personer.add(new Admin(10, "Albert Svanesen", "112233-4455",">02324it!<"));
	}

	public User getOperatoer(int oprId) throws DALException
	{
		int i = 0;
		try 
		{
			while (true)
			{
				if (personer.get(i).getOprID()==oprId)
				{
					return personer.get(i);
				}
				i++;
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new DALException(oprId);
		}
	}

	public ArrayList<OperatoerDTO> getOperatoerList() throws DALException
	{
		ArrayList<OperatoerDTO> operatoer = new ArrayList<OperatoerDTO>();
		for (int i = 0; personer.size() > i; i++)
		{
			if (personer.get(i) instanceof OperatoerDTO )
			{
				operatoer.add((OperatoerDTO)personer.get(i));
			}
		}
		return operatoer;
	}

	public void createOperatoer(User opr) throws DALException
	{
		int max = 0;

		for (int i = 0; personer.size() > i; i++)
		{
			if (personer.get(i).getOprID() >= max)
			{
				max = personer.get(i).getOprID();
			}
		}
		opr.setOprID(max+1);
		personer.add(opr);
	}
	public void updateOperatoer(User opr) throws DALException
	{	
		int i = 0;
		try 
		{
			while (true)
			{
				if(personer.get(i).getOprID()==opr.getOprID())
				{
					personer.set(i, opr);
					break;
				}
				i++;
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new DALException(opr.getOprID());
		}			
	}
	public void deleteOperatoer(User opr) throws DALException
	{
		int i = 0;
		try 
		{
			while (true)
			{
				if(personer.get(i).getOprID()==opr.getOprID())
				{
					personer.remove(i);
					break;
				}
				i++;
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new DALException(opr.getOprID());
		}
	}
	
	public boolean attemptLogin(int ID, String password) throws DALException {
		boolean loginOk = false;
		int i = 0;
		try 
		{
			while (true)
			{
				if(personer.get(i).getOprID()==ID)
				{
					if (personer.get(i).getPassword().equals(password)){
						loginOk = true;
						break;
					} 
				}
				i++;
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new DALException(ID);
		}	
		return loginOk;
	}
}
