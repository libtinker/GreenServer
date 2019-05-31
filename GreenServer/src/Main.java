
public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Thread thread = new Server(6061);
			thread.run();
		} catch (Exception e) {
			// TODO: handle exception
		e.printStackTrace();
			
		}
	}

}
